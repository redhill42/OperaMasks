/*
 * $Id: AjaxLibrary.java,v 1.6 2008/01/29 08:42:13 yangdong Exp $
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
package org.operamasks.faces.facelets.ajax;

import javax.faces.component.html.HtmlPanelGroup;
import org.operamasks.faces.component.ajax.AjaxAction;
import org.operamasks.faces.component.ajax.AjaxTimer;
import org.operamasks.faces.component.ajax.AjaxStatus;
import org.operamasks.faces.component.ajax.AjaxProgress;
import org.operamasks.faces.component.ajax.AjaxScripter;
import org.operamasks.faces.component.ajax.AjaxLogger;
import org.operamasks.faces.component.ajax.AjaxUpdater;

import com.sun.facelets.tag.AbstractTagLibrary;

public class AjaxLibrary extends AbstractTagLibrary
{
    public static final String Namespace = "http://www.apusic.com/jsf/ajax";

    public AjaxLibrary() {
        super(Namespace);

        this.addComponent("updater",
                          AjaxUpdater.COMPONENT_TYPE,
                          "org.operamasks.faces.AjaxUpdater",
                          AjaxUpdaterHandler.class);

        this.addComponent("action",
                          AjaxAction.COMPONENT_TYPE,
                          null,
                          AjaxActionHandler.class);

        this.addComponent("timer", 
                          AjaxTimer.COMPONENT_TYPE,
                          "org.operamasks.faces.AjaxTimer");

        this.addComponent("status",
                          AjaxStatus.COMPONENT_TYPE,
                          "org.operamasks.faces.AjaxStatus");

        this.addComponent("progress",
                          AjaxProgress.COMPONENT_TYPE,
                          "org.operamasks.faces.AjaxProgress",
                          AjaxProgressHandler.class);

        this.addComponent("renderGroup",
                          HtmlPanelGroup.COMPONENT_TYPE,
                          "org.operamasks.faces.RenderGroup");

        this.addComponent("scripter",
                          AjaxScripter.COMPONENT_TYPE,
                          "org.operamasks.faces.AjaxScripter");

        this.addComponent("logger",
                          AjaxLogger.COMPONENT_TYPE,
                          "org.operamasks.faces.AjaxLogger",
                          LoggerHandler.class);

        this.addValidator("clientValidator",
                          null,
                          ClientValidatorHandler.class);
    }
}
