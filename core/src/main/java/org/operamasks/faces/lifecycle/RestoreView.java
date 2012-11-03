/*
 * $Id: RestoreView.java,v 1.4 2007/10/24 04:40:43 daniel Exp $
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
package org.operamasks.faces.lifecycle;

import static javax.faces.event.PhaseId.RESTORE_VIEW;
import javax.faces.event.PhaseId;
import javax.faces.context.FacesContext;
import javax.faces.application.ViewHandler;
import javax.faces.application.ViewExpiredException;
import javax.faces.component.UIViewRoot;
import javax.faces.component.UIComponent;
import javax.faces.FacesException;
import javax.faces.render.RenderKit;
import javax.faces.render.ResponseStateManager;
import javax.el.ValueExpression;
import java.util.Map;
import java.util.Iterator;

import org.operamasks.faces.application.ApplicationAssociate;
import org.operamasks.faces.application.ViewBuilder;
import org.operamasks.faces.util.FacesUtils;
import org.operamasks.faces.binding.ModelBindingFactory;

public class RestoreView extends Phase
{
    private ApplicationAssociate associate;

    public RestoreView() {
        super(RESTORE_VIEW);
    }

    private ApplicationAssociate getAssociate(FacesContext context) {
        if (associate == null)
            associate = ApplicationAssociate.getInstance(context);
        return associate;
    }

    public void execute(FacesContext context) {
        ViewHandler viewHandler = context.getApplication().getViewHandler();
        viewHandler.initView(context);

        UIViewRoot view = context.getViewRoot();

        if (view != null) {
            // The application has explicitly set the view root in the context
            view.setLocale(context.getExternalContext().getRequestLocale());
            ModelBindingFactory.applyModelBindings(context, PhaseId.RESTORE_VIEW);
            updateValueBindings(context, view);
            return;
        }

        // Create new view for initial request
        if (!isPostback(context)) {
            String viewId = getAssociate(context).getViewId(context);
            if (viewId == null) {
                throw new FacesException("Cannot obtain request view ID");
            }
            view = viewHandler.createView(context, viewId);
            context.setViewRoot(view);
            context.renderResponse();
            return;
        }

        // Restore or build the view for postback
        String viewId = context.getExternalContext().getRequestParameterMap().get(FacesUtils.VIEW_ID_PARAM);
        if ((viewId != null) && (viewHandler instanceof ViewBuilder)) {
            view = ((ViewBuilder)viewHandler).buildView(context, viewId);
        } else {
            viewId = getAssociate(context).getViewId(context);
            if (viewId == null) {
                throw new FacesException("Cannot obtain request view ID");
            }
            view = viewHandler.restoreView(context, viewId);
        }

        if (view != null) {
            context.setViewRoot(view);
            ModelBindingFactory.applyModelBindings(context, PhaseId.RESTORE_VIEW);
            updateValueBindings(context, view);

            // save original view ID to detect view ID change
            context.getExternalContext().getRequestMap().put(
                FacesUtils.ORIGINAL_VIEW_ID, view.getViewId());
        } else {
            // For backwards compatability with implementations
            // of ResponseStateManager prior to JSF 1.2, consult
            // the request parameter to see if a view state is
            // posted back.
            if (!isStatePostback(context)) {
                view = viewHandler.createView(context, viewId);
                context.setViewRoot(view);
                context.renderResponse();
            } else {
                throw new ViewExpiredException(viewId);
            }
        }
    }

    private boolean isPostback(FacesContext context) {
        RenderKit renderKit = context.getRenderKit();
        if (renderKit == null) {
            String renderKitId = context.getApplication().getViewHandler().calculateRenderKitId(context);
            renderKit = FacesUtils.getRenderKit(context, renderKitId);
        }
        if (renderKit != null) {
            return renderKit.getResponseStateManager().isPostback(context);
        } else {
            return false;
        }
    }

    private boolean isStatePostback(FacesContext context) {
        Map<String,String> paramMap = context.getExternalContext().getRequestParameterMap();
        return paramMap.containsKey(ResponseStateManager.VIEW_STATE_PARAM);
    }

    private void updateValueBindings(FacesContext context, UIComponent root) {
        Iterator<UIComponent> it = FacesUtils.createFacetsAndChildrenIterator(root, true);
        while (it.hasNext()) {
            UIComponent component = it.next();
            ValueExpression binding = component.getValueExpression("binding");
            if (binding != null) {
                binding.setValue(context.getELContext(), component);
            }
        }
    }
}
