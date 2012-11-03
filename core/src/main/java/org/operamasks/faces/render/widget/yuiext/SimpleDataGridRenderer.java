/*
 * $Id: SimpleDataGridRenderer.java,v 1.6 2008/01/16 03:01:55 lishaochuan Exp $
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

import static org.operamasks.faces.render.widget.yuiext.DataRendererHelper.getColumnValue;
import static org.operamasks.faces.render.widget.yuiext.DataRendererHelper.getColumns;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.faces.component.NamingContainer;
import javax.faces.component.UIColumn;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.operamasks.faces.component.widget.grid.UIOutputColumn;
import org.operamasks.faces.component.widget.grid.UISimpleDataGrid;
import org.operamasks.faces.render.ajax.AjaxResponseWriter;
import org.operamasks.faces.render.html.HtmlRenderer;
import org.operamasks.faces.render.resource.AbstractResource;
import org.operamasks.faces.render.resource.ResourceManager;
import org.operamasks.faces.render.resource.ResourceProvider;

public class SimpleDataGridRenderer extends HtmlRenderer implements ResourceProvider {
	private static final int COLUMN_DEFAULT_WIDTH = 100;
	private static final int SELECTION_COLUMN_WIDTH = 30;
	private static final String ROW_SELECTIONS_PARAM = NamingContainer.SEPARATOR_CHAR + "_selections";

	public void decode(FacesContext context, UIComponent component) {
		if (context == null || component == null)
			throw new NullPointerException();
		if (!component.isRendered())
			return;

		String clientId = component.getClientId(context);

		Map<String, String> paramMap = context.getExternalContext().getRequestParameterMap();
		String selectionsParam = paramMap.get(clientId + ROW_SELECTIONS_PARAM);
		if (selectionsParam != null && selectionsParam.length() > 0) {
			String[] selectionsValue = selectionsParam.split(",");
			if (selectionsValue.length > 1) {
				int[] selections = new int[selectionsValue.length - 1];
				int i = 0;
				for (String s : selectionsValue) {
					if (!"".equals(s)) {
						try {
							selections[i++] = Integer.parseInt(s);
						} catch (Exception ex) {/* ignored */
						}
					}
				}
				((UISimpleDataGrid) component).setSelections(selections);
			}
		}
	}

	public void provideResource(ResourceManager manager, UIComponent component) {
		final UISimpleDataGrid grid = (UISimpleDataGrid) component;

		String id = "urn:simpleDatagrid:" + component.getClientId(FacesContext.getCurrentInstance());
		manager.registerResource(new AbstractResource(id) {
			public int getPriority() {
				// just after system resources but before user resources
				// so user resources can override style rules.
				return LOW_PRIORITY - 100;
			}

			public void encodeBegin(FacesContext context) throws IOException {
				encodeCssStyle(context, grid);
			}
		});
		manager.registerScriptResource("simpleDataGrid.js");
	}

	private void encodeCssStyle(FacesContext context, UISimpleDataGrid grid) throws IOException {
		StringBuffer buffer = new StringBuffer();
		buffer.append("<style>\n");
		List<UIColumn> columns = getColumns(grid);
		int cssIndex = 0;
		for (UIColumn column : columns) {
			int width = ((UIOutputColumn) column).getWidth();
			buffer.append(String.format(".%s-x-grid3-col-%s{width:%spx;overflow:hidden;}\n", getSimpleClientId(context, grid), cssIndex++, width > 0 ? width : COLUMN_DEFAULT_WIDTH));
		}
		buffer.append(".x-grid3-cell-text, .x-grid3-hd-text {padding:0px 0px}\n");
		buffer.append("</style>");
		context.getResponseWriter().write(buffer.toString());
	}

	@Override
	public void encodeBegin(FacesContext context, UIComponent component) throws IOException {
		if (context == null || component == null)
			throw new NullPointerException();
		if (!component.isRendered())
			return;
		// render outer div start
		ResponseWriter out = context.getResponseWriter();
		UISimpleDataGrid grid = (UISimpleDataGrid)component;
		out.startElement("div", null);
		out.writeAttribute("id", getOuterClientId(context, component), null);
		out.writeAttribute("style", "background-color:white;overflow:auto;" + grid.getStyle(), null);
	}

	@Override
	public void encodeChildren(FacesContext context, UIComponent component) throws IOException {

		if (context == null || component == null)
			throw new NullPointerException();
		if (!component.isRendered())
			return;

		if (isAjaxResponse(context)) {
			renderAjaxResponse(context, component);
		}
	}

	private void renderAjaxResponse(FacesContext context, UIComponent component) throws IOException {
		AjaxResponseWriter out = (AjaxResponseWriter) context.getResponseWriter();
		String clientId = getOuterClientId(context, component);

		// create a temporary ResponseWriter to get inner HTML
		StringBuffer buf = new StringBuffer();
		UISimpleDataGrid grid = (UISimpleDataGrid) component;
		encodeGrid(context, grid, buf);
		// output javascript to set inner HTML
		out.writeInnerHtmlScript(clientId, buf.toString());
		// clear selected checkboxes in hidden
		out.writeScript(String.format("document.getElementById('%s').value=',';", grid.getClientId(context) + ROW_SELECTIONS_PARAM));
	}

	private String getOuterClientId(FacesContext context, UIComponent component) {
		String clientId = component.getClientId(context);
		return clientId + NamingContainer.SEPARATOR_CHAR + "_outer";
	}

	private String getSimpleClientId(FacesContext context, UIComponent component) {
		return component.getClientId(context).split("" + NamingContainer.SEPARATOR_CHAR)[0];
	}

	public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
		if (context == null || component == null)
			throw new NullPointerException();
		if (!component.isRendered())
			return;

		UISimpleDataGrid grid = (UISimpleDataGrid) component;
		synchronized (grid) {
			grid.setRowIndex(-1);
		}

		StringBuffer buffer = new StringBuffer();
		if(isAjaxHtmlResponse(context)){
			encodeGrid(context, grid, buffer);
		}
		ResponseWriter out = context.getResponseWriter();
		out.write(buffer.toString());
		// render out div end
		out.endElement("div");
		String hiddenInputId = component.getClientId(context) + ROW_SELECTIONS_PARAM;
		out.write(String.format("<input type='hidden' id='%s' name='%s' value=','/>", hiddenInputId, hiddenInputId));
	}

	private void encodeGrid(FacesContext context, UISimpleDataGrid grid, StringBuffer buffer) {
		List<UIColumn> columns = getColumns(grid);
		int totalWidth = 0;
		if (grid.getShowSelectionColumn()) {
			totalWidth += SELECTION_COLUMN_WIDTH;
		}
		for (UIColumn column : columns) {
			int width = ((UIOutputColumn) column).getWidth();
			totalWidth += width > 0 ? width : COLUMN_DEFAULT_WIDTH;
		}
		buffer.append(String.format("<div id='%s' style='%s' class='x-grid3-container'>\n", grid.getClientId(context), "background-color:white;overflow:auto;" + grid.getStyle()));
		buffer.append("<div hidefocus='true' style='width: " + totalWidth + "px; height: 100%;" + grid.getStyle() + "'>\n");
		buffer.append("<div class='x-grid3-viewport' style='left: 0px; top: 0px; width: 100%; height: 100%; visibility: visible;position: relative;'>\n");
		encodeGridHeader(context, grid, buffer);
		encodeGridBody(context, grid, buffer);
		buffer.append("</div></div></div>");
	}

	private void encodeGridHeader(FacesContext context, UISimpleDataGrid grid, StringBuffer buffer) {
		ResourceManager rm = ResourceManager.getInstance(context);
		buffer.append("<div class='x-grid3-header'>\n");
		buffer.append("<table cellspacing='0' cellpadding='0' border='0'>\n");
		buffer.append("<tbody>\n");
		buffer.append("<tr class='x-grid3-hd-row'>\n");
		List<UIColumn> columns = getColumns(grid);
		int cssIndex = 0;
		String simpleClientId = this.getSimpleClientId(context, grid);
		boolean drawSelectionColumn = grid.getShowSelectionColumn();
		for (UIColumn column : columns) {
			if (drawSelectionColumn && cssIndex == grid.getSelectionColumnIndex()) {
				encodeSeltionColumnHeader(context, grid, buffer, simpleClientId);
				drawSelectionColumn = false;
			}
			buffer.append(String.format("<td class='x-grid3-hd %s-x-grid3-col-%s' align='center'>\n", getSimpleClientId(context, grid), cssIndex++));
			buffer.append("<div class='x-grid3-hd-inner'>\n");
			buffer.append("<div unselectable='on' class='x-grid3-hd-text'>\n");
			buffer.append(String.format("%s<img src='%s' class='x-grid3-sort-icon'/>\n", ((UIOutputColumn) column).getColumnHeader(), rm.getResourceURL("/yuiext/s.gif")));
			buffer.append("</div></div></td>\n");
		}
		if (drawSelectionColumn) {
			encodeSeltionColumnHeader(context, grid, buffer, simpleClientId);
		}
		buffer.append("</tr></tbody></table></div>");
	}

	private void encodeSeltionColumnHeader(FacesContext context, UISimpleDataGrid grid, StringBuffer buffer, String gridClientId) {
		String hiddenInputId = grid.getClientId(context) + ROW_SELECTIONS_PARAM;
		buffer.append(String.format("<td class='x-grid3-hd' align='center' width='%s'>\n", SELECTION_COLUMN_WIDTH));
		buffer.append(String.format("<input type='checkbox' style='margin-left:1px;' onclick='selectAllRows(this, \"%s\", \"%s\")'>\n", getOuterClientId(context, grid), hiddenInputId));
		buffer.append("</td>");
	}

	private void encodeGridBody(FacesContext context, UISimpleDataGrid grid, StringBuffer buffer) {
		buffer.append("<div class='x-grid3-body' style='height: 100%;'>\n");
		buffer.append("<table cellspacing='0' cellpadding='0' border='0'>\n");
		buffer.append("<tbody>\n");

		String hiddenInputId = grid.getClientId(context) + ROW_SELECTIONS_PARAM;
		String simpleClientId = getSimpleClientId(context, grid);
		List<UIColumn> columns = getColumns(grid);
		int rowIndex = grid.getFirst();
		int rows = grid.getRows();
		for (int curRow = 0; rows == 0 || curRow < rows; curRow++) {
			String altRowCss = curRow % 2 == 0 ? " x-grid3-row-alt" : "";
			buffer.append(String.format("<tr class='x-grid3-row%s' style='height:24px;'>\n", altRowCss));
			grid.setRowIndex(rowIndex++);
			if (!grid.isRowAvailable()) {
				break;
			}
			int cssIndex = 0;
			boolean drawSelectionColumn = grid.getShowSelectionColumn();
			for (UIColumn column : columns) {
				Object value = getColumnValue(context, grid, column);
				String align = ((UIOutputColumn) column).getAlign();
				if (drawSelectionColumn && cssIndex == grid.getSelectionColumnIndex()) {
					encodeSeltionColumnBody(buffer, rowIndex, hiddenInputId, simpleClientId);
					drawSelectionColumn = false;
				}
				buffer.append(String.format("<td tabindex='0' class='x-grid3-col %s-x-grid3-col-%s' %s style='vertical-align:middle;'>\n", simpleClientId, cssIndex++, align == null ? "" : " align='" + align + "'"));
				buffer.append("<div class='x-grid3-cell-inner'>\n");
				buffer.append("<div unselectable='on' class='x-grid3-cell-text'>\n");
				buffer.append(value);
				buffer.append("</div></td>\n");
			}
			if (drawSelectionColumn) {
				encodeSeltionColumnBody(buffer, rowIndex, hiddenInputId, simpleClientId);
			}
			buffer.append("</tr>\n");
		}
		buffer.append("</tbody></table></div>");
		grid.setRowIndex(-1);
	}

	private void encodeSeltionColumnBody(StringBuffer buffer, int rowIndex, String hiddenInputId, String simpleClientId) {
		buffer.append(String.format("<td class='x-grid3-col' align='center' width='%s' style='vertical-align:middle;'>\n", SELECTION_COLUMN_WIDTH));
		buffer.append("<div class='x-grid3-cell-inner'>\n");
		buffer.append("<div unselectable='on' class='x-grid3-cell-text'>\n");
		buffer.append(String.format("<input type='checkbox' onclick='changeSelectState(this, %s, \"%s\")' id='%s' value='%s'>\n", rowIndex, hiddenInputId, simpleClientId + rowIndex, rowIndex));
		buffer.append("</div></div></td>\n");
	}

	public boolean getRendersChildren() {
		return true;
	}
}