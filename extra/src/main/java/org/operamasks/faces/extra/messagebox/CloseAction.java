/*
 * $Id: CloseAction.java,v 1.3 2008/04/19 11:50:11 jacky Exp $
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

import javax.faces.component.UIComponent;
import javax.faces.event.AbortProcessingException;

import org.operamasks.faces.component.action.ActionEvent;
import org.operamasks.faces.component.layout.impl.UIWindow;

public class CloseAction extends MessageBoxAction
{
	public CloseAction() {
		super(Button.CANCEL);
	}
	@Override
	public void processAction(ActionEvent event) throws AbortProcessingException{
		UIWindow target = findTarget((UIComponent)event.getSource());
		if (target != null) {
			target.close();
		}
	}

}
