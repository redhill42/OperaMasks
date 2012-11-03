//计算tree所有隐藏域的值
//event:事件名
//node:触发事件的节点
//treeClientId:树的客户端ID
//tree:树的客户端变量名
function buildEventParams(event, node, treeClientId, tree){
	var seperator = ",";
	var rootNode = tree.getRootNode();
	document.getElementById(treeClientId + "_eventName").value = event;
	document.getElementById(treeClientId + "_eventNode").value = node.id;
	var source = "";
	if(rootNode.getUI().checked && rootNode.getUI().checked() == "checked"){
		source += rootNode.id + seperator;
	}
	source += getCheckNodes(tree.getRootNode(), seperator);
	document.getElementById(treeClientId + "_checkedNodes").value = source;
	
	if(event == "select"){
		document.getElementById(treeClientId + "_selectedNode").value = node.id;
	}
	if(event == "collapsenode" || event == "expandnode"){
		var source = "";
		if(rootNode.isExpanded()){
			source += rootNode.id + seperator;
		}
		source += getExpandNodes(tree.getRootNode(), seperator);
		document.getElementById(treeClientId + "_expandedNodes").value = source;
	}
}

//得到某个节点下的所有勾中节点的id，用seperator指定分隔符
function getCheckNodes(node, seperator){
	var source = "";
	node.eachChild(function(child){
		if(child.getUI().checked && child.getUI().checked() == "checked"){
			source += child.id + seperator;
		}
		source += getCheckNodes(child, seperator);
	});
	return source;
}

//得到某个节点下的所有展开节点
function getExpandNodes(node, seperator){
	var source = "";
	node.eachChild(function(child){
		if(child.isExpanded()){
			source += child.id + seperator;
		}
		source += getExpandNodes(child, seperator);
	});
	return source;
}

//更新某个节点下的所有节点
function updateTreeNode(tree, loader, nodeId){
	var root = tree.getRootNode();
	var nodeToUpdate = null;
	if(nodeId == '' || nodeId == root.id){
		nodeToUpdate = root;
	}else{
		nodeToUpdate = findNodeById(root, nodeId);
	}
	if(nodeToUpdate != null){
		if(nodeToUpdate.isExpanded()){
			loader.load(nodeToUpdate,function(){
				nodeToUpdate.expand();
			});
		}else{
			nodeToUpdate.expand();
		}
	}
}

//查找指定节点下的某个节点
function findNodeById(node,nodeId){
	if(node.id == nodeId){
		return node;
	}
	var n = null;
	node.eachChild(function(child){
		if(child.id == nodeId){
			n = child;
		}
		if(n == null){
			n = findNodeById(child, nodeId);
		}
	}); 
	return n;
}
