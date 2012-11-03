/*
 * $Id: HelloAction.java,v 1.5 2007/12/13 18:13:46 jacky Exp $
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

import org.operamasks.faces.annotation.Inject;
import org.operamasks.faces.annotation.ManagedBean;
import org.operamasks.faces.annotation.ManagedBeanScope;
import org.operamasks.faces.annotation.Bind;
import org.operamasks.faces.annotation.Action;

/**
 * 这是Hello应用的动作对象，仅包含应用中要执行的动作，可以通过@Bind指令注入模型对象。
 *
 * 动作对象一般不需要保持状态，因此可以将Managed Bean的活动范围设置为REQUEST。
 */
@ManagedBean(scope=ManagedBeanScope.REQUEST)
public class HelloAction
{
    @Inject("#{HelloBean}")
    private HelloBean model;

    @Action
    public String sayHello() {
        model.setResponse("Hello, " + model.getGreeting());
        return "response.xhtml";
    }

    @Action
    public String back() {
        return "greeting.xhtml";
    }
}
