/*
 * $Id: DefaultDateTimeConverter.java,v 1.1 2008/03/17 01:20:54 patrick Exp $
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

import static org.operamasks.resources.Resources.*;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.ConverterException;
import javax.faces.convert.DateTimeConverter;

public class DefaultDateTimeConverter extends DateTimeConverter {
    @Override
    public Object getAsObject(FacesContext context, UIComponent component,
            String value) {
        try {
            return super.getAsObject(context, component, value);
        } catch (ConverterException ex) {
            if (ex.getFacesMessage() == null && ex.getMessage() != null) {
                String msgStr = _T(JSF_DATETIME_CONVERTER_GENERAL_EXCEPTION, ex.getMessage());
                FacesMessage facesMsg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        msgStr, msgStr);
                throw new ConverterException(facesMsg, ex.getCause());
            } else {
                throw ex;
            }
        }
    }
}
