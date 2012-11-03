/*
 * $Id: UIOutputColumn.java,v 1.7 2007/08/05 15:08:02 daniel Exp $
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

package org.operamasks.faces.component.widget.grid;

import javax.faces.component.ValueHolder;
import javax.faces.component.UIColumn;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.el.ValueExpression;
import javax.el.MethodExpression;

/**
 * The UIOutputColumn is a UIComponent that represents a single column of data
 * within a parent UIData component.
 */
public class UIOutputColumn extends UIColumn
    implements ValueHolder
{
    public static final String COMPONENT_TYPE = "org.operamasks.faces.widget.OutputColumn";

    public UIOutputColumn() {
        setRendererType(null);
    }

    private Converter converter;
    private Object value;

    public Converter getConverter() {
        if (this.converter != null) {
            return this.converter;
        }
        ValueExpression ve = getValueExpression("converter");
        if (ve != null) {
            return (Converter)ve.getValue(getFacesContext().getELContext());
        } else {
            return null;
        }
    }

    public void setConverter(Converter converter) {
        this.converter = converter;
    }

    public Object getLocalValue() {
        return value;
    }

    public Object getValue() {
        if (this.value != null) {
            return this.value;
        }
        ValueExpression ve = getValueExpression("value");
        if (ve != null) {
            return ve.getValue(getFacesContext().getELContext());
        } else {
            return null;
        }
    }

    public void setValue(Object value) {
        this.value = value;
    }

    /**
     * Call formatter method to format the complex data item.
     *
     * The formatter method has the signature:
     *      String format(UIColumn column, Object rowData);
     *
     * If a formatter method is defined then value attribute will not be consulted.
     */
    private MethodExpression formatter;

    public MethodExpression getFormatter() {
        return formatter;
    }

    public void setFormatter(MethodExpression formatter) {
        this.formatter = formatter;
    }

    // The UIColumn already defined the header facet, so use
    // different name for header attribute.
    private String header;

    public String getColumnHeader() {
        if (this.header != null) {
            return this.header;
        }
        ValueExpression ve = getValueExpression("columnHeader");
        if (ve != null) {
            return (String)ve.getValue(getFacesContext().getELContext());
        } else {
            return null;
        }
    }

    public void setColumnHeader(String header) {
        this.header = header;
    }

    private String tooltip;

    public String getTooltip() {
        if (this.tooltip != null) {
            return this.tooltip;
        }
        ValueExpression ve = getValueExpression("tooltip");
        if (ve != null) {
            return (String)ve.getValue(getFacesContext().getELContext());
        } else {
            return null;
        }
    }

    public void setTooltip(String tooltip) {
        this.tooltip = tooltip;
    }

    private Integer width;

    public int getWidth() {
        if (this.width != null) {
            return this.width;
        }
        ValueExpression ve = getValueExpression("width");
        if (ve != null) {
            Object value = ve.getValue(getFacesContext().getELContext());
            if (value != null) {
                return (Integer)value;
            }
        }
        return Integer.MIN_VALUE;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    private String align;

    public String getAlign() {
        if (this.align != null) {
            return this.align;
        }
        ValueExpression ve = getValueExpression("align");
        if (ve != null) {
            return (String)ve.getValue(getFacesContext().getELContext());
        } else {
            return null;
        }
    }

    public void setAlign(String align) {
        this.align = align;
    }

    private Boolean locked;

    public boolean isLocked() {
        if (this.locked != null) {
            return this.locked;
        }
        ValueExpression ve = getValueExpression("locked");
        if (ve != null) {
            Object value = ve.getValue(getFacesContext().getELContext());
            if (value != null) {
                return (Boolean)value;
            }
        }
        return false;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    private Boolean fixed;

    public boolean isFixed() {
        if (this.fixed != null) {
            return this.fixed;
        }
        ValueExpression ve = getValueExpression("fixed");
        if (ve != null) {
            Object value = ve.getValue(getFacesContext().getELContext());
            if (value != null) {
                return (Boolean)value;
            }
        }
        return false;
    }

    public void setFixed(boolean fixed) {
        this.fixed = fixed;
    }

    private Boolean sortable;

    public boolean isSortable() {
        if (this.sortable != null) {
            return this.sortable;
        }
        ValueExpression ve = getValueExpression("sortable");
        if (ve != null) {
            Object value = ve.getValue(getFacesContext().getELContext());
            if (value != null) {
                return (Boolean)value;
            }
        }
        return false;
    }

    public void setSortable(boolean sortable) {
        this.sortable = sortable;
    }

    private Boolean hidden;

    public boolean isHidden() {
        if (this.hidden != null) {
            return this.hidden;
        }
        ValueExpression ve = getValueExpression("hidden");
        if (ve != null) {
            Object value = ve.getValue(getFacesContext().getELContext());
            if (value != null) {
                return (Boolean)value;
            }
        }
        return false;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    private String clientFormatter;

    public String getClientFormatter() {
        if (this.clientFormatter != null) {
            return this.clientFormatter;
        }
        ValueExpression ve = getValueExpression("clientFormatter");
        if (ve != null) {
            return (String)ve.getValue(getFacesContext().getELContext());
        } else {
            return null;
        }
    }

    public void setClientFormatter(String clientFormatter) {
        this.clientFormatter = clientFormatter;
    }

    private String style;

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

    private String styleClass;

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
    
    public Object saveState(FacesContext context) {
        return new Object[] {
            super.saveState(context),
            saveAttachedState(context, converter),
            value,
            saveAttachedState(context, formatter),
            header,
            tooltip,
            width,
            align,
            locked,
            fixed,
            sortable,
            hidden,
            clientFormatter,
            style,
            styleClass
        };
    }

    public void restoreState(FacesContext context, Object state) {
        Object[] values = (Object[])state;
        int i = 0;
        super.restoreState(context, values[i++]);
        converter = (Converter)restoreAttachedState(context, values[i++]);
        value = values[i++];
        formatter = (MethodExpression)restoreAttachedState(context, values[i++]);
        header = (String)values[i++];
        tooltip = (String)values[i++];
        width = (Integer)values[i++];
        align = (String)values[i++];
        locked = (Boolean)values[i++];
        fixed = (Boolean)values[i++];
        sortable = (Boolean)values[i++];
        hidden = (Boolean)values[i++];
        clientFormatter = (String)values[i++];
        style = (String)values[i++];
        styleClass = (String)values[i++];
    }
}
