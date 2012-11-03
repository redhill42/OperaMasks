package org.operamasks.faces.component.html.base;

import javax.faces.component.UIComponentBase;

import org.operamasks.faces.annotation.component.Component;
import org.operamasks.faces.annotation.component.Operation;
import org.operamasks.faces.render.widget.IFrameRenderHandler;
import org.operamasks.faces.tools.annotation.ComponentMeta;

@ComponentMeta(tagName="iframe")
@Component(renderHandler=IFrameRenderHandler.class)
public abstract class UIIFrameBase extends UIComponentBase{
    protected String align;
    protected Integer border;
    protected Integer width;
    protected Integer height;
    protected String frameborder;
    protected String name;
    protected String scrolling;
    protected String src;
    protected String style;
    
    public void unload(){
        setSrc("");
    }
    
    public void load(String url){
        setSrc(url);
    }
    
    @Operation
    public void setSrc(java.lang.String value) {
        this.src = value;
    }
    public java.lang.String getSrc() {
        if (this.src != null) {
            return this.src;
        }
        javax.el.ValueExpression ve = this.getValueExpression("src");
        if (ve != null) {
            try {
                return (java.lang.String) ve.getValue(this.getFacesContext().getELContext());
            } catch (javax.el.ELException e) {
                throw new javax.faces.FacesException(e);
            }
        }
        return null;
    }
}
