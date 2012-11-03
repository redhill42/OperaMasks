/*
 * $Id: LoggerTag.java,v 1.8 2008/01/30 07:58:15 yangdong Exp $
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
import org.operamasks.faces.component.ajax.AjaxLogger;

/**
 * @jsp.tag name="logger" body-content="empty"
 * description_zh_CN="一个可视的日志，显示在页面中，显示AJAX交互过程中的各种信息。"
 */
public class LoggerTag extends HtmlBasicELTag
{
    public String getRendererType() {
        return "org.operamasks.faces.AjaxLogger";
    }

    public String getComponentType() {
        return AjaxLogger.COMPONENT_TYPE;
    }
    
    /**
     * @jsp.attribute required="false" type="java.lang.String"
     * description_zh_CN="日志级别，可用值为下面之一：
     * OFF，FATAL，ERROR, WARN, INFO, DEBUG, ALL, 缺省为ALL。"
     */
    public void setLevel(ValueExpression level) {
        setValueExpression("level", level);
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
     * @jsp.attribute required="false" type="java.lang.Boolean"
     */
    public void setServerLog(ValueExpression serverLog) {
        setValueExpression("serverLog", serverLog);
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
    
    @Override
    protected void setProperties(UIComponent component) {
    	super.setProperties(component);
    	
    	AjaxLogger logger = (AjaxLogger)component;
    	
    	if (logger.getStyle() == null)
    		logger.setStyle("overflow:scroll;width:100%;height:200px;left:0px;bottom:0px;position:absolute;font-size:9pt");
    }
}
