/*
 * $Id: HistogramData.java,v 1.2 2007/12/11 04:14:14 jacky Exp $
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

import javax.faces.model.ArrayDataModel;
import java.util.Random;

import org.operamasks.faces.annotation.ManagedBean;
import org.operamasks.faces.annotation.ManagedBeanScope;

@ManagedBean(scope=ManagedBeanScope.SESSION)
public class HistogramData extends ArrayDataModel
{
    private double min;
    private double max;
    private double mean;
    private double std;

    public HistogramData() {
        this(1000);
    }

    public HistogramData(int numRows) {
        createData(numRows);
    }

    public void randomize() {
        createData(1000);
    }

    private void createData(int numRows) {
        // create random data
        Random random = new Random();
        Double[] data = new Double[numRows];
        double offset = random.nextInt(6) + 2;
        for (int i = 0; i < numRows; i++) {
            data[i] = random.nextGaussian() + offset;
        }
        setWrappedData(data);

        // compute mean and std
        min = max = data[0];
        double sum = 0.0;
        for (int i = 0; i < numRows; i++) {
            sum += data[i];
            min = Math.min(min, data[i]);
            max = Math.max(max, data[i]);
        }
        this.mean = sum / numRows;

        sum = 0.0;
        for (int i = 0; i < numRows; i++) {
            sum += (data[i] - this.mean) * (data[i] - this.mean);
        }
        this.std = Math.sqrt(sum / numRows);
    }

    public double getMin() {
        return min;
    }

    public double getMax() {
        return max;
    }

    public double getMean() {
        return mean;
    }

    public double getStd() {
        return std;
    }

    public double normalDistribution(double x) {
        return Math.exp(-1.0 * (x-mean)*(x-mean) / (2*std*std))
             / Math.sqrt(2*Math.PI*std*std);
    }
}
