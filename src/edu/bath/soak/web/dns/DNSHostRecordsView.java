package edu.bath.soak.web.dns;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.discovery.resource.names.DiscoverServiceNames;
import org.springframework.util.Assert;

import edu.bath.soak.dns.model.DNSRecord;
import edu.bath.soak.web.BeanView;
import edu.bath.soak.web.host.HostView;

@BeanView("beanview/dns/DNSHostRecordsView")
public class DNSHostRecordsView {

	HostView hostView;

	static enum DNSRecordState {
		OK, MINOR, MISSING, SPURIOUS

	};

	List<DNSRecord> records = new LinkedList<DNSRecord>();
	Map<DNSRecord, DNSRecordState> recordState = new HashMap<DNSRecord, DNSRecordState>();

	public DNSHostRecordsView(HostView hostView) {
		Assert.notNull(hostView);
		this.hostView = hostView;
	}

	public List<DNSRecord> getRecords() {
		return records;
	}

	public DNSRecordState getWorstRecordState() {
		DNSRecordState state = DNSRecordState.OK;
		for (DNSRecordState ds : recordState.values()) {
			if (ds.equals(DNSRecordState.SPURIOUS)) {
				if (state.equals(DNSRecordState.OK)) {
					state = ds;
				}
			}
			if (ds.equals(DNSRecordState.MINOR)) {
				if (state.equals(DNSRecordState.OK)
						|| state.equals(DNSRecordState.SPURIOUS)) {
					state = ds;
				}
			}
			if (ds.equals(DNSRecordState.MISSING)) {
				if (state.equals(DNSRecordState.OK)
						|| state.equals(DNSRecordState.SPURIOUS)
						|| state.equals(DNSRecordState.MINOR)) {
					state = ds;
				}
			}

		}
		return state;
	}

	public boolean isContainsBadRecords() {
		for (DNSRecordState ds : recordState.values()) {
			if (!ds.equals(DNSRecordState.OK)) {
				return true;
			}
		}
		return false;
	}

	public HostView getHostView() {
		return hostView;
	}

	public void setHostView(HostView hostView) {
		this.hostView = hostView;
	}

	public Map<DNSRecord, DNSRecordState> getRecordState() {
		return recordState;
	}

	public void setRecordState(Map<DNSRecord, DNSRecordState> recordState) {
		this.recordState = recordState;
	}

	public void setRecords(List<DNSRecord> records) {
		this.records = records;
	}

}
