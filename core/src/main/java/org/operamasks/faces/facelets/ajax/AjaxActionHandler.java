/*
 * $Id: AjaxActionHandler.java,v 1.1 2007/08/15 06:44:04 daniel Exp $
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
package org.operamasks.faces.facelets.ajax;

import javax.faces.component.UIComponent;

import com.sun.facelets.tag.jsf.ComponentHandler;
import com.sun.facelets.tag.jsf.ComponentConfig;
import com.sun.facelets.tag.TagAttribute;
import com.sun.facelets.tag.TagAttributeException;
import com.sun.facelets.FaceletContext;
import org.operamasks.faces.component.ajax.AjaxAction;

public class AjaxActionHandler extends ComponentHandler
{
    private String event;

    public AjaxActionHandler(ComponentConfig config) {
        super(config);
        TagAttribute attr = this.getRequiredAttribute("event");
        if (!attr.isLiteral()) {
            throw new TagAttributeException(this.tag, attr, "Must be literal");
        }
        this.event = attr.getValue();
    }

    protected void onComponentCreated(FaceletContext ctx, UIComponent c, UIComponent parent) {
        if (parent != null) {
            ((AjaxAction)c).attachEvent(this.event, parent);
        }
    }
}
