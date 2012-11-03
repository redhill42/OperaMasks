package demo;

import org.operamasks.faces.annotation.ManagedBean;
import org.operamasks.faces.annotation.ManagedBeanScope;
import org.operamasks.faces.annotation.ManagedProperty;

@ManagedBean(name = "AjaxUpdaterBean", scope = ManagedBeanScope.SESSION)
public class AjaxUpdaterBean {

	@ManagedProperty
	private String name;

	@ManagedProperty
	private String responeText;

	public void button_action() {
		if( null == name  || "".endsWith(name)){
			responeText = "";
		}else{
			responeText = "Hello " + name;
		}
	}
}
