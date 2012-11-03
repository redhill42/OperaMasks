/*
 * $Id: TextAreaTag.java,v 1.5 2008/03/14 05:44:08 lishaochuan Exp $
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

import org.operamasks.faces.component.widget.UITextArea;
import org.operamasks.faces.component.widget.UITextField;

public class TextAreaTag extends TextFieldTag {
    public String getComponentType() {
        return UITextArea.COMPONENT_TYPE;
    }

    @Override
    public String getRendererType() {
        return UITextArea.DEFAULT_RENDERER_TYPE;
    }
    
    /**
     * @jsp.attribute type="boolean" 
     * description="{Boolean} preventScrollbars True to prevent scrollbars 
     *   from appearing regardless of how much text is
     *   in the field (equivalent to setting overflow: hidden, defaults to false)"
     * description_zh_CN="如果为true，表示阻止滚动条出现，而不管输入框中有多少文本。
     *   相当于设置CSS风格 overflow: hidden，缺省为false。"
     */
    public void setPreventScrollbars(ValueExpression preventScrollbars) {
        setValueExpression("preventScrollbars", preventScrollbars);
    }
    /**
     * @jsp.attribute type="int" 
     * description="{Number} growMin The minimum height to allow when grow = true (defaults to 60)"
     * description_zh_CN="当设置 grow = true 时，允许的最小高度，缺省为60。"
     */
    public void setGrowMin(ValueExpression growMin) {
        setValueExpression("growMin", growMin);
    }
    
    /**
     * @jsp.attribute type="int" 
     * description="{Number} growMax The maximum height to allow when grow = true (defaults to 1000)"
     * description_zh_CN="当设置 grow = true 时，允许的最大高度，缺省为1000。"
     */
    public void setGrowMax(ValueExpression growMax) {
        setValueExpression("growMax", growMax);
    }
}
