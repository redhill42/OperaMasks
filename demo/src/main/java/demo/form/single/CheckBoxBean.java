package demo.form.single;

import org.operamasks.faces.annotation.Action;
import org.operamasks.faces.annotation.Bind;
import org.operamasks.faces.annotation.ManagedBean;
import org.operamasks.faces.annotation.ManagedBeanScope;
@ManagedBean(name="CheckBoxBean", scope=ManagedBeanScope.SESSION)
public class CheckBoxBean {
	
	@Bind
	private Boolean red = false;
	
	@Bind
	private Boolean blue = false;
	
	@Bind
	private Boolean green = false;
	
	@Bind
	private String response;
	
	@Action
	private void red_oncheck(){
		checkBox_oncheck();
	}
	@Action
	private void blue_oncheck(){
		checkBox_oncheck();
	}
	@Action
	private void green_oncheck(){
		checkBox_oncheck();
	}
	
	private void checkBox_oncheck(){
		String selectedColors = "";
		if(red){
			selectedColors += "<span style='color:red'>红色</span> ";
		}
		if(blue){
			selectedColors += "<span style='color:blue'>蓝色</span> ";
		}
		if(green){
			selectedColors += "<span style='color:green'>绿色</span> ";
		}
		response = "您选择的颜色是：" + selectedColors;
	}
}
