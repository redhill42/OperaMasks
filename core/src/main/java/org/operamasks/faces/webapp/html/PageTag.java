/*
 * $Id: PageTag.java,v 1.6 2007/08/24 05:12:16 daniel Exp $
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

package org.operamasks.faces.webapp.html;

import javax.el.ValueExpression;
import javax.faces.component.UIComponent;
import javax.faces.component.UIComponentBase;
import javax.faces.context.FacesContext;
import javax.faces.webapp.UIComponentClassicTagBase;
import javax.servlet.jsp.JspException;
import org.operamasks.faces.component.html.HtmlPage;

/**
 * @deprecated This tag should not be used anymore.
 */
@Deprecated
public class PageTag extends HtmlBasicELTag
{
    public String getRendererType() {
        return "org.operamasks.faces.HtmlPage";
    }

    public String getComponentType() {
        return HtmlPage.COMPONENT_TYPE;
    }

    public void setSkin(ValueExpression skin) {
        setValueExpression("skin", skin);
    }
    
    public void setBgcolor(ValueExpression bgcolor) {
        setValueExpression("bgcolor", bgcolor);
    }
    
    public void setDir(ValueExpression dir) {
        setValueExpression("dir", dir);
    }

    public void setLang(ValueExpression lang) {
        setValueExpression("lang", lang);
    }

    public void setStyle(ValueExpression style) {
        setValueExpression("style", style);
    }

    public void setStyleClass(ValueExpression styleClass) {
        setValueExpression("styleClass", styleClass);
    }

    public void setTitle(ValueExpression title) {
        setValueExpression("title", title);
    }

    public void setOnload(ValueExpression onload) {
        setValueExpression("onload", onload);
    }

    public void setOnunload(ValueExpression onunload) {
        setValueExpression("onunload", onunload);
    }
    
    @Override
    protected UIComponent createComponent(FacesContext context, String newId) throws JspException {
        // Check to see if the tag is enclosed by another PageTag.
        UIComponentClassicTagBase parentTag = getParentUIComponentClassicTagBase(pageContext);
        if (parentTag != null) {
            UIComponent parent = parentTag.getComponentInstance();
            while (parent != null) {
                if (parent instanceof HtmlPage) {
                    // Create passthrough component to eliminate this PageTag
                    Passthrough passthrough = new Passthrough();
                    passthrough.setId(newId);
                    return passthrough;
                }
                parent = parent.getParent();
            }
        }

        return super.createComponent(context, newId);
    }

    @Override
    protected void setProperties(UIComponent component) {
        if (component instanceof HtmlPage) {
            super.setProperties(component);
        }
    }

    public static final class Passthrough extends UIComponentBase {
        public String getFamily() {
            return "org.operamasks.faces.Passthrough";
        }
    }
}
