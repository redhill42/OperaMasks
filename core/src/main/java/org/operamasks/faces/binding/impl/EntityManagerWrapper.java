/*
 * $Id: EntityManagerWrapper.java,v 1.2 2007/10/15 21:09:47 daniel Exp $
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

import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.LockModeType;
import javax.persistence.Query;
import javax.persistence.EntityTransaction;
import java.util.List;
import java.util.ArrayList;

import org.operamasks.faces.binding.ModelBean;

final class EntityManagerWrapper implements EntityManager
{
    private ModelBean bean;
    private EntityManager em;

    public EntityManagerWrapper(ModelBean bean, EntityManager em) {
        this.bean = bean;
        this.em = em;
    }
    
    public void persist(Object entity) {
        em.persist(entity);
    }

    public <T> T merge(T entity) {
        return em.merge(entity);
    }

    public void remove(Object entity) {
        em.remove(entity);
    }

    public <T> T find(Class<T> entityClass, Object key) {
        return em.find(entityClass, key);
    }

    public <T> T getReference(Class<T> entityClass, Object key) {
        return em.getReference(entityClass, key);
    }

    public void flush() {
        em.flush();
    }

    public void setFlushMode(FlushModeType flushMode) {
        em.setFlushMode(flushMode);
    }

    public FlushModeType getFlushMode() {
        return em.getFlushMode();
    }

    public void lock(Object entity, LockModeType lockMode) {
        em.lock(entity, lockMode);
    }

    public void refresh(Object entity) {
        em.refresh(entity);
    }

    public void clear() {
        em.clear();
    }

    public boolean contains(Object entity) {
        return em.contains(entity);
    }

    public Query createQuery(String ejbqlString) {
        if (ejbqlString.indexOf("#{") == -1) {
            return em.createQuery(ejbqlString);
        }

        StringBuilder ejbql = new StringBuilder(ejbqlString.length());
        List<String> params = new ArrayList<String>();
        int start = 0, current = 0;

        // replace embedded EL expression with parameter marker.
        while ((current = ejbqlString.indexOf("#{", current)) != -1) {
            int terminate = ejbqlString.indexOf('}', current);
            if (terminate == -1) {
                current += 2;
                continue;
            }

            ejbql.append(ejbqlString.substring(start, current));
            ejbql.append(":").append(getParameterName(params.size()));
            params.add(ejbqlString.substring(current, terminate+1));
            start = current = terminate + 1;
        }
        ejbql.append(ejbqlString.substring(start));

        // evaluate embedded EL expression and set query parameters.
        Query q = em.createQuery(ejbql.toString());
        for (int i = 0; i < params.size(); i++) {
            String name = getParameterName(i);
            Object value = this.bean.evaluateExpression(params.get(i), Object.class);
            q.setParameter(name, value);
        }
        return q;
    }

    private static String getParameterName(int pos) {
        return "el" + pos;
    }

    public Query createNamedQuery(String name) {
        return em.createNamedQuery(name);
    }

    public Query createNativeQuery(String sqlString) {
        return em.createNativeQuery(sqlString);
    }

    public Query createNativeQuery(String sqlString, Class resultClass) {
        return em.createNativeQuery(sqlString, resultClass);
    }

    public Query createNativeQuery(String sqlString, String resultSetMapping) {
        return em.createNativeQuery(sqlString, resultSetMapping);
    }

    public void joinTransaction() {
        em.joinTransaction();
    }

    public Object getDelegate() {
        return em.getDelegate();
    }

    public void close() {
        em.close();
    }

    public boolean isOpen() {
        return em.isOpen();
    }

    public EntityTransaction getTransaction() {
        return em.getTransaction();
    }
}
