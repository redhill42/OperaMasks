/*
 * $Id 
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
package demo.grid;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.operamasks.faces.annotation.Bind;
import org.operamasks.faces.annotation.DataModel;
import org.operamasks.faces.annotation.ManagedBean;
import org.operamasks.faces.annotation.ManagedBeanScope;
import org.operamasks.faces.annotation.ManagedProperty;

@ManagedBean(scope=ManagedBeanScope.SESSION)
public class GridDataModel 
{
    @SuppressWarnings("unused")
    @ManagedProperty
    javax.faces.model.SelectItem[] trainItems = {
            new javax.faces.model.SelectItem("Server Train", "应用服务器培训"),
            new javax.faces.model.SelectItem("MQ Train", "MQ服务器培训"),
            new javax.faces.model.SelectItem("Apusic Studio Train", "Apusic Studio培训"),
            new javax.faces.model.SelectItem("AOM Train", "AOM使用培训"),
    };

    @SuppressWarnings("unused")
    @DataModel
    List<TrainRecord> trainRecord = new ArrayList<TrainRecord>(Arrays.asList(new TrainRecord[]{
            new TrainRecord("左敏", "Server Train", 2),
            new TrainRecord("丁俊杰", "MQ Train", 1),
            new TrainRecord("罗帆", "Apusic Studio Train", 1),
            new TrainRecord("张东", "Server Train", 3),
            new TrainRecord("王冠雄", "MQ Train", 1),
            new TrainRecord("张勇", "AOM Train", 2),
    }));
    
    public TrainRecord getDataById(String id) {
        for (TrainRecord record : trainRecord) {
            if (record.getId().equals(id)) {
                return record;
            }
        }
        return null;
    }

    public void updateValue(TrainRecord newValue) {
        TrainRecord oldValue = getDataById(newValue.getId());
        if (oldValue != null) {
            oldValue.setEmployeeName(newValue.getEmployeeName());
            oldValue.setTrainDate(newValue.getTrainDate());
            oldValue.setTrainHours(newValue.getTrainHours());
            oldValue.setTrainItem(newValue.getTrainItem());
        }
    }
    
    public static final class TrainRecord {
        public TrainRecord() {
        }
        public TrainRecord(String employeeName, String trainItem, int trainHours) {
            this.id = UUID.randomUUID().toString();
            this.employeeName = employeeName;
            this.trainItem = trainItem;
            this.trainHours = trainHours;
            this.trainDate = new Date();
        }
        @Bind
        private String id;
        @Bind
        String employeeName;
        @Bind
        String trainItem;
        @Bind
        int trainHours;
        @Bind
        Date trainDate;
        public String getEmployeeName() {
            return employeeName;
        }
        public void setEmployeeName(String employeeName) {
            this.employeeName = employeeName;
        }
        public String getTrainItem() {
            return trainItem;
        }
        public void setTrainItem(String trainItem) {
            this.trainItem = trainItem;
        }
        public int getTrainHours() {
            return trainHours;
        }
        public void setTrainHours(int trainHours) {
            this.trainHours = trainHours;
        }
        public Date getTrainDate() {
            return trainDate;
        }
        public void setTrainDate(Date trainDate) {
            this.trainDate = trainDate;
        }
        public String getId() {
            return id;
        }
        public void setId(String id) {
            this.id = id;
        }
    }

}
