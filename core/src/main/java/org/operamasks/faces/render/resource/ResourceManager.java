/*
 * $Id: ResourceManager.java,v 1.24 2008/02/18 14:02:02 jacky Exp $
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

package org.operamasks.faces.render.resource;

import javax.faces.context.FacesContext;
import javax.faces.application.ViewHandler;
import javax.faces.component.UIComponent;
import javax.faces.render.Renderer;
import javax.faces.FacesException;

import java.util.HashMap;
import java.util.Map;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Collections;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.UndeclaredThrowableException;
import java.net.URL;

import org.operamasks.faces.annotation.component.Container;
import org.operamasks.faces.annotation.component.EncodeInitScript;
import org.operamasks.faces.annotation.component.EncodeResourceBegin;
import org.operamasks.faces.annotation.component.EncodeResourceChildren;
import org.operamasks.faces.annotation.component.EncodeResourceEnd;
import org.operamasks.faces.application.ApplicationAssociate;
import org.operamasks.faces.component.ComponentContainer;
import org.operamasks.faces.component.ComponentFactory;
import org.operamasks.faces.component.MetaComponentFactory;
import org.operamasks.faces.component.interceptor.RenderHandlerInvokor;
import org.operamasks.faces.render.ResourceRenderer;
import org.operamasks.faces.util.FacesUtils;

/**
 * 管理由 {@link ResourceProvider} 所登记的资源。同时还提供一些辅助方法帮助
 * 创建各种资源。
 */
public final class ResourceManager
{
    private static final String RESOURCE_MANAGER_PARAM
        = "org.operamasks.faces.RESOURCE_MANAGER";

    static final String VIEW_ID_PREFIX      = "/_global";
    static final String RESOURCE_VIEW_ID    = VIEW_ID_PREFIX + "/resource";
    static final String SKIN_VIEW_ID        = VIEW_ID_PREFIX + "/skin";
    static final String LOCAL_RESOURCE_BASE = "META-INF/resource";

    public static ResourceManager getInstance(FacesContext context) {
        Map<String,Object> requestMap = context.getExternalContext().getRequestMap();
        ResourceManager manager = (ResourceManager)requestMap.get(RESOURCE_MANAGER_PARAM);

        if (manager == null) {
            manager = new ResourceManager(context);
            requestMap.put(RESOURCE_MANAGER_PARAM, manager);
        }

        return manager;
    }

    /** Resources keyed by resource identifier. */
    private ApplicationAssociate associate;
    private Map<String,Resource> resources;
    private SkinManager skinManager;

    /**
     * After the page begin encoded, resource registration should
     * encode resource immediately.
     */
    private boolean beginEncoded;
    
    private Map<UIComponent,Boolean> ignoreChildren;

    private ResourceManager(FacesContext context) {
        skinManager = SkinManager.getInstance(context);
        resources = new LinkedHashMap<String, Resource>();
        ignoreChildren = new HashMap<UIComponent, Boolean>();
    }

    /**
     * 登记一个资源。当页面被渲染时资源在页面的开始或结束部分被渲染。具有相同id的多个
     * 资源只会被登记一次。
     *
     * @param resource 需要登记的资源
     */
    public void registerResource(Resource resource) {
        String id = resource.getId();
        if (!resources.containsKey(id)) {
            resources.put(id, resource);

            if (beginEncoded) {
                // Must encode resource immediately after page already rendered
                try {
                    resource.encodeBegin(FacesContext.getCurrentInstance());
                } catch (IOException ex) {
                    throw new FacesException(ex);
                }
            }
        }
    }

    /**
     * 检查一个指定的资源是否已经登记。
     *
     * @param id 资源id
     * @return 当资源已被登记时返回<code>true</code>，否则返回<code>false</code>
     */
    public boolean isResourceRegistered(String id) {
        return resources.containsKey(id);
    }

    /**
     * 根据资源id获得已登记的资源。
     *
     * @param id 资源id
     * @return 已登记的资源，如果资源没有被登记则返回为<code>null</code>
     */
    public Resource getRegisteredResource(String id) {
        return resources.get(id);
    }

    /**
     * 返回外部资源URL。资源管理器将根据资源的相对路径进行适当的转换，以便于使资源
     * 能够从外部访问。当以外部资源URL访问资源时，资源管理器将执行解码，并转换成一
     * 个内部资源。
     *
     * @param path 资源的相对路径
     * @return 外部资源URL
     */
    public String getResourceURL(String path) {
        if (path == null)
            throw new NullPointerException();

        String mapping = getResourceMapping(RESOURCE_VIEW_ID);
        if (!path.startsWith("/"))
            mapping = mapping.concat("/");
        return mapping.concat(path);
    }

    /**
     * 返回支持本地化的外部资源URL。
     *
     * @param path 资源的相对路径
     * @param locale 本地化参数
     * @return 外部资源URL
     */
    public String getResourceURL(String path, Locale locale) {
        String url = getResourceURL(path);
        if (locale != null)
            url += "?" + locale;
        return url;
    }

    /**
     * 返回与资源服务相关的外部资源URL。
     *
     * @param serviceName 资源服务名
     * @param path 资源相对路径
     * @return 外部资源URL
     */
    public String getServiceResourceURL(String serviceName, String path) {
        if (serviceName == null) {
            throw new NullPointerException();
        }

        String viewId = VIEW_ID_PREFIX;
        if (!serviceName.startsWith("/"))
            viewId += "/";
        viewId += serviceName;
        if (viewId.endsWith("/"))
            viewId = viewId.substring(0, viewId.length()-1);

        String mapping = getResourceMapping(viewId);
        if (path != null && path.length() != 0) {
            if (!path.startsWith("/"))
                mapping += "/";
            mapping += path;
        }
        return mapping;
    }
    
    /**
     * 返回与指定皮肤相关的外部资源URL。
     *
     * @param skin 皮肤名
     * @param path 资源的相对路径
     * @return 外部资源URL
     */
    public String getSkinResourceURL(String skin, String path) {
        if (skin == null || path == null)
            throw new NullPointerException();

        if (skin.indexOf('/') != -1)
            throw new IllegalArgumentException("Invalid skin name: " + skin);

        // The skin resource URL has the form
        // "/_global/skin.faces/%skin%/%path%"  - for suffix mapping
        // "/faces/_gobal/skin/%skin%/%path"    - for prefix mapping
        // "/_global/skin/%skin%/%path%"        - for ResourceServlet mapping

        String mapping = getResourceMapping(SKIN_VIEW_ID);

        StringBuilder buf = new StringBuilder();
        buf.append(mapping);
        buf.append("/");
        buf.append(skin);
        if (!path.startsWith("/"))
            buf.append("/");
        buf.append(path);
        return buf.toString();
    }

    /**
     * 返回与指定皮肤相关并支持本地化的外部资源URL。
     *
     * @param skin 皮肤名
     * @param path 资源的相对路径
     * @param locale 本地化参数
     * @return 外部资源URL
     */
    public String getSkinResourceURL(String skin, String path, Locale locale) {
        String url = getSkinResourceURL(skin, path);
        if (locale != null)
            url += "?" + locale;
        return url;
    }

    /**
     * 返回与当前场景中的皮肤相关的外部资源URL。
     *
     * @param path 资源相对路径
     * @return 外部资源URL
     */
    public String getSkinResourceURL(String path) {
        FacesContext context = FacesContext.getCurrentInstance();
        String skin = SkinManager.getCurrentSkin(context);
        return getSkinResourceURL(skin, path);
    }

    /**
     * 返回与当前场景中的皮肤相关并支持本地化的外部资源URL。
     *
     * @param path 资源相对路径
     * @param locale 本地化参数
     * @return 外部资源URL
     */
    public String getSkinResourceURL(String path, Locale locale) {
        String url = getSkinResourceURL(path);
        if (locale != null)
            url += "?" + locale;
        return url;
    }

    /**
     * 将外部资源URL转换成本地资源。
     *
     * @param url 外部资源URL
     * @return 本地资源，如果指定的url并不表示一个外部资源则返回<code>null</code>
     * @throws java.io.FileNotFoundException 如果指定的本地资源不存在
     */
    public URL getLocalResource(String url)
        throws FileNotFoundException
    {
        return getLocalResource(url, null, false);
    }

    /**
     * 将外部资源URL转换成本地资源。
     *
     * @param url 外部资源URL
     * @param locale 本地化参数
     * @return 本地资源，如果指定的url并不表示一个外部资源则返回<code>null</code>
     * @throws java.io.FileNotFoundException 如果指定的本地资源不存在
     */
    public URL getLocalResource(String url, String locale)
        throws FileNotFoundException
    {
        return getLocalResource(url, locale, false);
    }

    /**
     * 将外部资源URL转换成本地资源，并支持资源的压缩。
     *
     * @param url 外部资源URL
     * @param locale 本地化参数
     * @param gzip 当为<code>true</code>时返回经过gzip压缩的资源，否则返回未经压缩的资源
     * @return 本地资源，如果指定的url并不表示一个外部资源则返回<code>null</code>
     * @throws java.io.FileNotFoundException 如果指定的本地资源不存在
     */
    public URL getLocalResource(String uri, String locale, boolean gzip)
        throws FileNotFoundException
    {
        // The resource URI has the form "/_global/resource.faces/%path%"
        int pos = uri.indexOf(RESOURCE_VIEW_ID);
        if (pos != -1) {
            pos = uri.indexOf('/', pos + RESOURCE_VIEW_ID.length());
            if (pos == -1 || pos == uri.length() - 1)
                return null;

            ClassLoader cl = getClassLoader();
            String path = LOCAL_RESOURCE_BASE.concat(uri.substring(pos));
            URL resource = null;

            // Find localized resource
            String localPath;
            String prefix, suffix;
            if (locale != null && locale.length() != 0) {
                int dot = path.indexOf('.');
                if (dot == -1) {
                    prefix = path;
                    suffix = "";
                } else {
                    prefix = path.substring(0, dot);
                    suffix = path.substring(dot);
                }
                localPath = prefix + "-" + locale + suffix;
            } else {
                prefix = suffix = locale = null;
                localPath = path;
            }

            do {
                if (gzip) // find gzip resource
                    resource = cl.getResource(localPath + ".gz");
                if (resource == null)
                    resource = cl.getResource(localPath);

                if (resource == null && locale != null) {
                    int sep = locale.lastIndexOf("_");
                    if (sep != -1) {
                        locale = locale.substring(0, sep);
                        localPath = prefix + "-" + locale + suffix;
                    } else {
                        locale = null;
                        localPath = path;
                    }
                } else {
                    break;
                }
            } while (true);

            if (resource == null)
                throw new FileNotFoundException(path);
            return resource;
        }

        // The skin request URI has the form "/_global/skin.faces/%skin%/%path%
        // The skin resource URI has the form "/%location%/%path%
        pos = uri.indexOf(SKIN_VIEW_ID);
        if (pos != -1) {
            pos = uri.indexOf('/', pos + SKIN_VIEW_ID.length());
            if (pos == -1 || pos == uri.length() - 1)
                return null;
            uri = uri.substring(pos+1); // skip "/_global/skin.jsf/"

            // extract skin name from uri and use it to lookup skin location
            pos = uri.indexOf('/');
            if (pos == -1 || pos == uri.length() - 1)
                return null;

            String skin = uri.substring(0, pos);
            String path = uri.substring(pos+1);

            URL resource = skinManager.getSkinResource(skin, path, locale);
            if (resource == null)
                throw new FileNotFoundException(uri);
            return resource;
        }

        return null;
    }

    /**
     * 登记一个脚本资源。
     *
     * @param path 脚本资源的本地相对路径
     */
    public void registerScriptResource(String path) {
        String url = getResourceURL(path);
        registerResource(new ScriptResource(url));
    }

    /**
     * 登记一个CSS资源。
     *
     * @param path CSS资源的本地相对路径
     */
    public void registerCssResource(String path) {
        String url = getResourceURL(path);
        registerResource(new CssResource(url));
    }

    /**
     * 登记一个与皮肤相关的脚本资源。
     *
     * @param skin 皮肤名
     * @param path 资源本地相对路径
     */
    public void registerSkinScriptResource(String skin, String path) {
        String url = getSkinResourceURL(skin, path);
        registerResource(new ScriptResource(url));
    }

    /**
     * 登记一个与当前场景中的皮肤相关的脚本资源。

     * @param path 资源的本地相对路径
     */
    public void registerSkinScriptResource(String path) {
        String url = getSkinResourceURL(path);
        registerResource(new ScriptResource(url));
    }

    /**
     * 登记一个与皮肤相关的CSS资源。
     *
     * @param skin 皮肤名
     * @param path 资源本地相对路径
     */
    public void registerSkinCssResource(String skin, String path) {
        String url = getSkinResourceURL(skin, path);
        registerResource(new CssResource(url));
    }

    /**
     * 登记一个与当前场景中的皮肤相关的CSS资源。
     *
     * @param path 资源本地相对路径
     */
    public void registerSkinCssResource(String path) {
        String url = getSkinResourceURL(path);
        registerResource(new CssResource(url));
    }
    

    /**
     * 执行所有打了@Container标记的组件的resource
     * @param context
     * @param component
     */
    public void consumeContainerResources(FacesContext context, UIComponent component) {
        consumeResources(context, component, true);
    }
    
    /**
     * 收集组件树中的所有资源。
     *
     * @param context 当前Faces场景
     * @param component 从这里开始收集
     */
    public void consumeResources(FacesContext context, UIComponent component) {
        consumeResources(context, component, false);
    }
    
    public void consumeResources(FacesContext context, UIComponent component, boolean isContainer) {
        if (!isComponentRendered(component))
            return;
        boolean isResourceMeta = false;
        try {
            isResourceMeta = consumeResourcesByMeta(context, component, isContainer);
        } catch(UndeclaredThrowableException ue) {
            throw new FacesException(ue.getUndeclaredThrowable());
        } catch (Exception e) {
            throw new FacesException(e);
        }
        
        if (isContainer(component) == isContainer) {
            boolean isResourceRenderer = false;
            
            if (component instanceof ResourceProvider) {
                ((ResourceProvider)component).provideResource(this, component);
            } else if (component instanceof ResourceRenderer) {
                isResourceRenderer = true;
                consumeResourcesRenderer(context, component, isContainer);
            } else {
                Renderer renderer = FacesUtils.getRenderer(context, component);
                if (renderer instanceof ResourceProvider) {
                    ((ResourceProvider)renderer).provideResource(this, component);
                } else if (renderer instanceof ResourceRenderer) {
                    isResourceRenderer = true;
                    consumeResourcesRenderer(context, component, isContainer);
                }
            }

            if (isResourceRenderer )
                return;
        }
        
        if (isResourceMeta)
            return;

        Iterator<UIComponent> kids = component.getFacetsAndChildren();
        while (kids.hasNext()) {
            UIComponent child = kids.next();
            Boolean ignore = ignoreChildren.get(child);
            if (ignore == null || !ignore) {
                consumeResources(context, child, isContainer);
            }
        }
    }

    public void consumeInitScriptByMeta(FacesContext context, UIComponent component) {
        ComponentContainer container = ComponentContainer.getInstance();
        ComponentFactory fac = container.getComponentFactoryByClass(component.getClass());
        if (fac != null && (fac instanceof MetaComponentFactory)) {
            RenderHandlerInvokor invoker = ((MetaComponentFactory)fac).getRenderInvoker();
            try {
                invoker.invoke(EncodeInitScript.class, context, component, this);
            } catch(UndeclaredThrowableException ue) {
                throw new FacesException(ue.getUndeclaredThrowable());
            } catch (Exception e) {
                throw new FacesException(e);
            }
        }
        Iterator<UIComponent> kids = component.getFacetsAndChildren();
        while (kids.hasNext()) {
            UIComponent child = kids.next();
            Boolean ignore = ignoreChildren.get(child);
            if (ignore == null || !ignore) {
                consumeInitScriptByMeta(context, child);
            }
        }
    }
    
    public boolean consumeResourcesByMeta(FacesContext context, UIComponent component, boolean isContainer) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        boolean shouldExec = isContainer(component) == isContainer;
        ComponentContainer container = ComponentContainer.getInstance();
        ComponentFactory fac = container.getComponentFactoryByClass(component.getClass());
        if (fac != null && (fac instanceof MetaComponentFactory)) {
            RenderHandlerInvokor invoker = ((MetaComponentFactory)fac).getRenderInvoker();
            if (shouldExec) {
                invoker.invoke(EncodeResourceBegin.class, context, component, this);
            }
            if (invoker.encodeResourceChildren()) {
                if (shouldExec) {
                    invoker.invoke(EncodeResourceChildren.class, context, component, this);
                }
            } else {
                Iterator<UIComponent> kids = component.getFacetsAndChildren();
                while (kids.hasNext()) {
                    UIComponent child = kids.next();
                    Boolean ignore = ignoreChildren.get(child);
                    if (ignore == null || !ignore) {
                        consumeResources(context, child, isContainer);
                    }
                }
            }
            if (shouldExec) {
                invoker.invoke(EncodeResourceEnd.class, context, component, this);
            }
            return true;
        }
        return false;
    }

    /**
     * 收集组件树中的所有资源。
     *
     * @param context 当前Faces场景
     * @param component 从这里开始收集
     * @param isContainer 
     */
    public void consumeResourcesRenderer(FacesContext context, UIComponent component, boolean isContainer) {
        if (!isComponentRendered(component))
            return;

        ResourceRenderer r = null;
        if (component instanceof ResourceRenderer) {
            r = (ResourceRenderer)component;
        } else {
            Renderer renderer = FacesUtils.getRenderer(context, component);
            if (renderer instanceof ResourceRenderer) {
                r = (ResourceRenderer)renderer;
            }
        }
        boolean shouldExec = isContainer(component) == isContainer;
        if (r != null && shouldExec) {
            r.encodeResourceBegin(this, component);
        }

        if (r!= null && r.getEncodeResourceChildren()) {
            if (shouldExec) {
                r.encodeResourceChildren(this, component);
            }
        } else {
            Iterator<UIComponent> kids = component.getFacetsAndChildren();
            while (kids.hasNext()) {
                UIComponent child = kids.next();
                Boolean ignore = ignoreChildren.get(child);
                if (ignore == null || !ignore) {
                    consumeResources(context, child, isContainer);
                }
            }
        }
        
        if (r!= null && shouldExec) {
            r.encodeResourceEnd(this, component);
        }
    }

    private boolean isComponentRendered(UIComponent component) {
        boolean rendered = false;
        try {
            rendered = component.isRendered();
        } catch (Exception e) {
            rendered = false;
        }
        
        return rendered;
    }

    private boolean isContainer(UIComponent component) {
        return component.getClass().getAnnotation(Container.class) != null;
    }

    public void setIgnoreChildren(UIComponent component,boolean ignore){
        ignoreChildren.put(component, ignore);
    }

    public void removeIgnoreChildren(UIComponent component){
        ignoreChildren.remove(component);
    }
    
    /**
     * 在页面的开始部分渲染所有资源。
     *
     * @param context 当前的Faces场景
     * @throw java.io.IOException 如果发生I/O错误
     */
    public void encodeBegin(FacesContext context)
        throws IOException
    {
        beginEncoded = true;

        for (Resource res : getResources()) {
            res.encodeBegin(context);
        }
    }

   /**
    * 在页面的结束部分渲染所有资源。
    *
    * @param context 当前的Faces场景
    * @throw java.io.IOException 如果发生I/O错误
    */
    public void encodeEnd(FacesContext context)
        throws IOException
    {
        for (Resource res : getResources()) {
            res.encodeEnd(context);
        }
    }

    /**
     * 获得页面装载脚本，该脚本将作为<code>body</code>元素的<code>onload</code>
     * 属性渲染。
     *
     * @param context 当前的Faces场景
     */
    public String getLoadScript(FacesContext context) {
        StringBuilder buf = new StringBuilder();
        for (Resource res : getResources()) {
            String script = res.getLoadScript(context);
            if (script != null && script.length() != 0) {
                buf.append(script);
                if (!script.endsWith(";"))
                    buf.append(";");
            }
        }
        return buf.length() == 0 ? null : buf.toString();
    }

    /**
     * 获得页面卸载脚本，该脚本将作为<code>body</code>元素的<code>onunload</code>
     * 属性渲染。
     *
     * @param context 当前的Faces场景
     */
    public String getUnloadScript(FacesContext context) {
        StringBuilder buf = new StringBuilder();
        for (Resource res : getResources()) {
            String script = res.getUnloadScript(context);
            if (script != null && script.length() != 0) {
                buf.append(script);
                if (!script.endsWith(";"))
                    buf.append(";");
            }
        }
        return buf.length() == 0 ? null : buf.toString();
    }

    /**
     * 重置资源管理器。
     */
    public void reset() {
        beginEncoded = false;
        resources.clear();
    }
    
    private String getResourceMapping(String viewId) {
        FacesContext context = FacesContext.getCurrentInstance();
        ViewHandler vh = context.getApplication().getViewHandler();

        // If a ResourceServlet is present, then use it to map resources.
        ApplicationAssociate associate = getAssociate(context);
        if (associate != null) {
            String mapping = associate.getResourceMapping();
            if (mapping != null) {
                assert mapping.startsWith("/") && !mapping.endsWith("/");
                if (mapping.equals(VIEW_ID_PREFIX)) {
                    mapping = viewId;
                } else if (mapping.endsWith(VIEW_ID_PREFIX)) {
                    mapping += viewId.substring(VIEW_ID_PREFIX.length());
                } else {
                    mapping += viewId;
                }
                return vh.getResourceURL(context, mapping);
            }
        }

        // Otherwise, use ResourcePhaseListener to map resources. This may not
        // work on some application servers such as Tomcat.
        return vh.getActionURL(context, viewId);
    }

    /**
     * 得到所有按优先级排序的资源。
     */
    private List<Resource> getResources() {
        List<Resource> result = new ArrayList<Resource>(resources.values());

        // sort resources with priority
        Collections.sort(result,
            new Comparator<Resource>() {
                public int compare(Resource r1, Resource r2) {
                    return r1.getPriority() - r2.getPriority();
                }
            });

        return result;
    }

    private ApplicationAssociate getAssociate(FacesContext context) {
        if (associate == null)
            associate = ApplicationAssociate.getInstance(context);
        return associate;
    }

    private ClassLoader getClassLoader() {
        FacesContext context = FacesContext.getCurrentInstance();
        ApplicationAssociate associate = getAssociate(context);
        if (associate != null) {
            return associate.getClassLoader();
        }

        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        if (loader == null)
            loader = ClassLoader.getSystemClassLoader();
        return loader;
    }
}
