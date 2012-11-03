package demo.form.single;

import org.operamasks.faces.annotation.Action;
import org.operamasks.faces.annotation.Bind;
import org.operamasks.faces.annotation.ManagedBean;
import org.operamasks.faces.annotation.ManagedBeanScope;
@ManagedBean(name="TimeFieldBean", scope=ManagedBeanScope.SESSION)
public class TimeFieldBean {
	@Bind
	private String time;
	
	@Bind
	private String response;
	
	@Action
	private void time_onselect(){
		response = "您选择的时间是：" + time;
	}
}
