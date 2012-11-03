/*
 * $Id: UIDrawImage.java,v 1.10 2008/04/29 05:21:13 lishaochuan Exp $
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

package org.operamasks.faces.component.widget;

import javax.faces.component.UIComponent;
import javax.faces.component.html.HtmlGraphicImage;
import javax.faces.context.FacesContext;
import javax.el.MethodExpression;
import javax.el.ValueExpression;

import org.operamasks.faces.util.FacesUtils;

public class UIDrawImage extends HtmlGraphicImage
{
    public static final String COMPONENT_TYPE = "org.operamasks.faces.widget.DrawImage";
    public static final String RENDERER_TYPE = "org.operamasks.faces.widget.DrawImage";

    public UIDrawImage() {
        setRendererType(RENDERER_TYPE);
    }
    
    public UIDrawImage(UIComponent parent) {
        this();
        FacesUtils.createComponent(parent, this);
    }

    // 继承的属性：
    //
    // value:  已对UIGraphic的value属性进行了扩展，除String类型之外，还可以使用以下类型：
    //   java.awt.image.Image: 一个已绘制好的图片
    //   byte[]: 包含原始图片数据的字节数组
    //   java.io.InputStream: 包含原始图片数据的输入流
    //   java.net.URL: 指向图片的URL
    //   java.lang.String: 如果字符串以resource:开头，则从类路径中查找图片并输出， 否则将
    //   字符串作为指向图片的URL
    // 如果已指定value属性则不再调用draw方法。
    //
    // width:  指定图片宽度，如果使用draw方法绘制图片则必须指定此属性
    // height: 指定图片高度，如果使用draw方法绘制图片则必须指定此属性

    private String type;
    private Boolean alpha;
    private Boolean inline;
    private MethodExpression drawMethod;
    private Boolean needRefresh = true;

    /**
     * 图片MIME类型，如"image/jpeg"等，缺省为"image/jpeg"
     */
    public String getType() {
        if (this.type != null) {
            return this.type;
        }
        ValueExpression ve = getValueExpression("type");
        if (ve != null) {
            return (String)ve.getValue(getFacesContext().getELContext());
        } else {
            return null;
        }
    }

    public void setType(String type) {
        this.type = type;
    }

    /**
     * 图片中是否包含alpha通道，即是否输出透明图片。
     */
    public boolean getAlpha() {
        if (this.alpha != null) {
            return this.alpha;
        }
        ValueExpression ve = getValueExpression("alpha");
        if (ve != null) {
            return (Boolean)ve.getValue(getFacesContext().getELContext());
        } else {
            return false;
        }
    }

    public void setAlpha(boolean alpha) {
        this.alpha = alpha;
    }

    /**
     * 指定图片数据是否以Base64的格式嵌入在页面中。使用嵌入式图片可以减少一次
     * 网络交互，但请注意Internet Explorer并不支持 嵌入式图片，因此此选项并
     * 不总是生效。
     */
    public boolean isInline() {
        if (this.inline != null) {
            return this.inline;
        }
        ValueExpression ve = getValueExpression("inline");
        if (ve != null) {
            return (Boolean)ve.getValue(getFacesContext().getELContext());
        } else {
            return true;
        }
    }

    public void setInline(boolean inline) {
        this.inline = inline;
    }

    /**
     * 图片绘制方法，调用此方法生成动态图片，此方法的原型为：
     *   void draw(java.awt.Graphics g, int width, int height);
     */
    public MethodExpression getDrawMethod() {
        return drawMethod;
    }

    public void setDrawMethod(MethodExpression drawMethod) {
        this.drawMethod = drawMethod;
    }
    
    /**
     * 刷新图片
     */
    public void refresh(){
        this.needRefresh = true;
    }
    
    public Boolean isNeedRefresh() {
        return needRefresh;
    }

    public void setNeedRefresh(Boolean needRefresh) {
        this.needRefresh = needRefresh;
    }

    public Object saveState(FacesContext context) {
        return new Object[] {
            super.saveState(context),
            type,
            alpha,
            inline,
            needRefresh,
            saveAttachedState(context, drawMethod)
        };
    }

    public void restoreState(FacesContext context, Object state) {
        Object[] values = (Object[])state;
        super.restoreState(context, values[0]);
        type = (String)values[1];
        alpha = (Boolean)values[2];
        inline = (Boolean)values[3];
        needRefresh = (Boolean)values[4];
        drawMethod = (MethodExpression)restoreAttachedState(context, values[5]);
    }

}
