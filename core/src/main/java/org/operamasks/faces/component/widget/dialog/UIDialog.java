/*
 * $Id:
 *
 * Copyright (c) 2006 Operamasks Community.
 * Copyright (c) 2000-2006 Apusic Systems, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.operamasks.faces.component.widget.dialog;

import javax.el.ValueExpression;
import javax.faces.component.UIComponentBase;
import javax.faces.context.FacesContext;

import org.operamasks.faces.render.widget.yuiext.ComponentOperationManager;
import org.operamasks.faces.util.FacesUtils;

/**
 * @deprecated 此类已经被org.operamasks.faces.component.layout.impl.UIWindow代替
 */
@Deprecated
public class UIDialog extends UIComponentBase
{
    public static final String COMPONENT_TYPE = "org.operamasks.faces.widget.Dialog";
    public static final String COMPONENT_FAMILY = "org.operamasks.faces.widget.Dialog";
    public static final String RENDERER_TYPE = "org.operamasks.faces.widget.Dialog";
    
    public UIDialog() {
        setRendererType( RENDERER_TYPE ) ;
    }
    
    public String getFamily() {
        return COMPONENT_FAMILY;
    }
    private Integer width;
    private Integer height;
    private String jsvar;
    private String title;
    private String left ;
    private String top ;
    private String contentStyle ;
    private String contentStyleClass ;
    private Boolean draggable ;
    private Boolean resizable ;
    private Boolean collapsible ;
    private Boolean closable ;
    private Boolean autoScroll ;
    private Boolean modal ;
    private Boolean show ;
    
    public String getJsvar() {
        if (this.jsvar != null) {
            return this.jsvar;
        }
        ValueExpression ve = getValueExpression("jsvar");
        if (ve != null) {
            return (String)ve.getValue(getFacesContext().getELContext());
        } else {
            return null;
        }
    }

    public void setJsvar(String jsvar) {
        this.jsvar = jsvar;
    }

    public String getTitle() {
        if (this.title!= null) {
            return this.title;
        }
        ValueExpression ve = getValueExpression("title");
        if (ve != null) {
            return (String)ve.getValue(getFacesContext().getELContext());
        } else {
            return null;
        }
    }

    public void setTitle(String title) {
        this.title = title;
        ComponentOperationManager cm = ComponentOperationManager.getInstance(getFacesContext());
        cm.getAttributes().put("jsvar", FacesUtils.getJsvar(getFacesContext(), this));
        cm.getAttributes().put("title", title);
        cm.invoke(getFacesContext(), "setTitle", this);
    }
    

    public Integer getWidth() {
        if (this.width != null) {
            return this.width;
        }
        ValueExpression ve = getValueExpression("width");
        if (ve != null) {
            return (Integer)ve.getValue(getFacesContext().getELContext());
        } else {
            return null;
        }
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Integer getHeight() {
        if (this.height != null) {
            return this.height;
        }
        ValueExpression ve = getValueExpression("height");
        if (ve != null) {
            return (Integer)ve.getValue(getFacesContext().getELContext());
        } else {
            return null;
        }
    }

    public void setHeight(Integer height) {
        this.height = height;
    }
    
    public void resizeTo(int width, int height){
    	ComponentOperationManager cm = ComponentOperationManager.getInstance(getFacesContext());
        cm.getAttributes().put("jsvar", FacesUtils.getJsvar(getFacesContext(), this));
        cm.getAttributes().put("width", width);
        cm.getAttributes().put("height", height);
        cm.invoke(getFacesContext(), "resizeTo", this);
    }
    
    public void moveTo(int x, int y){
    	ComponentOperationManager cm = ComponentOperationManager.getInstance(getFacesContext());
        cm.getAttributes().put("jsvar", FacesUtils.getJsvar(getFacesContext(), this));
        cm.getAttributes().put("x", x);
        cm.getAttributes().put("y", y);
        cm.invoke(getFacesContext(), "moveTo", this);
    }

    public String getLeft() {
        if (this.left != null) {
            return this.left;
        }
        ValueExpression ve = getValueExpression("left");
        if (ve != null) {
            return (String)ve.getValue(getFacesContext().getELContext());
        } else {
            return null;
        }
    }

    public void setLeft(String left) {
        this.left = left;
    }

    public String getTop() {
        if (this.top != null) {
            return this.top;
        }
        ValueExpression ve = getValueExpression("top");
        if (ve != null) {
            return (String)ve.getValue(getFacesContext().getELContext());
        } else {
            return null;
        }
    }

    public void setTop(String top) {
        this.top = top;
    }
    
    public String getContentStyle() {
        if (this.contentStyle != null) {
            return this.contentStyle;
        }
        ValueExpression ve = getValueExpression("contentStyle");
        if (ve != null) {
            return (String)ve.getValue(getFacesContext().getELContext());
        } else {
            return null;
        }
    }

    public void setContentStyle(String contentStyle) {
        this.contentStyle = contentStyle;
    }

    public String getContentStyleClass() {
        if (this.contentStyleClass != null) {
            return this.contentStyleClass;
        }
        ValueExpression ve = getValueExpression("contentStyleClass");
        if (ve != null) {
            return (String)ve.getValue(getFacesContext().getELContext());
        } else {
            return null;
        }
    }

    public void setContentStyleClass(String contentStyleClass) {
        this.contentStyleClass = contentStyleClass;
    }

    public Boolean getDraggable() {
        if (this.draggable != null) {
            return this.draggable;
        }
        ValueExpression ve = getValueExpression("draggable");
        if (ve != null) {
            return (Boolean)ve.getValue(getFacesContext().getELContext());
        } else {
            return null;
        }
    }

    public void setDraggable(Boolean draggable) {
        this.draggable = draggable;
    }
    
    public Boolean getResizable() {
        if (this.resizable != null) {
            return this.resizable;
        }
        ValueExpression ve = getValueExpression("resizable");
        if (ve != null) {
            return (Boolean)ve.getValue(getFacesContext().getELContext());
        } else {
            return null;
        }
    }

    public void setResizable(Boolean resizable) {
        this.resizable = resizable;
    }
    
    public Boolean getCollapsible() {
        if (this.collapsible != null) {
            return this.collapsible;
        }
        ValueExpression ve = getValueExpression("collapsible");
        if (ve != null) {
            return (Boolean)ve.getValue(getFacesContext().getELContext());
        } else {
            return null;
        }
    }

    public void setCollapsible(Boolean collapsible) {
        this.collapsible = collapsible;
    }
    
    public Boolean getClosable() {
        if (this.closable != null) {
            return this.closable;
        }
        ValueExpression ve = getValueExpression("closable");
        if (ve != null) {
            return (Boolean)ve.getValue(getFacesContext().getELContext());
        } else {
            return null;
        }
    }

    public void setClosable(Boolean closable) {
        this.closable = closable;
    }
    
    public Boolean getAutoScroll() {
        if (this.autoScroll != null) {
            return this.autoScroll;
        }
        ValueExpression ve = getValueExpression("autoScroll");
        if (ve != null) {
            return (Boolean)ve.getValue(getFacesContext().getELContext());
        } else {
            return null;
        }
    }

    public void setAutoScroll(Boolean autoScroll) {
        this.autoScroll = autoScroll;
    }
    
    public Boolean getModal() {
        if (this.modal != null) {
            return this.modal;
        }
        ValueExpression ve = getValueExpression("modal");
        if (ve != null) {
            return (Boolean)ve.getValue(getFacesContext().getELContext());
        } else {
            return null;
        }
    }

    public void setModal(Boolean modal) {
        this.modal = modal;
    }
    
    public boolean isShow() {
        if (this.show != null) {
            return this.show;
        }
        ValueExpression ve = getValueExpression("show");
        if (ve != null) {
            Boolean value = (Boolean)ve.getValue(getFacesContext().getELContext());
            if (value != null) {
                return value;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public void setShow(boolean show) {
        this.show = show;
    }
    
    public void show() {
        this.show = true;
    }

    public void close() {
        this.show = false;
    }

    public Object saveState(FacesContext context) {
        return new Object[] {
            super.saveState(context),
            jsvar       ,
            title       ,
            width       ,
            height      ,
            left        ,
            top         ,
            draggable   ,
            resizable   ,
            collapsible ,
            closable    ,
            autoScroll  ,
            modal       ,
            show
        };
    }

    public void restoreState(FacesContext context, Object state) {
        Object[] values = (Object[]) state;
        int i = 0;
        super.restoreState(context, values[i++]);
        jsvar       = (String)  values[i++];
        title       = (String)values[i++];
        width       = (Integer)values[i++];
        height      = (Integer)values[i++];
        left        = (String)values[i++];
        top         = (String)values[i++];
        draggable   = (Boolean)values[i++];
        resizable   = (Boolean)values[i++];
        collapsible = (Boolean)values[i++];
        closable    = (Boolean)values[i++];
        autoScroll  = (Boolean)values[i++];
        modal       = (Boolean)values[i++];
        show        = (Boolean)values[i++];
    }
}
