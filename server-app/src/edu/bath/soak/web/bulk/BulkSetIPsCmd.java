package edu.bath.soak.web.bulk;

import java.io.Serializable;

import edu.bath.soak.net.model.Subnet;

/**
 * Command for setting IP addreses on mulitple hosts
 * 
 * @author cspocc
 * 
 */

public class BulkSetIPsCmd implements Serializable {
	Subnet newSubnet;
	boolean clearIPs = false;

	public Subnet getNewSubnet() {
		return newSubnet;
	}

	public void setNewSubnet(Subnet newSubnet) {
		this.newSubnet = newSubnet;
	}

	public boolean isClearIPs() {
		return clearIPs;
	}

	public void setClearIPs(boolean clearIPs) {
		this.clearIPs = clearIPs;
	}

}
