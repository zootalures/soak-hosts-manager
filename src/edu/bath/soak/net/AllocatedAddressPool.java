package edu.bath.soak.net;

import java.net.Inet4Address;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import edu.bath.soak.net.model.Host;

/**
 * A request-scoped placeholder for storing addresses allocated in this request
 * 
 * @author cspocc
 * 
 */
public class AllocatedAddressPool {
	Map<Host, Inet4Address> allocatedAddresses = new HashMap<Host, Inet4Address>();
	Logger log = Logger.getLogger(getClass());

	/**
	 * Stores a given address in the pool
	 * 
	 * @param addr
	 */
	public void allocateAddress(Host h, Inet4Address addr) {
		log.debug("Pool allocating address " + addr.getHostAddress());
		allocatedAddresses.put(h, addr);
	}

	/**
	 * indicates whether or not a given address is allocated
	 * 
	 * @param addr
	 */
	public boolean isAllocated(Inet4Address addr) {
		return allocatedAddresses.values().contains(addr);
	}

	public Inet4Address getAllocation(Host host) {
		return allocatedAddresses.get(host);
	}

	public Host getHostByAllocation(Inet4Address ip) {
		for (Entry<Host, Inet4Address> entry : allocatedAddresses.entrySet()) {
			if (entry.getValue().equals(ip)) {
				return entry.getKey();
			}
		}
		return null;
	}

}
