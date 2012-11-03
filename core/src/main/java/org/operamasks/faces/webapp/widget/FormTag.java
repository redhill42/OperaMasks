/*
 * $Id: FormTag.java,v 1.8 2008/04/22 08:46:19 lishaochuan Exp $
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

package org.operamasks.faces.webapp.widget;

import javax.el.ValueExpression;

import org.operamasks.faces.webapp.html.HtmlBasicELTag;

/**
 * @jsp.tag name="form" body-content="JSP"
 */
public class FormTag extends HtmlBasicELTag
{
    public String getRendererType() {
        return "javax.faces.Form";
    }

    public String getComponentType() {
        return org.operamasks.faces.component.widget.UIForm.COMPONENT_TYPE;
    }

    /**
     * @jsp.attribute type="boolean"
     */
    public void setPrependId(ValueExpression prependId) {
        setValueExpression("prependId", prependId);
    }

    /**
     * @jsp.attribute type="boolean"
     */
    public void setClientValidate(ValueExpression clientValidate) {
        setValueExpression("clientValidate", clientValidate);
    }

    /**
     * @jsp.attribute type="java.lang.String"
     */
    public void setAccept(ValueExpression accept) {
        setValueExpression("accept", accept);
    }

    /**
     * @jsp.attribute type="java.lang.String"
     */
    public void setAcceptcharset(ValueExpression acceptcharset) {
        setValueExpression("acceptcharset", acceptcharset);
    }

    /**
     * @jsp.attribute type="java.lang.String"
     */
    public void setDir(ValueExpression dir) {
        setValueExpression("dir", dir);
    }

    /**
     * @jsp.attribute type="java.lang.String"
     */
    public void setEnctype(ValueExpression enctype) {
        setValueExpression("enctype", enctype);
    }

    /**
     * @jsp.attribute type="java.lang.String"
     */
    public void setLang(ValueExpression lang) {
        setValueExpression("lang", lang);
    }

    /**
     * @jsp.attribute type="java.lang.String"
     */
    public void setOnclick(ValueExpression onclick) {
        setValueExpression("onclick", onclick);
    }

    /**
     * @jsp.attribute type="java.lang.String"
     */
    public void setOndblclick(ValueExpression ondblclick) {
        setValueExpression("ondblclick", ondblclick);
    }

    /**
     * @jsp.attribute type="java.lang.String"
     */
    public void setOnkeydown(ValueExpression onkeydown) {
        setValueExpression("onkeydown", onkeydown);
    }

    /**
     * @jsp.attribute type="java.lang.String"
     */
    public void setOnkeypress(ValueExpression onkeypress) {
        setValueExpression("onkeypress", onkeypress);
    }

    /**
     * @jsp.attribute type="java.lang.String"
     */
    public void setOnkeyup(ValueExpression onkeyup) {
        setValueExpression("onkeyup", onkeyup);
    }

    /**
     * @jsp.attribute type="java.lang.String"
     */
    public void setOnmousedown(ValueExpression onmousedown) {
        setValueExpression("onmousedown", onmousedown);
    }

    /**
     * @jsp.attribute type="java.lang.String"
     */
    public void setOnmousemove(ValueExpression onmousemove) {
        setValueExpression("onmousemove", onmousemove);
    }

    /**
     * @jsp.attribute type="java.lang.String"
     */
    public void setOnmouseout(ValueExpression onmouseout) {
        setValueExpression("onmouseout", onmouseout);
    }

    /**
     * @jsp.attribute type="java.lang.String"
     */
    public void setOnmouseover(ValueExpression onmouseover) {
        setValueExpression("onmouseover", onmouseover);
    }

    /**
     * @jsp.attribute type="java.lang.String"
     */
    public void setOnmouseup(ValueExpression onmouseup) {
        setValueExpression("onmouseup", onmouseup);
    }

    /**
     * @jsp.attribute type="java.lang.String"
     */
    public void setOnreset(ValueExpression onreset) {
        setValueExpression("onreset", onreset);
    }

    /**
     * @jsp.attribute type="java.lang.String"
     */
    public void setOnsubmit(ValueExpression onsubmit) {
        setValueExpression("onsubmit", onsubmit);
    }

    /**
     * @jsp.attribute type="java.lang.String"
     */
    public void setOnbeforerequest(ValueExpression onbeforerequest) {
        setValueExpression("onbeforerequest", onbeforerequest);
    }

    /**
     * @jsp.attribute type="java.lang.String"
     */
    public void setOnsuccess(ValueExpression onsuccess) {
        setValueExpression("onsuccess", onsuccess);
    }

    /**
     * @jsp.attribute type="java.lang.String"
     */
    public void setOnfailure(ValueExpression onfailure) {
        setValueExpression("onfailure", onfailure);
    }

    /**
     * @jsp.attribute type="java.lang.String"
     */
    public void setOncomplete(ValueExpression oncomplete) {
        setValueExpression("oncomplete", oncomplete);
    }

    /**
     * @jsp.attribute type="java.lang.String"
     */
    public void setStyle(ValueExpression style) {
        setValueExpression("style", style);
    }

    /**
     * @jsp.attribute type="java.lang.String"
     */
    public void setStyleClass(ValueExpression styleClass) {
        setValueExpression("styleClass", styleClass);
    }

    /**
     * @jsp.attribute type="java.lang.String"
     */
    public void setTarget(ValueExpression target) {
        setValueExpression("target", target);
    }

    /**
     * @jsp.attribute type="java.lang.String"
     */
    public void setTitle(ValueExpression title) {
        setValueExpression("title", title);
    }
    
    /**
     * @jsp.attribute type="boolean"
     */
    public void setRich(ValueExpression rich) {
        setValueExpression("rich", rich);
    }
    
    /**
     * @jsp.attribute type="java.lang.String"
     */
    public void setValidationEvent(ValueExpression validationEvent) {
        setValueExpression("validationEvent", validationEvent);
    }
    
    /**
     * @jsp.attribute type="boolean"
     */
    public void setValidateOnBlur(ValueExpression validateOnBlur) {
        setValueExpression("validateOnBlur", validateOnBlur);
    }
    
    /**
     * @jsp.attribute type="boolean"
     */
    public void setTransient(ValueExpression transientFlag) {
        setValueExpression("transient", transientFlag);
    }
    
    /**
     * @jsp.attribute type="boolean"
     */
    public void setEnableExtValidateErrorMsg(ValueExpression enableExtValidateErrorMsg) {
        setValueExpression("enableExtValidateErrorMsg", enableExtValidateErrorMsg);
    }
    
    /**
     * @jsp.attribute type="java.lang.String"
     */
    public void setGroupId(ValueExpression groupId) {
        setValueExpression("groupId", groupId);
    }
}
