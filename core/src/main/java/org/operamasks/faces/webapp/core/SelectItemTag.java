/*
 * $Id: SelectItemTag.java,v 1.4 2007/07/02 07:38:09 jacky Exp $
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

import javax.faces.webapp.UIComponentELTag;
import javax.faces.component.UIComponent;
import javax.el.ValueExpression;

public class SelectItemTag extends UIComponentELTag
{
    private ValueExpression itemValue;
    private ValueExpression itemLabel;
    private ValueExpression itemDisabled;
    private ValueExpression itemDescription;
    private ValueExpression value;

    public String getRendererType() {
        return null;
    }

    public String getComponentType() {
        return javax.faces.component.UISelectItem.COMPONENT_TYPE;
    }

    public void setItemValue(ValueExpression itemValue) {
        this.itemValue = itemValue;
    }

    public void setItemLabel(ValueExpression itemLabel) {
        this.itemLabel = itemLabel;
    }

    public void setItemDisabled(ValueExpression itemDisabled) {
        this.itemDisabled = itemDisabled;
    }

    public void setItemDescription(ValueExpression itemDescription) {
        this.itemDescription = itemDescription;
    }

    public void setValue(ValueExpression value) {
        this.value = value;
    }

    protected void setProperties(UIComponent component) {
        super.setProperties(component);

        if (value != null)
            component.setValueExpression("value", value);
        if (itemValue != null)
            component.setValueExpression("itemValue", itemValue);
        if (itemLabel != null)
            component.setValueExpression("itemLabel", itemLabel);
        if (itemDescription != null)
            component.setValueExpression("itemDescription", itemDescription);
        if (itemDisabled != null)
            component.setValueExpression("itemDisabled", itemDisabled);
    }

    public void release() {
        super.release();
        itemValue = null;
        itemLabel = null;
        itemDisabled = null;
        itemDescription = null;
        value = null;
    }
}
