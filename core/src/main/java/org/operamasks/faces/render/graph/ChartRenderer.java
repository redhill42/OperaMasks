/*
 * $Id: ChartRenderer.java,v 1.15 2008/01/08 05:17:38 lishaochuan Exp $
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

package org.operamasks.faces.render.graph;

import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.component.UIComponent;
import javax.faces.FacesException;

import java.awt.Paint;
import java.awt.Font;
import java.awt.Image;
import java.awt.Color;
import java.awt.BasicStroke;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javax.el.MethodExpression;
import javax.el.ELContext;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.Iterator;
import java.text.NumberFormat;
import java.text.DecimalFormat;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.entity.ChartEntity;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.labels.StandardXYItemLabelGenerator;
import org.jfree.chart.labels.StandardCategoryToolTipGenerator;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.block.LineBorder;
import org.jfree.chart.annotations.CategoryTextAnnotation;
import org.jfree.chart.annotations.XYTextAnnotation;
import org.jfree.chart.annotations.CategoryPointerAnnotation;
import org.jfree.chart.annotations.XYPointerAnnotation;
import org.jfree.chart.axis.Axis;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.axis.LogarithmicAxis;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.DateTickUnit;
import org.jfree.chart.renderer.AbstractRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.chart.title.LegendTitle;
import org.jfree.chart.title.Title;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.Align;
import org.jfree.ui.HorizontalAlignment;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.VerticalAlignment;
import org.jfree.ui.TextAnchor;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.statistics.Regression;
import org.jfree.data.function.Function2D;
import org.jfree.data.function.LineFunction2D;
import org.jfree.data.function.PowerFunction2D;

import org.operamasks.faces.component.graph.UIChart;
import org.operamasks.faces.component.graph.UIDataSeries;
import org.operamasks.faces.component.graph.UIDataItem;
import org.operamasks.faces.component.graph.OrientationType;
import org.operamasks.faces.component.graph.UITitle;
import org.operamasks.faces.component.graph.UIAxis;
import org.operamasks.faces.component.graph.UITextAnnotation;
import org.operamasks.faces.component.graph.PositionType;
import org.operamasks.faces.component.graph.UITimeSeries;
import org.operamasks.faces.component.graph.TimePeriodType;
import org.operamasks.faces.component.graph.UILegend;
import org.operamasks.faces.component.graph.UIRegressionLine;
import org.operamasks.faces.component.graph.RegressionType;
import org.operamasks.faces.component.graph.UIAverageLine;
import org.operamasks.faces.component.graph.UICurve;
import org.operamasks.faces.component.graph.UISpline;
import org.operamasks.faces.component.graph.SplineType;
import org.operamasks.faces.render.html.HtmlRenderer;
import org.operamasks.faces.render.resource.ResourceManager;
import org.operamasks.faces.render.ajax.AjaxResponseWriter;
import org.operamasks.faces.util.FacesUtils;
import org.operamasks.faces.util.HtmlEncoder;
import org.operamasks.el.eval.Coercion;
import static org.operamasks.resources.Resources.*;

import static org.operamasks.faces.render.graph.ChartRendererHelper.*;
import org.operamasks.faces.render.widget.yuiext.YuiExtResource;

public abstract class ChartRenderer extends HtmlRenderer
{
    @Override
    public void encodeEnd(FacesContext context, UIComponent component)
        throws IOException
    {
        if (context == null || component == null)
            throw new NullPointerException();
        if (!component.isRendered())
            return;

        UIChart c = (UIChart)component;
        JFreeChart chart = createChart(c);

        if (chart == null) {
            throw new FacesException(_T(UI_CHART_INCOMPATIBLE_DATA_SERIES));
        }

        initChart(chart, c);
        encodeChartImage(context, c, chart);
    }

    @Override
    public boolean getRendersChildren() {
        return true;
    }

    @Override
    public void encodeChildren(FacesContext context, UIComponent component)
        throws IOException
    {
        // no children to render
    }

    protected abstract JFreeChart createChart(UIChart comp);

    protected void initChart(JFreeChart chart, UIChart comp) {
        setChartStyles(chart, comp);
        setLegendStyles(chart, comp);
        setChartTitles(chart, comp);
        setChartAxes(chart, comp);
        createCurveSeries(chart, comp);
        setSeriesStyles(chart, comp);
        addTextAnnotations(chart, comp);
        setToolTipGenerator(chart, comp);

        MethodExpression init = comp.getInit();
        if (init != null) {
            ELContext context = FacesContext.getCurrentInstance().getELContext();
            init.invoke(context, new Object[]{chart});
        }
    }

    protected PlotOrientation getChartOrientation(UIChart comp) {
        if (comp.getOrientation() == OrientationType.Horizontal) {
            return PlotOrientation.HORIZONTAL;
        } else {
            return PlotOrientation.VERTICAL;
        }
    }

    protected void setChartStyles(JFreeChart chart, UIChart comp) {
        Plot plot = chart.getPlot();

        RectangleInsets insets = plot.getInsets();
        Double tm = comp.getTopMargin();
        Double lm = comp.getLeftMargin();
        Double bm = comp.getBottomMargin();
        Double rm = comp.getRightMargin();
        if (tm == null || tm < 0)
            tm = insets.getTop();
        if (lm == null || lm < 0)
            lm = insets.getLeft();
        if (bm == null || bm < 0)
            bm = insets.getBottom();
        if (rm == null || rm < 0)
            rm = insets.getRight();
        plot.setInsets(new RectangleInsets(tm, lm, bm, rm));

        Paint color = comp.getBackgroundColor();
        if (color != null) {
            chart.setBackgroundPaint(color);
        }

        Image image = loadImage(comp.getBackgroundImage());
        if (image != null) {
            chart.setBackgroundImage(image);
            chart.setBackgroundImageAlignment(getImageAlign(comp.getBackgroundImagePosition()));
            chart.setBackgroundImageAlpha(comp.getBackgroundImageAlpha());
        }

        color = comp.getPlotColor();
        if (color != null) {
            plot.setBackgroundPaint(color);
        }

        Float alpha;
        if ((alpha = comp.getBackgroundAlpha()) != null) {
            plot.setBackgroundAlpha(alpha);
        }
        if ((alpha = comp.getForegroundAlpha()) != null) {
            plot.setForegroundAlpha(alpha);
        }

        image = loadImage(comp.getPlotImage());
        if (image != null) {
            plot.setBackgroundImage(image);
            plot.setBackgroundImageAlignment(getImageAlign(comp.getPlotImagePosition()));
            plot.setBackgroundImageAlpha(comp.getBackgroundImageAlpha());
        }

        Paint[] colorPalette = comp.getColorPalette();
        if (colorPalette != null) {
            plot.setDrawingSupplier(new CustomDrawingSupplier(colorPalette));
        } else {
            plot.setDrawingSupplier(new CustomDrawingSupplier());
        }
    }

    private Image loadImage(Object obj) {
        if (obj instanceof Image) {
            return (Image)obj;
        }

        if (obj instanceof String) {
            try {
                FacesContext context = FacesContext.getCurrentInstance();
                URL url = context.getExternalContext().getResource((String)obj);
                if (url != null) {
                    return ImageIO.read(url);
                }
            } catch (IOException ex) {}
        }

        return null;
    }

    private int getImageAlign(PositionType position) {
        if (position != null) {
            switch (position) {
            case Top:           return Align.TOP;
            case Bottom:        return Align.BOTTOM;
            case Left:          return Align.LEFT;
            case Right:         return Align.RIGHT;
            case TopLeft:       return Align.TOP_LEFT;
            case LeftTop:       return Align.TOP_LEFT;
            case TopRight:      return Align.TOP_RIGHT;
            case RightTop:      return Align.TOP_RIGHT;
            case BottomLeft:    return Align.BOTTOM_LEFT;
            case LeftBottom:    return Align.BOTTOM_LEFT;
            case BottomRight:   return Align.BOTTOM_RIGHT;
            case RightBottom:   return Align.BOTTOM_RIGHT;
            case Center:        return Align.CENTER;
            case Stretch:       return Align.FIT;
            }
        }
        return Align.FIT;
    }

    protected void setChartTitles(JFreeChart chart, UIChart comp) {
        String titleText = comp.getTitle();
        if (titleText != null) {
            chart.setTitle(titleText);
        }

        for (UIComponent kid : comp.getChildren()) {
            if (kid.isRendered() && (kid instanceof UITitle)) {
                TextTitle title = createTitle((UITitle)kid);
                if (chart.getTitle() == null) {
                    chart.setTitle(title);
                } else {
                    chart.addSubtitle(title);
                }
            }
        }
    }

    private TextTitle createTitle(UITitle titlecomp) {
        FacesContext context = FacesContext.getCurrentInstance();
        String text = FacesUtils.getFormattedValue(context, titlecomp, titlecomp.getValue());
        TextTitle title = new TextTitle(text);

        Font font = titlecomp.getFont();
        if (font != null) {
            title.setFont(font);
        }

        Paint color = titlecomp.getColor();
        if (color != null) {
            title.setPaint(color);
        }

        Paint bgcolor = titlecomp.getBackgroundColor();
        if (bgcolor != null) {
            title.setBackgroundPaint(bgcolor);
        }

        PositionType position = titlecomp.getPosition();
        if (position != null) {
            setTitlePosition(title, position);
        }

        return title;
    }

    private void setTitlePosition(Title title, PositionType position) {
        switch (position) {
        case Top:
            title.setPosition(RectangleEdge.TOP);
            break;
        case Bottom:
            title.setPosition(RectangleEdge.BOTTOM);
            break;
        case Left:
            title.setPosition(RectangleEdge.LEFT);
            break;
        case Right:
            title.setPosition(RectangleEdge.RIGHT);
            break;
        case TopLeft:
            title.setPosition(RectangleEdge.TOP);
            title.setHorizontalAlignment(HorizontalAlignment.LEFT);
            break;
        case TopRight:
            title.setPosition(RectangleEdge.TOP);
            title.setHorizontalAlignment(HorizontalAlignment.RIGHT);
            break;
        case BottomLeft:
            title.setPosition(RectangleEdge.BOTTOM);
            title.setHorizontalAlignment(HorizontalAlignment.LEFT);
            break;
        case BottomRight:
            title.setPosition(RectangleEdge.BOTTOM);
            title.setHorizontalAlignment(HorizontalAlignment.RIGHT);
            break;
        case LeftTop:
            title.setPosition(RectangleEdge.LEFT);
            title.setVerticalAlignment(VerticalAlignment.TOP);
            break;
        case LeftBottom:
            title.setPosition(RectangleEdge.LEFT);
            title.setVerticalAlignment(VerticalAlignment.BOTTOM);
            break;
        case RightTop:
            title.setPosition(RectangleEdge.RIGHT);
            title.setVerticalAlignment(VerticalAlignment.TOP);
            break;
        case RightBottom:
            title.setPosition(RectangleEdge.RIGHT);
            title.setVerticalAlignment(VerticalAlignment.BOTTOM);
            break;
        }
    }

    protected void setLegendStyles(JFreeChart chart, UIChart comp) {
        Boolean showLegend = comp.getShowLegend();
        if (showLegend != null && !showLegend) {
            return;
        }

        UILegend legendcomp = comp.getLegend();
        if (legendcomp == null) {
            if (showLegend != null && showLegend) {
                // Create default legend
                LegendTitle legend = new LegendTitle(chart.getPlot());
                legend.setMargin(new RectangleInsets(1.0, 1.0, 1.0, 1.0));
                legend.setFrame(new LineBorder());
                legend.setBackgroundPaint(Color.white);
                legend.setPosition(RectangleEdge.BOTTOM);
                chart.addSubtitle(legend);
            }
        } else {
            chart.addSubtitle(createLegend(chart, legendcomp));
        }
    }

    private LegendTitle createLegend(JFreeChart chart, UILegend legendcomp) {
        LegendTitle legend = new LegendTitle(chart.getPlot());

        PositionType position = legendcomp.getPosition();
        if (position == null) {
            legend.setPosition(RectangleEdge.BOTTOM);
        } else {
            setTitlePosition(legend, position);
        }

        Double tm = legendcomp.getTopMargin();
        Double lm = legendcomp.getLeftMargin();
        Double bm = legendcomp.getBottomMargin();
        Double rm = legendcomp.getRightMargin();
        if (tm == null) tm = 1.0;
        if (lm == null) lm = 1.0;
        if (bm == null) bm = 1.0;
        if (rm == null) rm = 1.0;
        legend.setMargin(new RectangleInsets(tm, lm, bm, rm));

        Paint bgcolor = legendcomp.getBackgroundColor();
        if (bgcolor != null) {
            legend.setBackgroundPaint(bgcolor);
        }

        Float borderWidth = legendcomp.getBorderWidth();
        Paint borderColor = legendcomp.getBorderColor();
        if (borderWidth != null || borderColor != null) {
            if (borderWidth == null)
                borderWidth = 1.0f;
            if (borderColor == null)
                borderColor = Color.black;
            LineBorder border = new LineBorder(borderColor,
                                               new BasicStroke(borderWidth),
                                               new RectangleInsets(1.0,1.0,1.0,1.0));
            legend.setFrame(border);
        }

        Font itemFont = legendcomp.getItemFont();
        Paint itemColor = legendcomp.getItemColor();
        if (itemFont != null)
            legend.setItemFont(itemFont);
        if (itemColor != null)
            legend.setItemPaint(itemColor);

        return legend;
    }

    protected void setChartAxes(JFreeChart chart, UIChart comp) {
        UIAxis xAxis = comp.getxAxis();
        UIAxis yAxis = comp.getyAxis();
        String xAxisLabel = comp.getxAxisLabel();
        String yAxisLabel = comp.getyAxisLabel();

        Plot plot = chart.getPlot();
        Axis domainAxis = null;
        Axis rangeAxis = null;

        if (plot instanceof CategoryPlot) {
            CategoryPlot categoryPlot = (CategoryPlot)plot;
            if (yAxis != null && yAxis.isLogarithmic())
                categoryPlot.setRangeAxis(new LogarithmicAxis(null));

            domainAxis = categoryPlot.getDomainAxis();
            rangeAxis = categoryPlot.getRangeAxis();

            if (xAxis != null) {
                Boolean drawGridLine = xAxis.getDrawGridLine();
                if (drawGridLine != null) {
                    categoryPlot.setDomainGridlinesVisible(drawGridLine);
                }
                Paint gridLineColor = xAxis.getGridLineColor();
                if (gridLineColor != null) {
                    categoryPlot.setDomainGridlinePaint(gridLineColor);
                }
            }

            if (yAxis != null) {
                Boolean drawGridLine = yAxis.getDrawGridLine();
                if (drawGridLine != null) {
                    categoryPlot.setRangeGridlinesVisible(drawGridLine);
                }
                Paint gridLineColor = yAxis.getGridLineColor();
                if (gridLineColor != null) {
                    categoryPlot.setRangeGridlinePaint(gridLineColor);
                }
            }
        } else if (plot instanceof XYPlot) {
            XYPlot xyPlot = (XYPlot)plot;
            if (xAxis != null && xAxis.isLogarithmic())
                xyPlot.setDomainAxis(new LogarithmicAxis(null));
            if (yAxis != null && yAxis.isLogarithmic())
                xyPlot.setRangeAxis(new LogarithmicAxis(null));

            domainAxis = xyPlot.getDomainAxis();
            rangeAxis = xyPlot.getRangeAxis();

            if (xAxis != null) {
                Boolean drawGridLine = xAxis.getDrawGridLine();
                if (drawGridLine != null) {
                    xyPlot.setDomainGridlinesVisible(drawGridLine);
                }
                Paint gridLineColor = xAxis.getGridLineColor();
                if (gridLineColor != null) {
                    xyPlot.setDomainGridlinePaint(gridLineColor);
                }
                Boolean drawBaseLine = xAxis.getDrawBaseLine();
                if (drawBaseLine != null) {
                    xyPlot.setDomainZeroBaselineVisible(drawBaseLine);
                }
                Paint baseLineColor = xAxis.getBaseLineColor();
                if (baseLineColor != null) {
                    xyPlot.setDomainZeroBaselinePaint(baseLineColor);
                }
            }

            if (yAxis != null) {
                Boolean drawGridLine = yAxis.getDrawGridLine();
                if (drawGridLine != null) {
                    xyPlot.setRangeGridlinesVisible(drawGridLine);
                }
                Paint gridLineColor = yAxis.getGridLineColor();
                if (gridLineColor != null) {
                    xyPlot.setRangeGridlinePaint(gridLineColor);
                }
                Boolean drawBaseLine = yAxis.getDrawBaseLine();
                if (drawBaseLine != null) {
                    xyPlot.setRangeZeroBaselineVisible(drawBaseLine);
                }
                Paint baseLineColor = yAxis.getBaseLineColor();
                if (baseLineColor != null) {
                    xyPlot.setRangeZeroBaselinePaint(baseLineColor);
                }
            }
        }

        if (domainAxis != null) {
            if (xAxisLabel != null)
                domainAxis.setLabel(xAxisLabel);
            if (xAxis != null)
                setAxisStyles(domainAxis, xAxis);
        }
        if (rangeAxis != null) {
            if (yAxisLabel != null)
                rangeAxis.setLabel(yAxisLabel);
            if (yAxis != null)
                setAxisStyles(rangeAxis, yAxis);
        }
    }

    protected void setAxisStyles(Axis axis, UIAxis comp) {
        axis.setVisible(comp.isVisible());
        axis.setAxisLineVisible(comp.isDrawLine());
        axis.setTickLabelsVisible(comp.isDrawTickLabels());
        axis.setTickMarksVisible(comp.isDrawTickMarks());

        String label          = comp.getLabel();
        Font   labelFont      = comp.getLabelFont();
        Paint  labelColor     = comp.getLabelColor();
        Paint  lineColor      = comp.getLineColor();
        Font   tickLabelFont  = comp.getTickLabelFont();
        Paint  tickLabelColor = comp.getTickLabelColor();
        Paint  tickMarkColor  = comp.getTickMarkColor();

        if (label != null)
            axis.setLabel(label);
        if (labelFont != null)
            axis.setLabelFont(labelFont);
        if (labelColor != null)
            axis.setLabelPaint(labelColor);
        if (lineColor != null)
            axis.setAxisLinePaint(lineColor);
        if (tickLabelFont != null)
            axis.setTickLabelFont(tickLabelFont);
        if (tickLabelColor != null)
            axis.setTickLabelPaint(tickLabelColor);
        if (tickMarkColor != null)
            axis.setTickMarkPaint(tickMarkColor);
        axis.setTickMarkInsideLength(comp.getTickMarkInsideLength());
        axis.setTickMarkOutsideLength(comp.getTickMarkOutsideLength());

        if (axis instanceof CategoryAxis) {
            setCategoryAxisStyles((CategoryAxis)axis, comp);
        } else if (axis instanceof DateAxis) {
            setDateAxisStyles((DateAxis)axis, comp);
        } else if (axis instanceof NumberAxis) {
            setNumberAxisStyles((NumberAxis)axis, comp);
        }
    }

    private void setCategoryAxisStyles(CategoryAxis axis, UIAxis comp) {
        Double lowerMargin = comp.getLowerMargin();
        Double upperMargin = comp.getUpperMargin();
        if (lowerMargin != null)
            axis.setLowerMargin(lowerMargin);
        if (upperMargin != null)
            axis.setUpperMargin(upperMargin);
        
        Double labelAngle = comp.getLabelAngle();
        if (labelAngle != null) {
            CategoryLabelPositions clp;
            double angle = Math.PI * labelAngle / 180.0;
            if (angle >= 0) {
                clp = CategoryLabelPositions.createDownRotationLabelPositions(angle);
            } else {
                clp = CategoryLabelPositions.createUpRotationLabelPositions(-angle);
            }
            axis.setCategoryLabelPositions(clp);
        }
    }

    private void setDateAxisStyles(DateAxis axis, UIAxis comp) {
        UIDataSeries data = ((UIChart)comp.getParent()).getDataSeries();
        if (!(data instanceof UITimeSeries))
            return;
        UITimeSeries ts = (UITimeSeries)data;

        axis.setInverted(comp.isInverted());

        Object lowerBound = comp.getLowerBound();
        Object upperBound = comp.getUpperBound();
        Double lowerMargin = comp.getLowerMargin();
        Double upperMargin = comp.getUpperMargin();

        if (lowerBound != null)
            axis.setLowerBound(getTimePeriodValue(ts, lowerBound));
        if (upperBound != null)
            axis.setUpperBound(getTimePeriodValue(ts, upperBound));
        if (lowerMargin != null)
            axis.setLowerMargin(lowerMargin);
        if (upperMargin != null)
            axis.setUpperMargin(upperMargin);

        Double tickStep = comp.getTickStep();
        String tickFormat = comp.getTickLabelFormat();
        int dateTickUnit = 0;
        int dateTickStep = 0;

        if (tickStep != null) {
            dateTickStep = tickStep.intValue();

            TimePeriodType tp = comp.getTickUnit();
            if (tp == null) {
                tp = ts.getTimePeriod();
            }
            switch (tp) {
            case Year:          dateTickUnit = DateTickUnit.YEAR;
                                break;
            case Quarter:       dateTickUnit = DateTickUnit.MONTH;
                                dateTickStep *= 4;
                                break;
            case Month:         dateTickUnit = DateTickUnit.MONTH;
                                break;
            case Week:          dateTickUnit = DateTickUnit.DAY;
                                dateTickStep *= 7;
                                break;
            case Day:           dateTickUnit = DateTickUnit.DAY;
                                break;
            case Hour:          dateTickUnit = DateTickUnit.HOUR;
                                break;
            case Minute:        dateTickUnit = DateTickUnit.MINUTE;
                                break;
            case Second:        dateTickUnit = DateTickUnit.SECOND;
                                break;
            case Millisecond:   dateTickUnit = DateTickUnit.MILLISECOND;
                                break;
            default:            throw new AssertionError();
            }
        }

        if ((tickStep != null && tickStep > 0) || tickFormat != null) {
            if (tickFormat == null) {
                axis.setTickUnit(new DateTickUnit(dateTickUnit, dateTickStep));
            } else if (tickStep == null) {
                DateFormat format = new SimpleDateFormat(tickFormat);
                axis.setDateFormatOverride(format);
            } else {
                DateFormat format = new SimpleDateFormat(tickFormat);
                axis.setTickUnit(new DateTickUnit(dateTickUnit, dateTickStep, format));
            }
        }
    }

    private void setNumberAxisStyles(NumberAxis axis, UIAxis comp) {
        axis.setInverted(comp.isInverted());

        Object lowerBound = comp.getLowerBound();
        Object upperBound = comp.getUpperBound();
        Double lowerMargin = comp.getLowerMargin();
        Double upperMargin = comp.getUpperMargin();

        if (lowerBound != null)
            axis.setLowerBound(Coercion.coerceToDouble(lowerBound));
        if (upperBound != null)
            axis.setUpperBound(Coercion.coerceToDouble(upperBound));
        if (lowerMargin != null)
            axis.setLowerMargin(lowerMargin);
        if (upperMargin != null)
            axis.setUpperMargin(upperMargin);

        Double tickStep = comp.getTickStep();
        String tickFormat = comp.getTickLabelFormat();

        if ((tickStep != null && tickStep > 0) || tickFormat != null) {
            if (tickFormat == null) {
                axis.setTickUnit(new NumberTickUnit(tickStep));
            } else if (tickStep == null) {
                NumberFormat format = new DecimalFormat(tickFormat);
                axis.setNumberFormatOverride(format);
            } else {
                NumberFormat format = new DecimalFormat(tickFormat);
                axis.setTickUnit(new NumberTickUnit(tickStep, format));
            }
        }
    }

    protected void createCurveSeries(JFreeChart chart, UIChart comp) {
        UIDataSeries data = comp.getDataSeries();
        if (data == null) {
            return;
        }

        if (!(chart.getPlot() instanceof XYPlot)) {
            return;
        }

        XYPlot plot = (XYPlot)chart.getPlot();
        XYDataset dataset = plot.getDataset();
        if (dataset.getSeriesCount() == 0) {
            return;
        }

        UIDataItem[] items = data.getItems();
        XYSeriesCollection collection = null;
        XYLineAndShapeRenderer renderer = null;

        int curSeries = 0;
        for (int i = 0; i < items.length; i++) {
            if (dataset.getItemCount(i) < 2) {
                continue;
            }

            for (UIComponent kid : items[i].getChildren()) {
                if (kid.isRendered() && (kid instanceof UICurve)) {
                    UICurve curve = (UICurve)kid;
                    XYSeries series = createCurveSeries(curve, dataset, i);

                    if (collection == null) {
                        collection = new XYSeriesCollection();
                        renderer = new XYLineAndShapeRenderer(true, false);
                    }
                    collection.addSeries(series);

                    String legend = curve.getLegend();
                    if (legend == null || legend.length() == 0) {
                        renderer.setSeriesVisibleInLegend(curSeries, false);
                    }

                    Paint color = curve.getColor();
                    if (color != null) {
                        renderer.setSeriesPaint(curSeries, color);
                    }

                    curSeries++;
                }
            }
        }

        if (collection != null) {
            plot.setDataset(1, collection);
            plot.setRenderer(1, renderer);
        }
    }

    private XYSeries createCurveSeries(UICurve curve, XYDataset data, int index) {
        if (data.getItemCount(index) < 2) {
            throw new IllegalArgumentException("Not enough data");
        }

        Key key = new Key(index, curve.getLegend());

        if (curve instanceof UIAverageLine) {
            double period = ((UIAverageLine)curve).getPeriod();
            double skip = ((UIAverageLine)curve).getSkip();
            if (data instanceof TimeSeriesCollection) {
                TimeSeries ts = ((TimeSeriesCollection)data).getSeries(index);
                return MovingAverage.createMovingAverage(ts, key, (int)period, (int)skip);
            } else {
                return MovingAverage.createMovingAverage(data, index, key, period, skip);
            }
        } else if (curve instanceof UIRegressionLine) {
            RegressionType type = ((UIRegressionLine)curve).getType();
            int samples = ((UIRegressionLine)curve).getSamples();
            if (type == null || type == RegressionType.Linear) {
                double[] p = Regression.getOLSRegression(data, index);
                Function2D f = new LineFunction2D(p[0], p[1]);
                return createFunctionSeries(data, index, key, f, samples);
            } else if (type == RegressionType.Power) {
                double[] p = Regression.getPowerRegression(data, index);
                Function2D f = new PowerFunction2D(p[0], p[1]);
                return createFunctionSeries(data, index, key, f, samples);
            } else {
                throw new IllegalArgumentException(type.toString());
            }
        } else if (curve instanceof UISpline) {
            SplineType type = ((UISpline)curve).getType();
            int samples = ((UISpline)curve).getSamples();
            int degree = ((UISpline)curve).getDegree();
            if (type == null || type == SplineType.BSpline) {
                return BSpline.createBSpline(data, index, key, samples, degree);
            } else if (type == SplineType.CubicSpline) {
                return CubicSpline.createCubicSpline(data, index, key, samples);
            }
        }

        return null;
    }

    private XYSeries createFunctionSeries(XYDataset data,
                                          int index,
                                          Key key,
                                          Function2D f,
                                          int samples)
    {
        XYSeries series = new XYSeries(key);
        double start = data.getXValue(index, 0);
        double end = data.getXValue(index, data.getItemCount(index)-1);
        double step = (end - start) / samples;
        for (int i = 0; i < samples; i++) {
            double x = start + (step * i);
            series.add(x, f.getValue(x));
        }
        return series;
    }

    protected void setSeriesStyles(JFreeChart chart, UIChart comp) {
        UIDataSeries data = comp.getDataSeries();
        if (data == null) {
            return;
        }

        Plot plot = chart.getPlot();
        AbstractRenderer renderer = null;

        if (plot instanceof CategoryPlot) {
            renderer = (AbstractRenderer) ((CategoryPlot)plot).getRenderer();
        } else if (plot instanceof XYPlot) {
            renderer = (AbstractRenderer) ((XYPlot)plot).getRenderer();
        }

        if (renderer != null) {
            if (comp.isDrawItemLabel()) {
                renderer.setBaseItemLabelsVisible(true);
                if (renderer instanceof CategoryItemRenderer) {
                    CategoryItemRenderer r = (CategoryItemRenderer)renderer;
                    r.setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator());
                } else if (renderer instanceof XYItemRenderer) {
                    XYItemRenderer r = (XYItemRenderer)renderer;
                    r.setBaseItemLabelGenerator(new StandardXYItemLabelGenerator());
                }
            }

            Paint itemLabelColor = comp.getItemLabelColor();
            if (itemLabelColor != null) {
                renderer.setBaseItemLabelPaint(itemLabelColor);
            }

            Font itemLabelFont = comp.getItemLabelFont();
            if (itemLabelFont != null) {
                renderer.setBaseItemLabelFont(itemLabelFont);
            }

            UIDataItem[] items = data.getItems();
            for (int i = 0; i < items.length; i++) {
                setSeriesStyles(renderer, comp, i, items[i]);
            }
        }
    }

    protected void setSeriesStyles(AbstractRenderer renderer, UIChart comp, int index, UIDataItem item) {
        if (!item.isShowLegend() || item.getLegend() == null) {
            renderer.setSeriesVisibleInLegend(index, false);
        }

        Paint color = item.getColor();
        if (color != null) {
            renderer.setSeriesPaint(index, color);
        }

        Paint outlineColor = item.getOutlineColor();
        if (outlineColor == null) {
            outlineColor = comp.getOutlineColor();
            if (outlineColor == null) {
                outlineColor = renderer.getSeriesPaint(index);
                if (outlineColor instanceof Color) {
                    outlineColor = ((Color)outlineColor).darker();
                }
            }
        }
        renderer.setSeriesOutlinePaint(index, outlineColor);

        Boolean drawItemLabel = item.getDrawItemLabel();
        if (drawItemLabel != null) {
            renderer.setSeriesItemLabelsVisible(index, drawItemLabel);
            if (drawItemLabel) {
                if (renderer instanceof CategoryItemRenderer) {
                    CategoryItemRenderer r = (CategoryItemRenderer)renderer;
                    if (r.getBaseItemLabelGenerator() == null) {
                        r.setSeriesItemLabelGenerator(index, new StandardCategoryItemLabelGenerator());
                    }
                } else if (renderer instanceof XYItemRenderer) {
                    XYItemRenderer r = (XYItemRenderer)renderer;
                    if (r.getBaseItemLabelGenerator() == null) {
                        r.setSeriesItemLabelGenerator(index, new StandardXYItemLabelGenerator());
                    }
                }
            }
        }

        Paint itemLabelColor = item.getItemLabelColor();
        if (itemLabelColor != null) {
            renderer.setSeriesItemLabelPaint(index, itemLabelColor);
        }

        Font itemLabelFont = item.getItemLabelFont();
        if (itemLabelFont != null) {
            renderer.setSeriesItemLabelFont(index, itemLabelFont);
        }
    }

    protected void addTextAnnotations(JFreeChart chart, UIChart comp) {
        Plot plot = chart.getPlot();
        if (plot instanceof CategoryPlot) {
            addCategoryTextAnnotations(chart, comp);
        } else if (plot instanceof XYPlot) {
            addXYTextAnnotations(chart, comp);
        }
    }

    private void addCategoryTextAnnotations(JFreeChart chart, UIChart comp) {
        UIDataSeries data = comp.getDataSeries();
        if (data == null) {
            return;
        }

        CategoryPlot plot = (CategoryPlot)chart.getPlot();

        for (UIComponent kid : comp.getChildren()) {
            if (kid.isRendered() && (kid instanceof UITextAnnotation)) {
                UITextAnnotation at = (UITextAnnotation)kid;
                Comparable key = getCategoryKey(data, at.getxValue());
                if (key != null) {
                    CategoryTextAnnotation a = createCategoryTextAnnotation(at, key);
                    if (a != null) {
                        plot.addAnnotation(a);
                    }
                }
            }
        }
    }

    private void addXYTextAnnotations(JFreeChart chart, UIChart comp) {
        XYPlot plot = (XYPlot)chart.getPlot();

        UIDataSeries data = comp.getDataSeries();
        if (data instanceof UITimeSeries) {
            Class timePeriodClass = getTimePeriodClass(((UITimeSeries)data).getTimePeriod());

            for (UIComponent kid : comp.getChildren()) {
                if (kid.isRendered() && (kid instanceof UITextAnnotation)) {
                    XYTextAnnotation a = createTimeSeriesTextAnnotation
                        ((UITextAnnotation)kid, timePeriodClass);
                    if (a != null) {
                        plot.addAnnotation(a);
                    }
                }
            }
        } else {
            for (UIComponent kid : comp.getChildren()) {
                if (kid.isRendered() && (kid instanceof UITextAnnotation)) {
                    XYTextAnnotation a = createXYTextAnnotation((UITextAnnotation)kid);
                    if (a != null) {
                        plot.addAnnotation(a);
                    }
                }
            }
        }
    }

    private CategoryTextAnnotation createCategoryTextAnnotation(UITextAnnotation at, Comparable key) {
        CategoryTextAnnotation result;

        String label = at.getText();
        double value = Coercion.coerceToDouble(at.getyValue());

        if (at.isDrawArrow()) {
            Double angle = at.getArrowAngle();
            if (angle == null)
                angle = 0.0;
            CategoryPointerAnnotation pointer = new CategoryPointerAnnotation
                (label, key, value, angle*Math.PI/180.0);

            Double length = at.getArrowLength();
            if (length != null) {
                pointer.setBaseRadius(length);
            }

            Paint arrowColor = at.getArrowColor();
            if (arrowColor == null)
                arrowColor = at.getColor();
            if (arrowColor != null)
                pointer.setArrowPaint(arrowColor);

            result = pointer;
        } else {
            result = new CategoryTextAnnotation(label, key, value);
        }

        Font font = at.getFont();
        if (font != null) {
            result.setFont(font);
        }

        Paint color = at.getColor();
        if (color != null) {
            result.setPaint(color);
        }

        TextAnchor anchor = getTextAnchor(at.getAnchor());
        if (anchor != null) {
            result.setTextAnchor(anchor);
        }

        Double rotationAngle = at.getRotationAngle();
        if (rotationAngle != null) {
            result.setRotationAngle(rotationAngle * Math.PI / 180.0);
        }

        return result;
    }

    private XYTextAnnotation createXYTextAnnotation(UITextAnnotation at) {
        double x = Coercion.coerceToDouble(at.getxValue());
        double y = Coercion.coerceToDouble(at.getyValue());
        return createXYTextAnnotation(at, x, y);
    }

    private XYTextAnnotation createTimeSeriesTextAnnotation(UITextAnnotation at, Class timePeriodClass) {
        Date time = ChartUtils.convertDate(at.getxValue());
        if (time == null) {
            return null; // FIXME
        }

        RegularTimePeriod timePeriod = RegularTimePeriod.createInstance
            (timePeriodClass, time, RegularTimePeriod.DEFAULT_TIME_ZONE);

        double x = timePeriod.getFirstMillisecond();
        double y = Coercion.coerceToDouble(at.getyValue());
        return createXYTextAnnotation(at, x, y);
    }

    private XYTextAnnotation createXYTextAnnotation(UITextAnnotation at, double x, double y) {
        XYTextAnnotation result;

        if (at.isDrawArrow()) {
            Double angle = at.getArrowAngle();
            if (angle == null)
                angle = 0.0;
            XYPointerAnnotation pointer = new XYPointerAnnotation
                (at.getText(), x, y, angle*Math.PI/180.0);

            Double length = at.getArrowLength();
            if (length != null) {
                pointer.setBaseRadius(length);
            }

            Paint arrowColor = at.getArrowColor();
            if (arrowColor == null)
                arrowColor = at.getColor();
            if (arrowColor != null)
                pointer.setArrowPaint(arrowColor);

            result = pointer;
        } else {
            result = new XYTextAnnotation(at.getText(), x, y);
        }

        Font font = at.getFont();
        if (font != null) {
            result.setFont(font);
        }

        Paint color = at.getColor();
        if (color != null) {
            result.setPaint(color);
        }

        TextAnchor anchor = getTextAnchor(at.getAnchor());
        if (anchor != null) {
            result.setTextAnchor(anchor);
        }

        Double rotationAngle = at.getRotationAngle();
        if (rotationAngle != null) {
            result.setRotationAngle(rotationAngle * Math.PI / 180.0);
        }

        return result;
    }

    private TextAnchor getTextAnchor(PositionType position) {
        if (position != null) {
            switch (position) {
            case Left:          return TextAnchor.CENTER_LEFT;
            case Right:         return TextAnchor.CENTER_RIGHT;
            case Top:           return TextAnchor.TOP_CENTER;
            case Bottom:        return TextAnchor.BOTTOM_CENTER;
            case TopLeft:       return TextAnchor.TOP_LEFT;
            case TopRight:      return TextAnchor.TOP_RIGHT;
            case BottomLeft:    return TextAnchor.BOTTOM_LEFT;
            case BottomRight:   return TextAnchor.BOTTOM_RIGHT;
            case LeftTop:       return TextAnchor.TOP_LEFT;
            case LeftBottom:    return TextAnchor.BOTTOM_LEFT;
            case RightTop:      return TextAnchor.TOP_RIGHT;
            case RightBottom:   return TextAnchor.BOTTOM_RIGHT;
            case Center:        return TextAnchor.CENTER;
            case Stretch:       return TextAnchor.CENTER;
            }
        }
        return null;
    }

    protected void setToolTipGenerator(JFreeChart chart, UIChart comp) {
        if (!comp.isShowItemTips()) {
            return;
        }

        Plot plot = chart.getPlot();
        if (plot instanceof CategoryPlot) {
            setCategoryToolTipGenerator((CategoryPlot)plot, comp);
        } else if (plot instanceof XYPlot) {
            setXYToolTipGenerator((XYPlot)plot, comp);
        }
    }

    private void setCategoryToolTipGenerator(CategoryPlot plot, UIChart comp) {
        NumberFormat format;

        UIAxis axis = comp.getyAxis();
        if (axis != null && axis.getItemTipFormat() != null) {
            format = new DecimalFormat(axis.getItemTipFormat());
        } else {
            format = NumberFormat.getInstance();
        }

        plot.getRenderer().setToolTipGenerator(
            new StandardCategoryToolTipGenerator("<h3>{1}</h3>{0} = {2}", format)
        );
    }

    private void setXYToolTipGenerator(XYPlot plot, UIChart comp) {
        UIAxis xAxis = comp.getxAxis();
        UIAxis yAxis = comp.getyAxis();

        String xLabel = comp.getxAxisLabel();
        if (xLabel == null && xAxis != null)
            xLabel = xAxis.getLabel();
        String yLabel = comp.getyAxisLabel();
        if (yLabel == null && yAxis != null)
            yLabel = yAxis.getLabel();

        String labelFormat;
        if (xLabel != null && yLabel != null) {
            labelFormat = String.format("<h3>{0}</h3>%s: {1}<br/>%s: {2}", xLabel, yLabel);
        } else {
            labelFormat = "<h3>{0}</h3>({1}, {2})";
        }

        if (comp.getDataSeries() instanceof UITimeSeries) {
            DateFormat xfmt;
            if (xAxis != null && xAxis.getItemTipFormat() != null) {
                xfmt = new SimpleDateFormat(xAxis.getItemTipFormat());
            } else {
                xfmt = DateFormat.getInstance();
            }

            NumberFormat yfmt;
            if (yAxis != null && yAxis.getItemTipFormat() != null) {
                yfmt = new DecimalFormat(yAxis.getItemTipFormat());
            } else {
                yfmt = NumberFormat.getInstance();
            }

            plot.getRenderer().setToolTipGenerator(
                new StandardXYToolTipGenerator(labelFormat, xfmt, yfmt)
            );
        } else {
            NumberFormat xfmt;
            if (xAxis != null && xAxis.getItemTipFormat() != null) {
                xfmt = new DecimalFormat(xAxis.getItemTipFormat());
            } else {
                xfmt = NumberFormat.getInstance();
            }

            NumberFormat yfmt;
            if (yAxis != null && yAxis.getItemTipFormat() != null) {
                yfmt = new DecimalFormat(yAxis.getItemTipFormat());
            } else {
                yfmt = NumberFormat.getInstance();
            }

            plot.getRenderer().setToolTipGenerator(
                new StandardXYToolTipGenerator(labelFormat, xfmt, yfmt)
            );
        }
    }

    protected void encodeChartImage(FacesContext context, UIChart component, JFreeChart chart)
        throws IOException
    {
        int width = component.getWidth();
        int height = component.getHeight();
        ChartRenderingInfo info = null;

        if (component.isShowItemTips()) {
            info = new ChartRenderingInfo();
        }

        BufferedImage image = chart.createBufferedImage(width, height, info);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        ImageIO.write(image, "PNG", stream);
        byte[] data = stream.toByteArray();

        ChartKeeper keeper = ChartKeeper.getInstance(context);
        String filename = keeper.save(data, ".png");

        ResourceManager rm = ResourceManager.getInstance(context);
        String url = rm.getServiceResourceURL("chart", filename);

        if (isAjaxResponse(context)) {
            AjaxResponseWriter out = (AjaxResponseWriter)context.getResponseWriter();
            out.writeAttributeScript(component.getClientId(context), "src", url);
        } else {
            ResponseWriter out = context.getResponseWriter();
            out.startElement("img", component);
            out.writeAttribute("id", component.getClientId(context), "clientId");
            out.writeURIAttribute("src", url, null);
            renderPassThruAttributes(out, component);
            out.endElement("img");
        }

        if (info != null) {
            encodeItemTips(context, component, info);
        }
    }

    private void encodeItemTips(FacesContext context, UIChart comp, ChartRenderingInfo info)
        throws IOException
    {
        StringBuilder buf = new StringBuilder();

        buf.append("Ext.om.AreaTips.init();\n");
        buf.append("Ext.om.AreaTips.register({");
        buf.append("target:'").append(comp.getClientId(context)).append("'");
        buf.append(",trackMouse:true");
        buf.append(",areas:[");

        EntityCollection entities = info.getEntityCollection();
        Iterator it = entities.iterator();
        while (it.hasNext()) {
            ChartEntity entity = (ChartEntity)it.next();
            String tooltip = entity.getToolTipText();
            if (tooltip != null) {
                buf.append("{");
                buf.append("shape:'").append(entity.getShapeType()).append("'");
                buf.append(",coords:[").append(entity.getShapeCoords()).append("]");
                buf.append(",text:").append(HtmlEncoder.enquote(tooltip));
                buf.append("},");
            }
        }

        if (buf.charAt(buf.length()-1) == ',') {
            buf.setLength(buf.length()-1);
        }

        buf.append("]});\n");

        ResourceManager rm = ResourceManager.getInstance(context);
        if (isAjaxResponse(context)) {
            AjaxResponseWriter out = (AjaxResponseWriter)context.getResponseWriter();
            out.writeScript("OM.ajax.loadScript('" +
                            rm.getResourceURL("/ext/om/AreaTips.js") +
                            "');\n");
            out.writeScript(buf.toString());
        } else {
            YuiExtResource resource = YuiExtResource.register(rm, "Ext.om.AreaTips");
            resource.addInitScript(buf.toString());
        }
    }
}
