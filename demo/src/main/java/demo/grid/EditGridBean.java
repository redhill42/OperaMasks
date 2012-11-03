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

import java.util.Date;
import java.util.UUID;

import javax.faces.FacesException;

import org.operamasks.faces.annotation.Action;
import org.operamasks.faces.annotation.Bind;
import org.operamasks.faces.annotation.ManagedBean;
import org.operamasks.faces.annotation.ManagedBeanScope;
import org.operamasks.faces.annotation.ManagedProperty;
import org.operamasks.faces.component.widget.grid.UIDataGrid;
import org.operamasks.faces.component.widget.grid.UIEditDataGrid;
import org.operamasks.faces.component.widget.grid.UIDataGrid.SelectionModelType;
import org.operamasks.org.json.simple.JSONArray;
import org.operamasks.org.json.simple.JSONObject;

import demo.grid.GridDataModel.TrainRecord;

@ManagedBean(scope=ManagedBeanScope.SESSION)
public class EditGridBean {
    @SuppressWarnings("unused")
    @Bind(id="trainRecord")
    private UIEditDataGrid trainRecord;
    
    @ManagedProperty("#{GridDataModel}")
    private GridDataModel model;
    
    @Bind(id="trainRecord", attribute="addedData")
    private Object addedData;
    @Bind(id="trainRecord", attribute="modifiedData")
    private Object modifiedData;
    @Bind(id="trainRecord", attribute="removedData")
    private Object removedData;
    @Bind(id="trainRecord", attribute="bindBean")
    private String bindBean = "demo.grid.GridDataModel$TrainRecord";
    
    @SuppressWarnings("unused")
    @Bind(id="trainRecord", attribute="selectionModel")
    private SelectionModelType selectionType = SelectionModelType.cell;
    
    @Action(id="add")
    public void insert() {
        GridDataModel.TrainRecord defaultRecord = new TrainRecord();
        defaultRecord.setEmployeeName("新增的");
        defaultRecord.setTrainDate(new Date());
        defaultRecord.setTrainItem("AOM Train");
        trainRecord.insertRow(0, defaultRecord);
    }

    @Action(id="remove")
    public void remove() {
        trainRecord.remove();
    }

    @Action(id="save")
    public void save() {
        System.out.println(trainRecord.getTransData());
        try {
            if (addedData != null) {
                add((TrainRecord[]) addedData);
            } 
            if (modifiedData != null) {
                update((TrainRecord[]) modifiedData);
            }
            if (removedData != null) {
                remove((TrainRecord[]) removedData);
            }
            trainRecord.commit();
        } catch (Exception e) {
            throw new FacesException(e);
        }
    }

    private void remove(GridDataModel.TrainRecord[] data) {
        for (GridDataModel.TrainRecord record : data) {
            model.trainRecord.remove(model.getDataById(record.getId()));
        }
    }

    private void update(GridDataModel.TrainRecord[] data) {
        for (GridDataModel.TrainRecord record : data) {
            model.updateValue(record);
        }
    }

    private void add(GridDataModel.TrainRecord[] data) {
        for (GridDataModel.TrainRecord record : data) {
            record.setId(UUID.randomUUID().toString());
            model.trainRecord.add(record);
        }
    }

}
