/*
 * $Id: ELResolverRegistry.java,v 1.1 2007/10/24 04:40:43 daniel Exp $
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

package org.operamasks.faces.application;

import java.util.List;
import javax.el.ELResolver;
import javax.faces.el.*;
import javax.servlet.ServletContext;

import org.operamasks.faces.application.impl.DefaultELResolverRegistry;

/**
 * Centire registry for EL Resolvers and legacy Variable/Property Resolver.
 */
@SuppressWarnings("deprecation")
public abstract class ELResolverRegistry
{
    private static final String KEY = ELResolverRegistry.class.getName();

    public static final ELResolverRegistry getInstance() {
        ApplicationAssociate assoc = ApplicationAssociate.getInstance();
        return assoc.getSingleton(KEY, DefaultELResolverRegistry.class);
    }

    /**
     * Get ELResolvers that specified in the configuration file.
     */
    public abstract List<ELResolver> getELResolversFromConfig();

    /**
     * Add an ELResolver that specified in the configuration file.
     */
    public abstract void addELResolverFromConfig(ELResolver resolver);

    /**
     * Get ELResolvers that added by calling Application.addELResolver().
     */
    public abstract List<ELResolver> getELResolversFromApp();

    /**
     * Add an ELResolver by calling Application.addELResolver().
     */
    public abstract void addELResolverFromApp(ELResolver resolver);

    /**
     * Get the default legacy VariableResolver.
     */
    public abstract VariableResolver getDefaultVariableResolver();

    /**
     * Get the default legacy PropertyResolver.
     */
    public abstract PropertyResolver getDefaultPropertyResolver();

    /**
     * Get legacy VariableResolver that specified in the configuration file.
     */
    public abstract VariableResolver getVariableResolverFromConfig();

    /**
     * Set legacy VariableResolver that specified in the configuration file.
     */
    public abstract void setVariableResolverFromConfig(VariableResolver resolver);

    /**
     * Get legacy VariableResolver by calling Application.setVariableResolver().
     */
    public abstract VariableResolver getVariableResolverFromApp();

    /**
     * Set legacy VariableResolver by calling Application.setVariableResolver().
     */
    public abstract void setVariableResolverFromApp(VariableResolver resolver);

    /**
     * Get the legacy PropertyResolver that specified in the configuration file.
     */
    public abstract PropertyResolver getPropertyResolverFromConfig();

    /**
     * Set the legacy PropertyResolver that specified in the configuration file.
     */
    public abstract void setPropertyResolverFromConfig(PropertyResolver resolver);

    /**
     * Get the legacy PropertyResolver by calling Application.setPropertyResolver().
     */
    public abstract PropertyResolver getPropertyResolverFromApp();

    /**
     * Set the legacy PropertyResolver by calling Application.setPropertyResolver().
     */
    public abstract void setPropertyResolverFromApp(PropertyResolver resolver);

    /**
     * Returns the variable resolver adapter for legacy application.
     */
    public abstract VariableResolver getVariableResolverAdapter();

    /**
     * Returns the property resolver adapter for legacy application.
     */
    public abstract PropertyResolver getPropertyResolverAdapter();

    /**
     * Get the final composite ELResolver for faces.
     */
    public abstract ELResolver getELResolverForFaces();

    /**
     * Get the final composite ELResolver for JSP.
     */
    public abstract ELResolver getELResolverForJsp();

    /**
     * Register ELResolver with JSP.
     */
    public abstract void registerELResolverWithJsp(ServletContext context);
}
