/*
 * $Id: DrawImageHelper.java,v 1.10 2008/04/29 05:21:13 lishaochuan Exp $
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

package org.operamasks.faces.render.widget;

import javax.el.MethodExpression;
import javax.el.ELException;
import javax.faces.context.FacesContext;
import javax.faces.context.ExternalContext;
import javax.faces.FacesException;
import java.awt.image.RenderedImage;
import java.awt.image.BufferedImage;
import java.awt.Graphics;
import javax.imageio.ImageWriter;
import javax.imageio.ImageIO;
import javax.imageio.stream.ImageOutputStream;
import java.util.Iterator;
import java.io.Serializable;
import java.io.OutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileNotFoundException;
import java.io.ObjectOutputStream;
import java.io.ByteArrayOutputStream;
import java.net.URL;

import org.operamasks.faces.component.widget.UIDrawImage;
import org.operamasks.util.Utils;

public class DrawImageHelper implements Serializable
{
    private static final long serialVersionUID = 7296338326431031087L;

    public static final String DRAW_IMAGE_PARAM = "org.operamasks.faces.DrawImage";

    private String           id;
    private String           type;
    private String           width;
    private String           height;
    private boolean          alpha;
    private Object           value;
    private MethodExpression draw;
    private UIDrawImage component;

    public DrawImageHelper(UIDrawImage component) {
        this.component = component;
        FacesContext context = FacesContext.getCurrentInstance();

        this.id     = component.getClientId(context);
        this.type   = component.getType();
        this.width  = component.getWidth();
        this.height = component.getHeight();
        this.alpha  = component.getAlpha();
        this.value  = component.getValue();
        this.draw   = component.getDrawMethod();

        if (value == null && draw == null) {
            throw new FacesException("UIDrawImage: Missing value or draw attribute.");
        }

        if (draw != null) {
            if ((width == null || width.length() == 0) || (height == null || height.length() == 0)) {
                throw new FacesException("UIDrawImage: Missing width or height attribute.");
            }
        }

        if (type == null) {
            type = alpha ? "image/png" : "image/jpeg";
        }
    }

    public String getType() {
        return type;
    }

    public String getWidth() {
        return width;
    }

    public String getHeight() {
        return height;
    }

    public boolean isAlpha() {
        return alpha;
    }

    public Object getValue() {
        return value;
    }

    public MethodExpression getDraw() {
        return draw;
    }

    public void save(FacesContext context) {
        ExternalContext ectx = context.getExternalContext();
        ectx.getSessionMap().put(idkey(this.id), this);
    }

    public static DrawImageHelper restore(FacesContext context) {
        ExternalContext ectx = context.getExternalContext();
        String id = ectx.getRequestParameterMap().get(DRAW_IMAGE_PARAM);
        if (id != null && ectx.getSession(false) != null) {
            return (DrawImageHelper)ectx.getSessionMap().get(idkey(id));
        } else {
            return null;
        }
    }

    public void clear(FacesContext context) {
        ExternalContext ectx = context.getExternalContext();
        if (ectx.getSession(false) != null) {
            ectx.getSessionMap().remove(idkey(this.id));
        }
    }

    private static String idkey(String id) {
        return DRAW_IMAGE_PARAM + "_" + id;
    }

    public void encode(FacesContext context, OutputStream stream)
        throws IOException
    {
        if (value != null) {
            encodeValue(value, stream);
        } else if (draw != null) {
            RenderedImage image = drawImage(context);
            writeImage(image, stream);
            this.component.setNeedRefresh(false);
        }
    }

    private void encodeValue(Object obj, OutputStream out)
        throws IOException
    {
        if (obj instanceof RenderedImage) {
            writeImage((RenderedImage)obj, out);
        } else if (obj instanceof byte[]) {
            out.write((byte[])obj);
        } else if (obj instanceof InputStream) {
            writeStream((InputStream)obj, out);
        } else if (obj instanceof URL) {
            writeStream(((URL)obj).openStream(), out);
        } else if (obj instanceof String) {
            String path = (String)obj;
            InputStream input;

            if (path.startsWith("resource:")) {
                // handle special URL scheme, read image data from classpath
                path = path.substring(9);
                if (path.startsWith("/")) {
                    path = path.substring(1);
                }
                ClassLoader loader = Thread.currentThread().getContextClassLoader();
                input = loader.getResourceAsStream(path);
            } else {
                FacesContext context = FacesContext.getCurrentInstance();
                path = context.getApplication().getViewHandler().getResourceURL(context, path);
                if (Utils.isAbsoluteURL(path)) {
                    input = new URL(path).openStream();
                } else {
                    input = context.getExternalContext().getResourceAsStream(path);
                }
            }

            if (input == null) {
                throw new FileNotFoundException("Resource not found: " + path);
            } else {
                writeStream(input, out);
            }
        }
    }

    private void writeStream(InputStream in, OutputStream out)
        throws IOException
    {
        byte[] buf = new byte[8192];
        for (int n; (n = in.read(buf)) != -1; ) {
            out.write(buf, 0, n);
        }
        in.close();
    }

    private BufferedImage drawImage(FacesContext context) {
        int imageWidth = Integer.parseInt(width);
        int imageHeight = Integer.parseInt(height);
        int imageType = alpha ? BufferedImage.TYPE_INT_ARGB : BufferedImage.TYPE_INT_RGB;

        BufferedImage image = new BufferedImage(imageWidth, imageHeight, imageType);
        Graphics g = image.getGraphics();

        try {
            draw.invoke(context.getELContext(), new Object[]{g, imageWidth, imageHeight});
        } catch (ELException ex) {
            throw new FacesException(ex);
        }

        g.dispose();
        return image;
    }

    private void writeImage(RenderedImage image, OutputStream stream)
        throws IOException
    {
        ImageWriter writer = null;
        Iterator iter = ImageIO.getImageWritersByMIMEType(type);
        if (iter.hasNext()) {
            writer = (ImageWriter)iter.next();
        }
        if (writer == null) {
            throw new FacesException("Invalid MIME type: " + type);
        }

        ImageOutputStream output = ImageIO.createImageOutputStream(stream);
        writer.setOutput(output);
        writer.write(image);
        output.flush();
        writer.dispose();
    }

    private void writeObject(ObjectOutputStream out)
        throws IOException
    {
        out.defaultWriteObject();

        if ((value != null) && !(value instanceof Serializable)) {
            ByteArrayOutputStream buf = new ByteArrayOutputStream();
            encodeValue(value, buf);
            value = buf.toByteArray();
        }
    }

    public UIDrawImage getComponent() {
        return component;
    }
}
