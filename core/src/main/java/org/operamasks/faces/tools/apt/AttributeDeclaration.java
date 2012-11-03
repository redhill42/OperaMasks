/*
 * $Id: AttributeDeclaration.java,v 1.3 2008/03/10 08:28:52 lishaochuan Exp $
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

package org.operamasks.faces.tools.apt;

import org.operamasks.faces.annotation.component.ext.ExtConfigOption;

import com.sun.mirror.declaration.FieldDeclaration;
import com.sun.mirror.type.PrimitiveType;
import com.sun.mirror.type.TypeMirror;

public class AttributeDeclaration
{
    private ComponentDeclaration component;
    private FieldDeclaration field;
    private ExtConfigOption extConfigOption;

    AttributeDeclaration(ComponentDeclaration component, FieldDeclaration field) {
        this.component = component;
        this.field = field;
        this.extConfigOption = field.getAnnotation(ExtConfigOption.class);
    }

    public String getName() {
        return field.getSimpleName();
    }

    public String getType() {
        return field.getType().toString();
    }

    public boolean isPrimitiveType() {
        return field.getType() instanceof PrimitiveType;
    }

    public String getWrapperType() {
        TypeMirror type = field.getType();
        if (type instanceof PrimitiveType) {
            switch (((PrimitiveType)type).getKind()) {
                case BOOLEAN:   return "Boolean";
                case BYTE:      return "Byte";
                case CHAR:      return "Character";
                case SHORT:     return "Short";
                case INT:       return "Integer";
                case LONG:      return "Long";
                case FLOAT:     return "Float";
                case DOUBLE:    return "Double";
                default:        assert false;
            }
        }
        return type.toString();
    }

    public boolean isReadMethodPresent() {
        String name = GeneratorUtils.capitalize(this.getName());
        if (component.getMethod("get" + name) != null) {
            return true;
        }

        if ("boolean".equals(this.getType())) {
            if (component.getMethod("is" + name) != null) {
                return true;
            }
        }

        return false;
    }

    public boolean isWriteMethodPresent() {
        String name = GeneratorUtils.capitalize(this.getName());
        return (component.getMethod("set"+name, this.getType()) != null);
    }

    public boolean isAccessorMethodPresent() {
        return this.isReadMethodPresent() || this.isWriteMethodPresent();
    }
    
    public String getDocumentation() {
        return field.getDocComment();
    }

    public String getDefaultValue() {
    	if(extConfigOption != null){
    		return extConfigOption.defalutValue();
    	}
        return "null";
    }
}
