/*
 * $Id 
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
package org.operamasks.faces.tools.generate;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import org.operamasks.faces.tools.apt.ComponentAnnotationProcessorFactory;

public abstract class AbstractGenerator {
    protected final String generatorName = ComponentAnnotationProcessorFactory.class.getName();
    protected final Date generateDate = new Date();

    // Format for ISO 8601 date -- "1994-10-06T08:49:37Z"
    protected static final String ISO8601 = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    protected static final DateFormat iso8601Format;

    static {
        iso8601Format = new SimpleDateFormat(ISO8601, Locale.US);
        iso8601Format.setTimeZone(TimeZone.getDefault());
    }
    
    public abstract void generate();
}
