package demo.form.single;

import org.operamasks.faces.annotation.Action;
import org.operamasks.faces.annotation.Bind;
import org.operamasks.faces.annotation.ManagedBean;
import org.operamasks.faces.annotation.ManagedBeanScope;
@ManagedBean(name="TextAreaBean", scope=ManagedBeanScope.SESSION)
public class TextAreaBean {
	@Bind
	private String title = "";
	
	@Bind
	private String content = "";
	
	@Bind
	private String resultList = "";
	
	@Action
	private void content_onblur(){
		if(!"".equals(title)&& !"".equals(content)){
			resultList += "标题：" + title + "<br/>" + "内容：" + content + "<br/><hr/>";
			title = "";
			content = "";
		}
	}
}
