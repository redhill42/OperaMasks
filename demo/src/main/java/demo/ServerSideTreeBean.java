/*
 * $$Id: ServerSideTreeBean.java,v 1.2 2007/12/11 04:14:14 jacky Exp $$
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
 *
 */
 
package demo;

import java.util.List;

import javax.faces.event.AbortProcessingException;

import org.operamasks.faces.annotation.ManagedBean;
import org.operamasks.faces.annotation.ManagedBeanScope;
import org.operamasks.faces.annotation.ManagedProperty;
import org.operamasks.faces.component.widget.UITree;
import org.operamasks.faces.component.widget.UITreeNode;
import org.operamasks.faces.component.widget.tree.event.TreeEvent;
import org.operamasks.faces.event.TreeEventListener;

@ManagedBean(name="ServerSideTree", scope=ManagedBeanScope.REQUEST)
public class ServerSideTreeBean implements TreeEventListener {
	private static final long serialVersionUID = -6233290261631595653L;
	
	private WindowsExplorer windowsExplorer;
	private UITree tree;
	
	public List<File> getFilesInFolder(Object userData) {
		return windowsExplorer.getFiles((File)userData);
	}

	@ManagedProperty("#{WindowsExplorer}")
	public WindowsExplorer getWindowsExplorer() {
		return windowsExplorer;
	}

	public void setWindowsExplorer(WindowsExplorer windowsExplorer) {
		this.windowsExplorer = windowsExplorer;
	}
	
	public String getFileIcon(Object userData) {
		return windowsExplorer.getIcon((File)userData);
	}
	
	public boolean isFolder(Object userData) {
		return ((File)userData).isFolder();
	}
	
	public String getFileName(Object userData) {
		String name = ((File)userData).getName();
		int lastDotIndex = name.lastIndexOf(".");
		
		if (lastDotIndex == -1)
			return name;
		
		return name.substring(0, lastDotIndex);
	}
	
	public void processEvent(TreeEvent event) throws AbortProcessingException {
		UITreeNode serverControlNode = tree.findTreeNodeByUserData(
				event.getAffectedNode().getUserData());
		
		if (event.getEventType().equals(UITreeNode.EXPAND))
			serverControlNode.expand();
		else if (event.getEventType().equals(UITreeNode.COLLAPSE))
			serverControlNode.collapse();
		else if (event.getEventType().equals(UITreeNode.SELECT))
			serverControlNode.select();
	}

	public UITree getTree() {
		return tree;
	}

	public void setTree(UITree tree) {
		this.tree = tree;
	}
}