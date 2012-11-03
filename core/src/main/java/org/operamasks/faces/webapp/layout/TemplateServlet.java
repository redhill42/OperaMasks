/*
 * $Id: TemplateServlet.java,v 1.4 2007/07/02 07:38:11 jacky Exp $
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

package org.operamasks.faces.webapp.layout;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import javax.faces.context.FacesContext;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.InputStreamReader;
import java.io.StringWriter;

public class TemplateServlet extends HttpServlet
{
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException
    {
        FacesContext context = FacesContext.getCurrentInstance();

        TemplateBuilder builder = TemplateBuilder.newBuilder(context);
        if (builder == null) {
            return;
        }

        String template = readTemplate(request, response);
        if (template == null) {
            return;
        }

        TemplateProcessor processor = new TemplateProcessor(builder, template);
        processor.process();
    }

    private String readTemplate(HttpServletRequest request, HttpServletResponse response)
        throws IOException
    {
        String path = (String)request.getAttribute("javax.servlet.include.servlet_path");
        String pathInfo = (String)request.getAttribute("javax.servlet.include.path_info");

        if (path == null) {
            return null;
        } else if (pathInfo != null) {
            path = path.concat(pathInfo);
        }

        InputStream ins = getServletContext().getResourceAsStream(path);
        StringWriter sw = new StringWriter();

        try {
            String charset = request.getParameter("charset");
            if (charset == null) {
                charset = response.getCharacterEncoding();
                if (charset == null)
                    charset = "ISO-8859-1";
            }

            Reader in = new InputStreamReader(ins, charset);
            for (int c; (c = in.read()) != -1; ) {
                sw.write(c);
            }
        } finally {
            ins.close();
        }

        return sw.toString();
    }

    private static final class TemplateProcessor
    {
        private TemplateBuilder builder;
        private String template;
        private int pos;
        private int next;

        TemplateProcessor(TemplateBuilder builder, String template) {
            this.builder = builder;
            this.template = template;
            this.pos = 0;
            this.next = -1;
        }

        public void process() throws IOException {
            do {
                next = template.indexOf('$', pos);
                if (next == -1) {
                    passthrough(); // passthrough character after placeholder
                    builder.close();
                    return;
                }

                passthrough(); // passthrough character before '$'
                next++; // skip '$'

                if (nextchar() == '{') { // found "${"
                    int end = template.indexOf('}', next);
                    if (end == -1) {
                        next++; // skip '{'
                        passthrough();
                        continue;
                    } else {
                        String expr = template.substring(next-1, end+1);
                        pos = next = end+1; // eating "${...}"
                        eval(expr);
                    }
                } else {
                    String keyword = keyword();
                    if (keyword == null) {
                        passthrough();
                        continue;
                    }

                    if (keyword.equals("slot")) {
                        String param = param();
                        if (param == null || param.length() == 0) {
                            builder.addFaceletSlot();
                        } else if (param.startsWith("\"") && param.endsWith("\"")) {
                            param = param.substring(1, param.length()-1);
                            builder.addFaceletSlot(param);
                        } else {
                            try {
                                int index = Integer.parseInt(param);
                                builder.addFaceletSlot(index);
                            } catch (NumberFormatException ex) {
                                passthrough();
                                continue;
                            }
                        }
                        pos = next; // eating "$slot(...)"
                    } else {
                        passthrough();
                    }
                }
            } while (true);
        }

        private int nextchar() {
            if (next < template.length())
                return template.charAt(next);
            return -1;
        }

        private void passthrough() throws IOException {
            if (next == -1)
                next = template.length();
            if (next > pos)
                builder.getWriter().write(template.substring(pos, next));
            pos = next;
        }

        private void whitespace() {
            while (next < template.length()) {
                char c = template.charAt(next);
                if (!Character.isWhitespace(c))
                    break;
                next++;
            }
        }

        private String keyword() {
            int start = next;
            while (next < template.length()) {
                char c = template.charAt(next);
                if (!Character.isLetter(c))
                    break;
                next++;
            }

            if (next > start) {
                return template.substring(start, next);
            } else {
                return null;
            }
        }

        private String param() {
            int start = next; // save start position

            int c;
            int begStr = -1, endStr = -1;

            whitespace();
            if (nextchar() == '(') {
                next++; // skip '('
                whitespace();

                c = nextchar();
                if (c == '"') {
                    begStr = next++;
                    endStr = template.indexOf('"', next);
                    if (endStr != -1) {
                        next = ++endStr;
                    }
                } else if (Character.isDigit(c)) {
                    begStr = next;
                    while (++next < template.length()) {
                        c = template.charAt(next);
                        if (!Character.isDigit((char)c))
                            break;
                    }
                    endStr = next;
                } else if (c == ')') {
                    next++;
                    return "";
                }

                if (begStr != -1 && endStr != -1) {
                    whitespace();
                    if (nextchar() == ')') {
                        next++;
                        return template.substring(begStr, endStr);
                    }
                }
            }

            next = start; // restore start position
            return null;
        }

        private void eval(String expr) throws IOException {
            FacesContext context = FacesContext.getCurrentInstance();
            String value = (String)context.getApplication()
                .evaluateExpressionGet(context, expr, String.class);
            if (value != null) {
                builder.getWriter().write(value);
            }
        }
    }
}
