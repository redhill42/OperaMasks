//Ext.grid.FacesEditorGrid = function(container,config) {
//    Ext.grid.FacesEditorGrid.superclass.constructor.call(this, container, config);
//};

Ext.grid.FacesEditorGrid = Ext.extend(Ext.grid.EditorGridPanel, {
    invalidClass : "x-form-invalid",
    
    invalidText : "The value in this field is invalid",
    
    error : false,
    
    modifiedRecord: [],
    
    removedRecord: [],

    getInvalidText: function() {
        return this.invalidText;
    },
    
    modifiedDataField: '',
    
    removedDataField: '',
    
    hasError : function() {
        return this.error;
    },
    
    startEditing : function(row, col){
        this.stopEditing();
        var colModel = this.colModel;
        var colCnt = colModel.getColumnCount();
        
        var editAble = colModel.isCellEditable(col, row);
        if(!editAble) {
            for (var i = 0; i < colCnt; i++ ){
                editAble = colModel.isCellEditable(i, row);
                if (editAble) {
                    col = i;
                    break;
                }
            }
        }
        
        if(!editAble) {
            return;
        }
        this.view.ensureVisible(row, col, true);
        var r = this.getStore().getAt(row);
        var field = this.colModel.getDataIndex(col);
        var e = {
            grid: this,
            record: r,
            field: field,
            value: r.data[field],
            row: row,
            column: col,
            cancel:false
        };
        if(this.fireEvent("beforeedit", e) !== false && !e.cancel){
            this.editing = true;
            var ed = this.colModel.getCellEditor(col, row);
            if(!ed.rendered){
                ed.render(ed.parentEl || document.body);
            }
            (function(){ 
                ed.row = row;
                ed.col = col;
                ed.record = r;
                ed.on("complete", this.onEditComplete, this, {single: true});
                ed.on("specialkey", this.selModel.onEditorKey, this.selModel);
                this.activeEditor = ed;
                var v = r.data[field];
                ed.startEdit(this.view.getCell(row, col), v);
            }).defer(50, this);
        }
    },
    
    onEditComplete : function(ed, value, startValue){
        this.editing = false;
        this.activeEditor = null;
        ed.un("specialkey", this.selModel.onEditorKey, this.selModel);
        if(String(value) != String(startValue) ){
            var r = ed.record;
            var field = this.colModel.getDataIndex(ed.col);
            var e = {
                grid: this,
                record: r,
                field: field,
                originalValue: startValue,
                value: value,
                row: ed.row,
                column: ed.col,
                cancel:false
            };
            r.set(field, e.value);
            delete e.cancel;
            this.fireEvent("afteredit", e);
        }
        this.validate();
        this.view.focusCell(ed.row, ed.col);
    },
    
    
    
    getFormValidator: function() {
        return {
            grid: this,
            _validateField: function() {
                this.grid.applyData();
                return this.grid.hasError()?this.grid.getInvalidText():null;
            },
            getField : function() {
                return null;
            },
            handleValidationError: function(message, focusField) {
                return true;
            },
            clearMessage : function() {
            },
            displayMessage: function(message) {
            }
        };
    },
    
    validate : function() {
        this.error = false;
        var ds = this.getStore();
        var rowCnt = ds.data.length;
        var colCnt = this.colModel.getColumnCount();
        for (var row = 0 ; row < rowCnt ; row++) {
            for (var col = 0; col < colCnt; col++) {
                if(this.colModel.isCellEditable(col, row)){
                    if (this.validateField(row, col) == false ) {
                        this.error = true;
                        var colModel = this.colModel;
                        var field = colModel.getDataIndex(col);
                        var header = colModel.getColumnHeader(col);
                        this.invalidText = "'" + header + "[" + row + "," + col + "]' has an invalid value";
                        this.markInvalid(this.invalidText);
                        break;
                    }
                }
            }
            if (this.error) {
                break;
            }
        }
        if (!this.error) {
            this.clearInvalid();
            this.modifiedRecord = ds.getModifiedRecords();
            this.applyData();
        }
    },
    
    applyData : function() {
        var jsonData = '';
        var m = Ext.get(this.modifiedDataField);
        if (m) {
            var data = [];
            for(var i = 0; i < this.modifiedRecord.length; i++){
                data.push(JSON.stringify(this.modifiedRecord[i].data));
            }
            jsonData = '[' + data.join(',') + ']';
            m.dom.value = jsonData;
        }
        var r = Ext.get(this.removedDataField);
        if (r) {
            var data = [];
            for(var i = 0; i < this.removedRecord.length; i++){
                data.push(JSON.stringify(this.removedRecord[i].data));
            }
            jsonData = '[' + data.join(',') + ']';
            r.dom.value = jsonData;
        }
    },
    
    validateField: function(row, col) {
        var r = this.getStore().getAt(row);
        var field = this.colModel.getDataIndex(col);
        var ed = this.colModel.getCellEditor(col, row);
        var value = r.data[field];
        
        if (value == null || typeof(value) == typeof(undef)) value = '';
        if (ed.field.validateValue) {
            var result = ed.field.validateValue(value);
            return result;
        }
        return true;
        
        var preValue = ed.field.value;
        ed.field.value = value;
        if(!ed.rendered){
            ed.render(ed.parentEl || document.body);
        }
        if (ed.field.isValid) {
            var result = ed.field.isValid();
            ed.field.value = preValue;
            return result;
        }
        ed.field.value = preValue;
        return true;
    },
    
    stopEditing : function(){
        if(this.activeEditor){
            this.activeEditor.completeEdit();
        }
        this.activeEditor = null;
    },
    markInvalid : function(msg){
        if(!this.rendered || this.preventMark){             return;
        }
        var invalidEl = this.getGridEl();
        invalidEl = Ext.get(invalidEl.dom.parentNode)
        invalidEl.addClass(this.invalidClass);
        msg = msg || this.invalidText;
                invalidEl.dom.qtip = msg;
                invalidEl.dom.qclass = 'x-form-invalid-tip';
                this.getView().mainBody.dom.qtip = msg;
                this.getView().mainBody.dom.qclass = 'x-form-invalid-tip';
                if(Ext.QuickTips){Ext.QuickTips.enable();
                }
    },
        clearInvalid : function(){
        if(!this.rendered || this.preventMark){             return;
        }
        var invalidEl = this.getGridEl();
        invalidEl = Ext.get(invalidEl.dom.parentNode)
        invalidEl.removeClass(this.invalidClass);
        invalidEl.dom.qtip = '';
        this.getView().mainBody.dom.qtip='';
    },
    reset: function() {
        this.removedRecord = [];
        this.modifiedRecord = [];
        this.error = false;
        this.clearInvalid();
    },
    remove: function(row) {
        var r = this.getStore().getAt(row);
        var selModel = this.selModel;
        if (r == null || typeof(r) == typeof(undef)) {
            if (selModel.getSelected) {
                r = selModel.getSelected();
            } else if(selModel.selection) {
                r = selModel.selection.record;
            }
        }
        if (r == null || typeof(r) == typeof(undef)) {
            return;
        }
        if (r.data['_serverRowIndex'] != null) {
            this.removedRecord.push(r);
        }
        this.getStore().remove(r);
        this.getStore().getModifiedRecords().remove(r);
        this.modifiedRecord = this.getStore().getModifiedRecords();
    }
});
