package demo;

import javax.faces.component.UICommand;
import javax.faces.component.UIComponent;
import javax.faces.component.UISelectItems;
import javax.faces.component.html.HtmlCommandButton;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.operamasks.faces.annotation.ManagedBean;
import org.operamasks.faces.annotation.ManagedBeanScope;
import org.operamasks.faces.component.widget.UICombo;
import org.operamasks.faces.component.widget.UISeparator;
import org.operamasks.faces.component.widget.UIToolBar;
import org.operamasks.faces.component.widget.menu.UICommandMenuItem;
import org.operamasks.faces.component.widget.menu.UILinkMenuItem;
import org.operamasks.faces.component.widget.menu.UIMenu;
import org.operamasks.faces.util.UIComponentFactory;

@ManagedBean(name="DynamicToolBar", scope=ManagedBeanScope.REQUEST)
public class DynamicToolBarBean {
    private SelectItem[] colors = {
            new SelectItem("Red", "Red Color"),
            new SelectItem("Green", "Green Color"),
            new SelectItem("Blue", "Blue Color")
        };
    
	private UIToolBar toolbar;
	private String response;

	public UIToolBar getToolbar() {
		return toolbar;
	}

	public void setToolbar(UIToolBar toolbar) {
		this.toolbar = toolbar;
	}

	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
	}
	
	public void addMenu() {
		for (UIComponent component : toolbar.getChildren()) {
			if (component instanceof UIMenu) {
				response = "Menu has already added.";
				
				return;
			}
		}
		
		FacesContext context = FacesContext.getCurrentInstance();
		
		UIComponentFactory factory = new UIComponentFactory();
		
		UICommandMenuItem text = createMenuItem(context, factory, "文本文件");
		UICommandMenuItem xml = createMenuItem(context, factory, "XML文件");
		UICommandMenuItem java = createMenuItem(context, factory, "Java文件");

		UIMenu _new = createMenu(context, factory, "新建", "images/new.gif");
		_new.getChildren().add(text);
		_new.getChildren().add(xml);
		_new.getChildren().add(java);
		
		UICommandMenuItem open = createCommandMenuItem(context, factory, "打开", "images/open.gif");
		UICommandMenuItem save = createCommandMenuItem(context, factory, "保存", "images/save.gif");
		UISeparator separator = createSeparator(context, factory);
		UILinkMenuItem close = createLinkMenuItem(context, factory, "关闭", "dynamicToolbar.jsp", "return confirm('Are you sure?')");

		UIMenu file = createMenu(context, factory, "File", null, "#{DynamicToolBar.menuAction}");
		file.getChildren().add(_new);
		file.getChildren().add(open);
		file.getChildren().add(save);
		file.getChildren().add(separator);
		file.getChildren().add(close);
		
		toolbar.addItem(file);
	}
	
	private UILinkMenuItem createLinkMenuItem(FacesContext context,
			UIComponentFactory factory, String label, String value,
			String onclick) {
		factory.clear();
		factory.addAttribute("label", label).
				addAttribute("value", value).
				addAttribute("onclick", onclick);

		return factory.createLinkMenuItem(context);
	}

	private UISeparator createSeparator(FacesContext context,
			UIComponentFactory factory) {
		factory.clear();
		
		return (UISeparator)factory.createComponent(context, UISeparator.COMPONENT_TYPE);
	}

	private UIMenu createMenu(FacesContext context, UIComponentFactory factory,
			String label, String image) {
		return createMenu(context, factory, label, image, null);
	}
	
	private UIMenu createMenu(FacesContext context, UIComponentFactory factory,
			String label, String image, String action) {
		factory.clear();
		
		factory.addAttribute("label", label);
		if (image != null)
			factory.addAttribute("image", image);
		if (action != null)
			factory.addAttribute("action", action);

		return factory.createMenu(context);
	}

	private UICommandMenuItem createMenuItem(FacesContext context,
			UIComponentFactory factory, String label) {
		factory.clear();
		factory.addAttribute("label", label);
		
		return factory.createCommandMenuItem(context);
	}
	
	private UICommandMenuItem createCommandMenuItem(FacesContext context,
			UIComponentFactory factory, String label, String image) {
		factory.clear();
		
		factory.addAttribute("label", label);
		if (image != null)
			factory.addAttribute("image", image);
		
		return factory.createCommandMenuItem(context);
	}

	public void addButton() {
		for (UIComponent component : toolbar.getChildren()) {
			if (component instanceof UICommand && isNotMenu(component)) {
				response = "Button has already added.";
				
				return;
			}
		}
		
		HtmlCommandButton command = new UIComponentFactory().
						addAttribute("value", "Click me").
						addAttribute("image", "images/example.gif").
						addAttribute("action", "#{DynamicToolBar.click}").
						createButton(FacesContext.getCurrentInstance());
		
		toolbar.addItem(command);
	}

	private boolean isNotMenu(UIComponent component) {
		return (!(component instanceof UIMenu) && !(component instanceof UICommandMenuItem));
	}

	public void addCombo() {
		for (UIComponent component : toolbar.getChildren()) {
			if (component instanceof UICombo) {
				response = "Combo has already added.";
				
				return;
			}
		}
		
		UIComponentFactory factory = new UIComponentFactory().
						addAttribute("valueChangeListener", "#{DynamicToolBar.valueChange}").
						addAttribute("emptyText", "请选择颜色...").
						addAttribute("typeAhead", true);
		UICombo combo = factory.createCombo(FacesContext.getCurrentInstance());
		
		factory.clear();
		factory.addAttribute("value", "#{DynamicToolBar.colors}");
		UISelectItems selectItems = factory.createUISelectItems(FacesContext.getCurrentInstance());
		
		combo.getChildren().add(selectItems);
		
		toolbar.addItem(combo);
	}
	
	public void valueChange(ValueChangeEvent event) {
		response = event.getNewValue() + " selected";
	}
	
	public void click() {
		response = "Click me";
	}
	
    public String menuAction(UIComponent item) {
        String label = (String)item.getAttributes().get("label");
        response = item.getId() + " (" + label + ") selected";

        return null;
    }

    public SelectItem[] getColors() {
        return colors;
    }
    
    public void addSeparator() {
    	toolbar.addItem(createSeparator(FacesContext.getCurrentInstance(), new UIComponentFactory()));
    }
}
