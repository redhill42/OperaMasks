/*
 * $Id: PagingToolbarTag.java,v 1.6 2008/03/24 00:49:05 lishaochuan Exp $
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
import org.operamasks.faces.component.widget.UIPagingToolbar;

/**
 * 分页器标签
 *
 * @jsp.tag name="pagingToolbar" body-content="JSP"
 */
public class PagingToolbarTag extends HtmlBasicELTag
{
    public String getComponentType() {
        return UIPagingToolbar.COMPONENT_TYPE;
    }

    public String getRendererType() {
        return UIPagingToolbar.RENDERER_TYPE;
    }

    /**
     * @jsp.attribute required="false" type="java.lang.String"
     * 控件在客户端的js变量名
     */
    public void setJsvar(ValueExpression jsvar) {
        setValueExpression("jsvar", jsvar);
    }

    /**
     * @jsp.attribute required="true" type="java.lang.String"
     * 目标组件ID.
     */
    public void setFor(ValueExpression for_) {
        setValueExpression("for", for_);
    }

    /**
     * @jsp.attribute required="false" type="int"
     * 从记录的第多少行开始显示, default: 0
     */
    public void setStart(ValueExpression start) {
        setValueExpression("start", start);
    }

    /**
     * @jsp.attribute required="false" type="int"
     * 每页显示的记录数, default: 20
     */
    public void setPageSize(ValueExpression pageSize) {
        setValueExpression("pageSize", pageSize);
    }

//    /**
//     * @jsp.attribute type="java.lang.String"
//     */
//    public void setStyle(ValueExpression style) {
//        setValueExpression("style", style);
//    }

//    /**
//     * @jsp.attribute type="java.lang.String"
//     */
//    public void setStyleClass(ValueExpression styleClass) {
//        setValueExpression("styleClass", styleClass);
//    }

    /**
     * @jsp.attribute required="false" type="java.lang.String"
     * 显示的提示, default: 'Displaying {0} - {1} of {2}';
     */
    public void setDisplayMsg(ValueExpression displayMsg) {
        setValueExpression("displayMsg", displayMsg);
    }

    /**
     * @jsp.attribute required="false" type="java.lang.String"
     * 没有记录的时候显示的提示, : default: 'No data to display';
     */
    public void setEmptyMsg(ValueExpression emptyMsg) {
        setValueExpression("emptyMsg", emptyMsg);
    }

    /**
     * @jsp.attribute required="false" type="java.lang.String"
     * 对分页工具栏上的文字的细微调整
     */
    public void setBeforePageText(ValueExpression beforePageText) {
        setValueExpression("beforePageText", beforePageText);
    }

    /**
     * 对分页工具栏上的文字的细微调整
     *
     * @jsp.attribute required="false" type="java.lang.String"
     */
    public void setAfterPageText(ValueExpression afterPageText) {
        setValueExpression("afterPageText", afterPageText);
    }

    /**
     * 对分页工具栏上的文字的细微调整
     *
     * @jsp.attribute required="false" type="java.lang.String"
     */
    public void setFirstText(ValueExpression firstText) {
        setValueExpression("firstText", firstText);
    }

    /**
     * 对分页工具栏上的文字的细微调整
     *
     * @jsp.attribute required="false" type="java.lang.String"
     */
    public void setPrevText(ValueExpression prevText) {
        setValueExpression("prevText", prevText);
    }

    /**
     * @jsp.attribute required="false" type="java.lang.String"
     * 对分页工具栏上的文字的细微调整
     */
    public void setNextText(ValueExpression nextText) {
        setValueExpression("nextText", nextText);
    }

    /**
     * @jsp.attribute required="false" type="java.lang.String"
     * 对分页工具栏上的文字的细微调整
     */
    public void setLastText(ValueExpression lastText) {
        setValueExpression("lastText", lastText);
    }

    /**
     * @jsp.attribute required="false" type="java.lang.String"
     * 对分页工具栏上的文字的细微调整
     */
    public void setRefreshText(ValueExpression refreshText) {
        setValueExpression("refreshText", refreshText);
    }
}
