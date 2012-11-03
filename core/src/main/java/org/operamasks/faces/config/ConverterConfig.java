/*
 * $Id: ConverterConfig.java,v 1.2 2007/10/22 18:39:38 daniel Exp $
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

package org.operamasks.faces.config;

public class ConverterConfig
{
    private String converterId;
    private String converterForClass;
    private String converterClass;

    public String getConverterId() {
        return this.converterId;
    }

    public void setConverterId(String converterId) {
        this.converterId = converterId;
    }

    public String getConverterForClass() {
        return this.converterForClass;
    }

    public void setConverterForClass(String converterForClass) {
        this.converterForClass = converterForClass;
    }

    public String getConverterClass() {
        return this.converterClass;
    }

    public void setConverterClass(String converterClass) {
        this.converterClass = converterClass;
    }
}
