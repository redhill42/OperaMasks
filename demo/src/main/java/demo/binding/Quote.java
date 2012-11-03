/*
 * $Id: Quote.java,v 1.2 2007/12/11 04:14:14 jacky Exp $
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

import java.util.Date;

import org.operamasks.faces.annotation.Bind;
import org.operamasks.faces.annotation.ConvertNumber;
import org.operamasks.faces.annotation.Pattern;
import org.operamasks.faces.annotation.Action;

public class Quote
{
    @Bind
    private String company;

    @Bind
    @ConvertNumber(currencySymbol="$", pattern="$0.00")
    private double price;

    @Bind
    @Pattern("0.00")
    private double change;

    @Bind
    @Pattern("HH:mm:ss")
    private Date lastUpdated;

    @Bind
    private String comment;

    public Quote(String company, double price) {
        this.company = company;
        this.price = price;
        this.change = 0;
        this.lastUpdated = new Date();
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getChange() {
        return change;
    }

    public void setChange(double change) {
        this.change = change;
    }

    @Bind(id="change", attribute="style")
    public String getChangeStyle() {
        if (this.change > 0) {
            return "color:green";
        } else if (this.change < 0) {
            return "color:red";
        } else {
            return "color:black";
        }
    }

    @Bind
    @Pattern("0.00%")
    public double getPctChange() {
        return change / price;
    }

    @Bind(id="pctChange", attribute="style")
    public String getPctChangeStyle() {
        return getChangeStyle();
    }
    
    public Date getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @Action
    public void increment() {
        this.change = +(this.price * 0.01);
        this.price += this.change;
    }

    @Action
    public void decrement() {
        this.change = -(this.price * 0.01);
        this.price += this.change;
    }
}
