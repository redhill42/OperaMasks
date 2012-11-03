/*
 * $Id: PhaseMonitorListener.java,v 1.5 2008/01/29 11:09:02 yangdong Exp $
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

package org.operamasks.faces.render.widget;

import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;

import org.operamasks.faces.debug.Debug;
import org.operamasks.faces.debug.DebugMode;
import org.operamasks.faces.render.ajax.AjaxRenderKitImpl;

@SuppressWarnings("serial")
public class PhaseMonitorListener implements PhaseListener {
	public static final String PHASE_ID = "org.operamasks.faces.render.widget.PhaseMonitor.phaseId";
	private static final String KEY_BEFORE_PHASE = "BEFORE_PHASE";
	private static final String KEY_AFTER_PHASE = "AFTER_PHASE";
	private static final String KEY_MISC_INFO = "MISC_INFO";
	private static final String KEY_REQUEST_START_TIME = "org.operamasks.faces.RequestStartTime";
	private static final String KEY_VALIDATION_ERROR = "VALIDATION_ERROR";
	
	public void afterPhase(PhaseEvent event) {
		FacesContext context = event.getFacesContext();
        Logger logger = Debug.getLogger();
        
		if (event.getPhaseId() == PhaseId.PROCESS_VALIDATIONS &&
					Debug.isEnabled(DebugMode.EXCEPTION)) {
			FacesContext facesContext = FacesContext.getCurrentInstance();
			Iterator<FacesMessage> iter = facesContext.getMessages();
			if (iter != null && iter.hasNext()) {
				while (iter.hasNext()) {
					FacesMessage message = iter.next();
					Debug.getLogger().log(Level.INFO, KEY_VALIDATION_ERROR,
							new Object[] {
								message.getSeverity(),
								message.getSummary(),
								message.getDetail()
					});
				}				
			}
		}
        
        if (event.getPhaseId() == PhaseId.RENDER_RESPONSE &&
        		Debug.isEnabled(context, DebugMode.MISC)) {
        	logger.log(Level.INFO, KEY_MISC_INFO,
        			new Object[] {
        					context.getViewRoot().getViewId(),
        					getResponseMode(context),
        					getRenderKitId(context),
        					getResponseTime(context)
        			}
        	);
        }
        
        if (isDebugPhase(event)) {
        	logger.log(Level.INFO, KEY_AFTER_PHASE, event.getPhaseId());
        }
	}

	private Object getResponseTime(FacesContext context) {
		long startTime = (Long)context.getExternalContext().getRequestMap(
				).get(KEY_REQUEST_START_TIME);
		
		return System.currentTimeMillis() - startTime;
	}

	private boolean isDebugPhase(PhaseEvent event) {
		FacesContext context = event.getFacesContext();
		DebugMode phaseDebugMode = Debug.DEBUG_MODE_LIFECYCLE_PHASES[event.getPhaseId().getOrdinal() - 1];

		return Debug.isEnabled(context, phaseDebugMode);
	}

	private String getResponseMode(FacesContext context) {
		String responseMode = "HTML Response";
		if (AjaxRenderKitImpl.isAjaxResponse(context)) {
			responseMode = "AJAX Response";
		}
		return responseMode;
	}

	private String getRenderKitId(FacesContext context) {
		String renderKitId = context.getViewRoot().getRenderKitId();
		if (renderKitId == null)
			renderKitId = context.getApplication().getDefaultRenderKitId();
		if (renderKitId == null)
			renderKitId = "HTML_BASIC";
		return renderKitId;
	}

	public void beforePhase(PhaseEvent event) {
		FacesContext context = event.getFacesContext();
        Logger logger = Debug.getLogger();
        
        if (isDebugPhase(event)) {
        	logger.log(Level.INFO, KEY_BEFORE_PHASE, event.getPhaseId());
        }
        
        context.getExternalContext().getRequestMap().put(PHASE_ID, event.getPhaseId());
        
        if (event.getPhaseId() == PhaseId.RESTORE_VIEW) {
        	long startTime = System.currentTimeMillis();
        	context.getExternalContext().getRequestMap().put(KEY_REQUEST_START_TIME, startTime);
        }
	}

	public PhaseId getPhaseId() {
		return PhaseId.ANY_PHASE;
	}

}
