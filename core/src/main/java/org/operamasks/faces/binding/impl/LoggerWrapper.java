/*
 * $Id: LoggerWrapper.java,v 1.2 2007/10/15 21:09:47 daniel Exp $
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

import java.util.logging.*;
import java.util.ResourceBundle;
import org.operamasks.faces.util.FacesUtils;
import org.operamasks.faces.binding.ModelBean;

class LoggerWrapper extends Logger
{
    private ModelBean bean;
    private Logger logger;

    public LoggerWrapper(ModelBean bean, Logger logger, String name) {
        super(name, null);
        this.bean = bean;
        this.logger = logger;
    }

    public void log(LogRecord record) {
        if (logger.isLoggable(record.getLevel())) {
            String msg = record.getMessage();
            if (FacesUtils.isValueExpression(msg)) {
                msg = this.bean.evaluateExpression(msg, String.class);
                record.setMessage(msg);
            }
            logger.log(record);
        }
    }

    public ResourceBundle getResourceBundle() {
        return logger.getResourceBundle();
    }

    public String getResourceBundleName() {
        return logger.getResourceBundleName();
    }

    public void setFilter(Filter newFilter)
        throws SecurityException
    {
        logger.setFilter(newFilter);
    }

    public Filter getFilter() {
        return logger.getFilter();
    }

    public void setLevel(Level newLevel)
        throws SecurityException
    {
        logger.setLevel(newLevel);
    }

    public Level getLevel() {
        return logger.getLevel();
    }

    public boolean isLoggable(Level level) {
        return logger.isLoggable(level);
    }

    public String getName() {
        return logger.getName();
    }

    public void addHandler(Handler handler)
        throws SecurityException
    {
        logger.addHandler(handler);
    }

    public void removeHandler(Handler handler)
        throws SecurityException
    {
        logger.removeHandler(handler);
    }

    public Handler[] getHandlers() {
        return logger.getHandlers();
    }

    public void setUseParentHandlers(boolean useParentHandlers) {
        logger.setUseParentHandlers(useParentHandlers);
    }

    public boolean getUseParentHandlers() {
        return logger.getUseParentHandlers();
    }

    public Logger getParent() {
        return logger.getParent();
    }

    public void setParent(Logger parent) {
        logger.setParent(parent);
    }
}
