/*
 * Ext JS Library 2.0 RC 1
 * Copyright(c) 2006-2007, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */


Ext.ComponentMgr=function(){var all=new Ext.util.MixedCollection();var types={};return{register:function(c){all.add(c);},unregister:function(c){all.remove(c);},get:function(id){return all.get(id);},onAvailable:function(id,fn,scope){all.on("add",function(index,o){if(o.id==id){fn.call(scope||o,o);all.un("add",fn,scope);}});},all:all,registerType:function(xtype,cls){types[xtype]=cls;cls.xtype=xtype;},create:function(config,defaultType){return new types[config.xtype||defaultType](config);}};}();Ext.reg=Ext.ComponentMgr.registerType;

Ext.Component=function(config){config=config||{};if(config.initialConfig){if(config.isAction){this.baseAction=config;}
config=config.initialConfig;}else if(config.tagName||config.dom||typeof config=="string"){config={applyTo:config,id:config.id||config};}
this.initialConfig=config;Ext.apply(this,config);this.addEvents('disable','enable','beforeshow','show','beforehide','hide','beforerender','render','beforedestroy','destroy','beforestaterestore','staterestore','beforestatesave','statesave');this.getId();Ext.ComponentMgr.register(this);Ext.Component.superclass.constructor.call(this);if(this.baseAction){this.baseAction.addComponent(this);}
this.initComponent();if(this.plugins){if(this.plugins instanceof Array){for(var i=0,len=this.plugins.length;i<len;i++){this.plugins[i].init(this);}}else{this.plugins.init(this);}}
if(this.stateful!==false){this.initState(config);}
if(this.applyTo){this.applyToMarkup(this.applyTo);delete this.applyTo;}else if(this.renderTo){this.render(this.renderTo);delete this.renderTo;}};Ext.Component.AUTO_ID=1000;Ext.extend(Ext.Component,Ext.util.Observable,{disabledClass:"x-item-disabled",allowDomMove:true,autoShow:false,hideMode:'display',hideParent:false,hidden:false,disabled:false,rendered:false,ctype:"Ext.Component",actionMode:"el",getActionEl:function(){return this[this.actionMode];},initComponent:Ext.emptyFn,render:function(container,position){if(!this.rendered&&this.fireEvent("beforerender",this)!==false){if(!container&&this.el){this.el=Ext.get(this.el);container=this.el.dom.parentNode;this.allowDomMove=false;}
this.container=Ext.get(container);if(this.ctCls){this.container.addClass(this.ctCls);}
this.rendered=true;if(position!==undefined){if(typeof position=='number'){position=this.container.dom.childNodes[position];}else{position=Ext.getDom(position);}}
this.onRender(this.container,position||null);if(this.autoShow){this.el.removeClass(['x-hidden','x-hide-'+this.hideMode]);}
if(this.cls){this.el.addClass(this.cls);delete this.cls;}
if(this.style){this.el.applyStyles(this.style);delete this.style;}
this.fireEvent("render",this);this.afterRender(this.container);if(this.hidden){this.hide();}
if(this.disabled){this.disable();}
this.initStateEvents();}
return this;},initState:function(config){if(Ext.state.Manager){var state=Ext.state.Manager.get(this.stateId||this.id);if(state){if(this.fireEvent('beforestaterestore',this,state)!==false){this.applyState(state);this.fireEvent('staterestore',this,state);}}}},initStateEvents:function(){if(this.stateEvents){for(var i=0,e;e=this.stateEvents[i];i++){this.on(e,this.saveState,this,{delay:100});}}},applyState:function(state,config){if(state){Ext.apply(this,state);}},getState:function(){return null;},saveState:function(){if(Ext.state.Manager){var state=this.getState();if(this.fireEvent('beforestatesave',this,state)!==false){Ext.state.Manager.set(this.stateId||this.id,state);this.fireEvent('statesave',this,state);}}},applyToMarkup:function(el){this.allowDomMove=false;this.el=Ext.get(el);this.render(this.el.dom.parentNode);},addClass:function(cls){if(this.el){this.el.addClass(cls);}else{this.cls=this.cls?this.cls+' '+cls:cls;}},removeClass:function(cls){if(this.el){this.el.removeClass(cls);}else if(this.cls){this.cls=this.cls.split(' ').remove(cls).join(' ');}},onRender:function(ct,position){if(this.autoEl){if(typeof this.autoEl=='string'){this.el=document.createElement(this.autoEl);}else{var div=document.createElement('div');Ext.DomHelper.overwrite(div,this.autoEl);this.el=div.firstChild;}}
if(this.el){this.el=Ext.get(this.el);if(this.allowDomMove!==false){ct.dom.insertBefore(this.el.dom,position);}}},getAutoCreate:function(){var cfg=typeof this.autoCreate=="object"?this.autoCreate:Ext.apply({},this.defaultAutoCreate);if(this.id&&!cfg.id){cfg.id=this.id;}
return cfg;},afterRender:Ext.emptyFn,destroy:function(){if(this.fireEvent("beforedestroy",this)!==false){this.beforeDestroy();if(this.rendered){this.el.removeAllListeners();this.el.remove();if(this.actionMode=="container"){this.container.remove();}}
this.onDestroy();Ext.ComponentMgr.unregister(this);this.fireEvent("destroy",this);this.purgeListeners();}},beforeDestroy:Ext.emptyFn,onDestroy:Ext.emptyFn,getEl:function(){return this.el;},getId:function(){return this.id||(this.id="ext-comp-"+(++Ext.Component.AUTO_ID));},getItemId:function(){return this.itemId||this.getId();},focus:function(selectText,delay){if(delay){this.focus.defer(typeof delay=='number'?delay:10,this,[selectText,false]);return;}
if(this.rendered){this.el.focus();if(selectText===true){this.el.dom.select();}}
return this;},blur:function(){if(this.rendered){this.el.blur();}
return this;},disable:function(){if(this.rendered){this.onDisable();}
this.disabled=true;this.fireEvent("disable",this);return this;},onDisable:function(){this.getActionEl().addClass(this.disabledClass);this.el.dom.disabled=true;},enable:function(){if(this.rendered){this.onEnable();}
this.disabled=false;this.fireEvent("enable",this);return this;},onEnable:function(){this.getActionEl().removeClass(this.disabledClass);this.el.dom.disabled=false;},setDisabled:function(disabled){this[disabled?"disable":"enable"]();},show:function(){if(this.fireEvent("beforeshow",this)!==false){this.hidden=false;if(this.autoRender){this.render(typeof this.autoRender=='boolean'?Ext.getBody():this.autoRender);}
if(this.rendered){this.onShow();}
this.fireEvent("show",this);}
return this;},onShow:function(){if(this.hideParent){this.container.removeClass('x-hide-'+this.hideMode);}else{this.getActionEl().removeClass('x-hide-'+this.hideMode);}},hide:function(){if(this.fireEvent("beforehide",this)!==false){this.hidden=true;if(this.rendered){this.onHide();}
this.fireEvent("hide",this);}
return this;},onHide:function(){if(this.hideParent){this.container.addClass('x-hide-'+this.hideMode);}else{this.getActionEl().addClass('x-hide-'+this.hideMode);}},setVisible:function(visible){if(visible){this.show();}else{this.hide();}
return this;},isVisible:function(){return this.rendered&&this.getActionEl().isVisible();},cloneConfig:function(overrides){overrides=overrides||{};var id=overrides.id||Ext.id();var cfg=Ext.applyIf(overrides,this.initialConfig);cfg.id=id;return new this.constructor(cfg);},getXType:function(){return this.constructor.xtype;},isXType:function(xtype,shallow){return!shallow?('/'+this.getXTypes()+'/').indexOf('/'+xtype+'/')!=-1:this.constructor.xtype==xtype;},getXTypes:function(){var tc=this.constructor;if(!tc.xtypes){var c=[],sc=this;while(sc&&sc.constructor.xtype){c.unshift(sc.constructor.xtype);sc=sc.constructor.superclass;}
tc.xtypeChain=c;tc.xtypes=c.join('/');}
return tc.xtypes;}});Ext.reg('component',Ext.Component);

Ext.BoxComponent=Ext.extend(Ext.Component,{initComponent:function(){Ext.BoxComponent.superclass.initComponent.call(this);this.addEvents('resize','move');},boxReady:false,deferHeight:false,setSize:function(w,h){if(typeof w=='object'){h=w.height;w=w.width;}
if(!this.boxReady){this.width=w;this.height=h;return this;}
if(this.lastSize&&this.lastSize.width==w&&this.lastSize.height==h){return this;}
this.lastSize={width:w,height:h};var adj=this.adjustSize(w,h);var aw=adj.width,ah=adj.height;if(aw!==undefined||ah!==undefined){var rz=this.getResizeEl();if(!this.deferHeight&&aw!==undefined&&ah!==undefined){rz.setSize(aw,ah);}else if(!this.deferHeight&&ah!==undefined){rz.setHeight(ah);}else if(aw!==undefined){rz.setWidth(aw);}
this.onResize(aw,ah,w,h);this.fireEvent('resize',this,aw,ah,w,h);}
return this;},setWidth:function(width){return this.setSize(width);},setHeight:function(height){return this.setSize(undefined,height);},getSize:function(){return this.el.getSize();},getPosition:function(local){if(local===true){return[this.el.getLeft(true),this.el.getTop(true)];}
return this.xy||this.el.getXY();},getBox:function(local){var s=this.el.getSize();if(local===true){s.x=this.el.getLeft(true);s.y=this.el.getTop(true);}else{var xy=this.xy||this.el.getXY();s.x=xy[0];s.y=xy[1];}
return s;},updateBox:function(box){this.setSize(box.width,box.height);this.setPagePosition(box.x,box.y);return this;},getResizeEl:function(){return this.resizeEl||this.el;},getPositionEl:function(){return this.positionEl||this.el;},setPosition:function(x,y){if(x&&typeof x[1]=='number'){y=x[1];x=x[0];}
this.x=x;this.y=y;if(!this.boxReady){return this;}
var adj=this.adjustPosition(x,y);var ax=adj.x,ay=adj.y;var el=this.getPositionEl();if(ax!==undefined||ay!==undefined){if(ax!==undefined&&ay!==undefined){el.setLeftTop(ax,ay);}else if(ax!==undefined){el.setLeft(ax);}else if(ay!==undefined){el.setTop(ay);}
this.onPosition(ax,ay);this.fireEvent('move',this,ax,ay);}
return this;},setPagePosition:function(x,y){if(x&&typeof x[1]=='number'){y=x[1];x=x[0];}
this.pageX=x;this.pageY=y;if(!this.boxReady){return;}
if(x===undefined||y===undefined){return;}
var p=this.el.translatePoints(x,y);this.setPosition(p.left,p.top);return this;},onRender:function(ct,position){Ext.BoxComponent.superclass.onRender.call(this,ct,position);if(this.resizeEl){this.resizeEl=Ext.get(this.resizeEl);}
if(this.positionEl){this.positionEl=Ext.get(this.positionEl);}},afterRender:function(){Ext.BoxComponent.superclass.afterRender.call(this);this.boxReady=true;this.setSize(this.width,this.height);if(this.x||this.y){this.setPosition(this.x,this.y);}else if(this.pageX||this.pageY){this.setPagePosition(this.pageX,this.pageY);}},syncSize:function(){delete this.lastSize;this.setSize(this.autoWidth?undefined:this.el.getWidth(),this.autoHeight?undefined:this.el.getHeight());return this;},onResize:function(adjWidth,adjHeight,rawWidth,rawHeight){},onPosition:function(x,y){},adjustSize:function(w,h){if(this.autoWidth){w='auto';}
if(this.autoHeight){h='auto';}
return{width:w,height:h};},adjustPosition:function(x,y){return{x:x,y:y};}});Ext.reg('box',Ext.BoxComponent);

(function(){Ext.Layer=function(config,existingEl){config=config||{};var dh=Ext.DomHelper;var cp=config.parentEl,pel=cp?Ext.getDom(cp):document.body;if(existingEl){this.dom=Ext.getDom(existingEl);}
if(!this.dom){var o=config.dh||{tag:"div",cls:"x-layer"};this.dom=dh.append(pel,o);}
if(config.cls){this.addClass(config.cls);}
this.constrain=config.constrain!==false;this.visibilityMode=Ext.Element.VISIBILITY;if(config.id){this.id=this.dom.id=config.id;}else{this.id=Ext.id(this.dom);}
this.zindex=config.zindex||this.getZIndex();this.position("absolute",this.zindex);if(config.shadow){this.shadowOffset=config.shadowOffset||4;this.shadow=new Ext.Shadow({offset:this.shadowOffset,mode:config.shadow});}else{this.shadowOffset=0;}
this.useShim=config.shim!==false&&Ext.useShims;this.useDisplay=config.useDisplay;this.hide();};var supr=Ext.Element.prototype;var shims=[];Ext.extend(Ext.Layer,Ext.Element,{getZIndex:function(){return this.zindex||parseInt(this.getStyle("z-index"),10)||11000;},getShim:function(){if(!this.useShim){return null;}
if(this.shim){return this.shim;}
var shim=shims.shift();if(!shim){shim=this.createShim();shim.enableDisplayMode('block');shim.dom.style.display='none';shim.dom.style.visibility='visible';}
var pn=this.dom.parentNode;if(shim.dom.parentNode!=pn){pn.insertBefore(shim.dom,this.dom);}
shim.setStyle('z-index',this.getZIndex()-2);this.shim=shim;return shim;},hideShim:function(){if(this.shim){this.shim.setDisplayed(false);shims.push(this.shim);delete this.shim;}},disableShadow:function(){if(this.shadow){this.shadowDisabled=true;this.shadow.hide();this.lastShadowOffset=this.shadowOffset;this.shadowOffset=0;}},enableShadow:function(show){if(this.shadow){this.shadowDisabled=false;this.shadowOffset=this.lastShadowOffset;delete this.lastShadowOffset;if(show){this.sync(true);}}},sync:function(doShow){var sw=this.shadow;if(!this.updating&&this.isVisible()&&(sw||this.useShim)){var sh=this.getShim();var w=this.getWidth(),h=this.getHeight();var l=this.getLeft(true),t=this.getTop(true);if(sw&&!this.shadowDisabled){if(doShow&&!sw.isVisible()){sw.show(this);}else{sw.realign(l,t,w,h);}
if(sh){if(doShow){sh.show();}
var a=sw.adjusts,s=sh.dom.style;s.left=(Math.min(l,l+a.l))+"px";s.top=(Math.min(t,t+a.t))+"px";s.width=(w+a.w)+"px";s.height=(h+a.h)+"px";}}else if(sh){if(doShow){sh.show();}
sh.setSize(w,h);sh.setLeftTop(l,t);}}},destroy:function(){this.hideShim();if(this.shadow){this.shadow.hide();}
this.removeAllListeners();Ext.removeNode(this.dom);Ext.Element.uncache(this.id);},remove:function(){this.destroy();},beginUpdate:function(){this.updating=true;},endUpdate:function(){this.updating=false;this.sync(true);},hideUnders:function(negOffset){if(this.shadow){this.shadow.hide();}
this.hideShim();},constrainXY:function(){if(this.constrain){var vw=Ext.lib.Dom.getViewWidth(),vh=Ext.lib.Dom.getViewHeight();var s=Ext.getDoc().getScroll();var xy=this.getXY();var x=xy[0],y=xy[1];var w=this.dom.offsetWidth+this.shadowOffset,h=this.dom.offsetHeight+this.shadowOffset;var moved=false;if((x+w)>vw+s.left){x=vw-w-this.shadowOffset;moved=true;}
if((y+h)>vh+s.top){y=vh-h-this.shadowOffset;moved=true;}
if(x<s.left){x=s.left;moved=true;}
if(y<s.top){y=s.top;moved=true;}
if(moved){if(this.avoidY){var ay=this.avoidY;if(y<=ay&&(y+h)>=ay){y=ay-h-5;}}
xy=[x,y];this.storeXY(xy);supr.setXY.call(this,xy);this.sync();}}},isVisible:function(){return this.visible;},showAction:function(){this.visible=true;if(this.useDisplay===true){this.setDisplayed("");}else if(this.lastXY){supr.setXY.call(this,this.lastXY);}else if(this.lastLT){supr.setLeftTop.call(this,this.lastLT[0],this.lastLT[1]);}},hideAction:function(){this.visible=false;if(this.useDisplay===true){this.setDisplayed(false);}else{this.setLeftTop(-10000,-10000);}},setVisible:function(v,a,d,c,e){if(v){this.showAction();}
if(a&&v){var cb=function(){this.sync(true);if(c){c();}}.createDelegate(this);supr.setVisible.call(this,true,true,d,cb,e);}else{if(!v){this.hideUnders(true);}
var cb=c;if(a){cb=function(){this.hideAction();if(c){c();}}.createDelegate(this);}
supr.setVisible.call(this,v,a,d,cb,e);if(v){this.sync(true);}else if(!a){this.hideAction();}}},storeXY:function(xy){delete this.lastLT;this.lastXY=xy;},storeLeftTop:function(left,top){delete this.lastXY;this.lastLT=[left,top];},beforeFx:function(){this.beforeAction();return Ext.Layer.superclass.beforeFx.apply(this,arguments);},afterFx:function(){Ext.Layer.superclass.afterFx.apply(this,arguments);this.sync(this.isVisible());},beforeAction:function(){if(!this.updating&&this.shadow){this.shadow.hide();}},setLeft:function(left){this.storeLeftTop(left,this.getTop(true));supr.setLeft.apply(this,arguments);this.sync();},setTop:function(top){this.storeLeftTop(this.getLeft(true),top);supr.setTop.apply(this,arguments);this.sync();},setLeftTop:function(left,top){this.storeLeftTop(left,top);supr.setLeftTop.apply(this,arguments);this.sync();},setXY:function(xy,a,d,c,e){this.fixDisplay();this.beforeAction();this.storeXY(xy);var cb=this.createCB(c);supr.setXY.call(this,xy,a,d,cb,e);if(!a){cb();}},createCB:function(c){var el=this;return function(){el.constrainXY();el.sync(true);if(c){c();}};},setX:function(x,a,d,c,e){this.setXY([x,this.getY()],a,d,c,e);},setY:function(y,a,d,c,e){this.setXY([this.getX(),y],a,d,c,e);},setSize:function(w,h,a,d,c,e){this.beforeAction();var cb=this.createCB(c);supr.setSize.call(this,w,h,a,d,cb,e);if(!a){cb();}},setWidth:function(w,a,d,c,e){this.beforeAction();var cb=this.createCB(c);supr.setWidth.call(this,w,a,d,cb,e);if(!a){cb();}},setHeight:function(h,a,d,c,e){this.beforeAction();var cb=this.createCB(c);supr.setHeight.call(this,h,a,d,cb,e);if(!a){cb();}},setBounds:function(x,y,w,h,a,d,c,e){this.beforeAction();var cb=this.createCB(c);if(!a){this.storeXY([x,y]);supr.setXY.call(this,[x,y]);supr.setSize.call(this,w,h,a,d,cb,e);cb();}else{supr.setBounds.call(this,x,y,w,h,a,d,cb,e);}
return this;},setZIndex:function(zindex){this.zindex=zindex;this.setStyle("z-index",zindex+2);if(this.shadow){this.shadow.setZIndex(zindex+1);}
if(this.shim){this.shim.setStyle("z-index",zindex);}}});})();

Ext.Shadow=function(config){Ext.apply(this,config);if(typeof this.mode!="string"){this.mode=this.defaultMode;}
var o=this.offset,a={h:0};var rad=Math.floor(this.offset/2);switch(this.mode.toLowerCase()){case"drop":a.w=0;a.l=a.t=o;a.t-=1;if(Ext.isIE){a.l-=this.offset+rad;a.t-=this.offset+rad;a.w-=rad;a.h-=rad;a.t+=1;}
break;case"sides":a.w=(o*2);a.l=-o;a.t=o-1;if(Ext.isIE){a.l-=(this.offset-rad);a.t-=this.offset+rad;a.l+=1;a.w-=(this.offset-rad)*2;a.w-=rad+1;a.h-=1;}
break;case"frame":a.w=a.h=(o*2);a.l=a.t=-o;a.t+=1;a.h-=2;if(Ext.isIE){a.l-=(this.offset-rad);a.t-=(this.offset-rad);a.l+=1;a.w-=(this.offset+rad+1);a.h-=(this.offset+rad);a.h+=1;}
break;};this.adjusts=a;};Ext.Shadow.prototype={offset:4,defaultMode:"drop",show:function(target){target=Ext.get(target);if(!this.el){this.el=Ext.Shadow.Pool.pull();if(this.el.dom.nextSibling!=target.dom){this.el.insertBefore(target);}}
this.el.setStyle("z-index",this.zIndex||parseInt(target.getStyle("z-index"),10)-1);if(Ext.isIE){this.el.dom.style.filter="progid:DXImageTransform.Microsoft.alpha(opacity=50) progid:DXImageTransform.Microsoft.Blur(pixelradius="+(this.offset)+")";}
this.realign(target.getLeft(true),target.getTop(true),target.getWidth(),target.getHeight());this.el.dom.style.display="block";},isVisible:function(){return this.el?true:false;},realign:function(l,t,w,h){if(!this.el){return;}
var a=this.adjusts,d=this.el.dom,s=d.style;var iea=0;s.left=(l+a.l)+"px";s.top=(t+a.t)+"px";var sw=(w+a.w),sh=(h+a.h),sws=sw+"px",shs=sh+"px";if(s.width!=sws||s.height!=shs){s.width=sws;s.height=shs;if(!Ext.isIE){var cn=d.childNodes;var sww=Math.max(0,(sw-12))+"px";cn[0].childNodes[1].style.width=sww;cn[1].childNodes[1].style.width=sww;cn[2].childNodes[1].style.width=sww;cn[1].style.height=Math.max(0,(sh-12))+"px";}}},hide:function(){if(this.el){this.el.dom.style.display="none";Ext.Shadow.Pool.push(this.el);delete this.el;}},setZIndex:function(z){this.zIndex=z;if(this.el){this.el.setStyle("z-index",z);}}};Ext.Shadow.Pool=function(){var p=[];var markup=Ext.isIE?'<div class="x-ie-shadow"></div>':'<div class="x-shadow"><div class="xst"><div class="xstl"></div><div class="xstc"></div><div class="xstr"></div></div><div class="xsc"><div class="xsml"></div><div class="xsmc"></div><div class="xsmr"></div></div><div class="xsb"><div class="xsbl"></div><div class="xsbc"></div><div class="xsbr"></div></div></div>';return{pull:function(){var sh=p.shift();if(!sh){sh=Ext.get(Ext.DomHelper.insertHtml("beforeBegin",document.body.firstChild,markup));sh.autoBoxAdjust=false;}
return sh;},push:function(sh){p.push(sh);}};}();

Ext.Container=Ext.extend(Ext.BoxComponent,{autoDestroy:true,defaultType:'panel',initComponent:function(){Ext.Container.superclass.initComponent.call(this);this.addEvents('afterlayout','beforeadd','beforeremove','add','remove');var items=this.items;if(items){delete this.items;if(items instanceof Array){this.add.apply(this,items);}else{this.add(items);}}},initItems:function(){if(!this.items){this.items=new Ext.util.MixedCollection(false,this.getComponentId);this.getLayout();}},setLayout:function(layout){if(this.layout&&this.layout!=layout){this.layout.setContainer(null);}
this.initItems();this.layout=layout;layout.setContainer(this);},render:function(){Ext.Container.superclass.render.apply(this,arguments);if(this.layout){if(typeof this.layout=='string'){this.layout=new Ext.Container.LAYOUTS[this.layout.toLowerCase()](this.layoutConfig);}
this.setLayout(this.layout);if(this.activeItem!==undefined){var item=this.activeItem;delete this.activeItem;this.layout.setActiveItem(item);return;}}
if(!this.ownerCt){this.doLayout();}
if(this.monitorResize===true){Ext.EventManager.onWindowResize(this.doLayout,this);}},getLayoutTarget:function(){return this.el;},getComponentId:function(comp){return comp.itemId||comp.id;},add:function(comp){if(!this.items){this.initItems();}
var a=arguments,len=a.length;if(len>1){for(var i=0;i<len;i++){this.add(a[i]);}
return;}
var c=this.lookupComponent(this.applyDefaults(comp));var pos=this.items.length;if(this.fireEvent('beforeadd',this,c,pos)!==false&&this.onBeforeAdd(c)!==false){this.items.add(c);c.ownerCt=this;this.fireEvent('add',this,c,pos);}
return c;},insert:function(index,comp){if(!this.items){this.initItems();}
var a=arguments,len=a.length;if(len>2){for(var i=len-1;i>=1;--i){this.insert(index,a[i]);}
return;}
var c=this.lookupComponent(this.applyDefaults(comp));if(c.ownerCt==this&&this.items.indexOf(c)<index){--index;}
if(this.fireEvent('beforeadd',this,c,index)!==false&&this.onBeforeAdd(c)!==false){this.items.insert(index,c);c.ownerCt=this;this.fireEvent('add',this,c,index);}
return c;},applyDefaults:function(c){if(this.defaults){if(typeof c=='string'){c=Ext.ComponentMgr.get(c);Ext.apply(c,this.defaults);}else if(!c.events){Ext.applyIf(c,this.defaults);}else{Ext.apply(c,this.defaults);}}
return c;},onBeforeAdd:function(item){if(item.ownerCt){item.ownerCt.remove(item,false);}
if(this.hideBorders===true){item.border=(item.border===true);}},remove:function(comp,autoDestroy){var c=this.getComponent(comp);if(c&&this.fireEvent('beforeremove',this,c)!==false){this.items.remove(c);delete c.ownerCt;if(autoDestroy===true||(autoDestroy!==false&&this.autoDestroy)){c.destroy();}
if(this.layout&&this.layout.activeItem==c){delete this.layout.activeItem;}
this.fireEvent('remove',this,c);}
return c;},getComponent:function(comp){if(typeof comp=='object'){return comp;}
return this.items.get(comp);},lookupComponent:function(comp){if(typeof comp=='string'){return Ext.ComponentMgr.get(comp);}else if(!comp.events){return this.createComponent(comp);}
return comp;},createComponent:function(config){return Ext.ComponentMgr.create(config,this.defaultType);},doLayout:function(){if(this.rendered&&this.layout){this.layout.layout();}
if(this.items){var cs=this.items.items;for(var i=0,len=cs.length;i<len;i++){var c=cs[i];if(c.doLayout){c.doLayout();}}}},getLayout:function(){if(!this.layout){var layout=new Ext.layout.ContainerLayout(this.layoutConfig);this.setLayout(layout);}
return this.layout;},onDestroy:function(){if(this.items){var cs=this.items.items;for(var i=0,len=cs.length;i<len;i++){Ext.destroy(cs[i]);}}
if(this.monitorResize){Ext.EventManager.removeResizeListener(this.doLayout,this);}
Ext.Container.superclass.onDestroy.call(this);},bubble:function(fn,scope,args){var p=this;while(p){if(fn.apply(scope||p,args||[p])===false){break;}
p=p.ownerCt;}},cascade:function(fn,scope,args){if(fn.apply(scope||this,args||[this])!==false){if(this.items){var cs=this.items.items;for(var i=0,len=cs.length;i<len;i++){if(cs[i].cascade){cs[i].cascade(fn,scope,args);}else{fn.apply(scope||this,args||[cs[i]]);}}}}},findById:function(id){var m,ct=this;this.cascade(function(c){if(ct!=c&&c.id===id){m=c;return false;}});return m||null;},findByType:function(xtype){return typeof xtype=='function'?this.findBy(function(c){return c.constructor===xtype;}):this.findBy(function(c){return c.constructor.xtype===xtype;});},find:function(prop,value){return this.findBy(function(c){return c[prop]===value;});},findBy:function(fn,scope){var m=[],ct=this;this.cascade(function(c){if(ct!=c&&fn.call(scope||c,c,ct)===true){m.push(c);}});return m;}});Ext.Container.LAYOUTS={};Ext.reg('container',Ext.Container);

Ext.Panel=Ext.extend(Ext.Container,{baseCls:'x-panel',collapsedCls:'x-panel-collapsed',maskDisabled:true,animCollapse:Ext.enableFx,headerAsText:true,buttonAlign:'right',collapsed:false,collapseFirst:true,minButtonWidth:75,elements:'body',toolTarget:'header',collapseEl:'bwrap',slideAnchor:'t',deferHeight:true,expandDefaults:{duration:.25},collapseDefaults:{duration:.25},initComponent:function(){Ext.Panel.superclass.initComponent.call(this);this.addEvents('bodyresize','titlechange','collapse','expand','beforecollapse','beforeexpand','beforeclose','close','activate','deactivate');if(this.tbar){this.elements+=',tbar';if(typeof this.tbar=='object'){this.topToolbar=this.tbar;}
delete this.tbar;}
if(this.bbar){this.elements+=',bbar';if(typeof this.bbar=='object'){this.bottomToolbar=this.bbar;}
delete this.bbar;}
if(this.header===true){this.elements+=',header';delete this.header;}else if(this.title&&this.header!==false){this.elements+=',header';}
if(this.footer===true){this.elements+=',footer';delete this.footer;}
if(this.buttons){var btns=this.buttons;this.buttons=[];for(var i=0,len=btns.length;i<len;i++){if(btns[i].render){this.buttons.push(btns[i]);}else{this.addButton(btns[i]);}}}
if(this.autoLoad){this.on('render',this.doAutoLoad,this,{delay:10});}},createElement:function(name,pnode){if(this[name]){pnode.appendChild(this[name].dom);return;}
if(name==='bwrap'||this.elements.indexOf(name)!=-1){if(this[name+'Cfg']){this[name]=Ext.fly(pnode).createChild(this[name+'Cfg']);}else{var el=document.createElement('div');el.className=this[name+'Cls'];this[name]=Ext.get(pnode.appendChild(el));}}},onRender:function(ct,position){Ext.Panel.superclass.onRender.call(this,ct,position);this.createClasses();if(this.el){this.el.addClass(this.baseCls);this.header=this.el.down('.'+this.headerCls);this.bwrap=this.el.down('.'+this.bwrapCls);var cp=this.bwrap?this.bwrap:this.el;this.tbar=cp.down('.'+this.tbarCls);this.body=cp.down('.'+this.bodyCls);this.bbar=cp.down('.'+this.bbarCls);this.footer=cp.down('.'+this.footerCls);this.fromMarkup=true;}else{this.el=ct.createChild({id:this.id,cls:this.baseCls},position);}
var el=this.el,d=el.dom;if(this.cls){this.el.addClass(this.cls);}
if(this.buttons){this.elements+=',footer';}
if(this.frame){el.insertHtml('afterBegin',String.format(Ext.Element.boxMarkup,this.baseCls));this.createElement('header',d.firstChild.firstChild.firstChild);this.createElement('bwrap',d);var bw=this.bwrap.dom;var ml=d.childNodes[1],bl=d.childNodes[2];bw.appendChild(ml);bw.appendChild(bl);var mc=bw.firstChild.firstChild.firstChild;this.createElement('tbar',mc);this.createElement('body',mc);this.createElement('bbar',mc);this.createElement('footer',bw.lastChild.firstChild.firstChild);if(!this.footer){this.bwrap.dom.lastChild.className+=' x-panel-nofooter';}}else{this.createElement('header',d);this.createElement('bwrap',d);var bw=this.bwrap.dom;this.createElement('tbar',bw);this.createElement('body',bw);this.createElement('bbar',bw);this.createElement('footer',bw);if(!this.header){this.body.addClass(this.bodyCls+'-noheader');if(this.tbar){this.tbar.addClass(this.tbarCls+'-noheader');}}}
if(this.border===false){this.el.addClass(this.baseCls+'-noborder');this.body.addClass(this.bodyCls+'-noborder');if(this.header){this.header.addClass(this.headerCls+'-noborder');}
if(this.footer){this.footer.addClass(this.footerCls+'-noborder');}
if(this.tbar){this.tbar.addClass(this.tbarCls+'-noborder');}
if(this.bbar){this.bbar.addClass(this.bbarCls+'-noborder');}}
if(this.bodyBorder===false){this.body.addClass(this.bodyCls+'-noborder');}
if(this.bodyStyle){this.body.applyStyles(this.bodyStyle);}
this.bwrap.enableDisplayMode('block');if(this.header){this.header.unselectable();if(this.headerAsText){this.header.dom.innerHTML='<span class="'+this.headerTextCls+'">'+this.header.dom.innerHTML+'</span>';if(this.iconCls){this.setIconClass(this.iconCls);}}}
if(this.floating){this.makeFloating(this.floating);}
if(this.collapsible){this.tools=this.tools?this.tools.slice(0):[];if(!this.hideCollapseTool){this.tools[this.collapseFirst?'unshift':'push']({id:'toggle',handler:this.toggleCollapse,scope:this});}
if(this.titleCollapse&&this.header){this.header.on('click',this.toggleCollapse,this);this.header.setStyle('cursor','pointer');}}
if(this.tools){var ts=this.tools;this.tools={};this.addTool.apply(this,ts);}else{this.tools={};}
if(this.buttons&&this.buttons.length>0){var tb=this.footer.createChild({cls:'x-panel-btns-ct',cn:{cls:"x-panel-btns x-panel-btns-"+this.buttonAlign,html:'<table cellspacing="0"><tbody><tr></tr></tbody></table><div class="x-clear"></div>'}},null,true);var tr=tb.getElementsByTagName('tr')[0];for(var i=0,len=this.buttons.length;i<len;i++){var b=this.buttons[i];var td=document.createElement('td');td.className='x-panel-btn-td';b.render(tr.appendChild(td));}}
if(this.tbar&&this.topToolbar){if(this.topToolbar instanceof Array){this.topToolbar=new Ext.Toolbar(this.topToolbar);}
this.topToolbar.render(this.tbar);}
if(this.bbar&&this.bottomToolbar){if(this.bottomToolbar instanceof Array){this.bottomToolbar=new Ext.Toolbar(this.bottomToolbar);}
this.bottomToolbar.render(this.bbar);}},setIconClass:function(cls){var old=this.iconCls;this.iconCls=cls;if(this.rendered){if(this.frame){this.header.addClass('x-panel-icon');this.header.replaceClass(old,this.iconCls);}else{var hd=this.header.dom;var img=hd.firstChild&&String(hd.firstChild.tagName).toLowerCase()=='img'?hd.firstChild:null;if(img){Ext.fly(img).replaceClass(old,this.iconCls);}else{Ext.DomHelper.insertBefore(hd.firstChild,{tag:'img',src:Ext.BLANK_IMAGE_URL,cls:'x-panel-inline-icon '+this.iconCls});}}}},makeFloating:function(cfg){this.floating=true;this.el=new Ext.Layer(typeof cfg=='object'?cfg:{shadow:this.shadow!==undefined?this.shadow:'sides',shadowOffset:this.shadowOffset,constrain:false,shim:this.shim===false?false:undefined},this.el);},getTopToolbar:function(){return this.topToolbar;},getBottomToolbar:function(){return this.bottomToolbar;},addButton:function(config,handler,scope){var bc={handler:handler,scope:scope,minWidth:this.minButtonWidth,hideParent:true};if(typeof config=="string"){bc.text=config;}else{Ext.apply(bc,config);}
var btn=new Ext.Button(bc);if(!this.buttons){this.buttons=[];}
this.buttons.push(btn);return btn;},addTool:function(){if(!this[this.toolTarget]){return;}
if(!this.toolTemplate){var tt=new Ext.Template('<div class="x-tool x-tool-{id}">&#160;</div>');tt.disableFormats=true;tt.compile();Ext.Panel.prototype.toolTemplate=tt;}
for(var i=0,a=arguments,len=a.length;i<len;i++){var tc=a[i],overCls='x-tool-'+tc.id+'-over';var t=this.toolTemplate.insertFirst(this[this.toolTarget],tc,true);this.tools[tc.id]=t;t.enableDisplayMode('block');t.on('click',this.createToolHandler(t,tc,overCls,this));if(tc.on){t.on(tc.on);}
if(tc.hidden){t.hide();}
if(tc.qtip){if(typeof tc.qtip=='object'){Ext.QuickTips.register(Ext.apply({target:t.id},tc.qtip));}else{t.dom.qtip=tc.qtip;}}
t.addClassOnOver(overCls);}},onShow:function(){if(this.floating){return this.el.show();}
Ext.Panel.superclass.onShow.call(this);},onHide:function(){if(this.floating){return this.el.hide();}
Ext.Panel.superclass.onHide.call(this);},createToolHandler:function(t,tc,overCls,panel){return function(e){t.removeClass(overCls);e.stopEvent();if(tc.handler){tc.handler.call(tc.scope||t,e,t,panel);}};},afterRender:function(){if(this.fromMarkup&&this.height===undefined&&!this.autoHeight){this.height=this.el.getHeight();}
if(this.floating&&!this.hidden&&!this.initHidden){this.el.show();}
if(this.title){this.setTitle(this.title);}
if(this.autoScroll){this.body.dom.style.overflow='auto';}
if(this.html){this.body.update(typeof this.html=='object'?Ext.DomHelper.markup(this.html):this.html);delete this.html;}
if(this.contentEl){var ce=Ext.getDom(this.contentEl);Ext.fly(ce).removeClass(['x-hidden','x-hide-display']);this.body.dom.appendChild(ce);}
if(this.collapsed){this.collapsed=false;this.collapse(false);}
Ext.Panel.superclass.afterRender.call(this);this.initEvents();},getKeyMap:function(){if(!this.keyMap){this.keyMap=new Ext.KeyMap(this.el,this.keys);}
return this.keyMap;},initEvents:function(){if(this.keys){this.getKeyMap();}
if(this.draggable){this.initDraggable();}},initDraggable:function(){this.dd=new Ext.Panel.DD(this,typeof this.draggable=='boolean'?null:this.draggable);},beforeEffect:function(){if(this.floating){this.el.beforeAction();}
this.el.addClass('x-panel-animated');},afterEffect:function(){this.syncShadow();this.el.removeClass('x-panel-animated');},createEffect:function(a,cb,scope){var o={scope:scope,block:true};if(a===true){o.callback=cb;return o;}else if(!a.callback){o.callback=cb;}else{o.callback=function(){cb.call(scope);Ext.callback(a.callback,a.scope);};}
return Ext.applyIf(o,a);},collapse:function(animate){if(this.collapsed||this.el.hasFxBlock()||this.fireEvent('beforecollapse',this,animate)===false){return;}
var doAnim=animate===true||(animate!==false&&this.animCollapse);this.beforeEffect();this.onCollapse(doAnim,animate);return this;},onCollapse:function(doAnim,animArg){if(doAnim){this[this.collapseEl].slideOut(this.slideAnchor,Ext.apply(this.createEffect(animArg||true,this.afterCollapse,this),this.collapseDefaults));}else{this[this.collapseEl].hide();this.afterCollapse();}},afterCollapse:function(){this.collapsed=true;this.el.addClass(this.collapsedCls);this.afterEffect();this.fireEvent('collapse',this);},expand:function(animate){if(!this.collapsed||this.el.hasFxBlock()||this.fireEvent('beforeexpand',this,animate)===false){return;}
var doAnim=animate===true||(animate!==false&&this.animCollapse);this.el.removeClass(this.collapsedCls);this.beforeEffect();this.onExpand(doAnim,animate);return this;},onExpand:function(doAnim,animArg){if(doAnim){this[this.collapseEl].slideIn(this.slideAnchor,Ext.apply(this.createEffect(animArg||true,this.afterExpand,this),this.expandDefaults));}else{this[this.collapseEl].show();this.afterExpand();}},afterExpand:function(){this.collapsed=false;this.afterEffect();this.fireEvent('expand',this);},toggleCollapse:function(animate){this[this.collapsed?'expand':'collapse'](animate);return this;},onDisable:function(){if(this.rendered&&this.maskDisabled){this.el.mask();}
Ext.Panel.superclass.onDisable.call(this);},onEnable:function(){if(this.rendered&&this.maskDisabled){this.el.unmask();}
Ext.Panel.superclass.onEnable.call(this);},onResize:function(w,h){if(w!==undefined||h!==undefined){if(!this.collapsed){if(typeof w=='number'){this.body.setWidth(this.adjustBodyWidth(w-this.getFrameWidth()));}else if(w=='auto'){this.body.setWidth(w);}
if(typeof h=='number'){this.body.setHeight(this.adjustBodyHeight(h-this.getFrameHeight()));}else if(h=='auto'){this.body.setHeight(h);}}else{this.queuedBodySize={width:w,height:h};if(!this.queuedExpand&&this.allowQueuedExpand!==false){this.queuedExpand=true;this.on('expand',function(){delete this.queuedExpand;this.onResize(this.queuedBodySize.width,this.queuedBodySize.height);this.doLayout();},this,{single:true});}}
this.fireEvent('bodyresize',this,w,h);}
this.syncShadow();},adjustBodyHeight:function(h){return h;},adjustBodyWidth:function(w){return w;},onPosition:function(){this.syncShadow();},onDestroy:function(){if(this.tools){for(var k in this.tools){Ext.destroy(this.tools[k]);}}
if(this.buttons){for(var b in this.buttons){Ext.destroy(this.buttons[b]);}}
Ext.destroy(this.topToolbar,this.bottomToolbar);Ext.Panel.superclass.onDestroy.call(this);},getFrameWidth:function(){var w=this.el.getFrameWidth('lr');if(this.frame){var l=this.bwrap.dom.firstChild;w+=(Ext.fly(l).getFrameWidth('l')+Ext.fly(l.firstChild).getFrameWidth('r'));var mc=this.bwrap.dom.firstChild.firstChild.firstChild;w+=Ext.fly(mc).getFrameWidth('lr');}
return w;},getFrameHeight:function(){var h=this.el.getFrameWidth('tb');h+=(this.tbar?this.tbar.getHeight():0)+
(this.bbar?this.bbar.getHeight():0);if(this.frame){var hd=this.el.dom.firstChild;var ft=this.bwrap.dom.lastChild;h+=(hd.offsetHeight+ft.offsetHeight);var mc=this.bwrap.dom.firstChild.firstChild.firstChild;h+=Ext.fly(mc).getFrameWidth('tb');}else{h+=(this.header?this.header.getHeight():0)+
(this.footer?this.footer.getHeight():0);}
return h;},getInnerWidth:function(){return this.getSize().width-this.getFrameWidth();},getInnerHeight:function(){return this.getSize().height-this.getFrameHeight();},syncShadow:function(){if(this.floating){this.el.sync(true);}},getLayoutTarget:function(){return this.body;},setTitle:function(title,iconCls){this.title=title;if(this.header&&this.headerAsText){this.header.child('span').update(title);}
if(iconCls){this.setIconClass(iconCls);}
this.fireEvent('titlechange',this,title);return this;},getUpdater:function(){return this.body.getUpdater();},load:function(){var um=this.body.getUpdater();um.update.apply(um,arguments);return this;},beforeDestroy:function(){Ext.Element.uncache(this.header,this.tbar,this.bbar,this.footer,this.body);},createClasses:function(){this.headerCls=this.baseCls+'-header';this.headerTextCls=this.baseCls+'-header-text';this.bwrapCls=this.baseCls+'-bwrap';this.tbarCls=this.baseCls+'-tbar';this.bodyCls=this.baseCls+'-body';this.bbarCls=this.baseCls+'-bbar';this.footerCls=this.baseCls+'-footer';},createGhost:function(cls,useShim,appendTo){var el=document.createElement('div');el.className='x-panel-ghost '+(cls?cls:'');if(this.header){el.appendChild(this.el.dom.firstChild.cloneNode(true));}
Ext.fly(el.appendChild(document.createElement('ul'))).setHeight(this.bwrap.getHeight());el.style.width=this.el.dom.offsetWidth+'px';;if(!appendTo){this.container.dom.appendChild(el);}else{Ext.getDom(appendTo).appendChild(el);}
if(useShim!==false&&this.el.useShim!==false){var layer=new Ext.Layer({shadow:false,useDisplay:true,constrain:false},el);layer.show();return layer;}else{return new Ext.Element(el);}},doAutoLoad:function(){this.body.load(typeof this.autoLoad=='object'?this.autoLoad:{url:this.autoLoad});}});Ext.reg('panel',Ext.Panel);

Ext.Viewport=Ext.extend(Ext.Container,{initComponent:function(){Ext.Viewport.superclass.initComponent.call(this);document.getElementsByTagName('html')[0].className+=' x-viewport';this.el=Ext.getBody();this.el.setHeight=Ext.emptyFn;this.el.setWidth=Ext.emptyFn;this.el.setSize=Ext.emptyFn;this.el.dom.scroll='no';this.allowDomMove=false;this.autoWidth=true;this.autoHeight=true;Ext.EventManager.onWindowResize(this.fireResize,this);this.renderTo=this.el;},fireResize:function(w,h){this.fireEvent('resize',this,w,h,w,h);}});Ext.reg('viewport',Ext.Viewport);
