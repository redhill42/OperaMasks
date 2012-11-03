/*
 * $Id: ValueChangeListenerTEI.java,v 1.4 2007/07/02 07:38:09 jacky Exp $
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

package org.operamasks.faces.webapp.core;

import javax.servlet.jsp.tagext.TagExtraInfo;
import javax.servlet.jsp.tagext.TagData;
import javax.servlet.jsp.tagext.ValidationMessage;
import static org.operamasks.resources.Resources.*;

public class ValueChangeListenerTEI extends TagExtraInfo
{
    public ValidationMessage[] validate(TagData data) {
        if (data.getAttribute("type") == null &&
            data.getAttribute("binding") == null)
        {
            return new ValidationMessage[] {
                new ValidationMessage(data.getId(), _T(JSF_REQUIRED_VALUE_CHANGE_LISTENER_ATTRIBUTES))
            };
        }

        return null;
    }
}
