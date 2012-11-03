/*
 * $Id: ComponentOperationManager.java,v 1.13 2008/04/22 06:00:08 patrick Exp $
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
package org.operamasks.faces.render.widget.yuiext;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;

import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.Renderer;

import org.operamasks.faces.component.SensitivePropertyChecker;
import org.operamasks.faces.render.ajax.AjaxResponseWriter;
import org.operamasks.faces.render.resource.ResourceManager;
import org.operamasks.faces.util.FacesUtils;
import org.operamasks.cglib.proxy.Enhancer;

@SuppressWarnings("unchecked")
public class ComponentOperationManager
{
    private static final String COMPONENT_MANAGER_PARAM = "org.operamasks.faces.COMPONENT_OPERATION_MANAGER";
    private static final String HANDLER_METHOD_NAME = "handleOperation";
    
    private StringBuffer initScript = new StringBuffer();
    private StringBuffer endScript = new StringBuffer();
    private Map<String, Object> attributes = new HashMap<String, Object>();
    
    private static final Class[] OPERATION_METHOD_SIGNATURE = new Class[]{FacesContext.class, UIComponent.class};  
    private static final Class[] HANDLE_METHOD_SIGNATURE = new Class[]{String.class, FacesContext.class, UIComponent.class};
    private static final String RESOURCE_EXT = ".properties";
    private static final String RESOURE_PREFIX = "/_global/resource/cscript/";
    
    private List<Command> commandQueue;

    /**
     * 根据facesContext得到ComponentOperationManager的实例，意味着此实例是线程隔离的
     * @param context 当前的FacesContext
     * @return
     */
    public static ComponentOperationManager getInstance(FacesContext context) {
        Map<String,Object> requestMap = context.getExternalContext().getRequestMap();
        ComponentOperationManager cm = (ComponentOperationManager)requestMap.get(COMPONENT_MANAGER_PARAM);

        if (cm == null) {
            cm = new ComponentOperationManager();
            requestMap.put(COMPONENT_MANAGER_PARAM, cm);
        }
        return cm;
    }
    
    private ComponentOperationManager() {
        commandQueue = new LinkedList<Command>();
    }
    
    /**
     * 执行一个组件的行为，此方法执行如下操作：<br/>
     * 1. 去对应的位置（默认为META-INF/resource/cscript/下面的组件对应的目录）取出与组件名称对应的属性文件；<br/>
     * 2. 根据operationName取出所需执行的js脚本；<br/>
     * 3. 根据attributes中的key-value，对js脚本进行合并；<br/>
     * 4. 输出合并后的脚本；<br/>
     * 5. 根据组件找到renderer，并查找renderer是否拥有${operationName}(FacesContext, UIComponent)签名的方法，如果有则执行，结束。<br/>
     * 6. 如果没有找到第5步的方法，则查找renderer是否拥有handleOperation(String, FacesContext, UIComponent)签名的方法，如果有则执行，结束。<br/>
     * @param context 当前的FacesContext
     * @param operationName 组件的API名称，会根据此名称去对应的属性文件提取相应的脚本（如果存在的话）
     * @param component 组件实例
     */
    public void invoke(FacesContext context, String operationName, UIComponent component) {
        commandQueue.add(new Command(context, operationName, component, this.attributes));
        this.attributes.clear();
    }
    
    public void addOperationScript(String s) {
        endScript.append(s);
    }
    
    public void encodeOperationScript(String operationName, FacesContext context, 
            UIComponent component, Map<String,Object> valueContext) {
        String s = getMergedScript(operationName, context, component, valueContext);
        if (SensitivePropertyChecker.isSensitvieProperties(component, operationName) && s == null) {
            s = ""; 
        } else if ((s == null || s.length() == 0)) {
            s = String.format("%s.%s();\n", FacesUtils.getJsvar(context, component), operationName);
        }
        endScript.append(s);
    }

    /**
     * 根据指定的操作名称，去组件对应的属性文件中提取合并后的脚本，如果无需合并，则valueContext传入null即可
     * @param operationName 组件API名称
     * @param context 当前的FacesContext
     * @param component 组件实例
     * @param valueContext 包含了合并值的列表
     * @return
     */
    public String getMergedScript(String operationName, FacesContext context, 
            UIComponent component, Map valueContext) {
        String script = getOperationScript(operationName, context, component);
        script = merge(script, valueContext);
        script = merge(script, this.attributes);
        return script;
    }

    private void renderAndInvoke(FacesContext context, String operationName, UIComponent component, Map<String,Object> valueContext) {
        if (!component.isRendered())
            return;
        encodeOperationScript(operationName, context, component, valueContext);
        Renderer renderer = FacesUtils.getRenderer(context, component);
        if (renderer != null) {
            invokeRenderer(context, renderer, operationName, component);
        }
    }
    
    private String merge(String opScript, Map<String,Object> valueContext) {
        if (opScript == null) {
            return null;
        }
        String result = opScript;
        if (null != valueContext) {
            for (String key: valueContext.keySet()) {
                String variable = "\\Q${" + key + "}";
                //variable = Pattern.quote(variable);
                String value = valueContext.get(key).toString();
                result = result.replaceAll(variable, Matcher.quoteReplacement(value));
            }
        }
        return result;
    }

    private String getOperationScript(String operationName, FacesContext context, 
            UIComponent component) {
        URL url = getResourceURL(context, component.getClass());
        if( url == null) {
            return null;
        }
        Properties props = new Properties();
        InputStream in = null;
        try {
            in = url.openStream();
            props.load(in);
        } catch (IOException e) {
        }
        finally {
            if (null != in) {
                try {
                    in.close();
                } catch (IOException e) {
                    // ignore
                }
            }
        }
        String script = null;
        String operationValue = null;
        /**
         * 若存在{component}.{operationName}属性：
         *   若{component}.{operationName}属性非空，则返回模板中键值为{operation}.{value}的script；
         *   若无此键值，则返回键值为{operationName}的script并将其中的${value}替换为属性值。
         *   若{component}.{operationName}属性为空，则返回null
         * 若不存在{component}.{operationName}属性：
         *   返回键值为{operationName}的script
         *   若不存在键值{operationName}，返回null
         */
        try {
            operationValue = FacesUtils.getFieldValueString(component, operationName);
            if (operationValue != null) {
                operationValue = operationValue.toLowerCase();
                String operationWithValue = operationName + "." + 
                FacesUtils.getFieldValueString(component, operationName).toLowerCase();
                script = props.getProperty(operationWithValue);
                if (script == null) {
                    script = props.getProperty(operationName);
                }
                if (script != null) {
                    script = script.replaceAll("\\Q${value}", operationValue);
                }
            }
        } catch (NoSuchFieldException ex) {
            //没有该敏感属性，说明是API方法调用
            script = props.getProperty(operationName);
        }
        return script;
    }

    private URL getResourceURL(FacesContext context, Class<? extends UIComponent> componentClass) {
        for(Class<?> clz = componentClass; clz.getSuperclass() != null; clz = clz.getSuperclass()) {
            String res = clz.getName().replace('.', '/').concat(RESOURCE_EXT);
            URL url = null;
            try {
                url = ResourceManager.getInstance(context).getLocalResource(RESOURE_PREFIX + res);
            } catch (FileNotFoundException e) {
                // do nothing
            }
            if (url != null) {
                return url;
            }
        }
        return null;
    }

    private void invokeRenderer(FacesContext context, Renderer renderer, String operationName,
            UIComponent component) {
        Class rendererClass = renderer.getClass();
        Method opMethod = getOperationMethod(rendererClass, operationName);
        if (null != opMethod) {
            try {
                opMethod.invoke(renderer, new Object[]{context, component});
            } catch (IllegalArgumentException e) {
                // ignore;
            } catch (IllegalAccessException e) {
                // ignore;
            } catch (InvocationTargetException e) {
                throw new FacesException(e.getTargetException());
            }
        }
        else {
            Method handlerMethod = getHandlerMethod(rendererClass, operationName);
            if (null != handlerMethod) {
                try {
                    handlerMethod.invoke(renderer, new Object[]{operationName, context, component});
                } catch (IllegalArgumentException e) {
                    // ignore;
                } catch (IllegalAccessException e) {
                    // ignore;
                } catch (InvocationTargetException e) {
                    throw new FacesException(e.getTargetException());
                }
            }
         }
    }

    private Method getHandlerMethod(Class rendererClass, String operationName) {
        Method method = null;
        try {
            method = rendererClass.getMethod(HANDLER_METHOD_NAME, HANDLE_METHOD_SIGNATURE);
        } catch (SecurityException e) {
            // do nothing
        } catch (NoSuchMethodException e) {
            // do nothing
        }
        return method;
    }

    private Method getOperationMethod(Class rendererClass, String operationName) {
        Method method = null;
        try {
            method = rendererClass.getMethod(operationName, OPERATION_METHOD_SIGNATURE);
        } catch (SecurityException e) {
            // do nothing
        } catch (NoSuchMethodException e) {
            // do nothing
        }
        return method;
    }

    public void encodeBegin(FacesContext context)
    throws IOException {
        writeScript(context, initScript);
    }

    private void executeCommand() {
        for (Command cmd : commandQueue) {
            String jsvar = FacesUtils.getJsvar(cmd.context, cmd.component);
            if (jsvar != null && jsvar.length() > 0) {
            	cmd.valueContext.put("jsvar", jsvar);
            }
            String clientId = cmd.component != null ? cmd.component.getClientId(cmd.context) : null;
            if (clientId != null && clientId.length() > 0) {
            	cmd.valueContext.put("clientId", clientId);
            }
            renderAndInvoke(cmd.context, cmd.operationName, cmd.component, cmd.valueContext);
        }
    }

    public void encodeEnd(FacesContext context)
    throws IOException {
        executeCommand();
        writeScript(context, endScript);
    }

    private void writeScript(FacesContext context, StringBuffer script) throws IOException {
        if (script.length() != 0) {
            ResponseWriter out = context.getResponseWriter();
            if (out instanceof AjaxResponseWriter) {
                String s = encodeAjaxInitScript(script);
                ((AjaxResponseWriter)out).writeActionScript(s);
            } else {
                String s = encodeInitScript(script);
                out.write(s);
            }
        }
    }
    
    private String encodeInitScript(StringBuffer script) {
        StringBuilder buf = new StringBuilder();
        buf.append("<script type=\"text/javascript\">\n<!--\n");
        buf.append("Ext.onReady(function(){\n");
        buf.append(script);
        buf.append("\n});\n");
        buf.append("//-->\n</script>\n");
        return buf.toString();
    }

    private String encodeAjaxInitScript(StringBuffer script) {
        StringBuilder buf = new StringBuilder();
        buf.append("(function(){");
        buf.append(script);
        buf.append("})();");
        return buf.toString();
    }
    
    public void addInitScript(String script) {
        initScript.append(script);
    }

    public void addEndScript(String script) {
        endScript.append(script);
    }
    
    public Map<String, Object> getAttributes() {
        return attributes;
    }
    
    public void reset() {
        initScript.setLength(0);
        endScript.setLength(0);
        attributes.clear();
    }
    
    private static class Command {
        FacesContext context;
        String operationName;
        UIComponent component;
        Map<String, Object> valueContext = new HashMap<String, Object>();
        Command(FacesContext context, String operationName, UIComponent component, Map<String, Object> attrs) {
            this.context = context;
            this.operationName = operationName;
            this.component = component;
            valueContext.putAll(attrs);
        }
    }
}
