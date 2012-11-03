package org.operamasks.faces.component.action;

/**
 * 实现了该接口的组件类，可以由其绑定的Action来控制组件的行为，组件发生的默认操作也会委派给相应的Action来处理。
 *
 */
public interface ActionSupport 
{
	public void setActionBinding(Action action);
	public Action getActionBinding();
}
