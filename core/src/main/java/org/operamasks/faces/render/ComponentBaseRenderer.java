/*
 * $Id: ComponentBaseRenderer.java,v 1.4 2008/01/23 05:33:07 yangdong Exp $
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

package org.operamasks.faces.render;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import org.operamasks.faces.render.ajax.AjaxResponseWriter;
import org.operamasks.faces.render.html.HtmlRenderer;
import org.operamasks.faces.render.html.HtmlResponseWriter;

/**
 * 所有组件的渲染器基类，根据请求来源的简化渲染过程到HtmlBasicRenderer和AjaxRenderer接口中
 */
public abstract class ComponentBaseRenderer extends HtmlRenderer {
    public void encodeBegin(FacesContext context, UIComponent component) throws IOException {
        if (context == null || component == null)
            throw new NullPointerException();
        
        if (!component.isRendered())
            return;
        
        if (isHtmlResponse(context) && HtmlBasicRenderer.class.isAssignableFrom(this.getClass())) {
            HtmlResponseWriter out = (HtmlResponseWriter)context.getResponseWriter();
            ((HtmlBasicRenderer)this).encodeBegin(context, component, out);
        }
        
        if (isAjaxResponse(context) && AjaxRenderer.class.isAssignableFrom(this.getClass())) {
            AjaxResponseWriter out = (AjaxResponseWriter)context.getResponseWriter();
            ((AjaxRenderer)this).encodeBegin(context, component, out);
        }
    }
    
    protected boolean isHtmlResponse(FacesContext context) {
		return (context.getResponseWriter() instanceof HtmlResponseWriter);
	}

	public void encodeChildren(FacesContext context, UIComponent component) throws IOException {
        if (context == null || component == null)
            throw new NullPointerException();
        
        if (!component.isRendered())
            return;
        
        if (isHtmlResponse(context) && HtmlBasicRenderer.class.isAssignableFrom(this.getClass())) {
            HtmlResponseWriter out = (HtmlResponseWriter)context.getResponseWriter();
            ((HtmlBasicRenderer)this).encodeChildren(context, component, out);
        }
        
        if (isAjaxResponse(context) && AjaxRenderer.class.isAssignableFrom(this.getClass())) {
            AjaxResponseWriter out = (AjaxResponseWriter)context.getResponseWriter();
            ((AjaxRenderer)this).encodeChildren(context, component, out);
        }
    }
    
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        if (context == null || component == null)
            throw new NullPointerException();
        
        if (!component.isRendered())
            return;
        
        if (isHtmlResponse(context) && HtmlBasicRenderer.class.isAssignableFrom(this.getClass())) {
            HtmlResponseWriter out = (HtmlResponseWriter)context.getResponseWriter();
            ((HtmlBasicRenderer)this).encodeEnd(context, component, out);
        }
        
        if (isAjaxResponse(context) && AjaxRenderer.class.isAssignableFrom(this.getClass())) {
            AjaxResponseWriter out = (AjaxResponseWriter)context.getResponseWriter();
            ((AjaxRenderer)this).encodeEnd(context, component, out);
        } else if (isAjaxResponse(context) && AjaxRenderer2.class.isAssignableFrom(this.getClass())) {
        	AjaxResponseWriter out = (AjaxResponseWriter)context.getResponseWriter();
            ((AjaxRenderer2)this).encodeAjax(context, component, out);
        }
    }
    
    public FacesContext getContext() {
    	return FacesContext.getCurrentInstance();
    }
}
