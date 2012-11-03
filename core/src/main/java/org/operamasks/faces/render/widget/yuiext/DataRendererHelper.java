/*
 * $Id: DataRendererHelper.java,v 1.18 2008/04/29 08:03:41 jacky Exp $
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

package org.operamasks.faces.render.widget.yuiext;

import static org.operamasks.faces.util.FacesUtils.encodeComponent;
import static org.operamasks.faces.util.FacesUtils.encodeComponentChildren;
import static org.operamasks.faces.util.FacesUtils.getFormattedValue;
import static org.operamasks.resources.Resources.MVB_MISSING_COMPONENT_ATTRIBUTE;
import static org.operamasks.resources.Resources._T;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import javax.el.ELException;
import javax.el.MethodExpression;
import javax.faces.FacesException;
import javax.faces.component.UIColumn;
import javax.faces.component.UIComponent;
import javax.faces.component.UIData;
import javax.faces.component.ValueHolder;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;

import org.operamasks.faces.component.widget.grid.UIDataGrid;
import org.operamasks.faces.component.widget.grid.UIIdColumn;
import org.operamasks.faces.component.widget.grid.UIInputColumn;
import org.operamasks.faces.component.widget.grid.UIOutputColumn;
import org.operamasks.faces.render.resource.ResourceManager;
import org.operamasks.faces.util.FacesUtils;
import org.operamasks.faces.util.HtmlEncoder;
import org.operamasks.org.json.simple.JSONArray;
import org.operamasks.org.json.simple.JSONObject;
import org.operamasks.util.Base64;

public class DataRendererHelper
{
    public static final Object SERVER_ROW_INDEX = "_serverRowIndex";
    public static final String CALLBACK_FUNCTION = "callback";
    public static final String GRID_STATE_PARAM = "_selfstate";
    public static final String JSON_TOTAL = "totalcount";
    public static final String JSON_ROOT = "records";
    public static final String JSON_VIEWSTATE = "viewState";
    public static final String JSON_PARAMS = "params";

    private DataRendererHelper() {}

    public static List<UIColumn> getColumns(UIComponent component) {
        List<UIColumn> result = new ArrayList<UIColumn>();
        for (UIComponent kid : component.getChildren()) {
            if ((kid instanceof UIColumn) && kid.isRendered())
                result.add((UIColumn)kid);
        }
        return result;
    }

    public static List<UIColumn> getOutputColumns(UIComponent component) {
        List<UIColumn> result = new ArrayList<UIColumn>();
        for (UIComponent kid : component.getChildren()) {
            if ((kid instanceof UIOutputColumn) && kid.isRendered())
                result.add((UIOutputColumn)kid);
        }
        return result;
    }

    public static UIOutputColumn getOutputColumnById(UIComponent component, String id) {
        for (UIComponent kid : component.getChildren()) {
            if ((kid instanceof UIOutputColumn) && kid.isRendered()) {
                String columnId = kid.getId();
                if (columnId != null && columnId.equals(id)) {
                    return (UIOutputColumn) kid;
                }
            }
        }
        return null;
    }

    
    public static List<UIInputColumn> getInputColumns(UIComponent component) {
        List<UIInputColumn> result = new ArrayList<UIInputColumn>();
        for (UIComponent kid : component.getChildren()) {
            if ((kid instanceof UIInputColumn) && kid.isRendered())
                result.add((UIInputColumn)kid);
        }
        return result;
    }

    public static String getIdColumn(UIComponent component) {
        for (UIComponent kid : component.getChildren()) {
            if ((kid instanceof UIIdColumn) && kid.isRendered()) {
                return kid.getId();
            }
        }
        return null;
    }

    public static String encodeRecordDefinition(List<UIColumn> columns) {
        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < columns.size(); i++) {
            UIColumn column = columns.get(i);
            if (i > 0)
                buf.append(",");
            buf.append('\'').append(column.getId()).append('\'');
        }
        if(buf.length() > 0){
            buf.append(',').append('\'').append(SERVER_ROW_INDEX).append('\'');
        }
        return buf.toString();
    }

    public static String encodeRecordValue(List<UIColumn> columns, Object rowData, StringBuilder modified, String modifiedVar) {
        Map preData = new HashMap();;
        if (rowData != null) {
            if (rowData instanceof Map) {
                preData = (Map) rowData;
            } else {
                Class<?> rowClass = rowData.getClass();
                try {
                    BeanInfo beanInfo = Introspector.getBeanInfo(rowClass);
                    PropertyDescriptor[] props = beanInfo.getPropertyDescriptors();
                    for (PropertyDescriptor prop : props) {
                        if (prop.getReadMethod() != null) {
                            preData.put(prop.getName(), prop.getReadMethod());
                        }
                    }
                } catch (IntrospectionException e) {
                    // ignore
                }
            }
        }
        StringBuilder buf = new StringBuilder();
        buf.append("{");
        for (int i = 0; i < columns.size(); i++) {
            if (i > 0)
                buf.append(",");
            UIColumn column = columns.get(i);
            String columnId = column.getId();
            buf.append(columnId).append(":");
            Object value = preData.get(columnId);
            if (value instanceof Method) {
                try {
                    value = ((Method)value).invoke(rowData);
                } catch (Exception e) {
                    value = null;
                }
            }
            if (value != null) {
                String val = getFormattedValue(FacesContext.getCurrentInstance(), column, value);
                modified.append(modifiedVar).append("['").append(columnId).append("']");
                modified.append("='").append(val).append("';\n");
                buf.append("'").append(val).append("'");
            } else {
                buf.append("null");
            }
            
        }
        if(buf.length() > 0){
            buf.append(",").append(SERVER_ROW_INDEX).append(":null");
        }
        buf.append("}\n");
        return buf.toString();
    }

    public static String createRecord(List<UIColumn> columns) {
        StringBuilder buf = new StringBuilder();
        buf.append("[");
        for (int i = 0; i < columns.size(); i++) {
            if (i > 0)
                buf.append(",");
            buf.append("{ name:");
            UIColumn column = columns.get(i);
            buf.append('\'').append(column.getId()).append("'}");
        }
        if(buf.length() > 0){
            buf.append(",{").append("name:'").append(SERVER_ROW_INDEX).append("'}");
        }
        buf.append("]");
        return buf.toString();
    }

    public static String encodeArrayData(FacesContext context,
                                         UIData data,
                                         List<UIColumn> columns)
    {
        return encodeArrayData(context, data, columns, data.getFirst(), data.getRows());
    }

    public static String encodeArrayData(FacesContext context,
                                         UIData data,
                                         List<UIColumn> columns,
                                         int rowIndex, int rows)
    {
        StringBuilder buf = new StringBuilder();

        for (int curRow = 0; rows == 0 || curRow < rows; curRow++, rowIndex++) {
            data.setRowIndex(rowIndex);
            if (!data.isRowAvailable()) {
                break;
            }

            if (curRow > 0)
                buf.append(",\n");
            buf.append("[");
            for (int i = 0; i < columns.size(); i++) {
                UIColumn column = columns.get(i);
                Object value = getColumnValue(context, data, column);
                if (i > 0) buf.append(",");
                buf.append(HtmlEncoder.enquote(value.toString()));
            }
            buf.append("]");
        }

        data.setRowIndex(-1);
        return buf.toString();
    }

    public static JSONArray encodeJsonData(FacesContext context,
                                           UIData data,
                                           List<UIColumn> columns)
    {
        return encodeJsonData(context, data, columns, data.getFirst(), data.getRows());
    }
    
    @SuppressWarnings("unchecked")
    public static JSONArray encodeJsonData(FacesContext context,
                                           UIData data,
                                           List<UIColumn> columns,
                                           int rowIndex, int rows)
    {
        JSONArray json = new JSONArray();

        for (int curRow = 0; rows == 0 || curRow < rows; curRow++, rowIndex++) {
            data.setRowIndex(rowIndex);
            if (!data.isRowAvailable()) {
                break;
            }

            JSONObject rowData = new JSONObject();
            for (UIColumn column : columns) {
                Object value = getColumnValue(context, data, column);
                Object key = column.getId();
                if (key != null) {
                	rowData.put(column.getId(), value);
                } else {
                	throw new FacesException(_T(MVB_MISSING_COMPONENT_ATTRIBUTE, FacesUtils.getComponentDesc(column), "id"));
                }
            }
            rowData.put(SERVER_ROW_INDEX, rowIndex);
            json.add(rowData);
        }

        data.setRowIndex(-1);
        return json;
    }

    public static void sendJsonData(FacesContext context, JSONObject json)
        throws IOException
    {
        ExternalContext ectx = context.getExternalContext();
        HttpServletResponse response = (HttpServletResponse)ectx.getResponse();
        response.setHeader("Cache-Control", "no-cache");
        response.setHeader("Pragma", "no-cache");
        response.setContentType("text/json;charset=UTF-8");
        Map<String,String> paramMap = context.getExternalContext().getRequestParameterMap();
        String callback = paramMap.get(CALLBACK_FUNCTION);
        String script = json.toString();
        if(callback != null) {
            script = callback.concat("(").concat(script).concat(");");
        }
        response.getWriter().write(script);
        
        context.responseComplete();
    }
    
    public static Object getColumnValue(FacesContext context, UIData data, UIColumn column) {
        // If there is a renderer method then use it to render column value
        if (column instanceof UIOutputColumn) {
            MethodExpression formatter = ((UIOutputColumn)column).getFormatter();
            if (formatter != null) {
                try {
                    Object[] args = { column, data.getRowData() };
                    String text = (String)formatter.invoke(context.getELContext(), args);
                    return (text == null) ? "" : text;
                } catch (ELException ex) {
                    throw new FacesException(ex);
                }
            }
        }

        // If the column has value binding then render the formatted value
        if (column instanceof ValueHolder) {
            Object value = ((ValueHolder)column).getValue();
            if (value != null || column.getValueExpression("value") != null) {
                // If a CSS style explictly specified then render the formatted value
                String style = (String)column.getAttributes().get("style");
                String styleClass = (String)column.getAttributes().get("styleClass");
                if (style != null || styleClass != null) {
                    String text = "<span";
                    if (style != null)
                        text += " style=" + HtmlEncoder.enquote(style, '"');
                    if (styleClass != null)
                        text += " class=\"" + styleClass + "\"";
                    text += ">" + getFormattedValue(context, column, value) + "</span>";
                    return text;
                }

                // If a Converter specified for the column value then render the formatted value
                if (((ValueHolder)column).getConverter() != null) {
                    return getFormattedValue(context, column, value);
                }

                // For numeric value render the value literal.
                if (value instanceof Number) {
                    return value;
                }

                // For other values render the formatted value.
                // TODO: handle date value.
                return getFormattedValue(context, column, value);
            }
        }

        // Otherwise, render child components of column
        return encodeComponentChildren(context, column);
    }


    public static String getColumnHeader(UIColumn column) {
        Object header = column.getAttributes().get("columnHeader");
        if (header != null) {
            return header.toString();
        }

        UIComponent facet = column.getHeader();
        if (facet != null) {
            FacesContext context = FacesContext.getCurrentInstance();
            return encodeComponent(context, facet);
        }

        return null;
    }

    public static void registerSelectionModel() {
        ResourceManager rm = ResourceManager.getInstance(FacesContext.getCurrentInstance());
        YuiExtResource.register(rm, "Ext.grid.SelectionModel2");
    }

    @SuppressWarnings("unchecked")
    public static String loadData(FacesContext context, UIDataGrid data)
        throws IOException {
        return loadData(context, data, data.getSelectedRow(), data.getSelectedColumn());
    }
    
    @SuppressWarnings("unchecked")
    public static String loadData(FacesContext context, UIData data, int row, int col)
        throws IOException
    {
        List<UIColumn> columns = getColumns(data);
        Map<String,String> paramMap = context.getExternalContext().getRequestParameterMap();

        int totalRows = data.getRowCount();
        int rows, rowIndex;

        String startParam = paramMap.get("start");
        if (startParam != null) {
            rowIndex = Integer.parseInt(startParam);
            data.setFirst(rowIndex);
        } else {
            rowIndex = data.getFirst();
        }

        String limitParam = paramMap.get("limit");
        if (limitParam != null) {
            rows = Integer.parseInt(limitParam);
            data.setRows(rows);
        } else {
            rows = data.getRows();
        }

        JSONArray gridData = encodeJsonData(context, data, columns, rowIndex, rows);
        JSONObject json = new JSONObject();
        json.put(JSON_TOTAL, totalRows);
        json.put(JSON_ROOT, gridData);

//        String[] state = FacesUtils.getViewState(context);
//        if (state[0] != null) {
//            json.put(JSON_VIEWSTATE, state[0]);
//        }
        
        // extra params
        JSONObject params = new JSONObject();
        params.put("start", rowIndex);
        params.put("row", row);
        params.put("col", col);
        json.put(JSON_PARAMS, params);

        String callback = paramMap.get(DataRendererHelper.CALLBACK_FUNCTION);
        String script = json.toString();
        if(callback != null) {
            script = callback.concat("(").concat(script).concat(");");
        }
        return script;
        //sendJsonData(context, json);
    }

    public static String encodeGridState(FacesContext context, UIDataGrid grid)
    throws IOException
    {
        Object state = grid.processSaveState(context);
        List<UIColumn> columns = getOutputColumns(grid);
        Object[] states = new Object[columns.size() + 1];
        states[0] = state;
        for (int i = 0 ; i < columns.size() ; i++) {
            UIColumn column = columns.get(i);
            states[i+1] = column.processSaveState(context);
        }
        ByteArrayOutputStream bout;
        ObjectOutputStream out;
        bout = new ByteArrayOutputStream();
        out = new ObjectOutputStream(new GZIPOutputStream(bout));
        out.writeObject(states);
        out.close();
        byte[] bytes = bout.toByteArray();
        return Base64.encode(bytes);
    }

    public static void decodeGridState(FacesContext context, UIDataGrid grid, String viewString)
    throws IOException, ClassNotFoundException
    {
        byte[] bytes = Base64.decode(viewString);
        InputStream bin = new ByteArrayInputStream(bytes);
        bin = new GZIPInputStream(bin);
        final ClassLoader contextLoader = Thread.currentThread().getContextClassLoader();
        ObjectInputStream in = new ObjectInputStream(bin) {
            @Override protected Class resolveClass(ObjectStreamClass desc)
                throws ClassNotFoundException
            {
                return Class.forName(desc.getName(), true, contextLoader);
            }};
        Object state = in.readObject();
        Object[] states = (Object[])state;
        for (int i = 0 ; i < states.length ; i++) {
            if (i == 0) {
                grid.processRestoreState(context, states[0]);
            }
            else {
                UIOutputColumn output = new UIOutputColumn();
                output.processRestoreState(context, states[i]);
                grid.getChildren().add(output);
            }
        }
    }

}
