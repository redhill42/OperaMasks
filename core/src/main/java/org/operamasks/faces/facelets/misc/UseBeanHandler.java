/*
 * $Id: UseBeanHandler.java,v 1.1 2007/09/12 10:24:34 daniel Exp $
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
package org.operamasks.faces.facelets.misc;

import javax.faces.component.UIComponent;

import com.sun.facelets.tag.jsf.ComponentHandler;
import com.sun.facelets.tag.jsf.ComponentConfig;
import com.sun.facelets.tag.TagAttribute;
import com.sun.facelets.tag.MetaRuleset;
import com.sun.facelets.FaceletContext;
import org.operamasks.faces.component.misc.UIUseBean;

public class UseBeanHandler extends ComponentHandler
{
    private final TagAttribute value;

    public UseBeanHandler(ComponentConfig config) {
        super(config);
        this.value = this.getRequiredAttribute("value");
    }

    protected void onComponentCreated(FaceletContext ctx, UIComponent c, UIComponent parent) {
        ((UIUseBean)c).setValue(this.value.getValueExpression(ctx, Object.class));
    }

    protected MetaRuleset createMetaRuleset(Class type) {
        return super.createMetaRuleset(type).ignore("value");
    }
}
