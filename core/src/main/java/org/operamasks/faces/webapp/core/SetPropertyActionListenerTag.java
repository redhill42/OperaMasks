/*
 * $Id: SetPropertyActionListenerTag.java,v 1.4 2007/07/02 07:38:09 jacky Exp $
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

import javax.servlet.jsp.tagext.TagSupport;
import javax.servlet.jsp.JspException;
import javax.el.ValueExpression;
import javax.faces.webapp.UIComponentClassicTagBase;
import javax.faces.webapp.UIComponentELTag;
import javax.faces.component.UIComponent;
import javax.faces.component.ActionSource;
import javax.faces.event.ActionListener;
import static org.operamasks.resources.Resources.*;

public class SetPropertyActionListenerTag extends TagSupport
{
    private ValueExpression target;
    private ValueExpression value;

    public void setTarget(ValueExpression target) {
        this.target = target;
    }

    public void setValue(ValueExpression value) {
        this.value = value;
    }

    public int doStartTag()
        throws JspException
    {
        UIComponentClassicTagBase tag = UIComponentELTag.getParentUIComponentClassicTagBase(pageContext);
        if (tag == null)
            throw new JspException(_T(JSF_NOT_NESTED_IN_FACES_TAG, "f:setPropertyActionListener"));
        if (!tag.getCreated())
            return SKIP_BODY;

        UIComponent component = tag.getComponentInstance();
        if (!(component instanceof ActionSource))
            throw new JspException(_T(JSF_NOT_NESTED_IN_ACTION_TAG, tag.getClass().getName()));

        ActionListener listener = new SetPropertyActionListener(target, value);
        ((ActionSource)component).addActionListener(listener);

        return SKIP_BODY;
    }

    public void release() {
        super.release();
        target = null;
        value = null;
    }
}
