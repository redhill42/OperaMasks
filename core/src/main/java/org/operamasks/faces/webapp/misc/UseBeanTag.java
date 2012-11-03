/*
 * $Id: UseBeanTag.java,v 1.1 2007/09/12 10:24:34 daniel Exp $
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
package org.operamasks.faces.webapp.misc;

import javax.faces.webapp.UIComponentELTag;
import javax.faces.component.UIComponent;
import javax.el.ValueExpression;

import org.operamasks.faces.component.misc.UIUseBean;

/**
 * @jsp.tag name="useBean" body-content="JSP"
 *
 * @jsp.attribute name="id" required="false" rtexprvalue="true"
 */
public class UseBeanTag extends UIComponentELTag
{
    private ValueExpression value;
    private boolean prependId;
    private boolean prependIdSet;

    public String getComponentType() {
        return UIUseBean.COMPONENT_TYPE;
    }

    public String getRendererType() {
        return null;
    }

    /**
     * @jsp.attribute type="java.lang.Object" required="true"
     */
    public void setValue(ValueExpression value) {
        this.value = value;
    }

    /**
     * @jsp.attribute required="false" rtexprvalue="true"
     */
    public void setPrependId(boolean prependId) {
        this.prependId = prependId;
        this.prependIdSet = true;
    }

    protected void setProperties(UIComponent component) {
        super.setProperties(component);
        ((UIUseBean)component).setValue(this.value);
        if (this.prependIdSet) {
            ((UIUseBean)component).setPrependId(this.prependId);
        }
    }

    public void release() {
        super.release();
        this.value = null;
        this.prependId = false;
        this.prependIdSet = false;
    }
}
