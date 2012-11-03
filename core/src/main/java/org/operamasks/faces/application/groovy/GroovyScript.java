/*
 * $Id: GroovyScript.java,v 1.2 2007/12/10 06:55:33 jacky Exp $
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

package org.operamasks.faces.application.groovy;

import java.net.URL;
import java.io.File;
import groovy.lang.GroovyClassLoader;

final class GroovyScript
{
    private GroovyClassLoader loader;
    private Class<?> scriptClass;
    private String filename;

    private URL resource;
    private File scriptFile;
    private long lastModified = -1;

    GroovyScript(GroovyClassLoader loader, URL resource, String filename) {
        this.loader = loader;
        this.resource = resource;
        this.filename = filename;

        if (resource.getProtocol().equals("file")) {
            File scriptFile = new File(resource.getFile());
            if (scriptFile.exists()) {
                this.scriptFile = scriptFile;
                this.lastModified = scriptFile.lastModified();
            }
        }
    }

    public Class<?> getScriptClass() throws Exception {
        if (this.scriptClass == null || this.isModified()) {
            this.scriptClass = loadScriptClass();
        }

        return this.scriptClass;
    }

    public String getScriptName() {
        return this.filename;
    }
    
    public ClassLoader getClassLoader() {
        return this.loader;
    }

    protected Class<?> loadScriptClass() throws Exception {
        return loader.parseClass(resource.openStream(), filename);
    }

    protected boolean isModified() {
        if (this.scriptFile != null) {
            long timestamp = this.scriptFile.lastModified();
            if (timestamp != this.lastModified) {
                this.lastModified = timestamp;
                return true;
            }
        }
        return false;
    }
}
