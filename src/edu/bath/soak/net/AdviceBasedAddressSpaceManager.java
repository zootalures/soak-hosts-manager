package edu.bath.soak.net;

import java.net.Inet4Address;
import java.util.Collection;
import java.util.HashSet;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;

import edu.bath.soak.mgr.AddressManagerAdvisor;
import edu.bath.soak.mgr.AddressSpaceManager;
import edu.bath.soak.mgr.AddressManagerAdvisor.AddressManagerAdvice;
import edu.bath.soak.net.model.Host;
import edu.bath.soak.net.model.HostClass;
import edu.bath.soak.net.model.NetDAO;
import edu.bath.soak.net.model.Subnet;
import edu.bath.soak.util.OrderedComparator;
import edu.bath.soak.util.TypeUtils;

/**
 * Address space manager,
 * 
 * Allocates IP addressed to Hosts based on their scope
 * 
 * uses an {@link AllocatedAddressPool} object which should be scope-local to
 * store allocated addresses
 * 
 * @author cspocc
 * 
 */
public class AdviceBasedAddressSpaceManager implements AddressSpaceManager {

	Logger log = Logger.getLogger(AdviceBasedAddressSpaceManager.class);
	NetDAO hostsDAO;
	AllocatedAddressPool allocatedAddressPool;
	SortedSet<AddressManagerAdvisor> advisors = new TreeSet<AddressManagerAdvisor>(
			new OrderedComparator());

	/**
	 * Selects a new IP address, iterates over all addresses on this subnet and
	 * picks and allocates an address in the pool if it is unused by an extant
	 * host on this subnet and has not already been allocated in the pool
	 * 
	 * @param hc
	 *            ignored by this allocator, may be null
	 * @param subnet
	 *            subnet
	 */
	public Inet4Address allocateIPAddress(Host host, Subnet subnet) {
		Assert.notNull(host);
		Assert.notNull(subnet);

		HostClass hc = host.getHostClass();
		Inet4Address preAllocated = null;

		// bail out early if an address is already allocated for this host and
		// that address is already on the correct subnet
		if (null != (preAllocated = allocatedAddressPool.getAllocation(host))
				&& subnet.containsIp(preAllocated)) {
			return preAllocated;
		}

		// bail out early if host already has a specified IP which on this
		// subnet
		if (null != (preAllocated = host.getIpAddress())
				&& subnet.containsIp(preAllocated)) {
			allocatedAddressPool.allocateAddress(host, preAllocated);
			return preAllocated;
		}

		HashSet<Inet4Address> hs = new HashSet<Inet4Address>();
		log.trace("getting existing hosts for subnet " + subnet);
		hs.addAll(hostsDAO.getUsedIPsInRange(subnet));
		log.trace("got " + hs.size() + " existing hosts for subnet " + subnet);
		Inet4Address foundAddress = null;

		// remember the first non-prefered candidate
		Inet4Address firstNonPreferredCandiate = null;

		nextAddress: for (Inet4Address addr = subnet.getMinUsableAddress(); !addr
				.equals(subnet.getMaxUsableAddress()); addr = TypeUtils
				.ipIncrement(addr)) {
			if (!hs.contains(addr) && !allocatedAddressPool.isAllocated(addr)) {
				AddressManagerAdvice advice = AddressManagerAdvice.OK;
				for (AddressManagerAdvisor advisor : advisors) {
					AddressManagerAdvice advAdvice = advisor
							.getAdviceForAllocation(host, subnet, addr);
					if (advAdvice.equals(AddressManagerAdvice.NOK)) {
						log
								.trace("advisor " + advisor + " says NOK to "
										+ addr);
						// if somebody doesn't want this address quickly reject
						continue nextAddress;
					}
					if (advAdvice.equals(AddressManagerAdvice.PREFER_NOT)) {
						advice = AddressManagerAdvice.PREFER_NOT;
					}
				}
				Assert.isTrue(!advice.equals(AddressManagerAdvice.NOK));

				if (null == firstNonPreferredCandiate
						&& advice.equals(AddressManagerAdvice.PREFER_NOT)) {
					firstNonPreferredCandiate = addr;
				} else if (advice.equals(AddressManagerAdvice.OK)) {
					// we've found our address
					foundAddress = addr;
					break;
				}
			}
		}

		if (null == foundAddress && null != firstNonPreferredCandiate) {
			log.debug("Using non-prefered candidate address "
					+ firstNonPreferredCandiate.getHostAddress() + " for host "
					+ host);
			foundAddress = firstNonPreferredCandiate;
		}

		if (null == foundAddress)
			throw new AddressSpaceFullException(" no free addresses on subnet "
					+ subnet.getName());
		// finally allocate the address
		allocatedAddressPool.allocateAddress(host, foundAddress);
		log.debug("Allocated address " + foundAddress.getHostAddress()
				+ " to host " + host + " with class " + hc
				+ " on subnet " + subnet.getName());
		return foundAddress;

	}

	/**
	 * Returns the number of available addresses on this subnet (hc) is
	 * currently ign
	 */
	public long getNumAvailableAddresses(HostClass hc, Subnet s) {
		HashSet<Inet4Address> hs = new HashSet<Inet4Address>();

		for (Host h : hostsDAO.getAllHostsInRange(s)) {
			hs.add(h.getIpAddress());
		}

		long numFree = 0;
		Inet4Address addr = s.getMinUsableAddress();
		while (TypeUtils.ipCmp(addr, s.getMaxUsableAddress()) <= 0) {
			if (!hs.contains(addr) && !allocatedAddressPool.isAllocated(addr)) {
				numFree++;
			}
			addr = TypeUtils.ipIncrement(addr);
		}
		return numFree;
	}

	/**
	 * Inserts a static reservation for host into the pool,
	 * 
	 * 
	 * @throws AddressInUseException
	 *             if another host has already claimed the specified address
	 */
	public void preAllocateIPAddress(Host h, Inet4Address ip)
			throws AddressInUseException {
		Host goth = allocatedAddressPool.getHostByAllocation(ip);
		if (goth != null && !goth.equals(h)) {
			throw new AddressInUseException("Address " + ip.getHostAddress()
					+ " is already assigned to host " + goth.getHostName());
		}
		allocatedAddressPool.allocateAddress(h, ip);
	}

	public boolean hasFreeAddresses(HostClass hc, Subnet s) {
		return getNumAvailableAddresses(hc, s) > 0;
	}

	/**
	 * Gets the IP which will should be used for this host.
	 * 
	 * This method will throw a runtime exception iff: the host IP is null, and
	 * no previous allocation has been made for this host
	 * 
	 */
	public Inet4Address getAllocatedIP(Host h) {
		Assert.notNull(h);
		Inet4Address addr;
		// have we allocated an address for this host?
		if (null != (addr = allocatedAddressPool.getAllocation(h))) {
			return addr;
		}

		// Oh well
		throw new RuntimeException(
				"Attempt to get host IP for unallocated host " + h);
	}

	@Required
	public void setHostsDAO(NetDAO hostsDAO) {
		this.hostsDAO = hostsDAO;
	}

	@Required
	public void setAllocatedAddressPool(
			AllocatedAddressPool allocatedAddressPool) {
		this.allocatedAddressPool = allocatedAddressPool;
	}

	public void registerAdvisor(AddressManagerAdvisor adv) {
		advisors.add(adv);
	}

	public void setAdvisors(Collection<AddressManagerAdvisor> advisors) {
		// this.advisors.clear();

		this.advisors.addAll(advisors);
	}

}
