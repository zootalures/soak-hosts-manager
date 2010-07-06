package edu.bath.soak.mgr;

import java.net.Inet4Address;

import edu.bath.soak.net.AdviceBasedAddressSpaceManager;
import edu.bath.soak.net.model.Host;
import edu.bath.soak.net.model.HostClass;
import edu.bath.soak.net.model.Subnet;

/**
 * Interface for allocating and requesting information about the free addresses
 * on given subnets
 * 
 * @see AdviceBasedAddressSpaceManager
 * @author cspocc
 * 
 */
public interface AddressSpaceManager {

	public static class AddressSpaceFullException extends RuntimeException {
		public AddressSpaceFullException(String msg) {
			super(msg);
		}

		public AddressSpaceFullException(String msg, Throwable t) {
			super(msg, t);
		}
	}

	/**
	 * 
	 * @author cspocc
	 * 
	 */
	public static class AddressInUseException extends RuntimeException {
		public AddressInUseException(String msg) {
		}

	}

	/**
	 * Statically assign a host /ip to the pool This can be used when adding a
	 * mixture of pre-assigned and automaticallly allocated addresses
	 * 
	 * After an address is assigned then
	 * 
	 * @param h
	 * @param ip
	 * @throws AddressInUseException
	 *             when the ip address has already been allocated to a different
	 *             host
	 */
	public void preAllocateIPAddress(Host h, Inet4Address ip)
			throws AddressInUseException;

	/**
	 * Determine an IP address for this host in this subnet
	 * 
	 * @param h
	 * @param s
	 * @return a new IP address, this address is stored internally by the
	 *         allocator
	 */
	public java.net.Inet4Address allocateIPAddress(Host h, Subnet s);

	/**
	 * Returns the IP address allocated to a given host
	 * 
	 * @param h
	 * @return
	 */
	public java.net.Inet4Address getAllocatedIP(Host h);

	/**
	 * returns true if a subnet has one or more addresses that can be allocated
	 * 
	 * @param h
	 * @param s
	 * @return
	 */
	public boolean hasFreeAddresses(HostClass h, Subnet s);

	/**
	 * Returns the number of addresses on a subnet which can be used by a
	 * particular host class.
	 * 
	 * @param hc
	 * @param s
	 * @return
	 */
	public long getNumAvailableAddresses(HostClass hc, Subnet s);
}
