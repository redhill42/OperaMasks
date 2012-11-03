/*
 * $Id: BoxResourceService.java,v 1.5 2007/07/02 07:38:07 jacky Exp $
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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.operamasks.faces.render.resource.HttpResourceService;

public class BoxResourceService extends HttpResourceService
{
    public BoxResourceService() {
        super("box-service");
    }

    protected void service(HttpServletRequest request, HttpServletResponse response)
        throws IOException
    {
        // check referer to prevent abuse of this service
        //if (!checkReferer(request)) {
        //    response.sendError(HttpServletResponse.SC_NOT_FOUND);
        //    return;
        //}

        Box box;

        try {
            box = parseParameters(request);
        } catch (Exception ex) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        // draw box
        Dimension size = box.shape.getSize(box);
        BufferedImage image = new BufferedImage((int)size.getWidth(),
                                                (int)size.getHeight(),
                                                BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        box.shape.render(g, box);
        g.dispose();

        // send image response, the image can be cached if parameters doesn't change.
        response.reset();
        response.setContentType("image/png");
        response.setDateHeader("Last-Modified", System.currentTimeMillis());
        response.setHeader("Cache-Control", "max-age=86400");
//        AnimatedGifEncoder e = new AnimatedGifEncoder();
//        e.setTransparent( new Color( 0 , 0 , 0 , 0 ) ) ;
//        e.start(response.getOutputStream());
//        e.addFrame(image);
//        e.finish();
        ImageIO.write(image, "PNG", response.getOutputStream());
    }

    private boolean checkReferer(HttpServletRequest request) {
        String host = request.getServerName();
        if (host.equals("localhost")) {
            return true; // for debug use
        }

        String referer = request.getHeader("Referer");
        if (referer == null) {
            return false;
        }

        try {
            URI uri = new URI(referer);
            return host.equals(uri.getHost());
        } catch (URISyntaxException ex) {
            return false;
        }
    }

    // Implementation

    private static abstract class Renderer {
        public abstract Dimension getSize(Box box);
        public abstract void render(Graphics2D g, Box box);
    }

    private static class Box {
        Shape shape;
        int   height;
        Color background;
        Paint fill;
        int   borderWidth;
        Color borderColor;
        int   borderRadius;
        int   radius;
    }

    private static enum Shape {
        tl(new TLR()),  // top-left corner
        tr(new TRR()),  // top-right corner
        bl(new BLR()),  // bottom-left corner
        br(new BRR()),  // bottom-right corner
        mc(new MCR());  // middle-center pane

        private Renderer r;

        private Shape(Renderer r) {
            this.r = r;
        }

        public Renderer getRenderer() {
            return r;
        }

        public Dimension getSize(Box box) {
            return r.getSize(box);
        }

        public void render(Graphics2D g, Box box) {
            r.render(g, box);
        }
    }

    private Box parseParameters(HttpServletRequest request) {
        Box box = new Box();

        box.shape = Shape.valueOf(request.getParameter("a"));

        String height = request.getParameter("height");
        if (height != null) {
            box.height = parseInt(height, Integer.MAX_VALUE);
        }

        box.background = parseColor(request.getParameter("bg"));

        String fill = request.getParameter("fill");
        if (fill != null) {
            if (fill.indexOf(",") != -1) {
                String[] g = fill.split(",");
                Color startColor = parseColor(g[0]);
                Color endColor = parseColor(g[1]);
                int extent = parseInt(g[2], Integer.MAX_VALUE);

                box.fill = new GradientPaint(0, 0, startColor, 0, extent, endColor);
                if (box.height <= 0) {
                    box.height = extent;
                }
            } else {
                box.fill = parseColor(fill);
            }
        }

        String border = request.getParameter("border");
        if (border != null) {
            String g[] = border.split(",");
            box.borderWidth = parseInt(g[0], 50);
            box.borderColor = parseColor(g[1]);
            box.borderRadius = parseInt(g[2], 1500);
            if (box.borderWidth > box.borderRadius)
                box.borderRadius = 0; // overlapped, no rounded corner
            box.radius = Math.max(box.borderWidth, box.borderRadius);
        }

        return box;
    }

    private static Map<String,Color> colorMap = new HashMap<String, Color>();

    // Color table from CSS specification
    // http://www.w3.org/TR/CSS21/syndata.html#color-units
    static {
        colorMap.put("transparent", new Color(0,0,0,0));
        colorMap.put("aqua",    new Color(0,255,255));
        colorMap.put("black",   new Color(0,0,0));
        colorMap.put("blue",    new Color(0,0,255));
        colorMap.put("fuchsia", new Color(255,0,255));
        colorMap.put("gray",    new Color(128,128,128));
        colorMap.put("green",   new Color(0,128,0));
        colorMap.put("lime",    new Color(0,255,0));
        colorMap.put("maroon",  new Color(128,0,0));
        colorMap.put("navy",    new Color(0,0,128));
        colorMap.put("olive",   new Color(128,128,0));
        colorMap.put("orange",  new Color(255,165,0));
        colorMap.put("purple",  new Color(128,0,128));
        colorMap.put("red",     new Color(255,0,0));
        colorMap.put("silver",  new Color(192,192,192));
        colorMap.put("teal",    new Color(0,128,128));
        colorMap.put("white",   new Color(255,255,255));
        colorMap.put("yellow",  new Color(255,255,0));
    }

    private static Color parseColor(String s) {
        if (s == null || s.length() == 0) {
            return null;
        }

        Color c = colorMap.get(s);
        if (c != null) {
            return c;
        }

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

    private static int parseInt(String s, int max) {
        if (s == null || s.length() == 0) {
            return 0;
        }

        int i = Integer.parseInt(s);
        if (i < 0 || i > max) {
            throw new IllegalArgumentException();
        }
        return i;
    }

    // Renderer Implemenation

    private static abstract class CornerRenderer extends Renderer {
        public Dimension getSize(Box b) {
            if (b.radius > 0) {
                return new Dimension(b.radius, b.radius);
            } else {
                return new Dimension(1, 1);
            }
        }
    }

    private static class TLR extends CornerRenderer {
        public void render(Graphics2D g, Box b) {
            int r = b.borderRadius;
            int s = b.borderWidth;

            if (r > 0) {
                if (b.background != null) {
                    g.setPaint(b.background);
                    g.fillRect(0, 0, r, r);
                }
                if (b.fill != null) {
                    g.setPaint(b.fill);
                    g.fillArc(0, 0, r*2, r*2, 90, 90);
                }
                if (s > 0) {
                    g.setStroke(new BasicStroke(s));
                    g.setColor(b.borderColor);
                    g.drawArc(s/2, s/2, r*2-s, r*2-s, 90, 90);
                }
            } else if (s > 0) {
                g.setColor(b.borderColor);
                g.fillRect(0, 0, s, s);
            }
        }
    }

    private static class TRR extends CornerRenderer {
        public void render(Graphics2D g, Box b) {
            int r = b.borderRadius;
            int s = b.borderWidth;

            if (r > 0) {
                if (b.background != null) {
                    g.setPaint(b.background);
                    g.fillRect(0, 0, r, r);
                }
                if (b.fill != null) {
                    g.setPaint(b.fill);
                    g.fillArc(-r, 0, r*2, r*2, 0, 90);
                }
                if (s > 0) {
                    g.setStroke(new BasicStroke(s));
                    g.setColor(b.borderColor);
                    g.drawArc(-r+s/2, s/2, r*2-s, r*2-s, 0, 90);
                }
            } else if (s > 0) {
                g.setColor(b.borderColor);
                g.fillRect(0, 0, s, s);
            }
        }
    }

    private static class BLR extends CornerRenderer {
        public void render(Graphics2D g, Box b) {
            int r = b.borderRadius;
            int s = b.borderWidth;

            if (r > 0) {
                if (b.background != null) {
                    g.setPaint(b.background);
                    g.fillRect(0, 0, r, r);
                }
                if (b.fill != null) {
                    g.setPaint(b.fill);
                    g.fillArc(0, -r, r*2, r*2, 180, 90);
                }
                if (s > 0) {
                    g.setStroke(new BasicStroke(s));
                    g.setColor(b.borderColor);
                    g.drawArc(s/2, -r+s/2, r*2-s, r*2-s, 180, 90);
                }
            } else if (s > 0) {
                g.setColor(b.borderColor);
                g.fillRect(0, 0, s, s);
            }
        }
    }
    
    private static class BRR extends CornerRenderer {
        public void render(Graphics2D g, Box b) {
            int r = b.borderRadius;
            int s = b.borderWidth;

            if (r > 0) {
                if (b.background != null) {
                    g.setPaint(b.background);
                    g.fillRect(0, 0, r, r);
                }
                if (b.fill != null) {
                    g.setPaint(b.fill);
                    g.fillArc(-r, -r, r*2, r*2, 0, -90);
                }
                if (s > 0) {
                    g.setStroke(new BasicStroke(s));
                    g.setColor(b.borderColor);
                    g.drawArc(-r+s/2, -r+s/2, r*2-s, r*2-s, 0, -90);
                }
            } else if (s > 0) {
                g.setColor(b.borderColor);
                g.fillRect(0, 0, s, s);
            }
        }
    }

    private static class MCR extends Renderer {
        private static final int W = 1;

        public Dimension getSize(Box b) {
            if (b.height > 0) {
                return new Dimension(W, b.height);
            } else if (b.borderWidth > 0) {
                return new Dimension(W, b.borderWidth);
            } else {
                return new Dimension(1, 1);
            }
        }

        public void render(Graphics2D g, Box b) {
            int h = b.height;
            int s = b.borderWidth;

            if (b.fill != null) {
                g.setPaint(b.fill);
                g.fillRect(0, 0, W, h);
            }
            if (s > 0) {
                g.setColor(b.borderColor);
                g.fillRect(0, 0, W, s);
            }
        }
    }
}
