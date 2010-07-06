package edu.bath.soak.dhcp;

import java.net.Inet4Address;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;

import edu.bath.soak.dhcp.model.DBBackedDHCPServer;
import edu.bath.soak.dhcp.model.DHCPDao;
import edu.bath.soak.dhcp.model.DHCPReservation;
import edu.bath.soak.dhcp.model.DHCPScope;
import edu.bath.soak.dhcp.model.DHCPServer;
import edu.bath.soak.dhcp.model.StaticDHCPReservation;
import edu.bath.soak.util.MacAddress;

/**
 * Endpoint for DB-Backed DHCP service
 * 
 * This endpoint simply stores Reservation information a database tables
 * 
 * @author cspocc
 * 
 */
public class DBBackedDHCPServiceEndpoint implements DHCPServiceEndPoint {

	DHCPDao dhcpDAO;

	/**
	 * Saves a reservation to the database using the attached {@link DHCPDao}
	 * 
	 * @param server
	 *            the server to save the reservation on
	 * @param res
	 *            the reservation to save
	 * 
	 * @throws DHCPException
	 *             if the endpoint could not be updateed
	 */
	public void createReservation(DHCPServer server, DHCPReservation res)
			throws DHCPException {

		Assert.isInstanceOf(StaticDHCPReservation.class, res);
		StaticDHCPReservation sres = (StaticDHCPReservation) res;

		dhcpDAO.saveReservation(sres);
	}

	/**
	 * Deletes a reservation from the database using the attacehd
	 * {@link DHCPDao}
	 * 
	 * @param server
	 *            the server to delete the reservation from
	 * @param res
	 *            the reservation to delete *
	 * @throws DHCPException
	 *             if the endpoint could not be updateed
	 * 
	 */
	public void deleteReservation(DHCPServer server, DHCPReservation res)
			throws DHCPException {
		Assert.isInstanceOf(StaticDHCPReservation.class, res);
		Assert.notNull(res.getId());

		if (res != null) {
			dhcpDAO.deleteReservation(res.getId());
		}
	}

	/**
	 * Returns all clients in a given scope
	 * 
	 * @param server
	 *            the server to retrieve info from
	 * @param scope
	 *            the scopWe to get clients for
	 * @return a list of DHCP reservation objects
	 * @throws DHCPException
	 *             if the endpoint could not be updateed
	 * 
	 */
	public List<StaticDHCPReservation> getAllClientsInScope(DHCPServer server,
			DHCPScope scope) throws DHCPException {
		return dhcpDAO.getReservationsInScope(scope);
	}

	/**
	 * Gets all scopes stored on a given server
	 * 
	 * @param server
	 *            the server
	 * 
	 * @return a list of all scopes on that server
	 * @throws DHCPException
	 *             if an error occured while contacting the relevant server
	 */
	public List<DHCPScope> getAllScopes(DHCPServer server) throws DHCPException {
		return dhcpDAO.getDHCPScopes(server);
	}

	/**
	 * returns client infor for a single IP address
	 * 
	 * @param server
	 * @param scope
	 * @param address
	 *            the Client address to search fo
	 * @return the static reservation associated with this IP or null if none
	 *         was found
	 * @throws DHCPException
	 *             if an errro occured while retrieving the info
	 */
	public StaticDHCPReservation getClientInfo(DHCPServer server,
			DHCPScope scope, Inet4Address address) throws DHCPException {
		return dhcpDAO.getReservationForIP(scope, address);
	}

	/**
	 * returns client info for a given MAC address in a given scope
	 * 
	 * @param server
	 * @param scope
	 * @param address
	 * @return
	 * @throws DHCPException
	 */
	public StaticDHCPReservation getClientInfo(DHCPServer server,
			DHCPScope scope, MacAddress address) throws DHCPException {
		return dhcpDAO.getReservationForMAC(scope, address);
	}

	/**
	 * Gets scope info for a scope starting with the given IP address
	 * 
	 * @param server
	 * @param scopeAddr
	 *            the base address of the scope to find
	 * @return the specified scope
	 * @throws DHCPException
	 *             if the server could not be contacted
	 * 
	 */
	public DHCPScope getScopeInfo(DHCPServer server, Inet4Address scopeAddr)
			throws DHCPException {
		DHCPScope scope = dhcpDAO.getScopeContainingIp(server, scopeAddr);
		if (scope != null && scope.getMinIP().equals(scopeAddr)) {
			return scope;
		}
		return null;
	}

	public boolean supportsServer(DHCPServer server) {
		return DBBackedDHCPServer.class.equals(server.getClass());
	}

	public int getOrder() {
		return LOWEST_PRECEDENCE;
	}

	@Required
	@Resource(name = "dhcpDAO")
	public void setDhcpDAO(DHCPDao dhcpDAO) {
		this.dhcpDAO = dhcpDAO;
	}

	public DHCPDao getDhcpDAO() {
		return dhcpDAO;
	}

}
