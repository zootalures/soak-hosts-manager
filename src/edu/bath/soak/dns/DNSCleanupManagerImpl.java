package edu.bath.soak.dns;

import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import edu.bath.soak.cmd.BaseCompositeCommand;
import edu.bath.soak.cmd.CommandExpander;
import edu.bath.soak.dns.cmd.CleanUpDNSRecordsForHosts;
import edu.bath.soak.dns.cmd.HostBasedDNSCmd;
import edu.bath.soak.dns.model.DNSRecord;
import edu.bath.soak.net.model.Host;

/**
 * DNS Manager implementation
 * 
 * Implements DNS Commands via a {@link DNSService } object
 * 
 * Also implemnets periodic DNS updates on given zones.
 * 
 * 
 * @author cspocc
 * 
 */
public class DNSCleanupManagerImpl implements
		CommandExpander<CleanUpDNSRecordsForHosts> {

	Logger log = Logger.getLogger(DNSCleanupManagerImpl.class);
	DNSMgr dnsManager;
	DNSHostsInterceptor dnsHostsInterceptor;

	public void expandCmd(CleanUpDNSRecordsForHosts cmd,
			BaseCompositeCommand result) {

		for (Host host : cmd.getHosts()) {
			Set<DNSRecord> requiredRecords = dnsHostsInterceptor
					.getRequiredRecordsForHost(host);

			Set<DNSRecord> existingRecords = new HashSet<DNSRecord>();
			existingRecords.addAll(dnsHostsInterceptor
					.filterRecordsToExisting(requiredRecords));

			Set<DNSRecord> missingRecords = new HashSet<DNSRecord>();
			missingRecords.addAll(requiredRecords);
			missingRecords.removeAll(existingRecords);
			Set<DNSRecord> clashingRecords = new HashSet<DNSRecord>();
			for (DNSRecord existingRecord : existingRecords) {
				clashingRecords.addAll(dnsHostsInterceptor
						.getClashingRecord(existingRecord));
			}

			clashingRecords.removeAll(existingRecords);
			HostBasedDNSCmd subcmd = new HostBasedDNSCmd();
			subcmd.setHost(host);

			for (DNSRecord r : missingRecords) {
				subcmd.insertAdd(r);
			}
			if (cmd.isDeleteConflictingRecords()) {
				for (DNSRecord r : clashingRecords) {
					subcmd.insertDelete(r);
				}
			}
			result.getCommands().add(subcmd);
		}
	}

	public void setupCommand(CleanUpDNSRecordsForHosts cmd) {

	}

	public boolean canExpand(Class type) {
		// TODO Auto-generated method stub
		return false;
	}

	public int getOrder() {
		return 0;
	}

	@Required
	public void setDnsManager(DNSMgr dnsManager) {
		this.dnsManager = dnsManager;
	}

	@Required
	public void setDnsHostsInterceptor(DNSHostsInterceptor dnsHostsInterceptor) {
		this.dnsHostsInterceptor = dnsHostsInterceptor;
	}

}
