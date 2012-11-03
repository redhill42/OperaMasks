/*
 * $Id: AjaxUpdater.java,v 1.15 2008/03/19 03:12:37 lishaochuan Exp $
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

package org.operamasks.faces.component.ajax;

import javax.faces.component.UIComponentBase;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.el.ValueExpression;

import org.operamasks.faces.render.ajax.AjaxRenderKitImpl;
import org.operamasks.faces.binding.ComponentBinder;
import org.operamasks.faces.binding.ModelBindingContext;

public class AjaxUpdater extends UIComponentBase implements ComponentBinder
{
    public static final String RENDER_ID_PARAM = "org.operamasks.faces.RenderId";

    public static final String COMPONENT_FAMILY = "org.operamasks.faces.AjaxUpdater";
    public static final String COMPONENT_TYPE = "org.operamasks.faces.AjaxUpdater";

    private String  jsvar;
    private String  renderId;
    private String  url;
    private String  context;
    private String  subviewId;
    private boolean isNewView;
    private boolean viewRestored;
    private Boolean update;
    private String  charEncoding;
    private String  layout;
    private String  style;
    private String  styleClass;
    private Boolean globalAction;

    public AjaxUpdater() {
        setRendererType("org.operamasks.faces.AjaxUpdater");
    }

    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    /**
     * Get the Javascript variable name that can be used by client script.
     * Client scripts usually call the update() function of this variable
     * to reload subview.
     *
     * @return the Javascript variable name.
     */
    public String getJsvar() {
        if (this.jsvar != null) {
            return this.jsvar;
        }
        ValueExpression ve = getValueExpression("jsvar");
        if (ve != null) {
            return (String)ve.getValue(getFacesContext().getELContext());
        } else {
            return null;
        }
    }

    /**
     * Set the Javascript variable.
     *
     * @param jsvar the Javascript variable.
     */
    public void setJsvar(String jsvar) {
        this.jsvar = jsvar;
    }

    /**
     * Get the render Id of this updater. The render Id is used to identify
     * a group of updaters that updated in a single request/response phase.
     *
     * @return the render Id.
     */
    public String getRenderId() {
        String result = this.renderId;
        if (result == null || result.length() == 0) {
            ValueExpression ve = getValueExpression("renderId");
            if (ve != null) {
                result = (String)ve.getValue(getFacesContext().getELContext());
            }
        }
        if (result == null || result.length() == 0) {
            result = getClientId(getFacesContext());
        }
        return result;
    }

    /**
     * Set the render Id of this updater.
     *
     * @param renderId the render Id.
     */
    public void setRenderId(String renderId) {
        this.renderId = renderId;
    }

    /**
     * Get the render Id from request parameter.
     *
     * @param context The faces context.
     * @return the render Id for current ajax request, or null if the current
     *         request is not a partial render request.
     */
    public static final String getRequestRenderId(FacesContext context) {
        String renderId = null;
        if (AjaxRenderKitImpl.isAjaxResponse(context)) {
            renderId = context.getExternalContext()
                              .getRequestParameterMap()
                              .get(RENDER_ID_PARAM);
            if (renderId != null && renderId.length() == 0) {
                renderId = null;
            }
        }
        return renderId;
    }

    /**
     * Get the initial url to be loaded into the updater. Once the updater
     * is loaded this attribute is no longer used. Invoke the {@link #load)
     * method to reload the updater with new url.
     *
     * @return the initial url.
     */
    public String getUrl() {
        if (this.url != null) {
            return this.url;
        }
        ValueExpression ve = getValueExpression("url");
        if (ve != null) {
            return (String)ve.getValue(getFacesContext().getELContext());
        } else {
            return null;
        }
    }

    /**
     * Set the initial url of this updater.
     *
     * @param url the initial url.
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * Get the context path used to load external resource. This attribute should
     * use with caution that resources from other context must share same managed
     * bean class and navigation rules.
     *
     * @return the external context path.
     */
    public String getContext() {
        if (this.context != null) {
            return this.context;
        }
        ValueExpression ve = getValueExpression("context");
        if (ve != null) {
            return (String)ve.getValue(getFacesContext().getELContext());
        } else {
            return null;
        }
    }

    /**
     * Set the context path used to load external resource.
     *
     * @param context the external context path.
     */
    public void setContext(String context) {
        this.context = context;
    }

    /**
     * If set to true, the updater is loaded automatically for the initial
     * request with url specified with {@link #setUrl} method. Otherwise,
     * the updater is loaded in the subsequent Ajax request.
     *
     * @return whether the updater should load initially.
     */
    public boolean getUpdate() {
        if (this.update != null) {
            return this.update;
        }
        ValueExpression ve = getValueExpression("update");
        if (ve != null) {
            return (Boolean)ve.getValue(getFacesContext().getELContext());
        } else {
            return true; // default is true for most cases
        }
    }

    /**
     * True to load updater initially, false otherwise.
     *
     * @param update whether the updater should load initially.
     */
    public void setUpdate(boolean update) {
        this.update = update;
    }

    /**
     * 是否忽略renderId，而使得updater内的action可影响updater外的组件更新，默认为false
     */
    public boolean getGlobalAction() {
        if (this.globalAction != null) {
            return this.globalAction;
        }
        ValueExpression ve = getValueExpression("globalAction");
        if (ve != null) {
            return (Boolean)ve.getValue(getFacesContext().getELContext());
        } else {
            return true;
        }
    }

    public void setGlobalAction(boolean globalAction) {
        this.globalAction = globalAction;
    }

    /**
     * Get the current subview ID. Subviews can be navigated using navigation
     * rules defined in the faces-config.xml configuration file. The current
     * subview Id is set by NavigationHandler by applying the navigation rules.
     *
     * @return the current subview ID.
     */
    public String getSubviewId() {
        return subviewId;
    }

    /**
     * This method is invoked by NavigationHandler to set current subview ID
     * by applying navigation rules defined in the faces-config.xml configuration
     * file. Application code should not use this method but can use {@link #load}
     * method to load a new subview into updater.
     *
     * @param subviewId the subview Id.
     */
    public void setSubviewId(String subviewId) {
        this.subviewId = fixURL(subviewId);
    }

    /**
     * Used by updater Renderer to determine whether the subview should render
     * with new subview. Application code should not use this method.
     *
     * @return true if the subview should rerender, false otherwise.
     */
    public boolean isNewView() {
        return isNewView;
    }

    /**
     * Set by NavigationHandler to determine that the subview should rerender.
     * Application code should not use this method.
     *
     * @param newView true if the subview should rerender, false otherwise.
     */
    public void setNewView(boolean newView) {
        this.isNewView = newView;
    }

    /**
     * If the subview is restored in the Restore View phase by ViewBuilder then
     * no need to rebuild subview in the Render Response phase.
     */
    public boolean viewRestored() {
        return this.viewRestored;
    }

    /**
     * If the subview is restored in the Restore View phase by ViewBuilder then
     * no need to rebuild subview in the Render Response phase.
     */
    public void viewRestored(boolean viewRestored) {
        this.viewRestored = viewRestored;
    }

    /**
     * Bind sub view components with model.
     */
    public void applyModel(FacesContext ctx, ModelBindingContext mbc) {
        mbc.applyInnerModel(ctx, getSubviewId(), this);
    }

    /**
     * Load new view into this updater. This method usually invoked by application
     * code to manually load a new view. For example, when user clicked a link in the
     * page a new view is loaded with the specified view Id.
     *
     * @param viewId the url to the new view.
     * @throw IllegalArgumentException if the given view Id is null or doesn't starts
     *        with a slash ('/') character.
     */
    public void load(String url) {
        load(url, null);
    }

    /**
     * Load new view into this updater. This method usually invoked by application
     * code to manually load a new view. For example, when user clicked a link in the
     * page a new view is loaded with the specified view Id.
     *
     * @param viewId the url to the new view.
     * @param context path to the other web application context, or null for current
     *        web application context.
     * @throw IllegalArgumentException if the given view Id is null or doesn't starts
     *        with a slash ('/') character.
     */
    public void load(String url, String context) {
        url = fixURL(url);
        if (url == null || !url.startsWith("/")) {
            throw new IllegalArgumentException("Invalid view ID: " + url);
        }
        if (context != null && !context.startsWith("/")) {
            throw new IllegalArgumentException("Invalid context path: " + context);
        }

        getChildren().clear();
        setSubviewId(url);
        setContext(context);
        setNewView(true);
    }

    /**
     * Reload current subview.
     */
    public void reload() {
        setNewView(true);
    }

    /**
     * Unload current subview. The component will render it's default contents.
     */
    public void unload() {
        // clear previously loaded subview
        if (getSubviewId() != null) {
            getChildren().clear();
        }

        // replace with fallback contents
        UIComponent fallback = getFacets().get("fallback");
        if (fallback != null) {
            getChildren().addAll(fallback.getChildren());
            getFacets().remove("fallback");
        }

        setSubviewId(null);
        setNewView(true);
    }

    /**
     * Get the character encoding used to read external resource if the character
     * encoding cannot determined from the resource.
     *
     * @return the character encoding used to read external resource.
     */
    public String getCharEncoding() {
        if (this.charEncoding != null) {
            return this.charEncoding;
        }
        ValueExpression ve = getValueExpression("charEncoding");
        if (ve != null) {
            return (String)ve.getValue(getFacesContext().getELContext());
        } else {
            return null;
        }
    }

    /**
     * Set the character encoding used to read external resource if the character
     * encoding cannot determined from the resource.
     *
     * @param charEncoding the character encoding used to read external resource.
     */
    public void setCharEncoding(String charEncoding) {
        this.charEncoding = charEncoding;
    }

    /**
     * When layout attribute set "block" then the component is rendered
     * as a HTML "div" element, otherwise the component is rendered as
     * a HTML "span" element.
     *
     * @return the layout of the component.
     */
    public String getLayout() {
        if (this.layout != null) {
            return this.layout;
        }
        ValueExpression ve = getValueExpression("layout");
        if (ve != null) {
            return (String)ve.getValue(getFacesContext().getELContext());
        } else {
            return null;
        }
    }

    /**
     * When set layout to "block" then the component is rendered
     * as a HTML "div" element, otherwise the component is rendered
     * as a HTML "span" element.
     *
     * @param layout the layout of the component.
     */
    public void setLayout(String layout) {
        this.layout = layout;
    }

    /**
     * Get the CSS style used to render the component.
     *
     * @return the CSS style.
     */
    public String getStyle() {
        if (this.style != null) {
            return this.style;
        }
        ValueExpression ve = getValueExpression("style");
        if (ve != null) {
            return (String)ve.getValue(getFacesContext().getELContext());
        } else {
            return null;
        }
    }

    /**
     * Set the CSS style used to render the component.
     *
     * @param style the CSS style.
     */
    public void setStyle(String style) {
        this.style = style;
    }

    /**
     * Get the CSS style class used to render the component.
     *
     * @return the CSS style class.
     */
    public String getStyleClass() {
        if (this.styleClass != null) {
            return this.styleClass;
        }
        ValueExpression ve = getValueExpression("styleClass");
        if (ve != null) {
            return (String)ve.getValue(getFacesContext().getELContext());
        } else {
            return null;
        }
    }

    /**
     * Set the CSS style class used to render the component.
     *
     * @param styleClass the CSS style class.
     */
    public void setStyleClass(String styleClass) {
        this.styleClass = styleClass;
    }

    public Object saveState(FacesContext context) {
        return new Object[] {
            super.saveState(context),
            this.jsvar,
            this.renderId,
            this.url,
            this.context,
            this.update,
            this.subviewId,
            this.charEncoding,
            this.layout,
            this.style,
            this.styleClass,
            this.globalAction
        };
    }

    public void restoreState(FacesContext context, Object state) {
        Object[] values = (Object[])state;
        int i = 0;
        super.restoreState(context, values[i++]);
        this.jsvar = (String)values[i++];
        this.renderId = (String)values[i++];
        this.url = (String)values[i++];
        this.context = (String)values[i++];
        this.update = (Boolean)values[i++];
        this.subviewId = (String)values[i++];
        this.charEncoding = (String)values[i++];
        this.layout = (String)values[i++];
        this.style = (String)values[i++];
        this.styleClass = (String)values[i++];
        this.globalAction = (Boolean)values[i++];
        this.viewRestored = true;
    }
    
    private String fixURL(String url) {
        String fixedURL = url;
        if (null != url && !url.startsWith("/")) {
            String viewId = getFacesContext().getViewRoot().getViewId();
            // resolve relative path
            fixedURL = viewId.substring(0, viewId.lastIndexOf('/')+1) + url;
        }
        return fixedURL;
    }
}
