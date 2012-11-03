package org.operamasks.faces.component.action;

import java.beans.PropertyChangeListener;
import java.util.Collection;

import javax.faces.event.AbortProcessingException;

/**
 * Action用于设置给实现了ActionSupport接口的组件，可以通过Action来控制组件行为的变化。<p>
 * Action的自身不负责维护状态，Action本身由其所绑定的组件维护，组件持有的是Action引用的EL。<p>
 * 
 * @see ActionSupport
 */
public interface Action 
{
	/**
	 * 得到所有属性名
	 */
	public Collection<String> getAttributesKey();
	
	/**
	 * 设置Action的属性值，由Action设置的值将会直接作用在组件上
	 */
    public void setAttribute(String key, Object value);
    public Object getAttribute(String key);
	
    /**
     * 设置Action的状态，当enable==false时，将不触发行为。
     */
    public void setEnabled(boolean enabled);
    public boolean isEnabled();
    
    /**
     * 是否只执行ProcessValidators生命周期
     */
    public void setImmediate(boolean immediate);
    public boolean isImmediate();


    /**
     * 属性变化监听器，可自行扩展
     */
	public void addPropertyChangeListener(PropertyChangeListener listener);
    public void removePropertyChangeListener(PropertyChangeListener listener);
    public void clearPropertyChangeListener();

    /**
     * 组件发生默认操作会调用此方法
     */
    public void processAction(ActionEvent event) throws AbortProcessingException;
}