package demo.binding;

import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;

import org.operamasks.faces.annotation.AsyncTreeMethod;
import org.operamasks.faces.annotation.AsyncTreeMethodType;
import org.operamasks.faces.annotation.Bind;
import org.operamasks.faces.annotation.ManagedBean;
import org.operamasks.faces.annotation.ManagedBeanScope;
import org.operamasks.faces.annotation.ManagedProperty;
import org.operamasks.faces.annotation.TreeEventListener;
import org.operamasks.faces.component.widget.UICheckTreeNode;
import org.operamasks.faces.component.widget.UITree;
import org.operamasks.faces.component.widget.UITreeNode;
import org.operamasks.faces.component.widget.tree.event.CheckStateChange;
import org.operamasks.faces.component.widget.tree.event.CheckStateChangedEvent;
import org.operamasks.faces.component.widget.tree.event.TreeEvent;

import demo.File;
import demo.WindowsExplorer;

@SuppressWarnings("serial")
@ManagedBean(name="CheckNodesTree2Bean", scope=ManagedBeanScope.REQUEST)
public class CheckNodesTree2Bean {
	@Bind
	protected String response;
	protected WindowsExplorer windowsExplorer;
	@Bind
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
	
	@AsyncTreeMethod(id="tree", value=AsyncTreeMethodType.asyncData)
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
	
	@AsyncTreeMethod(id="tree", value=AsyncTreeMethodType.nodeImage)
	public String getFileIcon(Object userData) {
		return windowsExplorer.getIcon((File)userData);
	}
	
	@AsyncTreeMethod(id="tree", value=AsyncTreeMethodType.nodeHasChildren)
	public boolean isFolder(Object userData) {
		return ((File)userData).isFolder();
	}
	
	@AsyncTreeMethod(id="tree", value=AsyncTreeMethodType.nodeText)
	public String getFileName(Object userData) {
		String name = ((File)userData).getName();
		int lastDotIndex = name.lastIndexOf(".");
		
		if (lastDotIndex == -1)
			return name;
		
		return name.substring(0, lastDotIndex);
	}
	
	@AsyncTreeMethod
	public void tree_initAction(FacesContext context, UITree tree) {
		tree.loadAllAsyncNodes();
	}
	
	@AsyncTreeMethod(id="tree")
	public Class<? extends UITreeNode> nodeClass(Object bizObject) {
		return UICheckTreeNode.class;
	}
	
	@TreeEventListener(id="tree", events="select,expand,collapse,check,checkStateChanged")
	public void processEvent(TreeEvent event) throws AbortProcessingException {
		if (event instanceof CheckStateChangedEvent) {
			System.out.print("Changed nodes: ");
			CheckStateChangedEvent changedEvent = (CheckStateChangedEvent)event;
			for (int i = 0; i < changedEvent.getCheckStateChanges().size(); i++) {
				if (i != 0)
					System.out.print(", ");
				
				CheckStateChange change = changedEvent.getCheckStateChanges().get(i);
				System.out.print(change.getChangedNode().getText() + "(" + change.getOldCheckType() +
						"->" + change.getChangedNode().getCheckType() + ")");
			}
			System.out.println();
		} else {
			response = "Event: " + (event.getAffectedNode()).getUserData() + ", ";
			
			if (event.getEventType() == UICheckTreeNode.CHECK)
				response += (((UICheckTreeNode)event.getAffectedNode()).isChecked() ? "check" : "uncheck");
			else
				response += event.getEventType();
		
			UICheckTreeNode[] checkedNodes = ((UICheckTreeNode)tree.getRootNode()).getCheckedNodes();
		
			if (checkedNodes.length > 0) {
				response += "<br/>Checked nodes: ";
				for (int i = 0; i < checkedNodes.length; i++) {
					if (i != 0)
						response += ", ";
				
					response += checkedNodes[i].getUserData();
				}
			}
		}
	}
	
	@AsyncTreeMethod
	public void tree_postCreate(UITreeNode treeNode, Object bizObject) {
		String fileName;
		
		if (bizObject instanceof String)
			fileName = (String)bizObject;
		else
			fileName = ((File)bizObject).getName();
		
		if ("My Music".equals(fileName) || fileName.endsWith(".wma"))
			((UICheckTreeNode)treeNode).check();
	}
}
