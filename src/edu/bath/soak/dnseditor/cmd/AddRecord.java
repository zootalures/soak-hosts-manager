package edu.bath.soak.dnseditor.cmd;

import edu.bath.soak.dns.cmd.DNSCmd;
import edu.bath.soak.dns.model.DNSRecord;

public class AddRecord extends UIDNSChange {
	DNSRecord newRecord;

	@Override
	public void fillCmd(DNSCmd cmd) {
		cmd.insertAdd(newRecord);

	}

	@Override
	public boolean matchesRecord(DNSRecord rec) {
		// TODO Auto-generated method stub
		return false;
	}
}
