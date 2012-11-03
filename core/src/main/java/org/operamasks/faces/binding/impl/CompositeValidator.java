/*
 * $Id: CompositeValidator.java,v 1.3 2008/04/17 09:28:44 lishaochuan Exp $
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
import javax.faces.context.FacesContext;
import javax.faces.component.UIComponent;
import javax.faces.component.StateHolder;
import javax.faces.component.EditableValueHolder;
import javax.faces.event.PhaseId;
import java.util.List;
import java.util.ArrayList;

import org.operamasks.faces.validator.ClientValidator;

public final class CompositeValidator implements Validator, ClientValidator, StateHolder
{
    public static CompositeValidator getCompositeValidator(EditableValueHolder vh, PhaseId phaseId) {
        CompositeValidator composite = null;
        for (Validator v : vh.getValidators()) {
            if (v instanceof CompositeValidator) {
                if (phaseId != ((CompositeValidator)v).getPhaseId()) {
                    vh.removeValidator(v);
                } else {
                    composite = (CompositeValidator)v;
                    break;
                }
            }
        }

        if (composite == null) {
            composite = new CompositeValidator();
            composite.setPhaseId(phaseId);
            vh.addValidator(composite);
        }

        return composite;
    }

    private List<Validator> validators;
    private PhaseId phaseId;
    private boolean isTransient;

    public CompositeValidator() {
        this.validators = new ArrayList<Validator>();
    }

    public PhaseId getPhaseId() {
        return this.phaseId;
    }

    public void setPhaseId(PhaseId phaseId) {
        this.phaseId = phaseId;
    }

    public void addValidator(Validator validator) {
        this.validators.add(validator);
    }

    public void validate(FacesContext context, UIComponent component, Object value)
        throws ValidatorException
    {
        for (Validator v : this.validators) {
            v.validate(context, component, value);
        }
    }

    public String getValidatorScript(FacesContext context, UIComponent component) {
        StringBuilder buf = null;
        for (Validator v : this.validators) {
            if (v instanceof ClientValidator) {
                String vs = ((ClientValidator)v).getValidatorScript(context, component);
                if (vs != null) {
                    if (buf == null)
                        buf = new StringBuilder();
                    buf.append(vs);
                }
            }
        }
        return (buf == null) ? null : buf.toString();
    }

    public String getValidatorInstanceScript(FacesContext context, UIComponent component) {
        StringBuilder buf = null;
        for (Validator v : this.validators) {
            if (v instanceof ClientValidator) {
                String vs = ((ClientValidator)v).getValidatorInstanceScript(context, component);
                if (vs != null) {
                    if (buf == null)
                        buf = new StringBuilder();
                    if (buf.length() != 0)
                        buf.append(",");
                    buf.append(vs);
                }
            }
        }
        return (buf == null) ? null : buf.toString();
    }

    public Object saveState(FacesContext context) {
        // no need to save state, validators restored by injector
        return null;
    }

    public void restoreState(FacesContext context, Object state) {
        // no need to save state, validators restored by injector
    }

    public boolean isTransient() {
        return this.isTransient;
    }

    public void setTransient(boolean newTransientValue) {
        this.isTransient = newTransientValue;
    }

    public List<Validator> getValidators() {
        return validators;
    }
}
