/*
 * $Id: UIPager.java,v 1.12 2008/03/13 12:28:58 jacky Exp $
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
package org.operamasks.faces.component.widget;

import javax.el.ValueExpression;
import javax.faces.component.UIComponentBase;
import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.render.Renderer;

import java.util.Map;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

import org.operamasks.faces.util.FacesUtils;
import org.operamasks.faces.component.ajax.AjaxUpdater;
import org.operamasks.faces.render.resource.ResourceManager;
import org.operamasks.faces.render.resource.ResourceProvider;

/**
 * 分页器组件
 */
public class UIPager extends UIComponentBase
{
    public static final String COMPONENT_FAMILY = "org.operamasks.faces.widget.Pager";
    public static final String COMPONENT_TYPE = "org.operamasks.faces.widget.Pager";

    public static final int DEFAULT_PAGESIZE = 20 ;

    public UIPager() {
        setRendererType(null);
    }

    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    /**
     * 分页器所对应的目标组件ID
     */
    private String for_;

    public String getFor() {
        if (this.for_ != null) {
            return this.for_;
        }
        ValueExpression ve = getValueExpression("for");
        if (ve != null) {
            return (String)ve.getValue(getFacesContext().getELContext());
        } else {
            return null;
        }
    }

    public void setFor(String for_) {
        this.for_ = for_;
    }

    public static UIPager getPagerFor(FacesContext context, UIComponent target) {
        UIComponent top = target;
        while (top.getParent() != null && !(top instanceof UIViewRoot)) {
            top = top.getParent();
        }

        Iterator<UIComponent> itr = FacesUtils.createChildrenIterator(top, true);
        while (itr.hasNext()) {
            UIComponent current = itr.next();
            if (current.isRendered() && (current instanceof UIPager)) {
                String forId = ((UIPager)current).getFor();
                if (FacesUtils.getForComponent(context, forId, current) == target) {
                    return (UIPager)current;
                }
            }
        }
        return null;
    }

    public static List<UIPager> getAllPagersFor(FacesContext context, UIComponent target) {
        UIComponent top = target;
        while (top.getParent() != null && !(top instanceof UIViewRoot)) {
            top = top.getParent();
        }

        List<UIPager> result = new ArrayList<UIPager>();
        Iterator<UIComponent> itr = FacesUtils.createChildrenIterator(top, true);
        while (itr.hasNext()) {
            UIComponent current = itr.next();
            if (current.isRendered() && (current instanceof UIPager)) {
                String forId = ((UIPager)current).getFor();
                if (FacesUtils.getForComponent(context, forId, current) == target) {
                    result.add((UIPager)current);
                }
            }
        }
        return result;
    }

    private static final String PAGER_BOUND_PARAM = "org.operamasks.faces.PAGER_BOUND";
    private static final String BINDING_PAGER = "binding";
    private static final String PAGER_BOUND = "bound";

    /**
     * Bind this pager to the target component.
     *
     * @params context the current Facescontext
     * @param dsvar the variable name of target data store.
     * @return true if bound, false otherwise.
     */
    public boolean bind(FacesContext context, String dsvar) {
        Renderer renderer = getRenderer(context);
        if (renderer instanceof ResourceProvider) {
            Map<String,Object> requestMap = context.getExternalContext().getRequestMap();
            String key = PAGER_BOUND_PARAM + getClientId(context);

            // pager can only bind to one target component
            if (!requestMap.containsKey(key)) {
                requestMap.put(key, BINDING_PAGER);
                this.getAttributes().put("_dsvar", dsvar);

                // inform the pager renderer to encode script
                ResourceManager rm = ResourceManager.getInstance(context);
                ((ResourceProvider)renderer).provideResource(rm, this);
                requestMap.put(key, PAGER_BOUND);
                return true;
            }
        }
        return false;
    }

    public boolean isBound() {
        FacesContext context = getFacesContext();
        Map<String,Object> requestMap = context.getExternalContext().getRequestMap();
        return requestMap.containsKey(PAGER_BOUND_PARAM + getClientId(context));
    }

    public String getBindingDataStore() {
        FacesContext context = getFacesContext();
        Map<String,Object> requestMap = context.getExternalContext().getRequestMap();
        Object param = requestMap.get(PAGER_BOUND_PARAM + getClientId(context));
        if (param == BINDING_PAGER) {
            return getTargetDataStore();
        } else {
            return null;
        }
    }

    public String getTargetDataStore() {
        return (String)this.getAttributes().get("_dsvar");
    }

    /**
     * 从记录的第多少行开始显示, default: 0 ;
     */
    private Integer start;

    public int getStart() {
        if (this.start != null) {
            return this.start;
        }
        ValueExpression ve = getValueExpression("startIndex");
        if (ve != null) {
            return (Integer) ve.getValue(getFacesContext().getELContext());
        } else {
            return 0;
        }
    }

    public void setStart(int start) {
        this.start = start;
    }

    /**
     * 每页显示的记录数, default: 20 ;
     */
    private Integer pageSize;

    public int getPageSize() {
        if (this.pageSize != null) {
            return this.pageSize;
        }
        ValueExpression ve = getValueExpression("pageSize");
        if (ve != null) {
            return (Integer) ve.getValue(getFacesContext().getELContext());
        } else {
            return DEFAULT_PAGESIZE;
        }
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
    
    /**
     * 内嵌的风格
     */
    private String theme;
    
    public String getTheme(){
    	if (this.theme != null) {
            return this.theme;
        }
        ValueExpression ve = getValueExpression("theme");
        if (ve != null) {
            return (String) ve.getValue(getFacesContext().getELContext());
        } else {
            return null;
        }
    }
    
    public void setTheme(String theme) {
        this.theme = theme;
    }
    
    public Object saveState(FacesContext context) {
        return new Object[] {
            super.saveState(context),
            for_,
            start,
            pageSize,
            theme
        };
    }

    public void restoreState(FacesContext context, Object state) {
        Object[] values = (Object[])state;
        int i = 0;
        super.restoreState(context, values[i++]);
        for_ = (String)values[i++];
        start = (Integer)values[i++];
        pageSize = (Integer)values[i++];
        theme = (String)values[i++];
    }
}
