/*
 * $Id: UserBean.java,v 1.2 2007/12/11 04:14:14 jacky Exp $
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.operamasks.faces.annotation.ManagedBean;
import org.operamasks.faces.annotation.ManagedBeanScope;

@ManagedBean(scope= ManagedBeanScope.SESSION)
public class UserBean {
    private Map<String,String> filters = new HashMap<String, String>() ;

    public Map<String, String> getFilters() {
        this.filters.put( "info" , "like '%abc%'" ) ;
        return this.filters;
    }

    public void setFilters(Map<String, String> filters) {
        this.filters = filters;
    }

    public List<UserInfo> getUserList() {
        List<UserInfo> users = new ArrayList<UserInfo>() ;
        for( int i = 0 ; i < 1000 ; i++ ) {
            UserInfo user = new UserInfo() ;
            user.setId( "user" + i ) ;
            user.setAddr( "Addr" + i ) ;
            user.setName( "name" + i ) ;
            user.setTel( "tel" + i ) ;
            user.setEmail( user.getName() + "@operamasks.org" ) ;
            users.add(user);
        }
        return users ;
    }
}
