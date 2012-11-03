/*
 * $Id: RequiresTag.java,v 1.3 2007/07/02 07:37:57 jacky Exp $
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

package org.operamasks.faces.webapp.widget;

import javax.servlet.jsp.tagext.TagSupport;
import javax.faces.context.FacesContext;

import org.operamasks.faces.render.widget.yuiext.YuiExtResource;
import org.operamasks.faces.render.resource.ResourceManager;

/**
 * @jsp.tag name="requires" body-content="empty"
 */
public class RequiresTag extends TagSupport
{
    private String pkg;

    /**
     * @jsp.attribute required="true"
     */
    public void setPackage(String pkg) {
        this.pkg = pkg;
    }

    public int doStartTag() {
        if (pkg != null) {
            FacesContext context = FacesContext.getCurrentInstance();
            ResourceManager rm = ResourceManager.getInstance(context);
            YuiExtResource.register(rm, pkg);
        }
        return SKIP_BODY;
    }
}
