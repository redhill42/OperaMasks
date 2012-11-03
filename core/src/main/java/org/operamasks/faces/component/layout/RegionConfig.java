/*
 * $Id: RegionConfig.java,v 1.12 2008/03/11 03:21:00 lishaochuan Exp $
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

package org.operamasks.faces.component.layout;

import org.operamasks.faces.component.widget.ExtConfig;

/**
 * @deprecated 此类已经废弃
 */
@Deprecated
public class RegionConfig extends ExtConfig
{
    private static final long serialVersionUID = -1436805164940337664L;

    private Region region;

    public boolean isBorder() {
        return get("border", false);
    }

    public void setBorder(boolean border) {
        set("border", border);
    }

    public boolean isSplit() {
        return get("split", false);
    }

    public void setSplit(boolean split) {
        set("split", split);
    }

    public int getInitialSize() {
        return get("initialSize", -1);
    }

    public void setInitialSize(int initialSize) {
        set("initialSize", initialSize);
    }

    public int getMinSize() {
        return get("minSize", -1);
    }

    public void setMinSize(int minSize) {
        set("minSize", minSize);
    }

    public int getMaxSize() {
        return get("maxSize", -1);
    }

    public void setMaxSize(int maxSize) {
        set("maxSize", maxSize);
    }

    /**
     * False to disable collapsing (defaults to true)
     */
    public boolean isCollapsible() {
        return get("collapsible", true);
    }

    public void setCollapsible(boolean collapsible) {
        set("collapsible", collapsible);
    }

    /**
     * True to start region collapsed. (defaults to false)
     */
    public boolean isCollapsed() {
        return get("collapsed", false);
    }

    public void setCollapsed(boolean collapsed) {
        set("collapsed", collapsed);
    }
    
    /**
     * False to disable floating (defaults to true)
     */
    public boolean isFloatable() {
        return get("floatable", true);
    }

    public void setFloatable(boolean floatable) {
        set("floatable", floatable);
    }

    /**
     * Margins for the element (defaults to {top: 0, left: 0, right: 0, bottom: 0})
     */
    public String getMargins() {
        return get("margins", null);
    }

    public void setMargins(String margins) {
        set("margins", margins);
    }

    /**
     * Margins for the element when collapsed (defaults to:
     * north/south {top:2, left:0, right:0, bottom:2} or
     * east/west {top:0, left:2, right:2, bottom:0})
     */
    public String getCmargins() {
        return get("cmargins", null);
    }

    public void setCmargins(String cmargins) {
        set("cmargins", cmargins);
    }

    /**
     * "top" or "bottom" (defaults to "bottom")
     */
    public String getTabPosition() {
        return get("tabPosition", "bottom");
    }

    public void setTabPosition(String tabPosition) {
        set("tabPosition", tabPosition);
    }

    /**
     * Optional string message to display in the collapsed block of a north or south region.
     */
    public String getCollapsedTitle() {
        return get("collapsedTitle", null);
    }

    public void setCollapsedTitle(String collapsedTitle) {
        set("collapsedTitle", collapsedTitle);
    }

    /**
     * True to always display tabs even when only 1 panel (defaults to false).
     */
    public boolean isAlwaysShowTabs() {
        return get("isAlwaysShowTabs", false);
    }

    public void setAlwaysShowTabs(boolean alwaysShowTabs) {
        set("isAlwaysShowTabs", alwaysShowTabs);
    }

    /**
     * True to enable overflow scrolling (defaults to false).
     */
    public boolean isAutoScroll() {
        return get("autoScroll", false);
    }

    public void setAutoScroll(boolean autoScroll) {
        set("autoScroll", autoScroll);
    }

    /**
     * True to display a title bar (defaults to false).
     */
    public boolean isTitlebar() {
        return get("titlebar", false);
    }

    public void setTitlebar(boolean titlebar) {
        set("titlebar", titlebar);
    }

    /**
     * The title for the region (overrides panel titles).
     */
    public String getTitle() {
        return get("title", null);
    }

    public void setTitle(String title) {
        set("title", title);
    }

    /**
     * True to animate expand/collapse (defaults to false).
     */
    public boolean isAnimate() {
        return get("animate", false);
    }

    public void setAnimate(boolean animate) {
        set("animate", animate);
    }

    /**
     * False to disable autoHide when the mouse leaves the "floated" region (defaults to true).
     */
    public boolean isAutoHide() {
        return get("autoHide", true);
    }

    public void setAutoHide(boolean autoHide) {
        set("autoHide", autoHide);
    }

    /**
     * True to preserve removed panels so they can be readded later (defaults to false).
     */
    public boolean isPreservePanels() {
        return get("preservePanels", false);
    }

    public void setPreservePanels(boolean preservePanels) {
        set("preservePanels", preservePanels);
    }

    /**
     * True to place the close icon on the tabs instead of the region titlebar
     * (defaults to false).
     */
    public boolean isCloseOnTab() {
        return get("closeOnTab", false);
    }

    public void setCloseOnTab(boolean closeOnTab) {
        set("closeOnTab", closeOnTab);
    }

    /**
     * True to hide the tab strip (defaults to false).
     */
    public boolean isHideTabs() {
        return get("hideTabs", false);
    }

    public void setHideTabs(boolean hideTabs) {
        set("hideTabs", hideTabs);
    }

    /**
     * True to enable automatic tab resizing. This will resize the tabs
     * so they are all the same size and fit within.
     */
    public boolean isResizeTabs() {
        return get("resizeTabs", false);
    }

    public void setResizeTabs(boolean resizeTabs) {
        set("resizeTabs", resizeTabs);
    }

    /**
     * The minimum tab width (defaults to 40).
     */
    public int getMinTabWidth() {
        return get("minTabWidth", 40);
    }

    public void setMinTabWidth(int minTabWidth) {
        set("minTabWidth", minTabWidth);
    }

    /**
     * The preferred tab width (defaults to 150).
     */
    public int getPreferredTabWidth() {
        return get("preferredTabWidth", 150);
    }

    public void setPreferredTabWidth(int preferredTabWidth) {
        set("preferredTabWidth", preferredTabWidth);
    }

    /**
     * True to show a pin button.
     */
    public boolean isShowPin() {
        return get("showPin", false);
    }

    public void setShowPin(boolean showPin) {
        set("showPin", showPin);
    }

    /**
     * True to start the region hidden.
     */
    public boolean isHidden() {
        return get("hidden", false);
    }

    public void setHidden(boolean hidden) {
        set("hidden", hidden);
    }

    /**
     * True to hide the region when it has no panels (defaults to true).
     */
    public boolean isHideWhenEmpty() {
        return get("hideWhenEmpty", false);
    }

    public void setHideWhenEmpty(boolean hideWhenEmpty) {
        set("hideWhenEmpty", hideWhenEmpty);
    }

    /**
     * True to disable tab tooltips.
     */
    public boolean isDisableTabTips() {
        return get("disableTabTips", false);
    }

    public void setDisableTabTips(boolean disableTabTips) {
        set("disableTabTips", disableTabTips);
    }
    
    protected void scriptOnStr(StringBuilder buf, String propName, String propValue) {
        // special property
        if (propName.equals("margin") || propName.equals("cmargin")) {
            buf.append(propValue);
        } else {
            super.scriptOnStr(buf, propName, propValue);
        }
    }

    public RegionConfig clone() {
        return (RegionConfig)super.clone();        
    }

    protected String getReplacedName(String name) {
        if ("initialSize".equals(name)) {
            return (region == Region.north || region == Region.south) ? "height" : "width";
        } else if ("titlebar".equals(name)) {
            return "header";
        } else if ("hidden".equals(name)) {
            return "collapsed";
        }
        return name;
    }

    public Region getRegion() {
        return region;
    }

    public void setRegion(Region region) {
        this.region = region;
    }
}
