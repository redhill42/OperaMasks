/*
 * $Id: ELConverterBinding.java,v 1.4 2008/01/31 04:12:24 daniel Exp $
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

import javax.faces.context.FacesContext;
import javax.faces.component.UIComponent;
import javax.faces.component.ValueHolder;
import javax.faces.convert.Converter;
import elite.lang.Closure;
import org.operamasks.faces.binding.ModelBindingContext;

class ELConverterBinding extends Binding
{
    private String[] ids;
    private Closure convert;
    private Closure format;

    ELConverterBinding(String[] ids) {
        super(null);
        this.ids = ids;
    }

    public Closure getConvertClosure() {
        return this.convert;
    }

    public void setConvertClosure(Closure closure) {
        this.convert = closure;
    }

    public Closure getFormatClosure() {
        return this.format;
    }

    public void setFormatClosure(Closure closure) {
        this.format = closure;
    }

    public void apply(FacesContext ctx, ModelBindingContext mbc) {
        for (String id : ids) {
            UIComponent comp = mbc.getComponent(id);
            if ((comp == null) || !(comp instanceof ValueHolder)) {
                continue;
            }

            ValueHolder vh = (ValueHolder)comp;
            Converter previous = vh.getConverter();

            ConverterAdapter adapter;
            if (previous == null) {
                adapter = new ConverterAdapter();
                vh.setConverter(adapter);
            } else if (previous instanceof ConverterAdapter) {
                adapter = (ConverterAdapter)previous;
            } else {
                continue;
            }

            if (this.convert != null)
                adapter.setConvertAction(new ClosureConvertAction(this.convert));
            if (this.format != null)
                adapter.setFormatAction(new ClosureFormatAction(this.format));
        }
    }
}
