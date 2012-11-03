package org.operamasks.faces.render.form;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.component.UISelectItem;
import javax.faces.component.UISelectItems;
import javax.faces.model.SelectItem;

import static org.operamasks.resources.Resources._T;
import static org.operamasks.resources.Resources.UI_FORM_SELECTITEM_TYPEERROR_MESSAGE;
import static org.operamasks.resources.Resources.UI_FORM_SELECTITEMS_TYPEERROR_MESSAGE;
import static org.operamasks.resources.Resources.UI_FORM_SELECTITEMS_VALUE_TYPEERROR_MESSAGE;
import static org.operamasks.resources.Resources.UI_FORM_SELECTITEMS_INCHECKBOXGROUP_TYPEERROR_MESSAGE;

public class SelectItemsUtil {
	@SuppressWarnings("unchecked")
    public static List<SelectItem> getSelectItems(UIComponent component) {
        List<SelectItem> list = new ArrayList<SelectItem>();

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
                    throw new IllegalArgumentException(_T(UI_FORM_SELECTITEM_TYPEERROR_MESSAGE));
                }
            } else if (kid instanceof UISelectItems) {
                Object value = ((UISelectItems)kid).getValue();
                if(value == null) {
                }
                else if (value instanceof SelectItem) {
                    list.add((SelectItem)value);
                } else if (value instanceof String) {
                    list.add(new SelectItem(value, value.toString()));
                }                
                else if (value instanceof SelectItem[]) {
                    for (SelectItem item : (SelectItem[])value) {
                        list.add(item);
                    }
                } else if ( value instanceof String[]) {
                    for( String str : (String[]) value) {
                        list.add(new SelectItem(str, str));
                    }
                } else if (value instanceof Collection) {
                    for (Object element : (Collection)value) {
                        if (element instanceof SelectItem) {
                            list.add((SelectItem)element);
                        } else if( element instanceof String) {
                            list.add(new SelectItem(element,element.toString()));
                        }
                        else {
                            throw new IllegalArgumentException(_T(UI_FORM_SELECTITEMS_TYPEERROR_MESSAGE));
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
                    throw new IllegalArgumentException(_T(UI_FORM_SELECTITEMS_VALUE_TYPEERROR_MESSAGE));
                }
            }
        }

        return list;
    }
	
	@SuppressWarnings("unchecked")
    public static void updateSelectItems(UIComponent component, List<SelectItem> list) {
        for (UIComponent kid : component.getChildren()) {
            if (kid instanceof UISelectItems) {
                Object value = ((UISelectItems)kid).getValue();
                if(value == null) {
                }
                else if (value instanceof SelectItem[]) {
                    for (SelectItem item : (SelectItem[])value) {
                    	for(SelectItem newItem : list){
                    		if(item.getLabel().equals(newItem.getLabel())){
                    			item.setValue(newItem.getValue());
                    		}
                    	}
                    }
                }else {
                    throw new IllegalArgumentException(_T(UI_FORM_SELECTITEMS_INCHECKBOXGROUP_TYPEERROR_MESSAGE));
                }
            }
        }

    }
}
