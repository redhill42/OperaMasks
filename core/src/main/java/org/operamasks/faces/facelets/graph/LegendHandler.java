package org.operamasks.faces.facelets.graph;

import javax.faces.component.UIComponent;

import org.operamasks.faces.component.graph.UIDataItem;
import org.operamasks.faces.component.graph.UILegend;
import org.operamasks.faces.render.graph.ChartUtils;

import com.sun.facelets.FaceletContext;
import com.sun.facelets.tag.MetaRuleset;
import com.sun.facelets.tag.TagAttribute;
import com.sun.facelets.tag.jsf.ComponentConfig;
import com.sun.facelets.tag.jsf.ComponentHandler;

public class LegendHandler extends ComponentHandler{
	public LegendHandler(ComponentConfig config) {
		super(config);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected MetaRuleset createMetaRuleset(Class type) {
		MetaRuleset m = super.createMetaRuleset(type);
		m.ignore("backgroundColor");
		m.ignore("borderColor");
		m.ignore("itemColor");
		m.ignore("itemFont");
        return m;
    }
	
    @Override
    protected void onComponentCreated(FaceletContext ctx, UIComponent c, UIComponent parent) {
		UILegend legend = (UILegend)c;
		
		TagAttribute backgroundColor = getAttribute("backgroundColor");
		if (backgroundColor != null) {
			String sBackgroundColor = backgroundColor.getValue();
			legend.setBackgroundColor(ChartUtils.convertColor(sBackgroundColor));
		}
		
		TagAttribute borderColor = getAttribute("borderColor");
		if (borderColor != null) {
			String sBorderColor = borderColor.getValue();
			legend.setBorderColor(ChartUtils.convertColor(sBorderColor));
		}
		
		TagAttribute itemColor = getAttribute("itemColor");
		if (itemColor != null) {
			String sItemColor = itemColor.getValue();
			legend.setItemColor(ChartUtils.convertColor(sItemColor));
		}
		
		TagAttribute itemFont = getAttribute("itemFont");
		if (itemFont != null) {
			String sItemFont = itemFont.getValue();
			legend.setItemFont(ChartUtils.convertFont(sItemFont));
		}
    }
}
