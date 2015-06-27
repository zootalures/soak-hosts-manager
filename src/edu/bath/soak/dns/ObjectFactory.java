package edu.bath.soak.dns;

import javax.xml.bind.annotation.XmlRegistry;

import edu.bath.soak.dns.cmd.CleanUpUnusedDNSRecordsCmd;
import edu.bath.soak.dns.cmd.DNSChange;
import edu.bath.soak.dns.cmd.DNSCmd;
import edu.bath.soak.dns.cmd.DNSHostCommandFlags;

@XmlRegistry
public class ObjectFactory extends edu.bath.soak.ObjectFactory {

	public DNSCmd createDNSCmd() {
		return new DNSCmd();
	}

	public DNSChange createDNSChange() {
		return new DNSChange();
	}

	public DNSHostCommandFlags createDNSHostCommandFlags() {
		return new DNSHostCommandFlags();
	}

	public CleanUpUnusedDNSRecordsCmd createCleanUpUnusedDNSRecordsCmd() {
		return new CleanUpUnusedDNSRecordsCmd();
	}
}
