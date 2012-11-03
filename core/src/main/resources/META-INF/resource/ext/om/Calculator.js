/*
 * $Id: Calculator.js,v 1.3 2007/12/29 03:05:37 yangdong Exp $
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
 * @class Ext.om.Calculator
 * @extends Ext.Component
 * Calculator class.
 * @constructor
 * Create a new Calculator
 * @param {Object} config The config object
 */
Ext.om.Calculator = function(config){
    Ext.om.Calculator.superclass.constructor.call(this, config);

    // 显示屏中显示的值
    this.value = "0.";

    // 计算器的显示屏的id
    this.dispId = "-disp";

    // 计算器的显示屏
    this.display = null;

    // 内存显示ID
    this.memoryId = "-m";

    // 内存显示屏
    this.memory = null;

    // 内存中的存储值
    this.memoryValue = null;

    // 内存值中是否存有值
    this.memoryValueSet = false;

    // 上一个按键
    this.lastKey = null;

    this.status = null;

    this.dot = false;
    this.plus_minus = true;

    // 当前操作数数组
    this.num = [];

    // 保存的第一个操作数, 用于连续按等号（=）的场景
    this.firstNum = null;
    // 保存的第二个操作数, 用于连续按等号（=）的场景
    this.secondNum = null;

    // 当前的操作符: (+,-,*,/)之一
    this.opr = null;
    // 上一个操作符: (+,-,*,/)之一
    this.lastOpr = null;

    this.addEvents({
        /**
         * @event fillBack
         * 回填运算结果时发送
         * @param {Calculator} this
         * @param {String} result 运算结果
         */
        fillBack: true
    });

    if(this.handler){
        this.on("fillBack", this.handler,  this.scope || this);
    }
};

Ext.extend(Ext.om.Calculator, Ext.Component, {
    fillBackText: "Fill Back",

    fillBackTip: "(Spacebar)",


    /**
     * Sets the result value of the calc number field
     * @param {String} value The result to set
     */
    setValue : function(value){
        this.value = value;
    },

    /**
     * Gets the current result value of the calc number field
     * @return {String} The result value
     */
    getValue : function(){
        return this.value;
    },

    // private
    focus : function(){
        if(this.el){
            Ext.get(this.dispId).focus();
        }
    },

    buildKeyTD : function(stypeClass, key, text) {
        if (typeof text == 'undefined') {
            text = key;
        }

        return '<td><span class="' + stypeClass + '" hidefocus="on" tabindex="-1" key="' + key + '">' + text + '</span></td>';
    },

    // private
    onRender : function(container){
        this.dispId = Ext.id() + this.dispId;
        this.memoryId = Ext.id() + this.memoryId;
        var m = [
             '<table cellspacing="0">',
                '<tr><td><input id="' + this.dispId + '" type="text" class="x-form-text" style="width:180px;text-align:right" readonly/></td></tr>',
                '<tr><td colspan="1"><table class="x-calc-inner" cellspacing="0"><tr>'];

        m[m.length] = '<td><span class="x-calc-memory" id="' + this.memoryId + '">&nbsp;</span></td><td colspan=\"5\">';
        m[m.length] = '<table cellpadding="0" cellspacing="0"><tr>';
        m[m.length] = '<td><span class="x-calc-key x-calc-opr" hidefocus="on" qtip="" key="Backspace">Backspace</span></td>';
        m[m.length] = '<td style="width:50px"><span class="x-calc-key x-calc-opr" hidefocus="on" key="CE">CE</span></td>';
        m[m.length] = '<td style="width:50px"><span class="x-calc-key x-calc-opr" hidefocus="on" key="C">C</span></td>';
        m[m.length] = '</tr></table></td></tr>';

        m[m.length] = '<tr>';
        m[m.length] = this.buildKeyTD("x-calc-key x-calc-opr","MC", "MC");
        m[m.length] = this.buildKeyTD("x-calc-key x-calc-num","7");
        m[m.length] = this.buildKeyTD("x-calc-key x-calc-num","8");
        m[m.length] = this.buildKeyTD("x-calc-key x-calc-num","9");
        m[m.length] = this.buildKeyTD("x-calc-key x-calc-opr","/");
        m[m.length] = this.buildKeyTD("x-calc-key x-calc-num","sqrt");
        m[m.length] = "</tr>";

        m[m.length] = '<tr>';
        m[m.length] = this.buildKeyTD("x-calc-key x-calc-opr","MR");
        m[m.length] = this.buildKeyTD("x-calc-key x-calc-num","4");
        m[m.length] = this.buildKeyTD("x-calc-key x-calc-num","5");
        m[m.length] = this.buildKeyTD("x-calc-key x-calc-num","6");
        m[m.length] = this.buildKeyTD("x-calc-key x-calc-opr","*");
        m[m.length] = this.buildKeyTD("x-calc-key x-calc-num","%");
        m[m.length] = "</tr>";

        m[m.length] = '<tr>';
        m[m.length] = this.buildKeyTD("x-calc-key x-calc-opr","MS");
        m[m.length] = this.buildKeyTD("x-calc-key x-calc-num","1");
        m[m.length] = this.buildKeyTD("x-calc-key x-calc-num","2");
        m[m.length] = this.buildKeyTD("x-calc-key x-calc-num","3");
        m[m.length] = this.buildKeyTD("x-calc-key x-calc-opr","-");
        m[m.length] = this.buildKeyTD("x-calc-key x-calc-num","1/x");
        m[m.length] = "</tr>";

        m[m.length] = '<tr>';
        m[m.length] = this.buildKeyTD("x-calc-key x-calc-opr","M+");
        m[m.length] = this.buildKeyTD("x-calc-key x-calc-num","0");
        m[m.length] = this.buildKeyTD("x-calc-key x-calc-num","+/-");
        m[m.length] = this.buildKeyTD("x-calc-key x-calc-num",".");
        m[m.length] = this.buildKeyTD("x-calc-key x-calc-opr","+");
        m[m.length] = this.buildKeyTD("x-calc-key x-calc-opr","=");
        m[m.length] = '</tr>';
        m[m.length] = '</table></td></tr>';

        m[m.length] = '<tr><td class="x-date-bottom" align="center"><span class="fillback"/></td></tr>';
        m[m.length] = '</table>';

        var el = document.createElement("div");
        el.className = "x-date-picker";
        el.innerHTML = m.join("");

        container.dom.appendChild(el);

        this.el = Ext.get(el);

        var map = new Ext.KeyMap(this.el, [
            {
                key: [8, // Backspace
                      10,13, // 回车
                      27, // ESC
                      46, // CE(DEL)
                      48,49,50,51,52,53,54,55,56,57, // 0-9, sqrt(@), %, *, (, ),
                      96,97,98,99,100,101,102,103,104,105, //小键盘0-9
                      67, // C, MC(SHIFT+C)
                      76, // MC(L)
                      77, // MS(M)
                      83, // MS(S)
                      80, // M+(P)
                      82, // 1/x(r), MR(shift+r)
                      106, // *
                      107, // +
                      109,/*firefox*/, 189,/*IE*/ // -
                      111, 191, // /
                      61,/*firefox*/, 187,/*IE*/ // +,=
                      110, 188, 190, // . 和 ,
                      120 // +/-(F9)
                      ],
                stopEvent: true,
                fn: function(k, e){
                    if (k == 8) { // Backspace
                        e.stopEvent();
                    }
                    this.handleCalcKeyDown(k, e)
                },
                scope: this
            }
        ]);

        this.el.on("click", this.handleCalcClick, this, {delegate: "span.x-calc-key"});
        this.el.on("mouseover", this.handleCalcMouseover, this, {delegate: "span.x-calc-key"});
        this.el.on("mouseout", this.handleCalcMouseout, this, {delegate: "span.x-calc-key"});

        this.el.addKeyListener(Ext.EventObject.SPACE, this.fillBack,  this);

        this.el.unselectable();

        this.cells = this.el.select("table.x-calc-inner .x-calc-key");
        this.cells.each(function(c){
            var t = c.dom.tagName;

            if ("SPAN" == t) {
                var keyAttr = c.dom.attributes.getNamedItem('key');
                if (!keyAttr) {
                    return;
                }
                var keyValue = keyAttr.nodeValue;

                switch (keyValue) {
                    case "0": c.dom.keyValue = CalcKey.KEY_0; break;
                    case "1": c.dom.keyValue = CalcKey.KEY_1; break;
                    case "2": c.dom.keyValue = CalcKey.KEY_2; break;
                    case "3": c.dom.keyValue = CalcKey.KEY_3; break;
                    case "4": c.dom.keyValue = CalcKey.KEY_4; break;
                    case "5": c.dom.keyValue = CalcKey.KEY_5; break;
                    case "6": c.dom.keyValue = CalcKey.KEY_6; break;
                    case "7": c.dom.keyValue = CalcKey.KEY_7; break;
                    case "8": c.dom.keyValue = CalcKey.KEY_8; break;
                    case "9": c.dom.keyValue = CalcKey.KEY_9; break;
                    case ".": c.dom.keyValue = CalcKey.DOT; break;
                    case "+": c.dom.keyValue = CalcKey.PLUS; break;
                    case "-": c.dom.keyValue = CalcKey.MINUS; break;
                    case "*": c.dom.keyValue = CalcKey.MULTIPLY; break;
                    case "/": c.dom.keyValue = CalcKey.DIVIDE; break;
                    case "=": c.dom.keyValue = CalcKey.EQUAL; break;
                    case "Backspace": c.dom.keyValue = CalcKey.BACKSPACE; break;
                    case "CE": c.dom.keyValue = CalcKey.CE; break;
                    case "C": c.dom.keyValue = CalcKey.C; break;
                    case "1/x": c.dom.keyValue = CalcKey.RECIPROCAL; break;
                    case "%": c.dom.keyValue = CalcKey.PERCENT; break;
                    case "sqrt": c.dom.keyValue = CalcKey.SQRT; break;
                    case "MC": c.dom.keyValue = CalcKey.MC; break;
                    case "MR": c.dom.keyValue = CalcKey.MR; break;
                    case "MS": c.dom.keyValue = CalcKey.MS; break;
                    case "M+": c.dom.keyValue = CalcKey.MPLUS; break;
                    case "+/-": c.dom.keyValue = CalcKey.PLUS_MINUS; break;
                    default: break;
                }
            }
        }, this);
        this.textNodes = this.el.query("table.x-date-inner tbody span");
        this.memory = Ext.get(this.memoryId);
        this.display = Ext.get(this.dispId);
        this.display.dom.value = "0.";
	
        var fillBackBtn = new Ext.Button({
        	applyTo: this.el.child("span.fillback", true),
            text: this.fillBackText,
            tooltip: this.fillBackTip,
            handler: this.fillBack,
            scope: this
        });
        
        if(Ext.isIE){
            //this.el.repaint();
        }
    },

    // private
    handleCalcClick : function(e, t){
        e.stopEvent();
        if(t.keyValue){
            this.handleCalcKey(t.keyValue);
        }
    },
    // private
    handleCalcMouseover : function(e, t){
        Ext.get(t).addClass("hover");
        var keyAttr = t.attributes.getNamedItem('key');
        var qtip = null;
        if (keyAttr) {
            var keyValue = keyAttr.nodeValue;
            switch (keyValue) {
                case "CE": qtip = "Del"; break;
                case "C" : qtip = "c"; break;
                case "sqrt": qtip = "@"; break;
                case "1/x": qtip = "r"; break;
                case "+/-": qtip = "F9, Shift+\"-\""; break;
                case "MC" : qtip = 'Shift+"c", L'; break;
                case "MR" : qtip = 'Shitf+"r"'; break;
                case "MS" : qtip = 's, m'; break;
                case "M+" : qtip = 'p'; break;
                default: break;
            }
            if (qtip) {
                t['qtip'] = qtip;
            }
        }
    },
    // private
    handleCalcMouseout : function(e, t){
        Ext.get(t).removeClass("hover");
    },
    // private
    handleCalcKeyDown : function(k, e) {
        var key;
        switch (k) {
            case 8:   key =  CalcKey.BACKSPACE; break;
            case 10:
            case 13:  key = CalcKey.EQUAL; break;
            case 46:  key = CalcKey.CE; break;
            case 67:  key = e.shiftKey ? CalcKey.MC : CalcKey.C; break;
            case 48:
            case 96:  key = CalcKey.KEY_0; break;
            case 49:
            case 97:  key = CalcKey.KEY_1; break;
            case 50:  key = e.shiftKey ? CalcKey.SQRT : CalcKey.KEY_2; break;
            case 98:  key = CalcKey.KEY_2; break;
            case 51:
            case 99:  key = CalcKey.KEY_3; break;
            case 52:
            case 100: key = CalcKey.KEY_4; break;
            case 53:  key = e.shiftKey ? CalcKey.PERCENT : CalcKey.KEY_5; break;
            case 101: key = CalcKey.KEY_5; break;
            case 54:
            case 102: key = CalcKey.KEY_6; break;
            case 55:
            case 103: key = CalcKey.KEY_7; break;
            case 56:  key = e.shiftKey ? CalcKey.MULTIPLY : CalcKey.KEY_8; break;
            case 104: key = CalcKey.KEY_8; break;
            case 57:
            case 105: key = CalcKey.KEY_9; break;
            case 76:  key = CalcKey.MC; break;
            case 77:
            case 83:  key = CalcKey.MS; break;
            case 80:  key = CalcKey.MPLUS; break;
            case 82:  key = e.shiftKey ? CalcKey.MR : CalcKey.RECIPROCAL; break;
            case 106: key = CalcKey.MULTIPLY; break;
            case 107: key = CalcKey.PLUS; break;
            case 110:
            case 188:
            case 190: key = CalcKey.DOT; break;
            case 111:
            case 191: key = CalcKey.DIVIDE; break;
            case 61:
                if (Ext.isIE) {
                    return;
                }
                key = e.shiftKey ? CalcKey.PLUS : CalcKey.EQUAL; break;
            case 187:
                if (!Ext.isIE) {
                    return;
                }
                key = e.shiftKey ? CalcKey.PLUS : CalcKey.EQUAL; break;

            case 109:
                if (Ext.isIE) {
                    return;
                }
                key = e.shiftKey ? CalcKey.PLUS_MINUS : CalcKey.MINUS; break;
            case 189:
                if (!Ext.isIE) {
                    return;
                }
                key = e.shiftKey ? CalcKey.PLUS_MINUS : CalcKey.MINUS; break;

            case 120: key = CalcKey.PLUS_MINUS; break;
            default: return;
        }
        this.handleCalcKey(key);
    },

    // private
    handleCalcKey : function(key) {
        if (this.isNumChar(key)) {
            if (this.lastKey && this.isNumChar(this.lastKey)) { // 如果前面已经输入数字了
                if (key != CalcKey.DOT || !this.dot) {
                    if (key == CalcKey.DOT) {
                        this.dot = true;
                    }
                    var append = true;
                    if (!this.dot && this.num[this.num.length-1].charAt(0) == '0') {
                        append = false;
                    }

                    if (append) {
                        this.num[this.num.length-1] = this.num[this.num.length-1] + this.key2value(key)
                    } else {
                        this.num[this.num.length-1] = String(this.key2value(key));
                    }
                }
            } else { // 从头开始输入数字
                this.dot = false;

                var numChar;
                if (key == CalcKey.DOT) {
                    numChar = '0.';
                    this.dot = true;
                } else {
                    numChar = String(this.key2value(key));
                }
                var i = this.num.length;
                if (this.opr == null) {
                    i = this.num.length - 1;
                }
                if (i < 0) i = 0;
                if (i >= 2) i = 1;

                this.num[i] = numChar;
            }
            //alert("push " + key);
        } else if (this.isOprChar(key)) { // 输入的是 +, -, *, /
            this.lastOpr = this.opr;
            this.opr = key;
            if (this.num.length > 1) {
                this.compute(this.lastOpr, false);
            }
        } else if (key == CalcKey.EQUAL) {
            this.compute(this.opr || this.lastOpr, true);
        } else if (key == CalcKey.C) {
            this.num = [];
            this.opr = null;
            this.lastOpr = null;
            this.lastKey = null;
            this.dot = false;
        } else if (key == CalcKey.PLUS_MINUS) {
            if (this.num.length > 0) {
                this.num[this.num.length-1] = '' + (0 - this.num[this.num.length-1]);
            }
        } else if (key == CalcKey.RECIPROCAL) {
            var numCount = this.num.length;
            if (numCount > 0) {
                if (numCount == 1) { // 只有一个操作数
                    if (this.opr) {
                        this.num[1] = 1/this.num[0];
                    } else {
                        this.num[0] = 1/this.num[0];
                    }
                } else { // 有两个操作数
                    this.num[1] = 1/this.num[1];
                }
            }
        } else if (key == CalcKey.SQRT) {
            var numCount = this.num.length;
            if (numCount > 0) {
                if (numCount == 1) { // 只有一个操作数
                    if (this.opr) {
                        this.num[1] = String(Math.sqrt(this.num[0]));
                    } else {
                        this.num[0] = String(Math.sqrt(this.num[0]));
                    }
                } else { // 有两个操作数
                    this.num[1] = String(Math.sqrt(this.num[1]));
                }
            }
        } else if (key == CalcKey.PERCENT) {
            var numCount = this.num.length;
            if (numCount > 0) {
                if (numCount == 1) { // 只有一个操作数
                    if (this.opr) {
                        this.num[1] = String(this.num[0]*this.num[0]/100);
                    } else {
                        this.num = [];
                    }
                } else { // 有两个操作数
                    this.num[1] = String(this.num[0] * this.num[1]/100);
                }
            }
        } else if (key == CalcKey.CE) {
            if (this.lastKey && !this.isOprChar(this.lastKey)) {
                this.num.pop();
            }
        } else if (key == CalcKey.BACKSPACE) {
            if (this.lastKey && (this.isNumChar(this.lastKey) || this.lastKey == CalcKey.BACKSPACE)) {
                var numCount = this.num.length;
                if (numCount > 0) {
                    var n = String(this.num[numCount-1]);
                    if (n.length > 0 && !(/e\+|\-\d+$/.test(n))/*确保不是科学计数法*/) {
                        var endChar = n.charAt(n.length-1);
                        if (endChar == '.' || !isNaN(Number(endChar))) {
                            n = n.slice(0, -1);
                            if (n.length > 0) {
                                this.num[numCount-1] = n;
                            } else {
                                this.num[numCount-1] = '0';
                            }
                        }
                    }
                }
            }
        } else if (key == CalcKey.MS) {
            if (this.num.length > 0) {
                this.memoryValue = this.num[this.num.length-1];
                this.memoryValueSet = true;
                this.memory.dom.innerHTML = 'M';
            }
        } else if (key == CalcKey.MR) {
            if (this.memoryValueSet) {
                var i = this.num.length;

                if (this.lastKey == CalcKey.PLUS ||
                    this.lastKey == CalcKey.MINUS ||
                    this.lastKey == CalcKey.MULTIPLY ||
                    this.lastKey == CalcKey.DIVIDE) {

                } else {
                    i--;
                }
                if (i < 0) i = 0;
                if (i >= 2) i = 1;

                this.num[i] = this.memoryValue;
            }
        } else if (key == CalcKey.MC) {
            if (this.memoryValueSet) {
                this.memoryValueSet = false;
                this.memory.dom.innerHTML = '&nbsp;';
            }
        } else if (key == CalcKey.MPLUS) {
            if (this.memoryValueSet) {
                if (this.num.length > 0) {
                    this.memoryValue = this.doCompute(CalcKey.PLUS, this.memoryValue, this.num[this.num.length-1]);
                }
            } else {
                this.memoryValueSet = true;
                if (this.num.length > 0) {
                    this.memoryValue = this.num[this.num.length-1];
                }
                this.memory.dom.innerHTML = 'M';
            }
        }

        var dispValue;

        dispValue = new String(this.num.length > 0 ? this.num[this.num.length-1] : '0');
        if (dispValue.indexOf('.') != -1) {
            this.dot = true;
        } else {
            this.dot = false;
        }
        if (!this.dot) {
            dispValue += '.';
        }


        this.display.dom.value = dispValue;

        this.lastKey = key;
    },

    // private
    key2value : function(key) {
        if (key == CalcKey.KEY_0) {
            return 0;
        } else if (key == CalcKey.DOT) {
            return ".";
        } else if (key == CalcKey.PLUS) {
            return "+";
        } else if (key == CalcKey.MINUS) {
            return "-";
        } else if (key == CalcKey.MULTIPLY) {
            return "*";
        } else if (key == CalcKey.DIVIDE) {
            return "/";
        } else {
            return key;
        }
    },

    // private
    isNumChar : function(key) {
        if (key >= CalcKey.KEY_1 && key <= CalcKey.KEY_0 || key == CalcKey.DOT) {
            return true;
        } else {
            return false;
        }
    },

    // private
    isOprChar : function(key) {
        if (key >= CalcKey.PLUS && key <= CalcKey.DIVIDE) {
            return true;
        } else {
            return false;
        }
    },

    /**
     * 计算 +,-,*,/ 算式
     * @param {Integer} oprKey CalculatorKeyEnum枚举类型的值PLUS, MINUS, MULTIPLY, DIVIDE
     * @param {Boolean} clearOpr 是否清除this.opr,如果是按等号导致的compute则应清除，
     *                              如果是按某一个运算符(+,-,*,/)导致的compute，则不应清除
     * @private
     */
    compute : function(oprKey, clearOpr) {
        var numCount = this.num.length;
        if (oprKey && numCount > 0) {
            if (this.lastKey != CalcKey.EQUAL) {
                if (numCount > 1) {
                    this.firstNum = this.num[0];
                    this.secondNum = this.num[1];
                } else { // 只有一个操作数
                    if (oprKey == this.lastKey) {
                        this.firstNum = this.secondNum = this.num[0];
                    } else {
                        this.firstNum = '0';
                        this.secondNum = this.num[0];
                    }
                }
            } else { // 连续按等号的情况
                this.firstNum = this.num[0];
                this.opr = this.lastOpr;
            }
            var num1 = this.firstNum.toString();
            var num2 = this.secondNum.toString();

            var result = this.doCompute(oprKey, num1, num2);
            this.num = [];
            this.num[0] = result;
            if (this.opr) {
                this.lastOpr = this.opr;
                if (clearOpr) {
                    this.opr = null; // 把this.opr清空，以指示再输入数字的话，算式从头开始。
                }
            }
        }
    },

    /**
     *
     * @param {Object} oprKey CalculatorKeyEnum枚举类型的值PLUS, MINUS, MULTIPLY, DIVIDE
     * @param num1 第一个操作数
     * @param num2 第二个操作数
     * @return 算式的值, 以String类型返回
     * @private
     */
    doCompute : function(oprKey, num1, num2) {
        var finalDot = this.getFinalDecimalDigits(oprKey, num1, num2);
        if (oprKey == CalcKey.DIVIDE) {
            var dot1 = this.getDecimalDigits(num1);
            var dot2 = this.getDecimalDigits(num2);
            num1 = num1.replace(/\./,"");
            num2 = num2.replace(/\./,"");
            if (dot1 > dot2) {
                for (var i = 0; i < dot1 - dot2; i++) {
                    num2 += "0";
                }
            } else {
                for (var i = 0; i < dot2 - dot1; i++) {
                    num1 += "0";
                }
            }
            num1 = this.trimLeadingZero(num1);
            num2 = this.trimLeadingZero(num2);
        }
        var result = eval(num1 + this.key2value(oprKey) + num2);
        var sResult = result.toString();
        if (oprKey != CalcKey.DIVIDE) {
            sResult = result.toFixed(finalDot);
        }
        if (sResult.indexOf('.') != -1) {
            sResult = sResult.replace(/0+$/, "");
            if (sResult.charAt(sResult.length-1) == '.') {
                sResult = sResult.slice(0, sResult.length-1);
            }
        }
        return sResult;
    },

    /**
     * 去掉前导零，比如：00056700=56700, 000=0, 5600=5600
     * @param {String} num 一个数字，可能有前导零
     * @private
     */
    trimLeadingZero : function(num) {
        if (/^0+$/.test(num)) {
            return '0';
        }
        return num.replace(/^0+/g,"");
    },

    /**
     * 给定一个数字，返回它的小数位数
     * @param {Object} num 任意一个数，整型或浮点型
     * @private
     */
    getDecimalDigits : function(num) {
        var dot = 0;
        var sNum = num.toString();
        if (sNum.indexOf('.') != -1) {
            sNum = sNum.replace(/0+$/,"");
            dot = sNum.length - sNum.indexOf('.') - 1;
        }
        return dot;
    },

    /**
     * 根据运算的种类，返回最终结果的小数位数,
     * 对于除法，始终返回 0,因为除法最终结果的小数位数不能确定，要特殊处理。
     *
     * @param {Integer} oprKey 运算符 （+, -, *, /, 都是CalculatorKeyEnum枚举类型的值PLUS, MINUS, MULTIPLY, DIVIDE）
     * @param num1 第一个操作数
     * @param num2 第二个操作数
     * @private
     */
    getFinalDecimalDigits : function(oprKey, num1, num2) {
        var dot1 = this.getDecimalDigits(num1);
        var dot2 = this.getDecimalDigits(num2);
        var finalDot = 0;
        if (oprKey == CalcKey.MULTIPLY) {
            finalDot = dot1 + dot2;
        } else if (oprKey == CalcKey.DIVIDE){
            finalDot = 0;
        } else {
            finalDot = Math.max(dot1, dot2);
        }
        return finalDot;
    },

    // private
    fillBack : function(){
        if (this.num.length > 0) {
            this.setValue(this.num[0]);
        } else {
            this.setValue('0');
        }
        this.fireEvent("fillBack", this, this.value);
    }
});

Ext.om.Calculator.KEY = {
    // 变量的值的大小顺序不要随便变化，因为程序中的逻辑要依赖变量的值的范围。比如（1...10, +.../）
    KEY_1 : 1,
    KEY_2 : 2,
    KEY_3 : 3,
    KEY_4 : 4,
    KEY_5 : 5,
    KEY_6 : 6,
    KEY_7 : 7,
    KEY_8 : 8,
    KEY_9 : 9,
    KEY_0 : 10,

    PLUS : 11,
    MINUS : 12,
    MULTIPLY : 13,
    DIVIDE : 14,

    EQUAL : 15,
    BACKSPACE : 16,
    CE : 17,
    C : 18,
    ESC : 18,
    RECIPROCAL : 19,
    PERCENT : 20,
    SQRT : 21,
    MC : 22,
    MR : 23,
    MS : 24,
    MPLUS : 25,
    PLUS_MINUS : 26,
    DOT : 27,
    ENTER : 28
};

CalcKey = Ext.om.Calculator.KEY;
