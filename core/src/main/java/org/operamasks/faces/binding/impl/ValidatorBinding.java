/*
 * $Id: ValidatorBinding.java,v 1.4 2007/10/22 11:27:14 daniel Exp $
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

import javax.faces.component.UIComponent;
import javax.faces.component.EditableValueHolder;
import javax.faces.component.UIData;
import javax.faces.context.FacesContext;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.operamasks.faces.binding.ModelBindingContext;
import org.operamasks.faces.binding.ModelBean;

final class ValidatorBinding extends Binding
{
    private String[] ids;
    private Method method;
    private String script;
    private String message;

    ValidatorBinding(String viewId, String[] ids) {
        super(viewId);
        this.ids = ids;
    }

    public String[] getIds() {
        return ids;
    }

    public Method getValidateMethod() {
        return method;
    }

    public void setValidateMethod(Method method) {
        method.setAccessible(true);
        this.method = BindingUtils.getInterfaceMethod(method);
    }

    public String getValidateScript() {
        return this.script;
    }

    public void setValidateScript(String script) {
        this.script = script;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void apply(FacesContext ctx, ModelBindingContext mbc) {
        ModelBean bean = mbc.getModelBean();

        for (String id : this.ids) {
            UIComponent comp = mbc.getComponent(id);
            if ((comp == null) || !(comp instanceof EditableValueHolder)) {
                continue;
            }

            CompositeValidator composite = CompositeValidator.getCompositeValidator(
                (EditableValueHolder)comp, mbc.getPhaseId());
            composite.addValidator(new ValidatorAdapter(bean, this.method, this.script, this.message));
        }
    }

    public void applyDataItem(FacesContext ctx, ModelBindingContext mbc, UIData data) {
        // only static method is supported
        if (!Modifier.isStatic(this.method.getModifiers())) {
            return;
        }

        for (String id : this.ids) {
            UIComponent comp = mbc.getComponent(id);
            if ((comp == null) || !(comp instanceof EditableValueHolder)) {
                continue;
            }

            CompositeValidator composite = CompositeValidator.getCompositeValidator(
                (EditableValueHolder)comp, mbc.getPhaseId());
            ModelBean bean = ModelBean.NULL_MODEL_BEAN;
            composite.addValidator(new ValidatorAdapter(bean, this.method, this.script, this.message));
        }
    }
}
