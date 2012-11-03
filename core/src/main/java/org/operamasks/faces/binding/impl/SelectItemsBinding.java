/*
 * $Id:
 *
 * Copyright (c) 2006 Operamasks Community.
 * Copyright (c) 2000-2006 Apusic Systems, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.operamasks.faces.binding.impl;

import javax.faces.model.SelectItem;
import javax.faces.component.UIComponent;
import javax.faces.component.UISelectItems;
import javax.faces.context.FacesContext;
import javax.el.VariableMapper;
import javax.el.ValueExpression;

import java.util.Locale;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

import org.operamasks.faces.binding.ModelBindingContext;
import org.operamasks.faces.binding.ModelBean;
import static org.operamasks.faces.util.FacesUtils.*;

final class SelectItemsBinding extends PropertyBinding
{
    private String source;
    private String mapValue;
    private String mapLabel;
    private SelectItem[] items;
    private Class<?> valueClass;

    private Map<Locale,Object> cachedValue = new ConcurrentHashMap<Locale, Object>();

    SelectItemsBinding(String viewId) {
        super(viewId);
    }

    public String getSource() {
        return this.source;
    }

    public void setSource(String source) {
        if (source != null && source.length() == 0)
            source = null;
        this.source = source;
    }

    public String getMapValue() {
        return this.mapValue;
    }

    public void setMapValue(String mapValue) {
        if (mapValue != null && mapValue.length() == 0)
            mapValue = null;
        this.mapValue = mapValue;
    }

    public String getMapLabel() {
        return this.mapLabel;
    }

    public void setMapLabel(String mapLabel) {
        if (mapLabel != null && mapLabel.length() == 0)
            mapLabel = null;
        this.mapLabel = mapLabel;
    }

    public SelectItem[] getItems() {
        return this.items;
    }

    public void setItems(SelectItem[] items) {
        this.items = items;
    }

    public Class getValueClass() {
        return this.valueClass;
    }

    public void setValueClass(Class valueClass) {
        this.valueClass = valueClass;
    }

    public void apply(UIComponent parent, ModelBean bean) {
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
            selection.setTransient(true); // no state saving
            parent.getChildren().add(selection);
        }

        selection.setValueExpression("value", new SelectItemsValueAdapter(this, bean));
    }

    @SuppressWarnings("unchecked")
    public void applyGlobal(FacesContext ctx, ModelBindingContext mbc) {
        ModelBean bean = mbc.getModelBean();

        if (getModelValue(bean) != null) {
            return;
        }

        Object value = getValue(bean);

        if (value != null) {
            if (this.type == SelectItem[].class) {
                if (value instanceof List) {
                    value = ((List)value).toArray(new SelectItem[((List)value).size()]);
                } else if (!(value instanceof SelectItem[])) {
                    throw new ClassCastException(value.getClass().getName());
                }
            } else if (this.type == List.class) {
                if (value instanceof SelectItem[]) {
                    value = Arrays.asList((SelectItem[])value);
                } else if (!(value instanceof List)) {
                    throw new ClassCastException(value.getClass().getName());
                }
            }

            setModelValue(bean, value);
        }
    }

    @SuppressWarnings("unchecked")
    public Object getValue(ModelBean bean) {
        FacesContext ctx = FacesContext.getCurrentInstance();

        if (this.source != null) {
            return evaluateSource(ctx, bean, this.source);
        }

        Locale locale = ctx.getViewRoot().getLocale();
        if (locale == null) {
            locale = Locale.getDefault();
        }

        if (this.cachedValue.get(locale) != null) {
            return this.cachedValue.get(locale);
        }

        if (this.items.length == 0) {
            if (this.valueClass.isEnum()) {
                return loadEnumItems(locale, (Class<? extends Enum>)this.valueClass);
            } else {
                return loadSelectItems(locale, bean);
            }
        } else {
            return populateSelectItems(locale, bean);
        }
    }

    private Object evaluateSource(FacesContext ctx, ModelBean bean, String expr) {
        Object source = bean.evaluateExpression(expr, Object.class);

        // Return source object that is accepted by UISelectItems.
        if (source == null) {
            return null;
        }

        // Convert source object to meet the requirements of UISelectItems.
        if (source instanceof Collection) {
            return mapListItems(ctx, bean, (Collection)source);
        } else if (source.getClass().isArray()) {
            return mapArrayItems(ctx, bean, (Object[])source);
        } else if (source instanceof Enum) {
            return mapEnumItem((Enum<?>)source);
        } else if (source instanceof Enum[]) {
            return mapEnumItems((Enum[])source);
        } else {
            return mapSelectItem(ctx, bean, source);
        }
    }

    private SelectItem[] mapListItems(FacesContext ctx, ModelBean bean, Collection source) {
        SelectItem[] result = new SelectItem[source.size()];
        int i = 0;
        for (Object item : source) {
            result[i++] = mapSelectItem(ctx, bean, item);
        }
        return result;
    }

    private SelectItem[] mapArrayItems(FacesContext ctx, ModelBean bean, Object[] source) {
        SelectItem[] result = new SelectItem[source.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = mapSelectItem(ctx, bean, source[i]);
        }
        return result;
    }

    private SelectItem mapSelectItem(FacesContext ctx, ModelBean bean, Object item) {
        if (item instanceof SelectItem) {
            return populateSelectItem(bean, (SelectItem)item);
        }

        if (this.mapValue == null && this.mapLabel == null) {
            return populateSelectItem(bean, new SelectItem(item));
        }

        Object value;
        String label;

        VariableMapper varMapper = ctx.getELContext().getVariableMapper();
        ValueExpression oldVar = varMapper.setVariable("item", new ValueWrapper(item, Object.class));
        if (this.mapValue != null) {
            value = bean.evaluateExpression(this.mapValue, Object.class);
        } else {
            value = item;
        }
        if (this.mapLabel != null) {
            label = bean.evaluateExpression(this.mapLabel, String.class);
        } else {
            label = (value == null) ? null : value.toString();
        }
        varMapper.setVariable("item", oldVar);

        return populateSelectItem(bean, new SelectItem(value, label));
    }

    private SelectItem populateSelectItem(ModelBean bean, SelectItem selectItem) {
        if (selectItem == null) {
            return null;
        }
        Object value = selectItem.getValue();
        String label = selectItem.getLabel();
        
        if ((value instanceof String) && isValueExpression((String)value)) {
            value = bean.evaluateExpression((String)value, valueClass);
        }

        if (isValueExpression(label)) {
            label = bean.evaluateExpression(label, String.class);
        }

        if (label == null && value != null) {
            label = value.toString();
        }

        return new SelectItem(value, label);
    }

    private SelectItem mapEnumItem(Enum<?> source) {
        String name = source.name();

        // Load enum constant label from resource bundle. First lookup
        // from resource bundle in the package of enum class, then
        // lookup from resource bundle in the package of managed bean.
        String label = getLocalString(source.getClass(), name + ".label");
        if (label == null) {
            label = getLocalString(getDeclaringClass(), source.getClass().getName()
                                                        + "." + name + ".label");
            if (label == null) {
                label = source.toString();
            }
        }

        return new SelectItem(source, label);
    }

    private SelectItem[] mapEnumItems(Enum<?>[] source) {
        SelectItem[] result = new SelectItem[source.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = mapEnumItem(source[i]);
        }
        return result;
    }

    private SelectItem[] loadEnumItems(Locale locale, Class<? extends Enum> enumClass) {
        SelectItem[] result = mapEnumItems(enumClass.getEnumConstants());
        this.cachedValue.put(locale, result);
        return result;
    }

    private List<SelectItem> loadSelectItems(Locale locale, ModelBean bean) {
        List<SelectItem> result = new ArrayList<SelectItem>();
        boolean cacheable = true;

        for (int i = 0; ; i++) {
            Object value = getText(i, "value");
            if (value == null) {
                break;
            }

            if (isValueExpression((String)value)) {
                value = bean.evaluateExpression((String)value, valueClass);
                cacheable = false;
            }

            String label = getText(i, "label");
            if (isValueExpression(label)) {
                label = bean.evaluateExpression(label, String.class);
                cacheable = false;
            }
            if (label == null && value != null) {
                label = value.toString();
            }

            String description = getText(i, "description");
            if (isValueExpression(description)) {
                description = bean.evaluateExpression(description, String.class);
                cacheable = false;
            }

            result.add(new SelectItem(value, label, description, false, false));
        }

        if (cacheable) {
            this.cachedValue.put(locale, result);
        }

        return result;
    }

    private SelectItem[] populateSelectItems(Locale locale, ModelBean bean) {
        int count = this.items.length;
        SelectItem[] result = new SelectItem[count];
        boolean cacheable = true;

        for (int i = 0; i < count; i++) {
            SelectItem item = this.items[i];

            Object value = item.getValue();
            if (isValueExpression((String)value)) {
                value = bean.evaluateExpression((String)value, valueClass);
                cacheable = false;
            }

            String label = item.getLabel();
            if (label == null || label.length() == 0) {
                label = getText(i, "label");
            }
            if (isValueExpression(label)) {
                label = bean.evaluateExpression(label, String.class);
                cacheable = false;
            }
            if (label == null && value != null) {
                label = value.toString();
            }

            String description = item.getDescription();
            if (description == null || description.length() == 0) {
                description = getText(i, "description");
            }
            if (isValueExpression(description)) {
                description = bean.evaluateExpression(description, String.class);
                cacheable = false;
            }

            result[i] = new SelectItem(value, label, description, item.isDisabled(), item.isEscape());
        }

        if (cacheable) {
            this.cachedValue.put(locale, result);
        }

        return result;
    }

    private String getText(int index, String attribute) {
        return getLocalString(getDeclaringClass(), getName() + ".selectItems[" + index + "]." + attribute);
    }
}
