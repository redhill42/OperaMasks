package org.operamasks.faces.render.widget.yuiext;


import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import org.operamasks.faces.component.widget.UIPagingToolbar;
import org.operamasks.faces.component.widget.UIToolBar;
import org.operamasks.faces.render.resource.AbstractResource;
import org.operamasks.faces.render.resource.ResourceManager;

public class ToolBarButtonRenderer extends ButtonRenderer {
	@Override
	public void provideResource(final ResourceManager rm, final UIComponent component) {
		super.provideResource(rm, component);
		
		rm.registerResource(new AbstractResource(getResourceId(component)) {
			@Override
			public int getPriority() {
				return LOW_PRIORITY - 300;
			}
			
			@Override
			public void encodeBegin(FacesContext context) throws IOException {
				ToolBarUtils.appendItemToToolBar(rm, component);
			}
		});
	}
	
	private String getResourceId(UIComponent component) {
		return "urn:toolBarButton:" + component.getClientId(FacesContext.getCurrentInstance());
	}
	
    protected void encodeButton(ResourceManager rm, StringBuilder buf, UIComponent component, String jsvar) {
		UIComponent parent = component.getParent();
		if (parent instanceof UIToolBar || parent instanceof UIPagingToolbar) {
	        ToolBarUtils.encodeToolBarButton(rm, buf, component, jsvar, parent);
		} else {
			super.encodeButton(rm, buf, component, jsvar);
		}
    }
}
