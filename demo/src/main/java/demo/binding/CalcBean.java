/*
 * $Id: CalcBean.java,v 1.5 2008/03/10 08:30:46 lishaochuan Exp $
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

import java.util.HashMap;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import org.operamasks.faces.annotation.Action;
import org.operamasks.faces.annotation.Bind;
import org.operamasks.faces.annotation.ComponentAttributes;
import org.operamasks.faces.annotation.ManagedBean;
import org.operamasks.faces.annotation.ManagedBeanScope;
import org.operamasks.faces.annotation.Pattern;
import org.operamasks.faces.annotation.Required;
import org.operamasks.faces.annotation.Validate;
import org.operamasks.faces.annotation.ValidateDoubleRange;

@ManagedBean(scope=ManagedBeanScope.SESSION)
public class CalcBean
{
    @Bind
    @Required(message="The first number is required.")
    @Pattern("#,##0.00")
    @ValidateDoubleRange(minimum=0, maximum=100)
    private double first = 10;

    @Bind
    @Required(message="The second number is required.")
    @Pattern("#,##0.00")
    @ValidateDoubleRange(minimum=0, maximum=100)
    private double second = 20;

    @Bind
    @Pattern("#,##0.00")
    private double result;

    @ComponentAttributes(id="result")
    private Map<String, String> attrs = new HashMap<String, String>();

    @Validate
    private void validateFirst(int value) {
        System.out.println("The first value to be validated is " + value);
    }

    @Validate
    private void validateSecond(int value) {
        System.out.println("The second value to be validated is " + value);
    }

    @Validate(id={"first","second"})
    private void validate(FacesContext context, UIComponent component, Object value) {
        System.out.println("Validating component " + component.getId() + " with value " + value);
    }

    @Action
    public void add() {
        result = first + second;
        attrs.put("style", "color:red");
    }

    @Action
    public void subtract() {
        result = first - second;
        attrs.put("style", "color:green");
    }

    @Action
    public void multiply() {
        result = first * second;
        attrs.put("style", "color:navy");
    }

    @Action
    public void divide() {
        result = first / second;
        attrs.put("style", "color:black");
    }
}
