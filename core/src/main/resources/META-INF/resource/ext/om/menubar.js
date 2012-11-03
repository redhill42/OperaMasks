/*
 * $Id: menubar.js,v 1.4 2007/12/30 10:55:11 yangdong Exp $
 *
 * Copyright (c) 2006 Operamasks Community.
 * Copyright (c) 2000-2006 Apusic Systems, Inc.
 */

Ext.menu.MenuBar = function(config) {
    Ext.menu.MenuBar.superclass.constructor.call(this, config);
    this.expandMenu = false;
   	this.render();
};

Ext.extend(Ext.menu.MenuBar, Ext.menu.Menu, {
    autoExpand : false,
    subMenuAlign : "bl-tl?",
    defaultAlign : "bl-tl?",

    render : function() {
        if (this.el || !this.renderTo) {
            return;
        }

        if (!Ext.menu.MenuBar.menuBarTemplate) {
            Ext.menu.MenuBar.menuBarTemplate = new Ext.Template(
                '<table border="0" cellpadding="0" cellspacing="0" class="x-menu-bar">',
                '<tbody><tr class="x-menu-list"><td width="100%">&#160</td></tr></tbody></table>');
        }

        var el = this.el = Ext.menu.MenuBar.menuBarTemplate.append(this.renderTo, [], true);
        var tr = this.tr = el.child(".x-menu-list");
        tr.on("click", this.onClick, this);
        tr.on("mouseover", this.onMouseOver, this);
        tr.on("mouseout", this.onMouseOut, this);
        this.items.each(function(item) {
            var td = document.createElement("td");
            td.className = "x-menu-list-item";
            tr.dom.insertBefore(td, tr.dom.lastChild);
            item.render(td, this);
        }, this);

        this.keyNav = new Ext.menu.MenuBarNav(this);

        this.el.setZIndex = function(){}; // fake Layer
    },

    addItem : function(item) {
        this.items.add(item);
        if (this.tr) {
            var td = document.createElement("td");
            td.className = "x-menu-list-item";
            this.tr.dom.insertBefore(td, this.tr.dom.lastChild);
            item.render(td, this);
        }
        return item;
    },

    insert : function(index, item) {
        this.items.insert(index, item);
        if (this.tr) {
            var td = document.createElement("td");
            td.className = "x-menu-list-item";
            this.tr.dom.insertBefore(td, this.tr.dom.childNodes[index]);
            item.render(td, this);
        }
        return item;
    },

    onClick : function(e) {
        var t;
        if (t = this.findTargetItem(e)) {
            if (!this.expandMenu) {
                this.expandMenu = true;
                this.fireEvent("show", this);
            }
            this.setActiveItem(t, true);
            this.fireEvent("click", this, t, e);
        }
    },
    
    onMouseOver : function(e) {
        var t;
        if (t = this.findTargetItem(e)) {
            if (t.canActivate && !t.disabled) {
                if (this.autoExpand) {
                    if (!this.expandMenu) {
                        this.expandMenu = true;
                        this.fireEvent("show", this);
                    }
                    this.setActiveItem(t, true);
                } else {
                    this.setActiveItem(t, this.expandMenu);
                }
            }
        }
        this.fireEvent("mouseover", this, e, t);
    },

    onMouseOut : function(e) {
        var t;
        if (this.autoExpand || !this.expandMenu) {
            if (t = this.findTargetItem(e)) {
                if (t == this.activeItem && t.shouldDeactivate(e)) {
                    this.activeItem.deactivate();
                    delete this.activeItem;
                }
            }
        }
        this.fireEvent("mouseout", this, e, t);
    },
    
    hide : function(){
        if (this.el && this.isVisible()) {
            this.fireEvent("beforehide", this);
            if (this.activeItem) {
                this.activeItem.deactivate();
                this.activeItem = null;
            }
            this.expandMenu = false;
            this.fireEvent("hide", this);
        }
    },

    // unused
    autoWidth : function(){},
    delayAutoWidth: function(){},
    show : function(){},
    showAt : function(){}
});

Ext.menu.MenuBarNav = function(menu) {
    Ext.menu.MenuBarNav.superclass.constructor.call(this, menu.el);
    this.scope = this.menu = menu;
};

Ext.extend(Ext.menu.MenuBarNav, Ext.KeyNav, {
    doRelay : function(e, h) {
        var k = e.getKey();
        if (!this.menu.activeItem && e.isNavKeyPress() && k != e.SPACE && k != e.RETURN) {
            this.menu.tryActivate(0, 1);
            return false;
        }
        return h.call(this.scope || this, e, this.menu);
    },

    down : function(e, m) {
        if (m.activeItem) {
            m.activeItem.expandMenu(true);
        }
    },

    left : function(e, m) {
        if (!m.tryActivate(m.items.indexOf(m.activeItem)-1, -1)) {
            m.tryActivate(m.items.length-1, -1);
        }
    },

    right : function(e, m) {
        if (!m.tryActivate(m.items.indexOf(m.activeItem)+1, 1)) {
            m.tryActivate(0, 1);
        }
    }
});
