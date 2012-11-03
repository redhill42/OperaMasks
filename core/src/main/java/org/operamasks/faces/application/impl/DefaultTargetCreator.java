/*
 * $Id: DefaultTargetCreator.java,v 1.1 2007/10/26 16:30:11 daniel Exp $
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

package org.operamasks.faces.application.impl;

import java.lang.reflect.Constructor;
import javax.faces.FacesException;
import org.operamasks.faces.application.TargetCreator;
import static org.operamasks.resources.Resources.*;

public class DefaultTargetCreator implements TargetCreator
{
    private Class targetClass;
    private Constructor constructor;

    public DefaultTargetCreator(Class targetClass) {
        this.targetClass = targetClass;
    }

    public DefaultTargetCreator(Class targetClass, Class argType) {
        this.targetClass = targetClass;

        try {
            this.constructor = targetClass.getConstructor(argType);
        } catch (NoSuchMethodException ex) {/*ignored*/}
    }

    public Object createTarget(Object arg) {
        try {
            if (arg != null && this.constructor != null) {
                return this.constructor.newInstance(arg);
            } else {
                return this.targetClass.newInstance();
            }
        } catch (Exception ex) {
            throw new FacesException(_T(JSF_CREATE_TARGET_ERROR, targetClass.getName()), ex);
        }
    }

    public Class<?> getTargetClass() {
        return this.targetClass;
    }
}
