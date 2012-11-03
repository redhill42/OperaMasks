/*
 * $Id: UICombo.java,v 1.23 2008/03/11 03:21:00 lishaochuan Exp $
 *
 * Copyright (C) 2006 Operamasks Community.
 * Copyright (C) 2000-2006 Apusic Systems, Inc.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see http://www.gnu.org/licenses.
 */

package org.operamasks.faces.component.widget;

import java.util.Arrays;
import java.util.Map;

import javax.el.ELContext;
import javax.el.ELException;
import javax.el.ValueExpression;
import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.component.html.HtmlSelectOneMenu;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;

import org.operamasks.faces.render.widget.yuiext.ComponentOperationManager;
import org.operamasks.faces.util.FacesUtils;

/**
 * @deprecated 此类已经被org.operamasks.faces.component.form.impl.UICombo代替
 */
@Deprecated
public class UICombo extends HtmlSelectOneMenu
{
    public static final java.lang.String COMPONENT_TYPE = "org.operamasks.faces.widget.Combo";

    private ComboConfig comboConfig = new ComboConfig();

    private String jsvar;

    public UICombo() {
        setRendererType("org.operamasks.faces.widget.Combo");
    }
    
    public UICombo(UIComponent parent) {
        this();
        FacesUtils.createComponent(parent, this);
    }

    public String getJsvar() {
        if (this.jsvar != null) {
            return this.jsvar;
        }
        ValueExpression ve = getValueExpression("jsvar");
        if (ve != null) {
            return (String)ve.getValue(getFacesContext().getELContext());
        } else {
            return null;
        }
    }

    public void setJsvar(String jsvar) {
        this.jsvar = jsvar;
    }

    /**
     * Ext属性，这个数组中的元素必须是按字母排序的。
     */
    private static final String[] EXT_CONFIGS = {
        "allowBlank",
        "disableKeyFilter",
        "editable",
        "emptyText",
        "forceSelection",
        "grow",
        "growMax",
        "growMin",
        "handleHeight",
        "listAlign",
        "listWidth",
        "maskRe",
        "maxHeight",
        "maxlength", // Ext的属性为 maxLength, Html input text 元素的属性为 maxlength
        "minChars",
        "minLength",
        "minListWidth",
        "onTriggerClick",
        "queryDelay",
        "resizable",
        "selectedClass",
        "selectOnFocus",
        "shadow",
        "triggerClass",
        "typeAhead",
        "typeAheadDelay",
        "valueNotFoundText",
        "vtype",
        "width"};

    protected String[] getExtConfigElements() {
        return EXT_CONFIGS;
    }

    public Boolean getAllowBlank() {
        return comboConfig.getAllowBlank();
    }

    public void setAllowBlank(Boolean value) {
        comboConfig.setAllowBlank(value);
    }

    // 在facelets情况下，因为没有通过tag来调用组件模型，因此不会调用到setValueExpression方法；
    // 没有在组件中声明的属性将不会被设置值。
    @Override
    public void setValueExpression(String name, ValueExpression binding) {
        super.setValueExpression(name, binding);

        if (Arrays.binarySearch(getExtConfigElements(), name) >= 0) {
            comboConfig.set(name, binding);
            if (binding != null && binding.isLiteralText()) {
                ELContext context = FacesContext.getCurrentInstance().getELContext();
                try {
                    // 用实际的值覆盖前面设置的 ValueExpression
                    comboConfig.set(name, binding.getValue(context));
                } catch (ELException ele) {
                    throw new FacesException(ele);
                }
            }
        }
    }

    public Integer getListWidth() {
        return comboConfig.getListWidth();
    }

    public void setListWidth(Integer listWidth) {
        comboConfig.setListWidth(listWidth);
    }

    public String getListClass() {
        return comboConfig.getListClass();
    }

    public void setListClass(String listClass) {
        comboConfig.setListClass(listClass);
    }

    public String getSelectedClass() {
        return comboConfig.getSelectedClass();
    }

    public void setSelectedClass(String selectedClass) {
        comboConfig.setSelectedClass(selectedClass);
    }

    public String getTriggerClass() {
        return comboConfig.getTriggerClass();
    }

    public void setTriggerClass(String triggerClass) {
        comboConfig.setTriggerClass(triggerClass);
    }

    public String getShadow() {
        return comboConfig.getShadow();
    }

    public void setShadow(String shadow) {
        comboConfig.setShadow(shadow);
    }

    public String getListAlign() {
        return comboConfig.getListAlign();
    }

    public void setListAlign(String listAlign) {
        comboConfig.setListAlign(listAlign);
    }

    public Integer getMaxHeight() {
        return comboConfig.getMaxHeight();
    }

    public void setMaxHeight(Integer maxHeight) {
        comboConfig.setMaxHeight(maxHeight);
    }

    public Integer getMinChars() {
        return comboConfig.getMinChars();
    }

    public void setMinChars(Integer minChars) {
        comboConfig.setMinChars(minChars);
    }

    public Boolean getTypeAhead() {
        return comboConfig.getTypeAhead();
    }

    public void setTypeAhead(Boolean typeAhead) {
        comboConfig.setTypeAhead(typeAhead);
    }

    public Integer getQueryDelay() {
        return comboConfig.getQueryDelay();
    }

    public void setQueryDelay(Integer queryDelay) {
        comboConfig.setQueryDelay(queryDelay);
    }

    public Boolean getSelectOnFocus() {
        return comboConfig.getSelectOnFocus();
    }

    public void setSelectOnFocus(Boolean selectOnFocus) {
        comboConfig.setSelectOnFocus(selectOnFocus);
    }

    public Boolean getResizable() {
        return comboConfig.getResizable();
    }

    public void setResizable(Boolean resizable) {
        comboConfig.setResizable(resizable);
    }

    public Integer getHandleHeight() {
        return comboConfig.getHandleHeight();
    }

    public void setHandleHeight(Integer handleHeight) {
        comboConfig.setHandleHeight(handleHeight);
    }

    public Boolean getEditable() {
        return comboConfig.getEditable();
    }

    public void setEditable(Boolean editable) {
        comboConfig.setEditable(editable);
        ComponentOperationManager cm = ComponentOperationManager.getInstance(getFacesContext());
        cm.getAttributes().put("jsvar", FacesUtils.getJsvar(getFacesContext(), this));
        cm.getAttributes().put("editable", editable);
        cm.invoke(getFacesContext(), "setEditable", this);
    }

	@Override
	public void setReadonly(boolean readonly) {
		super.setReadonly(readonly);
        ComponentOperationManager cm = ComponentOperationManager.getInstance(getFacesContext());
        cm.getAttributes().put("jsvar", FacesUtils.getJsvar(getFacesContext(), this));
        cm.getAttributes().put("readonly", readonly);
        cm.invoke(getFacesContext(), "setReadonly", this);
	}
	
	@Override
	public void setDisabled(boolean disabled) {
		super.setDisabled(disabled);
        ComponentOperationManager cm = ComponentOperationManager.getInstance(getFacesContext());
        cm.getAttributes().put("jsvar", FacesUtils.getJsvar(getFacesContext(), this));
        cm.getAttributes().put("disabled", disabled);
        cm.invoke(getFacesContext(), "setDisabled", this);
	}

	public void select(int index) {
        ComponentOperationManager cm = ComponentOperationManager.getInstance(getFacesContext());
        cm.getAttributes().put("jsvar", FacesUtils.getJsvar(getFacesContext(), this));
        cm.getAttributes().put("selectIndex", new Integer(index));
        cm.invoke(getFacesContext(), "select", this);
    }

    public Integer getMinListWidth() {
        return comboConfig.getMinListWidth();
    }

    public void setMinListWidth(Integer minListWidth) {
        comboConfig.setMinListWidth(minListWidth);
    }

    public Boolean getForceSelection() {
        return comboConfig.getForceSelection();
    }

    public void setForceSelection(Boolean forceSelection) {
        comboConfig.setForceSelection(forceSelection);
    }

    public Integer getTypeAheadDelay() {
        return comboConfig.getTypeAheadDelay();
    }

    public void setTypeAheadDelay(Integer typeAheadDelay) {
        comboConfig.setTypeAheadDelay(typeAheadDelay);
    }

    public String getValueNotFoundText() {
        return comboConfig.getValueNotFoundText();
    }

    public void setValueNotFoundText(String valueNotFoundText) {
        comboConfig.setValueNotFoundText(valueNotFoundText);
    }

    public String getOnTriggerClick() {
        return comboConfig.getOnTriggerClick();
    }

    public void setOnTriggerClick(String onTriggerClick) {
        comboConfig.setOnTriggerClick(onTriggerClick);
    }

    public String getEmptyText() {
        return comboConfig.getEmptyText();
    }

    public void setEmptyText(String emptyText) {
        comboConfig.setEmptyText(emptyText);
//        ComponentOperationManager cm = ComponentOperationManager.getInstance(getFacesContext());
//        cm.getAttributes().put("jsvar", FacesUtils.getJsvar(getFacesContext(), this));
//        cm.getAttributes().put("emptyText", emptyText);
//        cm.invoke(getFacesContext(), "setEmptyText", this);
    }

    public Integer getWidth() {
        return comboConfig.getWidth();
    }

    public void setWidth(Integer value) {
        comboConfig.setWidth(value);
    }

    public Object saveState(FacesContext context) {
        return new Object[] {
            super.saveState(context),
            jsvar,
            saveAttachedState(context, comboConfig)
        };
    }

    public void restoreState(FacesContext context, Object state) {
        Object[] values = (Object[])state;
        int i = 0;
        super.restoreState(context, values[i++]);
        jsvar = ((String)values[i++]);
        comboConfig = (ComboConfig)restoreAttachedState(context, values[i++]);
    }

    public ComboConfig getComboConfig() {
        return comboConfig;
    }

    public void setComboConfig(ComboConfig comboConfig) {
        this.comboConfig = comboConfig;
    }

    @Override
    protected void validateValue(FacesContext context, Object newValue) {
            UIInputDelegate delegate = new UIInputDelegate();            
            delegate.validateValue(context, newValue);
    }

    private class UIInputDelegate extends UIInput {
        public UIInputDelegate() {
            Validator[] vs = UICombo.this.getValidators();
            for (int i = 0; i < vs.length; i++) {
                this.addValidator(vs[i]);
            }
        }
        public String getId() {
            return UICombo.this.getId();
        }

        public String getClientId(FacesContext context) {
            return UICombo.this.getClientId(context);
        }

        public UIComponent getParent() {
            return UICombo.this.getParent();
        }

        public void resetValue() {
            UICombo.this.resetValue();
        }

        public boolean isRequired() {
            return UICombo.this.isRequired();
        }

        public Validator[] getValidators() {
            return UICombo.this.getValidators();
        }

        public String getRequiredMessage() {
            return UICombo.this.getRequiredMessage();
        }

        public String getConverterMessage() {
            return UICombo.this.getConverterMessage();
        }

        public String getValidatorMessage() {
            return UICombo.this.getValidatorMessage();
        }

        public boolean isValid() {
            return UICombo.this.isValid();
        }

        public void setValid(boolean valid) {
            UICombo.this.setValid(valid);
        }
        
        public Map<String, Object> getAttributes() {
            return UICombo.this.getAttributes();
        }
        
        public ValueExpression getValueExpression(String name) {
            return UICombo.this.getValueExpression(name);
        }
        
        // 扩大可访问性为 public
        public void validateValue(FacesContext context, Object newValue) {
            super.validateValue(context, newValue);
        }
    }
}
