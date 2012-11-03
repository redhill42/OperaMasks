/*
 * $Id: AjaxResponseStateManager.java,v 1.6 2007/07/02 07:37:51 jacky Exp $
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

package org.operamasks.faces.render.ajax;

import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import java.io.IOException;
import org.operamasks.faces.render.html.HtmlResponseStateManager;

public class AjaxResponseStateManager extends HtmlResponseStateManager
{
    @Override
    protected void writeState(FacesContext context, String viewState, String renderKitId)
        throws IOException
    {
        ResponseWriter out = context.getResponseWriter();
        if (out instanceof AjaxResponseWriter) {
            ((AjaxResponseWriter)out).setViewStateChanged();
        } else if (out instanceof AjaxHtmlResponseWriter) {
            ((AjaxHtmlResponseWriter)out).setViewStateChanged();
        } else {
            super.writeState(context, viewState, renderKitId);
        }
    }
}
