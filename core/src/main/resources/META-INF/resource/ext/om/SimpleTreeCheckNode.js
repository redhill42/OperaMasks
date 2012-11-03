Ext.tree.TreeNode.prototype.getChecked = function() {
	if (typeof this.attributes.check == 'undefined')
		this.attributes.check = 'unchecked';
		
	return this.attributes.check;
};

Ext.tree.TreeEventModel.prototype.delegateClick = function(e, t) {
	if (!this.beforeEvent(e)) {
		return;
	}

	if (e.getTarget('img[class^=tree-node-]', 1)) {
		return;
	} else if (e.getTarget('.x-tree-ec-icon', 1)) {
		this.onIconClick(e, this.getNode(e));
	} else if (this.getNodeTarget(e)) {
		this.onNodeClick(e, this.getNode(e));
	}
};

Ext.tree.TreeEventModel.prototype.delegateDblClick = function(e, t) {
	if (e.getTarget('img[class^=tree-node-]', 1)) {
		return;
	}
		
    if(this.beforeEvent(e) && this.getNodeTarget(e)){
        this.onNodeDblClick(e, this.getNode(e));
    }
};

Ext.tree.SimpleCheckboxNodeUI = function(node){
    Ext.tree.SimpleCheckboxNodeUI.superclass.constructor.apply(this, arguments);
};

Ext.extend(Ext.tree.SimpleCheckboxNodeUI, Ext.tree.TreeNodeUI, {
	renderElements : function(n, a, targetNode, bulkRender){
		this.indentMarkup = n.parentNode ? n.parentNode.ui.getChildIndent() : '';

        var buf = ['<li class="x-tree-node"><div ext:tree-node-id="',n.id,'" class="x-tree-node-el x-tree-node-leaf x-unselectable ', a.cls,'" unselectable="on">',
            '<span class="x-tree-node-indent">',this.indentMarkup,"</span>",
            '<img src="', this.emptyIcon, '" class="x-tree-ec-icon x-tree-elbow" />',
            '<img src="', a.icon || this.emptyIcon, '" class="x-tree-node-icon',(a.icon ? " x-tree-node-inline-icon" : ""),(a.iconCls ? " "+a.iconCls : ""),'" unselectable="on" />',
            '<img src="', this.emptyIcon, '" class="tree-node-', n.getChecked(), '">',
            '<a hidefocus="on" class="x-tree-node-anchor" href="',a.href,'" tabIndex="1" ',
             a.hrefTarget ? ' target="'+a.hrefTarget+'"' : "", '><span unselectable="on">',n.text,"</span></a></div>",
            '<ul class="x-tree-node-ct" style="display:none;"></ul>',
            "</li>"];

		if(bulkRender !== true && n.nextSibling && n.nextSibling.ui.getEl()){
			this.wrap = Ext.DomHelper.insertHtml("beforeBegin", n.nextSibling.ui.getEl(), buf.join(""));
		} else {
			this.wrap = Ext.DomHelper.insertHtml("beforeEnd", targetNode, buf.join(""));
		}
		this.elNode = this.wrap.childNodes[0];
		this.ctNode = this.wrap.childNodes[1];
		var cs = this.elNode.childNodes;
		this.indentNode = cs[0];
		this.ecNode = cs[1];
		this.iconNode = cs[2];
		this.checkbox = cs[3];
		this.checkbox.check = n.getChecked();
		this.anchor = cs[4];
		this.textNode = cs[4].firstChild;

		Ext.fly(this.checkbox).on('click', this.check.createDelegate(this, [null]));
	},
	
	changeCheck : function(node) {
		if (node.getChecked() == 'unchecked')
			return 'checked';
		else
			return 'unchecked';
	},	

	checked : function() {
		return this.node.getChecked();
	},

	check : function(state, bulk) {
		var n = this.node;		
		var tree = n.getOwnerTree();
		var parentNode = n.parentNode;

		if (typeof state == 'undefined' || state == null)
			state = this.changeCheck(n);

            if(this.fireEvent("beforecheck", n, state) === false){
                return;
            }

		if( typeof bulk == 'undefined' ) {
			bulk = false;
		}
		
		if (typeof callByParent == 'undefined') {
			callByParent = false;
		}
		
		
		if (typeof this.checkbox != 'undefined') {
			this.checkbox.check = state;
			this.checkbox.className = 'tree-node-' + state
		}

		n.attributes.check = state;

		if( !bulk) {
		     tree.fireEvent('check', n, state);
		}
	},
	
	setChecked : function(checked){
		var state = checked ? "checked" : "unchecked";
		this.checkbox.check = state;
		this.node.attributes.check = state;
		this.checkbox.className = 'tree-node-' + state;
	}
});

/**
 * @class Ext.tree.RootTreeNodeUI
 * This class provides the default UI implementation for <b>root</b> Ext TreeNodes.
 * The RootTreeNode UI implementation allows customizing the appearance of the root tree node.<br>
 * <p>
 * If you are customizing the Tree's user interface, you
 * may need to extend this class, but you should never need to instantiate this class.<br>
 */
Ext.tree.RootSimpleCheckboxNodeUI = Ext.extend(Ext.tree.SimpleCheckboxNodeUI, {
    // private
    render : function(){
        if(!this.rendered){
            var targetNode = this.node.ownerTree.innerCt.dom;
            this.node.expanded = true;
            targetNode.innerHTML = '<div class="x-tree-root-node"></div>';
            this.wrap = this.ctNode = targetNode.firstChild;
        }
    },
    collapse : Ext.emptyFn,
    expand : Ext.emptyFn
});