package edu.bath.soak.hostactivity.web;

import java.util.List;

import edu.bath.soak.hostactivity.model.MacHistory;
import edu.bath.soak.net.model.LastUsageInfo;
import edu.bath.soak.web.BeanView;
import edu.bath.soak.web.host.HostView;

/**
 * 
 * @author cspocc
 * 
 */
@BeanView("beanview/hostactivity/HostNetworkActivitySection")
public class HostNetworkActivitySection {

	HostView hostView;
	List<MacHistory> historyForIp;
	List<MacHistory> historyForMac;
	LastUsageInfo lastIpSeen;

	public HostNetworkActivitySection(HostView hostView) {
		this.hostView = hostView;
	}

	public LastUsageInfo getLastIpSeen() {
		return lastIpSeen;
	}

	public void setLastIpSeen(LastUsageInfo lastIpSeen) {
		this.lastIpSeen = lastIpSeen;
	}

	public HostView getHostView() {
		return hostView;
	}

	public void setHostView(HostView hostView) {
		this.hostView = hostView;
	}

	public List<MacHistory> getHistoryForIp() {
		return historyForIp;
	}

	public void setHistoryForIp(List<MacHistory> historyForIp) {
		this.historyForIp = historyForIp;
	}

	public List<MacHistory> getHistoryForMac() {
		return historyForMac;
	}

	public void setHistoryForMac(List<MacHistory> historyForMac) {
		this.historyForMac = historyForMac;
	}

}
