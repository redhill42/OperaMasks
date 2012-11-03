package org.operamasks.faces.facelets.ajax;

import com.sun.facelets.tag.MetaRuleset;
import com.sun.facelets.tag.MethodRule;
import com.sun.facelets.tag.jsf.ComponentConfig;
import com.sun.facelets.tag.jsf.ComponentHandler;

public class AjaxProgressHandler extends ComponentHandler {
	private static final MethodRule actionRule =
        new MethodRule("action", String.class, new Class[] {});
	
	public AjaxProgressHandler(ComponentConfig config) {
		super(config);
	}
	
	 @SuppressWarnings("unchecked")
	protected MetaRuleset createMetaRuleset(Class type) {
	        MetaRuleset m = super.createMetaRuleset(type);
	        m.addRule(actionRule);

	        return m;
	    }
}
