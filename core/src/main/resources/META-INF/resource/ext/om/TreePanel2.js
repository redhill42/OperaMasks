Ext.tree.TreePanel2 = function(config){
    Ext.tree.TreePanel2.superclass.constructor.apply(this, arguments);
};
Ext.extend(Ext.tree.TreePanel2, Ext.tree.TreePanel, {
    disableEvent : function(){
        this.suppressEvent = true; // suppress event firing
        this.selModel.disableEvent();
    },

    enableEvent : function(){
        this.suppressEvent = false; // resume event firing
        this.selModel.enableEvent();
    },

    fireEvent : function(){
        if (!this.suppressEvent) {
            return Ext.tree.TreePanel2.superclass.fireEvent.apply(this, arguments);
        }
    },
    
    expandPath2 : function(path, attr, callback){
        attr = attr || "id";
        var keys = path.split(this.pathSeparator);
        var curNode = this.root;
        if(curNode.attributes[attr] != keys[1]){ // invalid root
            if(callback){
                callback(false, null);
            }
            return;
        }
        var index = 1;
        var f = function(){
            if(++index == keys.length){
                if(callback){
                    callback(true, curNode);
                }
                return;
            }
            var c = curNode.findChild(attr, keys[index]);
            if(!c){
                if(callback){
                    callback(false, curNode);
                }
                return;
            }
            curNode = c;
            c.expand2(false, false, f);
        };
        curNode.expand2(false, false, f);
    }
});

Ext.tree.DefaultSelectionModel2 = function(){
	Ext.tree.DefaultSelectionModel2.superclass.constructor.apply(this);
};
Ext.extend(Ext.tree.DefaultSelectionModel2, Ext.tree.DefaultSelectionModel, {
    disableEvent : function(){
        this.suppressEvent = true; // suppress event firing
    },

    enableEvent : function(){
        this.suppressEvent = false; // resume event firing
    },

    fireEvent : function(){
        if (!this.suppressEvent) {
            return Ext.tree.DefaultSelectionModel2.superclass.fireEvent.apply(this, arguments);
        }
    }
});

Ext.tree.TreeNode.prototype.select2 = function() {
	this.getOwnerTree().disableEvent();
	this.select();
	this.getOwnerTree().enableEvent();
};

Ext.tree.TreeNode.prototype.unselect2 = function() {
	this.getOwnerTree().disableEvent();
	this.unselect2();
	this.getOwnerTree().enableEvent();
};

Ext.tree.TreeNode.prototype.expand2 = function(deep, anim, callback) {
	this.getOwnerTree().disableEvent();
	this.expand(deep, anim, callback);
	this.getOwnerTree().enableEvent();
};

Ext.tree.TreeNode.prototype.collapse2 = function(deep, anim){
	this.getOwnerTree().disableEvent();
	this.collapse(deep, anim);
	this.getOwnerTree().enableEvent();
};

Ext.tree.TreeNode.prototype.removeChild2 = function(node){
	this.getOwnerTree().disableEvent();
	this.removeChild(node);
	this.getOwnerTree().enableEvent();
};

Ext.tree.TreeNode.prototype.render = function(bulkRender) {
	this.ui.render(bulkRender);
	if(!this.rendered){
		this.rendered = true;
		if(this.expanded){
			this.expanded = false;
			this.expand2(false, false);
		}
	}
};

Ext.tree.AsyncTreeNode.prototype.expand2 = function(deep, anim, callback) {
	if (this.childNodes.length > 0) {
		this.loading = false;
        this.loaded = true;
	}
	
	this.getOwnerTree().disableEvent();
	this.expand(deep, anim, callback);
	this.getOwnerTree().enableEvent();
};