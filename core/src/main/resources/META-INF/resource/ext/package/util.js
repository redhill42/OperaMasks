/*
 * Ext JS Library 2.0 RC 1
 * Copyright(c) 2006-2007, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */


Ext.util.DelayedTask=function(fn,scope,args){var id=null,d,t;var call=function(){var now=new Date().getTime();if(now-t>=d){clearInterval(id);id=null;fn.apply(scope,args||[]);}};this.delay=function(delay,newFn,newScope,newArgs){if(id&&delay!=d){this.cancel();}
d=delay;t=new Date().getTime();fn=newFn||fn;scope=newScope||scope;args=newArgs||args;if(!id){id=setInterval(call,d);}};this.cancel=function(){if(id){clearInterval(id);id=null;}};};

Ext.util.MixedCollection=function(allowFunctions,keyFn){this.items=[];this.map={};this.keys=[];this.length=0;this.addEvents("clear","add","replace","remove","sort");this.allowFunctions=allowFunctions===true;if(keyFn){this.getKey=keyFn;}
Ext.util.MixedCollection.superclass.constructor.call(this);};Ext.extend(Ext.util.MixedCollection,Ext.util.Observable,{allowFunctions:false,add:function(key,o){if(arguments.length==1){o=arguments[0];key=this.getKey(o);}
if(typeof key=="undefined"||key===null){this.length++;this.items.push(o);this.keys.push(null);}else{var old=this.map[key];if(old){return this.replace(key,o);}
this.length++;this.items.push(o);this.map[key]=o;this.keys.push(key);}
this.fireEvent("add",this.length-1,o,key);return o;},getKey:function(o){return o.id;},replace:function(key,o){if(arguments.length==1){o=arguments[0];key=this.getKey(o);}
var old=this.item(key);if(typeof key=="undefined"||key===null||typeof old=="undefined"){return this.add(key,o);}
var index=this.indexOfKey(key);this.items[index]=o;this.map[key]=o;this.fireEvent("replace",key,old,o);return o;},addAll:function(objs){if(arguments.length>1||objs instanceof Array){var args=arguments.length>1?arguments:objs;for(var i=0,len=args.length;i<len;i++){this.add(args[i]);}}else{for(var key in objs){if(this.allowFunctions||typeof objs[key]!="function"){this.add(key,objs[key]);}}}},each:function(fn,scope){var items=[].concat(this.items);for(var i=0,len=items.length;i<len;i++){if(fn.call(scope||items[i],items[i],i,len)===false){break;}}},eachKey:function(fn,scope){for(var i=0,len=this.keys.length;i<len;i++){fn.call(scope||window,this.keys[i],this.items[i],i,len);}},find:function(fn,scope){for(var i=0,len=this.items.length;i<len;i++){if(fn.call(scope||window,this.items[i],this.keys[i])){return this.items[i];}}
return null;},insert:function(index,key,o){if(arguments.length==2){o=arguments[1];key=this.getKey(o);}
if(index>=this.length){return this.add(key,o);}
this.length++;this.items.splice(index,0,o);if(typeof key!="undefined"&&key!=null){this.map[key]=o;}
this.keys.splice(index,0,key);this.fireEvent("add",index,o,key);return o;},remove:function(o){return this.removeAt(this.indexOf(o));},removeAt:function(index){if(index<this.length&&index>=0){this.length--;var o=this.items[index];this.items.splice(index,1);var key=this.keys[index];if(typeof key!="undefined"){delete this.map[key];}
this.keys.splice(index,1);this.fireEvent("remove",o,key);return o;}
return false;},removeKey:function(key){return this.removeAt(this.indexOfKey(key));},getCount:function(){return this.length;},indexOf:function(o){return this.items.indexOf(o);},indexOfKey:function(key){return this.keys.indexOf(key);},item:function(key){var item=typeof this.map[key]!="undefined"?this.map[key]:this.items[key];return typeof item!='function'||this.allowFunctions?item:null;},itemAt:function(index){return this.items[index];},key:function(key){return this.map[key];},contains:function(o){return this.indexOf(o)!=-1;},containsKey:function(key){return typeof this.map[key]!="undefined";},clear:function(){this.length=0;this.items=[];this.keys=[];this.map={};this.fireEvent("clear");},first:function(){return this.items[0];},last:function(){return this.items[this.length-1];},_sort:function(property,dir,fn){var dsc=String(dir).toUpperCase()=="DESC"?-1:1;fn=fn||function(a,b){return a-b;};var c=[],k=this.keys,items=this.items;for(var i=0,len=items.length;i<len;i++){c[c.length]={key:k[i],value:items[i],index:i};}
c.sort(function(a,b){var v=fn(a[property],b[property])*dsc;if(v==0){v=(a.index<b.index?-1:1);}
return v;});for(var i=0,len=c.length;i<len;i++){items[i]=c[i].value;k[i]=c[i].key;}
this.fireEvent("sort",this);},sort:function(dir,fn){this._sort("value",dir,fn);},keySort:function(dir,fn){this._sort("key",dir,fn||function(a,b){return String(a).toUpperCase()-String(b).toUpperCase();});},getRange:function(start,end){var items=this.items;if(items.length<1){return[];}
start=start||0;end=Math.min(typeof end=="undefined"?this.length-1:end,this.length-1);var r=[];if(start<=end){for(var i=start;i<=end;i++){r[r.length]=items[i];}}else{for(var i=start;i>=end;i--){r[r.length]=items[i];}}
return r;},filter:function(property,value,anyMatch,caseSensitive){if(Ext.isEmpty(value,false)){return this.clone();}
value=this.createValueMatcher(value,anyMatch,caseSensitive);return this.filterBy(function(o){return o&&value.test(o[property]);});},filterBy:function(fn,scope){var r=new Ext.util.MixedCollection();r.getKey=this.getKey;var k=this.keys,it=this.items;for(var i=0,len=it.length;i<len;i++){if(fn.call(scope||this,it[i],k[i])){r.add(k[i],it[i]);}}
return r;},findIndex:function(property,value,start,anyMatch,caseSensitive){if(Ext.isEmpty(value,false)){return-1;}
value=this.createValueMatcher(value,anyMatch,caseSensitive);return this.findIndexBy(function(o){return o&&value.test(o[property]);},null,start);},findIndexBy:function(fn,scope,start){var k=this.keys,it=this.items;for(var i=(start||0),len=it.length;i<len;i++){if(fn.call(scope||this,it[i],k[i])){return i;}}
if(typeof start=='number'&&start>0){for(var i=0;i<start;i++){if(fn.call(scope||this,it[i],k[i])){return i;}}}
return-1;},createValueMatcher:function(value,anyMatch,caseSensitive){if(!value.exec){value=String(value);value=new RegExp((anyMatch===true?'':'^')+Ext.escapeRe(value),caseSensitive?'':'i');}
return value;},clone:function(){var r=new Ext.util.MixedCollection();var k=this.keys,it=this.items;for(var i=0,len=it.length;i<len;i++){r.add(k[i],it[i]);}
r.getKey=this.getKey;return r;}});Ext.util.MixedCollection.prototype.get=Ext.util.MixedCollection.prototype.item;

Ext.util.JSON=new(function(){var useHasOwn={}.hasOwnProperty?true:false;var pad=function(n){return n<10?"0"+n:n;};var m={"\b":'\\b',"\t":'\\t',"\n":'\\n',"\f":'\\f',"\r":'\\r','"':'\\"',"\\":'\\\\'};var encodeString=function(s){if(/["\\\x00-\x1f]/.test(s)){return'"'+s.replace(/([\x00-\x1f\\"])/g,function(a,b){var c=m[b];if(c){return c;}
c=b.charCodeAt();return"\\u00"+
Math.floor(c/16).toString(16)+
(c%16).toString(16);})+'"';}
return'"'+s+'"';};var encodeArray=function(o){var a=["["],b,i,l=o.length,v;for(i=0;i<l;i+=1){v=o[i];switch(typeof v){case"undefined":case"function":case"unknown":break;default:if(b){a.push(',');}
a.push(v===null?"null":Ext.util.JSON.encode(v));b=true;}}
a.push("]");return a.join("");};var encodeDate=function(o){return'"'+o.getFullYear()+"-"+
pad(o.getMonth()+1)+"-"+
pad(o.getDate())+"T"+
pad(o.getHours())+":"+
pad(o.getMinutes())+":"+
pad(o.getSeconds())+'"';};this.encode=function(o){if(typeof o=="undefined"||o===null){return"null";}else if(o instanceof Array){return encodeArray(o);}else if(o instanceof Date){return encodeDate(o);}else if(typeof o=="string"){return encodeString(o);}else if(typeof o=="number"){return isFinite(o)?String(o):"null";}else if(typeof o=="boolean"){return String(o);}else{var a=["{"],b,i,v;for(i in o){if(!useHasOwn||o.hasOwnProperty(i)){v=o[i];switch(typeof v){case"undefined":case"function":case"unknown":break;default:if(b){a.push(',');}
a.push(this.encode(i),":",v===null?"null":this.encode(v));b=true;}}}
a.push("}");return a.join("");}};this.decode=function(json){return eval("("+json+')');};})();Ext.encode=Ext.util.JSON.encode;Ext.decode=Ext.util.JSON.decode;

Ext.util.Format=function(){var trimRe=/^\s+|\s+$/g;return{ellipsis:function(value,len){if(value&&value.length>len){return value.substr(0,len-3)+"...";}
return value;},undef:function(value){return value!==undefined?value:"";},defaultValue:function(value,defaultValue){return value!==undefined&&value!==''?value:defaultValue;},htmlEncode:function(value){return!value?value:String(value).replace(/&/g,"&amp;").replace(/>/g,"&gt;").replace(/</g,"&lt;").replace(/"/g,"&quot;");},htmlDecode:function(value){return!value?value:String(value).replace(/&amp;/g,"&").replace(/&gt;/g,">").replace(/&lt;/g,"<").replace(/&quot;/g,'"');},trim:function(value){return String(value).replace(trimRe,"");},substr:function(value,start,length){return String(value).substr(start,length);},lowercase:function(value){return String(value).toLowerCase();},uppercase:function(value){return String(value).toUpperCase();},capitalize:function(value){return!value?value:value.charAt(0).toUpperCase()+value.substr(1).toLowerCase();},call:function(value,fn){if(arguments.length>2){var args=Array.prototype.slice.call(arguments,2);args.unshift(value);return eval(fn).apply(window,args);}else{return eval(fn).call(window,value);}},usMoney:function(v){v=(Math.round((v-0)*100))/100;v=(v==Math.floor(v))?v+".00":((v*10==Math.floor(v*10))?v+"0":v);v=String(v);var ps=v.split('.');var whole=ps[0];var sub=ps[1]?'.'+ps[1]:'.00';var r=/(\d+)(\d{3})/;while(r.test(whole)){whole=whole.replace(r,'$1'+','+'$2');}
v=whole+sub;if(v.charAt(0)=='-'){return'-$'+v.substr(1);}
return"$"+v;},date:function(v,format){if(!v){return"";}
if(!(v instanceof Date)){v=new Date(Date.parse(v));}
return v.dateFormat(format||"m/d/Y");},dateRenderer:function(format){return function(v){return Ext.util.Format.date(v,format);};},stripTagsRE:/<\/?[^>]+>/gi,stripTags:function(v){return!v?v:String(v).replace(this.stripTagsRE,"");},stripScriptsRe:/(?:<script.*?>)((\n|\r|.)*?)(?:<\/script>)/ig,stripScripts:function(v){return!v?v:String(v).replace(this.stripScriptsRe,"");},fileSize:function(size){if(size<1024){return size+" bytes";}else if(size<1048576){return(Math.round(((size*10)/1024))/10)+" KB";}else{return(Math.round(((size*10)/1048576))/10)+" MB";}},math:function(){var fns={};return function(v,a){if(!fns[a]){fns[a]=new Function('v','return v '+a+';');}
return fns[a](v);}}()};}();

Ext.util.CSS=function(){var rules=null;var doc=document;var camelRe=/(-[a-z])/gi;var camelFn=function(m,a){return a.charAt(1).toUpperCase();};return{createStyleSheet:function(cssText,id){var ss;var head=doc.getElementsByTagName("head")[0];var rules=doc.createElement("style");rules.setAttribute("type","text/css");if(id){rules.setAttribute("id",id);}
if(Ext.isIE){head.appendChild(rules);ss=rules.styleSheet;ss.cssText=cssText;}else{try{rules.appendChild(doc.createTextNode(cssText));}catch(e){rules.cssText=cssText;}
head.appendChild(rules);ss=rules.styleSheet?rules.styleSheet:(rules.sheet||doc.styleSheets[doc.styleSheets.length-1]);}
this.cacheStyleSheet(ss);return ss;},removeStyleSheet:function(id){var existing=doc.getElementById(id);if(existing){existing.parentNode.removeChild(existing);}},swapStyleSheet:function(id,url){this.removeStyleSheet(id);var ss=doc.createElement("link");ss.setAttribute("rel","stylesheet");ss.setAttribute("type","text/css");ss.setAttribute("id",id);ss.setAttribute("href",url);doc.getElementsByTagName("head")[0].appendChild(ss);},refreshCache:function(){return this.getRules(true);},cacheStyleSheet:function(ss){if(!rules){rules={};}
try{var ssRules=ss.cssRules||ss.rules;for(var j=ssRules.length-1;j>=0;--j){rules[ssRules[j].selectorText]=ssRules[j];}}catch(e){}},getRules:function(refreshCache){if(rules==null||refreshCache){rules={};var ds=doc.styleSheets;for(var i=0,len=ds.length;i<len;i++){try{this.cacheStyleSheet(ds[i]);}catch(e){}}}
return rules;},getRule:function(selector,refreshCache){var rs=this.getRules(refreshCache);if(!(selector instanceof Array)){return rs[selector];}
for(var i=0;i<selector.length;i++){if(rs[selector[i]]){return rs[selector[i]];}}
return null;},updateRule:function(selector,property,value){if(!(selector instanceof Array)){var rule=this.getRule(selector);if(rule){rule.style[property.replace(camelRe,camelFn)]=value;return true;}}else{for(var i=0;i<selector.length;i++){if(this.updateRule(selector[i],property,value)){return true;}}}
return false;}};}();

Ext.util.ClickRepeater=function(el,config)
{this.el=Ext.get(el);this.el.unselectable();Ext.apply(this,config);this.addEvents("mousedown","click","mouseup");this.el.on("mousedown",this.handleMouseDown,this);if(this.preventDefault||this.stopDefault){this.el.on("click",function(e){if(this.preventDefault){e.preventDefault();}
if(this.stopDefault){e.stopEvent();}},this);}
if(this.handler){this.on("click",this.handler,this.scope||this);}
Ext.util.ClickRepeater.superclass.constructor.call(this);};Ext.extend(Ext.util.ClickRepeater,Ext.util.Observable,{interval:20,delay:250,preventDefault:true,stopDefault:false,timer:0,handleMouseDown:function(){clearTimeout(this.timer);this.el.blur();if(this.pressClass){this.el.addClass(this.pressClass);}
this.mousedownTime=new Date();Ext.getDoc().on("mouseup",this.handleMouseUp,this);this.el.on("mouseout",this.handleMouseOut,this);this.fireEvent("mousedown",this);this.fireEvent("click",this);if(this.accelerate){this.delay=400;}
this.timer=this.click.defer(this.delay||this.interval,this);},click:function(){this.fireEvent("click",this);this.timer=this.click.defer(this.accelerate?this.easeOutExpo(this.mousedownTime.getElapsed(),400,-390,12000):this.interval,this);},easeOutExpo:function(t,b,c,d){return(t==d)?b+c:c*(-Math.pow(2,-10*t/d)+1)+b;},handleMouseOut:function(){clearTimeout(this.timer);if(this.pressClass){this.el.removeClass(this.pressClass);}
this.el.on("mouseover",this.handleMouseReturn,this);},handleMouseReturn:function(){this.el.un("mouseover",this.handleMouseReturn);if(this.pressClass){this.el.addClass(this.pressClass);}
this.click();},handleMouseUp:function(){clearTimeout(this.timer);this.el.un("mouseover",this.handleMouseReturn);this.el.un("mouseout",this.handleMouseOut);Ext.getDoc().un("mouseup",this.handleMouseUp);this.el.removeClass(this.pressClass);this.fireEvent("mouseup",this);}});

Ext.KeyNav=function(el,config){this.el=Ext.get(el);Ext.apply(this,config);if(!this.disabled){this.disabled=true;this.enable();}};Ext.KeyNav.prototype={disabled:false,defaultEventAction:"stopEvent",forceKeyDown:false,prepareEvent:function(e){var k=e.getKey();var h=this.keyToHandler[k];if(Ext.isSafari&&h&&k>=37&&k<=40){e.stopEvent();}},relay:function(e){var k=e.getKey();var h=this.keyToHandler[k];if(h&&this[h]){if(this.doRelay(e,this[h],h)!==true){e[this.defaultEventAction]();}}},doRelay:function(e,h,hname){return h.call(this.scope||this,e);},enter:false,left:false,right:false,up:false,down:false,tab:false,esc:false,pageUp:false,pageDown:false,del:false,home:false,end:false,keyToHandler:{37:"left",39:"right",38:"up",40:"down",33:"pageUp",34:"pageDown",46:"del",36:"home",35:"end",13:"enter",27:"esc",9:"tab"},enable:function(){if(this.disabled){if(this.forceKeyDown||Ext.isIE||Ext.isAir){this.el.on("keydown",this.relay,this);}else{this.el.on("keydown",this.prepareEvent,this);this.el.on("keypress",this.relay,this);}
this.disabled=false;}},disable:function(){if(!this.disabled){if(this.forceKeyDown||Ext.isIE||Ext.isAir){this.el.un("keydown",this.relay);}else{this.el.un("keydown",this.prepareEvent);this.el.un("keypress",this.relay);}
this.disabled=true;}}};

Ext.KeyMap=function(el,config,eventName){this.el=Ext.get(el);this.eventName=eventName||"keydown";this.bindings=[];if(config){this.addBinding(config);}
this.enable();};Ext.KeyMap.prototype={stopEvent:false,addBinding:function(config){if(config instanceof Array){for(var i=0,len=config.length;i<len;i++){this.addBinding(config[i]);}
return;}
var keyCode=config.key,shift=config.shift,ctrl=config.ctrl,alt=config.alt,fn=config.fn||config.handler,scope=config.scope;if(typeof keyCode=="string"){var ks=[];var keyString=keyCode.toUpperCase();for(var j=0,len=keyString.length;j<len;j++){ks.push(keyString.charCodeAt(j));}
keyCode=ks;}
var keyArray=keyCode instanceof Array;var handler=function(e){if((!shift||e.shiftKey)&&(!ctrl||e.ctrlKey)&&(!alt||e.altKey)){var k=e.getKey();if(keyArray){for(var i=0,len=keyCode.length;i<len;i++){if(keyCode[i]==k){if(this.stopEvent){e.stopEvent();}
fn.call(scope||window,k,e);return;}}}else{if(k==keyCode){if(this.stopEvent){e.stopEvent();}
fn.call(scope||window,k,e);}}}};this.bindings.push(handler);},on:function(key,fn,scope){var keyCode,shift,ctrl,alt;if(typeof key=="object"&&!(key instanceof Array)){keyCode=key.key;shift=key.shift;ctrl=key.ctrl;alt=key.alt;}else{keyCode=key;}
this.addBinding({key:keyCode,shift:shift,ctrl:ctrl,alt:alt,fn:fn,scope:scope})},handleKeyDown:function(e){if(this.enabled){var b=this.bindings;for(var i=0,len=b.length;i<len;i++){b[i].call(this,e);}}},isEnabled:function(){return this.enabled;},enable:function(){if(!this.enabled){this.el.on(this.eventName,this.handleKeyDown,this);this.enabled=true;}},disable:function(){if(this.enabled){this.el.removeListener(this.eventName,this.handleKeyDown,this);this.enabled=false;}}};

Ext.util.TextMetrics=function(){var shared;return{measure:function(el,text,fixedWidth){if(!shared){shared=Ext.util.TextMetrics.Instance(el,fixedWidth);}
shared.bind(el);shared.setFixedWidth(fixedWidth||'auto');return shared.getSize(text);},createInstance:function(el,fixedWidth){return Ext.util.TextMetrics.Instance(el,fixedWidth);}};}();Ext.util.TextMetrics.Instance=function(bindTo,fixedWidth){var ml=new Ext.Element(document.createElement('div'));document.body.appendChild(ml.dom);ml.position('absolute');ml.setLeftTop(-1000,-1000);ml.hide();if(fixedWidth){ml.setWidth(fixedWidth);}
var instance={getSize:function(text){ml.update(text);var s=ml.getSize();ml.update('');return s;},bind:function(el){ml.setStyle(Ext.fly(el).getStyles('font-size','font-style','font-weight','font-family','line-height'));},setFixedWidth:function(width){ml.setWidth(width);},getWidth:function(text){ml.dom.style.width='auto';return this.getSize(text).width;},getHeight:function(text){return this.getSize(text).height;}};instance.bind(bindTo);return instance;};Ext.Element.measureText=Ext.util.TextMetrics.measure;

Ext.util.Observable=function(){if(this.listeners){this.on(this.listeners);delete this.listeners;}};Ext.util.Observable.prototype={fireEvent:function(){if(this.eventsSuspended!==true){var ce=this.events[arguments[0].toLowerCase()];if(typeof ce=="object"){return ce.fire.apply(ce,Array.prototype.slice.call(arguments,1));}}
return true;},filterOptRe:/^(?:scope|delay|buffer|single)$/,addListener:function(eventName,fn,scope,o){if(typeof eventName=="object"){o=eventName;for(var e in o){if(this.filterOptRe.test(e)){continue;}
if(typeof o[e]=="function"){this.addListener(e,o[e],o.scope,o);}else{this.addListener(e,o[e].fn,o[e].scope,o[e]);}}
return;}
o=(!o||typeof o=="boolean")?{}:o;eventName=eventName.toLowerCase();var ce=this.events[eventName]||true;if(typeof ce=="boolean"){ce=new Ext.util.Event(this,eventName);this.events[eventName]=ce;}
ce.addListener(fn,scope,o);},removeListener:function(eventName,fn,scope){var ce=this.events[eventName.toLowerCase()];if(typeof ce=="object"){ce.removeListener(fn,scope);}},purgeListeners:function(){for(var evt in this.events){if(typeof this.events[evt]=="object"){this.events[evt].clearListeners();}}},relayEvents:function(o,events){var createHandler=function(ename){return function(){return this.fireEvent.apply(this,Ext.combine(ename,Array.prototype.slice.call(arguments,0)));};};for(var i=0,len=events.length;i<len;i++){var ename=events[i];if(!this.events[ename]){this.events[ename]=true;};o.on(ename,createHandler(ename),this);}},addEvents:function(o){if(!this.events){this.events={};}
if(typeof o=='string'){for(var i=0,a=arguments,v;v=a[i];i++){if(!this.events[a[i]]){o[a[i]]=true;}}}else{Ext.applyIf(this.events,o);}},hasListener:function(eventName){var e=this.events[eventName];return typeof e=="object"&&e.listeners.length>0;},suspendEvents:function(){this.eventsSuspended=true;},resumeEvents:function(){this.eventsSuspended=false;},getMethodEvent:function(method){if(!this.methodEvents){this.methodEvents={};}
var e=this.methodEvents[method];if(!e){e={};this.methodEvents[method]=e;e.originalFn=this[method];e.methodName=method;e.before=[];e.after=[];var returnValue,v,cancel;var obj=this;var makeCall=function(fn,scope,args){if((v=fn.apply(scope||obj,args))!==undefined){if(typeof v==='object'){if(v.returnValue!==undefined){returnValue=v.returnValue;}else{returnValue=v;}
if(v.cancel===true){cancel=true;}}else if(v===false){cancel=true;}else{returnValue=v;}}}
this[method]=function(){returnValue=v=undefined;cancel=false;var args=Array.prototype.slice.call(arguments,0);for(var i=0,len=e.before.length;i<len;i++){makeCall(e.before[i].fn,e.before[i].scope,args);if(cancel){return returnValue;}}
if((v=e.originalFn.apply(obj,args))!==undefined){returnValue=v;}
for(var i=0,len=e.after.length;i<len;i++){makeCall(e.after[i].fn,e.after[i].scope,args);if(cancel){return returnValue;}}
return returnValue;};}
return e;},beforeMethod:function(method,fn,scope){var e=this.getMethodEvent(method);e.before.push({fn:fn,scope:scope});},afterMethod:function(method,fn,scope){var e=this.getMethodEvent(method);e.after.push({fn:fn,scope:scope});},removeMethodListener:function(method,fn,scope){var e=this.getMethodEvent(method);for(var i=0,len=e.before.length;i<len;i++){if(e.before[i].fn==fn&&e.before[i].scope==scope){e.before.splice(i,1);return;}}
for(var i=0,len=e.after.length;i<len;i++){if(e.after[i].fn==fn&&e.after[i].scope==scope){e.after.splice(i,1);return;}}}};Ext.util.Observable.prototype.on=Ext.util.Observable.prototype.addListener;Ext.util.Observable.prototype.un=Ext.util.Observable.prototype.removeListener;Ext.util.Observable.capture=function(o,fn,scope){o.fireEvent=o.fireEvent.createInterceptor(fn,scope);};Ext.util.Observable.releaseCapture=function(o){o.fireEvent=Ext.util.Observable.prototype.fireEvent;};(function(){var createBuffered=function(h,o,scope){var task=new Ext.util.DelayedTask();return function(){task.delay(o.buffer,h,scope,Array.prototype.slice.call(arguments,0));};};var createSingle=function(h,e,fn,scope){return function(){e.removeListener(fn,scope);return h.apply(scope,arguments);};};var createDelayed=function(h,o,scope){return function(){var args=Array.prototype.slice.call(arguments,0);setTimeout(function(){h.apply(scope,args);},o.delay||10);};};Ext.util.Event=function(obj,name){this.name=name;this.obj=obj;this.listeners=[];};Ext.util.Event.prototype={addListener:function(fn,scope,options){scope=scope||this.obj;if(!this.isListening(fn,scope)){var l=this.createListener(fn,scope,options);if(!this.firing){this.listeners.push(l);}else{this.listeners=this.listeners.slice(0);this.listeners.push(l);}}},createListener:function(fn,scope,o){o=o||{};scope=scope||this.obj;var l={fn:fn,scope:scope,options:o};var h=fn;if(o.delay){h=createDelayed(h,o,scope);}
if(o.single){h=createSingle(h,this,fn,scope);}
if(o.buffer){h=createBuffered(h,o,scope);}
l.fireFn=h;return l;},findListener:function(fn,scope){scope=scope||this.obj;var ls=this.listeners;for(var i=0,len=ls.length;i<len;i++){var l=ls[i];if(l.fn==fn&&l.scope==scope){return i;}}
return-1;},isListening:function(fn,scope){return this.findListener(fn,scope)!=-1;},removeListener:function(fn,scope){var index;if((index=this.findListener(fn,scope))!=-1){if(!this.firing){this.listeners.splice(index,1);}else{this.listeners=this.listeners.slice(0);this.listeners.splice(index,1);}
return true;}
return false;},clearListeners:function(){this.listeners=[];},fire:function(){var ls=this.listeners,scope,len=ls.length;if(len>0){this.firing=true;var args=Array.prototype.slice.call(arguments,0);for(var i=0;i<len;i++){var l=ls[i];if(l.fireFn.apply(l.scope||this.obj||window,arguments)===false){this.firing=false;return false;}}
this.firing=false;}
return true;}};})();

Ext.XTemplate=function(){Ext.XTemplate.superclass.constructor.apply(this,arguments);var s=this.html;s=['<tpl>',s,'</tpl>'].join('');var re=/<tpl\b[^>]*>((?:(?=([^<]+))\2|<(?!tpl\b[^>]*>))*?)<\/tpl>/;var nameRe=/^<tpl\b[^>]*?for="(.*?)"/;var ifRe=/^<tpl\b[^>]*?if="(.*?)"/;var execRe=/^<tpl\b[^>]*?exec="(.*?)"/;var m,id=0;var tpls=[];while(m=s.match(re)){var m2=m[0].match(nameRe);var m3=m[0].match(ifRe);var m4=m[0].match(execRe);var exp=null,fn=null,exec=null;var name=m2&&m2[1]?m2[1]:'';if(m3){exp=m3&&m3[1]?m3[1]:null;if(exp){fn=new Function('values','parent','xindex','xcount','with(values){ return '+(Ext.util.Format.htmlDecode(exp))+'; }');}}
if(m4){exp=m4&&m4[1]?m4[1]:null;if(exp){exec=new Function('values','parent','xindex','xcount','with(values){ '+(Ext.util.Format.htmlDecode(exp))+'; }');}}
if(name){switch(name){case'.':name=new Function('values','parent','with(values){ return values; }');break;case'..':name=new Function('values','parent','with(values){ return parent; }');break;default:name=new Function('values','parent','with(values){ return '+name+'; }');}}
tpls.push({id:id,target:name,exec:exec,test:fn,body:m[1]||''});s=s.replace(m[0],'{xtpl'+id+'}');++id;}
for(var i=tpls.length-1;i>=0;--i){this.compileTpl(tpls[i]);}
this.master=tpls[tpls.length-1];this.tpls=tpls;};Ext.extend(Ext.XTemplate,Ext.Template,{re:/\{([\w-\.\#]+)(?:\:([\w\.]*)(?:\((.*?)?\))?)?(\s?[\+\-\*\\]\s?[\d\.\+\-\*\\\(\)]+)?\}/g,codeRe:/\{\[((?:\\\]|.|\n)*?)\]\}/g,applySubTemplate:function(id,values,parent,xindex,xcount){var t=this.tpls[id];if(t.test&&!t.test.call(this,values,parent,xindex,xcount)){return'';}
if(t.exec&&t.exec.call(this,values,parent,xindex,xcount)){return'';}
var vs=t.target?t.target.call(this,values,parent):values;parent=t.target?values:parent;if(t.target&&vs instanceof Array){var buf=[];for(var i=0,len=vs.length;i<len;i++){buf[buf.length]=t.compiled.call(this,vs[i],parent,i+1,len);}
return buf.join('');}
return t.compiled.call(this,vs,parent,xindex,xcount);},compileTpl:function(tpl){var fm=Ext.util.Format;var useF=this.disableFormats!==true;var sep=Ext.isGecko?"+":",";var fn=function(m,name,format,args,math){if(name.substr(0,4)=='xtpl'){return"'"+sep+'this.applySubTemplate('+name.substr(4)+', values, parent, xindex, xcount)'+sep+"'";}
var v;if(name==='.'){v='values';}else if(name==='#'){v='xindex';}else if(name.indexOf('.')!=-1){v=name;}else{v="values['"+name+"']";}
if(math){v='('+v+math+')';}
if(format&&useF){args=args?','+args:"";if(format.substr(0,5)!="this."){format="fm."+format+'(';}else{format='this.call("'+format.substr(5)+'", ';args=", values";}}else{args='';format="("+v+" === undefined ? '' : ";}
return"'"+sep+format+v+args+")"+sep+"'";};var codeFn=function(m,code){return"'"+sep+'('+code+')'+sep+"'";};var body;if(Ext.isGecko){body="tpl.compiled = function(values, parent, xindex, xcount){ return '"+
tpl.body.replace(/(\r\n|\n)/g,'\\n').replace(/'/g,"\\'").replace(this.re,fn).replace(this.codeRe,codeFn)+"';};";}else{body=["tpl.compiled = function(values, parent, xindex, xcount){ return ['"];body.push(tpl.body.replace(/(\r\n|\n)/g,'\\n').replace(/'/g,"\\'").replace(this.re,fn).replace(this.codeRe,codeFn));body.push("'].join('');};");body=body.join('');}
eval(body);return this;},apply:function(values){return this.master.compiled.call(this,values,{},1,1);},applyTemplate:function(values){return this.master.compiled.call(this,values,{},1,1);},compile:function(){return this;}});Ext.XTemplate.from=function(el){el=Ext.getDom(el);return new Ext.XTemplate(el.value||el.innerHTML);};

Ext.util.TaskRunner=function(interval){interval=interval||10;var tasks=[],removeQueue=[];var id=0;var running=false;var stopThread=function(){running=false;clearInterval(id);id=0;};var startThread=function(){if(!running){running=true;id=setInterval(runTasks,interval);}};var removeTask=function(t){removeQueue.push(t);if(t.onStop){t.onStop.apply(t.scope||t);}};var runTasks=function(){if(removeQueue.length>0){for(var i=0,len=removeQueue.length;i<len;i++){tasks.remove(removeQueue[i]);}
removeQueue=[];if(tasks.length<1){stopThread();return;}}
var now=new Date().getTime();for(var i=0,len=tasks.length;i<len;++i){var t=tasks[i];var itime=now-t.taskRunTime;if(t.interval<=itime){var rt=t.run.apply(t.scope||t,t.args||[++t.taskRunCount]);t.taskRunTime=now;if(rt===false||t.taskRunCount===t.repeat){removeTask(t);return;}}
if(t.duration&&t.duration<=(now-t.taskStartTime)){removeTask(t);}}};this.start=function(task){tasks.push(task);task.taskStartTime=new Date().getTime();task.taskRunTime=0;task.taskRunCount=0;startThread();return task;};this.stop=function(task){removeTask(task);return task;};this.stopAll=function(){stopThread();for(var i=0,len=tasks.length;i<len;i++){if(tasks[i].onStop){tasks[i].onStop();}}
tasks=[];removeQueue=[];};};Ext.TaskMgr=new Ext.util.TaskRunner();
