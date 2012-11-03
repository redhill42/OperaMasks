package org.operamasks.faces.render.widget.yuiext;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import org.operamasks.faces.component.widget.UIToolBar;
import org.operamasks.faces.component.widget.toolbar.AddToolBarItem;
import org.operamasks.faces.component.widget.toolbar.RemoveToolBarItem;
import org.operamasks.faces.component.widget.toolbar.ToolBarStateChange;
import org.operamasks.faces.render.resource.ResourceManager;

public class AjaxToolBarRenderer extends ToolBarRenderer {
	@Override
	public void encodeBegin(FacesContext context, UIComponent component) throws IOException {
		if (isAjaxHtmlResponse(context)) {
			super.encodeBegin(context, component);
		} else {
			UIToolBar toolBar = (UIToolBar)component;
			
			if (toolBar.getChanges() == null || toolBar.getChanges().size() == 0)
				return;
			
			ResourceManager rm = ResourceManager.getInstance(context);

			for (ToolBarStateChange change : toolBar.getChanges()) {
				UIComponent item = change.getItem();
				ToolBarUtils.adjustToolBarItemRenderer(item);
				
				if (change instanceof AddToolBarItem) {
					rm.encodeBegin(context);
					rm.consumeResources(context, item);
					rm.encodeEnd(context);
					rm.reset();
				} else if (change instanceof RemoveToolBarItem) {
					// TODO extjs v1.1 don't support removing toolbar item.
				}
			}
		}
	}
	
	@Override
	public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
		if (isAjaxHtmlResponse(context)) {
			super.encodeEnd(context, component);
		}
	}
}
