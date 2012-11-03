/*
 * $Id: HelloBean.java,v 1.2 2007/12/11 04:14:14 jacky Exp $
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

import java.util.Date;

import org.operamasks.faces.annotation.ManagedBean;
import org.operamasks.faces.annotation.ManagedBeanScope;
import org.operamasks.faces.annotation.Bind;
import org.operamasks.faces.annotation.Action;
import org.operamasks.faces.annotation.Outject;
import org.operamasks.faces.annotation.Required;
import org.operamasks.faces.annotation.Pattern;

/**
 * 这是Hello应用的主模型对象，用于保存视图中的数据。
 *
 * 本例仅为说明如何使用OperaMasks的MVC架构，对于这样一个简单的例子将模型对象和动作对象完全
 * 分离是不必要的。
 *
 * 本例将Managed Bean的活动范围设置为SESSION，因此可以在服务器保持模型状态。
 */
@ManagedBean(scope=ManagedBeanScope.SESSION)
public class HelloBean
{
    @Bind(view="greeting") @Required
    private String greeting;

    @Outject
    @Bind(view="response")
    private String response;

    @Bind
    @Pattern("yyyy-MM-dd HH:mm:ss")
    public Date getCurrentTime() {
        return new Date();
    }

    public String getGreeting() {
        return greeting;
    }

    public void setGreeting(String greeting) {
        this.greeting = greeting;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }
}
