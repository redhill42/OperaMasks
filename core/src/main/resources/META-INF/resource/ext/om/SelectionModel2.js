Ext.grid.NoneSelectionModel = function(){};
Ext.extend(Ext.grid.NoneSelectionModel, Ext.grid.AbstractSelectionModel, {
    initEvents : function(){},
    getCount : function(){return 0;},
    getSelections : function(){return [];},
    hasSelection : function(){return false;}
});


Ext.grid.RowSelectionModel2 = function(config){
    Ext.grid.RowSelectionModel2.superclass.constructor.call(this,config);
};
Ext.extend(Ext.grid.RowSelectionModel2, Ext.grid.RowSelectionModel, {
    beginSelect : function(){
        this.suppressEvent = true; // suppress event firing
    },

    endSelect : function(){
        this.suppressEvent = false; // resume event firing
    },

    fireEvent : function(){
        if (!this.suppressEvent) {
            return Ext.grid.RowSelectionModel2.superclass.fireEvent.apply(this, arguments);
        }
    },

    internalSelectRows : function(rows){
        this.clearSelections();
        for (var i = 0; i < rows.length; i++) {
            this.internalSelectRow(rows[i], true);
        }
    },

    internalSelectRow : function(row, keep){
        row = this.indexOfRow(row);
        if (row >= 0) {
            this.beginSelect();
            this.selectRow(row,keep);
            this.endSelect();
        }
    },
    indexOfRow : function(row){
        var ds = this.grid.getStore();
        var count = ds.getCount();
        for (var i = 0; i < count; i++){
            if (row == ds.getAt(i).get('_serverRowIndex'))
                return i;
        }
        return -1;
    },

    rowOfIndex : function(index){
        var r = this.grid.getStore().getAt(index);
        return r ? r.get('_serverRowIndex') : -1;
    },
    
    getSelectedRows: function() {
        var items = this.getSelections();
        var rows = [];
        if (items) {
           for (var i = 0; i < items.length; i++ ) {
               rows.push(items[i].get('_serverRowIndex'));
           }
        }
        return rows;
    }
});


//Ext.grid.CellSelectionModel2 = function(config) {
//    Ext.grid.CellSelectionModel2.superclass.constructor.call(this,config);
//};
Ext.grid.CellSelectionModel2 = Ext.extend(Ext.grid.CellSelectionModel, {
    beginSelect : function(){
        this.suppressEvent = true; // suppress event firing
    },

    endSelect : function(){
        this.suppressEvent = false; // resume event firing
    },

    fireEvent : function() {
        if (!this.suppressEvent) {
            return Ext.grid.CellSelectionModel2.superclass.fireEvent.apply(this, arguments);
        }
    },

    internalSelect : function(row, col) {
        row = this.indexOfRow(row);
        if (row >= 0) {
            this.beginSelect();
            this.select(row, col);
            this.endSelect();
        }
    },

    indexOfRow : function(row){
        var ds = this.grid.getStore();
        var count = ds.getCount();
        for (var i = 0; i < count; i++){
            if (row == ds.getAt(i).get('_serverRowIndex'))
                return i;
        }
        return -1;
    },

    rowOfIndex : function(index){
        var r = this.grid.getStore().getAt(index);
        return r ? r.get('_serverRowIndex') : -1;
    }
});
