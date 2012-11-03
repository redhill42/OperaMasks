/*
 * $Id: ClosureValidatorAdapter.java,v 1.4 2008/01/31 04:12:24 daniel Exp $
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

import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import javax.faces.component.StateHolder;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.application.FacesMessage;
import javax.el.ELContext;
import elite.lang.Closure;

public class ClosureValidatorAdapter implements Validator, StateHolder
{
    private Closure closure;

    public ClosureValidatorAdapter(Closure closure) {
        this.closure = closure;
    }

    public void validate(FacesContext context, UIComponent component, Object value)
        throws ValidatorException
    {
        if (value != null) {
            ELContext elctx = context.getELContext();
            Object result;

            try {
                switch (closure.arity(elctx)) {
                case 1:
                    result = closure.call(elctx, value);
                    break;
                case 2:
                    result = closure.call(elctx, component, value);
                    break;
                case 3:
                    result = closure.call(elctx, context, component, value);
                    break;
                default:
                    return;
                }
            } catch (ValidatorException ex) {
                throw ex;
            } catch (Exception ex) {
                String errInfo = ex.getMessage();
                FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                                        errInfo, errInfo);
                throw new ValidatorException(message, ex.getCause());
            }

            if (result != null) {
                if (result instanceof String) {
                    String errInfo = (String)result;
                    FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                                            errInfo, errInfo);
                    throw new ValidatorException(message);
                } else if (Boolean.FALSE.equals(result)) {
                    String errInfo = (String)component.getAttributes().get("validatorMessage");
                    FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                                            errInfo, errInfo);
                    throw new ValidatorException(message);
                }
            }
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
        return true;
    }

    public void setTransient(boolean newTransientValue) {
        // noop
    }
}
