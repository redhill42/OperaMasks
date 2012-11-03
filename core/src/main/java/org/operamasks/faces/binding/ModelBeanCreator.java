/*
 * $Id: ModelBeanCreator.java,v 1.1 2007/10/15 21:09:47 daniel Exp $
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

package org.operamasks.faces.binding;

import org.operamasks.faces.binding.impl.DefaultModelBean;

public class ModelBeanCreator
{
    private ModelBeanCreator parent;

    public ModelBeanCreator(ModelBeanCreator parent) {
        this.parent = parent;
    }

    public ModelBean createModelBean(Object target) {
        if (this.parent != null) {
            return this.parent.createModelBean(target);
        } else {
            return new DefaultModelBean(target);
        }
    }
}
