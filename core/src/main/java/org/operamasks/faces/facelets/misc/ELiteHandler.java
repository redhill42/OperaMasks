/*
 * $Id: ELiteHandler.java,v 1.3 2008/01/03 10:18:56 daniel Exp $
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

package org.operamasks.faces.facelets.misc;

import java.io.IOException;
import java.io.Reader;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.io.FileNotFoundException;
import java.util.Iterator;
import java.lang.ref.SoftReference;
import javax.faces.component.UIComponent;
import javax.faces.FacesException;
import javax.faces.context.FacesContext;
import javax.faces.context.ExternalContext;
import javax.el.ELException;

import com.sun.facelets.tag.TagHandler;
import com.sun.facelets.tag.TagConfig;
import com.sun.facelets.tag.TextHandler;
import com.sun.facelets.tag.TagAttribute;
import com.sun.facelets.FaceletContext;
import org.operamasks.el.eval.ELProgram;
import org.operamasks.el.parser.Parser;
import org.operamasks.el.parser.ResourceResolver;

public class ELiteHandler extends TagHandler implements ResourceResolver
{
    private TagAttribute src;
    private String path;
    private int line;
    private boolean reparse;

    private SoftReference<ELProgram> progRef;

    public ELiteHandler(TagConfig config) {
        super(config);
        src = config.getTag().getAttributes().get("src");
        path = config.getTag().getLocation().getPath();
        line = config.getTag().getLocation().getLine();
    }

    public void apply(FaceletContext ctx, UIComponent parent)
        throws IOException, FacesException, ELException
    {
        ELProgram prog = (progRef == null) ? null : progRef.get();
        if (prog == null || reparse) {
            Parser parser = new Parser(getText(ctx));
            parser.setFileName(path);
            parser.setLineNumber(line);
            parser.setResourceResolver(this);
            prog = parser.parse();
            progRef = new SoftReference<ELProgram>(prog);
        }

        prog.execute(ctx, path, line);
    }

    private String getText(FaceletContext ctx)
        throws IOException
    {
        StringBuilder buf = new StringBuilder();

        if (src != null) {
            read(src.getValue(ctx), buf);
        }

        Iterator iter = findNextByType(TextHandler.class);
        while (iter.hasNext()) {
            TextHandler th = (TextHandler)iter.next();
            String text = th.getText().trim();
            if (text.startsWith("<![CDATA[") && text.endsWith("]]>")) {
                text = text.substring(9, text.length()-3);
            }
            buf.append(text);
        }

        return buf.toString();
    }

    public Reader open(String path) throws IOException {
        reparse = true;

        ExternalContext ext = FacesContext.getCurrentInstance().getExternalContext();
        InputStream in = ext.getResourceAsStream(path);

        if (in == null) {
            throw new FileNotFoundException(path + ": file not found.");
        } else {
            return new InputStreamReader(in);
        }
    }

    private void read(String src, StringBuilder buf)
        throws IOException
    {
        if (!src.startsWith("/")) {
            src = path.substring(0, path.lastIndexOf('/')+1) + src;
        }

        Reader reader = open(src);
        char[] cbuf = new char[8192];
        for (int len; (len = reader.read(cbuf)) != -1; ) {
            buf.append(cbuf, 0, len);
        }
        reader.close();
    }
}
