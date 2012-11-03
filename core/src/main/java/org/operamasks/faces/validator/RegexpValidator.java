/*
 * $Id: RegexpValidator.java,v 1.3 2008/04/17 09:29:21 lishaochuan Exp $
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

package org.operamasks.faces.validator;

import java.util.regex.Pattern;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import javax.faces.component.StateHolder;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.application.FacesMessage;

import org.operamasks.faces.util.FacesUtils;
import org.operamasks.faces.util.HtmlEncoder;
import static org.operamasks.resources.Resources.*;

/**
 * 使用正则表达式校验输入值。此校验器必须设置一个符合正则表达式语法的模式字符串，
 * 当输入值和指定的正则表达式匹配时校验成功，否则将抛出ValidatorException。
 */
public class RegexpValidator implements Validator, ClientValidator, StateHolder
{
    /**
     * 正则表达式校验器的唯一ID。
     */
    public static final String VALIDATOR_ID = "org.operamasks.faces.Regexp";
    
    private Pattern pattern;
    private String message;

    /**
     * 构造一个未指定模式字符串的校验器。模式字符串可以通过后续的setPattern()
     * 方法设置。
     */
    public RegexpValidator() {}

    /**
     * 使用指定的模式字符串构造此校验器。
     *
     * @param pattern 模式字符串
     */
    public RegexpValidator(String pattern) {
        this.pattern = Pattern.compile(pattern);
    }

    /**
     * 使用一个预先编译好的模式构造此校验器。
     *
     * @param pattern 预先编译好的正则表达式模式
     */
    public RegexpValidator(Pattern pattern) {
        this.pattern = pattern;
    }

    /**
     * 设置用于模式匹配的符合正则表达式语法的模式字符串。
     *
     * @param pattern 模式字符串
     */
    public void setPattern(String pattern) {
        this.pattern = Pattern.compile(pattern);
    }

    /**
     * 设置校验错误信息。如果UI组件已设置了validatorMessage属性则此信息将被忽略。
     *
     * @param message 错误信息
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * 支持通过标注的方式设置模式字符串。
     * @see org.operamasks.faces.annotation.ValidateRegexp
     *
     * @param value 模式字符串
     */
    public void setValue(String value) {
        this.setPattern(value);
    }
    
    /**
     * 执行正则表达式模式匹配。
     */
    public void validate(FacesContext context, UIComponent component, Object value)
        throws ValidatorException
    {
        if (context == null || component == null) {
            throw new NullPointerException();
        }
        if (value != null && this.pattern != null) {
            String converted = stringValue(value);
            if (!this.pattern.matcher(converted).matches()) {
                String message = this.message;
                if (message == null || message.length() == 0) {
                    message = _T(JSF_VALIDATE_REGEXP_NOT_MATCH, FacesUtils.getLabel(context, component));
                }
                throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, message, null));
            }
        }
    }

    private static String stringValue(Object value) {
        if (value == null) {
            return null;
        } else if (value instanceof String) {
            return (String)value;
        } else {
            return value.toString();
        }
    }

    // ClientValidator --------------------------------------------

    public String getValidatorScript(FacesContext context, UIComponent component) {
        return null;
    }

    public String getValidatorInstanceScript(FacesContext context, UIComponent component) {
        if (this.pattern == null) {
            return null;
        }

        String message = (String)component.getAttributes().get("validatorMessage");
        if (message == null) {
            message = this.message;
            if (message == null) {
                message = _T(JSF_VALIDATE_REGEXP_NOT_MATCH, FacesUtils.getLabel(context, component));
            }
        }

        String display = FacesUtils.getMessageComponentId(context, component);
        if (display != null) {
            display = HtmlEncoder.enquote(display);
        }

        return String.format("new RegexpValidator('%s',%s,%s,%s)",
                             component.getClientId(context),
                             HtmlEncoder.enquote(message),
                             display,
                             HtmlEncoder.enquote(this.pattern.pattern()));
    }

    // StateHolder ------------------------------------------------

    public Object saveState(FacesContext context) {
        return (this.pattern == null) ? null : this.pattern.pattern();
    }

    public void restoreState(FacesContext context, Object state) {
        if (state != null) {
            this.pattern = Pattern.compile((String)state);
        }
    }

    private boolean transientFlag = false;

    public boolean isTransient() {
        return this.transientFlag;
    }

    public void setTransient(boolean transientFlag) {
        this.transientFlag = transientFlag;
    }

    public Pattern getPattern() {
        return pattern;
    }
}
