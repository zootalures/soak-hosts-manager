package edu.bath.soak.dns;

import edu.bath.soak.cmd.CommandProcessor;
import edu.bath.soak.dns.cmd.DNSCmd;
import edu.bath.soak.dns.model.DNSZone;

/**
 * Top-level interface for DNS mananager,
 * 
 * @author cspocc
 * 
 */
public interface DNSMgr extends CommandProcessor<DNSCmd> {

	/**
	 * Returns the appropriate default TTL value for records in this zone.
	 * 
	 * @param zone
	 * @return
	 */
	public long getDefaultTTL(DNSZone zone);
	
}
