/*
 * $Id: ajax.js,v 1.35 2008/04/24 03:18:41 lishaochuan Exp $
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

var _IS_MSIE = window.navigator && navigator.userAgent.toLowerCase().indexOf("msie") != -1 && navigator.userAgent.toLowerCase().indexOf("opera") == -1;
var _IS_OPERA = window.navigator && navigator.userAgent.toLowerCase().indexOf("opera") != -1;
var _IS_FIREFOX = window.navigator && navigator.userAgent.toLowerCase().indexOf("firefox") != -1;

if (!String.prototype.trim) {
    String.prototype.trim = function() {
        return this.replace(/^\s*(.*?)\s*$/, "$1");
    }
}

if (!window.OM) {var OM = {};}
OM.ajax = {
    RENDER_ID_PARAM : "org.operamasks.faces.RenderId",

    getRequest : function() {
        if (window.XMLHttpRequest) {
            return new XMLHttpRequest();
        } else if (window.ActiveXObject) {
            try {
                return new ActiveXObject("Msxml2.XMLHTTP");
            } catch (e) {
                return new ActiveXObject("Microsoft.XMLHTTP");
            }
        }
    },

    requestParams : {},

    addRequestParameter : function(k,v) {
        this.requestParams[k] = v;
    },

    removeRequestParameter : function(k) {
        delete this.requestParams[k];
    },
    
    addFieldParameter : function(params, field) {
        if (!field.type || field.disabled || field.readonly)
            return;

        var key = encodeURIComponent(field.name);
        switch (field.type) {
        case 'checkbox':
        case 'radio':
            if (field.checked) {
                params.push(key + '=' + encodeURIComponent(field.value));
            }
            break;
        case 'select-multiple':
            for (var i = 0; i < field.options.length; i++) {
                var opt = field.options[i];
                if (opt.selected) {
                    params.push(key + '=' + encodeURIComponent(opt.value));
                }
            }
            break;
        case 'button':
        case 'submit':
        case 'reset':
            break;
        case 'hidden':
            if (field.name != 'javax.faces.ViewState')
                params.push(key + '=' + encodeURIComponent(field.value));
            break;
        default:
            params.push(key + '=' + encodeURIComponent(field.value));
            break;
        }
    },

    buildParameterList : function(args, form) {
        var params = new Array();

        if (args) {
            if (args instanceof Array) {
                params = params.concat(args);
            } else if (typeof args == 'object') {
                for (var k in args) {
                    var v = args[k];
                    v = (v == undefined) ? '' : encodeURIComponent(v);
                    params.push(encodeURIComponent(k) + '=' + v);
                }
            }
        }
        
        if (form) {
            var elems = form.elements;
            if (elems != null) {
                for (var p=0; p<elems.length; p++) {
                    this.addFieldParameter(params, elems[p]);
                }
            }
        }

        for (var k in this.requestParams) {
            var v = this.requestParams[k];
            v = (v == undefined) ? '' : encodeURIComponent(v);
            params.push(encodeURIComponent(k) + "=" + v);
        }

        return params;
    },

    getParentForm : function(obj) {
        if (typeof obj == 'string')
            obj = document.getElementById(obj);
        var parent = obj;
        while (parent && parent.nodeName.toLowerCase() != 'form') {
            parent = parent.parentNode;
        }
        return parent;
    },

    actionId : null,
    viewId : null,
    viewState : null,
    renderKitId : null,

    buildQuery : function(form, params) {
        params = this.buildParameterList(params, form);
        if (this.viewId)
            params.push('javax.faces.ViewId=' + encodeURIComponent(this.viewId));
        if (this.viewState)
            params.push('javax.faces.ViewState=' + this.viewState.replace(/\+/g, '%2b'));
        if (this.renderKitId)
            params.push('javax.faces.RenderKitId=' + this.renderKitId);
        return params.join('&');
    },

    submit : function(source, url, params, immediate) {
        var form = this.getParentForm(source);
        var cb = null;
        if (form) {
            cb = form._callback;
            if (cb && cb.inprogress)
                return false;
            if (cb && cb.onsubmit && cb.onsubmit.call(form) === false)
                return false;
            if (!immediate && form._validators && !this.validateForm(form))
                return false;
            if (!url) url = form.action;
        } else {
            if (!url) url = this.actionId;
        }

        var query = this.buildQuery(form, params);
		query = this.buildGroupParameterList(form, query);
        this.sendRequest(url, query, cb);
        return false;
    },
    
    buildGroupParameterList : function(form, query){
    	if(form && form.groupId){
        	for(var i=0;i<document.forms.length;i++){
        		if(form.groupId == document.forms[i].groupId){
        			var f = this.buildParameterList({},document.forms[i]);
        			query += '&' + f.join('&');
        		}
        	}
        }
        return query;
    },

    action : function(source, url, actionId, immediate) {
        var params = [encodeURIComponent(actionId) + '='];
        window.setTimeout(function() {
            OM.ajax.submit(source, url, params, immediate);
        }, 50);
    },
    
    sendRequest : function(url, query, cb) {
    	
        if (window.LOG) {
        	LOG.clear();
        	LOG.debug("AJAX request ("+ query.length + ")", query);
        }

        if (cb) {
            if (cb.onbeforerequest && cb.onbeforerequest.call(cb.scope||cb, cb) == false)
                return;
            cb.inprogress = true;
        }

        // extract render ID from query string
        var renderId = '';
        var begin = query.indexOf(this.RENDER_ID_PARAM);
        if (begin != -1) {
            begin += this.RENDER_ID_PARAM.length + 1;
            var end = query.indexOf('&', begin);
            if (end == -1) end = query.length;
            renderId = query.substring(begin, end);
        }

        var req = this.getRequest();
        req.onreadystatechange = function() { OM.ajax._onStateChange(renderId, req, cb); };
        this.updateStatus(renderId, true);
        req.open('POST', url, true);
        req.setRequestHeader('X-Requested-By', 'XMLHttpRequest');
        req.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded;charset=UTF-8');
        req.send(query);
    },

    _onStateChange : function(renderId, req, cb) {
        if (req.readyState == 4) {
            if (cb && cb.handleResponse) {
                try {
                    cb.handleResponse.call(cb.scope||cb, cb, req);
                } finally {
                    cb.inprogress = false;
                    req.onreadystatechange = function() {};
                    this.updateStatus(renderId, false);
                }
                return;
            }

            var success = true;
            try {
                var status = req.status;
                var response = req.responseText;
                var scriptAndServerLog = response.split('!@#$%');
                response = scriptAndServerLog[0];
                var serverLog = null;
                
                if (scriptAndServerLog[1])
                	serverLog = scriptAndServerLog[1];

                if (status == 200) {
                    var header = req.getResponseHeader('Content-Type');
                    if ((header || '').match(/^text\/javascript/i)) {
                    	// If logger is opened, eval response in single-step mode
                    	if (window.LOG) {
                    		LOG.debug("AJAX response (" + response.length + ")", response);
                    		var statements = response.split(';\n');
                    		var statement;
                    		for (var i = 0; i < statements.length; i++) {
                    			statement = statements[i];
                    			if (statement.length == 0)
                    				continue;
                    				
                    			if (statement.charAt(statement.length - 1) != ';')
                    				statement += ";";
                    				
                    			LOG.debug("eval script: ", statement);
                    			eval(statement);
                    		}
                    		
                    		if (serverLog)
                    			LOG.debug("server log: ", serverLog);
                    	} else {
                    		eval(response);
                    	}
                    } else {
                        this.showPage(response);
                    }
                } else if (status == 301 || status == 302) {
                    window.location = req.getResponseHeader('Location');
                } else {
                    success = false;
                    this.showPage(response);
                }
            } catch (e) {
                success = false;
                if (window.LOG) LOG.error(e.message);
                else throw e;
            } finally {
                // Avoid memory leak in MSIE: clean up the oncomplete event heandler
                req.onreadystatechange = function() {};
                this.updateStatus(renderId, false);

                if (cb) {
                    cb.inprogress = false;
                    if (success && cb.onsuccess) {
                        cb.onsuccess.call(cb.scope||cb, cb);
                    } else if (!success && cb.onfailure) {
                        cb.onfailure.call(cb.scope||cb, cb);
                    }
                    if (cb.oncomplete) {
                        cb.oncomplete.call(cb.scope||cb, cb);
                    }
                }
            }
        }
    },

    showPage : function(html) {
        window.setTimeout(function() {
            if (_IS_MSIE) {
                // fix for stupid IE bug, load page and activate all scripts
                var h = window.onerror;
                document.open('text/html');
                window.onerror = function(){return true;}
                document.write(html);
                window.onerror = h;
                document.close();
                window.location.reload(false);
            } else {
                document.open('text/html');
                document.write(html);
                document.close();
            }
        }, 50);
    },

    initForm : function(form, callback, groupId) {
        if (callback) {
            callback.scope = form;
            form._callback = callback;
        }
        form.nonAjaxSubmit = form.submit; // save old submit method
        form.submit = function() {
            return OM.ajax.submit(form);
        };
        form.onsubmit = function() {
            return OM.ajax.submit(form, form.action, callback.params, true);
        };
        if(groupId){
        	form.groupId = groupId;
        }
    },

    initValidation : function(formid, id, field, vob, ve) {
        var v = document.forms[formid]._validators;
        if (v) {
            field.validateOnBlur = vob;
            field.validationEvent = ve;
            for (var i = 0; i < v.length; i++) {
                if (v[i]._id == id) {
                    v[i].srcField = field;
                }
            }
        }
    },

    _statusTargets : new Array(),

    addStatusTarget : function(renderId, observer) {
        if (renderId == null) renderId = '';

        for (var i = 0; i < this._statusTargets.length; i++) {
            var target = this._statusTargets[i];
            if (target.renderId == renderId) {
                target.observers.push(observer);
                return;
            }
        }

        var target = new Object();
        target.count = 0;
        target.renderId = renderId;
        target.observers = [observer];
        this._statusTargets.push(target);
    },

    updateStatus : function(renderId, start) {
        for (var i = 0; i < this._statusTargets.length; i++) {
            var target = this._statusTargets[i];
            if (target.renderId == '' || target.renderId == renderId) {
                target.count += start ? 1 : -1;
                this._updateStatusTargets(target);
            }
        }
    },

    _updateStatusTargets : function(target) {
        var event = (target.count > 0) ? "onstart" : "onstop";
        for (var i = 0; i < target.observers.length; i++) {
            var o = target.observers[i];
            if (typeof o[event] == 'function') {
                o[event]();
            }
        }
    },

    _loadedUris : [],

    isScriptLoaded : function(uri) {
        if (this._loadedUris[uri]) {
            return true;
        }
        var head = document.getElementsByTagName("head")[0];
        var els = head.getElementsByTagName("script");
        for (var i = 0; i < els.length; i++) {
            var src = els[i].src;
            if (src) {
                var x = src.length - uri.length;
                if (x >= 0 && src.lastIndexOf(uri) == x) {
                    this._loadedUris[uri] = true;
                    return true;
                }
            }
        }
        return false;
    },

    loadScript : function(uri) {
        if (this.isScriptLoaded(uri))
            return;

        var req = this.getRequest();
        req.open('GET', uri, false);
        req.send(null);

        var status = req.status;
        if (status != 200) {
            var err = Error("Unable to load "+uri+", status:"+status);
            err.status = status;
            err.responseText = req.responseText;
            throw err;
        } else {
            eval(req.responseText);
            this._loadedUris[uri] = true;
            if (window.LOG) LOG.debug("Loaded "+uri);
        }
    },

    isStylesheetLoaded : function(uri) {
        var head = document.getElementsByTagName("head")[0];
        var els = head.getElementsByTagName("link");
        for (var i = 0; i < els.length; i++) {
            var el = els[i];
            if (el.rel == "stylesheet" && el.type == "text/css" && el.href) {
                var x = el.href.length - uri.length;
                if (x >= 0 && el.href.lastIndexOf(uri) == x) {
                    return true;
                }
            }
        }
        return false;
    },

    loadStylesheet : function(uri,id,cls) {
        if (this.isStylesheetLoaded(uri))
            return;

        var css = document.createElement("link");
        css.setAttribute("rel", "stylesheet");
        css.setAttribute("type", "text/css");
        css.setAttribute("href", uri);
        if (id) css.id = id;
        if (cls) css.className = cls;
        document.getElementsByTagName("head")[0].appendChild(css);
    },

    _END_:0
};

// Ajax utilitity functions
OM.F = function(id,p,v) {
    var el = document.getElementById(id);
    if (el) el[p] = v;
}
OM.S = function(id,v) {
    var el = document.getElementById(id);
    if (el) el.style.cssText = v;
}
OM.T = function(id,v) {
    var el = document.getElementById(id);
    if (el) el.innerHTML = v;
}

OM.H = function(id,html) {
    var el = document.getElementById(id);
    if (!el) return;

    var inspId = id + "_" + new Date().getTime();
    html += "<span id='" + inspId + "'></span>";

    var timer = null;
    var count = 0;
    var loadScripts = function() {
        var insp = document.getElementById(inspId);
        if (!insp) {
            if (++count > 10) {
                if (timer) window.clearInterval(timer);
            }
            return;
        }

        insp.parentNode.removeChild(insp);
        if (timer) window.clearInterval(timer);

        var re = /(?:<script(?:[^>]*)?>)(?:((\n|\r)*?)<!--)?((\n|\r|.)*?)(?:<\/script>)/img;
        var m;
        while (m = re.exec(html)) {
            var s = m[3];
            if (s && s.length > 0) {
                if (window.LOG) LOG.debug("eval script", s);
                try {
                    eval(s);
                } catch (e) {
                    if (window.LOG) LOG.error(e.message);
                }
            }
        }
    };

    el.innerHTML = html.replace(/(?:<script.*?>)((\n|\r|.)*?)(?:<\/script>)/img, "");
    if (document.getElementById(inspId)) {
        loadScripts();
    } else {
        timer = window.setInterval(loadScripts, 500);
    }
};

OM.SEL = function(id,items) {
    var el = document.getElementById(id);
    if (el) el = el.firstChild;
    if (el && el.options) {
        var opts = el.options;
        for (var i = 0; i < opts.length; i++) {
            opts[i].selected = false;
        }
        for (var i = 0; i < items.length; i++) {
            opts[items[i]].selected = true;
        }
    }
}

// -------------------------------------
// Updater support
// -------------------------------------
OM.ajax.Updater = function(url, renderId, loaded) {
    this.loaded = loaded;
    this.update = function(once) {
        if (!(once && this.loaded)) {
            OM.ajax.submit(null, url, [OM.ajax.RENDER_ID_PARAM+'='+renderId], true);
            this.loaded = true;
        }
    }
}

// -------------------------------------
// Timer support
// -------------------------------------
OM.ajax.Timer = function(id,url,delay,period) {
    this.id = id;
    this.url = url;
    this.delay = delay;
    this.period = period;
    this.form = null;
    this.params = new Array();
    this.ontimeout = null;

    var _self = this;
    this._callback = function() { _self._onTimerEvent(); }
}

OM.ajax.Timer.prototype = {
    scheduled : false,
    cancelled : false,
    requested : false,

    addParameter : function(name, value) {
        this.params.push(encodeURIComponent(name) + '=' + encodeURIComponent(value));
    },

    schedule : function() {
        if (!this.scheduled && !this.requested) {
            this.cancelled = false;
            this.scheduled = true;
            window.setTimeout(this._callback, this.delay);
        }
    },

    cancel : function() {
        this.cancelled = true;
    },

    _onTimerEvent : function() {
        this.scheduled = false;
        if (!this.cancelled) {
            if (typeof this.ontimeout == 'function') {
                this.cancelled = (this.ontimeout() == false);
            }
            if (!this.cancelled && this.period > 0) {
                this.requested = true;
                this._sendRequest();
            }
        }
    },

    _sendRequest : function() {
        var params = new Array();
        params.push(encodeURIComponent(this.id) + '=');
        params = params.concat(this.params);

        var query = OM.ajax.buildQuery(this.form, params);
        OM.ajax.sendRequest(this.url, query, {scope:this, oncomplete:this._onComplete});
    },

    _onComplete : function() {
        this.requested = false;
        if (!this.cancelled && this.period > 0) {
            this.scheduled = true;
            window.setTimeout(this._callback, this.period);
        }
    }
};

// -------------------------------------
// Progress support
// -------------------------------------
OM.ajax.Progress = function(id, url, interval) {
    this.id = id;
    this.url = url;
    this.interval = interval || 1000;
    this.params = new Array();

    this.state = "stopped";
    this.phase = 0;
    this.percentage = 0;
    this.message = null;

    this._timer = null;
}

OM.ajax.Progress.prototype.addParameter = function(name,value) {
    this.params.push(encodeURIComponent(name) + '=' + encodeURIComponent(value));
}

OM.ajax.Progress.prototype.setAutoStart = function() {
    var _self = this;
    var autoStart = function() {
        window.setTimeout(function() { _self.start(); }, 100);
    }

    if (window.addEventListener) { // for DOM Level 2
        window.addEventListener('load', autoStart, false);
    } else if (window.attachEvent) { // for IE
        window.attachEvent('onload', autoStart);
    }
}

OM.ajax.Progress.prototype.isRunning = function() {
    return this.state == 'running';
}
OM.ajax.Progress.prototype.isStopped = function() {
    return this.state == 'stopped';
}
OM.ajax.Progress.prototype.isPaused = function() {
    return this.state == 'paused';
}
OM.ajax.Progress.prototype.isCompleted = function() {
    return this.state == 'completed';
}
OM.ajax.Progress.prototype.isFailed = function() {
    return this.state == 'failed';
}

OM.ajax.Progress.prototype.start = function() {
    if (!this.isRunning())
        this._sendRequest('start');
}

OM.ajax.Progress.prototype.stop = function() {
    if (this.isRunning() || this.isPaused())
        this._sendRequest('stop');
}

OM.ajax.Progress.prototype.pause = function() {
    if (this.isRunning())
        this._sendRequest('pause');
}

OM.ajax.Progress.prototype.resume = function() {
    if (this.isPaused())
        this._sendRequest('resume');
}

OM.ajax.Progress.prototype.poll = function() {
    if (this.isRunning())
        this._sendRequest('poll');
}

OM.ajax.Progress.prototype._sendRequest = function(action) {
    if (this._timer != null)
        window.clearTimeout(this._timer);

    var params = new Array();
    params.push(encodeURIComponent(this.id) + '=' + action);
    params = params.concat(this.params);

    var query = OM.ajax.buildQuery(null, params);
    OM.ajax.sendRequest(this.url, query, null);
}

OM.ajax.Progress.prototype._handleResponse = function(params) {
    this.state = params.state;
    this.phase = params.phase;
    this.percentage = params.percentage;
    this.message = params.message;

    if (typeof this.onstatechange == 'function') {
        this.onstatechange();
    }

    if (this.isRunning()) {
        var _self = this;
        this._timer = window.setTimeout(function(){_self.poll();}, this.interval);
    }
}

// -------------------------------------
// Client side validator support
// -------------------------------------
OM.ajax.validateForm = function(f) {
    var validators = f._validators;
    if (!validators)
        return true;

    // reset validator messages
    for (var n = 0; n < validators.length; n++) {
        validators[n].clearMessage();
    }

    var result = true;
    var messages = new Array();
    var focusField = null;
    for (var n = 0; n < validators.length; n++) {
        var message = validators[n]._validateField();
        if (message != null) {
            result = false;
            if (focusField == null)
                focusField = validators[n].getField();
            if (!validators[n].displayMessage(message))
                messages.push(message);
        }
    }
    if (messages.length > 0) {
        this.handleValidationError(messages, focusField);
    }
    return result;
}

OM.ajax.validateField = function(fieldId,fieldVar) {
	var form = this.getParentForm(fieldId);
	if(form){
	    var validators = form._validators;
	    if (!validators)
	        return true;
	
	    var result = true;
	    var messages = new Array();
	    var focusField = null;
	    for (var n = 0; n < validators.length; n++) {
	        if(fieldVar.getEl().dom == validators[n].getField()){
	        	var result = validators[n]._validateField();
	        	if(result){
	        		return result;
	        	}
	        }
	    }
	}
	return true;
}

OM.ajax.handleValidationError = function(messages, focusField) {
    if (focusField != null) {
        try {
            focusField.focus();
        } catch (e) {}
    }
    alert(messages.join('\n'));
}

function Validator(id, message, display) {
    this._id = id;
    this._message = message;
    this._display = display;
}

Validator.prototype.getField = function() {
    return document.getElementById(this._id);
}

Validator.prototype._validateField = function() {
    var field = this.getField();
    if (!field || field.disabled)
        return null;

    var value = null;
    if (this.srcField) {
        value = this.srcField.processValue(this.srcField.getRawValue())
    }

    if (value == null) {
    	switch (field.type) {
    	case 'text':
    	case 'textarea':
    	case 'password':
    	case 'radio':
    	case 'hidden':
            value = field.value;
            break;

    	case 'select-one':
            var si = field.selectedIndex;
            if (si >= 0) {
            	value = field.options[si].value;
            }
            break;

    	default:
            return null;
    	}
    }

    if (!this.validate(value)) {
        return this._message.replace(/\{0\}/g, value);
    }
    return null;
}

Validator.prototype.clearMessage = function() {
    if (this._display) {
        var el = document.getElementById(this._display);
        if (el) {
            el.style.display = "none"
            el.innerHTML = "";
        }
    }
    if (this.srcField) {
        this.srcField.clearInvalid();
    }
}

Validator.prototype.displayMessage = function(msg) {
    var result = false;
    if (this.srcField) {
        this.srcField.markInvalid(msg);
        result = true;
    }
    if (this._display) {
        var el = document.getElementById(this._display);
        if (el) {
            if (!el.innerHTML) {
                el.style.display = "";
                el.innerHTML = msg;
            }
            result = true;
        }
    }
    return result;
}

Validator.prototype.validate = function(v) {
    return false;
}

function ClientValidator(id,message,display,validate) {
    this._id = id;
    this._message = message;
    this._display = display;
    this.validate = validate;
}

ClientValidator.prototype = new Validator();

function IntegerValidator(id, message, display, minValue, maxValue) {
    this._id = id;
    this._message = message;
    this._display = display;
    this._minValue = minValue;
    this._maxValue = maxValue;
}

IntegerValidator.prototype = new Validator();

IntegerValidator.prototype.validate = function(v) {
    v = v.trim();
    if (v.length == 0)
        return true;
    if (!/^[-+]?\d+$/.test(v))
        return false;
    if (this._minValue == null && this._maxValue == null)
        return true;
    var value = parseInt(v);
    if (isNaN(value))
        return false;
    if (this._minValue != null && value < this._minValue)
        return false;
    if (this._maxValue != null && value > this._maxValue)
        return false;
    return true;
}

function FloatValidator(id, message, display, minValue, maxValue) {
    this._id = id;
    this._message = message;
    this._display = display;
    this._minValue = minValue;
    this._maxValue = maxValue;
}

FloatValidator.prototype = new Validator();

FloatValidator.prototype.validate = function(v) {
    v = v.trim();
    if (v.length == 0)
        return true;
    if (!/^[-+]?(\d+|\d+\.\d*|\d*\.\d+)([eE][-+]?\d+)?$/.test(v))
        return false;
    if (this._minValue == null && this._maxValue == null)
        return true;
    var value = parseFloat(v);
    if (isNaN(value))
        return false;
    if (this._minValue != null && value < this._minValue)
        return false;
    if (this._maxValue != null && value > this._maxValue)
        return false;
    return true;
}

function RequiredValidator(id, message, display, trim) {
    this._id = id;
    this._message = message;
    this._display = display;
    this._trim = trim;
}

RequiredValidator.prototype = new Validator();

RequiredValidator.prototype.validate = function(v) {
    if (this._trim) v = v.trim();
    return v.length > 0;
}

function LengthValidator(id, message, display, minimum, maximum) {
    this._id = id;
    this._message = message;
    this._display = display;
    this._minimum = minimum;
    this._maximum = maximum;
}

LengthValidator.prototype = new Validator();

LengthValidator.prototype.validate = function(v) {
    if (this._minimum != null && v.length < this._minimum)
        return false;
    if (this._maximum != null && v.length > this._maximum)
        return false;
    return true;
}

function RegexpValidator(id, message, display, pattern) {
    this._id = id;
    this._message = message;
    this._display = display;
    this._pattern = new RegExp(pattern);
}

RegexpValidator.prototype = new Validator();

RegexpValidator.prototype.validate = function(v) {
    return v.length == 0 || this._pattern.test(v);
}

function DateValidator(id, message, display, format) {
    this._id = id;
    this._message = message;
    this._display = display;
    this._format = format;
}

DateValidator.prototype = new Validator();

DateValidator.prototype.validate = function(v) {
    return Date.parseDate(v, this._format);
}