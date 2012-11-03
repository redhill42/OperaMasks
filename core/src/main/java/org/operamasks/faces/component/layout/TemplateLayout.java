/*
 * $Id: TemplateLayout.java,v 1.5 2007/07/02 07:38:06 jacky Exp $
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

package org.operamasks.faces.component.layout;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.IdentityHashMap;
import java.io.IOException;

import org.operamasks.faces.layout.Facelet;
import org.operamasks.faces.layout.LayoutManager;
import org.operamasks.faces.layout.LayoutContext;

public class TemplateLayout extends LayoutManagerSupport
{
    public static final String COMPONENT_TYPE = "org.operamasks.faces.layout.TemplateLayout";

    public TemplateLayout() {
        setRendererType(null);
    }

    public UITemplateContainer getTemplateContainer() {
        for (UIComponent kid : getChildren()) {
            if (kid instanceof UITemplateContainer) {
                return (UITemplateContainer)kid;
            }
        }
        return null;
    }

    public void encodeAll(FacesContext context)
        throws IOException
    {
        if (!isRendered()) {
            return;
        }

        UITemplateContainer container = getTemplateContainer();
        if (container == null) {
            return;
        }

        layout(container);

        LayoutContext.pushLayoutContext(context, this);
        container.encodeAll(context);
        LayoutContext.popLayoutContext(context);
    }

    private void layout(UITemplateContainer container) {
        List<UIFaceletSlot> slots = new ArrayList<UIFaceletSlot>();
        getSlots(container, slots);

        Map<Facelet,Facelet> assigned = new IdentityHashMap<Facelet,Facelet>();

        // assign named slots
        Iterator<UIFaceletSlot> itr = slots.iterator();
        while (itr.hasNext()) {
            UIFaceletSlot slot = itr.next();
            String name = slot.getName();
            if (name != null) {
                for (Facelet facelet : facelets) {
                    if (name.equals(facelet.getName())) {
                        slot.setFacelet(facelet);
                        itr.remove();
                        assigned.put(facelet, facelet);
                        break;
                    }
                }
            }
        }

        // assign indexed slots
        itr = slots.iterator();
        while (itr.hasNext()) {
            UIFaceletSlot slot = itr.next();
            Integer index = slot.getIndex();
            if (index != null) {
                int idx = index.intValue();
                if (idx >= 0 && idx < facelets.size()) {
                    Facelet facelet = facelets.get(idx);
                    slot.setFacelet(facelet);
                    itr.remove();
                    assigned.put(facelet, facelet);
                }
            }
        }

        // assign unnamed and unindexed slots
        for (Facelet facelet : facelets) {
            if (slots.isEmpty()) {
                break;
            }
            if (!assigned.containsKey(facelet)) {
                UIFaceletSlot slot = slots.remove(0);
                if (slot.getName() == null && slot.getIndex() == null) {
                    slot.setFacelet(facelet);
                }
            }
        }
    }

    private void getSlots(UIComponent component, List<UIFaceletSlot> slots) {
        Iterator<UIComponent> itr = component.getFacetsAndChildren();
        while (itr.hasNext()) {
            UIComponent kid = itr.next();
            if (kid instanceof UIFaceletSlot) {
                slots.add((UIFaceletSlot)kid);
            } else if (kid instanceof LayoutManager) {
                // find slots in the facelet body of inner LayoutManager
                for (Facelet facelet : ((LayoutManager)kid).getFacelets()) {
                    if (facelet instanceof UIFacelet) {
                        UIFacelet f = (UIFacelet)facelet;
                        if (f.getDelegate() == null && f.getUri() == null) {
                            getSlots(f, slots);
                        }
                    }
                }
                for (UIComponent facet : kid.getFacets().values()) {
                    getSlots(facet, slots);
                }
            } else {
                getSlots(kid, slots);
            }
        }
    }
}
