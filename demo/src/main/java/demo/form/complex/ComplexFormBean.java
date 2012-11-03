package demo.form.complex;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Formatter;

import javax.faces.model.SelectItem;

import org.operamasks.faces.annotation.Action;
import org.operamasks.faces.annotation.Bind;
import org.operamasks.faces.annotation.ManagedBean;
import org.operamasks.faces.annotation.ManagedBeanScope;
import org.operamasks.faces.annotation.Required;
import org.operamasks.faces.annotation.SelectItems;
import org.operamasks.faces.annotation.Validate;
import org.operamasks.faces.annotation.ValidateLength;
import org.operamasks.faces.annotation.ValidateLongRange;
import org.operamasks.faces.component.form.impl.UICombo;
@ManagedBean(name="ComplexFormBean", scope=ManagedBeanScope.SESSION)
public class ComplexFormBean {
	@Bind
	@Required(message="名字不能为空")
	private String name;
	
	@Bind
	private String sex = "male";
	
	@Bind
	@ValidateLongRange(minimum=1,maximum=100,message="年龄必须在1和100之间")
	private Integer age;
	
	@Bind
	private Date birthday;
	
	@Bind
	@ValidateLength(minimum=10,message="地址不少于10个字符")
	private String address;
	
	@Bind
	private String province = "guangdong";
	
	@Bind(id="province")
	private UICombo province_comboBox;
	
	@Bind
	@SelectItems
	private SelectItem[] provinces = new SelectItem[]{
		new SelectItem("guangdong","广东"),
		new SelectItem("beijing","北京"),
		new SelectItem("shanghai","上海")
	};
	
	@Bind
	private String city = "shenzhen";
	
	@Bind(id="city")
	private UICombo city_comboBox;
	
	@Bind
	@SelectItems
	private SelectItem[] cities = queryCities("guangdong");
	
	@Action(immediate=true)
	private void province_onselect(){
	    province = province_comboBox.getSubmittedValue().toString();
		cities = queryCities(province);
	}
	
	@Action(immediate=true)
    private void city_onselect(){
	    city = city_comboBox.getSubmittedValue().toString();
    }
	
	private SelectItem[] queryCities(String province) {
		if("guangdong".equals(province)){
			if(city_comboBox != null){
			    city = "shenzhen";
				city_comboBox.setSubmittedValue(city);
				city_comboBox.show();
			}
			return new SelectItem[]{
					new SelectItem("guangzhou","广州"),
					new SelectItem("shenzhen","深圳"),
					new SelectItem("zhuhai","珠海")
				};
		}else{
			if(city_comboBox != null){
			    city = "";
				city_comboBox.setSubmittedValue("");
				city_comboBox.hide();
			}
			return new SelectItem[]{};
		}
	}
	
	@Bind
	private String email;
	@Validate(message="请输入正确的email格式")
	private boolean validateEmail(String email){
		return email.contains("@");
	}
	
	@Bind
	private String startTime;
	@Bind
	private String endTime;
	
	@Bind
	@SelectItems
	private SelectItem[] interests = new SelectItem[]{
			new SelectItem(false,"打球"),
			new SelectItem(false,"听歌"),
			new SelectItem(false,"上网"),
			new SelectItem(false,"看书"),
			new SelectItem(false,"唱k")
	};
	
	@Bind
	private String sign;
	
	@Bind
	private String response;

	@Action
	private void save(){
		Formatter fm = new Formatter(new StringBuffer());
		fm.format("您填写的信息如下：<br/>");
		fm.format("名字：%s<br/>", name);
		fm.format("性别:%s<br/>", sex);
		fm.format("年龄：%s<br/>", age);
		SimpleDateFormat sf = new SimpleDateFormat("yy-M-d");
		if(birthday != null){
			fm.format("生日：%s<br/>", sf.format(birthday));
		}
		fm.format("地址：%s<br/>", address);
		fm.format("所在地：%s %s<br/>", province, city);
		fm.format("电子邮箱：%s<br/>", email);
		fm.format("工作时间：%s-%s<br/>", startTime,endTime);
		fm.format("兴趣：");
		for(SelectItem item : interests){
			if((Boolean)item.getValue()){
				fm.format(item.getLabel() + " ");
			}
		}
		fm.format("<br/>");
		fm.format("签名：%s<br/>", sign);
		
		response = fm.toString();
	}
}
