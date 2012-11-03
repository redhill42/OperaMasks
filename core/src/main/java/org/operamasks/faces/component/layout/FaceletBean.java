/*
 * $Id: FaceletBean.java,v 1.4 2007/07/02 07:38:06 jacky Exp $
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

package org.operamasks.faces.component.layout;

import java.io.Serializable;

public class FaceletBean implements Serializable
{
    private static final long serialVersionUID = -9044140676269950366L;

    private String name;
    private Object value;
    private String srcClass;
    private String content;

    public FaceletBean() { }

    public FaceletBean(String name) {
        this.name = name;
    }

    public FaceletBean(String name, Object value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public String getSrcClass() {
        return srcClass;
    }

    public void setSrcClass(String srcClass) {
        this.srcClass = srcClass;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
