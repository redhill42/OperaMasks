package org.operamasks.faces.binding.impl;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.el.ELContext;
import javax.el.ValueExpression;
import javax.faces.component.EditableValueHolder;
import javax.faces.component.UIComponent;
import javax.faces.component.ValueHolder;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseId;

import org.operamasks.faces.binding.ModelBean;
import org.operamasks.faces.binding.ModelBindingContext;
import org.operamasks.faces.util.FacesUtils;

class AttributesBinding extends ValueBinding
{
	AttributesBinding(String viewId) {
		super(viewId);
	}

	@Override
	public void apply(FacesContext ctx, ModelBindingContext mbc) {
        UIComponent comp = mbc.getComponent(getId());
        if (comp == null) {
            return;
        }

        ModelBean bean = mbc.getModelBean();
	    Object obj = getModelValue(bean);
	    
	    if (obj == null) {
	    	return;
	    }
	    applyValue(ctx, mbc, obj, comp);
	}
	
	@SuppressWarnings("unchecked")
	private void applyValue(FacesContext ctx, ModelBindingContext mbc, Object obj, UIComponent comp) {
		List<String> keys = new ArrayList<String>();;
	    if (obj instanceof Map) {
	    	Map<String, Object> attrs = (Map)obj;
	    	keys.addAll(attrs.keySet());
	    } else {
		    // fixme, should cache?
	        PropertyDescriptor[] pds = null;
	        try {
	            pds = Introspector.getBeanInfo(obj.getClass()).
	                getPropertyDescriptors();
	        } catch (IntrospectionException e) {
	            // do nothing
	        }
	        if (pds == null) {
	            return;
	        }
	        for (PropertyDescriptor pd : pds) {
				keys.add(pd.getName());
	        }
	    }
	    
	    PhaseId phaseId = mbc.getPhaseId();
	    ModelBean bean = mbc.getModelBean();
	    for (String key : keys) {
	        ValueExpression previous = comp.getValueExpression(key);
	        if (previous instanceof CompositeDataItemAdapter) {
	            return; // a data item already bound
	        }
	        if ((previous == null) || (previous instanceof CompositeValueAdapter)) {
	            CompositeValueAdapter adapter;
	            if ((previous == null) || (phaseId != ((CompositeValueAdapter)previous).getPhaseId())) {
	                adapter = new CompositeValueAdapter();
	                adapter.setPhaseId(phaseId);
	                comp.setValueExpression(key, adapter);
	            } else {
	                adapter = (CompositeValueAdapter)previous;
	            }

	            String expression = "#{this."+key+"}";
	            ValueExpression binding = FacesUtils.createValueExpression(obj, expression, Object.class);
	            adapter.addValueBinding(binding);

	            ELContext elctx = ctx.getELContext();
	            if ("binding".equals(key)) {
	                UIComponent oldValue = (UIComponent)binding.getValue(elctx);
	                if (oldValue != null && oldValue != comp) {
	                    comp.restoreState(ctx, oldValue.saveState(ctx));
	                }
	                binding.setValue(elctx, comp);
	            }
	        }
	        if ("value".equals(key)) {
	            if (comp instanceof ValueHolder) {
	                applyConverter(bean, (ValueHolder)comp);
	                if (comp instanceof EditableValueHolder) {
	                    applyValidators(bean, phaseId, (EditableValueHolder)comp);
	                }
	            }
	        }
	    }

	}
}
