/*
 * $Id: JSONArray.java,v 1.1 2007/04/17 10:59:38 jacky Exp $
 * Created on 2006-4-10
 */
package org.operamasks.org.json.simple;

import java.util.ArrayList;
import java.util.Iterator;


/**
 * @author FangYidong<fangyidong@yahoo.com.cn>
 */
public class JSONArray extends ArrayList {
	public String toString(){
		ItemList list=new ItemList();
		
		Iterator iter=iterator();
		
		while(iter.hasNext()){
			Object value=iter.next();				
			if(value instanceof String){
				list.add("\""+JSONObject.escape((String)value)+"\"");
			}
			else
				list.add(String.valueOf(value));
		}
		return "["+list.toString()+"]";
	}
		
}
