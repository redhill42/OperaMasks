/*
 * $Id: UITextArea.java,v 1.6 2008/03/11 03:21:00 lishaochuan Exp $
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

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import org.operamasks.faces.util.FacesUtils;

/**
 * @deprecated 此类已经被org.operamasks.faces.component.form.impl.UITextArea代替
 */
@Deprecated
public class UITextArea extends UITextField {
    public static final java.lang.String COMPONENT_TYPE = "org.operamasks.faces.widget.TextArea";
    public static final String DEFAULT_RENDERER_TYPE = "org.operamasks.faces.widget.TextArea";
    private TextAreaConfig textConfig = new TextAreaConfig();
    
    public UITextArea() {
        setRendererType(DEFAULT_RENDERER_TYPE);
    }
    
    public UITextArea(UIComponent parent) {
        this();
        FacesUtils.createComponent(parent, this);
    }
    
    /**
     * Ext属性，这个数组中的元素必须是按字母排序的。
     */
    private static final String[] EXT_CONFIGS = {        
        "preventScrollbars"};
    protected String[] getExtConfigElements() {
        String[] parentArray = super.getExtConfigElements();
        String[] newArray = new String[EXT_CONFIGS.length + parentArray.length];
        System.arraycopy(EXT_CONFIGS, 0, newArray, 0, EXT_CONFIGS.length);
        System.arraycopy(parentArray, 0, newArray, EXT_CONFIGS.length, parentArray.length);
        Arrays.sort(newArray);
        return newArray;
    }
    public Boolean getPreventScrollbars() {
        return textConfig.getPreventScrollbars();
    }

    public void setPreventScrollbars(Boolean preventScrollbars) {
        textConfig.setPreventScrollbars(preventScrollbars);
    }
    
    public Object saveState(FacesContext context) {
        return new Object[] {
            super.saveState(context),
            saveAttachedState(context, textConfig)
        };
    }

    public void restoreState(FacesContext context, Object state) {
        Object[] values = (Object[])state;
        int i = 0;
        super.restoreState(context, values[i++]);
        textConfig = (TextAreaConfig)restoreAttachedState(context, values[i++]);
    }
    
    protected TextAreaConfig getExtConfig() {
        return getTextAreaConfig();
    }
    
    public TextAreaConfig getTextAreaConfig() {
        textConfig.merge(getTextConfig());
        return textConfig;
    }

    public void setTextAreaConfig(TextAreaConfig textConfig) {
        this.textConfig = textConfig;
    }
}
