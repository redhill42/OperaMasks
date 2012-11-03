/*
 * $Id: TreeHandler.java,v 1.3 2007/12/19 07:44:49 yangdong Exp $
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
package org.operamasks.faces.facelets.widget;

import java.util.List;

import javax.faces.context.FacesContext;

import org.operamasks.faces.component.widget.UITree;
import org.operamasks.faces.component.widget.UITreeNode;

import com.sun.facelets.tag.MetaRuleset;
import com.sun.facelets.tag.MethodRule;
import com.sun.facelets.tag.jsf.ComponentConfig;
import com.sun.facelets.tag.jsf.ComponentHandler;

@SuppressWarnings("unchecked")
public class TreeHandler extends ComponentHandler
{
	private static final Class[] SIGNATURE = { Object.class };
    private static final Class[] INIT_ACTION_SIGNATURE = {
    	FacesContext.class, UITree.class
    };
    
    private static final Class[] POST_CREATE_SIGNATURE = {
    	UITreeNode.class, Object.class
    };

    private static final MethodRule asyncDataRule =
        new MethodRule("asyncData", List.class, SIGNATURE);
    private static final MethodRule nodeTextRule =
        new MethodRule("nodeText", String.class, SIGNATURE);
    private static final MethodRule nodeImageRule =
        new MethodRule("nodeImage", String.class, SIGNATURE);
    private static final MethodRule nodeUserDataRule =
        new MethodRule("nodeUserData", Object.class, SIGNATURE);
    private static final MethodRule nodeHasChildrenRule =
        new MethodRule("nodeHasChildren", Boolean.class, SIGNATURE);
    private static final MethodRule nodeClassRule =
        new MethodRule("nodeClass", Class.class, SIGNATURE);
    private static final MethodRule initActionRule =
        new MethodRule("initAction", Void.class, INIT_ACTION_SIGNATURE);
    
    private static final MethodRule postCreateRule =
        new MethodRule("postCreate", Void.class, POST_CREATE_SIGNATURE);

    public TreeHandler(ComponentConfig config) {
        super(config);
    }

    protected MetaRuleset createMetaRuleset(Class type) {
        MetaRuleset m = super.createMetaRuleset(type);

        m.addRule(asyncDataRule);
        m.addRule(nodeTextRule);
        m.addRule(nodeImageRule);
        m.addRule(nodeUserDataRule);
        m.addRule(nodeHasChildrenRule);
        m.addRule(nodeClassRule);
        m.addRule(initActionRule);
        m.addRule(postCreateRule);

        return m;
    }
}
