/*
 * $Id: FaceletSetTag.java,v 1.12 2007/08/01 03:12:51 navy Exp $
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

package org.operamasks.faces.webapp.layout;

import javax.faces.webapp.UIComponentELTag;
import javax.faces.component.UIComponent;
import javax.faces.FacesException;
import javax.faces.context.FacesContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.el.ExpressionFactory;
import javax.el.ValueExpression;

import java.net.URL;
import java.io.File;
import java.io.InputStream;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ListIterator;

import org.operamasks.faces.component.layout.UIFaceletSet;
import org.operamasks.faces.component.layout.FaceletBean;
import org.operamasks.faces.layout.Facelet;
import org.operamasks.faces.layout.LayoutManager;
import org.operamasks.faces.layout.LayoutContext;
import org.operamasks.faces.util.FacesUtils;
import org.operamasks.util.SimpleCache;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * @jsp.tag name="faceletSet" body-content="JSP"
 * @jsp.attribute name="id" required="false" rtexprvalue="true"
 * @jsp.attribute name="rendered" required="false" type="boolean"
 * @jsp.attribute name="binding" required="false" type="javax.faces.component.UIComponent"
 */
public class FaceletSetTag extends UIComponentELTag
{
    private ValueExpression ref;
    private ValueExpression src;

    private List<String> parsedUris;

    public String getRendererType() {
        return null;
    }

    public String getComponentType() {
        return UIFaceletSet.COMPONENT_TYPE;
    }

    /**
     * @jsp.attribute type="java.lang.String"
     */
    public void setRef(ValueExpression ref) {
        this.ref = ref;
    }

    /**
     * @jsp.attribute type="java.lang.Object"
     * @param src
     */
    public void setSrc(ValueExpression src) {
        this.src = src;
    }

    public void release() {
        super.release();
        ref = null;
        src = null;
        parsedUris = null;
    }

    public int doEndTag() throws JspException {
        UIFaceletSet set = (UIFaceletSet)getComponentInstance();

        if (ref != null && src != null) {
            throw new FacesException("The 'src' and 'ref' attribute cannot be used together");
        }

        if (src != null) {
            addFaceletsFromSrc(set, src);
        }

        if (ref != null) {
            String refid = (String)ref.getValue(getFacesContext().getELContext());
            addFaceletsFromRef(set, refid);
        }

        // add facelets into enclosing LayoutManager
        UIComponent parent = set.getParent();
        if (parent instanceof LayoutManager) {
            ((LayoutManager)parent).getFacelets().addAll(set.getFacelets());
        }

        return super.doEndTag();
    }

    private void addFaceletsFromRef(UIFaceletSet set, String refid)
        throws JspException
    {
        UIComponent component = FacesUtils.getForComponent(getFacesContext(), refid, set);
        if (!(component instanceof UIFaceletSet)) {
            throw new JspTagException("Cannot find referenced facelet set of id " + refid);
        }

        UIFaceletSet refset = (UIFaceletSet)component;
        addAllFacelets(set, refset.getFacelets());
    }

    private void addFaceletsFromInherit(UIFaceletSet set)
        throws JspException
    {
        LayoutContext lctx = LayoutContext.getCurrentInstance();
        while (lctx != null) {
            LayoutManager layout = lctx.getLayoutManager();
            addAllFacelets(set, layout.getFacelets());
            lctx = lctx.getParent();
        }
    }

    private void addAllFacelets(UIFaceletSet set, List<Facelet> from) {
        List<Facelet> bak = new ArrayList<Facelet>(set.getFacelets());
        set.getFacelets().clear();
        set.getFacelets().addAll(from);
        for (Facelet facelet : bak) {
            String name = facelet.getName();
            int pos = -1;
            if (name != null && (pos = indexOfFaceletByName(set, name)) >= 0) {
                // replace the named facelet if the name already exists                
                set.getFacelets().remove(pos);
                set.getFacelets().add(pos, facelet);
            } else {                
                set.getFacelets().add(facelet);
            }
        }
    }

    private Facelet getFaceletByName(UIFaceletSet set, String name) {
        for (Facelet facelet : set.getFacelets()) {
            if (name.equals(facelet.getName())) {
                return facelet;
            }
        }
        return null;
    }
    
    private int indexOfFaceletByName(UIFaceletSet set, String name) {
        int i = 0;
        for (Facelet facelet : set.getFacelets()) {
            if (name.equals(facelet.getName())) {
                return i;
            }
            i++;
        }
        return -1;
    }

    private void addFaceletsFromSrc(UIFaceletSet set, ValueExpression src)
        throws JspException
    {
        Object srcObj = src.getValue(getFacesContext().getELContext());
        FaceletSetConfig config = null;

        // get FaceletSet from virous source
        if (srcObj instanceof FaceletBean[]) {
            config = new FaceletSetConfig((FaceletBean[])srcObj);
        } else if (srcObj instanceof Facelet[]) {
            config = new FaceletSetConfig((Facelet[])srcObj);
        } else if (srcObj instanceof List) {
            config = new FaceletSetConfig((List)srcObj);
        } else if (srcObj instanceof String) {
            String uri = (String)srcObj;
            if (uri.startsWith("#")) { // special syntax for reference
                if (uri.equals("#inherit")) {
                    addFaceletsFromInherit(set);
                } else {
                    addFaceletsFromRef(set, uri.substring(1));
                }
                return;
            } else {
                if (!uri.startsWith("/")) {
                    String sp = ((HttpServletRequest)pageContext.getRequest()).getServletPath();
                    uri = sp.substring(0, sp.lastIndexOf('/')+1) + uri;
                }
                config = getFaceletSetConfigFromURI(uri);
            }
        }

        if (config != null) {
            for (FaceletBean bean : config.getFaceletBeans()) {
                // don't add the named facelet if the name already exists
                String name = bean.getName();
                if (name == null || getFaceletByName(set, name) == null) {
                    createFacelet(bean);
                }
            }
        }
    }

    private void createFacelet(FaceletBean bean)
        throws JspException
    {
        // Use a FaceletTag to build component tree
        FaceletTag tag = new FaceletTag();
        tag.setPageContext(pageContext);
        tag.setParent(this);

        ExpressionFactory factory = getFacesContext().getApplication().getExpressionFactory();

        String name = bean.getName();
        if (name != null && name.length() != 0) {
            tag.setName(factory.createValueExpression(name, String.class));
        }

        Object value = bean.getValue();
        if (value != null) {
            if (value instanceof ValueExpression) {
                tag.setSrc((ValueExpression)value);
            } else {
                tag.setSrc(factory.createValueExpression(value, Object.class));
            }
        }

        String srcClass = bean.getSrcClass();
        if (srcClass != null && srcClass.length() != 0) {
            tag.setSrcClass(factory.createValueExpression(srcClass, String.class));
        }

        int rc = tag.doStartTag();
        
        if (rc != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
            String content = bean.getContent();
            if (content != null && content.length() != 0) {
                try {
                    BodyContent out = pageContext.pushBody();
                    tag.setBodyContent(out);
                    tag.doInitBody();
                    out.write(content);
                    tag.doAfterBody();
                    pageContext.popBody();
                } catch (IOException ex) { /*ignored*/ }
            }
        }

        tag.doEndTag();
        tag.release();
    }

    private static class FaceletSetConfig {
        private FaceletBean[] faceletBeans;
        private long lastModified;

        public FaceletSetConfig(FaceletBean[] faceletBeans) {
            this.faceletBeans = faceletBeans;
        }

        public FaceletSetConfig(Facelet[] facelets) {
            faceletBeans = new FaceletBean[facelets.length];
            for (int i = 0; i < facelets.length; i++) {
                FaceletBean bean = new FaceletBean();
                bean.setName(facelets[i].getName());
                bean.setValue(facelets[i]);
                faceletBeans[i] = bean;
            }
        }

        public FaceletSetConfig(List srcList) {
            faceletBeans = new FaceletBean[srcList.size()];
            for (int i = 0; i < faceletBeans.length; i++) {
                Object obj = srcList.get(i);
                if (obj instanceof FaceletBean) {
                    faceletBeans[i] = (FaceletBean)obj;
                } else if (obj instanceof Facelet) {
                    FaceletBean bean = new FaceletBean();
                    bean.setName(((Facelet)obj).getName());
                    bean.setValue(obj);
                    faceletBeans[i] = bean;
                } else {
                    throw new IllegalArgumentException();
                }
            }
        }

        public FaceletBean[] getFaceletBeans() {
            return faceletBeans;
        }

        public void setFaceletBeans(FaceletBean[] faceletBeans) {
            this.faceletBeans = faceletBeans;
        }

        public long getLastModified() {
            return lastModified;
        }

        public void setLastModified(long lastModified) {
            this.lastModified = lastModified;
        }
    }

    private static final String FACELET_SET_CONFIG_ATTR =
        "org.operamasks.faces.layout.FACELET_SET_CONFIG";

    @SuppressWarnings("unchecked")
    private SimpleCache<String,FaceletSetConfig> getFaceletSetConfigCache() {
        SimpleCache<String,FaceletSetConfig> cache = (SimpleCache<String,FaceletSetConfig>)
            pageContext.getServletContext().getAttribute(FACELET_SET_CONFIG_ATTR);

        if (cache == null) {
            cache = new SimpleCache<String,FaceletSetConfig>(50);
            pageContext.getServletContext().setAttribute(FACELET_SET_CONFIG_ATTR, cache);
        }

        return cache;
    }

    private FaceletSetConfig getFaceletSetConfigFromURI(String uri)
        throws JspException
    {
        long lastModified = -1;

        try {
            URL url = pageContext.getServletContext().getResource(uri);
            if (url == null) {
                throw new FileNotFoundException(uri);
            }
            if (url.getProtocol().equalsIgnoreCase("file")) {
                File f = new File(url.getFile());
                lastModified = f.lastModified();
            }
        } catch (IOException ex) {
            throw new JspException(ex);
        }

        SimpleCache<String,FaceletSetConfig> cache = getFaceletSetConfigCache();
        FaceletSetConfig config = cache.get(uri);

        if (config == null || lastModified == -1 || config.getLastModified() != lastModified) {
            config = parseFaceletSetConfig(uri);
            if (config != null) {
                config.setLastModified(lastModified);
                cache.put(uri, config);
            }
        }

        return config;
    }

    private FaceletSetConfig parseFaceletSetConfig(String uri)
        throws JspException
    {
        if (parsedUris == null) {
            parsedUris = new ArrayList<String>();
        } else if (parsedUris.contains(uri)) {
            throw new JspException("Circular facelet-set reference: " + uri);
        }
        parsedUris.add(uri);
        
        Document doc;
        try {
            InputStream stream = pageContext.getServletContext().getResourceAsStream(uri);
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setValidating(false);
            DocumentBuilder builder = dbf.newDocumentBuilder();
            doc = builder.parse(stream, uri);
        } catch (Exception ex) {
            throw new JspException(ex);
        }

        Element root = doc.getDocumentElement();
        NodeList elements = root.getElementsByTagName("facelet");
        List<FaceletBean> beans = new ArrayList<FaceletBean>();
        List<FaceletBean> baseBeans = null;
        FacesContext context = getFacesContext();

        // Load facelet set from base config file
        String base = root.getAttribute("src");
        if (base != null && base.length() != 0) {
            if (!base.startsWith("/")) {
                base = uri.substring(0, uri.lastIndexOf('/')+1) + base;
            }
            FaceletSetConfig baseConfig = getFaceletSetConfigFromURI(base);
            if (baseConfig != null) {
                baseBeans = Arrays.asList(baseConfig.getFaceletBeans());
                beans.addAll(baseBeans);
            }
        }

        // Load facelet set from list of facelet elements
        for (int i = 0; i < elements.getLength(); i++) {
            Element el = (Element)elements.item(i);
            FaceletBean bean = new FaceletBean();

            String name = el.getAttribute("name");
            boolean replaced = false;
            if (name != null && name.length() != 0) {
                bean.setName(name);

                // replace facelet bean from base config of the same name
                if (baseBeans != null) {
                    ListIterator<FaceletBean> itr = beans.listIterator();
                    while (itr.hasNext()) {
                        FaceletBean baseBean = itr.next();
                        if (name.equals(baseBean.getName()) && baseBeans.contains(baseBean)) {
                            itr.set(bean);
                            replaced = true;
                            break;
                        }
                    }
                }
            }
            if (!replaced) {
                beans.add(bean);
            }

            String src = el.getAttribute("src");
            if (src != null && src.length() != 0) {
                if (src.startsWith("#{") && src.endsWith("}")) {
                    ValueExpression value =
                        context.getApplication()
                               .getExpressionFactory()
                               .createValueExpression(context.getELContext(), src, Object.class);
                    bean.setValue(value);
                } else {
                    bean.setValue(src);
                }
            }

            String srcClass = el.getAttribute("srcClass");
            if (srcClass != null && srcClass.length() != 0) {
                bean.setSrcClass(srcClass);
            }

            String content = el.getTextContent();
            if (content != null && content.length() != 0) {
                bean.setContent(content);
            }
        }

        return new FaceletSetConfig(beans);
    }
}
