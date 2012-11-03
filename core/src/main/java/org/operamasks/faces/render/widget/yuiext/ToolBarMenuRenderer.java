package org.operamasks.faces.render.widget.yuiext;

import java.io.IOException;
import java.util.Formatter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import org.operamasks.faces.component.widget.UIPagingToolbar;
import org.operamasks.faces.component.widget.UIToolBar;
import org.operamasks.faces.component.widget.menu.UIMenu;
import org.operamasks.faces.render.resource.AbstractResource;
import org.operamasks.faces.render.resource.ResourceManager;
import org.operamasks.faces.util.FacesUtils;

public class ToolBarMenuRenderer extends MenuRenderer {
	@Override
	public void provideResource(final ResourceManager rm, final UIComponent component) {
		final YuiExtResource resource = YuiExtResource.register(rm, "Ext.Toolbar");
		
		super.provideResource(rm, component);
		
		rm.registerResource(new AbstractResource(getResourceId(component)) {
			@Override
			public int getPriority() {
				return LOW_PRIORITY - 300;
			}
			
			@Override
			public void encodeBegin(FacesContext context) throws IOException {
				encodeScript(component, resource);
			}
		});
	}

	private String getResourceId(UIComponent component) {
		return "urn:toolBarMenu:" + component.getClientId(FacesContext.getCurrentInstance());
	}

	private void encodeScript(UIComponent component, YuiExtResource resource) {
		StringBuilder buf = new StringBuilder();
        Formatter fmt = new Formatter(buf);
        FacesContext context = FacesContext.getCurrentInstance();
        
		UIComponent parent = component.getParent();
		if (!(parent instanceof UIToolBar) && !(parent instanceof UIPagingToolbar))
			return;
		
        UIMenu menu = (UIMenu)component;
        fmt.format(
                "\n%s.add({" +
                    "\ntext: '%s'," +
                    "\nmenu: %s" +
                "});",
                FacesUtils.getJsvar(context, parent),
                menu.getLabel(),
                FacesUtils.getJsvar(context, menu)
        );
        
        resource.addInitScript(buf.toString());
	}
}