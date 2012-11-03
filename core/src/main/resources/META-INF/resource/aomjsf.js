function _OM_submit(id,params,target) {
    var f = document.forms(id);
    var t = f.target;
    var ap = new Array();
    if (target) {
        f.target = target;
    }
    for (var i=0; i < params.length; i+=2) {
        var p = document.createElement('input');
        p.type = 'hidden';
        p.name = params[i];
        p.value = params[i+1];
        f.appendChild(p);
        ap.push(p);
    }
    try {
        f.submit();
    } finally {
        f.target = t;
        for (var i=0; i < ap.length; i++) {
            f.removeChild(ap[i]);
        }
    }
}

function _OM_redirect(url,form,params) {
    function addField(field) {
        if (!field.type || field.disabled || field.readonly)
            return;
        var name = field.name;
        switch (field.type) {
        case 'checkbox':
        case 'radio':
            if (field.checked) {
                params.push(name);
                params.push(field.value);
            }
            break;
        case 'select-multiple':
            for (var i=0; i<field.options.length; i++) {
                var opt = field.options[i];
                if (opt.selected) {
                    params.push(name);
                    params.push(opt.value);
                }
            }
            break;
        case 'button':
        case 'submit':
        case 'reset':
            break;
        case 'hidden':
            if (!name.match(/^javax\.faces/) && !name.match(/_postback$/)) {
                params.push(name);
                params.push(field.value);
            }
            break;
        default:
            params.push(name);
            params.push(field.value);
            break;
        }
    }

    if (form) {
        form = document.forms[form];
        var elems = form.elements;
        if (elems != null) {
            for (var i=0; i<elems.length; i++) {
                addField(elems[i]);
            }
        }
    }

    var buf = new Array();
    buf.push(url);
    for (var i=0; i<params.length; i+=2) {
        buf.push((i==0)?'?':'&');
        if (i>0) buf.push('&');
        buf.push(encodeURIComponent(params[i]));
        buf.push('=');
        buf.push(encodeURIComponent(params[i+1]));
    }

    window.location = buf.join("");
}
