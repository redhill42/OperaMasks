package demo.form.single;


import java.util.Date;

import org.operamasks.faces.annotation.Action;
import org.operamasks.faces.annotation.Bind;
import org.operamasks.faces.annotation.ManagedBean;
import org.operamasks.faces.annotation.ManagedBeanScope;
@ManagedBean(name="DateFieldBean", scope=ManagedBeanScope.SESSION)
public class DateFieldBean {
	@Bind
	private Date date1;
	
	@Bind
    private Date date2;
	
	@Bind
    private java.sql.Date date3;
	
	@Bind
    private String date4;
	
	@Bind
	private String response;
	
	@SuppressWarnings("deprecation")
	@Action
	public void click(){
		StringBuffer sb = new StringBuffer();
		if(date1 != null)
		    sb.append("date1:").append(date1.toLocaleString()).append("<br/>");
		if(date2 != null)
		    sb.append("date2:").append(date2.toLocaleString()).append("<br/>");
		if(date3 != null)
		    sb.append("date3:").append(date3).append("<br/>");
		if(date4 != null)
		    sb.append("date4:").append(date4).append("<br/>");
		response = sb.toString();
	}
}
