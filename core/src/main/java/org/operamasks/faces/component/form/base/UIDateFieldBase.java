/*
 * $Id: UIDateFieldBase.java,v 1.9 2008/03/27 04:59:14 lishaochuan Exp $
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
package org.operamasks.faces.component.form.base;

import java.text.SimpleDateFormat;

import javax.el.ELException;
import javax.el.ValueExpression;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import org.operamasks.faces.annotation.component.Component;
import org.operamasks.faces.annotation.component.ext.ExtConfigOption;
import org.operamasks.faces.component.form.impl.UITriggerField;
import org.operamasks.faces.render.form.DateFieldRenderHandler;
import org.operamasks.faces.tools.annotation.ComponentMeta;
import static org.operamasks.resources.Resources.*;
import org.operamasks.faces.util.DateTimeFormatUtils;

@ComponentMeta(tagName = "dateField")
@Component(renderHandler = DateFieldRenderHandler.class)
public abstract class UIDateFieldBase extends UITriggerField {
    
    @ExtConfigOption
    protected String altFormats;
    @ExtConfigOption
    protected String disabledDates;
    @ExtConfigOption
    protected String disabledDatesText;
    @ExtConfigOption
    protected String disabledDays;
    @ExtConfigOption
    protected String disabledDaysText;
    @ExtConfigOption
    protected String format;
    @ExtConfigOption
    protected String invalidText;
    @ExtConfigOption
    protected String maxText;
    @ExtConfigOption
    protected String maxValue;
    @ExtConfigOption
    protected String minText;
    @ExtConfigOption
    protected String minValue;
    @ExtConfigOption
    protected String triggerClass;

    @Override
    public void updateModel(FacesContext context) {

        if (context == null) {
            throw new NullPointerException();
        }

        if (!isValid() || !isLocalValueSet()) {
            return;
        }
        ValueExpression ve = getValueExpression("value");
        if (ve != null) {
            try {
                Object value = getLocalValue();
                if (java.util.Date.class.equals(ve.getExpectedType()) || Object.class.equals(ve.getExpectedType())) {
                    ve.setValue(context.getELContext(), value);
                } else if (java.sql.Date.class.equals(ve.getExpectedType())) {
                    if(value != null){
                        ve.setValue(context.getELContext(), new java.sql.Date(((java.util.Date) value).getTime())); 
                    }else{
                        ve.setValue(context.getELContext(),null);
                    }
                } else if (String.class.equals(ve.getExpectedType())) {
                    String format = this.format == null ? DateTimeFormatUtils.DEFAUTL_DATE_FORMAT : this.format;
                    SimpleDateFormat sf = new SimpleDateFormat(format);
                    if (value != null)
                        ve.setValue(context.getELContext(), sf.format((java.util.Date) value));
                } else {
                    String msgString = _T(UI_DATEFIELD_BINDINGTYPE_MISMATCH);
                    FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            msgString, msgString);
                    context.addMessage(getClientId(context), message);
                }
                setValue(null);
                setLocalValueSet(false);
            } catch (ELException e) {
                String messageStr = e.getMessage();
                Throwable result = e.getCause();
                while (null != result &&
                    result.getClass().isAssignableFrom(ELException.class)) {
                    messageStr = result.getMessage();
                    result = result.getCause();
                }
                FacesMessage message;
                if (null == messageStr) {
                    messageStr = _T(UI_UIINPUT_EL_UNKNOWN);
                }
                message = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                               messageStr,
                                               messageStr);
                context.addMessage(getClientId(context), message);
                setValid(false);
            } catch (Exception e) {
                String valueTypeStr = getLocalValue() != null ? getLocalValue().getClass().getSimpleName() : "null"; 
                String messageStr = _T(UI_UIINPUT_UPDATEMODEL_ERROR, 
                        valueTypeStr, 
                        ve.getExpectedType().getSimpleName());
                FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        messageStr,
                        messageStr);
                context.addMessage(getClientId(context), message);
                setValid(false);
            }
        }
    }
}
