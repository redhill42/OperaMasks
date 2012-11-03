/*
 * $Id:
 *
 * Copyright (c) 2006 Operamasks Community.
 * Copyright (c) 2000-2006 Apusic Systems, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.operamasks.faces.render.widget.yuiext;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Formatter;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.component.UIForm;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.operamasks.faces.component.widget.dialog.UIDialog;
import org.operamasks.faces.render.ajax.AjaxRenderKitImpl;
import org.operamasks.faces.render.ajax.AjaxResponseWriter;
import org.operamasks.faces.render.html.HtmlRenderer;
import org.operamasks.faces.render.resource.ResourceManager;
import org.operamasks.faces.render.resource.ResourceProvider;
import org.operamasks.faces.util.FacesUtils;
import org.operamasks.faces.util.HtmlEncoder;

public class DialogRenderer extends HtmlRenderer implements ResourceProvider
{
    private final static String ON_HIDE = "_onhide";
    private final static String TMP_POSTFIX= "_tmp";
    private final static String CONTAINER_PREFIX = "_container";
    
    @Override
    public void decode(FacesContext context, UIComponent component) {
        if (context == null || component == null)
            throw new NullPointerException();
        if (!component.isRendered())
            return;
        if (!AjaxRenderKitImpl.isAjaxResponse(context))
            return;
        Map<String,String> paramMap = context.getExternalContext().getRequestParameterMap();
        String clientId = component.getClientId(context);
        String event = paramMap.get(clientId + "_EVENT");
        if (ON_HIDE.equals(event)) {
            ((UIDialog)component).setShow(false);
        }
    }
    @Override
    public void encodeBegin(FacesContext context, UIComponent component)
        throws IOException 
    {
        if (context == null || component == null)
            throw new NullPointerException();
        if (!component.isRendered())
            return;
        if (isAjaxResponse(context))
            return;

        UIDialog dialog = (UIDialog)component;
        String clientId = component.getClientId(context);
        ResponseWriter out = context.getResponseWriter();

        // dialog container div
        out.startElement("div", component);
        out.writeAttribute("id", clientId, "clientId");
        out.writeAttribute("class", "x-hidden", null);

        // dialog title div
        String title = dialog.getTitle();
        out.startElement("div", component);
        out.writeAttribute("class", "x-window-header", null);
        out.write(title == null ? "" :title);
        out.endElement("div");

        // dialog body div
        String contentStyle = dialog.getContentStyle();
        String contentStyleClass = dialog.getContentStyleClass();
        out.startElement("div", component);
        out.writeAttribute("id", clientId + CONTAINER_PREFIX, "clientId");
        
        String style = "position:relative;";
        if (contentStyle != null) {
            style += contentStyle;
        }
        out.writeAttribute( "style", style, null);
        if (contentStyleClass == null) {
            contentStyleClass = "x-window-body";
        } else {
            contentStyleClass = "x-window-body " + contentStyleClass;
        }
        out.writeAttribute("class", contentStyleClass, null);
    }
    
    @Override
    public boolean getRendersChildren() {
        return true;
    }
    
    @Override
    public void encodeChildren(FacesContext context, UIComponent component)
            throws IOException {
        if (context == null || component == null)
            throw new NullPointerException();
        if (!component.isRendered())
            return;

        if(isAjaxResponse(context)) {
            AjaxResponseWriter out = (AjaxResponseWriter)context.getResponseWriter();
            StringWriter strWriter = new StringWriter();
            ResponseWriter inner = out.cloneWithWriter(strWriter);
            ResourceManager rm = ResourceManager.getInstance(context);

            context.setResponseWriter(inner);
            for(UIComponent child : component.getChildren()) {
                rm.consumeResources(context, child);
            }
            rm.encodeBegin(context);
            out.writeScript(strWriter.toString());
            rm.encodeEnd(context);
            rm.reset();
            context.setResponseWriter(out);
        }
        super.encodeChildren(context, component);
    }
    
    @Override
    public void encodeEnd(FacesContext context, UIComponent component)
        throws IOException
    {
        if (context == null || component == null)
            throw new NullPointerException();
        if (!component.isRendered())
            return;

        if (isAjaxResponse(context)) {
            UIDialog dialog = (UIDialog)component;
            String jsvar = FacesUtils.getJsvar(context, dialog);
            String script = null;
            if (dialog.isShow()) {
                script = jsvar + ".show();";
            }
            else {
                String onhideFunc = jsvar + "_" + ON_HIDE;
                script = String.format( "%s.un('hide',%s);%s.hide();%s.on('hide',%s);\n", 
                    jsvar,
                    onhideFunc,
                    jsvar,
                    jsvar,
                    onhideFunc
                );
                script += String.format("OM.ajax.removeRequestParameter('%s');\n", component.getClientId(context) + "_EVENT");
            }
            

            AjaxResponseWriter out = (AjaxResponseWriter)context.getResponseWriter();
            out.setViewStateChanged();
            out.writeActionScript(script);
        } else {
            ResponseWriter out = context.getResponseWriter();
            out.endElement("div"); // end of dialog body div
            out.endElement("div"); // end of dialog container div
        }
    }
    
    private static final String[] CONFIGS = {
        "width", "height", "left", "top", "draggable", "resizable",
        "collapsible", "closable", "autoScroll", "modal"
    };
    
    public void provideResource(ResourceManager rm, UIComponent component) {
        YuiExtResource resource = YuiExtResource.register(rm, "Ext.Dialogs");

        FacesContext context = FacesContext.getCurrentInstance();
        UIDialog dialog = (UIDialog)component;
        String clientId = dialog.getClientId(context);
        String jsvar = resource.allocVariable(dialog);

        StringBuilder buf = new StringBuilder();
        Formatter fmt = new Formatter(buf);
        
        // Remove the old dialog container and add the new one to the top level
        // layer. This will guarantee the modal dialog can work with AjaxUpdater.
//        fmt.format(
//                "\nvar dialog_%1$s = document.getElementById('%2$s');" +
//                "\nif(dialog_%1$s){dialog_%1$s.parentNode.removeChild(dialog_%1$s);}" +
//                "\ndialog_%1$s = document.getElementById('%3$s');" +
//                "\nif(dialog_%1$s){" +
//                "\ndialog_%1$s.parentNode.removeChild(dialog_%1$s);" +
//                "\ndialog_%1$s.id='%2$s';" +
//                "\ndocument.body.appendChild(dialog_%s);" +
//                "\n}",
//                jsvar, clientId, clientId + TMP_POSTFIX
//        );
        
        fmt.format("%s=new Ext.Window({applyTo:'%s',\n", jsvar, clientId);
        buf.append("closeAction:'hide',\n");
        fmt.format("minimizable: %s,\n", dialog.getCollapsible());
        for (String config : CONFIGS) {
            Object value = dialog.getAttributes().get(config);
            if (value != null) {
                if (value instanceof String) {
                    value = HtmlEncoder.enquote((String)value);
                }
                fmt.format("%s:%s,\n", config, value);
            }
        }
        if (buf.charAt(buf.length()-1) == ',') {
            buf.setLength(buf.length() - 1);
        }
        fmt.format("keys: [{key: 27, fn: function(){%s.hide();}}]\n", jsvar);
        buf.append("});\n");
        fmt.format("%s.on('minimize', function(){%s.toggleCollapse();});", jsvar, jsvar);
        
        UIForm form = getParentForm(component);
        String onhide = String.format(
            "OM.ajax.addRequestParameter('%s','%s');" +
            "OM.ajax.action(%s,null,'%s',%b);",
            clientId + "_EVENT",
            ON_HIDE,
            ((form == null) ? "null" : "'" + form.getClientId(context) + "'"),
            clientId,
            false
        );
        String onhideFunc = jsvar + "_" + ON_HIDE;
        resource.addVariable(onhideFunc);
        fmt.format("%s = function(){%s};\n", onhideFunc, onhide);
        fmt.format("%s.on('hide',%s);\n", jsvar, onhideFunc);
        if (dialog.isShow()) {
            fmt.format("%s.show();\n", jsvar);
        }
        
        resource.addInitScript(buf.toString());
        resource.releaseVariable(jsvar);
        resource.releaseVariable(onhideFunc);
    }
}
