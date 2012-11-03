package demo;

import javax.faces.event.AbortProcessingException;

import org.operamasks.faces.annotation.ManagedBean;
import org.operamasks.faces.annotation.ManagedBeanScope;
import org.operamasks.faces.component.widget.UICheckTreeNode;
import org.operamasks.faces.component.widget.UITreeNode;
import org.operamasks.faces.component.widget.tree.event.CheckStateChange;
import org.operamasks.faces.component.widget.tree.event.CheckStateChangedEvent;
import org.operamasks.faces.component.widget.tree.event.TreeEvent;

import demo.DynamicNodesTreeBean;

@SuppressWarnings("serial")
@ManagedBean(name="CheckNodesTree", scope=ManagedBeanScope.REQUEST)
public class CheckNodesTreeBean extends DynamicNodesTreeBean {
	public Class<? extends UITreeNode> nodeClass(Object bizObject) {
		return UICheckTreeNode.class;
	}
	
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
	
	public void restoreCheckStatus(UITreeNode treeNode, Object bizObject) {
		String fileName;
		
		if (bizObject instanceof String)
			fileName = (String)bizObject;
		else
			fileName = ((File)bizObject).getName();
		
		if ("My Music".equals(fileName) || fileName.endsWith(".wma"))
			((UICheckTreeNode)treeNode).check();
	}
}
