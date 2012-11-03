/*
 * $Id: LayoutContext.java,v 1.4 2007/07/02 07:38:12 jacky Exp $
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

package org.operamasks.faces.layout;

import javax.faces.context.FacesContext;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;

public class LayoutContext
{
    private static final String LAYOUT_CONTEXT_STACK_ATTR =
        "org.operamasks.faces.layout.LAYOUT_CONTEXT_STACK";

    public static LayoutContext getCurrentInstance() {
        FacesContext context = FacesContext.getCurrentInstance();
        return (context == null) ? null : getCurrentInstance(context);
    }

    @SuppressWarnings("unchecked")
    public static LayoutContext getCurrentInstance(FacesContext context) {
        Map<String,Object> requestMap = context.getExternalContext().getRequestMap();
        List<LayoutContext> stack = (List<LayoutContext>)requestMap.get(LAYOUT_CONTEXT_STACK_ATTR);
        if (stack != null && !stack.isEmpty()) {
            return stack.get(stack.size() - 1);
        } else {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public static void pushLayoutContext(FacesContext context, LayoutManager layout) {
        Map<String,Object> requestMap = context.getExternalContext().getRequestMap();
        List<LayoutContext> stack = (List<LayoutContext>)requestMap.get(LAYOUT_CONTEXT_STACK_ATTR);
        if (stack == null) {
            stack = new ArrayList<LayoutContext>();
            requestMap.put(LAYOUT_CONTEXT_STACK_ATTR, stack);
        }

        LayoutContext parent = null;
        if (!stack.isEmpty()) {
            parent = stack.get(stack.size() - 1);
        }
        stack.add(new LayoutContext(parent, layout));
    }

    @SuppressWarnings("unchecked")
    public static void popLayoutContext(FacesContext context) {
        Map<String,Object> requestMap = context.getExternalContext().getRequestMap();
        List<LayoutContext> stack = (List<LayoutContext>)requestMap.get(LAYOUT_CONTEXT_STACK_ATTR);
        if (stack != null) {
            stack.remove(stack.size() - 1);
            if (stack.isEmpty()) {
                requestMap.remove(LAYOUT_CONTEXT_STACK_ATTR);
            }
        }
    }

    private LayoutContext parent;
    private LayoutManager layout;

    private LayoutContext(LayoutContext parent, LayoutManager layout) {
        this.parent = parent;
        this.layout = layout;
    }

    public LayoutContext getParent() {
        return parent;
    }

    public LayoutManager getLayoutManager() {
        return layout;
    }

    public int getFaceletCount() {
        return layout.getFacelets().size();
    }

    public Facelet[] getFacelets() {
        List<Facelet> facelets = layout.getFacelets();
        Facelet[] result = new Facelet[facelets.size()];
        return facelets.toArray(result);
    }

    public Map<String,Facelet> getFacelet() {
        Map<String,Facelet> result = new HashMap<String, Facelet>();
        for (Facelet facelet : layout.getFacelets()) {
            String name = facelet.getName();
            if (name != null && !result.containsKey(name)) {
                result.put(name, facelet);
            }
        }
        return result;
    }

    public Map<String,Object> getAttributes() {
        return layout.getAttributes();
    }
}
