package org.operamasks.faces.render.widget.yuiext;

import javax.faces.component.UIComponent;

import org.operamasks.faces.component.widget.UIPagingToolbar;
import org.operamasks.faces.component.widget.UIToolBar;
import org.operamasks.faces.render.resource.ResourceManager;

public class AjaxToolBarButtonRenderer extends AjaxButtonRenderer {
	
    protected void encodeButton(ResourceManager rm, StringBuilder buf, UIComponent component, String jsvar) {
		UIComponent parent = component.getParent();
		if (parent instanceof UIToolBar || parent instanceof UIPagingToolbar) {
	        ToolBarUtils.encodeToolBarButton(rm, buf, component, jsvar, parent);
		} else {
			super.encodeButton(rm, buf, component, jsvar);
		}
    }
}
