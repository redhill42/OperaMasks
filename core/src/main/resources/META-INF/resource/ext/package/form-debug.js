/*
 * Ext JS Library 2.0 RC 1
 * Copyright(c) 2006-2007, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */




Ext.form.Field = Ext.extend(Ext.BoxComponent,  {
    
    
    
    
    
    

    
    invalidClass : "x-form-invalid",
    
    invalidText : "The value in this field is invalid",
    
    focusClass : "x-form-focus",
    
    validationEvent : "keyup",
    
    validateOnBlur : true,
    
    validationDelay : 250,
    
    defaultAutoCreate : {tag: "input", type: "text", size: "20", autocomplete: "off"},
    
    fieldClass : "x-form-field",
    
    msgTarget : 'qtip',
    
    msgFx : 'normal',
    
    
    readOnly : false,

    
    disabled : false,

    

    

    // private
    isFormField : true,

    // private
    hasFocus : false,

    
    
    
    

	// private ??
	initComponent : function(){
        Ext.form.Field.superclass.initComponent.call(this);
        this.addEvents(
            
            'focus',
            
            'blur',
            
            'specialkey',
            
            'change',
            
            'invalid',
            
            'valid'
        );
    },

    
    getName: function(){
         return this.rendered && this.el.dom.name ? this.el.dom.name : (this.hiddenName || '');
    },

    // private
    onRender : function(ct, position){
        Ext.form.Field.superclass.onRender.call(this, ct, position);
        if(!this.el){
            var cfg = this.getAutoCreate();
            if(!cfg.name){
                cfg.name = this.name || this.id;
            }
            if(this.inputType){
                cfg.type = this.inputType;
            }
            this.el = ct.createChild(cfg, position);
        }
        var type = this.el.dom.type;
        if(type){
            if(type == 'password'){
                type = 'text';
            }
            this.el.addClass('x-form-'+type);
        }
        if(this.readOnly){
            this.el.dom.readOnly = true;
        }
        if(this.tabIndex !== undefined){
            this.el.dom.setAttribute('tabIndex', this.tabIndex);
        }

        this.el.addClass([this.fieldClass, this.cls]);
        this.initValue();
    },

    // private
    initValue : function(){
        if(this.value !== undefined){
            this.setValue(this.value);
        }else if(this.el.dom.value.length > 0){
            this.setValue(this.el.dom.value);
        }
    },

    
    isDirty : function() {
        if(this.disabled) {
            return false;
        }
        return String(this.getValue()) !== String(this.originalValue);
    },

    // private
    afterRender : function(){
        Ext.form.Field.superclass.afterRender.call(this);
        this.initEvents();
    },

    // private
    fireKey : function(e){
        if(e.isSpecialKey()){
            this.fireEvent("specialkey", this, e);
        }
    },

    
    reset : function(){
        this.setValue(this.originalValue);
        this.clearInvalid();
    },

    // private
    initEvents : function(){
        this.el.on(Ext.isIE ? "keydown" : "keypress", this.fireKey,  this);
        this.el.on("focus", this.onFocus,  this);
        this.el.on("blur", this.onBlur,  this);

        // reference to original value for reset
        this.originalValue = this.getValue();
    },

    // private
    onFocus : function(){
        if(!Ext.isOpera && this.focusClass){ // don't touch in Opera
            this.el.addClass(this.focusClass);
        }
        if(!this.hasFocus){
            this.hasFocus = true;
            this.startValue = this.getValue();
            this.fireEvent("focus", this);
        }
    },

    beforeBlur : Ext.emptyFn,

    // private
    onBlur : function(){
        this.beforeBlur();
        if(!Ext.isOpera && this.focusClass){ // don't touch in Opera
            this.el.removeClass(this.focusClass);
        }
        this.hasFocus = false;
        if(this.validationEvent !== false && this.validateOnBlur && this.validationEvent != "blur"){
            this.validate();
        }
        var v = this.getValue();
        if(String(v) !== String(this.startValue)){
            this.fireEvent('change', this, v, this.startValue);
        }
        this.fireEvent("blur", this);
    },

    
    isValid : function(preventMark){
        if(this.disabled){
            return true;
        }
        var restore = this.preventMark;
        this.preventMark = preventMark === true;
        var v = this.validateValue(this.processValue(this.getRawValue()));
        this.preventMark = restore;
        return v;
    },

    
    validate : function(){
        if(this.disabled || this.validateValue(this.processValue(this.getRawValue()))){
            this.clearInvalid();
            return true;
        }
        return false;
    },

    processValue : function(value){
        return value;
    },

    // private
    // Subclasses should provide the validation implementation by overriding this
    validateValue : function(value){
        return true;
    },

    
    markInvalid : function(msg){
        if(!this.rendered || this.preventMark){ // not rendered
            return;
        }
        this.el.addClass(this.invalidClass);
        msg = msg || this.invalidText;
        switch(this.msgTarget){
            case 'qtip':
                this.el.dom.qtip = msg;
                this.el.dom.qclass = 'x-form-invalid-tip';
                if(Ext.QuickTips){ // fix for floating editors interacting with DND
                    Ext.QuickTips.enable();
                }
                break;
            case 'title':
                this.el.dom.title = msg;
                break;
            case 'under':
                if(!this.errorEl){
                    var elp = this.el.findParent('.x-form-element', 5, true);
                    this.errorEl = elp.createChild({cls:'x-form-invalid-msg'});
                    this.errorEl.setWidth(elp.getWidth(true)-20);
                }
                this.errorEl.update(msg);
                Ext.form.Field.msgFx[this.msgFx].show(this.errorEl, this);
                break;
            case 'side':
                if(!this.errorIcon){
                    var elp = this.el.findParent('.x-form-element', 5, true);
                    this.errorIcon = elp.createChild({cls:'x-form-invalid-icon'});
                }
                this.alignErrorIcon();
                this.errorIcon.dom.qtip = msg;
                this.errorIcon.dom.qclass = 'x-form-invalid-tip';
                this.errorIcon.show();
                this.on('resize', this.alignErrorIcon, this);
                break;
            default:
                var t = Ext.getDom(this.msgTarget);
                t.innerHTML = msg;
                t.style.display = this.msgDisplay;
                break;
        }
        this.fireEvent('invalid', this, msg);
    },

    // private
    alignErrorIcon : function(){
        this.errorIcon.alignTo(this.el, 'tl-tr', [2, 0]);
    },

    
    clearInvalid : function(){
        if(!this.rendered || this.preventMark){ // not rendered
            return;
        }
        this.el.removeClass(this.invalidClass);
        switch(this.msgTarget){
            case 'qtip':
                this.el.dom.qtip = '';
                break;
            case 'title':
                this.el.dom.title = '';
                break;
            case 'under':
                if(this.errorEl){
                    Ext.form.Field.msgFx[this.msgFx].hide(this.errorEl, this);
                }
                break;
            case 'side':
                if(this.errorIcon){
                    this.errorIcon.dom.qtip = '';
                    this.errorIcon.hide();
                    this.un('resize', this.alignErrorIcon, this);
                }
                break;
            default:
                var t = Ext.getDom(this.msgTarget);
                t.innerHTML = '';
                t.style.display = 'none';
                break;
        }
        this.fireEvent('valid', this);
    },

    
    getRawValue : function(){
        var v = this.rendered ? this.el.getValue() : Ext.value(this.value, '');
        if(v === this.emptyText){
            v = '';
        }
        return v;
    },

    
    getValue : function(){
        if(!this.rendered) {
            return this.value;
        }
        var v = this.el.getValue();
        if(v === this.emptyText || v === undefined){
            v = '';
        }
        return v;
    },

    
    setRawValue : function(v){
        return this.el.dom.value = (v === null || v === undefined ? '' : v);
    },

    
    setValue : function(v){
        this.value = v;
        if(this.rendered){
            this.el.dom.value = (v === null || v === undefined ? '' : v);
            this.validate();
        }
    },

    adjustSize : function(w, h){
        var s = Ext.form.Field.superclass.adjustSize.call(this, w, h);
        s.width = this.adjustWidth(this.el.dom.tagName, s.width);
        return s;
    },

    adjustWidth : function(tag, w){
        tag = tag.toLowerCase();
        if(typeof w == 'number' && !Ext.isSafari){
            if(Ext.isIE && (tag == 'input' || tag == 'textarea')){
                if(tag == 'input' && !Ext.isStrict){
                    return w - 3;
                }
                if(tag == 'input' && Ext.isStrict){
                    return w - (Ext.isIE6 ? 4 : 1);
                }
                if(tag = 'textarea' && Ext.isStrict){
                    return w-2;
                }
            }else if(Ext.isOpera && Ext.isStrict){
                if(tag == 'input'){
                    return w + 2;
                }
                if(tag = 'textarea'){
                    return w-2;
                }
            }
        }
        return w;
    }
});


// anything other than normal should be considered experimental
Ext.form.Field.msgFx = {
    normal : {
        show: function(msgEl, f){
            msgEl.setDisplayed('block');
        },

        hide : function(msgEl, f){
            msgEl.setDisplayed(false).update('');
        }
    },

    slide : {
        show: function(msgEl, f){
            msgEl.slideIn('t', {stopFx:true});
        },

        hide : function(msgEl, f){
            msgEl.slideOut('t', {stopFx:true,useDisplay:true});
        }
    },

    slideRight : {
        show: function(msgEl, f){
            msgEl.fixDisplay();
            msgEl.alignTo(f.el, 'tl-tr');
            msgEl.slideIn('l', {stopFx:true});
        },

        hide : function(msgEl, f){
            msgEl.slideOut('l', {stopFx:true,useDisplay:true});
        }
    }
};
Ext.reg('field', Ext.form.Field);




Ext.form.TextField = Ext.extend(Ext.form.Field,  {
    
    
    grow : false,
    
    growMin : 30,
    
    growMax : 800,
    
    vtype : null,
    
    maskRe : null,
    
    disableKeyFilter : false,
    
    allowBlank : true,
    
    minLength : 0,
    
    maxLength : Number.MAX_VALUE,
    
    minLengthText : "The minimum length for this field is {0}",
    
    maxLengthText : "The maximum length for this field is {0}",
    
    selectOnFocus : false,
    
    blankText : "This field is required",
    
    validator : null,
    
    regex : null,
    
    regexText : "",
    
    emptyText : null,
    
    emptyClass : 'x-form-empty-field',

    initComponent : function(){
        Ext.form.TextField.superclass.initComponent.call(this);
        this.addEvents(
            
            'autosize'
        );
    },

    // private
    initEvents : function(){
        Ext.form.TextField.superclass.initEvents.call(this);
        if(this.validationEvent == 'keyup'){
            this.validationTask = new Ext.util.DelayedTask(this.validate, this);
            this.el.on('keyup', this.filterValidation, this);
        }
        else if(this.validationEvent !== false){
            this.el.on(this.validationEvent, this.validate, this, {buffer: this.validationDelay});
        }
        if(this.selectOnFocus || this.emptyText){
            this.on("focus", this.preFocus, this);
            if(this.emptyText){
                this.on('blur', this.postBlur, this);
                this.applyEmptyText();
            }
        }
        if(this.maskRe || (this.vtype && this.disableKeyFilter !== true && (this.maskRe = Ext.form.VTypes[this.vtype+'Mask']))){
            this.el.on("keypress", this.filterKeys, this);
        }
        if(this.grow){
            this.el.on("keyup", this.onKeyUp,  this, {buffer:50});
            this.el.on("click", this.autoSize,  this);
        }
    },

    processValue : function(value){
        if(this.stripCharsRe){
            var newValue = value.replace(this.stripCharsRe, '');
            if(newValue !== value){
                this.setRawValue(newValue);
                return newValue;
            }
        }
        return value;
    },

    filterValidation : function(e){
        if(!e.isNavKeyPress()){
            this.validationTask.delay(this.validationDelay);
        }
    },

    // private
    onKeyUp : function(e){
        if(!e.isNavKeyPress()){
            this.autoSize();
        }
    },

    
    reset : function(){
        Ext.form.TextField.superclass.reset.call(this);
        this.applyEmptyText();
    },

    applyEmptyText : function(){
        if(this.rendered && this.emptyText && this.getRawValue().length < 1){
            this.setRawValue(this.emptyText);
            this.el.addClass(this.emptyClass);
        }
    },

    // private
    preFocus : function(){
        if(this.emptyText){
            if(this.el.dom.value == this.emptyText){
                this.setRawValue('');
            }
            this.el.removeClass(this.emptyClass);
        }
        if(this.selectOnFocus){
            this.el.dom.select();
        }
    },

    // private
    postBlur : function(){
        this.applyEmptyText();
    },

    // private
    filterKeys : function(e){
        var k = e.getKey();
        if(!Ext.isIE && (e.isNavKeyPress() || k == e.BACKSPACE || (k == e.DELETE && e.button == -1))){
            return;
        }
        var c = e.getCharCode(), cc = String.fromCharCode(c);
        if(Ext.isIE && (e.isSpecialKey() || !cc)){
            return;
        }
        if(!this.maskRe.test(cc)){
            e.stopEvent();
        }
    },

    setValue : function(v){
        if(this.emptyText && this.el && v !== undefined && v !== null && v !== ''){
            this.el.removeClass(this.emptyClass);
        }
        Ext.form.TextField.superclass.setValue.apply(this, arguments);
        this.applyEmptyText();
        this.autoSize();
    },

    
    validateValue : function(value){
        if(value.length < 1 || value === this.emptyText){ // if it's blank
             if(this.allowBlank){
                 this.clearInvalid();
                 return true;
             }else{
                 this.markInvalid(this.blankText);
                 return false;
             }
        }
        if(value.length < this.minLength){
            this.markInvalid(String.format(this.minLengthText, this.minLength));
            return false;
        }
        if(value.length > this.maxLength){
            this.markInvalid(String.format(this.maxLengthText, this.maxLength));
            return false;
        }
        if(this.vtype){
            var vt = Ext.form.VTypes;
            if(!vt[this.vtype](value, this)){
                this.markInvalid(this.vtypeText || vt[this.vtype +'Text']);
                return false;
            }
        }
        if(typeof this.validator == "function"){
            var msg = this.validator(value);
            if(msg !== true){
                this.markInvalid(msg);
                return false;
            }
        }
        if(this.regex && !this.regex.test(value)){
            this.markInvalid(this.regexText);
            return false;
        }
        return true;
    },

    
    selectText : function(start, end){
        var v = this.getRawValue();
        if(v.length > 0){
            start = start === undefined ? 0 : start;
            end = end === undefined ? v.length : end;
            var d = this.el.dom;
            if(d.setSelectionRange){
                d.setSelectionRange(start, end);
            }else if(d.createTextRange){
                var range = d.createTextRange();
                range.moveStart("character", start);
                range.moveEnd("character", v.length-end);
                range.select();
            }
        }
    },

    
    autoSize : function(){
        if(!this.grow || !this.rendered){
            return;
        }
        if(!this.metrics){
            this.metrics = Ext.util.TextMetrics.createInstance(this.el);
        }
        var el = this.el;
        var v = el.dom.value;
        var d = document.createElement('div');
        d.appendChild(document.createTextNode(v));
        v = d.innerHTML;
        d = null;
        v += "&#160;";
        var w = Math.min(this.growMax, Math.max(this.metrics.getWidth(v) +  10, this.growMin));
        this.el.setWidth(w);
        this.fireEvent("autosize", this, w);
    }
});
Ext.reg('textfield', Ext.form.TextField);




Ext.form.TriggerField = Ext.extend(Ext.form.TextField,  {
    
    
    defaultAutoCreate : {tag: "input", type: "text", size: "16", autocomplete: "off"},
    
    hideTrigger:false,

    
    autoSize: Ext.emptyFn,
    // private
    monitorTab : true,
    // private
    deferHeight : true,
    // private
    mimicing : false,

    // private
    onResize : function(w, h){
        Ext.form.TriggerField.superclass.onResize.call(this, w, h);
        if(typeof w == 'number'){
            this.el.setWidth(this.adjustWidth('input', w - this.trigger.getWidth()));
        }
        this.wrap.setWidth(this.el.getWidth()+this.trigger.getWidth());
    },

    // private
    adjustSize : Ext.BoxComponent.prototype.adjustSize,

    // private
    getResizeEl : function(){
        return this.wrap;
    },

    // private
    getPositionEl : function(){
        return this.wrap;
    },

    // private
    alignErrorIcon : function(){
        this.errorIcon.alignTo(this.wrap, 'tl-tr', [2, 0]);
    },

    // private
    onRender : function(ct, position){
        Ext.form.TriggerField.superclass.onRender.call(this, ct, position);
        this.wrap = this.el.wrap({cls: "x-form-field-wrap"});
        this.trigger = this.wrap.createChild(this.triggerConfig ||
                {tag: "img", src: Ext.BLANK_IMAGE_URL, cls: "x-form-trigger " + this.triggerClass});
        if(this.hideTrigger){
            this.trigger.setDisplayed(false);
        }
        this.initTrigger();
        if(!this.width){
            this.wrap.setWidth(this.el.getWidth()+this.trigger.getWidth());
        }
    },

    // private
    initTrigger : function(){
        this.trigger.on("click", this.onTriggerClick, this, {preventDefault:true});
        this.trigger.addClassOnOver('x-form-trigger-over');
        this.trigger.addClassOnClick('x-form-trigger-click');
    },

    // private
    onDestroy : function(){
        if(this.trigger){
            this.trigger.removeAllListeners();
            this.trigger.remove();
        }
        if(this.wrap){
            this.wrap.remove();
        }
        Ext.form.TriggerField.superclass.onDestroy.call(this);
    },

    // private
    onFocus : function(){
        Ext.form.TriggerField.superclass.onFocus.call(this);
        if(!this.mimicing){
            this.wrap.addClass('x-trigger-wrap-focus');
            this.mimicing = true;
            Ext.get(Ext.isIE ? document.body : document).on("mousedown", this.mimicBlur, this, {delay: 10});
            if(this.monitorTab){
                this.el.on("keydown", this.checkTab, this);
            }
        }
    },

    // private
    checkTab : function(e){
        if(e.getKey() == e.TAB){
            this.triggerBlur();
        }
    },

    // private
    onBlur : function(){
        // do nothing
    },

    // private
    mimicBlur : function(e){
        if(!this.wrap.contains(e.target) && this.validateBlur(e)){
            this.triggerBlur();
        }
    },

    // private
    triggerBlur : function(){
        this.mimicing = false;
        Ext.get(Ext.isIE ? document.body : document).un("mousedown", this.mimicBlur);
        if(this.monitorTab){
            this.el.un("keydown", this.checkTab, this);
        }
        this.beforeBlur();
        this.wrap.removeClass('x-trigger-wrap-focus');
        Ext.form.TriggerField.superclass.onBlur.call(this);
    },

    beforeBlur : Ext.emptyFn, 

    // private
    // This should be overriden by any subclass that needs to check whether or not the field can be blurred.
    validateBlur : function(e){
        return true;
    },

    // private
    onDisable : function(){
        Ext.form.TriggerField.superclass.onDisable.call(this);
        if(this.wrap){
            this.wrap.addClass('x-item-disabled');
        }
    },

    // private
    onEnable : function(){
        Ext.form.TriggerField.superclass.onEnable.call(this);
        if(this.wrap){
            this.wrap.removeClass('x-item-disabled');
        }
    },


    // private
    onShow : function(){
        if(this.wrap){
            this.wrap.dom.style.display = '';
            this.wrap.dom.style.visibility = 'visible';
        }
    },

    // private
    onHide : function(){
        this.wrap.dom.style.display = 'none';
    },

    
    onTriggerClick : Ext.emptyFn

    
    
    
});

// TwinTriggerField is not a public class to be used directly.  It is meant as an abstract base class
// to be extended by an implementing class.  For an example of implementing this class, see the custom
// SearchField implementation here: http://extjs.com/deploy/ext/examples/form/custom.html
Ext.form.TwinTriggerField = Ext.extend(Ext.form.TriggerField, {
    initComponent : function(){
        Ext.form.TwinTriggerField.superclass.initComponent.call(this);

        this.triggerConfig = {
            tag:'span', cls:'x-form-twin-triggers', cn:[
            {tag: "img", src: Ext.BLANK_IMAGE_URL, cls: "x-form-trigger " + this.trigger1Class},
            {tag: "img", src: Ext.BLANK_IMAGE_URL, cls: "x-form-trigger " + this.trigger2Class}
        ]};
    },

    getTrigger : function(index){
        return this.triggers[index];
    },

    initTrigger : function(){
        var ts = this.trigger.select('.x-form-trigger', true);
        this.wrap.setStyle('overflow', 'hidden');
        var triggerField = this;
        ts.each(function(t, all, index){
            t.hide = function(){
                var w = triggerField.wrap.getWidth();
                this.dom.style.display = 'none';
                triggerField.el.setWidth(w-triggerField.trigger.getWidth());
            };
            t.show = function(){
                var w = triggerField.wrap.getWidth();
                this.dom.style.display = '';
                triggerField.el.setWidth(w-triggerField.trigger.getWidth());
            };
            var triggerIndex = 'Trigger'+(index+1);

            if(this['hide'+triggerIndex]){
                t.dom.style.display = 'none';
            }
            t.on("click", this['on'+triggerIndex+'Click'], this, {preventDefault:true});
            t.addClassOnOver('x-form-trigger-over');
            t.addClassOnClick('x-form-trigger-click');
        }, this);
        this.triggers = ts.elements;
    },

    onTrigger1Click : Ext.emptyFn,
    onTrigger2Click : Ext.emptyFn
});
Ext.reg('trigger', Ext.form.TriggerField);



Ext.form.TextArea = Ext.extend(Ext.form.TextField,  {
    
    growMin : 60,
    
    growMax: 1000,
    growAppend : '&#160;\n&#160;',
    growPad : 0,

    enterIsSpecial : false,

    
    preventScrollbars: false,
    

    // private
    onRender : function(ct, position){
        if(!this.el){
            this.defaultAutoCreate = {
                tag: "textarea",
                style:"width:100px;height:60px;",
                autocomplete: "off"
            };
        }
        Ext.form.TextArea.superclass.onRender.call(this, ct, position);
        if(this.grow){
            this.textSizeEl = Ext.DomHelper.append(document.body, {
                tag: "pre", cls: "x-form-grow-sizer"
            });
            if(this.preventScrollbars){
                this.el.setStyle("overflow", "hidden");
            }
            this.el.setHeight(this.growMin);
        }
    },

    onDestroy : function(){
        if(this.textSizeEl){
            Ext.removeNode(this.textSizeEl);
        }
        Ext.form.TextArea.superclass.onDestroy.call(this);
    },

    fireKey : function(e){
        if(e.isSpecialKey() && (this.enterIsSpecial || (e.getKey() != e.ENTER || e.hasModifier()))){
            this.fireEvent("specialkey", this, e);
        }
    },

    // private
    onKeyUp : function(e){
        if(!e.isNavKeyPress() || e.getKey() == e.ENTER){
            this.autoSize();
        }
    },

    
    autoSize : function(){
        if(!this.grow || !this.textSizeEl){
            return;
        }
        var el = this.el;
        var v = el.dom.value;
        var ts = this.textSizeEl;
        ts.innerHTML = '';
        ts.appendChild(document.createTextNode(v));
        v = ts.innerHTML;

        Ext.fly(ts).setWidth(this.el.getWidth());
        if(v.length < 1){
            v = "&#160;&#160;";
        }else{
            if(Ext.isIE){
                v = v.replace(/\n/g, '<p>&#160;</p>');
            }
            v += this.growAppend;
        }
        ts.innerHTML = v;
        var h = Math.min(this.growMax, Math.max(ts.offsetHeight, this.growMin)+this.growPad);
        if(h != this.lastHeight){
            this.lastHeight = h;
            this.el.setHeight(h);
            this.fireEvent("autosize", this, h);
        }
    }
});
Ext.reg('textarea', Ext.form.TextArea);



Ext.form.NumberField = Ext.extend(Ext.form.TextField,  {
    
    fieldClass: "x-form-field x-form-num-field",
    
    allowDecimals : true,
    
    decimalSeparator : ".",
    
    decimalPrecision : 2,
    
    allowNegative : true,
    
    minValue : Number.NEGATIVE_INFINITY,
    
    maxValue : Number.MAX_VALUE,
    
    minText : "The minimum value for this field is {0}",
    
    maxText : "The maximum value for this field is {0}",
    
    nanText : "{0} is not a valid number",
    
    baseChars : "0123456789",

    // private
    initEvents : function(){
        Ext.form.NumberField.superclass.initEvents.call(this);
        var allowed = this.baseChars+'';
        if(this.allowDecimals){
            allowed += this.decimalSeparator;
        }
        if(this.allowNegative){
            allowed += "-";
        }
        this.stripCharsRe = new RegExp('[^'+allowed+']', 'gi');
        var keyPress = function(e){
            var k = e.getKey();
            if(!Ext.isIE && (e.isSpecialKey() || k == e.BACKSPACE || k == e.DELETE)){
                return;
            }
            var c = e.getCharCode();
            if(allowed.indexOf(String.fromCharCode(c)) === -1){
                e.stopEvent();
            }
        };
        this.el.on("keypress", keyPress, this);
    },

    // private
    validateValue : function(value){
        if(!Ext.form.NumberField.superclass.validateValue.call(this, value)){
            return false;
        }
        if(value.length < 1){ // if it's blank and textfield didn't flag it then it's valid
             return true;
        }
        value = String(value).replace(this.decimalSeparator, ".");
        if(isNaN(value)){
            this.markInvalid(String.format(this.nanText, value));
            return false;
        }
        var num = this.parseValue(value);
        if(num < this.minValue){
            this.markInvalid(String.format(this.minText, this.minValue));
            return false;
        }
        if(num > this.maxValue){
            this.markInvalid(String.format(this.maxText, this.maxValue));
            return false;
        }
        return true;
    },

    getValue : function(){
        return this.fixPrecision(this.parseValue(Ext.form.NumberField.superclass.getValue.call(this)));
    },

    setValue : function(v){
        Ext.form.NumberField.superclass.setValue.call(this, String(parseFloat(v)).replace(".", this.decimalSeparator));
    },

    // private
    parseValue : function(value){
        value = parseFloat(String(value).replace(this.decimalSeparator, "."));
        return isNaN(value) ? '' : value;
    },

    // private
    fixPrecision : function(value){
        var nan = isNaN(value);
        if(!this.allowDecimals || this.decimalPrecision == -1 || nan || !value){
           return nan ? '' : value;
        }
        return parseFloat(parseFloat(value).toFixed(this.decimalPrecision));
    },

    beforeBlur : function(){
        var v = this.parseValue(this.getRawValue());
        if(v){
            this.setValue(this.fixPrecision(v));
        }
    }
});
Ext.reg('numberfield', Ext.form.NumberField);



Ext.form.DateField = Ext.extend(Ext.form.TriggerField,  {
    
    format : "m/d/y",
    
    altFormats : "m/d/Y|m-d-y|m-d-Y|m/d|m-d|md|mdy|mdY|d|Y-m-d",
    
    disabledDays : null,
    
    disabledDaysText : "Disabled",
    
    disabledDates : null,
    
    disabledDatesText : "Disabled",
    
    minValue : null,
    
    maxValue : null,
    
    minText : "The date in this field must be equal to or after {0}",
    
    maxText : "The date in this field must be equal to or before {0}",
    
    invalidText : "{0} is not a valid date - it must be in the format {1}",
    
    triggerClass : 'x-form-date-trigger',
    

    // private
    defaultAutoCreate : {tag: "input", type: "text", size: "10", autocomplete: "off"},

    initComponent : function(){
        Ext.form.DateField.superclass.initComponent.call(this);
        if(typeof this.minValue == "string"){
            this.minValue = this.parseDate(this.minValue);
        }
        if(typeof this.maxValue == "string"){
            this.maxValue = this.parseDate(this.maxValue);
        }
        this.ddMatch = null;
        if(this.disabledDates){
            var dd = this.disabledDates;
            var re = "(?:";
            for(var i = 0; i < dd.length; i++){
                re += dd[i];
                if(i != dd.length-1) re += "|";
            }
            this.ddMatch = new RegExp(re + ")");
        }
    },

    // private
    validateValue : function(value){
        value = this.formatDate(value);
        if(!Ext.form.DateField.superclass.validateValue.call(this, value)){
            return false;
        }
        if(value.length < 1){ // if it's blank and textfield didn't flag it then it's valid
             return true;
        }
        var svalue = value;
        value = this.parseDate(value);
        if(!value){
            this.markInvalid(String.format(this.invalidText, svalue, this.format));
            return false;
        }
        var time = value.getTime();
        if(this.minValue && time < this.minValue.getTime()){
            this.markInvalid(String.format(this.minText, this.formatDate(this.minValue)));
            return false;
        }
        if(this.maxValue && time > this.maxValue.getTime()){
            this.markInvalid(String.format(this.maxText, this.formatDate(this.maxValue)));
            return false;
        }
        if(this.disabledDays){
            var day = value.getDay();
            for(var i = 0; i < this.disabledDays.length; i++) {
            	if(day === this.disabledDays[i]){
            	    this.markInvalid(this.disabledDaysText);
                    return false;
            	}
            }
        }
        var fvalue = this.formatDate(value);
        if(this.ddMatch && this.ddMatch.test(fvalue)){
            this.markInvalid(String.format(this.disabledDatesText, fvalue));
            return false;
        }
        return true;
    },

    // private
    // Provides logic to override the default TriggerField.validateBlur which just returns true
    validateBlur : function(){
        return !this.menu || !this.menu.isVisible();
    },

    
    getValue : function(){
        return this.parseDate(Ext.form.DateField.superclass.getValue.call(this)) || "";
    },

    
    setValue : function(date){
        Ext.form.DateField.superclass.setValue.call(this, this.formatDate(this.parseDate(date)));
    },

    // private
    parseDate : function(value){
        if(!value || value instanceof Date){
            return value;
        }
        var v = Date.parseDate(value, this.format);
        if(!v && this.altFormats){
            if(!this.altFormatsArray){
                this.altFormatsArray = this.altFormats.split("|");
            }
            for(var i = 0, len = this.altFormatsArray.length; i < len && !v; i++){
                v = Date.parseDate(value, this.altFormatsArray[i]);
            }
        }
        return v;
    },

    // private
    onDestroy : function(){
        if(this.wrap){
            this.wrap.remove();
        }
        Ext.form.DateField.superclass.onDestroy.call(this);
    },

    // private
    formatDate : function(date){
        return (!date || !(date instanceof Date)) ?
               date : date.dateFormat(this.format);
    },

    // private
    menuListeners : {
        select: function(m, d){
            this.setValue(d);
        },
        show : function(){ // retain focus styling
            this.onFocus();
        },
        hide : function(){
            this.focus.defer(10, this);
            var ml = this.menuListeners;
            this.menu.un("select", ml.select,  this);
            this.menu.un("show", ml.show,  this);
            this.menu.un("hide", ml.hide,  this);
        }
    },

    // private
    // Implements the default empty TriggerField.onTriggerClick function to display the DatePicker
    onTriggerClick : function(){
        if(this.disabled){
            return;
        }
        if(this.menu == null){
            this.menu = new Ext.menu.DateMenu();
        }
        Ext.apply(this.menu.picker,  {
            minDate : this.minValue,
            maxDate : this.maxValue,
            disabledDatesRE : this.ddMatch,
            disabledDatesText : this.disabledDatesText,
            disabledDays : this.disabledDays,
            disabledDaysText : this.disabledDaysText,
            format : this.format,
            minText : String.format(this.minText, this.formatDate(this.minValue)),
            maxText : String.format(this.maxText, this.formatDate(this.maxValue))
        });
        this.menu.on(Ext.apply({}, this.menuListeners, {
            scope:this
        }));
        this.menu.picker.setValue(this.getValue() || new Date());
        this.menu.show(this.el, "tl-bl?");
    },

    beforeBlur : function(){
        var v = this.parseDate(this.getRawValue());
        if(v){
            this.setValue(v);
        }
    }

    
    
    
    
});
Ext.reg('datefield', Ext.form.DateField);



Ext.form.Checkbox = Ext.extend(Ext.form.Field,  {
    
    focusClass : undefined,
    
    fieldClass: "x-form-field",
    
    checked: false,
    
    defaultAutoCreate : { tag: "input", type: 'checkbox', autocomplete: "off"},
    
    

	// private
    initComponent : function(){
        Ext.form.Checkbox.superclass.initComponent.call(this);
        this.addEvents(
            
            'check'
        );
    },

    // private
    onResize : function(){
        Ext.form.Checkbox.superclass.onResize.apply(this, arguments);
        if(!this.boxLabel){
            this.el.alignTo(this.wrap, 'c-c');
        }
    },
    
    // private
    initEvents : function(){
        Ext.form.Checkbox.superclass.initEvents.call(this);
        this.el.on("click", this.onClick,  this);
        this.el.on("change", this.onClick,  this);
    },

	// private
    getResizeEl : function(){
        return this.wrap;
    },

    // private
    getPositionEl : function(){
        return this.wrap;
    },

    
    markInvalid : Ext.emptyFn,
    
    clearInvalid : Ext.emptyFn,

    // private
    onRender : function(ct, position){
        Ext.form.Checkbox.superclass.onRender.call(this, ct, position);
        if(this.inputValue !== undefined){
            this.el.dom.value = this.inputValue;
        }
        this.wrap = this.el.wrap({cls: "x-form-check-wrap"});
        if(this.boxLabel){
            this.wrap.createChild({tag: 'label', htmlFor: this.el.id, cls: 'x-form-cb-label', html: this.boxLabel});
        }
        if(this.checked){
            this.setValue(true);
        }else{
            this.checked = this.el.dom.checked;
        }
    },
    
    // private
    onDestroy : function(){
        if(this.wrap){
            this.wrap.remove();
        }
        Ext.form.Checkbox.superclass.onDestroy.call(this);
    },

    // private
    initValue : Ext.emptyFn,

    
    getValue : function(){
        if(this.rendered){
            return this.el.dom.checked;
        }
        return false;
    },

	// private
    onClick : function(){
        if(this.el.dom.checked != this.checked){
            this.setValue(this.el.dom.checked);
        }
    },

    
    setValue : function(v){
        this.checked = (v === true || v === 'true' || v == '1' || String(v).toLowerCase() == 'on');
        if(this.el && this.el.dom){
            this.el.dom.checked = this.checked;
            this.el.dom.defaultChecked = this.checked;
        }
        this.fireEvent("check", this, this.checked);
    }
});
Ext.reg('checkbox', Ext.form.Checkbox);



Ext.form.Radio = Ext.extend(Ext.form.Checkbox, {
    inputType: 'radio',

    
    markInvalid : Ext.emptyFn,
    
    clearInvalid : Ext.emptyFn,

    
    getGroupValue : function(){
        return this.el.up('form').child('input[name='+this.el.dom.name+']:checked', true).value;
    }
});
Ext.reg('radio', Ext.form.Radio);



Ext.form.ComboBox = Ext.extend(Ext.form.TriggerField, {
    
    
    
    
    
    
    // private
    defaultAutoCreate : {tag: "input", type: "text", size: "24", autocomplete: "off"},
    
    
    
    
    
    listClass: '',
    
    selectedClass: 'x-combo-selected',
    
    triggerClass : 'x-form-arrow-trigger',
    
    shadow:'sides',
    
    listAlign: 'tl-bl?',
    
    maxHeight: 300,
    
    triggerAction: 'query',
    
    minChars : 4,
    
    typeAhead: false,
    
    queryDelay: 500,
    
    pageSize: 0,
    
    selectOnFocus:false,
    
    queryParam: 'query',
    
    loadingText: 'Loading...',
    
    resizable: false,
    
    handleHeight : 8,
    
    editable: true,
    
    allQuery: '',
    
    mode: 'remote',
    
    minListWidth : 70,
    
    forceSelection:false,
    
    typeAheadDelay : 250,
    
    
    
    lazyInit : true,

    initComponent : function(){
        Ext.form.ComboBox.superclass.initComponent.call(this);
        this.addEvents(
            
            'expand',
            
            'collapse',
            
            'beforeselect',
            
            'select',
            
            'beforequery'
        );
        if(this.transform){
            this.allowDomMove = false;
            var s = Ext.getDom(this.transform);
            if(!this.hiddenName){
                this.hiddenName = s.name;
            }
            if(!this.store){
                this.mode = 'local';
                var d = [], opts = s.options;
                for(var i = 0, len = opts.length;i < len; i++){
                    var o = opts[i];
                    var value = (Ext.isIE ? o.getAttributeNode('value').specified : o.hasAttribute('value')) ? o.value : o.text;
                    if(o.selected) {
                        this.value = value;
                    }
                    d.push([value, o.text]);
                }
                this.store = new Ext.data.SimpleStore({
                    'id': 0,
                    fields: ['value', 'text'],
                    data : d
                });
                this.valueField = 'value';
                this.displayField = 'text';
            }
            s.name = Ext.id(); // wipe out the name in case somewhere else they have a reference
            if(!this.lazyRender){
                this.target = true;
                this.el = Ext.DomHelper.insertBefore(s, this.autoCreate || this.defaultAutoCreate);
                Ext.removeNode(s); // remove it
                this.render(this.el.parentNode);
            }else{
                Ext.removeNode(s); // remove it
            }

        }
        this.selectedIndex = -1;
        if(this.mode == 'local'){
            if(this.initialConfig.queryDelay === undefined){
                this.queryDelay = 10;
            }
            if(this.initialConfig.minChars === undefined){
                this.minChars = 0;
            }
        }
    },

    // private
    onRender : function(ct, position){
        Ext.form.ComboBox.superclass.onRender.call(this, ct, position);
        if(this.hiddenName){
            this.hiddenField = this.el.insertSibling({tag:'input', type:'hidden', name: this.hiddenName, id: (this.hiddenId||this.hiddenName)},
                    'before', true);
            this.hiddenField.value =
                this.hiddenValue !== undefined ? this.hiddenValue :
                this.value !== undefined ? this.value : '';

            // prevent input submission
            this.el.dom.removeAttribute('name');
        }
        if(Ext.isGecko){
            this.el.dom.setAttribute('autocomplete', 'off');
        }

        if(!this.lazyInit){
            this.initList();
        }else{
            this.on('focus', this.initList, this, {single: true});
        }

        if(!this.editable){
            this.editable = true;
            this.setEditable(false);
        }
    },

    initList : function(){
        if(!this.list){
            var cls = 'x-combo-list';

            this.list = new Ext.Layer({
                shadow: this.shadow, cls: [cls, this.listClass].join(' '), constrain:false
            });

            var lw = this.listWidth || Math.max(this.wrap.getWidth(), this.minListWidth);
            this.list.setWidth(lw);
            this.list.swallowEvent('mousewheel');
            this.assetHeight = 0;

            if(this.title){
                this.header = this.list.createChild({cls:cls+'-hd', html: this.title});
                this.assetHeight += this.header.getHeight();
            }

            this.innerList = this.list.createChild({cls:cls+'-inner'});
            this.innerList.on('mouseover', this.onViewOver, this);
            this.innerList.on('mousemove', this.onViewMove, this);
            this.innerList.setWidth(lw - this.list.getFrameWidth('lr'));

            if(this.pageSize){
                this.footer = this.list.createChild({cls:cls+'-ft'});
                this.pageTb = new Ext.PagingToolbar({
                    store:this.store,
                    pageSize: this.pageSize,
                    renderTo:this.footer
                });
                this.assetHeight += this.footer.getHeight();
            }

            if(!this.tpl){
			    
                this.tpl = '<tpl for="."><div class="'+cls+'-item">{' + this.displayField + '}</div></tpl>';
            }

		    
            this.view = new Ext.DataView({
                applyTo: this.innerList,
                tpl: this.tpl,
                singleSelect: true,
                selectedClass: this.selectedClass,
                itemSelector: this.itemSelector || '.' + cls + '-item'
            });

            this.view.on('click', this.onViewClick, this);

            this.bindStore(this.store, true);

            if(this.resizable){
                this.resizer = new Ext.Resizable(this.list,  {
                   pinned:true, handles:'se'
                });
                this.resizer.on('resize', function(r, w, h){
                    this.maxHeight = h-this.handleHeight-this.list.getFrameWidth('tb')-this.assetHeight;
                    this.listWidth = w;
                    this.innerList.setWidth(w - this.list.getFrameWidth('lr'));
                    this.restrictHeight();
                }, this);
                this[this.pageSize?'footer':'innerList'].setStyle('margin-bottom', this.handleHeight+'px');
            }
        }
    },


    // private
    bindStore : function(store, initial){
        if(this.store && !initial){
            this.store.un('beforeload', this.onBeforeLoad, this);
            this.store.un('load', this.onLoad, this);
            this.store.un('loadexception', this.collapse, this);
            if(!store){
                this.store = null;
                if(this.view){
                    this.view.setStore(null);
                }
            }
        }
        if(store){
            this.store = Ext.StoreMgr.lookup(store);

            this.store.on('beforeload', this.onBeforeLoad, this);
            this.store.on('load', this.onLoad, this);
            this.store.on('loadexception', this.collapse, this);

            if(this.view){
                this.view.setStore(store);
            }
        }
    },

    // private
    initEvents : function(){
        Ext.form.ComboBox.superclass.initEvents.call(this);

        this.keyNav = new Ext.KeyNav(this.el, {
            "up" : function(e){
                this.inKeyMode = true;
                this.selectPrev();
            },

            "down" : function(e){
                if(!this.isExpanded()){
                    this.onTriggerClick();
                }else{
                    this.inKeyMode = true;
                    this.selectNext();
                }
            },

            "enter" : function(e){
                this.onViewClick();
                //return true;
            },

            "esc" : function(e){
                this.collapse();
            },

            "tab" : function(e){
                this.onViewClick(false);
                return true;
            },

            scope : this,

            doRelay : function(foo, bar, hname){
                if(hname == 'down' || this.scope.isExpanded()){
                   return Ext.KeyNav.prototype.doRelay.apply(this, arguments);
                }
                return true;
            },

            forceKeyDown : true
        });
        this.queryDelay = Math.max(this.queryDelay || 10,
                this.mode == 'local' ? 10 : 250);
        this.dqTask = new Ext.util.DelayedTask(this.initQuery, this);
        if(this.typeAhead){
            this.taTask = new Ext.util.DelayedTask(this.onTypeAhead, this);
        }
        if(this.editable !== false){
            this.el.on("keyup", this.onKeyUp, this);
        }
        if(this.forceSelection){
            this.on('blur', this.doForce, this);
        }
    },

    onDestroy : function(){
        if(this.view){
            this.view.el.removeAllListeners();
            this.view.el.remove();
            this.view.purgeListeners();
        }
        if(this.list){
            this.list.destroy();
        }
        this.bindStore(null);
        Ext.form.ComboBox.superclass.onDestroy.call(this);
    },

    // private
    fireKey : function(e){
        if(e.isNavKeyPress() && !this.list.isVisible()){
            this.fireEvent("specialkey", this, e);
        }
    },

    // private
    onResize: function(w, h){
        Ext.form.ComboBox.superclass.onResize.apply(this, arguments);
        if(this.list && this.listWidth === undefined){
            var lw = Math.max(w, this.minListWidth);
            this.list.setWidth(lw);
            this.innerList.setWidth(lw - this.list.getFrameWidth('lr'));
        }
    },

    // private
    onDisable: function(){
        Ext.form.ComboBox.superclass.onDisable.apply(this, arguments);
        if(this.hiddenField){
            this.hiddenField.disabled = this.disabled;
        }
    },

    
    setEditable : function(value){
        if(value == this.editable){
            return;
        }
        this.editable = value;
        if(!value){
            this.el.dom.setAttribute('readOnly', true);
            this.el.on('mousedown', this.onTriggerClick,  this);
            this.el.addClass('x-combo-noedit');
        }else{
            this.el.dom.setAttribute('readOnly', false);
            this.el.un('mousedown', this.onTriggerClick,  this);
            this.el.removeClass('x-combo-noedit');
        }
    },

    // private
    onBeforeLoad : function(){
        if(!this.hasFocus){
            return;
        }
        this.innerList.update(this.loadingText ?
               '<div class="loading-indicator">'+this.loadingText+'</div>' : '');
        this.restrictHeight();
        this.selectedIndex = -1;
    },

    // private
    onLoad : function(){
        if(!this.hasFocus){
            return;
        }
        if(this.store.getCount() > 0){
            this.expand();
            this.restrictHeight();
            if(this.lastQuery == this.allQuery){
                if(this.editable){
                    this.el.dom.select();
                }
                if(!this.selectByValue(this.value, true)){
                    this.select(0, true);
                }
            }else{
                this.selectNext();
                if(this.typeAhead && this.lastKey != Ext.EventObject.BACKSPACE && this.lastKey != Ext.EventObject.DELETE){
                    this.taTask.delay(this.typeAheadDelay);
                }
            }
        }else{
            this.onEmptyResults();
        }
        //this.el.focus();
    },

    // private
    onTypeAhead : function(){
        if(this.store.getCount() > 0){
            var r = this.store.getAt(0);
            var newValue = r.data[this.displayField];
            var len = newValue.length;
            var selStart = this.getRawValue().length;
            if(selStart != len){
                this.setRawValue(newValue);
                this.selectText(selStart, newValue.length);
            }
        }
    },

    // private
    onSelect : function(record, index){
        if(this.fireEvent('beforeselect', this, record, index) !== false){
            this.setValue(record.data[this.valueField || this.displayField]);
            this.collapse();
            this.fireEvent('select', this, record, index);
        }
    },

    
    getValue : function(){
        if(this.valueField){
            return typeof this.value != 'undefined' ? this.value : '';
        }else{
            return Ext.form.ComboBox.superclass.getValue.call(this);
        }
    },

    
    clearValue : function(){
        if(this.hiddenField){
            this.hiddenField.value = '';
        }
        this.setRawValue('');
        this.lastSelectionText = '';
        this.applyEmptyText();
    },

    
    setValue : function(v){
        var text = v;
        if(this.valueField){
            var r = this.findRecord(this.valueField, v);
            if(r){
                text = r.data[this.displayField];
            }else if(this.valueNotFoundText !== undefined){
                text = this.valueNotFoundText;
            }
        }
        this.lastSelectionText = text;
        if(this.hiddenField){
            this.hiddenField.value = v;
        }
        Ext.form.ComboBox.superclass.setValue.call(this, text);
        this.value = v;
    },

    // private
    findRecord : function(prop, value){
        var record;
        if(this.store.getCount() > 0){
            this.store.each(function(r){
                if(r.data[prop] == value){
                    record = r;
                    return false;
                }
            });
        }
        return record;
    },

    // private
    onViewMove : function(e, t){
        this.inKeyMode = false;
    },

    // private
    onViewOver : function(e, t){
        if(this.inKeyMode){ // prevent key nav and mouse over conflicts
            return;
        }
        var item = this.view.findItemFromChild(t);
        if(item){
            var index = this.view.indexOf(item);
            this.select(index, false);
        }
    },

    // private
    onViewClick : function(doFocus){
        var index = this.view.getSelectedIndexes()[0];
        var r = this.store.getAt(index);
        if(r){
            this.onSelect(r, index);
        }
        if(doFocus !== false){
            this.el.focus();
        }
    },

    // private
    restrictHeight : function(){
        this.innerList.dom.style.height = '';
        var inner = this.innerList.dom;
        var fw = this.list.getFrameWidth('tb');
        var h = Math.max(inner.clientHeight, inner.offsetHeight, inner.scrollHeight);
        this.innerList.setHeight(h < this.maxHeight ? 'auto' : this.maxHeight);
        this.list.beginUpdate();
        this.list.setHeight(this.innerList.getHeight()+fw+(this.resizable?this.handleHeight:0)+this.assetHeight);
        this.list.alignTo(this.el, this.listAlign);
        this.list.endUpdate();
    },

    // private
    onEmptyResults : function(){
        this.collapse();
    },

    
    isExpanded : function(){
        return this.list && this.list.isVisible();
    },

    
    selectByValue : function(v, scrollIntoView){
        if(v !== undefined && v !== null){
            var r = this.findRecord(this.valueField || this.displayField, v);
            if(r){
                this.select(this.store.indexOf(r), scrollIntoView);
                return true;
            }
        }
        return false;
    },

    
    select : function(index, scrollIntoView){
        this.selectedIndex = index;
        this.view.select(index);
        if(scrollIntoView !== false){
            var el = this.view.getNode(index);
            if(el){
                this.innerList.scrollChildIntoView(el, false);
            }
        }
    },

    // private
    selectNext : function(){
        var ct = this.store.getCount();
        if(ct > 0){
            if(this.selectedIndex == -1){
                this.select(0);
            }else if(this.selectedIndex < ct-1){
                this.select(this.selectedIndex+1);
            }
        }
    },

    // private
    selectPrev : function(){
        var ct = this.store.getCount();
        if(ct > 0){
            if(this.selectedIndex == -1){
                this.select(0);
            }else if(this.selectedIndex != 0){
                this.select(this.selectedIndex-1);
            }
        }
    },

    // private
    onKeyUp : function(e){
        if(this.editable !== false && !e.isSpecialKey()){
            this.lastKey = e.getKey();
            this.dqTask.delay(this.queryDelay);
        }
    },

    // private
    validateBlur : function(){
        return !this.list || !this.list.isVisible();   
    },

    // private
    initQuery : function(){
        this.doQuery(this.getRawValue());
    },

    // private
    doForce : function(){
        if(this.el.dom.value.length > 0){
            this.el.dom.value =
                this.lastSelectionText === undefined ? '' : this.lastSelectionText;
            this.applyEmptyText();
        }
    },

    
    doQuery : function(q, forceAll){
        if(q === undefined || q === null){
            q = '';
        }
        var qe = {
            query: q,
            forceAll: forceAll,
            combo: this,
            cancel:false
        };
        if(this.fireEvent('beforequery', qe)===false || qe.cancel){
            return false;
        }
        q = qe.query;
        forceAll = qe.forceAll;
        if(forceAll === true || (q.length >= this.minChars)){
            if(this.lastQuery !== q){
                this.lastQuery = q;
                if(this.mode == 'local'){
                    this.selectedIndex = -1;
                    if(forceAll){
                        this.store.clearFilter();
                    }else{
                        this.store.filter(this.displayField, q);
                    }
                    this.onLoad();
                }else{
                    this.store.baseParams[this.queryParam] = q;
                    this.store.load({
                        params: this.getParams(q)
                    });
                    this.expand();
                }
            }else{
                this.selectedIndex = -1;
                this.onLoad();   
            }
        }
    },

    // private
    getParams : function(q){
        var p = {};
        //p[this.queryParam] = q;
        if(this.pageSize){
            p.start = 0;
            p.limit = this.pageSize;
        }
        return p;
    },

    
    collapse : function(){
        if(!this.isExpanded()){
            return;
        }
        this.list.hide();
        Ext.getDoc().un('mousewheel', this.collapseIf, this);
        Ext.getDoc().un('mousedown', this.collapseIf, this);
        this.fireEvent('collapse', this);
    },

    // private
    collapseIf : function(e){
        if(!e.within(this.wrap) && !e.within(this.list)){
            this.collapse();
        }
    },

    
    expand : function(){
        if(this.isExpanded() || !this.hasFocus){
            return;
        }
        this.list.alignTo(this.wrap, this.listAlign);
        this.list.show();
        Ext.getDoc().on('mousewheel', this.collapseIf, this);
        Ext.getDoc().on('mousedown', this.collapseIf, this);
        this.fireEvent('expand', this);
    },

    // private
    // Implements the default empty TriggerField.onTriggerClick function
    onTriggerClick : function(){
        if(this.disabled){
            return;
        }
        if(this.isExpanded()){
            this.collapse();
            this.el.focus();
        }else {
            this.onFocus({});
            if(this.triggerAction == 'all') {
                this.doQuery(this.allQuery, true);
            } else {
                this.doQuery(this.getRawValue());
            }
            this.el.focus();
        }
    }

    
    
    
    

});
Ext.reg('combo', Ext.form.ComboBox);



Ext.Editor = function(field, config){
    this.field = field;
    Ext.Editor.superclass.constructor.call(this, config);
};

Ext.extend(Ext.Editor, Ext.Component, {
    
    
    
    
    
    value : "",
    
    alignment: "c-c?",
    
    shadow : "frame",
    
    constrain : false,
    
    swallowKeys : true,
    
    completeOnEnter : false,
    
    cancelOnEsc : false,
    
    updateEl : false,

    initComponent : function(){
        Ext.Editor.superclass.initComponent.call(this);

        this.addEvents(
            
            "beforestartedit",
            
            "startedit",
            
            "beforecomplete",
            
            "complete",
            
            "specialkey"
        );
    },

    // private
    onRender : function(ct, position){
        this.el = new Ext.Layer({
            shadow: this.shadow,
            cls: "x-editor",
            parentEl : ct,
            shim : this.shim,
            shadowOffset:4,
            id: this.id,
            constrain: this.constrain
        });
        this.el.setStyle("overflow", Ext.isGecko ? "auto" : "hidden");
        if(this.field.msgTarget != 'title'){
            this.field.msgTarget = 'qtip';
        }
        this.field.render(this.el);
        if(Ext.isGecko){
            this.field.el.dom.setAttribute('autocomplete', 'off');
        }
        this.field.on("specialkey", this.onSpecialKey, this);
        if(this.swallowKeys){
            this.field.el.swallowEvent(['keydown','keypress']);
        }
        this.field.show();
        this.field.on("blur", this.onBlur, this);
        if(this.field.grow){
            this.field.on("autosize", this.el.sync,  this.el, {delay:1});
        }
    },

    onSpecialKey : function(field, e){
        if(this.completeOnEnter && e.getKey() == e.ENTER){
            e.stopEvent();
            this.completeEdit();
        }else if(this.cancelOnEsc && e.getKey() == e.ESC){
            this.cancelEdit();
        }else{
            this.fireEvent('specialkey', field, e);
        }
    },

    
    startEdit : function(el, value){
        if(this.editing){
            this.completeEdit();
        }
        this.boundEl = Ext.get(el);
        var v = value !== undefined ? value : this.boundEl.dom.innerHTML;
        if(!this.rendered){
            this.render(this.parentEl || document.body);
        }
        if(this.fireEvent("beforestartedit", this, this.boundEl, v) === false){
            return;
        }
        this.startValue = v;
        this.field.setValue(v);
        if(this.autoSize){
            var sz = this.boundEl.getSize();
            switch(this.autoSize){
                case "width":
                this.setSize(sz.width,  "");
                break;
                case "height":
                this.setSize("",  sz.height);
                break;
                default:
                this.setSize(sz.width,  sz.height);
            }
        }
        this.el.alignTo(this.boundEl, this.alignment);
        this.editing = true;
        this.show();
    },

    
    setSize : function(w, h){
        this.field.setSize(w, h);
        if(this.el){
            this.el.sync();
        }
    },

    
    realign : function(){
        this.el.alignTo(this.boundEl, this.alignment);
    },

    
    completeEdit : function(remainVisible){
        if(!this.editing){
            return;
        }
        var v = this.getValue();
        if(this.revertInvalid !== false && !this.field.isValid()){
            v = this.startValue;
            this.cancelEdit(true);
        }
        if(String(v) === String(this.startValue) && this.ignoreNoChange){
            this.editing = false;
            this.hide();
            return;
        }
        if(this.fireEvent("beforecomplete", this, v, this.startValue) !== false){
            this.editing = false;
            if(this.updateEl && this.boundEl){
                this.boundEl.update(v);
            }
            if(remainVisible !== true){
                this.hide();
            }
            this.fireEvent("complete", this, v, this.startValue);
        }
    },

    // private
    onShow : function(){
        this.el.show();
        if(this.hideEl !== false){
            this.boundEl.hide();
        }
        this.field.show();
        if(Ext.isIE && !this.fixIEFocus){ // IE has problems with focusing the first time
            this.fixIEFocus = true;
            this.deferredFocus.defer(50, this);
        }else{
            this.field.focus();
        }
        this.fireEvent("startedit", this.boundEl, this.startValue);
    },

    deferredFocus : function(){
        if(this.editing){
            this.field.focus();
        }
    },

    
    cancelEdit : function(remainVisible){
        if(this.editing){
            this.setValue(this.startValue);
            if(remainVisible !== true){
                this.hide();
            }
        }
    },

    // private
    onBlur : function(){
        if(this.allowBlur !== true && this.editing){
            this.completeEdit();
        }
    },

    // private
    onHide : function(){
        if(this.editing){
            this.completeEdit();
            return;
        }
        this.field.blur();
        if(this.field.collapse){
            this.field.collapse();
        }
        this.el.hide();
        if(this.hideEl !== false){
            this.boundEl.show();
        }
    },

    
    setValue : function(v){
        this.field.setValue(v);
    },

    
    getValue : function(){
        return this.field.getValue();
    },

    beforeDestroy : function(){
        this.field.destroy();
        this.field = null;
    }
});
Ext.reg('editor', Ext.Editor);



Ext.form.BasicForm = function(el, config){
    Ext.apply(this, config);
    
    this.items = new Ext.util.MixedCollection(false, function(o){
        return o.id || (o.id = Ext.id());
    });
    this.addEvents(
        
        'beforeaction',
        
        'actionfailed',
        
        'actioncomplete'
    );
    
    if(el){
        this.initEl(el);
    }
    Ext.form.BasicForm.superclass.constructor.call(this);
};

Ext.extend(Ext.form.BasicForm, Ext.util.Observable, {
    
    
    
    
    
    
    
    timeout: 30,

    // private
    activeAction : null,

    
    trackResetOnLoad : false,

    
    
    // private
    initEl : function(el){
        this.el = Ext.get(el);
        this.id = this.el.id || Ext.id();
        this.el.on('submit', this.onSubmit, this);
        this.el.addClass('x-form');
    },

    
    getEl: function(){
        return this.el;
    },

    // private
    onSubmit : function(e){
        e.stopEvent();
    },
    
    // private
	destroy: function() {
        this.items.each(function(f){
            Ext.destroy(f);
        });
		this.el.removeAllListeners();
		this.el.remove();
		this.purgeListeners();
	},

    
    isValid : function(){
        var valid = true;
        this.items.each(function(f){
           if(!f.validate()){
               valid = false;
           }
        });
        return valid;
    },

    
    isDirty : function(){
        var dirty = false;
        this.items.each(function(f){
           if(f.isDirty()){
               dirty = true;
               return false;
           }
        });
        return dirty;
    },

    
    doAction : function(action, options){
        if(typeof action == 'string'){
            action = new Ext.form.Action.ACTION_TYPES[action](this, options);
        }
        if(this.fireEvent('beforeaction', this, action) !== false){
            this.beforeAction(action);
            action.run.defer(100, action);
        }
        return this;
    },

    
    submit : function(options){
        this.doAction('submit', options);
        return this;
    },

    
    load : function(options){
        this.doAction('load', options);
        return this;
    },

    
    updateRecord : function(record){
        record.beginEdit();
        var fs = record.fields;
        fs.each(function(f){
            var field = this.findField(f.name);
            if(field){
                record.set(f.name, field.getValue());
            }
        }, this);
        record.endEdit();
        return this;
    },

    
    loadRecord : function(record){
        this.setValues(record.data);
        return this;
    },

    // private
    beforeAction : function(action){
        var o = action.options;
        if(o.waitMsg){
            if(this.waitMsgTarget === true){
                this.el.mask(o.waitMsg, 'x-mask-loading');
            }else if(this.waitMsgTarget){
                this.waitMsgTarget = Ext.get(this.waitMsgTarget);
                this.waitMsgTarget.mask(o.waitMsg, 'x-mask-loading');
            }else{
                Ext.MessageBox.wait(o.waitMsg, o.waitTitle || this.waitTitle || 'Please Wait...');
            }
        }
    },

    // private
    afterAction : function(action, success){
        this.activeAction = null;
        var o = action.options;
        if(o.waitMsg){
            if(this.waitMsgTarget === true){
                this.el.unmask();
            }else if(this.waitMsgTarget){
                this.waitMsgTarget.unmask();
            }else{
                Ext.MessageBox.updateProgress(1);
                Ext.MessageBox.hide();
            }
        }
        if(success){
            if(o.reset){
                this.reset();
            }
            Ext.callback(o.success, o.scope, [this, action]);
            this.fireEvent('actioncomplete', this, action);
        }else{
            Ext.callback(o.failure, o.scope, [this, action]);
            this.fireEvent('actionfailed', this, action);
        }
    },

    
    findField : function(id){
        var field = this.items.get(id);
        if(!field){
            this.items.each(function(f){
                if(f.isFormField && (f.dataIndex == id || f.id == id || f.getName() == id)){
                    field = f;
                    return false;
                }
            });
        }
        return field || null;
    },


    
    markInvalid : function(errors){
        if(errors instanceof Array){
            for(var i = 0, len = errors.length; i < len; i++){
                var fieldError = errors[i];
                var f = this.findField(fieldError.id);
                if(f){
                    f.markInvalid(fieldError.msg);
                }
            }
        }else{
            var field, id;
            for(id in errors){
                if(typeof errors[id] != 'function' && (field = this.findField(id))){
                    field.markInvalid(errors[id]);
                }
            }
        }
        return this;
    },

    
    setValues : function(values){
        if(values instanceof Array){ // array of objects
            for(var i = 0, len = values.length; i < len; i++){
                var v = values[i];
                var f = this.findField(v.id);
                if(f){
                    f.setValue(v.value);
                    if(this.trackResetOnLoad){
                        f.originalValue = f.getValue();
                    }
                }
            }
        }else{ // object hash
            var field, id;
            for(id in values){
                if(typeof values[id] != 'function' && (field = this.findField(id))){
                    field.setValue(values[id]);
                    if(this.trackResetOnLoad){
                        field.originalValue = field.getValue();
                    }
                }
            }
        }
        return this;
    },

    
    getValues : function(asString){
        var fs = Ext.lib.Ajax.serializeForm(this.el.dom);
        if(asString === true){
            return fs;
        }
        return Ext.urlDecode(fs);
    },

    
    clearInvalid : function(){
        this.items.each(function(f){
           f.clearInvalid();
        });
        return this;
    },

    
    reset : function(){
        this.items.each(function(f){
            f.reset();
        });
        return this;
    },

    
    add : function(){
        this.items.addAll(Array.prototype.slice.call(arguments, 0));
        return this;
    },


    
    remove : function(field){
        this.items.remove(field);
        return this;
    },

    
    render : function(){
        this.items.each(function(f){
            if(f.isFormField && !f.rendered && document.getElementById(f.id)){ // if the element exists
                f.applyToMarkup(f.id);
            }
        });
        return this;
    },

    
    applyToFields : function(o){
        this.items.each(function(f){
           Ext.apply(f, o);
        });
        return this;
    },

    
    applyIfToFields : function(o){
        this.items.each(function(f){
           Ext.applyIf(f, o);
        });
        return this;
    }
});

// back compat
Ext.BasicForm = Ext.form.BasicForm;



Ext.FormPanel = Ext.extend(Ext.Panel, {
    
    
    
    buttonAlign:'center',

    
    minButtonWidth:75,

    
    labelAlign:'left',

    
    monitorValid : false,

    
    monitorPoll : 200,

    
    layout: 'form',

    // private
    initComponent :function(){
        this.form = this.createForm();
        
        Ext.FormPanel.superclass.initComponent.call(this);

        this.addEvents(
            
            'clientvalidation'
        );

        this.relayEvents(this.form, ['beforeaction', 'actionfailed', 'actioncomplete']);
    },

    // private
    createForm: function(){
        delete this.initialConfig.listeners;
        return new Ext.form.BasicForm(null, this.initialConfig);
    },

    // private
    initFields : function(){
        var f = this.form;
        var formPanel = this;
        var fn = function(c){
            if(c.doLayout && c != formPanel){
                Ext.applyIf(c, {
                    labelAlign: c.ownerCt.labelAlign,
                    labelWidth: c.ownerCt.labelWidth,
                    itemCls: c.ownerCt.itemCls
                });
                if(c.items){
                    c.items.each(fn);
                }
            }else if(c.isFormField){
                f.add(c);
            }
        }
        this.items.each(fn);
    },

    // private
    getLayoutTarget : function(){
        return this.form.el;
    },

    
    getForm : function(){
        return this.form;
    },

    // private
    onRender : function(ct, position){
        this.initFields();

        Ext.FormPanel.superclass.onRender.call(this, ct, position);
        var o = {
            tag: 'form',
            method : this.method || 'POST',
            id : this.formId || Ext.id()
        };
        if(this.fileUpload) {
            o.enctype = 'multipart/form-data';
        }
        this.form.initEl(this.body.createChild(o));
    },
    
    // private
    beforeDestroy: function(){
        Ext.FormPanel.superclass.beforeDestroy.call(this);
        Ext.destroy(this.form);
    },

    // private
    initEvents : function(){
        Ext.FormPanel.superclass.initEvents.call(this);

        if(this.monitorValid){ // initialize after render
            this.startMonitoring();
        }
    },

    
    startMonitoring : function(){
        if(!this.bound){
            this.bound = true;
            Ext.TaskMgr.start({
                run : this.bindHandler,
                interval : this.monitorPoll || 200,
                scope: this
            });
        }
    },

    
    stopMonitoring : function(){
        this.bound = false;
    },

    
    load : function(){
        this.form.load.apply(this.form, arguments);  
    },

    // private
    onDisable : function(){
        Ext.FormPanel.superclass.onDisable.call(this);
        if(this.form){
            this.form.items.each(function(){
                 this.disable();
            });
        }
    },

    // private
    onEnable : function(){
        Ext.FormPanel.superclass.onEnable.call(this);
        if(this.form){
            this.form.items.each(function(){
                 this.enable();
            });
        }
    },

    // private
    bindHandler : function(){
        if(!this.bound){
            return false; // stops binding
        }
        var valid = true;
        this.form.items.each(function(f){
            if(!f.isValid(true)){
                valid = false;
                return false;
            }
        });
        if(this.buttons){
            for(var i = 0, len = this.buttons.length; i < len; i++){
                var btn = this.buttons[i];
                if(btn.formBind === true && btn.disabled === valid){
                    btn.setDisabled(!valid);
                }
            }
        }
        this.fireEvent('clientvalidation', this, valid);
    }
});
Ext.reg('form', Ext.FormPanel);

Ext.form.FormPanel = Ext.FormPanel;





Ext.form.Action = function(form, options){
    this.form = form;
    this.options = options || {};
};


Ext.form.Action.CLIENT_INVALID = 'client';

Ext.form.Action.SERVER_INVALID = 'server';

Ext.form.Action.CONNECT_FAILURE = 'connect';

Ext.form.Action.LOAD_FAILURE = 'load';

Ext.form.Action.prototype = {










    type : 'default',


    // interface method
    run : function(options){

    },

    // interface method
    success : function(response){

    },

    // interface method
    handleResponse : function(response){

    },

    // default connection failure
    failure : function(response){
        this.response = response;
        this.failureType = Ext.form.Action.CONNECT_FAILURE;
        this.form.afterAction(this, false);
    },

    // private
    processResponse : function(response){
        this.response = response;
        if(!response.responseText){
            return true;
        }
        this.result = this.handleResponse(response);
        return this.result;
    },

    // utility functions used internally
    getUrl : function(appendParams){
        var url = this.options.url || this.form.url || this.form.el.dom.action;
        if(appendParams){
            var p = this.getParams();
            if(p){
                url += (url.indexOf('?') != -1 ? '&' : '?') + p;
            }
        }
        return url;
    },

    // private
    getMethod : function(){
        return (this.options.method || this.form.method || this.form.el.dom.method || 'POST').toUpperCase();
    },

    // private
    getParams : function(){
        var bp = this.form.baseParams;
        var p = this.options.params;
        if(p){
            if(typeof p == "object"){
                p = Ext.urlEncode(Ext.applyIf(p, bp));
            }else if(typeof p == 'string' && bp){
                p += '&' + Ext.urlEncode(bp);
            }
        }else if(bp){
            p = Ext.urlEncode(bp);
        }
        return p;
    },

    // private
    createCallback : function(opts){
		var opts = opts || {};
        return {
            success: this.success,
            failure: this.failure,
            scope: this,
            timeout: (opts.timeout*1000) || (this.form.timeout*1000),
            upload: this.form.fileUpload ? this.success : undefined
        };
    }
};


Ext.form.Action.Submit = function(form, options){
    Ext.form.Action.Submit.superclass.constructor.call(this, form, options);
};

Ext.extend(Ext.form.Action.Submit, Ext.form.Action, {
    
    type : 'submit',

    // private
    run : function(){
        var o = this.options;
        var method = this.getMethod();
        var isPost = method == 'POST';
        if(o.clientValidation === false || this.form.isValid()){
            Ext.Ajax.request(Ext.apply(this.createCallback(o), {
                form:this.form.el.dom,
                url:this.getUrl(!isPost),
                method: method,
                params:isPost ? this.getParams() : null,
                isUpload: this.form.fileUpload
            }));

        }else if (o.clientValidation !== false){ // client validation failed
            this.failureType = Ext.form.Action.CLIENT_INVALID;
            this.form.afterAction(this, false);
        }
    },

    // private
    success : function(response){
        var result = this.processResponse(response);
        if(result === true || result.success){
            this.form.afterAction(this, true);
            return;
        }
        if(result.errors){
            this.form.markInvalid(result.errors);
            this.failureType = Ext.form.Action.SERVER_INVALID;
        }
        this.form.afterAction(this, false);
    },

    // private
    handleResponse : function(response){
        if(this.form.errorReader){
            var rs = this.form.errorReader.read(response);
            var errors = [];
            if(rs.records){
                for(var i = 0, len = rs.records.length; i < len; i++) {
                    var r = rs.records[i];
                    errors[i] = r.data;
                }
            }
            if(errors.length < 1){
                errors = null;
            }
            return {
                success : rs.success,
                errors : errors
            };
        }
        return Ext.decode(response.responseText);
    }
});



Ext.form.Action.Load = function(form, options){
    Ext.form.Action.Load.superclass.constructor.call(this, form, options);
    this.reader = this.form.reader;
};

Ext.extend(Ext.form.Action.Load, Ext.form.Action, {
    // private
    type : 'load',

    // private
    run : function(){
        Ext.Ajax.request(Ext.apply(
                this.createCallback(this.options), {
                    method:this.getMethod(),
                    url:this.getUrl(false),
                    params:this.getParams()
        }));
    },

    // private
    success : function(response){
        var result = this.processResponse(response);
        if(result === true || !result.success || !result.data){
            this.failureType = Ext.form.Action.LOAD_FAILURE;
            this.form.afterAction(this, false);
            return;
        }
        this.form.clearInvalid();
        this.form.setValues(result.data);
        this.form.afterAction(this, true);
    },

    // private
    handleResponse : function(response){
        if(this.form.reader){
            var rs = this.form.reader.read(response);
            var data = rs.records && rs.records[0] ? rs.records[0].data : null;
            return {
                success : rs.success,
                data : data
            };
        }
        return Ext.decode(response.responseText);
    }
});

Ext.form.Action.ACTION_TYPES = {
    'load' : Ext.form.Action.Load,
    'submit' : Ext.form.Action.Submit
};




Ext.form.VTypes = function(){
    // closure these in so they are only created once.
    var alpha = /^[a-zA-Z_]+$/;
    var alphanum = /^[a-zA-Z0-9_]+$/;
    var email = /^([\w]+)(.[\w]+)*@([\w-]+\.){1,5}([A-Za-z]){2,4}$/;
    var url = /(((https?)|(ftp)):\/\/([\-\w]+\.)+\w{2,3}(\/[%\-\w]+(\.\w{2,})?)*(([\w\-\.\?\\\/+@&#;`~=%!]*)(\.\w{2,})?)*\/?)/i;

    // All these messages and functions are configurable
    return {
        
        'email' : function(v){
            return email.test(v);
        },
        
        'emailText' : 'This field should be an e-mail address in the format "user@domain.com"',
        
        'emailMask' : /[a-z0-9_\.\-@]/i,

        
        'url' : function(v){
            return url.test(v);
        },
        
        'urlText' : 'This field should be a URL in the format "http:/'+'/www.domain.com"',
        
        
        'alpha' : function(v){
            return alpha.test(v);
        },
        
        'alphaText' : 'This field should only contain letters and _',
        
        'alphaMask' : /[a-z_]/i,

        
        'alphanum' : function(v){
            return alphanum.test(v);
        },
        
        'alphanumText' : 'This field should only contain letters, numbers and _',
        
        'alphanumMask' : /[a-z0-9_]/i
    };
}();




Ext.form.HtmlEditor = Ext.extend(Ext.form.Field, {
    
    enableFormat : true,
    
    enableFontSize : true,
    
    enableColors : true,
    
    enableAlignments : true,
    
    enableLists : true,
    
    enableSourceEdit : true,
    
    enableLinks : true,
    
    enableFont : true,
    
    createLinkText : 'Please enter the URL for the link:',
    
    defaultLinkValue : 'http:/'+'/',
    
    fontFamilies : [
        'Arial',
        'Courier New',
        'Tahoma',
        'Times New Roman',
        'Verdana'
    ],
    defaultFont: 'tahoma',

    // private properties
    validationEvent : false,
    deferHeight: true,
    initialized : false,
    activated : false,
    sourceEditMode : false,
    onFocus : Ext.emptyFn,
    iframePad:3,
    hideMode:'offsets',
    defaultAutoCreate : {
        tag: "textarea",
        style:"width:500px;height:300px;",
        autocomplete: "off"
    },

    // private
    initComponent : function(){
        this.addEvents(
            
            'initialize',
            
            'activate',
             
            'beforesync',
             
            'beforepush',
             
            'sync',
             
            'push',
             
            'editmodechange'
        )
    },

    createFontOptions : function(){
        var buf = [], fs = this.fontFamilies, ff, lc;
        for(var i = 0, len = fs.length; i< len; i++){
            ff = fs[i];
            lc = ff.toLowerCase();
            buf.push(
                '<option value="',lc,'" style="font-family:',ff,';"',
                    (this.defaultFont == lc ? ' selected="true">' : '>'),
                    ff,
                '</option>'
            );
        }
        return buf.join('');
    },
    
    createToolbar : function(editor){

        function btn(id, toggle, handler){
            return {
                itemId : id,
                cls : 'x-btn-icon x-edit-'+id,
                enableToggle:toggle !== false,
                scope: editor,
                handler:handler||editor.relayBtnCmd,
                clickEvent:'mousedown',
                tooltip: editor.buttonTips[id] || undefined,
                tabIndex:-1
            };
        }

        // build the toolbar
        var tb = new Ext.Toolbar({
            renderTo:this.wrap.dom.firstChild
        });

        // stop form submits
        tb.el.on('click', function(e){
            e.preventDefault();
        });

        if(this.enableFont && !Ext.isSafari){
            this.fontSelect = tb.el.createChild({
                tag:'select',
                cls:'x-font-select',
                html: this.createFontOptions()
            });
            this.fontSelect.on('change', function(){
                var font = this.fontSelect.dom.value;
                this.relayCmd('fontname', font);
                this.deferFocus();
            }, this);
            tb.add(
                this.fontSelect.dom,
                '-'
            );
        };

        if(this.enableFormat){
            tb.add(
                btn('bold'),
                btn('italic'),
                btn('underline')
            );
        };

        if(this.enableFontSize){
            tb.add(
                '-',
                btn('increasefontsize', false, this.adjustFont),
                btn('decreasefontsize', false, this.adjustFont)
            );
        };

        if(this.enableColors){
            tb.add(
                '-', {
                    itemId:'forecolor',
                    cls:'x-btn-icon x-edit-forecolor',
                    clickEvent:'mousedown',
                    tooltip: editor.buttonTips['forecolor'] || undefined,
                    tabIndex:-1,
                    menu : new Ext.menu.ColorMenu({
                        allowReselect: true,
                        focus: Ext.emptyFn,
                        value:'000000',
                        plain:true,
                        selectHandler: function(cp, color){
                            this.execCmd('forecolor', Ext.isSafari || Ext.isIE ? '#'+color : color);
                            this.deferFocus();
                        },
                        scope: this,
                        clickEvent:'mousedown'
                    })
                }, {
                    itemId:'backcolor',
                    cls:'x-btn-icon x-edit-backcolor',
                    clickEvent:'mousedown',
                    tooltip: editor.buttonTips['backcolor'] || undefined,
                    tabIndex:-1,
                    menu : new Ext.menu.ColorMenu({
                        focus: Ext.emptyFn,
                        value:'FFFFFF',
                        plain:true,
                        allowReselect: true,
                        selectHandler: function(cp, color){
                            if(Ext.isGecko){
                                this.execCmd('useCSS', false);
                                this.execCmd('hilitecolor', color);
                                this.execCmd('useCSS', true);
                                this.deferFocus();
                            }else{
                                this.execCmd(Ext.isOpera ? 'hilitecolor' : 'backcolor', Ext.isSafari || Ext.isIE ? '#'+color : color);
                                this.deferFocus();
                            }
                        },
                        scope:this,
                        clickEvent:'mousedown'
                    })
                }
            );
        };

        if(this.enableAlignments){
            tb.add(
                '-',
                btn('justifyleft'),
                btn('justifycenter'),
                btn('justifyright')
            );
        };

        if(!Ext.isSafari){
            if(this.enableLinks){
                tb.add(
                    '-',
                    btn('createlink', false, this.createLink)
                );
            };

            if(this.enableLists){
                tb.add(
                    '-',
                    btn('insertorderedlist'),
                    btn('insertunorderedlist')
                );
            }
            if(this.enableSourceEdit){
                tb.add(
                    '-',
                    btn('sourceedit', true, function(btn){
                        this.toggleSourceEdit(btn.pressed);
                    })
                );
            }
        }

        this.tb = tb;
    },

    
    getDocMarkup : function(){
        return '<html><head><style type="text/css">body{border:0;margin:0;padding:3px;height:98%;cursor:text;}</style></head><body></body></html>';
    },

    getEditorBody : function(){
        return this.doc.body || this.doc.documentElement;
    },

    // private
    onRender : function(ct, position){
        Ext.form.HtmlEditor.superclass.onRender.call(this, ct, position);
        this.el.dom.style.border = '0 none';
        this.el.dom.setAttribute('tabIndex', -1);
        this.el.addClass('x-hidden');
        if(Ext.isIE){ // fix IE 1px bogus margin
            this.el.applyStyles('margin-top:-1px;margin-bottom:-1px;')
        }
        this.wrap = this.el.wrap({
            cls:'x-html-editor-wrap', cn:{cls:'x-html-editor-tb'}
        });

        this.createToolbar(this);

        this.tb.items.each(function(item){
           if(item.itemId != 'sourceedit'){
                item.disable();
            }
        });

        var iframe = document.createElement('iframe');
        iframe.name = Ext.id();
        iframe.frameBorder = 'no';

        iframe.src=(Ext.SSL_SECURE_URL || "javascript:false");

        this.wrap.dom.appendChild(iframe);

        this.iframe = iframe;

        if(Ext.isIE){
            iframe.contentWindow.document.designMode = 'on';
            this.doc = iframe.contentWindow.document;
            this.win = iframe.contentWindow;
        } else {
            this.doc = (iframe.contentDocument || window.frames[iframe.name].document);
            this.win = window.frames[iframe.name];
            this.doc.designMode = 'on';
        }
        this.doc.open();
        this.doc.write(this.getDocMarkup())
        this.doc.close();

        var task = { // must defer to wait for browser to be ready
            run : function(){
                if(this.doc.body || this.doc.readyState == 'complete'){
                    Ext.TaskMgr.stop(task);
                    this.doc.designMode="on";
                    this.initEditor.defer(10, this);
                }
            },
            interval : 10,
            duration:10000,
            scope: this
        };
        Ext.TaskMgr.start(task);

        if(!this.width){
            this.setSize(this.el.getSize());
        }
    },

    // private
    onResize : function(w, h){
        Ext.form.HtmlEditor.superclass.onResize.apply(this, arguments);
        if(this.el && this.iframe){
            if(typeof w == 'number'){
                var aw = w - this.wrap.getFrameWidth('lr');
                this.el.setWidth(this.adjustWidth('textarea', aw));
                this.iframe.style.width = aw + 'px';
            }
            if(typeof h == 'number'){
                var ah = h - this.wrap.getFrameWidth('tb') - this.tb.el.getHeight();
                this.el.setHeight(this.adjustWidth('textarea', ah));
                this.iframe.style.height = ah + 'px';
                if(this.doc){
                    this.getEditorBody().style.height = (ah - (this.iframePad*2)) + 'px';
                }
            }
        }
    },

    
    toggleSourceEdit : function(sourceEditMode){
        if(sourceEditMode === undefined){
            sourceEditMode = !this.sourceEditMode;
        }
        this.sourceEditMode = sourceEditMode === true;
        var btn = this.tb.items.get('sourceedit');
        if(btn.pressed !== this.sourceEditMode){
            btn.toggle(this.sourceEditMode);
            return;
        }
        if(this.sourceEditMode){
            this.tb.items.each(function(item){
                if(item.itemId != 'sourceedit'){
                    item.disable();
                }
            });
            this.syncValue();
            this.iframe.className = 'x-hidden';
            this.el.removeClass('x-hidden');
            this.el.dom.removeAttribute('tabIndex');
            this.el.focus();
        }else{
            if(this.initialized){
                this.tb.items.each(function(item){
                    item.enable();
                });
            }
            this.pushValue();
            this.iframe.className = '';
            this.el.addClass('x-hidden');
            this.el.dom.setAttribute('tabIndex', -1);
            this.deferFocus();
        }
        var lastSize = this.lastSize;
        if(lastSize){
            delete this.lastSize;
            this.setSize(lastSize);
        }
        this.fireEvent('editmodechange', this, this.sourceEditMode);
    },

    // private used internally
    createLink : function(){
        var url = prompt(this.createLinkText, this.defaultLinkValue);
        if(url && url != 'http:/'+'/'){
            this.relayCmd('createlink', url);
        }
    },

    // private (for BoxComponent)
    adjustSize : Ext.BoxComponent.prototype.adjustSize,

    // private (for BoxComponent)
    getResizeEl : function(){
        return this.wrap;
    },

    // private (for BoxComponent)
    getPositionEl : function(){
        return this.wrap;
    },

    // private
    initEvents : function(){
        this.originalValue = this.getValue();
    },

    
    markInvalid : Ext.emptyFn,
    
    clearInvalid : Ext.emptyFn,

    setValue : function(v){
        Ext.form.HtmlEditor.superclass.setValue.call(this, v);
        this.pushValue();
    },

    
    cleanHtml : function(html){
        html = String(html);
        if(html.length > 5){
            if(Ext.isSafari){ // strip safari nonsense
                html = html.replace(/\sclass="(?:Apple-style-span|khtml-block-placeholder)"/gi, '');
            }
        }
        if(html == '&nbsp;'){
            html = '';
        }
        return html;
    },

    
    syncValue : function(){
        if(this.initialized){
            var bd = this.getEditorBody();
            var html = bd.innerHTML;
            if(Ext.isSafari){
                var bs = bd.getAttribute('style'); // Safari puts text-align styles on the body element!
                var m = bs.match(/text-align:(.*?);/i);
                if(m && m[1]){
                    html = '<div style="'+m[0]+'">' + html + '</div>';
                }
            }
            html = this.cleanHtml(html);
            if(this.fireEvent('beforesync', this, html) !== false){
                this.el.dom.value = html;
                this.fireEvent('sync', this, html);
            }
        }
    },

    
    pushValue : function(){
        if(this.initialized){
            var v = this.el.dom.value;
            if(!this.activated && v.length < 1){
                v = '&nbsp;';
            }
            if(this.fireEvent('beforepush', this, v) !== false){
                this.getEditorBody().innerHTML = v;
                this.fireEvent('push', this, v);
            }
        }
    },

    // private
    deferFocus : function(){
        this.focus.defer(10, this);
    },

    // doc'ed in Field
    focus : function(){
        if(this.win && !this.sourceEditMode){
            this.win.focus();
        }else{
            this.el.focus();
        }
    },

    // private
    initEditor : function(){
        var dbody = this.getEditorBody();
        var ss = this.el.getStyles('font-size', 'font-family', 'background-image', 'background-repeat');
        ss['background-attachment'] = 'fixed'; // w3c
        dbody.bgProperties = 'fixed'; // ie
        Ext.DomHelper.applyStyles(dbody, ss);
        Ext.EventManager.on(this.doc, {
            'mousedown': this.onEditorEvent,
            'dblclick': this.onEditorEvent,
            'click': this.onEditorEvent,
            'keyup': this.onEditorEvent,
            buffer:100,
            scope: this
        });
        if(Ext.isGecko){
            Ext.EventManager.on(this.doc, 'keypress', this.applyCommand, this);
        }
        if(Ext.isIE || Ext.isSafari || Ext.isOpera){
            Ext.EventManager.on(this.doc, 'keydown', this.fixKeys, this);
        }
        this.initialized = true;

        this.fireEvent('initialize', this);
        this.pushValue();
    },

    // private
    onDestroy : function(){
        if(this.rendered){
            this.tb.items.each(function(item){
                if(item.menu){
                    item.menu.removeAll();
                    if(item.menu.el){
                        item.menu.el.destroy();
                    }
                }
                item.destroy();
            });
            this.wrap.dom.innerHTML = '';
            this.wrap.remove();
        }
    },

    // private
    onFirstFocus : function(){
        this.activated = true;
        this.tb.items.each(function(item){
           item.enable();
        });
        if(Ext.isGecko){ // prevent silly gecko errors
            this.win.focus();
            var s = this.win.getSelection();
            if(!s.focusNode || s.focusNode.nodeType != 3){
                var r = s.getRangeAt(0);
                r.selectNodeContents(this.getEditorBody());
                r.collapse(true);
                this.deferFocus();
            }
            try{
                this.execCmd('useCSS', true);
                this.execCmd('styleWithCSS', false);
            }catch(e){}
        }
        this.fireEvent('activate', this);
    },

    // private
    adjustFont: function(btn){
        var adjust = btn.itemId == 'increasefontsize' ? 1 : -1;
        if(Ext.isSafari){ // safari
            adjust *= 2;
        }
        var v = parseInt(this.doc.queryCommandValue('FontSize')|| 3, 10);
        v = Math.max(1, v+adjust);
        this.execCmd('FontSize', v + (Ext.isSafari ? 'px' : 0));
    },

    onEditorEvent : function(e){
        this.updateToolbar();
    },


    
    updateToolbar: function(){

        if(!this.activated){
            this.onFirstFocus();
            return;
        }

        var btns = this.tb.items.map, doc = this.doc;

        if(this.enableFont && !Ext.isSafari){
            var name = (this.doc.queryCommandValue('FontName')||this.defaultFont).toLowerCase();
            if(name != this.fontSelect.dom.value){
                this.fontSelect.dom.value = name;
            }
        }
        if(this.enableFormat){
            btns.bold.toggle(doc.queryCommandState('bold'));
            btns.italic.toggle(doc.queryCommandState('italic'));
            btns.underline.toggle(doc.queryCommandState('underline'));
        }
        if(this.enableAlignments){
            btns.justifyleft.toggle(doc.queryCommandState('justifyleft'));
            btns.justifycenter.toggle(doc.queryCommandState('justifycenter'));
            btns.justifyright.toggle(doc.queryCommandState('justifyright'));
        }
        if(!Ext.isSafari && this.enableLists){
            btns.insertorderedlist.toggle(doc.queryCommandState('insertorderedlist'));
            btns.insertunorderedlist.toggle(doc.queryCommandState('insertunorderedlist'));
        }
        
        Ext.menu.MenuMgr.hideAll();

        this.syncValue();
    },

    // private
    relayBtnCmd : function(btn){
        this.relayCmd(btn.itemId);
    },

    
    relayCmd : function(cmd, value){
        this.win.focus();
        this.execCmd(cmd, value);
        this.updateToolbar();
        this.deferFocus();
    },

    
    execCmd : function(cmd, value){
        this.doc.execCommand(cmd, false, value === undefined ? null : value);
        this.syncValue();
    },

    // private
    applyCommand : function(e){
        if(e.ctrlKey){
            var c = e.getCharCode(), cmd;
            if(c > 0){
                c = String.fromCharCode(c);
                switch(c){
                    case 'b':
                        cmd = 'bold';
                    break;
                    case 'i':
                        cmd = 'italic';
                    break;
                    case 'u':
                        cmd = 'underline';
                    break;
                }
                if(cmd){
                    this.win.focus();
                    this.execCmd(cmd);
                    this.deferFocus();
                    e.preventDefault();
                }
            }
        }
    },

    
    insertAtCursor : function(text){
        if(!this.activated){
            return;
        }
        if(Ext.isIE){
            this.win.focus();
            var r = this.doc.selection.createRange();
            if(r){
                r.collapse(true);
                r.pasteHTML(text);
                this.syncValue();
                this.deferFocus();
            }
        }else if(Ext.isGecko || Ext.isOpera){
            this.win.focus();
            this.execCmd('InsertHTML', text);
            this.deferFocus();
        }else if(Ext.isSafari){
            this.execCmd('InsertText', text);
            this.deferFocus();
        }
    },

    // private
    fixKeys : function(){ // load time branching for fastest keydown performance
        if(Ext.isIE){
            return function(e){
                var k = e.getKey(), r;
                if(k == e.TAB){
                    e.stopEvent();
                    r = this.doc.selection.createRange();
                    if(r){
                        r.collapse(true);
                        r.pasteHTML('&nbsp;&nbsp;&nbsp;&nbsp;');
                        this.deferFocus();
                    }
                }else if(k == e.ENTER){
                    r = this.doc.selection.createRange();
                    if(r){
                        var target = r.parentElement();
                        if(!target || target.tagName.toLowerCase() != 'li'){
                            e.stopEvent();
                            r.pasteHTML('<br />');
                            r.collapse(false);
                            r.select();
                        }
                    }
                }
            };
        }else if(Ext.isOpera){
            return function(e){
                var k = e.getKey();
                if(k == e.TAB){
                    e.stopEvent();
                    this.win.focus();
                    this.execCmd('InsertHTML','&nbsp;&nbsp;&nbsp;&nbsp;');
                    this.deferFocus();
                }
            };
        }else if(Ext.isSafari){
            return function(e){
                var k = e.getKey();
                if(k == e.TAB){
                    e.stopEvent();
                    this.execCmd('InsertText','\t');
                    this.deferFocus();
                }
             };
        }
    }(),

    
    getToolbar : function(){
        return this.tb;
    },

    
    buttonTips : {
        bold : {
            title: 'Bold (Ctrl+B)',
            text: 'Make the selected text bold.',
            cls: 'x-html-editor-tip'
        },
        italic : {
            title: 'Italic (Ctrl+I)',
            text: 'Make the selected text italic.',
            cls: 'x-html-editor-tip'
        },
        underline : {
            title: 'Underline (Ctrl+U)',
            text: 'Underline the selected text.',
            cls: 'x-html-editor-tip'
        },
        increasefontsize : {
            title: 'Grow Text',
            text: 'Increase the font size.',
            cls: 'x-html-editor-tip'
        },
        decreasefontsize : {
            title: 'Shrink Text',
            text: 'Decrease the font size.',
            cls: 'x-html-editor-tip'
        },
        backcolor : {
            title: 'Text Highlight Color',
            text: 'Change the background color of the selected text.',
            cls: 'x-html-editor-tip'
        },
        forecolor : {
            title: 'Font Color',
            text: 'Change the color of the selected text.',
            cls: 'x-html-editor-tip'
        },
        justifyleft : {
            title: 'Align Text Left',
            text: 'Align text to the left.',
            cls: 'x-html-editor-tip'
        },
        justifycenter : {
            title: 'Center Text',
            text: 'Center text in the editor.',
            cls: 'x-html-editor-tip'
        },
        justifyright : {
            title: 'Align Text Right',
            text: 'Align text to the right.',
            cls: 'x-html-editor-tip'
        },
        insertunorderedlist : {
            title: 'Bullet List',
            text: 'Start a bulleted list.',
            cls: 'x-html-editor-tip'
        },
        insertorderedlist : {
            title: 'Numbered List',
            text: 'Start a numbered list.',
            cls: 'x-html-editor-tip'
        },
        createlink : {
            title: 'Hyperlink',
            text: 'Make the selected text a hyperlink.',
            cls: 'x-html-editor-tip'
        },
        sourceedit : {
            title: 'Source Edit',
            text: 'Switch to source editing mode.',
            cls: 'x-html-editor-tip'
        }
    }

    // hide stuff that is not compatible
    
    
    
    
    
    
    
    
    
    
    
    
});
Ext.reg('htmleditor', Ext.form.HtmlEditor);


Ext.form.TimeField = Ext.extend(Ext.form.ComboBox, {
    
    minValue : null,
    
    maxValue : null,
    
    minText : "The time in this field must be equal to or after {0}",
    
    maxText : "The time in this field must be equal to or before {0}",
    
    invalidText : "{0} is not a valid time",
    
    format : "g:i A",
    
    altFormats : "g:ia|g:iA|g:i a|g:i A|h:i|g:i|H:i|ga|ha|gA|h a|g a|g A|gi|hi|gia|hia|g|H",
    
    increment: 15,

    
    mode: 'local',
    
    triggerAction: 'all',
    
    typeAhead: false,

    
    initComponent : function(){
        Ext.form.TimeField.superclass.initComponent.call(this);

        if(typeof this.minValue == "string"){
            this.minValue = this.parseDate(this.minValue);
        }
        if(typeof this.maxValue == "string"){
            this.maxValue = this.parseDate(this.maxValue);
        }

        if(!this.store){
            var min = this.parseDate(this.minValue);
            if(!min){
                min = new Date().clearTime();
            }
            var max = this.parseDate(this.maxValue);
            if(!max){
                max = new Date().clearTime().add('mi', (24 * 60) - 1);
            }
            var times = [];
            while(min <= max){
                times.push([min.dateFormat(this.format)]);
                min = min.add('mi', this.increment);
            }
            this.store = new Ext.data.SimpleStore({
                fields: ['text'],
                data : times
            });
            this.displayField = 'text';
        }
    },

    
    getValue : function(){
        var v = Ext.form.TimeField.superclass.getValue.call(this);
        return this.formatDate(this.parseDate(v)) || '';
    },

    
    setValue : function(value){
        Ext.form.TimeField.superclass.setValue.call(this, this.formatDate(this.parseDate(value)));
    },

    
    validateValue : Ext.form.DateField.prototype.validateValue,
    parseDate : Ext.form.DateField.prototype.parseDate,
    formatDate : Ext.form.DateField.prototype.formatDate,

    
    beforeBlur : function(){
        var v = this.parseDate(this.getRawValue());
        if(v){
            this.setValue(v.dateFormat(this.format));
        }
    }

    
    
    
    
});
Ext.reg('timefield', Ext.form.TimeField);
