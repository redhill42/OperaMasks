/* TODO: Minifier */

Ext.namespace("Ext.om");

/**
 * @class Ext.om.AreaTips
 * Provides attractive and customizable tooltips for areas in an element.
 * @singleton
 */
Ext.om.AreaTips = function(){
    var el, tipBody, tipBodyText, tipTitle, close, tm, tagEls = {}, esc, removeCls = null, bdLeft, bdRight;
    var ce, xy;
    var disabled = true, inited = false;
    var showProc = 1, hideProc = 1, dismissProc = 1, locks = [];

    var onMove = function(e){
        if(disabled){
            return;
        }
        clearTimeout(showProc);
        clearTimeout(hideProc);
        var t = e.getTarget();
        if (!t || t.nodeType !== 1 || e.within(el)) {
            return;
        }
        if (t == document || t == document.body) {
            if (ce && tm.autoHide && ce.autoHide !== false) {
                hideProc = setTimeout(hide, tm.hideDelay);
            }
            return;
        }
        var o = tagEls[t.id], a;
        if(o){
            if(t.title){
                t.removeAttribute("title");
                e.preventDefault();
            }
            a = hitTest(o, Ext.fly(t), e);
        }
        if(o && a){
            xy = e.getXY(); xy[1] += 18;
            if (ce && a == ce.a) {
                if (ce.trackMouse || (tm.trackMouse && ce.trackMouse !== false)) {
                    el.setXY(xy);
                }
            } else {
                o.el = t; o.a = a;
                if (ce && t == ce.el) {
                    show(o);
                } else {
                    showProc = show.defer(tm.showDelay, tm, [o]);
                }
            }
        } else {
            if (ce && tm.autoHide && ce.autoHide !== false) {
                hideProc = setTimeout(hide, tm.hideDelay);
            }
        }
    };

    var hitTest = function(o,t,e){
        var exy = e.getXY();
        var txy = Ext.fly(t).getXY();
        var x = exy[0] - txy[0];
        var y = exy[1] - txy[1];
        for (var len = o.areas.length, i = len-1; i >= 0; i--) {
            var a = o.areas[i];
            if (a.shape == 'poly') {
                if (polyHitTest(a.coords, x, y)) {
                    return a;
                }
            } else {
                if (rectHitTest(a.coords, x, y)) {
                    return a;
                }
            }
        }
        return null;
    };

    var rectHitTest = function(coords,x,y) {
        return x >= coords[0] && y >= coords[1] && x <= coords[2] && y <= coords[3];
    }

    var polyHitTest = function(coords,x,y) {
        var n = coords.length/2;
        if (n <= 2) return false;

        var hits = 0;
        var lastx = coords[(n-1)*2];
        var lasty = coords[(n-1)*2+1];
        var curx, cury;

        for (var i = 0; i < n; lastx = curx, lasty = cury, i++) {
            curx = coords[i*2];
            cury = coords[i*2+1];
            if (cury == lasty) {
                continue;
            }

            var leftx;
            if (curx < lastx) {
                if (x >= lastx) {
                    continue;
                }
                leftx = curx;
            } else {
                if (x >= curx) {
                    continue;
                }
                leftx = lastx;
            }

            var test1, test2;
            if (cury < lasty) {
                if (y < cury || y >= lasty) {
                    continue;
                }
                if (x < leftx) {
                    hits++;
                    continue;
                }
                test1 = x - curx;
                test2 = y - cury;
            } else {
                if (y < lasty || y >= cury) {
                    continue;
                }
                if (x < leftx) {
                    hits++;
                    continue;
                }
                test1 = x - lastx;
                test2 = y - lasty;
            }

            if (test1 < (test2 / (lasty - cury) * (lastx - curx))) {
                hits++;
            }
        }

        return ((hits & 1) != 0);
    };

    var onDown = function(e){
        clearTimeout(showProc);
        if (!e.within(el)){
            if (tm.hideOnClick){
                hide();
            }
        }
    };

    var getPad = function(){
        return bdLeft.getPadding('l')+bdRight.getPadding('r');
    };

    var show = function(o){
        if (disabled || !o.a){
            return;
        }
        clearTimeout(dismissProc);
        ce = o;
        if (removeCls){ // in case manually hidden
            el.removeClass(removeCls);
            removeCls = null;
        }
        if (ce.cls){
            el.addClass(ce.cls);
            removeCls = ce.cls;
        }
        if (ce.a.title){
            tipTitle.update(ce.a.title);
            tipTitle.show();
        } else if (ce.title){
            tipTitle.update(ce.title);
            tipTitle.show();
        } else {
            tipTitle.update('');
            tipTitle.hide();
        }
        el.dom.style.width = tm.maxWidth+'px';
        tipBodyText.update(ce.a.text);
        var p = getPad(), w = ce.width;
        if(!w){
            var td = tipBodyText.dom;
            var aw = Math.max(td.offsetWidth, td.clientWidth, td.scrollWidth);
            if (aw > tm.maxWidth) {
                w = tm.maxWidth;
            } else if (aw < tm.minWidth) {
                w = tm.minWidth;
            } else {
                w = aw;
            }
        }
        el.setWidth(w + p);
        if (tm.autoHide && ce.autoHide !== false) {
            close.setDisplayed(false);
        } else {
            close.setDisplayed(true);
        }
        if (xy){
            el.setXY(xy);
        }
        if (tm.animate) {
            el.setOpacity(.1);
            el.setStyle("visibility", "visible");
            el.fadeIn({callback: afterShow});
        } else {
            afterShow();
        }
    };

    var afterShow = function(){
        if(ce){
            el.show();
            esc.enable();
            if (tm.autoDismiss && ce.autoDismiss !== false) {
                dismissProc = setTimeout(hide, tm.autoDismissDelay);
            }
        }
    };

    var hide = function(noanim) {
        clearTimeout(dismissProc);
        clearTimeout(hideProc);
        ce = null;
        if (el.isVisible()) {
            esc.disable();
            if (noanim !== true && tm.animate){
                el.fadeOut({callback: afterHide});
            } else {
                afterHide();
            }
        }
    };

    var afterHide = function(){
        el.hide();
        if(removeCls){
            el.removeClass(removeCls);
            removeCls = null;
        }
    };

    return {
        /**
         * @cfg {Number} minWidth
         * The minimum width of the area tip (defaults to 40)
         */
        minWidth : 40,
        /**
         * @cfg {Number} maxWidth
         * The maximum width of the area tip (defaults to 300)
         */
        maxWidth : 300,
        /**
         * @cfg {Boolean} trackMouse
         * True to have the area tip follow the mouse as it moves over the target element (defaults to false)
         */
        trackMouse : false,
        /**
         * @cfg {Boolean} hideOnClick
         * True to hide the area tip if the user clicks anywhere in the document (defaults to true)
         */
        hideOnClick : true,
        /**
         * @cfg {Number} showDelay
         * Delay in milliseconds before the area tip displays after the mouse enters the target element (defaults to 500)
         */
        showDelay : 500,
        /**
         * @cfg {Number} hideDelay
         * Delay in milliseconds before the area tip hides when autoHide = true (defaults to 200)
         */
        hideDelay : 200,
        /**
         * @cfg {Boolean} autoHide
         * True to automatically hide the area tip after the mouse exits the target element (defaults to true).
         * Used in conjunction with hideDelay.
         */
        autoHide : true,
        /**
         * @cfg {Boolean}
         * True to automatically hide the area tip after a set period of time, regardless of the user's actions
         * (defaults to true).  Used in conjunction with autoDismissDelay.
         */
        autoDismiss : false,
        /**
         * @cfg {Number}
         * Delay in milliseconds before the area tip hides when autoDismiss = true (defaults to 5000)
         */
        autoDismissDelay : 5000,
        /**
         * @cfg {Boolean} animate
         * True to turn on fade animation. Defaults to false (ClearType/scrollbar flicker issues in IE7).
         */
        animate : false,

        // private
        init : function(){
            tm = Ext.om.AreaTips;
            if(!inited){
                el = new Ext.Layer({cls:"x-tip", shadow:"drop", shim:true, constraint:true, shadowOffet:3});
                el.fxDefaults = {stopFx: true};
                el.update('<div class="x-tip-top-left"><div class="x-tip-top-right"><div class="x-tip-top"></div></div></div><div class="x-tip-bd-left"><div class="x-tip-bd-right"><div class="x-tip-bd"><div class="x-tip-close"></div><h3></h3><div class="x-tip-bd-inner"></div><div class="x-clear"></div></div></div></div><div class="x-tip-ft-left"><div class="x-tip-ft-right"><div class="x-tip-ft"></div></div></div>');
                tipTitle = el.child('h3');
                tipTitle.enableDisplayMode("block");
                tipBody = el.child('div.x-tip-bd');
                tipBodyText = el.child('div.x-tip-bd-inner');
                bdLeft = el.child('div.x-tip-bd-left');
                bdRight = el.child('div.x-tip-bd-right');
                close = el.child('div.x-tip-close');
                close.enableDisplayMode("block");
                close.on("click", hide);
                var d = Ext.get(document);
                d.on("mousemove", onMove);
                d.on("mousedown", onDown);
                esc = d.addKeyListener(27, hide);
                esc.disable();
                inited = true;
            }
            this.enable();
        },

        /**
         * Configures a new area tip instance and assigns it to a target element
         * @param {Object} config The config object
         */
        register : function(config) {
            var cs = config instanceof Array ? config : arguments;
            for (var i = 0, len = cs.length; i < len; i++) {
                var c = cs[i];
                var target = c.target;
                if (target) {
                    if (target instanceof Array) {
                        for (var j = 0, jlen = target.length; j < jlen; j++) {
                            tagEls[target[j]] = c;
                        }
                    } else {
                        tagEls[typeof target == 'string' ? target : Ext.id(target.id)] = c;
                    }
                }
            }
        },

        /**
         * Removes this area tip from its element and destroys it.
         */
        unregister : function(el) {
            delete tagEls[Ext.id(el)];
        },

        /**
         * Enable this area tip
         */
        enable : function(){
            if(inited){
                locks.pop();
                if(locks.length < 1){
                    disabled = false;
                }
            }
        },

        /**
         * Disable this area tip
         */
        disable : function(){
            disabled = true;
            clearTimeout(showProc);
            clearTimeout(hideProc);
            clearTimeout(dismissProc);
            if(ce){
                hide(true);
            }
            locks.push(1);
        },

        /**
         * Returns true if the area tip is enabled, else false
         */
        isEnabled : function(){
            return !disabled;
        }
    };
}();
