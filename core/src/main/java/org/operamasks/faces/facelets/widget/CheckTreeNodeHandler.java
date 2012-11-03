package org.operamasks.faces.facelets.widget;

import com.sun.facelets.tag.jsf.ComponentConfig;

public class CheckTreeNodeHandler extends SimpleCheckTreeNodeHandler {
	public CheckTreeNodeHandler(ComponentConfig config) {
		super(config);
	}

	@Override
	protected String getTagName() {
		return "checkTreeNodeTag";
	}
}