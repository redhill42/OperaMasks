/*
 * $Id: ValidatorFactory.java,v 1.1 2007/10/22 17:04:22 daniel Exp $
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

package org.operamasks.faces.application;

import javax.faces.validator.Validator;

/**
 * Responsible to create validator instance.
 */
public interface ValidatorFactory
{
    /**
     * Create a new instance of Validator.
     * 
     * @return a new Validator instance
     */
    public Validator createValidator();
}
