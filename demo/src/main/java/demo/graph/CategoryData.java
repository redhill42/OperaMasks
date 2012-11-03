/*
 * $Id: CategoryData.java,v 1.2 2007/12/11 04:14:14 jacky Exp $
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
public class CategoryData extends ArrayDataModel
{
    public CategoryData() {
        this(20);
    }

    public CategoryData(int numRows) {
        super(createData(numRows));
    }

    public void randomize() {
        setWrappedData(createData(20));
    }

    private static CategoryDataItem[] createData(int numRows) {
        Random r = new Random();
        CategoryDataItem[] data = new CategoryDataItem[numRows];
        for (int i = 0; i < numRows; i++) {
            data[i] = new CategoryDataItem("Category " + (i+1),
                                           r.nextInt(10)+1,
                                           r.nextInt(10)+1,
                                           r.nextInt(10)+1);
        }
        return data;
    }
}
