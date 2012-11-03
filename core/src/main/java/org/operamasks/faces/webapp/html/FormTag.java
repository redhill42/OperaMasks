/*
 * $Id: FormTag.java,v 1.7 2008/04/11 06:19:41 lishaochuan Exp $
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

import org.operamasks.faces.component.widget.UIForm;

public class FormTag extends HtmlBasicELTag
{
    public String getRendererType() {
        return "javax.faces.Form";
    }

    public String getComponentType() {
        return UIForm.COMPONENT_TYPE;
    }

    public void setPrependId(ValueExpression prependId) {
        setValueExpression("prependId", prependId);
    }

    public void setClientValidate(ValueExpression clientValidate) {
        setValueExpression("clientValidate", clientValidate);
    }
    
    public void setAccept(ValueExpression accept) {
        setValueExpression("accept", accept);
    }

    public void setAcceptcharset(ValueExpression acceptcharset) {
        setValueExpression("acceptcharset", acceptcharset);
    }

    public void setDir(ValueExpression dir) {
        setValueExpression("dir", dir);
    }

    public void setEnctype(ValueExpression enctype) {
        setValueExpression("enctype", enctype);
    }

    public void setLang(ValueExpression lang) {
        setValueExpression("lang", lang);
    }

    public void setOnclick(ValueExpression onclick) {
        setValueExpression("onclick", onclick);
    }

    public void setOndblclick(ValueExpression ondblclick) {
        setValueExpression("ondblclick", ondblclick);
    }

    public void setOnkeydown(ValueExpression onkeydown) {
        setValueExpression("onkeydown", onkeydown);
    }

    public void setOnkeypress(ValueExpression onkeypress) {
        setValueExpression("onkeypress", onkeypress);
    }

    public void setOnkeyup(ValueExpression onkeyup) {
        setValueExpression("onkeyup", onkeyup);
    }

    public void setOnmousedown(ValueExpression onmousedown) {
        setValueExpression("onmousedown", onmousedown);
    }

    public void setOnmousemove(ValueExpression onmousemove) {
        setValueExpression("onmousemove", onmousemove);
    }

    public void setOnmouseout(ValueExpression onmouseout) {
        setValueExpression("onmouseout", onmouseout);
    }

    public void setOnmouseover(ValueExpression onmouseover) {
        setValueExpression("onmouseover", onmouseover);
    }

    public void setOnmouseup(ValueExpression onmouseup) {
        setValueExpression("onmouseup", onmouseup);
    }

    public void setOnreset(ValueExpression onreset) {
        setValueExpression("onreset", onreset);
    }

    public void setOnsubmit(ValueExpression onsubmit) {
        setValueExpression("onsubmit", onsubmit);
    }

    public void setStyle(ValueExpression style) {
        setValueExpression("style", style);
    }

    public void setStyleClass(ValueExpression styleClass) {
        setValueExpression("styleClass", styleClass);
    }

    public void setTarget(ValueExpression target) {
        setValueExpression("target", target);
    }

    public void setTitle(ValueExpression title) {
        setValueExpression("title", title);
    }    
    
    public void setRich(ValueExpression rich) {
        setValueExpression("rich", rich);
    }
}
