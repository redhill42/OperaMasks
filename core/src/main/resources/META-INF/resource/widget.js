/*
 * $Id: widget.js,v 1.3 2007/07/02 07:38:14 jacky Exp $
 *
 * Copyright (C) 2006 Operamasks Community.
 * Copyright (C) 2000-2006 Apusic Systems, Inc.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see http://www.gnu.org/licenses.
 */

// UIApplication

function UIApplication() {
    throw 'instantiation error';
}

UIApplication._initializeHandlers = [];
UIApplication._cleanupHandlers = [];

UIApplication.addInitializeHandler = function(handler) {
    this._initializeHandlers.push(handler);
}
UIApplication.addCleanupHandler = function(handler) {
    this._cleanupHandlers.push(handler);
}

UIApplication.initialize = function() {
    this.mnemonicAll();
    for (var i = 0; i < this._initializeHandlers.length; i++) {
        this._initializeHandlers[i].call();
    }
}

UIApplication.cleanup = function() {
    for (var i = 0; i < this._cleanupHandlers.length; i++) {
        this._cleanupHandlers[i].call();
    }
}

// Utility method to add mnemonic for access key
UIApplication.mnemonic = function(el) {
    if (typeof el == 'string')
        el = document.getElementById(el);
    if (!el.accessKey)
        return;

    var kids = el.childNodes;
    for (var i = 0; i < kids.length; i++) {
        if (kids[i].nodeType == 3) { // Text node
            this._addMnemonic(el, kids[i], el.accessKey);
            return;
        }
    }
}

UIApplication._addMnemonic = function(el, node, ak) {
    var text = node.nodeValue;
    ak = ak.toUpperCase();
    var pos = text.indexOf(ak);
    if (pos == -1) {
        ak = ak.toLowerCase();
        pos = text.indexOf(ak);
    }

    var left = null, middle = null, right = null;
    if (pos != -1) {
        if (pos > 0)
            left = document.createTextNode(text.substr(0,pos));
        if (pos+1 < text.length)
            right = document.createTextNode(text.substr(pos+1));
        middle = document.createTextNode(ak);
    } else {
        pos = text.indexOf(':');
        if (pos == -1)
            pos = text.length;
        left = document.createTextNode(text.substr(0,pos) + '(');
        middle = document.createTextNode(ak.toUpperCase());
        right = document.createTextNode(')' + text.substr(pos));
    }

    var mnemonic = document.createElement('span');
    mnemonic.className = 'mnemonic';
    mnemonic.appendChild(middle);

    if (right != null) {
        el.insertBefore(right, node);
        el.insertBefore(mnemonic, right);
    } else {
        el.insertBefore(mnemonic, node);
    }
    if (left != null) {
        el.insertBefore(left, mnemonic);
    }
    el.removeChild(node);
}

UIApplication.mnemonicAll = function(tag) {
    if (tag == null) {
        this.mnemonicAll('label');
        this.mnemonicAll('button');
    } else {
        var tags = document.getElementsByTagName(tag);
        for (var i = 0; i < tags.length; i++) {
            this.mnemonic(tags[i]);
        }
    }
}

// UIWidget

function UIWidget() {}

// Browser sniffy
UIWidget.ua = navigator.userAgent.toLowerCase();
UIWidget.version = parseFloat(navigator.appVersion.substr(21)) ||
                   parseFloat(navigator.appVersion);
UIWidget.ie = navigator.appName == 'Microsoft Internet Explorer';
UIWidget.ns = navigator.appName == 'Netscape';
UIWidget.win = navigator.platform == 'Win32';
UIWidget.mac = UIWidget.ua.indexOf("mac") != -1;
if (UIWidget.ua.indexOf('opera') != -1) {
    UIWidget.ie = UIWidget.ns = false;
    UIWidget.opera = true;
}
if (UIWidget.ua.indexOf("gecko") != -1) {
    UIWidget.ie = UIWidget.ns = false;
    UIWidget.moz = true;
}

if (UIWidget.ie) {
    UIWidget.quirks = document.compatMode == 'BackCompat';
} else if (UIWidget.opera) {
    UIWidget.quirks = document.compatMode == 'QuirksMode';
} else {
    UIWidget.quirks = false;
}

UIWidget.extend = function(target) {
    for (var p in UIWidget.prototype) {
        if (typeof target.prototype[p] == 'undefined') {
            target.prototype[p] = UIWidget.prototype[p];
        }
    }
}

UIWidget.prototype.checkRecreate = function(el) {
    if (UIWidget.opera) {
        if (el._this)
            return el._this;
        el._this = this;
    }
    return null;
}

if (window.addEventListener) {
    // for DOM Level 2 Event Model
    UIWidget.prototype.addEventListener = function(el, type, handler) {
        var _self = this;
        var fn = function(e) { handler.call(_self, e); }
        el.addEventListener(type, fn, false);
        return fn;
    }

    UIWidget.prototype.removeEventListener = function(el, type, fn) {
        el.removeEventListener(type, fn, false);
    }
} else if (window.attachEvent) {
    // for IE Event Model, must handle IE memory leak bug.
    if (!window.__IE_Event) {
        var __IE_Event = {};

        __IE_Event.guid_next = 0;
        __IE_Event.event_handlers = [];

        __IE_Event.preventDefault = function() { this.returnValue = false; }
        __IE_Event.stopPropagation = function() { this.cancelBubble = true; }

        __IE_Event.create_event_handler = function(guid) {
            var fn = function() {
                var assoc = __IE_Event.event_handlers[guid];
                if (assoc) {
                    var e = window.event || assoc.element.document.parentWindow.event;
                    e.target = e.srcElement;
                    e.preventDefault = __IE_Event.preventDefault;
                    e.stopPropagation = __IE_Event.stopPropagation;
                    assoc.handler.call(assoc.object, e);
                }
            };
            fn.guid = guid;
            return fn;
        }
    }

    UIWidget.prototype.addEventListener = function(el, type, handler) {
        var guid = __IE_Event.guid_next++;
        __IE_Event.event_handlers[guid] = { element: el, object: this, handler: handler };
        var fn = __IE_Event.create_event_handler(guid);
        el.attachEvent('on' + type, fn);
        return fn;
    }

    UIWidget.prototype.removeEventListener = function(el, type, fn) {
        el.detachEvent('on' + type, fn);
        delete __IE_Event.event_handlers[fn.guid];
    }
} else {
    // for very old browsers, may not work
    UIWidget.prototype.addEventListener = function(el, type, handler) {
        var _self = this;
        var chained = el['on' + type];
        var fn = function(e) {
            if (chained) {
                // this refers to the element that receiving event
                if (chained.call) {
                    chained.call(this, e);
                } else {
                    this.__fn = chained;
                    this.__fn(e);
                    this.__fn = null;
                }
            }

            e = e || window.event;
            if (!e.target && e.srcElement)
                e.target = e.srcElement;
            if (!e.keyCode && e.which)
                e.keyCode = e.which;
            if (!e.preventDefault)
                e.preventDefault = function() { e.returnValue = false; }
            if (!e.stopPropagation)
                e.stopPropagation = function() { e.cancelBubble = true; }
            handler.call(_self, e);
        };

        fn.chained = chained;
        el['on' + type] = fn;
        return fn;
    }

    UIWidget.prototype.removeEventListener = function(el, type, fn) {
        el['on' + type] = fn.chained;
    }
}

UIWidget.getComputedStyle = function(el, property) {
    if (document.defaultView) { // DOM2
        try {
            var cs = document.defaultView.getComputedStyle(el, "");
            if (cs) return cs.getPropertyValue(property);
        } catch (e) {}
    }
    if (el.currentStyle) // IE
        return el.currentStyle[UIWidget.toPropertyName(property)];
    if (el.style.getPropertyValue) // DOM2
        return el.style.getPropertyValue(property);
    return null;
}

UIWidget.toPropertyName = function(s) {
    var a = s.split('-'), rs = a[0];
    for (var i = 1; i < a.length; i++)
        rs += a[i].charAt(0).toUpperCase() + a[i].substring(1);
    return rs;
}

UIWidget.getOffsetLeft = function(el) {
    var x = 0;
    do {
        x += el.offsetLeft;
        el = el.offsetParent;
    } while (el);
    return x;
}

UIWidget.getOffsetTop = function(el) {
    var y = 0;
    do {
        y += el.offsetTop;
        el = el.offsetParent;
    } while (el);
    return y;
}

UIWidget.setLeft = function(el,x) {
    el.style.left = x + 'px';
}
UIWidget.setTop = function(el,y) {
    el.style.top = y + 'px';
}
UIWidget.setRight = function(el,x) {
    el.style.right = x + 'px';
}
UIWidget.setBottom = function(el,y) {
    el.style.bottom = y + 'px';
}
UIWidget.setWidth = function(el,dx) {
    el.style.width = dx + 'px';
}
UIWidget.setHeight = function(el,dy) {
    el.style.height = dy + 'px';
}

UIWidget.getPixelValue = function(el, property) {
    var value = UIWidget.getComputedStyle(el, property);
    if (value && value.match(/px$/i))
        return parseInt(value);
    return 0;
}

UIWidget.isBorderBoxSizing = function(el) {
    if (UIWidget.quirks) {
        return true;
    } else if (UIWidget.moz) {
        return (el.style.MozBoxSizing == 'border-box');
    } else {
        return false;
    }
}

UIWidget.getOuterBoxWidth = function(el) {
    return UIWidget.getPixelValue(el, 'border-left-width') +
           UIWidget.getPixelValue(el, 'border-right-width') +
           UIWidget.getPixelValue(el, 'padding-left') +
           UIWidget.getPixelValue(el, 'padding-right');
}

UIWidget.getOuterBoxHeight = function(el) {
    return UIWidget.getPixelValue(el, 'border-top-width') +
           UIWidget.getPixelValue(el, 'border-bottom-width') +
           UIWidget.getPixelValue(el, 'padding-top') +
           UIWidget.getPixelValue(el, 'padding-bottom');
}

UIWidget.getOffsetWidth = function(el) {
    if (el.offsetWidth)
        return el.offsetWidth;

    var width = UIWidget.getPixelValue(el, 'width');
    if (!UIWidget.isBorderBoxSizing(el))
        width += UIWidget.getOuterBoxWidth(el);
    return width;
}

UIWidget.getOffsetHeight = function(el) {
    if (el.offsetHeight)
        return el.offsetHeight;

    var height = UIWidget.getPixelValue(el, 'height');
    if (!UIWidget.isBorderBoxSizing(el))
        height += UIWidget.getOuterBoxHeight(el);
    return height;
}

UIWidget.getClientWidth = function(el) {
    var width = el.offsetWidth;
    if (width != 0) {
        width -= UIWidget.getOuterBoxWidth(el);
    } else {
        width = UIWidget.getPixelValue(el, 'width');
        if (UIWidget.isBorderBoxSizing(el))
            width -= UIWidget.getOuterBoxWidth(el);
    }
    if (width < 0) width = 0;
    return width;
}

UIWidget.getClientHeight = function(el) {
    var height = el.offsetHeight;
    if (height != 0) {
        height -= UIWidget.getOuterBoxHeight(el);
    } else {
        height = UIWidget.getPixelValue(el, 'height');
        if (UIWidget.isBorderBoxSizing(el))
            height -= UIWidget.getOuterBoxHeight(el);
    }
    if (height < 0) height = 0;
    return height;
}

UIWidget.setOffsetWidth = function(el, width) {
    if (!UIWidget.isBorderBoxSizing(el)) {
        width -= UIWidget.getOuterBoxWidth(el);
        if (width < 0) width = 0;
    }
    el.style.width = width + "px";
}

UIWidget.setOffsetHeight = function(el, height) {
    if (!UIWidget.isBorderBoxSizing(el)) {
        height -= UIWidget.getOuterBoxHeight(el);
        if (height < 0) height = 0;
    }
    el.style.height = height + "px";
}

UIWidget.setTabStop = function(el,f) {
    if ((f == null || f == true) && !el.disabled) {
        if (UIWidget.ie) {
            el.unselectable = false;
        } else if (UIWidget.moz) {
            el.style.MozUserFocus = 'normal';
        }
        if (el.tabIndex <= 0) {
            el.tabIndex = 1;
        }
    } else {
        if (UIWidget.ie) {
            el.unselectable = true;
        } else if (UIWidget.moz) {
            el.style.MozUserFocus = 'ignore';
        }
        el.tabIndex = -1;
    }
}

function __IE_Event_disableSelection() { window.event.returnValue = false; }

UIWidget.disableSelection = function(el) {
    if (UIWidget.ie) {
        el.attachEvent('onselectstart', __IE_Event_disableSelection);
    } else if (UIWidget.moz) {
        el.style.MozUserSelect = 'none';
    }
}

UIWidget.setOpacity = function(el, opacity) {
    el.style.opacity = opacity;
    if (UIWidget.ie) {
        el.style.filter = "Alpha(Opacity="+100*opacity+")";
    } else if (UIWidget.moz) {
        el.style.MozOpacity = opacity;
    }
}

UIWidget.addCssClass = function(el, cs) {
    if (el.className) {
        el.className += ' ' + cs;
    } else {
        el.className = cs;
    }
}

UIWidget.removeCssClass = function(el, cs) {
    var re = new RegExp(' ' + cs, 'g');
    el.className = el.className.replace(re, '');
}

UIWidget.createCanvas = function() {
    var canvas = document.createElement('canvas');
    if (UIWidget.ie && window.G_vmlCanvasManager)
        G_vmlCanvasManager.initElement(canvas);
    return canvas.getContext ? canvas : null;
}

// UICanvas

var UICanvas = {
    create: function() {
        var canvas = document.createElement('canvas');
        if (UIWidget.ie && window.G_vmlCanvasManager)
            G_vmlCanvasManager.initElement(canvas);
        return canvas.getContext ? canvas : null;
    },

    roundedRect: function(ctx,x,y,width,height,radius) {
        var tl, bl, tr, br;
        if (typeof radius == 'number') {
            tl = bl = tr = br = radius;
        } else {
            tl = radius.tl, tr = radius.tr, bl = radius.bl, br = radius.br;
        }

        ctx.beginPath();
        ctx.moveTo(x, y+tl);
        ctx.lineTo(x, y+height-bl);
        if (bl > 0)
            ctx.quadraticCurveTo(x, y+height, x+bl, y+height);
        ctx.lineTo(x+width-br, y+height);
        if (br > 0)
            ctx.quadraticCurveTo(x+width, y+height, x+width, y+height-br);
        ctx.lineTo(x+width, y+tr);
        if (tr > 0)
            ctx.quadraticCurveTo(x+width, y, x+width-tr, y);
        ctx.lineTo(x+tl, y);
        if (tl > 0)
            ctx.quadraticCurveTo(x, y, x, y+tl);
    }
}

// UIAnimator

var UIAnimator = {};

UIAnimator.fadeIn = function(el,p) {
    if (p == null)
        p = new Object();
    if (p.startOpacity == null)
        p.startOpacity = 0;
    if (p.endOpacity == null)
        p.endOpacity = 1;
    UIAnimator.fade(el, p);
}

UIAnimator.fadeOut = function(el,p) {
    if (p == null)
        p = new Object();
    if (p.startOpacity == null)
        p.startOpacity = 1;
    if (p.endOpacity == null)
        p.endOpacity = 0;
    UIAnimator.fade(el, p);
}

UIAnimator.fade = function(el,p) {
    if (typeof el == 'string')
        el = document.getElementById(el);
    if (el._fade != null)
        UIAnimator.clearFade(el);
    el._fade = p;

    if (p.steps == null) p.steps = 10;
    if (p.msecs == null) p.msecs = 25;

    var onstep = p.onstep;
    var onfinish = p.onfinish;

    p.onstep = function() {
        UIWidget.setOpacity(el, p.startOpacity + (p.endOpacity - p.startOpacity) * (p.steps - p.counter) / p.steps);
        if (onstep) onstep(el);
    };

    p.onfinish = function() {
        UIWidget.setOpacity(el, p.endOpacity);
        if (onfinish) onfinish(el);
    }

    UIWidget.setOpacity(el, p.startOpacity);

    p.counter = p.steps;
    p.timer = window.setTimeout(function() { UIAnimator._repeatFade(el); }, p.msecs);
}

UIAnimator._repeatFade = function(el) {
    var p = el._fade;
    if (p == null) return;

    if (--p.counter == 0) {
        UIAnimator.clearFade(el);
        p.onfinish.call();
    } else {
        p.onstep.call();
        p.timer = window.setTimeout(function() { UIAnimator._repeatFade(el); }, p.msecs);
    }
}

UIAnimator.clearFade = function(el) {
    var p = el._fade;
    if (p) {
        el._fade = null;
        if (p.timer) window.clearTimeout(p.timer);
    }
}

UIAnimator.wipeIn = function(el,p) {
    UIAnimator.wipe(el, true, p);
}
UIAnimator.wipeOut = function(el,p) {
    UIAnimator.wipe(el, false, p);
}

UIAnimator.wipe = function(el,IN,p) {
    if (typeof el == 'string')
        el = document.getElementById(el);
    if (el._wipe != null)
        UIAnimator.clearWipe(el);

    if (p == null)
        p = new Object();
    el._wipe = p;

    if (p.steps == null) p.steps = 10;
    if (p.msecs == null) p.msecs = 25;

    var overflow = UIWidget.getComputedStyle(el, 'overflow');
    if (overflow == 'visible')
        el.style.overflow = 'hidden';
    el.style.display = '';

    var onstep = p.onstep;
    var onfinish = p.onfinish;

    var offset = 0;
    if (!UIWidget.isBorderBoxSizing(el)) {
        offset = UIWidget.getOuterBoxHeight(el);
    }

    if (IN) {
        var endHeight = el.scrollHeight - offset;
        p.onstep = function() {
            var h = endHeight * (p.steps - p.counter) / p.steps;
            el.style.height = h + "px";
            el.scrollTop = endHeight - h + offset;
            UIWidget.setOpacity(el, (p.steps - p.counter) / p.steps);
            if (onstep) onstep(el);
        };

        p.onfinish = function() {
            el.style.overflow = overflow;
            el.style.height = 'auto';
            el.scrollTop = 0;
            UIWidget.setOpacity(el, 1);
            if (onfinish) onfinish(el);
        };

        if (endHeight <= 0) {
            p.onfinish.call();
            return;
        } else {
            el.style.height = "1px"; // should "0px" but IE
            el.scrollTop = endHeight;
            UIWidget.setOpacity(el, 0);
        }
    } else {
        var startHeight = el.offsetHeight - offset;
        p.onstep = function() {
            var h = startHeight * p.counter / p.steps;
            el.style.height = h + "px";
            el.scrollTop = startHeight - h + offset;
            UIWidget.setOpacity(el, p.counter / p.steps);
            if (onstep) onstep(el);
        };

        p.onfinish = function() {
            el.style.display = 'none';
            el.style.overflow = overflow;
            el.style.height = 'auto';
            el.scrollTop = 0;
            UIWidget.setOpacity(el, 1);
            if (onfinish) onfinish(el);
        };

        if (startHeight <= 0) {
            p.onfinish.call();
            return;
        }
    }

    p.counter = p.steps;
    p.timer = window.setTimeout(function() { UIAnimator._repeatWipe(el); }, p.msecs);
}

UIAnimator._repeatWipe = function(el) {
    var p = el._wipe;
    if (p == null) return;

    if (--p.counter == 0) {
        UIAnimator.clearWipe(el);
        p.onfinish.call();
    } else {
        p.onstep.call();
        p.timer = window.setTimeout(function() { UIAnimator._repeatWipe(el); }, p.msecs);
    }
}

UIAnimator.clearWipe = function(el) {
    var p = el._wipe;
    if (p) {
        el._wipe = null;
        if (p.timer) window.clearTimeout(p.timer);
    }
}

UIAnimator.positionTo = function(el,endLeft,endTop,endWidth,endHeight,p) {
    if (typeof el == 'string')
        el = document.getElementById(el);
    if (el._positionTo != null)
        UIAnimator.clearPositionTo(el);

    if (p == null)
        p = new Object();
    el._positionTo = p;

    if (p.steps == null) p.steps = 10;
    if (p.msecs == null) p.msecs = 25;

    var onstep = p.onstep;
    var onfinish = p.onfinish;

    var startLeft = el.getLeft ? el.getLeft() : el.offsetLeft;
    var startTop = el.getTop ? el.getTop() : el.offsetTop;
    var startWidth = el.getWidth ? el.getWidth() : el.offsetWidth;
    var startHeight = el.getHeight ? el.getHeight() : el.offsetHeight;

    if (endLeft == null) endLeft = startLeft;
    if (endTop == null) endTop = startTop;
    if (endWidth == null) endWidth = startWidth;
    if (endHeight == null) endHeight = startHeight;

    p.onstep = function() {
        var factor = (p.steps - p.counter) / p.steps;
        var x = startLeft + (endLeft - startLeft) * factor;
        var y = startTop + (endTop - startTop) * factor;
        var w = startWidth + (endWidth - startWidth) * factor;
        var h = startHeight + (endHeight - startHeight) * factor;

        if (el.setRect) {
            el.setRect(x, y, w, h);
        } else {
            el.style.left = x + 'px';
            el.style.top = y + 'px';
            UIWidget.setOffsetWidth(el, w);
            UIWidget.setOffsetHeight(el, h);
        }

        if (onstep) onstep(el);
    };

    p.onfinish = function() {
        if (el.setRect) {
            el.setRect(endLeft, endTop, endWidth, endHeight);
        } else {
            el.style.left = endLeft + 'px';
            el.style.top = endTop + 'px';
            UIWidget.setOffsetWidth(el, endWidth);
            UIWidget.setOffsetHeight(el, endHeight);
        }

        if (onfinish) onfinish(el);
    };

    p.counter = p.steps;
    p.timer = window.setTimeout(function() { UIAnimator._repeatPositionTo(el); }, p.msecs);
}

UIAnimator._repeatPositionTo = function(el) {
    var p = el._positionTo;
    if (p == null) return;

    if (--p.counter == 0) {
        UIAnimator.clearPositionTo(el);
        p.onfinish.call();
    } else {
        p.onstep.call();
        p.timer = window.setTimeout(function() { UIAnimator._repeatPositionTo(el); }, p.msecs);
    }
}

UIAnimator.clearPositionTo = function(el) {
    var p = el._positionTo;
    if (p) {
        el._positionTo = null;
        if (p.timer) window.clearTimeout(p.timer);
    }
}

// -------------------------------------------------------------------------------
// UIButton

function UIButton(el) {
    if (typeof el == 'string')
        el = document.getElementById(el);
    if (!el) return;
    if (this.checkRecreate(el))
        return el._this;
    this._button = el;

    if (this._drawButton) {
        this._initialize();
    }
}

UIWidget.extend(UIButton);

UIButton.prototype._initialize = function() {
    this._label = document.createElement('span');
    while (this._button.firstChild)
        this._label.appendChild(this._button.firstChild);
    this._button.appendChild(this._label);

    var width = this._button.offsetWidth;
    var height = this._button.offsetHeight;
    var labelWidth = this._label.offsetWidth;
    var labelHeight = this._label.offsetHeight;

    if (width == 0 && height == 0) {
        var phantom = this._button.cloneNode(true);
        document.body.appendChild(phantom);
        width = phantom.offsetWidth;
        height = phantom.offsetHeight;
        labelWidth = phantom.firstChild.offsetWidth;
        labelHeight = phantom.firstChild.offsetHeight;
        document.body.removeChild(phantom);
    }

    this._width = width = this._getPreferredWidth(width);
    this._height = height = this._getPreferredHeight(height);

    var offset_x = 0, offset_y = 0;
    if (UIWidget.moz) { // FIXME
        offset_x = -3;
        offset_y = -height/2;
    }

    this._canvas = UIWidget.createCanvas();
    this._canvas.width = width;
    this._canvas.height = height;
    this._canvas.style.width = width + 'px';
    this._canvas.style.height = height + 'px';
    this._canvas.style.left = offset_x + 'px';
    this._canvas.style.top = offset_y + 'px';
    this._button.insertBefore(this._canvas, this._label);

    if (UIWidget.ie) { // fix IE bug
        var glass = document.createElement('div');
        glass.className = 'UIGlassPane';
        this._button.insertBefore(glass, this._label);
    }

    UIWidget.addCssClass(this._button, 'UIButton');
    UIWidget.setOffsetWidth(this._button, width);
    UIWidget.setOffsetHeight(this._button, height);

    this._label.className = 'button-label';
    this._label.style.width = labelWidth + 'px';
    this._label.style.height = labelHeight + 'px';
    this._label.style.left = (width - labelWidth) / 2 + offset_x + 'px';
    this._label.style.top = (height - labelHeight) / 2 + offset_y + 'px';

    this.addEventListener(this._button, 'focus', this._onfocus);
    this.addEventListener(this._button, 'blur', this._onblur);
    this.addEventListener(this._button, 'mousedown', this._startPush);
    this.addEventListener(this._button, 'mouseover', this._onmouseover);
    this.addEventListener(this._button, 'mouseout', this._onmouseout);
    this.addEventListener(this._button, 'propertychange', this._onpropertychange);

    this.drawButton();
}

UIButton.prototype._getPreferredWidth = function(width) {
    return width;
}

UIButton.prototype._getPreferredHeight = function(height) {
    return height;
}

UIButton.prototype.drawButton = function() {
    if (this._drawButton) {
        var width = this._width;
        var height = this._height;
        if (UIWidget.ie) width--, height--;
        this._drawButton(width, height);
    }
}

UIButton.prototype._onpropertychange = function(e) {
    if (e.propertyName == 'disabled') {
        this.drawButton();
    }
}

UIButton.prototype._onfocus = function() {
    this._focus = true;
    this.drawButton();
}

UIButton.prototype._onblur = function() {
    this._focus = false;
    this.drawButton();
}

UIButton.prototype._onmouseover = function() {
    if (this._hover != true) {
        this._hover = true;
        this.drawButton();
    }
}

UIButton.prototype._onmouseout = function() {
    if (this._hover != false) {
        this._hover = false;
        this.drawButton();
    }
}

UIButton.prototype._startPush = function() {
    if (this._button.disabled)
        return;

    this._button.focus();
    this._setPressed(true);

    var p = this._pushData = {};
    if (this._button.setCapture) {
        this._button.setCapture();
        p.mousemove = this.addEventListener(this._button, 'mousemove', this._continuePush);
        p.mouseup = this.addEventListener(this._button, 'mouseup', this._endPush);
        p.losecapture = this.addEventListener(this._button, 'losecapture', this._endPush);
    } else {
        p.mousemove = this.addEventListener(window, 'mousemove', this._continuePush);
        p.mouseup = this.addEventListener(window, 'mouseup', this._endPush);
    }
}

UIButton.prototype._continuePush = function(e) {
    if (this._pushData != null) {
        var el = e.target;
        var pressed = false;
        while (el) {
            if (el == this._button) {
                pressed = true;
                break;
            }
            el = el.parentNode;
        }
        this._hover = pressed;
        this._setPressed(pressed);
    }
}

UIButton.prototype._endPush = function(e) {
    if (this._pushData != null) {
        var p = this._pushData;
        this._pushData = null;
        this._setPressed(false);

        if (this._button.releaseCapture) {
            this._button.releaseCapture();
            this.removeEventListener(this._button, 'mousemove', p.mousemove);
            this.removeEventListener(this._button, 'mouseup', p.mouseup);
            this.removeEventListener(this._button, 'losecapture', p.losecapture);
        } else {
            this.removeEventListener(window, 'mousemove', p.mousemove);
            this.removeEventListener(window, 'mouseup', p.mouseup);
        }
    }
}

UIButton.prototype._setPressed = function(pressed) {
    if (this._pressed != pressed) {
        var width = this._button.clientWidth;
        var height = this._button.clientHeight;
        var labelWidth = this._label.offsetWidth;
        var labelHeight = this._label.offsetHeight;

        var offsetLeft = (width - labelWidth) / 2;
        var offsetTop = (height - labelHeight) / 2;
        if (pressed) offsetLeft++, offsetTop++;
        if (UIWidget.moz) offsetLeft -= 3, offsetTop -= height/2; // FIXME
        this._label.style.left = offsetLeft + 'px';
        this._label.style.top = offsetTop + 'px';

        this._pressed = pressed;
        this.drawButton();
    }
}

if (UIWidget.ie || UIWidget.moz) { // FIXME: only IE and Gecko tested and work
    UIApplication.addInitializeHandler(function() {
        var buttons = document.getElementsByTagName('button');
        var i;
        for (i = 0; i < buttons.length; i++) {
            if (/button-face/.test(buttons[i].className))
                new UIButton(buttons[i]);
        }
    });}

// -------------------------------------------------------------------------------
// Slider

function UISlider(el, orient, minimum, maximum) {
    if (typeof el == 'string')
        el = document.getElementById(el);
    if (!el) return;
    if (this.checkRecreate(el))
        return el._this;
    this._slider = el;

    if (!orient) orient = 'horizontal';
    this._horizontal = (orient == 'horizontal');

    this._track = document.createElement('div');
    this._track.className = 'slider-track';
    this._slider.appendChild(this._track);
    this._thumb = document.createElement('div');
    this._thumb.className = 'slider-thumb';
    this._slider.appendChild(this._thumb);
    UIWidget.setTabStop(this._slider);

    this._unitIncrement = 1;
    this._blockIncrement = 10;
    this._minimum = minimum;
    this._maximum = maximum;
    this._value = minimum;
    this.repaint();

    this.addEventListener(this._slider, "resize", this.repaint);
    this.addEventListener(this._slider, "mousedown", this._onmousedown);
    this.addEventListener(this._slider, "click", this._onclick);
    this.addEventListener(this._slider, "mousewheel", this._onmousewheel);
    this.addEventListener(this._slider, "keydown", this._onkeydown);
    this.addEventListener(this._slider, "focus", this._onfocus);
    this.addEventListener(this._slider, "blur", this._onblur);
    this.addEventListener(this._slider, "mouseover", this._onmouseover);
    this.addEventListener(this._slider, "mouseout", this._onmouseout);
    this._focused = false;
}

UIWidget.extend(UISlider);

UISlider.prototype.getValue = function() {
    return this._value;
}

UISlider.prototype.setValue = function(n) {
    n = Number(n); if (isNaN(n)) return;

    var m = n % this._unitIncrement;
    n = n - m;
    if (2*m > this._unitIncrement)
        n += this._unitIncrement;
    if (n < this._minimum)
        n = this._minimum;
    else if (n > this._maximum)
        n = this._maximum;

    if (n != this._value) {
        this._value = n;
        if (this.onchange)
            this.onchange();
        this.repaint();
    }
}

UISlider.prototype.setMaximum = function(n) {
    this._maximum = n;
    this.repaint();
}

UISlider.prototype.getMaximum = function() {
    return this._maximum;
}

UISlider.prototype.setMinimum = function(n) {
    this._minimum = n;
    this.repaint();
}

UISlider.prototype.getMinimum = function() {
    return this._minimum;
}

UISlider.prototype.repaint = function() {
    var sliderWidth = UIWidget.getClientWidth(this._slider);
    var sliderHeight = UIWidget.getClientHeight(this._slider);

    if (this._horizontal) {
        UIWidget.setTop(this._track, (sliderHeight - UIWidget.getOffsetHeight(this._track)) / 2);

        UIWidget.setLeft(this._thumb, (this._value - this._minimum) / (this._maximum - this._minimum) *
                                      (sliderWidth - UIWidget.getOffsetWidth(this._thumb)));
        UIWidget.setTop(this._thumb, (sliderHeight - UIWidget.getOffsetHeight(this._thumb)) / 2);
    } else {
        UIWidget.setLeft(this._track, (sliderWidth - UIWidget.getOffsetWidth(this._track)) / 2);

        UIWidget.setLeft(this._thumb, (sliderWidth - UIWidget.getOffsetWidth(this._thumb)) / 2);
        UIWidget.setTop(this._thumb, (sliderHeight - UIWidget.getOffsetHeight(this._thumb)) *
                                     (1 - ((this._value - this._minimum) / (this._maximum - this._minimum))));
    }
}

UISlider.prototype._onmousedown = function(e) {
    this._slider.focus();
    if (e.target == this._thumb) {
        var d = this._dragData = { dx: e.screenX-this._thumb.offsetLeft, dy: e.screenY-this._thumb.offsetTop };
        if (this._slider.setCapture) {
            this._slider.setCapture();
            d.mousemove = this.addEventListener(this._slider, 'mousemove', this._onmousemove);
            d.mouseup = this.addEventListener(this._slider, 'mouseup', this._onmouseup);
            d.losecapture = this.addEventListener(this._slider, 'losecapture', this._onmouseup);
        } else {
            d.mousemove = this.addEventListener(window, 'mousemove', this._onmousemove);
            d.mouseup = this.addEventListener(window, 'mouseup', this._onmouseup);
        }
    }
}

UISlider.prototype._onmousemove = function(e) {
    if (this._dragData) {
        var size, pos;
        if (this._horizontal) {
            size = this._slider.clientWidth - this._thumb.offsetWidth;
            pos = e.screenX - this._dragData.dx;
        } else {
            size = this._slider.clientHeight - this._thumb.offsetHeight;
            pos = size - (e.screenY - this._dragData.dy);
        }
        this.setValue(this._minimum + ((this._maximum - this._minimum)*pos/size));
        e.preventDefault();
    }
}

UISlider.prototype._onmouseup = function(e) {
    if (this._dragData) {
        var d = this._dragData;
        this._dragData = null;
        if (this._slider.releaseCapture) {
            this._slider.releaseCapture();
            this.removeEventListener(this._slider, 'mousemove', d.mousemove);
            this.removeEventListener(this._slider, 'mouseup', d.mouseup);
            this.removeEventListener(this._slider, 'losecapture', d.losecapture);
        } else {
            this.removeEventListener(window, 'mousemove', d.mousemove);
            this.removeEventListener(window, 'mouseup', d.mouseup);
        }
    }
}

UISlider.prototype._onclick = function(e) {
    this._slider.focus();
    if (!this._dragData && e.target != this._thumb) {
        var size, pos;
        if (this._horizontal) {
            size = this._slider.clientWidth;
            pos = e.clientX - UIWidget.getOffsetLeft(this._slider);
        } else {
            size = this._slider.clientHeight;
            pos = size - (e.clientY - UIWidget.getOffsetTop(this._slider));
        }
        this.setValue(this._minimum + ((this._maximum - this._minimum)*pos/size));
    }
}

UISlider.prototype._onkeydown = function(e) {
    switch (e.keyCode||e.which) {
    case 33: // PAGE_UP
        this.setValue(this._value + this._blockIncrement);
        e.preventDefault();
        break;
    case 34: // PAGE_DOWN
        this.setValue(this._value - this._blockIncrement);
        e.preventDefault();
        break;
    case 36: // HOME
        this.setValue(this._horizontal ? this._minimum : this._maximum);
        e.preventDefault();
        break;
    case 35: // END
        this.setValue(this._horizontal ? this._maximum : this._minimum);
        e.preventDefault();
        break;
    case 38: // UP
    case 39: // RIGHT
        this.setValue(this._value + this._unitIncrement);
        e.preventDefault();
        break;
    case 37: // LEFT
    case 40: // DOWN
        this.setValue(this._value - this._unitIncrement);
        e.preventDefault(e);
        break;
    }
}

UISlider.prototype._onmousewheel = function(e) {
    if (this.focused) {
        var delta = e.wheelDelta ? e.wheelDelta / 40 : 0;
        this.setValue(this.getValue() + (delta * this._unitIncrement));
        e.preventDefault();
    }
}

UISlider.prototype._onfocus = function(e) {
    this.focused = true;
    UIWidget.addCssClass(this._thumb, 'slider-active');
}

UISlider.prototype._onblur = function(e) {
    this.focused = false;
    UIWidget.removeCssClass(this._thumb, 'slider-active');
}

UISlider.prototype._onmouseover = function(e) {
    if (e.target == this._thumb) {
        UIWidget.addCssClass(this._thumb, 'slider-hover');
    }
}

UISlider.prototype._onmouseout = function(e) {
    if (e.target == this._thumb) {
        UIWidget.removeCssClass(this._thumb, 'slider-hover');
    }
}

// -------------------------------------------------------------------
// UIProgressBar

function UIProgressBar(el, minimum, maximum, value) {
    if (typeof el == 'string')
        el = document.getElementById(el);
    if (!el) return;
    if (this.checkRecreate(el))
        return el._this;
    this._progressBar = el;

    this._thumb = document.createElement('div');
    this._thumb.className = "progress-thumb";
    this._progressBar.appendChild(this._thumb);

    this._minimum = minimum;
    this._maximum = maximum;
    this.setValue(value);
    this.repaint();
}

UIWidget.extend(UIProgressBar);

UIProgressBar.prototype.setValue = function(n) {
    var value = Number(n);
    if (isNaN(value) || value < this._minimum)
        value = this._minimum;
    else if (value > this._maximum)
        value = this._maximum;
    if (value != this._value) {
        this._thumb.value = this._value = value;
        this.repaint();
    }
}

UIProgressBar.prototype.getValue = function() {
    return this._value;
}

UIProgressBar.prototype.setMaximum = function(n) {
    this._maximum = n;
    this.repaint();
}

UIProgressBar.prototype.getMaximum = function() {
    return this._maximum;
}

UIProgressBar.prototype.setMinimum = function(n) {
    this._minimum = n;
    this.repaint();
}

UIProgressBar.prototype.getMinimum = function() {
    return this._minimum;
}

UIProgressBar.prototype.getState = function() {
    return this._state;
}

UIProgressBar.prototype.setState = function(s) {
    this._state = s;
    this._thumb.className = 'progress-thumb ' + s;
}

UIProgressBar.prototype.repaint = function() {
    var w = (this._value - this._minimum) / (this._maximum - this._minimum);
    UIWidget.setOffsetWidth(this._thumb, w * UIWidget.getClientWidth(this._progressBar));
    UIWidget.setOffsetHeight(this._thumb, UIWidget.getClientHeight(this._progressBar));
}

// -------------------------------------------------------------------
// UITabbedPane

function UITabbedPane(el, indexField) {
    if (typeof el == 'string')
        el = document.getElementById(el);
    if (!el) return;
    if (this.checkRecreate(el))
        return el._this;
    this._tabpane = el;

    UIWidget.addCssClass(el, 'UITabbedPane');

    this._pages = [];
    this._selectedIndex = 0;

    if (indexField) {
        // use hidden form field to store selected index
        this._indexField = indexField;
        this._selectedIndex = Number(indexField.value);
    }

    this._tabrow = document.createElement("div");
    this._tabrow.className = "tab-row";
    this._tabpane.insertBefore(this._tabrow, this._tabpane.firstChild);

    var kids = this._tabpane.childNodes;
    for (var i = 0; i < kids.length; i++) {
        if (kids[i].tagName == 'FIELDSET') {
            kids[i].style.display = 'none';
            this.addTabPage(kids[i]);
        }
    }

    if (this._pages.length > 0) {
        UIWidget.addCssClass(this._pages[0]._tab, 'first-tab');
        UIWidget.addCssClass(this._pages[this._pages.length-1]._tab, 'last-tab');
    }

    this.addEventListener(this._tabpane, "keydown", this._onkeydown);
}

UIWidget.extend(UITabbedPane);

UITabbedPane.prototype.addTabPage = function(el) {
    var index = this._pages.length;
    var tabpage = this._pages[index] = new UITabPage(el, this, index);
    this._tabrow.appendChild(tabpage._tab);
    this._tabpane.appendChild(tabpage._tabpage);

    if (index == this._selectedIndex) {
        tabpage.show();
    } else {
        tabpage.hide();
    }
    return tabpage;
}

UITabbedPane.prototype.setSelectedIndex = function(n) {
    if (this._selectedIndex != n) {
        if (this._pages[this._selectedIndex])
            this._pages[this._selectedIndex].hide();
        this._selectedIndex = n;
        if (this._indexField)
            this._indexField.value = n;
        this._pages[this._selectedIndex].show();
    }
}

UITabbedPane.prototype.getSelectedIndex = function() {
    return this._selectedIndex;
}

UITabbedPane.prototype.selectNextPage = function() {
    var index = this._selectedIndex + 1;
    if (index >= this._pages.length)
        index = 0;
    this.setSelectedIndex(index);
}

UITabbedPane.prototype.selectPreviousPage = function() {
    var index = this._selectedIndex - 1;
    if (index < 0)
        index = this._pages.length - 1;
    this.setSelectedIndex(index);
}

UITabbedPane.prototype._onkeydown = function(e) {
    var kc = e.keyCode||e.which;
    if (e.ctrlKey && kc == 34) { // SHIFT+PAGE_DOWN
        this.selectNextPage();
        e.preventDefault();
    } else if (e.ctrlKey && kc == 33) { // SHIFT+PAGE_UP
        this.selectPreviousPage();
        e.preventDefault();
    }
}

function UITabPage(el, tabpane, index) {
    this._tabpane = tabpane;
    this._index = index;

    this._tab = document.createElement('span');
    this._tab.className = 'tab';
    this._label = document.createElement('span');
    this._label.className = 'tab-label';

    var kids = el.childNodes;
    for (var i = 0; i < kids.length; i++) {
        var kid = kids[i];
        if (kid.tagName == 'LEGEND') {
            while (kid.firstChild) {
                this._label.appendChild(kid.firstChild);
            }
        } else if (kid.tagName == 'DIV') {
            kid.className = 'tab-page';
            this._tabpage = kid;
        }
    }

    this._tab.appendChild(this._label);
    UIWidget.disableSelection(this._tab);

    this.addEventListener(this._tab, 'click', this.select);
    this.addEventListener(this._tab, 'mouseover', this._onmouseover);
    this.addEventListener(this._tab, 'mouseout', this._onmouseout);
}

UIWidget.extend(UITabPage);

UITabPage.prototype.show = function() {
    UIWidget.addCssClass(this._tab, 'active-tab');
    this._tabpage.style.display = 'block';
}

UITabPage.prototype.hide = function() {
    UIWidget.removeCssClass(this._tab, 'active-tab');
    this._tabpage.style.display = 'none';
}

UITabPage.prototype.select = function() {
    this._tabpane.setSelectedIndex(this._index);
}

UITabPage.prototype._onmouseover = function() {
    UIWidget.addCssClass(this._tab, 'hover-tab');
}

UITabPage.prototype._onmouseout = function() {
    UIWidget.removeCssClass(this._tab, 'hover-tab');
}

UITabPage.prototype._onkeydown = function(e) {
    switch (e.keyCode||e.which) {
    case 38: // UP
    case 39: // RIGHT
        this._nextPage();
        e.preventDefault();
        break;
    case 37: // LEFT
    case 40: // DOWN
        this._previousPage();
        e.preventDefault();
        break;
    }
}

UITabPage.prototype._nextPage = function() {
    var tabpane = this._tabpane;
    var index = this._index + 1;
    if (index >= tabpane._pages.length)
        index = 0;
    tabpane.setSelectedIndex(index);
    tabpane._pages[tabpane._selectedIndex]._tab.focus();
}

UITabPage.prototype._previousPage = function() {
    var tabpane = this._tabpane;
    var index = this._index - 1;
    if (index < 0)
        index = tabpane._pages.length - 1;
    tabpane.setSelectedIndex(index);
    tabpane._pages[tabpane._selectedIndex]._tab.focus();
}

// -------------------------------------------------------------------
// UICollapsiblePanel

function UICollapsiblePanel(el, expanded) {
    if (typeof el == 'string')
        el = document.getElementById(el);
    if (!el) return;
    if (this.checkRecreate(el))
        return el._this;
    this._panel = el;

    this.animate = 'wipe';

    var kids = this._panel.childNodes;
    for (var i = 0; i < kids.length; i++) {
        if (kids[i].className == 'collapsible-header') {
            this._header = kids[i];
        } else if (kids[i].className == 'collapsible-body') {
            this._body = kids[i];
        }
    }

    UIWidget.setTabStop(this._header);
    UIWidget.disableSelection(this._header);

    this._button = document.createElement('span');
    this._button.className = 'collapsible-button';
    this._header.insertBefore(this._button, this._header.firstChild);

    this.addEventListener(this._header, 'click', this._onclick);
    this.addEventListener(this._header, 'dblclick', this._onclick);
    this.addEventListener(this._header, 'keyup', this._onkeyup);
    this.addEventListener(this._header, 'mouseover', this._onmouseover);
    this.addEventListener(this._header, 'mouseout', this._onmouseout);
    this.addEventListener(this._header, 'focus', this._onfocus);
    this.addEventListener(this._header, 'blur', this._onblur);
    this.addEventListener(this._button, 'focus', this.focus);

    if (expanded) {
        this._expanded = true;
        this._panel.className += ' expanded';
        this._body.style.display = 'block';
    } else {
        this._expanded = false;
        this._panel.className += ' collapsed';
        this._body.style.display = 'none';
    }
}

UIWidget.extend(UICollapsiblePanel);

UICollapsiblePanel.prototype.expand = function() {
    if (this._expanded != true) {
        this._expanded = true;
        UIWidget.removeCssClass(this._panel, 'collapsed');
        UIWidget.addCssClass(this._panel, 'expanded');

        var ani = this.animate;
        if (ani && ani != 'none' && UIAnimator[ani+'In']) {
            this._body.style.display = '';
            UIAnimator[ani+'In'](this._body, {steps:8,msecs:25});
        } else {
            this._body.style.display = '';
        }

        if (typeof this.onexpand == 'function') {
            this.onexpand();
        }
    }
}

UICollapsiblePanel.prototype.collapse = function() {
    if (this._expanded != false) {
        this._expanded = false;
        UIWidget.removeCssClass(this._panel, 'expanded');
        UIWidget.addCssClass(this._panel, 'collapsed');

        var ani = this.animate;
        if (ani && ani != 'none' && UIAnimator[ani+'Out']) {
            var p = { steps:8, msecs: 25,
                onfinish: function(el) { el.style.display = 'none'; }
            };
            UIAnimator[ani+'Out'](this._body, p);
        } else {
            this._body.style.display = 'none';
        }

        if (typeof this.oncollapse == 'function') {
            this.oncollapse();
        }
    }
}

UICollapsiblePanel.prototype.getExpanded = function() {
    return this._expanded;
}

UICollapsiblePanel.prototype.setExpanded = function(expanded) {
    if (expanded) {
        this.expand();
    } else {
        this.collapse();
    }
}

UICollapsiblePanel.prototype.toggleExpanded = function() {
    this.setExpanded(!this._expanded);
}

UICollapsiblePanel.prototype.getBody = function() {
    return this._body;
}

UICollapsiblePanel.prototype._onclick = function() {
    this.toggleExpanded();
}

UICollapsiblePanel.prototype._onkeyup = function(e) {
    var kc = e.keyCode||e.which;
    if (kc == 13 || kc == 32)
        this.toggleExpanded();
}

UICollapsiblePanel.prototype._onmouseover = function() {
    UIWidget.addCssClass(this._header, 'hover');
}

UICollapsiblePanel.prototype._onmouseout = function() {
    UIWidget.removeCssClass(this._header, 'hover');
}

UICollapsiblePanel.prototype._onfocus = function() {
    UIWidget.addCssClass(this._header, 'active');
}

UICollapsiblePanel.prototype._onblur = function() {
    UIWidget.removeCssClass(this._header, 'active');
}

UICollapsiblePanel.prototype.focus = function() {
    this._header.focus();
}


// -------------------------------------------------------------------------------
// UIWindow

function UIWindow(el) {
    if (typeof el == 'string')
        el = document.getElementById(el);
    if (!el) return;
    if (this.checkRecreate(el))
        return el._this;
    this._window = el;

    var kids = el.childNodes;
    for (var i = 0; i < kids.length; i++) {
        if (kids[i].tagName == 'IMG') {
            this._icon = kids[i];
        } else if (kids[i].tagName == 'LABEL') {
            this._label = kids[i];
        } else if (kids[i].tagName == 'DIV') {
            this._contentPane = kids[i];
        }
    }

    UIWidget.addCssClass(this._window, 'UIWindow');

    this._caption = document.createElement('div');
    this._caption.className = 'window-caption';
    UIWidget.disableSelection(this._caption);

    if (!this._icon) {
        this._icon = document.createElement('div');
        this._icon.className = 'default-icon';
    }
    UIWidget.addCssClass(this._icon, 'window-icon');
    this._caption.appendChild(this._icon);

    if (!this._label) {
        this._label = document.createElement('label');
        this._label.appendChild(document.createTextNode('Untitled'));
    }
    UIWidget.addCssClass(this._label, 'window-label');
    this._caption.appendChild(this._label);

    this._buttonPane = document.createElement('div');
    this._buttonPane.className = 'window-button-pane';
    this._caption.appendChild(this._buttonPane);

    this._minimizeButton = document.createElement('div');
    this._minimizeButton.className = 'window-button minimize-button';
    this._buttonPane.appendChild(this._minimizeButton);

    this._maximizeButton = document.createElement('div');
    this._maximizeButton.className = 'window-button maximize-button';
    this._buttonPane.appendChild(this._maximizeButton);

    this._closeButton = document.createElement('div');
    this._closeButton.className = 'window-button close-button';
    this._buttonPane.appendChild(this._closeButton);

    this._clientPane = document.createElement('div');
    this._clientPane.className = 'window-client-pane';
    if (!this._contentPane)
        this._contentPane = document.createElement('div');
    UIWidget.addCssClass(this._contentPane, 'window-content-pane');
    this._clientPane.appendChild(this._contentPane);

    while (el.firstChild) {
        el.removeChild(el.firstChild);
    }

    el.appendChild(this._caption);
    el.appendChild(this._clientPane);

    this._wm = UIWindowManager.getWindowManager();
    this._wm._add(this);
    this._setVisibility('hidden');

    // Fix for IFrames
    var frames = this._contentPane.getElementsByTagName('IFRAME');
    if (frames && frames.length > 0) {
        // use a transparent glass mask to receive activate,
        // move, and resize events.
        this._protector = document.createElement('div');
        this._protector.className = 'UIGlassPane';
        this._protector.style.visibility = 'hidden';
        this._clientPane.appendChild(this._protector);
    }

    this.addEventListener(this._window, 'mousemove', this._onWindowMouseMove);
    this.addEventListener(this._window, 'mousedown', this._onWindowMouseDown);
    this.addEventListener(this._caption, 'mouseover', this._onCaptionMouseOver);
    this.addEventListener(this._caption, 'mouseout', this._onCaptionMouseOut);
    this.addEventListener(this._caption, 'contextmenu', this._onCaptionContextMenu);
    this.addEventListener(this._caption, 'dblclick', this._onCaptionDblClick);
    this.addEventListener(this._maximizeButton, 'click', this._onMaximizeButtonClick);
    this.addEventListener(this._minimizeButton, 'click', this._onMinimizeButtonClick);
    this.addEventListener(this._closeButton, 'click', this._onCloseButtonClick);
    this.addEventListener(this._icon, 'dblclick', this._onCloseButtonClick);

    if (typeof this._createWindowHook == 'function')
        this._createWindowHook();
}

UIWidget.extend(UIWindow);

UIWindow.prototype._showIcon = true;
UIWindow.prototype._canMove = true;
UIWindow.prototype._canResize = true;
UIWindow.prototype._canMinimize = true;
UIWindow.prototype._canMaximize = true;
UIWindow.prototype._canClose = true;

UIWindow.prototype.getId = function() {
    return this._window.id;
}

UIWindow.prototype.getShowIcon = function() {
    return this._showIcon;
}

UIWindow.prototype.setShowIcon = function(b) {
    if (this._showIcon != b) {
        this._showIcon = b;
        if (this._visible)
            this.layoutChildren();
    }
    return this;
}

UIWindow.prototype.setCanMove = function(b) {
    this._canMove = b;
    return this;
}

UIWindow.prototype.getCanMove = function() {
    return this._canMove;
}

UIWindow.prototype.setCanResize = function(b) {
    this._canResize = b;
    return this;
}

UIWindow.prototype.getCanResize = function() {
    return this._canResize;
}

UIWindow.prototype.getCanMinimize = function() {
    return this._canMinimize;
}

UIWindow.prototype.setCanMinimize = function(b) {
    if (this._canMinimize != b) {
        this._canMinimize = b;
        if (this._visible)
            this.layoutChildren();
    }
    return this;
}

UIWindow.prototype.getCanMaximize = function() {
    return this._canMaximize;
}

UIWindow.prototype.setCanMaximize = function(b) {
    if (this._canMaximize != b) {
        this._canMaximize = b;
        if (this._visible)
            this.layoutChildren();
    }
    return this;
}

UIWindow.prototype.getCanClose = function() {
    return this._canClose;
}

UIWindow.prototype.setCanClose = function(b) {
    if (this._canClose != b) {
        this._canClose = b;
        if (this._visible)
            this.layoutChildren();
    }
    return this;
}

UIWindow.prototype.getCaption = function() {
    return this._label.innerHTML;
}

UIWindow.prototype.setCaption = function(s) {
    this._label.innerHTML = s;
    return this;
}

UIWindow.prototype.getIcon = function() {
    return this._icon.src;
}

UIWindow.prototype.setIcon = function(url) {
    this._icon.src = url;
    return this;
}

UIWindow.prototype.getContentPane = function() {
    return this._contentPane;
}

UIWindow.prototype.setContentPane = function(p) {
    this._clientPane.replaceChild(p, this._contentPane);
    this._contentPane = p;
    return this;
}

// Skinable defaults
UIWindow.prototype.MIN_CAPTION_HEIGHT = 18;
UIWindow.prototype.MIN_WIDTH = 124;

UIWindow.prototype.CAPTION_MARGIN_LEFT = 2;
UIWindow.prototype.CAPTION_MARGIN_RIGHT = 2;
UIWindow.prototype.CAPTION_MARGIN_TOP = 2;
UIWindow.prototype.CAPTION_PADDING_TOP = 0;
UIWindow.prototype.CAPTION_PADDING_BOTTOM = 0;
UIWindow.prototype.ICON_MARGIN_RIGHT = 4;
UIWindow.prototype.CONTENT_MARGIN_LEFT = 2;
UIWindow.prototype.CONTENT_MARGIN_RIGHT = 2;
UIWindow.prototype.CONTENT_MARGIN_BOTTOM = 2;

UIWindow.prototype.layoutChildren = function() {
    this._layoutCaption();
    this._layoutClientPane();

    if (typeof this._layoutChildrenHook == 'function')
        this._layoutChildrenHook();

    if (typeof this._drawWindow == 'function')
        this._drawWindow();
}

UIWindow.prototype._layoutCaption = function() {
    var LM, RM, TM;
    if (this._state == 'maximized') {
        LM = RM = TM = 0;
    } else {
        LM = this.CAPTION_MARGIN_LEFT;
        RM = this.CAPTION_MARGIN_RIGHT;
        TM = this.CAPTION_MARGIN_TOP;
    }

    var TP = this.CAPTION_PADDING_TOP;
    var BP = this.CAPTION_PADDING_BOTTOM;

    var captionHeight = Math.max(this.MIN_CAPTION_HEIGHT, this._label.offsetHeight);

    UIWidget.setLeft(this._caption, LM);
    UIWidget.setRight(this._caption, RM);
    UIWidget.setTop(this._caption, TM);
    UIWidget.setHeight(this._caption, captionHeight + TP + BP);

    if (UIWidget.ie || UIWidget.opera) {
        if (this._state == 'maximized')
            this._caption.style.width = '100%';
        else
            UIWidget.setOffsetWidth(this._caption, this._window.clientWidth - LM - RM);
    }

    this._layoutChromes(captionHeight);
}

UIWindow.prototype._layoutChromes = function(captionHeight) {
    var TP = this.CAPTION_PADDING_TOP;

    UIWidget.setTop(this._icon, TP + (captionHeight - this._icon.offsetHeight) / 2);
    UIWidget.setTop(this._label, TP + (captionHeight - this._label.offsetHeight) / 2);
    UIWidget.setTop(this._buttonPane, TP + (captionHeight - this._buttonPane.offsetHeight) / 2);

    this._icon.style.visibility = this._showIcon ? 'inherit' : 'hidden';
    this._closeButton.style.display = this._canClose ? '' : 'none';
    this._minimizeButton.style.display = this._canMinimize ? '' : 'none';
    this._maximizeButton.style.display = this._canMaximize ? '' : 'none';

    var l, r;
    if (this._icon.offsetLeft < this._buttonPane.offsetLeft) {
        l = this._icon.offsetLeft;
        if (this._showIcon)
            l += this._icon.offsetWidth + this.ICON_MARGIN_RIGHT;
        r = this._caption.clientWidth - this._buttonPane.offsetLeft;
    } else {
        l = this._buttonPane.offsetLeft + this._buttonPane.offsetWidth;
        r = this._caption.clientWidth - this._icon.offsetLeft;
    }

    UIWidget.setLeft(this._label, l);
    UIWidget.setRight(this._label, r);
    if (UIWidget.ie || UIWidget.opera) {
        var labelWidth = this._caption.offsetWidth - l - r;
        if (labelWidth <= 0) labelWidth = 1;
        UIWidget.setWidth(this._label, labelWidth);
    }
}

UIWindow.prototype._layoutClientPane = function() {
    var LM, RM, TM, BM;
    if (this._state == 'maximized') {
        LM = RM = TM = BM = 0;
    } else {
        LM = this.CONTENT_MARGIN_LEFT;
        RM = this.CONTENT_MARGIN_RIGHT;
        TM = this.CAPTION_MARGIN_TOP;
        BM = this.CONTENT_MARGIN_BOTTOM;
    }

    UIWidget.setLeft(this._clientPane, LM);
    UIWidget.setTop(this._clientPane, this._caption.offsetHeight + TM);
    UIWidget.setRight(this._clientPane, RM);
    UIWidget.setBottom(this._clientPane, BM);

    if (UIWidget.ie) {
        if (this._state == 'maximized') {
            this._clientPane.style.width = '100%';
            this._clientPane.style.height = '100%';
        } else {
            UIWidget.setOffsetWidth(this._clientPane, this._window.clientWidth - LM - RM);
            UIWidget.setOffsetHeight(this._clientPane, this._window.clientHeight - this._caption.offsetHeight - TM - BM);
        }

        UIWidget.setOffsetWidth(this._contentPane, this._clientPane.clientWidth);
        UIWidget.setOffsetHeight(this._contentPane, this._clientPane.clientHeight);
    }
}

UIWindow.prototype._drawWindow = function() {}

UIWindow.prototype.show = function() {
    if (this._visible != true) {
        this._visible = true;
        this._setVisibility('visible');
        this.layoutChildren();
    }
    this._wm.activate(this);
    this._window.focus();
}

UIWindow.prototype.hide = function() {
    if (this._visible != false) {
        this._visible = false;
        this._setVisibility('hidden');
        this._wm.deactivate(this);
    }
}

UIWindow.prototype._setVisibility = function(v) {
    this._window.style.visibility = v;
}

UIWindow.prototype.isVisible = function() {
    return this._visible;
}

UIWindow.prototype.close = function() {
    if (this._window == null)
        return;

    this.hide();
    this._wm._remove(this);

    try {
        this._window.parentNode.removeChild(this._window);
    } catch (e) {}

    this._window = null;
    this._caption = null;
    this._icon = null;
    this._label = null;
    this._buttonPane = null;
    this._minimizeButton = null;
    this._maximizeButton = null;
    this._closeButton = null;
    this._clientPane = null;
    this._contentPane = null;
    this._wm = null;
}

UIWindow.prototype.isClosed = function() {
    return this._window == null;
}

UIWindow.prototype._state = 'normal';

UIWindow.prototype.setState = function(new_state) {
    if (this._state == new_state)
        return;

    if (this._state == 'normal' || this._state == 'minimized') {
        this._restoreLeft = this._window.offsetLeft;
        this._restoreTop = this._window.offsetTop;
        if (this._state == 'normal') {
            this._restoreWidth = this._window.offsetWidth;
            this._restoreHeight = this._window.offsetHeight;
        }
    }

    var cs = this._window.className;
    cs = (new_state == 'maximized') ? cs + ' maximized' : cs.replace(/ maximized/g, '');
    cs = (new_state == 'minimized') ? cs + ' minimized' : cs.replace(/ minimized/g, '');
    this._window.className = cs;

    if (new_state == 'maximized') {
        this._window.style.left = 0;
        this._window.style.top = 0;
        this._window.style.right = 0;
        this._window.style.bottom = 0;
        this._window.style.width = '100%';
        this._window.style.height = '100%';
    } else {
        this._window.style.left = this._restoreLeft + 'px';
        this._window.style.top = this._restoreTop + 'px';
        this._window.style.right = null;
        this._window.style.bottom = null;
        if (new_state == 'minimized') {
            UIWidget.setOffsetWidth(this._window, this._getMinWidth());
            UIWidget.setOffsetHeight(this._window, this._getMinHeight());
        } else {
            UIWidget.setOffsetWidth(this._window, this._restoreWidth);
            UIWidget.setOffsetHeight(this._window, this._restoreHeight);
        }
    }

    if (new_state == 'minimized') {
        this._clientPane.style.display = 'none';
    } else {
        this._clientPane.style.display = '';
    }

    this._state = new_state;
    this.layoutChildren();
}

UIWindow.prototype.getState = function() {
    return this._state;
}

UIWindow.prototype.isMaximized = function() {
    return this._state == 'maximized';
}

UIWindow.prototype.isMinimized = function() {
    return this._state == 'minimized';
}

UIWindow.prototype.activate = function() {
    this._wm.activate(this);
}

UIWindow.prototype.setActive = function(b) {
    if (this._active != b) {
        this._active = b;
        if (b) {
            UIWidget.addCssClass(this._window, 'active-window');
        } else {
            UIWidget.removeCssClass(this._window, 'active-window');
        }

        if (typeof this._drawWindow == 'function') {
            this._drawWindow();
        }

        // Fix for IFrames
        if (this._protector) {
            this._protector.style.visibility = b ? 'hidden' : '';
        }
    }
    return this;
}

UIWindow.prototype.isActive = function() {
    return this._active;
}

UIWindow.prototype.bringToFront = function() {
    this._wm.bringToFront(this);
}

UIWindow.prototype.sendToBack = function() {
    this._wm.sendToBack(this);
}

UIWindow.prototype._setZIndex = function(z) {
    this._window.style.zIndex = z;
}

UIWindow.prototype.getLeft = function() {
    return this._window.offsetLeft;
}

UIWindow.prototype.setLeft = function(x) {
    UIWidget.setLeft(this._window, x);
    return this;
}

UIWindow.prototype.getTop = function() {
    return this._window.offsetTop;
}

UIWindow.prototype.setTop = function(y) {
    UIWidget.setTop(this._window, y);
    return this;
}

UIWindow.prototype.getWidth = function() {
    return this._window.offsetWidth;
}

UIWindow.prototype.setWidth = function(dx) {
    var minWidth = this._getMinWidth();
    if (dx < minWidth) dx = minWidth;
    if (this._state == 'normal') {
        UIWidget.setOffsetWidth(this._window, dx);
        if (this.isVisible())
            this.layoutChildren();
    } else {
        this._restoreHeight = dx;
    }
    return this;
}

UIWindow.prototype.getHeight = function() {
    return this._window.offsetHeight;
}

UIWindow.prototype.setHeight = function(dy) {
    var minHeight = this._getMinHeight();
    if (dy < minHeight) dy = minHeight;
    if (this._state == 'normal') {
        UIWidget.setOffsetHeight(this._window, dy);
        if (this.isVisible())
            this.layoutChildren();
    } else {
        this._restoreHeight = dy;
    }
    return this;
}

UIWindow.prototype._getMinWidth = function() {
    return this.MIN_WIDTH;
}

UIWindow.prototype._getMinHeight = function() {
    return this._caption.offsetHeight +
           this.CAPTION_MARGIN_TOP + this.CONTENT_MARGIN_BOTTOM +
           UIWidget.getOuterBoxHeight(this._window);
}

UIWindow.prototype.setRect = function(x,y,dx,dy) {
    this.setLeft(x);
    this.setTop(y);
    this.setWidth(dx);
    this.setHeight(dy);
    return this;
}

UIWindow.prototype.center = function() {
    var w = this._window, p = w.offsetParent;
    UIWidget.setLeft(w, (p.clientWidth - w.offsetWidth) / 2);
    UIWidget.setTop(w, (p.clientHeight - w.offsetHeight) / 2);
}

UIWindow.prototype.flushWindow = function() {
    var counter = 6;
    var active = false;
    var _self = this;
    function flush() {
        if (--counter == 0) {
            _self.setActive(true);
        } else {
            _self.setActive(active);
            active = !active;
            window.setTimeout(flush, 50);
        }
    }
    window.setTimeout(flush, 50);
}

UIWindow.prototype.savePosition = function() {
    var x, y, dx, dy;
    switch (this._state) {
    case 'normal':
        x = this.getLeft();
        y = this.getTop();
        dx = this.getWidth();
        dy = this.getHeigth();
        break;
    case 'minimized':
        x = this.getLeft();
        y = this.getTop();
        dx = this._restoreWidth;
        dy = this._restoreHeight;
        break;
    case 'maximized':
        x = this._restoreLeft;
        y = this._restoreTop;
        dx = this._restoreWidth;
        dy = this._restoreHeight;
        break;
    default:
        throw 'invalid state';
    }
    return [this._state, x, y, dx, dy];
}

UIWindow.prototype.restorePosition = function(props) {
    var state = props[0];
    this.setState(state);
    if (state == 'normal') {
        this.setRect(props[1], props[2], props[3], props[4]);
    } else {
        this._restoreLeft = props[1];
        this._restoreTop = props[2];
        this._restoreWidth = props[3];
        this._restoreHeight = prosp[4];
    }
}

UIWindow.prototype._getResizeDirection = function(e) {
    var x = e.clientX - UIWidget.getOffsetLeft(this._window);
    var y = e.clientY - UIWidget.getOffsetTop(this._window);
    var dir = '';

    if (y <= 20)
        dir += 'n';
    else if (y >= this._window.offsetHeight - 20)
        dir += 's';
    if (x <= 20)
        dir += 'w';
    else if (x >= this._window.offsetWidth - 20)
        dir += 'e';

    return dir;
}

UIWindow.prototype._onWindowMouseMove = function(e) {
    if (this._moveData || this._resizeData)
        return;

    var el = e.target;
    while (el) {
        if (el == this._window)
            break;
        if (el == this._contentPane)
            return;
        el = el.parentNode;
    }

    if (el) {
        var dir = this._getResizeDirection(e);
        if (dir && this._canResize && this._state == 'normal') {
            this._window.style.cursor = dir + '-resize';
        } else {
            this._window.style.cursor = 'default';
        }
    }
}

UIWindow.prototype._onWindowMouseDown = function(e) {
    this.activate();

    switch (e.target) {
    case this._caption:
    case this._label:
        if (this._canMove && this._state != 'maximized')
            this._startMove(e);
        break;
    case this._minimizeButton:
    case this._maximizeButton:
    case this._closeButton:
        this._startPush(e.target);
        break;

    default:
        var el = e.target;
        while (el) {
            if (el == this._window) {
                break;
            } else if (el == this._contentPane) {
                return;
            }
            el = el.parentNode;
        }

        if (el) {
            if (this._canResize && this._state == 'normal') {
                var dir = this._getResizeDirection(e);
                if (dir) this._startResize(e, dir);
            }
            break;
        }
        break;
    }
}

UIWindow.prototype._getParentBound = function() {
    var p = this._window.offsetParent;
    var left, right, top, bottom;
    if (p) {
        left = UIWidget.getOffsetLeft(p);
        right = left + p.offsetWidth;
        top = UIWidget.getOffsetTop(p);
        bottom = top + p.offsetHeight;
        left += UIWidget.getPixelValue(p, 'border-left-width') + UIWidget.getPixelValue(p, 'padding-left');
        right -= UIWidget.getPixelValue(p, 'border-right-width') + UIWidget.getPixelValue(p, 'padding-right');
        top += UIWidget.getPixelValue(p, 'border-top-width') + UIWidget.getPixelValue(p, 'padding-top');
        bottom -= UIWidget.getPixelValue(p, 'border-bottom-width') + UIWidget.getPixelValue(p, 'padding-bottom');
    } else {
        left = 0;
        top = 0;
        right = screen.availWidth;
        bottom = screen.availHeight;
    }

    return { left: left, top: top, right: right, bottom: bottom };
}

UIWindow.prototype._startMove = function(e) {
    var mov = this._moveData = {
        dx: e.clientX - this._window.offsetLeft,
        dy: e.clientY - this._window.offsetTop,
        bound: this._getParentBound()
    };

    if (this._window.setCapture) {
        this._window.setCapture();
        mov.mousemove = this.addEventListener(this._window, 'mousemove', this._continueMove);
        mov.mouseup = this.addEventListener(this._window, 'mouseup', this._endMove);
        mov.losecapture = this.addEventListener(this._window, 'losecapture', this._endMove);
    } else {
        mov.mousemove = this.addEventListener(window, 'mousemove', this._continueMove);
        mov.mouseup = this.addEventListener(window, 'mouseup', this._endMove);
    }

    if (this._protector) {
        this._protector.style.visibility = '';
    }

    e.preventDefault();
}

UIWindow.prototype._continueMove = function(e) {
    if (this._moveData == null)
        return;

    var mov = this._moveData;
    var ex = e.clientX, ey = e.clientY;

    ex = Math.max(Math.min(ex, mov.bound.right), mov.bound.left);
    ey = Math.max(Math.min(ey, mov.bound.bottom), mov.bound.top);

    var x = ex - mov.dx;
    var y = ey - mov.dy;

    if (UIWidget.moz) {
        var p = this._window.offsetParent;
        if (p) {
            x += UIWidget.getPixelValue(p, 'border-left-width');
            y += UIWidget.getPixelValue(p, 'border-top-width');
        }
    }

    this.setLeft(x);
    this.setTop(y);

    e.preventDefault();
}

UIWindow.prototype._endMove = function(e) {
    if (this._moveData == null)
        return;

    var mov = this._moveData;
    this._moveData = null;

    if (this._window.releaseCapture) {
        this._window.releaseCapture();
        this.removeEventListener(this._window, 'mousemove', mov.mousemove);
        this.removeEventListener(this._window, 'mouseup', mov.mouseup);
        this.removeEventListener(this._window, 'losecapture', mov.losecapture);
    } else {
        this.removeEventListener(window, 'mousemove', mov.mousemove);
        this.removeEventListener(window, 'mouseup', mov.mouseup);
    }

    if (this._protector) {
        this._protector.style.visibility = 'hidden';
    }

    e.preventDefault();
}

UIWindow.prototype._startResize = function(e, dir) {
    var r = this._resizeData = {
        dir: dir,
        clientX: e.clientX,
        clientY: e.clientY,
        startX: this._window.offsetLeft,
        startY: this._window.offsetTop,
        startW: this._window.offsetWidth,
        startH: this._window.offsetHeight,
        bound: this._getParentBound()
    }

    if (this._window.setCapture) {
        this._window.setCapture();
        r.mousemove = this.addEventListener(this._window, 'mousemove', this._continueResize);
        r.mouseup = this.addEventListener(this._window, 'mouseup', this._endResize);
        r.losecapture = this.addEventListener(this._window, 'losecapture', this._endResize);
    } else {
        r.mousemove = this.addEventListener(window, 'mousemove', this._continueResize);
        r.mouseup = this.addEventListener(window, 'mouseup', this._endResize);
    }

    if (this._protector) {
        this._protector.style.visibility = '';
    }

    e.preventDefault();
}

UIWindow.prototype._continueResize = function(e) {
    if (this._resizeData == null)
        return;

    var r = this._resizeData;
    var ex = e.clientX, ey = e.clientY;

    ex = Math.max(Math.min(ex, r.bound.right), r.bound.left);
    ey = Math.max(Math.min(ey, r.bound.bottom), r.bound.top);

    var x = r.startX, y = r.startY, w = r.startW, h = r.startH;

    if (/e/i.test(r.dir)) {
        w = Math.max(this._getMinWidth(), r.startW + (ex - r.clientX));
    } else if (/w/i.test(r.dir)) {
        w = Math.max(this._getMinWidth(), r.startW - (ex - r.clientX));
        x = r.startW + r.startX - w;
    }

    if (/s/i.test(r.dir)) {
        h = Math.max(this._getMinHeight(), r.startH + (ey - r.clientY));
    } else if (/n/i.test(r.dir)) {
        h = Math.max(this._getMinHeight(), r.startH - (ey - r.clientY));
        y = r.startH + r.startY - h;
    }

    this.setRect(x, y, w, h);
    e.preventDefault();
}

UIWindow.prototype._endResize = function(e) {
    if (this._resizeData == null)
        return;

    var r = this._resizeData;
    this._resizeData = null;

    if (this._window.releaseCapture) {
        this._window.releaseCapture();
        this.removeEventListener(this._window, 'mousemove', r.mousemove);
        this.removeEventListener(this._window, 'mouseup', r.mouseup);
        this.removeEventListener(this._window, 'losecapture', r.losecapture);
    } else {
        this.removeEventListener(window, 'mousemove', r.mousemove);
        this.removeEventListener(window, 'mouseup', r.mouseup);
    }

    if (this._protector) {
        this._protector.style.visibility = 'hidden';
    }

    e.preventDefault();
}

UIWindow.prototype._startPush = function(btn) {
    this._setButtonPressed(btn, true);
    var p = this._pushData = { button: btn };
    if (btn.setCapture) {
        btn.setCapture();
        p.mousemove = this.addEventListener(btn, 'mousemove', this._checkPush);
        p.mouseup = this.addEventListener(btn, 'mouseup', this._endPush);
        p.losecapture = this.addEventListener(btn, 'losecapture', this._endPush);
    } else {
        p.mousemove = this.addEventListener(window, 'mousemove', this._checkPush);
        p.mouseup = this.addEventListener(window, 'mouseup', this._endPush);
    }
}

UIWindow.prototype._checkPush = function(e) {
    if (this._pushData != null) {
        var button = this._pushData.button;
        this._setButtonPressed(button, e.target == button);
    }
}

UIWindow.prototype._endPush = function(e) {
    if (this._pushData != null) {
        var p = this._pushData;
        this._pushData = null;
        this._setButtonPressed(p.button, false);
        if (p.button.releaseCapture) {
            p.button.releaseCapture();
            this.removeEventListener(p.button, 'mousemove', p.mousemove);
            this.removeEventListener(p.button, 'mouseup', p.mouseup);
            this.removeEventListener(p.button, 'losecapture', p.losecapture);
        } else {
            this.removeEventListener(window, 'mousemove', p.mousemove);
            this.removeEventListener(window, 'mouseup', p.mouseup);
        }
    }
}

UIWindow.prototype._onCaptionMouseOver = function(e) {
    switch (e.target) {
    case this._minimizeButton:
    case this._maximizeButton:
    case this._closeButton:
        this._setButtonHover(e.target, true);
        break;
    }
}

UIWindow.prototype._onCaptionMouseOut = function(e) {
    switch (e.target) {
    case this._minimizeButton:
    case this._maximizeButton:
    case this._closeButton:
        this._setButtonHover(e.target, false);
        break;
    }
}

UIWindow.prototype._setButtonHover = function(b,f) {
    if (b._hover != f) {
        b._hover = f;
        if (f) {
            UIWidget.addCssClass(b, 'hover');
        } else {
            UIWidget.removeCssClass(b, 'hover');
        }
    }
}

UIWindow.prototype._setButtonPressed = function(b,f) {
    if (b._pressed != f) {
        b._pressed = f;
        if (f) {
            UIWidget.addCssClass(b, 'pressed');
        } else {
            UIWidget.removeCssClass(b, 'pressed');
        }
    }
}

UIWindow.prototype._onMaximizeButtonClick = function(e) {
    if (this._canMaximize) {
        if (this._state == 'maximized') {
            this.setState('normal');
        } else {
            this.setState('maximized');
        }
    }
}

UIWindow.prototype._onMinimizeButtonClick = function(e) {
    if (this._canMinimize) {
        if (this._state == 'minimized') {
            this.setState('normal');
        } else {
            this.setState('minimized');

            this.sendToBack();
            var top = this._wm.top();
            if (top != this) top.activate();
        }
    }
}

UIWindow.prototype._onCaptionContextMenu = function(e) {
    this.activate();
    this._onMinimizeButtonClick(e);
    e.preventDefault();
}

UIWindow.prototype._onCaptionDblClick = function(e) {
    if (this._state == 'normal') {
        if (this._canMaximize) {
            this.setState('maximized');
        }
    } else if (this._state == 'maximized') {
        if (this._canMaximize) {
            this.setState('normal');
        }
    } else if (this._state == 'minimized') {
        if (this._canMinimize) {
            this.setState('normal');
        }
    }
}

UIWindow.prototype._onCloseButtonClick = function(e) {
    e.stopPropagation();

    if (this.onbeforeclose && this.onbeforeclose() == false)
        return;
    this.hide();

    // if onclose callback function returns true, then destroy
    // the window. Otherwise, keep window in hidden mode.
    if (!this.onclose || this.onclose() == false)
        return;
    this.close();
}

// UIWindowTemplate

function UIWindowTemplate(el) {
    if (typeof el == 'string')
        el = document.getElementById(el);
    if (!el) return;
    if (this.checkRecreate(el))
        return el._this;
    el.style.display = 'none';
    this._template = el;
}

UIWidget.extend(UIWindowTemplate);

UIWindowTemplate.prototype.getLeft = function() { return this._left; };
UIWindowTemplate.prototype.setLeft = function(x) { this._left = x; return this; };
UIWindowTemplate.prototype.getTop = function() { return this._top; };
UIWindowTemplate.prototype.setTop = function(y) { this._top = y; return this; };
UIWindowTemplate.prototype.getWidth = function() { return this._width; };
UIWindowTemplate.prototype.setWidth = function(dx) { this._width = dx; return this; };
UIWindowTemplate.prototype.getHeight = function() { return this._height; };
UIWindowTemplate.prototype.setHeight = function(dy) { this._height = dy; return this; };
UIWindowTemplate.prototype.getShowIcon = function() { return this._showIcon; };
UIWindowTemplate.prototype.setShowIcon = function(b) { this._showIcon = b; return this; };
UIWindowTemplate.prototype.getCanMove = function(b) { return this._canMove; };
UIWindowTemplate.prototype.setCanMove = function(b) { this._canMove = b; return this; };
UIWindowTemplate.prototype.getCanResize = function() { return this._canResize; };
UIWindowTemplate.prototype.setCanResize = function(b) { this._canResize = b; return this; };
UIWindowTemplate.prototype.getCanMinimize = function() { return this._canMinimize; };
UIWindowTemplate.prototype.setCanMinimize = function(b) { this._canMinimize = b; return this; };
UIWindowTemplate.prototype.getCanMaximize = function() { return this._canMaximize; };
UIWindowTemplate.prototype.setCanMaximize = function(b) { this._canMaximize = b; return this; };
UIWindowTemplate.prototype.getCanClose = function() { return this._canClose; };
UIWindowTemplate.prototype.setCanClose = function(b) { this._canClose = b; return this; };

UIWindowTemplate._nextID = 0;

UIWindowTemplate.prototype.create = function() {
    var el = this._template.cloneNode(true);
    el.style.display = '';
    this._template.parentNode.appendChild(el);

    var win = new UIWindow(el);

    var winid = win._window.id || '_window';
    winid += '_' + UIWindowTemplate._nextID++;
    win._window.id = winid;
    UIWindowTemplate._setChildID(win._window, winid);
    win.getElementById = function(id) {
        return document.getElementById(winid + ':' + id);
    };

    if (this._left != null)
        win.setLeft(this._left);
    if (this._top != null)
        win.setTop(this._top);
    if (this._width != null)
        win.setWidth(this._width);
    if (this._height != null)
        win.setHeight(this._height);
    if (this._showIcon != null)
        win.setShowIcon(this._showIcon);
    if (this._canMove != null)
        win.setCanMove(this._canMove);
    if (this._canResize != null)
        win.setCanResize(this._canResize);
    if (this._canMinimize != null)
        win.setCanMinimize(this._canMinimize);
    if (this._canMaximize != null)
        win.setCanMaximize(this._canMaximize);
    if (this._canClose != null)
        win.setCanClose(this._canClose);

    if (this.onbeforeclose != null)
        win.onbeforeclose = this.onbeforeclose;

    var onclose = this.onclose;
    win.onclose = function() {
        if (onclose && onclose.call(win) == false)
            return false;
        return true;
    };

    return win;
}

UIWindowTemplate._setChildID = function(el, prefix) {
    var kids = el.childNodes;
    if (kids) {
        for (var i = 0; i < kids.length; i++) {
            var child = kids[i];
            if (child.nodeType == 1 && child.id) {
                child.id = prefix + ':' + child.id;
            }
            UIWindowTemplate._setChildID(child, prefix);
        }
    }
}

// UIWindowManager

function UIWindowManager() {
    if (UIWindowManager._singleton)
        return UIWindowManager._singleton;
    UIWindowManager._singleton = this;
    this._windows = [];
}

if (!UIWindowManager._singleton) {
    new UIWindowManager();
}

UIWindowManager.getWindowManager = function() {
    return UIWindowManager._singleton;
}

UIWindowManager.prototype.indexOf = function(w) {
    for (var i = 0; i < this._windows.length; i++) {
        if (this._windows[i] == w)
            return i;
    }
    return -1;
}

UIWindowManager.prototype.byId = function(id) {
    for (var i = 0; i < this._windows.length; i++) {
        if (this._windows[i].getId() == id)
            return this._windows[i];
    }
    return null;
}

UIWindowManager.prototype._add = function(w) {
    this._windows.push(w);
    this.arrange();
}

UIWindowManager.prototype._remove = function(w) {
    var i = this.indexOf(w);
    if (i != -1) {
        this._windows.splice(i, 1);
    }
}

UIWindowManager.prototype.getWindows = function() {
    var result = new Array();
    for (var i = 0; i < this._windows.length; i++) {
        result[i] = this._windows[i];
    }
    return result;
}

UIWindowManager.prototype.activate = function(w) {
    if (w.isVisible() && this._activeWindow != w) {
        if (this._activeWindow)
            this._activeWindow.setActive(false);
        this._activeWindow = w;
        w.setActive(true);
        this.bringToFront(w);
    }
}

UIWindowManager.prototype.deactivate = function(w) {
    if (w == this._activeWindow) {
        this._activeWindow = null;
        var top = this.top();
        if (top) top.activate();
    }
}

UIWindowManager.prototype.getActiveWindow = function() {
    return this._activeWindow;
}

UIWindowManager.prototype.bringToFront = function(w) {
    var i = this.indexOf(w);
    if (i != -1) {
        this._windows.splice(i, 1);
        this._windows.push(w);
        this.arrange();
    }
}

UIWindowManager.prototype.sendToBack = function(w) {
    var i = this.indexOf(w);
    if (i != -1) {
        this._windows.splice(i, 1);
        this._windows.unshift(w);
        this.arrange();
    }
}

UIWindowManager.prototype.top = function() {
    for (var i = this._windows.length-1; i >= 0; i--) {
        if (this._windows[i].isVisible()) {
            return this._windows[i];
        }
    }
    return null;
}

UIWindowManager.prototype.arrange = function() {
    for (var i = 0; i < this._windows.length; i++) {
        if (this._windows[i].isVisible()) {
            this._windows[i]._setZIndex(1000 + i);
        }
    }
}

UIWindowManager.prototype.savePreferences = function() {
    var prefs = '';
    for (var i = 0; i < this._windows.length; i++) {
        var w = this._windows[i];
        if (!(w instanceof UIDialog)) {
            if (prefs.length != 0) prefs += '|';
            prefs += w.getId() + ',' + w.savePosition().join(',');
        }
    }
    return prefs;
}

UIWindowManager.prototype.restorePreferences = function(prefs) {
    var components = prefs.split('|');
    for (var i = 0; i < components.length; i++) {
        var props = components[i].split(',');
        var win = this.byId(props[0]);
        if (win != null) {
            props.shift();
            win.restorePosition(props);
        }
    }
}

// UIDialog

function UIDialog(el) {
    if (!el) return;

    this._glassPane = document.createElement('div');
    this._glassPane.className = 'UIGlassPane';
    UIWidget.setTabStop(this._glassPane, false);

    UIWindow.call(this, el);
    this._window.className += ' UIDialog';
    this._glassPane.appendChild(this._window);
    document.body.appendChild(this._glassPane);

    this.setShowIcon(false);
    this.setCanMove(true);
    this.setCanResize(false);
    this.setCanMinimize(false);
    this.setCanMaximize(false);
    this.setCanClose(true);

    this.addEventListener(this._glassPane, 'mousedown', this._onmousedown);
    this.addEventListener(this._glassPane, 'contextmenu', this._oncontextmenu);
    this.addEventListener(this._window, 'keydown', this._onkeydown);
}

UIDialog.prototype = new UIWindow;

UIDialog.prototype.show = function() {
    if (!this.isVisible()) {
        UIWindow.prototype.show.call(this);
        this.center();
        if (UIWidget.ie) {
            this._hideControls('SELECT');
            this._hideControls('OBJECT');
        }
    }
}

UIDialog.prototype.hide = function() {
    if (this.isVisible()) {
        UIWindow.prototype.hide.call(this);
        if (UIWidget.ie) {
            this._showControls();
        }
    }
}

UIDialog.prototype._setVisibility = function(v) {
    this._window.style.visibility = v;
    this._glassPane.style.visibility = v;
}
UIDialog.prototype._setZIndex = function(z) {
    this._glassPane.style.zIndex = z;
}

UIDialog.prototype.setDefaultButton = function(btn) {
    if (typeof btn == 'string')
        btn = document.getElementById(btn);
    this._defaultButton = btn;
}

UIDialog.prototype.setCancelButton = function(btn) {
    if (typeof btn == 'string')
        btn = document.getElementById(btn);
    if (btn) {
        this.addEventListener(btn, 'click', this._onCancelCommand);
        this._cancelButton = btn;
    }
}

UIDialog.prototype._onCancelCommand = function(e) {
    this.hide();
    e.preventDefault();
}

UIDialog.prototype._onkeydown = function(e) {
    if (e.keyCode == 13) {
        if (this._defaultButton) {
            this._defaultButton.click();
            e.preventDefault();
        }
    } else if (e.keyCode == 27) {
        if (this._cancelButton) {
            this._cancelButton.click();
            e.preventDefault();
        }
    }
}

UIDialog.prototype._hideControls = function(tagName) {
    if (!this._overlap) this._overlap = [];

    var tags = document.all.tags(tagName);
    for (var i = 0; i < tags.length; i++) {
        var obj = tags[i];
        if (!this._isDescendent(obj) && obj.style.visibility != 'hidden') {
            this._overlap.push(obj);
            obj.style.visibility = 'hidden';
        }
    }
}

UIDialog.prototype._showControls = function() {
    if (this._overlap) {
        for (var i = 0; i < this._overlap.length; i++) {
            this._overlap[i].style.visibility = '';
        }
        this._overlap = null;
    }
}

UIDialog.prototype._isDescendent = function(el) {
    while (el) {
        if (el == this._window)
            return true;
        el = el.parentNode;
    }
    return false;
}

UIDialog.prototype._onmousedown = function(e) {
    if (e.target == this._glassPane) {
        this.flushWindow();
        e.preventDefault();
        e.stopPropagation();
    }
}

UIDialog.prototype._oncontextmenu = function(e) {
    if (e.target.tagName == 'INPUT' && e.target.type == 'text') {
        return;
    } else if (e.target.tagName == 'TEXTAREA') {
        return;
    } else {
        e.preventDefault();
    }
}


// -------------------------------------------------------------------------------
// UICookMenu: Based on JSCookMenu, modified to support JavaServer Faces.
// TODO: Make UICookMenu object oriented

/*
    JSCookMenu v1.4.4.  (c) Copyright 2002-2005 by Heng Yuan

    Permission is hereby granted, free of charge, to any person obtaining a
    copy of this software and associated documentation files (the "Software"),
    to deal in the Software without restriction, including without limitation
    the rights to use, copy, modify, merge, publish, distribute, sublicense,
    and/or sell copies of the Software, and to permit persons to whom the
    Software is furnished to do so, subject to the following conditions:

    The above copyright notice and this permission notice shall be included
    in all copies or substantial portions of the Software.

    THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
    OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
    ITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
    AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
    LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
    FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
    DEALINGS IN THE SOFTWARE.
*/

// Globals
var _cmIDCount = 0;
var _cmIDName = 'cmSubMenuID';      // for creating submenu id

var _cmTimeOut = null;              // how long the menu would stay
var _cmCurrentItem = null;          // the current menu item being selected;
var _cmMenuIsOpen = false;          // indicate a sub menu is open

var _cmSplit = new Object();        // indicate that the item is a menu split

// default node properties
var _cmNodeProperties =
{
    // main menu display attributes
    //
    // Note.  When the menu bar is horizontal,
    // mainFolderLeft and mainFolderRight are
    // put in <span></span>.  When the menu
    // bar is vertical, they would be put in
    // a separate TD cell.

    // HTML code to the left of the folder item
    mainFolderLeft: '',
    // HTML code to the right of the folder item
    mainFolderRight: '',
    // HTML code to the left of the regular item
    mainItemLeft: '',
    // HTML code to the right of the regular item
    mainItemRight: '',

    // sub menu display attributes

    // HTML code to the left of the folder item
    folderLeft: '',
    // HTML code to the right of the folder item
    folderRight: '',
    // HTML code to the left of the regular item
    itemLeft: '',
    // HTML code to the right of the regular item
    itemRight: '',

    // cell spacing for main menu
    mainSpacing: 0,
    // cell spacing for sub menus
    subSpacing: 0,
    // auto disappear time for submenus in milli-seconds
    delay: 600,

    // act on click to open sub menu
    clickOpen: true
};

//////////////////////////////////////////////////////////////////////
//
// Drawing Functions and Utility Functions
//
//////////////////////////////////////////////////////////////////////

//
// produce a new unique id
//
function cmNewID()
{
    return _cmIDName + (++_cmIDCount);
}

//
// return the property string for the menu item
//
function cmActionItem(item, idSub, orient, nodeProperties)
{
    var index = nodeProperties.itemList.length;
    nodeProperties.itemList[index] = item;
    idSub = (!idSub) ? 'null' : ('\'' + idSub + '\'');
    orient = '\'' + orient + '\'';
    var returnStr;
    if (nodeProperties.clickOpen)
        returnStr = ' onmouseover="cmItemMouseOver(this,' + idSub + ',' + orient + ',' + index + ')"' +
                    ' onmousedown="cmItemMouseDownOpenSub(this,' + index + ',' + orient + ',' + idSub + ')"';
    else
        returnStr = ' onmouseover="cmItemMouseOverOpenSub(this,' + idSub + ',' + orient + ',' + index + ')"' +
                    ' onmousedown="cmItemMouseDown(this,' + index + ')"';
    return returnStr + ' onmouseout="cmItemMouseOut(this,' + nodeProperties.delay + ')"'
                     + ' onmouseup="cmItemMouseUp(this,' + index + ')"';
}

//
// this one is used by _cmNoClick to only take care of onmouseover and onmouseout
// events which are associated with menu but not actions associated with menu clicking/closing
//
function cmNoClickItem(item, idSub, orient, nodeProperties)
{
    var index = nodeProperties.itemList.length;
    nodeProperties.itemList[index] = item;
    idSub = !idSub ? 'null' : ('\'' + idSub + '\'');
    orient = '\'' + orient + '\'';
    return ' onmouseover="cmItemMouseOver(this,' + idSub + ',' + orient + ',' + index + ')"' +
           ' onmouseout="cmItemMouseOut(this,' + nodeProperties.delay + ')"';
}

function cmSplitItem(isMain, vertical, nodeProperties)
{
    var name;
    if (isMain) {
        name = vertical ? 'MainHSplit' : 'MainVSplit';
    } else {
        name = 'HSplit';
    }

    var splitStr = nodeProperties[name];
    if (!splitStr) splitStr = '';
    return { disabled: true, split: splitStr };
}

//
// draw the sub menu recursively
//
function cmDrawSubMenu(subMenu, id, orient, nodeProperties)
{
    var str = '<div class="SubMenu" id="' + id + '" onselectstart="return false" style="-moz-user-select:none">' +
              '<table summary="sub menu" cellspacing="' + nodeProperties.subSpacing + '" class="SubMenuTable">\n';
    var strSub = '';

    var item, idSub, i;

    for (i = 0; i < subMenu.length; ++i) {
        item = subMenu[i];
        if (!item)
            continue;

        if (item == _cmSplit)
            item = cmSplitItem(false, true, nodeProperties);

        idSub = item.subMenu ? cmNewID() : null;

        str += '<tr class="MenuItem';
        if (item.subMenu)
            str += ' Folder';
        if (item.disabled)
            str += ' Disabled';
        str += '"';
        if (!item.disabled)
            str += cmActionItem(item, idSub, orient, nodeProperties);
        else
            str += cmNoClickItem(item, idSub, orient, nodeProperties);
        str += '>\n';

        if (item.split) {
            str += item.split;
            str += '</tr>\n';
            continue;
        }

        str += '<td class="Left">';
        if (item.icon) {
            str += item.icon;
        } else if (item.subMenu) {
            str += nodeProperties.folderLeft;
        } else {
            str += nodeProperties.itemLeft;
        }

        str += '</td><td class="Text">';
        if (item.label)
            str += item.label;

        str += '</td><td class="Right">';
        if (item.subMenu) {
            str += nodeProperties.folderRight;
            strSub += cmDrawSubMenu(item.subMenu, idSub, orient, nodeProperties);
        } else {
            str += nodeProperties.itemRight;
        }
        str += '</td></tr>\n';
    }

    str += '</table></div>\n' + strSub;
    return str;
}


//
// The function that builds the menu inside the specified element id.
//
// @param	id	id of the element
//		orient	orientation of the menu in [hv][ab][lr] format
//		menu	the menu object to be drawn
//		nodeProperties	properties for each menu node
//
function cmDraw(id, menu, orient, nodeProperties) {
    var obj = cmGetObject(id);

    if (!nodeProperties) {
        nodeProperties = new Object();
    }
    if (window.UICookMenu_skinProperties) {
        var skinProperties = UICookMenu_skinProperties;
        for (var p in skinProperties) {
            if (typeof nodeProperties[p] == 'undefined')
                nodeProperties[p] = skinProperties[p];
        }
    }
    for (var p in _cmNodeProperties) {
        if (typeof nodeProperties[p] == 'undefined') {
            nodeProperties[p] = _cmNodeProperties[p];
        }
    }

    var str = '<table summary="main menu" class="Menu" cellspacing="' + nodeProperties.mainSpacing + '">\n';
    var strSub = '';

    if (!orient)
        orient = 'hbr';

    var orientStr = String(orient);
    var orientSub;
    var vertical;

    // draw the main menu items
    if (orientStr.charAt(0) == 'h') {
        // horizontal menu
        orientSub = 'v' + orientStr.substr(1, 2);
        str += '<tr>';
        vertical = false;
    } else {
        // vertical menu
        orientSub = 'v' + orientStr.substr(1, 2);
        vertical = true;
    }

    var i, item, idSub;

    nodeProperties.itemList = new Array();
    for (i = 0; i < menu.length; i++) {
        item = menu[i];
        if (!item)
            continue;

        if (item == _cmSplit)
            item = cmSplitItem(true, vertical, nodeProperties);

        idSub = item.subMenu ? cmNewID() : null;

        str += (vertical ? '<tr' : '<td') + ' class="MainItem';
        if (item.subMenu)
            str += ' Folder';
        if (item.disabled)
            str += ' Disabled';
        str += '"';
        if (!item.disabled)
            str += cmActionItem(item, idSub, orient, nodeProperties);
        else
            str += cmNoClickItem(item, idSub, orient, nodeProperties);
        str += '>\n';

        if (item.split) {
            str += item.split;
            str += vertical ? '</tr>\n' : '</td>\n';
            continue;
        }

        str += vertical ? '<td' : '<span';
        str += ' class="Left">';
        if (item.icon) {
            str += item.icon;
        } else if (item.subMenu) {
            str += nodeProperties.mainFolderLeft;
        } else {
            str += nodeProperties.mainItemLeft;
        }
        str += vertical ? '</td>' : '</span>';

        str += vertical ? '<td' : '<span';
        str += ' class="Text">';
        if (item.label)
            str += item.label;
        str += vertical ? '</td>' : '</span>';

        str += vertical ? '<td' : '<span';
        str += ' class="Right">';
        if (vertical)
            str += item.subMenu ? nodeProperties.folderRight : nodeProperties.itemRight;
        else
            str += item.subMenu ? nodeProperties.mainFolderRight : nodeProperties.mainItemRight;
        str += vertical ? '</td>' : '</span>';

        str += vertical ? '</tr>\n' : '</td>\n';

        if (item.subMenu) {
            strSub += cmDrawSubMenu(item.subMenu, idSub, orientSub, nodeProperties);
        }
    }

    if (!vertical)
        str += '<td width="100%"></td></tr>';
    str += '</table>\n' + strSub;

    obj.innerHTML = str;
    //document.write("<xmp>" + str + "</xmp>");

    // store the node properties in dom for later retrieve
    obj.cmNodeProperties = nodeProperties;
}

//
// The function builds the menu inside the specified element id.
//
// This function is similar to cmDraw except that menu is taken from HTML node
// rather a javascript tree.  This feature allows links to be scanned by search
// bots.
//
// This function basically converts HTML node to a javascript tree, and then calls
// cmDraw to draw the actual menu, replacing the hidden menu tree.
//
// Format:
//  <div id="menu">
//      <ul style="visibility: hidden">
//          <li><span>icon</span><a href="link" title="description">main menu text</a>
//              <ul>
//                  <li><span>icon</span><a href="link" title="description">submenu item</a>
//                  </li>
//              </ul>
//          </li>
//      </ul>
//  </div>
//
function cmDrawMenu(id, domId, orient, nodeProperties)
{
    var menu = null;
    var domMenu = cmGetObject(domId);
    for (var currentDomItem = domMenu.firstChild; currentDomItem; currentDomItem = currentDomItem.nextSibling) {
        if (!currentDomItem.tagName || currentDomItem.tagName.toLowerCase() != 'ul')
            continue;
        menu = cmBuildMenu(currentDomItem);
        break;
    }
    if (menu) {
        cmDraw(id, menu, orient, nodeProperties);
    }
}

//
// a recursive function that build menu tree structure
//
function cmBuildMenu(domMenu)
{
    var items = new Array();
    for (var currentDomItem = domMenu.firstChild; currentDomItem; currentDomItem = currentDomItem.nextSibling) {
        if (!currentDomItem.tagName || currentDomItem.tagName.toLowerCase() != 'li')
            continue;

        var currentItem = currentDomItem.firstChild;
        var item = new Object();

        // scan for icon and link
        for (; currentItem; currentItem = currentItem.nextSibling) {
            if (!currentItem.tagName)
                continue;
            var tag = currentItem.tagName.toLowerCase();
            if (tag == 'img') {
                item.icon = '<img src="' + currentItem.src + '"';
                if (currentItem.alt)
                    item.icon += ' alt="' + currentItem.alt + '"';
                item.icon += '>';
            } else {
                var domItem = currentItem;
                if (tag == 'span' && currentItem.id.match(/\:_outer$/)) {
                    // an AJAX dynamic content, go into inner node
                    domItem = currentItem.firstChild;
                    tag = domItem.tagName.toLowerCase();
                }
                if (tag == 'a') {
                    item.source = domItem;
                    item.href = domItem.href;
                    item.target = domItem.target;
                    item.label = domItem.innerHTML;
                    item.title = domItem.title;
                    break;
                } else if (tag == 'p' || tag == 'span' || tag == 'div') {
                    item.disabled = true;
                    item.label = domItem.innerHTML;
                    item.title = domItem.title;
                    break;
                }
            }
        }

        if (!item.icon && !item.label) {
            items[items.length] = _cmSplit;
            continue;
        }

        // scan for sub menu
        currentItem = currentDomItem.firstChild;
        for (; currentItem; currentItem = currentItem.nextSibling) {
            if (!currentItem.tagName || currentItem.tagName.toLowerCase() != 'ul')
                continue;
            item.subMenu = cmBuildMenu(currentItem);
            break;
        }

        items[items.length] = item;
    }
    return items;
}

//////////////////////////////////////////////////////////////////////
//
// Mouse Event Handling Functions
//
//////////////////////////////////////////////////////////////////////

//
// action should be taken for mouse moving in to the menu item
//
// Here we just do things concerning this menu item, w/o opening sub menus.
//
function cmItemMouseOver(obj, idSub, orient, index)
{
    clearTimeout(_cmTimeOut);

    var thisMenu = cmGetThisMenu(obj);

    // insert obj into cmItems if cmItems doesn't have obj
    if (!thisMenu.cmItems)
        thisMenu.cmItems = new Array();
    for (var i = 0; i < thisMenu.cmItems.length; ++i) {
        if (thisMenu.cmItems[i] == obj)
            break;
    }
    if (i == thisMenu.cmItems.length) {
        thisMenu.cmItems[i] = obj;
    }

    // hide the previous submenu that is not this branch
    if (_cmCurrentItem) {
        // occationally, we get this case when user
        // move the mouse slowly to the border
        if (_cmCurrentItem == obj || _cmCurrentItem == thisMenu) {
            var item = cmGetMenuItem(obj, index);
            cmSetStatus(item);
            return;
        }

        var thatMenu = cmGetThisMenu(_cmCurrentItem);
        if (thatMenu != thisMenu.cmParentMenu) {
            var str = _cmCurrentItem.className;
            str = str.replace(/ Hover/g, '').replace(/ Active/g, '');
            _cmCurrentItem.className = str;
            if (thatMenu.id != idSub) {
                cmHideMenu(thatMenu, thisMenu);
            }
        }
    }

    // okay, set the current menu to this obj
    _cmCurrentItem = obj;

    // just in case, reset all items in this menu to MenuItem
    cmResetMenu(thisMenu);

    var item = cmGetMenuItem(obj, index);
    if (!item.disabled)
        obj.className += ' Hover';
    cmSetStatus(item);

    // if any menu was open, no need to click to open the sub menu
    if (_cmMenuIsOpen && idSub && !item.disabled) {
        var subMenu = cmGetObject(idSub);
        cmShowSubMenu(obj, subMenu, orient);
    }
}

//
// action should be taken for mouse moving in to the menu item
//
// This function also opens sub menu
//
function cmItemMouseOverOpenSub(obj, idSub, orient, index)
{
    cmItemMouseOver(obj, idSub, orient, index);
    if (idSub) {
        var subMenu = cmGetObject(idSub);
        cmShowSubMenu(obj, subMenu, orient);
    }
}

//
// action should be taken for mouse moving out of the menu item
//
function cmItemMouseOut(obj, delayTime)
{
    _cmTimeOut = window.setTimeout('cmHideMenuTime()', delayTime);
    window.defaultStatus = '';
}

//
// action should be taken for mouse button down at a menu item
//
function cmItemMouseDown(obj, index)
{
    var item = cmGetMenuItem(obj, index);
    obj.className += ' Active';
}

//
// action should be taken for mouse button down at a menu item
// this is one also opens submenu if needed
//
function cmItemMouseDownOpenSub(obj, index, orient, idSub)
{
    cmItemMouseDown(obj, index);
    if (idSub) {
        var subMenu = cmGetObject(idSub);
        cmShowSubMenu(obj, subMenu, orient);
    }
}

//
// action should be taken for mouse button up at a menu item
//
function cmItemMouseUp(obj, index)
{
    var item = cmGetMenuItem(obj, index);

    if (!item.disabled) {
        if (!item.subMenu) {
            // click on a menu item, hide menu and perform menu item action
            obj.className = obj.className.replace(/ Active/g, '');

            var thisMenu = cmGetThisMenu(obj);
            cmHideMenu(thisMenu, null);
            _cmCurrentItem = null;
            _cmMenuIsOpen = false;

            if (item.source && item.source.onclick) {
                if (item.source.onclick() == false)
                    return;
            }
            if (item.href) {
                var target = item.target ? item.target : '_self';
                window.open(item.href, target);
            }
        } else {
            // click on a sub menu, no action performed
            obj.className += ' Hover';
        }
    }
}

//////////////////////////////////////////////////////////////////////
//
// Mouse Event Support Utility Functions
//
//////////////////////////////////////////////////////////////////////

//
// move submenu to the appropriate location
//
// @param	obj	the menu item that opens up the subMenu
//		subMenu	the sub menu to be shown
//		orient	the orientation of the subMenu
//
function cmMoveSubMenu(obj, subMenu, orient)
{
    var mode = String(orient);
    var p = subMenu.offsetParent;
    var subMenuWidth = cmGetWidth(subMenu);
    var horiz = cmGetHorizontalAlign(obj, mode, p, subMenuWidth);
    if (mode.charAt (0) == 'h') {
        if (mode.charAt(1) == 'b')
            subMenu.style.top = (cmGetYAt(obj, p) + cmGetHeight(obj)) + 'px';
        else
            subMenu.style.top = (cmGetYAt(obj, p) - cmGetHeight(subMenu)) + 'px';
        if (horiz == 'r')
            subMenu.style.left = cmGetXAt(obj, p) + 'px';
        else
            subMenu.style.left = (cmGetXAt(obj, p) + cmGetWidth(obj) - subMenuWidth) + 'px';
    } else {
        if (horiz == 'r')
            subMenu.style.left = (cmGetXAt(obj, p) + cmGetWidth(obj)) + 'px';
        else
            subMenu.style.left = (cmGetXAt(obj, p) - subMenuWidth) + 'px';
        if (mode.charAt(1) == 'b')
            subMenu.style.top = (cmGetYAt(obj, p)) + 'px';
        else
            subMenu.style.top = (cmGetYAt(obj, p) + cmGetHeight(obj) - cmGetHeight(subMenu)) + 'px';
    }
}

//
// automatically re-adjust the menu position based on available screen size.
//
function cmGetHorizontalAlign(obj, mode, p, subMenuWidth)
{
    var horiz = mode.charAt(2);
    if (!(document.body))
        return horiz;
    var body = document.body;
    var browserLeft;
    var browserRight;
    if (window.innerWidth) {
        // DOM window attributes
        browserLeft = window.pageXOffset;
        browserRight = window.innerWidth + browserLeft;
    } else if (body.clientWidth) {
        // IE attributes
        browserLeft = body.clientLeft;
        browserRight = body.clientWidth + browserLeft;
    } else {
        return horiz;
    }
    if (mode.charAt(0) == 'h') {
        if (horiz == 'r' && (cmGetXAt(obj) + subMenuWidth) > browserRight)
            horiz = 'l';
        if (horiz == 'l' && (cmGetXAt(obj) + cmGetWidth(obj) - subMenuWidth) < browserLeft)
            horiz = 'r';
        return horiz;
    } else {
        if (horiz == 'r' && (cmGetXAt(obj, p) + cmGetWidth(obj) + subMenuWidth) > browserRight)
            horiz = 'l';
        if (horiz == 'l' && (cmGetXAt(obj, p) - subMenuWidth) < browserLeft)
            horiz = 'r';
        return horiz;
    }
}

//
// show the subMenu w/ specified orientation
// also move it to the correct coordinates
//
// @param	obj	the menu item that opens up the subMenu
//		subMenu	the sub menu to be shown
//		orient	the orientation of the subMenu
//
function cmShowSubMenu(obj, subMenu, orient)
{
    if (!subMenu.cmParentMenu) {
        // establish the tree w/ back edge
        var thisMenu = cmGetThisMenu(obj);
        subMenu.cmParentMenu = thisMenu;
        if (!thisMenu.cmSubMenu)
            thisMenu.cmSubMenu = new Array();
        thisMenu.cmSubMenu[thisMenu.cmSubMenu.length] = subMenu;
    }

    // position the sub menu
    cmMoveSubMenu(obj, subMenu, orient);
    subMenu.style.visibility = 'visible';
    _cmMenuIsOpen = true;

    //
    // On IE, controls such as SELECT, OBJECT, IFRAME (before 5.5)
    // are window based controls.  So, if the sub menu and these
    // controls overlap, sub menu would be hidden behind them.  Thus
    // one needs to turn the visibility of these controls off when the
    // sub menu is showing, and turn their visibility back on
    // when the sub menu is hiding.
    //
    if (document.all) { // it is IE
        /* part of Felix Zaslavskiy's fix on hiding controls
                    not really sure if this part is necessary, but shouldn't
                    hurt. */
        if (!subMenu.cmOverlap)
            subMenu.cmOverlap = new Array();
        /*@cc_on @*/
        /*@if (@_jscript_version >= 5.5)
       @else @*/
        cmHideControl("IFRAME", subMenu);
        /*@end @*/
        cmHideControl("SELECT", subMenu);
        cmHideControl("OBJECT", subMenu);
    }
}

//
// reset all the menu items to class MenuItem in thisMenu
//
function cmResetMenu(thisMenu)
{
    if (thisMenu.cmItems) {
        var items = thisMenu.cmItems;
        for (var i = 0; i < items.length; ++i) {
            var str = items[i].className;
            str = str.replace(/ Hover/g, '').replace(/ Active/g, '');
            if (items[i].className != str)
                items[i].className = str;
        }
    }
}

//
// called by the timer to hide the menu
//
function cmHideMenuTime()
{
    if (_cmCurrentItem) {
        cmHideMenu(cmGetThisMenu(_cmCurrentItem), null);
        _cmCurrentItem = null;
        _cmMenuIsOpen = false;
    }
}

//
// hide thisMenu, children of thisMenu, as well as the ancestor
// of thisMenu until currentMenu is encountered.  currentMenu
// will not be hidden
//
function cmHideMenu(thisMenu, currentMenu)
{
    // hide the down stream menus
    if (thisMenu && thisMenu.cmSubMenu) {
        for (var i = 0; i < thisMenu.cmSubMenu.length; ++i) {
            cmHideSubMenu(thisMenu.cmSubMenu[i]);
        }
    }

    // hide the upstream menus
    while (thisMenu && thisMenu != currentMenu) {
        cmResetMenu(thisMenu);
        if (thisMenu.className == 'SubMenu') {
            thisMenu.style.visibility = 'hidden';
            cmShowControl(thisMenu);
        } else {
            break;
        }
        thisMenu = cmGetThisMenu(thisMenu.cmParentMenu);
    }
}

//
// hide thisMenu as well as its sub menus if thisMenu is not
// already hidden
//
function cmHideSubMenu(thisMenu)
{
    if (thisMenu.style.visibility == 'hidden')
        return;
    if (thisMenu.cmSubMenu) {
        var i;
        for (i = 0; i < thisMenu.cmSubMenu.length; ++i) {
            cmHideSubMenu(thisMenu.cmSubMenu[i]);
        }
    }
    cmResetMenu(thisMenu);
    thisMenu.style.visibility = 'hidden';
    cmShowControl(thisMenu);
}

//
// hide a control such as IFRAME
//
function cmHideControl(tagName, subMenu)
{
    var x = cmGetX(subMenu);
    var y = cmGetY(subMenu);
    var w = subMenu.offsetWidth;
    var h = subMenu.offsetHeight;

    var i;
    for (i = 0; i < document.all.tags(tagName).length; ++i) {
        var obj = document.all.tags(tagName)[i];
        if (!obj || !obj.offsetParent)
            continue;

        // check if the object and the subMenu overlap

        var ox = cmGetX(obj);
        var oy = cmGetY(obj);
        var ow = obj.offsetWidth;
        var oh = obj.offsetHeight;

        if (ox > (x + w) || (ox + ow) < x)
            continue;
        if (oy > (y + h) || (oy + oh) < y)
            continue;

        // if object is already made hidden by a different
        // submenu then we dont want to put it on overlap list of
        // of a submenu a second time.
        // - bug fixed by Felix Zaslavskiy
        if(obj.style.visibility == "hidden")
            continue;

        subMenu.cmOverlap[subMenu.cmOverlap.length] = obj;
        obj.style.visibility = "hidden";
    }
}

//
// show the control hidden by the subMenu
//
function cmShowControl(subMenu)
{
    if (subMenu.cmOverlap) {
        var i;
        for (i = 0; i < subMenu.cmOverlap.length; ++i)
            subMenu.cmOverlap[i].style.visibility = "";
    }
    subMenu.cmOverlap = null;
}

//
// returns the main menu or the submenu table where this obj (menu item)
// is in
//
function cmGetThisMenu(obj)
{
    while (obj) {
        if (obj.className == 'SubMenu' || obj.className == 'Menu')
            return obj;
        obj = obj.parentNode;
    }
    return null;
}

//
// returns the node properties from current node of menu tree
//
function cmGetNodeProperties(obj)
{
    while (obj) {
        if (obj.cmNodeProperties)
            return obj.cmNodeProperties;
        obj = obj.parentNode;
    }
    return null;
}

//
// return the menu item list by index
//
function cmGetMenuItem(obj, index)
{
    var nodeProperties = cmGetNodeProperties(obj);
    if (nodeProperties)
        return nodeProperties.itemList[index];
    return null;
}

//
// returns the object baring the id
//
function cmGetObject(id)
{
    if (document.all)
        return document.all[id];
    return document.getElementById(id);
}

//
// functions that obtain the width of an HTML element.
//
function cmGetWidth(obj)
{
    var width = obj.offsetWidth;
    if (width > 0 || !cmIsTRNode(obj))
        return width;
    if (!obj.firstChild)
        return 0;
    // use TABLE's length can cause an extra pixel gap
    //return obj.parentNode.parentNode.offsetWidth;

    // use the left and right child instead
    return obj.lastChild.offsetLeft - obj.firstChild.offsetLeft + cmGetWidth(obj.lastChild);
}

//
// functions that obtain the height of an HTML element.
//
function cmGetHeight(obj)
{
    var height = obj.offsetHeight;
    if (height > 0 || !cmIsTRNode(obj))
        return height;
    if (!obj.firstChild)
        return 0;
    // use the first child's height
    return obj.firstChild.offsetHeight;
}

//
// functions that obtain the coordinates of an HTML element
//
function cmGetX(obj)
{
    var x = 0;
    do {
        x += obj.offsetLeft;
        obj = obj.offsetParent;
    } while (obj);
    return x;
}

function cmGetXAt(obj, elm)
{
    var x = 0;
    while (obj && obj != elm) {
        x += obj.offsetLeft;
        obj = obj.offsetParent;
    }
    if (obj == elm)
        return x;
    return x - cmGetX(elm);
}

function cmGetY(obj)
{
    var y = 0;
    do {
        y += obj.offsetTop;
        obj = obj.offsetParent;
    } while (obj);
    return y;
}

function cmIsTRNode(obj)
{
    var tagName = obj.tagName;
    return tagName == "TR" || tagName == "tr" || tagName == "Tr" || tagName == "tR";
}

//
// get the Y position of the object.  In case of TR element though,
// we attempt to adjust the value.
//
function cmGetYAt(obj, elm)
{
    var y = 0;
    if (!obj.offsetHeight && cmIsTRNode(obj)) {
        var firstTR = obj.parentNode.firstChild;
        obj = obj.firstChild;
        y -= firstTR.firstChild.offsetTop;
    }

    while (obj && obj != elm) {
        y += obj.offsetTop;
        obj = obj.offsetParent;
    }

    if (obj == elm)
        return y;
    return y - cmGetY(elm);
}

//
// extract description from the menu item and set the status text
// @param	item	the menu item
//
function cmSetStatus(item)
{
    var descript = '';
    if (item.title)
        descript = item.title;
    else if (item.href)
        descript = item.href;
    window.defaultStatus = descript;
}
