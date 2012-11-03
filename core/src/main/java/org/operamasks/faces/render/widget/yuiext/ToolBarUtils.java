package org.operamasks.faces.render.widget.yuiext;

import java.io.IOException;
import java.util.Formatter;

import javax.faces.component.NamingContainer;
import javax.faces.component.UICommand;
import javax.faces.component.UIComponent;
import javax.faces.component.UIForm;
import javax.faces.component.html.HtmlCommandButton;
import javax.faces.context.FacesContext;
import javax.faces.render.Renderer;

import org.operamasks.faces.component.form.impl.UICombo;
import org.operamasks.faces.component.form.impl.UIField;
import org.operamasks.faces.component.widget.UIPagingToolbar;
import org.operamasks.faces.component.widget.UISeparator;
import org.operamasks.faces.component.widget.UIToolBar;
import org.operamasks.faces.component.widget.menu.UIMenu;
import org.operamasks.faces.render.html.HtmlRenderer;
import org.operamasks.faces.render.resource.AbstractResource;
import org.operamasks.faces.render.resource.ResourceManager;
import org.operamasks.faces.util.FacesUtils;
import org.operamasks.faces.util.HtmlEncoder;

public class ToolBarUtils {
	public static void encodeToolBarButton(final ResourceManager rm,final StringBuilder buf,final  UIComponent component,
			String jsvar,final UIComponent parent) {
		Formatter fmt = new Formatter(buf);
		
		// Create button instance
		fmt.format("%s=new Ext.Toolbar.Button({", jsvar);

		String text = getText(component);
		String tooltip = (String)component.getAttributes().get("tooltip");
		Object disabled = component.getAttributes().get("disabled");
		Object minWidth = component.getAttributes().get("minWidth");
		String icon = getIcon(component);

		// Config button
		fmt.format("text:%s", HtmlEncoder.enquote(text, '\''));
		if (tooltip != null)
		    fmt.format(",tooltip:%s", HtmlEncoder.enquote(tooltip, '\''));
		if (disabled != null)
		    fmt.format(",disabled:%b", disabled);
		if (minWidth != null)
		    fmt.format(",minWidth:%s", minWidth);
		if (icon != null) {
			fmt.format(",icon:%s", HtmlEncoder.enquote(icon, '\''));
			fmt.format(",iconCls:%s", HtmlEncoder.enquote("x-btn-text-icon", '\''));
		}
		buf.append("});\n");

		rm.registerResource(new AbstractResource(getResourceId(component)) {
            @Override
            public int getPriority() {
                return LOW_PRIORITY - 300;
            }
            
            @Override
            public void encodeBegin(FacesContext context) throws IOException {
                ToolBarUtils.appendItemToToolBar(rm, component);
            }
        });
	}
	
	private static String getResourceId(UIComponent component) {
        return "urn:toolBarButton:" + component.getClientId(FacesContext.getCurrentInstance());
    }
	
    private static String getText(UIComponent component) {
        String text = (String)component.getAttributes().get("label");
        if (text == null) {
            Object value = ((UICommand)component).getValue();
            text = (value != null) ? value.toString() : "";
        }
        return text;
    }

    protected static String getHiddenFieldName(FacesContext context, UIComponent component) {
        String result = null;
        UIForm form = HtmlRenderer.getParentForm(component);
        if (form != null) {
            result = form.getClientId(context) + NamingContainer.SEPARATOR_CHAR + "_link";
        }
        return result;
    }
    
    private static String getIcon(UIComponent component) {
        return (String)component.getAttributes().get("image");
	}

	public static void appendItemToToolBar(ResourceManager rm, UIComponent component) {
		YuiExtResource resource = YuiExtResource.register(rm, "Ext.Toolbar");
	
	    StringBuilder buf = new StringBuilder();
	    Formatter fmt = new Formatter(buf);
	    FacesContext context = FacesContext.getCurrentInstance();
	    
		UIComponent parent = component.getParent();
		if (!(parent instanceof UIToolBar) && (!(parent instanceof UIPagingToolbar)))
			return;
		
		if (ToolBarUtils.isToolBarButton(component)) {
			fmt.format(
					"\nif (typeof %1$s != 'undefined') {" +
	                	"\n%2$s.addButton(%1$s);" +
	               	"\n}",
	                FacesUtils.getJsvar(context, component),
	                FacesUtils.getJsvar(context, parent)
			);
		} else if (ToolBarUtils.isToolBarCombo(component)) {
			fmt.format(
					"\nif (typeof %1$s != 'undefined') {" +
	                	"\n%2$s.addField(%1$s);" +
	               	"\n}",
	                FacesUtils.getJsvar(context, component),
	                FacesUtils.getJsvar(context, parent)
			);
		} else {
			fmt.format(
					"\nif (typeof %1$s != 'undefined') {" +
	                	"\n%2$s.add(%1$s);" +
	               	"\n}",
	               	FacesUtils.getJsvar(context, component),
	                FacesUtils.getJsvar(context, parent)
			);
		}
		
		resource.addInitScript(buf.toString());
	}

	private static boolean isToolBarCombo(UIComponent component) {
		return component instanceof UICombo && component.getRendererType().equals("org.operamasks.faces.widget.ToolBarCombo");
	}

	private static boolean isToolBarButton(UIComponent component) {
		return component instanceof UICommand && component.getRendererType().equals("org.operamasks.faces.widget.ToolBarButton");
	}

	public static void adjustToolBarItemsRenderer(UIToolBar toolBar) {
		ToolBarUtils.adjustToolBarItemsRenderer((UIComponent)toolBar);
	}

	public static void adjustToolBarItemsRenderer(UIPagingToolbar toolBar) {
		ToolBarUtils.adjustToolBarItemsRenderer((UIComponent)toolBar);
	}

	private static void adjustToolBarItemsRenderer(UIComponent toolBar) {
		if (toolBar.getChildCount() > 0) {
	        for (UIComponent child : toolBar.getChildren()) {
	        	ToolBarUtils.adjustToolBarItemRenderer(child);
	        }
	    }
	}

	public static void adjustToolBarItemRenderer(UIComponent item) {
		if (ToolBarUtils.isSeparator(item)) {
			item.setRendererType("org.operamasks.faces.widget.ToolBarSeparator");
		} else if (ToolBarUtils.isButton(item)) {
			item.setRendererType("org.operamasks.faces.widget.ToolBarButton");
		} else if (ToolBarUtils.isField(item)) {
			item.setRendererType("org.operamasks.faces.widget.ToolBarCombo");
		} else if (ToolBarUtils.isMenu(item)) {
			item.setRendererType("org.operamasks.faces.widget.ToolBarMenu");
		}
	}

	private static boolean isMenu(UIComponent component) {
		return component instanceof UIMenu && component.getRendererType().equals("org.operamasks.faces.widget.Menu");
	}

	private static boolean isField(UIComponent component) {
		return component instanceof UIField && component.getRendererType().equals("org.operamasks.faces.widget.Combo");
	}

	private static boolean isButton(UIComponent component) {
		return component instanceof HtmlCommandButton &&
		(component.getRendererType().equals("org.operamasks.faces.widget.Button") ||
				component.getRendererType().equals("javax.faces.Button"));
	}

	private static boolean isSeparator(UIComponent component) {
		return component instanceof UISeparator;
	}

	public static String renderToolbar(FacesContext context, UIToolBar toolbar) {
		return renderToolbar(context, (UIComponent)toolbar);
	}

	private static String renderToolbar(FacesContext context, UIComponent component) {
		UIComponent parent = component.getParent();
		
		Renderer renderer = FacesUtils.getRenderer(context, parent);
			
		// ToolBarContainer should call render() itself
		if (renderer != null && ToolBarContainer.class.isAssignableFrom(renderer.getClass()))
			return "";
		else
			return String.format("\n%s.render('%s');",
					FacesUtils.getJsvar(context, component),
					component.getClientId(context)
				);
	}

	public static String renderToolbar(FacesContext context, UIPagingToolbar pager) {
		return renderToolbar(context, (UIComponent)pager);
	}
}
