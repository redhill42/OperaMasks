/*
 * $Id: ComponentAnnotationProcessorFactory.java,v 1.2 2008/03/10 08:28:52 lishaochuan Exp $
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

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import com.sun.mirror.apt.AnnotationProcessorFactory;
import com.sun.mirror.apt.AnnotationProcessor;
import com.sun.mirror.apt.AnnotationProcessorEnvironment;
import com.sun.mirror.declaration.AnnotationTypeDeclaration;
import com.sun.mirror.declaration.TypeDeclaration;
import static com.sun.mirror.util.DeclarationVisitors.*;

public class ComponentAnnotationProcessorFactory implements AnnotationProcessorFactory
{
    public Collection<String> supportedOptions() {
        return Collections.emptySet();
    }
    
    public Collection<String> supportedAnnotationTypes() {
        return Collections.singleton("*");
    }

    public AnnotationProcessor getProcessorFor(Set<AnnotationTypeDeclaration> atds,
                                               final AnnotationProcessorEnvironment env) {
        final ComponentDeclarationVisitor visitor = new ComponentDeclarationVisitor(env);
        return new AnnotationProcessor() {
            public void process() {
                GeneratorUtils.initConfigFile();
                for (TypeDeclaration decl : env.getSpecifiedTypeDeclarations()) {
                    decl.accept(getDeclarationScanner(visitor, NO_OP));
                }
            }
        };
    }
}
