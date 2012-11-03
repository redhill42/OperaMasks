/*
 * $Id: AjaxPhaseListener.java,v 1.5 2007/07/02 07:37:53 jacky Exp $
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

import javax.faces.event.PhaseListener;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.context.FacesContext;
import javax.faces.FacesException;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.net.URL;
import java.io.InputStream;
import java.io.IOException;
import java.io.File;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

public class AjaxPhaseListener implements PhaseListener
{
    public static final String AJAX_SCRIPT_VIEW_ID = "/_global/script/ajax-script";
    public static final String LOGGING_SCRIPT_VIEW_ID = "/_global/script/logging-script";

    private static final String AJAX_SCRIPT_RESOURCE_NAME = "/META-INF/ajax.js";
    private static final String LOGGING_SCRIPT_RESOURCE_NAME = "/META-INF/logging.js";

    private static final class CachedScript {
        public final String viewId;
        public final String resourceName;
        public byte[] data;
        public long lastModified;

        public CachedScript(String viewId, String resourceName) {
            this.viewId = viewId;
            this.resourceName = resourceName;
            this.data = null;
            this.lastModified = -1;
        }
    }

    private final CachedScript[] cachedScripts = new CachedScript[] {
        new CachedScript(AJAX_SCRIPT_VIEW_ID, AJAX_SCRIPT_RESOURCE_NAME),
        new CachedScript(LOGGING_SCRIPT_VIEW_ID, LOGGING_SCRIPT_RESOURCE_NAME)
    };

    private CachedScript getCachedScript(String viewId) {
        for (CachedScript s : cachedScripts) {
            if (viewId.indexOf(s.viewId) != -1)
                return s;
        }
        return null;
    }

    public PhaseId getPhaseId() {
        return PhaseId.RESTORE_VIEW;
    }

    public void beforePhase(PhaseEvent event) {}

    public void afterPhase(PhaseEvent event) {
        FacesContext context = event.getFacesContext();
        if (context.getViewRoot() != null) {
            String viewId = context.getViewRoot().getViewId();
            if (viewId != null) {
                CachedScript script = getCachedScript(viewId);
                if (script != null) {
                    try {
                        context.responseComplete();
                        renderScript(context, script);
                    } catch (IOException ex) {
                        throw new FacesException(ex);
                    }
                }
            }
        }
    }

    private void renderScript(FacesContext context, CachedScript script)
        throws IOException
    {
        URL url = AjaxPhaseListener.class.getResource(script.resourceName);
        long modTime = getLastModified(url);
        byte[] data;

        synchronized (script) {
            if (script.data == null || script.lastModified != modTime) {
                data = script.data = loadScript(url);
                script.lastModified = modTime;
            } else {
                data = script.data;
            }
        }

        HttpServletRequest request = (HttpServletRequest)
            context.getExternalContext().getRequest();
        HttpServletResponse response = (HttpServletResponse)
            context.getExternalContext().getResponse();

        response.setContentType("text/javascript;charset=ISO-8859-1");
        response.setContentLength(data.length);
        response.setDateHeader("Last-Modified", modTime);

        if (!"HEAD".equals(request.getMethod())) {
            OutputStream out = response.getOutputStream();
            out.write(data);
        }
    }

    private byte[] loadScript(URL url) throws IOException {
        InputStream in = url.openStream();
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            for (int c; (c = in.read()) != -1; ) {
                out.write(c);
            }
            return out.toByteArray();
        } finally {
            in.close();
        }
    }

    private long getLastModified(URL url) {
        try {
            if (url.getProtocol().equals("file")) {
                return new File(url.getFile()).lastModified();
            } else if (url.getProtocol().equals("jar")) {
                String path = url.getPath();
                if (path.startsWith("file:") && path.indexOf("!/") != -1) {
                    path = path.substring(5, path.indexOf("!/"));
                    return new File(path).lastModified();
                }
            }
            return url.openConnection().getLastModified();
        } catch (Exception ex) {
            return -1;
        }
    }
}
