package demo.form.single;

import javax.faces.model.SelectItem;

import org.operamasks.faces.annotation.Action;
import org.operamasks.faces.annotation.Bind;
import org.operamasks.faces.annotation.ManagedBean;
import org.operamasks.faces.annotation.ManagedBeanScope;
import org.operamasks.faces.annotation.SelectItems;
@ManagedBean(name="CheckBoxGroupBean", scope=ManagedBeanScope.SESSION)
public class CheckBoxGroupBean {
	@Bind
	private String response;
	
	@Bind
	@SelectItems
	private SelectItem[] colors = new SelectItem[]{
		new SelectItem(true, "红色"),
		new SelectItem(false, "蓝色"),
		new SelectItem(false, "绿色"),
	};
	
	@Action
	public void ok(){
		response = "您选择的颜色是：";
		for(SelectItem item : colors){
			if((Boolean)item.getValue()){
				response += item.getLabel() + " ";
			}
		}
	}
}
