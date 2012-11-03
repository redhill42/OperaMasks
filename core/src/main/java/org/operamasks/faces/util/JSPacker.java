/*
 * $Id: JSPacker.java,v 1.4 2007/07/02 07:38:13 jacky Exp $
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

/*
 * JSMin.java 2006-02-13
 *
 * Copyright (c) 2006 John Reilly (www.inconspicuous.org)
 *
 * This work is a translation from C to Java of jsmin.c published by
 * Douglas Crockford.  Permission is hereby granted to use the Java
 * version under the same conditions as the jsmin.c on which it is
 * based.
 *
 *
 *
 *
 * jsmin.c 2003-04-21
 *
 * Copyright (c) 2002 Douglas Crockford (www.crockford.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * The Software shall be used for Good, not Evil.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.operamasks.faces.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PushbackInputStream;
import java.io.FileOutputStream;
import java.io.File;

public class JSPacker
{
    private static final int EOF = -1;

    private PushbackInputStream in;
    private OutputStream out;

    private int theA;
    private int theB;

    public JSPacker(InputStream in, OutputStream out) {
        this.in = new PushbackInputStream(in);
        this.out = out;
    }

    /**
     * isAlphanum -- return true if the character is a letter, digit,
     * underscore, dollar sign, or non-ASCII character.
     */
    private static boolean isAlphanum(int c) {
        return ((c >= 'a' && c <= 'z') ||
                (c >= '0' && c <= '9') ||
                (c >= 'A' && c <= 'Z') ||
                c == '_' ||
                c == '$' ||
                c == '\\' ||
                c > 126);
    }

    /**
     * get -- return the next character from stdin. Watch out for lookahead. If
     * the character is a control character, translate it to a space or
     * linefeed.
     */
    private int get() throws IOException {
        int c = in.read();

        if (c >= ' ' || c == '\n' || c == EOF) {
            return c;
        }

        if (c == '\r') {
            return '\n';
        }

        return ' ';
    }



    /**
     * Get the next character without getting it.
     */
    private int peek() throws IOException {
        int lookaheadChar = in.read();
        in.unread(lookaheadChar);
        return lookaheadChar;
    }

    /**
     * next -- get the next character, excluding comments. peek() is used to see
     * if a '/' is followed by a '/' or '*'.
     */
    private int next() throws IOException {
        int c = get();
        if (c == '/') {
            switch (peek()) {
            case '/':
                for (;;) {
                    c = get();
                    if (c <= '\n') {
                        return c;
                    }
                }

            case '*':
                get();
                for (;;) {
                    switch (get()) {
                    case '*':
                        if (peek() == '/') {
                            get();
                            return ' ';
                        }
                        break;
                    case EOF:
                        // unterminated comment
                        return EOF;
                    }
                }

            default:
                return c;
            }
        }
        return c;
    }

    /**
     * action -- do something! What you do is determined by the argument: 1
     * Output A. Copy B to A. Get the next B. 2 Copy B to A. Get the next B.
     * (Delete A). 3 Get the next B. (Delete B). action treats a string as a
     * single character. Wow! action recognizes a regular expression if it is
     * preceded by ( or , or =.
     */

    private void action(int d) throws IOException {
        switch (d) {
        case 1:
            out.write(theA);
        case 2:
            theA = theB;

            if (theA == '\'' || theA == '"') {
                for (;;) {
                    out.write(theA);
                    theA = get();
                    if (theA == theB) {
                        break;
                    } else if (theA <= '\n') {
                        // unterminated string literal
                        break;
                    } else if (theA == '\\') {
                        out.write(theA);
                        theA = get();
                    }
                }
            }

        case 3:
            theB = next();
            if (theB == '/' && (theA == '(' || theA == ',' || theA == '=')) {
                out.write(theA);
                out.write(theB);
                for (;;) {
                    theA = get();
                    if (theA == '/') {
                        break;
                    } else if (theA == '\\') {
                        out.write(theA);
                        theA = get();
                    } else if (theA <= '\n') {
                        // unterminated regular expression
                        out.write(theA);
                        break;
                    }
                    out.write(theA);
                }
                theB = next();
            }
        }
    }

    /**
     * pack -- Copy the input to the output, deleting the characters which are
     * insignificant to JavaScript. Comments will be removed. Tabs will be
     * replaced with spaces. Carriage returns will be replaced with linefeeds.
     * Most spaces and linefeeds will be removed.
     */
    public void pack() throws IOException {
        theA = '\n';
        action(3);
        while (theA != EOF) {
            switch (theA) {
            case ' ':
                if (isAlphanum(theB)) {
                    action(1);
                } else {
                    action(2);
                }
                break;

            case '\n':
                switch (theB) {
                case '{': case '[': case '(': case '+': case '-':
                    action(1);
                    break;
                case ' ':
                    action(3);
                    break;
                default:
                    if (isAlphanum(theB)) {
                        action(1);
                    } else {
                        action(2);
                    }
                }
                break;

            default:
                switch (theB) {
                case ' ':
                    if (isAlphanum(theA)) {
                        action(1);
                        break;
                    }
                    action(3);
                    break;
                case '\n':
                    switch (theA) {
                    case '}': case ']': case ')': case '+': case '-': case '"': case '\'':
                        action(1);
                        break;
                    default:
                        if (isAlphanum(theA)) {
                            action(1);
                        } else {
                            action(3);
                        }
                    }
                    break;
                default:
                    action(1);
                    break;
                }
            }
        }
        out.flush();
    }

    // ------------------------------------------------------------------------

    public static void main(String args[]) {
        try {
            if (args.length == 0) {
                packFile(System.in, System.out);
            } else if (args.length == 1) {
                packFile(new FileInputStream(args[0]), System.out);
            } else {
                File inFile = new File(args[0]);
                File outFile = new File(args[1]);
                if (inFile.isDirectory()) {
                    packDirectory(inFile, outFile);
                } else {
                    packFile(new FileInputStream(args[0]), new FileOutputStream(args[1]));
                }
            }
        } catch (FileNotFoundException e) {
            System.err.println(e.getMessage() + ": not found.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void packFile(InputStream in, OutputStream out)
        throws IOException
    {
        JSPacker packer = new JSPacker(in, out);
        packer.pack();
        in.close();
        out.close();
    }

    private static void packDirectory(File srcDir, File dstDir)
        throws IOException
    {
        String[] filenames = srcDir.list();
        if (filenames == null) {
            return;
        }
        for (String filename : filenames) {
            File srcFile = new File(srcDir, filename);
            File dstFile = new File(dstDir, filename);
            if (srcFile.isFile() && filename.endsWith(".js")) {
                if (!dstDir.exists())
                    dstDir.mkdirs();
                packFile(new FileInputStream(srcFile), new FileOutputStream(dstFile));
            } else if (srcFile.isDirectory()) {
                packDirectory(srcFile, dstFile);
            }
        }
    }
}
