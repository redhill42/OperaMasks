/*
 * $Id:
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

package org.operamasks.faces.webapp.filter;

import java.io.IOException;

import javax.faces.application.ViewHandler;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.operamasks.faces.application.ApplicationAssociate;

/**
 * A servlet filter that redirect a request URL with 
 * default suffix to FacesServlet.
 */
public class DefaultSuffixRedirector implements javax.servlet.Filter {

	private ServletContext servletContext;
	private String defaultSuffix;

	public void destroy() {
		//no-op
	}

	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;
		String path = req.getServletPath();
		int periodPos = path.lastIndexOf(".");
		if (periodPos >= 0) {
			String pathSuffix = path.substring(periodPos);

			ApplicationAssociate associate = ApplicationAssociate.getInstance(this.servletContext);
//          Enable the following code if ApplicationAssociate is not surely initilized before the first request.
//			if (associate == null) {
//				FacesConfigLoader loader = new FacesConfigLoader();
//				loader.loadFacesConfig(this.servletContext);
//				associate = ApplicationAssociate.getInstance(this.servletContext);
//			}
			if (associate != null && defaultSuffix.equals(pathSuffix)) {
				String facesPath = getFacesPath(path, associate.getFacesMappings());
				try {
					facesPath = this.servletContext.getContextPath().concat(facesPath);
					res.sendRedirect(facesPath);
				} catch (Exception ex) {
					throw new ServletException(ex);
				} 
			}
		}
		chain.doFilter(req, res);
	}
	
	private String getFacesPath(String path, String[] facesMappings) throws ServletException{
		String facesPath = null;
		for (String mapping : facesMappings) {
			char c = mapping.charAt(0);
			if (c == '/') {
				// for prefix mapping
				facesPath = mapping.concat(path);
			} else if (c == '.') {
				// for suffix mapping
				int period = path.lastIndexOf('.');
				if (period == -1) {
					facesPath = path.concat(mapping);
				} else if (!path.endsWith(mapping)) {
					facesPath = path.substring(0, period).concat(mapping);
				}
			}
			if (facesPath != null) {
				break;
			}
		}

		if (facesPath == null) {
			throw new ServletException("Cannot find FacesServlet mapping");
		}
		return facesPath;
	}

	private String getDefaultSuffix() {
		if (defaultSuffix == null) {
			defaultSuffix = this.servletContext.getInitParameter(ViewHandler.DEFAULT_SUFFIX_PARAM_NAME);
			if (defaultSuffix == null) {
				defaultSuffix = ViewHandler.DEFAULT_SUFFIX;
			}
		}
		return defaultSuffix;
	}

	public void init(FilterConfig config) throws ServletException {
		this.servletContext = config.getServletContext();
		this.defaultSuffix = getDefaultSuffix();
	}
}

