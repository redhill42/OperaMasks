/**
 * An implementation of Ext.data.DataProxy that reads a data object from Ajax response.
 * This class is intended to be used by operamasks Ajax architecture.
 */
Ext.data.FacesScriptProxy = function(config) {
    Ext.data.FacesScriptProxy.superclass.constructor.call(this);
    Ext.apply(this, config);
    this.head = document.getElementsByTagName("head")[0];
};
Ext.extend(Ext.data.FacesScriptProxy, Ext.data.DataProxy, {
    
    
    timeout : 30000,
    
    callbackParam : "callback",
    
    nocache : true,

    
    load : function(params, reader, callback, scope, arg){
        if(this.fireEvent("beforeload", this, params) !== false){

            var p = Ext.urlEncode(Ext.apply(params, this.extraParams));

            var url = this.url;
            url += (url.indexOf("?") != -1 ? "&" : "?") + p;
            if(this.nocache){
                url += "&_dc=" + (new Date().getTime());
            }
            var transId = ++Ext.data.ScriptTagProxy.TRANS_ID;
            var trans = {
                id : transId,
                cb : "stcCallback"+transId,
                scriptId : "stcScript"+transId,
                params : params,
                arg : arg,
                url : url,
                callback : callback,
                scope : scope,
                reader : reader
            };
            var conn = this;

            window[trans.cb] = function(o){
                conn.handleResponse(o, trans);
            };

            url += String.format("&{0}={1}", this.callbackParam, trans.cb);
            
            if(this.autoAbort !== false){
                this.abort();
            }

            trans.timeoutId = this.handleFailure.defer(this.timeout, this, [trans]);

            var script = document.createElement("script");
            script.setAttribute("src", url);
            script.setAttribute("type", "text/javascript");
            script.setAttribute("id", trans.scriptId);
            this.head.appendChild(script);

            this.trans = trans;
        }else{
            callback.call(scope||this, null, arg, false);
        }
    },

    
    isLoading : function(){
        return this.trans ? true : false;
    },

    
    abort : function(){
        if(this.isLoading()){
            this.destroyTrans(this.trans);
        }
    },

    
    destroyTrans : function(trans, isLoaded){
        this.head.removeChild(document.getElementById(trans.scriptId));
        clearTimeout(trans.timeoutId);
        if(isLoaded){
            window[trans.cb] = undefined;
            try{
                delete window[trans.cb];
            }catch(e){}
        }else{
            
            window[trans.cb] = function(){
                window[trans.cb] = undefined;
                try{
                    delete window[trans.cb];
                }catch(e){}
            };
        }
    },

    
    handleResponse : function(o, trans){
        this.trans = false;
        this.destroyTrans(trans, true);
        var result;
        try {
            result = trans.reader.readRecords(o);
        }catch(e){
            this.fireEvent("loadexception", this, o, trans.arg, e);
            trans.callback.call(trans.scope||window, null, trans.arg, false);
            return;
        }
        this.fireEvent("load", this, o, trans.arg);
        trans.callback.call(trans.scope||window, result, trans.arg, true);
    },

    
    handleFailure : function(trans){
        this.trans = false;
        this.destroyTrans(trans, false);
        this.fireEvent("loadexception", this, null, trans.arg);
        trans.callback.call(trans.scope||window, null, trans.arg, false);
    }

});


Ext.data.FacesProxy = function(config) {
    Ext.data.FacesProxy.superclass.constructor.call(this);
    Ext.apply(this, config);
};

Ext.extend(Ext.data.FacesProxy, Ext.data.DataProxy, {
    load: function(params, reader, callback, scope, arg){
        if (this.fireEvent("beforeload", this, arg) !== false) {
            if (typeof arg.params == "undefined") {
                arg.params = params; // pass arguments to Store.onload
            }

            var p = OM.ajax.buildQuery(null, Ext.apply(params, this.extraParams));

            var cb = {
                scope : this,
                handleResponse : this.loadResponse,
                params: params,
                reader: reader,
                callback: callback,
                cbscope: scope,
                arg: arg
            };

            OM.ajax.sendRequest(this.url, p, cb);
        } else {
            callback.call(scope||this, null, arg, false);
        }
    },

    loadResponse : function(cb, response) {
        var result;
        try {
            if (window.LOG) LOG.debug("FacesProxy response", response.responseText);
            if (response.status != 200) {
                this.fireEvent("loadexception", this, cb.arg, null);
                cb.callback.call(cb.cbscope, null, cb.arg, false);
                return;
            }
            result = cb.reader.read(response, cb.arg);
            delete response;
            if (typeof(CollectGarbage)!= typeof(undef)){CollectGarbage();}
        } catch (e) {
            if (window.LOG) LOG.error(e.message);
            this.fireEvent("loadexception", this, cb.arg, e);
            cb.callback.call(cb.cbscope, null, cb.arg, false);
            return;
        }
        this.fireEvent("load", this, cb.arg);
        cb.callback.call(cb.cbscope, result, cb.arg, true);
    },

    update : function(params, records) {

    }
});

/**
 * An extension of Ext.data.JsonReader that receive Faces view state from server.
 */
Ext.data.FacesReader = function(meta, recordType) {
    Ext.data.FacesReader.superclass.constructor.call(this, meta, recordType);
};
Ext.extend(Ext.data.FacesReader, Ext.data.JsonReader, {
    read : function(response, a) {
        var json = response.responseText;
        var o = eval("("+json+")");
        delete json;
        if (typeof(CollectGarbage)!= typeof(undef)){CollectGarbage();}
        if (!o) {
            throw {message: "FacesReader.read: Json object not found"};
        }
        if (o.viewState) {
            OM.ajax.viewState = o.viewState;
            delete o.viewState;
            if (typeof(CollectGarbage)!= typeof(undef)){CollectGarbage();}
        }
        if (o.params) {
            if (a && a.params)
                Ext.apply(a.params, o.params);
            delete o.params;
            if (typeof(CollectGarbage)!= typeof(undef)){CollectGarbage();}
        }
        if(o.metaData){
            delete this.ef;
            if (typeof(CollectGarbage)!= typeof(undef)){CollectGarbage();}
            this.meta = o.metaData;
            this.recordType = Ext.data.Record.create(o.metaData.fields);
            this.onMetaChange(this.meta, this.recordType, o);
        }
        return this.readRecords(o);
    }
});
