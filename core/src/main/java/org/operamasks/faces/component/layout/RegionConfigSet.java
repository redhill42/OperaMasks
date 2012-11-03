/*
 * $Id: RegionConfigSet.java,v 1.5 2008/03/11 03:21:00 lishaochuan Exp $
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

import java.io.Serializable;

/**
 * @deprecated 此类已经废弃
 */
@Deprecated
public class RegionConfigSet implements Serializable
{
    private static final long serialVersionUID = 4218742869139859609L;

    private RegionConfig north;
    private RegionConfig south;
    private RegionConfig west;
    private RegionConfig east;
    private RegionConfig center;

    public RegionConfig get(Region region) {
        if (region == Region.north) {
            return north;
        } else if (region == Region.south) {
            return south;
        } else if (region == Region.west) {
            return west;
        } else if (region == Region.east) {
            return east;
        } else if (region == Region.center) {
            return center;
        } else {
            return null;
        }
    }

    public void set(Region region, RegionConfig config) {
        if (region == Region.north) {
            north = config;
        } else if (region == Region.south) {
            south = config;
        } else if (region == Region.west) {
            west = config;
        } else if (region == Region.east) {
            east = config;
        } else if (region == Region.center) {
            center = config;
        }
    }

    public RegionConfig getNorth() {
        return north;
    }

    public void setNorth(RegionConfig north) {
        this.north = north;
    }

    public RegionConfig getSouth() {
        return south;
    }

    public void setSouth(RegionConfig south) {
        this.south = south;
    }

    public RegionConfig getWest() {
        return west;
    }

    public void setWest(RegionConfig west) {
        this.west = west;
    }

    public RegionConfig getEast() {
        return east;
    }

    public void setEast(RegionConfig east) {
        this.east = east;
    }

    public RegionConfig getCenter() {
        return center;
    }

    public void setCenter(RegionConfig center) {
        this.center = center;
    }
}
