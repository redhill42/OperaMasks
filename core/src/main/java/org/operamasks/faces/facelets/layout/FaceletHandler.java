/*
 * $Id: FaceletHandler.java,v 1.2 2007/08/14 07:37:06 daniel Exp $
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
package org.operamasks.faces.facelets.layout;

import javax.faces.component.UIComponent;
import javax.faces.FacesException;
import javax.el.VariableMapper;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

import com.sun.facelets.tag.jsf.ComponentHandler;
import com.sun.facelets.tag.jsf.ComponentConfig;
import com.sun.facelets.tag.TagAttribute;
import com.sun.facelets.tag.MetaRuleset;
import com.sun.facelets.tag.ui.ParamHandler;
import com.sun.facelets.FaceletContext;
import com.sun.facelets.el.VariableMapperWrapper;

import org.operamasks.faces.layout.Facelet;
import org.operamasks.faces.layout.LayoutManager;
import org.operamasks.faces.layout.LayoutContext;
import org.operamasks.faces.component.layout.UIFacelet;
import org.operamasks.faces.component.layout.UIFaceletSet;
import org.operamasks.util.Utils;

public class FaceletHandler extends ComponentHandler
{
    private TagAttribute src;
    private TagAttribute srcClass;
    private ParamHandler[] params;

    public FaceletHandler(ComponentConfig config) {
        super(config);
        
        this.src = this.getAttribute("src");
        this.srcClass = this.getAttribute("srcClass");

        List<ParamHandler> list = new ArrayList<ParamHandler>();
        Iterator itr = this.findNextByType(ParamHandler.class);
        while (itr.hasNext()) {
            list.add((ParamHandler)itr.next());
        }
        if (list.size() > 0) {
            this.params = new ParamHandler[list.size()];
            list.toArray(this.params);
        }
    }

    @Override
    protected MetaRuleset createMetaRuleset(Class type) {
        return super.createMetaRuleset(type)
                    .ignore("src")
                    .ignore("srcClass")
                    .ignore("uri")
                    .ignore("delegate");
    }

    @Override
    protected void onComponentCreated(FaceletContext ctx, UIComponent c, UIComponent parent) {
        Facelet delegate = null;
        String uri = null;

        if (this.src != null) {
            Object value = this.src.getValue(ctx);
            if (value instanceof Facelet) {
                delegate = (Facelet)value;
            } else if (value instanceof String) {
                uri = (String)value;
            }
        }

        if (delegate == null && this.srcClass != null) {
            try {
                String className = this.srcClass.getValue(ctx);
                Class clazz = Utils.findClass(className);
                delegate = (Facelet)clazz.newInstance();
            } catch (Exception ex) {
                throw new FacesException(ex);
            }
        }

        UIFacelet facelet = (UIFacelet)c;
        facelet.setDelegate(delegate);
        facelet.setUri(uri);
    }

    @Override
    protected void onComponentPopulated(FaceletContext ctx, UIComponent c, UIComponent parent) {
        UIFacelet facelet = (UIFacelet)c;

        // Add this facelet to enclosing layout manager or facelet-set
        if (parent instanceof LayoutManager) {
            ((LayoutManager)parent).getFacelets().add(facelet);
        } else if (parent instanceof UIFaceletSet) {
            ((UIFaceletSet)parent).getFacelets().add(facelet);
        }
    }

    @Override
    protected void applyNextHandler(FaceletContext ctx, UIComponent c)
        throws IOException
    {
        UIFacelet facelet = (UIFacelet)c;

        String uri = facelet.getUri();
        if (uri != null) {
            if (uri.equals("#inherit")) {
                // get inherited facelet from layout manager hieracy
                Facelet delegate = getInheritedDelegate(facelet.getName());
                if (delegate != null) {
                    facelet.setDelegate(delegate);
                }
            } else {
                // include "src" as facelet content
                VariableMapper orig = ctx.getVariableMapper();
                if (this.params != null) {
                    VariableMapper vm = new VariableMapperWrapper(orig);
                    ctx.setVariableMapper(vm);
                    for (int i = 0; i < this.params.length; i++) {
                        this.params[i].apply(ctx, c);
                    }
                }
                try {
                    ctx.includeFacelet(c, uri);
                } finally {
                    ctx.setVariableMapper(orig);
                }
            }
        } else {
            super.applyNextHandler(ctx, c);
        }
    }

    private Facelet getInheritedDelegate(String name) {
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
}
