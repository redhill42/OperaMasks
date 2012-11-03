Ext.onReady(function(){
//layout.getRegion('center').showPanel('contentPanel');
//layout.getRegion('center').getPanel('sourcePanel').on("activate",function(){
var sourcePanel = content.getItem(1);
sourcePanel.on("activate",function(){
var node = examTree.getSelectionModel().getSelectedNode();
if(node && treeMenuData[node.id]) {
        var curURL = document.getElementById('source_frm').src;
        if (curURL.indexOf(treeMenuData[node.id].mbsrc) < 0) {
            document.getElementById('source_frm').src = treeMenuData[node.id].mbsrc;
        }
}
});
var jspSourcePanel = content.getItem(2);
//layout.getRegion('center').getPanel('jspSourcePanel').on("activate",function(){
jspSourcePanel.on("activate",function(){
var node = examTree.getSelectionModel().getSelectedNode();
if(node && treeMenuData[node.id]) {
        var curURL = document.getElementById('jspsource_frm').src;
        if (curURL.indexOf(treeMenuData[node.id].jspsrc) < 0) {
            document.getElementById('jspsource_frm').src = treeMenuData[node.id].jspsrc;
        }
}
});
examTree.getSelectionModel().purgeListeners();
//examTree.purgeListeners();
examTree.getSelectionModel().on("selectionchange",function(selMode, node){
    if (node.isLeaf() && treeMenuData[node.id]) {
        var curURL = document.getElementById('main').src;
        if (curURL != treeMenuData[node.id].url) {
            contentPanel.setTitle(node.text);
            layout.layout.center.el.select("span[class=x-panel-header-text]").elements[0].innerHTML
=node.text;
            document.getElementById('main').src = treeMenuData[node.id].url;
        }
        //layout.getRegion('center').showPanel('contentPanel');
    }
});
(function showExample() {
var treeNode;
var iIntervalId = null;
function f() {
    var treeNodeId = '<%=request.getParameter("node")%>';
    if (treeNodeId == 'null') {
        clearInterval(iIntervalId);
        return;
    }
    treeNode = examTree.getNodeById(treeNodeId);
    
    if (treeNode == null) {
        return;
    } else {
        var pNode = treeNode.parentNode;        
        if (pNode != null) {            
            examTree.expandPath(pNode.getPath());
        }
        treeNode.select();        
        clearInterval(iIntervalId);
    }    
}
iIntervalId = setInterval(f, 1000);
})();
});