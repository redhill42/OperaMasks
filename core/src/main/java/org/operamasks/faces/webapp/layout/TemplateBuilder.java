/*
 * $Id: TemplateBuilder.java,v 1.4 2007/07/02 07:38:12 jacky Exp $
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
import javax.faces.component.UIOutput;
import javax.faces.context.FacesContext;
import java.io.Writer;
import java.io.StringWriter;

import org.operamasks.faces.layout.LayoutContext;
import org.operamasks.faces.layout.LayoutManager;
import org.operamasks.faces.component.layout.UITemplateContainer;
import org.operamasks.faces.component.layout.UIFaceletSlot;

public class TemplateBuilder
{
    public static TemplateBuilder newBuilder(FacesContext context) {
        LayoutContext lc = LayoutContext.getCurrentInstance(context);
        LayoutManager layout = lc.getLayoutManager();

        if (!(layout instanceof UIComponent)) {
            return null;
        }

        UIComponent container = null;
        for (UIComponent component : ((UIComponent)layout).getChildren()) {
            if (component instanceof UITemplateContainer) {
                container = component;
                break;
            }
        }

        if (container == null) {
            return null;
        }

        return new TemplateBuilder(context, container);
    }

    private FacesContext context;
    private UIComponent container;
    private StringWriter writer;

    public TemplateBuilder(FacesContext context, UIComponent container) {
        this.context = context;
        this.container = container;
        this.writer = new StringWriter();
    }

    public Writer getWriter() {
        return writer;
    }

    public UIFaceletSlot addFaceletSlot() {
        addVerbatim();

        UIFaceletSlot slot = new UIFaceletSlot();
        slot.setId(createId());
        container.getChildren().add(slot);
        return slot;
    }

    public UIFaceletSlot addFaceletSlot(String name) {
        UIFaceletSlot slot = addFaceletSlot();
        slot.setName(name);
        return slot;
    }

    public UIFaceletSlot addFaceletSlot(int index) {
        UIFaceletSlot slot = addFaceletSlot();
        slot.setIndex(index);
        return slot;
    }

    public void addUIComponent(UIComponent component) {
        addVerbatim();
        container.getChildren().add(component);
    }

    private void addVerbatim() {
        StringBuffer buffer = writer.getBuffer();
        if (buffer.length() == 0) {
            return;
        }

        String value = buffer.toString();
        buffer.delete(0, buffer.length());

        UIOutput verbatim = createVerbatim(value);
        container.getChildren().add(verbatim);
    }

    private UIOutput createVerbatim(String value) {
        UIOutput verbatim = (UIOutput)
            context.getApplication().createComponent("javax.faces.HtmlOutputText");

        verbatim.setId(createId());
        verbatim.setTransient(true);
        verbatim.getAttributes().put("escape", Boolean.FALSE);
        verbatim.setValue(value);
        return verbatim;
    }

    private String createId() {
        return context.getViewRoot().createUniqueId();
    }

    public void close() {
        addVerbatim();
    }
}
