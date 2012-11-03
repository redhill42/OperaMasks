/*
 * $Id: ActionTag.java,v 1.13 2007/08/15 06:44:04 daniel Exp $
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
import javax.faces.event.MethodExpressionActionListener;
import javax.faces.webapp.UIComponentELTag;
import javax.faces.webapp.UIComponentClassicTagBase;
import javax.faces.context.FacesContext;
import javax.servlet.jsp.JspException;

import org.operamasks.faces.component.ajax.AjaxAction;
import static org.operamasks.resources.Resources.*;

/**
 * @jsp.tag name="action" body-content="JSP" 
 * description_zh_CN="可以为任意一个UI组件增加行动侦听器，
 *   在响应客户端事件时向服务器发送一个行动事件，以执行相应的服务器端逻辑。
 *   也就是说，可以在服务器端处理客户端事件。"
 * 
 * @jsp.attribute name="id" required = "false" rtexprvalue = "true"
 * description="The component identifier for this component.  This value must be
 *       unique within the closest parent component that is a naming
 *       container."
 * description_zh_CN="这个组件的组件标识符。这个值在最近的命名容器类型的父组件范围内，必须是唯一的。"
 *   
 * @jsp.attribute name="rendered" required = "false" type = "boolean"
 * description="Flag indicating whether or not this component should be rendered
 *       (during Render Response Phase), or processed on any subsequent
 *       form submit. The default value for this property is true."
 * description_zh_CN="一个标志，指出该组件是否要在任何随后的form提交过程中被渲染或处理。
 *       这个属性的缺省值是true。"
 * 
 * @jsp.attribute name="binding" required = "false" type = "javax.faces.component.UIComponent"
 * description="The ValueExpression linking this component to a property in a backing bean"
 * description_zh_CN="一个值表达式，用于把该组件链接到一个ManagedBean的某个属性。"  
 */
public class ActionTag extends UIComponentELTag
{
    private String event;
    private MethodExpression action;
    private MethodExpression actionListener;
    private ValueExpression immediate;

    public String getRendererType() {
        return null;
    }

    public String getComponentType() {
        return AjaxAction.COMPONENT_TYPE;
    }

    /**
     * @jsp.attribute rtexprvalue="true" required="true" 
     * type="java.lang.String" deferred="false"
     * description_zh_CN="客户端事件名称。"
     */
    public void setEvent(String event) {
        this.event = event;
    }
    
    /**
     * @jsp.attribute method-signature="java.lang.Object action()" required="false"
     * description="MethodBinding representing the application action to invoke when
     *   the closest outer component is activated by the user.  The expression must
     *   evaluate to a public method that takes no parameters, and returns
     *   a String (the logical outcome) which is passed to the
     *   NavigationHandler for this application."
     * description_zh_CN="代表应用行动的方法绑定，当外围组件被用户激活时调用。
     *   这个表达式必须对应到一个公有的不带参数的方法，这个方法返回一个字符串（逻辑输出），
     *   这个字符串用来被传递个这个应用的NavigationHandler。"
     */
    public void setAction(MethodExpression action) {
        this.action = action;
    }

    /**
     * @jsp.attribute method-signature="void actionListener(javax.faces.event.ActionEvent)" required="false"
     * description="MethodBinding representing an action listener method that will be
     *   notified when the closest outer component is activated by the user.  The
     *   expression must evaluate to a public method that takes an
     *   ActionEvent parameter, with a return type of void."
     * description_zh_CN="代表一个行动侦听器方法的方法绑定，当外围组件被用户激活时，这个方法将会得到通知。
     *    这个表达式必须对应到一个公有方法，带有一个ActionEvent参数，且具有类型为void的返回值。"
     */
    public void setActionListener(MethodExpression actionListener) {
        this.actionListener = actionListener;
    }

    /**
     * @jsp.attribute type="boolean" required="false"
     * description="Flag indicating that, if this component is activated by the user,
     *   notifications should be delivered to interested listeners and actions
     *   immediately (that is, during Apply Request Values phase) rather than
     *   waiting until Invoke Application phase."
     * description_zh_CN="一个标志，指出当该组件被用户激活时，立即向感兴趣的侦听器和行动处理器发出通知
     *   （也就是说在应用请求值阶段），而不是等到调用应用阶段。"
     */
    public void setImmediate(ValueExpression immediate) {
        this.immediate = immediate;
    }

    protected void setProperties(UIComponent component) {
        super.setProperties(component);

        AjaxAction actionSource = (AjaxAction)component;
        if (event != null)
            actionSource.setEvent(event);
        if (action != null)
            actionSource.setActionExpression(action);
        if (actionListener != null)
            actionSource.addActionListener(new MethodExpressionActionListener(actionListener));
        if (immediate != null)
            actionSource.setValueExpression("immediate", immediate);
    }

    public void release() {
        super.release();
        event = null;
        action = null;
        actionListener = null;
        immediate = null;
    }

    public int doEndTag()
        throws JspException
    {
        AjaxAction action = (AjaxAction)getComponentInstance();
        String event = this.event;

        int rc = super.doEndTag();

        UIComponentClassicTagBase tag = getParentUIComponentClassicTagBase(pageContext);
        if (tag == null)
            throw new JspException(_T(JSF_NOT_NESTED_IN_FACES_TAG, "ajax:action"));
        if (!tag.getCreated())
            return rc;

        UIComponent parent = tag.getComponentInstance();
        action.attachEvent(event, parent);

        return rc;
    }
}
