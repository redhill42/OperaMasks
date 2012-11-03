/**
 * A TreeLoader extension that integrate to operamasks Ajax architecture.
 */
Ext.tree.FacesTreeLoader = function(config) {
    this.baseParams = {};
    Ext.apply(this, config);

    this.events = {
        "beforeload" : true,
        "load" : true,
        "loadexception" : true
    };
};

Ext.extend(Ext.tree.FacesTreeLoader, Ext.util.Observable, {
    uiProviders : {},
    clearOnLoad : true,

    load : function(node, callback) {
        if (this.clearOnLoad) {
            while (node.firstChild) {
                node.removeChild(node.firstChild);
            }
        }
        if (node.attributes.children) { // preloaded json children
            var cs = node.attributes.children;
            for (var i = 0, len = cs.length; i < len; i++) {
                node.appendChild(this.createNode(cs[i]));
            }
            if (typeof callback == "function") {
                callback();
            }
        } else if (this.dataUrl) {
            this.requestData(node, callback);
        }
    },

    getParams : function(node) {
        var params = [], bp = this.baseParams;
        for (var key in bp) {
            if (typeof bp[key] != "function") {
                params.push(encodeURIComponent(key) + "=" + encodeURIComponent(bp[key]));
            }
        }
        params.push("node=" + encodeURIComponent(node.id));
        
        var form = OM.ajax.getParentForm(this.baseParams._componentDomNode);
        return OM.ajax.buildQuery(form, params);
    },

    requestData : function(node, callback) {
        if (this.fireEvent("beforeload", this, node, callback) !== false) {
            var p = this.getParams(node);
            var cb = {
                scope: this,
                handleResponse: this.handleResponse,
                node: node,
                callback: callback
            };
            OM.ajax.sendRequest(this.dataUrl, p, cb);
            this.loading = true;
        } else {
            if (typeof callback == 'function') {
                callback();
            }
        }
    },

    isLoading : function() {
        return this.isLoading;
    },

    abort : function() {
        // NYI
    },

    createNode : function(attr) {
        if (this.applyLoader !== false) {
            attr.loader = this;
        }
        if (typeof attr.uiProvider == 'string') {
            attr.uiProvider = this.uiProviders[attr.uiProvider] || eval(attr.uiProvider);
        }
        return (attr.leaf ? new Ext.tree.TreeNode(attr)
                          : new Ext.tree.AsyncTreeNode(attr));
    },

    processResponse : function(response, node, callback) {
        var json = response.responseText;
        var o = eval("("+json+")");

        if (o["javax.faces.ViewState"])
            OM.ajax.viewState = o["javax.faces.ViewState"];
        if (o["_root"])
            o = o["_root"];
        
        for (var i = 0, len = o.length; i < len; i++) {
            var n = this.createNode(o[i]);
            if (n) {
                node.appendChild(n);
            }
        }
        if (typeof callback == "function") {
            callback(this, node);
        }
    },

    handleResponse : function(cb, response) {
        this.loading = false;
        try {
            this.processResponse(response, cb.node, cb.callback);
            this.fireEvent("load", this, cb.node, response);
        } catch (e) {
            this.handleFailure(cb, response);
        }
    },

    handleFailure : function(cb, response) {
        this.loading = false;
        this.fireEvent("loadexception", this, cb.node, response);
        if (typeof cb.callback == "function") {
            cb.callback(this, cb.node);
        }
    }
});
