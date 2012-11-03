package org.operamasks.faces.render.widget;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.operamasks.faces.annotation.component.EncodeHtmlBegin;
import org.operamasks.faces.annotation.component.EncodeHtmlEnd;
import org.operamasks.faces.annotation.component.OperationListener;
import org.operamasks.faces.component.html.impl.UIIFrame;
import org.operamasks.faces.context.ServletExternalContext;
import org.operamasks.faces.render.html.HtmlResponseWriter;
import org.operamasks.faces.render.widget.yuiext.ComponentOperationManager;

public class IFrameRenderHandler {
    @EncodeHtmlBegin
    public void htmlBegin(FacesContext context, UIComponent component) throws IOException {
        UIIFrame iframe = (UIIFrame)component;
        HtmlResponseWriter out = (HtmlResponseWriter) context.getResponseWriter();
        out.startElement("iframe", component);
        out.writeAttribute("id", iframe.getClientId(context), null);
        if(iframe.getAlign() != null){
            out.writeAttribute("align", iframe.getAlign(), null);
        }
        if(iframe.getWidth() != null){
            out.writeAttribute("width", iframe.getWidth(), null);
        }
        if(iframe.getHeight() != null){
            out.writeAttribute("height", iframe.getHeight(), null);
        }
        if(iframe.getFrameborder() != null){
            out.writeAttribute("frameborder", iframe.getFrameborder(), null);
        }
        if(iframe.getName() != null){
            out.writeAttribute("name", iframe.getName(), null);
        }
        if(iframe.getScrolling() != null){
            out.writeAttribute("scrolling", iframe.getScrolling(), null);
        }
        if(iframe.getSrc() != null){
            String fixSrc = fixURL(iframe.getSrc());
            out.writeAttribute("src", fixSrc, null);
        }
        if(iframe.getStyle() != null){
            out.writeAttribute("style", iframe.getStyle(), null);
        }
    }
    
    @EncodeHtmlEnd
    public void htmlEnd(FacesContext context, UIComponent component) throws IOException {
        HtmlResponseWriter out = (HtmlResponseWriter) context.getResponseWriter();
        out.endElement("iframe");
    }
    
    @OperationListener("setSrc")
    public void setSrc(UIIFrame iframe, String url){
        FacesContext context = FacesContext.getCurrentInstance();
        String script = String.format("document.getElementById('%s').src='%s';\n", iframe.getClientId(context), fixURL(url));
        ComponentOperationManager.getInstance(context).addOperationScript(script);
    }
    
    private String fixURL(String url){
        String fixSrc = url;
        if(fixSrc.startsWith("/")){
            FacesContext context = FacesContext.getCurrentInstance();
            HttpServletRequest request = (HttpServletRequest)(context.getExternalContext().getRequest());
            String requestURL = request.getRequestURL().toString();
            String serverPath = request.getServletPath();
            requestURL = requestURL.replace(serverPath, "");
            fixSrc = requestURL + fixSrc;
        }
        return fixSrc;
    }
}
