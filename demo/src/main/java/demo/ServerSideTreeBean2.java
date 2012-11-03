/*
 * $Id: ServerSideTreeBean2.java,v 1.2 2007/12/11 04:14:14 jacky Exp $
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

import javax.faces.event.AbortProcessingException;
import javax.faces.model.SelectItem;

import org.operamasks.faces.annotation.ManagedBean;
import org.operamasks.faces.annotation.ManagedBeanScope;
import org.operamasks.faces.component.widget.UITree;
import org.operamasks.faces.component.widget.UITreeNode;
import org.operamasks.faces.component.widget.tree.event.TreeEvent;
import org.operamasks.faces.event.TreeEventListener;

@SuppressWarnings("serial")
@ManagedBean(name="ServerSideTree2", scope=ManagedBeanScope.REQUEST)
public class ServerSideTreeBean2 implements TreeEventListener {
	private UITree tree;
	private String response;
	private String nodeText;
	private String nodeImage;
	
    private SelectItem[] allImages = {
    		new SelectItem("images/my_documents.gif", "My Documents"),
            new SelectItem("images/document_music.gif", "WMA format file"),
            new SelectItem("images/document_gif.gif", "GIF format file"),
            new SelectItem("images/document_jpg.gif", "JPEG format file"),
            new SelectItem("images/document_text.gif", "Text format file"),
            new SelectItem("images/document_word.gif", "Word format file"),
            new SelectItem("images/document_excel.gif", "Excel format file")
    };

	public UITree getTree() {
		return tree;
	}

	public void setTree(UITree tree) {
		this.tree = tree;
	}

	public synchronized void add() {
		UITreeNode selected = getSelectedTreeNode();
		if (selected == null) {
			response = "Please choose a node to add to";
			return;
		}
		
		if (nodeText == null || "".equals(nodeText)) {
			response = "Node text mustn't be a null string";
			
			return;
		}
		
		UITreeNode child = tree.createTreeNode();
		child.setUserData(getChildUserData(selected));
		
		if (nodeText != null && !nodeText.equals(""))
			child.setText(nodeText);
		else
			child.setText(child.getUserData().toString());
		
		if (nodeImage != null && !nodeImage.equals(""))
			child.setImage(nodeImage);
		else
			child.setImage("images/document_text.gif");
		
		selected.getChildren().add(child);
		
		if (!selected.isExpand())
			selected.expand();
		
		selected.setImage(getMyDocumentsImage());
	}
	
	private String getMyDocumentsImage() {
		return "images/my_documents.gif";
	}

	private UITreeNode getSelectedTreeNode() {
		return tree.getSelectedNode();
	}

	private Object getChildUserData(UITreeNode parent) {
		int serialNumber = parent.getChildCount();
		
		while (parent.findChildByUserData(parent.getUserData() + "_" + serialNumber) != null) {
			serialNumber++;
		}
		
		return parent.getUserData() + "_" + serialNumber;
	}

	public synchronized void remove() {
		UITreeNode selected = getSelectedTreeNode();
		
		if (selected == null) {
			response = "Please choose a node to remove";
			return;
		}
		
		if (selected.getParent() instanceof UITree) {
			response = "Can't delete root node";
		} else {
			UITreeNode parent = (UITreeNode)selected.getParent();
			parent.getChildren().remove(selected);
		}
		
		
	}
	
	public synchronized void update() {
		UITreeNode selected = getSelectedTreeNode();
		
		if (selected  == null) {
			response = "Please choose a node to update";
			return;
		}
		
		if (selected.getParent() instanceof UITree) {
			response = "Can't update root node";
			
			return;
		}
		
		if (nodeText == null || "".equals(nodeText)) {
			response = "Node text mustn't be a null string";
			
			return;
		}
		
		if (selected.getChildCount() > 0 && !nodeImage.equals(getMyDocumentsImage())) {
			response = "Directory's image must be 'My Documents'";
			
			return;
		}
		
		selected.setText(nodeText);
		
		if (nodeImage != null)
			selected.setImage(nodeImage);
	}

	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
	}

	public String getNodeImage() {
		return nodeImage;
	}

	public void setNodeImage(String nodeImage) {
		this.nodeImage = nodeImage;
	}

	public String getNodeText() {
		return nodeText;
	}

	public void setNodeText(String nodeText) {
		this.nodeText = nodeText;
	}

	public SelectItem[] getAllImages() {
		return allImages;
	}

	public void processEvent(TreeEvent event) throws AbortProcessingException {
		if (event.getEventType().equals(UITreeNode.SELECT)) {
			nodeText = event.getAffectedNode().getText();
			nodeImage = event.getAffectedNode().getImage();
		}
	}
}
