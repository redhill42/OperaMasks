/*
 * $Id: StateManagerImpl.java,v 1.5 2008/03/09 19:01:48 jacky Exp $
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

import javax.faces.application.StateManager;
import javax.faces.component.UIViewRoot;
import javax.faces.component.UIComponent;
import javax.faces.component.UIComponentBase;
import javax.faces.context.FacesContext;
import javax.faces.render.RenderKit;
import javax.faces.FacesException;
import java.util.HashSet;
import java.util.Set;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.io.IOException;

import org.operamasks.faces.application.StateAware;
import org.operamasks.faces.component.misc.UIStateAware;
import org.operamasks.faces.util.FacesUtils;
import static org.operamasks.resources.Resources.*;

public class StateManagerImpl extends StateManager
{
    private static final String SERVER_VIEW_STATE = "org.operamasks.faces.SERVER_VIEW_STATE";
    private static final String ATTACHED_STATE_PARAM = "org.operamasks.faces.ATTACHED_STATE";

    @Override
    public Object saveView(FacesContext context) {
        if (context.getViewRoot().isTransient()) {
            return null;
        }

        checkIdUniqueness(context);

        Object treeStructure = getTreeStructureToSave(context);
        Object componentState = getComponentStateToSave(context);
        Object attachedState = getAttachedStateToSave(context);

        if (treeStructure == null &&
            componentState == null &&
            attachedState == null) {
            return null;
        }

        Object[] state = new Object[] {
            treeStructure,
            componentState,
            attachedState
        };

        if (isSavingStateInClient(context)) {
            return state;
        } else {
            return saveStateInServer(context, state);
        }
    }

    @Override
    public void writeState(FacesContext context, Object state)
        throws IOException
    {
        String renderKitId = context.getViewRoot().getRenderKitId();
        RenderKit renderKit = FacesUtils.getRenderKit(context, renderKitId);
        if (renderKit == null) {
            throw new FacesException(_T(JSF_NO_SUCH_RENDER_KIT_ID, renderKitId));
        } else {
            renderKit.getResponseStateManager().writeState(context, state);
        }
    }
    
    @SuppressWarnings("unchecked")
    public UIViewRoot restoreView(FacesContext context, String viewId, String renderKitId) {
        UIViewRoot view = context.getViewRoot();

        Object state;
        if (isSavingStateInClient(context)) {
            state = restoreStateFromClient(context, viewId, renderKitId);
        } else {
            state = restoreStateFromServer(context, viewId);
        }

        if (state != null) {
            Object[] stateArray = (Object[])state;
            Object treeStructure = stateArray[0];
            Object componentState = stateArray[1];
            Object attachedState = stateArray[2];

            if (view == null && treeStructure != null) {
                view = (UIViewRoot)restoreTreeStructure((TreeStructure)treeStructure);
            }

            if (view != null) {
                if (treeStructure instanceof Map) {
                    restoreStateAwareTreeStructure(context, view, (Map<String, TreeStructure>) treeStructure);
                }
                
                if (componentState instanceof Map) {
                    restoreOptimizedViewState(context, view, (Map)componentState);
                } else if (componentState != null) {
                    view.processRestoreState(context, componentState);
                }

                if (attachedState != null) {
                    setAttachedStateToRestore(context, attachedState);
                }
            }
        }

        return view;
    }

    private Object restoreStateFromClient(FacesContext context, String viewId, String renderKitId) {
        RenderKit renderKit = context.getRenderKit();
        if (renderKit == null) {
            renderKit = FacesUtils.getRenderKit(context, renderKitId);
            if (renderKit == null)
                throw new FacesException(_T(JSF_NO_SUCH_RENDER_KIT_ID, renderKitId));
        }
        return renderKit.getResponseStateManager().getState(context, viewId);
    }

    @SuppressWarnings("unchecked")
    private Object saveStateInServer(FacesContext context, Object state) {
        String viewId = context.getViewRoot().getViewId();
        Map<String,Object> sessionMap = context.getExternalContext().getSessionMap();
        Map<String,Object> viewMap = (Map<String,Object>)sessionMap.get(SERVER_VIEW_STATE);
        if (viewMap == null) {
            viewMap = new HashMap<String,Object>();
            sessionMap.put(SERVER_VIEW_STATE, viewMap);
        }
        viewMap.put(viewId, state);
        return new Object[] { viewId, null };
    }

    @SuppressWarnings("unchecked")
    private Object restoreStateFromServer(FacesContext context, String viewId) {
        if (context.getExternalContext().getSession(false) == null)
            return null;

        Map<String,Object> sessionMap = context.getExternalContext().getSessionMap();
        Map<String,Object> viewMap = (Map<String,Object>)sessionMap.get(SERVER_VIEW_STATE);
        return (viewMap == null) ? null : viewMap.get(viewId);
    }

    private TreeStructure buildTreeStructure(UIComponent component) {
        TreeStructure struct = new TreeStructure(component);

        for (UIComponent kid : component.getChildren()) {
            if (!kid.isTransient()) {
                TreeStructure childStruct = buildTreeStructure(kid);
                struct.addChild(childStruct);
            }
        }
        for (Map.Entry<String,UIComponent> e : component.getFacets().entrySet()) {
            UIComponent facet = e.getValue();
            if (!facet.isTransient()) {
                TreeStructure facetStruct = buildTreeStructure(facet);
                struct.addFacet(e.getKey(), facetStruct);
            }
        }
        return struct;
    }
    
    private Object buildStateAwareTreeStructure(FacesContext context, UIComponent component) {
        Map<String,TreeStructure> treeStrutureMap = new HashMap<String, TreeStructure>();
        Iterator<UIComponent> it = FacesUtils.createFacetsAndChildrenIterator(context.getViewRoot(), true);
        while (it.hasNext()) {
            UIComponent comp = it.next();
            if (!comp.isTransient()&& (comp instanceof UIStateAware)) {
                treeStrutureMap.put(comp.getClientId(context), buildTreeStructure(comp));
            }
        }
        return treeStrutureMap.isEmpty() ? null : treeStrutureMap;
    }

    private void restoreStateAwareTreeStructure(FacesContext context, UIViewRoot root, Map<String,TreeStructure> treeStrutureMap) {
        Iterator<UIComponent> it = FacesUtils.createFacetsAndChildrenIterator(root, true);
        while (it.hasNext()) {
            UIComponent comp = it.next();
            if (!comp.isTransient()&& (comp instanceof UIStateAware)) {
                TreeStructure struct = treeStrutureMap.get(comp.getClientId(context));
                if (struct != null) {
                    UIComponent newComp = restoreStateAwareTreeStructure(struct);
                    if (newComp != null) {
                        UIComponent parent = comp.getParent();
                        parent.getChildren().remove(comp);
                        parent.getChildren().add(newComp);
                    }
                }
            }
        }
    }


    private UIComponent restoreTreeStructure(TreeStructure struct) {
        UIComponent component = struct.createComponent();

        for (TreeStructure childStruct : struct.getChildren()) {
            UIComponent child = restoreTreeStructure(childStruct);
            component.getChildren().add(child);
        }
        for (Map.Entry<String,TreeStructure> e : struct.getFacets().entrySet()) {
            UIComponent facet = restoreTreeStructure(e.getValue());
            component.getFacets().put(e.getKey(), facet);
        }
        return component;
    }

    private UIComponent restoreStateAwareTreeStructure(TreeStructure struct) {
        UIComponent component = struct.createComponent();
        component.setId(struct.getId());
        for (TreeStructure childStruct : struct.getChildren()) {
            UIComponent child = restoreStateAwareTreeStructure(childStruct);
            component.getChildren().add(child);
        }
        for (Map.Entry<String,TreeStructure> e : struct.getFacets().entrySet()) {
            UIComponent facet = restoreStateAwareTreeStructure(e.getValue());
            component.getFacets().put(e.getKey(), facet);
        }
        return component;
    }

    private void checkIdUniqueness(FacesContext context) {
        Set<String> componentIds = new HashSet<String>();
        Iterator<UIComponent> itr = FacesUtils.createFacetsAndChildrenIterator(context.getViewRoot(), true);
        while (itr.hasNext()) {
            UIComponent component = itr.next();
            String id = component.getClientId(context);
            if (id != null && !componentIds.add(id)) {
                throw new IllegalStateException(_T(JSF_DUPLICATE_COMPONENT_ID, id + "[" + component.getClass().getName() + "]"));
            }
        }
    }

    // Deprecated APIs

    @SuppressWarnings("deprecation")
    public SerializedView saveSerializedView(FacesContext context) {
        Object[] state = (Object[])saveView(context);
        if (state == null)
            return null;
        return new SerializedView(state[0], state[1]);
    }

    @SuppressWarnings("deprecation")
    public void writeState(FacesContext context, SerializedView view)
        throws IOException
    {
        Object[] state = new Object[] { view.getStructure(), view.getState() };
        writeState(context, state);
    }

    @SuppressWarnings("deprecation")
    protected Object getTreeStructureToSave(FacesContext context) {
        UIViewRoot viewRoot = context.getViewRoot();
        if (viewRoot.isTransient() || FacesUtils.isMarkedForTransientState(context))
            return buildStateAwareTreeStructure(context, viewRoot);
        return buildTreeStructure(viewRoot);
    }

    @SuppressWarnings("deprecation")
    protected Object getComponentStateToSave(FacesContext context) {
        if (FacesUtils.isMarkedForTransientState(context)) {
            return saveOptimizedViewState(context);
        } else {
            return context.getViewRoot().processSaveState(context);
        }
    }

    private Object saveOptimizedViewState(FacesContext context) {
        Map<String,Object> stateMap = new HashMap<String, Object>();
        Iterator<UIComponent> it = FacesUtils.createFacetsAndChildrenIterator(context.getViewRoot(), true);
        while (it.hasNext()) {
            UIComponent comp = it.next();
            if (!comp.isTransient()) {
                Object state = null;
                if (comp instanceof StateAware) {
                    state = ((StateAware)comp).saveOptimizedState(context);
                } else if (isStateSaving(comp)) {
                    state = comp.saveState(context);
                }
                if (state != null) {
                    stateMap.put(comp.getClientId(context), state);
                }
            }
        }
        return stateMap.isEmpty() ? null : stateMap;
    }

    private boolean isStateSaving(UIComponent component) {
        UIComponent parent = component.getParent();
        if (parent == null) {
            return false;
        } else if (parent instanceof UIStateAware) {
            return true;
        } else {
            while ((parent = parent.getParent()) != null) {
                if ((parent instanceof UIStateAware) && ((UIStateAware)parent).isDeep()) {
                    return true;
                }
            }
            return false;
        }
    }

    @SuppressWarnings("unchecked")
    private void restoreOptimizedViewState(FacesContext context, UIViewRoot root, Map stateMap) {
        Iterator<UIComponent> it = FacesUtils.createFacetsAndChildrenIterator(root, true);
        while (it.hasNext()) {
            UIComponent comp = it.next();
            if (!comp.isTransient()) {
                Object state = stateMap.get(comp.getClientId(context));
                if (state != null) {
                    if (comp instanceof StateAware) {
                        ((StateAware)comp).restoreOptimizedState(context, state);
                    } else {
                        comp.restoreState(context, state);
                    }
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    protected Object getAttachedStateToSave(FacesContext context) {
        Map stateMap = (Map)context.getExternalContext().getRequestMap().get(ATTACHED_STATE_PARAM);
        if (stateMap == null) {
            return null;
        }

        Object[] stateArray = new Object[stateMap.size() * 2];
        Iterator it = stateMap.entrySet().iterator();
        for (int i = 0; it.hasNext(); i++) {
            Map.Entry e = (Map.Entry)it.next();
            stateArray[i*2] = e.getKey();
            stateArray[i*2+1] = UIComponentBase.saveAttachedState(context, e.getValue());
        }
        return stateArray;
    }

    @SuppressWarnings("unchecked")
    protected void setAttachedStateToRestore(FacesContext context, Object stateObj) {
        Object[] stateArray = (Object[])stateObj;
        Map stateMap = new HashMap(stateArray.length/2);
        for (int i = 0; i < stateArray.length; i += 2) {
            Object key = stateArray[i];
            Object value = UIComponentBase.restoreAttachedState(context, stateArray[i+1]);
            stateMap.put(key, value);
        }

        context.getExternalContext().getRequestMap().put(ATTACHED_STATE_PARAM, stateMap);
    }

    @SuppressWarnings("unchecked")
    public static void saveAttachedState(FacesContext context, Object key, Object value) {
        Map<String,Object> requestMap = context.getExternalContext().getRequestMap();
        Map stateMap = (Map)requestMap.get(ATTACHED_STATE_PARAM);
        if (stateMap == null) {
            stateMap = new HashMap();
            requestMap.put(ATTACHED_STATE_PARAM, stateMap);
        }
        stateMap.put(key, value);
    }

    @SuppressWarnings("unchecked")
    public static Object restoreAttachedState(FacesContext context, Object key) {
        Map<String,Object> requestMap = context.getExternalContext().getRequestMap();
        Map stateMap = (Map)requestMap.get(ATTACHED_STATE_PARAM);
        if (stateMap != null) {
            return stateMap.get(key);
        } else {
            return null;
        }
    }
}
