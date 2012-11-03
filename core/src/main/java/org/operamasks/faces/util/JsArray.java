package org.operamasks.faces.util;

import java.util.HashMap;
import java.util.Map;

public class JsArray {
	private Map<String, Object> values;

	public void put(String key, Object value) {
		if (values == null)
			values = new HashMap<String, Object>();
		
		values.put(key, value);
	}
	
	public Object get(String key) {
		return values.get(key);
	}
	
	@Override
	public String toString() {
		return toString('\'');
	}
	
	public String toString(char quote) {
		if (quote != '\'' && quote != '"')
				quote = '\'';
					
		return toScript(values, quote);
	}
	
	@SuppressWarnings("unchecked")
	private String toScript(Map<String, Object> config, char quote) {
		if (config == null || config.size() == 0)
            return "{}";
        
        StringBuilder buf = new StringBuilder();
        buf.append("{");
        
        for (String key : config.keySet()) {
            Object value = config.get(key);
            if (value == null)
                continue;
            
            if (value == null || value.toString() == null)
            	continue;
            
            buf.append("\n");
            buf.append(key);
            buf.append(": ");
            
            if (value instanceof Boolean || value instanceof Integer ||
            			value instanceof JsObject) {
                buf.append(value);
            } else if (value instanceof Object[][]) {
            	Object[][] array = (Object[][])value;
            	Map<String, Object> nestedConfig = new HashMap<String, Object>();
            	
            	for (Object[] data : array) {
            		if (data.length < 2)
            			continue;
            		
            		nestedConfig.put((String)data[0], data[1]);
            	}
            	
            	buf.append(toScript(nestedConfig, quote));
            } else if (value instanceof Map){
            	buf.append(toScript((Map<String, Object>)value, quote));
            } else {
                buf.append(quote).append(value.toString()).append(quote);
            }
            
            buf.append(",");
        }
        
        if (buf.length() > 1) {
        	// remove last ','
            buf.delete(buf.length() - 1, buf.length());
        	buf.append("\n}");
        } else {
        	buf.append("}");
        }
        
        return buf.toString();
	}
}
