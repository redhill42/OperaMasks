/*
 * $Id: UpdaterTag.java,v 1.11 2007/12/11 04:20:13 jacky Exp $
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

package org.operamasks.faces.webapp.ajax;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.el.ValueExpression;
import javax.servlet.jsp.JspException;

import org.operamasks.faces.component.ajax.AjaxUpdater;
import org.operamasks.faces.webapp.html.HtmlBasicELTag;
import org.operamasks.faces.render.ajax.AjaxRenderKitImpl;

/**
 * @jsp.tag name="updater" body-content="JSP"
 */
public class UpdaterTag extends HtmlBasicELTag
{
    public String getRendererType() {
        return "org.operamasks.faces.AjaxUpdater";
    }

    public String getComponentType() {
        return AjaxUpdater.COMPONENT_TYPE;
    }
    
    /**
     * @jsp.attribute required="false" type="java.lang.String"
     */
    public void setJsvar(ValueExpression jsvar) {
        setValueExpression("jsvar", jsvar);
    }

    /**
     * @jsp.attribute required="false" type="java.lang.String"
     */
    public void setRenderId(ValueExpression renderId) {
        setValueExpression("renderId", renderId);
    }

    /**
     * @jsp.attribute required="false" type="java.lang.String"
     */
    public void setUrl(ValueExpression url) {
        setValueExpression("url", url);
    }

    /**
     * @jsp.attribute required="false" type="java.lang.String"
     */
    public void setContext(ValueExpression context) {
        setValueExpression("context", context);
    }

    /**
     * @jsp.attribute required="false" type="boolean"
     */
    public void setUpdate(ValueExpression update) {
        setValueExpression("update", update);
    }

    /**
     * @jsp.attribute required="false" type="boolean"
     */
    public void setGlobalAction(ValueExpression globalAction) {
        setValueExpression("globalAction", globalAction);
    }

    /**
     * @jsp.attribute required="false" type="java.lang.String"
     */
    public void setCharEncoding(ValueExpression charEncoding) {
        setValueExpression("charEncoding", charEncoding);
    }
    
    /**
     * @jsp.attribute required="false" type="java.lang.String"
     */
    public void setLayout(ValueExpression layout) {
        setValueExpression("layout", layout);
    }

    /**
     * @jsp.attribute required="false" type="java.lang.String"
     * description="CSS style(s) to be applied when this component is rendered."
     * description_zh_CN="组件渲染时应用到组件的CSS风格，对应于元素的style属性。"
     */
    public void setStyle(ValueExpression style) {
        setValueExpression("style", style);
    }

    /**
     * @jsp.attribute required="false" type="java.lang.String"
     * description="Space-separated list of CSS style class(es) to be applied when
     *   this element is rendered.  This value must be passed through
     *   as the \"class\" attribute on generated markup."
     * description_zh_CN="空格分隔的CSS风格类名列表，渲染时作为元素的\"class\"属性"
     */
    public void setStyleClass(ValueExpression styleClass) {
        setValueExpression("styleClass", styleClass);
    }

    protected void setProperties(UIComponent component) {
        super.setProperties(component);

        if (component.getAttributes().get("layout") == null) {
            component.getAttributes().put("layout", "block");
        }
    }

    public int doStartTag() throws JspException {
        int rc = super.doStartTag();

        FacesContext context = getFacesContext();
        AjaxUpdater component = (AjaxUpdater)getComponentInstance();

        if (component != null) {
            if (!AjaxRenderKitImpl.isAjaxResponse(context)) {
                // clear subview for initial request
                component.getChildren().clear();
            }
            
            if (component.getSubviewId() == null) {
                String url = component.getUrl();
                if (url != null && url.length() != 0) {
                    if (component.getUpdate()) {
                        // the subview must update at first response
                        component.setSubviewId(url);
                        component.setNewView(true);
                    } else if (AjaxRenderKitImpl.isAjaxResponse(context)) {
                        String requestRenderId = AjaxUpdater.getRequestRenderId(context);
                        String renderId = component.getRenderId();
                        if ("*".equals(renderId) || (requestRenderId != null && requestRenderId.equals(renderId))) {
                            // we are requested to update our subview
                            component.setSubviewId(url);
                            component.setNewView(true);
                        }
                    }
                }
            }

            if (component.getSubviewId() != null) {
                // If the AjaxUpdater component already have a subview ID
                // then render the subview instead of component children.
                rc = SKIP_BODY;
            }
        }

        return rc;
    }
}
