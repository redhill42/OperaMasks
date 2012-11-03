/*
 * $Id: SkinManager.java,v 1.9 2007/12/11 04:20:12 jacky Exp $
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

import javax.faces.context.FacesContext;
import javax.faces.context.ExternalContext;
import javax.faces.component.UIComponent;
import java.util.Map;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Properties;
import java.util.LinkedHashMap;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.net.URL;
import java.io.InputStream;

import org.operamasks.faces.application.ApplicationAssociate;
import org.operamasks.faces.util.FacesUtils;

public class SkinManager
{
    public static final String SKIN_PARAM = "org.operamasks.faces.SKIN";
    public static final String DEFAULT_SKIN = "default";

    private static final String SKIN_PROPERTY_ENTRY = "META-INF/skin.properties";

    public static SkinManager getInstance(FacesContext context) {
        return ApplicationAssociate.getInstance(context).getSingleton(SkinManager.class);
    }

    private Map<String,SkinDescriptor> skins;
    private String defaultSkin;

    private SkinManager() {
        skins = Collections.unmodifiableMap(loadSkins());

        // Set application default skin
        ExternalContext extctx = FacesContext.getCurrentInstance().getExternalContext();
        String skin = extctx.getInitParameter(SKIN_PARAM);
        if (skin == null || skin.length() == 0)
            skin = DEFAULT_SKIN; // the "default" skin should always available
        this.defaultSkin = skin;
    }
    
    public String getDefaultSkin() {
        return defaultSkin;
    }

    public void setDefaultSkin(String skin) {
        if (skin == null || skin.length() == 0)
            throw new IllegalArgumentException();
        defaultSkin = skin;
    }

    public Map<String,SkinDescriptor> getSkins() {
        return skins;
    }

    public SkinDescriptor getSkin(String name) {
        return skins.get(name);
    }

    public URL getSkinResource(String skin, String path, String locale) {
        ClassLoader loader = getClassLoader();

        if (path.startsWith("/")) {
            path = path.substring(1);
        }

        String prefix, suffix;
        if (locale != null && locale.length() != 0) {
            int dot = path.indexOf('.');
            if (dot == -1) {
                prefix = path;
                suffix = "";
            } else {
                prefix = path.substring(0, dot);
                suffix = path.substring(dot);
            }
        } else {
            prefix = suffix = locale = null;
        }

        while (skin != null) {
            SkinDescriptor skinDesc = skins.get(skin);
            if (skinDesc == null) {
                // fallback to default skin for non-existing skin
                if (!DEFAULT_SKIN.equals(skin)) {
                    if ((skinDesc = skins.get(skin = DEFAULT_SKIN)) == null)
                        break;
                } else {
                    break;
                }
            }

            String location = skinDesc.getLocation();
            assert !location.startsWith("/") && location.endsWith("/");

            // Handle localized resource
            String localPath = (locale == null)
                    ? (location + path)
                    : (location + prefix + "_" + locale + suffix);
            String currentLocale = locale;

            do {
                URL resource = loader.getResource(localPath);
                if (resource != null) {
                    return resource;
                }

                if (currentLocale != null) {
                    int sep = currentLocale.lastIndexOf("_");
                    if (sep != -1) {
                        currentLocale = currentLocale.substring(0, sep);
                        localPath = location + prefix + "_" + currentLocale + suffix;
                    } else {
                        currentLocale = null;
                        localPath = location + path;
                    }
                } else {
                    break;
                }
            } while (true);

            if (skinDesc.getBase() != null) {
                // fallback to base skin
                skin = skinDesc.getBase();
            } else if (!skin.equals(DEFAULT_SKIN)){
                // fallback to system default skin, which should always available
                skin = DEFAULT_SKIN;
            } else {
                break;
            }
        }
        return null;
    }

    public static String getCurrentSkin(FacesContext context) {
        String skin;

        // choose skin from HtmlPage component
        UIComponent page = FacesUtils.getHtmlPage(context.getViewRoot());
        if (page != null) {
            skin = (String)page.getAttributes().get("skin");
            if (skin != null && skin.length() != 0)
                return skin;
        }

        // choose skin from session
        ExternalContext extctx = context.getExternalContext();
        if (extctx.getSession(false) != null) {
            skin = (String)extctx.getSessionMap().get(SKIN_PARAM);
            if (skin != null && skin.length() != 0)
                return skin;
        }

        // chose default skin from global configuration
        return SkinManager.getInstance(context).getDefaultSkin();
    }

    public static void setCurrentSkin(FacesContext context, String skin) {
        if (skin == null)
            throw new NullPointerException();
        if (skin.length() == 0)
            throw new IllegalArgumentException("Empty skin name");

        // set skin into session
        ExternalContext ectx = context.getExternalContext();
        ectx.getSessionMap().put(SKIN_PARAM, skin);
    }
    
    private static Map<String,SkinDescriptor> loadSkins() {
        Map<String,SkinDescriptor> skins = new LinkedHashMap<String,SkinDescriptor>();

        try {
            ClassLoader loader = getClassLoader();
            Enumeration<URL> resources = loader.getResources(SKIN_PROPERTY_ENTRY);
            Pattern namePattern = Pattern.compile("skin\\.(\\S+)\\.location");

            while (resources.hasMoreElements()) {
                Properties p = new Properties();

                URL url = resources.nextElement();
                InputStream stream = url.openStream();
                p.load(stream);
                stream.close();

                for (Object key : p.keySet()) {
                    Matcher matcher = namePattern.matcher(key.toString());
                    if (matcher.matches()) {
                        String name = matcher.group(1);
                        String location = (String)p.get(key);
                        String prefix = "skin." + name + ".";
                        String base = p.getProperty(prefix + "base");

                        if (!location.endsWith("/"))
                            location += "/";
                        if (location.startsWith("/"))
                            location = location.substring(1);
                        
                        SkinDescriptor skin = new SkinDescriptor(name, location, base);
                        skins.put(name, skin);

                        // set other properties
                        for (Object key2 : p.keySet()) {
                            String skey = key2.toString();
                            if (skey.startsWith(prefix)) {
                                skin.setProperty(skey.substring(prefix.length()),
                                                 p.getProperty(skey));
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) {}

        return skins;
    }

    private static ClassLoader getClassLoader() {
        ApplicationAssociate associate = ApplicationAssociate.getInstance();
        if (associate != null) {
            return associate.getClassLoader();
        }

        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        if (loader == null)
            loader = ClassLoader.getSystemClassLoader();
        return loader;
    }
}
