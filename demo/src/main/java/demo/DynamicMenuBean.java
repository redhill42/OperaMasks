/*
 * $Id: DynamicMenuBean.java,v 1.2 2007/12/11 04:14:14 jacky Exp $
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

import java.util.ArrayList;
import java.util.List;

import javax.faces.application.Application;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import org.operamasks.faces.annotation.ManagedBean;
import org.operamasks.faces.annotation.ManagedBeanScope;
import org.operamasks.faces.annotation.ManagedProperty;
import org.operamasks.faces.component.widget.UISeparator;
import org.operamasks.faces.component.widget.menu.UICommandMenuItem;
import org.operamasks.faces.component.widget.menu.UILinkMenuItem;
import org.operamasks.faces.component.widget.menu.UIMenu;

@ManagedBean(scope=ManagedBeanScope.SESSION)
public class DynamicMenuBean {
    @ManagedProperty
    private List<MenuInfo> menus;
    
    @ManagedProperty 
    private UIMenu menu;
    
    public DynamicMenuBean() {
        initMenu();
    }

    private void initMenu() {
        menus = new ArrayList<MenuInfo>();
        
        MenuInfo menu = new MenuInfo("文件");
        menu.items.add(new MenuItemInfo("新建", "../menu/images/new.gif"));
        menu.items.add(new MenuItemInfo("打开", "../menu/images/open.gif"));
        menu.items.add(new MenuItemInfo("保存", "../menu/images/save.gif"));
        
        menus.add(menu);
        
        menu = new MenuInfo("编辑");
        menu.items.add(new MenuItemInfo("复制", "../menu/images/copy.gif"));
        menu.items.add(new MenuItemInfo("剪切", "../menu/images/cut.gif"));
        menu.items.add(new MenuItemInfo("粘贴", "../menu/images/paste.gif"));
        
        menus.add(menu);
    }

    private class MenuInfo {
        @ManagedProperty
        List<MenuItemInfo> items;
        @ManagedProperty
        String label;
        MenuInfo(String label) {
            this.label = label;
            items = new ArrayList<MenuItemInfo>();
        }
    }
    private class MenuItemInfo {
        @ManagedProperty
        String label;
        @ManagedProperty
        String img;
        @ManagedProperty
        List<MenuItemInfo> items;

        MenuItemInfo(String label, String img) {
            this.label = label;
            this.img = img;
            this.items = new ArrayList<MenuItemInfo>();
        }
    }
    
    public void addMenu1() {
        menu.removeAll();
        UIMenu fileMenu = createMenu("文件", null);
        UIMenu newMenu = createMenu("新建", "../menu/images/new.gif");
        
        UICommandMenuItem item;
        item = (UICommandMenuItem) createMenuItem("文本文件", null, UICommandMenuItem.COMPONENT_TYPE);
        newMenu.addMenuItem(item);
        item = (UICommandMenuItem) createMenuItem("XML文件", null, UICommandMenuItem.COMPONENT_TYPE);
        newMenu.addMenuItem(item);
        item = (UICommandMenuItem) createMenuItem("Java文件", null, UICommandMenuItem.COMPONENT_TYPE);
        newMenu.addMenuItem(item);

        fileMenu.addMenu(newMenu);


        item = (UICommandMenuItem) createMenuItem("打开", "../menu/images/open.gif", UICommandMenuItem.COMPONENT_TYPE);
        fileMenu.addMenuItem(item);
        item = (UICommandMenuItem) createMenuItem("保存", "../menu/images/save.gif", UICommandMenuItem.COMPONENT_TYPE);
        fileMenu.addMenuItem(item);
        item = (UICommandMenuItem) createMenuItem("另存为", null, UICommandMenuItem.COMPONENT_TYPE);
        item.getAttributes().put("disabled", Boolean.TRUE);
        fileMenu.addMenuItem(item);
        UISeparator separator = (UISeparator) createComponent(UISeparator.COMPONENT_TYPE);
        fileMenu.addMenuItem(separator);
        UILinkMenuItem link = (UILinkMenuItem) createMenuItem("关闭", null, UILinkMenuItem.COMPONENT_TYPE);
        link.setValue("dynamicMenu.jsp");
        link.setOnclick("return confirm('Are you sure?')");
        fileMenu.addMenuItem(link);

        menu.addMenu(fileMenu);
        
        UIMenu editMenu = createMenu("编辑", null);
        menu.addMenu(editMenu);
        item = (UICommandMenuItem) createMenuItem("复制", "../menu/images/copy.gif", UICommandMenuItem.COMPONENT_TYPE);
        editMenu.addMenuItem(item);
        item = (UICommandMenuItem) createMenuItem("剪切", "../menu/images/cut.gif", UICommandMenuItem.COMPONENT_TYPE);
        editMenu.addMenuItem(item);
        item = (UICommandMenuItem) createMenuItem("粘贴", "../menu/images/paste.gif", UICommandMenuItem.COMPONENT_TYPE);
        editMenu.addMenuItem(item);
    }
    
    public void addMenu2() {
        menu.removeAll();
        UIMenu fileMenu = createMenu("搜索", null);
        
        UICommandMenuItem item;
        item = (UICommandMenuItem) createMenuItem("上一个", "../menu/images/open.gif", UICommandMenuItem.COMPONENT_TYPE);
        fileMenu.addMenuItem(item);
        item = (UICommandMenuItem) createMenuItem("下一个", "../menu/images/save.gif", UICommandMenuItem.COMPONENT_TYPE);
        fileMenu.addMenuItem(item);
        item = (UICommandMenuItem) createMenuItem("逐字搜索", null, UICommandMenuItem.COMPONENT_TYPE);
        item.getAttributes().put("disabled", Boolean.TRUE);
        fileMenu.addMenuItem(item);

        menu.addMenu(fileMenu);
        
        UIMenu editMenu = createMenu("格式", null);
        menu.addMenu(editMenu);
        item = (UICommandMenuItem) createMenuItem("转为大写", "../menu/images/copy.gif", UICommandMenuItem.COMPONENT_TYPE);
        editMenu.addMenuItem(item);
        item = (UICommandMenuItem) createMenuItem("转为小写", "../menu/images/cut.gif", UICommandMenuItem.COMPONENT_TYPE);
        editMenu.addMenuItem(item);
        item = (UICommandMenuItem) createMenuItem("去除空格", "../menu/images/paste.gif", UICommandMenuItem.COMPONENT_TYPE);
        editMenu.addMenuItem(item);
    }

    
    private UIMenu createMenu(String label, String img) {
        UIMenu  newMenu = (UIMenu) createComponent(UIMenu.COMPONENT_TYPE);
        newMenu.setLabel(label);
        newMenu.setImage(img);
        return newMenu;
    }

    private UIComponent createMenuItem(String label, String img, String componentType) {
        UIComponent item = createComponent(componentType);
        item.getAttributes().put("label", label);
        item.getAttributes().put("image", img);
        return item;
    }
    
    private UIComponent createComponent(String componentType) {
        FacesContext context = FacesContext.getCurrentInstance();
        Application app = context.getApplication();
        UIComponent component = app.createComponent(componentType);
        component.setId(context.getViewRoot().createUniqueId());
        return component;
    }
}
