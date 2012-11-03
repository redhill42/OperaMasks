/*
 * $Id: FacesConfigLoader.java,v 1.11 2007/10/24 04:40:43 daniel Exp $
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

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.FactoryFinder;
import javax.faces.application.Application;
import javax.faces.application.ApplicationFactory;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.operamasks.faces.application.ApplicationAssociate;
import org.operamasks.faces.application.ApplicationEvent;
import org.operamasks.faces.application.ApplicationListener;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class FacesConfigLoader implements ServletContextListener
{
    public void loadFacesConfig(ServletContext context) {
        if (ApplicationAssociate.getInstance(context) != null) {
            return; // Faces config already loaded
        }

        WebXmlHandler handler = new WebXmlHandler();
        parseWebXml(context, handler);

        String[] facesMappings = handler.getFacesMappings();

        // load faces configurations only if a FacesServlet is mapped
        if (facesMappings != null) {
            BootstrapFacesContext facesctx = new BootstrapFacesContext(context);
            try {
                FacesConfig config = new FacesConfig(context);
                config.load();

                ApplicationAssociate associate = ApplicationAssociate.getInstance(context);
                associate.setFacesMappings(facesMappings);
                associate.setResourceMapping(handler.getResourceMapping());
            } catch (RuntimeException ex) {
                FactoryFinder.releaseFactories();
                throw ex;
            } finally {
                facesctx.release();
            }
        }
    }

    public void unloadFacesConfig(ServletContext context) {
        ApplicationAssociate associate = ApplicationAssociate.getInstance(context);
        if (associate == null) {
            return; // Faces config already unloaded
        }

        // Notify that the application is destroyed.
        ApplicationListener[] applicationListeners = associate.getApplicationListeners();
        if (applicationListeners != null) {
            try {
                ApplicationFactory factory = (ApplicationFactory)
                    FactoryFinder.getFactory(FactoryFinder.APPLICATION_FACTORY);
                Application application = factory.getApplication();
                ApplicationEvent event = new ApplicationEvent(application, context);
                for (ApplicationListener l : applicationListeners) {
                    try {
                        l.applicationDestroyed(event);
                    } catch (Throwable ex) {}
                }
            } catch (Throwable ex) {
                // silently ignore any exception
            }
        }

        FactoryFinder.releaseFactories();

        context.setAttribute(ApplicationAssociate.ASSOCIATE_KEY, null);
    }

    public void contextInitialized(ServletContextEvent event) {
        ServletContext context = event.getServletContext();
        loadFacesConfig(context);
    }

    public void contextDestroyed(ServletContextEvent event) {
        ServletContext context = event.getServletContext();
        unloadFacesConfig(context);
    }

    private static final String WEB_XML_ENTRY = "/WEB-INF/web.xml";
    private static final String FACES_SERVLET = "javax.faces.webapp.FacesServlet";
    private static final String RESOURCE_SERVLET = "org.operamasks.faces.render.resource.ResourceServlet";

    /*
     * Parse the web.xml for the current application and scan for
     * a FacesServlet entry, if present.
     */
    private void parseWebXml(ServletContext context, WebXmlHandler handler) {
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            factory.setValidating(false);
            factory.setNamespaceAware(false);

            SAXParser parser = factory.newSAXParser();
            parser.parse(context.getResourceAsStream(WEB_XML_ENTRY), handler);
        } catch (Exception ex) { /*ignored*/ }
    }

    // Find out all faces servlet mappings
    private static class WebXmlHandler extends DefaultHandler {

        private List<String> facesServletNames = new ArrayList<String>();
        private Map<String,String> servletMappings = new HashMap<String,String>();
        private String resourceServletName = null;

        private static final int INITIAL = 0;
        private static final int SERVLET = 1;
        private static final int SERVLET_MAPPING = 2;

        private int state = INITIAL;
        private String servletName;
        private String servletClass;
        private StringBuilder content = new StringBuilder();

        public String[] getFacesMappings() {
            if (facesServletNames.isEmpty()) {
                return null;
            }

            List<String> result = new ArrayList<String>();
            for (Map.Entry<String,String> e : servletMappings.entrySet()) {
                String pattern = e.getKey();
                String servletName = e.getValue();
                if (facesServletNames.contains(servletName)) {
                    if (pattern.equals("/") || pattern.equals("/*")) {
                        pattern = "/";
                    } else if (pattern.startsWith("/") && pattern.endsWith("/*")) {
                        pattern = pattern.substring(0, pattern.length()-2);
                    } else if (pattern.startsWith("/")) {
                        if (pattern.endsWith("/")) {
                            pattern = pattern.substring(0, pattern.length()-1);
                        }
                    } else if (pattern.startsWith("*.")) {
                        pattern = pattern.substring(1);
                    } else {
                        pattern = null;
                    }
                    if (pattern != null && !result.contains(pattern)) {
                        result.add(pattern);
                    }
                }
            }

            return result.toArray(new String[result.size()]);
        }

        public String getResourceMapping() {
            if (resourceServletName != null) {
                for (Map.Entry<String,String> e : servletMappings.entrySet()) {
                    String pattern = e.getKey();
                    String servletName = e.getValue();
                    if (resourceServletName.equals(servletName)) {
                        if (pattern.startsWith("/") && pattern.endsWith("/*")) {
                            return pattern;
                        }
                    }
                }
            }
            return null;
        }

        @Override
        public void startElement(String uri, String localeName,
                                 String qName, Attributes attributes)
            throws SAXException
        {
            content.setLength(0);

            switch (state) {
            case INITIAL:
                if (qName.equals("servlet")) {
                    state = SERVLET;
                } else if (qName.equals("servlet-mapping")) {
                    state = SERVLET_MAPPING;
                }
                break;

            case SERVLET:
            case SERVLET_MAPPING:
                break;

            default:
                state = INITIAL;
                break;
            }
        }

        @Override
        public void characters(char[] ch, int start, int length)
            throws SAXException
        {
            if (state != INITIAL) {
                content.append(ch, start, length);
            }
        }

        @Override
        public void endElement(String uri, String localeName, String qName)
            throws SAXException
        {
            switch (state) {
            case SERVLET:
                if (qName.equals("servlet-name")) {
                    servletName = content.toString().trim();
                } else if (qName.equals("servlet-class")) {
                    servletClass = content.toString().trim();
                } else if (qName.equals("servlet")) {
                    if (servletName != null && servletClass != null) {
                        if (FACES_SERVLET.equals(servletClass)) {
                            facesServletNames.add(servletName);
                        } else if (RESOURCE_SERVLET.equals(servletClass)) {
                            resourceServletName = servletName;
                        }
                    }
                    servletName = servletClass = null;
                    state = INITIAL;
                }
                break;

            case SERVLET_MAPPING:
                if (qName.equals("servlet-name")) {
                    servletName = content.toString().trim();
                } else if (qName.equals("url-pattern")) {
                    String pattern = content.toString().trim();
                    servletMappings.put(pattern, servletName);
                } else if (qName.equals("servlet-mapping")) {
                    servletName = null;
                    state = INITIAL;
                }
                break;
            }
        }

        @Override
        public InputSource resolveEntity(String publicId, String systemId)
            throws SAXException
        {
            return new InputSource(new StringReader(""));
        }
    }
}
