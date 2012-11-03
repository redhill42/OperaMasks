/*
 * $Id: FacesContextImpl.java,v 1.13 2008/01/07 13:59:17 daniel Exp $
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

package org.operamasks.faces.context;

import javax.faces.context.FacesContext;
import javax.faces.context.ExternalContext;
import javax.faces.context.ResponseStream;
import javax.faces.context.ResponseWriter;
import javax.faces.application.Application;
import javax.faces.application.ApplicationFactory;
import javax.faces.application.FacesMessage;
import javax.faces.FactoryFinder;
import javax.faces.component.UIViewRoot;
import javax.faces.render.RenderKit;
import javax.el.ELContext;
import javax.el.ELResolver;
import java.util.List;
import java.util.Map;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Collections;

import org.operamasks.el.eval.ELContextImpl;
import org.operamasks.faces.util.FacesUtils;

public class FacesContextImpl extends FacesContext
{
    private ExternalContext externalContext;
    private Application application;
    private ELContext elContext;
    private UIViewRoot viewRoot;

    private boolean responseComplete;
    private boolean renderResponse;
    private boolean released;

    private ResponseWriter responseWriter;
    private ResponseStream responseStream;
    private int useWriterOrStream = USE_NONE;
    private static final int USE_NONE = 0;
    private static final int USE_STREAM = 1;
    private static final int USE_WRITER = 2;

    private List<FacesMessage> messages = new ArrayList<FacesMessage>();
    private Map<String,List<FacesMessage>> clientMsgMap = new HashMap<String, List<FacesMessage>>();

    FacesContextImpl(ExternalContext externalContext) {
        this.externalContext = externalContext;
        setCurrentInstance(this);
    }

    public Application getApplication() {
        assertNotReleased();
        if (this.application == null) {
            ApplicationFactory factory = (ApplicationFactory)
                FactoryFinder.getFactory(FactoryFinder.APPLICATION_FACTORY);
            this.application = factory.getApplication();
        }
        return this.application;
    }

    public ExternalContext getExternalContext() {
        assertNotReleased();
        return this.externalContext;
    }

    public ELContext getELContext() {
        assertNotReleased();

        if (elContext == null) {
            elContext = new FacesELContext(getApplication().getELResolver());
        }
        return elContext;
    }

    private static class FacesELContext extends ELContextImpl {
        FacesELContext(ELResolver resolver) {
            super(resolver);
        }

        public Object getContext(Class key) {
            if (key == FacesContext.class) {
                return FacesContext.getCurrentInstance();
            } else {
                return super.getContext(key);
            }
        }
    }

    public RenderKit getRenderKit() {
        assertNotReleased();
        if (this.viewRoot != null) {
            String renderKitId = this.viewRoot.getRenderKitId();
            if (renderKitId != null) {
                return FacesUtils.getRenderKit(this, renderKitId);
            }
        }
        return null;
    }

    public void renderResponse() {
        assertNotReleased();
        this.renderResponse = true;
    }

    public boolean getRenderResponse() {
        assertNotReleased();
        return this.renderResponse;
    }

    public void responseComplete() {
        assertNotReleased();
        this.responseComplete = true;
    }

    public boolean getResponseComplete() {
        assertNotReleased();
        return this.responseComplete;
    }

    public ResponseStream getResponseStream() {
        assertNotReleased();
        if (this.useWriterOrStream == USE_WRITER)
            throw new IllegalStateException("Response writer is in use");
        if (this.responseStream != null)
            this.useWriterOrStream = USE_STREAM;
        return this.responseStream;
    }

    public void setResponseStream(ResponseStream stream) {
        assertNotReleased();
        assertNotNull(stream);
        this.responseStream = stream;
    }

    public ResponseWriter getResponseWriter() {
        assertNotReleased();
        if (this.useWriterOrStream == USE_STREAM)
            throw new IllegalStateException("Response stream is in use");
        if (this.responseWriter != null)
            this.useWriterOrStream = USE_WRITER;
        return this.responseWriter;
    }

    public void setResponseWriter(ResponseWriter writer) {
        assertNotReleased();
        assertNotNull(writer);
        this.responseWriter = writer;
    }

    public UIViewRoot getViewRoot() {
        assertNotReleased();
        return this.viewRoot;
    }

    public void setViewRoot(UIViewRoot root) {
        assertNotReleased();
        assertNotNull(root);
        this.viewRoot = root;
    }

    public void addMessage(String clientId, FacesMessage message) {
        assertNotReleased();
        assertNotNull(message);

        String summary = message.getSummary();
        String detail = message.getDetail();
        if (summary != null && FacesUtils.isValueExpression(summary)) {
            summary = FacesUtils.evaluateExpressionGet(summary, String.class);
            message.setSummary(summary);
        }
        if (detail != null && FacesUtils.isValueExpression(detail)) {
            detail = FacesUtils.evaluateExpressionGet(detail, String.class);
            message.setDetail(detail);
        }

        List<FacesMessage> clientMsgs = this.clientMsgMap.get(clientId);
        if (clientMsgs == null) {
            clientMsgs = new ArrayList<FacesMessage>();
            this.clientMsgMap.put(clientId, clientMsgs);
        }
        clientMsgs.add(message);
        this.messages.add(message);
    }

    public Iterator<FacesMessage> getMessages() {
        assertNotReleased();
        return this.messages.iterator();
    }

    public Iterator<FacesMessage> getMessages(String clientId) {
        assertNotReleased();
        List<FacesMessage> clientMsgs = this.clientMsgMap.get(clientId);
        if (clientMsgs == null) {
            clientMsgs = Collections.emptyList();
        }
        return clientMsgs.iterator();
    }

    public Iterator<String> getClientIdsWithMessages() {
        assertNotReleased();
        return this.clientMsgMap.keySet().iterator();
    }

    public FacesMessage.Severity getMaximumSeverity() {
        assertNotReleased();
        FacesMessage.Severity severity = null;
        for (FacesMessage msg : this.messages) {
            if (severity == null) {
                severity = msg.getSeverity();
            } else if (severity.compareTo(msg.getSeverity()) < 0) {
                severity = msg.getSeverity();
            }
        }
        return severity;
    }

    public void release() {
        this.released = true;
        setCurrentInstance(null);
    }

    private void assertNotReleased() {
        if (this.released) {
            throw new IllegalStateException("FacesContext released");
        }
    }

    private void assertNotNull(Object obj) {
        if (obj == null) {
            throw new NullPointerException();
        }
    }
}
