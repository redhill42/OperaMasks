/*
 * $Id: ComponentDeclarationVisitor.java,v 1.3 2008/03/10 08:28:52 lishaochuan Exp $
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

import static org.operamasks.faces.tools.apt.GeneratorUtils.COMPONENT_CLASS_EXT;

import org.operamasks.faces.tools.annotation.ComponentMeta;
import org.operamasks.faces.tools.generate.ComponentClassGenerator;
import org.operamasks.faces.tools.generate.FaceletsConfigGenerator;
import org.operamasks.faces.tools.generate.FacesConfigGenerator;
import org.operamasks.faces.tools.generate.TagClassGenerator;

import com.sun.mirror.apt.AnnotationProcessorEnvironment;
import com.sun.mirror.declaration.ClassDeclaration;
import com.sun.mirror.util.SimpleDeclarationVisitor;

public class ComponentDeclarationVisitor extends SimpleDeclarationVisitor
{
    private final AnnotationProcessorEnvironment env;


    ComponentDeclarationVisitor(AnnotationProcessorEnvironment env) {
        this.env = env;
    }

    public void visitClassDeclaration(ClassDeclaration d) {
        if (isComponentClass(d)) {
            System.out.println("process component["+d.getSimpleName()+"]...\t begin");
            ComponentDeclaration comp = new ComponentDeclaration(env, d);
            //generate component class
            ComponentClassGenerator compGenerator = new ComponentClassGenerator(env, comp);
            compGenerator.generate();
            
            //generate tag class
            TagClassGenerator tagGenerator = new TagClassGenerator(env, comp);
            tagGenerator.generate();

            //generate faces-config.xml fragment
            FacesConfigGenerator configGenerator = new FacesConfigGenerator(env, comp);
            configGenerator.generate();

            FaceletsConfigGenerator faceletsGenerator = new FaceletsConfigGenerator(env, comp);
            faceletsGenerator.generate();
            // TODO: generate .taglib.xml fragment
            // TODO: generate .tld
            System.out.println("process component["+d.getSimpleName()+"]...\t end");
        }
    }

    private boolean isComponentClass(ClassDeclaration d) {
        return (d != null && d.getAnnotation(ComponentMeta.class) != null) && d.getSimpleName().endsWith(COMPONENT_CLASS_EXT);
    }

}
