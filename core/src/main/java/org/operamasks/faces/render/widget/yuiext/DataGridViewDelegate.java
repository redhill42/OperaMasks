/*
 * $Id: DataGridViewDelegate.java,v 1.3 2008/03/24 05:21:49 patrick Exp $
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
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.component.UIComponent;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;

import org.operamasks.faces.component.widget.UIPager;
import org.operamasks.faces.component.widget.grid.UIDataGrid;
import org.operamasks.faces.render.delegate.ViewDelegate;
import org.operamasks.faces.util.FacesUtils;

public class DataGridViewDelegate implements ViewDelegate {
    
    protected Logger dumpLog = Logger.getLogger("org.operamasks.faces.dump");

    public void delegate(FacesContext context) throws IOException {
        ExternalContext ectx = context.getExternalContext();
        Map<String,String> paramMap = ectx.getRequestParameterMap();
        String gridId = paramMap.get(UIDataGrid.REQUEST_DATA_PARAM);
        if (gridId == null) {
            return;
        }
        
        Map<String, Object> sessionmap = ectx.getSessionMap();
        UIComponent component = (UIComponent) sessionmap.get(gridId+UIDataGrid.GRID_COMPONENT_KEY);
        if (component == null) {
            component = FacesUtils.getForComponent(context, gridId, context.getViewRoot());
        }
        if (component != null && (component instanceof UIDataGrid)) {
            try {
                List<UIPager> pagers = UIPager.getAllPagersFor(context, component);
                UIDataGrid grid = ((UIDataGrid)component);
                if (pagers.size() > 0 && pagers.get(0).getPageSize() > 0) {
                    grid.setRows(pagers.get(0).getPageSize());
                }
                
                Map<String, Object> session = context.getExternalContext().getSessionMap();
                String startParam = paramMap.get("start");
                Integer first = grid.getFirst();
                if (startParam != null) {
                    first = Integer.parseInt(startParam);
                    grid.setFirst(first);
                }
                session.put(grid.getClientId(context) + UIDataGrid.FIRST_ROW_KEY, first);
                String limitParam = paramMap.get("limit");
                Integer limit = grid.getRows();
                if (limitParam != null) {
                    limit = Integer.parseInt(limitParam);
                    grid.setRows(limit);
                }
                session.put(grid.getClientId(context) + UIDataGrid.GRID_ROWS_KEY, limit);
                
                String script = DataRendererHelper.loadData(context, ((UIDataGrid)component));
                script = "Ext.onReady(function(){\n" + script + "\n});" ;
                HttpServletResponse response = (HttpServletResponse) ectx.getResponse();
                response.setHeader("Cache-Control", "no-cache");
                response.setHeader("Pragma", "no-cache");
                response.setHeader("Connection", "close");
                //response.setHeader("Transfer-Encoding", "chunked");
                //response.setContentType("text/html; charset=UTF-8");
                if (dumpLog.isLoggable(Level.FINE)) {
                    dumpLog.fine(this.getClass().getSimpleName() + " : Data script of " + FacesUtils.getComponentDesc(grid) + " with id '" + grid.getId() + "' is [" + script + "]");
                }
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
