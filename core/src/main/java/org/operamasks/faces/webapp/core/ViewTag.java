/*
 * $Id: ViewTag.java,v 1.22 2008/04/21 07:39:49 lishaochuan Exp $
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

package org.operamasks.faces.webapp.core;

import javax.faces.webapp.UIComponentELTag;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.component.UIViewRoot;
import javax.faces.component.UIComponent;
import javax.faces.component.UIComponentBase;
import javax.faces.render.RenderKitFactory;
import javax.faces.render.RenderKit;
import javax.faces.application.ViewHandler;
import javax.faces.event.PhaseId;
import javax.el.ValueExpression;
import javax.el.MethodExpression;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.jstl.core.Config;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletContext;
import java.io.IOException;
import java.util.Locale;
import org.operamasks.faces.util.FacesUtils;
import org.operamasks.faces.render.ajax.AjaxRenderKitImpl;
import org.operamasks.faces.render.delegate.ViewDelegateManager;
import org.operamasks.faces.application.ApplicationAssociate;
import org.operamasks.faces.binding.ModelBindingFactory;

public class ViewTag extends UIComponentELTag
{
    protected ValueExpression renderKitId;
    protected ValueExpression locale;
    protected MethodExpression beforePhase;
    protected MethodExpression afterPhase;

    public void setRenderKitId(ValueExpression renderKitId) {
        this.renderKitId = renderKitId;
    }

    public void setLocale(ValueExpression locale) {
        this.locale = locale;
    }

    public void setBeforePhase(MethodExpression beforePhase) {
        this.beforePhase = beforePhase;
    }

    public void setAfterPhase(MethodExpression afterPhase) {
        this.afterPhase = afterPhase;
    }

    @Override
    public void setJspId(String id) {
        try {
            super.setJspId(id);
        } catch (RuntimeException ex) {
            if (FacesContext.getCurrentInstance() == null) {
                // Ignore it, this can happen for non JSF request. We will
                // redirect request to FacesServlet later.
            } else {
                throw ex;
            }
        }
    }

    private boolean redirected = false;

    public int doStartTag() throws JspException {
        FacesContext context = FacesContext.getCurrentInstance();
        if (context == null) {
            // redirect to faces servlet for non faces request
            redirectToFacesServlet();
            redirected = true;
            return SKIP_BODY;
        }

        FacesUtils.beginView(context);

        int rc = super.doStartTag();
        pageContext.getResponse().setLocale(context.getViewRoot().getLocale());
        return rc;
    }

    public int doEndTag() throws JspException {
        if (redirected) {
            return SKIP_PAGE;
        }

        int rc = super.doEndTag();

        FacesContext context = getFacesContext();
        boolean nested = FacesUtils.endView(context);
        if (nested) {
            return rc; // Don't render view if current view is nested in another view.
        }

        // apply model bindings before render response.
        ModelBindingFactory.applyModelBindings(context, PhaseId.RENDER_RESPONSE);

        // Save character encoding
        HttpSession session = pageContext.getSession();
        if (session != null) {
            String charset = pageContext.getResponse().getCharacterEncoding();
            session.setAttribute(ViewHandler.CHARACTER_ENCODING_KEY, charset);
        }

        // Render the view
        if (!AjaxRenderKitImpl.isAjaxResponse(context)) {
            UIViewRoot viewToRender = context.getViewRoot();
            ResponseWriter oldWriter = context.getResponseWriter();

            try {
                // Create appropriate response writer.
                ResponseWriter writer;
                if (oldWriter != null) {
                    writer = oldWriter.cloneWithWriter(pageContext.getOut());
                } else {
                    RenderKit renderKit = context.getRenderKit();
                    writer = renderKit.createResponseWriter(pageContext.getOut(), null, null);
                }

                context.setResponseWriter(writer);

                // Delegate current view to registered view delegaters.
                ViewDelegateManager vdm = ViewDelegateManager.getInstance(context);
                if (vdm != null) {
                    vdm.processViewDelegates(context);
                }

                // Render the view only if the view doesn't delegated.
                if (!context.getResponseComplete()) {
                    writer.startDocument();
                    viewToRender.encodeAll(context);
                    writer.endDocument();
                }
            } catch (IOException ex) {
                throw new JspException(ex);
            } finally {
                if (oldWriter != null)
                    context.setResponseWriter(oldWriter);
            }
        }

        return rc;
    }

    private void redirectToFacesServlet() throws JspException {
        ServletContext context = pageContext.getServletContext();
        HttpServletRequest request = (HttpServletRequest)pageContext.getRequest();
        HttpServletResponse response = (HttpServletResponse)pageContext.getResponse();

        ApplicationAssociate associate = ApplicationAssociate.getInstance(context);
        if (associate == null) {
            throw new JspException("Cannot find FacesServlet mapping");
        }

        String path = request.getServletPath();
        String facesPath = null;

        for (String mapping : associate.getFacesMappings()) {
            char c = mapping.charAt(0);
            if (c == '/') {
                // for prefix mapping
                facesPath = mapping.concat(path);
            } else if (c == '.') {
                // for suffix mapping
                int period = path.lastIndexOf('.');
                if (period == -1) {
                    facesPath = path.concat(mapping);
                } else if (!path.endsWith(mapping)) {
                    facesPath = path.substring(0, period).concat(mapping);
                }
            }
            if (facesPath != null) {
                break;
            }
        }
        if (facesPath == null) {
            throw new JspException("Cannot find FacesServlet mapping");
        }

        try {
            facesPath = context.getContextPath().concat(facesPath);
            response.sendRedirect(facesPath);
        } catch (Exception ex) {
            throw new JspException(ex);
        }
    }

    public String getComponentType() {
        return null;
    }

    public String getRendererType() {
        return null;
    }
    
    @Override
    protected UIComponent createComponent(FacesContext context, String newId)
        throws JspException
    {
        if (FacesUtils.strict()) {
            throw new JspTagException("Nested view is not allowed");
        }

        // create passthrough component to store child components.
        UIComponent passthrough = new Passthrough();
        passthrough.setId(newId);
        return passthrough;
    }

    protected void setProperties(UIComponent component) {
        super.setProperties(component);

        FacesContext context = FacesContext.getCurrentInstance();
        UIViewRoot viewRoot = context.getViewRoot();

        if (renderKitId != null) {
            viewRoot.setValueExpression("renderKitId", renderKitId);
        } else if (viewRoot.getRenderKitId() == null) {
            String defaultRenderKitId = context.getApplication().getDefaultRenderKitId();
            if (defaultRenderKitId == null)
                defaultRenderKitId = RenderKitFactory.HTML_BASIC_RENDER_KIT;
            viewRoot.setRenderKitId(defaultRenderKitId);
        }

        if (locale != null) {
            Locale loc = FacesUtils.getLocaleFromExpression(context, locale);
            if (loc != null) {
                viewRoot.setLocale(loc);
                Config.set(pageContext.getRequest(), Config.FMT_LOCALE, loc);
            }
        }

        if (beforePhase != null)
            viewRoot.setBeforePhaseListener(beforePhase);
        if (afterPhase != null)
            viewRoot.setAfterPhaseListener(afterPhase);
    }

    public static final class Passthrough extends UIComponentBase {
        public String getFamily() {
            return "org.operamasks.faces.Passthrough";
        }
    }
}
