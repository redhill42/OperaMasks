/*
 * $Id: AjaxRenderKitImpl.java,v 1.10 2007/09/17 22:07:46 daniel Exp $
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

package org.operamasks.faces.render.ajax;

import java.io.Writer;
import javax.faces.context.ResponseWriter;
import javax.faces.context.FacesContext;
import javax.faces.render.RenderKit;
import javax.faces.render.RenderKitFactory;
import javax.faces.render.ResponseStateManager;
import javax.faces.render.Renderer;
import javax.servlet.http.HttpServletRequest;
import org.operamasks.faces.render.AbstractRenderKit;
import org.operamasks.faces.util.FacesUtils;

public class AjaxRenderKitImpl extends AbstractRenderKit
{
    public static final String AJAX_RENDER_KIT = "AJAX";
    public static final String AJAX_SUB_RENDER_KIT = ":AJAX";

    private static final String AJAX_REQUEST_HEADER = "X-Requested-By";
    private static final String AJAX_REQUEST_VALUE  = "XMLHttpRequest";

    public static boolean isAjaxResponse(FacesContext context) {
        String renderKitId = context.getViewRoot().getRenderKitId();
        if (renderKitId == null)
            return false;
        if (!AJAX_RENDER_KIT.equals(renderKitId) && !renderKitId.endsWith(AJAX_SUB_RENDER_KIT))
            return false;

        Object requestObj = context.getExternalContext().getRequest();
        if (!(requestObj instanceof HttpServletRequest))
            return false;

        HttpServletRequest request = (HttpServletRequest)requestObj;
        String header = request.getHeader(AJAX_REQUEST_HEADER);
        if (!AJAX_REQUEST_VALUE.equals(header))
            return false;

        // don't send ajax response if view ID changed, e.g., navigated to a new view.
        String originalViewId = (String)request.getAttribute(FacesUtils.ORIGINAL_VIEW_ID);
        String currentViewId = context.getViewRoot().getViewId();
        return originalViewId == null || currentViewId.equals(originalViewId);
    }

    private RenderKit delegate;
    private ResponseStateManager stateManager;

    public AjaxRenderKitImpl() {
        stateManager = new AjaxResponseStateManager();
    }

    public Renderer getRenderer(String family, String rendererType) {
        Renderer renderer = this.getPrivateRenderer(family, rendererType);
        if (renderer == null) {
            RenderKit delegate = getDelegateRenderKit();
            if (delegate != null) {
                renderer = delegate.getRenderer(family, rendererType);
            }
        }
        return renderer;
    }

    protected RenderKit getDelegateRenderKit() {
        if (this.delegate == null) {
            FacesContext context = FacesContext.getCurrentInstance();
            this.delegate = FacesUtils.getRenderKit(context, RenderKitFactory.HTML_BASIC_RENDER_KIT);
        }
        return this.delegate;
    }

    public ResponseStateManager getResponseStateManager() {
        return stateManager;
    }

    protected ResponseWriter implCreateResponseWriter(Writer writer, String contentType, String encoding) {
        FacesContext context = FacesContext.getCurrentInstance();
        if (isAjaxResponse(context)) {
            return new AjaxResponseWriter(writer, contentType, encoding);
        } else {
            return new AjaxHtmlResponseWriter(writer, contentType, encoding);
        }
    }
}
