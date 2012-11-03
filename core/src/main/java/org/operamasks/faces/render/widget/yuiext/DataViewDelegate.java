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
package org.operamasks.faces.render.widget.yuiext;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;

import org.operamasks.faces.component.widget.UIDataView;
import org.operamasks.faces.component.widget.UIPager;
import org.operamasks.faces.render.delegate.ViewDelegate;
import org.operamasks.faces.util.FacesUtils;

public class DataViewDelegate implements ViewDelegate {

    public void delegate(FacesContext context) throws IOException {
        ExternalContext ectx = context.getExternalContext();
        Map<String,String> paramMap = ectx.getRequestParameterMap();
        String dataViewId = paramMap.get(UIDataView.REQUEST_DATA_PARAM);
        if (dataViewId == null) {
            return;
        }
        
        Map<String, Object> sessionmap = ectx.getSessionMap();
        UIComponent component = (UIComponent) sessionmap.get(dataViewId + UIDataView.COMPONENT_KEY);
        if (component == null) {
            component = FacesUtils.getForComponent(context, dataViewId, context.getViewRoot());
        }
        if (component != null && (component instanceof UIDataView)) {
            try {
                List<UIPager> pagers = UIPager.getAllPagersFor(context, component);
                UIDataView dataView = ((UIDataView)component);
                if (pagers.size() > 0 && pagers.get(0).getPageSize() > 0) {
                    dataView.setRows(pagers.get(0).getPageSize());
                }
                
                Map<String, Object> session = context.getExternalContext().getSessionMap();
                String startParam = paramMap.get("start");
                Integer first = dataView.getFirst();
                if (startParam != null) {
                    first = Integer.parseInt(startParam);
                    dataView.setFirst(first);
                }
                session.put(dataView.getClientId(context) + UIDataView.FIRST_ROW_KEY, first);
                String limitParam = paramMap.get("limit");
                Integer limit = dataView.getRows();
                if (limitParam != null) {
                    limit = Integer.parseInt(limitParam);
                    dataView.setRows(limit);
                }
                session.put(dataView.getClientId(context) + UIDataView.ROWS_KEY, limit);
                
                String script = DataRendererHelper.loadData(context, ((UIDataView)component), -1, -1);
                script = "Ext.onReady(function(){\n" + script + "\n});" ;
                HttpServletResponse response = (HttpServletResponse) ectx.getResponse();
                response.setHeader("Cache-Control", "no-cache");
                response.setHeader("Pragma", "no-cache");
                response.setHeader("Connection", "close");
                //response.setHeader("Transfer-Encoding", "chunked");
                //response.setContentType("text/html; charset=UTF-8");
                response.getWriter().write(script);
            } finally {
                //FIXME: could not restore state from GET method
                //FIXME: could not create component tree from JSP TAG nor trans viewstate
                //sessionmap.remove(gridId+UIDataGrid.GRID_COMPONENT_KEY);
            }
            
        }
        context.responseComplete();
    }

}
