/*
 * $Id: HtmlGridCell.java,v 1.4 2007/12/11 04:20:13 jacky Exp $
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

package org.operamasks.faces.component.html;

import javax.faces.context.FacesContext;
import javax.faces.component.UIComponent;
import javax.faces.component.UIPanel;
import javax.el.ValueExpression;

import org.operamasks.faces.util.FacesUtils;

/**
 * Represents a single cell in a panel grid.
 */
public class HtmlGridCell extends UIPanel
{
    /**
     * The component type for this component.
     */
    public static final String COMPONENT_TYPE = "org.operamasks.faces.HtmlGridCell";

    /**
     * Create a new {@link HtmlGridCell} instance with default property values.
     */
    public HtmlGridCell() {
        super();
        setRendererType(null);
    }
    
    public HtmlGridCell(UIComponent parent) {
        this();
        FacesUtils.createComponent(parent, this);
    }

    private String style;
    private String styleClass;
    private int rowspan = -1;
    private int colspan = -1;
    private String align;
    private String valign;

    /**
     * Specifies style information for the current element.
     */
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

    /**
     * Specifies space-separated list of classes.
     */
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

    /**
     * Specifies the number of rows spanned by the current cell. The default value
     * of this attribute is one ("1"). The value zero ("0") means that the cell
     * spans all rows from the current row to the last row of the table section
     * in which the cell is defined.
     */
    public int getRowspan() {
        if (this.rowspan >= 0) {
            return this.rowspan;
        }
        ValueExpression ve = getValueExpression("rowspan");
        if (ve != null) {
            Object value = ve.getValue(getFacesContext().getELContext());
            if (value != null) {
                return (Integer)value;
            }
        }
        return 1;
    }

    public void setRowspan(int rowspan) {
        this.rowspan = rowspan;
    }

    /**
     * Specifies the number of columns spanned by the current cell. The default value
     * of this attribute if one ("1"). The value zero ("0") means that the cell spans
     * all columns from the current column to the last column of the column group
     * in which the cell is defined.
     */
    public int getColspan() {
        if (this.colspan >= 0) {
            return this.colspan;
        }
        ValueExpression ve = getValueExpression("colspan");
        if (ve != null) {
            Object value = ve.getValue(getFacesContext().getELContext());
            if (value != null) {
                return (Integer)value;
            }
        }
        return 1;
    }

    public void setColspan(int colspan) {
        this.colspan = colspan;
    }

    /**
     * Specifies the alignment of data and the justification of text in a cell.
     * Possible values:
     *
     * <ul>
     * <li><code>left</code>: Left-flush data/Left-justify text. This is the
     * default value for table data.</li>
     * <li><code>center</code>: Center data/Center-justify text. This is the
     * default value for table headers.</li>
     * <li><code>right</code>: Right-flush data/Righ-justify text.</li>
     * <li><code>justify</code>: Double-justify text.</li>
     * </ul>
     */
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

    /**
     * Specifies the vertical position of data within a cell. Possible values:
     * <ul>
     * <li><code>top</code>: Cell data is flush with the top of the cell.</li>
     * <li><code>middle</code>: Cell data is centered vertically within the cell.
     * This is the default value.</li>
     * <li><code>bottom</code>: Cell data is flush with the bottom of the cell.</li>
     * <li><code>baseline</code>: All cells in the same row as a cell whose
     * <code>valign</code> attribute has this value should have their textual data.
     * positioned so that the first text line occurs on a baseline common to all cells
     * in the row. This constraint does not apply to subsequent text lines in these cells.</li>
     * </ul>
     */
    public String getValign() {
        if (this.valign != null) {
            return this.valign;
        }
        ValueExpression ve = getValueExpression("valign");
        if (ve != null) {
            return (String)ve.getValue(getFacesContext().getELContext());
        } else {
            return null;
        }
    }

    public void setValign(String valign) {
        this.valign = valign;
    }

    public Object saveState(FacesContext context) {
        return new Object[] {
            super.saveState(context),
            style,
            styleClass,
            rowspan,
            colspan,
            align,
            valign
        };
    }

    public void restoreState(FacesContext context, Object state) {
        Object[] values = (Object[])state;
        int i = 0;
        super.restoreState(context, values[i++]);
        style = (String)values[i++];
        styleClass = (String)values[i++];
        rowspan = (Integer)values[i++];
        colspan = (Integer)values[i++];
        align = (String)values[i++];
        valign = (String)values[i++];
    }
}
