package org.operamasks.faces.component.action;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.faces.event.AbortProcessingException;

/**
 * Action默认实现
 *
 */
public abstract class AbstractAction implements Action, Serializable
{
	private Map<String, Object> attrs;
	private boolean isEnabled;
	private boolean immediate;
	private transient PropertyChangeSupport changeSupport;
	
	public AbstractAction() {
		this.attrs = new HashMap<String, Object>();
		this.isEnabled = true;
	}
	
	public abstract void processAction(ActionEvent event) throws AbortProcessingException;

	public Collection<String> getAttributesKey() {
		return Collections.unmodifiableCollection(this.attrs.keySet());
	}

	public void setAttribute(String key, Object value) {
		Object oldValue = null;
		if (attrs.containsKey(key))
		    oldValue = attrs.get(key);
		if (value == null) {
			attrs.remove(key);
		} else {
			attrs.put(key,value);
		}
		firePropertyChange(key, oldValue, value);
	}

	public void setEnabled(boolean enabled) {
		firePropertyChange("disabled", !this.isEnabled, !enabled);
		this.isEnabled = enabled;
	}

	public void setImmediate(boolean immediate) {
		firePropertyChange("immediate", this.immediate, immediate);
		this.immediate = immediate;
	}
	
	public Object getAttribute(String key) {
		return this.attrs.get(key);
	}

	public boolean isEnabled() {
		return this.isEnabled;
	}

	public boolean isImmediate() {
		return this.immediate;
	}

	protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
        if (changeSupport == null || 
	    (oldValue != null && newValue != null && oldValue.equals(newValue))) {
            return;
        }
        changeSupport.firePropertyChange(propertyName, oldValue, newValue);
    }

	public synchronized void addPropertyChangeListener(PropertyChangeListener listener) {
        if (changeSupport == null) {
    	    changeSupport = new PropertyChangeSupport(this);
        }
        changeSupport.addPropertyChangeListener(listener);
	}

	public synchronized void removePropertyChangeListener(PropertyChangeListener listener) {
        if (changeSupport == null) {
            return;
        }
        changeSupport.removePropertyChangeListener(listener);
	}
	
	public void clearPropertyChangeListener() {
        if (changeSupport == null || changeSupport.getPropertyChangeListeners() == null) {
            return;
        }
        
		for (PropertyChangeListener pcl : changeSupport.getPropertyChangeListeners()) {
			changeSupport.removePropertyChangeListener(pcl);
		}
	}

}
