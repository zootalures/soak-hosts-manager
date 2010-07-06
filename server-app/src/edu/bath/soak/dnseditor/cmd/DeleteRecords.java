package edu.bath.soak.dnseditor.cmd;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import edu.bath.soak.dns.cmd.DNSCmd;
import edu.bath.soak.dns.model.DNSRecord;

public class DeleteRecords extends UIDNSChange implements Serializable{
	Set<DNSRecord> records = new HashSet<DNSRecord>();

	@Override
	public void fillCmd(DNSCmd cmd) {
		for (DNSRecord rec : records) {
			cmd.insertDelete(rec);
		}
	}

	@Override
	public boolean matchesRecord(DNSRecord rec) {
		// TODO Auto-generated method stub
		return records.contains(rec);
	}

	public Set<DNSRecord> getRecords() {
		return records;
	}

	public void setRecords(Set<DNSRecord> records) {
		this.records = records;
	}
}
