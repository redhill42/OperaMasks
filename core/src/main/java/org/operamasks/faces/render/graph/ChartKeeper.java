/*
 * $Id: ChartKeeper.java,v 1.5 2007/12/13 18:10:34 jacky Exp $
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

package org.operamasks.faces.render.graph;

import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletContextEvent;
import java.util.Map;
import java.io.File;
import java.io.FileInputStream;
import java.io.DataInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.AccessController;
import java.security.PrivilegedAction;

public class ChartKeeper implements ServletContextListener
{
    public static final String CHART_KEEPER_KEY = "org.operamasks.faces.ChartKeeper";

    public static ChartKeeper getInstance(FacesContext context) {
        Map<String,Object> appMap = context.getExternalContext().getApplicationMap();
        ChartKeeper keeper = (ChartKeeper)appMap.get(CHART_KEEPER_KEY);
        if (keeper == null) {
            // for facelets, do not read listener class from tld, init it manual
            keeper = new ChartKeeper();
            keeper.init((File)appMap.get("javax.servlet.context.tempdir"));
        }
        return keeper;
    }

    private File tmpdir;
    private long lastCleanTime = 0;

    private static final int MAX_KEEP_TIME = 300; // keep images for 5 minutes

    public void contextInitialized(ServletContextEvent event) {
        final ServletContext context = event.getServletContext();
        context.setAttribute(CHART_KEEPER_KEY, this);
        init((File)context.getAttribute("javax.servlet.context.tempdir"));
    }

    private void init(final File defaultDir) {
        this.tmpdir = AccessController.doPrivileged(new PrivilegedAction<File>() {
            public File run() {
                File dir = defaultDir;
                if (dir == null) {
                    dir = new File(System.getProperty("java.io.tmpdir"));
                }

                dir = new File(dir, "jsf-chart-images");
                dir.mkdirs();
                return dir;
            }
        });
    }

    public void contextDestroyed(ServletContextEvent event) {
        cleanAll();
        event.getServletContext().removeAttribute(CHART_KEEPER_KEY);
    }

    public String save(byte[] data, String suffix)
        throws IOException
    {
        doSweep();

        File file = File.createTempFile("CHRT", suffix, tmpdir);
        FileOutputStream out = new FileOutputStream(file);
        out.write(data);
        out.close();

        return file.getName();
    }

    public byte[] retrieve(String filename) {
        File file = new File(tmpdir, filename);
        if (!file.exists() || !file.isFile()) {
            return null;
        }

        try {
            DataInputStream in = new DataInputStream(new FileInputStream(file));
            byte[] data = new byte[(int)file.length()];
            in.readFully(data);
            in.close();
            return data;
        } catch (IOException ex) {
            return null;
        }
    }

    private void doSweep() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastCleanTime > (long)MAX_KEEP_TIME*1000) {
            lastCleanTime = currentTime;
            try {
                File[] files = tmpdir.listFiles();
                for (File file : files) {
                    if (currentTime - file.lastModified() > (long)MAX_KEEP_TIME*1000) {
                        file.delete();
                    }
                }
            } catch (Throwable ex) {/*ignored*/}
        }
    }

    private void cleanAll() {
        try {
            File[] files = tmpdir.listFiles();
            for (File file : files) {
                file.delete();
            }
        } catch (Throwable ex) {/*ignored*/}
    }
}
