/*
 * $Id: ComponentDeclaration.java,v 1.8 2008/04/09 02:52:26 lishaochuan Exp $
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.operamasks.faces.tools.annotation.ComponentMeta;
import org.operamasks.faces.tools.annotation.ComponentPackage;
import org.operamasks.faces.tools.annotation.ComponentTagPackage;

import com.sun.mirror.apt.AnnotationProcessorEnvironment;
import com.sun.mirror.declaration.ClassDeclaration;
import com.sun.mirror.declaration.FieldDeclaration;
import com.sun.mirror.declaration.MethodDeclaration;
import com.sun.mirror.declaration.Modifier;
import com.sun.mirror.declaration.ParameterDeclaration;

public class ComponentDeclaration
{
    private ClassDeclaration classDecl;

    private String componentPackageName;
    private String componentBaseClassName;
    private String componentClassName;
    private String componentCatalog;
    private String componentConfigFilePath;

    private String tagName;
    private String tagBaseClassName;
    private String tagClassName;
    private String tagPackageName;
    private String tagConfigFilePath;

    private String componentFamily;
    private String componentType;
    private String rendererType;
    
    private boolean onlyBuildComponentFile;
    private boolean enable;
    
    private Collection<AttributeDeclaration> attributes;

    private String baseQualifiedName;
    private boolean isProxy;

    public ComponentDeclaration(AnnotationProcessorEnvironment env, ClassDeclaration decl) {
        this.classDecl = decl;

        ComponentMeta meta = decl.getAnnotation(ComponentMeta.class);
        if (meta == null) {
            throw new IllegalArgumentException();
        }
        
        this.isProxy = meta.isProxy();

        this.componentPackageName = decl.getPackage().getQualifiedName();
        this.componentCatalog = meta.catalog();

        ComponentPackage packageMeta = decl.getPackage().getAnnotation(ComponentPackage.class);
        if (packageMeta != null) {
            this.componentPackageName = packageMeta.value();
            if (componentCatalog == null || componentCatalog.length() == 0)
                componentCatalog = packageMeta.catalog();
            this.componentConfigFilePath = packageMeta.configFilePath();
            this.enable = packageMeta.enable();
        }
        
        if (this.componentPackageName.length() == 0) {
            this.componentPackageName = decl.getPackage().getQualifiedName();
        }

        this.componentBaseClassName = decl.getSimpleName();
        this.baseQualifiedName = decl.getQualifiedName();
        
        if (!this.componentBaseClassName.endsWith(GeneratorUtils.COMPONENT_CLASS_EXT)) {
            throw new IllegalArgumentException();
        }
        this.componentClassName = this.componentBaseClassName.substring(0, this.componentBaseClassName.length()-GeneratorUtils.COMPONENT_CLASS_EXT.length());

        this.tagPackageName = this.componentPackageName;
        ComponentTagPackage tagPackageMeta = decl.getPackage().getAnnotation(ComponentTagPackage.class);
        if (tagPackageMeta != null) {
            this.tagPackageName = tagPackageMeta.value();
            this.tagConfigFilePath = tagPackageMeta.configFilePath();
        }
        if (this.tagPackageName.length() == 0) {
            this.tagPackageName = null;
        }
        
        this.tagName = meta.tagName();
        if(tagName == null || "".equals(tagName)){
       	    onlyBuildComponentFile = true;
        }
        
        this.tagClassName = this.componentClassName + "Tag";
        this.tagBaseClassName = meta.tagBaseClass();
        if(this.tagBaseClassName == null || "".equals(this.tagBaseClassName)){
            if (env.getTypeDeclaration(getQualifiedTagClassName() + GeneratorUtils.COMPONENT_CLASS_EXT) != null) {
            	this.tagBaseClassName = meta.tagBaseClass();
            	if (this.tagBaseClassName == null || "".equals(this.tagBaseClassName)) {
           	    this.tagBaseClassName = this.tagClassName + GeneratorUtils.COMPONENT_CLASS_EXT;
            	}
            } else {
            	String catalog = componentCatalog == null ? "" : "." + componentCatalog;
            	String parentName = null;
            	String defaultTagBaseClass = "org.operamasks.faces.webapp.html.HtmlBasicELTag";
            	if (decl.getSuperclass() != null && decl.getSuperclass().getSuperclass()!= null) {
            	    ClassDeclaration classDec = decl.getSuperclass().getSuperclass().getDeclaration();
            	    if (classDec != null) {
       		        parentName = classDec.getSimpleName().replace("Base", "");
                	if (parentName != null && !"UIComponent".equals(parentName)) {
              	            this.tagBaseClassName = String.format("org.operamasks.faces.webapp%s.%sTag", catalog, parentName);
                	} else {
                	    this.tagBaseClassName = defaultTagBaseClass;
                	}
           	    } else {
                        this.tagBaseClassName = defaultTagBaseClass;
                    }
            	} else {
            	    this.tagBaseClassName = defaultTagBaseClass;
            	}
            }
        }

        this.componentFamily = meta.family();
        if (this.componentFamily.length() == 0) {
            MethodDeclaration getFamilyMethod = getMethod(decl, "getFamily");
            if (getFamilyMethod != null && !getFamilyMethod.getModifiers().contains(Modifier.ABSTRACT)) {
                this.componentFamily = null;
            } else {
                this.componentFamily = getQualifiedComponentClassName();
            }
        }

        this.componentType = meta.type();
        if (this.componentType.length() == 0) {
            this.componentType = getQualifiedComponentClassName();
        }

        this.rendererType = meta.rendererType();
        if (this.rendererType.length() == 0) {
            this.rendererType = getQualifiedComponentClassName();
        } else if (this.rendererType.equals("null")) {
            this.rendererType = null;
        }

        this.attributes = new ArrayList<AttributeDeclaration>();
        for (FieldDeclaration field : decl.getFields()) {
            Collection<Modifier> modifiers = field.getModifiers();
            if (modifiers.contains(Modifier.PROTECTED) && !modifiers.contains(Modifier.STATIC)) {
                this.attributes.add(new AttributeDeclaration(this, field));
            }
        }
    }

    public ClassDeclaration getComponentClassDeclaration() {
        return this.classDecl;
    }

    public String getComponentPackageName() {
        return this.componentPackageName;
    }

    public String getComponentBaseClassName() {
        return this.componentBaseClassName;
    }

    public String getComponentClassName() {
        return this.componentClassName;
    }

    public String getQualifiedComponentClassName() {
        if (this.componentPackageName != null) {
            return this.componentPackageName + "." + this.componentClassName;
        } else {
            return this.componentClassName;
        }
    }

    public String getTagPackageName() {
        return tagPackageName;
    }

    public String getTagBaseClassName() {
        return tagBaseClassName;
    }

    public String getTagClassName() {
        return tagClassName;
    }

    public String getQualifiedTagClassName() {
        if (this.tagPackageName != null) {
            return this.tagPackageName + "." + this.tagClassName;
        } else {
            return this.tagClassName;
        }
    }

    public String getComponentFamily() {
        return componentFamily;
    }

    public String getComponentType() {
        return componentType;
    }

    public String getRendererType() {
        return rendererType;
    }

    public String getDocumentation() {
        return this.classDecl.getDocComment();
    }

    public Collection<AttributeDeclaration> getAttributes() {
        return this.attributes;
    }

    public MethodDeclaration getMethod(String name, String... args) {
        return getMethod(this.classDecl, name, args);
    }

    private static MethodDeclaration getMethod(ClassDeclaration decl, String name, String... args) {
        while (decl != null) {
            for (MethodDeclaration method : decl.getMethods()) {
                if (isMethodMatch(method, name, args)) {
                    return method;
                }
            }
            if (decl.getSuperclass() == null) {
                break;
            }
            decl = decl.getSuperclass().getDeclaration();
        }
        return null;
    }

    private static boolean isMethodMatch(MethodDeclaration method, String name, String[] args) {
        if (!name.equals(method.getSimpleName())) {
            return false;
        }

        Collection<ParameterDeclaration> params = method.getParameters();
        if (params.size() != args.length) {
            return false;
        }

        Iterator<ParameterDeclaration> it = params.iterator();
        for (int i = 0; it.hasNext(); i++) {
            ParameterDeclaration paramDecl = it.next();
            if (!args[i].equals(paramDecl.getType().toString())) {
                return false;
            }
        }

        return true;
    }

    public ClassDeclaration getClassDecl() {
        return classDecl;
    }

    public String getBaseQualifiedName() {
        return baseQualifiedName;
    }

    public String getTagName() {
        return tagName;
    }

    public boolean isProxy() {
        return isProxy;
    }
    
    public String getComponentConfigFileURL(String projectBase){
        if ("".equals(componentConfigFilePath) || componentConfigFilePath == null) {
            componentConfigFilePath = String.format(projectBase + "/src/main/resources/org/operamasks/faces/config/%s-config.xml", this.componentCatalog);
        }
        return componentConfigFilePath;
    }

    public String getTagConfigFileURL(String projectBase){
        if ("".equals(tagConfigFilePath) || tagConfigFilePath == null) {
            tagConfigFilePath = String.format(projectBase + "/src/main/resources/META-INF/%s.taglib.xml", this.componentCatalog);
        }
        return tagConfigFilePath;
    }

    public boolean isOnlyBuildComponentFile() {
        return onlyBuildComponentFile;
    }

    public boolean isEnable() {
        return enable;
    }
}
