/*
 * $Id: FaceletViewHandlerHook.java,v 1.17 2008/04/24 05:55:44 patrick Exp $
 *
 * Copyright (C) 2006 Operamasks Community.
 * Copyright (C) 2000-2006 Apusic Systems, Inc.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see http://www.gnu.org/licenses.
 */
package org.operamasks.faces.facelets;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Method;

import javax.faces.FacesException;
import javax.faces.application.StateManager;
import javax.faces.application.ViewHandler;
import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.event.PhaseId;
import javax.servlet.ServletResponse;

import org.operamasks.faces.application.ViewBuilder;
import org.operamasks.faces.binding.ModelBindingFactory;
import org.operamasks.faces.component.ajax.AjaxUpdater;
import org.operamasks.faces.event.EventTypes;
import org.operamasks.faces.event.ThreadLocalEventBroadcaster;
import org.operamasks.faces.render.ajax.AjaxRenderKitImpl;
import org.operamasks.faces.render.delegate.ViewDelegateManager;
import org.operamasks.faces.util.StructureValidateUtils;

import com.sun.facelets.Facelet;
import com.sun.facelets.FaceletFactory;
import com.sun.facelets.FaceletViewHandler;
import com.sun.facelets.compiler.Compiler;

/**
 * The Facelet is not friendly to our AJAX implementation, so we have
 * to do some hook on FaceletViewHandler.
 */
public class FaceletViewHandlerHook extends FaceletViewHandler implements ViewBuilder
{
    private static final String BUILD_VIEW_PARAM = "org.operamasks.faces.BUILD_VIEW";

    // duplicate private fields from base class
    private FaceletFactory factory = null;

    public FaceletViewHandlerHook(ViewHandler parent) {
        super(parent);
    }

    @Override
    protected void initialize(FacesContext context) {
        synchronized (this) {
            if (this.factory == null) {
                super.initialize(context);
                assert(this.factory != null);
            }
        }
    }

    @Override
    protected FaceletFactory createFaceletFactory(Compiler c) {
        return this.factory = super.createFaceletFactory(c);
    }

    private void markForBuildBeforeRestore(FacesContext context) {
        context.getExternalContext().getRequestMap().put(BUILD_VIEW_PARAM, Boolean.TRUE);
    }

    private boolean buildBeforeRestore(FacesContext context) {
        return context.getExternalContext().getRequestMap().containsKey(BUILD_VIEW_PARAM);
    }

    public UIViewRoot buildView(FacesContext context, String viewId) {
        if (this.factory == null) {
            this.initialize(context);
        }

        UIViewRoot viewRoot = createView(context, viewId);
        context.setViewRoot(viewRoot);

        FaceletFactory.setInstance(this.factory);
        try {
            Facelet f = this.factory.getFacelet(viewId);
            f.apply(context, viewRoot);
        } catch (IOException ex) {
            throw new FacesException(ex);
        } finally {
            FaceletFactory.setInstance(null);
        }

        StateManager stateManager = context.getApplication().getStateManager();
        ViewHandler outerViewHandler = context.getApplication().getViewHandler();
        String renderKitId = outerViewHandler.calculateRenderKitId(context);
        stateManager.restoreView(context, viewId, renderKitId);

        markForBuildBeforeRestore(context);
        return viewRoot;
    }

    public void buildSubview(FacesContext context, String viewId, UIComponent parent) {
        if (this.factory == null) {
            this.initialize(context);
        }

        FaceletFactory.setInstance(this.factory);
        try {
            Facelet f = this.factory.getFacelet(viewId);
            f.apply(context, parent);
        } catch (IOException ex) {
            throw new FacesException(ex);
        } finally {
            FaceletFactory.setInstance(null);
        }
    }

    @Override
    public UIViewRoot restoreView(FacesContext context, String viewId) {
        return getWrapped().restoreView(context, viewId);
    }

    @Override
    public void renderView(FacesContext context, UIViewRoot viewToRender)
        throws IOException, FacesException
    {
        // exit if the view is not to be rendered
        if (!viewToRender.isRendered()) {
            return;
        }

        // lazy initialize so we have a FacesContext to use
        if (this.factory == null) {
            this.initialize(context);
        }

        // if facelets is not supposed to handle this request
        if (!handledByFacelets(viewToRender.getViewId())) {
            getWrapped().renderView(context, viewToRender);
            return;
        }

        // build view
        if (!buildBeforeRestore(context) || viewToRender.getChildren().isEmpty()) {
            super.buildView(context, viewToRender);
        }

        // apply model bindings before render response
        ModelBindingFactory.applyModelBindings(context, PhaseId.RENDER_RESPONSE);
        
        // validate tree structure for API level component classes
        StructureValidateUtils.validate(context);
        
        // broadcast before_render_view event
        ThreadLocalEventBroadcaster broadcaster = ThreadLocalEventBroadcaster.getInstance();
        broadcaster.broadcast(this, EventTypes.BEFORE_RENDER_VIEW);
        broadcaster.removeEventType(EventTypes.BEFORE_RENDER_VIEW);

        try {
            boolean isAjaxResponse = AjaxRenderKitImpl.isAjaxResponse(context);
            ServletResponse response = (ServletResponse)context.getExternalContext().getResponse();
            response.setContentType("text/html"); // FIXME

            // setup writer and assign it to the context
            ResponseWriter writer = this.createResponseWriter(context);
            context.setResponseWriter(writer);

            // set appropriate AJAX response content type
            if (isAjaxResponse) {
                response.setContentType("text/javascript");
            }

            // force creation of session if saving state there
            StateManager stateMgr = context.getApplication().getStateManager();
            if (!stateMgr.isSavingStateInClient(context)) {
                context.getExternalContext().getSession(true);
            }

            // delegate current view to registered delegates.
            ViewDelegateManager vdm = ViewDelegateManager.getInstance(context);
            if (vdm != null) {
                vdm.processViewDelegates(context);
                if (context.getResponseComplete()) {
                    // if delegate rendered the view then stop here
                    writer.close();
                    return;
                }
            }

            // render the view to the response
            writer.startDocument();

            boolean rendered = false;
            if (isAjaxResponse) {
                String renderId = AjaxUpdater.getRequestRenderId(context);
                if (renderId != null) {
                    // render partial view if a render id parameter present
                    rendered = renderPartialView(context, renderId, viewToRender);
                }
            }
            if (!rendered) {
                // render the complete view
                viewToRender.encodeAll(context);
            }

            writer.endDocument();

            // finish writing
            writer.close();
        } catch (FileNotFoundException fnfe) {
            this.handleFaceletNotFound(context, viewToRender.getViewId());
        } catch (Exception ex) {
            this.handleRenderException(context, ex);
        }
    }

    private boolean renderPartialView(FacesContext context, String renderId, UIComponent component)
        throws IOException, FacesException
    {
        boolean rendered = false;

        for (UIComponent child : component.getChildren()) {
            if (child instanceof AjaxUpdater) {
                String id = ((AjaxUpdater)child).getRenderId();
                if (id != null && id.equals(renderId)) {
                    child.encodeAll(context);
                    rendered = true;
                }
            } else {
                rendered |= renderPartialView(context, renderId, child);
            }
        }
        return rendered;
    }

    @Override
    public void writeState(FacesContext context) throws IOException {
        getWrapped().writeState(context);
    }

    private static Method handledByFaceletsMethod;
    static {
        try {
            handledByFaceletsMethod = FaceletViewHandler.class.getDeclaredMethod(
                "handledByFacelets", String.class);
            handledByFaceletsMethod.setAccessible(true);
        } catch (NoSuchMethodException ex) {
            throw new NoSuchMethodError(ex.getMessage());
        }
    }

    private boolean handledByFacelets(String viewId) {
        try {
            return (Boolean)handledByFaceletsMethod.invoke(this, viewId);
        } catch (Throwable ex) {
            return false;
        }
    }
}
