/*
 * $Id: Slider.js,v 1.3 2007/12/29 03:05:37 yangdong Exp $
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

Ext.namespace("Ext.om");

/**
 * @class Ext.om.Slider
 * @extends Ext.util.Observable
 * Slider control.
 */
Ext.om.Slider = function(renderTo, config) {
    if (config) Ext.apply(this, config);

    this.orientation = this.orientation || "horizontal";

    this.proxyEl = Ext.getDom(this.proxy);
    if (this.value == null && this.proxyEl)
        this.value = this.proxyEl.value;
    this.value = this.normalize(this.value);
    if (this.proxyEl)
        this.proxyEl.value = this.value;

    if (this.link) {
        this.setLinkValue = this.initLink(this.link);
        if (this.setLinkValue) {
            this.setLinkValue(this.value);
        }
    }

    if (renderTo) {
        this.render(renderTo);
    }

    this.events = {
        "change" : true
    };

    this.el.on("mousedown", this.onMouseDown, this);
    this.el.on("click", this.onClick, this);
    this.el.on("keydown", this.onKeyDown, this);
    this.el.on("focus", this.onFocus, this);
    this.el.on("blur", this.onBlur, this);
    this.thumb.addClassOnOver("x-slider-hover");

    if (this.hidden)
        this.hide();
    if (this.disabled)
        this.disable();

    Ext.om.Slider.superclass.constructor.call(this);
};

Ext.extend(Ext.om.Slider, Ext.util.Observable, {
    // Properties
    orientation : "horizontal",
    width : 200,
    unitIncrement : 1,
    blockIncrement : 10,
    minimum : 0,
    maximum : 100,
    hidden : false,
    disabled : false,

    render : function(renderTo) {
        if (!this.template) {
            if (!Ext.om.Slider.sliderTemplate) {
                Ext.om.Slider.sliderTemplate = new Ext.Template(
                    '<div class="x-slider-wrap"><div class="x-slider-track"></div><div class="x-slider-thumb"></div></div>');
            }
            this.template = Ext.om.Slider.sliderTemplate;
        }

        var el = this.el = this.template.append(renderTo, [], true);
        this.track = el.child(".x-slider-track");
        this.thumb = el.child(".x-slider-thumb");
        if (this.cls) el.addClass(this.cls);
        if (this.id) el.dom.id = el.id = this.id;
        el.addClass("x-slider-" + this.orientation);

        var width = Number(this.width);
        if (isNaN(width) || width <= 0)
            width = 200;
        if (this.orientation == "horizontal") {
            this.el.setWidth(width);
        } else {
            this.el.setHeight(width);
        }

        // enable focus
        if (Ext.isIE) {
            this.el.dom.unselectable = false;
        } else if (Ext.isGecko) {
            this.el.setStyle("-moz-user-focus", "normal");
        }
        if (this.el.dom.tabIndex <= 0) {
            this.el.dom.tabIndex = 1;
        }
        this.track.unselectable();
        this.thumb.unselectable();

        this.repaint();
    },

    repaint : function() {
        var width = this.el.getWidth(true);
        var height = this.el.getHeight(true);
        if (this.orientation == "horizontal") {
            this.track.setTop((height - this.track.getHeight()) / 2);
            this.thumb.setLeft((this.value - this.minimum) / (this.maximum - this.minimum) *
                               (width - this.thumb.getWidth()));
            this.thumb.setTop((height - this.thumb.getHeight()) / 2);
        } else {
            this.track.setLeft((width - this.track.getWidth()) / 2);
            this.thumb.setLeft((width - this.thumb.getWidth()) / 2);
            this.thumb.setTop((height - this.thumb.getHeight()) *
                              (1 - ((this.value - this.minimum) / (this.maximum - this.minimum))));
        }
    },

    initLink : function(link) {
        if (typeof link == 'function') {
            return link;
        }

        if (typeof link == 'object') {
            if (link instanceof Array) {
                var arr = new Array();
                for (var i = 0; i < link.length; i++) {
                    var fn = this.initLink(link[i]);
                    if (fn) arr.push(fn);
                }
                return function(value) {
                    for (var i = 0; i < arr.length; i++) {
                        arr[i](value);
                    }
                }
            } else {
                if (typeof link.setValue == 'function') {
                    return function(value) { link.setValue(value); }
                }
            }
        } else if (typeof link == 'string') {
            // is an Element?
            var el = Ext.getDom(link);
            if (el && el.tagName) {
                switch (el.tagName.toLowerCase()) {
                case 'input':
                    return function(value) { el.value = value; };
                case 'span':
                case 'div':
                    return function(value) { el.innerHTML = value; };
                default:
                    return null;
                }
            }

            // assume a variable name
            return function(value) {
                var obj = window[link];
                if (typeof obj == 'object' && typeof obj.setValue == 'function') {
                    obj.setValue(value);
                }
            };
        }
        return null;
    },

    getEl : function() {
        return this.el;
    },

    destroy : function() {
        this.el.removeAllListeners();
        this.el.purgeListeners();
        this.el.remove();
    },

    normalize : function(value) {
        var n = Number(value);
        if (isNaN(n)) n = 0;
        var m = n % this.unitIncrement;
        n = n - m;
        if (2*m > this.unitIncrement)
            n += this.unitIncrement;
        if (n < this.minimum)
            n = this.minimum;
        else if (n > this.maximum)
            n = this.maximum;
        return n;
    },

    setValue : function(value) {
        value = this.normalize(value);
        if (value != this.value) {
            this.value = value;
            if (this.proxyEl)
                this.proxyEl.value = value;
            if (this.setLinkValue)
                this.setLinkValue(value);
            this.fireEvent("change", this, value);
            this.repaint();
        }
    },

    getValue : function() {
        return this.value;
    },

    setMaximum : function(n) {
        this.maximum = n;
        this.repaint();
    },

    getMaximum : function() {
        return this.maximum;
    },

    setMinimum : function(n) {
        this.minimum = n;
        this.repaint();
    },

    getMinimum : function() {
        return this.minimum;
    },

    show : function() {
        this.hidden = false;
        if (this.el) {
            this.el.setStyle('display', '');
        }
    },

    hide : function() {
        this.hidden = true;
        if (this.el) {
            this.el.setStyle('display', 'none');
        }
    },

    setVisible : function(visible) {
        this[visible ? 'show' : 'hide']();
    },

    disable : function() {
        this.disabled = true;
        if (this.el) {
            this.el.addClass("x-slider-disabled");
        }
    },

    enable : function() {
        this.disabled = false;
        if (this.el) {
            this.el.removeClass("x-slider-disabled");
        }
    },

    setDisabled : function(v) {
        this[v ? "disable" : "enable"]();
    },

    onMouseDown : function(e) {
        if (e.target == this.thumb.dom) {
            this._dragData = { dx: e.getPageX()-this.thumb.dom.offsetLeft, dy: e.getPageY()-this.thumb.dom.offsetTop };
            var doc = Ext.get(document);
            doc.on("mousemove", this.onMouseMove, this);
            doc.on("mouseup", this.onMouseUp, this);
        }
        this.el.focus();
    },

    onMouseMove : function(e) {
        if (this._dragData) {
            var size, pos;
            if (this.orientation == "horizontal") {
                size = this.el.getWidth(true) - this.thumb.getWidth();
                pos = e.getPageX() - this._dragData.dx;
            } else {
                size = this.el.getHeight(true) - this.thumb.getHeight();
                pos = size - (e.getPageY() - this._dragData.dy);
            }
            this.setValue(this.minimum + ((this.maximum - this.minimum)*pos/size));
            e.preventDefault();
        }
    },

    onMouseUp : function(e) {
        if (this._dragData) {
            var doc = Ext.get(document);
            doc.un("mousemove", this.onMouseMove, this);
            doc.un("mouseup", this.onMouseUp, this);
            this._dragData = null;
        }
    },

    onClick : function(e) {
        this.el.focus();
        if (!this._dragData && e.target != this.thumb.dom) {
            var size, pos;
            if (this.orientation == "horizontal") {
                size = this.el.getWidth(true);
                pos = e.getPageX() - this.el.getX();
            } else {
                size = this.el.getHeight(true);
                pos = size - (e.getPageY() - this.el.getY());
            }
            this.setValue(this.minimum + ((this.maximum - this.minimum)*pos/size));
        }
    },

    onKeyDown : function(e) {
        switch (e.getKey()) {
        case e.PAGEUP:
            this.setValue(this.value + this.blockIncrement);
            e.preventDefault();
            break;
        case e.PAGEDOWN:
            this.setValue(this.value - this.blockIncrement);
            e.preventDefault();
            break;
        case e.HOME:
            this.setValue(this.orientation == "horizontal" ? this.minimum : this.maximum);
            e.preventDefault();
            break;
        case e.END:
            this.setValue(this.orientation == "horizontal" ? this.maximum : this.minimum);
            e.preventDefault();
            break;
        case e.UP:
        case e.RIGHT:
            this.setValue(this.value + this.unitIncrement);
            e.preventDefault();
            break;
        case e.LEFT:
        case e.DOWN:
            this.setValue(this.value - this.unitIncrement);
            e.preventDefault();
            break;
        }
    },

    onFocus : function(e) {
        this.thumb.addClass("x-slider-focus");
    },
    onBlur : function(e) {
        this.thumb.removeClass("x-slider-focus");
    }
});
