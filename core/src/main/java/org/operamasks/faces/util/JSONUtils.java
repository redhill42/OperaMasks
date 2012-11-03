/*
 * $Id: JSONUtils.java,v 1.4 2008/03/10 08:35:18 lishaochuan Exp $
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
package org.operamasks.faces.util;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;

import javax.faces.model.DataModel;

import org.operamasks.org.json.simple.JSONArray;
import org.operamasks.org.json.simple.JSONObject;
import org.operamasks.util.BeanProperty;
import org.operamasks.util.BeanUtils;

/**
 * JSON工具类
 * @author root
 *
 */
public class JSONUtils {

    @SuppressWarnings("unchecked")
    public static JSONArray writeDataModel( DataModel model ) {
        JSONArray values = new JSONArray() ;
        for( int i = 0 ; i < model.getRowCount() ; i++ ) {
            model.setRowIndex(i) ;
            JSONObject item = writeBean( model.getRowData() ) ;
            if( item != null ) {
                values.add( item ) ;
            }
        }
        return values ;
    }

    @SuppressWarnings("unchecked")
    public static JSONObject writeBean( Object bean ) {
        if( bean == null ) {
            return null ;
        }

        JSONObject propObj = new JSONObject() ;
        Collection<BeanProperty> props = null;
        try {
            props = BeanUtils.getProperties( bean.getClass() ) ;
        } catch (Exception e) {
            // ignore
        }
        
        if( props == null ) {
            return null ;
        }
        
        for( BeanProperty prop : props ) {
            Method getter = prop.getReadMethod() ;
            if( getter != null ) {
                try {
                    Object propValue = getter.invoke( bean ) ;
                    propObj.put( prop.getName() , propValue.toString() ) ;
                } catch (Exception e) {
                    // do nothing
                }
            }
        }
        
        return propObj ;
    }
    
    @SuppressWarnings("unchecked")
    public static JSONObject writeObject( Map<String,Object> object ) {
        if( object == null ) {
            return null ;
        }
        JSONObject jObject = new JSONObject() ;
        jObject.putAll( object ) ;
        return jObject ;
    }
}
