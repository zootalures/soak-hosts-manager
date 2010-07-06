package edu.bath.soak.net.query;

import java.util.Date;

import edu.bath.soak.model.OrgUnit;
import edu.bath.soak.query.SearchQuery;

public class HostChangeQuery extends SearchQuery {
	boolean showMine = false;
	String searchTerm;
	OrgUnit orgUnit;
	Date fromDate;
	Date toDate;

	public String getSearchTerm() {
		return searchTerm;
	}

	public void setSearchTerm(String searchTerm) {
		this.searchTerm = searchTerm;
	}

	public OrgUnit getOrgUnit() {
		return orgUnit;
	}

	public void setOrgUnit(OrgUnit organisationalUnit) {
		this.orgUnit = organisationalUnit;
	}

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

	public boolean isShowMine() {
		return showMine;
	}

	public void setShowMine(boolean showAll) {
		this.showMine = showAll;
	}
}
