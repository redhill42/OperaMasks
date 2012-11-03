/*
 * $Id: UpdaterBean.java,v 1.3 2007/12/13 18:13:46 jacky Exp $
 *
 * Copyright (c) 2006-2007 Operamasks Community.
 * Copyright (c) 2000-2007 Apusic Systems, Inc.
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

package demo;

import java.util.Date;

import javax.faces.context.FacesContext;

import org.operamasks.faces.annotation.ManagedBean;
import org.operamasks.faces.annotation.ManagedBeanScope;
import org.operamasks.faces.component.ajax.AjaxUpdater;

@ManagedBean(scope=ManagedBeanScope.SESSION)
public class UpdaterBean {
    public String getTime() {
        return new Date().toString();
    }
    
    private AjaxUpdater contentUpdater;
    public AjaxUpdater getContentUpdater() {
        return this.contentUpdater;
    }
    
    public void setContentUpdater(AjaxUpdater contentUpdater) {
        this.contentUpdater = contentUpdater;
    }
    
    public void updateAction() {
        FacesContext context = FacesContext.getCurrentInstance();
        if (context != null) {            
            if (isLoadUrl()) {                
                loadContent("/ajax/updater/simple.xhtml");
            } else {
                contentUpdater.unload();
            }
        }
    }
    
    private void loadContent(String uri) {
        contentUpdater.unload();
        String viewId = contentUpdater.getSubviewId();
        if (viewId == null || !viewId.equals(uri)) {
            contentUpdater.load(uri);
        }
    }    
    
    private boolean loadUrl = true;
    public boolean isLoadUrl() {
        return loadUrl;
    }
    public void setLoadUrl(boolean load) {
        this.loadUrl = load;
    }    
}
