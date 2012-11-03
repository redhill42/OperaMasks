/*
 * $Id: UIBorderLayout.java,v 1.8 2008/03/11 03:21:00 lishaochuan Exp $
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

import javax.el.ValueExpression;
import javax.faces.context.FacesContext;

/**
 * @deprecated 此类已经被org.operamasks.faces.component.layout.impl.UIBorderLayout代替
 */
@Deprecated
public class UIBorderLayout extends LayoutManagerSupport
{
    public static final String COMPONENT_TYPE = "org.operamasks.faces.layout.BorderLayout";

    private String jsvar;
    private String container;
    private String style;
    private String styleClass;
    private RegionConfigSet configSet;

    public UIBorderLayout() {
        setRendererType("org.operamasks.faces.layout.BorderLayout");
        configSet = new RegionConfigSet();
    }

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

    public void setJsvar(String jsvar) {
        this.jsvar = jsvar;
    }

    public String getContainer() {
        if (this.container != null) {
            return this.container;
        }
        ValueExpression ve = getValueExpression("container");
        if (ve != null) {
            return (String)ve.getValue(getFacesContext().getELContext());
        } else {
            return null;
        }
    }

    public void setContainer(String container) {
        this.container = container;
    }

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

    public void setStyle(String style) {
        this.style = style;
    }

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

    public void setStyleClass(String styleClass) {
        this.styleClass = styleClass;
    }

    public RegionConfig getRegionConfig(Region region) {
        if (region == null) {
            throw new NullPointerException();
        }

        RegionConfig result = configSet.get(region);

        if (result == null) {
            ValueExpression ve = getValueExpression(region.name());
            if (ve != null) {
                result = (RegionConfig)ve.getValue(getFacesContext().getELContext());
            }
        }

        if (result == null) {
            ValueExpression ve = getValueExpression("config");
            if (ve != null) {
                RegionConfigSet veConfigSet
                    = (RegionConfigSet)ve.getValue(getFacesContext().getELContext());
                if (veConfigSet != null) {
                    result = veConfigSet.get(region);
                }
            }
        }
        if (result != null) result.setRegion(region);
        return result;
    }

    public void setRegionConfig(Region region, RegionConfig config) {
        configSet.set(region, config);
    }

    public RegionConfig getNorth() {
        return getRegionConfig(Region.north);
    }

    public void setNorth(RegionConfig config) {
        setRegionConfig(Region.north, config);
    }

    public RegionConfig getSouth() {
        return getRegionConfig(Region.south);
    }

    public void setSouth(RegionConfig config) {
        setRegionConfig(Region.south, config);
    }

    public RegionConfig getWest() {
        return getRegionConfig(Region.west);
    }

    public void setWest(RegionConfig config) {
        setRegionConfig(Region.west, config);
    }

    public RegionConfig getEast() {
        return getRegionConfig(Region.east);
    }

    public void setEast(RegionConfig config) {
        setRegionConfig(Region.east, config);
    }

    public RegionConfig getCenter() {
        return getRegionConfig(Region.center);
    }

    public void setCenter(RegionConfig config) {
        setRegionConfig(Region.center, config);
    }

    public RegionConfigSet getConfig() {
        return configSet;
    }

    public void setConfig(RegionConfigSet configSet) {
        if (configSet == null)
            throw new NullPointerException();
        this.configSet = configSet;
    }

    public Object saveState(FacesContext context) {
        return new Object[] {
            super.saveState(context),
            jsvar,
            container,
            style,
            styleClass,
            configSet
        };
    }

    public void restoreState(FacesContext context, Object state) {
        Object[] values = (Object[])state;
        int i = 0;
        super.restoreState(context, values[i++]);
        jsvar = (String)values[i++];
        container = (String)values[i++];
        style = (String)values[i++];
        styleClass = (String)values[i++];
        configSet = (RegionConfigSet)values[i++];
    }
}
