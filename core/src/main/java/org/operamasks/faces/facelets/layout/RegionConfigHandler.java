/*
 * $Id: RegionConfigHandler.java,v 1.1 2007/08/13 13:25:13 daniel Exp $
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
import java.lang.reflect.Method;

import com.sun.facelets.tag.TagHandler;
import com.sun.facelets.tag.TagConfig;
import com.sun.facelets.tag.TagAttribute;
import com.sun.facelets.tag.TagAttributeException;
import com.sun.facelets.tag.TagException;
import com.sun.facelets.FaceletContext;

import org.operamasks.faces.component.layout.RegionConfig;
import org.operamasks.faces.component.layout.UIBorderLayout;
import org.operamasks.util.BeanUtils;
import static org.operamasks.resources.Resources.*;

public class RegionConfigHandler extends TagHandler
{
    private String target;

    public RegionConfigHandler(TagConfig config) {
        super(config);
        
        TagAttribute attr = this.getRequiredAttribute("target");
        if (!attr.isLiteral()) {
            throw new TagAttributeException(this.tag, attr, "Must be Literal");
        }
        this.target = attr.getValue();
    }

    public void apply(FaceletContext ctx, UIComponent parent) {
        if (!(parent instanceof UIBorderLayout)) {
            throw new TagException(this.tag, _T(JSF_NOT_NESTED_IN_FACES_TAG, "layout:borderLayout"));
        }

        RegionConfig config = new RegionConfig();
        try {
            for (TagAttribute attribute : this.tag.getAttributes().getAll()) {
                String name = attribute.getLocalName();
                Method write = BeanUtils.getWriteMethod(RegionConfig.class, name);
                if (write != null) {
                    Class type = write.getParameterTypes()[0];
                    write.invoke(config, attribute.getObject(ctx, type));
                }
            }
        } catch (Exception ex) {
            throw new FacesException(ex);
        }

        UIBorderLayout layout = (UIBorderLayout)parent;
        if ("all".equals(target)) {
            layout.setNorth(config);
            layout.setSouth(config);
            layout.setWest(config);
            layout.setEast(config);
            layout.setCenter(config);
        } else if ("north".equals(target)) {
            layout.setNorth(config);
        } else if ("west".equals(target)) {
            layout.setWest(config);
        } else if ("east".equals(target)) {
            layout.setEast(config);
        } else if ("south".equals(target)) {
            layout.setSouth(config);
        } else if ("center".equals(target)) {
            layout.setCenter(config);
        } else {
            throw new TagException(this.tag, "The \"target\" attribute must be one of: " +
                                             "'north', 'south', 'west', 'east', 'center', or 'all'");
        }
    }
}
