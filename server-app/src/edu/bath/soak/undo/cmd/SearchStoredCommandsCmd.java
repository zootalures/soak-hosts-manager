package edu.bath.soak.undo.cmd;

import edu.bath.soak.model.OrgUnit;
import edu.bath.soak.query.SearchQuery;

public class SearchStoredCommandsCmd extends SearchQuery {
	String userName;
	OrgUnit orgUnit;

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public OrgUnit getOrgUnit() {
		return orgUnit;
	}

	public void setOrgUnit(OrgUnit orgUnit) {
		this.orgUnit = orgUnit;
	}
}
