/*
 * $Id: JDKData.java,v 1.2 2007/12/11 04:14:14 jacky Exp $
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

import org.operamasks.faces.annotation.ManagedBean;
import org.operamasks.faces.annotation.ManagedBeanScope;

@ManagedBean(scope=ManagedBeanScope.REQUEST)
public class JDKData extends ArrayDataModel
{
    public static class JDKDataItem {
        private String releaseVersion;
        private int classCount;

        public JDKDataItem(String releaseVersion, int classCount) {
            this.releaseVersion = releaseVersion;
            this.classCount = classCount;
        }

        public String getReleaseVersion() {
            return releaseVersion;
        }

        public int getClassCount() {
            return classCount;
        }
    }

    private static final JDKDataItem[] data = new JDKDataItem[] {
        new JDKDataItem("JDK 1.0", 212),
        new JDKDataItem("JDK 1.1", 504),
        new JDKDataItem("JDK 1.2", 1520),
        new JDKDataItem("JDK 1.3", 1842),
        new JDKDataItem("JDK 1.4", 2991)
    };

    public JDKData() {
        super(data);
    }
}
