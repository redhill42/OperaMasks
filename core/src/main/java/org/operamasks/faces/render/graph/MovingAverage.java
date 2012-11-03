/*
 * $Id: MovingAverage.java,v 1.3 2007/07/02 07:37:44 jacky Exp $
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

import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesDataItem;
import org.jfree.data.time.RegularTimePeriod;

/**
 * A utility class for calculating moving average of time series data.
 */
public class MovingAverage
{
    /**
     * Create a new TimeSeries containing moving average values for
     * the given series.  If the series is empty (contains zero items), the
     * result is an empty series.
     *
     * @param source  the source series.
     * @param key  the series key
     * @param periodCount the number of periods used in the average calculation.
     * @param skip the number of initial periods to skip.
     * @return the moving average series
     */
    public static  XYSeries createMovingAverage(TimeSeries source,
                                                Comparable key,
                                                int periodCount,
                                                int skip)
    {
        if (source == null) {
            throw new NullPointerException();
        }
        if (periodCount < 1) {
            throw new IllegalArgumentException("period count must be greater than or equal to 1.");
        }

        XYSeries result = new XYSeries(key, false, true);

        if (source.getItemCount() > 0) {
            // if the initial averaging period is to be excluded, then
            // calculate the index of the first data item to have
            // an average calculated...
            long firstSerial = source.getDataItem(0).getPeriod().getSerialIndex() + skip;

            for (int i = source.getItemCount() - 1; i >= 0; i--) {
                // get the current data item...
                TimeSeriesDataItem current = source.getDataItem(i);
                RegularTimePeriod period = current.getPeriod();
                long serial = period.getSerialIndex();

                if (serial >= firstSerial) {
                    // work out the average for the earlier values...
                    int n = 0;
                    double sum = 0.0;
                    long serialLimit = period.getSerialIndex() - periodCount;
                    int offset = 0;
                    boolean finished = false;

                    while ((offset < periodCount) && !finished) {
                        if (i - offset >= 0) {
                            TimeSeriesDataItem item = source.getDataItem(i - offset);
                            RegularTimePeriod p = item.getPeriod();
                            Number v = item.getValue();
                            long currentIndex = p.getSerialIndex();
                            if (currentIndex > serialLimit) {
                                if (v != null) {
                                    sum = sum + v.doubleValue();
                                    n = n + 1;
                                }
                            } else {
                                finished = true;
                            }
                        }
                        offset = offset + 1;
                    }
                    if (n > 0) {
                        result.add(period.getFirstMillisecond(), sum / n);
                    } else {
                        result.add(period.getFirstMillisecond(), null);
                    }
                }
            }
        }

        return result;
    }

    /**
     * Create a new XYSeries containing the moving averages of one
     * series in the source dataset.
     *
     * @param source the source dataset.
     * @param series the series index (zero based).
     * @param key the serial key
     * @param period the averaging period.
     * @param skip the length of the initial skip period.
     * @return the data series
     */
    public static XYSeries createMovingAverage(XYDataset source,
                                               int series,
                                               Comparable key,
                                               double period,
                                               double skip)
    {
        if (source == null) {
            throw new NullPointerException();
        }
        if (period < Double.MIN_VALUE) {
            throw new IllegalArgumentException("period must be positive");
        }
        if (skip < 0.0) {
            throw new IllegalArgumentException("skip must be >= 0.0");
        }

        XYSeries result = new XYSeries(key);

        if (source.getItemCount(series) > 0) {
            // if the initial averaging period is to be excluded, then
            // calculate the lowest x-value to have an average calculated...
            double first = source.getXValue(series, 0) + skip;

            for (int i = source.getItemCount(series) - 1; i >= 0; i--) {
                // get the current data item...
                double x = source.getXValue(series, i);

                if (x >= first) {
                    // work out the average for the earlier values...
                    int n = 0;
                    double sum = 0.0;
                    double limit = x - period;
                    int offset = 0;
                    boolean finished = false;

                    while (!finished) {
                        if (i - offset >= 0) {
                            double xx = source.getXValue(series, i - offset);
                            Number yy = source.getY(series, i - offset);
                            if (xx > limit) {
                                if (yy != null) {
                                    sum = sum + yy.doubleValue();
                                    n = n + 1;
                                }
                            } else {
                                finished = true;
                            }
                        } else {
                            finished = true;
                        }
                        offset = offset + 1;
                    }
                    if (n > 0) {
                        result.add(x, sum / n);
                    }
                } else {
                    result.add(x, null);
                }
            }
        }

        return result;
    }
}
