package org.operamasks.faces.facelets.ajax;

import javax.faces.component.UIComponent;

import org.operamasks.faces.component.ajax.AjaxLogger;

import com.sun.facelets.FaceletContext;
import com.sun.facelets.tag.jsf.ComponentConfig;
import com.sun.facelets.tag.jsf.ComponentHandler;

public class LoggerHandler extends ComponentHandler {
    public LoggerHandler(ComponentConfig config) {
        super(config);
    }
    
    @Override
    protected void onComponentCreated(FaceletContext ctx, UIComponent c,
    		UIComponent parent) {
    	AjaxLogger logger = (AjaxLogger)c;
    	
    	if (logger.getStyle() == null) {
    		logger.setStyle("overflow:scroll;width:100%;height:200px;left:0px;bottom:0px;position:absolute;font-size:9pt");
    	}
    }
}
