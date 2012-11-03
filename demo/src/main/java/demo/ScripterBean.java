package demo;

import org.operamasks.faces.annotation.ManagedBean;
import org.operamasks.faces.annotation.ManagedBeanScope;
import org.operamasks.faces.annotation.ManagedProperty;

@ManagedBean(scope = ManagedBeanScope.SESSION)
public class ScripterBean {
	@ManagedProperty
	private String script;

	@ManagedProperty
	private String name;

	public void sumbit_action() {
		if (!"".equals(name.trim())) { // 不允许name为空字符串
			StringBuilder js = new StringBuilder(); // 使用StringBuilder暂存javascript代码
			js.append("<p>your name is: " + name + "</p>");
			if ("duke".equals(name.trim())) // 用户名为duke的情况
			{
				js.append("<p>Hi. We have the same name.</p>");
			} else {
				js.append("<p>Hello " + name + ". Nice to meet you.</p>");
			}

			// 输出javascript代码
			script = "document.getElementById('mySumbitSpan').innerHTML = '<font class=normal>"
					+ js.toString() + "</font>'";
		} else
			script = "document.getElementById('mySumbitSpan').innerHTML = '<font class=error>please input your name.</font>'";
	}

	public void reset_action() { // 重置
		name = "";
		StringBuilder js = new StringBuilder();
		js.append("document.getElementById('mySumbitSpan').innerHTML = '';");
		script = js.toString();
	}
}
