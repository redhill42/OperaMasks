/*
 * $Id: CustomDrawingSupplier.java,v 1.3 2007/07/02 07:37:44 jacky Exp $
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

import java.awt.Paint;
import java.awt.Color;

import org.jfree.chart.plot.DefaultDrawingSupplier;

public class CustomDrawingSupplier extends DefaultDrawingSupplier
{
    public static Paint[] CUSTOM_COLOR_PALETTE = createCustomColorPalette();

    public CustomDrawingSupplier() {
        super(CUSTOM_COLOR_PALETTE,
              DEFAULT_OUTLINE_PAINT_SEQUENCE,
              DEFAULT_STROKE_SEQUENCE,
              DEFAULT_OUTLINE_STROKE_SEQUENCE,
              DEFAULT_SHAPE_SEQUENCE);
    }

    public CustomDrawingSupplier(Paint[] colorPalette) {
        super(colorPalette,
              DEFAULT_OUTLINE_PAINT_SEQUENCE,
              DEFAULT_STROKE_SEQUENCE,
              DEFAULT_OUTLINE_STROKE_SEQUENCE,
              DEFAULT_SHAPE_SEQUENCE);
    }
    
    private static Paint[] createCustomColorPalette() {
        return new Paint[] {
            new Color(81, 121, 214),
            new Color(102, 204, 102),
            new Color(239, 47, 65),
            new Color(255, 199, 0),
            new Color(97, 189, 242),
            new Color(255, 255, 204),
            new Color(117, 136, 221),
            new Color(47, 94, 140),
            new Color(7, 186, 206),
            new Color(186, 229, 92),
            new Color(186, 24, 113),
            new Color(255, 121, 0),
            new Color(199, 199, 199),
            new Color(173, 168, 255),
            new Color(47, 166, 117)
        };
    }
}
