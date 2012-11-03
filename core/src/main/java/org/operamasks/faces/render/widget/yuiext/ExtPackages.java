/*
 * $Id: ExtPackages.java,v 1.3 2007/07/02 07:37:50 jacky Exp $
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

package org.operamasks.faces.render.widget.yuiext;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.io.InputStream;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Parse and build package dependencies.
 */
class ExtPackages
{
    public static Map<String,String[]> load(InputStream stream)
        throws Exception
    {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setValidating(false);
        dbf.setNamespaceAware(false);

        DocumentBuilder builder = dbf.newDocumentBuilder();
        Document doc = builder.parse(stream);

        ExtPackages ext = new ExtPackages();
        ext.parse(doc);
        return ext.build();
    }

    private Map<String,Package> packages;

    private ExtPackages() {
        packages = new HashMap<String, Package>();
    }

    void parse(Document doc) {
        Element root = doc.getDocumentElement();
        NodeList nodeList = root.getElementsByTagName("package");
        for (int i = 0; i < nodeList.getLength(); i++) {
            Package pkg = parsePackage((Element)nodeList.item(i));
            packages.put(pkg.name, pkg);
        }
    }

    private Package parsePackage(Element el) {
        Package pkg = new Package();

        pkg.name = el.getAttribute("name");
        pkg.url = el.getAttribute("file");

        NodeList requiresList = el.getElementsByTagName("requires");
        pkg.requires = new String[requiresList.getLength()];
        for (int i = 0; i < pkg.requires.length; i++) {
            Element node = (Element)requiresList.item(i);
            pkg.requires[i] = node.getAttribute("name");
        }

        return pkg;
    }

    Map<String,String[]> build() {
        Map<String,String[]> result = new HashMap<String, String[]>();
        for (Package pkg : packages.values()) {
            List<String> urls = new ArrayList<String>();
            buildDepends(pkg, urls);
            result.put(pkg.name, urls.toArray(new String[urls.size()]));
        }
        return result;
    }

    private void buildDepends(Package pkg, List<String> urls) {
        for (String requires : pkg.requires) {
            Package requiredPkg = packages.get(requires);
            if (requiredPkg != null) {
                buildDepends(requiredPkg, urls);
            } else {
                throw new IllegalStateException("Unknow required package: " + requires);
            }
        }

        if (pkg.url != null && pkg.url.length() != 0) {
            if (!urls.contains(pkg.url)) {
                urls.add(pkg.url);
            }
        }
    }

    private static class Package {
        String name;
        String url;
        String[] requires;
    }
}
