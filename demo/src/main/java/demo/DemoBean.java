/*
 * $Id: DemoBean.java,v 1.5 2008/01/14 07:54:59 yangdong Exp $
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.operamasks.faces.annotation.EventListener;
import org.operamasks.faces.annotation.ListEntries;
import org.operamasks.faces.annotation.ManagedBean;
import org.operamasks.faces.annotation.ManagedBeanScope;
import org.operamasks.faces.annotation.ManagedProperty;
import org.operamasks.faces.annotation.MapEntries;
import org.operamasks.faces.annotation.MapEntry;

@ManagedBean(scope=ManagedBeanScope.SESSION)
public class DemoBean implements java.io.Serializable
{
    /* 使用@ManagedProperty来定义并初始化一个ManagedBean属性。利用此方法可以
     × 方便地声明一个属性而不必定义get和set方法，属性的初始值可以来自于EL表达式。
     */
    @ManagedProperty(value="#{ColorBean}")
    private ColorBean color;

    /* 注意，color属性是只读的，这是由于只定义了get方法而没有定义set方法，但
     × 初始值仍会被注入。
     */
    public ColorBean getColor() {
        return color;
    }

    /* 使用@ListEntries的例子 */
    @ListEntries({"Sun", "Mon", "Tue", "Wen", "Thu", "Fri", "Sat"})
    private String[] dates;

    /* 使用@MapEntries的例子 */
    @MapEntries({
        @MapEntry(key="red", value="#ff0000"),
        @MapEntry(key="green", value="#00ff00"),
        @MapEntry(key="blue", value="#0000ff")
    })
    private Map<String,String> colorMap;

    /* 与stateless session bean互相传递数据 */
    @ManagedProperty
    private String name;
    @ManagedProperty
    private String greeting;
    
    @ManagedProperty
    private Integer number;

    @ManagedProperty
    private Date date;
    
    @ManagedProperty
    private String dateString;

    @ManagedProperty
    private int width = 200;

    @ManagedProperty
    private String format = "Y/m/d";

    @ManagedProperty
    private boolean richForm;

    public String delay() {
        try {
            Thread.sleep(2000);
        } catch (Exception e) {}
        return null;
    }

    private int clickCount;
    private String clickText;
    
    private int clickCount2;
    private String clickText2;

    public String getClickText() {
        return clickText == null ? "Click Me" : clickText;
    }
    
    public String getClickText2() {
        return clickText2 == null ? "Click Me" : clickText2;
    }

    public String click() {
        clickCount++;
        clickText = "You clicked me " + clickCount + " times";
        return null;
    }
    
    public String click2(Map<String, String> params) {
    	clickCount2++;
        clickText2 = String.format("Hello, %s. You clicked me %d times", params.get("name"), clickCount2);
        return null;
    }

    private String skin = null;

    public String getSkin() {
        if (this.skin == null) {
            FacesContext context = FacesContext.getCurrentInstance();
            Cookie cookie = (Cookie)context.getExternalContext().getRequestCookieMap().get("skin");
            if (cookie != null) skin = cookie.getValue();
            if (skin == null) skin = "classic";
        }
        return skin;
    }

    public void setSkin(String skin) {
        this.skin = skin;
    }

    public String chooseSkin() {
        try {
            FacesContext context = FacesContext.getCurrentInstance();
            ExternalContext extctx = context.getExternalContext();
            extctx.getSessionMap().put("com.apusic.faces.widget.SKIN", this.skin);

            HttpServletResponse response = (HttpServletResponse)extctx.getResponse();
            Cookie cookie = new Cookie("skin", this.skin);
            cookie.setMaxAge(30*86400);
            response.addCookie(cookie);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    @Resource(name="foo")
    private String foo;

    @PostConstruct
    private void init() {
        System.out.println("DemoBean initialized...");
        System.out.println("Injected resource: " + foo);
    }
    
    public void dateToString() {
    	if (date == null) {
    		dateString = "";
    		return;
    	}
    	
    	SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
    	dateString = format.format(date);
    }
    
    public void stringToDate() {
    	if (dateString == null || "".equals(dateString)) {
    		date = null;
    		return;
    	}
    	
    	SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
    	try {
			date = format.parse(dateString);
		} catch (ParseException e) {
			dateString = "Illegal date format. You should input a string which's format is yyyy/MM/dd.";
		}
    }
    
    @EventListener
    public String showResponse(String response) {
        this.greeting = response;
        return null;
    }
}
