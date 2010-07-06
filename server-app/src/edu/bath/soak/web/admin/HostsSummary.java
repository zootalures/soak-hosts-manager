package edu.bath.soak.web.admin;

import java.util.List;
import java.util.Map;

import edu.bath.soak.net.model.HostClass;
import edu.bath.soak.web.BeanView;

@BeanView(view = "adminConsole", value = "beanview/admin/HostsSummary-adminConsole")
public class HostsSummary implements AdminConsoleObject {

	int totalHosts;
	Map<HostClass, Integer> hostsByType;
	List<HostClass> hostClassesByNum;

	public int getOrder() {
		return -1000;
	}

	public int getTotalHosts() {
		return totalHosts;
	}

	public void setTotalHosts(int totalHosts) {
		this.totalHosts = totalHosts;
	}

	public Map<HostClass, Integer> getHostsByType() {
		return hostsByType;
	}

	public void setHostsByType(Map<HostClass, Integer> hostsByUsage) {
		this.hostsByType = hostsByUsage;
	}

	public List<HostClass> getHostClassesByNum() {
		return hostClassesByNum;
	}

	public void setHostClassesByNum(List<HostClass> hostClassesByNum) {
		this.hostClassesByNum = hostClassesByNum;
	}

}
