/*
 * $Id: ModelSecurity.java,v 1.1 2007/09/25 22:06:35 daniel Exp $
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
package org.operamasks.faces.binding.impl;

import javax.faces.context.FacesContext;
import javax.faces.context.ExternalContext;
import java.lang.reflect.AnnotatedElement;
import javax.annotation.security.DenyAll;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;

@SuppressWarnings("unchecked")
class ModelSecurity
{
    private boolean denyAll;
    private boolean permitAll;
    private String roles[];

    private ModelSecurity(boolean denyAll, boolean permitAll, String[] roles) {
        this.denyAll = denyAll;
        this.permitAll = permitAll;
        this.roles = roles;
    }

    public boolean isUserInRole(FacesContext context) {
        if (this.denyAll) {
            return false;
        }

        if (this.permitAll) {
            return true;
        }

        if (this.roles != null) {
            ExternalContext ext = context.getExternalContext();
            for (String role : this.roles) {
                if (ext.isUserInRole(role)) {
                    return true;
                }
            }
            return false;
        }

        return true;
    }

    public static ModelSecurity scan(AnnotatedElement elem) {
        if (denyAllClass == null || permitAllClass == null || rolesAllowedClass == null) {
            return null;
        }

        boolean denyAll = elem.isAnnotationPresent(denyAllClass);
        boolean permitAll = elem.isAnnotationPresent(permitAllClass);
        RolesAllowed rolesAllowed = elem.getAnnotation(rolesAllowedClass);

        if (denyAll || permitAll || rolesAllowed != null) {
            String[] roles = (rolesAllowed != null) ? rolesAllowed.value() : null;
            return new ModelSecurity(denyAll, permitAll, roles);
        } else {
            return null;
        }
    }

    private static Class<DenyAll> denyAllClass;
    private static Class<PermitAll> permitAllClass;
    private static Class<RolesAllowed> rolesAllowedClass;

    static {
        try {
            denyAllClass = (Class<DenyAll>)Class.forName("javax.annotation.security.DenyAll");
            permitAllClass = (Class<PermitAll>)Class.forName("javax.annotation.security.PermitAll");
            rolesAllowedClass = (Class<RolesAllowed>)Class.forName("javax.annotation.security.RolesAllowed");
        } catch (Throwable ex) {
            denyAllClass = null;
            permitAllClass = null;
            rolesAllowedClass = null;
        }
    }
}
