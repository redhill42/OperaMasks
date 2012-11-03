/*
 * $Id: ELiteBeanCache.java,v 1.6 2008/01/31 04:12:24 daniel Exp $
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

package org.operamasks.faces.binding.impl;

import java.net.URL;
import java.io.IOException;
import java.io.Reader;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.util.Map;
import javax.el.ValueExpression;
import javax.el.VariableMapper;
import javax.faces.context.FacesContext;

import elite.lang.Closure;
import org.operamasks.el.parser.Parser;
import org.operamasks.el.parser.ResourceResolver;
import org.operamasks.el.eval.ELProgram;
import org.operamasks.el.eval.VariableMapperImpl;
import org.operamasks.el.eval.ELContextImpl;
import org.operamasks.faces.application.ApplicationAssociate;
import org.operamasks.util.SimpleCache;

final class ELiteBeanCache
{
    public static ELiteBeanCache getInstance() {
        return ApplicationAssociate.getInstance().getSingleton(ELiteBeanCache.class);
    }

    private static final String SCRIPT_KEY = ELiteBeanCache.class.getName() + ":";

    private static class CacheEntry {
        ELProgram prog;
        long lastmod;

        CacheEntry(ELProgram prog, long lastmod) {
            this.prog = prog;
            this.lastmod = lastmod;
        }
    }

    private SimpleCache<URL,CacheEntry> cache;

    private ELiteBeanCache() {
        cache = new SimpleCache<URL,CacheEntry>(200);
    }

    public ELiteBean get(FacesContext ctx, String path) {
        // the external script need to run only once
        Map<String,Object> requestMap = ctx.getExternalContext().getRequestMap();
        ELiteBean bean = (ELiteBean)requestMap.get(SCRIPT_KEY + path);
        if (bean != null) {
            return bean;
        }

        try {
            URL url = ctx.getExternalContext().getResource(path);
            if (url == null) {
                return null;
            }

            ELProgram prog;

            // load and cache the AST representation of the script
            CacheEntry entry = cache.get(url);
            long lastmod = url.openConnection().getLastModified();
            if (entry != null && lastmod == entry.lastmod) {
                prog = entry.prog;
            } else {
                String script = readScript(url);
                prog = loadScript(script, path);
                cache.put(url, new CacheEntry(prog, lastmod));
            }

            // execute the script and wrap it as an ELiteBean
            bean = runScript(ctx, prog, path);
            requestMap.put(SCRIPT_KEY + path, bean);
            return bean;
        } catch (IOException ex) {
            return null;
        }
    }

    private String readScript(URL url) {
        try {
            Reader reader = new InputStreamReader(url.openStream());
            StringBuilder buf = new StringBuilder();
            char[] cbuf = new char[8192];
            for (int len; (len = reader.read(cbuf)) != -1; ) {
                buf.append(cbuf, 0, len);
            }
            reader.close();
            return buf.toString();
        } catch (IOException ex) {
            return null;
        }
    }

    private ELProgram loadScript(String script, String path) {
        Parser parser = new Parser(script);
        parser.setFileName(path);
        parser.setResourceResolver(new WebResourceResolver());
        return parser.parse();
    }

    private ELiteBean runScript(FacesContext ctx, ELProgram prog, String path) {
        ELContextImpl elctx = (ELContextImpl)ctx.getELContext();
        VariableMapper vm = elctx.getVariableMapper();
        VariableMapperImpl vmd = new DelegatingVariableMapper(vm);
        elctx.setVariableMapper(vmd);
        prog.execute(elctx, path, 1);
        elctx.setVariableMapper(vm);
        return ELiteBean.make(elctx, path, vmd.getVariableMap());
    }

    private static class WebResourceResolver implements ResourceResolver {
        public Reader open(String path) throws IOException {
            FacesContext ctx = FacesContext.getCurrentInstance();
            InputStream stream = ctx.getExternalContext().getResourceAsStream(path);
            return new InputStreamReader(stream);
        }
    }

    private static class DelegatingVariableMapper extends VariableMapperImpl {
        private VariableMapper delegate;

        DelegatingVariableMapper(VariableMapper delegate) {
            this.delegate = delegate;
        }

        public ValueExpression resolveVariable(String name) {
            return delegate.resolveVariable(name);
        }

        public ValueExpression setVariable(String name, ValueExpression var) {
            if (var instanceof Closure) {
                // collect variables defined in the script and prevent
                // redefinition of existing variable.
                ValueExpression oldvar = delegate.resolveVariable(name);
                if (oldvar == null) {
                    super.setVariable(name, var);
                    delegate.setVariable(name, var);
                } else {
                    super.setVariable(name, oldvar);
                }
                return oldvar;
            } else {
                return delegate.setVariable(name, var);
            }
        }
    }
}
