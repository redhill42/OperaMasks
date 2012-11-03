/*
 * $Id: AjaxSlideMessageRenderer.java,v 1.11 2007/07/02 07:37:50 jacky Exp $
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
 *
 */

package org.operamasks.faces.render.widget.yuiext;

import java.io.IOException;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.operamasks.faces.component.widget.MessageBean;
import org.operamasks.faces.component.widget.UISlideMessage;
import org.operamasks.faces.render.resource.ResourceProvider;
import org.operamasks.faces.render.resource.ResourceManager;
import org.operamasks.faces.render.ajax.AjaxResponseWriter;
import org.operamasks.faces.render.html.HtmlRenderer;

public class AjaxSlideMessageRenderer extends HtmlRenderer
    implements ResourceProvider
{
    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        if (component.isRendered() && isAjaxHtmlResponse(context))
            renderAjaxHtmlResponse(context, component);
        else
            renderAjaxReponse(context, component);
    }

    private void renderAjaxReponse(FacesContext context, UIComponent component) throws IOException {
        if (!isAjaxResponse(context))
            return;

        UISlideMessage slideMessage = (UISlideMessage)component;
        List<MessageBean> messages = slideMessage.getMessages();
        if (messages == null || messages.size() == 0)
            return;

        AjaxResponseWriter writer = (AjaxResponseWriter)context.getResponseWriter();
        String messgeScript = "slideMessage.addMessage('{0}','{1}');";
        for (MessageBean message : messages) {
            writer.writeScript(String.format(messgeScript, message.getTitle(), message.getContent()));
        }
        writer.writeScript("slideMessage.show();");
        messages.clear();
    }

    private void renderAjaxHtmlResponse(FacesContext context, UIComponent component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        UISlideMessage slideMessage = (UISlideMessage)component;
        String showMessageScript = "window.slideMessage = new SlideMessage({effectTime: {0},pauseTime : {1},position : '{2}'});";
        
        writer.write(String.format(showMessageScript, slideMessage.getEffectTime(), slideMessage.getPauseTime(), slideMessage.getPosition()));
    }

    public void provideResource(ResourceManager rm, UIComponent component) {
        YuiExtResource.register(rm);
        rm.registerScriptResource("examples.js");
        rm.registerCssResource("examples.css");
        rm.registerCssResource("box.css");
    }
}