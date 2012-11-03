/*
 * $Id: ConverterAdapter.java,v 1.5 2007/12/17 23:24:12 daniel Exp $
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
package org.operamasks.faces.binding.impl;

import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.component.StateHolder;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.application.FacesMessage;

import org.operamasks.faces.validator.ClientValidator;

public class ConverterAdapter implements Converter, ClientValidator, StateHolder
{
    private ConvertAction convert;
    private FormatAction  format;
    private Converter     fallback;

    public ConvertAction getConvertAction() {
        return this.convert;
    }

    public void setConvertAction(ConvertAction action) {
        this.convert = action;
    }

    public FormatAction getFormatAction() {
        return this.format;
    }

    public void setFormatAction(FormatAction action) {
        this.format = action;
    }

    public Converter getFallback() {
        return this.fallback;
    }

    public void setFallback(Converter converter) {
        this.fallback = converter;
    }

    public Object getAsObject(FacesContext context, UIComponent component, String value)
        throws ConverterException
    {
        if ((context == null) || (component == null)) {
            throw new NullPointerException();
        }

        if (this.convert != null) {
            try {
                return convert.convert(context, component, value);
            } catch (ConverterException ex) {
                throw ex;
            } catch (Exception ex) {
                String errInfo = ex.getMessage();
                FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                                        errInfo,
                                                        errInfo);
                throw new ConverterException(message, ex.getCause());
            }
        }

        if (this.fallback != null) {
            return this.fallback.getAsObject(context, component, value);
        }

        return value;
    }

    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if ((context == null) || (component == null)) {
            throw new NullPointerException();
        }

        if (this.format != null) {
            try {
                return format.format(context, component, value);
            } catch (ConverterException ex) {
                throw ex;
            } catch (Exception ex) {
                String errInfo = ex.getMessage();
                FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                                        errInfo,
                                                        errInfo);
                throw new ConverterException(message, ex.getCause());
            }
        }

        if (this.fallback != null) {
            return this.fallback.getAsString(context, component, value);
        }

        return (value == null) ? "" : value.toString();
    }

    public String getValidatorScript(FacesContext context, UIComponent component) {
        if (fallback instanceof ClientValidator) {
            return ((ClientValidator)fallback).getValidatorScript(context, component);
        } else {
            return null;
        }
    }

    public String getValidatorInstanceScript(FacesContext context, UIComponent component) {
        if (fallback instanceof ClientValidator) {
            return ((ClientValidator)fallback).getValidatorInstanceScript(context, component);
        } else {
            return null;
        }
    }

    public Object saveState(FacesContext context) {
        // no need to save state, restored by injector
        return null;
    }

    public void restoreState(FacesContext context, Object state) {
        // no need to save state, restored by injector
    }

    public boolean isTransient() {
        return true; // no state saving at all
    }

    public void setTransient(boolean newTransientValue) {
        // noop
    }
}
