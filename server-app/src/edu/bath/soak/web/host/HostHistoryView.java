package edu.bath.soak.web.host;

import java.util.Map;

import edu.bath.soak.net.model.Host;
import edu.bath.soak.net.model.HostChange;
import edu.bath.soak.query.SearchResult;
import edu.bath.soak.web.BeanView;

@BeanView("beanview/host/HostHistoryView")
public class HostHistoryView {

	public HostHistoryView(Host host, SearchResult<HostChange> changes) {
		this.host = host;
		this.changes = changes;
	}

	Host host;
	SearchResult<HostChange> changes;
	Map<HostChange, Host> hostBefore;

	public Host getHost() {
		return host;
	}

	public void setHost(Host host) {
		this.host = host;
	}

	public Map<HostChange, Host> getHostBefore() {
		return hostBefore;
	}

	public void setHostBefore(Map<HostChange, Host> versionBefore) {
		this.hostBefore = versionBefore;
	}

	public SearchResult<HostChange> getChanges() {
		return changes;
	}

	public void setChanges(SearchResult<HostChange> changes) {
		this.changes = changes;
	}
}
