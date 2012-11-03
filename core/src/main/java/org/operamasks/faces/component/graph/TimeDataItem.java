/*
 * $Id: TimeDataItem.java,v 1.3 2007/07/02 07:37:55 jacky Exp $
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

package org.operamasks.faces.component.graph;

import java.io.Serializable;
import java.util.Date;

/**
 * Represents one data item in a time series.
 */
public class TimeDataItem implements Serializable
{
    private static final long serialVersionUID = 3609174126518778306L;

    /** The time in the time series. */
    private Date time;

    /** The value associated with the time. */
    private Number value;

    /**
     * Constructs a new data item that associates a value with a time.
     *
     * @param time the time in the time series.
     * @param value the value associated with the time.
     */
    public TimeDataItem(Date time, Number value) {
        if (time == null) {
            throw new NullPointerException();
        }
        this.time = time;
        this.value = value;
    }

    /**
     * Constructs a new data item that associates a value with a time.
     *
     * @param time the time in the time series.
     * @param value the value associated with the time.
     */
    public TimeDataItem(Date time, double value) {
        this(time, new Double(value));
    }

    /**
     * Constructs a new data item that associates a value with a time.
     *
     * @param time the time in the time series.
     * @param value the value associated with the time.
     */
    public TimeDataItem(Date time, float value) {
        this(time, new Float(value));
    }

    /**
     * Constructs a new data item that associates a value with a time.
     *
     * @param time the time in the time series.
     * @param value the value associated with the time.
     */
    public TimeDataItem(Date time, long value) {
        this(time, new Long(value));
    }

    /**
     * Constructs a new data item that associates a value with a time.
     *
     * @param time the time in the time series.
     * @param value the value associated with the time.
     */
    public TimeDataItem(Date time, int value) {
        this(time, new Integer(value));
    }

    /**
     * Constructs a new data item that associates a value with a time.
     *
     * @param time the time in the time series.
     * @param value the value associated with the time.
     */
    public TimeDataItem(Date time, short value) {
        this(time, new Short(value));
    }

    /**
     * Constructs a new data item that associates a value with a time.
     *
     * @param time the time in the time series.
     * @param value the value associated with the time.
     */
    public TimeDataItem(Date time, byte value) {
        this(time, new Byte(value));
    }

    /**
     * Returns the time.
     *
     * @return the time.
     */
    public Date getTime() {
        return this.time;
    }

    /**
     * Returns the value.
     *
     * @return the value.
     */
    public Number getValue() {
        return this.value;
    }

    /**
     * Sets the value for this data item.
     *
     * @param value the value.
     */
    public void setValue(Number value) {
        this.value = value;
    }

    /**
     * Tests this object for equality with an arbitrary object.
     *
     * @param o the other object.
     * @return
     */
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TimeDataItem)) {
            return false;
        }

        TimeDataItem other = (TimeDataItem)o;

        if (this.time != null) {
            if (!this.time.equals(other.time)) {
                return false;
            }
        } else if (other.time != null) {
            return false;
        }

        if (this.value != null) {
            if (!this.value.equals(other.value)) {
                return false;
            }
        } else if (other.value != null) {
            return false;
        }

        return true;
    }

    /**
     * Returns a hash code.
     *
     * @return hash code
     */
    public int hashCode() {
        int result;
        result = (this.time != null ? this.time.hashCode() : 0);
        result = 29 * result + (this.value != null ? this.value.hashCode() : 0);
        return result;
    }
}
