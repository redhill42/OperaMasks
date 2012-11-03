/*
 * $Id: StatusTag.java,v 1.7 2007/07/02 07:38:08 jacky Exp $
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
import org.operamasks.faces.webapp.html.HtmlBasicELTag;
import org.operamasks.faces.component.ajax.AjaxStatus;

/**
 * @jsp.tag name="status" body-content="JSP"
 * description_zh_CN="使用&lt;ajax:status&gt;标签可以在发送和完成AJAX请求时获得回调"
 */
public class StatusTag extends HtmlBasicELTag
{
    public String getRendererType() {
        return "org.operamasks.faces.AjaxStatus";
    }

    public String getComponentType() {
        return AjaxStatus.COMPONENT_TYPE;
    }

    /**
     * @jsp.attribute requird="false" type="java.lang.String"
     * description = "The type of layout markup to use when rendering \"start\" and \"stop\" group.
     *   If the value is \"block\" the renderer must produce an HTML
     *   \"div\" element.  Otherwise HTML "span" element must
     *   be produced."
     * description_zh_CN="渲染“开始”和“结束”组时使用的布局类型。
     *   如果这个值是\"block\", 则渲染成HTML的\"div\"元素，否则渲染成\"span\"元素。"
     */
    public void setLayout(ValueExpression layout) {
        setValueExpression("layout", layout);
    }

    /**
     * @jsp.attribute required="false" type="java.lang.String"
     * description_zh_CN="组件渲染时应用到组件的CSS风格，对应于元素的style属性。
     *   这个属性的值是默认值，当startStyle或stopStyle没指定时采用。"
     */
    public void setStyle(ValueExpression style) {
        setValueExpression("style", style);
    }

    /**
     * @jsp.attribute required="false" type="java.lang.String"
     * description_zh_CN="空格分隔的CSS风格类名列表，渲染时作为元素的\"class\"属性。
     *   这个属性的值是默认值，当startStyleClass或stopStyleClass没指定时采用。"
     */
    public void setStyleClass(ValueExpression styleClass) {
        setValueExpression("styleClass", styleClass);
    }

    /**
     * @jsp.attribute required="false" type="java.lang.String"
     * description_zh_CN="“开始”组渲染时应用的CSS风格，对应于元素的style属性。"
     */
    public void setStartStyle(ValueExpression startStyle) {
        setValueExpression("startStyle", startStyle);
    }

    /**
     * @jsp.attribute required="false" type="java.lang.String"
     * description_zh_CN="空格分隔的CSS风格类名列表，渲染时作为“开始”组的\"class\"属性。"
     */
    public void setStartStyleClass(ValueExpression startStyleClass) {
        setValueExpression("startStyleClass", startStyleClass);
    }

    /**
     * @jsp.attribute required="false" type="java.lang.String"
     * description_zh_CN="“结束”组渲染时应用的CSS风格，对应于元素的style属性。"
     */
    public void setStopStyle(ValueExpression stopStyle) {
        setValueExpression("stopStyle", stopStyle);
    }

    /**
     * @jsp.attribute required="false" type="java.lang.String"
     * description_zh_CN="空格分隔的CSS风格类名列表，渲染时作为“结束”组的\"class\"属性。"
     */
    public void setStopStyleClass(ValueExpression stopStyleClass) {
        setValueExpression("stopStyleClass", stopStyleClass);
    }

    /**
     * @jsp.attribute required="false" type="java.lang.String"
     * description_zh_CN="用户提供的一段Javascript脚本，在发送AJAX请求前会被执行。"
     */
    public void setOnstart(ValueExpression onstart) {
        setValueExpression("onstart", onstart);
    }

    /**
     * @jsp.attribute required="false" type="java.lang.String"
     * description_zh_CN="用户提供的一段Javascript脚本，在接收到AJAX响应后会被执行。"
     */
    public void setOnstop(ValueExpression onstop) {
        setValueExpression("onstop", onstop);
    }
}
