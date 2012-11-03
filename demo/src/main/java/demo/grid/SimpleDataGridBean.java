package demo.grid;

import java.util.Date;

import org.operamasks.faces.annotation.ManagedBean;
import org.operamasks.faces.annotation.ManagedBeanScope;
import org.operamasks.faces.annotation.ManagedProperty;

@ManagedBean(name = "simpleDataGridBean", scope = ManagedBeanScope.REQUEST)
public class SimpleDataGridBean {
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

	private Quote[] stockData = new Quote[] { new Quote("3m Co.", 71.72),
			new Quote("Alcoa Inc", 29.01),
			new Quote("Altria Group Inc.", 83.81),
			new Quote("American Express Company", 52.55),
			new Quote("American International Group, Inc.", 64.13),
			new Quote("Apusic Systems, Inc.", 87.08),
			new Quote("AT&T Inc.", 31.61), new Quote("Boeing Co.", 75.43),
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
			new Quote("Merck & Co., Inc.", 40.96), };

	public Quote[] getStockData() {
		return stockData;
	}
	
	@ManagedProperty
	private int[] selections;
	
	@ManagedProperty
	private String responseText;
	
	public Object button_action() {
		if(selections != null){
			StringBuffer buffer = new StringBuffer();
			buffer.append("您选中了第");
			for(int i : selections){
				buffer.append(i + ",");
			}
			responseText = buffer.toString() + "行！";
		}else{
			responseText = "没有选中任何行！";
		}
		return null;
	}
}
