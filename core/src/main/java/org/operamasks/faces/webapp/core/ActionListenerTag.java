/*
 * $Id: ActionListenerTag.java,v 1.5 2007/07/02 07:38:10 jacky Exp $
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

import static org.operamasks.resources.Resources.JSF_NOT_NESTED_IN_ACTION_TAG;
import static org.operamasks.resources.Resources.JSF_NOT_NESTED_IN_FACES_TAG;
import static org.operamasks.resources.Resources._T;

import javax.el.ValueExpression;
import javax.faces.component.ActionSource;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionListener;
import javax.faces.webapp.UIComponentClassicTagBase;
import javax.faces.webapp.UIComponentELTag;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import org.operamasks.util.Utils;

public class ActionListenerTag extends TagSupport
{
    private ValueExpression type;
    private ValueExpression binding;

    public void setType(ValueExpression type) {
        this.type = type;
    }

    public void setBinding(ValueExpression binding) {
        this.binding = binding;
    }

    public int doStartTag()
        throws JspException
    {
        UIComponentClassicTagBase tag = UIComponentELTag.getParentUIComponentClassicTagBase(pageContext);
        if (tag == null)
            throw new JspException(_T(JSF_NOT_NESTED_IN_FACES_TAG, "f:actionListener"));
        if (!tag.getCreated())
            return SKIP_BODY;

        FacesContext context = FacesContext.getCurrentInstance();
        UIComponent component = tag.getComponentInstance();
        if (!(component instanceof ActionSource))
            throw new JspException(_T(JSF_NOT_NESTED_IN_ACTION_TAG, "f:actionListener"));

        ActionListener listener = null;
        if (binding != null) {
            listener = (ActionListener)binding.getValue(context.getELContext());
        }
        if (listener == null && type != null) {
            listener = createActionListener(context);
            if (listener != null && binding != null) {
                binding.setValue(context.getELContext(), listener);
            }
        }
        if (listener != null) {
            ((ActionSource)component).addActionListener(listener);
        }
        return SKIP_BODY;
    }

    private ActionListener createActionListener(FacesContext context)
        throws JspException
    {
        try {
            String className = type.getValue(context.getELContext()).toString();
            return (ActionListener)Utils.findClass(className).newInstance();
        } catch (Exception ex) {
            throw new JspException(ex);
        }
    }

    public void release() {
        super.release();
        type = null;
        binding = null;
    }
}
