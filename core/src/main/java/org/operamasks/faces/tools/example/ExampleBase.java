/*
 * $Id: ExampleBase.java,v 1.2 2008/01/25 08:38:41 jacky Exp $
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

package org.operamasks.faces.tools.example;

import org.operamasks.faces.tools.annotation.ComponentMeta;
import javax.faces.component.UIComponentBase;

@ComponentMeta
public abstract class ExampleBase extends UIComponentBase
{
    protected String name;
    protected int value;
    protected int custom;

    public int getCustom() {
        return this.custom;
    }

    public void setCustom(int value) {
        this.custom = value;
    }
}
