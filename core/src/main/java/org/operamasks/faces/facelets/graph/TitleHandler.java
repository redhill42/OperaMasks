package org.operamasks.faces.facelets.graph;

import javax.faces.component.UIComponent;

import org.operamasks.faces.component.graph.UITitle;
import org.operamasks.faces.render.graph.ChartUtils;

import com.sun.facelets.FaceletContext;
import com.sun.facelets.tag.MetaRuleset;
import com.sun.facelets.tag.TagAttribute;
import com.sun.facelets.tag.jsf.ComponentConfig;
import com.sun.facelets.tag.jsf.ComponentHandler;

public class TitleHandler extends ComponentHandler{
	public TitleHandler(ComponentConfig config) {
		super(config);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected MetaRuleset createMetaRuleset(Class type) {
		MetaRuleset m = super.createMetaRuleset(type);
		m.ignore("font");
		
        return m;
    }
	
    @Override
    protected void onComponentCreated(FaceletContext ctx, UIComponent c, UIComponent parent) {
    	UITitle title = (UITitle)c;
		
		TagAttribute font = getAttribute("font");
		if (font != null) {
			String sTitle = font.getValue();
			
			title.setFont(ChartUtils.convertFont(sTitle));
		}
    }
}
