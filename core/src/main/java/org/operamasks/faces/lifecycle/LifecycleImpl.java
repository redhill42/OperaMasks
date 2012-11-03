/*
 * $Id: LifecycleImpl.java,v 1.22 2008/03/24 05:21:49 patrick Exp $
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

package org.operamasks.faces.lifecycle;

import static javax.faces.event.PhaseId.ANY_PHASE;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.FacesException;
import javax.faces.application.ViewHandler;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;
import javax.faces.lifecycle.Lifecycle;

import org.operamasks.faces.application.ApplicationAssociate;
import org.operamasks.faces.util.FacesUtils;

public class LifecycleImpl extends Lifecycle
{
    protected static Logger log = Logger.getLogger("org.operamasks.faces.lifecycle");
    
    private ApplicationAssociate associate;

    private List<PhaseListener> phaseListeners;

    private Phase[] phases = new Phase[] {
            new RestoreView(),
            new ApplyRequestValues(),
            new ProcessValidations(),
            new UpdateModelValues(),
            new InvokeApplication()
    };

    private Phase renderResponse = new RenderResponse();

    public LifecycleImpl() {
        phaseListeners = new CopyOnWriteArrayList<PhaseListener>();
    }

    private ApplicationAssociate getAssociate(FacesContext context) {
        if (associate == null)
            associate = ApplicationAssociate.getInstance(context);
        return associate;
    }

    public void execute(FacesContext context) throws FacesException {
        try{
            // Indicate request has been made by this Lifecycle instance
            getAssociate(context).requestMade();

            // 要在request.getParameter之前设置charsetEncoding，保证使用预期的charsetEncoding解析parameters
            // 因为在PhaseListener里面可能会调用到request.getParameter，所以在此处设置
            ViewHandler viewHandler = context.getApplication().getViewHandler();
            viewHandler.initView(context);

            for (Phase phase : phases) {
                if (phase.skipPhase(context))
                    break;
                doPhase(phase, context);
            }
        } catch(FacesException e) {
            //wrap the exception in another FacesException
            //so that FacesServlet can throw the actual exception 
            //with proper message
            throw new FacesException(e);
        }
    }

    public void render(FacesContext context) throws FacesException {
        try {
            if (!renderResponse.skipPhase(context)) {
                doPhase(renderResponse, context);
            }
        } catch (FacesException e) {
            //wrap the exception in another FacesException
            //so that FacesServlet can throw the actual exception 
            //with proper message
            throw new FacesException(e);
        }
    }

    public void addPhaseListener(PhaseListener listener) {
        if (listener == null)
            throw new NullPointerException();
        phaseListeners.add(listener);
    }

    public void removePhaseListener(PhaseListener listener) {
        if (listener == null)
            throw new NullPointerException();
        phaseListeners.remove(listener);
    }

    private static final PhaseListener[] EMPTY_PHASE_LISTENERS = new PhaseListener[0];

    public PhaseListener[] getPhaseListeners() {
        if (phaseListeners.isEmpty())
            return EMPTY_PHASE_LISTENERS;
        return phaseListeners.toArray(EMPTY_PHASE_LISTENERS);
    }

    private void doPhase(Phase phase, FacesContext context) {
        if (log.isLoggable(Level.FINER)) {
            log.finer("Request lifecycle phase '" + phase.getPhaseId() + "' started.");
        }
        //setCurrentPhaseId
        context.getExternalContext().getRequestMap().put(FacesUtils.CURRENT_PHASE_ID, phase.getPhaseId());

        notifyPhaseListeners(context, phase.getPhaseId(), true);
        if (!phase.skipPhase(context)) {
            phase.execute(context);
        }
        notifyPhaseListeners(context, phase.getPhaseId(), false);
    }

    private void notifyPhaseListeners(FacesContext context, PhaseId phaseId, boolean isBefore) {
        PhaseListener[] listeners = getPhaseListeners();
        if (listeners.length > 0) {
            PhaseEvent event = new PhaseEvent(context, phaseId, LifecycleImpl.this);
            for (PhaseListener listener : listeners) {
                if (listener.getPhaseId() == phaseId || listener.getPhaseId() == ANY_PHASE) {
                    if (isBefore) {
                        listener.beforePhase(event);
                    } else {
                        listener.afterPhase(event);
                    }
                }
            }
        }
    }
}
