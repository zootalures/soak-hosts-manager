package edu.bath.soak.web.bulk;

import java.io.Serializable;
import java.text.DecimalFormat;

import edu.bath.soak.web.DefaultHostData;

/**
 * Command to create a set of hosts from a range of values.
 * 
 * @author cspocc
 * 
 */
public class BulkCreateFromRangeCmd implements Serializable {
	DefaultHostData defaultHostData = new DefaultHostData();
	public static final String range1PlaceHolder = "$1";
	public static final String range2PlaceHolder = "$2";

	String hostnameTemplate;
	String ipAddressTemplate;

	public String formatHostName(int val1, int val2) {
		String val1Str = "" + val1;
		if (getRange1().getNumDigits() > 0) {
			String formatString = "";
			for (int i = 0; i < getRange1().getNumDigits(); i++) {
				formatString += "0";
			}
			DecimalFormat df = new DecimalFormat(formatString);
			val1Str = df.format(val1);

		}

		String val2Str = "" + val1;
		if (getRange2().getNumDigits() > 0) {
			String formatString = "";
			for (int i = 0; i < getRange2().getNumDigits(); i++) {
				formatString += "0";
			}
			DecimalFormat df = new DecimalFormat(formatString);
			val2Str = df.format(val2);
		}

		String hostname = getHostnameTemplate().replace(
				BulkCreateFromRangeCmd.range1PlaceHolder, val1Str).replace(
				BulkCreateFromRangeCmd.range2PlaceHolder, val2Str);
		return hostname;
	}

	public String formatIp(int val1, int val2) {
		if (getIpAddressTemplate() != null)
			return getIpAddressTemplate().replace(
					BulkCreateFromRangeCmd.range1PlaceHolder, "" + val1)
					.replace(BulkCreateFromRangeCmd.range2PlaceHolder,
							"" + val2);
		return null;
	}

	public static class BulkRange implements Serializable {
		Integer min;
		Integer max;
		int numDigits = 0;

		public Integer getMin() {
			return min;
		}

		public void setMin(Integer min) {
			this.min = min;
		}

		public Integer getMax() {
			return max;
		}

		public void setMax(Integer max) {
			this.max = max;
		}

		public boolean isSet() {
			return min != null && max != null;
		}

		public int getNumDigits() {
			return numDigits;
		}

		public void setNumDigits(int numDigits) {
			this.numDigits = numDigits;
		}
	}

	public int numHostsSpecified() {
		if (range1.getMin() == null || range1.getMax() == null
				|| range1.getMax() < range1.getMin()) {
			return 0;
		}
		int range2mul = 1;
		if (range2.getMin() != null || range2.getMax() != null
				&& range2.getMax() >= range2.getMin()) {
			range2mul = range2.getMax() - range2.getMin() + 1;
		}
		return (range1.getMax() - range1.getMin() + 1) * range2mul;
	}

	BulkRange range1 = new BulkRange();
	BulkRange range2 = new BulkRange();

	public BulkRange getRange1() {
		return range1;
	}

	public void setRange1(BulkRange range1) {
		this.range1 = range1;
	}

	public BulkRange getRange2() {
		return range2;
	}

	public void setRange2(BulkRange range2) {
		this.range2 = range2;
	}

	public DefaultHostData getDefaultHostData() {
		return defaultHostData;
	}

	public void setDefaultHostData(DefaultHostData defaultHostData) {
		this.defaultHostData = defaultHostData;
	}

	public String getHostnameTemplate() {
		return hostnameTemplate;
	}

	public void setHostnameTemplate(String hostNameTemplate) {
		this.hostnameTemplate = hostNameTemplate;
	}

	public String getIpAddressTemplate() {
		return ipAddressTemplate;
	}

	public void setIpAddressTemplate(String ipAddressTemplate) {
		this.ipAddressTemplate = ipAddressTemplate;
	}

}
