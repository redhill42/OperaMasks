/*
 * $Id: FacesConfig.java,v 1.42 2008/03/24 05:21:49 patrick Exp $
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

import static org.operamasks.resources.Resources.JSF_CLASS_NOT_FOUND;
import static org.operamasks.resources.Resources.JSF_DUPPLICATE_MANAGED_BEAN;
import static org.operamasks.resources.Resources.JSF_INSTANTIATION_ERROR;
import static org.operamasks.resources.Resources.JSF_LOAD_FACES_CONFIG;
import static org.operamasks.resources.Resources.JSF_NO_SUCH_RENDER_KIT_ID;
import static org.operamasks.resources.Resources.JSF_UNEXPECTED_CLASS;
import static org.operamasks.resources.Resources._T;
import static org.operamasks.util.Utils.findClass;

import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Constructor;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.el.ELResolver;
import javax.faces.FacesException;
import javax.faces.FactoryFinder;
import javax.faces.application.Application;
import javax.faces.application.ApplicationFactory;
import javax.faces.application.NavigationHandler;
import javax.faces.application.StateManager;
import javax.faces.application.ViewHandler;
import javax.faces.el.PropertyResolver;
import javax.faces.el.VariableResolver;
import javax.faces.event.ActionListener;
import javax.faces.event.PhaseListener;
import javax.faces.lifecycle.Lifecycle;
import javax.faces.lifecycle.LifecycleFactory;
import javax.faces.render.RenderKit;
import javax.faces.render.RenderKitFactory;
import javax.faces.render.Renderer;
import javax.faces.webapp.FacesServlet;
import javax.servlet.ServletContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.operamasks.faces.application.ApplicationAssociate;
import org.operamasks.faces.application.ApplicationEvent;
import org.operamasks.faces.application.ApplicationListener;
import org.operamasks.faces.application.ELResolverRegistry;
import org.operamasks.faces.application.ManagedBeanContainer;
import org.operamasks.faces.application.ViewMapper;
import org.operamasks.faces.application.impl.DefaultNavigationHandler;
import org.operamasks.faces.application.impl.DefaultViewMapper;
import org.operamasks.faces.binding.ModelBindingFactory;
import org.operamasks.faces.component.ComponentConfig;
import org.operamasks.faces.component.ComponentContainer;
import org.operamasks.faces.component.ComponentFactory;
import org.operamasks.faces.component.MetaComponentFactory;
import org.operamasks.faces.debug.Debug;
import org.operamasks.faces.debug.DebugMode;
import org.operamasks.faces.lifecycle.PhaseListenerAdapter;
import org.operamasks.faces.util.FacesUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

class FacesConfig
{
    static final Logger log = Logger.getLogger("org.operamasks.faces.config");
    static final Logger lifecycleLog = Logger.getLogger("org.operamasks.faces.lifecycle");

    private ServletContext context;

    // factory config
    private List<String>          applicationFactories = newList();
    private List<String>          facesContextFactories = newList();
    private List<String>          lifecycleFactories = newList();
    private List<String>          renderKitFactories = newList();

    // application config
    private List<String>          actionListeners = newList();
    private String                defaultRenderKitId;
    private String                messageBundle;
    private List<String>          navigationHandlers = newList();
    private List<String>          viewHandlers = newList();
    private List<String>          viewMappers = newList();
    private List<String>          stateManagers = newList();
    private List<String>          elResolvers = newList();
    private List<String>          propertyResolvers = newList();
    private List<String>          variableResolvers = newList();
    private Locale                defaultLocale;
    private List<Locale>          supportedLocales = newList();
    private List<ResourceBundleConfig> resourceBundles = newList();

    private List<ComponentConfig>   components = newList();
    private List<ComponentConfig>   metaComponents = newList();
    private List<ConverterConfig>   converters = newList();
    private List<ValidatorConfig>   validators = newList();
    private List<ManagedBeanConfig> managedBeans = newList();
    private List<NavigationCase>    navigationCases = newList();
    private List<RenderKitConfig>   renderKits = newList();
    private List<String>            phaseListeners = newList();
    private List<ViewMappingConfig> viewMappings = newList();
    
    private List<String> metaDataJars = newList();
    private List<String> metaDataPackages = newList();

    private static final String FACELET_VIEW_HANDLER
        = "com.sun.facelets.FaceletViewHandler";
    private static final String FACELET_VIEW_HANDLER_HOOK
        = "org.operamasks.faces.facelets.FaceletViewHandlerHook";

    private class RenderKitConfig {
        public String renderKitId;
        public String renderKitClass;
        public List<RendererConfig> renderers = newList();
    }

    private static class RendererConfig {
        public String componentFamily;
        public String rendererType;
        public String rendererClass;
    }

    private static class ResourceBundleConfig {
        public String var;
        public String baseName;
        public String displayName;
    }

    private static class ViewMappingConfig {
        public List<String> viewIds = newList();
        public List<String> beanNames = newList();
    }

    FacesConfig(ServletContext context) {
        this.context = context;
    }

    void load() {
        loadDefaultConfig();
        loadFromMetaInf();
        loadContextSpecified();
        loadFromWebInf();
        loadFromMetaData();

        applyConfig();
    }

    private static String[] defaultConfigs = {
        "faces-config.xml",
        "ajax-config.xml",
        "widget-config.xml",
        "layout-config.xml",
        "graph-config.xml",
        "form-config.xml"
    };

    private void loadDefaultConfig() {
        // load default faces config from current package
        for (String config : defaultConfigs) {
            URL resource = FacesConfigLoader.class.getResource(config);
            if (resource != null) {
                parse(resource);
            }
        }
    }

    private void loadFromMetaInf() {
        // Search for all resources named "META-INF/faces-config.xml" in the
        // ServletContext resource paths for the current web application,
        // and load each as a JSF configuration resource (in reverse order of
        // the order in which they are returned by getResources() on the
        //  current Thread's ContextClassLoader).
        try {
            ClassLoader loader = getClassLoader();
            Enumeration<URL> resources = loader.getResources("META-INF/faces-config.xml");
            while (resources.hasMoreElements()) {
                URL resource = resources.nextElement();
                log.info(_T(JSF_LOAD_FACES_CONFIG, resource.getFile()));
                parse(resource);
            }
        } catch (IOException ex) {
            throw new FacesException(ex);
        }
    }

    private void loadContextSpecified() {
        // Check for the existence of a context initialization parameter named
        // javax.faces.CONFIG_FILES. If it exists, treat it as a comma-delimited
        // list of context relative resource paths (starting with a "/"), and
        // load each of the specified resources.
        String parameter = context.getInitParameter(FacesServlet.CONFIG_FILES_ATTR);
        if (parameter == null)
            return;

        for (String path : parameter.split(",")) {
            try {
                URL resource = context.getResource(path.trim());
                log.info(_T(JSF_LOAD_FACES_CONFIG, resource.getFile()));
                parse(resource);
            } catch (MalformedURLException ex) {
                throw new FacesException(ex);
            }
        }
    }

    private void loadFromWebInf() {
        try {
            URL resource = context.getResource("/WEB-INF/faces-config.xml");
            if (resource != null) {
                log.info(_T(JSF_LOAD_FACES_CONFIG, resource.getFile()));
                parse(resource);
            }
        } catch (MalformedURLException ex) {
            throw new FacesException(ex);
        }

        try {
            URL resource = context.getResource("/WEB-INF/operamasks.xml");
            if (resource != null) {
                log.info(_T(JSF_LOAD_FACES_CONFIG, resource.getFile()));
                parseExtension(resource);
            }
        } catch (MalformedURLException ex) {
            throw new FacesException(ex);
        }
    }

    private void loadFromMetaData() {
        AnnotationProcessor processor = new AnnotationProcessor(context, getClassLoader());

        // Scan annotations for configured managed beans.
        for (ManagedBeanConfig mbean : this.managedBeans) {
            processor.scanManagedBean(mbean);
        }

        // Scan all classes in the classpath and jar lib to find annotated
        // managed beans, converters, and validators.
        processor.setMetaDataJars(metaDataJars);
        if (metaDataPackages.size() == 0) {
        	metaDataPackages.add("*");
        }
        
        processor.setMetaDataPackages(metaDataPackages);
        processor.scan();

        for (ManagedBeanConfig bean : processor.getManagedBeans()) {
            addManagedBean(bean);
        }

        for (ComponentConfig component : processor.getComponents()) {
            metaComponents.add(component);
        }

        this.converters.addAll(processor.getConverters());
        this.validators.addAll(processor.getValidators());
        this.phaseListeners.addAll(processor.getPhaseListeners());
    }

    private Document parseResource(URL resource) {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setValidating(false); // FIXME
            dbf.setNamespaceAware(false);

            DocumentBuilder db = dbf.newDocumentBuilder();
            db.setEntityResolver(new EntityResolver() {
                public InputSource resolveEntity(String publicId, String systemId)
                    throws SAXException, IOException
                {
                    return new InputSource(new StringReader(""));
                }});

            return db.parse(resource.toString());
        } catch (Exception ex) {
            throw new FacesException(ex);
        }
    }

    private void parse(URL resource) {
        Document doc = parseResource(resource);
        Node root = doc.getDocumentElement();
        for (Node kid = root.getFirstChild(); kid != null; kid = kid.getNextSibling()) {
            if (kid.getNodeType() == Node.ELEMENT_NODE) {
                String tag = kid.getNodeName();
                if (tag.equals("application")) {
                    parseApplication(kid);
                } else if (tag.equals("factory")) {
                    parseFactory(kid);
                } else if (tag.equals("component")) {
                    parseComponent(kid);
                } else if (tag.equals("converter")) {
                    parseConverter(kid);
                } else if (tag.equals("managed-bean")) {
                    parseManagedBean(kid);
                } else if (tag.equals("navigation-rule")) {
                    parseNavigationRule(kid);
                } else if (tag.equals("referenced-bean")) {
                    // used by design time tools, not used at runtime
                } else if (tag.equals("render-kit")) {
                    parseRenderKit(kid);
                } else if (tag.equals("lifecycle")) {
                    parseLifecycle(kid);
                } else if (tag.equals("validator")) {
                    parseValidator(kid);
                }
            }
        }
    }

    private void parseExtension(URL resource) {
        Document doc = parseResource(resource);
        Node root = doc.getDocumentElement();
        for (Node kid = root.getFirstChild(); kid != null; kid = kid.getNextSibling()) {
            if (kid.getNodeType() == Node.ELEMENT_NODE) {
                String tag = kid.getNodeName();
                if (tag.equals("view-mapper")) {
                    viewMappers.add(kid.getTextContent().trim());
                } else if (tag.equals("view-mapping")) {
                    parseViewMapping(kid);
                } else if (tag.equals("debug-mode")) {
                	parseDebugMode(kid);
                } else if (tag.equals("metadata")) {
                	parseMetadata(kid);
                }
            }
        }
    }

	private void parseFactory(Node el) {
        for (Node kid = el.getFirstChild(); kid != null; kid = kid.getNextSibling()) {
            if (kid.getNodeType() == Node.ELEMENT_NODE) {
                String tag = kid.getNodeName();
                if (tag.equals("application-factory")) {
                    applicationFactories.add(kid.getTextContent().trim());
                } else if (tag.equals("faces-context-factory")) {
                    facesContextFactories.add(kid.getTextContent().trim());
                } else if (tag.equals("lifecycle-factory")) {
                    lifecycleFactories.add(kid.getTextContent().trim());
                } else if (tag.equals("render-kit-factory")) {
                    renderKitFactories.add(kid.getTextContent().trim());
                }
            }
        }
    }

    private void parseApplication(Node el) {
        for (Node kid = el.getFirstChild(); kid != null; kid = kid.getNextSibling()) {
            if (kid.getNodeType() == Node.ELEMENT_NODE) {
                String tag = kid.getNodeName();
                if (tag.equals("action-listener")) {
                    actionListeners.add(kid.getTextContent().trim());
                } else if (tag.equals("default-render-kit-id")) {
                    defaultRenderKitId = kid.getTextContent().trim();
                } else if (tag.equals("message-bundle")) {
                    messageBundle = kid.getTextContent().trim();
                } else if (tag.equals("navigation-handler")) {
                    navigationHandlers.add(kid.getTextContent().trim());
                } else if (tag.equals("view-handler")) {
                    // XXX Hook into FaceletViewHandler
                    String name = kid.getTextContent().trim();
                    if (name.equals(FACELET_VIEW_HANDLER)) {
                        name = FACELET_VIEW_HANDLER_HOOK;
                    }
                    viewHandlers.add(name);
                } else if (tag.equals("state-manager")) {
                    stateManagers.add(kid.getTextContent().trim());
                } else if (tag.equals("el-resolver")) {
                    elResolvers.add(kid.getTextContent().trim());
                } else if (tag.equals("property-resolver")) {
                    propertyResolvers.add(kid.getTextContent().trim());
                } else if (tag.equals("variable-resolver")) {
                    variableResolvers.add(kid.getTextContent().trim());
                } else if (tag.equals("locale-config")) {
                    parseLocaleConfig(kid);
                } else if (tag.equals("resource-bundle")) {
                    ResourceBundleConfig rb = new ResourceBundleConfig();
                    rb.displayName = getElementContent(kid, "display-name");
                    rb.baseName = getElementContent(kid, "base-name");
                    rb.var = getElementContent(kid, "var");
                    resourceBundles.add(rb);
                }
            }
        }
    }

    private void parseLocaleConfig(Node el) {
        for (Node kid = el.getFirstChild(); kid != null; kid = kid.getNextSibling()) {
            if (kid.getNodeType() == Node.ELEMENT_NODE) {
                String tag = kid.getNodeName();
                if (tag.equals("default-locale")) {
                    String localeString = kid.getTextContent().trim();
                    Locale l = FacesUtils.getLocaleFromString(localeString);
                    if (l != null) this.defaultLocale = l;
                } else if (tag.equals("supported-locale")) {
                    String localeString = kid.getTextContent().trim();
                    Locale l = FacesUtils.getLocaleFromString(localeString);
                    if (l != null) supportedLocales.add(l);
                }
            }
        }
    }

    private void parseComponent(Node el) {
        String componentType = getElementContent(el, "component-type");
        String componentClass = getElementContent(el, "component-class");
        ComponentConfig config = new ComponentConfig(componentType, componentClass);
        components.add(config);
    }

    private void parseConverter(Node el) {
        ConverterConfig config = new ConverterConfig();
        config.setConverterId(getElementContent(el, "converter-id"));
        config.setConverterForClass(getElementContent(el, "converter-for-class"));
        config.setConverterClass(getElementContent(el, "converter-class"));
        converters.add(config);
    }

    private void parseManagedBean(Node el) {
        ManagedBeanConfig bean = new ManagedBeanConfig();

        bean.setDisplayName(getElementContent(el, "display-name"));
        bean.setDescription(getElementContent(el, "description"));
        bean.setManagedBeanName(getElementContent(el, "managed-bean-name"));
        bean.setManagedBeanClass(getElementContent(el, "managed-bean-class"));
        bean.setManagedBeanScope(getElementContent(el, "managed-bean-scope"));

        Node mapEntriesNode = getElementByTagName(el, "map-entries");
        Node listEntriesNode = getElementByTagName(el, "list-entries");

        if (mapEntriesNode != null) {
            bean.setMapEntries(parseMapEntries(mapEntriesNode));
        } else if (listEntriesNode != null) {
            bean.setListEntries(parseListEntries(listEntriesNode));
        } else {
            for (Node managedPropertyNode : getElementsByTagName(el, "managed-property")) {
                bean.addManagedProperty(parseManagedProperty(managedPropertyNode));
            }
        }

        addManagedBean(bean);
    }

    private void addManagedBean(ManagedBeanConfig bean) {
        String name = bean.getManagedBeanName();

        for (ManagedBeanConfig exist : this.managedBeans) {
            if (name.equals(exist.getManagedBeanName())) {
                // already exist, don't add it
                String oldBeanClass = exist.getManagedBeanClass();
                String newBeanClass = bean.getManagedBeanClass();
                if (!oldBeanClass.equals(newBeanClass)) {
                    log.warning(_T(JSF_DUPPLICATE_MANAGED_BEAN, name, oldBeanClass, newBeanClass));
                }
                return;
            }
        }

        this.managedBeans.add(bean);
    }

    private ManagedBeanConfig.Property parseManagedProperty(Node el) {
        ManagedBeanConfig.Property property = new ManagedBeanConfig.Property();

        for (Node kid = el.getFirstChild(); kid != null; kid = kid.getNextSibling()) {
            if (kid.getNodeType() == Node.ELEMENT_NODE) {
                String tag = kid.getNodeName();
                if (tag.equals("property-name")) {
                    property.setPropertyName(kid.getTextContent().trim());
                } else if (tag.equals("property-class")) {
                    property.setPropertyClass(kid.getTextContent().trim());
                } else if (tag.equals("null-value")) {
                    property.setValue(null);
                } else if (tag.equals("value")) {
                    property.setValue(kid.getTextContent().trim());
                } else if (tag.equals("map-entries")) {
                    property.setMapEntries(parseMapEntries(kid));
                } else if (tag.equals("list-entries")) {
                    property.setListEntries(parseListEntries(kid));
                }
            }
        }

        return property;
    }

    private ManagedBeanConfig.MapEntries parseMapEntries(Node el) {
        ManagedBeanConfig.MapEntries mapEntries = new ManagedBeanConfig.MapEntries();

        for (Node kid = el.getFirstChild(); kid != null; kid = kid.getNextSibling()) {
            if (kid.getNodeType() == Node.ELEMENT_NODE) {
                String tag = kid.getNodeName();
                if (tag.equals("key-class")) {
                    mapEntries.setKeyClass(kid.getTextContent().trim());
                } else if (tag.equals("value-class")) {
                    mapEntries.setValueClass(kid.getTextContent().trim());
                } else if (tag.equals("map-entry")) {
                    ManagedBeanConfig.MapEntry entry = new ManagedBeanConfig.MapEntry();
                    entry.setKey(getElementContent(kid, "key"));
                    if (getElementByTagName(kid, "null-value") != null) {
                        entry.setValue(null);
                    } else {
                        entry.setValue(getElementContent(kid, "value"));
                    }
                    mapEntries.addMapEntry(entry);
                }
            }
        }

        return mapEntries;
    }

    private ManagedBeanConfig.ListEntries parseListEntries(Node el) {
        ManagedBeanConfig.ListEntries listEntries = new ManagedBeanConfig.ListEntries();

        for (Node kid = el.getFirstChild(); kid != null; kid = kid.getNextSibling()) {
            if (kid.getNodeType() == Node.ELEMENT_NODE) {
                String tag = kid.getNodeName();
                if (tag.equals("value-class")) {
                    listEntries.setValueClass(kid.getTextContent().trim());
                } else if (tag.equals("null-value")) {
                    listEntries.addValue(null);
                } else if (tag.equals("value")) {
                    listEntries.addValue(kid.getTextContent().trim());
                }
            }
        }

        return listEntries;
    }

    private void parseNavigationRule(Node el) {
        String fromViewId = getElementContent(el, "from-view-id");
        if (fromViewId == null || fromViewId.length() == 0) {
            fromViewId = "*";
        }

        for (Node kid = el.getFirstChild(); kid != null; kid = kid.getNextSibling()) {
            if (kid.getNodeType() == Node.ELEMENT_NODE && kid.getNodeName().equals("navigation-case")) {
                NavigationCase navcase = new NavigationCase();
                navcase.setFromViewId(fromViewId);
                navcase.setFromAction(getElementContent(kid, "from-action"));
                navcase.setFromOutcome(getElementContent(kid, "from-outcome"));
                navcase.setToViewId(getElementContent(kid, "to-view-id"));
                if (getElementByTagName(kid, "redirect") != null)
                    navcase.setRedirect(true);
                navigationCases.add(navcase);
            }
        }
    }

    private void parseRenderKit(Node el) {
        RenderKitConfig rk = new RenderKitConfig();

        rk.renderKitId = getElementContent(el, "render-kit-id");
        rk.renderKitClass = getElementContent(el, "render-kit-class");

        for (Node kid = el.getFirstChild(); kid != null; kid = kid.getNextSibling()) {
            if (kid.getNodeType() == Node.ELEMENT_NODE && kid.getNodeName().equals("renderer")) {
                RendererConfig rd = new RendererConfig();
                rd.componentFamily = getElementContent(kid, "component-family");
                rd.rendererType = getElementContent(kid, "renderer-type");
                rd.rendererClass = getElementContent(kid, "renderer-class");
                rk.renderers.add(rd);
            }
        }

        renderKits.add(rk);
    }

    private void parseLifecycle(Node el) {
        for (Node kid = el.getFirstChild(); kid != null; kid = kid.getNextSibling()) {
            if (kid.getNodeType() == Node.ELEMENT_NODE && kid.getNodeName().equals("phase-listener")) {
                phaseListeners.add(kid.getTextContent().trim());
            }
        }
    }

    private void parseValidator(Node el) {
        ValidatorConfig v = new ValidatorConfig();
        v.setValidatorId(getElementContent(el, "validator-id"));
        v.setValidatorClass(getElementContent(el, "validator-class"));
        validators.add(v);
    }

    private void parseViewMapping(Node el) {
        ViewMappingConfig config = new ViewMappingConfig();
        for (Node kid = el.getFirstChild(); kid != null; kid = kid.getNextSibling()) {
            if (kid.getNodeType() == Node.ELEMENT_NODE) {
                if (kid.getNodeName().equals("url-pattern")) {
                    config.viewIds.add(kid.getTextContent().trim());
                } else if (kid.getNodeName().equals("model-bean")) {
                    config.beanNames.add(kid.getTextContent().trim());
                }
            }
        }
        this.viewMappings.add(config);
    }

	private void parseDebugMode(Node el) {
        for (Node kid = el.getFirstChild(); kid != null; kid = kid.getNextSibling()) {
            if (kid.getNodeType() == Node.ELEMENT_NODE) {
                if (kid.getNodeName().equals("lifecycle-phases")) {
                	parseLifecyclePhasesDebugMode(kid);
                } else if (kid.getNodeName().equals("include-component-ids")) {
                    Debug.setValue(DebugMode.INCLUDE_COMPONENT_IDS, kid.getTextContent().trim().split(","));
                } else if (kid.getNodeName().equals("exclude-component-ids")) {
                    Debug.setValue(DebugMode.EXCLUDE_COMPONENT_IDS, kid.getTextContent().trim().split(","));
                } else if (kid.getNodeName().equals("include-component-classes")) {
                    Debug.setValue(DebugMode.INCLUDE_COMPONENT_CLASSES, kid.getTextContent().trim().split(","));
                } else if (kid.getNodeName().equals("exclude-component-classes")) {
                    Debug.setValue(DebugMode.EXCLUDE_COMPONENT_CLASSES, kid.getTextContent().trim().split(","));
                } else if (kid.getNodeName().equals("include-component-methods")) {
                    Debug.setValue(DebugMode.INCLUDE_COMPONENT_METHODS, kid.getTextContent().trim().split(","));
                } else if (kid.getNodeName().equals("exclude-component-methods")) {
                    Debug.setValue(DebugMode.EXCLUDE_COMPONENT_METHODS, kid.getTextContent().trim().split(","));
                } else if (kid.getNodeName().equals("include-renderer-types")) {
                    Debug.setValue(DebugMode.INCLUDE_RENDERER_TYPES, kid.getTextContent().trim().split(","));
                } else if (kid.getNodeName().equals("exclude-renderer-types")) {
                    Debug.setValue(DebugMode.EXCLUDE_RENDERER_TYPES, kid.getTextContent().trim().split(","));
                } else if (kid.getNodeName().equals("include-renderer-methods")) {
                    Debug.setValue(DebugMode.INCLUDE_RENDERER_METHODS, kid.getTextContent().trim().split(","));
                } else if (kid.getNodeName().equals("exclude-renderer-methods")) {
                	Debug.setValue(DebugMode.EXCLUDE_RENDERER_METHODS, kid.getTextContent().trim().split(","));
                } else if (kid.getNodeName().equals("exception")) {
                	String exception = kid.getTextContent().trim().toLowerCase();
                	if ("true".equals(exception) || "on".equals(exception))
                		Debug.setValue(DebugMode.EXCEPTION, true);
                } else if (kid.getNodeName().equals("uncompressed-js")) {
                	String uncompressedJs = kid.getTextContent().trim().toLowerCase();
                	if ("true".equals(uncompressedJs) || "on".equals(uncompressedJs))
                		Debug.setValue(DebugMode.UNCOMPRESSED_JS, true);
                } else if (kid.getNodeName().equals("misc")) {
                	String misc = kid.getTextContent().trim().toLowerCase();
                	if ("true".equals(misc) || "on".equals(misc))
                		Debug.setValue(DebugMode.MISC, true);
                }
            }
        }
    }
	
    private void parseMetadata(Node el) {
        for (Node kid = el.getFirstChild(); kid != null; kid = kid.getNextSibling()) {
            if (kid.getNodeType() == Node.ELEMENT_NODE) {
                if (kid.getNodeName().equals("jar")) {
                	metaDataJars.add(kid.getTextContent().trim());
                } else if (kid.getNodeName().equals("package")) {
                    metaDataPackages.add(kid.getTextContent().trim());
                }
            }
        }
	}

	private void parseLifecyclePhasesDebugMode(Node kid) {
		String phases = kid.getTextContent().trim();
		StringTokenizer tokenizer = new StringTokenizer(phases, ",");
		while (tokenizer.hasMoreTokens()) {
			String strPhaseOrdinal = tokenizer.nextToken();
			int phaseOrdinal = -1;
			try {
				phaseOrdinal = Integer.parseInt(strPhaseOrdinal);
				
				if (phaseOrdinal >= 1 && phaseOrdinal <= 6) {
					Debug.setValue(Debug.DEBUG_MODE_LIFECYCLE_PHASES[phaseOrdinal - 1], true);
				}
			} catch (Exception e) {
				// invalid lifecycle-phases option, ignore it
			}
		}
	}

    private static Node getElementByTagName(Node el, String tag) {
        for (Node node = el.getFirstChild(); node != null; node = node.getNextSibling()) {
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                if (tag.equals(node.getNodeName())) {
                    return node;
                }
            }
        }
        return null;
    }

    private static List<Node> getElementsByTagName(Node el, String tag) {
        List<Node> nodeList = new ArrayList<Node>();
        for (Node node = el.getFirstChild(); node != null; node = node.getNextSibling()) {
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                if (tag.equals(node.getNodeName())) {
                    nodeList.add(node);
                }
            }
        }
        return nodeList;
    }

    private static String getElementContent(Node el, String tag) {
        Node node = getElementByTagName(el, tag);
        if (node == null) {
            return null;
        } else {
            return node.getTextContent().trim();
        }
    }

    private void applyConfig() {
        applyFactoryConfig();
        applyApplicationConfig();
        applyELResolverConfig();
        applyManagedBeanConfig();
        applyNavigationRuleConfig();
        applyRenderKitConfig();
        applyLifecycleConfig();
        applyViewMappingConfig();

        // notify that the application is initialized.
        ApplicationFactory factory = (ApplicationFactory)
            FactoryFinder.getFactory(FactoryFinder.APPLICATION_FACTORY);
        Application app = factory.getApplication();
        ApplicationAssociate associate = ApplicationAssociate.getInstance(context);

        ApplicationListener[] applicationListeners = associate.getApplicationListeners();
        if (applicationListeners != null) {
            ApplicationEvent event = new ApplicationEvent(app, this.context);
            for (ApplicationListener l : applicationListeners) {
                try {
                    l.applicationInitialized(event);
                } catch (Throwable ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    private void applyFactoryConfig() {
        for (String factory : applicationFactories)
            FactoryFinder.setFactory(FactoryFinder.APPLICATION_FACTORY, factory);
        for (String factory : facesContextFactories)
            FactoryFinder.setFactory(FactoryFinder.FACES_CONTEXT_FACTORY, factory);
        for (String factory : lifecycleFactories)
            FactoryFinder.setFactory(FactoryFinder.LIFECYCLE_FACTORY, factory);
        for (String factory : renderKitFactories)
            FactoryFinder.setFactory(FactoryFinder.RENDER_KIT_FACTORY, factory);
    }

    private void applyApplicationConfig() {
        ApplicationFactory factory = (ApplicationFactory)
            FactoryFinder.getFactory(FactoryFinder.APPLICATION_FACTORY);
        Application app = factory.getApplication();
        ApplicationAssociate associate = ApplicationAssociate.getInstance(context);

        // notify that the application is created
        ApplicationListener[] applicationListeners = associate.getApplicationListeners();
        if (applicationListeners != null) {
            ApplicationEvent event = new ApplicationEvent(app, this.context);
            for (ApplicationListener l : applicationListeners) {
                try {
                    l.applicationCreated(event);
                } catch (Throwable ex) {
                    ex.printStackTrace();
                }
            }
        }

        if (!actionListeners.isEmpty()) {
            ActionListener actionListener = createInstance(
                ActionListener.class, actionListeners, app.getActionListener());
            app.setActionListener(actionListener);
        }

        if (defaultRenderKitId != null) {
            app.setDefaultRenderKitId(defaultRenderKitId);
        }

        if (messageBundle != null) {
            app.setMessageBundle(messageBundle);
        }

        if (!navigationHandlers.isEmpty()) {
            NavigationHandler navigationHandler = createInstance(
                NavigationHandler.class, navigationHandlers, app.getNavigationHandler());
            app.setNavigationHandler(navigationHandler);
        }

        if (!viewHandlers.isEmpty()) {
            ViewHandler viewHandler = createInstance(
                ViewHandler.class, viewHandlers, app.getViewHandler());
            app.setViewHandler(viewHandler);
        }

        if (!viewMappers.isEmpty()) {
            ModelBindingFactory mbf = ModelBindingFactory.instance();
            ViewMapper viewMapper = createInstance(
                ViewMapper.class, viewMappers, mbf.getViewMapper());
            mbf.setViewMapper(viewMapper);
        }

        if (!stateManagers.isEmpty()) {
            StateManager stateManager = createInstance(
                StateManager.class, stateManagers, app.getStateManager());
            app.setStateManager(stateManager);
        }

        if (defaultLocale != null) {
            app.setDefaultLocale(defaultLocale);
        }
        if (!supportedLocales.isEmpty()) {
            app.setSupportedLocales(supportedLocales);
        }

        if (!metaComponents.isEmpty()) {
            for (ComponentConfig x : metaComponents) {
                ComponentContainer container = ComponentContainer.getInstance();
                container.addComponentFactory(new MetaComponentFactory(x));
            }
        }
        // if exist component by config file, overwrite it with config file
        if (!components.isEmpty()) {
            for (ComponentConfig x : components) {
                app.addComponent(x.getComponentType(), x.getComponentClass());
            }
        }

        if (!converters.isEmpty()) {
            for (ConverterConfig x : converters) {
                if (x.getConverterId() != null) {
                    app.addConverter(x.getConverterId(), x.getConverterClass());
                }
                if (x.getConverterForClass() != null) {
                    try {
                        Class c = findClass(x.getConverterForClass(), getClassLoader());
                        app.addConverter(c, x.getConverterClass());
                    } catch (ClassNotFoundException ex) {
                        throw new FacesException(_T(JSF_CLASS_NOT_FOUND, x.getConverterClass()), ex);
                    }
                }
            }
        }

        if (!validators.isEmpty()) {
            for (ValidatorConfig x : validators) {
                app.addValidator(x.getValidatorId(), x.getValidatorClass());
            }
        }

        if (!resourceBundles.isEmpty()) {
            for (ResourceBundleConfig x : resourceBundles) {
                associate.addResourceBundle(x.var, x.baseName, x.displayName);
            }
        }
    }

    @SuppressWarnings("deprecation")
    private void applyELResolverConfig() {
        ELResolverRegistry registry = ELResolverRegistry.getInstance();

        if (!elResolvers.isEmpty()) {
            for (String name : elResolvers) {
                ELResolver resolver = createInstance(ELResolver.class, name, null);
                registry.addELResolverFromConfig(resolver);
            }
        }

        if (!propertyResolvers.isEmpty()) {
            PropertyResolver resolver = registry.getDefaultPropertyResolver();
            resolver = createInstance(PropertyResolver.class, propertyResolvers, resolver);
            registry.setPropertyResolverFromConfig(resolver);
        }

        if (!variableResolvers.isEmpty()) {
            VariableResolver resolver = registry.getDefaultVariableResolver();
            resolver = createInstance(VariableResolver.class, variableResolvers, resolver);
            registry.setVariableResolverFromConfig(resolver);
        }

        registry.registerELResolverWithJsp(this.context);
    }

    private void applyManagedBeanConfig() {
        ManagedBeanContainer container = ManagedBeanContainer.getInstance();
        for (ManagedBeanConfig mbean : managedBeans) {
            container.addBeanFactory(mbean);
        }
    }

    private void applyNavigationRuleConfig() {
        DefaultNavigationHandler handler = DefaultNavigationHandler.getInstance();
        if (handler != null) {
            for (NavigationCase navcase : navigationCases) {
                handler.addNavigationCase(navcase);
            }
        }
    }

    private void applyRenderKitConfig() {
        RenderKitFactory rkFactory = (RenderKitFactory)
            FactoryFinder.getFactory(FactoryFinder.RENDER_KIT_FACTORY);

        for (RenderKitConfig rkc: renderKits) {
            String renderKitId = rkc.renderKitId;
            if (renderKitId == null)
                renderKitId = RenderKitFactory.HTML_BASIC_RENDER_KIT;
            RenderKit rk = rkFactory.getRenderKit(null, renderKitId);

            if (rk == null) {
                if (rkc.renderKitClass == null)
                    throw new FacesException(_T(JSF_NO_SUCH_RENDER_KIT_ID, renderKitId));
                rk = createInstance(RenderKit.class, rkc.renderKitClass, null);
                rkFactory.addRenderKit(renderKitId, rk);
            }

            for (RendererConfig rdc : rkc.renderers) {
            	Renderer rd = createInstance(Renderer.class, rdc.rendererClass, null);
                rk.addRenderer(rdc.componentFamily, rdc.rendererType, rd);
            }
        }
    }

    private void applyLifecycleConfig() {
        LifecycleFactory factory = (LifecycleFactory)
            FactoryFinder.getFactory(FactoryFinder.LIFECYCLE_FACTORY);
        Lifecycle lifecycle = factory.getLifecycle(LifecycleFactory.DEFAULT_LIFECYCLE);

        for (String className : phaseListeners) {
        	PhaseListener listener = new PhaseListenerAdapter(loadClass(className));
            lifecycle.addPhaseListener(listener);
            if (lifecycleLog.isLoggable(Level.FINEST)) {
                lifecycleLog.finest("PhaseListener '" + className +"' added");
            }
        }
    }

    private void applyViewMappingConfig() {
        DefaultViewMapper viewMapper = DefaultViewMapper.getInstance();
        if (viewMapper != null) {
            for (ViewMappingConfig config : this.viewMappings) {
                for (String viewId : config.viewIds) {
                    for (String beanName : config.beanNames) {
                        viewMapper.addViewMapping(viewId, beanName);
                    }
                }
            }
        }
    }

    private static ClassLoader getClassLoader() {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        if (loader == null)
            loader = FacesConfigLoader.class.getClassLoader();
        return loader;
    }

    private static <T> T createInstance(Class<T> root, List<String> classNames, T delegate) {
        T result = delegate;
        for (String name : classNames) {
            result = createInstance(root, name, result);
        }
        return result;
    }

    private static Class<?> loadClass(String className) {
        Class<?> clazz;
        try {
            clazz = (Class<?>)getClassLoader().loadClass(className);
        } catch (ClassNotFoundException ex) {
            throw new FacesException(_T(JSF_CLASS_NOT_FOUND, className), ex);
        }
        return clazz;
    }

    @SuppressWarnings("unchecked")
    private static <T> T createInstance(Class<T> root, String className, T delegate) {
        Class<T> clazz;
        try {
            clazz = (Class<T>)getClassLoader().loadClass(className);
        } catch (ClassNotFoundException ex) {
            throw new FacesException(_T(JSF_CLASS_NOT_FOUND, className), ex);
        }
        if (!root.isAssignableFrom(clazz)) {
            throw new FacesException(_T(JSF_UNEXPECTED_CLASS, className, root.getName()));
        }

        T result;
        try {
            try {
                Constructor<T> cons = clazz.getConstructor(root);
                result = cons.newInstance(delegate);
            } catch (NoSuchMethodException ex) {
                result = clazz.newInstance();
            }
        } catch (Exception ex) {
            throw new FacesException(_T(JSF_INSTANTIATION_ERROR, className), ex);
        }
        return result;
    }

    private static <T> List<T> newList() {
        return new ArrayList<T>();
    }
}
