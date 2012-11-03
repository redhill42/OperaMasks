/*
 * $Id: TimerTag.java,v 1.8 2007/07/02 07:38:08 jacky Exp $
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

import javax.faces.component.UIComponent;
import javax.faces.event.MethodExpressionActionListener;
import javax.el.ValueExpression;
import javax.el.MethodExpression;
import org.operamasks.faces.component.ajax.AjaxTimer;
import org.operamasks.faces.webapp.html.HtmlBasicELTag;

/**
 * @jsp.tag name="timer" body-content="JSP"
 * description_zh_CN="一个定时器，既可以定时执行客户端脚本，也可以定时执行服务器端动作。
 *   通过定时向服务器端发送AJAX请求，向服务器端传递参数。"
 */
public class TimerTag extends HtmlBasicELTag
{
    private MethodExpression action;
    private MethodExpression actionListener;

    public String getRendererType() {
        return "org.operamasks.faces.AjaxTimer";
    }

    public String getComponentType() {
        return AjaxTimer.COMPONENT_TYPE;
    }

    /**
     * @jsp.attribute requird="false" type="java.lang.String"
     * description_zh_CN="客户端脚本使用的javascript变量名，引用脚本中OM.ajax.Timer的对象，
     *   在客户端脚本中可以调用这个变量的如下方法来控制Timer:<p><blockquote><pre>
     *      addParameter(name, value) 为Timer的服务器端交互添加请求参数 
     *      schedule()                启动Timer
     *      cancel()                  取消Timer
     *   </pre></blockquote>"
     */
    public void setJsvar(ValueExpression jsvar) {
        setValueExpression("jsvar", jsvar);
    }

    /**
     * @jsp.attribute required="false" type="int"
     * description_zh_CN="从启动开始，经过多少时间，Timer开始第一次动作，单位为秒。"
     */
    public void setDelay(ValueExpression delay) {
        setValueExpression("delay", delay);
    }

    /**
     * @jsp.attribute required="false" type="int"
     * description_zh_CN="Timer每次动作的时间间隔，单位为秒。"
     */
    public void setPeriod(ValueExpression period) {
        setValueExpression("period", period);
    }

    /**
     * @jsp.attribute required="false" type="boolean"
     * description_zh_CN="Timer是否自动启动。true，自动启动；false，需要客户明确调用schedule()方法启动。"
     */
    public void setStart(ValueExpression start) {
        setValueExpression("start", start);
    }

    /**
     * @jsp.attribute method-signature="java.lang.Object action()" required="false"
     * description_zh_CN="服务器端执行动作的方法绑定，定时器定时时间到达的时候调用。
     *   这个表达式必须对应到一个公有的不带参数的方法，这个方法返回一个字符串（逻辑输出），
     *   这个字符串用来被传递个这个应用的NavigationHandler。"
     */
    public void setAction(MethodExpression action) {
        this.action = action;
    }

    /**
     * @jsp.attribute method-signature="void actionListener(javax.faces.event.ActionEvent)" required="false"
     * description_zh_CN="代表一个行动侦听器方法的方法绑定，定时器定时时间到达的时候会得到通知。
     *    这个表达式必须对应到一个公有方法，带有一个ActionEvent参数，且具有类型为void的返回值。"
     */
    public void setActionListener(MethodExpression actionListener) {
        this.actionListener = actionListener;
    }

    /**
     * @jsp.attribute type="boolean" required="false"
     * description_zh_CN="一个标志，指出当该定时器定时时间到达时，立即向感兴趣的侦听器和行动处理器发出通知
     *   （也就是说在应用请求值阶段），而不是等到调用应用阶段。"
     */
    public void setImmediate(ValueExpression immediate) {
        setValueExpression("immediate", immediate);
    }

    /**
     * @jsp.attribute required="false" type="java.lang.String"
     * description_zh_CN = "一段Javascript脚本（并不是一个完整的function)，定时时间到达时会被调用。
     *   若在脚本的最后返回false，则在脚本执行完后会终止定时器。"
     */
    public void setOntimeout(ValueExpression ontimeout) {
        setValueExpression("ontimeout", ontimeout);
    }

    /**
     * @jsp.attribute required="false" type="boolean"
     * description_zh_CN = "一个标志，当为true时，表示: 如果该定时器位于一个UIForm组件内，
     *   则向服务器发送的AJAX请求的参数中包含所有Form内的有效field（指所有非disabled和readonly的field），
     *   相当于提交form；当为false时，发送的AJAX请求与form无关。"
     */
    public void setSendForm(ValueExpression sendForm) {
        setValueExpression("sendForm", sendForm);
    }

    protected void setProperties(UIComponent component) {
        super.setProperties(component);

        AjaxTimer timer = (AjaxTimer)component;
        if (action != null)
            timer.setActionExpression(action);
        if (actionListener != null)
            timer.addActionListener(new MethodExpressionActionListener(actionListener));
    }

    public void release() {
        super.release();
        action = null;
        actionListener = null;
    }
}
