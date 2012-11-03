package org.operamasks.faces.facelets.widget;

import javax.faces.FacesException;
import javax.faces.component.UIComponent;

import org.operamasks.faces.component.widget.UIFileUpload;
import org.operamasks.faces.component.widget.fileupload.FileUploadItem;
import org.operamasks.faces.util.FacesUtils;

import com.sun.facelets.FaceletContext;
import com.sun.facelets.tag.MetaRuleset;
import com.sun.facelets.tag.MethodRule;
import com.sun.facelets.tag.TagAttribute;
import com.sun.facelets.tag.jsf.ComponentConfig;
import com.sun.facelets.tag.jsf.ComponentHandler;

public class FileUploadHandler extends ComponentHandler {
	private TagAttribute maxSize;
	private TagAttribute uploadListener;
	
	@SuppressWarnings("unchecked")
	private static final Class[] UPLOAD_LISTENER_SIG = new Class[] {FileUploadItem.class};
    private static final MethodRule uploadListenerRule =
        new MethodRule("uploadListener", void.class, UPLOAD_LISTENER_SIG);
	
	
    public FileUploadHandler(ComponentConfig config) {
    	super(config);
		
		maxSize = getAttribute("maxSize");
		
		uploadListener = getAttribute("uploadListener");
		TagAttribute writeTo = getAttribute("writeTo");
		
		if (writeTo == null && uploadListener == null) {
			throw new FacesException("Neither attribute 'writeTo' or 'uploadListener' is set.");
		}
	}
    
	@SuppressWarnings("unchecked")
	protected MetaRuleset createMetaRuleset(Class type) {
		MetaRuleset m = super.createMetaRuleset(type);
		m.ignore("maxSize");
		m.addRule(uploadListenerRule);
		
        return m;
    }
    
    @Override
    protected void onComponentCreated(FaceletContext ctx, UIComponent c, UIComponent parent) {
    	UIFileUpload fileUpload = (UIFileUpload)c;
    	
        if (this.maxSize != null) {
			String maxSizeString = maxSize.getValue();
			fileUpload.setMaxSize(FacesUtils.getMaxSizeInBytes(maxSizeString));
        }
    }
}
