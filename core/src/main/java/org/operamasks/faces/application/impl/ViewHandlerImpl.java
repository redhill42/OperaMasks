/*
 * $Id: ViewHandlerImpl.java,v 1.3 2007/09/17 20:04:17 daniel Exp $
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

package org.operamasks.faces.application.impl;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

import javax.faces.FacesException;
import javax.faces.application.StateManager;
import javax.faces.application.ViewHandler;
import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.RenderKit;
import javax.faces.render.RenderKitFactory;
import javax.faces.render.ResponseStateManager;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import javax.servlet.jsp.jstl.core.Config;

import org.operamasks.faces.component.ajax.AjaxUpdater;
import org.operamasks.faces.render.ajax.AjaxHtmlResponseWriter;
import org.operamasks.faces.render.ajax.AjaxRenderKitImpl;
import org.operamasks.faces.render.ajax.AjaxResponseWriter;
import org.operamasks.faces.util.FacesUtils;
import org.operamasks.faces.application.ApplicationAssociate;
import org.operamasks.util.Utils;

public class ViewHandlerImpl extends ViewHandler
{
    private static final String FACES_MAPPING = "org.operamasks.faces.FACES_MAPPING";

    private ApplicationAssociate associate;

    private ApplicationAssociate getAssociate(FacesContext context) {
        if (associate == null)
            associate = ApplicationAssociate.getInstance(context);
        return associate;
    }

    public Locale calculateLocale(FacesContext context) {
        Locale result = null;
        Iterator<Locale> requestLocales = context.getExternalContext().getRequestLocales();
        while (result == null && requestLocales.hasNext()) {
            Locale pref = requestLocales.next();
            result = getPreferredLocale(context, pref);
        }
        if (result == null)
            result = context.getApplication().getDefaultLocale();
        if (result == null)
            result = Locale.getDefault();
        return result;
    }

    private Locale getPreferredLocale(FacesContext context, Locale pref) {
        Locale result = null;
        Iterator<Locale> it = context.getApplication().getSupportedLocales();
        while (it.hasNext()) {
            Locale locale = it.next();
            if (pref.equals(locale)) {
                result = locale;
                break;
            }
            if (pref.getLanguage().equals(locale.getLanguage()) && locale.getCountry().equals("")) {
                result = locale;
            }
        }
        return result;
    }

    public String calculateCharacterEncoding(FacesContext context) {
        String encoding = super.calculateCharacterEncoding(context);
        if (encoding != null && encoding.startsWith("\"") && encoding.endsWith("\""))
            encoding = encoding.substring(1, encoding.length()-1);
        return encoding;
    }

    public String calculateRenderKitId(FacesContext context) {
        Map<String,String> paramMap = context.getExternalContext().getRequestParameterMap();
        String renderKitId = paramMap.get(ResponseStateManager.RENDER_KIT_ID_PARAM);
        if (renderKitId == null)
            renderKitId = context.getApplication().getDefaultRenderKitId();
        if (renderKitId == null)
            renderKitId = RenderKitFactory.HTML_BASIC_RENDER_KIT;
        return renderKitId;
    }

    public UIViewRoot createView(FacesContext context, String viewId) {
        Locale locale = null;
        String renderKitId = null;

        UIViewRoot viewRoot = context.getViewRoot();
        if (viewRoot != null) {
            locale = viewRoot.getLocale();
            renderKitId = viewRoot.getRenderKitId();
        }

        ViewHandler outerViewHandler = context.getApplication().getViewHandler();
        if (locale == null)
            locale = outerViewHandler.calculateLocale(context);
        if (renderKitId == null)
            renderKitId = outerViewHandler.calculateRenderKitId(context);

        viewRoot = new UIViewRoot();
        viewRoot.setViewId(viewId);
        viewRoot.setLocale(locale);
        viewRoot.setRenderKitId(renderKitId);
        return viewRoot;
    }

    public String getActionURL(FacesContext context, String viewId) {
        if (viewId == null || !viewId.startsWith("/")) {
            throw new IllegalArgumentException("Invalid view ID: " + viewId);
        }

        String contextPath = context.getExternalContext().getRequestContextPath();
        String mapping = getFacesMapping(context);

        if (mapping == null) {
            return contextPath.concat(viewId);
        } else if (isPrefixMapping(mapping)) {
            return contextPath.concat(mapping).concat(viewId);
        } else {
            return contextPath.concat(replaceSuffix(viewId, mapping));
        }
    }

    public String getResourceURL(FacesContext context, String path) {
        ExternalContext ectx = context.getExternalContext();
        String contextPath = ectx.getRequestContextPath();

        if (path.startsWith("/")) {
            path = contextPath.concat(path);
        } else if (!Utils.isAbsoluteURL(path)) {
            String prefix = (String)ectx.getRequestMap().get("javax.servlet.include.servlet_path");
            if (prefix == null)
                prefix = ectx.getRequestServletPath();
            if (prefix != null) {
                prefix = prefix.substring(0, prefix.lastIndexOf('/')+1);
                path = contextPath.concat(prefix).concat(path);
            }
        }
        return path;
    }

    private String getFacesMapping(FacesContext context) {
        Map<String,Object> requestMap = context.getExternalContext().getRequestMap();
        String mapping = (String)requestMap.get(FACES_MAPPING);
        if (mapping == null) {
            mapping = getAssociate(context).getFacesMapping(context);
            requestMap.put(FACES_MAPPING, mapping);
        }
        return mapping;
    }

    private static boolean isPrefixMapping(String mapping) {
        return mapping.charAt(0) == '/';
    }

    private String replaceSuffix(String viewId, String suffix) {
        int period = viewId.lastIndexOf('.');
        if (period == -1) {
            return viewId.concat(suffix);
        } else if (!viewId.endsWith(suffix)) {
            return viewId.substring(0, period).concat(suffix);
        } else {
            return viewId;
        }
    }

    private String defaultSuffix;

    private String getDefaultSuffix(FacesContext context) {
        if (defaultSuffix == null) {
            defaultSuffix = context.getExternalContext().getInitParameter(DEFAULT_SUFFIX_PARAM_NAME);
            if (defaultSuffix == null) {
                defaultSuffix = DEFAULT_SUFFIX;
            }
        }
        return defaultSuffix;
    }

    public UIViewRoot restoreView(FacesContext context, String viewId) {
        ExternalContext extCtx = context.getExternalContext();

        String mapping = getFacesMapping(context);
        if (mapping != null) {
            if (!isPrefixMapping(mapping)) {
                // replace suffix
                viewId = replaceSuffix(viewId, getDefaultSuffix(context));
            } else if (mapping.equals(viewId)) {
                // The request was to the FacesServlet only,
                // send redirect to correct path info
                try {
                    context.responseComplete();
                    extCtx.redirect(extCtx.getRequestContextPath());
                    return null;
                } catch (IOException ex) {
                    throw new FacesException(ex);
                }
            }
        }

        StateManager stateManager = context.getApplication().getStateManager();
        ViewHandler outerViewHandler = context.getApplication().getViewHandler();
        String renderKitId = outerViewHandler.calculateRenderKitId(context);
        return stateManager.restoreView(context, viewId, renderKitId);
    }

    public void renderView(FacesContext context, UIViewRoot viewToRender)
        throws IOException, FacesException
    {
        if (!viewToRender.isRendered())
            return;

        // Convert view ID
        String viewId = viewToRender.getViewId();
        String mapping = getFacesMapping(context);

        if (mapping != null) {
            if (!isPrefixMapping(mapping)) {
                // replace suffix
                viewId = replaceSuffix(viewId, getDefaultSuffix(context));
            } else if (mapping.equals(viewId)) {
                // The request was to the FacesServlet only, this may cause
                // a recursion when dispatching the request
                HttpServletResponse response = (HttpServletResponse)
                    context.getExternalContext().getResponse();
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
        }

        viewToRender.setViewId(viewId);

        // Set JSTL locale configuration. This must be done before JSTL
        // setBundle tag is called.
        ExternalContext extCtx = context.getExternalContext();
        if (extCtx.getRequest() instanceof ServletRequest) {
            Config.set((ServletRequest)extCtx.getRequest(), Config.FMT_LOCALE, viewToRender.getLocale());
        }

        // resolve cache problems
        try {
            HttpServletResponse response = (HttpServletResponse)extCtx.getResponse();
            String encoding = extCtx.getRequestCharacterEncoding() ;
            if( encoding != null && Charset.isSupported( encoding ) ) {
            	response.setCharacterEncoding( encoding ) ;
            }
            response.setHeader("Cache-Control", "no-cache");
            response.setHeader("Pragma", "no-cache");
        } catch (Throwable ex) {}
        
        associate.responseRendered();

        if (AjaxRenderKitImpl.isAjaxResponse(context)) {
            // Render AJAX response
            renderAjaxView(context, viewToRender);
        } else {
            // Dispatch to JSP page to build and render the view
            extCtx.dispatch(viewId);
        }
    }

    private static class ResponseWrapper extends HttpServletResponseWrapper {
        public ResponseWrapper(HttpServletResponse wrapped) {
            super(wrapped);
        }

        private boolean useWriter;
        private boolean useStream;
        public PrintWriter getWriter() {
            if(useStream) {
                throw new IllegalStateException();
            }
            useWriter = true;
            return new PrintWriter(new NullWriter());
        }
        public ServletOutputStream getOutputStream() {
            if(useWriter) {
                throw new IllegalStateException();
            }
            useStream = true;
            return new NullOutputStream();
        }

        public void setContentType(String x) {}
        public void setContentLength(int x) {}
        public void setLocale(Locale x) {}
        public void flushBuffer() {}
    }

    private static class NullWriter extends Writer {
        public void write(int c) {}
        public void write(char cbuf[], int off, int len) {}
        public void write(String s, int off, int len) {}
        public void flush() {}
        public void close() {}
    }

    private static class NullOutputStream extends ServletOutputStream{
        public void write(int c) {}
        public void write(char cbuf[], int off, int len) {}
        public void write(String s, int off, int len) {}
        public void flush() {}
        public void close() {}
    }

    private static final String AJAX_RESPONSE_TYPE = "text/javascript";

    private void renderAjaxView(FacesContext context, UIViewRoot viewToRender)
        throws IOException, FacesException
    {
        // eval page to rebuild component tree
        ExternalContext extCtx = context.getExternalContext();
        HttpServletResponse response = (HttpServletResponse)extCtx.getResponse();
        response.setLocale(context.getViewRoot().getLocale());

        try {
            extCtx.setResponse(new ResponseWrapper(response));
            extCtx.dispatch(viewToRender.getViewId());
        } finally {
            extCtx.setResponse(response);
        }

        String contentType = response.getContentType();
        String encoding = response.getCharacterEncoding();
        response.setContentType(AJAX_RESPONSE_TYPE);

        RenderKit renderKit = FacesUtils.getRenderKit(context, AjaxRenderKitImpl.AJAX_RENDER_KIT);
        ResponseWriter writer = renderKit.createResponseWriter(response.getWriter(), contentType, encoding);

        context.setResponseWriter(writer);
        writer.startDocument();

        String renderId = AjaxUpdater.getRequestRenderId(context);
        boolean rendered = false;
        if (renderId != null) {
            // render partial view if a render id parameter present
            rendered = renderPartialView(context, renderId, viewToRender);
        }

        if (!rendered) {
            // render the complete view
            viewToRender.encodeAll(context);
        }

        writer.endDocument();
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

    public void writeState(FacesContext context) throws IOException {
        // For Ajax response the view state is written at end of response,
        // so just set a flag instead of write actual view state.
        ResponseWriter writer = context.getResponseWriter();
        if (writer instanceof AjaxResponseWriter) {
            ((AjaxResponseWriter)writer).setViewStateChanged();
        } else if (writer instanceof AjaxHtmlResponseWriter) {
            ((AjaxHtmlResponseWriter)writer).setViewStateChanged();
        } else {
            StateManager stateManager = context.getApplication().getStateManager();
            Object state = stateManager.saveView(context);
            stateManager.writeState(context, state);
        }
    }
}
