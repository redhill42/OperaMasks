/*
 * $Id: RenderGroupTag.java,v 1.6 2007/07/02 07:38:08 jacky Exp $
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

package org.operamasks.faces.webapp.ajax;

import javax.el.ValueExpression;
import javax.faces.component.UIComponent;
import org.operamasks.faces.webapp.html.HtmlBasicELTag;

/**
 * @jsp.tag name="renderGroup" body-content="JSP"
 * description_zh_CN="用于包围一个不支持AJAX的第三方组件，或任何其他动态内容，使其支持AJAX。
 * 每次渲染时通过判断被包围内容是否变化，决定是否重新渲染，所以，为了提高效率，每个renderGroup应尽可能包含少的内容，
 * 也就是说包含的粒度尽可能小，对于较多的复杂内容，可以通过多个renderGroup来解决。
 * 
 * <p>
 * 该组件在支持AJAX的同时也具有标准的HtmlPanelGroup组件的特性。
 */
public class RenderGroupTag extends HtmlBasicELTag
{
    public String getRendererType() {
        return "org.operamasks.faces.RenderGroup";
    }

    public String getComponentType() {
        return javax.faces.component.html.HtmlPanelGroup.COMPONENT_TYPE;
    }

    /**
     * @jsp.attribute requird="false" type="java.lang.String"
     * description = "The type of layout markup to use when rendering this group.
     *   If the value is \"block\" the renderer must produce an HTML
     *   \"div\" element.  Otherwise HTML "span" element must
     *   be produced."
     * description_zh_CN="渲染这个组时使用的布局类型。
     *   如果这个值是\"block\", 则渲染成HTML的\"div\"元素，否则渲染成\"span\"元素。"
     */
    public void setLayout(ValueExpression layout) {
        setValueExpression("layout", layout);
    }

    /**
     * @jsp.attribute required="false" type="java.lang.String"
     * description="CSS style(s) to be applied when this component is rendered."
     * description_zh_CN="组件渲染时应用到组件的CSS风格，对应于元素的style属性。"
     */
    public void setStyle(ValueExpression style) {
        setValueExpression("style", style);
    }

    /**
     * @jsp.attribute required="false" type="java.lang.String"
     * description="Space-separated list of CSS style class(es) to be applied when
     *   this element is rendered.  This value must be passed through
     *   as the \"class\" attribute on generated markup."
     * description_zh_CN="空格分隔的CSS风格类名列表，渲染时作为元素的\"class\"属性"
     */
    public void setStyleClass(ValueExpression styleClass) {
        setValueExpression("styleClass", styleClass);
    }

    protected void setProperties(UIComponent component) {
        super.setProperties(component);

        // set default "layout" to "block" because this tag is often
        // used to enclose an non-AJAX component tag to support AJAX.
        if (component.getAttributes().get("layout") == null) {
            component.getAttributes().put("layout", "block");
        }
    }
}
