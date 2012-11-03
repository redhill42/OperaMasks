/*
 * $Id: HelloValidation.java,v 1.2 2007/12/11 04:14:14 jacky Exp $
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
package demo.binding;

import org.operamasks.faces.annotation.*;

/**
 * 这是Hello应用的数据转换和校验对象，利用一个分离的对象可以实现数据转换和校验的
 * 可插拔性，获得更大的可维护性。
 *
 * 数据校验和转换一般是无状态的。
 */
@ManagedBean(scope=ManagedBeanScope.REQUEST)
public class HelloValidation
{
    @Convert
    private String convertGreeting(String value) {
        System.out.println("convertGreeting called");
        return value.toLowerCase();
    }

    @Format
    private String formatResponse(String value) {
        System.out.println("formatResponse called");
        return value.toUpperCase();
    }

    @Validate
    private String validateGreeting(String value) {
        if (value.equalsIgnoreCase(".NET")) {
            return "Sorry, I don't like .NET";
        }
        return null;
    }
}
