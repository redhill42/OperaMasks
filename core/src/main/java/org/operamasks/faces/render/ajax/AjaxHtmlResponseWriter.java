/*
 * $Id: AjaxHtmlResponseWriter.java,v 1.10 2007/09/11 12:50:48 daniel Exp $
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

import javax.faces.context.ResponseWriter;
import javax.faces.context.FacesContext;

import org.operamasks.faces.render.html.HtmlResponseWriter;
import org.operamasks.faces.render.html.HtmlRenderer;
import org.operamasks.faces.util.FacesUtils;
import org.operamasks.faces.util.HtmlEncoder;

import java.io.Writer;
import java.io.IOException;
import java.util.Formatter;
import java.util.Map;
import java.util.LinkedHashMap;

public class AjaxHtmlResponseWriter extends HtmlResponseWriter
{
    private AjaxResponseWriter outer;

    private boolean viewStateChanged;
    private boolean writeFullState;
    private Map<String,String> requestParams;

    AjaxHtmlResponseWriter(Writer writer, String contentType, String encoding) {
        super(writer, contentType, encoding);
        if (!FacesUtils.isTransientStateSupported(FacesContext.getCurrentInstance())) {
            this.writeFullState = true;
        }
    }

    AjaxHtmlResponseWriter(Writer writer, AjaxResponseWriter outer) {
        super(writer, outer.getContentType(), outer.getCharacterEncoding());
        this.outer = outer;
    }

    public ResponseWriter cloneWithWriter(Writer writer) {
        return new AjaxHtmlResponseWriter(writer, getContentType(), getCharacterEncoding());
    }

    public void setViewStateChanged() {
        if (outer != null) {
            outer.setViewStateChanged();
        } else {
            this.viewStateChanged = true;
            if (!this.writeFullState && !FacesUtils.isMarkedForTransientState(FacesContext.getCurrentInstance())) {
                this.writeFullState = true;
            }
        }
    }

    public void setViewStateChanged(boolean writeFullState) {
        if (outer != null) {
            outer.setViewStateChanged(writeFullState);
        } else {
            this.viewStateChanged = true;
            if (writeFullState) {
                this.writeFullState = true;
            }
        }
    }

    public boolean isViewStateChanged() {
        return this.viewStateChanged;
    }

    public boolean isWriteFullState() {
        return this.writeFullState;
    }

    public void addRequestParameter(String key, String value) {
        if (this.outer != null) {
            this.outer.addRequestParameter(key, value);
        } else {
            if (key != null && value != null) {
                if (this.requestParams == null)
                    this.requestParams = new LinkedHashMap<String,String>();
                this.requestParams.put(key, value);
                this.setViewStateChanged(false);
            }
        }
    }

    public void writeState(FacesContext context)
        throws IOException
    {
        // called by AjaxHtmlPageRenderer to write view state at end of document
        if (viewStateChanged) {
            String[] state = FacesUtils.getViewState(context);
            Formatter fmt = new Formatter();

            fmt.format("OM.ajax.actionId=%s;\n", HtmlEncoder.enquote(HtmlRenderer.getActionURL(context)));
            if (!writeFullState)
                fmt.format("OM.ajax.viewId='%s';\n", context.getViewRoot().getViewId());
            if (state[0] != null)
                fmt.format("OM.ajax.viewState='%s';\n", state[0]);
            if (state[1] != null)
                fmt.format("OM.ajax.renderKitId='%s';\n", state[1]);

            if (this.requestParams != null) {
                for (String key : this.requestParams.keySet()) {
                    String value = this.requestParams.get(key);
                    fmt.format("OM.ajax.addRequestParameter(%s,%s);\n",
                               HtmlEncoder.enquote(key), HtmlEncoder.enquote(value));
                }
            }

            String script = fmt.toString();
            if (script.length() > 0) {
                out.write("<script type=\"text/javascript\">\n");
                out.write(script);
                out.write("</script>\n");
            }
        }
    }
}
