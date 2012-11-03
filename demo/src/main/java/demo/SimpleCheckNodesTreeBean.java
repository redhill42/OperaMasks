package demo;

import javax.faces.event.AbortProcessingException;

import org.operamasks.faces.annotation.ManagedBean;
import org.operamasks.faces.annotation.ManagedBeanScope;
import org.operamasks.faces.component.widget.UISimpleCheckTreeNode;
import org.operamasks.faces.component.widget.UITreeNode;
import org.operamasks.faces.component.widget.tree.event.TreeEvent;

@SuppressWarnings("serial")
@ManagedBean(name="SimpleCheckNodesTree", scope=ManagedBeanScope.REQUEST)
public class SimpleCheckNodesTreeBean extends DynamicNodesTreeBean {
	public Class<? extends UITreeNode> nodeClass(Object bizObject) {
		return UISimpleCheckTreeNode.class;
	}
	
	public void processEvent(TreeEvent event) throws AbortProcessingException {
		response = "Event: " + (event.getAffectedNode()).getUserData() + ", ";
			
		if (event.getEventType() == UISimpleCheckTreeNode.CHECK)
			response += (((UISimpleCheckTreeNode)event.getAffectedNode()).isChecked() ? "check" : "uncheck");
		else
			response += event.getEventType();
		
		UISimpleCheckTreeNode[] checkedNodes = ((UISimpleCheckTreeNode)tree.getRootNode(
				)).getCheckedSimpleCheckNodes();
		
		if (checkedNodes.length > 0) {
			response += "<br/>Checked nodes: ";
			for (int i = 0; i < checkedNodes.length; i++) {
				if (i != 0)
					response += ", ";
				
				response += checkedNodes[i].getUserData();
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
			((UISimpleCheckTreeNode)treeNode).check();
	}
}
