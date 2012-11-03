/*
 * $Id: MessageBoxEvent.java,v 1.1 2008/03/19 02:27:03 jacky Exp $
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

import java.util.EventObject;

import org.operamasks.faces.component.layout.impl.UIWindow;

@SuppressWarnings("serial")
public class MessageBoxEvent extends EventObject
{

	private UIWindow source;
	private String messageboxId;

	public MessageBoxEvent(UIWindow source, Button button) {
		super(source);
		this.source = source;
		this.button = button;
	}
	
	private Button button;

	public Button getButton() {
		return button;
	}
	@Override
	public UIWindow getSource() {
		return this.source;
	}
	public String getMessageboxId() {
		return messageboxId;
	}
	public void setMessageboxId(String messageboxId) {
		this.messageboxId = messageboxId;
	}
}
