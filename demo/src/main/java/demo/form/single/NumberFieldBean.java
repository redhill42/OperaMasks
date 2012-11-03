package demo.form.single;

import org.operamasks.faces.annotation.Action;
import org.operamasks.faces.annotation.Bind;
import org.operamasks.faces.annotation.ManagedBean;
import org.operamasks.faces.annotation.ManagedBeanScope;
@ManagedBean(name="NumberFieldBean", scope=ManagedBeanScope.SESSION)
public class NumberFieldBean {
	@Bind
	private Integer number;
	
	@Bind 
	private String response;
	
	@Action
	private void number_onblur(){
		response = "您输入的数字为：" + number;
	}
}
