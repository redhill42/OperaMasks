/*
 * $Id: UISelectRenderer.java,v 1.6 2007/12/11 04:20:12 jacky Exp $
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

package org.operamasks.faces.render.html;

import javax.faces.context.FacesContext;
import javax.faces.component.UIComponent;
import javax.faces.component.UISelectMany;
import javax.faces.component.UISelectOne;
import javax.faces.component.UISelectItem;
import javax.faces.component.UISelectItems;
import javax.faces.convert.ConverterException;
import javax.faces.convert.Converter;
import javax.faces.model.SelectItem;
import javax.faces.model.SelectItemGroup;
import javax.el.ValueExpression;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;
import java.util.Collection;
import java.lang.reflect.Array;
import org.operamasks.el.eval.Coercion;
import static org.operamasks.resources.Resources.*;

public abstract class UISelectRenderer extends UIInputRenderer
{
    public void decode(FacesContext context, UIComponent component) {
        if (context == null || component == null)
            throw new NullPointerException();
        if (isDisabledOrReadonly(component))
            return;

        String clientId = component.getClientId(context);
        if (component instanceof UISelectMany) {
            Map<String, String []> paramValuesMap = context.getExternalContext().getRequestParameterValuesMap();
            if (paramValuesMap.containsKey(clientId)) {
                String[] newValues = paramValuesMap.get(clientId);
                setSubmittedValue(component, newValues);
            } else {
                setSubmittedValue(component, new String[0]);
            }
        } else {
            Map<String,String> paramMap = context.getExternalContext().getRequestParameterMap();
            if (paramMap.containsKey(clientId)) {
                String newValue = paramMap.get(clientId);
                setSubmittedValue(component, newValue);
            } else {
                setSubmittedValue(component, "");
            }
        }
    }

    public Object getConvertedValue(FacesContext context, UIComponent component, Object submittedValue)
        throws ConverterException
    {
        if (component instanceof UISelectMany)
            return convertSelectManyValue(context, (UISelectMany)component, (String[])submittedValue);
        else
            return convertSelectOneValue(context, (UISelectOne)component, (String)submittedValue);
    }

    protected Object convertSelectOneValue(FacesContext context, UISelectOne component, String newValue)
        throws ConverterException
    {
        if (newValue == null || newValue.length() == 0)
            return null;
        return super.getConvertedValue(context, component, newValue);
    }

    @SuppressWarnings("unchecked")
    protected Object convertSelectManyValue(FacesContext context, UISelectMany component, String newValues[])
        throws ConverterException
    {
        ValueExpression binding = component.getValueExpression("value");
        Object result = null;

        if (binding != null) {
            Class bindingType = binding.getType(context.getELContext());
            if (bindingType != null) {
                if (bindingType.isArray()) {
                    result = convertArrayValue(context, component, bindingType, newValues);
                } else if (java.util.List.class.isAssignableFrom(bindingType)) {
                    result = convertListValue(context, newValues);
                } else {
                    // FIXME
                }
            }
        } else {
            result = newValues;
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    protected Object convertArrayValue(FacesContext context, UISelectMany component,
                                       Class arrayType, String[] newValues)
        throws ConverterException
    {
        Class elementType = arrayType.getComponentType();
        int len = newValues.length;

        if (elementType == String.class || elementType == Object.class)
            return newValues;

        Object result;
        try {
            result = Array.newInstance(elementType, len);
        } catch (Exception ex) {
            throw new ConverterException(ex);
        }

        Converter converter = component.getConverter();
        if (converter == null)
            converter = context.getApplication().createConverter(elementType);
        if (converter == null)
            throw new ConverterException(_T(JSF_NO_SUCH_CONVERTER_TYPE, elementType.getName()));

        for (int i = 0; i < len; i++) {
            Array.set(result, i, converter.getAsObject(context, component, newValues[i]));
        }
        return result;
    }

    protected Object convertListValue(FacesContext context, String[] newValues) {
        ArrayList<String> result = new ArrayList<String>(newValues.length);
        for (int i = 0; i < newValues.length; i++)
            result.add(newValues[i]);
        return result;
    }

    @SuppressWarnings("unchecked")
    protected List<SelectItem> getSelectItems(FacesContext context, UIComponent component) {
        List<SelectItem> list = new ArrayList<SelectItem>(component.getChildCount());

        for (UIComponent kid : component.getChildren()) {
            if (kid instanceof UISelectItem) {
                UISelectItem item = (UISelectItem)kid;
                Object value = item.getValue();
                if (value == null) {
                    list.add(new SelectItem(item.getItemValue(),
                                            item.getItemLabel(),
                                            item.getItemDescription(),
                                            item.isItemDisabled()));
                } else if (value instanceof SelectItem) {
                    list.add((SelectItem)value);
                } else {
                    throw new IllegalArgumentException("Not a SelectItem");
                }
            } else if (kid instanceof UISelectItems) {
//              modified by ZhangYong, 适应字符串这种形式的value            	
                Object value = ((UISelectItems)kid).getValue();
                if(value == null) {
//                	do nothing
                }
                else if (value instanceof SelectItem) {
                    list.add((SelectItem)value);
                } else if (value instanceof String) {
                	list.add(new SelectItem(value));
                }                
                else if (value instanceof SelectItem[]) {
                    for (SelectItem item : (SelectItem[])value) {
                        list.add(item);
                    }
                } else if ( value instanceof String[]) {
                	for( String str : (String[]) value) {
                		list.add(new SelectItem(str));
                	}
                } else if (value instanceof Collection) {
                    for (Object element : (Collection)value) {
                        if (element instanceof SelectItem) {
                            list.add((SelectItem)element);
                        } else if( element instanceof String) {
                        	list.add(new SelectItem(element));
                        }
                        else {
                            throw new IllegalArgumentException("SelectItems's value MUST BE SelectItem(s) or String(s)");
                        }
                    }
                } else if (value instanceof Map) {
                    for (Map.Entry entry : ((Map<Object,Object>)value).entrySet()) {
                        Object key = entry.getKey();
                        Object val = entry.getValue();
                        if (key != null && val != null) {
                            list.add(new SelectItem(key.toString(), val.toString()));
                        }
                    }
                } else {
                    throw new IllegalArgumentException("SelectItems's value MUST BE SelectItem(s) or String(s)");
                }
            }
        }

        return list;
    }

    protected int getSelectItemCount(FacesContext context, UIComponent component) {
        int itemCount = 0;
        for (SelectItem item : getSelectItems(context, component)) {
            itemCount++;
            if (item instanceof SelectItemGroup) {
                itemCount += ((SelectItemGroup)item).getSelectItems().length;
            }
        }
        return itemCount;
    }

    protected Object[] getSubmittedSelectedValues(FacesContext context, UIComponent component) {
        if (component instanceof UISelectMany) {
            return (Object[])((UISelectMany)component).getSubmittedValue();
        } else {
            Object value = ((UISelectOne)component).getSubmittedValue();
            if (value == null)
                return null;
            return new Object[] { value };
        }
    }

    protected Object getCurrentSelectedValues(FacesContext cotnext, UIComponent component) {
        if (component instanceof UISelectMany) {
            Object value = ((UISelectMany)component).getValue();
            if (value == null) {
                return null;
            } else if (value instanceof List) {
                return ((List)value).toArray();
            } else if (value.getClass().isArray()) {
                return value;
            } else {
                return new Object[] { value };
            }
        } else {
            Object value = ((UISelectOne)component).getValue();
            if (value == null)
                return null;
            return new Object[] { value };
        }
    }


    protected boolean isSelected(FacesContext context, UIComponent component, SelectItem item) {
        Object itemValue = item.getValue();
        String itemStr = getFormattedValue(context, component, itemValue);
        return isSelected(context, component, itemValue, itemStr);
    }

    protected boolean isSelected(FacesContext context, UIComponent component,
                                 Object itemValue, String itemStr)
    {
        if (component instanceof UISelectMany) {
            return isManySelected(context, component, itemValue, itemStr);
        } else {
            return isOneSelected(context, component, itemValue);
        }
    }

    @SuppressWarnings("unchecked")
    protected boolean isOneSelected(FacesContext context, UIComponent component, Object itemValue) {
        Object currentValue = getCurrentValue(context, component);
        if (currentValue == null) {
            return itemValue == null;
        } else {
            Class itemType = currentValue.getClass();
            itemValue = Coercion.coerce(itemValue, itemType);
            return (currentValue.equals(itemValue));
        }
    }

    @SuppressWarnings("unchecked")
    protected boolean isManySelected(FacesContext context, UIComponent component,
                                     Object itemValue, String itemStr)
    {
        Object[] submittedValues = getSubmittedSelectedValues(context, component);
        if (submittedValues != null) {
            int len = submittedValues.length;
            for (int i = 0; i < len; i++) {
                if (submittedValues[i].equals(itemStr))
                    return true;
            }
        }

        Object currentValues = getCurrentSelectedValues(context, component);
        if (currentValues != null) {
            Class elementType = currentValues.getClass().getComponentType();
            itemValue = Coercion.coerce(itemValue, elementType);
            int len = Array.getLength(currentValues);
            for (int i = 0; i < len; i++) {
                Object value = Array.get(currentValues, i);
                if (value == null) {
                    if (itemValue == null)
                        return true;
                } else {
                    if (value.equals(itemValue))
                        return true;
                }
            }
        }

        return false;
    }
}
