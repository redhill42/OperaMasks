/*
 * $Id: MessageBoxBean.java,v 1.3 2008/03/19 05:08:00 lishaochuan Exp $
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

package demo;

import org.operamasks.faces.annotation.Action;
import org.operamasks.faces.annotation.Bind;
import org.operamasks.faces.annotation.EventListener;
import org.operamasks.faces.annotation.ManagedBean;
import org.operamasks.faces.annotation.ManagedBeanScope;
import org.operamasks.faces.annotation.ManagedProperty;
import org.operamasks.faces.extra.messagebox.MessageBox;
import org.operamasks.faces.extra.messagebox.MessageBoxEvent;
import org.operamasks.faces.extra.messagebox.MessageBoxType;

@ManagedBean(scope=ManagedBeanScope.SESSION)
public class MessageBoxBean 
{
	@ManagedProperty
	private MessageBox messagebox;

	@Bind
	private String okCancelText;
	@Bind
	private String yesNoText;
	@Bind
    private String yesNoCancelText;
	
	@Action
	public void okCancel() {
		messagebox.showMessageBox("okCancelBox", MessageBoxType.OKCANCEL, "提示", "确认吗？", MessageBox.QUESTION, false);
	}
	
	@Action
	public void yesNo() {
		messagebox.showMessageBox("yesNoBox", MessageBoxType.YESNO, "提示", "是否进入下一步？");
	}
	
	@Action
	public void yesNoCancel(){
	    messagebox.showMessageBox("yesNoCancel", MessageBoxType.YESNOCANCEL, "提示", "是否保存？");
	}

	@EventListener(MessageBox.MESSAGE_BOX_EVENT)
	public void processAction(MessageBoxEvent event) {
		if ("yesNoBox".equals(event.getMessageboxId())) {
			this.yesNoText = "click button: " + event.getButton();
		} else if ("okCancelBox".equals(event.getMessageboxId())) {
			this.okCancelText = "click button: " + event.getButton();
		} else if ("yesNoCancel".equals(event.getMessageboxId())) {
            this.yesNoCancelText = "click button: " + event.getButton();
        }

		event.getSource().close();
	}
}
