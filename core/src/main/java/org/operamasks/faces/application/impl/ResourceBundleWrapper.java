/*
 * $Id: ResourceBundleWrapper.java,v 1.6 2008/01/31 04:12:24 daniel Exp $
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

import javax.el.MethodInfo;
import javax.el.MethodNotFoundException;
import javax.el.ELContext;
import java.util.ResourceBundle;
import java.util.Enumeration;
import java.util.MissingResourceException;
import java.text.MessageFormat;

import elite.lang.Closure;
import org.operamasks.el.eval.MethodResolvable;
import org.operamasks.el.eval.ELEngine;

public class ResourceBundleWrapper extends ResourceBundle
    implements MethodResolvable
{
    private ResourceBundle wrapped;

    public ResourceBundleWrapper(ResourceBundle wrapped) {
        this.wrapped = wrapped;
    }

    protected Object handleGetObject(String key) {
        return wrapped.getObject(key);
    }

    public Enumeration<String> getKeys() {
        return wrapped.getKeys();
    }

    public String format(String key, Object... params) {
        String msgtext = this.getString(key);
        if (params != null && params.length != 0) {
            msgtext = MessageFormat.format(msgtext, params);
        }
        return msgtext;
    }

    public MethodInfo getMethodInfo(ELContext ctx, String name)
        throws MethodNotFoundException
    {
        try {
            this.getObject(name);
        } catch (MissingResourceException ex) {
            throw new MethodNotFoundException(name, ex);
        }

        return new MethodInfo(name, String.class, new Class[0]);
    }

    public Object invoke(ELContext ctx, String name, Closure[] args)
        throws MethodNotFoundException
    {
        try {
            String msgtext = this.getString(name);
            if (args.length != 0) {
                msgtext = MessageFormat.format(msgtext, ELEngine.getArgValues(ctx, args));
            }
            return msgtext;
        } catch (MissingResourceException ex) {
            throw new MethodNotFoundException(name, ex);
        }
    }
}
