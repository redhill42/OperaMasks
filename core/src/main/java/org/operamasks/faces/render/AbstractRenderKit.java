/*
 * $Id: AbstractRenderKit.java,v 1.6 2008/02/16 07:00:26 lishaochuan Exp $
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

package org.operamasks.faces.render;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.faces.context.ResponseStream;
import javax.faces.context.ResponseWriter;
import javax.faces.render.RenderKit;
import javax.faces.render.Renderer;

import org.operamasks.faces.debug.Debug;
import org.operamasks.faces.interceptor.ProxyFactory;

public abstract class AbstractRenderKit extends RenderKit
{
    private static final String[] SUPPORTED_CONTENT_TYPES = {
        "text/html",
        "application/xhtml+xml",
        "application/xml",
        "text/xml",
        "*/*"
    };

    private static final String DEFAULT_CONTENT_TYPE = "text/html";
    private static final String DEFAULT_ENCODING = "ISO-8859-1";

    protected Map<String, Map<String, Renderer>> familyMap;

    protected AbstractRenderKit() {
        this.familyMap = new HashMap<String, Map<String, Renderer>>();
    }

    public void addRenderer(String family, String rendererType, Renderer renderer) {
        if (family == null || rendererType == null || renderer == null) {
            throw new NullPointerException();
        }

        synchronized (familyMap) {
            Map<String,Renderer> rendererMap = familyMap.get(family);
            if (rendererMap == null) {
                rendererMap = new HashMap<String, Renderer>();
                familyMap.put(family, rendererMap);
            }
            rendererMap.put(rendererType, renderer);
        }
    }

    public Renderer getRenderer(String family, String rendererType) {
        return this.getPrivateRenderer(family, rendererType);
    }

    public Renderer getPrivateRenderer(String family, String rendererType) {
        if (family == null || rendererType == null) {
            throw new NullPointerException();
        }

        synchronized (familyMap) {
            Map<String,Renderer> rendererMap = familyMap.get(family);
            if (rendererMap == null)
                return null;
            Renderer renderer = rendererMap.get(rendererType);
            
            // Creating of proxy object is delayed to here to make aom starting faster
            if (ProxyFactory.needProxy(renderer) && !ProxyFactory.isProxyRenderer(renderer)) {
            	renderer = ProxyFactory.createProxyRenderer(renderer);
            	rendererMap.put(rendererType, renderer);
            }
            
            return renderer;
        }
    }
    
	protected abstract ResponseWriter implCreateResponseWriter
        (Writer writer, String contentType, String encoding);

    protected String[] getSupportedContentTypes() {
        return SUPPORTED_CONTENT_TYPES;
    }

    // Non-HTML render kit SHOULD override this method
    public ResponseWriter createResponseWriter(Writer writer,
                                               String desiredContentType,
                                               String encoding)
    {
        if (writer == null)
            return null;

        FacesContext context = FacesContext.getCurrentInstance();
        String[] supportedTypes = getSupportedContentTypes();
        String contentType = null;

        if (desiredContentType != null) {
            // check the content type passed in is supported
            contentType = findMatch(desiredContentType, supportedTypes);
        } else {
            // check the response content type
            desiredContentType = context.getExternalContext().getResponseContentType();
            if (desiredContentType != null) {
                contentType = findMatch(desiredContentType, supportedTypes);
            }

            // check the Accpet header content type
            if (contentType == null) {
                String[] acceptTypes = context.getExternalContext().getRequestHeaderValuesMap().get("Accept");
                if (acceptTypes.length > 0) {
                    StringBuffer buf = new StringBuffer();
                    buf.append(acceptTypes[0]);
                    for (int i = 1; i < acceptTypes.length; i++) {
                        buf.append(',');
                        buf.append(acceptTypes[i]);
                    }
                    desiredContentType = buf.toString();
                } else {
                    desiredContentType = null;
                }

                if (desiredContentType != null) {
                    contentType = findMatch(desiredContentType, supportedTypes);
                } else {
                    // default to text/html
                    contentType = DEFAULT_CONTENT_TYPE;
                }
            }
        }

        if (contentType == null) {
            throw new IllegalArgumentException("The content type '" + desiredContentType +
                                               "' is not supported to create response writer.");
        }

        if (contentType.equals("*/*")) {
            contentType = DEFAULT_CONTENT_TYPE;
        }

        if (encoding != null) {
            if (!Charset.isSupported(encoding)) {
                throw new IllegalArgumentException("The character encoding '" + encoding +
                                                   "' is not supported to create response writer.");
            }
        } else {
            encoding = context.getExternalContext().getResponseCharacterEncoding();
            if (encoding == null) {
                encoding = DEFAULT_ENCODING;
            }
        }

        return implCreateResponseWriter(writer, contentType, encoding);
    }

    private String findMatch(String desiredContentType, String[] supportedTypes) {
        String[] desiredTypes = desiredContentType.split(",");
        for (int i = 0; i < desiredTypes.length; i++) {
            int semi = desiredTypes[i].indexOf(';');
            if (semi != -1) {
                desiredTypes[i] = desiredTypes[i].substring(0, semi);
            }
            desiredTypes[i] = desiredTypes[i].trim();
        }

        // For each entry in the desiredTypes array, look for a match in
        // the supportedTypes array
        for (String curDesiredType : desiredTypes) {
            for (String curSupportedType : supportedTypes) {
                if (curDesiredType.indexOf(curSupportedType) != -1) {
                    return curDesiredType;
                }
            }
        }

        return null;
    }

    public ResponseStream createResponseStream(final OutputStream stream) {
        return new ResponseStream() {
            public void write(int b) throws IOException {
                stream.write(b);
            }
            public void write(byte b[]) throws IOException {
                stream.write(b);
            }
            public void write(byte b[], int off, int len) throws IOException {
                stream.write(b, off, len);
            }
            public void flush() throws IOException {
                stream.flush();
            }
            public void close() throws IOException {
                stream.close();
            }
        };
    }
}
