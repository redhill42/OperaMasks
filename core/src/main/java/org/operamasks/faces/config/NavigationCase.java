/*
 * $Id: NavigationCase.java,v 1.4 2007/07/02 07:38:08 jacky Exp $
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

package org.operamasks.faces.config;

public class NavigationCase
{
    private String fromViewId;
    private String fromAction;
    private String fromOutcome;
    private String toViewId;
    private boolean redirect;

    public String getFromViewId() {
        return fromViewId;
    }

    public void setFromViewId(String fromViewId) {
        this.fromViewId = fromViewId;
    }

    public String getFromAction() {
        return fromAction;
    }

    public void setFromAction(String fromAction) {
        this.fromAction = fromAction;
    }

    public String getFromOutcome() {
        return fromOutcome;
    }

    public void setFromOutcome(String fromOutcome) {
        this.fromOutcome = fromOutcome;
    }

    public String getToViewId() {
        return toViewId;
    }

    public void setToViewId(String toViewId) {
        this.toViewId = toViewId;
    }

    public boolean isRedirect() {
        return redirect;
    }

    public void setRedirect(boolean redirect) {
        this.redirect = redirect;
    }
}
