package org.operamasks.faces.render.widget.yuiext;

import java.io.IOException;
import java.util.Formatter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.render.Renderer;

import org.operamasks.faces.component.widget.UIPagingToolbar;
import org.operamasks.faces.component.widget.UIToolBar;
import org.operamasks.faces.render.resource.AbstractResource;
import org.operamasks.faces.render.resource.ResourceManager;
import org.operamasks.faces.render.resource.ResourceProvider;
import org.operamasks.faces.util.FacesUtils;

public class ToolBarSeparatorRenderer extends Renderer implements ResourceProvider {
	public void provideResource(final ResourceManager manager, final UIComponent component) {
		manager.registerResource(new AbstractResource(getResourceId(component)) {
			@Override
			public int getPriority() {
				return LOW_PRIORITY - 300;
			}
			
			@Override
			public void encodeBegin(FacesContext context) throws IOException {
				YuiExtResource resource = YuiExtResource.register(manager, "Ext.Toolbar");
				
			    StringBuilder buf = new StringBuilder();
			    Formatter fmt = new Formatter(buf);
			    
				UIComponent parent = component.getParent();
				if (!(parent instanceof UIToolBar) && !(parent instanceof UIPagingToolbar))
					return;
				
				fmt.format("\n%s.addSeparator();", FacesUtils.getJsvar(context, parent));
				
				resource.addInitScript(buf.toString());
			}
		});
	}
	
	private String getResourceId(UIComponent component) {
		return "urn:toolBarSeparator:" + component.getClientId(FacesContext.getCurrentInstance());
	}
}
