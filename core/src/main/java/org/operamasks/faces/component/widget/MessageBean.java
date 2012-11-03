/*
 * $Id: MessageBean.java,v 1.3 2007/07/02 07:37:43 jacky Exp $
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
package org.operamasks.faces.component.widget;

import java.util.ArrayList;
import java.util.List;

public class MessageBean {
	private String title;
	private String content;
	
	public MessageBean(String title, String content) {
		this.title = title;
		this.content = content;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
	public static List<MessageBean> createMessage(String title, String content) {
		List<MessageBean> messages = new ArrayList<MessageBean>();
		messages.add(new MessageBean(title, content));
		
		return messages;
	}
}
