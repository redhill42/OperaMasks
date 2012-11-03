/*
 * $Id: DoubleConverter.java,v 1.5 2007/09/12 19:25:36 daniel Exp $
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

package org.operamasks.faces.convert;

import javax.faces.context.FacesContext;
import javax.faces.component.UIComponent;

import org.operamasks.faces.validator.ClientValidator;
import org.operamasks.faces.util.FacesUtils;
import org.operamasks.faces.util.HtmlEncoder;
import static org.operamasks.resources.Resources.*;

public class DoubleConverter extends javax.faces.convert.DoubleConverter
    implements ClientValidator
{
    public String getValidatorScript(FacesContext context, UIComponent component) {
        return null;
    }

    public String getValidatorInstanceScript(FacesContext context, UIComponent component) {
        String message = (String)component.getAttributes().get("converterMessage");
        if (message == null) {
            message = _T(JSF_DOUBLE_CONVERTER, "{0}", FacesUtils.getLabel(context, component));
        }

        String display = FacesUtils.getMessageComponentId(context, component);
        if (display != null) {
            display = HtmlEncoder.enquote(display);
        }

        return "new FloatValidator('" +
               component.getClientId(context) + "'," +
               HtmlEncoder.enquote(message) + "," +
               display + "," +
               (-Double.MAX_VALUE) + "," + Double.MAX_VALUE +
               ")";
    }
}
