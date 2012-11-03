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
package demo;

import java.util.Date;
import java.util.Random;

import javax.faces.component.UIColumn;
import javax.faces.context.FacesContext;
import javax.faces.convert.NumberConverter;

import org.operamasks.faces.annotation.Action;
import org.operamasks.faces.annotation.ManagedBean;
import org.operamasks.faces.annotation.ManagedBeanScope;
import org.operamasks.faces.annotation.ManagedProperty;
import org.operamasks.faces.component.widget.grid.UIDataGrid;
import org.operamasks.faces.component.widget.page.PagedDataProvider;

import demo.StockBean.Quote;

@ManagedBean(scope=ManagedBeanScope.SESSION)
@SuppressWarnings("unused")
public class PagedStockBean
{
    public PagedStockBean() {
        update();
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
    
    @ManagedProperty
    private PagedDataProvider pagedStockData = new PagedDataProvider() {

        public Object getData(int start, int limit) {
            if(start < 0 || start > stockData.length -1 ) {
                throw new IllegalArgumentException("out of bound, start:" + start);
            }
            
            if( start + limit > stockData.length) {
                limit = stockData.length - start;
            }
            
            Quote[] data = new Quote[limit];
            System.arraycopy(stockData, start, data, 0, limit);
            return data;
        }

        public int getTotalCount() {
            return stockData.length;
        }
        
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
    
    @ManagedProperty
    private int row;

    @ManagedProperty
    private Quote selectedQuote;
    
    @ManagedProperty
    private UIDataGrid grid;
    
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

    @Action
    public void nextRow() {
        selectRow(row + 1);
    }

    @Action
    public void previousRow() {
        selectRow(row - 1);
    }

    @Action
    public void refresh() {
        grid.reload();
    }
    
    private void selectRow(int row) {
        if(row >=0 && row < stockData.length) {
            this.row = row;
            grid.setSelectedRow(row);
            int rows = grid.getRows();
            if (rows > 0) {
                int first = (row / rows) * rows;
                if (first != grid.getFirst()) {
                    grid.setFirst(first);
                    grid.reload();
                }
            }
        }
    }
}
