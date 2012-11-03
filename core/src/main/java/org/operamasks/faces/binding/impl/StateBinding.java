/*
 * $Id: StateBinding.java,v 1.1 2007/12/21 03:00:24 daniel Exp $
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

import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.util.Arrays;
import javax.faces.context.FacesContext;
import javax.faces.context.ExternalContext;
import javax.faces.component.UIViewRoot;
import javax.faces.component.UIComponent;
import javax.faces.event.PhaseId;
import javax.faces.FacesException;
import javax.el.ValueExpression;

import org.operamasks.faces.binding.ModelBindingContext;
import org.operamasks.faces.component.misc.UIHiddenState;
import org.operamasks.faces.application.impl.StateManagerImpl;

abstract class StateBinding extends Binding
{
    private String key;
    private Class<?> type;
    private boolean inServer;

    private static final String HIDDEN_STATE_ID = UIViewRoot.UNIQUE_ID_PREFIX + "_savestate";
    private static final String HIDDEN_STATE_PREFIX = UIViewRoot.UNIQUE_ID_PREFIX + "_state_";

    private static final String SERVER_STATE_KEY = "org.operamasks.faces.SERVER_MODEL_STATE";

    StateBinding(String viewId, String key, Class<?> type, boolean inServer) {
        super(viewId);
        this.key = key;
        this.type = (type == null) ? Object.class : type;
        this.inServer = inServer;
    }

    public boolean isSavingStateInServer(FacesContext ctx) {
        return this.inServer || !ctx.getApplication().getStateManager().isSavingStateInClient(ctx);
    }

    protected abstract Object getStateValue(FacesContext ctx, ModelBindingContext mbc);
    protected abstract void setStateValue(FacesContext ctx, ModelBindingContext mbc, Object value);
    protected abstract ValueExpression createValueAdapter(FacesContext ctx, ModelBindingContext mbc);

    public void applyGlobal(FacesContext ctx, ModelBindingContext mbc) {
        if (isSavingStateInServer(ctx)) {
            applyServerState(ctx, mbc);
        } else if (isEncodable(this.type)) {
            applyClientState(ctx, mbc);
        } else {
            applyAttachedState(ctx, mbc);
        }
    }

    @SuppressWarnings("unchecked")
    private void applyServerState(FacesContext ctx, ModelBindingContext mbc) {
        PhaseId phaseId = mbc.getPhaseId();

        try {
            if (phaseId == PhaseId.RESTORE_VIEW) {
                Map stateMap = getServerStateMap(ctx, false);
                if (stateMap != null) {
                    Object value = stateMap.get(this.key);
                    if (value != null) {
                        setStateValue(ctx, mbc, value);
                        stateMap.remove(this.key);
                        if (stateMap.isEmpty()) {
                            removeServerStateMap(ctx);
                        }
                    }
                }
            } else if (phaseId == PhaseId.RENDER_RESPONSE) {
                Object value = getStateValue(ctx, mbc);
                if (value != null) {
                    Map stateMap = getServerStateMap(ctx, true);
                    stateMap.put(this.key, value);
                } else {
                    Map stateMap = getServerStateMap(ctx, false);
                    if (stateMap != null) {
                        stateMap.remove(this.key);
                    }
                }
            }
        } catch (Exception ex) {
            throw new FacesException(ex);
        }
    }

    @SuppressWarnings("unchecked")
    private Map getServerStateMap(FacesContext ctx, boolean create) {
        ExternalContext ext = ctx.getExternalContext();
        if (ext.getSession(create) == null) {
            return null;
        }

        Map<String,Object> sessionMap = ext.getSessionMap();
        Map stateMap = (ConcurrentMap)sessionMap.get(SERVER_STATE_KEY);
        if (stateMap == null && create) {
            stateMap = new ConcurrentHashMap();
            sessionMap.put(SERVER_STATE_KEY, stateMap);
        }
        return stateMap;
    }

    private void removeServerStateMap(FacesContext ctx) {
        ExternalContext ext = ctx.getExternalContext();
        if (ext.getSession(false) != null) {
            Map<String,Object> sessionMap = ext.getSessionMap();
            sessionMap.remove(SERVER_STATE_KEY);
        }
    }

    private void applyClientState(FacesContext ctx, ModelBindingContext mbc) {
        PhaseId phaseId = mbc.getPhaseId();

        UIHiddenState hidden = null;
        for (UIComponent kid : ctx.getViewRoot().getChildren()) {
            if (kid instanceof UIHiddenState) {
                hidden = (UIHiddenState)kid;
                break;
            }
        }

        if (hidden == null) {
            hidden = new UIHiddenState();
            hidden.setId(HIDDEN_STATE_ID);
            hidden.setPhaseId(phaseId);
            hidden.setTransient(true);
            ctx.getViewRoot().getChildren().add(0, hidden);
        } else if (phaseId != hidden.getPhaseId()) {
            hidden.clearAttachedStates();
            hidden.setPhaseId(phaseId);
        }

        String name = HIDDEN_STATE_PREFIX + this.key;
        ValueExpression binding = createValueAdapter(ctx, mbc);
        hidden.addAttachedState(name, binding);
    }

    private void applyAttachedState(FacesContext ctx, ModelBindingContext mbc) {
        PhaseId phaseId = mbc.getPhaseId();

        try {
            if (phaseId == PhaseId.RESTORE_VIEW) {
                Object value = StateManagerImpl.restoreAttachedState(ctx, this.key);
                if (value != null) {
                    setStateValue(ctx, mbc, value);
                }
            } else if (phaseId == PhaseId.RENDER_RESPONSE) {
                Object value = getStateValue(ctx, mbc);
                if (value != null) {
                    StateManagerImpl.saveAttachedState(ctx, this.key, value);
                }
            }
        } catch (Exception ex) {
            throw new FacesException(ex);
        }
    }

    private static final String[] encodableTypes = {
        "byte", "char", "short", "int", "long", "float", "double",
        "java.lang.Byte",
        "java.lang.Character",
        "java.lang.Short",
        "java.lang.Integer",
        "java.lang.Long",
        "java.lang.Float",
        "java.lang.Double",
        "java.math.BigInteger",
        "java.math.BigDecimal",
        "java.util.Date",
        "java.lang.String"
    };

    static {
        Arrays.sort(encodableTypes);
    }

    private static boolean isEncodable(Class<?> type) {
        if (type == null) {
            return false;
        } else {
            return Arrays.binarySearch(encodableTypes, type.getName()) >= 0;
        }
    }
}
