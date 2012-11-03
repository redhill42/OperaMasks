/*
 * $Id: Department.java,v 1.2 2007/12/11 04:14:14 jacky Exp $
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
package demo;

import java.io.Serializable;

public class Department implements Serializable {
	private static final long serialVersionUID = -8779101156860460105L;
	
	private String id;
    private String name;
    private boolean hasSubDepartments;
    
    public Department(String id, String name, boolean hasSubDepartments) {
        this.id = id;
        this.name = name;
        this.hasSubDepartments = hasSubDepartments;
    }
    
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String text) {
        this.name = text;
    }
    public boolean isHasSubDepartments() {
        return hasSubDepartments;
    }
    public void setHasSubDepartments(boolean hasSubDepartments) {
        this.hasSubDepartments = hasSubDepartments;
    } 
}
