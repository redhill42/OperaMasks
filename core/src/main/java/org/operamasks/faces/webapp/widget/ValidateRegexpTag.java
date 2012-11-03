/*
 * $Id: ValidateRegexpTag.java,v 1.2 2008/04/11 06:54:24 lishaochuan Exp $
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
import javax.faces.validator.Validator;
import javax.faces.context.FacesContext;
import javax.servlet.jsp.JspException;

import org.operamasks.faces.validator.RegexpValidator;
import org.operamasks.faces.webapp.core.ValidatorTagSupport;

/**
 * @jsp.tag name="validateRegexp" body-content="JSP"
 * description_zh_CN="使用正则表达式校验输入值。此校验器必须设置
 * 一个符合正则表达式语法的模式字符串，当输入值和指定的正则表达式匹配
 * 时校验成功，否则将抛出ValidatorException。"
 */
public class ValidateRegexpTag extends ValidatorTagSupport
{
    private ValueExpression pattern;
    private ValueExpression message;

    /**
     * @jsp.attribute type="java.lang.String" required="true"
     * description_zh_CN="用于模式匹配的符合正则表达式语法的模式字符串"
     */
    public void setPattern(ValueExpression pattern) {
        this.pattern = pattern;
    }

    /**
     * @jsp.attribute type="java.lang.String"
     * description_zh_CN="校验错误信息。"
     */
    public void setMessage(ValueExpression message) {
        this.message = message;
    }

    @Override
    protected Validator createValidator()
        throws JspException
    {
        RegexpValidator validator = (RegexpValidator)super.createValidator(RegexpValidator.VALIDATOR_ID);
        if (validator != null) {
            FacesContext context = FacesContext.getCurrentInstance();
            if (pattern != null)
                validator.setPattern((String)pattern.getValue(context.getELContext()));
            if (message != null)
                validator.setMessage((String)message.getValue(context.getELContext()));
        }
        return validator;
    }

    public void release() {
        super.release();
        this.pattern = null;
        this.message = null;
    }
}
