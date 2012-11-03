package org.operamasks.faces.facelets.widget;

import javax.faces.component.UIComponent;

import org.operamasks.faces.component.widget.menu.UIMenu;

import com.sun.facelets.FaceletContext;
import com.sun.facelets.tag.MetaRuleset;
import com.sun.facelets.tag.TagAttribute;
import com.sun.facelets.tag.jsf.ComponentConfig;
import com.sun.facelets.tag.jsf.ComponentHandler;

public class MenuHandler extends ComponentHandler {
	private TagAttribute action;
	private static final Class[] ACTION_SIG = new Class[] {UIComponent.class};
	
    public MenuHandler(ComponentConfig config) {
		super(config);
		
		action = getAttribute("action");
	}
    
    @Override
    protected void onComponentCreated(FaceletContext ctx, UIComponent c, UIComponent parent) {
        if (this.action != null) {
            ((UIMenu)c).setMenuAction(this.action.getMethodExpression(ctx, String.class, ACTION_SIG ));
        }
    }

    protected MetaRuleset createMetaRuleset(Class type) {
        return super.createMetaRuleset(type).
        	ignore("action");
    }
}
