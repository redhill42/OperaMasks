/*
 * $Id: BindingBuilder.java,v 1.25 2008/04/02 00:51:54 patrick Exp $
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
package org.operamasks.faces.binding.impl;

import static org.operamasks.resources.Resources.JSF_NO_SUCH_CONVERTER_ID;
import static org.operamasks.resources.Resources.JSF_NO_SUCH_VALIDATOR_ID;
import static org.operamasks.resources.Resources.MVB_INVALID_ACTION_LISTENER_METHOD;
import static org.operamasks.resources.Resources.MVB_INVALID_ACTION_METHOD;
import static org.operamasks.resources.Resources.MVB_INVALID_AFTER_RENDER_METHOD;
import static org.operamasks.resources.Resources.MVB_INVALID_ASYNC_TREE_METHOD;
import static org.operamasks.resources.Resources.MVB_INVALID_BEFORE_RENDER_METHOD;
import static org.operamasks.resources.Resources.MVB_INVALID_CONVERT_METHOD;
import static org.operamasks.resources.Resources.MVB_INVALID_FORMAT_METHOD;
import static org.operamasks.resources.Resources.MVB_INVALID_PHASE_LISTENER_METHOD;
import static org.operamasks.resources.Resources.MVB_INVALID_READ_METHOD;
import static org.operamasks.resources.Resources.MVB_INVALID_SELECT_ITEMS_TYPE;
import static org.operamasks.resources.Resources.MVB_INVALID_TREE_EVENT_LISTENER_METHOD;
import static org.operamasks.resources.Resources.MVB_INVALID_VALIDATE_METHOD;
import static org.operamasks.resources.Resources.MVB_ASYNC_TREE_NULL_TREE_ID;
import static org.operamasks.resources.Resources.IOVC_INIT_CALLBACK_EMPTYNAME;
import static org.operamasks.resources.Resources._T;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.PhaseEvent;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.operamasks.faces.annotation.Action;
import org.operamasks.faces.annotation.ActionListener;
import org.operamasks.faces.annotation.AfterPhase;
import org.operamasks.faces.annotation.AfterRender;
import org.operamasks.faces.annotation.AsyncTreeMethod;
import org.operamasks.faces.annotation.AsyncTreeMethodType;
import org.operamasks.faces.annotation.BeforePhase;
import org.operamasks.faces.annotation.BeforeRender;
import org.operamasks.faces.annotation.Bind;
import org.operamasks.faces.annotation.ComponentAttributes;
import org.operamasks.faces.annotation.Convert;
import org.operamasks.faces.annotation.Converter;
import org.operamasks.faces.annotation.DataModel;
import org.operamasks.faces.annotation.Description;
import org.operamasks.faces.annotation.Format;
import org.operamasks.faces.annotation.Init;
import org.operamasks.faces.annotation.Inject;
import org.operamasks.faces.annotation.Label;
import org.operamasks.faces.annotation.LocalString;
import org.operamasks.faces.annotation.ManagedBean;
import org.operamasks.faces.annotation.ManagedBeanScope;
import org.operamasks.faces.annotation.Outject;
import org.operamasks.faces.annotation.RequestParam;
import org.operamasks.faces.annotation.Required;
import org.operamasks.faces.annotation.SaveState;
import org.operamasks.faces.annotation.SelectItem;
import org.operamasks.faces.annotation.SelectItems;
import org.operamasks.faces.annotation.TreeEventListener;
import org.operamasks.faces.annotation.Validate;
import org.operamasks.faces.annotation.Validator;
import org.operamasks.faces.annotation.Validators;
import org.operamasks.faces.application.ConverterFactory;
import org.operamasks.faces.application.ConverterRegistry;
import org.operamasks.faces.application.ManagedBeanContainer;
import org.operamasks.faces.application.ManagedBeanFactory;
import org.operamasks.faces.application.ValidatorFactory;
import org.operamasks.faces.application.ValidatorRegistry;
import org.operamasks.faces.binding.factories.CustomizingConverterFactory;
import org.operamasks.faces.binding.factories.CustomizingValidatorFactory;
import org.operamasks.faces.component.widget.tree.event.TreeEvent;
import org.operamasks.resources.Resources;

class BindingBuilder
{
    private final List<Binding> bindings = new ArrayList<Binding>();
    private final List<Injector> injectors = new ArrayList<Injector>();

    public List<Binding> getBindings() {
        Collections.sort(this.bindings, new Comparator<Binding>() {
            public int compare(Binding x, Binding y) {
                return x.getOrder() - y.getOrder();
            }
        });
        return this.bindings;
    }

    public List<Injector> getInjectors() {
        Collections.sort(this.injectors, new Comparator<Injector>() {
            public int compare(Injector x, Injector y) {
                return x.getOrder() - y.getOrder();
            }
        });
        return this.injectors;
    }

    protected void addBinding(Binding binding) {
        this.bindings.add(binding);

        if (binding instanceof Injector) {
            this.injectors.add((Injector)binding);
        }
    }

    public void build(Class<?> targetClass) {
        scan(targetClass);

        Class[] interfaces = targetClass.getInterfaces();
        if (interfaces != null) {
            for (Class c : interfaces) {
                build(c);
            }
        }

        Class<?> superclass = targetClass.getSuperclass();
        if (superclass != null) {
            build(superclass);
        }
    }

    private static AnnotationProcessor[] processors = {
        // don't change processors' order
        new SaveStateProcessor(),
        new ComponentAttributesProcessor(),
        new BindProcessor(),
        new InjectProcessor(),
        new OutjectProcessor(),
        new RequestParamProcessor(),
        new EntityManagerProcessor(),
        new LocalStringProcessor(),
        new DataModelProcessor(),
        new SelectItemsProcessor(),
        new ActionProcessor(),
        new ActionListenerProcessor(),
        new AsyncTreeProcessor(),
        new TreeEventListenerProcessor(),
        new ConvertProcessor(),
        new FormatProcessor(),
        new ValidateProcessor(),
        new BeforePhaseProcessor(),
        new AfterPhaseProcessor(),
        new BeforeRenderProcessor(),
        new AfterRenderProcessor()
    };

    @SuppressWarnings("unchecked")
    private void scan(Class<?> targetClass) {
        List<Method> initMethods = new ArrayList<Method>();

        for (Field field : targetClass.getDeclaredFields()) {
            for (AnnotationProcessor p : processors) {
                if (p.process(this, targetClass, field)) {
                    break;
                }
            }
        }

        for (Method method : targetClass.getDeclaredMethods()) {
            if (method.isAnnotationPresent(Init.class)) {
                initMethods.add(method);
                continue;
            }

            for (AnnotationProcessor p : processors) {
                if (p.process(this, targetClass, method)) {
                    break;
                }
            }
        }

        if (initMethods.size() > 0) {
            for (Method method : initMethods) {
                addInitMethod(method);
            }
        }
    }

    private void addInitMethod(Method method) {
        if (method.getParameterTypes().length != 0) {
            return;
        }

        String name = method.getAnnotation(Init.class).value();
        if (name == null || name.length() == 0) {
            name = method.getName();
            if (name.startsWith("init")) {
            	if (name.length() <= 4 /*length of "init"*/) {
            		throw new FacesException(_T(IOVC_INIT_CALLBACK_EMPTYNAME));
            	}
                name = name.substring(4);
                name = Character.toLowerCase(name.charAt(0)) + name.substring(1);
            } else {
                return;
            }
        }

        Class<?> type = method.getReturnType();

        for (Binding b : this.bindings) {
            if (b instanceof PropertyBinding) {
                PropertyBinding pb = (PropertyBinding)b;
                Field field = pb.getField();
                if (field != null &&
                    name.equals(field.getName()) &&
                    (type == Void.TYPE || type == field.getType()) &&
                    field.getDeclaringClass() == method.getDeclaringClass()) {
                    pb.setInitMethod(method);
                    break;
                }
            }
        }
    }

    private static abstract class AnnotationProcessor<T extends Annotation> {
        protected Class<T> metaType;

        protected AnnotationProcessor(Class<T> metaType) {
            this.metaType = metaType;
        }

        protected AnnotationProcessor(String metaTypeName) {
            try {
                this.metaType = (Class<T>)Class.forName(metaTypeName);
            } catch (Throwable ex) {
                this.metaType = null;
            }
        }

        public boolean process(BindingBuilder builder, Class<?> targetClass, AnnotatedElement f) {
            if (this.metaType == null)
                return false;

            T meta = f.getAnnotation(this.metaType);
            if (meta != null) {
                Binding b = this.build(meta, targetClass, f);
                if (b != null) {
                    builder.addBinding(b);
                    return this.exclusive();
                }
            }
            return false;
        }

        protected boolean exclusive() {
            return true;
        }

        protected Binding build(T meta, Class<?> targetClass, AnnotatedElement f) {
            if (f instanceof Field) {
                return build(meta, targetClass, (Field)f);
            } else {
                return build(meta, targetClass, (Method)f);
            }
        }

        protected Binding build(T meta, Class<?> targetClass, Field field) {
            return null;
        }

        protected Binding build(T meta, Class<?> targetClass, Method method) {
            return null;
        }
    }

    private static class ComponentAttributesProcessor extends AnnotationProcessor<ComponentAttributes> {

		ComponentAttributesProcessor() {
			super(ComponentAttributes.class);
		}
		
        protected Binding build(ComponentAttributes meta, Class<?> targetClass, AnnotatedElement f) {
            AttributesBinding b = new AttributesBinding(meta.view());
            b.init(ComponentAttributes.class, targetClass, f);

            String id = meta.id();

            if (id == null || id.length() == 0) {
                id = b.getName(); // the default ID is the property name
            }
            b.setId(id);
            return b;
        }
    }
    
    private static class BindProcessor extends AnnotationProcessor<Bind> {
        BindProcessor() {
            super(Bind.class);
        }

        protected Binding build(Bind meta, Class<?> targetClass, AnnotatedElement f) {
            ValueBinding b = new ValueBinding(meta.view());
            b.init(Bind.class, targetClass, f);

            String id = meta.id();
            String attribute = meta.attribute();
            String expr = meta.value();

            if (id == null || id.length() == 0) {
                id = b.getName(); // the default ID is the property name
            }

            if (attribute == null || attribute.length() == 0) {
                if (UIComponent.class.isAssignableFrom(b.getType())) {
                    attribute = "binding";
                } else {
                    attribute = "value";
                }
            }

            if (expr != null && expr.length() == 0) {
                expr = null;
            }

            b.setId(id);
            b.setAttribute(attribute);
            b.setExpression(expr);
            b.setOrder(meta.order());

            scanDescriptiveInformation(b, targetClass, f);
            scanConverterAndValidators(b, f);
            scanRequestParam(b, targetClass, f);
            scanSelectItems(b, f);

            return b;
        }

        private void scanDescriptiveInformation(ValueBinding b, Class<?> targetClass, AnnotatedElement f) {
            Label label = f.getAnnotation(Label.class);
            if (label != null) {
                b.setLabel(label.value());
            }

            Description desc = f.getAnnotation(Description.class);
            if (desc != null) {
                b.setDescription(desc.value());
            }

            LocalString ls = f.getAnnotation(LocalString.class);
            if (ls != null) {
                LocalStringProcessor lsp = new LocalStringProcessor();
                LocalStringBinding lsb = (LocalStringBinding)lsp.build(ls, targetClass, f);
                b.setLocalString(lsb);

                // fix for local string key
                String key = ls.key();
                if (key == null || key.length() == 0) {
                    key = b.getId() + "." + b.getAttribute();
                }
                lsb.setKey(key);
            }
        }

        private void scanConverterAndValidators(ValueBinding b, AnnotatedElement f) {
            for (Annotation a : f.getAnnotations()) {
                ConverterFactory cf = createConverterFactory(a);
                if (cf != null) {
                    b.setConverterFactory(cf);
                    b.setConverterMessage(getConverterMessage(a));
                }

                ValidatorFactory vf = createValidatorFactory(a);
                if (vf != null) {
                    b.addValidatorFactory(vf);
                }
            }

            Validators validators = f.getAnnotation(Validators.class);
            if (validators != null) {
                for (Validator v : validators.value()) {
                    b.addValidatorFactory(createValidatorFactory(v));
                }
            }

            Required required = f.getAnnotation(Required.class);
            if (required != null) {
                b.setRequired(true);
                b.setRequiredMessage(required.message());
            }
        }

        private void scanRequestParam(ValueBinding b, Class<?> targetClass, AnnotatedElement f) {
            RequestParam meta = f.getAnnotation(RequestParam.class);
            if (meta == null) {
                return;
            }

            RequestParamProcessor rpp = new RequestParamProcessor();
            RequestParamBinding rpb = (RequestParamBinding)rpp.build(meta, targetClass, f);
            b.setRequestParam(rpb);
        }

        private void scanSelectItems(ValueBinding b, AnnotatedElement f) {
            SelectItems meta = f.getAnnotation(SelectItems.class);
            if (meta == null) {
                return;
            }

            SelectItemsBinding sel = new SelectItemsBinding(b.getViewId());
            sel.setName(b.getName());
            sel.setType(b.getType());
            sel.setDeclaringClass(b.getDeclaringClass());
            SelectItemsProcessor.scanSelectItems(sel, meta);
            sel.setValueClass(b.getType());
            b.setSelectItems(sel);
        }
    }

    private static class InjectProcessor extends AnnotationProcessor<Inject> {
        InjectProcessor() {
            super(Inject.class);
        }

        public boolean exclusive() {
            return false;
        }

        protected Binding build(Inject meta, Class<?> targetClass, AnnotatedElement f) {
            DependencyBinding b = new DependencyBinding();
            b.init(Inject.class, targetClass, f);

            DependencyBinder binder = Binders.getDependencyBinder(b.getType());
            String expr = meta.value();

            if (binder != null) {
                binder.init(b, meta);
            } else if (expr.length() != 0) {
                binder = new InjectBinder(expr, meta.renew());
            } else {
                binder = createManagedBeanBinder(b, meta);
            }

            if (binder != null) {
                b.setBinder(binder);
                b.setOrder(meta.order());
                return b;
            }
            return null;
        }

        private DependencyBinder createManagedBeanBinder(DependencyBinding b, Inject meta) {
            String name = null;
            ManagedBeanScope scope = null;

            // find managed bean name by annotation
            for (Class<?> c = b.getType(); c != null; c = c.getSuperclass()) {
                ManagedBean mb = c.getAnnotation(ManagedBean.class);
                if (mb != null) {
                    name = mb.name();
                    scope = mb.scope();
                    if (name.length() == 0) {
                        name = c.getName();
                        name = name.substring(name.lastIndexOf('.')+1);
                    }
                    break;
                }
            }

            // find managed bean name from config file
            if (name == null) {
                String classname = b.getType().getName();
                for (ManagedBeanFactory f : ManagedBeanContainer.getInstance().getBeanFactories()) {
                    if (classname.equals(f.getBeanClassName())) {
                        name = f.getBeanName();
                        scope = f.getScope();
                    }
                }
            }

            if (name != null) {
                String expr = "#{" + name + "}";
                boolean renew = meta.renew();

                if (scope == null || scope == ManagedBeanScope.NONE || scope == ManagedBeanScope.REQUEST) {
                    renew = true;
                } else if (scope == ManagedBeanScope.APPLICATION) {
                    renew = false;
                }

                return new InjectBinder(expr, renew);
            }

            return null;
        }
    }

    private static class OutjectProcessor extends AnnotationProcessor<Outject> {
        OutjectProcessor() {
            super(Outject.class);
        }

        public boolean exclusive() {
            return false;
        }

        protected Binding build(Outject meta, Class<?> targetClass, AnnotatedElement f) {
            DependencyBinding b = new DependencyBinding();
            b.init(Outject.class, targetClass, f);

            String expr = meta.value();
            if (expr.length() == 0) {
                expr = getInjectionExpression(b);
                if (expr == null) {
                    expr = b.getName();
                }
            }

            b.setBinder(new OutjectBinder(expr, meta.scope()));
            return b;
        }

        private String getInjectionExpression(DependencyBinding b) {
            String expr = null;
            Inject inject = null;

            // Find @Inject annotation from field, read method, or write method
            if (b.getField() != null) {
                inject = b.getField().getAnnotation(Inject.class);
            } else {
                Method read = b.getReadMethod();
                Method write = b.getWriteMethod();
                if (read != null)
                    inject = read.getAnnotation(Inject.class);
                if (inject == null && write != null)
                    inject = write.getAnnotation(Inject.class);
            }

            // Use inject expression as outject expression if any
            if (inject != null) {
                expr = inject.value();
                if (expr.length() == 0) {
                    expr = null;
                }
            }

            return expr;
        }
    }

    private static class RequestParamProcessor extends AnnotationProcessor<RequestParam> {
        RequestParamProcessor() {
            super(RequestParam.class);
        }

        protected Binding build(RequestParam meta, Class<?> targetClass, AnnotatedElement f) {
            RequestParamBinding b = new RequestParamBinding(null);
            b.init(RequestParam.class, targetClass, f);

            String name = meta.name();
            if (name.length() == 0) {
                name = b.getName();
            }
            b.setId(name);

            scanConverterAndValidators(b, f);

            return b;
        }

        private void scanConverterAndValidators(RequestParamBinding b, AnnotatedElement f) {
            for (Annotation a : f.getAnnotations()) {
                ConverterFactory cf = createConverterFactory(a);
                if (cf != null) {
                    b.setConverterFactory(cf);
                    b.setConverterMessage(getConverterMessage(a));
                }

                ValidatorFactory vf = createValidatorFactory(a);
                if (vf != null) {
                    b.addValidatorFactory(vf);
                }
            }

            Validators validators = f.getAnnotation(Validators.class);
            if (validators != null) {
                for (Validator v : validators.value()) {
                    b.addValidatorFactory(createValidatorFactory(v));
                }
            }

            Required required = f.getAnnotation(Required.class);
            if (required != null) {
                b.setRequired(true);
                b.setRequiredMessage(required.message());
            }
        }
    }

    static ConverterFactory createConverterFactory(Annotation a) {
        Class<? extends Annotation> at = a.annotationType();

        // Create ConverterFactory from @Converter annotation
        if (a instanceof Converter) {
            return createConverterFactory((Converter)a);
        }

        // Create ConverterFactory from annotation that annotated with @Converter
        if (at.isAnnotationPresent(Converter.class)) {
            try {
                ConverterFactory factory = createConverterFactory(at.getAnnotation(Converter.class));
                CustomizingConverterFactory customized = new CustomizingConverterFactory(factory);

                for (Method method : at.getDeclaredMethods()) {
                    String name = method.getName();
                    if (!name.equals("message")) {
                        Object value = method.invoke(a);
                        customized.setConverterProperty(name, value);
                    }
                }

                return customized;
            } catch (Exception ex) {
                throw new FacesException(ex);
            }
        }

        return null;
    }

    static ConverterFactory createConverterFactory(Converter meta) {
        ConverterRegistry registry = ConverterRegistry.getInstance();
        if (meta.id().length() != 0) {
            ConverterFactory factory = registry.getConverterFactory(meta.id());
            if (factory == null) {
                throw new FacesException(_T(JSF_NO_SUCH_CONVERTER_ID, meta.id()));
            } else {
                return factory;
            }
        } else {
            return registry.createConverterFactory(meta.value());
        }
    }

    static String getConverterMessage(Annotation a) {
        // Retrieve message attribute from meta annotation.
        try {
            Method m = a.annotationType().getMethod("message");
            if (m.getReturnType() == String.class) {
                String message = (String)m.invoke(a);
                if (message != null && message.length() != 0) {
                    return message;
                }
            }
        } catch (Exception ex) {
            // ignore message attribute
        }

        // Retrieive message attribute from @Converter meta-meta annotation.
        Converter meta = a.annotationType().getAnnotation(Converter.class);
        if (meta != null) {
            String message = meta.message();
            if (message != null && message.length() != 0) {
                return message;
            }
        }

        return null;
    }

    static ValidatorFactory createValidatorFactory(Annotation a) {
        Class<? extends Annotation> at = a.annotationType();

        // Create ValidatorFactory from @Validator annotation
        if (a instanceof Validator) {
            return createValidatorFactory((Validator)a);
        }

        // Create ValidatorFactory from annotation that annotated with @Validator
        if (at.isAnnotationPresent(Validator.class)) {
            try {
                ValidatorFactory factory = createValidatorFactory(at.getAnnotation(Validator.class));
                CustomizingValidatorFactory customized = new CustomizingValidatorFactory(factory);

                for (Method method : at.getDeclaredMethods()) {
                    String name = method.getName();
                    Object value = method.invoke(a);
                    customized.setValidatorProperty(name, value);
                }

                return customized;
            } catch (Exception ex) {
                throw new FacesException(ex);
            }
        }

        return null;
    }

    static ValidatorFactory createValidatorFactory(Validator meta) {
        ValidatorRegistry registry = ValidatorRegistry.getInstance();
        if (meta.id().length() != 0) {
            ValidatorFactory factory = registry.getValidatorFactory(meta.id());
            if (factory == null) {
                throw new FacesException(_T(JSF_NO_SUCH_VALIDATOR_ID, meta.id()));
            } else {
                return factory;
            }
        } else {
            return registry.createValidatorFactory(meta.value());
        }
    }

    private static class EntityManagerProcessor extends AnnotationProcessor<PersistenceContext> {
        EntityManagerProcessor() {
            super("javax.persistence.PersistenceContext");
        }

        protected Binding build(PersistenceContext meta, Class targetClass, AnnotatedElement f) {
            DependencyBinding b = new DependencyBinding();
            b.init(PersistenceContext.class, targetClass, f);
            b.setBinder(Binders.getDependencyBinder(EntityManager.class));
            b.setOrder(Integer.MIN_VALUE);
            return b;
        }
    }

    private static class LocalStringProcessor extends AnnotationProcessor<LocalString> {
        LocalStringProcessor() {
            super(LocalString.class);
        }

        protected Binding build(LocalString meta, Class<?> targetClass, AnnotatedElement f) {
            LocalStringBinding b = new LocalStringBinding(null);
            b.init(LocalString.class, targetClass, f);

            String key = meta.key();
            if (key == null || key.length() == 0) {
                key = b.getName();
            }

            b.setBasename(meta.basename());
            b.setKey(key);
            return b;
        }
    }

    private static class SaveStateProcessor extends AnnotationProcessor<SaveState> {
        SaveStateProcessor() {
            super(SaveState.class);
        }

        public boolean exclusive() {
            return false;
        }

        // The unique server state key generator.
        private static AtomicInteger keyGenerator = new AtomicInteger();

        protected Binding build(SaveState meta, Class<?> targetClass, Field field) {
            String key = String.valueOf(keyGenerator.incrementAndGet());
            return new FieldStateBinding(meta.view(), key, field, meta.inServer());
        }
    }

    private static class DataModelProcessor extends AnnotationProcessor<DataModel> {
        DataModelProcessor() {
            super(DataModel.class);
        }

        protected Binding build(DataModel meta, Class<?> targetClass, Field field) {
            DataModelBinding b = new DataModelBinding(meta.view());
            b.init(DataModel.class, targetClass, field);
            initDataModelBinding(b, meta);
            return b;
        }

        protected Binding build(DataModel meta, Class<?> targetClass, Method method) {
            String methodName = method.getName();
            Class returnType = method.getReturnType();
            Class[] paramTypes = method.getParameterTypes();

            DataModelBinding b = new DataModelBinding(meta.view());
            if (methodName.startsWith("get") || methodName.startsWith("set")) {
                b.init(DataModel.class, targetClass, method);
            } else {
                if (paramTypes.length != 0 || returnType == Void.TYPE)
                    throw new FacesException(_T(MVB_INVALID_READ_METHOD, methodName));
                b.init(DataModel.class, methodName, returnType, targetClass, null, method, null);
                b.setDeclaringClass(method.getDeclaringClass());
            }

            initDataModelBinding(b, meta);
            return b;
        }

        private void initDataModelBinding(DataModelBinding b, DataModel meta) {
            String name = b.getName();
            Class type = b.getType();
            Field field = b.getField();
            Method method = b.getReadMethod();

            if (field == null && method == null) {
                throw new FacesException(name + ": The data model binding must have a get method."); // i18n
            }

            Class itemType = meta.itemType();
            if (itemType == null || itemType == Object.class) {
                if (type.isArray()) {
                    itemType = type.getComponentType();
                } else if (List.class.isAssignableFrom(type)) {
                    Type t = (method != null) ? method.getGenericReturnType()
                                              : field.getGenericType();
                    if (t instanceof ParameterizedType) {
                        Type[] args = ((ParameterizedType)t).getActualTypeArguments();
                        if ((args.length == 1) && (args[0] instanceof Class)) {
                            itemType = (Class)args[0];
                        }
                    }
                }
                if (itemType == null || itemType == Object.class) {
                    throw new FacesException(name + ": The itemType must be specified."); // i18n
                }
            }

            String id = meta.id();
            if (id == null || id.length() == 0) {
                id = name; // the default ID is the property name.
            }

            b.setId(id);
            b.setItemType(itemType);
        }
    }

    private static class SelectItemsProcessor extends AnnotationProcessor<SelectItems> {
        SelectItemsProcessor() {
            super(SelectItems.class);
        }

        protected Binding build(SelectItems meta, Class<?> targetClass, AnnotatedElement f) {
            SelectItemsBinding b = new SelectItemsBinding(null);
            b.init(SelectItems.class, targetClass, f);

            Class type = b.getType();
            if (type != javax.faces.model.SelectItem[].class && type != List.class) {
                throw new FacesException(_T(MVB_INVALID_SELECT_ITEMS_TYPE, b.getName()));
            }

            scanSelectItems(b, meta);
            return b;
        }

        static void scanSelectItems(SelectItemsBinding b, SelectItems meta) {
            SelectItem[] metaItems = meta.value();
            int count = metaItems.length;
            javax.faces.model.SelectItem[] selectItems = new javax.faces.model.SelectItem[count];

            for (int i = 0; i < count; i++) {
                javax.faces.model.SelectItem item = new javax.faces.model.SelectItem();
                item.setValue(metaItems[i].value());
                item.setLabel(metaItems[i].label());
                item.setDescription(metaItems[i].description());
                item.setDisabled(metaItems[i].disabled());
                item.setEscape(metaItems[i].escape());
                selectItems[i] = item;
            }

            b.setSource(meta.source());
            b.setMapValue(meta.mapValue());
            b.setMapLabel(meta.mapLabel());
            b.setItems(selectItems);
            b.setValueClass(meta.valueClass());
        }
    }

    private static class ActionProcessor extends AnnotationProcessor<Action> {
        ActionProcessor() {
            super(Action.class);
        }

        protected Binding build(Action meta, Class<?> targetClass, Method method) {
            if (method.getParameterTypes().length != 0) {
                throw new FacesException(_T(MVB_INVALID_ACTION_METHOD, method.getName()));
            }

            ActionBinding b = createActionBinding(meta.view(),
                                                  meta.id(),
                                                  meta.event(),
                                                  meta.immediate(),
                                                  method.getName());
            scanDescriptiveInformation(b, method);
            b.setActionMethod(method);
            return b;
        }

        private void scanDescriptiveInformation(ActionBinding b, AnnotatedElement f) {
            Label label = f.getAnnotation(Label.class);
            if (label != null) {
                b.setLabel(label.value());
            }

            Description desc = f.getAnnotation(Description.class);
            if (desc != null) {
                b.setDescription(desc.value());
            }
        }
    }

    private static class ActionListenerProcessor extends AnnotationProcessor<ActionListener> {
        ActionListenerProcessor() {
            super(ActionListener.class);
        }

        protected Binding build(ActionListener meta, Class<?> targetClass, Method method) {
            if (!checkActionListenerMethod(method)) {
                throw new FacesException(_T(MVB_INVALID_ACTION_LISTENER_METHOD, method.getName()));
            }

            ActionBinding b = createActionBinding(meta.view(),
                                                  meta.id(),
                                                  meta.event(),
                                                  meta.immediate(),
                                                  method.getName());
            b.setActionListenerMethod(method);
            return b;
        }

        private boolean checkActionListenerMethod(Method method) {
            Class[] params = method.getParameterTypes();
            if (params.length == 0) {
                return true;
            } else if (params.length == 1) {
                return (params[0] == ActionEvent.class);
            } else {
                return false;
            }
        }
    }
    
    static ActionBinding createActionBinding(String viewId,
                                             String id,
                                             String event,
                                             boolean immediate,
                                             String methodName)
    {
        if (id != null && id.length() == 0)
            id = null;
        if (event != null && event.length() == 0)
            event = null;

        if (id == null) {
            if (event == null) {
                // The naming convention for action method is xxx_onyyy, where
                // xxx is the component ID and onyyy is the event type.
                int pos = methodName.indexOf("_on");
                if (pos != -1) {
                    id = methodName.substring(0, pos);
                    event = methodName.substring(pos+1);
                } else {
                    id = methodName;
                    event = null;
                }
            } else {
                id = methodName;
            }
        }

        ActionBinding b = new ActionBinding(viewId, id);
        b.setEvent(event);
        b.setImmediate(immediate);
        return b;
    }
    
    private static class AsyncTreeProcessor extends AnnotationProcessor<AsyncTreeMethod> {
    	AsyncTreeProcessor() {
            super(AsyncTreeMethod.class);
        }

        protected Binding build(AsyncTreeMethod meta, Class<?> targetClass, Method method) {
            return createAsyncTreeBinding(meta, targetClass, method);
        }
        
        private AsyncTreeMethodBinding createAsyncTreeBinding(AsyncTreeMethod meta,
				Class<?> targetClass, Method method) {
        	String asyncTreeMethod = null;
        	String id = (meta == null) ? "" : meta.id();
        	String view = (meta == null) ? "" : meta.view();
       		asyncTreeMethod = (meta == null) ? null : meta.value().toString();
        	
        	if (id.equals("") || asyncTreeMethod == null) {
        		String methodName = method.getName();
        		int pos = methodName.indexOf("_");
        		
        		if (pos != -1 && id.equals("")) {
        			id = methodName.substring(0, pos);
        		}
        		
        		if (pos != -1 && asyncTreeMethod == null) {
        			asyncTreeMethod = methodName.substring(pos + 1);
       			} else if (asyncTreeMethod == null) {
       				asyncTreeMethod = methodName;
       			}
        	}
        	
        	if ("".equals(id))
        		throw new FacesException(Resources._T(MVB_ASYNC_TREE_NULL_TREE_ID));
        	
        	boolean isAllowedMethod = false;
        	for (AsyncTreeMethodType allowedMethod : AsyncTreeMethodType.values()) {
        		if (asyncTreeMethod == null || allowedMethod == null ||
        						"".equals(asyncTreeMethod))
        			break;
        		
        		if (allowedMethod.toString().equals(asyncTreeMethod)) {
        			isAllowedMethod = true;
        			break;
        		}
        	}
        	
        	if (!isAllowedMethod)
        		throw new FacesException(Resources._T(MVB_INVALID_ASYNC_TREE_METHOD,
        				(asyncTreeMethod == null || !"".equals(asyncTreeMethod) ||
        								"NULL".equals(asyncTreeMethod)) ?
        							method.getName() : asyncTreeMethod ));
        	
        	return new AsyncTreeMethodBinding(view, id, asyncTreeMethod, method);
		}
    }
    
    private static class TreeEventListenerProcessor extends AnnotationProcessor<TreeEventListener> {
        TreeEventListenerProcessor() {
            super(TreeEventListener.class);
        }

        protected Binding build(TreeEventListener meta, Class<?> targetClass, Method method) {
            if (!checkTreeEventListenerMethod(method)) {
                throw new FacesException(_T(MVB_INVALID_TREE_EVENT_LISTENER_METHOD, method.getName()));
            }

            TreeEventListenerBinding b = createTreeEventListenerBinding(meta, targetClass, method);
            return b;
        }
        
        private TreeEventListenerBinding createTreeEventListenerBinding(
					TreeEventListener meta, Class<?> targetClass, Method method) {
        	String view = meta.view();
        	String id = meta.id();
        	boolean click = meta.click();
        	boolean dblClick = meta.dblClick();
        	boolean collapse = meta.collapse();
        	boolean expand = meta.expand();
        	boolean select = meta.select();
        	String[] events = meta.events();
        	
        	if (id.equals("")) {
        		String methodName = method.getName();
        		int pos = methodName.indexOf("_");
        		
        		if (pos != -1) {
        			id = methodName.substring(0, pos);
        		}
        	}
        	
        	TreeEventListenerBinding b = new TreeEventListenerBinding(view, id);
        	b.setClick(click);
        	b.setDblClick(dblClick);
        	b.setExpand(expand);
        	b.setCollapse(collapse);
        	b.setSelect(select);
        	b.setEvents(events);
        	b.setTreeEventListenerMethod(method);
        	
			return b;
		}

		@SuppressWarnings("unchecked")
		private boolean checkTreeEventListenerMethod(Method method) {
            Class[] params = method.getParameterTypes();
            if (params.length == 0) {
                return true;
            } else if (params.length == 1) {
                return (params[0] == TreeEvent.class);
            } else {
                return false;
            }
        }
    }

    private static class ConvertProcessor extends AnnotationProcessor<Convert> {
        ConvertProcessor() {
            super(Convert.class);
        }

        protected Binding build(Convert meta, Class<?> targetClass, Method method) {
            if (!checkConvertMethod(method)) {
                throw new FacesException(_T(MVB_INVALID_CONVERT_METHOD, method.getName()));
            }

            String[] ids = meta.id();
            if (ids == null || ids.length == 0) {
                String name = method.getName();
                if (name.startsWith("convert")) {
                    name = name.substring("convert".length());
                    if (name.length() == 0)
                        return null;
                    name = Character.toLowerCase(name.charAt(0)) + name.substring(1);
                    ids = new String[] { name };
                }
            }

            ConverterBinding b = new ConverterBinding(meta.view(), ids);
            b.setConvertMethod(method);
            return b;
        }

        private boolean checkConvertMethod(Method method) {
            // We support two convert method signature:
            //   one is standard method: Object convert(FacesContext, UIComponent, String)
            //   the other one only have one argument of the value to be converted.
            Class[] params = method.getParameterTypes();
            if (params.length == 1) {
                return (params[0] == String.class);
            } else if (params.length == 3) {
                return params[0] == FacesContext.class
                    && UIComponent.class.isAssignableFrom(params[1])
                    && params[2] == String.class;
            } else {
                return false;
            }
        }
    }

    private static class FormatProcessor extends AnnotationProcessor<Format> {
        FormatProcessor() {
            super(Format.class);
        }

        protected Binding build(Format meta, Class<?> targetClass, Method method) {
            if (!checkFormatMethod(method)) {
                throw new FacesException(_T(MVB_INVALID_FORMAT_METHOD, method.getName()));
            }

            String[] ids = meta.id();
            if (ids == null || ids.length == 0) {
                String name = method.getName();
                if (name.startsWith("format")) {
                    name = name.substring("format".length());
                    if (name.length() == 0)
                        return null;
                    name = Character.toLowerCase(name.charAt(0)) + name.substring(1);
                    ids = new String[] { name };
                }
            }

            ConverterBinding b = new ConverterBinding(meta.view(), ids);
            b.setFormatMethod(method);
            return b;
        }

        private boolean checkFormatMethod(Method method) {
            // We support two format method signature:
            //   one is standard method: String convert(FacesContext, UIComponent, Object)
            //   the other one only have one argument of the value to be converted.
            if (method.getReturnType() != String.class) {
                return false;
            }

            Class[] params = method.getParameterTypes();
            if (params.length == 1) {
                return true;
            } else if (params.length == 3) {
                return params[0] == FacesContext.class
                    && UIComponent.class.isAssignableFrom(params[1]);
            } else {
                return false;
            }
        }
    }

    private static class ValidateProcessor extends AnnotationProcessor<Validate> {
        ValidateProcessor() {
            super(Validate.class);
        }

        protected Binding build(Validate meta, Class<?> targetClass, Method method) {
            if (!checkValidateMethod(method)) {
                throw new FacesException(_T(MVB_INVALID_VALIDATE_METHOD, method.getName()));
            }

            String[] ids = meta.id();
            if (ids == null || ids.length == 0) {
                String name = method.getName();
                if (name.startsWith("validate")) {
                    name = name.substring("validate".length());
                    if (name.length() == 0)
                        return null;
                    name = Character.toLowerCase(name.charAt(0)) + name.substring(1);
                    ids = new String[] { name };
                }
            }

            ValidatorBinding b = new ValidatorBinding(meta.view(), ids);
            b.setValidateMethod(method);
            b.setValidateScript(meta.script());
            b.setMessage(meta.message());
            return b;
        }

        private boolean checkValidateMethod(Method method) {
            // We support two validate method signature:
            //   one is standard method: void validate(FacesContext, UIComponent, Object)
            //   the other one only have one argument of the value to be validated.
            Class[] params = method.getParameterTypes();
            if (params.length == 1) {
                return true;
            } else if (params.length == 3) {
                return params[0] == FacesContext.class
                    && UIComponent.class.isAssignableFrom(params[1]);
            } else {
                return false;
            }
        }
    }

    private static class BeforePhaseProcessor extends AnnotationProcessor<BeforePhase> {
        BeforePhaseProcessor() {
            super(BeforePhase.class);
        }

        protected Binding build(BeforePhase meta, Class<?> targetClass, Method method) {
            Class ret = method.getReturnType();
            Class[] args = method.getParameterTypes();
            if (ret != Void.TYPE || (args.length != 1 || args[0] != PhaseEvent.class)) {
                throw new FacesException(_T(MVB_INVALID_PHASE_LISTENER_METHOD, method.getName()));
            }

            PhaseListenerBinding b = new PhaseListenerBinding(meta.view());
            b.setBeforePhaseMethod(method);
            return b;
        }
    }

    private static class AfterPhaseProcessor extends AnnotationProcessor<AfterPhase> {
        AfterPhaseProcessor() {
            super(AfterPhase.class);
        }

        protected Binding build(AfterPhase meta, Class<?> targetClass, Method method) {
            Class ret = method.getReturnType();
            Class[] args = method.getParameterTypes();
            if (ret != Void.TYPE || (args.length != 1 || args[0] != PhaseEvent.class)) {
                throw new FacesException(_T(MVB_INVALID_PHASE_LISTENER_METHOD, method.getName()));
            }

            PhaseListenerBinding b = new PhaseListenerBinding(meta.view());
            b.setAfterPhaseMethod(method);
            return b;
        }
    }

    private static class BeforeRenderProcessor extends AnnotationProcessor<BeforeRender> {
        BeforeRenderProcessor() {
            super(BeforeRender.class);
        }

        protected Binding build(BeforeRender meta, Class<?> targetClass, Method method) {
            Class ret = method.getReturnType();
            Class[] args = method.getParameterTypes();
            if (ret != Void.TYPE || (args.length != 1 || args[0] != Boolean.TYPE)) {
                throw new FacesException(_T(MVB_INVALID_BEFORE_RENDER_METHOD, method.getName()));
            }

            PhaseListenerBinding b = new PhaseListenerBinding(meta.view());
            b.setBeforeRenderMethod(method);
            return b;
        }
    }

    private static class AfterRenderProcessor extends AnnotationProcessor<AfterRender> {
        AfterRenderProcessor() {
            super(AfterRender.class);
        }

        protected Binding build(AfterRender meta, Class<?> targetClass, Method method) {
            Class ret = method.getReturnType();
            Class[] args = method.getParameterTypes();
            if (ret != Void.TYPE || args.length != 0) {
                throw new FacesException(_T(MVB_INVALID_AFTER_RENDER_METHOD, method.getName()));
            }

            PhaseListenerBinding b = new PhaseListenerBinding(meta.view());
            b.setAfterRenderMethod(method);
            return b;
        }
    }
}
