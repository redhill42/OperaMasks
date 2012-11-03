/*
 * $Id: MessageBox.java,v 1.3 2008/03/19 05:08:00 lishaochuan Exp $
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

package org.operamasks.faces.extra.messagebox;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.component.html.HtmlPanelGrid;
import javax.faces.context.FacesContext;

import org.operamasks.faces.annotation.Accessible;
import org.operamasks.faces.annotation.LocalString;
import org.operamasks.faces.annotation.ManagedBean;
import org.operamasks.faces.annotation.ManagedBeanScope;
import org.operamasks.faces.annotation.ManagedProperty;
import org.operamasks.faces.component.ajax.AjaxUpdater;
import org.operamasks.faces.component.layout.impl.UIWindow;
import org.operamasks.faces.component.widget.UIButton;
import org.operamasks.faces.render.resource.ResourceManager;

@SuppressWarnings("unused")
@ManagedBean(name="org.operamasks.faces.extra.messagebox.MessageBox", scope=ManagedBeanScope.SESSION)
public class MessageBox 
{
	public final static String ERROR    = "messagebox/icon-error.gif";
	public final static String INFO     = "messagebox/icon-info.gif";
	public final static String QUESTION = "messagebox/icon-question.gif";
	public final static String WARNING  = "messagebox/icon-warning.gif";
	
	public final static String MESSAGE_BOX_EVENT  = "message_box_event";

	@ManagedProperty
	private UIWindow messageBox;
	
	private Map<MessageBoxType, MessageBoxHandler> handlers;
	private MessageBoxType lastType;
	
	public MessageBox() {
		handlers = new HashMap<MessageBoxType, MessageBoxHandler>();
		this.modal = true;
		registMessageBoxType();
	}
	
	public void showMessageBox(String id, String text) {
		showMessageBox(id, MessageBoxType.ALERT, "", text, INFO, true);
	}

	public void showMessageBox(String id, String title, String text) {
		showMessageBox(id, MessageBoxType.ALERT, title, text, INFO, true);
	}

	public void showMessageBox(String id, String title, String text, String icon) {
		showMessageBox(id, MessageBoxType.ALERT, title, text, icon, true);
	}

	public void showMessageBox(String id, MessageBoxType type, String title, String text) {
		showMessageBox(id, type, title, text, INFO, true);
	}

	public void showMessageBox(String id, MessageBoxType type, String title, String text, String icon) {
		showMessageBox(id, type, title, text, icon, true);
	}

	public void showMessageBox(String id, MessageBoxType type, String title, String text, String icon, boolean modal) {
		this.title    = title;
		this.text     = text;
		this.icon     = icon;
		this.modal    = modal;
		if (isKnownIcon(icon)) {
			FacesContext context = FacesContext.getCurrentInstance();
			ResourceManager rm = ResourceManager.getInstance(context);
			this.icon = rm.getResourceURL(icon);
			this.icon = this.icon.substring(1);
			this.icon = this.icon.substring(this.icon.indexOf('/'));
		}
		MessageBoxHandler handler = handlers.get(type);
		handler.showMessageBox(id, type);
		this.lastType = type;
	}
	
	private boolean isKnownIcon(String icon) {
		return ERROR.equals(icon) || INFO.equals(icon) || QUESTION.equals(icon) || WARNING.equals(icon);
	}

	@Accessible	private HtmlPanelGrid buttonContainer;
	@Accessible	private String text;
	@Accessible private String icon;
	@Accessible private String title;
	@LocalString private Map<String,String> resources;
	@Accessible private AjaxUpdater updater;
	@Accessible private boolean modal;

	private void registMessageBoxType() {
		this.handlers.put(MessageBoxType.YESNO, new YesNoMessageBox());
		this.handlers.put(MessageBoxType.OKCANCEL, new OkCancelMessageBox());
		this.handlers.put(MessageBoxType.YESNOCANCEL, new YesNoCancelMessageBox());
	}
	abstract class MessageBoxHandler {
		void showMessageBox(String id, MessageBoxType type) {
			createMessageBox(id);
			messageBox.setTitle(title);
			messageBox.setModal(modal);
			updater.getChildren().clear();
			updater.getChildren().add(buttonContainer);
			updater.reload();
			messageBox.show();
		}
		abstract void createMessageBox(String id);
	}
	
	class YesNoMessageBox extends MessageBoxHandler {
		@Override
		void createMessageBox(String id) {
			UIButton yesButton = new UIButton();
			MessageBoxAction yesAction = new MessageBoxAction(Button.YES);
			yesAction.setMessageBoxId(id);
			yesAction.setAttribute("value", resources.get("yes.label"));
			yesButton.setActionBinding(yesAction);

			UIButton noButton = new UIButton();
			MessageBoxAction noAction = new MessageBoxAction(Button.NO);
			noAction.setMessageBoxId(id);
			noAction.setAttribute("value", resources.get("no.label"));
			noButton.setActionBinding(noAction);
			
			buttonContainer.getChildren().clear();
			buttonContainer.getChildren().add(yesButton);
			buttonContainer.getChildren().add(noButton);
		}
	}

	class OkCancelMessageBox extends MessageBoxHandler {
		@Override
		void createMessageBox(String id) {
			UIButton okButton = new UIButton();
			MessageBoxAction okAction = new MessageBoxAction(Button.OK);
			okAction.setMessageBoxId(id);
			okAction.setAttribute("value", resources.get("ok.label"));
			okButton.setActionBinding(okAction);

			UIButton cancelButton = new UIButton();
			MessageBoxAction cancelAction = new MessageBoxAction(Button.CANCEL);
			cancelAction.setMessageBoxId(id);
			cancelAction.setAttribute("value", resources.get("cancel.label"));
			cancelButton.setActionBinding(cancelAction);
			buttonContainer.getChildren().clear();
			buttonContainer.getChildren().add(okButton);
			buttonContainer.getChildren().add(cancelButton);
		}
	}
	
	class YesNoCancelMessageBox extends MessageBoxHandler {
	    @Override
        void createMessageBox(String id) {
            UIButton yesButton = new UIButton();
            MessageBoxAction yesAction = new MessageBoxAction(Button.YES);
            yesAction.setMessageBoxId(id);
            yesAction.setAttribute("value", resources.get("yes.label"));
            yesButton.setActionBinding(yesAction);
            
            UIButton noButton = new UIButton();
            MessageBoxAction noAction = new MessageBoxAction(Button.NO);
            noAction.setMessageBoxId(id);
            noAction.setAttribute("value", resources.get("no.label"));
            noButton.setActionBinding(noAction);

            UIButton cancelButton = new UIButton();
            MessageBoxAction cancelAction = new MessageBoxAction(Button.CANCEL);
            cancelAction.setMessageBoxId(id);
            cancelAction.setAttribute("value", resources.get("cancel.label"));
            cancelButton.setActionBinding(cancelAction);
            
            buttonContainer.getChildren().clear();
            buttonContainer.getChildren().add(yesButton);
            buttonContainer.getChildren().add(noButton);
            buttonContainer.getChildren().add(cancelButton);
        }
    }
}
