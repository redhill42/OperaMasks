Ext.namespace("Ext.ux.plugins");

Ext.ux.plugins.MenuItemPlugin = function(config) {
	this.init = function(menuItem){
		menuItem.onRender = onRender;
	}
    function onRender(container, position){
        var el = document.createElement("span");
        el.hideFocus = true;
        if(this.hrefTarget){
            el.target = this.hrefTarget;
        }
        el.className = this.itemCls + (this.menu ?  " x-menu-item-arrow" : "") + (this.cls ?  " " + this.cls : "");
        el.innerHTML = String.format(
                '<img src="{0}" class="x-menu-item-icon {2}" />{1}',
                this.icon || Ext.BLANK_IMAGE_URL, this.text, this.iconCls || '');
        this.el = el;
        Ext.menu.Item.superclass.onRender.call(this, container, position);
    }
};
