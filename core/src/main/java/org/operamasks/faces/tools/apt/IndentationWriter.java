/*
 * $Id: IndentationWriter.java,v 1.1 2008/01/25 08:38:41 jacky Exp $
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

package org.operamasks.faces.tools.apt;

import java.io.Writer;
import java.io.PrintWriter;

/**
 * IndentationWriter is a BufferedWriter subclass that supports automatic
 * indentation of lines of text written to the underlying Writer.
 */
public class IndentationWriter extends PrintWriter
{
    /** number of spaces to change indent when indenting in or out */
    private int indentStep = 4;

    /** number of spaces to convert into tabs. */
    private int tabSize = 8;

    /** current number of spaces to prepend to lines */
    private int indent = 0;

    /** true if the next character written is the first on a line */
    private boolean beginingOfLine = true;

    /** The current line number */
    private int lineNumber = 1;

    /**
     * Create a new IndentationWriter that writes indent text to the
     * given Writer.  Use the default indent step of four spaces.
     */
    public IndentationWriter(Writer out) {
	super(out);
    }

    /**
     * Create a new IndentationWriter that writes indented text to the
     * given Writer and uses the supplied indent step.
     */
    public IndentationWriter(Writer out, int step) {
	this(out);

	if (step < 0)
	    throw new IllegalArgumentException("negative indent step");

	indentStep = step;
    }

    /**
     * Create a new IndentationWriter that writes indented text to the
     * given Writer and uses the supplied indent step and tab size.
     */
    public IndentationWriter(Writer out, int step, int tabSize) {
	this(out);

	if (step < 0)
	    throw new IllegalArgumentException("negation indent step");

	indentStep = step;
	this.tabSize = tabSize;
    }

    /**
     * Write a single character.
     */
    public void write(int c) {
	if (c == '\n')
	    lineNumber++;
	checkWrite();
	super.write(c);
    }

    /**
     * Write a portion of an array of characters.
     */
    public void write(char[] cbuf, int off, int len) {
	if (len > 0) {
	    checkWrite();
	}
	for (int i = 0; i < len; i++) {
	    if (cbuf[off+i] == '\n')
		lineNumber++;
	}
	super.write(cbuf, off, len);
    }

    /**
     * Write a portion of a String.
     */
    public void write(String s, int off, int len) {
	if (len > 0) {
	    checkWrite();
	}
	for (int i = 0; i < len; i++) {
	    if (s.charAt(off+i) == '\n')
		lineNumber++;
	}
	super.write(s, off, len);
    }

    /**
     * Write a line separator.  The next character written will be
     * preceded by an indent.
     */
    public void println() {
	super.println();
	beginingOfLine = true;
    }

    /**
     * Get the current line number.
     */
    public int getLineNumber() {
	return lineNumber;
    }

    /**
     * Check if an indent needs to be written before writing the next
     * character.
     */
    protected void checkWrite() {
	if (beginingOfLine) {
	    beginingOfLine = false;
	    int i = indent;
	    while (i >= tabSize) {
		super.write('\t');
		i -= tabSize;
	    }
	    while (i > 0) {
		super.write(' ');
		--i;
	    }
	}
    }

    /**
     * Increase the current indent by the indent step.
     */
    protected void indentPlus() {
	indent += indentStep;
    }

    /**
     * Decrease the current indent by the indent step.
     */
    protected void indentMinus() {
	indent -= indentStep;
	if (indent < 0)
	    indent = 0;
    }

    /**
     * Indent in.
     */
    public void I() {
	indentPlus();
    }

    /**
     * Indent out.
     */
    public void O() {
	indentMinus();
    }

    /**
     * Write string.
     */
    public void p(String s) {
        write(s);
    }

    /**
     * Write a string with format arguments.
     */
    public void p(String s, Object... args) {
        write(String.format(s, args));
    }

    /**
     * End current line.
     */
    public void pln() {
	println();
    }

    /**
     * Write string; end current line.
     */
    public void pln(String s) {
	p(s);
	pln();
    }

    /**
     * Write string with format arguments; end current line.
     */
    public void pln(String s, Object... args) {
        p(String.format(s, args));
        pln();
    }

    /**
     * Write string; end current line; indent in.
     */
    public void plnI(String s) {
	p(s);
	pln();
	I();
    }

    /**
     * Write string with format arguments; end current line; indent in.
     */
    public void plnI(String s, Object... args) {
	p(String.format(s, args));
	pln();
	I();
    }

    /**
     * Indent out; write string.
     */
    public void Op(String s) {
	O();
	p(s);
    }

    /**
     * Indent out; write string with format arguments.
     */
    public void Op(String s, Object... args) {
	O();
	p(String.format(s, args));
    }

    /**
     * Indent out; write string; end current line.
     */
    public void Opln(String s) {
	O();
	pln(s);
    }

    /**
     * Indent out; write string with arguments; end current line.
     */
    public void Opln(String s, Object... args) {
	O();
	pln(String.format(s, args));
    }

    /**
     * Indent out; write string; end current line; indent in.
     *
     * This method is useful for generating lines of code that both
     * end and begin nested blocks, like "} else {".
     */
    public void OplnI(String s) {
	O();
	pln(s);
	I();
    }

    /**
     * Indent out; write string with arguments; end current line; indent in.
     *
     * This method is useful for generating lines of code that both
     * end and begin nested blocks, like "} else {".
     */
    public void OplnI(String s, Object... args) {
	O();
	pln(String.format(s, args));
	I();
    }
}
