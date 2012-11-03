/*
 * $Id: ELBindingBuilder.java,v 1.13 2008/01/31 04:12:24 daniel Exp $
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

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.Collection;
import javax.el.ELContext;
import javax.el.ValueExpression;
import javax.faces.component.UIComponent;
import javax.faces.FacesException;
import javax.faces.model.SelectItem;

import elite.lang.Closure;
import elite.lang.Annotation;
import org.operamasks.el.eval.closure.AnnotatedClosure;
import org.operamasks.el.resolver.ClassResolver;
import org.operamasks.faces.application.ConverterFactory;
import org.operamasks.faces.application.ConverterRegistry;
import org.operamasks.faces.application.ValidatorFactory;
import org.operamasks.faces.application.ValidatorRegistry;
import org.operamasks.faces.binding.factories.CustomizingConverterFactory;
import org.operamasks.faces.binding.factories.CustomizingValidatorFactory;
import org.operamasks.faces.binding.ModelBinder;
import org.operamasks.faces.annotation.Converter;
import org.operamasks.faces.annotation.Validator;
import static org.operamasks.resources.Resources.*;

final class ELBindingBuilder
{
    private final List<Binding> bindings;

    private static final String BINDING_MARKER = "org.operamasks.faces.Binding";

    ELBindingBuilder() {
        this.bindings = new ArrayList<Binding>();
    }

    ModelBinder build(ELContext elctx, String path, Map<String,ValueExpression> vmap) {
        ClassResolver cr = ClassResolver.getInstance(elctx);
        cr.addImport("org.operamasks.faces.annotation.*");

        // Process annotated closures
        for (Map.Entry<String,ValueExpression> e : vmap.entrySet()) {
            if (e.getValue() instanceof AnnotatedClosure) {
                AnnotatedClosure c = (AnnotatedClosure)e.getValue();
                if (markForCurrentPath(c, path)) { // ensure no duplicate
                    scan(elctx, path, e.getKey(), c);
                }
            }
        }

        // Add the onPageLoad closure
        ValueExpression ve = vmap.get("onPageLoad");
        if (ve != null) {
            Object value = ve.getValue(elctx);
            if (value instanceof Closure) {
                ELPhaseListenerBinding b = new ELPhaseListenerBinding();
                b.setOnPageLoadClosure((Closure)value);
                bindings.add(b);
            }
        }

        // Add implicit local string bindings
        if (path != null) {
            for (String key : ELiteBean.getLocalStringKeys(path)) {
                int i = key.indexOf('.');
                if (i != -1) {
                    String id = key.substring(0, i);
                    String att = key.substring(i+1);
                    if (isValidAttribute(att)) {
                        addLocalStringBinding(key, id, att);
                    }
                }
            }
        }

        if (bindings.isEmpty()) {
            return null;
        } else {
            Collections.sort(this.bindings, new Comparator<Binding>() {
                public int compare(Binding x, Binding y) {
                    return x.getOrder() - y.getOrder();
                }});
            return new ELBinderImpl(bindings);
        }
    }

    private boolean markForCurrentPath(AnnotatedClosure closure, String path) {
        Annotation marker = closure.getAnnotation(BINDING_MARKER);

        if (path == null) {
            // the closure that load from external script cannot
            // bind to embeded script.
            return marker == null;
        }

        if (marker == null) {
            // we first encountered this closure, so bind it.
            marker = new Annotation(BINDING_MARKER);
            marker.setAttribute("path", path);
            closure.addAnnotation(marker);
            return true;
        }

        // the closure must be bound to a single script.
        String markpath = marker.getAttribute("path", String.class);
        return path.equals(markpath);
    }

    private void scan(ELContext ctx, String path, String name, AnnotatedClosure closure) {
        for (AnnotationProcessor p : processors) {
            Binding b = p.process(ctx, path, name, closure);
            if (b != null) {
                bindings.add(b);
                if (p.exclusive()) {
                    break;
                }
            }
        }
    }

    private void addLocalStringBinding(String key, String id, String att) {
        // find a ValueBinding with the same id and attribute
        for (Binding b : this.bindings) {
            if (b instanceof ELValueBinding) {
                ELValueBinding vb = (ELValueBinding)b;
                if (id.equals(vb.getId()) && att.equals(vb.getAttribute())) {
                    // if found then set the local string key for the
                    // value binding instead of create separate binding.
                    if (vb.getLocalString() == null) {
                        vb.setLocalString(new ELLocalStringBinding(vb.getClosure(), key, vb.getType()));
                    }
                    return;
                }
            }
        }

        bindings.add(new ELLocalStringAttributeBinding(id, att));
    }

    private boolean isValidAttribute(String att) {
        return att.matches("[a-zA-Z][a-zA-Z0-9_]*")
            && !"label".equals(att)
            && !"description".equals(att);
    }

    private static abstract class AnnotationProcessor {
        protected String metaType;

        protected AnnotationProcessor(String metaType) {
            this.metaType = metaType;
        }

        public Binding process(ELContext ctx, String path, String name, AnnotatedClosure c) {
            Annotation meta = c.getAnnotation(this.metaType);
            if (meta != null) {
                return this.build(ctx, name, c, meta);
            }
            return null;
        }

        protected boolean exclusive() {
            return true;
        }

        protected Binding build(ELContext ctx, String name, AnnotatedClosure c, Annotation meta) {
            throw new AssertionError();
        }
    }

    private static AnnotationProcessor[] processors = {
        new ScopeProcessor(),
        new BindProcessor(),
        new LocalStringProcessor(),
        new SelectItemsProcessor(),
        new DataModelProcessor(),
        new ActionProcessor(),
        new ActionListenerProcessor(),
        new ConvertProcessor(),
        new FormatProcessor(),
        new ValidateProcessor()
    };

    private static class ScopeProcessor extends AnnotationProcessor {
        ScopeProcessor() {
            super("Scope");
        }

        protected Binding build(ELContext ctx, String name, AnnotatedClosure c, Annotation meta) {
            String scope = meta.getAttribute("value", String.class);
            if (scope == null) {
                return null;
            }

            ValueExpression init = ctx.getVariableMapper().resolveVariable("init_" + name);
            if (!(init instanceof Closure)) {
                init = null;
            }

            if ("client".equals(scope)) {
                return new ClosureStateBinding(name, c.getType(ctx), false, c, (Closure)init);
            } else {
                return new ELScopedStateBinding(name, scope, c, (Closure)init);
            }
        }

        public boolean exclusive() {
            return false;
        }
    }

    private static class BindProcessor extends AnnotationProcessor {
        BindProcessor() {
            super("Bind");
        }

        protected Binding build(ELContext ctx, String name, AnnotatedClosure c, Annotation meta) {
            Class<?> type = c.getType(ctx);
            String id = meta.getAttribute("id", String.class);
            String attribute = meta.getAttribute("attribute", String.class);

            if (type == null)
                type = Object.class;
            if (id == null)
                id = name;
            if (attribute == null)
                attribute = UIComponent.class.isAssignableFrom(type) ? "binding" : "value";

            ELValueBinding b = new ELValueBinding(c, type, id, attribute);
            b.setOrder(meta.getAttribute("order", int.class));
            scanConverterAndValidators(ctx, b, c);
            scanSelectItems(b, name, c);
            return b;
        }

        private void scanConverterAndValidators(ELContext ctx, ELValueBinding b, AnnotatedClosure c) {
            for (Annotation a : c.getAnnotations()) {
                ConverterFactory cf = createConverterFactory(ctx, a);
                if (cf != null) {
                    b.setConverterFactory(cf);
                    b.setConverterMessage(a.getAttribute("message", String.class));
                }

                ValidatorFactory vf = createValidatorFactory(ctx, a);
                if (vf != null) {
                    b.addValidatorFactory(vf);
                }
            }

            Annotation required = c.getAnnotation("Required");
            if (required != null) {
                b.setRequired(true);
                b.setRequiredMessage(required.getAttribute("message", String.class));
            }
        }

        private void scanSelectItems(ELValueBinding b, String name, AnnotatedClosure c) {
            Annotation meta = c.getAnnotation("SelectItems");
            if (meta != null) {
                ELSelectItemsBinding sel = new ELSelectItemsBinding(c, name, b.getType());
                ELBindingBuilder.scanSelectItems(sel, meta);
                b.setSelectItems(sel);
            }
        }
    }

    static ConverterFactory createConverterFactory(ELContext ctx, Annotation a) {
        // Create ConverterFactory from @Converter annotation
        if (a.getAnnotationType().equals("Converter")) {
            String id = a.getAttribute("id", String.class);
            Class type = a.getAttribute("value", Class.class);
            return createConverterFactory(id, type);
        }

        // Create ConverterFactory from annotation that annotated with @Converter
        try {
            ClassResolver cr = ClassResolver.getInstance(ctx);
            Class<?> at = cr.resolveClass(a.getAnnotationType());

            if (at.isAnnotation() && at.isAnnotationPresent(Converter.class)) {
                Converter conv = at.getAnnotation(Converter.class);
                ConverterFactory factory = createConverterFactory(conv.id(), conv.value());

                CustomizingConverterFactory custom = new CustomizingConverterFactory(factory);
                for (Map.Entry<String,Object> e : a.getAttributes().entrySet()) {
                    if (!"message".equals(e.getKey())) {
                        custom.setConverterProperty(e.getKey(), e.getValue());
                    }
                }
                return custom;
            } else {
                return null;
            }
        } catch (ClassNotFoundException ex) {
            return null;
        }
    }

    static ConverterFactory createConverterFactory(String id, Class<?> type) {
        ConverterRegistry registry = ConverterRegistry.getInstance();
        if (id != null && id.length() != 0) {
            ConverterFactory factory = registry.getConverterFactory(id);
            if (factory == null)
                throw new FacesException(_T(JSF_NO_SUCH_CONVERTER_ID, id));
            return factory;
        } else {
            return registry.createConverterFactory(type);
        }
    }

    static ValidatorFactory createValidatorFactory(ELContext ctx, Annotation a) {
        // Create ValidatorFactory from @Validator annotation
        if (a.getAnnotationType().equals("Validator")) {
            String id = a.getAttribute("id", String.class);
            Class<?> type = a.getAttribute("value", Class.class);
            return createValidatorFactory(id, type);
        }

        // Create ValidatorFactory from annotation that annotated with @Validator
        try {
            ClassResolver cr = ClassResolver.getInstance(ctx);
            Class<?> at = cr.resolveClass(a.getAnnotationType());

            if (at.isAnnotation() && at.isAnnotationPresent(Validator.class)) {
                Validator val = at.getAnnotation(Validator.class);
                ValidatorFactory factory = createValidatorFactory(val.id(), val.value());

                CustomizingValidatorFactory custom = new CustomizingValidatorFactory(factory);
                for (Map.Entry<String,Object> e : a.getAttributes().entrySet()) {
                    custom.setValidatorProperty(e.getKey(), e.getValue());
                }
                return custom;
            } else {
                return null;
            }
        } catch (ClassNotFoundException ex) {
            return null;
        }
    }

    static ValidatorFactory createValidatorFactory(String id, Class<?> type) {
        ValidatorRegistry registry = ValidatorRegistry.getInstance();
        if (id != null && id.length() != 0) {
            ValidatorFactory factory = registry.getValidatorFactory(id);
            if (factory == null)
                throw new FacesException(_T(JSF_NO_SUCH_VALIDATOR_ID, id));
            return factory;
        } else {
            return registry.createValidatorFactory(type);
        }
    }

    private static class LocalStringProcessor extends AnnotationProcessor {
        LocalStringProcessor() {
            super("LocalString");
        }

        protected Binding build(ELContext ctx, String name, AnnotatedClosure c, Annotation meta) {
            String key = meta.getAttribute("key", String.class);
            if (key == null) key = name;
            return new ELLocalStringBinding(c, key, c.getType(ctx));
        }
    }

    private static class SelectItemsProcessor extends AnnotationProcessor {
        SelectItemsProcessor() {
            super("SelectItems");
        }

        protected Binding build(ELContext ctx, String name, AnnotatedClosure c, Annotation meta) {
            ELSelectItemsBinding b = new ELSelectItemsBinding(c, name, c.getType(ctx));
            scanSelectItems(b, meta);
            return b;
        }
    }

    static void scanSelectItems(ELSelectItemsBinding b, Annotation meta) {
        Object meta_items = meta.getAttribute("value");

        if (meta_items instanceof List) {
            meta_items = ((List)meta_items).toArray();
        }

        if (meta_items instanceof Object[]) {
            Object[] array = (Object[])meta_items;
            int count = array.length;
            SelectItem[] items = new SelectItem[count];

            for (int i = 0; i < count; i++) {
                if (array[i] instanceof Annotation) {
                    Annotation meta_item = (Annotation)array[i];
                    SelectItem item = new SelectItem();
                    item.setValue(meta_item.getAttribute("value", String.class));
                    item.setLabel(meta_item.getAttribute("label", String.class));
                    item.setDescription(meta_item.getAttribute("description", String.class));
                    item.setDisabled(meta_item.getAttribute("disabled", boolean.class));
                    item.setEscape(meta_item.getAttribute("escape", boolean.class));
                    items[i] = item;
                } else {
                    items[i] = new SelectItem(array[i], null);
                }
            }
            b.setItems(items);
        }

        b.setSource(meta.getAttribute("source"));
        b.setMapValue(meta.getAttribute("mapValue", Closure.class));
        b.setMapLabel(meta.getAttribute("mapLabel", Closure.class));
    }

    private static class DataModelProcessor extends AnnotationProcessor {
        DataModelProcessor() {
            super("DataModel");
        }

        protected Binding build(ELContext ctx, String name, AnnotatedClosure c, Annotation meta) {
            String id = meta.getAttribute("id", String.class);
            Class<?> itemType = meta.getAttribute("itemType", Class.class);
            ValueExpression init = ctx.getVariableMapper().resolveVariable("init_" + name);

            if (itemType == null)
                throw new FacesException(name + ": The itemType must be specified.");
            if (id == null)
                id = name;
            if (!(init instanceof Closure))
                init = null;
            return new ELDataModelBinding(c, id, itemType, (Closure)init);
        }
    }

    private static class ActionProcessor extends AnnotationProcessor {
        ActionProcessor() {
            super("Action");
        }

        protected Binding build(ELContext ctx, String name, AnnotatedClosure c, Annotation meta) {
            ELActionBinding b = createActionBinding(meta, name);
            b.setAction(c);
            return b;
        }
    }

    private static class ActionListenerProcessor extends AnnotationProcessor {
        ActionListenerProcessor() {
            super("ActionListener");
        }

        protected Binding build(ELContext ctx, String name, AnnotatedClosure c, Annotation meta) {
            ELActionBinding b = createActionBinding(meta, name);
            b.setActionListener(c);
            return b;
        }
    }

    static ELActionBinding createActionBinding(Annotation meta, String name) {
        String id = meta.getAttribute("id", String.class);
        String event = meta.getAttribute("event", String.class);
        boolean immediate = meta.getAttribute("immediate", boolean.class);

        if (id == null) {
            if (event == null) {
                // The naming convention for action method is xxx_onyyy, where
                // xxx is the component id and onyyy is the event type.
                int pos = name.indexOf("_on");
                if (pos != -1) {
                    id = name.substring(0, pos);
                    event = name.substring(pos+1);
                } else {
                    id = name;
                    event = null;
                }
            } else {
                id = name;
            }
        }

        return new ELActionBinding(id, event, immediate);
    }

    private static class ConvertProcessor extends AnnotationProcessor {
        ConvertProcessor() {
            super("Convert");
        }

        protected Binding build(ELContext ctx, String name, AnnotatedClosure c, Annotation meta) {
            int arity = c.arity(ctx);
            if (arity != 1 && arity != 2 && arity != 3) {
                throw new FacesException(_T(MVB_INVALID_CONVERT_METHOD, name));
            }

            String[] ids = getIds(meta.getAttribute("id"), name, "convert");
            if (ids != null) {
                ELConverterBinding b = new ELConverterBinding(ids);
                b.setConvertClosure(c);
                return b;
            }
            return null;
        }
    }

    private static class FormatProcessor extends AnnotationProcessor {
        FormatProcessor() {
            super("Format");
        }

        protected Binding build(ELContext ctx, String name, AnnotatedClosure c, Annotation meta) {
            int arity = c.arity(ctx);
            if (arity != 1 && arity != 2 && arity != 3) {
                throw new FacesException(_T(MVB_INVALID_FORMAT_METHOD, name));
            }

            String[] ids = getIds(meta.getAttribute("id"), name, "format");
            if (ids != null) {
                ELConverterBinding b = new ELConverterBinding(ids);
                b.setFormatClosure(c);
                return b;
            }
            return null;
        }
    }

    private static class ValidateProcessor extends AnnotationProcessor {
        ValidateProcessor() {
            super("Validate");
        }

        protected Binding build(ELContext ctx, String name, AnnotatedClosure c, Annotation meta) {
            int arity = c.arity(ctx);
            if (arity != 1 && arity != 2 && arity != 3) {
                throw new FacesException(_T(MVB_INVALID_VALIDATE_METHOD, name));
            }

            String[] ids = getIds(meta.getAttribute("id"), name, "validate");
            return ids == null ? null : new ELValidatorBinding(ids, c);
        }
    }

    static String[] getIds(Object att, String name, String prefix) {
        String[] ids = null;

        if (att != null) {
            if (att instanceof String) {
                ids = new String[] {(String)att};
            } else if (att instanceof Collection) {
                Object[] a = ((Collection)att).toArray();
                ids = new String[a.length];
                System.arraycopy(a, 0, ids, 0, a.length);
            } else if (att instanceof Object[]) {
                Object[] a = (Object[])att;
                ids = new String[a.length];
                System.arraycopy(a, 0, ids, 0, a.length);
            }
        } else if (name.startsWith(prefix)) {
            name = name.substring(prefix.length());
            if (name.length() == 0) {
                return null;
            } else if (name.charAt(0) == '_') {
                name = name.substring(1);
            } else {
                name = Character.toLowerCase(name.charAt(0)) + name.substring(1);
            }
            ids = new String[] {name};
        }
        
        return ids;
    }
}
