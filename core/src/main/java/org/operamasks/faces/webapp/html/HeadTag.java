/*
 * $Id: HeadTag.java,v 1.4 2007/07/02 07:38:07 jacky Exp $
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

package org.operamasks.faces.webapp.html;

import org.operamasks.faces.component.html.HtmlHead;
import org.operamasks.faces.webapp.html.HtmlBasicELTag;

public class HeadTag extends HtmlBasicELTag
{
    public String getRendererType() {
        return null;
    }

    public String getComponentType() {
        return HtmlHead.COMPONENT_TYPE;
    }
}
