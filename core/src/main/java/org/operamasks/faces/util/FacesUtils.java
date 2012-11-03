/*
 * $Id: FacesUtils.java,v 1.64 2008/04/22 06:00:08 patrick Exp $
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

package org.operamasks.faces.util;

import static org.operamasks.resources.Resources.JSF_CREATE_COMPONENT_ERROR;
import static org.operamasks.resources.Resources.JSF_LOCALE_TYPE_EXPECTED;
import static org.operamasks.resources.Resources._T;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.NoSuchElementException;
import java.util.ResourceBundle;
import java.util.TimeZone;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import javax.el.ELException;
import javax.el.ValueExpression;
import javax.faces.FacesException;
import javax.faces.FactoryFinder;
import javax.faces.application.Application;
import javax.faces.application.ApplicationFactory;
import javax.faces.application.StateManager;
import javax.faces.component.NamingContainer;
import javax.faces.component.UIComponent;
import javax.faces.component.UIForm;
import javax.faces.component.UIMessage;
import javax.faces.component.UIViewRoot;
import javax.faces.component.ValueHolder;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.event.PhaseId;
import javax.faces.render.RenderKit;
import javax.faces.render.RenderKitFactory;
import javax.faces.render.Renderer;
import javax.faces.render.ResponseStateManager;

import org.operamasks.el.eval.Coercion;
import org.operamasks.faces.annotation.LocalString;
import org.operamasks.faces.application.ViewBuilder;
import org.operamasks.faces.component.ajax.AjaxUpdater;
import org.operamasks.faces.component.html.HtmlPage;
import org.operamasks.faces.component.widget.UITree;
import org.operamasks.faces.component.widget.UITreeNode;
import org.operamasks.faces.render.ajax.AjaxHtmlResponseWriter;
import org.operamasks.faces.render.ajax.AjaxResponseWriter;
import org.operamasks.faces.render.html.HtmlResponseStateManager;
import org.operamasks.faces.render.html.HtmlResponseWriter;
import org.operamasks.faces.render.widget.PhaseMonitorListener;
import org.operamasks.faces.render.widget.yuiext.TreeRenderUtils;
import org.operamasks.util.Base64;
import org.operamasks.util.SimplePool;

public class FacesUtils
{
    public static final String VIEW_ID_PARAM = "javax.faces.ViewId";
    public static final String ORIGINAL_VIEW_ID = "org.operamasks.faces.ORIGINAL_VIEW_ID";
    public static final String PARAM_DEBUG_MODE = "org.operamasks.faces.DEBUG_MODE";
    public static final String CURRENT_PHASE_ID = "org.operamasks.faces.CURRENT_PHASE_ID";

    /**
     * Returns the locale represented by the expression.
     * @param localeExpr a String in the format specified by JSTL Specification
     *                   as follows:
     *                   "A String value is interpreted as the printable
     *                    representation of a locale, which must contain a
     *                    two-letter (lower-case) language code (as defined by
     *                    ISO-639), and may contain a two-letter (upper-case)
     *                    country code (as defined by ISO-3166). Language and
     *                    country c`odes must be separated by hyphen ('-') or
     *                    underscore ('_')."
     * @return Locale instance cosntructed from the expression.
     */
    // XXX the code is copied from javax.faces.UIViewRoot
    public static Locale getLocaleFromString(String localeExpr) {
        Locale result = Locale.getDefault();
        if (localeExpr.indexOf("_") == -1 && localeExpr.indexOf("-") == -1)  {
            // expression has just language code in it. make sure the
            // expression contains exactly 2 characters.
            if (localeExpr.length() == 2) {
                result = new Locale(localeExpr, "");
            }
        } else {
            // expression has country code in it. make sure the expression
            // contains exactly 5 characters.
            if (localeExpr.length() == 5) {
                // get the language and country to construct the locale.
                String language = localeExpr.substring(0,2);
                String country = localeExpr.substring(3,5);
                result = new Locale(language,country);
            }
        }
        return result;
    }

    public static Locale getLocaleFromExpression(FacesContext context, ValueExpression expr)
        throws FacesException
    {
        if (expr.isLiteralText()) {
            return getLocaleFromString(expr.getExpressionString());
        }

        try {
            Object value = expr.getValue(context.getELContext());
            if (value == null || value.equals("")) {
                return null;
            } else if (value instanceof Locale) {
                return (Locale)value;
            } else if (value instanceof String) {
                return getLocaleFromString((String)value);
            } else {
                throw new FacesException(_T(JSF_LOCALE_TYPE_EXPECTED, expr.getExpressionString()));
            }
        } catch (ELException ex) {
            throw new FacesException(ex);
        }
    }

    public static Locale getCurrentLocale() {
        FacesContext context = FacesContext.getCurrentInstance();
        Locale locale = null;

        if (context.getViewRoot() != null) {
            locale = context.getViewRoot().getLocale();
        }

        if (locale == null) {
            locale = context.getApplication().getViewHandler().calculateLocale(context);
        }

        if (locale == null) {
            locale = Locale.getDefault();
        }

        return locale;
    }

    public static boolean isValueExpression(String expression) {
        if (expression == null) {
            return false;
        }

        int start = expression.indexOf("#{");
        if (start == -1) {
            return false;
        }

        int escapeChars = 0;
        while (--start >= 0 && expression.charAt(start) == '\\') {
            escapeChars++;
        }
        return (escapeChars % 2) == 0;
    }

    public static boolean isMixedValueExpression(String expression) {
        if (expression == null)
            return false;
        if (expression.startsWith("#{") && expression.endsWith("}")) {
            return false;
        } else {
            return isValueExpression(expression);
        }
    }
    
    public static UIComponent cloneComponent(UIComponent source) {
        if (source == null) 
            return null;
        
        UIComponent newOne = null;
        try {
            FacesContext context = FacesContext.getCurrentInstance();
            newOne = source.getClass().newInstance();
            newOne.restoreState(context, source.saveState(context));
        } catch (Exception ex) {
            throw new FacesException(_T(JSF_CREATE_COMPONENT_ERROR, source.getClass()), ex);
        }
        return newOne;
    }

    public static ValueExpression createValueExpression(Object obj, Class expectedType) {
        FacesContext context = FacesContext.getCurrentInstance();
        return context.getApplication().getExpressionFactory()
                      .createValueExpression(obj, expectedType);
    }

    public static ValueExpression createValueExpression(String expression, Class expectedType) {
        FacesContext context = FacesContext.getCurrentInstance();
        return context.getApplication().getExpressionFactory()
                      .createValueExpression(context.getELContext(), expression, expectedType);
    }

    @SuppressWarnings("unchecked")
    public static <T> T evaluateExpressionGet(String expression, Class<T> expectedType) {
        if (!isValueExpression(expression)) {
            return (T)Coercion.coerce(expression, expectedType);
        } else {
            FacesContext context = FacesContext.getCurrentInstance();
            return (T)context.getApplication().evaluateExpressionGet(context, expression, expectedType);
        }
    }

    private static Object setScopeVariable(Object scope) {
        FacesContext context = FacesContext.getCurrentInstance();

        ValueExpression var;
        if (scope == null) {
            var = null;
        } else if (scope instanceof ValueExpression) {
            var = (ValueExpression)scope;
        } else {
            var = context.getApplication().getExpressionFactory()
                    .createValueExpression(scope, Object.class);
        }

        return context.getELContext().getVariableMapper().setVariable("this", var);
    }

    public static ValueExpression createValueExpression(Object scope, String expression, Class<?> expectedType) {
        FacesContext context = FacesContext.getCurrentInstance();
        ValueExpression result;

        if (scope == null) {
            result = context.getApplication().getExpressionFactory()
                .createValueExpression(context.getELContext(), expression, expectedType);
        } else {
            Object var = setScopeVariable(scope);
            result = context.getApplication().getExpressionFactory()
                .createValueExpression(context.getELContext(), expression, expectedType);
            setScopeVariable(var);
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    public static <T> T evaluateExpressionGet(Object scope, String expression, Class<T> expectedType) {
        if (!isValueExpression(expression)) {
            return (T)Coercion.coerce(expression, expectedType);
        }

        FacesContext context = FacesContext.getCurrentInstance();

        T result;
        if (scope == null) {
            result = (T)context.getApplication().evaluateExpressionGet(context, expression, expectedType);
        } else {
            Object var = setScopeVariable(scope);
            result = (T)context.getApplication().evaluateExpressionGet(context, expression, expectedType);
            setScopeVariable(var);
        }
        return result;
    }

    public static boolean isDynamicValue(UIComponent component, String name) {
        ValueExpression value = component.getValueExpression(name);
        if (value != null) {
            return !value.isLiteralText();
        } else {
            return component.getValueExpression("binding") != null;
        }
    }

    // Internalization/Localization

    public static ResourceBundle getLocalStringBundle(Class<?> declaringClass) {
        return getLocalStringBundle(declaringClass, null, getCurrentLocale());
    }

    public static ResourceBundle getLocalStringBundle(Class<?> declaringClass,
                                                      String   basename,
                                                      Locale   locale)
    {
        if (basename == null || basename.length() == 0) {
            LocalString meta = declaringClass.getAnnotation(LocalString.class);
            if (meta != null && meta.basename().length() != 0) {
                basename = meta.basename();
            } else {
                String classname = declaringClass.getName();
                basename = classname.substring(0, classname.lastIndexOf('.')+1) + "LocalStrings";
            }
        }

        try {
            ClassLoader loader = declaringClass.getClassLoader();
            if (loader != null) {
                return ResourceBundle.getBundle(basename, locale, loader);
            } else {
                return ResourceBundle.getBundle(basename, locale);
            }
        } catch (MissingResourceException ex) {
            return null;
        }
    }

    public static String getLocalString(Class<?> declaringClass, String key) {
        return getLocalString(declaringClass, null, key, getCurrentLocale());
    }

    public static String getLocalString(Class<?> declaringClass, String basename, String key) {
        return getLocalString(declaringClass, basename, key, getCurrentLocale());
    }

    public static String getLocalString(Class<?> declaringClass,
                                        String   basename,
                                        String   key,
                                        Locale   locale)
    {
        ResourceBundle bundle = getLocalStringBundle(declaringClass, basename, locale);
        if (bundle == null) {
            return null;
        }

        String classname = declaringClass.getName();
        String msgtext;

        try {
            // search string with the full class name prefix
            msgtext = bundle.getString(classname + "." + key);
        } catch (MissingResourceException ex) {
            try {
                // search string with the base class name prefix
                msgtext = bundle.getString(classname.substring(classname.lastIndexOf('.')+1) + "." + key);
            } catch (MissingResourceException ex2) {
                try {
                    // search string without any prefix
                    msgtext = bundle.getString(key);
                } catch (MissingResourceException ex3) {
                    return null;
                }
            }
        }

        // resolve relative reference
        if (msgtext != null && msgtext.startsWith("%") && msgtext.endsWith("%")) {
            try {
                String ref = msgtext.substring(1, msgtext.length()-1);
                msgtext = bundle.getString(ref);
            } catch (MissingResourceException ex) {}
        }
        
        return msgtext;
    }

    // The component iterator is used to step through a subtree in the view.

    public static Iterator<UIComponent> createChildrenIterator(UIComponent root, boolean includeRoot) {
        return new ComponentIterator(root, includeRoot) {
            protected Iterator<UIComponent> getChildren(UIComponent node) {
                return node.getChildren().iterator();
            }
        };
    }

    public static Iterator<UIComponent> createFacetsAndChildrenIterator(UIComponent root, boolean includeRoot) {
        return new ComponentIterator(root, includeRoot) {
            protected Iterator<UIComponent> getChildren(UIComponent node) {
                return node.getFacetsAndChildren();
            }
        };
    }

    private static abstract class ComponentIterator implements Iterator<UIComponent> {
        private UIComponent root;
        private UIComponent current;
        private UIComponent next;

        private Map<UIComponent,Iterator<UIComponent>> iteratorMap
            = new IdentityHashMap<UIComponent, Iterator<UIComponent>>();

        public ComponentIterator(UIComponent root, boolean includeRoot) {
            this.root = root;
            if (includeRoot) {
                current = next = root;
            } else {
                current = next = getNextNode(root);
            }
        }

        public boolean hasNext() {
            if (next == null && current != null) {
                current = next = getNextNode(current);
            }
            return next != null;
        }

        public UIComponent next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }

            UIComponent result = next;
            next = null;
            return result;
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }

        private UIComponent getNextNode(UIComponent node) {
            Iterator<UIComponent> children = getChildren(node);
            if (children.hasNext()) {
                iteratorMap.put(node, children);
                return children.next();
            }

            if (node == root) {
                return null;
            }

            UIComponent next;
            while ((next = getNextSibling(node)) == null) {
                node = node.getParent();
                if (node == null || node == root)
                    return null;
            }
            return next;
        }

        private UIComponent getNextSibling(UIComponent node) {
            UIComponent parent = node.getParent();
            if (parent == null) {
                return null;
            }

            Iterator<UIComponent> children = iteratorMap.get(parent);
            if (children != null && children.hasNext()) {
                return children.next();
            } else {
                iteratorMap.remove(parent);
                return null;
            }
        }

        protected abstract Iterator<UIComponent> getChildren(UIComponent node);
    }

    /**
     * Traversal the component tree to find any component that have the given
     * family and render type.
     */
    public static UIComponent findComponent(UIComponent from, String family, String renderType) {
        Iterator<UIComponent> kids = createFacetsAndChildrenIterator(from, false);
        while (kids.hasNext()) {
            UIComponent child = kids.next();
            if ((family == null || family.equals(child.getFamily())) &&
                renderType.equals(child.getRendererType())) {
                return child;
            }
        }
        return null;
    }

    /**
     * Traversal the component tree to find any component that is the instance
     * of the given type.
     */
    public static <T> T findComponent(UIComponent from, Class<T> type) {
        if (from == null) {
            return null;
        }

        Iterator<UIComponent> kids = createChildrenIterator(from, true);
        while (kids.hasNext()) {
            UIComponent kid = kids.next();
            if (type.isInstance(kid) && kid.isRendered()) {
                return type.cast(kid);
            }
        }
        return null;
    }

    public static HtmlPage getHtmlPage(UIComponent component) {
        return findComponent(component, HtmlPage.class);
    }

    public static UIComponent getForComponent(String forId, UIComponent component) {
        return getForComponent(null, forId, component);
    }
    
    public static UIComponent getForComponent(FacesContext context,
                                              String forId,
                                              UIComponent component)
    {
        UIComponent result = null;

        if (forId == null || forId.length() == 0) {
            return null;
        }

        // If the search expression is started with separater char, then search from root.
        if (forId.charAt(0) == NamingContainer.SEPARATOR_CHAR) {
            return component.findComponent(forId);
        }

        // Find component with the specified id from the naming container
        // of the current component.
        UIComponent parent = component;
        UIComponent root = component;
        while (parent != null) {
            if ((parent instanceof NamingContainer) || (parent instanceof UIViewRoot)) {
                try {
                    result = parent.findComponent(forId);
                    if (result != null) {
                        break;
                    }
                } catch (Exception ex) {/*ignored*/}
            } else if (parent instanceof AjaxUpdater){
            	break;
            }
            root = parent;
            parent = parent.getParent();
        }

//        if (context != null) {
//            // FIXME this is redundent
//            root = context.getViewRoot();
//        }

        // If no found then traversal whole tree
        if (result == null && root != null) {
            Iterator<UIComponent> itr = createChildrenIterator(root, false);
            while (itr.hasNext()) {
                UIComponent comp = itr.next();
                if (comp instanceof NamingContainer) {
                    try {
                        result = comp.findComponent(forId);
                    } catch (Exception ex) {
                        continue;
                    }
                    if (result != null) {
                        break;
                    }
                }
            }
        }

        return result;
    }

    public static Object getLabel(FacesContext context, UIComponent component) {
        // Gets the "label" property from the component.
        Object o = component.getAttributes().get("label");

        // Find a Label component which has the "for" attribute identifies our component.
        if (o == null) {
            UIComponent parent = component;
            while (parent != null) {
                if (parent instanceof NamingContainer)
                    break;
                parent = parent.getParent();
            }
            if (parent != null) {
                UIComponent label = getLabelComponentFor(context, parent, component);
                if (label != null) {
                    o = label.getAttributes().get("value");
                }
            }
        }

        // Use the "clientId" if there was no label specified.
        if (o == null) {
            o = component.getClientId(context);
        }
        return o;
    }

    private static UIComponent getLabelComponentFor(FacesContext context,
                                                    UIComponent from,
                                                    UIComponent target)
    {
        Iterator<UIComponent> kids = createChildrenIterator(from, false);
        while (kids.hasNext()) {
            UIComponent comp = kids.next();
            if ("javax.faces.Output".equals(comp.getFamily()) &&
                "javax.faces.Label".equals(comp.getRendererType()))
            {
                String forId = (String)comp.getAttributes().get("for");
                if ((forId != null) && (getForComponent(context, forId, comp) == target)) {
                    return comp;
                }
            }
        }
        return null;
    }
    
    /**
     * Describe a component in logging or exception handling messages.
     */
    public static String getComponentDesc(UIComponent component) {
        String simpleName = component.getClass().getSimpleName();
        //get rid of cglib generated part
        int separatorPos = simpleName.indexOf("$$");
        if (separatorPos > 0) {
            simpleName = simpleName.substring(0, separatorPos);
        }
        return simpleName;
    }

    public static UIMessage getMessageComponent(FacesContext context, UIComponent component) {
        // Find a UIMessage component which has the "for" attribute identifies our component.
        UIComponent parent = component;
        while (parent != null) {
            if (parent instanceof NamingContainer)
                break;
            parent = parent.getParent();
        }

        if (parent != null) {
            return getMessageComponentFor(context, parent, component);
        } else {
            return null;
        }
    }

    public static String getMessageComponentId(FacesContext context, UIComponent component) {
        UIMessage message = getMessageComponent(context, component);
        if (message != null) {
            return message.getClientId(context);
        } else {
            return null;
        }
    }

    private static UIMessage getMessageComponentFor(FacesContext context,
                                                    UIComponent from,
                                                    UIComponent target)
    {
        Iterator<UIComponent> kids = createChildrenIterator(from, false);
        while (kids.hasNext()) {
            UIComponent comp = kids.next();
            if (comp instanceof UIMessage) {
                String forId = (String)comp.getAttributes().get("for");
                if ((forId != null) && (getForComponent(context, forId, comp) == target)) {
                    return (UIMessage)comp;
                }
            }
        }
        return null;
    }

    public static boolean isPostback(FacesContext context) {
        RenderKit renderKit = context.getRenderKit();
        if (renderKit != null) {
            return renderKit.getResponseStateManager().isPostback(context);
        }
        return false;
    }

    public static RenderKit getRenderKit(FacesContext context, String renderKitId) {
        RenderKit renderKit = null;
        if (renderKitId != null) {
            RenderKitFactory factory = (RenderKitFactory)
                FactoryFinder.getFactory(FactoryFinder.RENDER_KIT_FACTORY);
            renderKit = factory.getRenderKit(context, renderKitId);
        }
//      added by zhangyong, 增强校验
        if(renderKit == null) throw new IllegalArgumentException("Can not find RenderKit '" + renderKitId + "'");
        return renderKit;
    }

    public static Renderer getRenderer(FacesContext context, UIComponent component) {
        Renderer result = null;
        String family = component.getFamily();
        String rendererType = component.getRendererType();
        if (rendererType != null) {
            result = context.getRenderKit().getRenderer(family, rendererType);
        }
        return result;
    }

    private static ResponseWriter createHtmlResponseWriter(FacesContext context, Writer writer) {
        RenderKit rk = getRenderKit(context, RenderKitFactory.HTML_BASIC_RENDER_KIT);
        return rk.createResponseWriter(writer, null, null);
    }

    public static String encodeComponent(FacesContext context, UIComponent component) {
        StringWriter strWriter = new StringWriter();
        ResponseWriter bufWriter = createHtmlResponseWriter(context, strWriter);
        ResponseWriter curWriter = context.getResponseWriter();

        try {
            context.setResponseWriter(bufWriter);
            component.encodeAll(context);
            return strWriter.toString();
        } catch (IOException ex) {
            throw new FacesException(ex);
        } finally {
            if (curWriter != null) {
                context.setResponseWriter(curWriter);
            }
        }
    }

    public static String encodeComponentChildren(FacesContext context, UIComponent component) {
        if (component.getChildCount() == 0) {
            return "";
        }

        StringWriter strWriter = new StringWriter();
        ResponseWriter bufWriter = createHtmlResponseWriter(context, strWriter);
        ResponseWriter curWriter = context.getResponseWriter();

        try {
            context.setResponseWriter(bufWriter);
            for (UIComponent kid : component.getChildren()) {
                kid.encodeAll(context);
            }
            return strWriter.toString();
        } catch (IOException ex) {
            throw new FacesException(ex);
        } finally {
            if (curWriter != null) {
                context.setResponseWriter(curWriter);
            }
        }
    }

    private static final String NESTED_VIEW_ATTR = "org.operamasks.faces.NESTED_VIEW";
    private static final String DELETE_VIEW_ATTR = "org.operamasks.faces.DELETE_VIEW";

    /**
     * Called before a view tag, or a subview needs to be built.
     */
    public static void beginView(FacesContext context) {
        Map<String,Object> requestMap = context.getExternalContext().getRequestMap();
        int[] counter = (int[])requestMap.get(NESTED_VIEW_ATTR);
        if (counter == null) {
            counter = new int[1];
            counter[0] = 0;
            requestMap.put(NESTED_VIEW_ATTR, counter);
        }
        ++counter[0];
    }

    /**
     * Called after a view tag, or a subview build finished. returns true
     * if the current view is nested in another view.
     */
    public static boolean endView(FacesContext context) {
        Map<String,Object> requestMap = context.getExternalContext().getRequestMap();
        int[] counter = (int[])requestMap.get(NESTED_VIEW_ATTR);
        if (counter != null && --counter[0] <= 0) {
            requestMap.remove(NESTED_VIEW_ATTR);
            return false;
        } else {
            return true;
        }
    }

    private static final String TRANSIENT_STATE_MARKER = "org.operamasks.faces.TRANSIENT_STATE";

    /**
     * Determine whether transient state is supported.
     */
    public static final boolean isTransientStateSupported(FacesContext context) {
        return (context.getApplication().getViewHandler() instanceof ViewBuilder);
    }

    /**
     * Marks a request to not save the state of the view into the response.
     */
    public static final void markForTransientState(FacesContext context) {
        if (isTransientStateSupported(context)) {
            context.getExternalContext().getRequestMap().put(TRANSIENT_STATE_MARKER, Boolean.TRUE);
        }
    }

    /**
     * Releases the marker to not save the state of the view into the response.
     */
    public static final void unmarkTransientState(FacesContext context) {
        if (isTransientStateSupported(context)) {
            context.getExternalContext().getRequestMap().remove(TRANSIENT_STATE_MARKER);
        }
    }

    /**
     * Checks whether the request is marked for writing the state of a view or not.
     */
    public static boolean isMarkedForTransientState(FacesContext context) {
        if (isTransientStateSupported(context)) {
            return context.getExternalContext().getRequestMap().containsKey(TRANSIENT_STATE_MARKER);
        } else {
            return false;
        }
    }
    
    /**
     * Get the current view state in string representation. This method
     * is useful for renderers that directly process view state string
     * such as a Ajax Renderer.
     */
    public static String[] getViewState(FacesContext context) {
        final String[] result = new String[2];

        boolean markedForTransientState = false;
        if (isTransientStateSupported(context)) {
            ResponseWriter resp = context.getResponseWriter();
            if (resp != null) {
                if (resp instanceof AjaxHtmlResponseWriter) {
                    markedForTransientState = !((AjaxHtmlResponseWriter)resp).isWriteFullState();
                } else if (resp instanceof AjaxResponseWriter) {
                    markedForTransientState = !((AjaxResponseWriter)resp).isWriteFullState();
                }
            } else {
                // This is not a normal Faces request such as an asynchronous
                // data loading request. Check for transient state.
                ExternalContext ext = context.getExternalContext();
                if (ext.getRequestParameterMap().containsKey(VIEW_ID_PARAM)) {
                    // Mark for transient state because the request doesn't pass full state.
                    markedForTransientState = true;
                }
            }
        }

        if (markedForTransientState) {
            markForTransientState(context);
        }

        // use a wrapper to intecept view state string
        ResponseStateManager wrapper = new HtmlResponseStateManager() {
            protected void writeState(FacesContext ctx, String viewState, String renderKitId) {
                result[0] = viewState;
                result[1] = renderKitId;
            }
        };

        try {
            StateManager stateManager = context.getApplication().getStateManager();
            wrapper.writeState(context, stateManager.saveView(context));
            return result;
        } catch (IOException ex) {
            throw new FacesException(ex);
        } finally {
            if (markedForTransientState) {
                unmarkTransientState(context);
            }
        }
    }

    private static final SimplePool<MessageDigest> mdpool = new SimplePool<MessageDigest>(10);

    public static MessageDigest getMD5() {
        try {
            MessageDigest md5 = mdpool.get();
            if (md5 == null)
                md5 = MessageDigest.getInstance("MD5");
            return md5;
        } catch (NoSuchAlgorithmException ex) {
            throw new FacesException(ex);
        }
    }

    public static void returnMD5(MessageDigest md5) {
        mdpool.put(md5);
    }

    public static String makeJavascriptIdentifier(String name) {
        return name.replace(':', '$').replace("-", "$_");
    }

    public static String getJsvar(FacesContext context, UIComponent component) {
        String jsvar = (String)component.getAttributes().get("jsvar");
        if (jsvar == null || jsvar.length() == 0) {
            String clientId = component.getClientId(context);
            jsvar = makeJavascriptIdentifier(clientId);
        }
        return jsvar;
    }
    
    public static String getFormattedValue(FacesContext context, UIComponent component, Object value)
        throws ConverterException
    {
        Converter converter = getConverter(context, component, value);
        if (converter != null) {
            return converter.getAsString(context, component, value);
        } else {
            return (value == null) ? "" : value.toString();
        }
    }

    public static Object getObjectValue(FacesContext context, UIComponent component, String value)
    throws ConverterException
    {
        Converter converter = getConverter(context, component, value);
        if (converter != null) {
            return converter.getAsObject(context, component, value);
        }
        return value;
   }

    public static Converter getConverter(FacesContext context, UIComponent component, Object value) {
        Converter converter = null;
        if (component instanceof ValueHolder) {
            converter = ((ValueHolder)component).getConverter();
        }
        if (converter == null) {
            if ((value != null) && !(value instanceof String)) {
                converter = context.getApplication().createConverter(value.getClass());
            }
        }
        return converter;
    }
    
    public static Application getApplication() {
        FacesContext context = FacesContext.getCurrentInstance();
        if (context != null) {
            return (FacesContext.getCurrentInstance().getApplication());
        }
        ApplicationFactory afactory = (ApplicationFactory)
            FactoryFinder.getFactory(FactoryFinder.APPLICATION_FACTORY);
        return (afactory.getApplication());
    }

    public static ClassLoader getCurrentLoader(Class fallbackClass) {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        if (loader == null) {
            loader = fallbackClass.getClassLoader();
        }
        return loader;
    }

    public static String toCamelCase(String str) {
        StringBuilder buf = new StringBuilder(str.length() * 2);
        char[] chars = str.toCharArray();

        // Change "firstName" to "First Name".
        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];

            // Make the first character uppercase.
            if (i == 0) {
                c = Character.toUpperCase(c);
                buf.append(c);
                continue;
            }

            // Look for an uppercase character, if found add a space.
            if (Character.isUpperCase(c)) {
                buf.append(' ');
                buf.append(c);
                continue;
            }

            buf.append(c);
        }

        return buf.toString();
    }

    @SuppressWarnings("unchecked")
    public static <T> T getExpressionValue(UIComponent component, String name) {
        ValueExpression ve = component.getValueExpression(name);
        if (ve != null) {
            return (T)ve.getValue(FacesContext.getCurrentInstance().getELContext());
        } else {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T getManagerBean(FacesContext context, String beanName) {
//        ApplicationAssociate associate = ApplicationAssociate.getInstance(context.getExternalContext());
//        ManagedBeanFactory bean = associate.getManagedBeanFactory(beanName);
//        if (bean == null) {
//            return null;
//        }
//
//        ManagedBeanScope scope = bean.getScope();
//        if (scope == ManagedBeanScope.REQUEST) {
//            return (T)context.getExternalContext().getRequestMap().get(beanName);
//        } else if (scope == ManagedBeanScope.SESSION) {
//            return (T)context.getExternalContext().getSessionMap().get(beanName);
//        } else if (scope == ManagedBeanScope.APPLICATION) {
//            return (T)context.getExternalContext().getApplicationMap().get(beanName);
//        }
        
        return null;
    }

    /**
     * The Microsoft Internet Explorer is the most widely used but the worst
     * browser on this planet. We must take care not to step on mines.
     */
    public static boolean isMSIE(FacesContext context) {
        String ua = context.getExternalContext().getRequestHeaderMap().get("User-Agent");
        if (ua != null) {
            ua = ua.toLowerCase();
            return ua.indexOf("msie") != -1 && ua.indexOf("opera") == -1;
        }
        return false;
    }
    
    /**
     * send a action script to client
     * @param context
     * @return
     */
    public static void sendScript(String script) {
        FacesContext context = FacesContext.getCurrentInstance();
        if (!(context.getResponseWriter() instanceof AjaxResponseWriter)) {
            throw new IllegalArgumentException("writer must be AjaxResponseWriter");
        }
        AjaxResponseWriter out = (AjaxResponseWriter)context.getResponseWriter();
        out.setViewStateChanged();
        out.writeActionScript(script);
    }
    
    public static String encodeComponentState(FacesContext context, UIComponent component)
    throws IOException
    {
        Object state = component.processSaveState(context);
        ByteArrayOutputStream bout;
        ObjectOutputStream out;
        bout = new ByteArrayOutputStream();
        out = new ObjectOutputStream(new GZIPOutputStream(bout));
        out.writeObject(state);
        out.close();
        byte[] bytes = bout.toByteArray();
        return Base64.encode(bytes);
    }

    public static void decodeComponentState(FacesContext context, UIComponent component, String viewString)
    throws IOException, ClassNotFoundException
    {
        byte[] bytes = Base64.decode(viewString);
        InputStream bin = new ByteArrayInputStream(bytes);
        bin = new GZIPInputStream(bin);
        final ClassLoader contextLoader = Thread.currentThread().getContextClassLoader();
        ObjectInputStream in = new ObjectInputStream(bin) {
            @Override protected Class resolveClass(ObjectStreamClass desc)
                throws ClassNotFoundException
            {
                return Class.forName(desc.getName(), true, contextLoader);
            }};
        Object state = in.readObject();
        component.processRestoreState(context, state);
    }

    /**
     * Find the enclosing UIForm of the given UIComponent.
     */
    public static UIForm getParentForm(UIComponent component) {
        while (component != null) {
            if (component instanceof UIForm)
                return (UIForm)component;
            component = component.getParent();
        }
        return null;
    }

    /**
     * Indicate the server behavior is strictly conform to J2EE specification.
     */
    public static boolean strict() {
        return strict;
    }

    private static boolean strict = Boolean.getBoolean("apusic.strict");

    public static boolean isDelegateView(FacesContext context) {
        Map<String,Object> requestMap = context.getExternalContext().getRequestMap();
        Boolean isDelegate = (Boolean)requestMap.get(DELETE_VIEW_ATTR);
        return isDelegate != null && isDelegate;
    }

    public static void createComponent(UIComponent parent, UIComponent child) {
        if (parent == null || child == null) {
            throw new NullPointerException("component to be created is null");
        }
        FacesContext context = FacesContext.getCurrentInstance();
        if (context == null) {
            throw new NullPointerException("context is null");
        }
        
        UIViewRoot root = context.getViewRoot();
        if (root == null) {
            throw new NullPointerException("view root hasn't been initialized");
        }
        
        if (child.getId() == null) {
            child.setId(root.createUniqueId());
        }
        // a child can be added any times with diffrent component id 
        parent.getChildren().add(child);
    }

	public static PhaseId currentPhase() {
        return (PhaseId)FacesContext.getCurrentInstance().getExternalContext(
        		).getRequestMap().get(PhaseMonitorListener.PHASE_ID);
	}
	
	private static final String TIMEZONE_INIT_PARAMETER = "org.operamasks.faces.TIMEZONE";
	
	public static TimeZone getInitTimeZone() {
		String initTimeZone = FacesContext.getCurrentInstance().getExternalContext(
			).getInitParameter(TIMEZONE_INIT_PARAMETER);

		if (initTimeZone != null)
			return TimeZone.getTimeZone(initTimeZone);
		
		return null;
	}

	public static long getMaxSizeInBytes(String maxSizeString) {
		boolean isLegalSizeFormat = true;
		
		if (maxSizeString.length() <= 1)
			isLegalSizeFormat = false;
		
		char unitChar = maxSizeString.charAt(
				maxSizeString.length() - 1);
		
		if (unitChar != 'm' && unitChar != 'M' &&
				unitChar != 'k' && unitChar != 'K' &&
				unitChar != 'b' && unitChar != 'B') {
			isLegalSizeFormat = false;
		}
		
		long size = -1;
		
		try {
			size = Long.parseLong(maxSizeString.substring(0,
					maxSizeString.length() - 1));
		} catch (NumberFormatException e) {
			isLegalSizeFormat = false;
		}
		
		if (!isLegalSizeFormat) {
			throw new FacesException(new IllegalArgumentException("Illegal maxSize format: " +
					maxSizeString + ". You can set maxSize in human readable format(e.g., 4096B, 10K, 234M"));
		}
		
		long factor;
		
		if (unitChar == 'm' || unitChar == 'M') {
			factor = 1024 * 1024;
		} else if (unitChar == 'k' || unitChar == 'K') {
			factor = 1024;
		} else {
			factor = 1;
		}
		
		return (size * factor);
	}
	public static boolean isOwnEventOfTreeNode(FacesContext context, UITree tree,
			UITreeNode node) {
		if (node.getId().equals(FacesUtils.getEventNodeId(context, tree))) {
			return true;
		}
		
		return false;
	}

	private static Object getEventNodeId(FacesContext context, UITree tree) {
		return context.getExternalContext().getRequestParameterMap().get(
				TreeRenderUtils.getNodeIdKey(context, tree));
	}
	
	public static UIViewRoot getUIViewRoot(UIComponent component) {
		UIComponent parent = component;
		
		for (;;) {
			if (parent == null || parent instanceof UIViewRoot) {
				return (UIViewRoot)parent;
			}
			
			parent = parent.getParent();
		}
	}
	
    public static boolean isHtmlResponse(FacesContext context) {
        return (context.getResponseWriter() instanceof HtmlResponseWriter);
    }

    public static boolean isAjaxResponse(FacesContext context) {
        return (context.getResponseWriter() instanceof AjaxResponseWriter);
    }
    
    public static PhaseId getCurrentPhaseId(FacesContext context) {
    	PhaseId phaseId = (PhaseId) context.getExternalContext().getRequestMap().get(CURRENT_PHASE_ID);
    	return phaseId == null ? PhaseId.ANY_PHASE : phaseId;
    }
    
    public static String getFieldValueString(UIComponent comp, String fieldName) throws NoSuchFieldException {
        Field f = null;
        for(Class<?> clz = comp.getClass(); clz.getSuperclass() != null; clz = clz.getSuperclass()) {
            try {
                f = clz.getDeclaredField(fieldName);
                break;
            } catch(NoSuchFieldException ex) {
                //no-op
            }
        }
        Object value = null;
        if (f != null) {
            f.setAccessible(true);
            try {
                value = f.get(comp);
            } catch (IllegalAccessException ex) {
                //this exception should never be thrown
                throw new FacesException(ex);
            }
            if (value == null) {
                {
                    FacesContext ctxt = FacesContext.getCurrentInstance();
                    javax.el.ValueExpression ve = comp.getValueExpression(fieldName);
                    if (ve != null) {
                        try {
                            value = ve.getValue(ctxt.getELContext()); 
                        } catch (javax.el.ELException e) {
                            throw new javax.faces.FacesException(e);
                        }
                    }
                }
            }
        } else
            throw new NoSuchFieldException();
        if (value != null) 
            return value.toString();
        else
            return null;
    }

}