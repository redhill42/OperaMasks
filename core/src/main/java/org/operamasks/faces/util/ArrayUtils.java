/*
 * $Id: ArrayUtils.java,v 1.1 2008/04/16 12:03:10 jacky Exp $
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
package org.operamasks.faces.util;

import java.lang.reflect.Array;

/**
 * 数组工具类
 *
 */
public class ArrayUtils {
    /**
     * 将新的数组添加到已有数组的尾部
     * @param <T>
     * @param target 原有数组
     * @param data 新的数组
     * @return 两个数组连接起来之后的结果
     */
    @SuppressWarnings("unchecked")
    public static <T> T[] push(T[] target, T[] data) {
        Class<T> clazz = (Class<T>) target.getClass().getComponentType();
        T[] result = (T[]) Array.newInstance(clazz, target.length + data.length);
        System.arraycopy(target, 0, result, 0, target.length);
        System.arraycopy(data, 0, result, target.length, data.length);
        return result;
    }

    @SuppressWarnings("unchecked")
    public static <T> T[] push(T[] target, T data) {
        return (T[]) push(target, new Object[]{data});
    }
    
    /**
     * 克隆一个数组
     * @param <T>
     * @param target 待克隆的数组
     * @return 新的数组
     */
    @SuppressWarnings("unchecked")
    public static <T> T[] clone(T[] target) {
        Class<T> clazz = (Class<T>) target.getClass().getComponentType();
        T[] result = (T[]) Array.newInstance(clazz, target.length);
        System.arraycopy(target, 0, result, 0, target.length);
        return result;
    }
}
