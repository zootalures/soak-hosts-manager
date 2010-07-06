package edu.bath.soak.dhcp;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;

import edu.bath.soak.dhcp.model.DHCPDao;
import edu.bath.soak.dhcp.model.DHCPScope;
import edu.bath.soak.dhcp.model.DHCPServer;
import edu.bath.soak.dhcp.model.StaticDHCPReservation;
import edu.bath.soak.model.HostDataSource;
import edu.bath.soak.net.model.Host;

/*******************************************************************************
 * 
 * Fills the MAC address for a host, based on an existing reservation with the
 * same IP
 * 
 * @author cspocc
 * 
 */
public class FillMacAddressFromExistingReservation implements HostDataSource {
	DHCPDao dhcpDAO;
	Logger log = Logger.getLogger(FillMacAddressFromExistingReservation.class);

	public String getId() {
		return "DHCP_MAC_FROM_IP";
	}

	public void fillInfoForHost(Host h, boolean overwrite) {
		Assert.notNull(h);
		log.debug("filling host mac from existing DHCP Reservation for host "
				+ h);

		if (h.getIpAddress() == null) {
			log.debug("Skipping host " + h + ", no IP specified");
			return;
		}

		for (DHCPServer server : dhcpDAO.getDHCPServers()) {
			DHCPScope scope = dhcpDAO.getScopeContainingIp(server, h
					.getIpAddress());
			if (scope == null) {
				log.debug("No scope found for IP " + h.getIpAddressTxt());
			}
			StaticDHCPReservation reservation = dhcpDAO.getReservationForIP(
					scope, h.getIpAddress());
			if (reservation != null) {
				if (overwrite || h.getMacAddress() == null) {
					h.setMacAddress(reservation.getMacAddress());
				} else {
					log.debug("Skipping host " + h);

				}

			} else {
				log.debug("no reservation found for " + h);
			}

		}
	}

	public int getOrder() {
		return -100;
	}

	public String getSourceDescription() {
		return "Import MAC addresses from existing DHCP reservations";
	}

	public DHCPDao getDhcpDAO() {
		return dhcpDAO;
	}

	@Required
	public void setDhcpDAO(DHCPDao dhcpDAO) {
		this.dhcpDAO = dhcpDAO;
	}

}
