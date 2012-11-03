/*
 * $Id: DynamicNodesTreeBean.java,v 1.4 2007/12/19 07:44:49 yangdong Exp $
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

import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;

import org.operamasks.faces.annotation.ManagedBean;
import org.operamasks.faces.annotation.ManagedBeanScope;
import org.operamasks.faces.annotation.ManagedProperty;
import org.operamasks.faces.component.widget.UITree;
import org.operamasks.faces.component.widget.UITreeNode;
import org.operamasks.faces.component.widget.tree.TreeNodeSeeker;
import org.operamasks.faces.component.widget.tree.event.TreeEvent;
import org.operamasks.faces.event.TreeEventListener;

@ManagedBean(name="DynamicNodesTree", scope=ManagedBeanScope.REQUEST)
public class DynamicNodesTreeBean implements TreeEventListener {
	private static final long serialVersionUID = -6233290261631595653L;
	
	protected String response;
	protected WindowsExplorer windowsExplorer;
	protected UITree tree;

	public UITree getTree() {
		return tree;
	}

	public void setTree(UITree tree) {
		this.tree = tree;
	}

	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
	}
	
	public List<File> getFilesInFolder(Object userData) {
		if (userData instanceof String)
			return windowsExplorer.getFiles(new File((String)userData, true));
		
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
		response = (event.getAffectedNode()).getUserData() + ", " + event.getEventType();
	}
	
	public void initTree(FacesContext context, UITree tree) {
		tree.loadAllAsyncNodes();
	}
	
	public void expandAll() {
		tree.expandAll();
	}
	
	public void collapseAll() {
		tree.collapseAll();
	}
	
	public void expandToSong1() {
		UITreeNode treeNode = tree.findTreeNode(tree.getRootNode(), "Song 1.wma", new TreeNodeSeeker() {
			public boolean isMatch(UITreeNode treeNode, Object identifier) {
				Object userData = treeNode.getUserData();
				if (userData instanceof String)
					return false;
				
				return identifier.equals(((File)treeNode.getUserData()).getName());
			}
			
		}, true);
		
		treeNode.expandTo();
		treeNode.select();
	}
}