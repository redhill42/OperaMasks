package org.operamasks.faces.render.widget;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.operamasks.faces.render.html.HtmlRenderer;

public class AjaxFileUploadProgressRenderer extends HtmlRenderer {
	@Override
	public boolean getRendersChildren() {
		return true;
	}
	
	@Override
	public void encodeChildren(FacesContext context, UIComponent component)
			throws IOException {
		if (isAjaxHtmlResponse(context)) {
			ResponseWriter writer = context.getResponseWriter();
			writer.write("<table>");
			writer.write("<tr>");
			writer.write("<td>");
			component.getChildren().get(0).encodeAll(context);
			writer.write("</td>");
			writer.write("</tr>");
			writer.write("<tr>");
			writer.write("<td>");
			component.getChildren().get(1).encodeAll(context);
			writer.write("</td>");
			writer.write("</tr>");
			writer.write("</table>");
			
			component.getChildren().get(2).encodeAll(context);
		} else {
			super.encodeChildren(context, component);
		}
	}
}