/*
 * $Id: SimpleHtmlEditorTag.java,v 1.2 2008/03/24 06:04:28 lishaochuan Exp $
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

import org.operamasks.faces.component.widget.UISimpleHtmlEditor;
import org.operamasks.faces.webapp.html.HtmlBasicELTag;


public class SimpleHtmlEditorTag extends HtmlBasicELTag
{

    public String getComponentType() {
        return UISimpleHtmlEditor.COMPONENT_TYPE;
    }

    public String getRendererType() {
        return UISimpleHtmlEditor.RENDERER_TYPE;
    }

    /**
     * @jsp.attribute type="java.lang.String"
     */
    public void setJsvar(ValueExpression jsvar) {
        setValueExpression("jsvar", jsvar);
    }

    /**
     * @jsp.attribute type="boolean"
     */
    public void setEnableAlignments(ValueExpression enableAlignments) {
        setValueExpression("enableAlignments", enableAlignments);
    }
    
    /**
     * @jsp.attribute type="boolean"
     */
    public void setEnableColors(ValueExpression enableColors) {
        setValueExpression("enableColors", enableColors);
    }
    
    /**
     * @jsp.attribute type="boolean"
     */
    public void setEnableFont(ValueExpression enableFont) {
        setValueExpression("enableFont", enableFont);
    }
    
    /**
     * @jsp.attribute type="boolean"
     */
    public void setEnableFontSize(ValueExpression enableFontSize) {
        setValueExpression("enableFontSize", enableFontSize);
    }
    
    /**
     * @jsp.attribute type="boolean"
     */
    public void setEnableFormat(ValueExpression enableFormat) {
        setValueExpression("enableFormat", enableFormat);
    }
    
    /**
     * @jsp.attribute type="boolean"
     */
    public void setEnableLinks(ValueExpression enableLinks) {
        setValueExpression("enableLinks", enableLinks);
    }
    
    /**
     * @jsp.attribute type="boolean"
     */
    public void setEnableLists(ValueExpression enableLists) {
        setValueExpression("enableLists", enableLists);
    }
    
    /**
     * @jsp.attribute type="boolean"
     */
    public void setEnableSourceEdit(ValueExpression enableSourceEdit) {
        setValueExpression("enableSourceEdit", enableSourceEdit);
    }
    
    /**
     * @jsp.attribute type="boolean"
     */
    public void setAutoHeight(ValueExpression autoHeight) {
        setValueExpression("autoHeight", autoHeight);
    }

    /**
     * @jsp.attribute type="java.lang.Object"
     */
    public void setValue(ValueExpression value) {
        setValueExpression("value", value);
    }

    /**
     * @jsp.attribute type="int"
     */
    public void setWidth(ValueExpression width) {
        setValueExpression("width", width);
    }
    
    /**
     * @jsp.attribute type="int"
     */
    public void setHeight(ValueExpression height) {
        setValueExpression("height", height);
    }
}
