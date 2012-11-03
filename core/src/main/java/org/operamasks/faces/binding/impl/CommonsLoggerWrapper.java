/*
 * $Id: CommonsLoggerWrapper.java,v 1.1 2007/10/17 00:55:12 daniel Exp $
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

package org.operamasks.faces.binding.impl;

import org.apache.commons.logging.Log;
import org.operamasks.faces.binding.ModelBean;
import static org.operamasks.faces.util.FacesUtils.isValueExpression;

class CommonsLoggerWrapper implements Log
{
    private final ModelBean bean;
    private final Log delegate;

    CommonsLoggerWrapper(ModelBean bean, Log delegate) {
        this.bean = bean;
        this.delegate = delegate;
    }

    public void debug(Object msg) {
        if (delegate.isDebugEnabled()) {
            if ((msg instanceof String) && isValueExpression((String)msg)) {
                msg = this.bean.evaluateExpression((String)msg, Object.class);
            }
            delegate.debug(msg);
        }
    }

    public void debug(Object msg, Throwable t) {
        if (delegate.isDebugEnabled()) {
            if ((msg instanceof String) && isValueExpression((String)msg)) {
                msg = this.bean.evaluateExpression((String)msg, Object.class);
            }
            delegate.debug(msg, t);
        }
    }

    public void error(Object msg) {
        if (delegate.isErrorEnabled()) {
            if ((msg instanceof String) && isValueExpression((String)msg)) {
                msg = this.bean.evaluateExpression((String)msg, Object.class);
            }
            delegate.error(msg);
        }
    }

    public void error(Object msg, Throwable t) {
        if (delegate.isErrorEnabled()) {
            if ((msg instanceof String) && isValueExpression((String)msg)) {
                msg = this.bean.evaluateExpression((String)msg, Object.class);
            }
            delegate.error(msg, t);
        }
    }

    public void fatal(Object msg) {
        if (delegate.isFatalEnabled()) {
            if ((msg instanceof String) && isValueExpression((String)msg)) {
                msg = this.bean.evaluateExpression((String)msg, Object.class);
            }
            delegate.fatal(msg);
        }
    }

    public void fatal(Object msg, Throwable t) {
        if (delegate.isFatalEnabled()) {
            if ((msg instanceof String) && isValueExpression((String)msg)) {
                msg = this.bean.evaluateExpression((String)msg, Object.class);
            }
            delegate.fatal(msg, t);
        }
    }

    public void info(Object msg) {
        if (delegate.isInfoEnabled()) {
            if ((msg instanceof String) && isValueExpression((String)msg)) {
                msg = this.bean.evaluateExpression((String)msg, Object.class);
            }
            delegate.info(msg);
        }
    }

    public void info(Object msg, Throwable t) {
        if (delegate.isInfoEnabled()) {
            if ((msg instanceof String) && isValueExpression((String)msg)) {
                msg = this.bean.evaluateExpression((String)msg, Object.class);
            }
            delegate.info(msg, t);
        }
    }

    public void trace(Object msg) {
        if (delegate.isTraceEnabled()) {
            if ((msg instanceof String) && isValueExpression((String)msg)) {
                msg = this.bean.evaluateExpression((String)msg, Object.class);
            }
            delegate.trace(msg);
        }
    }

    public void trace(Object msg, Throwable t) {
        if (delegate.isTraceEnabled()) {
            if ((msg instanceof String) && isValueExpression((String)msg)) {
                msg = this.bean.evaluateExpression((String)msg, Object.class);
            }
            delegate.trace(msg, t);
        }
    }

    public void warn(Object msg) {
        if (delegate.isWarnEnabled()) {
            if ((msg instanceof String) && isValueExpression((String)msg)) {
                msg = this.bean.evaluateExpression((String)msg, Object.class);
            }
            delegate.warn(msg);
        }
    }

    public void warn(Object msg, Throwable t) {
        if (delegate.isWarnEnabled()) {
            if ((msg instanceof String) && isValueExpression((String)msg)) {
                msg = this.bean.evaluateExpression((String)msg, Object.class);
            }
            delegate.warn(msg, t);
        }
    }

    public boolean isDebugEnabled() {
        return delegate.isDebugEnabled();
    }

    public boolean isErrorEnabled() {
        return delegate.isErrorEnabled();
    }

    public boolean isFatalEnabled() {
        return delegate.isFatalEnabled();
    }

    public boolean isInfoEnabled() {
        return delegate.isInfoEnabled();
    }

    public boolean isTraceEnabled() {
        return delegate.isTraceEnabled();
    }

    public boolean isWarnEnabled() {
        return delegate.isWarnEnabled();
    }
}
