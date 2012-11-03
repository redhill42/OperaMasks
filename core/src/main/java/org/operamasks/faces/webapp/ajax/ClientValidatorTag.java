/*
 * $Id: ClientValidatorTag.java,v 1.6 2007/07/02 07:38:08 jacky Exp $
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

import javax.servlet.jsp.tagext.BodyTag;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.JspException;
import javax.el.ValueExpression;
import javax.faces.validator.Validator;
import javax.faces.context.FacesContext;
import org.operamasks.faces.webapp.core.ValidatorTagSupport;

/**
 * @jsp.tag name="clientValidator" body-content="JSP"
 * description_zh_CN = "JSF提供了标准的数据转换与验证框架，可以满足大部分的表单处理要求，
 * 同时确保数据模型的完整性。由于将数据转换与验证的代码与正常处理逻辑代码有效地分离，使代码的可维护性大为增强。
 * 但JSF的数据转换与验证是在服务器端进行的，对于一些简单的数据校验来说这种方式增大了网络的开销，
 * 因此我们应当想办法使数据校验尽可能在客户端完成。
 * <p>
 * Operamasks对标准数据转换与验证框架进行了扩充，使大部分转换器与校验器都可以在客户端进行。
 * 这是在render一个页面时生成与服务器端转换与校验器等价的客户端Javascript代码来实现的。
 * 要使用客户端数据校验，可以将h:form标签的clientValidate属性设置为true，
 * 或者在web.xml中将参数org.operamasks.faces.CLIENT_VALIDATE的值设置为true。
 * 前者仅对指定的form有效，后者对整个web应用都将执行客户端校验。
 *
 * <p>
 * 支持客户端数据校验的标准转换器与校验器包括如下：
 * <blockquote><pre>
 * javax.faces.convert.ByteConverter
 * javax.faces.convert.DoubleConverter
 * javax.faces.convert.FloatConverter
 * javax.faces.convert.IntegerConverter
 * javax.faces.convert.LongConverter
 * javax.faces.convert.ShortConverter
 * javax.faces.validator.DoubleRangeValidator
 * javax.faces.validator.LengthValidator
 * javax.faces.validator.LongRangeValidator
 * </pre></blockquote>
 * 
 * <p>
 * 对自定义的转换器和校验器如果想提供客户端校验能力，可以实现org.operamasks.faces.validator.ClientValidator接口，
 * 这个接口包括两个方法：
 * <p><blockquote><pre>
 * String getValidatorScript(FacesContext context, UIComponent component);
 * </pre></blockquote>
 * <p>
 * 生成一个Javascript代码，这段代码将定义一个客户端校验器对象。在ajax.js脚本文件中有关于客户端校验器的完整定义。
 *
 * <p><blockquote><pre>
 * String getValidatorInstanceScript(FacesContext context, UIComponent component);
 * </pre></blockquote>
 * <p>
 * 生成一个Javascript代码，这段代码将创建一个在上个方法中定义的客户端校验器对象，完成对客户输入数据的校验。
 * 另外，也可以将校验器的定义放在一个单独的脚本文件中，这样getValidationScript方法可以返回null，
 * 而不必每次都包含相同的脚本。
 * <p>
 * 例如：下面的例子使用一个正则表达式对客户输入数据进行校验，它同时实现Validator和ClientValidator接口完成服务器端和客户端的校验。
 * 注意由于客户端校验很容易被有经验的客户所绕过，因此服务器端的校验无论如何都是要被执行的。
 * <p><blockquote><pre>
 * public class RegExpValidator implements Validator, ClientValidator {
 *     private String pattern;
 *
 *     public void setPattern(String patter)｛
 *            this.pattern = pattern;
 *     }
 *     
 * public void validate(FacesContext context, UIComponent component, Object value)
 *     throws ValidationException
 * {
 *     // 执行服务器端数据校验
 * }
 * 
 * public String getValidatorScript(FacesContext context, UIComponent component) {
 *     return \"function RegExpValidator(id,message,pattern) { +
 *            \"this._id = id;\" +
 *            \"this._message = message;\" +
 *            \"this._pattern = pattern;\" +
 *            \"}\" +
 *            \"RegExpValidator.prototype = new Validator();\" +
 *            \"RegExpValidator.prototype.validate = function(value) {\" +
 *            \"var re = new RegExp(this._pattern);\" +
 *            \"return re.test(value);\" +
 *            \"}\";
 *   }
 *
 *   public String getValidatorInstanceScript(FacesContext context, UIComponent component) {
 *       String id = component.getClientId(context);
 *       String message = \"Regular expression validation error.\";
 *       String pattern = this.pattern;
 *
 *       return \"new RegExpValidator('\" + id + \"', '\" + message + \"', '\" + pattern + \"')\";
 *   }
 * }
 * </pre></blockquote> 
 * <p>
 * 可见，实现一个客户端校验器并不复杂，只需扩展Validator对象并实现validate方法即可。
 * 一些标准的校验器在ajax.js中都有定义，可以参考他们的实现。
 * <p>
 * 如果不想写一个单独的校验器，也可以使用ajax:clientValidate标签将Javascript代码直接嵌入JSP页面，这对一些并不具有通用性的数据校验比较方便。例如：
 * <p><blockquote><pre>
 * &lt;h:form clientValidate=\"true\">
 *   &lt;h:inputText value=\"#{UserBean.phoneNumber}\" validator=\"#{UserBean.validatePhoneNumber}\">
 *     &lt;ajax:clientValidator message=\"Invalid phone number\">
 *         var re = new RegExp(\"/^0\d{1,3}-\d{3}-\d{4}$/\");
 *         return re.test(value);
 *     &lt;ajax:clientValidator>
 *   &lt;/h:inputText>
 * &lt;/h:form>
 * </pre></blockquote> 
 * <p>
 * 其中UserBean.validatePhoneNumber在Managed Bean中实现，执行服务器端校验，
 * ajax:clientValidateor标签的内容是一段Javascript脚本，执行客户端校验，
 * 输入值是value，返回值必须是true或false。"
 * 
 */
public class ClientValidatorTag extends ValidatorTagSupport
    implements BodyTag
{
    private ValueExpression message;
    private ClientValidatorImpl validator;
    private BodyContent bodyContent;

    /**
     * @jsp.attribute type="java.lang.String" 
     * description_zh_CN="验证失败时的错误消息"
     */
    public void setMessage(ValueExpression message) {
        this.message = message;
    }

    public void setBodyContent(BodyContent b) {
        this.bodyContent = b;
    }

    public int doStartTag() throws JspException {
        super.doStartTag();
        return EVAL_BODY_BUFFERED;
    }

    public void doInitBody() throws JspException {
    }

    public int doAfterBody() throws JspException {
        if (validator != null) {
            validator.setValidationScript(bodyContent.getString());
        }
        return SKIP_BODY;
    }

    protected Validator createValidator()
        throws JspException
    {
        validator = new ClientValidatorImpl();
        if (message != null) {
            FacesContext context = FacesContext.getCurrentInstance();
            validator.setValidationMessage((String)message.getValue(context.getELContext()));
        }
        return validator;
    }

    public void release() {
        super.release();
        message = null;
        validator = null;
        pageContext = null;
    }
}
