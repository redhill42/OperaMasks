/*
 * $Id: MenuBean.java,v 1.2 2007/12/11 04:14:14 jacky Exp $
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

package demo;

import javax.faces.component.UIComponent;
import javax.faces.component.UIOutput;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import java.util.Date;

import org.operamasks.faces.annotation.ManagedBean;
import org.operamasks.faces.annotation.ManagedBeanScope;

@ManagedBean(scope=ManagedBeanScope.SESSION)
public class MenuBean
{
    private UIOutput response;
    
    public UIOutput getResponse() {
        return response;
    }
    public void setResponse(UIOutput response) {
        this.response = response;
    }

    public String menuAction(UIComponent item) {
        if (response != null) {
            String label = (String)item.getAttributes().get("label");
            response.setValue(item.getId() + " (" + label + ") selected");
        }
        return null;
    }

    public void checkValueChanged(ValueChangeEvent event) {
        System.out.println(String.format("Check value changed, id=%s, oldValue=%s, newValue=%s",
                                         event.getComponent().getId(), event.getOldValue(), event.getNewValue()));
    }

    private boolean bold, italic, underline;

    public boolean isBold() {
        return bold;
    }

    public void setBold(boolean bold) {
        this.bold = bold;
    }

    public boolean isItalic() {
        return italic;
    }

    public void setItalic(boolean italic) {
        this.italic = italic;
    }

    public boolean isUnderline() {
        return underline;
    }

    public void setUnderline(boolean underline) {
        this.underline = underline;
    }

    public void checkAll() {
        bold = italic = underline = true;
    }

    public void uncheckAll() {
        bold = italic = underline = false;
    }

    public enum COLOR { RED, GREEN, BLUE };

    private COLOR color = COLOR.RED;

    public COLOR getColor() {
        return color;
    }

    public void setColor(COLOR color) {
        this.color = color;
    }

    public SelectItem[] getColors() {
        return new SelectItem[] {
            new SelectItem(COLOR.RED, "Red"),
            new SelectItem(COLOR.GREEN, "Green"),
            new SelectItem(COLOR.BLUE, "Blue")
        };
    }

    public void radioValueChanged(ValueChangeEvent event) {
        System.out.println(String.format("Check value changed, id=%s, oldValue=%s, newValue=%s",
                                         event.getComponent().getId(), event.getOldValue(), event.getNewValue()));
    }

    private Date date = new Date();

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
