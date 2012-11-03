/*
 * $Id: ELiteTag.java,v 1.2 2007/12/18 23:20:06 daniel Exp $
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

import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.servlet.jsp.JspException;
import org.operamasks.el.eval.ELProgram;
import org.operamasks.el.parser.Parser;

/**
 * @jsp.tag name="elite" body-content="JSP"
 */
public class ELiteTag extends BodyTagSupport
{
    public int doAfterBody() throws JspException {
        Parser parser = new Parser(getText());
        ELProgram prog = parser.parse();
        prog.execute(pageContext.getELContext());
        return SKIP_BODY;
    }

    private String getText() {
        String text = getBodyContent().getString();
        if (text.startsWith("<![CDATA[") && text.endsWith("]]>")) {
            text = text.substring(9, text.length()-3);
        }
        return text;
    }
}
