/*
 * $Id: ApplyRequestValues.java,v 1.1 2007/09/08 20:02:38 daniel Exp $
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
package org.operamasks.faces.lifecycle;

import static javax.faces.event.PhaseId.APPLY_REQUEST_VALUES;
import javax.faces.context.FacesContext;

public class ApplyRequestValues extends Phase
{
    public ApplyRequestValues() {
        super(APPLY_REQUEST_VALUES);
    }

    public void execute(FacesContext context) {
        context.getViewRoot().processDecodes(context);
    }
}
