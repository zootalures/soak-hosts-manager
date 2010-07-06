package edu.bath.soak.dhcp;

import java.net.Inet4Address;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;

import edu.bath.soak.dhcp.model.DHCPDao;
import edu.bath.soak.dhcp.model.DHCPServer;
import edu.bath.soak.mgr.AddressManagerAdvisor;
import edu.bath.soak.net.model.Host;
import edu.bath.soak.net.model.HostClass;
import edu.bath.soak.net.model.Subnet;
import edu.bath.soak.net.model.HostClass.DHCP_STATUS;

/**
 * {@link AddressManagerAdvisor} which advises the address manager on IP
 * allocations for hosts with host classes which required DHCP
 * 
 * Will advise that an address is OK if: * the host class does not require DHCP *
 * the address in question is within an existing DHCP scope.
 * 
 * @author cspocc
 * 
 */
public class DHCPAddressManagerAdvisor implements AddressManagerAdvisor {

	DHCPDao dhcpDao;

	/**
	 * Returns the addreess manager advice for a given host
	 * 
	 * If a hostclass mandates DHCP this advisor only returns OK if the given IP
	 * address is in a scope which is configured for DHCP.
	 * 
	 * 
	 * @param h
	 *            host to get advice for
	 * @param s
	 *            subnet of host
	 * @param addr
	 *            address to return advice for
	 * @return advice
	 */
	public AddressManagerAdvice getAdviceForAllocation(Host h, Subnet s,
			Inet4Address addr) {
		HostClass hc = h.getHostClass();
		if (hc==null || hc.getDHCPStatus() != DHCP_STATUS.REQUIRED) {
			return AddressManagerAdvice.OK;
		}
		for (DHCPServer server : dhcpDao.getDHCPServers())
			if (null != dhcpDao.getScopeContainingIp(server, addr)) {
				return AddressManagerAdvice.OK;

			}
		return AddressManagerAdvice.NOK;
	}

	@Required
	public void setDhcpDao(DHCPDao dhcpDao) {
		this.dhcpDao = dhcpDao;
	}

	public int getOrder() {
		return 0;
	}
}
