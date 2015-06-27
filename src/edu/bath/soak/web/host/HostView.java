package edu.bath.soak.web.host;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.bath.soak.net.model.Host;
import edu.bath.soak.util.OrderedComparator;

public class HostView {

	public static String BASIC_INFO = "basicInfo";
	public static String RELATED_INFO = "relatedInfo";
	public static String NETWORK_INFO = "networkInfo";
	public static String HOST_HISTORY = "hostHistory";

	Host host;
	Map<String, HostViewTab> tabs = new HashMap<String, HostViewTab>();

	public HostView(Host h) {
		this.host = h;
		HostViewTab basicTab = new HostViewTab();
		basicTab.setTabName(BASIC_INFO);
		basicTab.setTabTitle("Host Information");
		basicTab.setOrder(100);
		addTab(basicTab);
		HostViewTab networkInfoTab = new HostViewTab();
		networkInfoTab.setTabName(NETWORK_INFO);
		networkInfoTab.setTabTitle("Network Information");
		networkInfoTab.setOrder(300);
		addTab(networkInfoTab);
		HostViewTab relatedInfoTab = new HostViewTab();
		relatedInfoTab.setTabName(RELATED_INFO);
		relatedInfoTab.setTabTitle("DNS / DHCP");
		relatedInfoTab.setOrder(200);
		addTab(relatedInfoTab);

		HostViewTab hostHistoryTab = new HostViewTab();
		hostHistoryTab.setTabName(HOST_HISTORY);
		hostHistoryTab.setTabTitle("Change History");
		hostHistoryTab.setOrder(400);
		addTab(hostHistoryTab);

	}

	public Host getHost() {
		return host;
	}

	public void setHost(Host host) {
		this.host = host;
	}

	public void addTab(HostViewTab tab) {
		tabs.put(tab.getTabName(), tab);
	}

	public HostViewTab getTabByName(String name) {
		return tabs.get(name);
	}

	/**
	 * Retuns a list of non-empty host tabs sorted by their Order ordering.
	 * 
	 * @return a sorted list of host view tabs
	 */
	public List<HostViewTab> getSortedTabs() {
		ArrayList<HostViewTab> sortedTabs = new ArrayList<HostViewTab>();
		for (HostViewTab tab : tabs.values()) {
			if (tab.getRenderBeans().size() > 0) {
				sortedTabs.add(tab);
			}
		}
		// / sortedTabs.addAll(tabs.values());
		Collections.sort(sortedTabs, new OrderedComparator());
		return sortedTabs;
	}

}
