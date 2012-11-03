/*
 * $Id: PanelBoxTag.java,v 1.3 2007/07/02 07:37:58 jacky Exp $
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
import org.operamasks.faces.component.widget.UIPanelBox;

/**
 * @jsp.tag name="panelBox" body-content="JSP"
 */
public class PanelBoxTag extends HtmlBasicELTag
{
    public String getComponentType() {
        return UIPanelBox.COMPONENT_TYPE;
    }

    public String getRendererType() {
        return UIPanelBox.RENDERER_TYPE;
    }

    /**
     * @jsp.attribute type="java.lang.String"
     */
    public void setBgcolor(ValueExpression bgcolor) {
        setValueExpression("bgcolor", bgcolor);
    }

    /**
     * @jsp.attribute type="java.lang.String"
     */
    public void setColor(ValueExpression color) {
        setValueExpression("color", color);
    }

    /**
     * @jsp.attribute type="java.lang.String"
     */
    public void setColor2(ValueExpression color2) {
        setValueExpression("color2", color2);
    }

    /**
     * @jsp.attribute type="java.lang.String"
     */
    public void setColor3(ValueExpression color2) {
        setValueExpression("color3", color2);
    }

    /**
     * @jsp.attribute type="java.lang.Integer"
     */
    public void setGradientExtent(ValueExpression gradientExtent) {
        setValueExpression("gradientExtent", gradientExtent);
    }

    /**
     * @jsp.attribute type="java.lang.Integer"
     */
    public void setBorder(ValueExpression border) {
        setValueExpression("border", border);
    }

    /**
     * @jsp.attribute type="java.lang.String"
     */
    public void setBorderColor(ValueExpression borderColor) {
        setValueExpression("borderColor", borderColor);
    }

    /**
     * @jsp.attribute type="java.lang.Integer"
     */
    public void setBorderRadius(ValueExpression borderRadius) {
        setValueExpression("borderRadius", borderRadius);
    }

    /**
     * @jsp.attribute type="java.lang.String"
     */
    public void setRoundedCorners(ValueExpression roundedCorners) {
        setValueExpression("roundedCorners", roundedCorners);
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
    public void setContentStyle(ValueExpression contentStyle) {
        setValueExpression("contentStyle", contentStyle);
    }

    /**
     * @jsp.attribute type="java.lang.String"
     */
    public void setContentStyleClass(ValueExpression contentStyleClass) {
        setValueExpression("contentStyleClass", contentStyleClass);
    }
}
