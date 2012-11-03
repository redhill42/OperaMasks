package demo.form.single;

import javax.faces.model.SelectItem;

import org.operamasks.faces.annotation.Action;
import org.operamasks.faces.annotation.Bind;
import org.operamasks.faces.annotation.ManagedBean;
import org.operamasks.faces.annotation.ManagedBeanScope;
import org.operamasks.faces.annotation.SelectItems;
@ManagedBean(name="RadioGroupBean", scope=ManagedBeanScope.SESSION)
public class RadioGroupBean {
	@Bind
	private String response;
	
	@Bind
	private String color = "blue";
	
	@Bind
	@SelectItems
	private SelectItem[] colors = new SelectItem[]{
		new SelectItem("red", "红色"),
		new SelectItem("blue", "蓝色"),
		new SelectItem("green", "绿色")
	};
	
	@Action
	public void ok(){
		response = "您选择的颜色的值是：" + color;
	}
}
