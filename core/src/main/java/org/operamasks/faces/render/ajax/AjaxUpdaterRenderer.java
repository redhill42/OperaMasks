/*
 * $Id: AjaxUpdaterRenderer.java,v 1.24 2008/04/16 03:07:19 patrick Exp $
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

import static org.operamasks.resources.Resources.UI_IGNORING_NESTED_PAGE_TAG;
import static org.operamasks.resources.Resources._T;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.FacesException;
import javax.faces.application.ViewHandler;
import javax.faces.event.PhaseId;
import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.operamasks.faces.component.ajax.AjaxUpdater;
import org.operamasks.faces.render.html.HtmlRenderer;
import org.operamasks.faces.render.resource.ResourceManager;
import org.operamasks.faces.util.FacesUtils;
import org.operamasks.faces.util.HtmlEncoder;
import org.operamasks.faces.application.ViewBuilder;
import org.operamasks.faces.binding.ModelBindingFactory;

public class AjaxUpdaterRenderer extends HtmlRenderer
{
    private static final String DEFAULT_ENCODING = "ISO-8859-1";

    private static final String SERVLET_PATH_ATTR = "javax.servlet.include.servlet_path";

    /**
     * This is a request scoped attribute which contains an AtomicInteger
     * which we use to increment the PageContext count.
     */
    private static final String JAVAX_FACES_PAGECONTEXT_COUNTER =
        "javax.faces.webapp.PAGECONTEXT_COUNTER";

    /**
     * The random genrator used to generate unique PageContext counter.
     */
    private static final Random prngPcId = new Random();

    private Logger logger = Logger.getLogger("org.operamasks.faces.view"); 
    
    @Override
    public void encodeBegin(FacesContext context, UIComponent component)
        throws IOException
    {
        if (context == null || component == null)
            throw new NullPointerException();
        if (!component.isRendered())
            return;
        if (!isAjaxHtmlResponse(context))
            return;

        ResponseWriter out = context.getResponseWriter();
        String layout = (String)component.getAttributes().get("layout");
        if (layout != null && layout.equals("block")) {
            out.startElement("div", component);
        } else {
            out.startElement("span", component);
        }
        writeIdAttributeIfNecessary(context, out, component);
        String style = ((AjaxUpdater)component).getStyle();
        if (style == null) {
            style = "";
        }
        style = "position:relative;" + style;
        out.writeAttribute( "style", style, null);
        renderPassThruAttributes(out, component, "style");
    }

    @Override
    public boolean getRendersChildren() {
        return true;
    }

    @Override
    public void encodeChildren(FacesContext context, UIComponent component)
        throws IOException
    {
        if (context == null || component == null)
            throw new NullPointerException();
        if (!component.isRendered())
            return;

        AjaxUpdater updater = (AjaxUpdater)component;

        if (updater.getSubviewId() != null) {
            ViewHandler vh = context.getApplication().getViewHandler();
            if (vh instanceof ViewBuilder) {
                buildSubview(context, (ViewBuilder)vh, updater);
            } else {
                renderSubview(context, updater);
            }
        } else if (updater.isNewView() && isAjaxResponse(context)) {
            updater.setNewView(false);
            encodeNewView(context, component);
        } else {
            super.encodeChildren(context, component);
        }
    }

    @Override
    public void encodeEnd(FacesContext context, UIComponent component)
        throws IOException
    {
        if (context == null || component == null)
            throw new NullPointerException();
        if (!component.isRendered())
            return;

        AjaxUpdater updater = (AjaxUpdater)component;
        String clientId = updater.getClientId(context);
        String subviewId = updater.getSubviewId();
        ResponseWriter out = context.getResponseWriter();

        if (out instanceof AjaxHtmlResponseWriter) {
            String layout = (String)component.getAttributes().get("layout");
            if (layout != null && layout.equals("block")) {
                out.endElement("div");
            } else {
                out.endElement("span");
            }

            String jsvar = updater.getJsvar();
            String renderId = updater.getRenderId();
            if (!isEmpty(jsvar) && !isEmpty(renderId)) {
                String script =
                    "<script type=\"text/javascript\" language=\"Javascript\">" +
                    "var " + jsvar + " = new OM.ajax.Updater('" +
                    getActionURL(context) + "','" +
                    renderId + "'," +
                    updater.getUpdate() + ");" +
                    "</script>";
                out.write(script);

                // needed for client update
                ((AjaxHtmlResponseWriter)out).setViewStateChanged(false);
            }

            if (FacesUtils.isTransientStateSupported(context)) {
                ((AjaxHtmlResponseWriter)out).addRequestParameter(clientId, subviewId);
            }
        } else if (out instanceof AjaxResponseWriter) {
            if (FacesUtils.isTransientStateSupported(context)) {
                ((AjaxResponseWriter)out).addRequestParameter(clientId, subviewId);
            }
        }
    }

    protected void renderSubview(FacesContext context, AjaxUpdater component)
        throws IOException
    {
        ExternalContext ectx = context.getExternalContext();
        if (!(ectx.getRequest() instanceof HttpServletRequest
              && ectx.getResponse() instanceof HttpServletResponse))
            return;

        String url = component.getSubviewId();
        boolean isNewView = component.isNewView();
        component.setNewView(false);

        ServletContext c = (ServletContext)ectx.getContext();
        String contextPath = component.getContext();
        if (contextPath != null) {
            c = c.getContext(contextPath);
            if (c == null) {
                throw new FacesException("Context not found: " + contextPath);
            }
        }

        RequestDispatcher rd = c.getRequestDispatcher(url);
        if (url == null || rd == null) {
            throw new FacesException("Resource invalid: " + url);
        }
        
        String resourceURL = url.lastIndexOf('?') >= 0 ? url.substring(0, url.lastIndexOf('?')) : url;
        if(c.getResource(resourceURL) == null) {
            throw new FacesException("Resource not found: " + url);
        }

        // save our view, the target resource will build its own view
        UIViewRoot ourView = context.getViewRoot();

        UIViewRoot targetView = new UIViewRootWrapper(ourView, null);
        targetView.setViewId(ourView.getViewId());
        targetView.setLocale(ourView.getLocale());
        targetView.setRenderKitId(ourView.getRenderKitId());

        if (!isNewView) {
            // must rebuild existing view to make component ID uniqueness.
            targetView.getChildren().addAll(component.getChildren());
        }

        context.setViewRoot(targetView);

        // include the resource, using our custom wrapper
        ResponseWrapper response =
            new ResponseWrapper((HttpServletResponse)ectx.getResponse());

        // ensure component ID uniqueness
        restorePageCounter(context, component, url);

        try {
            FacesUtils.beginView(context); // inform nested view don't render it's contents
            rd.include((HttpServletRequest)ectx.getRequest(), response);
        } catch (Exception ex) {
            throw new FacesException(ex);
        } finally {
            // restore our view
            context.setViewRoot(ourView);
            FacesUtils.endView(context);
        }

        // disallow inappropriate response codes
        if (response.getStatus() < 200 || response.getStatus() > 299) {
            throw new FacesException(response.getStatus() + " " + url);
        }

        if (targetView.getChildCount() == 0) {
            component.getChildren().clear();
            
            // assume the target resource is not a JSF page, render
            // output content of target resource.
            String charEncoding = component.getCharEncoding();
            String content = response.getString(charEncoding);

            ResponseWriter out = context.getResponseWriter();
            if (out instanceof AjaxResponseWriter) {
                String clientId = component.getClientId(context);
                ((AjaxResponseWriter)out).writeInnerHtmlScript(clientId, content);
            } else {
                out.write(content);
            }
            return;
        }

        // apply model bindings
        ModelBindingFactory.applyModelBindings(context, PhaseId.RENDER_RESPONSE, url, targetView);

        // replace children of AjaxUpdater with the new subview
        component.getChildren().clear();
        // consume resources in the subview
        if (!isAjaxResponse(context)) {
            ResourceManager rm = ResourceManager.getInstance(context);
            rm.consumeContainerResources(context, targetView);
            rm.consumeResources(context, targetView);
            rm.consumeInitScriptByMeta(context, targetView);
        }
        
        component.getChildren().addAll(targetView.getChildren());

        // adjust relative path
        Map<String,Object> requestMap = ectx.getRequestMap();
        Object savedPath = requestMap.get(SERVLET_PATH_ATTR);
        requestMap.put(SERVLET_PATH_ATTR, url);

        try {
            if (isNewView && isAjaxResponse(context)) {
                encodeNewView(context, component);
            } else {
                // render subview in place
                encodeViewBody(context, component);
            }
        } finally {
            // restore relative path
            requestMap.put(SERVLET_PATH_ATTR, savedPath);
        }
    }

    protected void buildSubview(FacesContext context, ViewBuilder builder, AjaxUpdater updater)
        throws IOException
    {
        if (updater.isNewView() || !updater.viewRestored()) {
            // save our view, the target resource will build its own view
            UIViewRoot ourView = context.getViewRoot();

            UIViewRoot targetView = new UIViewRootWrapper(ourView, updater.getId());
            targetView.setViewId(ourView.getViewId());
            targetView.setLocale(ourView.getLocale());
            targetView.setRenderKitId(ourView.getRenderKitId());
            targetView.setParent(updater);

            try {
                // build subview
                context.setViewRoot(targetView);
                builder.buildSubview(context, updater.getSubviewId(), targetView);

                // apply model bindings
                ModelBindingFactory.applyModelBindings(
                    context, PhaseId.RENDER_RESPONSE, updater.getSubviewId(), targetView);
            } finally {
                context.setViewRoot(ourView);
            }

            // consume resoures in the subview
            if (!isAjaxResponse(context)) {
                ResourceManager rm = ResourceManager.getInstance(context);
                rm.consumeContainerResources(context, targetView);
                rm.consumeResources(context, targetView);
                rm.consumeInitScriptByMeta(context, targetView);
            }

            // replace children of AjaxUpdater with the new subview
            updater.getChildren().clear();
            updater.getChildren().addAll(targetView.getChildren());
        }

        if (updater.isNewView() && isAjaxResponse(context)) {
            encodeNewView(context, updater);
        } else {
            // render subview in place
            encodeViewBody(context, updater);
        }

        updater.setNewView(false);
        updater.viewRestored(false);
    }

    protected void encodeNewView(FacesContext context, UIComponent component)
        throws IOException
    {
        // render the subtree into a temporary buffer
        AjaxResponseWriter out = (AjaxResponseWriter)context.getResponseWriter();
        StringWriter strWriter = new StringWriter();
        AjaxHtmlResponseWriter inner = out.cloneWithHtmlWriter(strWriter);
        ResourceManager rm = ResourceManager.getInstance(context);

        context.setResponseWriter(inner);
        
        rm.consumeContainerResources(context, component);
        rm.consumeResources(context, component);
        rm.consumeInitScriptByMeta(context, component);
        encodeViewBody(context, component);
        context.setResponseWriter(out);

        // render updater body content, eval scripts in the body content.
        String clientId = component.getClientId(context);
        String content = HtmlEncoder.enquote(strWriter.toString(), '\'');
        rm.encodeBegin(context);
        out.writeScript("OM.H('" + clientId + "'," + content + ");\n");
        rm.encodeEnd(context);
        rm.reset();

        // should send back new view state
        out.setViewStateChanged(false);
    }

    protected void encodeViewBody(FacesContext context, UIComponent component)
        throws IOException
    {
        UIComponent root = FacesUtils.getHtmlPage(component);
        if (root == null) {
            root = component;
        } else {
            if (logger.isLoggable(Level.FINE)) {
                logger.fine(_T(UI_IGNORING_NESTED_PAGE_TAG, component.getId()));
            }
        }

        for (UIComponent child : root.getChildren()) {
            child.encodeAll(context);
        }
    }

    private void restorePageCounter(FacesContext context, UIComponent component, String url) {
        /*
         * XXX Hack standard JSF component ID generation to set a request
         * scoped attribute which contains an AtomicInteger which to use
         * to increment the PageContext count. The counter is used by JSF
         * tags to generate unique ID for components. Because we create
         * the subview dynamicly so the PageContext counter cannot keep
         * unique for each subview generation, to solve this problem we
         * must keep the PageContext counter in the component state and
         * use it to restore previously generated component ID.
         */

        // restore previously used PageContext counter.
        String idKey = JAVAX_FACES_PAGECONTEXT_COUNTER + url;
        Integer pcId = (Integer)component.getAttributes().get(idKey);
        if (pcId == null) {
            // create a random PageContext counter. the default PageContext
            // counter is some small number, we create larger one.
            do {
                int newId = prngPcId.nextInt();
                if (newId > 1000) {
                    pcId = newId;
                    break;
                }
            } while (true);
            component.getAttributes().put(idKey, pcId);
        }

        Map<String,Object> requestMap = context.getExternalContext().getRequestMap();
        AtomicInteger pcc = (AtomicInteger)requestMap.get(JAVAX_FACES_PAGECONTEXT_COUNTER);
        if (pcc == null) {
            pcc = new AtomicInteger();
            requestMap.put(JAVAX_FACES_PAGECONTEXT_COUNTER, pcc);
        }
        pcc.set(pcId);
    }

    /** A UIViewRoot wrapper that create unique ID. */
    private static final class UIViewRootWrapper extends UIViewRoot {
        private UIViewRoot wrapped;
        private String base;
        private int lastId = 0;

        public UIViewRootWrapper(UIViewRoot wrapped, String base) {
            this.wrapped = wrapped;

            if (base != null) {
                if (!base.startsWith(UNIQUE_ID_PREFIX))
                    base = UNIQUE_ID_PREFIX + base;
                this.base = base;
            }
        }

        @Override public String createUniqueId() {
            if (base == null) {
                return wrapped.createUniqueId();
            } else {
                return base + "_" + (lastId++);
            }
        }
    }

    /** Response wrapper to retrieve results as Strings. */
    private static final class ResponseWrapper extends HttpServletResponseWrapper {
        private StringWriter strWriter = new StringWriter();
        private ByteArrayOutputStream byteStream = new ByteArrayOutputStream();

        private ServletOutputStream sos = new ServletOutputStream() {
            public void write(int b) throws IOException {
                byteStream.write(b);
            }
            public void write(byte[] b, int off, int len) throws IOException {
                byteStream.write(b, off, len);
            }
        };

        /** True if getWriter() was called. */
        private boolean isWriterUsed;

        /** True if getOutputStream() was called. */
        private boolean isStreamUsed;

        /** The HTTP status set by the target. */
        private int status = 200;

        public ResponseWrapper(HttpServletResponse wrapped) {
            super(wrapped);
        }

        @Override
        public PrintWriter getWriter() throws IOException {
            if (isStreamUsed)
                throw new IllegalStateException("A writer is already in used.");
            isWriterUsed = true;
            return new PrintWriter(strWriter);
        }

        @Override
        public ServletOutputStream getOutputStream() {
            if (isWriterUsed)
                throw new IllegalStateException("A output stream is already in used.");
            isStreamUsed = true;
            return sos;
        }

        @Override
        public void setContentType(String x) {
            // has no effect
        }

        @Override
        public void setLocale(Locale x) {
            // has no effect
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public int getStatus() {
            return status;
        }

        /**
         * Retrieves the buffered output.
         */
        public String getString(String charEncoding) throws UnsupportedEncodingException {
            if (isWriterUsed) {
                return strWriter.toString();
            } else if (isStreamUsed) {
                if (isEmpty(charEncoding))
                    charEncoding = this.getCharacterEncoding();
                if (isEmpty(charEncoding))
                    charEncoding = DEFAULT_ENCODING;
                return byteStream.toString(charEncoding);
            } else {
                return "";
            }
        }
    }

    private static boolean isEmpty(String s) {
        return (s == null) || (s.length() == 0);
    }
}
