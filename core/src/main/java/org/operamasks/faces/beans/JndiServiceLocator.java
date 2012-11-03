/*
 * $Id: JndiServiceLocator.java,v 1.1 2007/10/18 08:59:15 daniel Exp $
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

package org.operamasks.faces.beans;

import java.util.Map;
import java.util.Hashtable;

import javax.faces.FacesException;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 * 基于JNDI的对象工厂, 支持可配置的JNDI资源查找.
 */
public class JndiServiceLocator implements ObjectFactory
{
    private String jndiName;
    private Map<?,?> jndiEnv;
    private Context context;

    /**
     * 返回用于查找的JNDI名称.
     */
    public String getJndiName() {
        return jndiName;
    }

    /**
     * 设置用于查找的JNDI名称.
     */
    public void setJndiName(String jndiName) {
        this.jndiName = jndiName;
    }

    /**
     * 返回用于初始化JNDI上下文的环境信息.
     */
    public Map<?,?> getJndiEnvironment() {
        return this.jndiEnv;
    }

    /**
     * 设置用于初始化JNDI上下文的环境信息.
     */
    public void setJndiEnvironment(Map<?,?> env) {
        this.jndiEnv = env;
    }

    /**
     * 查找指定的JNDI资源.
     */
    public Object getObject() {
        try {
            return getInitialContext().lookup(this.jndiName);
        } catch (NamingException ex) {
            throw new FacesException(ex);
        }
    }

    protected Context getInitialContext() throws NamingException {
        if (this.context == null) {
            if (this.jndiEnv == null) {
                this.context = new InitialContext();
            } else {
                Hashtable<?,?> env = new Hashtable<Object,Object>(this.jndiEnv);
                this.context = new InitialContext(env);
            }
        }

        return this.context;
    }
}
