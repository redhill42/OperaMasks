package org.operamasks.faces.component.widget.toolbar;

import javax.faces.component.UIComponent;

public abstract class ToolBarStateChange {
	private UIComponent toolBar;
	private UIComponent item;
	
	public ToolBarStateChange(UIComponent toolBar, UIComponent item) {
		this.toolBar = toolBar;
		this.item = item;
	}

	public UIComponent getToolBar() {
		return toolBar;
	}

	public void setToolBar(UIComponent toolBar) {
		this.toolBar = toolBar;
	}

	public UIComponent getItem() {
		return item;
	}

	public void setItem(UIComponent item) {
		this.item = item;
	}
}
