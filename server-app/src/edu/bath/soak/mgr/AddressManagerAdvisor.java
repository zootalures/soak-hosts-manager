package edu.bath.soak.mgr;

import java.net.Inet4Address;

import edu.bath.soak.net.model.Host;
import edu.bath.soak.net.model.Subnet;

/**
 * Call-out interface for address manager which allows other components to
 * influence the allocation of IP addresses.
 * 
 * @author cspocc
 * 
 */
public interface AddressManagerAdvisor extends org.springframework.core.Ordered {
	/**
	 * 
	 * @author cspocc
	 * 
	 */
	public enum AddressManagerAdvice {
		/**
		 * Using this address is OK
		 */
		OK,
		/**
		 * Using this address is possible but it may result in conflicts 
		 * 
		 */
		PREFER_NOT,
		/**
		 * Using this address is not OK. 
		 */
		NOK
	}

	/**
	 * Determines if a given address is acceptable
	 * 
	 * @param h the host to allocate this address to
	 * @param s the subnet upon which this address will be allocated
	 * @param addr the address to allcoate to this host. 
	 * @return advice about whether or not this address should be used. 
	 */
	AddressManagerAdvice getAdviceForAllocation(Host h, Subnet s,
			Inet4Address addr);
}
