package demo;

import java.util.Date;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.faces.validator.ValidatorException;

import org.operamasks.faces.annotation.ManagedBean;
import org.operamasks.faces.annotation.ManagedBeanScope;

@ManagedBean(scope=ManagedBeanScope.REQUEST)
public class ClientValidatorBean {
	private String phone;
	private int number;
	private Date date;
	private String text;
	private String color;
	
	private String response;
    private SelectItem[] colors = {
            new SelectItem("Red", "Red Color"),
            new SelectItem("Green", "Green Color"),
            new SelectItem("Blue", "Blue Color")
    };
	
	public ClientValidatorBean() {
		number = -1;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phoneNumber) {
		this.phone = phoneNumber;
	}
	
	public void validatePhone(FacesContext context, UIComponent component, Object value) {
		if(!((String)value).endsWith("1234")){
			throw new ValidatorException(
					new FacesMessage("Server validation: Phone number must end with 1234",""));
		}
	}
	
	public String getResponse() {
		return response;
	}

	public void submit() {
		response = "Phone:" + phone + ", Color: " + color + ", Number: " + number
			+ ", Date: " + date + ", Text: " + text;
	}
	
    public SelectItem[] getColors() {
        return colors;
    }

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}
}
