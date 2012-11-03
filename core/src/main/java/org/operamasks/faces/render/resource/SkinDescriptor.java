/*
 * $Id: SkinDescriptor.java,v 1.3 2007/07/02 07:38:03 jacky Exp $
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

package org.operamasks.faces.render.resource;

import java.util.Map;
import java.util.HashMap;
import java.util.Collections;

public class SkinDescriptor
{
    private String name;
    private String location;
    private String base;

    private Map<String,String> properties = new HashMap<String,String>();

    SkinDescriptor(String name, String location, String base) {
        this.name = name;
        this.location = location;
        this.base = base;
    }

    public String getName() {
        return name;
    }

    public String getLocation() {
        return location;
    }

    public String getBase() {
        return base;
    }

    public String getProperty(String key) {
        return properties.get(key);
    }

    /* package private */
    void setProperty(String key, String value) {
        properties.put(key, value);
    }

    public Map<String,String> getProperties() {
        return Collections.unmodifiableMap(properties);
    }

    public String getDisplayName() {
        return getProperty("displayName");
    }

    public String getDescription() {
        return getProperty("description");
    }

    public String getAuthorName() {
        return getProperty("author.name");
    }

    public String getAuthorEmail() {
        return getProperty("author.email");
    }
}
