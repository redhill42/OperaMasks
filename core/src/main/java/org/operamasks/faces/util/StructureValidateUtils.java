/*
 * $Id:
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

package org.operamasks.faces.util;

import static org.operamasks.resources.Resources.UI_MISSING_PAGE_PARENT;
import static org.operamasks.resources.Resources._T;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;

import org.operamasks.faces.component.html.HtmlHead;
import org.operamasks.faces.component.html.HtmlPage;
import org.operamasks.faces.component.misc.UIHiddenState;
import org.operamasks.faces.component.misc.UIUseBean;

/**
 * A utility class that helps validate component tree structure 
 * @author patrick
 */
public class StructureValidateUtils {
    private static Logger logger = Logger.getLogger("org.operamasks.faces.view");
    
    /**
     * 由于无法修改API，此方法负责校验涉及API类的组件树结构 
     */
    public static void validate(FacesContext context) {
        UIViewRoot root = context.getViewRoot();
        validateRootElements(root);
    }
    
    /**
     * Validate if the UIViewRoot elements contains only structural components.
     */
    private static void validateRootElements(UIViewRoot root) {
        
        UIComponent unexpectedChild = pickUnexpectedChildByClass(root, 
                HtmlPage.class, HtmlHead.class, UIUseBean.class, UIHiddenState.class);
        if (unexpectedChild != null) {
            if (logger.isLoggable(Level.FINE)) {
                logger.fine(_T(UI_MISSING_PAGE_PARENT, FacesUtils.getComponentDesc(unexpectedChild)));
            }
        }
    }
    
    /**
     * validate if the parent component contains only child components which are instances of a given class list.
     * @param parent
     * @param childrenClasses
     * @return null if all children are expected.
     */
    public static UIComponent pickUnexpectedChildByClass(UIComponent parent, Class<?>... childrenClasses) {
        boolean pass;
        for (UIComponent child : parent.getChildren()) {
            pass = false;
            for (Class<?> childClass : childrenClasses) {
                if (childClass.isAssignableFrom(child.getClass()) || isLiteralText(child)) {
                    pass = true;
                    break;
                }
            }
            if (!pass) {
                return child;
            }
        }
        return null;
    }

    public static boolean isLiteralText(UIComponent component) {
        return  "facelets.LiteralText".equals(component.getFamily());
    }
}

