/*
 * $Id: CalcNumberField.js,v 1.3 2007/12/29 03:05:37 yangdong Exp $
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

Ext.namespace("Ext.om.form");

/**
 * @class Ext.om.form.CalcNumberField
 * @extends Ext.form.TriggerField
 * Provides a date input field with a {@link Ext.om.Calculator} dropdown and automatic date validation.
 * @constructor
 * Create a new CalcNumberField
 * @param {Object} config
 */
Ext.om.form.CalcNumberField = function(config){
    Ext.om.form.CalcNumberField.superclass.constructor.call(this, config);
};

Ext.extend(Ext.om.form.CalcNumberField, Ext.form.TriggerField,  {

    /**
     * @cfg {String} triggerClass
     * An additional CSS class used to style the trigger button.  The trigger will always get the
     * class 'x-form-trigger' and triggerClass will be <b>appended</b> if specified (defaults to 'x-form-calc-trigger'
     * which displays a calculator icon).
     */
    triggerClass : 'x-form-calc-trigger',
    /**
     * @cfg {String/Object} autoCreate
     * A DomHelper element spec, or true for a default element spec (defaults to
     * {tag: "input", type: "text", size: "10", autocomplete: "off"})
     */

    // private
    defaultAutoCreate : {tag: "input", type: "text", size: "10", autocomplete: "off"},

    // private
    // Provides logic to override the default TriggerField.validateBlur which just returns true
    validateBlur : function(){
        return !this.menu || !this.menu.isVisible();
    },

    // private
    menuListeners : {
        fillBack: function(m, d){
            this.setValue(d);
            if (Ext.isIE) {
                var rng = this.el.dom.createTextRange();
                rng.move("textedit");
                rng.select();
            }
        },

        show : function(){ // retain focus styling
            this.onFocus();
            this.menu.calcItem.calculator.focus();

            // TODO: 我不知道为什么设了焦点后一闪就丢失了，不清楚在其他什么地方有另外的处理。
            // 所以暂时用下面这个比较丑陋的方法，可以确保计算器一出现，立刻就可以使用键盘。
            // 否则要用鼠标移入计算器一下，激活menu的mouseover事件以后才可以。
            var scope = this;
            setTimeout(function(){scope.menu.calcItem.calculator.focus();}, 400); // 为了较好的用户体验，尽快的可以使用键盘
            setTimeout(function(){scope.menu.calcItem.calculator.focus();}, 1000); // 在某些比较慢的情况下，以防万一
        },
        hide : function(){
            this.focus();
            var ml = this.menuListeners;
            this.menu.un("fillBack", ml.fillBack,  this);
            this.menu.un("show", ml.show,  this);
            this.menu.un("hide", ml.hide,  this);
        }
    },

    // private
    // Implements the default empty TriggerField.onTriggerClick function to display the Calculator
    onTriggerClick : function(){
        if(this.disabled){
            return;
        }
        if(this.menu == null){
            this.menu = new Ext.om.menu.CalcMenu();
        }

        this.menu.on(Ext.apply({}, this.menuListeners, {
            scope:this
        }));
        //this.menu.calculator.setValue(this.getValue());
        this.menu.show(this.el, "tl-bl?");
    }
});

(function (){
    var p = Ext.om.form.CalcNumberField.prototype;
    var pn = Ext.form.NumberField.prototype;
    Ext.applyIf(p, pn);
    p.initEvents = function() {
        pn.initEvents.call(this);

        this.keyNav = new Ext.KeyNav(this.el, {
            "down" : function(e){
                this.onTriggerClick();
            },

            scope : this,

            doRelay : function(foo, bar, hname){
                if(hname == 'down'){
                   return Ext.KeyNav.prototype.doRelay.apply(this, arguments);
                }
                return true;
            },
            enable: function(){
                // KeyNav类提供的缺省enable方法的处理好像有点问题，反正在FireFox下对CalcNumberField中按向下方向键不响应。
                // 我实在不知道为什么，所以改成了如下实现，覆盖默认实现，反正在FireFox下也能工作了。
                if(this.disabled){
                    // ie won't do special keys on keypress, no one else will repeat keys with keydown
                    // the EventObject will normalize Safari automatically
                    if(Ext.isSafari){
                        this.el.on("keydown", this.prepareEvent,  this);
                        this.el.on("keypress", this.relay,  this);
                    }else{
                        this.el.on("keydown", this.relay,  this);
                    }
                    this.disabled = false;
                }
            }
        });
    };
    p.validateValue = pn.validateValue;
})();

