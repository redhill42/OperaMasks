/*
 * $Id: UIUseBean.java,v 1.6 2008/03/05 12:50:40 jacky Exp $
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
package org.operamasks.faces.component.misc;

import javax.faces.component.UIComponentBase;
import javax.faces.component.NamingContainer;
import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.el.ValueExpression;
import javax.el.ELContext;
import java.util.List;
import java.util.ArrayList;

import org.operamasks.faces.binding.ComponentBinder;
import org.operamasks.faces.binding.ModelBindingContext;
import org.operamasks.faces.binding.ModelBindingFactory;
import org.operamasks.faces.binding.ModelBean;

public class UIUseBean extends UIComponentBase implements ComponentBinder, NamingContainer
{
    public static final String COMPONENT_TYPE = "org.operamasks.faces.UseBean";
    public static final String COMPONENT_FAMILY = "org.operamasks.faces.UseBean";

    private ValueExpression value;
    private ModelBean[] beans;
    private boolean prependId = true;

    public UIUseBean() {
        super();
    }

    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    public ValueExpression getValue() {
        return this.value;
    }

    public void setValue(ValueExpression value) {
        this.value = value;
    }

    public boolean isPrependId() {
        return this.prependId;
    }

    public void setPrependId(boolean prependId) {
        this.prependId = prependId;
    }

    public String getContainerClientId(FacesContext context) {
        if (this.isPrependId()) {
            return super.getContainerClientId(context);
        } else {
            UIComponent parent = this.getParent();
            while (parent != null) {
                if (parent instanceof NamingContainer) {
                    return parent.getContainerClientId(context);
                }
                parent = parent.getParent();
            }
        }
        return null;
    }

    private static final ModelBean[] EMPTY_BEANS = new ModelBean[0];

    public ModelBean[] getBeans(boolean reset) {
        if (reset || this.beans == null) {
            this.beans = getBeansFromValue(this.value);
        }
        return this.beans;
    }

    private ModelBean[] getBeansFromValue(ValueExpression value) {
        if (value == null) {
            return EMPTY_BEANS;
        }

        String id = this.getId();
        if (id.startsWith(UIViewRoot.UNIQUE_ID_PREFIX)) {
            id = null;
        }

        if (!value.isLiteralText()) {
            ModelBean bean = ModelBean.wrap(value);
            if (id != null)
                bean.addName(id);
            return new ModelBean[] { bean };
        }

        String beanNames = value.getExpressionString();
        ELContext elctx = getFacesContext().getELContext();

        if (beanNames != null) {
            if (beanNames.indexOf(',') == -1) {
                String name = beanNames.trim();
                if (name.length() > 0) {
                    Object target = elctx.getELResolver().getValue(elctx, null, name);
                    if (target != null) {
                        ModelBean bean = ModelBean.wrap(target);
                        if (id != null)
                            bean.addName(id);
                        bean.addName(name);
                        return new ModelBean[] { bean };
                    }
                }
            } else {
                List<ModelBean> list = new ArrayList<ModelBean>();
                for (String name : beanNames.split(",")) {
                    name = name.trim();
                    if (name.length() > 0) {
                        Object target = elctx.getELResolver().getValue(elctx, null, name);
                        if (target != null) {
                            ModelBean bean = ModelBean.wrap(target);
                            if (id != null)
                                bean.addName(id);
                            bean.addName(name);
                            list.add(bean);
                        }
                    }
                }
                return list.toArray(EMPTY_BEANS);
            }
        }

        return EMPTY_BEANS;
    }

    public void applyModel(FacesContext ctx, ModelBindingContext mbc) {
        ModelBean[] beans = this.getBeans(false);
        if (beans != null && this.getChildCount() > 0) {
            ModelBindingContext inner = ModelBindingFactory.instance()
                .createContext(mbc.getPhaseId(), mbc.getViewId(), beans);
            inner.setModelBeanFilter(mbc.getModelBeanFilter());
            for (UIComponent kid : this.getChildren()) {
                inner.applyModel(ctx, kid);
            }
        }
    }

    public Object saveState(FacesContext context) {
        Object[] values = new Object[2];
        values[0] = super.saveState(context);
        values[1] = saveAttachedState(context, value);
        return values;
    }

    public void restoreState(FacesContext context, Object state) {
        Object[] values = (Object[])state;
        super.restoreState(context, values[0]);
        this.value = (ValueExpression)restoreAttachedState(context, values[1]);
    }
}
