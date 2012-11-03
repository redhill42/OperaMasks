/*
 * $Id: DefaultNavigationHandler.java,v 1.5 2008/04/13 02:57:50 jacky Exp $
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

import javax.faces.context.FacesContext;
import javax.faces.application.ViewHandler;
import javax.faces.application.NavigationHandler;
import javax.faces.component.UIViewRoot;
import javax.faces.component.UIComponent;
import javax.faces.FacesException;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.TreeSet;
import java.util.Comparator;
import java.util.ArrayList;
import java.io.IOException;

import org.operamasks.faces.application.ApplicationAssociate;
import org.operamasks.faces.config.NavigationCase;
import org.operamasks.faces.component.ajax.AjaxUpdater;

public class DefaultNavigationHandler extends NavigationHandler
{
    private static final String KEY = DefaultNavigationHandler.class.getName();

    public DefaultNavigationHandler() {
        ApplicationAssociate.getInstance().setAttribute(KEY, this);
    }

    public static DefaultNavigationHandler getInstance() {
        return ApplicationAssociate.getInstance().getAttribute(KEY);
    }

    // maps of from view id to list of navigation cases, sorted in
    // descendent order so longest case get matched first
    private Map<String,List<NavigationCase>> navigationMap =
        new HashMap<String, List<NavigationCase>>();
    private TreeSet<String> wildcards = new TreeSet<String>(new DescOrder());

    private static class DescOrder implements Comparator<String> {
        public int compare(String a, String b) {
            return b.compareTo(a);
        }
    }

    public static final String VIEW_SCHEME = "view:";
    public static final String REDIRECT_SCHEME = "redirect:";

    /**
     * Add a navigation case.
     */
    public void addNavigationCase(NavigationCase navcase) {
        String fromViewId = navcase.getFromViewId();

        List<NavigationCase> navList = navigationMap.get(fromViewId);
        if (navList == null) {
            // add a new navigation case
            navList = new ArrayList<NavigationCase>();
            navList.add(navcase);
            navigationMap.put(fromViewId, navList);
        } else {
            // find a existing navigation case and replace it with new case
            boolean found = false;
            for (NavigationCase oldcase : navList) {
                if (eq(navcase.getFromAction(), oldcase.getFromAction()) &&
                    eq(navcase.getFromOutcome(), oldcase.getFromOutcome()))
                {
                    oldcase.setToViewId(navcase.getToViewId());
                    oldcase.setRedirect(navcase.isRedirect());
                    found = true;
                    break;
                }
            }
            if (!found) {
                navList.add(navcase);
            }
        }

        // add wildcard navigation case
        if (fromViewId.endsWith("*") && !fromViewId.equals("*")) {
            fromViewId = fromViewId.substring(0, fromViewId.length()-1);
            wildcards.add(fromViewId);
        }
    }

    /**
     * Find a navigation case from given action and outcome.
     */
    public NavigationCase findNavigationCase(String viewId, String action, String outcome) {
        NavigationCase result;

        result = findExactMatch(viewId, action, outcome);
        if (result == null) {
            result = findWildcardMatch(viewId, action, outcome);
            if (result == null) {
                result = findDefaultMatch(action, outcome);
            }
        }
        return result;
    }

    private NavigationCase findExactMatch(String viewId, String action, String outcome) {
        List<NavigationCase> navList = navigationMap.get(viewId);
        if (navList != null)
            return getViewFromActionOutcome(navList, action, outcome);
        return null;
    }

    private NavigationCase findWildcardMatch(String viewId, String action, String outcome) {
        for (String fromViewId : wildcards) {
            if (viewId.startsWith(fromViewId)) {
                List<NavigationCase> navList = navigationMap.get(fromViewId.concat("*"));
                if (navList != null) {
                    NavigationCase navcase = getViewFromActionOutcome(navList, action, outcome);
                    if (navcase != null) {
                        return navcase;
                    }
                }
            }
        }
        return null;
    }

    private NavigationCase findDefaultMatch(String action, String outcome) {
        List<NavigationCase> defaultNavList = navigationMap.get("*");
        if (defaultNavList != null)
            return getViewFromActionOutcome(defaultNavList, action, outcome);
        return null;
    }

    private NavigationCase getViewFromActionOutcome(List<NavigationCase> navList,
                                                    String action, String outcome)
    {
        for (NavigationCase navcase : navList) {
            String fromAction = navcase.getFromAction();
            String fromOutcome = navcase.getFromOutcome();

            if ((fromAction == null || fromAction.equals(action)) &&
                (fromOutcome == null || fromOutcome.equals(outcome)))
                return navcase;
        }
        return null;
    }

    private static final boolean eq(Object x, Object y) {
        if (x == null) {
            return y == null;
        } else if (y == null) {
            return false;
        } else {
            return x.equals(y);
        }
    }

    public void handleNavigation(FacesContext context, String fromAction, String outcome) {
        if (outcome == null) {
            return;
        }

        String renderId = AjaxUpdater.getRequestRenderId(context);
        boolean handled = false;

        // Handle partial navigation for an AJAX partial update request.
        if (renderId != null) {
            handled = handlePartialNavigation(context, renderId,
                                              fromAction, outcome,
                                              context.getViewRoot());
        }

        // If not a partial navigation then handle standard navigation.
        if (!handled) {
            handleStandardNavigation(context, fromAction, outcome);
        }
    }

    private void handleStandardNavigation(FacesContext context,
                                          String fromAction,
                                          String outcome)
    {
        String viewId = context.getViewRoot().getViewId();
        String toViewId = getToViewId(context, viewId, fromAction, outcome, true);

        if (toViewId != null) {
            ViewHandler vh = context.getApplication().getViewHandler();
            UIViewRoot newRoot = vh.createView(context, toViewId);
            context.setViewRoot(newRoot);
        }
    }

    private boolean handlePartialNavigation(FacesContext context,
                                            String renderId,
                                            String fromAction,
                                            String outcome,
                                            UIComponent component)
    {
        boolean handled = false;

        for (UIComponent child : component.getChildren()) {
            if (child instanceof AjaxUpdater) {
                AjaxUpdater updater = (AjaxUpdater)child;
                String id = updater.getRenderId();
                if (id == null || !id.equals(renderId)) {
                    continue;
                }

                String viewId = updater.getSubviewId();
                String contextPath = updater.getContext();
                String toViewId;

                if (viewId != null) {
                    // See if a context path specified...
                    if (contextPath != null) {
                        // Yes, then first try context based navigation rules...
                        toViewId = getToViewId(context, contextPath + viewId,
                                               fromAction, outcome, false);

                        if (toViewId != null) {
                            // Find the context view id, cut-off context path
                            // from to-view-id if necessary.
                            if (toViewId.startsWith(contextPath)) {
                                toViewId = toViewId.substring(contextPath.length());
                            }
                        } else {
                            // If no context based navigation rule was defined
                            // then find a match in local navigation rules.
                            toViewId = getToViewId(context, viewId,
                                                   fromAction, outcome, false);
                        }
                    } else {
                        // Find local navigation rules only if no context path specified.
                        toViewId = getToViewId(context, viewId, fromAction, outcome, false);
                    }

                    if (toViewId != null) {
                        updater.setSubviewId(toViewId);
                        updater.setNewView(true);
                        handled = true;
                    }
                }
            } else {
                handled |= handlePartialNavigation(context, renderId, fromAction, outcome, child);
            }
        }
        return handled;
    }

    private String getToViewId(FacesContext context,
                               String fromViewId,
                               String fromAction,
                               String outcome,
                               boolean handleRedirect)
    {
        String uri, query;
        String suffix = getDefaultSuffix(context);
        boolean redirect = false;

        // decode special "view:" scheme outcome
        if (outcome.startsWith(VIEW_SCHEME)) {
            uri = outcome.substring(VIEW_SCHEME.length());
            if (uri.startsWith(REDIRECT_SCHEME)) {
                uri = uri.substring(REDIRECT_SCHEME.length());
                redirect = true;
            }
        } else {
            uri = outcome;
        }

        // decode query string
        int q = uri.indexOf('?');
        if (q != -1) {
            query = uri.substring(q);
            uri = uri.substring(0, q);
        } else {
            query = null;
        }

        // find default navigation rule
        NavigationCase navcase = findNavigationCase(fromViewId, fromAction, uri);
        if (navcase != null) {
            if (handleRedirect && (redirect || navcase.isRedirect())) {
                redirect(context, navcase.getToViewId(), query);
                return null;
            } else {
                return navcase.getToViewId();
            }
        } else {
            if (outcome.startsWith(VIEW_SCHEME)) {
                if (!uri.endsWith(suffix)) {
                    uri = uri.concat(suffix);
                }
            } else if (isImplicitNavigation(context) && uri.endsWith(suffix)) {
                // Backward compatibility:
                // If navigation case was not found but the outcome is a valid
                // view identifier, that is, it's ends with JSF default suffix,
                // then treat outcome as toViewId.
            } else {
                return null;
            }
        }

        // resolve relative path
        if (!uri.startsWith("/")) {
            uri = fromViewId.substring(0, fromViewId.lastIndexOf('/')+1) + uri;
        }

        if (handleRedirect && redirect) {
            redirect(context, uri, query);
            return null;
        } else {
            return uri;
        }
    }

    private void redirect(FacesContext context, String viewId, String query) {
        try {
            ViewHandler vh = context.getApplication().getViewHandler();

            String url = vh.getActionURL(context, viewId);
            if (query != null) {
                url = url.concat(query);
            }

            context.responseComplete();
            context.getExternalContext().redirect(url);
        } catch (IOException ex) {
            throw new FacesException(ex);
        }
    }

    private boolean implicitNavigation;
    private boolean implicitNavigationSet;

    private static final String IMPLICIT_NAVIGATION_PARAM =
        "org.operamasks.faces.IMPLICIT_NAVIGATION";

    private boolean isImplicitNavigation(FacesContext context) {
        if (implicitNavigationSet) {
            return implicitNavigation;
        }

        String paramValue = context.getExternalContext().getInitParameter(IMPLICIT_NAVIGATION_PARAM);
        implicitNavigation = (paramValue == null) || (paramValue != null && paramValue.equalsIgnoreCase("true"));
        implicitNavigationSet = true;
        return implicitNavigation;
    }

    private String defaultSuffix;

    private String getDefaultSuffix(FacesContext context) {
        if (defaultSuffix == null) {
            defaultSuffix = context.getExternalContext()
                    .getInitParameter(ViewHandler.DEFAULT_SUFFIX_PARAM_NAME);
            if (defaultSuffix == null) {
                defaultSuffix = ViewHandler.DEFAULT_SUFFIX;
            }
        }
        return defaultSuffix;
    }
}
