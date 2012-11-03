/*
 * $Id: ChartMenu.java,v 1.3 2007/12/12 08:52:51 lishaochuan Exp $
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

package demo.graph;

import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.el.ValueExpression;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;

import org.operamasks.faces.annotation.ManagedBean;
import org.operamasks.faces.annotation.ManagedBeanScope;
import org.operamasks.faces.component.ajax.AjaxUpdater;
import org.operamasks.faces.component.graph.OrientationType;
import org.operamasks.faces.component.graph.UIChart;
import org.operamasks.faces.component.widget.UITreeNode;
import org.operamasks.faces.component.widget.grid.UIDataGrid;
import org.operamasks.faces.component.widget.tree.event.TreeEvent;
import org.operamasks.faces.event.TreeEventListener;

@ManagedBean(scope=ManagedBeanScope.REQUEST)
public class ChartMenu implements TreeEventListener
{
    private AjaxUpdater chartUpdater;
    private AjaxUpdater gridUpdater;
    private AjaxUpdater optionsUpdater;

    public AjaxUpdater getChartUpdater() {
        return chartUpdater;
    }

    public void setChartUpdater(AjaxUpdater chartUpdater) {
        this.chartUpdater = chartUpdater;
    }

    public AjaxUpdater getGridUpdater() {
        return gridUpdater;
    }

    public void setGridUpdater(AjaxUpdater gridUpdater) {
        this.gridUpdater = gridUpdater;
    }

    public AjaxUpdater getOptionsUpdater() {
        return optionsUpdater;
    }

    public void setOptionsUpdater(AjaxUpdater optionsUpdater) {
        this.optionsUpdater = optionsUpdater;
    }

    private static final String PREFIX = "/graph/demos/";
    private static final String SUFFIX = ".xhtml";

    public void processEvent(TreeEvent event)
        throws AbortProcessingException
    {
        UITreeNode node = event.getAffectedNode();
        String chartId = null;
        String gridId = null;

        if (node.isLeaf()) {
            String id = (String)node.getUserData();
            int i = id.indexOf('_');
            if (i != -1) {
                chartId = id.substring(0, i);
                gridId = id.substring(i+1);
            } else {
                chartId = id;
            }
        }
        
        if (chartId != null) {
            chartUpdater.load(PREFIX + chartId + SUFFIX);
            if (optionsUpdater.getSubviewId() == null) {
                optionsUpdater.load("/graph/options.xhtml");
            }
        } else {
            chartUpdater.unload();
            optionsUpdater.unload();
        }

        if (gridId != null) {
            loadGridData(PREFIX + gridId + SUFFIX);
        } else {
            gridUpdater.unload();
        }
    }

    private void loadGridData(String uri) {
        String viewId = gridUpdater.getSubviewId();
        if (viewId == null || !viewId.equals(uri)) {
            gridUpdater.load(uri);
        } else {
            refreshGrid();
        }
    }

    private void refreshGrid() {
        for (UIComponent kid : gridUpdater.getChildren()) {
            if (kid instanceof UIDataGrid) {
                ((UIDataGrid)kid).reload();
            }
        }
    }

    public void randomData() {
        FacesContext context = FacesContext.getCurrentInstance();
        ExpressionFactory expf = context.getApplication().getExpressionFactory();
        ELContext elc = context.getELContext();
        ValueExpression ve;

        ve = expf.createValueExpression(elc, "#{CategoryData}", CategoryData.class);
        CategoryData cdata = (CategoryData)ve.getValue(elc);
        cdata.randomize();

        ve = expf.createValueExpression(elc, "#{XYData}", XYData.class);
        XYData xydata = (XYData)ve.getValue(elc);
        xydata.randomize();

        ve = expf.createValueExpression(elc, "#{TimeSeriesData}", TimeSeriesData.class);
        TimeSeriesData tdata = (TimeSeriesData)ve.getValue(elc);
        tdata.randomize();

        ve = expf.createValueExpression(elc, "#{Point2DData}", Point2DData.class);
        Point2DData pdata = (Point2DData)ve.getValue(elc);
        pdata.randomize();

        ve = expf.createValueExpression(elc, "#{HistogramData}", HistogramData.class);
        HistogramData hdata = (HistogramData)ve.getValue(elc);
        hdata.randomize();

        refreshGrid();
    }

    private UIChart getChart() {
        for (UIComponent kid : chartUpdater.getChildren()) {
            if (kid instanceof UIChart) {
                return (UIChart)kid;
            }
        }
        return null;
    }

    public boolean isEffect3D() {
        UIChart chart = getChart();
        if (chart != null) {
            return chart.isEffect3D();
        } else {
            return false;
        }
    }

    public void setEffect3D(boolean effect3D) {
        UIChart chart = getChart();
        if (chart != null) {
            chart.setEffect3D(effect3D);
        }
    }

    public boolean isHorizontal() {
        UIChart chart = getChart();
        if (chart != null) {
            return chart.getOrientation() == OrientationType.Horizontal;
        } else {
            return false;
        }
    }

    public void setHorizontal(boolean horiz) {
        UIChart chart = getChart();
        if (chart != null) {
            chart.setOrientation(horiz ? OrientationType.Horizontal : OrientationType.Vertical);
        }
    }
    
    public boolean isStacked() {
        UIChart chart = getChart();
        if (chart != null) {
            return chart.isStacked();
        } else {
            return false;
        }
    }

    public void setStacked(boolean stacked) {
        UIChart chart = getChart();
        if (chart != null) {
            chart.setStacked(stacked);
        }
    }

    public boolean isStackDisabled() {
        UIChart chart = getChart();
        if (chart != null) {
            String rendererType = chart.getRendererType();
            if (rendererType.endsWith("BarChart") || rendererType.endsWith("AreaChart")) {
                return false;
            }
        }
        return true;
    }

    public Boolean getShowLegend() {
        UIChart chart = getChart();
        if (chart != null) {
            Boolean showLegend = chart.getShowLegend();
            if (showLegend != null) {
                return showLegend;
            }
            if (chart.getLegend() != null) {
                return true;
            }
        }
        return false;
    }

    public void setShowLegend(Boolean showLegend) {
        UIChart chart = getChart();
        if (chart != null) {
            chart.setShowLegend(showLegend);
        }
    }

    public Boolean getDrawItemLabel() {
        UIChart chart = getChart();
        if (chart != null) {
            return chart.isDrawItemLabel();
        } else {
            return false;
        }
    }

    public void setDrawItemLabel(Boolean drawItemLabel) {
        UIChart chart = getChart();
        if (chart != null) {
            chart.setDrawItemLabel(drawItemLabel);
            for (UIComponent kid : chart.getChildren()) {
                if (kid instanceof UIChart) {
                    ((UIChart)kid).setDrawItemLabel(drawItemLabel);
                }
            }
        }
    }

    public Boolean getShowItemTips() {
        UIChart chart = getChart();
        if (chart != null) {
            return chart.isShowItemTips();
        } else {
            return false;
        }
    }

    public void setShowItemTips(Boolean showItemTips) {
        UIChart chart = getChart();
        if (chart != null) {
            chart.setShowItemTips(showItemTips);
            for (UIComponent kid : chart.getChildren()) {
                if (kid instanceof UIChart) {
                    ((UIChart)kid).setShowItemTips(showItemTips);
                }
            }
        }
    }
}
