/*
 * $Id: ClickBean.java,v 1.2 2007/12/11 04:14:14 jacky Exp $
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
package demo.binding;

import javax.faces.event.ActionEvent;
import org.operamasks.faces.annotation.*;

@ManagedBean(scope=ManagedBeanScope.REQUEST) // stateless managed bean
public class ClickBean
{
    @Bind
    private int count = 0; // 利用inputHidden域可实现无状态数据绑定

    @Bind
    public String getText() {
        return (this.count == 0) ? "Click Me" : "You clicked me " + count + " times";
    }

    @Action
    public void text_onclick() {
        this.count++;
    }

    @Action
    public void reset() {
        this.count = 0;
    }

    @ActionListeners({
        @ActionListener(id="text", event="onclick"),
        @ActionListener(id="reset")
    })
    public void logEvent(ActionEvent event) {
        System.out.println(event.getComponent().getId() + " clicked");
    }
}
