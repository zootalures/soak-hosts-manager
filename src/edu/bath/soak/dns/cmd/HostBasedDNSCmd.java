package edu.bath.soak.dns.cmd;

import edu.bath.soak.net.model.Host;

public class HostBasedDNSCmd extends DNSCmd {

	Host host;

	public Host getHost() {
		return host;
	}

	public void setHost(Host host) {
		this.host = host;
	}
	
}
