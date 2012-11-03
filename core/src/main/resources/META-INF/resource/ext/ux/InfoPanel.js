// vim: ts=2:sw=2:nu:fdc=4:nospell

// Create user extensions namespace (Ext.ux)
Ext.namespace('Ext.ux');

/**
  * Ext.ux.InfoPanel Extension Class
	*
	* @author  Ing. Jozef Sakalos
	* @version $Id: InfoPanel.js,v 1.3 2007/12/29 03:05:37 yangdong Exp $
  *
  * @class Ext.ux.InfoPanel
  * @extends Ext.ContentPanel
  * @constructor
  * Creates new Ext.ux.InfoPanel
  * @param {String/HTMLElement/Element} el The container element for this panel
  * @param {String/Object} config A string to set only the title or a config object
	* @param {String} content (optional) Set the HTML content for this panel
	* @cfg {Boolean} animate false to switch animation of expand/collapse off (defaults to true)
	* @cfg {String} bodyClass css class added to the body in addition to the default class(es)
  * @cfg {String/HTMLElement/Element} bodyEl This element is used as body of panel.
	* @cfg {Boolean} collapsed false to start with the expanded body (defaults to true)
	* @cfg {Boolean} collapseOnUnpin unpinned panel is collapsed when possible (defaults to true)
	* @cfg {Boolean} collapsible false to disable collapsibility (defaults to true)
	* @cfg {Boolean} draggable true to allow panel dragging (defaults to false)
	* @cfg {Float} duration Duration of animation in seconds (defaults to 0.35)
	* @cfg {String} easingCollapse Easing to use for collapse animation (e.g. 'backIn')
	* @cfg {String} easingExpand Easing to use for expand animation (e.g. 'backOut')
	* @cfg {String} icon Path for icon to display in the title
	* @cfg {Integer} minWidth minimal width in pixels of the resizable panel (no default)
	* @cfg {Integer} maxWidth maximal width in pixels of the resizable panel (no default)
	* @cfg {Integer} minHeight minimal height in pixels of the resizable panel (no default)
	* @cfg {Integer} maxHeight maximal height in pixels of the resizable panel (no default)
	* @cfg {String} panelClass Set to override the default 'x-dock-panel' class.
	* @cfg {Boolean} pinned true to start in pinned state (implies collapsed:false) (defaults to false)
	* @cfg {Boolean} resizable true to allow use resize width of the panel.
	*  Handles are transparent. (defaults to false)
	* @cfg {String} shadowMode defaults to 'sides'.
	* @cfg {Boolean} showPin Show the pin button - makes sense only if panel is part of Accordion
	* @cfg {String} trigger 'title' or 'button'. Click where expands/collapses the panel (defaults to 'title')
	* @cfg {Boolean} useShadow Use shadows for undocked panels or panels w/o dock. (defaults to true)
  */
Ext.ux.InfoPanel = function(el, config, content) {

	// {{{
	// basic setup
	var oldHtml = content || null;
	if(config && config.content) {
		oldHtml = oldHtml || config.content;
		delete(config.content);
	}

	// save autoScroll to this.bodyScroll
	if(config && config.autoScroll) {
		this.bodyScroll = config.autoScroll;
		delete(config.autoScroll);
	}

	// call parent constructor
	Ext.ux.InfoPanel.superclass.constructor.call(this, el, config);

	this.desktop = Ext.get(this.desktop) || Ext.get(document.body);

	// shortcut of DomHelper
	var dh = Ext.DomHelper, oldTitleEl;

	this.el.clean();
	this.el.addClass(this.panelClass);

	// handle autoCreate
	if(this.autoCreate) {
		oldHtml = this.el.dom.innerHTML;
		this.el.update('');
		this.desktop.appendChild(this.el);
		this.el.removeClass('x-layout-inactive-content');
	}
	// handle markup
	else {
		this.el.clean();
		if(this.el.dom.firstChild && !this.bodyEl) {
			this.title = this.title || this.el.dom.firstChild.innerHTML;
			if(this.el.dom.firstChild.nextSibling) {
				this.body = Ext.get(this.el.dom.firstChild.nextSibling);
			}
			oldTitleEl = this.el.dom.firstChild;
			oldTitleEl = oldTitleEl.parentNode.removeChild(oldTitleEl);
			oldTitleEl = null;
		}
	}

	// get body element
	if(this.bodyEl) {
		this.body = Ext.get(this.bodyEl);
		this.el.appendChild(this.body);
	}
	// }}}
	// {{{
	// create title element
	var create = {
		tag:'div', unselectable:'on', cls:'x-unselectable x-layout-panel-hd x-dock-panel-title', children: [
			{tag:'table', cellspacing:0, children: [
				{tag:'tr', children: [
					{tag:'td', width:'100%', children: [
						{tag:'div', cls:'x-dock-panel x-layout-panel-hd-text x-dock-panel-title-text'}
					]}
					, {tag:'td', children:[
						{tag:'div', cls:'x-dock-panel x-dock-panel-tools'}
					]}
				]}
			]}
		]};
	this.titleEl = dh.insertFirst(this.el.dom, create, true);
	this.titleEl.addClassOnOver('x-dock-panel-title-over');
	this.titleEl.enableDisplayMode();
	this.titleTextEl = Ext.get(this.titleEl.select('.x-dock-panel-title-text').elements[0]);
	this.tools = Ext.get(this.titleEl.select('.x-dock-panel-tools').elements[0]);

	this.tm = Ext.util.TextMetrics.createInstance(this.titleTextEl);
	// }}}
	// {{{
	// set title
	if(this.title) {
		this.setTitle(this.title);
	}
	// }}}
	// {{{
	// create pin button
	if(this.showPin) {
		this.stickBtn = this.createTool(this.tools.dom, 'x-layout-stick');
		this.stickBtn.enableDisplayMode();
		this.stickBtn.on('click', function(e, target) {
			e.stopEvent();
			this.pinned = ! this.pinned;
			this.updateVisuals();
			this.fireEvent('pinned', this, this.pinned);
		}, this);
		this.stickBtn.hide();
	}
	// }}}
	// {{{
	// create collapse button
	if(this.collapsible) {
		this.collapseBtn = this.createTool(this.tools.dom
			, (this.collapsed ? 'x-layout-collapse-east' : 'x-layout-collapse-south')
		);
		this.collapseBtn.enableDisplayMode();
		if('title' === this.trigger) {
			this.titleEl.addClass('x-window-header-text');
			this.titleEl.on({
				  click:{scope: this, fn:this.toggle}
				, selectstart:{scope: this, fn: function(e) {
						e.preventDefault();
						return false;
				}}
			}, this);
		}
		else {
			this.collapseBtn.on("click", this.toggle, this);
		}
	}
	// }}}
	// {{{
	// create body if it doesn't exist yet
	if(!this.body) {
			this.body = dh.append(this.el, {
				tag: 'div'
				, cls: this.bodyClass || null
				, html: oldHtml || ''
				}, true);
	}
	this.body.enableDisplayMode();
	if(this.collapsed && !this.pinned) {
		this.body.hide();
	}
	else if(this.pinned) {
		this.body.show();
		this.collapsed = false;
		this.updateVisuals();
	}
	this.body.addClass(this.bodyClass);
	this.body.addClass('x-dock-panel-body-undocked');

	// bodyScroll
	// autoScroll -> bodyScroll is experimental due to IE bugs
	if(!Ext.isIE) {
		this.body.setStyle('overflow', this.bodyScroll === true ? 'auto' : 'hidden');
	}
	// }}}
	// {{{
	// add events
	this.addEvents({
		/**
			* @event beforecollapse
			* Fires before collapse is taking place. Return false to cancel collapse
			* @param {Ext.ux.InfoPanel} this
			*/
		beforecollapse: true
		/**
			* @event collapse
			* Fires after collapse
			* @param {Ext.ux.InfoPanel} this
			*/
		, collapse: true
		/**
			* @event beforecollapse
			* Fires before expand is taking place. Return false to cancel expand
			* @param {Ext.ux.InfoPanel} this
			*/
		, beforeexpand: true
		/**
			* @event expand
			* Fires after expand
			* @param {Ext.ux.InfoPanel} this
			*/
		, expand: true
		/**
			* @event pinned
			* Fires when panel is pinned/unpinned
			* @param {Ext.ux.InfoPanel} this
			* @param {Boolean} pinned true if the panel is pinned
			*/
		, pinned: true
		/**
			* @event animationcompleted
			* Fires when animation is completed
			* @param {Ext.ux.InfoPanel} this
			*/
		, animationcompleted: true
		/**
			* @event boxchange
			* Fires when the panel is resized
			* @param {Ext.ux.InfoPanel} this
			* @param {Object} box
			*/
		, boxchange: true

		/**
			* @event redize
			* Fires when info panel is resized
			* @param {Ext.ux.InfoPanel} this
			* @param {Integer} width New width
			* @param {Integer} height New height
			*/
		, resize: true

		/**
			* @event destroy
			* Fires after the panel is destroyed
			* @param {Ext.ux.InfoPanel} this
			*/
		, destroy: true

	});
	// }}}
	// {{{
	// setup dragging, resizing, and shadow
	this.setDraggable(this.draggable);
	this.setResizable(!this.collapsed);
	this.setShadow(this.useShadow);

	/*
	// todo revise
	if(this.minHeight && !this.docked) {
		this.setHeight(this.minHeight);
	}

	if(this.minWidth && !this.docked) {
		this.setWidth(this.minWidth);
	}
	*/
	// }}}

	this.id = this.el.id;

}; // end of constructor

// extend
Ext.extend(Ext.ux.InfoPanel, Ext.ContentPanel, {

	// {{{
	// defaults
	collapsible: true
	, collapsed: true
	, collapseOnUnpin: true
	, pinned: false
	, trigger: 'title'
	, animate: true
	, duration: 0.35
	, draggable: false
	, resizable: false
	, docked: false
	, useShadow: false
	, bodyClass: 'x-dock-panel-body'
	, panelClass: 'x-dock-panel'
	, shadowMode: 'sides'
	, dragPadding: {
		  left:8
		, right:16
		, top:0
		, bottom:8
	}
	, lastWidth: 0
	, lastHeight: 0
	, autoScroll: false
	// }}}
	// {{{
	/**
		* Called internally to create collapse button
		* Calls utility method of Ext.LayoutRegion createTool
		* @param {Element/HTMLElement/String} parentEl element to create the tool in
		* @param {String} className class of the tool
		*/
	, createTool : function(parentEl, className){
		return Ext.LayoutRegion.prototype.createTool(parentEl, className);
  }
	// }}}
	// {{{
	/**
		* Set title of the InfoPanel
		* @param {String} title Title to set
		* @return {Ext.ux.InfoPanel} this
		*/
	, setTitle: function(title) {
		this.title = title;
		this.titleTextEl.update(title);
		if(this.icon) {
			this.titleTextEl.set({
				style:
					'background-image:url('
					+ this.icon
					+ ');background-repeat:no-repeat;background-position:3px 50%;padding-left:23px;'
			});
		}
		return this;
	}
	// }}}
	// {{{
	/**
		* Get current title
		* @return {String} Current title
		*/
	, getTitle: function() {
		return this.title;
	}
	// }}}
	// {{{
	/**
		* Returns body element
		* This overrides the ContentPanel getEl for convenient access to the body element
		* @return {Element} this.body
		*/
	, getEl: function() {
		return this.body;
	}
	// }}}
	// {{{
	/**
		* Update the innerHTML of this element, optionally searching for and processing scripts
    * @param {String} html The new HTML
    * @param {Boolean} loadScripts (optional) true to look for and process scripts
    * @param {Function} callback For async script loading you can be noticed when the update completes
    * @return {Ext.Element} this
		*/
	, update: function(html, loadScripts, callback) {
		this.body.update(html, loadScripts, callback);
		return this;
	}
	// }}}
	// {{{
	/**
		* Expands the panel
		* @return {Ext.ux.InfoPanel} this
		*/
	, expand: function() {

		// do nothing if already expanded
		if(!this.collapsed) {
			return this;
		}

		// fire beforeexpand event
		if(false === this.fireEvent('beforeexpand', this)) {
			return this;
		}

		// reset collapsed flag
		this.collapsed = false;

		// hide shadow
		if(!this.docked) {
			this.setShadow(false);
		}

		// enable resizing
		if(this.resizer && !this.docked) {
			this.setResizable(true);
		}

		// animate expand
		if(this.animate) {
				this.body.slideIn('t', {
					easing: this.easingExpand || null
					, scope: this
					, duration: this.duration
					, callback: this.updateVisuals
				});
		}

		// don't animate, just show
		else {
			this.body.show();
			this.updateVisuals();
			this.fireEvent('animationcompleted', this);
		}

		// fire expand event
		this.fireEvent('expand', this);

		return this;

	}
	// }}}
	// {{{
	/**
		* Toggles the expanded/collapsed states
		* @return {Ext.ux.InfoPanel} this
		*/
	, toggle: function() {
			if(this.collapsed) {
				this.expand();
			}
			else {
				this.collapse();
			}
			return this;
	}
	// }}}
	// {{{
	/**
		* Collapses the panel
		* @return {Ext.ux.InfoPanel} this
		*/
	, collapse: function() {

		// do nothing if already collapsed or pinned
		if(this.collapsed || this.pinned) {
			return this;
		}

		// fire beforecollapse event
		if(false === this.fireEvent('beforecollapse', this)) {
				return this;
		}

		if(this.bodyScroll /*&& !Ext.isIE*/) {
			this.body.setStyle('overflow','hidden');
		}

		// set collapsed flag
		this.collapsed = true;

		// hide shadow
		this.setShadow(false);

		// disable resizing of collapsed panel
		if(this.resizer) {
			this.setResizable(false);
		}

		// animate collapse
		if(this.animate) {
				this.body.slideOut('t', {
					easing: this.easingCollapse || null
					, scope: this
					, duration: this.duration
					, callback: this.updateVisuals
				});
		}

		// don't animate, just hide
		else {
			this.body.hide();
			this.updateVisuals();
			this.fireEvent('animationcompleted', this);
		}

		// fire collapse event
		this.fireEvent('collapse', this);

		return this;

	}
	// }}}
	// {{{
	/**
		* Called internally to update class of the collapse button
		* as part of expand and collapse methods
		*
		* @return {Ext.ux.InfoPanel} this
		*/
	, updateVisuals: function() {

			// handle collapsed state
			if(this.collapsed) {
				if(this.showPin) {
					this.collapseBtn.show();
					this.stickBtn.hide();
				}
				Ext.fly(this.collapseBtn.dom.firstChild).replaceClass(
					'x-layout-collapse-south', 'x-layout-collapse-east'
				);
				this.body.replaceClass('x-dock-panel-body-expanded', 'x-dock-panel-body-collapsed');
				this.titleEl.replaceClass('x-dock-panel-title-expanded', 'x-dock-panel-title-collapsed');
			}

			// handle expanded state
			else {
				if(this.showPin) {
					if(this.pinned) {
						Ext.fly(this.stickBtn.dom.firstChild).replaceClass('x-layout-stick', 'x-layout-stuck');
						this.titleEl.addClass('x-dock-panel-title-pinned');
					}
					else {
						Ext.fly(this.stickBtn.dom.firstChild).replaceClass('x-layout-stuck', 'x-layout-stick');
						this.titleEl.removeClass('x-dock-panel-title-pinned');
					}
					this.collapseBtn.hide();
					this.stickBtn.show();
				}
				else {
					Ext.fly(this.collapseBtn.dom.firstChild).replaceClass(
						'x-layout-collapse-east', 'x-layout-collapse-south'
					);
				}
				this.body.replaceClass('x-dock-panel-body-collapsed', 'x-dock-panel-body-expanded');
				this.titleEl.replaceClass('x-dock-panel-title-collapsed', 'x-dock-panel-title-expanded');
			}

			// show shadow if necessary
			if(!this.docked) {
				this.setShadow(true);
			}

			if(this.bodyScroll && !this.docked && !this.collapsed /*&& !Ext.isIE*/) {
				this.body.setStyle('overflow', 'auto');
			}

			this.constrainToDesktop();

			// fire animationcompleted event
			this.fireEvent('animationcompleted', this);

			// clear visibility style of body's children
			var kids = this.body.select('div[className!=x-grid-viewport],input{visibility}');
//			var kids = this.body.select('div,input{visibility}');
			kids.setStyle.defer(1, kids, ['visibility','']);

			// restore visibility of grid-viewport if any
//			var gvp = this.body.select('div.x-grid-viewport');
//			gvp.setStyle('visibility', this.collapsed ? 'hidden' : 'visible');

			return this;
	}
	// }}}
	// {{{
	/**
		* Creates toolbar
		* @param {Array} config Configuration for Ext.Toolbar
		* @param {Boolean} bottom true to create bottom toolbar. (defaults to false = top toolbar)
		* @return {Ext.Toolbar} Ext.Toolbar object
		*/
	, createToolbar: function(config, bottom) {
			var create = {tag:'div'}, tbEl;
			config = config || null;
			if(bottom) {
				tbEl = Ext.DomHelper.append(this.body, create, true);
				tbEl.addClass('x-dock-panel-toolbar-bottom');
			}
			else {
				tbEl = Ext.DomHelper.insertFirst(this.body, create, true);
				tbEl.addClass('x-dock-panel-toolbar');
			}
			this.toolbar = new Ext.Toolbar(tbEl, config);
			return this.toolbar;
	}
	// }}}
	// {{{
	/**
		* Set the panel draggable
		* Uses lazy creation of dd object
		* @param {Boolean} enable pass false to disable dragging
		* @return {Ext.ux.InfoPanel} this
		*/
	, setDraggable: function(enable) {

		if(!this.draggable) {
			return this;
		}

		// lazy create proxy
		var dragTitleEl;
		if(!this.proxy) {
			this.proxy = this.el.createProxy('x-dlg-proxy');

			// setup title
			dragTitleEl = Ext.DomHelper.append(this.proxy, {tag:'div'}, true);
			dragTitleEl.update(this.el.dom.firstChild.innerHTML);
			dragTitleEl.dom.className = this.el.dom.firstChild.className;
			if(this.collapsed && Ext.isIE) {
				dragTitleEl.dom.style.borderBottom = "0";
			}

			this.proxy.hide();
			this.proxy.setOpacity(0.5);
			this.dd = new Ext.dd.DDProxy(this.el.dom, 'PanelDrag', {
				dragElId: this.proxy.id
				, scroll: false
			});
			this.dd.scroll = false;
			this.dd.afterDrag = function() {
				if(this.panel && this.panel.shadow && !this.panel.docked) {
					this.panel.shadow.show(this.panel.el);
				}
			};

			this.constrainToDesktop();
			Ext.EventManager.onWindowResize(this.moveToViewport, this);
		}

		this.dd.panel = this;
		this.dd.setHandleElId(this.titleEl.id);
		if(false === enable) {
			this.dd.lock();
		}
		else {
			this.dd.unlock();
		}

		return this;
	}
	// }}}
	// {{{
	/**
		* Set the panel resizable
		* Uses lazy creation of the resizer object
		* @param {Boolean} pass false to disable resizing
		* @return {Ext.ux.InfoPanel} this
		*/
	, setResizable: function(enable) {

		if(!this.resizable) {
			return this;
		}

		// {{{
		// lazy create resizer
		if(!this.resizer) {

			// {{{
			// create resizer
			this.resizer = new Ext.Resizable(this.el, {
				handles: 's w e sw se'
				, minWidth: this.minWidth || this.tm.getWidth(this.getTitle()) + 56 || 48
				, maxWidth: this.maxWidth || 9999
				, minHeight: this.minHeight || 48
				, maxHeight: this.maxHeight || 9999
				, transparent: true
				, draggable: false
			});
			// }}}
			// {{{
			// install event handlers
			this.resizer.on({
				beforeresize: {
					scope:this
					, fn: function(resizer, e) {
						var viewport = this.getViewport();
						var box = this.getBox();

						var pos = resizer.activeHandle.position;

						// left constraint
						if(pos.match(/west/)) {
							resizer.minX = viewport.x + (this.dragPadding.left || 8);
						}

						// down constraint
						if(pos.match(/south/)) {
							resizer.oldMaxHeight = resizer.maxHeight;
							resizer.maxHeight = viewport.y + viewport.height - box.y - (this.dragPadding.bottom || 8);
						}

						// right constraint
						if(pos.match(/east/)) {
							resizer.oldMaxWidth = resizer.maxWidth;
							resizer.maxWidth = viewport.x + viewport.width - box.x - (this.dragPadding.right || 10);
						}
				}}
				, resize: {
					scope: this
					, fn: function(resizer, width, height, e) {
						resizer.maxHeight = resizer.oldMaxHeight || resizer.maxHeight;
						resizer.maxWidth = resizer.oldMaxWidth || resizer.maxWidth;
						this.setSize(width, height);
						this.constrainToDesktop();
						this.fireEvent('boxchange', this, this.el.getBox());
						this.fireEvent('resize', this, width, height);
				}}
			});
			// }}}

		}
		// }}}

		this.resizer.enabled = enable;

		// this is custom override of Ext.Resizer
		this.resizer.showHandles(enable);

		return this;
	}
	// }}}
	// {{{
	/**
		* Called internally to clip passed width and height to viewport
		* @param {Integer} w width
		* @param {Integer} h height
		* @return {Object} {width:safeWidth, height:safeHeight}
		*/
	, safeSize: function(w, h) {
		var viewport = this.getViewport();
		var box = this.getBox();
		var gap = 0;
		var safeSize = {width:w, height:h};

		safeSize.height =
			box.y + h + this.dragPadding.bottom + gap > viewport.height + viewport.y
			? viewport.height - box.y + viewport.y - this.dragPadding.bottom - gap
			: safeSize.height
		;

		safeSize.width =
			box.x + w + this.dragPadding.right + gap > viewport.width + viewport.x
			? viewport.width - box.x + viewport.x - this.dragPadding.right - gap
			: safeSize.width
		;

		return safeSize;
	}
	// }}}
	// {{{
	/**
		* Called internally to get current viewport
		* @param {Element/HTMLElement/String} desktop Element to get size and position of
		* @return {Object} viewport {x:x, y:y, width:width, height:height} x and y are page coords
		*/
	, getViewport: function(desktop) {

		desktop = desktop || this.desktop || document.body;
		var viewport = Ext.get(desktop).getViewSize();
		var xy;
		if(document.body === desktop.dom) {
			viewport.x = 0;
			viewport.y = 0;
		}
		else {
			xy = desktop.getXY();
			viewport.x = isNaN(xy[0]) ? 0 : xy[0];
			viewport.y = isNaN(xy[1]) ? 0 : xy[1];
		}

		return viewport;
	}
	// }}}
	// {{{
	/**
		* Sets the size of the panel. Demanded size is clipped to the viewport
		*
		* @param {Integer} w width to set
		* @param {Integer} h height to set
		* @return {Ext.ux.InfoPanel} this
		*/
	, setSize: function(w, h) {
		var safeSize = this.safeSize(w, h);
		this.setWidth(safeSize.width);
		this.setHeight(safeSize.height);

		if(!this.docked) {
			this.setShadow(true);
		}
	}
	// }}}
	// {{{
	/**
		* Sets the width of the panel. Demanded width is clipped to the viewport
		*
		* @param {Integer} w width to set
		* @return {Ext.ux.InfoPanel} this
		*/
	, setWidth: function(w) {
		this.el.setWidth(w);
		this.body.setStyle('width','');
		if(!this.docked) {
			this.setShadow(true);
		}
		this.lastWidth = w;

		return this;
	}
	// }}}
	// {{{
	/**
		* Sets the height of the panel. Demanded height is clipped to the viewport
		*
		* @param {Integer} h height to set
		* @return {Ext.ux.InfoPanel} this
		*/
	, setHeight: function(h) {
		var newH = h - this.titleEl.getHeight();
		if(0 < newH) {
			this.body.setHeight(newH);
		}
		else {
			this.body.setStyle('height','');
		}

		if(!this.docked) {
			this.setShadow(true);
		}
//		this.lastHeight = h;
		this.el.setStyle('height','');

		return this;
	}
	// }}}
	// {{{
	/**
		* Called internally to set x, y, width and height of the panel
		*
		* @param {Object} box
		* @return {Ext.ux.InfoPanel} this
		*/
	, setBox: function(box) {
		this.el.setBox(box);
		this.moveToViewport();
		this.setSize(box.width, box.height);

		return this;
	}
	// }}}
	// {{{
	/**
		* Called internally to get the box of the panel
		*
		* @return {Object} box
		*/
	, getBox: function() {
		return this.el.getBox();
	}
	// }}}
	// {{{
	/**
		* Turns shadow on/off
		* Uses lazy creation of the shadow object
		* @param {Boolean} shadow pass false to hide, true to show the shadow
		* @return {Ext.ux.InfoPanel} this
		*/
	, setShadow: function(shadow) {

		// if I have shadow but shouldn't use it
		if(this.shadow && !this.useShadow) {
			this.shadow.hide();
			return this;
		}

		// if I shouldn't use shadow
		if(!this.useShadow) {
			return this;
		}

		// if I don't have shadow
		if(!this.shadow) {
			this.shadow = new Ext.Shadow({mode:this.shadowMode});
		}

		// show or hide
		var zindex;
		if(shadow) {
			this.shadow.show(this.el);

			// fix the Ext shadow z-index bug
			zindex = parseInt(this.el.getStyle('z-index'), 10);
			zindex = isNaN(zindex) ? '' : zindex - 1;
			this.shadow.el.setStyle('z-index', zindex);
		}
		else {
			this.shadow.hide();
		}

		return this;

	}
	// }}}
	// {{{
	/**
		* Show the panel
		* @param {Boolean} show (optional) if false hides the panel instead of show
		* @param {Boolean} alsoUndocked show/hide also undocked panel (defaults to false)
		* @return {Ext.ux.InfoPanel} this
		*/
	, show: function(show, alsoUndocked) {

		// ignore undocked panels if not forced to
		if(!this.docked && true !== alsoUndocked) {
			return this;
		}

		show = (false === show ? false : true);
		if(!this.docked) {
			this.setShadow(show);
		}

		this.el.setStyle('display', show ? '' : 'none');
		return this;
	}
	// }}}
	// {{{
	/**
		* Hide the panel
		* @param {Boolean} alsoUndocked show/hide also undocked panel (defaults to false)
		* @return {Ext.ux.InfoPanel} this
		*/
	, hide: function(alsoUndocked) {
		this.show(false, alsoUndocked);
	}
	// }}}
	// {{{
	/**
		* Constrains dragging of this panel to desktop boundaries
		* @param {Element} desktop the panel is to be constrained to
		* @return {Ext.ux.InfoPanel} this
		*/
	, constrainToDesktop: function(desktop) {
		desktop = desktop || this.desktop;
		if(desktop && this.dd) {
			this.dd.constrainTo(desktop, this.dragPadding, false);
		}
		return this;
	}
	// }}}
	// {{{
	/**
		* Called internally to move the panel to the viewport.
		* Also constrains the dragging to the desktop
		*
		* @param {Object} viewport (optional) object {x:x, y:y, width:width, height:height}
		* @return {Ext.ux.InfoPanel} this
		*/
	, moveToViewport: function(viewport) {
		viewport = viewport && !isNaN(viewport.x) ? viewport : this.getViewport();
		var box = this.getBox();
		var moved = false;
		var gap = 10;

		// horizontal
		if(box.x + box.width + this.dragPadding.right > viewport.x + viewport.width) {
			moved = true;
			box.x = viewport.width + viewport.x - box.width - this.dragPadding.right - gap;
		}
		if(box.x - this.dragPadding.left < viewport.x) {
			moved = true;
			box.x = viewport.x + this.dragPadding.left + gap;
		}

		// vertical
		if(box.y + box.height + this.dragPadding.bottom > viewport.y + viewport.height) {
			moved = true;
			box.y = viewport.height + viewport.y - box.height - this.dragPadding.bottom - gap;
		}
		if(box.y - this.dragPadding.top < viewport.y) {
			moved = true;
			box.y = viewport.y + this.dragPadding.top + gap;
		}

		var oldOverflow;
		if(moved) {
			// sanity clip
			box.x = box.x < viewport.x ? viewport.x : box.x;
			box.y = box.y < viewport.y ? viewport.y : box.y;

			// prevent scrollbars from appearing
			this.desktop.oldOverflow = this.desktop.oldOverflow || this.desktop.getStyle('overflow');
			this.desktop.setStyle('overflow', 'hidden');

			// set position
			this.el.setXY([box.x, box.y]);

			// restore overflow
			this.desktop.setStyle.defer(100, this.desktop, ['overflow', this.desktop.oldOverflow]);

			if(!this.docked) {
				this.setShadow(true);
			}
		}

		this.constrainToDesktop();

		return this;
	}
// }}}

	/**
		* destroys the panel
		*/
	, destroy: function() {
		if(this.shadow) {
			this.shadow.hide();
		}
		if(this.collapsible) {
			this.collapseBtn.removeAllListeners();
			this.titleEl.removeAllListeners();
		}

		if(this.resizer) {
			this.resizer.destroy();
		}
		if(this.dd) {
			if(this.proxy) {
				this.proxy.removeAllListeners();
				this.proxy.remove();
			}
			this.dd.unreg();
			this.dd = null;
		}

		this.body.removeAllListeners();

		// call parent destroy
		Ext.ux.InfoPanel.superclass.destroy.call(this);

		this.fireEvent('destroy', this);

	}

}); // end of extend

// {{{
// show/hide resizer handles override
Ext.override(Ext.Resizable, {

	/**
		* Hide resizer handles
		*/
	hideHandles: function() {
		this.showHandles(false);
	} // end of function hideHandles

	/**
		* Show resizer handles
		*
		* @param {Boolean} show (true = show, false = hide)
		*/
	, showHandles: function(show) {
		show = (false === show ? false : true);
		var pos;
		for(var p in Ext.Resizable.positions) {
			pos = Ext.Resizable.positions[p];
			if(this[pos]) {
				this[pos].el.setStyle('display', show ? '' : 'none');
			}
		}
	} // end of function showHandles
// }}}

});

// end of file
