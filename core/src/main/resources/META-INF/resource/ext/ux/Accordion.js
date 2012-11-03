// vim: ts=2:sw=2:nu:fdc=2:nospell

// Create user extensions namespace (Ext.ux)
Ext.namespace('Ext.ux');

/**
  * Ext.ux.Accordion Extension Class
	*
	* @author  Ing. Jozef Sakalos
	* @version $Id: Accordion.js,v 1.3 2007/12/29 03:05:37 yangdong Exp $
  *
  * @class Ext.ux.Accordion
  * @extends Ext.ContentPanel
  * @constructor
  * @param {String/HTMLElement/Element} el The container element for this panel
  * @param {String/Object} config A string to set only the title or a config object
	* @cfg {Boolean} animate global animation flag for all panels. (defaults to true)
	* @cfg {Boolean} boxWrap set to true to wrap wrapEl the body is child of (defaults to false)
	* @cfg {Boolean} draggable set to false to disallow panels dragging (defaults to true)
	* @cfg {Boolean} fitHeight set to true if you use fixed height dock
	* @cfg {Boolean} independent true to make panels independent (defaults to false)
	* @cfg {Integer} initialHeight Initial height to set box to (defaults to 0)
	* @cfg {Boolean} monitorWindowResize if true panels are moved to
	*  viewport if window is small (defaults to true)
	* @cfg {Boolean} resizable global resizable flag for all panels (defaults to true)
	* @cfg {Boolean} undockable true to allow undocking of panels (defaults to true)
	* @cfg {Boolean} useShadow global useShadow flag for all panels. (defaults to true)
	* @cfg {Element/HTMLElement/String} wrapEl Element to wrap with nice surrounding
  */
Ext.ux.Accordion = function(el, config) {

	// call parent constructor
	Ext.ux.Accordion.superclass.constructor.call(this, el, config);

	// create collection for panels
	this.items = new Ext.util.MixedCollection();

	// assume no panel is expanded
	this.expanded = null;

	// {{{
	// install event handlers
	this.on({

		// {{{
		// runs before expansion. Triggered by panel's beforeexpand event
		beforeexpand: {
			  scope: this
			, fn: function(panel) {
					// raise panel above others
					if(!panel.docked) {
						this.raise(panel);
					}

					// set fixed height
					var panelBodyHeight;
					if(this.fitHeight && panel.docked) {
						panelBodyHeight = this.getPanelBodyHeight();
						if(panelBodyHeight) {
							panel.body.setHeight(panelBodyHeight);
						}
					}

					if(panel.docked) {
						this.expandCount++;
						this.expanding = true;
//						this.setDockScroll(false);
					}

					// don't collapse others if independent or not docked
					if(this.independent || !panel.docked) {
						return this;
					}

					// collapse expanded panel
					if(this.expanded && this.expanded.docked) {
						this.expanded.collapse();
					}

					// remember this panel as expanded
					this.expanded = panel;
		}}
		// }}}
		// {{{
		// runs before panel collapses. Triggered by panel's beforecollapse event
		, beforecollapse: {
			  scope: this
			, fn: function(panel) {

				// raise panel if not docked
				if(!panel.docked) {
					this.raise(panel);
				}
				return this;
		}}
		// }}}
		// {{{
		// runs on when panel expands (before animation). Triggered by panel's expand event
		, expand: {
			  scope: this
			, fn: function(panel) {
				this.fireEvent('panelexpand', panel);
		}}
		// }}}
		// {{{
		// runs on when panel collapses (before animation). Triggered by panel's collapse event
		, collapse: {
		 	  scope: this
			, fn: function(panel) {
				this.fireEvent('panelcollapse', panel);
		}}
		// }}}
		// {{{
		// runs on when animation is completed. Triggered by panel's animationcompleted event
		, animationcompleted: {
			scope: this
			, fn: function(panel) {
				var box = panel.el.getBox();
				this.expandCount = (this.expandCount && this.expanding) ? --this.expandCount : 0;
				if((0 === this.expandCount) && this.expanding) {
//					this.setDockScroll(true);
					this.expanding = false;
				}
				this.fireEvent('panelbox', panel, box);
		}}
		// }}}
		// {{{
		// runs when panel is pinned. Triggered by panel's pinned event
		, pinned: {
			  scope: this
			, fn: function(panel, pinned) {
				if(!pinned) {
					if(panel.collapseOnUnpin) {
						panel.collapse();
					}
					else if(!this.independent) {
						this.items.each(function(p) {
							if(p !== panel && p.docked && !p.pinned) {
								p.collapse();
							}
						});
						this.expanded = panel;
					}
				}
				this.fireEvent('panelpinned', panel, pinned);
		}}
		// }}}

		, destroy: {
			scope:this
			, fn: function(panel) {
				this.items.removeKey(panel.id);
				this.updateOrder();
		}}
	});
	// }}}
	// {{{
	// add events
	this.addEvents({
		/**
			* Fires when a panel of the dock is collapsed
			* @event panelcollapse
			* @param {Ext.ux.InfoPanel} panel
			*/
		panelcollapse: true

		/**
			* Fires when a panel of the dock is expanded
			* @event panelexpand
			* @param {Ext.ux.InfoPanel} panel
			*/
		, panelexpand: true

		/**
			* Fires when a panel of the dock is pinned
			* @event panelpinned
			* @param {Ext.ux.InfoPanel} panel
			* @param {Boolean} pinned true if panel was pinned false if unpinned
			*/
		, panelpinned: true

		/**
			* Fires when the independent state of dock changes
			* @event independent
			* @param {Ext.ux.Accordion} this
			* @param {Boolean} independent New independent state
			*/
		, independent: true

		/**
			* Fires when the order of panel is changed
			* @event orderchange
			* @param {Ext.ux.Accordion} this
			* @param {Array} order New order array
			*/
		, orderchange: true

		/**
			* Fires when the undockable state of dock changes
			* @event undockable
			* @param {Ext.ux.Accordion} this
			* @param {Array} undockable New undockable state
			*/
		, undockable: true

		/**
			* Fires when a panel is undocked
			* @event panelundock
			* @param {Ext.ux.InfoPanel} panel
			* @param {Object} box Position and size object
			*/
		, panelundock: true

		/**
			* Fires when a panel is undocked
			* @event paneldock
			* @param {Ext.ux.InfoPanel} panel
			*/
		, paneldock: true

		/**
			* Fires when a panel box is changed, e.g. after dragging
			* @event panelbox
			* @param {Ext.ux.InfoPanel} panel
			* @param {Object} box Position and size object
			*/
		, panelbox: true

		/**
			* Fires when useShadow status changes
			* @event useshadow
			* @param {Ext.ux.Accordion} this
			* @param {Boolean} shadow Use shadow (for undocked panels) flag
			*/
		, useshadow: true
	});
	// }}}

	// setup body
	this.body = Ext.get(this.body) || this.el;
	this.resizeEl = this.body;
	this.id = this.el.id;
	this.body.addClass('x-dock-body');

	// setup desktop
	this.desktop = Ext.get(this.desktop || document.body);
	//this.desktop = this.desktop.dom || this.desktop;

	// setup fixed hight
	this.wrapEl = Ext.get(this.wrapEl);
	if(this.fitHeight) {
		this.body.setStyle('overflow', 'hidden');
//		this.bodyHeight = this.initialHeight || this.body.getHeight();
		this.body.setHeight(this.initialHeight || this.body.getHeight());
		if(this.boxWrap && this.wrapEl) {
			this.wrapEl.boxWrap();
		}
	}

	// watch window resize
	if(this.monitorWindowResize) {
		Ext.EventManager.onWindowResize(this.adjustViewport, this);
	}

	// create drop zone for panels
	this.dd = new Ext.dd.DropZone(this.body.dom, {ddGroup:'dock-' + this.id });

}; // end of constructor

// extend
Ext.extend(Ext.ux.Accordion, Ext.ContentPanel, {

	// {{{
	// defaults
	independent: false
	, undockable: true
	, useShadow: true
	, boxWrap: false
	, fitHeight: false
	, initialHeight: 0
	, animate: true // global animation flag
	, zindex: 9999 // (private)
	, zindexInc: 2 // (private) one for shadow
	, expandCount: 0
	, expanding: false
	, monitorWindowResize: true
	, resizable: true // global resizable flag
	, draggable: true // global draggable flag
	// }}}
	// {{{
	/**
		* Adds the panel to Accordion
		* @param {Ext.ux.InfoPanel} panel Panel to add
		* @return {Ext.ux.InfoPanel} added panel
		*/
	, add: function(panel) {


		// append panel to body
		this.body.appendChild(panel.el);

		panel.docked = true;

		// add docked class to panel body
//		panel.body.addClass('x-dock-panel-body-docked');
//		panel.body.removeClass('x-dock-panel-body-undocked');
		panel.body.replaceClass('x-dock-panel-body-undocked', 'x-dock-panel-body-docked');

		// add panel to items collection
		this.items.add(panel.el.id, panel);

		// relay these events from panel to dock
		this.relayEvents(panel, [
			'beforecollapse'
			, 'collapse'
			, 'beforeexpand'
			, 'expand'
			, 'animationcompleted'
			, 'pinned'
			, 'boxchange'
			, 'destroy'
		]);

		// panel dragging
		if(this.draggable) {
			panel.dd = new Ext.ux.Accordion.DDDock(panel, 'dock-' + this.id, this);
		}

		// panel resizing
		panel.resizable = this.resizable;
//		panel.setResizable(this.resizable);

		// shadow and animate flags
		panel.useShadow = this.useShadow;
		panel.setShadow(this.useShadow);
		if(panel.shadow) {
			panel.shadow.hide();
		}
		panel.animate = undefined === panel.animate ? this.animate : panel.animate;

		// z-index for panel
		this.zindex += this.zindexInc;
		panel.zindex = this.zindex;

		// onclick handler for panel body (allows raising when panel body is clicked)
		panel.body.on('click', this.onClickPanelBody.createDelegate(this, [panel]));

		if(this.fitHeight) {
			this.setPanelHeight(panel);
		}

		panel.dock = this;
		panel.desktop = this.desktop;

		return panel;

	}
	// }}}
	// {{{
	/**
		* Called internally to raise panel above others
		* Maintains z-index stack
		* @param {Ext.ux.InfoPanel} panel Panel to raise
		*/
	, raise: function(panel) {
		this.items.each(function(p) {
			if(p.zindex > panel.zindex) {
				p.zindex -= this.zindexInc;
				p.el.applyStyles({'z-index':p.zindex});
				if(!p.docked) {
					p.setShadow(true);
				}
			}
		}, this);
		panel.zindex = this.zindex;
		panel.el.applyStyles({'z-index':panel.zindex});
		if(this.desktop.lastChild !== panel.el.dom) {
			this.desktop.appendChild(panel.el.dom);
		}
		if(!panel.docked) {
			panel.setShadow(true);
		}
	}
	// }}}
	// {{{
	/**
		* Resets the order of panels within the dock
		*
		* @return {Ext.ux.Accordion} this
		*/
	, resetOrder: function() {
		this.items.each(function(panel) {
			if(!panel.docked) {
				return;
			}
			this.body.appendChild(panel.el);
		}, this);
		this.updateOrder();
		return this;
	}
	// }}}
	// {{{
	/**
		* Called internally to update the order variable after dragging
		*/
	, updateOrder: function() {
		var order = [];
		var titles = this.body.select('.x-layout-panel-hd');
		titles.each(function(titleEl){
			order.push(titleEl.dom.parentNode.id);
		});
		this.order = order;
		this.fireEvent('orderchange', this, order);
	}
	// }}}
	// {{{
	/**
		* Returns array of panel ids in the current order
		* @return {Array} order of panels
		*/
	, getOrder: function() {
		return this.order;
	}
	// }}}
	// {{{
	/**
		* Set the order of panels
		* @param {Array} order Array of ids of panels in required order.
		* @return {Ext.ux.Accordion} this
		*/
	, setOrder: function(order) {
		if('object' !== typeof order || undefined === order.length) {
			throw "setOrder: Argument is not array.";
		}
		var panelEl;
		for(var i = 0; i < order.length; i++) {
			panelEl = Ext.get(order[i]);
			if(panelEl) {
				this.body.appendChild(panelEl);
			}
		}
		this.updateOrder();
		return this;
	}
	// }}}
	// {{{
	/**
		* Collapse all docked panels
		* @param {Boolean} alsoPinned true to first unpin then collapse
		* @param {Ext.ux.InfoPanel} except This panel will not be collapsed.
		* @return {Ext.ux.Accordion} this
		*/
	, collapseAll: function(alsoPinned, except) {
		this.items.each(function(panel) {
			if(panel.docked) {
				panel.pinned = alsoPinned ? false : panel.pinned;
				if(!except || panel !== except) {
					panel.collapse();
				}
			}
		}, this);
		return this;
	}
	// }}}
	// {{{
	/**
		* Expand all docked panels in independent mode
		* @return {Ext.ux.Accordion} this
		*/
	, expandAll: function() {
		if(this.independent) {
			this.items.each(function(panel) {
				if(panel.docked && panel.collapsed) {
					panel.expand();
				}
			}, this);
		}
	}
	// }}}
	// {{{
	/**
		* Called internally while dragging and by state manager
		* @param {Ext.ux.InfoPanel/String} panel Panel object or id of the panel
		* @box {Object} box coordinates with target position and size
		* @return {Ext.ux.Accordion} this
		*/
	, undock: function(panel, box) {

		// get panel if necessary
		panel = 'string' === typeof panel ? this.items.get(panel) : panel;

		// proceed only if we have docked panel and in undockable mode
		if(panel && panel.docked && this.undockable) {

			// sanity check
			if(box.x < 0 || box.y < 0) {
				return this;
			}

			// move the panel in the dom (append to desktop)
			this.desktop.appendChild(panel.el.dom);

			// adjust panel visuals
			panel.el.applyStyles({
				position:'absolute'
				, 'z-index': panel.zindex
			});
			panel.body.replaceClass('x-dock-panel-body-docked', 'x-dock-panel-body-undocked');

			// position the panel
			panel.setBox(box);

			// reset docked flag
			panel.docked = false;

			// hide panel shadow (will be shown by raise)
			if(panel.shadow) {
				panel.shadow.hide();
			}

			// raise panel above others
			this.raise(panel);

			// set the height of a docked expanded panel
			this.setPanelHeight(this.expanded);

			// enable resizing and scrolling
			panel.setResizable(!panel.collapsed);
			if(panel.bodyScroll) {
				panel.body.setStyle('overflow','auto');
			}

			// size the undocked panel
			// todo: revise
			panel.lastWidth = box.width;
			panel.lastHeight = box.height;

			// fire panelundock event
			this.fireEvent('panelundock', panel, {x:box.x, y:box.y, width:box.width, height:box.height});
		}

		return this;
	}
	// }}}
	// {{{
	/**
		* Called internally while dragging
		* @param {Ext.ux.InfoPanel/String} panel Panel object or id of the panel
		* @param {String} targetId id of panel after which this panel will be docked
		* @return {Ext.ux.Accordion} this
		*/
	, dock: function(panel, targetId) {

		// get panel if necessary
		panel = 'string' === typeof panel ? this.items.get(panel) : panel;

		// proceed only if we have a docked panel
		if(panel && !panel.docked) {

			// remember width and height
			if(!panel.collapsed) {
				panel.lastWidth = panel.el.getWidth();
				panel.lastHeight = panel.el.getHeight();
			}

			// move the panel element in the dom
			if(targetId && (this.body.id !== targetId)) {
				panel.el.insertBefore(Ext.fly(targetId));
			}
			else {
				panel.el.appendTo(this.body);
			}

			// set docked flag
			panel.docked = true;

			// adjust panel visuals
			panel.body.replaceClass('x-dock-panel-body-undocked', 'x-dock-panel-body-docked');
			panel.el.applyStyles({
				top:''
				, left:''
				, width:''
				, height:''
				, 'z-index':''
				, position:'relative'
				, visibility:''
			});
			panel.body.applyStyles({width:'',height:''});

			// disable resizing and shadow
			panel.setResizable(false);
			if(panel.shadow) {
				panel.shadow.hide();
			}

			// set panel height (only if this.fitHeight = true)
			this.setPanelHeight(panel.collapsed ? this.expanded : panel);

			// fire paneldock event
			this.fireEvent('paneldock', panel);
		}

		return this;
	}
	// }}}
	// {{{
	/**
		* Sets the independent mode
		* @param {Boolean} independent set to false for normal mode
		* @return {Ext.ux.Accordion} this
		*/
	, setIndependent: function(independent) {
		this.independent = independent ? true : false;
		this.fireEvent('independent', this, independent);
		return this;
	}
	// }}}
	// {{{
	/**
		* Sets the undockable mode
		* If undockable === true all undocked panels are docked and collapsed (except pinned)
		* @param {Boolean} undockable set to true to not allow undocking
		* @return {Ext.ux.Accordion} this
		*/
	, setUndockable: function(undockable) {
		this.items.each(function(panel) {

			// dock and collapse (except pinned) all undocked panels if not undockable
			if(!undockable && !panel.docked) {
				this.dock(panel);
				if(!this.independent && !panel.collapsed && !panel.pinned) {
					panel.collapse();
				}
			}

			// refresh dragging constraints
			if(panel.docked && panel.draggable) {
				panel.dd.constrainTo(this.body, 0, false);
				panel.dd.clearConstraints();
				if(undockable) {
					panel.constrainToDesktop();
				}
				else {
					panel.dd.setXConstraint(0,0);
				}
			}
		}, this);

		// set the flag and fire event
		this.undockable = undockable;
		this.fireEvent('undockable', this, undockable);
		return this;
	}
	// }}}
	// {{{
	/**
		* Restores state of dock and panels
		* @param {Ext.state.Provider} provider (optional) An alternate state provider
		*/
	, restoreState: function(provider) {
		if(!provider) {
			provider = Ext.state.Manager;
		}
		var sm = new Ext.ux.AccordionStateManager();
		sm.init(this, provider);

	}
	// }}}
	// {{{
	/**
		* Sets the shadows for all panels
		* @param {Boolean} shadow set to false to disable shadows
		* @return {Ext.ux.Accordion} this
		*/
	, setShadow: function(shadow) {
		this.items.each(function(panel) {
			panel.useShadow = shadow;
			panel.setShadow(false);
			if(!panel.docked) {
				panel.setShadow(shadow);
			}
		});
		this.useShadow = shadow;
		this.fireEvent('useshadow', this, shadow);
		return this;
	}
	// }}}
// {{{
	/**
		* Called when user clicks the panel body
		* @param {Ext.ux.InfoPanel} panel
		*/
	, onClickPanelBody: function(panel) {
		if(!panel.docked) {
			this.raise(panel);
		}
	}
// }}}
	// {{{
	/**
		* Called internally for fixed height docks to get current height of panel(s)
		*/
	, getPanelBodyHeight: function() {
			var titleHeight = 0;
			this.items.each(function(panel) {
				titleHeight += panel.docked ? panel.titleEl.getHeight() : 0;
			});
			this.panelBodyHeight = this.body.getHeight() - titleHeight - this.body.getFrameWidth('tb') + 1;
//			this.panelBodyHeight = this.body.getHeight() - titleHeight - this.body.getFrameWidth('tb');
			return this.panelBodyHeight;
	}
	// }}}
	// {{{
	/**
		* Sets the height of panel body
		* Used with fixed height (fitHeight:true) docs
		* @param {Ext.ux.InfoPanel} panel (defaults to this.expanded)
		* @return {Ext.ux.Accordion} this
		*/
	, setPanelHeight: function(panel) {
		panel = panel || this.expanded;
		if(this.fitHeight && panel && panel.docked) {
			panel.body.setHeight(this.getPanelBodyHeight());
		}
		return this;
	}
	// }}}
	// {{{
	/**
		* Constrains the dragging of panels do the desktop
		* @return {Ext.ux.Accordion} this
		*/
	, constrainToDesktop: function() {
		this.items.each(function(panel) {
			panel.constrainToDesktop();
		}, this);
		return this;
	}
	// }}}
	// {{{
	/**
		* Clears dragging constraints of panels
		* @return {Ext.ux.Accordion} this
		*/
	, clearConstraints: function() {
		this.items.each(function(panel) {
			panel.dd.clearConstraints();
		});
	}
	// }}}
	// {{{
	/**
		* Shows all panels
		* @param {Boolean} show (optional) if false hides the panels instead of showing
		* @param {Boolean} alsoUndocked show also undocked panels (defaults to false)
		* @return {Ext.ux.Accordion} this
		*/
	, showAll: function(show, alsoUndocked) {
		show = (false === show ? false : true);
		this.items.each(function(panel) {
			panel.show(show, alsoUndocked);
		});
		return this;
	}
	// }}}
	// {{{
	/**
		* Hides all panels
		* @param {Boolean} alsoUndocked hide also undocked panels (defaults to false)
		* @return {Ext.ux.Accordion} this
		*/
	, hideAll: function(alsoUndocked) {
		return this.showAll(false, alsoUndocked);
	}
	// }}}
	// {{{
	/**
		* Called internally to disable/enable scrolling of the dock while animating
		* @param {Boolean} enable true to enable, false to disable
		* @return {void}
		* @todo not used at present - revise
		*/
	, setDockScroll: function(enable) {
		if(enable && !this.fitHeight) {
			this.body.setStyle('overflow','auto');
		}
		else {
			this.body.setStyle('overflow','hidden');
		}
	}
	// }}}
	// {{{
	/**
		* Set Accordion size
		* Overrides ContentPanel.setSize
		*
		* @param {Integer} w width
		* @param {Integer} h height
		* @return {Ext.ux.Accordion} this
		*/
	, setSize: function(w, h) {
		// call parent's setSize
		Ext.ux.Accordion.superclass.setSize.call(this, w, h);
//		this.body.setHeight(h);
		this.setPanelHeight();

		return this;
	}
	// }}}
	// {{{
	/**
		* Called as windowResize event handler
		*
		* @todo: review
		*/
	, adjustViewport: function() {
		var viewport = this.desktop.dom === document.body ? {} : Ext.get(this.desktop).getBox();

		viewport.height =
			this.desktop === document.body
			? window.innerHeight || document.documentElement.clientHeight || document.body.clientHeight
			: viewport.height
		;

		viewport.width =
			this.desktop === document.body
			? window.innerWidth || document.documentElement.clientWidth || document.body.clientWidth
			: viewport.width
		;

		viewport.x = this.desktop === document.body ? 0 : viewport.x;
		viewport.y = this.desktop === document.body ? 0 : viewport.y;

		this.items.each(function(panel) {
			if(!panel.docked) {
				panel.moveToViewport(viewport);
			}
		});

	}
	// }}}

}); // end of extend

// {{{
// {{{
/**
	* @class Ext.ux.Accordion.DDDock
	* @constructor
	* @extends Ext.dd.DDProxy
	* @param {Ext.ux.InfoPanel} panel Panel the dragging object is created for
	* @param {String} group Only elements of same group interact
	* @param {Ext.ux.Accordion} dock Place where panels are docked/undocked
	*/
Ext.ux.Accordion.DDDock = function(panel, group, dock) {

	// call parent constructor
	Ext.ux.Accordion.DDDock.superclass.constructor.call(this, panel.el.dom, group);

	// save panel and dock references for use in methods
	this.panel = panel;
	this.dock = dock;

	// drag by grabbing the title only
	this.setHandleElId(panel.titleEl.id);

	// move only in the dock if undockable
	if(false === dock.undockable) {
		this.setXConstraint(0, 0);
	}

	// init internal variables
	this.lastY = 0;

	//this.DDM.mode = Ext.dd.DDM.INTERSECT;
	this.DDM.mode = Ext.dd.DDM.POINT;

}; // end of constructor
// }}}

// extend
Ext.extend(Ext.ux.Accordion.DDDock, Ext.dd.DDProxy, {

	// {{{
	/**
		* Default DDProxy startDrag override
		* Saves some variable for use by other methods
		* and creates nice dragging proxy (ghost)
		*
		* Passed x, y arguments are not used
		*/
	startDrag: function(x, y) {

		this.lastMoveTarget = null;

		// create nice dragging ghost
		this.createGhost();

		// get srcEl (the original) and dragEl (the ghost)
		var srcEl = Ext.get(this.getEl());
		var dragEl = Ext.get(this.getDragEl());

		// refresh constraints
		this.panel.constrainToDesktop();
		var dragHeight, rightC, bottomC;
		if(this.dock.undockable) {
			if(this.panel.collapsed) {
				dragHeight = this.panel.titleEl.getHeight();
			}
			else {
				dragHeight = dragEl.getHeight();
				dragHeight = dragHeight <= this.panel.titleEl.getHeight() ? srcEl.getHeight() : dragHeight;
			}

			rightC = this.rightConstraint + srcEl.getWidth() - dragEl.getWidth();
			bottomC = this.bottomConstraint + srcEl.getHeight() - dragHeight;
			this.setXConstraint(this.leftConstraint, rightC);
			this.setYConstraint(this.topConstraint, bottomC);
		}
		else {
			if(this.panel.docked) {
				this.setXConstraint(0, 0);
			}
		}

		// hide dragEl (will be shown by onDrag)
		dragEl.hide();

		// raise panel's "window" above others
		if(!this.panel.docked) {
			this.dock.raise(this.panel);
		}

		// hide panel's shadow if any
		this.panel.setShadow(false);

		// clear visibility of panel's body (was setup by animations)
		this.panel.body.dom.style.visibility = '';

		// hide source panel if undocked
		if(!this.panel.docked) {
			srcEl.hide();
			dragEl.show();
		}

	} // end of function startDrag
	// }}}
	// {{{
	/**
		* Called internally to create nice dragging proxy (ghost)
		*/
	, createGhost: function() {

		// get variables
		var srcEl = Ext.get(this.getEl());
		var dragEl = Ext.get(this.getDragEl());
		var dock = this.dock;
		var panel = this.panel;

		// adjust look of ghost
		dragEl.addClass('x-dock-panel-ghost');
		dragEl.applyStyles({border:'1px solid #84a0c4','z-index': dock.zindex + dock.zindexInc});

		// set size of ghost same as original
		dragEl.setBox(srcEl.getBox());
		if(panel.docked) {
			if(panel.lastWidth && dock.undockable) {
				dragEl.setWidth(panel.lastWidth);
			}
			if(!panel.collapsed && dock.undockable && (panel.lastHeight > panel.titleEl.getHeight())) {
				dragEl.setHeight(panel.lastHeight);
			}
		}

		// remove unnecessary text nodes from srcEl
		srcEl.clean();

		// setup title
		var dragTitleEl = Ext.DomHelper.append(dragEl, {tag:'div'}, true);
		dragTitleEl.update(srcEl.dom.firstChild.innerHTML);
		dragTitleEl.dom.className = srcEl.dom.firstChild.className;
		if(panel.collapsed && Ext.isIE) {
			dragTitleEl.dom.style.borderBottom = "0";
		}

	} // end of function createGhost
	// }}}
	// {{{
	/**
		* Default DDProxy onDragOver override
		* It is called when dragging over a panel
		* or over the dock.body DropZone
		*
		* @param {Event} e not used
		* @param {String} targetId id of the target we're over
		*
		* Beware: While dragging over docked panels it's called
		* twice. Once for panel and once for DropZone
		*/
	, onDragOver: function(e, targetId) {

		this.currentTarget = targetId;

		// save targetId for use by endDrag
		this.lastTarget = targetId;

		// get panel element
		var srcEl = Ext.get(this.getEl());

		// get target panel
		var targetPanel = this.dock.items.get(targetId);

		// landing indicators
		if(targetPanel) {
			if(targetPanel.docked && (targetPanel.collapsed || !this.panel.docked)) {
				targetPanel.titleEl.addClass('x-dock-panel-title-dragover');
			}
		}
		else {
			if(!this.panel.docked) {
				this.dock.body.addClass('x-dock-body-dragover');
			}
			else {
				this.panel.titleEl.addClass('x-dock-panel-title-dragover');
			}
		}

		// do nothing else if we're not over another docked panel
		if(!targetPanel || !targetPanel.docked) {
			return;
		}

		// reorder panels in dock if we're docked too
		var targetEl;
		if(this.panel.docked) {
			targetEl = targetPanel.el;

			if(targetPanel.collapsed || this.lastMoveTarget !== targetPanel) {
				if(this.movingUp) {
					srcEl.insertBefore(targetEl);
					this.lastMoveTarget = targetPanel;
				}
				else {
					srcEl.insertAfter(targetEl);
					this.lastMoveTarget = targetPanel;
				}
			}
			this.DDM.refreshCache(this.groups);
		}

	} // end of function onDragOver
	// }}}
	// {{{
	/**
		* Called internally when cursor leaves a drop target
		* @param {Ext.Event} e
		* @param {String} targetId id of target we're leaving
		*/
	, onDragOut: function(e, targetId) {

		var targetPanel = this.dock.items.get(targetId);

		if(!targetPanel) {
			this.dock.body.removeClass('x-dock-body-dragover');
			if(this.dock.body.id === targetId) {
				this.panel.titleEl.removeClass('x-dock-panel-title-dragover');
			}
		}
		else {
			targetPanel.titleEl.removeClass('x-dock-panel-title-dragover');
		}
		this.currentTarget = null;
	}
	// }}}
	// {{{
	/**
		* Default DDProxy onDrag override
		*
		* It's called while dragging
		* @param {Event} e used to get coordinates
		*/
	, onDrag: function(e) {

		// get source (original) and proxy (ghost) elements
		var srcEl = Ext.get(this.getEl());
		var dragEl = Ext.get(this.getDragEl());

		if(!dragEl.isVisible()) {
			dragEl.show();
		}

		var y = e.getPageY();

		this.movingUp = this.lastY > y;
		this.lastY = y;

	} // end of function onDrag
	// }}}
	// {{{
	/**
		* Default DDProxy endDrag override
		*
		* Called when dragging is finished
		*/
	, endDrag: function() {

		// get the source (original) and proxy (ghost) elements
		var srcEl = Ext.get(this.getEl());
		var dragEl = Ext.get(this.getDragEl());

		// get box and hide the ghost
		var box = dragEl.getBox();

		// remove any dragover classes from panel title and dock
		this.panel.titleEl.removeClass('x-dock-panel-title-dragover');
		this.dock.body.removeClass('x-dock-body-dragover');

		var targetPanel = this.dock.items.get(this.currentTarget);

		// undock (docked panel dropped out of dock)
		if((this.panel.docked && !this.currentTarget) || (targetPanel && !targetPanel.docked)) {
			this.dock.undock(this.panel, box);
		}

		// dock (undocked panel dropped on dock)
		else if(this.currentTarget) {
			if(targetPanel) {
				targetPanel.titleEl.removeClass('x-dock-panel-title-dragover');
			}
			else {
				this.dock.body.removeClass('x-dock-body-dragover');
			}
			this.dock.dock(this.panel, this.currentTarget);
		}

		// just free dragging
		if(!this.panel.docked) {
			this.panel.setBox(box);
			srcEl.show();
		}

		// clear the ghost content, hide id and move it off screen
		dragEl.hide();
		dragEl.update('');
		dragEl.applyStyles({
			top:'-9999px'
			, left:'-9999px'
			, height:'0px'
			, width:'0px'
		});

		// update order of docked panels
		this.dock.updateOrder();

		// repair the expanded/collapsed states of panels in the dock
		if(!this.panel.collapsed && !this.dock.independent && this.panel.docked) {
			this.dock.collapseAll(false, this.panel);
			this.dock.expanded = this.panel;
		}

		// let the state manager know the new panel position
		this.dock.fireEvent('panelbox', this.panel, {x:box.x, y:box.y, width:box.width, height:box.height});

	} // end of function endDrag
	// }}}

});
// }}}
// {{{
/**
	* Private class for keeping and restoring state of the Accordion
	*/
Ext.ux.AccordionStateManager = function(dock) {
	this.state = {
		dock: {}
		, panels: {}
	};
};

Ext.ux.AccordionStateManager.prototype = {

	// {{{
	init: function(dock, provider) {
		this.provider = provider;
		var panel;
		var state = provider.get(dock.id + '-dock-state');
		if(state) {

			// {{{
			// handle dock
			if(undefined !== state.dock.independent) {
				dock.setIndependent(state.dock.independent);
			}

			if(undefined !== state.dock.undockable) {
				dock.setUndockable(state.dock.undockable);
			}

			if(undefined !== state.dock.useShadow) {
				dock.setShadow(state.dock.useShadow);
			}

			if('object' === typeof state.dock.order && state.dock.order.length) {
				dock.setOrder(state.dock.order);
			}
			// }}}
			// {{{
			// handle panels
			for(var panelId in state.panels) {
				panel = dock.items.get(panelId);
				if(panel) {
					// {{{
					// we need collapsed state early
					panel.collapsed =
						(undefined === typeof state.panels[panelId].collapsed)
						? true
						: state.panels[panelId].collapsed
					;
					// }}}
					// {{{
					// handle docked/undocked
					if(undefined !== typeof state.panels[panelId].docked) {
						if(!state.panels[panelId].docked && 'object' === typeof state.panels[panelId].box) {
							dock.undock(panel, state.panels[panelId].box);
						}
						panel.docked = state.panels[panelId].docked;
					}
					// }}}
					// {{{
					// handle pinned state
					if(undefined !== typeof state.panels[panelId].pinned) {
						panel.pinned = state.panels[panelId].pinned;
					}
					// }}}
					// {{{
					// handle collapsed state
					if(panel.collapsed) {
						panel.collapse();
					}
					else {
						if(!dock.expanded || dock.independent || panel.pinned || !panel.docked) {
							panel.body.show();
							panel.collapsed = false;
							if(!panel.pinned && panel.docked) {
								dock.expanded = panel;
							}
							panel.updateVisuals();
						}
					}
					// }}}

				}
			}
			// }}}

//			dock.setSize(dock.body.getWidth(), dock.body.getHeight());
			dock.setPanelHeight(dock.expanded);

			this.state = state;
		}
		this.dock = dock;

		// install event handlers on dock
		dock.on({
			panelcollapse: {scope: this, fn: this.onPanelCollapse}
			, panelexpand: {scope: this, fn: this.onPanelCollapse}
			, panelpinned: {scope: this, fn: this.onPanelPinned}
			, independent: {scope: this, fn: this.onIndependent}
			, orderchange: {scope: this, fn: this.onOrderChange}
			, undockable: {scope: this, fn: this.onUndockable}
			, paneldock: {scope: this, fn: this.onPanelUnDock}
			, panelundock: {scope: this, fn: this.onPanelUnDock}
			, panelbox: {scope: this, fn: this.onPanelUnDock}
			, boxchange: {scope: this, fn: this.onPanelUnDock}
			, useshadow: {scope: this, fn: this.onUseShadow}
		});

	}
	// }}}
	// {{{
	, onPanelCollapse: function(panel) {
		this.state.panels[panel.id] = this.state.panels[panel.id] || {};
		this.state.panels[panel.id].collapsed = panel.collapsed;
		this.storeState();
	}
	// }}}
	// {{{
	, onPanelPinned: function(panel, pinned) {
		this.state.panels[panel.id] = this.state.panels[panel.id] || {};
		this.state.panels[panel.id].pinned = pinned;
		this.storeState();
	}
	// }}}
	// {{{
	, onPanelUnDock: function(panel, box) {
		this.state.panels[panel.id] = this.state.panels[panel.id] || {};
		this.state.panels[panel.id].docked = panel.docked;
		this.state.panels[panel.id].box = box || null;
		this.storeState();
	}
	// }}}
	// {{{
	, onIndependent: function(dock, independent) {
		this.state.dock.independent = independent;
		this.storeState();
	}
	// }}}
	// {{{
	, onOrderChange: function(dock, order) {
		this.state.dock.order = order;
		this.storeState();
	}
	// }}}
	// {{{
	, onUndockable: function(dock, undockable) {
		this.state.dock.undockable = undockable;
		this.storeState();
	}
	// }}}
	// {{{
	, onUseShadow: function(dock, shadow) {
		this.state.dock.useShadow = shadow;
		this.storeState();
	}
	// }}}
	// {{{
	, storeState: function() {
		this.provider.set.defer(700, this, [this.dock.id + '-dock-state', this.state]);
	}
	// }}}

}; // end of Ext.ux.AccordionStateManager.prototype
// }}}

// end of file
