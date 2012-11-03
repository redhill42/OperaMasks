package org.operamasks.faces.facelets.graph;

import com.sun.facelets.tag.MetaRuleset;
import com.sun.facelets.tag.MethodRule;
import com.sun.facelets.tag.jsf.ComponentConfig;

public class FunctionItemHandler extends DataItemHandler{
	private static final MethodRule expressionRule =
        new MethodRule("expression", Double.class, new Class[] {Double.class});
	
	public FunctionItemHandler(ComponentConfig config) {
		super(config);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected MetaRuleset createMetaRuleset(Class type) {
		MetaRuleset m = super.createMetaRuleset(type);
		m.addRule(expressionRule);
		
        return m;
	}
}
