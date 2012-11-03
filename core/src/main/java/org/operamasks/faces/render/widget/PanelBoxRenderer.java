/*
 * $Id: PanelBoxRenderer.java,v 1.8 2008/03/13 02:48:20 lishaochuan Exp $
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

package org.operamasks.faces.render.widget;

import java.io.IOException;
import java.util.Formatter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.operamasks.faces.component.widget.UIPanelBox;
import org.operamasks.faces.render.html.HtmlRenderer;
import org.operamasks.faces.render.resource.AbstractResource;
import org.operamasks.faces.render.resource.ResourceManager;
import org.operamasks.faces.render.resource.ResourceProvider;
import org.operamasks.faces.render.widget.yuiext.YuiExtResource;

public class PanelBoxRenderer extends HtmlRenderer
    implements ResourceProvider
{
    public void encodeBegin(FacesContext context, UIComponent component)
        throws IOException
    {
        if (context == null || component == null)
            throw new NullPointerException();
        if (!component.isRendered())
            return;

        ResponseWriter out = context.getResponseWriter();

        String styleClass = (String)component.getAttributes().get("styleClass");
        if (styleClass == null || styleClass.indexOf("x-box") == -1)
            styleClass = (styleClass == null) ? "x-box" : ("x-box " + styleClass);
        styleClass += " " + getUniqueClassName(context, component);
        
        String style = (String)component.getAttributes().get("style");
        String width = "";
        String height = "";
        if(style != null){
            for(String cssAttr : style.split(";")){
                String[] parts = cssAttr.split(":");
                if("width".equals(parts[0].trim())){
                    width = parts[1];
                }
                if("height".equals(parts[0].trim())){
                    height = parts[1];
                }
            }
        }
        out.startElement("div", component);
        writeIdAttributeIfNecessary(context, out, component);
        out.writeAttribute("class", styleClass, "styleClass");
        out.writeAttribute("style", "width:" + width, null);

        out.startElement("div", null);
        out.writeAttribute("class", "x-box-tl", null);
        out.startElement("div", null);
        out.writeAttribute("class", "x-box-tr", null);
        out.startElement("div", null);
        out.writeAttribute("class", "x-box-tc", null);
        out.endElement("div");
        out.endElement("div");
        out.endElement("div");
        out.write("\n");

        out.startElement("div", null);
        out.writeAttribute("class", "x-box-ml", null);
        out.startElement("div", null);
        out.writeAttribute("class", "x-box-mr", null);

        String cstyle = (String)component.getAttributes().get("contentStyle");
        String cclass = (String)component.getAttributes().get("contentStyleClass");
        cclass = (cclass == null) ? "x-box-mc" : "x-box-mc " + cclass;
        out.startElement("div", null);
        out.writeAttribute("class", cclass, null);
        String styleHeight = String.format("height:%s;", height);
        if (cstyle != null) {
            out.writeAttribute("style", cstyle + styleHeight, null);
        }else{
            out.writeAttribute("style", styleHeight, null);
        }
    }

    public void encodeEnd(FacesContext context, UIComponent component)
        throws IOException
    {
        if (context == null || component == null)
            throw new NullPointerException();
        if (!component.isRendered())
            return;

        ResponseWriter out = context.getResponseWriter();

        out.endElement("div");
        out.endElement("div");
        out.endElement("div");
        out.write("\n");

        out.startElement("div", null);
        out.writeAttribute("class", "x-box-bl", null);
        out.startElement("div", null);
        out.writeAttribute("class", "x-box-br", null);
        out.startElement("div", null);
        out.writeAttribute("class", "x-box-bc", null);
        out.endElement("div");
        out.endElement("div");
        out.endElement("div");
        out.write("\n");

        out.endElement("div");
    }

    private String getUniqueClassName(FacesContext context, UIComponent component) {
        String uniqueClass = (String)component.getAttributes().get("uniqueClass");
        if (uniqueClass == null) {
            uniqueClass = "x-box-" + context.getViewRoot().createUniqueId();
            component.getAttributes().put("uniqueClass", uniqueClass);
        }
        return uniqueClass;
    }

    public void provideResource(ResourceManager rm, final UIComponent component) {
        if (isDefaultConfig(component)) {
            YuiExtResource.register(rm);
        } else {
            String id = "urn:panelBox:" + component.getClientId(FacesContext.getCurrentInstance());
            rm.registerResource(new AbstractResource(id) {
                public int getPriority() {
                    // just after system resources but before user resources
                    // so user resources can override style rules.
                    return LOW_PRIORITY - 100;
                }
                public void encodeBegin(FacesContext context) throws IOException {
                    encodeStyleSheet(context, component);
                }
            });
        }
    }

    private boolean isDefaultConfig(UIComponent component) {
        // If all attributes were not set then use default stylesheet from Ext.
        UIPanelBox box = (UIPanelBox)component;
        return box.getBgcolor() == null
            && box.getColor() == null
            && box.getColor2() == null
            && box.getBorder() < 0
            && box.getBorderColor() == null
            && box.getBorderRadius() < 0
            && box.getRoundedCorners() == null;
    }

    private void encodeStyleSheet(FacesContext context, UIComponent component)
        throws IOException
    {
        UIPanelBox box = (UIPanelBox)component;
        String id = getUniqueClassName(context, component);

        String bgcolor = box.getBgcolor();
        String color = box.getColor();
        String color2 = box.getColor2();
        String color3 = box.getColor3();
        int gradientExtent = box.getGradientExtent();
        int borderWidth = box.getBorder();
        String borderColor = box.getBorderColor();
        int borderRadius = box.getBorderRadius();
        String roundedCorners = box.getRoundedCorners();

        // Determine which corner is rounded
        boolean tl, tr, bl, br;
        if (roundedCorners == null || roundedCorners.equals("all")) {
            tl = tr = bl = br = true;
        } else if (roundedCorners.equals("none")) {
            tl = tr = bl = br = false;
        } else {
            tl = tr = bl = br = false;
            for (String s : roundedCorners.split(",")) {
                if (s.equals("tl")) {
                    tl = true;
                } else if (s.equals("tr")) {
                    tr = true;
                } else if (s.equals("bl")) {
                    bl = true;
                } else if (s.equals("br")) {
                    br = true;
                }
            }
        }

        // Determine the radius of rounded corner, or border width if no rounded corner
        // configured. This value is a quick test to see if whether the corners should
        // be displayed.
        int radius = 0;
        if (borderWidth > borderRadius) {
            borderRadius = 0; // border overlapps rounded corner
        }
        if (borderRadius > 0 && (tl | tr | bl | br)) {
            radius = borderRadius;
        } else if (borderWidth > 0) {
            radius = borderWidth;
        }

        // The border parameter used to generate rounded corner.
        String border = null;
        if (borderWidth > 0 || borderRadius > 0) {
            if (borderColor == null)
                borderColor = "black";
            border = borderWidth + "," + borderColor.replace("#", "") + "," + borderRadius;
        }

        // The fill parameter used to genrate gradient background.
        // If fill==null then use fillColor to fill background without
        // consult box resource service.
        String fill = null;
        String fillColor;
        if (color != null) {
            if (color2 != null) {
                if (gradientExtent <= 0)
                    gradientExtent = 16;
                fill = color.replace("#", "") + "," +
                       color2.replace("#", "") + "," +
                       gradientExtent;
                fillColor = (color3 != null) ? color3 : color2;
            } else {
                if (!color.equals("transparent") && radius > 0)
                    fill = color.replace("#", "");
                fillColor = color;
            }
        } else {
            fillColor = (bgcolor != null) ? bgcolor : "transparent";
        }

        ResourceManager rm = ResourceManager.getInstance(context);
        String url = rm.getServiceResourceURL("box-service", null);

        StringBuilder buf = new StringBuilder();
        Formatter fmt = new Formatter(buf);

        if (radius == 0) {
            // no rounded corner and border displayed
            fmt.format(".%s .x-box-tl {display:none;}\n", id);
        } else if (!(tl || tr)) {
            // no rounded corner displayed
            if (borderWidth > 0) {
                fmt.format(".%s .x-box-tl{\nborder-top:%dpx solid %s;\n}\n",
                           id, borderWidth, borderColor);
                fmt.format(".%s .x-box-tr{display:none;}\n", id);
            } else {
                fmt.format(".%s .x-box-tl{display:none;}\n", id);
            }
        } else {
            // Top Left corner
            fmt.format(".%s .x-box-tl{\n", id);
            if (tl) {
                if (fill == null && border == null) {
                    fmt.format("background:%s;\n", fillColor);
                } else {
                    fmt.format("background:transparent url(\"%s?a=tl", url);
                    if (fill != null)
                        fmt.format("&fill=%s", fill);
                    if (border != null)
                        fmt.format("&border=%s", border);
                    if (bgcolor != null)
                        fmt.format("&bg=%s", bgcolor.replace("#", ""));
                    buf.append("\") no-repeat top left;\n");
                }
            } else {
                if (borderWidth > 0) {
                    fmt.format("border-left:%dpx solid %s;\n", borderWidth, borderColor);
                }
            }
            buf.append("}\n");

            // Top Right corner
            fmt.format(".%s .x-box-tr{\n", id);
            if (tr) {
                if (fill == null && border == null) {
                    fmt.format("background:%s;\n", fillColor);
                } else {
                    fmt.format("background:transparent url(\"%s?a=tr", url);
                    if (fill != null)
                        fmt.format("&fill=%s", fill);
                    if (border != null)
                        fmt.format("&border=%s", border);
                    if (bgcolor != null)
                        fmt.format("&bg=%s", bgcolor.replace("#", ""));
                    buf.append("\") no-repeat top right;\n");
                }
            } else {
                if (borderWidth > 0) {
                    fmt.format("border-right:%dpx solid %s;\n", borderWidth, borderColor);
                }
            }
            buf.append("}\n");

            // Top Center
            fmt.format(".%s .x-box-tc{\n", id);
            if (fill == null && border == null) {
                fmt.format("background:%s;\n", fillColor);
            } else {
                fmt.format("background:%s url(\"%s?a=mc", fillColor, url);
                if (fill != null)
                    fmt.format("&fill=%s", fill);
                if (border != null)
                    fmt.format("&border=%s", border);
                if (bgcolor != null)
                    fmt.format("&bg=%s", bgcolor);
                buf.append("\") repeat-x 0 0;\n");
            }
            if (radius > 0 && (tl || tr)) {
                fmt.format("height:%dpx;\nmargin:0 %dpx 0 %dpx;\n",
                           radius, (tr ? radius : 0), (tl ? radius : 0));
            }
            buf.append("}\n");
        }

        // Middle Left
        fmt.format(".%s .x-box-ml{\n", id);
        if (fill != null) {
            fmt.format("background:%s url(\"%s?a=mc&fill=%s\") repeat-x 0 %dpx;\n",
                       fillColor, url, fill, ((tl||tr) ? -radius : 0));
        } else {
            fmt.format("background:%s;\n", fillColor);
        }
        if (borderWidth > 0)
            fmt.format("border-left:%dpx solid %s;\n", borderWidth, borderColor);
        buf.append("padding:0;\noverflow:hidden;\n}\n");

        // Middle Right
        fmt.format(".%s .x-box-mr{\n", id);
        if (borderWidth > 0)
            fmt.format("border-right:%dpx solid %s;\n", borderWidth, borderColor);
        buf.append("background:transparent;\npadding:0;\noverflow:hidden;\n}\n");

        // Middle Center
        if (radius > 0 && (tl || tr || bl || br)) {
            fmt.format(".%s .x-box-mc{\n", id);
            fmt.format("background:transparent;\n");
            fmt.format("margin:0 %dpx 0 %dpx;\npadding:4px 10px;\n",
                       ((tr||br) ? radius/2 : 0),
                       ((tl||bl) ? radius/2 : 0));
            buf.append("}\n");
        }

        if (radius == 0) {
            // no rounded corner or border displayed
            fmt.format(".%s .x-box-bl {display:none;}\n", id);
        } else if (!(bl || br)) {
            if (borderWidth > 0) {
                fmt.format(".%s .x-box-bl{\nborder-bottom:%dpx solid %s;\n}\n",
                           id, borderWidth, borderColor);
                fmt.format(".%s .x-box-br{display:none;}\n", id);
            } else {
                fmt.format(".%s .x-box-bl{display:none;}\n", id);
            }
        } else {
            // Bottom Left corner
            fmt.format(".%s .x-box-bl{\n", id);
            if (bl) {
                if (border != null) {
                    fmt.format("background:transparent url(\"%s?a=bl&border=%s&fill=%s",
                               url, border, fillColor.replace("#", ""));
                    if (bgcolor != null)
                        fmt.format("&bg=%s", bgcolor.replace("#", ""));
                    buf.append("\") no-repeat bottom left;\n");
                } else {
                    fmt.format("background:%s;\n", fillColor);
                }
            } else {
                if (borderWidth > 0) {
                    fmt.format("border-left:%dpx solid %s;\n", borderWidth, borderColor);
                }
            }
            buf.append("}\n");

            // Bottom Right corner
            fmt.format(".%s .x-box-br{\n", id);
            if (br) {
                if (border != null) {
                    fmt.format("background:transparent url(\"%s?a=br&border=%s&fill=%s",
                               url, border, fillColor.replace("#", ""));
                    if (bgcolor != null)
                        fmt.format("&bg=%s", bgcolor.replace("#", ""));
                    buf.append("\") no-repeat bottom right;\n");
                } else {
                    fmt.format("background:%s;\n", fillColor);
                }
            } else {
                if (borderWidth > 0) {
                    fmt.format("border-right:%dpx solid %s;\n", borderWidth, borderColor);
                }
            }
            buf.append("}\n");

            // Bottom Center
            fmt.format(".%s .x-box-bc{\n", id);
            fmt.format("background:%s;\n", fillColor);
            if (borderWidth > 0)
                fmt.format("border-bottom:%dpx solid %s;\n", borderWidth, borderColor);
            if (radius > 0 && (bl || br)) {
                fmt.format("height:%dpx;\nmargin:0 %dpx 0 %dpx;\n",
                           radius - borderWidth, (br ? radius : 0), (bl ? radius : 0));
            }
            buf.append("}\n");
        }

        if (radius > 0) {
            fmt.format(".%1$s .x-box-tl,.%1$s .x-box-tr,.%1$s .x-box-tc," +
                       ".%1$s .x-box-bl,.%1$s .x-box-br,.%1$s .x-box-bc",
                       id);
            fmt.format("{\npadding:0;\noverflow:hidden;\n}\n");
        }

        ResponseWriter out = context.getResponseWriter();
        out.startElement("style", null);
        out.writeAttribute("type", "text/css", null);
        out.write("\n");
        out.write(buf.toString());
        out.endElement("style");
        out.write("\n");
    }
}
