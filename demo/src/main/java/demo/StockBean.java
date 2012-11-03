/*
 * $Id: StockBean.java,v 1.2 2007/12/11 04:14:14 jacky Exp $
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

package demo;

import javax.faces.convert.NumberConverter;
import javax.faces.context.FacesContext;
import javax.faces.component.UIColumn;
import java.util.Date;
import java.util.Random;

import org.operamasks.faces.annotation.ManagedBean;
import org.operamasks.faces.annotation.ManagedBeanScope;
import org.operamasks.faces.component.widget.grid.UIDataGrid;

@ManagedBean(scope= ManagedBeanScope.SESSION)
public class StockBean
{
    public static final class Quote {
        private String company;
        private double price;
        private double change;
        private Date lastUpdated;
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

        public double getPctChange() {
            return (change * 100) / price;
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
    }

    private Quote[] stockData = new Quote[] {
        new Quote("3m Co.", 71.72),
        new Quote("Alcoa Inc", 29.01),
        new Quote("Altria Group Inc.", 83.81),
        new Quote("American Express Company", 52.55),
        new Quote("American International Group, Inc.", 64.13),
        new Quote("Apusic Systems, Inc.", 87.08),
        new Quote("AT&T Inc.", 31.61),
        new Quote("Boeing Co.", 75.43),
        new Quote("Caterpillar Inc.", 67.27),
        new Quote("Citigroup, Inc.", 49.37),
        new Quote("E.I. du Pont de Nemours and Company", 40.48),
        new Quote("Exxon Mobil Corp", 68.1),
        new Quote("General Electric Company", 34.14),
        new Quote("General Motors Corporation", 30.27),
        new Quote("Hewlett-Packard Co.", 36.53),
        new Quote("Honeywell Intl Inc.", 38.77),
        new Quote("Intel Corporation", 19.88),
        new Quote("International Business Machines", 81.41),
        new Quote("Johnson & Johnson", 64.72),
        new Quote("JP Morgan & Chase & Co", 45.73),
        new Quote("McDonald's Corporation", 36.76),
        new Quote("Merck & Co., Inc.", 40.96),
    };

    private void update() {
        Date date = new Date();
        Random random = new Random();

        for (Quote quote : stockData) {
            int radix = random.nextInt(10);
            double change = quote.getPrice() * (5-radix) / 100;
            quote.setPrice(quote.getPrice() + change);
            quote.setChange(change);
            quote.setLastUpdated(date);
        }
    }

    private long lastUpdate = 0;

    public Quote[] getStockData() {
        long current = System.currentTimeMillis();
        if (current - lastUpdate > 5000) {
            update();
            lastUpdate = current;
            grid.reload();
        }
        return stockData;
    }

    UIDataGrid grid;
    private int row = 0;
    private Quote selectedQuote;

    public UIDataGrid getGrid() {
        return grid;
    }

    public void setGrid(UIDataGrid grid) {
        this.grid = grid;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public void setSelectedQuote(Quote quote) {
        this.selectedQuote = quote;
    }
    
    public Quote getSelectedQuote() {
        return selectedQuote;
    }

    public String formatPctChange(UIColumn column, Object rowData) {
        Quote quote = (Quote)rowData;
        double pctchange = quote.getChange() / quote.getPrice();

        NumberConverter converter = new NumberConverter();
        converter.setPattern("0.00%");
        String formatted = converter.getAsString(FacesContext.getCurrentInstance(), column, pctchange);

        if (pctchange < 0) {
            return "<span style=\"color:red\">" + formatted + "</span>";
        } else {
            return formatted;
        }
    }
    
    public void nextRow() {
        selectRow(row + 1);
    }

    public void previousRow() {
        selectRow(row - 1);
    }

    private void selectRow(int row) {
        if (row < 0) {
            row = 0;
        } else if (row >= stockData.length) {
            row = stockData.length - 1;
        }
        this.row = row;

        int rows = grid.getRows();
        if (rows > 0) {
            int first = (row / rows) * rows;
            if (first != grid.getFirst()) {
                grid.setFirst(first);
                grid.reload();
            }
        }
    }

    public void refresh() {
        grid.reload();
    }
}
