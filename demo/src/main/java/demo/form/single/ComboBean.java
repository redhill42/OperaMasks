package demo.form.single;

import javax.faces.model.SelectItem;

import org.operamasks.faces.annotation.Action;
import org.operamasks.faces.annotation.Bind;
import org.operamasks.faces.annotation.ManagedBean;
import org.operamasks.faces.annotation.ManagedBeanScope;
import org.operamasks.faces.annotation.SelectItems;
import org.operamasks.faces.component.form.impl.UICombo;
@ManagedBean(name="ComboBean", scope=ManagedBeanScope.SESSION)
public class ComboBean {
	@Bind
	private String province = "guangdong";
	
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
	
	@Action
	private void province_onselect(){
		cities = queryCities(province);
	}

	private SelectItem[] queryCities(String province) {
		if("guangdong".equals(province)){
			if(city_comboBox != null){
				city_comboBox.setValue("shenzhen");
				city_comboBox.enable();
			}
			return new SelectItem[]{
					new SelectItem("guangzhou","广州"),
					new SelectItem("shenzhen","深圳"),
					new SelectItem("zhuhai","珠海")
				};
		}else{
			if(city_comboBox != null){
				city_comboBox.setValue("");
				city_comboBox.disable();
			}
			return new SelectItem[]{};
		}
	}
	
	
}
