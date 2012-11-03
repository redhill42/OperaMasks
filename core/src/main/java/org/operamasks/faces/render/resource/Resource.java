/*
 * $Id: Resource.java,v 1.4 2007/07/02 07:38:03 jacky Exp $
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

package org.operamasks.faces.render.resource;

import javax.faces.context.FacesContext;
import java.io.IOException;

/**
 * UI组件所依赖的外部资源，例如脚本、样式表等。
 */
public interface Resource
{
    /**
     * 高优先级。
     */
    int HIGH_PRIORITY = 0;

    /**
     * 普通优先级。
     */
    int NORMAL_PRIORITY = 500;

    /**
     * 低优先级。
     */
    int LOW_PRIORITY = 1000;

    /**
     * 返回能够唯一标识一个资源的标识符，具有相同标识符的资源只会被渲染一次。
     *
     * @return 资源标识符
     */
    public String getId();

    /**
     * 返回资源优先级。优先级数字较低的资源将在优先级数字较高的资源之前渲染。
     *
     * @return 资源优先级
     */
    public int getPriority();

    /**
     * 在页面的开始部分渲染资源。
     *
     * @throws IOException 当发生I/O异常时
     */
    public void encodeBegin(FacesContext context)
        throws IOException;

    /**
     * 在页面的结束部分渲染资源。
     *
     * @throws IOException 当发生I/O异常时
     */
    public void encodeEnd(FacesContext context)
        throws IOException;

    /**
     * 返回装载脚本，该脚本将作为<bode>body</code>元素的<code>"onload"</code>属性被渲染。
     *
     * @return 装载脚本，可以返回<code>null</code>
     */
    public String getLoadScript(FacesContext context);

    /**
     * 返回卸载脚本，该脚本将作为<bode>body</code>元素的<code>"onunload"</code>属性被渲染。
     *
     * @return 装载脚本，可以返回<code>null</code>
     */
    public String getUnloadScript(FacesContext context);
}
