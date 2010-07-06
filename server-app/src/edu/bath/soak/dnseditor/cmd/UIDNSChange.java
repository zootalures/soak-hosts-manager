package edu.bath.soak.dnseditor.cmd;

import java.io.Serializable;

import edu.bath.soak.dns.cmd.DNSCmd;
import edu.bath.soak.dns.model.DNSRecord;

public abstract class UIDNSChange implements Serializable{
	public abstract boolean matchesRecord(DNSRecord rec);
	public abstract void fillCmd(DNSCmd cmd);
}
