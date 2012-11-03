package org.operamasks.faces.render.widget.yuiext;

public class PagingLinkRendererHelper {
	static final String PAGELINK_CSS = "/yuiext/css/paginglink.css";
	
	static String getThemeCssClass(String themeName){
		if("google".equalsIgnoreCase(themeName)){
			return "google-paginglink";
		}
		if("modern".equalsIgnoreCase(themeName)){
			return "modern-paginglink";
		}
		if("yahoo".equalsIgnoreCase(themeName)){
			return "yahoo-paginglink";
		}
		return "";
	}
}
