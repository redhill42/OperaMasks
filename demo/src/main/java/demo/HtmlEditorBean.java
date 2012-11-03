/*
 * $Id: HtmlEditorBean.java,v 1.1 2008/01/16 03:02:55 lishaochuan Exp $
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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.operamasks.faces.annotation.ManagedBean;
import org.operamasks.faces.annotation.ManagedBeanScope;
import org.operamasks.faces.annotation.ManagedProperty;
import org.operamasks.faces.component.widget.UIDataView;

@ManagedBean(scope= ManagedBeanScope.SESSION)
public class HtmlEditorBean implements Serializable
{
    @ManagedProperty
    private String message;
    
    @ManagedProperty
    private List<String> messages = new ArrayList<String>();
    
    @ManagedProperty
    private UIDataView view;
    
    public void action(){
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {}
        messages.add(message);
        view.reload();
    }
}