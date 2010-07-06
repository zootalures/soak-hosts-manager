package edu.bath.soak.hostactivity;

import java.util.Date;

import edu.bath.soak.query.SearchFlag;
import edu.bath.soak.web.BeanView;

@BeanView(value = "beanview/hostactivity/HostActivitySearchFlags-form", view = "form")
public class HostActivitySearchFlags implements SearchFlag {
	public static final String FLAG_KEY = "HostActivitySeachFlags";

	public String flagKey() {
		return FLAG_KEY;
	}

	public boolean isFlagSet() {
		return fromDate != null || toDate != null;
	}

	Date fromDate;
	Date toDate;

	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public Date getToDate() {
		return toDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

}
