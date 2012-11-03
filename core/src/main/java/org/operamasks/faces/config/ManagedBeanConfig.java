/*
 * $Id: ManagedBeanConfig.java,v 1.3 2008/03/10 08:35:18 lishaochuan Exp $
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

package org.operamasks.faces.config;

import java.util.List;
import java.util.ArrayList;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.operamasks.faces.annotation.ManagedBeanScope;
import static org.operamasks.faces.annotation.ManagedBeanScope.*;
import static org.operamasks.resources.Resources.*;
import org.operamasks.util.Utils;

public class ManagedBeanConfig
{
    public static class Property {
        private String propertyName;
        private String propertyClass;
        private Field field;
        private String value;
        private MapEntries mapEntries;
        private ListEntries listEntries;

        public Property() {}

        public Property(String name, String type) {
            this.propertyName = name;
            this.propertyClass = type;
        }

        public String getPropertyName()                     { return propertyName; }
        public void setPropertyName(String name)            { propertyName = name;}
        public String getPropertyClass()                    { return propertyClass; }
        public void setPropertyClass(String clazz)          { propertyClass = clazz; }
        public Field getField()                             { return field; }
        public void setField(Field field)                   { this.field = field; }
        public String getValue()                            { return value; }
        public void setValue(String value)                  { this.value = value; }
        public MapEntries getMapEntries()                   { return mapEntries; }
        public void setMapEntries(MapEntries mapEntries)    { this.mapEntries = mapEntries; }
        public ListEntries getListEntries()                 { return listEntries; }
        public void setListEntries(ListEntries listEntries) { this.listEntries = listEntries; }

        public Field checkAndGetField(Class targetClass) {
            if (targetClass != null && this.field != null) {
                this.field = Utils.checkField(targetClass, this.field);
            }
            
            if (this.field != null) {
            	this.field.setAccessible(true);
            }
            return this.field;
        }
    }

    public static class EventListener {
        private String[] eventTypes;
        private Method listenerMethod;

        public String[] getEventTypes() {
            return this.eventTypes;
        }

        public void setEventTypes(String[] eventTypes) {
            this.eventTypes = eventTypes;
        }

        public Method getListenerMethod() {
            return this.listenerMethod;
        }

        public void setListenerMethod(Method method) {
            this.listenerMethod = method;
        }
    }

    public static class FactoryMethod extends ManagedBeanConfig {
        private String factoryName;
        private Method method;

        public String getFactoryName() {
            return factoryName;
        }

        public void setFactoryName(String name) {
            this.factoryName = name;
        }

        public void setMethod(Method method) {
            this.method = method;
        }

        public Method getMethod(Class targetClass) {
            if (targetClass != null && this.method != null) {
                this.method = Utils.checkMethod(targetClass, this.method);
            }
            return this.method;
        }
    }

    public static class MapEntries {
        private String keyClass;
        private String valueClass;
        private List<MapEntry> entries;

        public MapEntries() {
            this.entries = new ArrayList<MapEntry>();
        }

        public String getKeyClass()                  { return keyClass; }
        public void setKeyClass(String keyClass)     { if (keyClass == null)
                                                           keyClass = "java.lang.String";
                                                       this.keyClass = keyClass;
                                                     }
        public String getValueClass()                { return valueClass; }
        public void setValueClass(String valueClass) { if (valueClass == null)
                                                           valueClass = "java.lang.String";
                                                       this.valueClass = valueClass;
                                                     }
        public List<MapEntry> getMapEntries()        { return entries; }
        public void addMapEntry(MapEntry entry)      { entries.add(entry); }

        public void addMapEntry(String key, String value) {
            entries.add(new MapEntry(key, value));
        }
    }

    public static class MapEntry {
        private String key;
        private String value;

        public MapEntry() {}

        public MapEntry(String key, String value) {
            this.key = key;
            this.value = value;
        }

        public String getKey()                       { return key; }
        public void setKey(String key)               { this.key = key; }
        public String getValue()                     { return value; }
        public void setValue(String value)           { this.value = value; }
    }

    public static class ListEntries {
        private String valueClass;
        private List<String> values;

        public ListEntries() {
            values = new ArrayList<String>();
        }

        public String getValueClass()                { return valueClass; }
        public void setValueClass(String valueClass) { if (valueClass == null)
                                                           valueClass = "java.lang.String";
                                                       this.valueClass = valueClass;
                                                     }
        public List<String> getValues()              { return values; }
        public void addValue(String value)           { values.add(value); }
    }

    //---------------------------------

    private String name;
    private String className;
    private ManagedBeanScope scope = NONE;
    private String displayName;
    private String description;
    private List<Property> properties;
    private List<EventListener> listeners;
    private List<FactoryMethod> factories;
    private MapEntries mapEntries;
    private ListEntries listEntries;

    public ManagedBeanConfig() {
        this.properties = new ArrayList<Property>();
        this.listeners = new ArrayList<EventListener>();
        this.factories = new ArrayList<FactoryMethod>();
    }

    public String getManagedBeanName() {
        return this.name;
    }

    public void setManagedBeanName(String name) {
        this.name = name;
    }

    public String getManagedBeanClass() {
        return this.className;
    }

    public void setManagedBeanClass(String className) {
        this.className = className;
    }

    public ManagedBeanScope getManagedBeanScope() {
        return this.scope;
    }

    public void setManagedBeanScope(ManagedBeanScope scope) {
        this.scope = scope;
    }

    public void setManagedBeanScope(String scope) {
        if (scope == null || scope.equals("none")) {
            this.scope = NONE;
        } else if (scope.equals("request")) {
            this.scope = REQUEST;
        } else if (scope.equals("session")) {
            this.scope = SESSION;
        } else if (scope.equals("application")) {
            this.scope = APPLICATION;
        } else {
            throw new IllegalArgumentException(_T(JSF_INVALID_MANAGED_BEAN_SCOPE, scope));
        }
    }

    public String getDisplayName() {
        if (this.displayName != null)
            return this.displayName;
        return this.name;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Property> getManagedProperties() {
        return this.properties;
    }

    public Property getManagedProperty(String name) {
        for (Property p : this.properties) {
            if (name.equals(p.getPropertyName())) {
                return p;
            }
        }
        return null;
    }

    public void addManagedProperty(Property property) {
        if (getManagedProperty(property.getPropertyName()) == null) {
            this.properties.add(property);
        }
    }

    public List<EventListener> getEventListeners() {
        return this.listeners;
    }

    public void addEventListener(EventListener listener) {
        for (EventListener l : this.listeners) {
            if (l.listenerMethod.equals(listener.listenerMethod)) {
                return;
            }
        }
        this.listeners.add(listener);
    }

    public List<FactoryMethod> getFactoryMethods() {
        return this.factories;
    }

    public void addFactoryMethod(FactoryMethod factory) {
        for (FactoryMethod f : this.factories) {
            if (f.factoryName.equals(factory.factoryName)) {
                return;
            }
        }
        this.factories.add(factory);
    }

    public MapEntries getMapEntries() {
        return this.mapEntries;
    }

    public void setMapEntries(MapEntries mapEntries) {
        this.mapEntries = mapEntries;
    }

    public ListEntries getListEntries() {
        return this.listEntries;
    }

    public void setListEntries(ListEntries listEntries) {
        this.listEntries = listEntries;
    }
}
