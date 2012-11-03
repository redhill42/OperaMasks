/*
 * $Id: LayoutUtils.java,v 1.3 2007/07/02 07:38:13 jacky Exp $
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

package org.operamasks.faces.render.layout;

import org.operamasks.faces.layout.Facelet;
import org.operamasks.el.eval.Coercion;

class LayoutUtils
{
    public static Object getFaceletAttribute(Facelet facelet, String name) {
        return getFaceletAttribute(facelet, name, Object.class);
    }

    @SuppressWarnings("unchecked")
    public static <T> T getFaceletAttribute(Facelet facelet, String name, Class<T> type) {
        Object value;

        value = facelet.getAttributes().get(name);
        if (value != null) {
            return (T)Coercion.coerce(value, type);
        }

        Object constraints = facelet.getConstraints();
        if (constraints != null) {
            value = parseConstraintAttribute(constraints.toString(), name);
            if (value != null) {
                return (T)Coercion.coerce(value, type);
            }
        }

        return null;
    }

    private static String parseConstraintAttribute(String input, String name) {
        int index = input.indexOf(name);
        if (index == -1) return null;
        index = index + name.length(); // positioned after the attribute name
        index = input.indexOf('=', index); // positioned at the '='
        if (index == -1) return null;
        index += 1; // positioned after the '='
        input = input.substring(index).trim();

        int begin, end;
        if (input.charAt(0) == '\'') {
            // attribute value is a quoted string
            begin = 1;
            end = input.indexOf('\'', begin);
            if (end == -1) return null;
        } else {
            begin = 0;
            end = input.indexOf(';');
            if (end == -1) end = input.indexOf(' ');
            if (end == -1) end = input.length();
        }
        return input.substring(begin, end).trim();
    }
}
