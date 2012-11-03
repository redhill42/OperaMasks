/*
 * $Id: DefaultViewMapper.java,v 1.10 2008/04/08 09:35:09 jacky Exp $
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
package org.operamasks.faces.application.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import org.operamasks.faces.application.ApplicationAssociate;
import org.operamasks.faces.application.ViewMapper;
import org.operamasks.util.SimpleCache;

/**
 * The default implementation of {@link org.operamasks.faces.application.ViewMapper}
 * interface. The following algorithm is implemented:
 *
 * 1) Strip any leading slash ("/") character.
 * 2) Strip any traling extension (".jsp", e.g.), as long as it occurs after
 *    any slash ("/") character.
 * 3) Convert each occurence of a slash ("/") character into a dot (".") character.
 * 4) Replace converted view identifier with the managed bean name pattern that
 *    configured in the operamasks.xml configuration file as follows:
 *    a) Replace "#{~view}" with the converted view identifier.
 *    b) Capitalize last element in the converted view identifier and repalce
 *       "#{~View}" in the managed bean name pattern.
 *    c) Remove prefix in the converted view identifier and replace "#{view}" in the
 *       managed bean name pattern.
 *    d) Remove prefix in the converted view identifier, capitalize first letter, and
 *       replace "#{View}" in the managed bean name pattern.
 */
public class DefaultViewMapper implements ViewMapper
{
    public static DefaultViewMapper getInstance() {
        return ApplicationAssociate.getInstance().getSingleton(DefaultViewMapper.class);
    }

    private static final class DescOrder implements Comparator<String> {
        public int compare(String a, String b) {
            return b.compareTo(a);
        }
    }

    private static final class ViewMapping {
        private Map<String,List<String>> viewMap = new HashMap<String, List<String>>();
        private TreeSet<String> wildcards = new TreeSet<String>(new DescOrder());

        public void addMapping(String viewId, String beanName) {
            List<String> beanList = this.viewMap.get(viewId);
            if (beanList == null) {
                beanList = new ArrayList<String>();
                this.viewMap.put(viewId, beanList);
            }
            if (!beanList.contains(beanName)) {
                beanList.add(beanName);
            }

            // add wildcard view mapping
            if (viewId.endsWith("*") && !viewId.equals("*")) {
                viewId = viewId.substring(0, viewId.length()-1);
                this.wildcards.add(viewId);
            }
        }

        public List<String> findMapping(String viewId) {
            List<String> resultList = new ArrayList<String>();

            // find exact match
            List<String> beanList = this.viewMap.get(viewId);
            if (beanList != null) {
                resultList.addAll(beanList);
            }
            
            // if the mapping was assigned specially, ignore global mappings. 
            if (resultList.size() > 0) {
                return resultList;
            }

            // find wildcard match
            for (String wildcard : this.wildcards) {
                if (viewId.startsWith(wildcard)) {
                    beanList = this.viewMap.get(wildcard.concat("*"));
                    if (beanList != null) {
                        resultList.addAll(beanList);
                    }
                }
            }

            // find default match
            beanList = this.viewMap.get("*");
            if (beanList != null) {
                resultList.addAll(beanList);
            }

            return resultList;
        }
    }

    private ViewMapping viewMapping = new ViewMapping();
    private SimpleCache<String,List<String>> cache = SimpleCache.make(20);

    protected DefaultViewMapper() {
        // no direct instantiate
    }

    /**
     * Add a model view mapping.
     */
    public void addViewMapping(String viewId, String beanName) {
        this.viewMapping.addMapping(viewId, beanName);
    }

    /**
     * Find a model list from the given view id.
     */
    public List<String> mapViewId(String viewId) {
        List<String> resultList = this.cache.get(viewId);
        if (resultList != null) {
            return resultList;
        }

        resultList = this.viewMapping.findMapping(viewId);
        for (int i = 0; i < resultList.size(); i++) {
            String beanName = resultList.get(i);
            if (beanName.indexOf("#{") != -1) {
                beanName = mapViewId(beanName, viewId);
                resultList.set(i, beanName);
            }
        }

        this.cache.put(viewId, resultList);
        return resultList;
    }

    private static final String FULL_VIEW_ID = "#{~view}";
    private static final String FULL_VIEW_ID_CAPITALIZE = "#{~View}";
    private static final String WILDCARD_VIEW_ID = "#{@view}";
    private static final String WILDCARD_VIEW_ID_CAPITALIZE = "#{@View}";
    private static final String VIEW_NAME_ONLY = "#{view}";
    private static final String VIEW_NAME_ONLY_CAPITALIZE = "#{View}";

    private String mapViewId(String pattern, String viewId) {
        // 1) remove leading '/'
        if (viewId.startsWith("/")) {
            viewId = viewId.substring(1);
        }

        // 2) remove suffix
        int dot = viewId.lastIndexOf('.');
        if (dot > viewId.lastIndexOf('/')) {
            viewId = viewId.substring(0, dot);
        }

        // returns the script path as-is
        if (pattern.startsWith("/")) {
            return pattern.replace(FULL_VIEW_ID, viewId)
                          .replace(VIEW_NAME_ONLY, viewId);
        }
        
        // 2) replace '/' to '.'
        viewId = viewId.replace('/', '.');

        // 3) remove '-' and capitalize first letter after '-'.
        int dash;
        while ((dash = viewId.indexOf('-')) != -1) {
            viewId = viewId.substring(0, dash) + capitalize(viewId.substring(dash+1));
        }

        int lastDot = viewId.lastIndexOf('.');
        if (pattern.indexOf(FULL_VIEW_ID) != -1) {
            return pattern.replace(FULL_VIEW_ID, viewId);
        } else if (pattern.indexOf(FULL_VIEW_ID_CAPITALIZE) != -1) {
            viewId = capitalize(viewId, lastDot+1);
            return pattern.replace(FULL_VIEW_ID_CAPITALIZE, viewId);
        } else if (pattern.indexOf(WILDCARD_VIEW_ID) != -1) {
            viewId = getWildcardViewId(viewId);
            return pattern.replace(WILDCARD_VIEW_ID, viewId);
        } else if (pattern.indexOf(WILDCARD_VIEW_ID_CAPITALIZE) != -1) {
            viewId = getWildcardViewId(viewId);
            lastDot = viewId.lastIndexOf('.');
            viewId = capitalize(viewId, lastDot+1);
            return pattern.replace(WILDCARD_VIEW_ID_CAPITALIZE, viewId);
        } else if (pattern.indexOf(VIEW_NAME_ONLY) != -1) {
            viewId = viewId.substring(lastDot+1);
            return pattern.replace(VIEW_NAME_ONLY, viewId);
        } else if (pattern.indexOf(VIEW_NAME_ONLY_CAPITALIZE) != -1) {
            viewId = capitalize(viewId.substring(lastDot+1));
            return pattern.replace(VIEW_NAME_ONLY_CAPITALIZE, viewId);
        } else {
            return pattern;
        }
    }

    private String getWildcardViewId(String viewId) {
        for (String wildward : viewMapping.wildcards) {
            wildward = wildward.replace('/', '.');
            if (wildward.startsWith(".")) {
                wildward = wildward.substring(1);
            }
            if (viewId.startsWith(wildward)) {
                return wildward;
            }
        }
        return viewId;
    }

    private static final String capitalize(String s) {
        if (s.length() > 0) {
            s = Character.toUpperCase(s.charAt(0)) + s.substring(1);
        }
        return s;
    }

    private static final String capitalize(String s, int i) {
    	if (s.length() == 1)
    		return s.toUpperCase();
    	
        if (i < s.length()-1) {
            s = s.substring(0, i) + Character.toUpperCase(s.charAt(i)) + s.substring(i+1);
        }
        return s;
    }
}
