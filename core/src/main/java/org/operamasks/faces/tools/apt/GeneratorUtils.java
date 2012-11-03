/*
 * $Id: GeneratorUtils.java,v 1.1 2008/01/25 08:38:41 jacky Exp $
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

import java.io.File;

public class GeneratorUtils
{
    public final static String COMPONENT_CLASS_EXT = "Base"; 

    private final static String facesConfig = System.getProperty("facesconfig.file");
    private final static String faceletsConfig = System.getProperty("faceletsconfig.file");

    private GeneratorUtils() {}
    
    public static String capitalize(String name) {
        if (name == null || name.length() == 0) {
            return name;
        }
        return Character.toUpperCase(name.charAt(0)) + name.substring(1);
    }
    
    public static void initConfigFile() {
        if (facesConfig != null) {
            File configFile = new File(facesConfig);
            if (configFile.exists()) {
                configFile.delete();
            }
        }
        
        if (faceletsConfig != null) {
            File configFile = new File(faceletsConfig);
            if (configFile.exists()) {
                configFile.delete();
            }
        }

    }

    public static File getFacesConfigFile() {
        if (facesConfig == null) {
            return null;
        }
        return new File(facesConfig);
    }

    public static File getFaceletsConfigFile() {
        if (faceletsConfig == null) {
            return null;
        }
        return new File(faceletsConfig);
    }
}
