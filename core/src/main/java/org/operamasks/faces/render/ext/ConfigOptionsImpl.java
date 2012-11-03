/*
 * $Id: ConfigOptionsImpl.java,v 1.6 2008/01/23 05:33:07 yangdong Exp $
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

package org.operamasks.faces.render.ext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.faces.component.UIComponent;

import org.operamasks.faces.util.JsArray;
import org.operamasks.faces.util.JsObject;

class ConfigOptionsImpl implements ConfigOptions {
	private Map<String, Object> options;
	private UIComponent component;
	private enum AttributeOption {
		ITEM,
		VALUE,
		AUTO
	}
	private List<String> ignoreOptions;

	public ConfigOptions add(String option, Object value) {
		if (options == null)
			options = new HashMap<String, Object>();
		
		if (value != null)
			options.put(option, value);
		
		return this;
	}
	
	public ConfigOptions add(String option) {
		if (options == null)
			options = new HashMap<String, Object>();
		
		options.put(option, AttributeOption.AUTO);
		
		return this;
	}

	public ConfigOptions setComponent(UIComponent component) {
		this.component = component;
		
		return this;
	}

	public ConfigOptions addItem(String option) {
		add(option, AttributeOption.ITEM);

		return this;
	}

	public ConfigOptions addValue(String option) {
		add(option, AttributeOption.VALUE);
		
		return this;
	}
	
	public ConfigOptions addValue(String option, Object value) {
		add(option, value);
		
		return this;
	}
	
	@Override
	public String toString() {
		JsArray optionsArray = getOptionsItemArray(this);
		
		return optionsArray.toString();
	}
	
	private JsArray getOptionsItemArray(ConfigOptionsImpl configOptions) {
		if (component == null) {
			throw new IllegalStateException("Component is null");
		}
		
		JsArray optionsArray = new JsArray();
		
		Iterator<Entry<String, Object>> iter = configOptions.options.entrySet().iterator();
		while (iter.hasNext()) {
			Entry<String, Object> entry = iter.next();
			String option = entry.getKey();
			
			if (ignoreOptions != null && ignoreOptions.contains(option))
				continue;
			
			Object value = entry.getValue();
			
			if (AttributeOption.ITEM == value) {
				optionsArray.put(option, new JsObject(getAttributeOptionValue(option, component)));
			} else if (AttributeOption.VALUE == value) {
				Object optionValue = getAttributeOptionValue(option, component);
				optionsArray.put(option, optionValue == null ? "null" : optionValue.toString());
			} else if (AttributeOption.AUTO == value) {
				optionsArray.put(option, getAttributeOptionValue(option, component));
			} else if (value instanceof ConfigOptionsImpl) {
				optionsArray.put(option, getOptionsItemArray((ConfigOptionsImpl)value));
			} else if (value instanceof ConfigOptions) {
				optionsArray.put(option, ((ConfigOptions)value).toString());
			} else {
				optionsArray.put(option, value);
			}
		}
		
		return optionsArray;
	}

	private Object getAttributeOptionValue(String option, UIComponent component) {
		return component.getAttributes().get(option);
	}

	public ConfigOptions addItem(String option, Object value) {
		add(option, new JsObject(value));
		
		return this;
	}

	public ConfigOptions ignore(String option) {
		if (ignoreOptions == null)
			ignoreOptions = new ArrayList<String>();
		
		if (!ignoreOptions.contains(options)) {
			ignoreOptions.add(option);
		}
		
		return this;
	}
}
