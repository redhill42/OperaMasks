/*
 * $Id: YuiExtResource.java,v 1.28 2008/01/28 14:00:25 yangdong Exp $
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

package org.operamasks.faces.render.widget.yuiext;

import javax.faces.context.ResponseWriter;
import javax.faces.context.FacesContext;
import javax.faces.component.UIComponent;
import javax.faces.FacesException;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.operamasks.faces.debug.Debug;
import org.operamasks.faces.debug.DebugMode;
import org.operamasks.faces.render.resource.AbstractResource;
import org.operamasks.faces.render.resource.SkinManager;
import org.operamasks.faces.render.resource.ResourceManager;
import org.operamasks.faces.render.resource.SkinDescriptor;
import org.operamasks.faces.render.ajax.AjaxHtmlResponseWriter;
import org.operamasks.faces.render.ajax.AjaxResponseWriter;
import org.operamasks.faces.util.FacesUtils;

public class YuiExtResource extends AbstractResource
{
    public static final String RESOURCE_ID = "urn:yui-ext";

    public static final String EXT_BASE_JS = "/ext/ext-base.js";
    public static final String EXT_CORE_JS = "/ext/ext-core.js";
    public static final String EXT_ALL_JS = "/ext/ext-all.js";
    public static final String EXT_LANG_JS = "/ext/locale/ext-lang.js";
    public static final String EXT_ALL_CSS = "/yuiext/css/ext-all.css";
    public static final String EXT_EXTRA_CSS = "/yuiext/css/ext-extra.css";

    private static final String BLANK_IMAGE_URL = "/ext/s.gif";

    private static final Map<String,String[]> pkg2uris;
    private static Map<String, String> debugScriptsCache;

    static {
        try {
            URL url = YuiExtResource.class.getResource("ext-packages.xml");
            InputStream stream = url.openStream();
            pkg2uris = ExtPackages.load(stream);
            stream.close();
        } catch (Exception ex) {
            throw new Error(ex);
        }
    }

    private List<String> dependencies = new ArrayList<String>();
    private boolean beginEncoded = false;

    private StringBuffer initScript = new StringBuffer();
    private List<String> variables = new ArrayList<String>();
    private List<String> tempvars = new ArrayList<String>();
    private int maxTempVar = 0;

    private static final String TEMP_VAR_PREFIX = "_$";

    public YuiExtResource() {
        super(RESOURCE_ID);
    }

    public void addPackageDependency(String name) {
        // Get the resource URIs that needed by the give package.
        String[] uris = pkg2uris.get(name);
        if (uris == null) {
            throw new IllegalArgumentException("Unknown package: " + name);
        }

        if (!beginEncoded) {
            // Add the dependency resource URIs, duplicate URI is eliminated.
            for (String uri : uris) {
                if (!dependencies.contains(uri)) {
                    dependencies.add(uri);
                }
            }
        } else {
            // Encode depenency resources immediately because page begin already rendered
            List<String> deps = new ArrayList<String>();
            for (String uri : uris) {
                if (!dependencies.contains(uri)) {
                    deps.add(uri);
                }
            }

            try {
                FacesContext context = FacesContext.getCurrentInstance();
                ResourceManager rm = ResourceManager.getInstance(context);

                // If ext-all.js is encoded then no other packages need to encode.
                if (!dependencies.contains(EXT_ALL_JS)) {
                    if (deps.contains(EXT_ALL_JS)) {
                        encodeAllScript(context, rm);
                        dependencies.clear();
                        dependencies.add(EXT_ALL_JS);
                    } else {
                        if (dependencies.isEmpty()) {
                            encodeScriptByUri(context, EXT_CORE_JS);
                        }
                        for (String uri : deps) {
                            encodeScriptByUri(context, uri);
                            dependencies.add(uri);
                        }
                    }
                }
            } catch (IOException ex) {
                throw new FacesException(ex);
            }
        }
    }

    public void addVariable(String var) {
        variables.add(var);
    }

    public String allocVariable(UIComponent component) {
        String var = (String)component.getAttributes().get("jsvar");
        if (var == null) {
            // jsvar is commonly needed for AJAX responses
            FacesContext context = FacesContext.getCurrentInstance();
            ResponseWriter resp = context.getResponseWriter();
            if ((resp instanceof AjaxHtmlResponseWriter) || (resp instanceof AjaxResponseWriter)) {
                var = FacesUtils.getJsvar(context, component);
            }
        }

        if (var != null) {
            addVariable(var);
        } else {
            var = allocTempVariable();
        }
        return var;
    }

    public String allocTempVariable() {
        if (!tempvars.isEmpty()) {
            return tempvars.remove(0);
        } else {
            return TEMP_VAR_PREFIX + (maxTempVar++);
        }
    }

    public boolean isTempVariable(String var) {
        return var.startsWith(TEMP_VAR_PREFIX);
    }
    
    public void releaseVariable(String var) {
        if (var.startsWith(TEMP_VAR_PREFIX)) {
            tempvars.add(var);
        }
    }

    public void addInitScript(String script) {
        initScript.append(script);
    }

    @Override
    public void encodeBegin(FacesContext context)
        throws IOException
    {
        beginEncoded = true;

        ResourceManager rm = ResourceManager.getInstance(context);
        String skin = SkinManager.getCurrentSkin(context);
        ResponseWriter out = context.getResponseWriter();

        // Required Ext scripts
        encodeScriptByUri(context, EXT_BASE_JS);

        // Encode package dependency resources
        if (dependencies.contains(EXT_ALL_JS)) {
            // encode ext-all.js that includes all other packages
            encodeAllScript(context, rm);
        } else {
            // encode individual package scripts
            encodeScriptByUri(context, EXT_CORE_JS);
            for (String dep : dependencies) {
                encodeScriptByUri(context, dep);
            }
        }

        // Set BLANK_IMAGE_URL
        out.startElement("script", null);
        out.writeAttribute("type", "text/javascript", null);
        out.write("Ext.BLANK_IMAGE_URL=\"" + rm.getResourceURL(BLANK_IMAGE_URL) + "\";");
        out.endElement("script");
        out.write("\n");

        // Default Ext stylesheet
        encodeCss(context, rm.getSkinResourceURL(skin, EXT_ALL_CSS), "x-skin");
        encodeCss(context, rm.getSkinResourceURL(skin, EXT_EXTRA_CSS), "x-skin");

        // Extra files
        SkinDescriptor skinDesc = SkinManager.getInstance(context).getSkin(skin);
        if (skinDesc != null) {
            String extraFiles = skinDesc.getProperty("yuiext.files");
            if (extraFiles != null) {
                for (String file : extraFiles.split(",")) {
                    file = file.trim();
                    if (file.endsWith(".js")) {
                        encodeScriptByUri(context, rm.getSkinResourceURL(skin, file));
                    } else if (file.endsWith(".css")) {
                        encodeCss(context, rm.getSkinResourceURL(skin, file), "x-skin");
                    }
                }
            }
        }
    }

    @Override
    public void encodeEnd(FacesContext context)
        throws IOException
    {        
        // lastest out put Ext localization resource, to be sure all the js file is imported.
        ResourceManager rm = ResourceManager.getInstance(context);
        Locale locale = context.getViewRoot().getLocale();
        encodeScript(context, rm.getResourceURL(EXT_LANG_JS, locale));

        if (variables.size() != 0 || initScript.length() != 0) {
            ResponseWriter out = context.getResponseWriter();
            if (out instanceof AjaxResponseWriter) {
                String script = encodeAjaxInitScript();
                ((AjaxResponseWriter)out).writeActionScript(script);
            } else {
                String script = encodeInitScript();
                out.write(script);
            }
        }
    }

    private String encodeInitScript() {
        StringBuilder buf = new StringBuilder();

        buf.append("<script type=\"text/javascript\">\n<!--\n");
        buf.append("if(Ext && Ext.QuickTips)Ext.QuickTips.init();\n");

        if (variables.size() != 0) {
            buf.append("var ");
            for (int i = 0; i < variables.size(); i++) {
                if (i != 0) buf.append(",");
                buf.append(variables.get(i));
            }
            buf.append(";\n");
        }

        if (initScript.length() != 0) {
            buf.append("Ext.onReady(function(){\n");
            
            // Add an template element as first element for document.body
            buf.append("\ndocument.body.insertBefore(document.createElement('style'), document.body.firstChild );\n");

            if (maxTempVar > 0) {
                buf.append("var ");
                for (int i = 0; i < maxTempVar; i++) {
                    if (i != 0) buf.append(",");
                    buf.append(TEMP_VAR_PREFIX).append(i);
                }
                buf.append(";\n");
            }
            buf.append(initScript);
            buf.append("\n});\n");
        }

        buf.append("//-->\n</script>\n");
        return buf.toString();
    }

    private String encodeAjaxInitScript() {
        StringBuilder buf = new StringBuilder();

        if (initScript.length() != 0) {
            buf.append("(function(){");
            
            // IE isn't compatible with ECMAScript very well. So we need define a global
            // variable by such a statement: window['VAR_NAME'] = null;
            if (variables != null && variables.size() > 0) {
            	for (String var : variables) {
            		buf.append("\nwindow['" + var + "'] = null;");
            	}
            }
            
            if (maxTempVar > 0) {
                buf.append("var ");
                for (int i = 0; i < maxTempVar; i++) {
                    if (i != 0) buf.append(",");
                    buf.append(TEMP_VAR_PREFIX).append(i);
                }
                buf.append(";");
            }
            buf.append(initScript);
            buf.append("})();");
        }

        return buf.toString();
    }

    public static YuiExtResource register(ResourceManager rm, String... dependencies) {
        YuiExtResource resource = (YuiExtResource)rm.getRegisteredResource(RESOURCE_ID);

        if (resource == null) {
            resource = new YuiExtResource();
            rm.registerResource(resource);
        }

        if (dependencies != null) {
            for (String pkg : dependencies) {
                resource.addPackageDependency(pkg);
            }
        }

        return resource;
    }

    private static void encodeScriptByUri(FacesContext context, String uri)
        throws IOException
    {
    	// In debug mode, trying to replace compressed script to it's debug version.
    	if (Debug.isEnabled(DebugMode.UNCOMPRESSED_JS) && uri.endsWith(".js")) {
    		if (debugScriptsCache == null)
    			debugScriptsCache = new HashMap<String, String>();
    		
    		String debugScriptUri = debugScriptsCache.get(uri);
    		
    		if (debugScriptUri == null) {
        		int dotIndex = uri.lastIndexOf(".");
        		debugScriptUri = uri.substring(0, dotIndex) + "-debug" + uri.substring(dotIndex);
        		
        		if (YuiExtResource.class.getResource("/META-INF/resource" + debugScriptUri) == null) {
        			debugScriptUri = uri;
        		}
        		
        		debugScriptsCache.put(uri, debugScriptUri);
    		}
    		
    		uri = debugScriptUri;
    	}
    	
        encodeScript(context, ResourceManager.getInstance(context).getResourceURL(uri));
    }

	private static void encodeScript(FacesContext context, String url)
			throws IOException {
		ResponseWriter out = context.getResponseWriter();
        if (out instanceof AjaxResponseWriter) {
            ((AjaxResponseWriter)out).writeScript("OM.ajax.loadScript('" + url + "');\n");
        } else {
            out.startElement("script", null);
            out.writeAttribute("type", "text/javascript", null);
            out.writeAttribute("src", url, null);
            out.endElement("script");
            out.write("\n");
        }
	}

    private static void encodeAllScript(FacesContext context, ResourceManager rm)
        throws IOException
    {
        for (String uri : pkg2uris.get("All")) {
            encodeScriptByUri(context, uri);
        }
    }

    private static void encodeCss(FacesContext context, String url, String cls)
        throws IOException
    {
        ResponseWriter out = context.getResponseWriter();
        if (out instanceof AjaxResponseWriter) {
            ((AjaxResponseWriter)out).writeScript(
                "OM.ajax.loadStylesheet('" + url + "',null," +
                (cls == null ? "null" : "'"+cls+"'") + ");\n"
            );
        } else {
            out.startElement("link", null);
            if (cls != null)
                out.writeAttribute("class", cls, null);
            out.writeAttribute("rel", "stylesheet", null);
            out.writeAttribute("type", "text/css", null);
            out.writeAttribute("href", url, null);
            out.endElement("link");
            out.write("\n");
        }
    }
}
