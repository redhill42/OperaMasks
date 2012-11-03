/*
 * $Id: BootstrapFacesContext.java,v 1.2 2007/12/07 20:21:47 daniel Exp $
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

package org.operamasks.faces.config;

import java.util.Iterator;
import java.util.Collections;
import javax.faces.context.FacesContext;
import javax.faces.context.ExternalContext;
import javax.faces.context.ResponseStream;
import javax.faces.context.ResponseWriter;
import javax.faces.application.Application;
import javax.faces.application.FacesMessage;
import javax.faces.application.ApplicationFactory;
import javax.faces.render.RenderKit;
import javax.faces.component.UIViewRoot;
import javax.faces.FactoryFinder;
import javax.el.ELContext;
import javax.servlet.ServletContext;
import org.operamasks.el.eval.ELEngine;

public class BootstrapFacesContext extends FacesContext
{
    private Application application;
    private ExternalContext externalContext;
    private ELContext elContext;

    public BootstrapFacesContext(ServletContext context) {
        setCurrentInstance(this);
        this.externalContext = new BootstrapExternalContext(context);
    }

    public void release() {
        setCurrentInstance(null);
    }

    public Application getApplication() {
        if (this.application == null) {
            ApplicationFactory factory = (ApplicationFactory)
                FactoryFinder.getFactory(FactoryFinder.APPLICATION_FACTORY);
            this.application = factory.getApplication();
        }

        return this.application;
    }

    public ExternalContext getExternalContext() {
        return this.externalContext;
    }

    public void addMessage(String clientId, FacesMessage message) {
        // do nothing
    }

    public FacesMessage.Severity getMaximumSeverity() {
        return null;
    }

    @SuppressWarnings("unchecked")
    public Iterator<FacesMessage> getMessages() {
        return Collections.EMPTY_SET.iterator();
    }

    @SuppressWarnings("unchecked")
    public Iterator<FacesMessage> getMessages(String clientId) {
        return Collections.EMPTY_SET.iterator();
    }

    @SuppressWarnings("unchecked")
    public Iterator<String> getClientIdsWithMessages() {
        return Collections.EMPTY_SET.iterator();
    }

    public void renderResponse() {
        // do nothing
    }

    public void responseComplete() {
        // do nothing
    }

    public boolean getRenderResponse() {
        return false;
    }

    public boolean getResponseComplete() {
        return false;
    }

    public ResponseStream getResponseStream() {
        return null;
    }

    public void setResponseStream(ResponseStream responseStream) {
        // do nothing
    }

    public ResponseWriter getResponseWriter() {
        return null;
    }

    public void setResponseWriter(ResponseWriter responseWriter) {
        // do nothing
    }

    public UIViewRoot getViewRoot() {
        return null;
    }

    public void setViewRoot(UIViewRoot root) {
        // do nothing
    }

    public RenderKit getRenderKit() {
        return null;
    }

    public ELContext getELContext() {
        if (this.elContext == null) {
            ELContext elctx = ELEngine.createELContext(getApplication().getELResolver());
            elctx.putContext(FacesContext.class, this);
            this.elContext = elctx;
        }

        return this.elContext;
    }
}
