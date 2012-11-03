/*
 * $Id: ChartUtils.java,v 1.3 2007/07/02 07:37:44 jacky Exp $
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
import javax.faces.FacesException;
import javax.el.ValueExpression;
import java.util.Map;
import java.util.HashMap;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.Calendar;
import java.awt.Color;
import java.awt.Font;
import java.awt.Paint;
import java.awt.GradientPaint;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.text.ParseException;

public class ChartUtils
{
    private ChartUtils() {}

    // Format for ISO 8601 date -- "1994-10-06T08:49:37Z"
    private static final String ISO8601 = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    private static final DateFormat iso8601Format;

    static {
        iso8601Format = new SimpleDateFormat(ISO8601, Locale.US);
        iso8601Format.setTimeZone(TimeZone.getDefault());
    }

    public static Date convertDate(Object value) {
        if (value == null) {
            return null;
        } else if (value instanceof Date) {
            return (Date)value;
        } else if (value instanceof Calendar) {
            return ((Calendar)value).getTime();
        } else if (value instanceof Number) {
            return new Date(((Number)value).longValue());
        } else {
            // All literal date string must in ISO 8601 format.
            try {
                return iso8601Format.parse(value.toString());
            } catch (ParseException ex) {
                throw new FacesException(ex);
            }
        }
    }

    public static Date convertDate(ValueExpression ve) {
        return convertDate(ve.getValue(FacesContext.getCurrentInstance().getELContext()));
    }

    public static Paint convertColor(Object value) {
        if (value == null) {
            return null;
        } else if (value instanceof Paint) {
            return (Paint)value;
        } else {
            return parseColor(value.toString());
        }
    }

    public static Paint convertColor(ValueExpression ve) {
        return convertColor(ve.getValue(FacesContext.getCurrentInstance().getELContext()));
    }

    private static Map<String,Color> colorMap = new HashMap<String, Color>();

    static {
        colorMap.put("white", Color.white);
        colorMap.put("lightgray", Color.lightGray);
        colorMap.put("gray", Color.gray);
        colorMap.put("darkgray", Color.darkGray);
        colorMap.put("black", Color.black);
        colorMap.put("red", Color.red);
        colorMap.put("pink", Color.pink);
        colorMap.put("orange", Color.orange);
        colorMap.put("yellow", Color.yellow);
        colorMap.put("green", Color.green);
        colorMap.put("magenta", Color.magenta);
        colorMap.put("cyan", Color.cyan);
        colorMap.put("blue", Color.blue);
    }

    public static Color parseColor(String cs) {
        if (cs == null || cs.length() == 0) {
            return null;
        }

        Color c = colorMap.get(cs);
        if (c != null) {
            return c;
        }

        try {
            String s = cs.trim();

            if (s.startsWith("#")) {
                s = s.substring(1);
                if (s.length() == 3) {
                    int r = Integer.parseInt(s.substring(0,1), 16);
                    int g = Integer.parseInt(s.substring(1,2), 16);
                    int b = Integer.parseInt(s.substring(2,3), 16);
                    return new Color(r, g, b);
                } else if (s.length() == 6) {
                    int rgb = Integer.parseInt(s, 16);
                    return new Color(rgb, false);
                } else if (s.length() == 8) {
                    int argb = (int)Long.parseLong(s, 16);
                    return new Color(argb, true);
                } else {
                    throw new IllegalArgumentException();
                }
            }

            if (s.startsWith("rgb(") && s.endsWith(")")) {
                s = s.substring(4, s.length()-1);
                String[] ss = s.split(",");
                int r = Integer.parseInt(ss[0]);
                int g = Integer.parseInt(ss[1]);
                int b = Integer.parseInt(ss[2]);
                return new Color(r, g, b);
            }

            if (s.startsWith("rgba(") && s.endsWith(")")) {
                s = s.substring(5, s.length()-1);
                String[] ss = s.split(",");
                int r = Integer.parseInt(ss[0]);
                int g = Integer.parseInt(ss[1]);
                int b = Integer.parseInt(ss[2]);
                int a = Integer.parseInt(ss[3]);
                return new Color(r, g, b, a);
            }
        } catch (Exception ex) {
            // fall through
        }

        throw new IllegalArgumentException("Invalid color literal: " + cs);
    }

    public static Font convertFont(Object value) {
        if (value == null) {
            return null;
        } else if (value instanceof Font) {
            return (Font)value;
        } else {
            return parseFont(value.toString());
        }
    }

    public static Font convertFont(ValueExpression ve) {
        return convertFont(ve.getValue(FacesContext.getCurrentInstance().getELContext()));
    }

    public static Font parseFont(String s) {
        if (s == null || s.length() == 0) {
            return null;
        }

        try {
            String[] ss = s.split(",");
            String name = ss[0];
            String strStyle = ss[1];
            int size = Integer.parseInt(ss[2]);

            int style = 0;
            if (strStyle.indexOf("bold") != -1)
                style |= Font.BOLD;
            if (strStyle.indexOf("italic") != -1)
                style |= Font.ITALIC;

            return new Font(name, style, size);
        } catch (Exception ex) {
            throw new IllegalArgumentException("Invalid font literal: " + s);
        }
    }

    public static Object serialPaintObject(Paint paint) {
        if (paint instanceof Serializable) {
            return paint;
        } else if (paint instanceof GradientPaint) {
            return new SerialGradientPaint((GradientPaint)paint);
        } else {
            return null;
        }
    }

    private static class SerialGradientPaint extends GradientPaint
        implements Serializable
    {
        private static final long serialVersionUID = -6111029908120132859L;

        SerialGradientPaint(GradientPaint gp) {
            super(gp.getPoint1(), gp.getColor1(), gp.getPoint2(), gp.getColor2(), gp.isCyclic());
        }
    }
}
