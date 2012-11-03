/*
 * $Id: CalcMenu.js,v 1.3 2007/12/29 03:05:37 yangdong Exp $
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

Ext.namespace("Ext.om.menu");

/**
 * @class Ext.om.menu.CalcMenu
 * @extends Ext.menu.Menu
 * A menu containing a {@link Ext.menu.CalcItem} component (which provides a date calculator).
 * @constructor
 * Creates a new CalcMenu
 * @param {Object} config Configuration options
 */
Ext.om.menu.CalcMenu = function(config){
    Ext.om.menu.CalcMenu.superclass.constructor.call(this, config);
    this.plain = true;
    var ci = new Ext.om.menu.CalcItem(config);
    this.calcItem = ci;
    this.add(ci);
    /**
     * The {@link Ext.om.Calculator} instance for this CalcMenu
     * @type Calculator
     */
    this.calculator = ci.calculator;
    /**
     * @event fillBack
     * @param {Calculator} calculator
     * @param {String} numValue
     */
    this.relayEvents(ci, ["fillBack"]);
};

Ext.extend(Ext.om.menu.CalcMenu, Ext.menu.Menu, {
    setActiveItem : function(item, autoExpand){
        // Don't activate CalcItem to avoid CalcMenu hide when Carriage Return(13) is pressed.
    }
});

/**
 * @class Ext.om.menu.CalcItem
 * @extends Ext.menu.Adapter
 * A menu item that wraps the {@link Ext.DatPicker} component.
 * @constructor
 * Creates a new CalcItem
 * @param {Object} config Configuration options
 */
Ext.om.menu.CalcItem = function(config){
    Ext.om.menu.CalcItem.superclass.constructor.call(this, new Ext.om.Calculator(config), config);
    /** The Ext.om.Calculator object @type Ext.om.Calculator */
    this.calculator = this.component;
    this.addEvents({select: true});

    this.calculator.on("render", function(calculator){
        calculator.getEl().swallowEvent("click");
        calculator.container.addClass("x-menu-calc-item");
    });

    this.calculator.on("fillBack", this.onFillBack, this);
};

Ext.extend(Ext.om.menu.CalcItem, Ext.menu.Adapter, {
    // private
    onFillBack : function(calculator, result){
        this.fireEvent("fillBack", this, result, calculator);
        Ext.om.menu.CalcItem.superclass.handleClick.call(this);
    }
});
