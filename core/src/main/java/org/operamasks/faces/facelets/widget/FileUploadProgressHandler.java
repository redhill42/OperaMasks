package org.operamasks.faces.facelets.widget;

import javax.faces.component.UIComponent;

import org.operamasks.faces.component.widget.UIFileUploadProgress;
import org.operamasks.faces.util.FileUploadUtils;

import com.sun.facelets.FaceletContext;
import com.sun.facelets.tag.jsf.ComponentConfig;
import com.sun.facelets.tag.jsf.ComponentHandler;

public class FileUploadProgressHandler extends ComponentHandler {

	public FileUploadProgressHandler(ComponentConfig config) {
		super(config);
	}

	@Override
	protected void onComponentPopulated(FaceletContext ctx, UIComponent c,
			UIComponent parent) {
		FileUploadUtils.decorateFileUploadProgress(ctx.getFacesContext(),
				(UIFileUploadProgress)c);
	}
}
