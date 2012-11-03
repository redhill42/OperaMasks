package demo;

import org.operamasks.faces.annotation.Action;
import org.operamasks.faces.annotation.ManagedBean;
import org.operamasks.faces.annotation.ManagedBeanScope;
import org.operamasks.faces.annotation.ManagedProperty;

@ManagedBean(name = "templateBean", scope = ManagedBeanScope.SESSION)
public class TemplateBean {
	private static final String TEMPLATE1 = "/template/templates/operamasksMain.xhtml";
	private static final String TEMPLATE2 = "/template/templates/operamasksMain2.xhtml";
	private static final String TEMPLATE3 = "/template/templates/operamasksMain3.xhtml";

	@ManagedProperty
	private String templateURL = TEMPLATE1;

	@Action
	public Object changeLayout() {
		if (TEMPLATE1.equals(templateURL)) {
			templateURL = TEMPLATE2;
		}else if (TEMPLATE2.equals(templateURL)) {
			templateURL = TEMPLATE3;
		} else {
			templateURL = TEMPLATE1;
		}
		return null;
	}
}
