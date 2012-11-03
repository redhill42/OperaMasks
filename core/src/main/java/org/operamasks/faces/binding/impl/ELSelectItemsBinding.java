/*
 * $Id: ELSelectItemsBinding.java,v 1.3 2008/01/31 04:12:24 daniel Exp $
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

import java.util.Collection;
import java.util.List;
import java.util.ArrayList;
import javax.faces.model.SelectItem;
import javax.faces.component.UIComponent;
import javax.faces.component.UISelectItems;
import javax.faces.context.FacesContext;
import javax.el.ELContext;
import javax.el.ValueExpression;

import elite.lang.Closure;
import org.operamasks.el.eval.Coercion;
import org.operamasks.faces.binding.ModelBindingContext;

class ELSelectItemsBinding extends Binding
{
    private Closure closure;
    private String name;
    private Class<?> type;
    private Object source;
    private Closure mapValue;
    private Closure mapLabel;
    private SelectItem[] items;

    ELSelectItemsBinding(Closure closure, String name, Class<?> type) {
        super(null);
        this.closure = closure;
        this.name = name;
        this.type = type;
    }

    public Closure getClosure() {
        return closure;
    }

    public String getName() {
        return name;
    }

    public Class<?> getType() {
        return type;
    }

    public Object getSource() {
        return source;
    }

    public void setSource(Object source) {
        this.source = source;
    }

    public Closure getMapValue() {
        return mapValue;
    }

    public void setMapValue(Closure mapValue) {
        this.mapValue = mapValue;
    }

    public Closure getMapLabel() {
        return mapLabel;
    }

    public void setMapLabel(Closure mapLabel) {
        this.mapLabel = mapLabel;
    }

    public SelectItem[] getItems() {
        return items;
    }

    public void setItems(SelectItem[] items) {
        this.items = items;
    }

    public void apply(UIComponent parent, ELiteBean bean) {
        String selectionId = parent.getId() + "-selectItems";
        UISelectItems selection = null;

        for (UIComponent kid : parent.getChildren()) {
            if ((kid instanceof UISelectItems) && (selectionId.equals(kid.getId()))) {
                selection = (UISelectItems)kid;
                break;
            }
        }

        if (selection == null) {
            selection = new UISelectItems();
            selection.setId(selectionId);
            selection.setTransient(true);
            parent.getChildren().add(selection);
        }

        ValueExpression adapter = new ELSelectItemsValueAdapter(this, bean);
        selection.setValueExpression("value", adapter);
    }

    public void applyGlobal(FacesContext ctx, ModelBindingContext mbc) {
        ELContext elctx = ctx.getELContext();

        if (closure.getValue(elctx) != null) {
            return;
        }

        Object value = getValue(elctx, (ELiteBean)mbc.getModelBean());
        if (value != null) {
            closure.setValue(elctx, value);
        }
    }

    public SelectItem[] getValue(ELContext ctx, ELiteBean bean) {
        if (source != null) {
            return evaluateSource(ctx, source);
        } else if (items == null || items.length == 0) {
            return loadSelectItems(bean);
        } else {
            return populateSelectItems(bean, items);
        }
    }

    private SelectItem[] evaluateSource(ELContext ctx, Object source) {
        // Convert source object to meet the requirements of UISelectItems.
        if (source instanceof Collection) {
            return mapListItems(ctx, (Collection)source);
        } else if (source instanceof Object[]) {
            return mapArrayItems(ctx, (Object[])source);
        } else {
            return new SelectItem[] {mapSelectItem(ctx, source)};
        }
    }

    private SelectItem[] mapListItems(ELContext ctx, Collection source) {
        SelectItem[] result = new SelectItem[source.size()];
        int i = 0;
        for (Object item : source) {
            result[i++] = mapSelectItem(ctx, item);
        }
        return result;
    }

    private SelectItem[] mapArrayItems(ELContext ctx, Object[] source) {
        SelectItem[] result = new SelectItem[source.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = mapSelectItem(ctx, source[i]);
        }
        return result;
    }

    private SelectItem mapSelectItem(ELContext ctx, Object item) {
        if (item instanceof SelectItem) {
            return (SelectItem)item;
        }

        if (mapValue == null && mapLabel == null) {
            return new SelectItem(item);
        }

        Object value = (mapValue == null) ? item : mapValue.call(ctx, item);
        Object label = (mapLabel == null) ? item : mapLabel.call(ctx, item);
        return new SelectItem(value, Coercion.coerceToString(label));
    }

    private SelectItem[] loadSelectItems(ELiteBean bean) {
        List<SelectItem> result = new ArrayList<SelectItem>();

        for (int i = 0; ; i++) {
            String value = getText(bean, i, "value");
            if (value == null) {
                break;
            }

            String label = getText(bean, i, "label");
            String description = getText(bean, i, "description");
            if (label == null)
                label = value;
            result.add(new SelectItem(value, label, description, false, false));
        }

        return result.toArray(new SelectItem[result.size()]);
    }

    private SelectItem[] populateSelectItems(ELiteBean bean, SelectItem[] items) {
        int count = items.length;
        SelectItem[] result = new SelectItem[count];

        for (int i = 0; i < count; i++) {
            SelectItem item = items[i];
            Object value = item.getValue();
            String label = item.getLabel();
            String description = item.getDescription();

            if (label == null) {
                label = getText(bean, i, "label");
                if (label == null && value != null) {
                    label = value.toString();
                }
            }
            if (description == null) {
                description = getText(bean, i, "description");
            }
            result[i] = new SelectItem(value, label, description, item.isDisabled(), item.isEscape());
        }

        return result;
    }

    private String getText(ELiteBean bean, int index, String attribute) {
        return bean.getLocalString(name + ".selectItems[" + index + "]." + attribute);
    }
}
