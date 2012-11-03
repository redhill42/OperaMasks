/*
 * $Id: ViewDelegateManager.java,v 1.4 2008/04/21 07:40:22 lishaochuan Exp $
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
package org.operamasks.faces.render.delegate;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.faces.context.FacesContext;
import javax.faces.context.ExternalContext;

import org.operamasks.faces.render.tree.TreeDelegate;
import org.operamasks.faces.render.widget.yuiext.DataGridViewDelegate;
import org.operamasks.faces.render.widget.yuiext.DataViewDelegate;

public class ViewDelegateManager 
{
    private final static String VIEW_DELEGATE_KEY = "org.operamasks.faces.VIEW_DELEGATE";

    public static ViewDelegateManager getInstance(FacesContext context) {
        return getInstance(context, true);
    }

    public static ViewDelegateManager getInstance(FacesContext context, boolean create) {
        ExternalContext ext = context.getExternalContext();
        if (ext.getSession(create) == null) {
            return null;
        }

        Map<String,Object> sessionMap = ext.getSessionMap();
        ViewDelegateManager manager = (ViewDelegateManager)sessionMap.get(VIEW_DELEGATE_KEY);
        if (manager == null && create) {
            manager = new ViewDelegateManager();
            manager.init();
            sessionMap.put(VIEW_DELEGATE_KEY, manager);
        }
        return manager;
    }
    
    Set<ViewDelegate> delegates; 
    
    private ViewDelegateManager() {
        delegates = new HashSet<ViewDelegate>();
    }

    private void init() {
        // add view delegates;
        delegates.add(new DataGridViewDelegate());
        delegates.add(new DataViewDelegate());
        delegates.add(new TreeDelegate());
    }
    
    public void registerViewDelegate(ViewDelegate delegate) {
        delegates.add(delegate);
    }
    public void unregisterViewDelegate(ViewDelegate delegate) {
        delegates.remove(delegate);
    }
    
    public void processViewDelegates(FacesContext context) throws IOException{
        for (ViewDelegate delegate: delegates) {
            if (context.getResponseComplete()) {
                return;
            }
            delegate.delegate(context);
        }
    }
    
    public Set<ViewDelegate> getViewDelegates() {
        return this.delegates;
    }

}
