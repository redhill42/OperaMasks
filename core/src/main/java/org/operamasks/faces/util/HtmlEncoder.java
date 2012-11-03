/*
 * $Id: HtmlEncoder.java,v 1.6 2007/07/02 07:38:13 jacky Exp $
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

package org.operamasks.faces.util;

import javax.faces.context.FacesContext;
import java.io.IOException;
import java.io.Writer;
import java.io.StringWriter;
import java.util.BitSet;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.CharBuffer;
import java.nio.ByteBuffer;

public final class HtmlEncoder
{
    // Entities from HTML 4.0, section 24.2.1; character codes 0xA0 to 0xFF
    private static String[] ISO8859_1_ENTITIES = new String[] {
        "&nbsp;",
        "&iexcl;",
        "&cent;",
        "&pound;",
        "&curren;",
        "&yen;",
        "&brvbar;",
        "&sect;",
        "&uml;",
        "&copy;",
        "&ordf;",
        "&laquo;",
        "&not;",
        "&shy;",
        "&reg;",
        "&macr;",
        "&deg;",
        "&plusmn;",
        "&sup2;",
        "&sup3;",
        "&acute;",
        "&micro;",
        "&para;",
        "&middot;",
        "&cedil;",
        "&sup1;",
        "&ordm;",
        "&raquo;",
        "&frac14;",
        "&frac12;",
        "&frac34;",
        "&iquest;",
        "&Agrave;",
        "&Aacute;",
        "&Acirc;",
        "&Atilde;",
        "&Auml;",
        "&Aring;",
        "&AElig;",
        "&Ccedil;",
        "&Egrave;",
        "&Eacute;",
        "&Ecirc;",
        "&Euml;",
        "&Igrave;",
        "&Iacute;",
        "&Icirc;",
        "&Iuml;",
        "&ETH;",
        "&Ntilde;",
        "&Ograve;",
        "&Oacute;",
        "&Ocirc;",
        "&Otilde;",
        "&Ouml;",
        "&times;",
        "&Oslash;",
        "&Ugrave;",
        "&Uacute;",
        "&Ucirc;",
        "&Uuml;",
        "&Yacute;",
        "&THORN;",
        "&szlig;",
        "&agrave;",
        "&aacute;",
        "&acirc;",
        "&atilde;",
        "&auml;",
        "&aring;",
        "&aelig;",
        "&ccedil;",
        "&egrave;",
        "&eacute;",
        "&ecirc;",
        "&euml;",
        "&igrave;",
        "&iacute;",
        "&icirc;",
        "&iuml;",
        "&eth;",
        "&ntilde;",
        "&ograve;",
        "&oacute;",
        "&ocirc;",
        "&otilde;",
        "&ouml;",
        "&divide;",
        "&oslash;",
        "&ugrave;",
        "&uacute;",
        "&ucirc;",
        "&uuml;",
        "&yacute;",
        "&thorn;",
        "&yuml;"
    };

    public static void encode(Writer out, String s)
        throws IOException
    {
        int length = s.length();
        if (length == 0) return;
        int start = 0;
        for (int i = 0; i < length; i++) {
            char ch = s.charAt(i);
            String escape = encodeSingle(ch);
            if (escape != null) {
                if (start < i)
                    out.write(s.substring(start, i));
                start = i + 1;
                out.write(escape);
            }
        }
        if (start == 0) {
            out.write(s);
        } else if (start < length) {
            out.write(s.substring(start, length));
        }
    }

    public static String encode(String s) {
        int length = s.length();
        if (length == 0) return "";

        StringBuffer out = null;
        int start = 0;
        for (int i = 0; i < length; i++) {
            char ch = s.charAt(i);
            String escape = encodeSingle(ch);
            if (escape != null) {
                if (out == null)
                    out = new StringBuffer();
                if (start < i)
                    out.append(s.substring(start, i));
                start = i + 1;
                out.append(escape);
            }
        }
        if (out == null) {
            return s;
        } else {
            if (start < length)
                out.append(s.substring(start, length));
            return out.toString();
        }
    }

    public static void encode(Writer out, char[] text, int off, int len)
        throws IOException
    {
        if ((off < 0) || (off > text.length) || (len < 0) ||
            ((off + len) > text.length) || (off + len < 0)) {
            throw new IndexOutOfBoundsException();
        } else if (len == 0) {
            return;
        }

        int start = off;
        int end = off + len;
        for (int i = off; i < end; i++) {
            char ch = text[i];
            String escape = encodeSingle(ch);
            if (escape != null) {
                if (start < i)
                    out.write(text, start, i - start);
                out.write(escape);
                start = i + 1;
            }
        }
        if (start < end) {
            out.write(text, start, end - start);
        }
    }

    public static String encode(char[] text, int off, int len) {
        if ((off < 0) || (off > text.length) || (len < 0) ||
            ((off + len) > text.length) || (off + len < 0)) {
            throw new IndexOutOfBoundsException();
        } else if (len == 0) {
            return "";
        }

        StringBuffer out = null;
        int start = off;
        int end = off + len;
        for (int i = off; i < end; i++) {
            char ch = text[i];
            String escape = encodeSingle(ch);
            if (escape != null) {
                if (out == null)
                    out = new StringBuffer();
                if (start < i)
                    out.append(text, start, i - start);
                out.append(escape);
                start = i + 1;
            }
        }
        if (out == null) {
            return new String(text, off, len);
        } else {
            if (start < end)
                out.append(text, start, end - start);
            return out.toString();
        }
    }

    private static String encodeSingle(char ch) {
        if (ch >= 34 && ch <= 62) {
            if (ch == '<') {
                return "&lt;";
            } else if (ch == '>') {
                return "&gt;";
            } else if (ch == '&') {
                return "&amp;";
            } else if (ch == '"') {
                return "&quot;";
            }
        } else if (ch >= 0xA0) {
            if (ch <= 0xff) {
                // ISO-8859-1 entities: encode as needed
                return ISO8859_1_ENTITIES[ch - 0xA0];
            } else {
                // Double-byte characters encoded as CharRef.
                return "&#" + String.valueOf((int)ch) + ";";
            }
        }
        return null;
    }

    private static BitSet dontNeedEncoding = new BitSet();

    static {
        // The list of characters that are not encoded
        for (int i = 'a'; i <= 'z'; i++) {
            dontNeedEncoding.set(i);
        }
        for (int i = 'A'; i <= 'Z'; i++) {
            dontNeedEncoding.set(i);
        }
        for (int i = '0'; i <= '9'; i++) {
            dontNeedEncoding.set(i);
        }

        // Don't double encode '%'
        dontNeedEncoding.set('%');

        // encoding a space to a + is done in the encodeURI() method
        dontNeedEncoding.set(' ');

        dontNeedEncoding.set('#');
        dontNeedEncoding.set('&');
        dontNeedEncoding.set('=');
        dontNeedEncoding.set('/');
        dontNeedEncoding.set('-');
        dontNeedEncoding.set('_');
        dontNeedEncoding.set('.');
        dontNeedEncoding.set('!');
        dontNeedEncoding.set('~');
        dontNeedEncoding.set('*');
        dontNeedEncoding.set('\'');
        dontNeedEncoding.set('(');
        dontNeedEncoding.set(')');
    }

    private static final int maxBytesPerChar = 10;

    public static void encodeURI(Writer out, String s, String clientEncoding)
        throws IOException
    {
        encodeURI(out, s, clientEncoding, true);
    }

    public static void encodeURIComponent(Writer out, String s, String clientEncoding)
        throws IOException
    {
        encodeURI(out, s, clientEncoding, false);
    }

    private static void encodeURI(Writer out, String s, String clientEncoding, boolean beforeQuery)
        throws IOException
    {
        int length = s.length();
        if (length == 0) return;

        CharsetEncoder utf8Encoder = null;
        CharsetEncoder clientEncoder = null;
        CharBuffer charBuf = null;
        ByteBuffer byteBuf = null;

        int start = 0;
        for (int i = 0; i < length; i++) {
            int ch = (int)s.charAt(i);
            boolean needEscape;
            CharsetEncoder encoder = null;

            // XXX Apusic Servlet Engine decodes request URI using UTF-8 encoding
            // and decodes query string using client encoding. This behavior
            // may be revised in the future.
            //
            // All characters before the start of the query string will be
            // encoded using UTF-8. Characters after the start of the
            // query string will be encoded using client encoding.
            if (beforeQuery) {
                needEscape = (ch <= ' ') || (ch >= 127) || (ch == '"');
                if (needEscape) {
                    if (utf8Encoder == null)
                        utf8Encoder = Charset.forName("UTF-8").newEncoder();
                    encoder = utf8Encoder;
                }
                if (ch == '?') {
                    beforeQuery = false;
                }
            } else {
                needEscape = !dontNeedEncoding.get(ch);
                if (needEscape) {
                    if (clientEncoder == null)
                        clientEncoder = Charset.forName(clientEncoding).newEncoder();
                    encoder = clientEncoder;
                }
            }

            if (needEscape) {
                // write out unencoded characters
                if (start < i)
                    out.write(s.substring(start, i));
                start = i + 1;

                if (charBuf == null) {
                    charBuf = CharBuffer.allocate(1);
                    byteBuf = ByteBuffer.allocate(maxBytesPerChar);
                } else {
                    charBuf.clear();
                    byteBuf.clear();
                }

                // convert to external encoding before hex conversion
                try {
                    assert encoder != null;
                    charBuf.put((char)ch).flip();
                    encoder.encode(charBuf, byteBuf, true);
                    byteBuf.flip();
                } catch (Exception ex) {
                    encoder.reset();
                    continue;
                }

                byte[] ba = byteBuf.array();
                int lim = byteBuf.limit();
                for (int j = 0; j < lim; j++) {
                    int b = ba[j] & 0xff;
                    out.write('%');
                    out.write(intToHex((b >> 4) & 0xF));
                    out.write(intToHex((b & 0xF)));
                }
                encoder.reset();
            } else {
                if (ch == ' ') {
                    if (start < i)
                        out.write(s.substring(start, i));
                    start = i + 1;
                    out.write('+');
                }
            }
        }
        if (start == 0) {
            out.write(s);
        } else if (start < length) {
            out.write(s.substring(start, length));
        }
    }

    public static String encodeURI(String s, String encoding) {
        StringWriter out = new StringWriter();
        try {
            encodeURI(out, s, encoding, true);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        return out.toString();
    }

    public static String encodeURIComponent(String s, String encoding) {
        StringWriter out = new StringWriter();
        try {
            encodeURI(out, s, encoding, false);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        return out.toString();
    }

    public static String encodeURI(String s) {
        return encodeURI(s, getCurrentEncoding());
    }

    public static String encodeURIComponent(String s) {
        return encodeURIComponent(s, getCurrentEncoding());
    }
    
    private static String getCurrentEncoding() {
        FacesContext context = FacesContext.getCurrentInstance();
        if (context.getResponseWriter() != null) {
            return context.getResponseWriter().getCharacterEncoding();
        } else {
            return context.getExternalContext().getResponseCharacterEncoding();
        }
    }

    private static char intToHex(int i) {
        return (i < 10) ? ((char)('0' + i)) : ((char)('A' + i - 10));
    }

    public static String enquote(String s) {
        return enquote(s, '\'');
    }
    
    public static String enquote(String s, char quoteChar) {
        StringBuilder buf = new StringBuilder();
        buf.append(quoteChar);
        for (int i = 0, len = s.length(); i < len; i++) {
            char c = s.charAt(i);
            switch (c) {
              case '\n': buf.append("\\n"); break;
              case '\r': buf.append("\\r"); break;
              case '\t': buf.append("\\t"); break;
              case '\\': buf.append("\\\\"); break;
              case '\'': case '"':
                         if (quoteChar == c)
                            buf.append('\\');
                         buf.append(c);
                         break;
              default:   buf.append(c); break;
            }
        }
        buf.append(quoteChar);
        return buf.toString();
    }

    private HtmlEncoder() {}
}
