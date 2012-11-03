/*
 * $Id: ProgressBar.js,v 1.3 2007/12/29 03:05:37 yangdong Exp $
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
 * @class Ext.om.ProgressBar
 * @extends Ext.util.Observable
 * Progress bar control.
 */
Ext.om.ProgressBar = function(renderTo, config){
    Ext.apply(this, config);
    if (renderTo) {
        this.render(renderTo);
    }
    Ext.om.ProgressBar.superclass.constructor.call(this);
};

Ext.extend(Ext.om.ProgressBar, Ext.util.Observable, {
    /**
     * Read-only. The minimum value of this progress bar.
     * @type Number
     */
    minimum : 0,
    /**
     * Read-only. The maximum value of this progress bar.
     * @type Number
     */
    maximum : 100,
    /**
     * Read-only. The current value of this progress bar.
     * @type Number
     */
    value : 0,

    // private
    render : function(renderTo){
        if (!this.template) {
            if(!Ext.om.ProgressBar.progressTemplate){
                Ext.om.ProgressBar.progressTemplate = new Ext.Template(
                    '<div class="x-progress"><div class="x-progress-thumb"></div></div>');
            }
            this.template = Ext.om.ProgressBar.progressTemplate;
        }
        var pb = this.template.append(renderTo, [], true);
        if (this.cls)
            pb.addClass(this.cls);
        if (this.id)
            pb.dom.id = pb.id = this.id;
        this.el = pb;
        this.repaint();
    },

    /**
     * Return the underlying element
     * @return {Ext.Element} The element
     */
    getEl : function() {
        return this.el;
    },

    /**
     * Destroys this progress bar and removes any listeners.
     */
    destroy: function() {
        this.el.removeAllListeners();
        this.purgeListeners();
        this.el.remove();
    },

    /**
     * Sets the current value of this progress bar.
     * @param {Number} value The current value
     */
    setValue : function(value) {
        if (value != this.value) {
            this.value = value;
            this.repaint();
        }
    },

    /**
     * Gets the current value of this progress bar.
     * @return {Number} The current value
     */
    getValue : function() {
        return this.value;
    },

    /**
     * Sets the state of progress, can be "stopped", "running", "paused", etc.
     * @param {String} The progress state
     */
    setState : function(state) {
        if (this.el) {
            if (state != this.state) {
                this.el.addClass("x-progress-" + state);
                if (this.state) {
                    this.el.removeClass("x-progress-" + this.state);
                }
                this.repaint();
            }
        }
        this.state = state;
    },

    /**
     * Gets the state of progress.
     * @param {String} The progress state
     */
    getState : function() {
        return this.state;
    },

    repaint : function() {
        if (this.el) {
            var value = Number(this.value);
            if (isNaN(value) || value < this.minimum) {
                value = this.minimum;
            } else if (value > this.maximum) {
                value = this.maximum;
            }
            var w = (value - this.minimum) / (this.maximum - this.minimum);
            var thumb = this.el.child(".x-progress-thumb");
            thumb.setWidth(w * this.el.getWidth(true));
            thumb.setHeight(this.el.getHeight(true));
            this.el.repaint();
        }
    }
});
