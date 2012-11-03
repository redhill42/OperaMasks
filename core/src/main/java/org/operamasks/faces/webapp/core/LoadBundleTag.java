/*
 * $Id: LoadBundleTag.java,v 1.5 2007/07/02 07:38:10 jacky Exp $
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

package org.operamasks.faces.webapp.core;

import javax.servlet.jsp.tagext.TagSupport;
import javax.servlet.jsp.JspException;
import javax.el.ValueExpression;
import javax.faces.context.FacesContext;
import java.util.ResourceBundle;
import java.util.Map;
import java.util.AbstractMap;
import java.util.Set;
import java.util.HashMap;
import java.util.Enumeration;
import java.util.MissingResourceException;
import java.util.Collection;
import static org.operamasks.resources.Resources.*;

@SuppressWarnings("serial")
public class LoadBundleTag extends TagSupport
{
    private ValueExpression basenameExpr;
    private String var;

    public void setBasename(ValueExpression basename) {
        this.basenameExpr = basename;
    }

    public void setVar(String var) {
        this.var = var;
    }

    public void relese() {
        super.release();
        basenameExpr = null;
        var = null;
    }

    @SuppressWarnings("unchecked")
    public int doStartTag()
        throws JspException
    {
        FacesContext context = FacesContext.getCurrentInstance();

        String basename = null;
        if (basenameExpr != null)
            basename = (String)basenameExpr.getValue(context.getELContext());

        if (basename == null || var == null)
            throw new JspException(_T(JSF_EXPECT_BASENAME_AND_BAR_ATTRIBUTES));

        final ResourceBundle bundle = ResourceBundle.getBundle(basename, context.getViewRoot().getLocale(),
                                                               Thread.currentThread().getContextClassLoader());
        if (bundle == null)
            throw new JspException(_T(JSF_RESOURCE_BUNDLE_NOT_FOUND, basename));

        Map bundleMap = new AbstractMap() {
            private HashMap mappings;

            public boolean containsKey(Object key) {
                if (key == null)
                    return false;
                return bundle.getObject(key.toString()) != null;
            }

            public Object get(Object key) {
                if (key == null)
                    return null;
                try {
                    return bundle.getObject(key.toString());
                } catch (MissingResourceException ex) {
                    return "???" + key + "???";
                }
            }

            public Set keySet() {
                return getMappings().keySet();
            }

            public Set entrySet() {
                return getMappings().entrySet();
            }

            public Collection values() {
                return getMappings().values();
            }

            private HashMap getMappings() {
                if (mappings == null) {
                    mappings = new HashMap();
                    Enumeration keys = bundle.getKeys();
                    while (keys.hasMoreElements()) {
                        String key = (String)keys.nextElement();
                        mappings.put(key, bundle.getObject(key));
                    }
                }
                return mappings;
            }
        };

        context.getExternalContext().getRequestMap().put(var, bundleMap);
        return SKIP_BODY;
    }
}
