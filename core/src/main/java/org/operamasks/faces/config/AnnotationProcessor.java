/*
 * $Id: AnnotationProcessor.java,v 1.27 2008/03/10 08:35:18 lishaochuan Exp $
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

import java.beans.IntrospectionException;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.servlet.ServletContext;

import org.operamasks.faces.annotation.DefineConverter;
import org.operamasks.faces.annotation.DefineValidator;
import org.operamasks.faces.annotation.EventListener;
import org.operamasks.faces.annotation.Factory;
import org.operamasks.faces.annotation.ListEntries;
import org.operamasks.faces.annotation.ManagedBean;
import org.operamasks.faces.annotation.ManagedProperty;
import org.operamasks.faces.annotation.MapEntries;
import org.operamasks.faces.annotation.MapEntry;
import org.operamasks.faces.annotation.PhaseListener;
import org.operamasks.faces.annotation.component.Component;
import org.operamasks.faces.component.ComponentConfig;
import org.operamasks.util.BeanProperty;
import org.operamasks.util.BeanUtils;

public class AnnotationProcessor
{
    private static final String BASE_PATH = "/WEB-INF/classes/";
    private static final String OM_SPECIAL_RESOURCE = "/META-INF/services/org.operamasks.faces.application.ApplicationListener";

    private ServletContext context;
    private ClassLoader loader;
    
    private List<ManagedBeanConfig> managedBeans;
    private List<ComponentConfig> components;
    private List<ConverterConfig> converters;
    private List<ValidatorConfig> validators;
    private List<String> metaDataJars;
    private List<String> metaDataPackages;
	private List<String> phaseListeners;


    public AnnotationProcessor(ServletContext context, ClassLoader loader) {
        this.context = context;
        this.loader = loader;

        this.managedBeans = new ArrayList<ManagedBeanConfig>();
        this.converters = new ArrayList<ConverterConfig>();
        this.validators = new ArrayList<ValidatorConfig>();
        this.components = new ArrayList<ComponentConfig>();
        this.phaseListeners = new ArrayList<String>();
    }

    public void scan() {
        scan(BASE_PATH);
        scanOmJar();
        scanMetadataJars();
    }

    @SuppressWarnings("unchecked")
	private void scanMetadataJars() {
		try {
			String libDirPath = context.getRealPath("/WEB-INF/lib");
			File libDir = new File(libDirPath);
			if (libDir.exists() && libDir.isDirectory()) {
				File[] libs = libDir.listFiles();
				for (File lib : libs) {
					if (lib.isFile() && lib.getName().endsWith(".jar")) {
						try {
							JarFile jar = new JarFile(lib);
							scan(jar);
						} catch (IOException e) {
							// Illegal jar file, ignore it
						}
					}
				}
			}
		} catch (Exception e) {
			throw new FacesException("Failed to scan metadata from webapp lib jars", e);
		}
	}

	private boolean isMetaDataJar(String jarName) {
		for (String metaDataJar : metaDataJars) {
			if (metaDataJar.equals(jarName))
				return true;
		}
		
		return false;
	}

	@SuppressWarnings("unchecked")
	private void scanOmJar() {
		URL omResource = null;
        try {
            omResource = this.getClass().getResource(OM_SPECIAL_RESOURCE);
        } catch (Exception e1) {
            // ignore
        }
        
		if (omResource != null ) { 
            String path = omResource.getPath();
		    if ("jar".equals(omResource.getProtocol())) {
	            int pathStartIndex = 5;
	            int pathEndIndex = path.lastIndexOf("!");
	            String jarPath = path.substring(pathStartIndex, pathEndIndex);
	            
	            String libDir = context.getRealPath("/WEB-INF/lib");
	            // om jar is put in webapp lib directory, so delaying scan om jar until
	            // we scan webapp lib jars
	            if (jarPath.startsWith(libDir)) {
	                return;
	            }
	            
	            try {
	                JarFile jar = new JarFile(new File(jarPath));
	                scan(jar);
	            } catch (IOException e) {
	                throw new FacesException("Can't scan operamasks jar: " + jarPath, e);
	            }
		    } else if ("file".equals(omResource.getProtocol())) {
		        String filePath = path.substring(0, path.indexOf(OM_SPECIAL_RESOURCE));
		        File omPath = new File(filePath);
		        if (omPath.exists()) {
		            scanDir(omPath, "");
		        }
		    }
		}
	}

	private void scanDir(File omPath, String base) {
        File[] files = omPath.listFiles();
        for (File file : files) {
            String fileName = base + File.separator + file.getName();
            if (file.isDirectory()) {
                scanDir(file, fileName);
            } else if (file.getName().endsWith(".class")) {
                if (fileName.startsWith(File.separator)) {
                    fileName = fileName.substring(File.separator.length());
                }
                String className = fileName.replace(File.separatorChar, '.').substring(0, fileName.length() - 6);
                if (isInMetaDataPackages(className)) {
                    try {
                        scanClass(loader.loadClass(className));
                    } catch (NoClassDefFoundError e) {
                        // Can't load class, just ignore it
                    } catch (ClassNotFoundException e) {
                        // Can't load class, just ignore it
                    }
                }
            }
        }
    }

    private void scan(JarFile jar) {
		Enumeration<JarEntry> jarEntries = jar.entries();
		while (jarEntries.hasMoreElements()) {
			JarEntry jarEntry = jarEntries.nextElement();
			if (jarEntry.isDirectory())
				continue;
			
			String className = jarEntry.getName();
			if (!className.endsWith(".class")) {
				continue;
			} else {
				className = className.substring(0, (className.length() - 6));
			}
			
			if (className.startsWith("/")) {
				className = className.substring(1);
			}
			className = className.replace('/', '.');
			
			if (isInMetaDataPackages(className)) {
				try {
					scanClass(loader.loadClass(className));
				} catch (NoClassDefFoundError e) {
					// Can't load class, just ignore it
				} catch (ClassNotFoundException e) {
					// Can't load class, just ignore it
				}
			}
		}
	}

	private boolean isInMetaDataPackages(String name) {
		if (name.startsWith("org.operamasks.faces.component")) {
			return true;
		}
		
		for (String metaDataPackage : metaDataPackages) {
			if (metaDataPackage.equals("*"))
				return true;
			
			if (name.startsWith(metaDataPackage))
				return true;
		}
		
		return false;
	}

	public List<ManagedBeanConfig> getManagedBeans() {
        return this.managedBeans;
    }

    public List<ComponentConfig> getComponents() {
        return this.components;
    }

    public List<ConverterConfig> getConverters() {
        return this.converters;
    }

    public List<ValidatorConfig> getValidators() {
        return this.validators;
    }

    private void scan(String path) {
        @SuppressWarnings("unchecked")
        Set<String> paths = context.getResourcePaths(path);

        if (paths != null) {
            for (String file : paths) {
                if (file.endsWith("/")) {
                    scan(file);
                } else if (file.endsWith(".class")) {
                    try {
                        String classname = file.substring(BASE_PATH.length(), file.length()-6);
                        classname = classname.replace('/', '.');
                        scanClass(loader.loadClass(classname));
                    } catch (ClassNotFoundException ex) {
                        // ignored
                    } catch (NoClassDefFoundError ex) {
                        // ignored
                    }
                }
            }
        }
    }

    public void scanClass(Class<?> clazz) {
        ManagedBeanConfig mbean = scanManagedBeanClass(clazz);
        if (mbean != null) {
            this.managedBeans.add(mbean);
        }
        
        ComponentConfig component = scanComponentClass(clazz);
        if (component != null) {
            this.components.add(component);
        }

        ConverterConfig converter = scanConverterClass(clazz);
        if (converter != null) {
            this.converters.add(converter);
        }

        ValidatorConfig validator = scanValidatorClass(clazz);
        if (validator != null) {
            this.validators.add(validator);
        }
        
        if (clazz.getAnnotation(PhaseListener.class) != null) {
        	this.phaseListeners.add(clazz.getName());
        }
    }

    public ComponentConfig scanComponentClass(Class<?> clazz) {
        
        Component meta = clazz.getAnnotation(Component.class);

        boolean isComponentClass = UIComponent.class.isAssignableFrom(clazz) && 
            !Modifier.isAbstract(clazz.getModifiers()) &&
            !Modifier.isInterface(clazz.getModifiers()) &&
            meta != null;
    
        if (!isComponentClass) {
            return null;
        }
        
        String componentType = meta.type();
        if (componentType == null || componentType.length() == 0) {
            componentType = clazz.getName();
        }

        ComponentConfig component = new ComponentConfig(componentType, clazz.getName());
        component.setHandlerClass(meta.renderHandler());
        return component;
    }

    public ManagedBeanConfig scanManagedBeanClass(Class<?> clazz) {
        ManagedBean meta = clazz.getAnnotation(ManagedBean.class);
        if (meta == null) {
            return null;
        }

        String classname = clazz.getName();
        String beanName = meta.name();
        if (beanName.length() == 0) {
            beanName = classname.substring(classname.lastIndexOf('.')+1);
        }

        ManagedBeanConfig mbean = new ManagedBeanConfig();
        mbean.setManagedBeanName(beanName);
        mbean.setManagedBeanClass(classname);
        mbean.setManagedBeanScope(meta.scope());
        mbean.setDisplayName(meta.displayName());
        mbean.setDescription(meta.description());
        scanProperties(mbean, clazz);
        scanEventListeners(mbean, clazz);
        scanFactoryMethods(mbean, clazz);
        return mbean;
    }

    public void scanManagedBean(ManagedBeanConfig mbean) {
        Class<?> clazz;
        try {
            clazz = loader.loadClass(mbean.getManagedBeanClass());
        } catch (ClassNotFoundException ex) {
            return;
        } catch (NoClassDefFoundError ex) {
            return;
        }

        scanProperties(mbean, clazz);
        scanEventListeners(mbean, clazz);
        scanFactoryMethods(mbean, clazz);
    }

    private void scanProperties(ManagedBeanConfig mbean, Class<?> clazz) {
        Collection<BeanProperty> properties;
        try {
            properties = BeanUtils.getProperties(clazz);
        } catch (IntrospectionException ex) {
            throw new FacesException(ex);
        }

        // Scan for accessor methods...
        for (BeanProperty p : properties) {
            String name = p.getName();
            Class type = p.getType();
            Method readMethod = p.getReadMethod();
            Method writeMethod = p.getWriteMethod();

            if (readMethod != null) {
                scanProperty(mbean, readMethod, name, type);
            } else if (writeMethod != null) {
                scanProperty(mbean, writeMethod, name, type);
            }
        }

        // Scan for fields...
        for (Class<?> c = clazz; c != null; c = c.getSuperclass()) {
            for (Field field : c.getDeclaredFields()) {
                String name = field.getName();
                Class type = field.getType();
                if (mbean.getManagedProperty(name) == null) {
                    ManagedBeanConfig.Property p = scanProperty(mbean, field, name, type);
                    if (p != null) {
                        field.setAccessible(true);
                        p.setField(field);
                    }
                }
            }
        }
    }

    private ManagedBeanConfig.Property scanProperty(ManagedBeanConfig bean,
                                                    AccessibleObject member,
                                                    String name, Class type)
    {
        ManagedBeanConfig.Property p = null;

        if (member.isAnnotationPresent(ManagedProperty.class)) {
            ManagedProperty meta = member.getAnnotation(ManagedProperty.class);
            String value = meta.value();

            if (value != null && value.length() != 0) {
                // Note: only property with initial value is needed.
                p = new ManagedBeanConfig.Property(name, type.getName());
                p.setValue(value);
                bean.addManagedProperty(p);
            }
        }
        else if (member.isAnnotationPresent(MapEntries.class)) {
            MapEntries meta = member.getAnnotation(MapEntries.class);
            ManagedBeanConfig.MapEntries entries = new ManagedBeanConfig.MapEntries();

            entries.setKeyClass(meta.keyClass().getName());
            entries.setValueClass(meta.valueClass().getName());
            for (MapEntry entry : meta.value()) {
                entries.addMapEntry(entry.key(), entry.value());
            }

            p = new ManagedBeanConfig.Property(name, type.getName());
            p.setMapEntries(entries);
            bean.addManagedProperty(p);
        }
        else if (member.isAnnotationPresent(ListEntries.class)) {
            ListEntries meta = member.getAnnotation(ListEntries.class);
            ManagedBeanConfig.ListEntries entries = new ManagedBeanConfig.ListEntries();

            // determine value type if the property type is an array
            if (type.isArray()) {
                entries.setValueClass(type.getComponentType().getName());
            } else {
                entries.setValueClass(meta.valueClass().getName());
            }
            for (String entry : meta.value()) {
                entries.addValue(entry);
            }

            p = new ManagedBeanConfig.Property(name, type.getName());
            p.setListEntries(entries);
            bean.addManagedProperty(p);
        }

        return p;
    }

    private void scanEventListeners(ManagedBeanConfig mbean, Class<?> clazz) {
        for (Class<?> c = clazz; c != null; c = c.getSuperclass()) {
            for (Method method : c.getDeclaredMethods()) {
                EventListener meta = method.getAnnotation(EventListener.class);
                if (meta != null) {
                    String[] eventTypes = meta.value();
                    if (eventTypes.length == 0) {
                        eventTypes = new String[] { method.getName() };
                    }
                    method.setAccessible(true);

                    ManagedBeanConfig.EventListener listener =
                        new ManagedBeanConfig.EventListener();
                    listener.setEventTypes(eventTypes);
                    listener.setListenerMethod(method);
                    mbean.addEventListener(listener);
                }
            }
        }
    }

    private void scanFactoryMethods(ManagedBeanConfig mbean, Class<?> clazz) {
        for (Class<?> c = clazz; c != null; c = c.getSuperclass()) {
            for (Method method : c.getDeclaredMethods()) {
                Factory meta = method.getAnnotation(Factory.class);
                if (meta != null) {
                    addFactoryMethod(mbean, method, meta);
                }
            }
        }
    }

    private void addFactoryMethod(ManagedBeanConfig mbean, Method method, Factory meta) {
        Class type = method.getReturnType();

        if (type == Void.TYPE) {
            throw new FacesException("The factory method '" + method.getName() +
                                     "' must hava a return value.");
        }

        String typename = type.getName();
        String name = meta.name();

        if (name == null || name.length() == 0) {
            name = typename.substring(typename.lastIndexOf('.')+1);
        }

        for (ManagedBeanConfig.FactoryMethod fm : mbean.getFactoryMethods()) {
            if (name.equals(fm.getManagedBeanName())) {
                throw new FacesException("Duplciate factory method name: " + name);
            }
        }

        ManagedBeanConfig.FactoryMethod factory = new ManagedBeanConfig.FactoryMethod();
        factory.setFactoryName(mbean.getManagedBeanName());
        factory.setManagedBeanName(name);
        factory.setManagedBeanClass(typename);
        factory.setManagedBeanScope(meta.scope());
        scanProperties(factory, type);
        scanEventListeners(factory, type);
        scanFactoryMethods(factory, type);

        method.setAccessible(true);
        factory.setMethod(method);
        mbean.addFactoryMethod(factory);
    }

    public ConverterConfig scanConverterClass(Class<?> clazz) {
        DefineConverter meta = clazz.getAnnotation(DefineConverter.class);
        if (meta == null) {
            return null;
        }

        String id = meta.id();
        Class<?> type = meta.forType();

        if (id.length() == 0 && type == Object.class) {
            id = clazz.getName();
        }

        ConverterConfig converter = new ConverterConfig();
        if (id.length() != 0) {
            converter.setConverterId(id);
        }
        if (type != Object.class) {
            converter.setConverterForClass(type.getName());
        }
        converter.setConverterClass(clazz.getName());
        return converter;
    }

    public ValidatorConfig scanValidatorClass(Class<?> clazz) {
        DefineValidator meta = clazz.getAnnotation(DefineValidator.class);
        if (meta == null) {
            return null;
        }

        String id = meta.id();
        if (id.length() == 0) {
            id = clazz.getName();
        }

        ValidatorConfig validator = new ValidatorConfig();
        validator.setValidatorId(id);
        validator.setValidatorClass(clazz.getName());
        return validator;
    }

	public List<String> getMetaDataJars() {
		return metaDataJars;
	}

	public void setMetaDataJars(List<String> metadataJars) {
		this.metaDataJars = metadataJars;
	}

	public List<String> getMetaDataPackages() {
		return metaDataPackages;
	}

	public void setMetaDataPackages(List<String> metadataPackages) {
		this.metaDataPackages = metadataPackages;
	}

	public List<String> getPhaseListeners() {
		return phaseListeners;
	}
}
