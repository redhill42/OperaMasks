package demo.form.single;

import javax.faces.event.ActionEvent;

import org.operamasks.faces.annotation.Action;
import org.operamasks.faces.annotation.ActionListener;
import org.operamasks.faces.annotation.Bind;
import org.operamasks.faces.annotation.ManagedBean;
import org.operamasks.faces.annotation.ManagedBeanScope;
import org.operamasks.faces.component.form.impl.UITextField;

@ManagedBean(name="TextFieldBean", scope=ManagedBeanScope.REQUEST)
public class TextFieldBean {
	@Bind
	private String name;
	
	@Bind
	private String nameMsg;
	
	@Bind
	private String password;
	
	@Bind
	private String response;
	
	@ActionListener(id="name", event="onchange", immediate=true)
	public void change(ActionEvent event){
		UITextField field = (UITextField)event.getComponent();
		if(!"operamasks".equals(field.getSubmittedValue())){
			nameMsg = "请输入operamasks";
		}else{
			nameMsg = "";
		}
	}
	
	@Action
	public void login(){
		if("operamasks".equals(name)){
			response = "注册成功";
		}else{
			response = "注册失败";
		}
	}
}
