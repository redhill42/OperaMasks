/*
 * $Id: AjaxUpdaterHandler.java,v 1.8 2008/03/13 12:28:58 jacky Exp $
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
package org.operamasks.faces.facelets.ajax;

import javax.faces.component.NamingContainer;
import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.component.UIComponentBase;
import javax.faces.context.FacesContext;
import javax.faces.FacesException;
import javax.el.ELException;
import java.io.IOException;

import com.sun.facelets.tag.jsf.ComponentHandler;
import com.sun.facelets.tag.jsf.ComponentConfig;
import com.sun.facelets.FaceletContext;

import org.operamasks.faces.render.ajax.AjaxRenderKitImpl;
import org.operamasks.faces.component.ajax.AjaxUpdater;
import org.operamasks.faces.application.ViewBuilder;

public class AjaxUpdaterHandler extends ComponentHandler
{
    public AjaxUpdaterHandler(ComponentConfig config) {
        super(config);
    }

    @Override
    protected void onComponentCreated(FaceletContext ctx, UIComponent c, UIComponent parent) {
        boolean isAjaxResponse = AjaxRenderKitImpl.isAjaxResponse(ctx.getFacesContext());
        if (!isAjaxResponse) {
            c.getChildren().clear();
        }
        
        // set default layout to "block"
        if (c.getAttributes().get("layout") == null) {
            c.getAttributes().put("layout", "block");
        }

        // restore subview if in the restore view phase
        FacesContext context = ctx.getFacesContext();
        String subviewId = getSubViewIdFromRequest(context, c, parent);
        if (subviewId != null) {
        	restoreSubview(context, (AjaxUpdater)c, subviewId);
        }
    }

    private String getSubViewIdFromRequest(FacesContext context, UIComponent c, UIComponent parent) {
        String clientId = null;
        if (c.getParent() == null) {
            clientId = c.getId();
            // calc client id
            // on this time, parent is not set to current component yet.
            UIComponent namingContainer = parent;
            while (namingContainer != null) {
                if (namingContainer instanceof NamingContainer) {
                    break;
                }
                namingContainer = namingContainer.getParent();
            }
            
            String parentId = null;
            if (namingContainer != null) {
                parentId = namingContainer.getContainerClientId(context);
            }
            
            if (parentId != null) {
                clientId = parentId + NamingContainer.SEPARATOR_CHAR + clientId;
            }
        } else {
            clientId = c.getClientId(context);
        }

        String subviewId = context.getExternalContext().getRequestParameterMap().get(clientId);
        if (subviewId == null || subviewId.length() == 0) {
        	return null;
        }
        return subviewId;
	}

	private void restoreSubview(FacesContext ctx, AjaxUpdater updater, String subviewId) {
        // save our view
        UIViewRoot ourView = ctx.getViewRoot();

        UIViewRoot targetView = new UIViewRootWrapper(updater.getId());
        targetView.setViewId(ourView.getViewId());
        targetView.setLocale(ourView.getLocale());
        targetView.setRenderKitId(ourView.getRenderKitId());

        ctx.setViewRoot(targetView);

        try {
            // build subview
            ViewBuilder builder = (ViewBuilder)ctx.getApplication().getViewHandler();
            builder.buildSubview(ctx, subviewId, targetView);
        } finally {
            ctx.setViewRoot(ourView);
        }

        // replace children of AjaxUpdater with the new subview
        updater.getChildren().clear();
        updater.getChildren().addAll(targetView.getChildren());

        updater.setSubviewId(subviewId);
        updater.setNewView(false);
        updater.viewRestored(true);
    }

    @Override
    protected void onComponentPopulated(FaceletContext ctx, UIComponent c, UIComponent parent) {
        FacesContext context = ctx.getFacesContext();
        boolean isAjaxResponse = AjaxRenderKitImpl.isAjaxResponse(context);
        AjaxUpdater updater = (AjaxUpdater)c;

        if (updater.getSubviewId() == null) {
            String url = updater.getUrl();
            if (url != null && url.length() != 0) {
                if (isAjaxResponse) {
                    String requestRenderId = AjaxUpdater.getRequestRenderId(context);
                    String renderId = updater.getRenderId();
                    if (requestRenderId != null && requestRenderId.equals(renderId)) {
                        // we are requested to update our subview
                        updater.setSubviewId(url);
                        updater.setNewView(true);
                    }
                } else if (updater.getUpdate()) {
                    // the subview must update at first response
                    updater.setSubviewId(url);
                    updater.setNewView(true);
                }
            }
        }
    }

    @Override
    protected void applyNextHandler(FaceletContext ctx, UIComponent c)
        throws IOException, FacesException, ELException
    {
        boolean isAjaxResponse = AjaxRenderKitImpl.isAjaxResponse(ctx.getFacesContext());
        if (!isAjaxResponse) {
        	if (getSubViewIdFromRequest(ctx.getFacesContext(), c, null) != null) {
        		return ;
        	}
            c.getChildren().clear();
        }

        if (((AjaxUpdater)c).getSubviewId() != null) {
            // If the AjaxUpdater component already have a subview ID
            // then render the subview instead of component children.
            for (UIComponent cc : c.getChildren()) {
                cc.getAttributes().remove("com.sun.facelets.MARK_DELETED");
            }

            // save fallback contents
            if (!ctx.getFacesContext().getRenderResponse()) {
                Fallback fallback = new Fallback();
                fallback.setTransient(true);
                super.applyNextHandler(ctx, fallback);
                c.getFacets().put("fallback", fallback);
            }
        } else {
            super.applyNextHandler(ctx, c);
        }
    }

    private static final class Fallback extends UIComponentBase {
        public String getFamily() {
            return "org.operamasks.faces.Fallback";
        }
    }

    // A UIViewRoot wrapper that create unique ID. Same algorithm used in AjaxUpdaterRenderer.
    private static final class UIViewRootWrapper extends UIViewRoot {
        private String base;
        private int lastId = 0;

        public UIViewRootWrapper(String base) {
            if (!base.startsWith(UNIQUE_ID_PREFIX))
                base = UNIQUE_ID_PREFIX + base;
            this.base = base;
        }

        @Override
        public String createUniqueId() {
            return base + "_" + (lastId++);
        }
    }
}
