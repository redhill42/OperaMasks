/*
 * $Id: ChooseSkinMenuItemRenderer.java,v 1.3 2007/07/02 07:37:50 jacky Exp $
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

package org.operamasks.faces.render.widget.yuiext;

import javax.faces.context.FacesContext;
import javax.faces.component.UIComponent;
import javax.faces.model.SelectItem;
import javax.faces.convert.ConverterException;
import java.util.List;

import org.operamasks.faces.render.widget.ChooseSkinRenderer;
import org.operamasks.faces.render.resource.ResourceManager;

public class ChooseSkinMenuItemRenderer extends RadioMenuItemRenderer
{
    private ChooseSkinRenderer delegate = new ChooseSkinRenderer();

    @Override
    public String getCurrentValue(FacesContext context, UIComponent component) {
        return delegate.getCurrentValue(context, component);
    }

    @Override
    public Object getConvertedValue(FacesContext context, UIComponent component, Object submittedValue)
        throws ConverterException
    {
        return submittedValue;
    }

    @Override
    public List<SelectItem> getSelectItems(FacesContext context, UIComponent component) {
        return delegate.getSelectItems(context);
    }

    @Override
    public void provideResource(ResourceManager rm, UIComponent component) {
        super.provideResource(rm, component);
        delegate.provideResource(rm, component);
    }
}
