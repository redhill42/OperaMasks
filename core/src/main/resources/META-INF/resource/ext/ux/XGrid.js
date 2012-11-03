Ext.namespace("Ext.ux.plugins");

Ext.ux.plugins.XGrid = function(config) {
	Ext.apply(this, config);
};

Ext.extend(Ext.ux.plugins.XGrid, Ext.util.Observable, {
    init: function(grid){
		this.grid = grid;
		this.view = this.grid.getView();
        this.view.layout = this.layout.createDelegate(this);
        this.grid.render = this.render.createDelegate(this);
	},
    render : function(container, position){
        if(!this.grid.rendered && this.grid.fireEvent("beforerender", this) !== false){
            if(!container && this.grid.el){
                var c = document.getElementById(this.grid.el);
                if (!c.style.height) {
                    c.style.height = '100%';
                }

                this.grid.el = Ext.get(this.grid.el);
                container = this.grid.el.dom.parentNode;
                this.grid.allowDomMove = false;
            }
            this.grid.container = Ext.get(container);
            if(this.grid.ctCls){
                this.grid.container.addClass(this.grid.ctCls);
            }
            this.grid.rendered = true;
            if(position !== undefined){
                if(typeof position == 'number'){
                    position = this.grid.container.dom.childNodes[position];
                }else{
                    position = Ext.getDom(position);
                }
            }
            this.grid.onRender(this.grid.container, position || null);
            if(this.grid.autoShow){
                this.grid.el.removeClass(['x-hidden','x-hide-' + this.grid.hideMode]);
            }
            if(this.grid.cls){
                this.grid.el.addClass(this.grid.cls);
                delete this.grid.cls;
            }
            if(this.grid.style){
                this.grid.el.applyStyles(this.grid.style);
                delete this.grid.style;
            }
            this.grid.fireEvent("render", this);
            this.grid.afterRender(this.grid.container);
            if(this.grid.hidden){
                this.grid.hide();
            }
            if(this.grid.disabled){
                this.grid.disable();
            }

            this.grid.initStateEvents();
        }
        return this;
    },

    layout : function(){
        if(!this.view.mainBody){
            return; // not rendered
        }
        var g = this.view.grid;
        var c = g.getGridEl(), cm = this.view.cm,
                expandCol = g.autoExpandColumn,
                gv = this;

        var csize = c.getSize(true);
        var vw = csize.width;

        if(vw < 20 || csize.height < 20){ // display: none?
            return;
        }

        if(g.autoHeight){
            this.view.scroller.dom.style.overflow = 'visible';
        }else{
            if( Ext.isIE) {
                this.view.el.setSize('100%', csize.height);
    
                var hdHeight = this.view.mainHd.getHeight();
                var vh = csize.height - (hdHeight);
    
                this.view.scroller.setSize('100%', vh);
                if(this.view.innerHd){
                    this.view.innerHd.style.width = '100%';
                }
            }    
            else {
                this.view.el.setSize(csize.width, csize.height);
    
                var hdHeight = this.view.mainHd.getHeight();
                var vh = csize.height - (hdHeight);
    
                this.view.scroller.setSize(vw, vh);
                if(this.view.innerHd){
                    this.view.innerHd.style.width = (vw)+'px';
                }
            }
        }
        if(this.view.forceFit){
            if(this.view.lastViewWidth != vw){
                this.view.fitColumns(false, false);
                this.view.lastViewWidth = vw;
            }
        }else {
            this.view.autoExpand();
        }
        this.view.onLayout(vw, vh);
    }
});