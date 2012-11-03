/*
 * $Id: PhaseListenerTag.java,v 1.4 2007/07/02 07:38:09 jacky Exp $
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
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseListener;
import org.operamasks.util.Utils;

public class PhaseListenerTag extends TagSupport
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
        FacesContext context = FacesContext.getCurrentInstance();
        UIViewRoot root = context.getViewRoot();
        PhaseListener listener = null;

        if (binding != null) {
            listener = (PhaseListener)binding.getValue(context.getELContext());
        }
        if (listener == null && type != null) {
            listener = createPhaseListener(context);
            if (listener != null && binding != null) {
                binding.setValue(context.getELContext(), listener);
            }
        }
        if (listener != null)
            root.addPhaseListener(listener);
        return SKIP_BODY;
    }

    private PhaseListener createPhaseListener(FacesContext context)
        throws JspException
    {
        try {
            String className = type.getValue(context.getELContext()).toString();
            return (PhaseListener)Utils.findClass(className).newInstance();
        } catch (Exception ex) {
            throw new JspException(ex);
        }
    }
}
