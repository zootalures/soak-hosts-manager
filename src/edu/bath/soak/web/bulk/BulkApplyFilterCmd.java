package edu.bath.soak.web.bulk;

import java.io.Serializable;

import edu.bath.soak.net.model.Subnet;

/**
 * Command for setting IP addreses on mulitple hosts
 * 
 * @author cspocc
 * 
 */

public class BulkApplyFilterCmd implements Serializable {
	String filter;
	boolean overwrite = false;

	public boolean isOverwrite() {
		return overwrite;
	}

	public void setOverwrite(boolean overwrite) {
		this.overwrite = overwrite;
	}

	public String getFilter() {
		return filter;
	}

	public void setFilter(String filter) {
		this.filter = filter;
	}
}
