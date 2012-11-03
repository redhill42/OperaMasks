/*
 * $Id: FaceletTag.java,v 1.13 2007/07/02 07:38:12 jacky Exp $
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

package org.operamasks.faces.webapp.layout;

import javax.faces.component.UIComponent;
import javax.faces.FacesException;
import javax.el.ValueExpression;
import javax.el.ELContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.ServletException;
import java.io.IOException;

import org.operamasks.faces.layout.LayoutContext;
import org.operamasks.faces.layout.LayoutManager;
import org.operamasks.faces.layout.Facelet;
import org.operamasks.faces.component.layout.UIFacelet;
import org.operamasks.faces.component.layout.UIFaceletSet;
import org.operamasks.faces.webapp.html.HtmlBasicELTag;
import org.operamasks.util.Utils;

/**
 * @jsp.tag name="facelet" body-content="JSP"
 */
public class FaceletTag extends HtmlBasicELTag
{
    private ValueExpression src;
    private ValueExpression srcClass;

    public String getRendererType() {
        return null;
    }

    public String getComponentType() {
        return UIFacelet.COMPONENT_TYPE;
    }

    /**
     * @jsp.attribute required="false" type="java.lang.String"
     */
    public void setName(ValueExpression name) {
        setValueExpression("name", name);
    }

    /**
     * @jsp.attribute required="false" type="java.lang.Object"
     */
    public void setConstraints(ValueExpression constraints) {
        setValueExpression("constraints", constraints);
    }

    /**
     * @jsp.attribute required="false" type="java.lang.Object"
     */
    public void setSrc(ValueExpression src) {
        this.src = src;
    }

    /**
     * @jsp.attribute required="false" type="java.lang.String"
     * @param srcClass
     */
    public void setSrcClass(ValueExpression srcClass) {
        this.srcClass = srcClass;
    }

    protected void setProperties(UIComponent component) {
        super.setProperties(component);

        ELContext elContext = getFacesContext().getELContext();
        Facelet delegate = null;
        String uri = null;

        if (this.src != null) {
            Object value = src.getValue(elContext);
            if (value instanceof Facelet) {
                delegate = (Facelet)value;
            } else if (value instanceof String) {
                uri = (String)value;
            }
        }
        
        if (delegate == null && srcClass != null) {
            try {
                String className = (String)srcClass.getValue(elContext);
                Class clazz = Utils.findClass(className);
                delegate = (Facelet)clazz.newInstance();
            } catch (Exception ex) {
                throw new FacesException(ex);
            }
        }

        UIFacelet facelet = (UIFacelet)component;
        facelet.setDelegate(delegate);
        facelet.setUri(uri);
    }

    public int doStartTag() throws JspException {
        int rc = super.doStartTag();

        UIFacelet facelet = (UIFacelet)this.getComponentInstance();
        UIComponent parent = facelet.getParent();

        // Add this Facelet to enclosing layout manager or facelet-set
        if (parent instanceof LayoutManager) {
            ((LayoutManager)parent).getFacelets().add(facelet);
        } else if (parent instanceof UIFaceletSet) {
            ((UIFaceletSet)parent).getFacelets().add(facelet);
        }

        String uri = facelet.getUri();
        if (uri != null) {
            if (uri.equals("#inherit")) {
                // get inherited facelet from layout manager hieracy
                Facelet delegate = getInheritDelegate(facelet.getName());
                if (delegate != null) {
                    facelet.setDelegate(delegate);
                    rc = SKIP_BODY;
                }
            } else {
                // Include "src" as facelet content
                try {
                    BodyContent out = pageContext.pushBody();
                    setBodyContent(out);
                    doInitBody();
                    pageContext.include(uri, true);
                    doAfterBody();
                    pageContext.popBody();
                } catch (ServletException ex) {
                    throw new JspException(ex);
                } catch (IOException ex) {
                    throw new JspException(ex);
                }
                rc = SKIP_BODY;
            }
        }

        return rc;
    }

    private Facelet getInheritDelegate(String name) {
        if (name != null && name.length() != 0) {
            LayoutContext lctx = LayoutContext.getCurrentInstance();
            while (lctx != null) {
                for (Facelet facelet : lctx.getLayoutManager().getFacelets()) {
                    if (name.equals(facelet.getName())) {
                        return facelet;
                    }
                }
                lctx = lctx.getParent();
            }
        }
        return null;
    }

    public void release() {
        super.release();
        src = null;
        srcClass = null;
    }
}
