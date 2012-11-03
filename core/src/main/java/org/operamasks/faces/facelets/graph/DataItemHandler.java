package org.operamasks.faces.facelets.graph;

import javax.faces.component.UIComponent;

import org.operamasks.faces.component.graph.UIDataItem;
import org.operamasks.faces.render.graph.ChartUtils;

import com.sun.facelets.FaceletContext;
import com.sun.facelets.tag.MetaRuleset;
import com.sun.facelets.tag.TagAttribute;
import com.sun.facelets.tag.jsf.ComponentConfig;
import com.sun.facelets.tag.jsf.ComponentHandler;

public class DataItemHandler extends ComponentHandler{
	public DataItemHandler(ComponentConfig config) {
		super(config);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected MetaRuleset createMetaRuleset(Class type) {
		MetaRuleset m = super.createMetaRuleset(type);
		m.ignore("color");
		m.ignore("outlineColor");
		m.ignore("itemLabelColor");
		m.ignore("itemLabelFont");
		m.ignore("markerFillColor");
		
        return m;
    }
	
    @Override
    protected void onComponentCreated(FaceletContext ctx, UIComponent c, UIComponent parent) {
		UIDataItem dataItem = (UIDataItem)c;
		
		TagAttribute color = getAttribute("color");
		if (color != null) {
			String sColor = color.getValue();
			dataItem.setColor(ChartUtils.convertColor(sColor));
		}
		
		TagAttribute outlineColor = getAttribute("outlineColor");
		if (outlineColor != null) {
			String sOutlineColor = outlineColor.getValue();
			dataItem.setOutlineColor(ChartUtils.convertColor(sOutlineColor));
		}
		
		TagAttribute itemLabelColor = getAttribute("itemLabelColor");
		if (itemLabelColor != null) {
			String sItemLabelColor = itemLabelColor.getValue();
			dataItem.setItemLabelColor(ChartUtils.convertColor(sItemLabelColor));
		}
		
		TagAttribute itemLabelFont = getAttribute("itemLabelFont");
		if (itemLabelFont != null) {
			String sItemLabelFont = itemLabelFont.getValue();
			dataItem.setItemLabelFont(ChartUtils.convertFont(sItemLabelFont));
		}
		
		TagAttribute markerFillColor = getAttribute("markerFillColor");
		if (markerFillColor != null) {
			String sMarkerFillColor = markerFillColor.getValue();
			dataItem.setMarkerFillColor(ChartUtils.convertColor(sMarkerFillColor));
		}
		
    }
}
