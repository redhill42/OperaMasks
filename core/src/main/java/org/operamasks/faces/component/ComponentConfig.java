/*
 * $Id 
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
package org.operamasks.faces.component;

public class ComponentConfig {
    private String componentType;
    private String componentClass;
    private Class<?> handlerClass;
    
    public ComponentConfig(String componentType, String componentClass) {
        this.componentType = componentType;
        this.componentClass = componentClass;
    }
    
    public String getComponentType() {
        return componentType;
    }
    public void setComponentType(String componentType) {
        this.componentType = componentType;
    }
    public String getComponentClass() {
        return componentClass;
    }
    public void setComponentClass(String componentClass) {
        this.componentClass = componentClass;
    }
    public Class<?> getHandlerClass() {
        return handlerClass;
    }
    public void setHandlerClass(Class<?> handlerClass) {
        this.handlerClass = handlerClass;
    }

}
