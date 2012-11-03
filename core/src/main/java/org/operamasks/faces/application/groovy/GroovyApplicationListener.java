/*
 * $Id: GroovyApplicationListener.java,v 1.5 2007/10/26 16:30:11 daniel Exp $
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

import java.util.Set;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.net.URL;
import java.net.MalformedURLException;

import javax.servlet.ServletContext;
import groovy.lang.GroovyClassLoader;

import org.operamasks.faces.application.ApplicationListener;
import org.operamasks.faces.application.ApplicationEvent;
import org.operamasks.faces.application.ApplicationAssociate;
import org.operamasks.faces.application.ManagedBeanContainer;
import org.operamasks.faces.application.ValidatorRegistry;
import org.operamasks.faces.application.ConverterRegistry;
import org.operamasks.faces.application.ValidatorFactory;
import org.operamasks.faces.application.ConverterFactory;
import org.operamasks.faces.binding.ModelBindingFactory;
import org.operamasks.faces.config.AnnotationProcessor;
import org.operamasks.faces.config.ManagedBeanConfig;
import org.operamasks.faces.config.ValidatorConfig;
import org.operamasks.faces.config.ConverterConfig;
import static org.operamasks.resources.Resources.*;

public class GroovyApplicationListener implements ApplicationListener
{
    private static final String BASE_PATH = "/WEB-INF/groovy/";
    private static final String SUFFIX = ".groovy";

    private static Logger log = Logger.getLogger(GroovyApplicationListener.class.getName());

    private GroovyClassLoader groovyClassLoader;
    private AnnotationProcessor annotationProcessor;

    public void applicationCreated(ApplicationEvent event) {
        // do nothing
    }

    public void applicationDestroyed(ApplicationEvent event) {
        // do nothing
    }

    public void applicationInitialized(ApplicationEvent event) {
        ServletContext context = event.getServletContext();
        ApplicationAssociate assoc = ApplicationAssociate.getInstance(context);
        ClassLoader loader = assoc.getClassLoader();

        try {
            this.groovyClassLoader = new GroovyClassLoader(loader);
        } catch (NoClassDefFoundError ex) {
            return; // groovy jar not in path, so return
        }

        try {
            URL url = context.getResource(BASE_PATH);
            if (url != null) {
                // Add "/WEB-INF/groovy" to the classpath of groovy class loader
                this.groovyClassLoader.addURL(url);

                // scan for groovy scripts in "/WEB-INF/groovy"
                this.annotationProcessor = new AnnotationProcessor(context, loader);
                scan(context, BASE_PATH);
            }
        } catch (MalformedURLException ex) {
            throw new InternalError();
        }

        // register groovy property EL resolver to resolve dynamic properties
        event.getApplication().addELResolver(new GroovyELResolver());

        // set groovy model bean creator to resolve dynamic methods
        ModelBindingFactory mbf = ModelBindingFactory.instance();
        mbf.setModelBeanCreator(new GroovyBeanCreator(mbf.getModelBeanCreator()));
    }

    private void scan(ServletContext context, String path) {
        @SuppressWarnings("unchecked")
        Set<String> paths = context.getResourcePaths(path);

        if (paths != null) {
            for (String file : paths) {
                if (file.endsWith("/")) {
                    scan(context, file);
                } else if (file.endsWith(SUFFIX)) {
                    parseGroovyScript(context, file);
                }
            }
        }
    }

    private void parseGroovyScript(ServletContext context, String filename) {
        URL resource;
        try {
            resource = context.getResource(filename);
            if (resource == null) {
                return;
            }
        } catch (MalformedURLException ex) {
            throw new InternalError(); // should not happen
        }

        try {
            GroovyScript script = new GroovyScript(groovyClassLoader, resource, filename);
            Class<?> scriptClass = script.getScriptClass();

            ManagedBeanConfig mbean = annotationProcessor.scanManagedBeanClass(scriptClass);
            if (mbean != null) {
                GroovyManagedBeanFactory factory = new GroovyManagedBeanFactory(mbean, script);
                ManagedBeanContainer.getInstance().addBeanFactory(factory);
            }

            ConverterConfig converter = annotationProcessor.scanConverterClass(scriptClass);
            if (converter != null) {
                ConverterRegistry registry = ConverterRegistry.getInstance();
                ConverterFactory factory = registry.createConverterFactory(new GroovyTargetCreator(script));

                String converterId = converter.getConverterId();
                if (converterId != null) {
                    registry.addConverterFactory(converterId, factory);
                }

                String converterType = converter.getConverterForClass();
                if (converterType != null) {
                    try {
                        Class c = groovyClassLoader.loadClass(converterType);
                        registry.addConverterFactory(c, factory);
                    } catch (ClassNotFoundException ex) {
                        log.severe(_T(JSF_CLASS_NOT_FOUND, converterType));
                    }
                }
            }

            ValidatorConfig validator = annotationProcessor.scanValidatorClass(scriptClass);
            if (validator != null) {
                ValidatorRegistry registry = ValidatorRegistry.getInstance();
                ValidatorFactory factory = registry.createValidatorFactory(new GroovyTargetCreator(script));
                registry.addValidatorFactory(validator.getValidatorId(), factory);
            }
        } catch (Exception ex) {
            log.log(Level.SEVERE, "Error occurred while parsing groovy script '" + filename + "'.", ex);
        }
    }
}
