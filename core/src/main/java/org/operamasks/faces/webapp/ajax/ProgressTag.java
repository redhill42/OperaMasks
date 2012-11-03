/*
 * $Id: ProgressTag.java,v 1.9 2007/07/02 07:38:08 jacky Exp $
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

import javax.el.MethodExpression;
import javax.el.ValueExpression;
import javax.faces.component.UIComponent;
import org.operamasks.faces.webapp.html.HtmlBasicELTag;
import org.operamasks.faces.component.ajax.AjaxProgress;

/**
 * @jsp.tag name="progress" body-content="JSP"
 * description_zh_CN="表示一个进度支持组件，不可视，通常与UIProgressBar组件（表示进度）
 *   和HtmlOutputText组件（表示进度消息）连用，为这些组件提供后台逻辑支持。该组件只能用于AJAX RenderKit下。"
 */
public class ProgressTag extends HtmlBasicELTag
{
    private MethodExpression action;

    public String getRendererType() {
        return "org.operamasks.faces.AjaxProgress";
    }

    public String getComponentType() {
        return AjaxProgress.COMPONENT_TYPE;
    }
    
    /**
     * @jsp.attribute requird="false" type="java.lang.String"
     * description_zh_CN="客户端脚本使用的javascript变量名，引用脚本中OM.ajax.Progress的对象，
     *   在客户端脚本中可以调用这个变量的start(), stop(), pause(), resume(), poll()方法操纵Progress对象的行为。
     *   可以调用isRunning(), isStopped(), isPause(), isCompleted(), isFailed()方法得到Progress对象的状态。"
     */
    public void setJsvar(ValueExpression jsvar) {
        setValueExpression("jsvar", jsvar);
    }

    /**
     * @jsp.attribute requird="false" type="java.lang.String"
     * description_zh_CN="空格分隔的组件id列表，每个id所代表的组件或者是UIProgressBar，
     *   或者是HtmlOutputText，这些组件将获得该组件提供的服务。
     *   <p>
     *   具体的，该组件会把自己的状态值传给UIProgressBar类型的组件，
     *   把状态文本传给HtmlOutputText类型的组件作为innerHTML的值。"
     */
    public void setFor(ValueExpression _for) {
        setValueExpression("for", _for);
    }

    /**
     * @jsp.attribute requird="false" type="int"
     * description_zh_CN = "客户端脚本中Progress对象自动与服务器端交互的时间间隔，单位为秒。默认为1秒。
     *   如果在间隔指定的时间未到之前，客户主动通过与服务器端交互发出请求以图改变Progress的状态，
     *   则该间隔的剩余计时取消，在处理了服务器端的响应后，如果Progress还处于运行状态，再重新开始计时。"
     */
    public void setInterval(ValueExpression interval) {
        setValueExpression("interval", interval);
    }

    /**
     * @jsp.attribute requird="false" type="boolean"
     * description_zh_CN = "如果为true，则在页面的onload事件中自动向服务器发出start的请求；
     *   如果为false，则需要用户自己主动发出start请求。默认值为false。"
     */
    public void setStart(ValueExpression start) {
        setValueExpression("start", start);
    }
    
    /**
     * @jsp.attribute method-signature="void action(org.operamasks.faces.component.ajax.ProgressStatus)" required="false"
     * description_zh_CN="服务器端执行动作的方法绑定，在服务器端收到客户端发出的请求后会得到调用。
     *   这个表达式必须对应到一个公有方法，带有一个ProgressStatus参数，且具有类型为void的返回值。"
     */
    public void setAction(MethodExpression action) {
        this.action = action;
    }

    /**
     * @jsp.attribute requird="false" type="java.lang.String"
     * description_zh_CN="用户提供的一段javascript脚本，在每次接收到服务器端的响应以后会执行"
     */
    public void setOnstatechange(ValueExpression onstatechange) {
        setValueExpression("onstatechange", onstatechange);
    }

    protected void setProperties(UIComponent component) {
        super.setProperties(component);

        AjaxProgress progress = (AjaxProgress)component;
        if (action != null)
            progress.setAction(action);
    }

    public void release() {
        super.release();
        action = null;
    }
}
