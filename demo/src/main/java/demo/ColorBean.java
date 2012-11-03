/*
 * $Id: ColorBean.java,v 1.2 2007/12/11 04:14:14 jacky Exp $
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
package demo;

import java.io.Serializable;

import javax.faces.model.SelectItem;

import org.operamasks.faces.annotation.ManagedBean;
import org.operamasks.faces.annotation.ManagedBeanScope;

@ManagedBean(scope= ManagedBeanScope.SESSION)
public class ColorBean implements Serializable
{
    public static final String[] COLORS = { "Red", "Green", "Blue" };

    private String color = null;
    private String[] multiColors = new String[0];
    
    public ColorBean() {
        color = "Red";
    }

    private SelectItem[] colors = {
        new SelectItem("Red", "Red Color"),
        new SelectItem("Green", "Green Color"),
        new SelectItem("Blue", "Blue Color")
    };

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String[] getMultiColors() {
        return multiColors;
    }

    public void setMultiColors(String[] multiColors) {
        this.multiColors = multiColors;
    }

    public String getColorCombination() {
        int c = 0x1000000;
        for (int i = 0; i < multiColors.length; i++) {
            if (multiColors[i].equals("Red")) {
                c |= 0xFF0000;
            } else if (multiColors[i].equals("Green")) {
                c |= 0x00FF00;
            } else if (multiColors[i].equals("Blue")) {
                c |= 0x0000FF;
            }
        }
        return "#" + Integer.toHexString(c).substring(1);
    }

    public SelectItem[] getColors() {
        return colors;
    }
    public String changeColors() {
        SelectItem black = null; 
        colors = new SelectItem[] {
            new SelectItem("Red", "Red Color"),
            new SelectItem("Green", "Green Color"),
            new SelectItem("Blue", "Blue Color"),
            black = new SelectItem("Black", "Black Color")
        };
        setColor((String)black.getValue());
        return null;
    }
}