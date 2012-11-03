/*
 * $Id: HtmlRenderer.java,v 1.10 2008/03/24 05:21:49 patrick Exp $
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

package org.operamasks.faces.render.html;

import javax.faces.render.Renderer;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.component.UIForm;
import javax.faces.component.UICommand;

import org.operamasks.el.eval.Coercion;
import org.operamasks.faces.render.ajax.AjaxResponseWriter;
import org.operamasks.faces.render.ajax.AjaxHtmlResponseWriter;
import org.operamasks.faces.render.resource.ResourceManager;
import org.operamasks.faces.render.resource.AbstractResource;

import java.util.Map;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;
import java.io.IOException;

public abstract class HtmlRenderer extends Renderer
{
    protected static Logger log = Logger.getLogger("org.operamasks.faces.render");
    // Utility methods

    /**
     * Find the enclosing UIForm of the given UIComponent.
     */
    public static UIForm getParentForm(UIComponent component) {
        while (component != null) {
            if (component instanceof UIForm)
                return (UIForm)component;
            component = component.getParent();
        }
        return null;
    }

    /**
     * Get the current action URL.
     */
    public static String getActionURL(FacesContext context) {
        String viewId = context.getViewRoot().getViewId();
        String url = context.getApplication().getViewHandler().getActionURL(context, viewId);
        return context.getExternalContext().encodeActionURL(url);
    }

    private static final String SUBMIT_FUNCTION_NAME = "_OM_submit";

    private static final String SUBMIT_FUNCTION =
        "function " + SUBMIT_FUNCTION_NAME +"(id,params,target){\n" +
        "var f = document.forms[id];" +
        "var t = f.target;" +
        "var ap = new Array();" +
        "if (target){" +
           "f.target = target;" +
        "}" +
        "for (var i=0; i < params.length; i+=2){" +
           "var p = document.createElement('input');" +
           "p.type = 'hidden';" +
           "p.name = params[i];" +
           "p.value = params[i+1];" +
           "f.appendChild(p);" +
           "ap.push(p);" +
        "}" +
        "try{" +
           "f.submit();" +
        "}finally{" +
           "f.target = t;" +
           "for (var i = 0; i < ap.length; i++){" +
               "f.removeChild(ap[i]);" +
           "}" +
        "}}";

    private static void encodeSubmitFunction(FacesContext context) {
        ResourceManager rm = ResourceManager.getInstance(context);
        rm.registerResource(new AbstractResource("urn:function:submit_form") {
            public void encodeEnd(FacesContext context) throws IOException {
                ResponseWriter out = context.getResponseWriter();
                out.startElement("script", null);
                out.writeAttribute("type", "text/javascript", null);
                out.writeAttribute("language", "Javascript", null);
                out.write(SUBMIT_FUNCTION);
                out.endElement("script");
            }
        });
    }

    /**
     * Encode the form submit.
     */
    public static String encodeSubmit(FacesContext context,
                                      UIForm form,
                                      String target,
                                      String... params)
    {
        encodeSubmitFunction(context);

        StringBuilder buf = new StringBuilder();

        buf.append(SUBMIT_FUNCTION_NAME);
        buf.append("('").append(form.getClientId(context)).append("',[");
        for (int i = 0; i < params.length; i += 2) {
            if (i > 0)
                buf.append(",");
            buf.append(params[i]).append(",").append(params[i+1]);
        }
        buf.append("]");
        if (target != null) {
            buf.append(",'").append(target).append("'");
        }
        buf.append(");");
        return buf.toString();
    }

    /**
     * Encode the AJAX form submit.
     */
    public static String encodeAjaxSubmit(FacesContext context,
                                          UIComponent component,
                                          String... params)
    {
        UIForm form = getParentForm(component);
        boolean immediate = (component instanceof UICommand)
                                ? ((UICommand)component).isImmediate()
                                : false;

        StringBuilder buf = new StringBuilder();

        buf.append("OM.ajax.submit(");
        if (form != null || params.length != 0 || immediate) {
            if (form != null) {
                buf.append("'").append(form.getClientId(context)).append("',");
            } else {
                buf.append("null,");
            }
            buf.append("null,");
            if (params.length == 0) {
                buf.append("null,");
            } else {
                buf.append("{");
                for (int i = 0; i < params.length; i += 2) {
                    if (i > 0) buf.append(",");
                    buf.append(params[i]);
                    buf.append(":");
                    buf.append(params[i+1]);
                }
                buf.append("},");
            }
            buf.append(immediate);
        }
        buf.append(");");

        return buf.toString();
    }

    public static boolean isDisabled(UIComponent component) {
        Object disabled = component.getAttributes().get("disabled");
        if (disabled != null) {
            if (disabled instanceof String) {
                return ((String)disabled).equalsIgnoreCase("true");
            } else {
                return disabled.equals(Boolean.TRUE);
            }
        }
        return false;
    }

    public static boolean isReadonly(UIComponent component) {
        Object readonly = component.getAttributes().get("readonly");
        if (readonly != null) {
            if (readonly instanceof String) {
                return ((String)readonly).equalsIgnoreCase("true");
            } else {
                return readonly.equals(Boolean.TRUE);
            }
        }
        return false;
    }

    public static boolean isDisabledOrReadonly(UIComponent component) {
        return isDisabled(component) ||  isReadonly(component);
    }

    public static boolean needsEscape(UIComponent component) {
        Object escape = component.getAttributes().get("escape");
        if (escape != null) {
            if (escape instanceof String) {
                return ((String)escape).equalsIgnoreCase("true");
            } else {
                return escape.equals(Boolean.TRUE);
            }
        }
        return false;
    }

    protected boolean isAjaxResponse(FacesContext context) {
        return (context.getResponseWriter() instanceof AjaxResponseWriter);
    }

    protected boolean isAjaxHtmlResponse(FacesContext context) {
        return (context.getResponseWriter() instanceof AjaxHtmlResponseWriter);
    }

    protected boolean shouldWriteIdAttribute(FacesContext context, UIComponent component) {
        if (component.isTransient()) {
            // no ID for verbatim
            return false;
        }

        if (isAjaxResponse(context) || isAjaxHtmlResponse(context)) {
            // ID is required for AJAX response
            return true;
        }

        String id = component.getId();
        if (id != null && !id.startsWith(UIViewRoot.UNIQUE_ID_PREFIX)) {
            // render ID if explicitly specified
            return true;
        }

        return false;
    }

    protected boolean writeIdAttributeIfNecessary(FacesContext context,
                                                  ResponseWriter writer,
                                                  UIComponent component)
        throws IOException
    {
        if (shouldWriteIdAttribute(context, component)) {
            String id = component.getClientId(context);
            writer.writeAttribute("id", id, "clientId");
            return true;
        } else {
            return false;
        }
    }

    /**
     * The table of pass through attributes. The first column in the table is the
     * attribute name from UIComponent. The second column in the table is the
     * attribute name of HTML markup to be rendererd. If a row has a third column
     * then indicates a boolean attribute and column value is ignored.
     */
    private static final String[][] PASSTHRU_ATTRIBUTES = {
        { "accept",         "accept"         },
        { "acceptcharset",  "accept-charset" },
        { "accesskey",      "accesskey"      },
        { "align",          "align"          },
        { "alt",            "alt"            },
        { "bgcolor",        "bgcolor"        },
        { "border",         "border"         },
        { "cellpadding",    "cellpadding"    },
        { "cellspacing",    "cellspacing"    },
        { "charset",        "charset"        },
        { "cols",           "cols"           },
        { "colspan",        "colspan"        },
        { "coords",         "coords"         },
        { "dir",            "dir"            },
        { "disabled",       "disabled",      "disabled" },
        { "enctype",        "enctype"        },
        { "frame",          "frame"          },
        { "height",         "height"         },
        { "hreflang",       "hreflang"       },
        { "ismap",          "ismap",         "ismap" },
        { "lang",           "lang"           },
        { "longdesc",       "longdesc"       },
        { "maxlength",      "maxlength"      },
        { "onblur",         "onblur"         },
        { "onchange",       "onchange"       },
        { "onclick",        "onclick"        },
        { "ondblclick",     "ondblclick"     },
        { "onfocus",        "onfocus"        },
        { "onkeydown",      "onkeydown"      },
        { "onkeypress",     "onkeypress"     },
        { "onkeyup",        "onkeyup"        },
        { "onload",         "onload"         },
        { "onmousedown",    "onmousedown"    },
        { "onmousemove",    "onmousemove"    },
        { "onmouseout",     "onmouseout",    },
        { "onmouseover",    "onmouseover"    },
        { "onmouseup",      "onmouseup"      },
        { "onreset",        "onreset"        },
        { "onselect",       "onselect"       },
        { "onsubmit",       "onsubmit"       },
        { "onunload",       "onunload"       },
        { "readonly",       "readonly",      "readonly" },
        { "rel",            "rel"            },
        { "rev",            "rev"            },
        { "rows",           "rows"           },
        { "rowspan",        "rowspan"        },
        { "rules",          "rules"          },
        { "shape",          "shape"          },
        { "size",           "size"           },
        { "style",          "style"          },
        { "styleClass",     "class"          },
        { "summary",        "summary"        },
        { "tabindex",       "tabindex"       },
        { "target",         "target"         },
        { "title",          "title"          },
        { "usemap",         "usemap"         },
        { "valign",         "valign"         },
        { "width",          "width"          },
    };

    private static ConcurrentHashMap<String,String[][]> passthruAttributesWithExclusion =
        new ConcurrentHashMap<String,String[][]>();

    /**
     * Build a pass through attribute table by removing
     * excluded attribute names. Allows cache result table.
     */
    private static String[][] getPassThruAttributes(String exclusion) {
        if (exclusion == null)
            return PASSTHRU_ATTRIBUTES;

        String[][] passthruAttributes = passthruAttributesWithExclusion.get(exclusion);
        if (passthruAttributes == null) {
            String[] excludes = exclusion.split(",");
            passthruAttributes = new String[PASSTHRU_ATTRIBUTES.length - excludes.length][];
            int next = 0;
            for (int i = 0; i < PASSTHRU_ATTRIBUTES.length; i++) {
                String name = PASSTHRU_ATTRIBUTES[i][0];
                boolean skip = false;
                for (int j = 0; j < excludes.length; j++) {
                    if (name.equals(excludes[j])) {
                        skip = true;
                        break;
                    }
                }
                if (!skip) {
                    passthruAttributes[next++] = PASSTHRU_ATTRIBUTES[i];
                }
            }
            assert next == passthruAttributes.length;
            passthruAttributesWithExclusion.putIfAbsent(exclusion, passthruAttributes);
        }
        return passthruAttributes;
    }

    public static boolean hasPassThruAttributes(UIComponent component) {
        return hasPassThruAttributes(component, null);
    }

    public static boolean hasPassThruAttributes(UIComponent component, String exclusion) {
        if (component == null)
            return false;

        Map<String,Object> attrs = component.getAttributes();
        for (String[] attribute : getPassThruAttributes(exclusion)) {
            Object value = attrs.get(attribute[0]);
            if (value != null && !"".equals(value)) {
                return true;
            }
        }
        return false;
    }

    public static void renderPassThruAttributes(ResponseWriter writer, UIComponent component)
        throws IOException
    {
        renderPassThruAttributes(writer, component, null);
    }

    public static void renderPassThruAttributes(ResponseWriter writer, UIComponent component, String exclusion)
        throws IOException
    {
        for (String[] attribute : getPassThruAttributes(exclusion)) {
            Object value = component.getAttributes().get(attribute[0]);
            if (value != null) {
                if (attribute.length == 3) {
                    // render a boolean attribute
                    Boolean result;
                    if (value instanceof Boolean) {
                        result = ((Boolean)value);
                    } else {
                        result = Boolean.valueOf(value.toString());
                    }
                    writer.writeAttribute(attribute[1], result, attribute[0]);
                } else if (shouldRenderAttribute(value)) {
                    writer.writeAttribute(attribute[1], value.toString(), attribute[0]);
                }
            }
        }
    }

    private static boolean shouldRenderAttribute(Object attributeValue) {
        switch (Coercion.typeof(attributeValue)) {
        case Coercion.BOOLEAN_BOXED_TYPE:
            return ((Boolean)attributeValue).booleanValue() != false;
        case Coercion.BYTE_BOXED_TYPE:
            return ((Byte)attributeValue).byteValue() != Byte.MIN_VALUE;
        case Coercion.CHAR_BOXED_TYPE:
            return ((Character)attributeValue).charValue() != 0;
        case Coercion.SHORT_BOXED_TYPE:
            return ((Short)attributeValue).shortValue() != Short.MIN_VALUE;
        case Coercion.INT_BOXED_TYPE:
            return ((Integer)attributeValue).intValue() != Integer.MIN_VALUE;
        case Coercion.LONG_BOXED_TYPE:
            return ((Long)attributeValue).longValue() != Long.MIN_VALUE;
        case Coercion.FLOAT_BOXED_TYPE:
            return ((Float)attributeValue).floatValue() != Float.MIN_VALUE;
        case Coercion.DOUBLE_BOXED_TYPE:
            return ((Double)attributeValue).doubleValue() != Double.MIN_VALUE;
        default:
            return true;
        }
    }

    private static final String[] EMPTY_ELEMENTS = {
        "area",
        "base",
        "basefont",
        "br",
        "col",
        "frame",
        "hr",
        "img",
        "input",
        "isindex",
        "link",
        "meta",
        "param"
    };

    public static boolean isEmptyElement(String name) {
        return Arrays.binarySearch(EMPTY_ELEMENTS, name.toLowerCase()) >= 0;
    }
}
