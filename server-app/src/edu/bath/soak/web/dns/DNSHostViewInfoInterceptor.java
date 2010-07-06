package edu.bath.soak.web.dns;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;

import edu.bath.soak.dns.DNSHostsInterceptor;
import edu.bath.soak.dns.model.DNSRecord;
import edu.bath.soak.web.dns.DNSHostRecordsView.DNSRecordState;
import edu.bath.soak.web.host.HostView;
import edu.bath.soak.web.host.HostViewTab;
import edu.bath.soak.web.host.ShowHostInfoInterceptor;

/*******************************************************************************
 * View interceptor which adds host DNS record information to view in the
 * RELATED_INFO tab
 * 
 * @author cspocc
 * 
 */
public class DNSHostViewInfoInterceptor implements ShowHostInfoInterceptor {

	DNSHostsInterceptor dnsHostsInteceptor;

	public void elaborateView(HostView view, HttpServletRequest request) {
		HostViewTab tab = view.getTabByName(HostView.RELATED_INFO);
		Assert.notNull(tab);
		DNSHostRecordsView rv = new DNSHostRecordsView(view);
		Set<DNSRecord> existingRecords = new HashSet<DNSRecord>();

		Set<DNSRecord> requiredRecords = dnsHostsInteceptor
				.getRequiredRecordsForHost(view.getHost());

		Set<DNSRecord> existingRequiredRecords = dnsHostsInteceptor
				.filterRecordsToExisting(requiredRecords);
		for (DNSRecord rec : existingRequiredRecords) {
			if (!existingRecords.contains(rec)) {
				existingRecords.add(rec);
			}
		}
		for (DNSRecord rec : dnsHostsInteceptor
				.getExtraExistingAssociatedRecords(view.getHost())) {
			if (!existingRecords.contains(rec)) {
				existingRecords.add(rec);
			}
		}

		Set<DNSRecord> allRecords = new HashSet<DNSRecord>();
		allRecords.addAll(existingRecords);
		allRecords.addAll(requiredRecords);

		Map<DNSRecord, DNSRecordState> recState = new HashMap<DNSRecord, DNSRecordState>();

		record: for (DNSRecord r : requiredRecords) {
			if (existingRecords.contains(r)) {
				for (DNSRecord existingRecord : existingRecords) {
					if (r.equals(existingRecord)) {
						if (r.equalsIncludingTtl(existingRecord)) {
							recState.put(existingRecord, DNSRecordState.OK);
						} else {
							recState.put(existingRecord, DNSRecordState.MINOR);
						}
						continue record;
					}
				}
			} else {
				recState.put(r, DNSRecordState.MISSING);
			}
		}
		for (DNSRecord r : existingRecords) {
			if (!requiredRecords.contains(r)) {
				recState.put(r, DNSRecordState.SPURIOUS);
			}
		}
		rv.setRecordState(recState);

		ArrayList<DNSRecord> recs = new ArrayList<DNSRecord>();
		recs.addAll(allRecords);
		Collections.sort(recs);
		rv.setRecords(recs);
		tab.getRenderBeans().add(rv);
	}

	public int getOrder() {
		return Integer.MAX_VALUE / 2;
	}

	@Required
	public void setDnsHostsInteceptor(DNSHostsInterceptor dnsHostsInteceptor) {
		this.dnsHostsInteceptor = dnsHostsInteceptor;
	}

}
