package edu.bath.soak.dhcp;

import java.net.Inet4Address;
import java.util.List;

import org.springframework.core.Ordered;

import edu.bath.soak.dhcp.model.DHCPReservation;
import edu.bath.soak.dhcp.model.DHCPScope;
import edu.bath.soak.dhcp.model.DHCPServer;
import edu.bath.soak.dhcp.model.StaticDHCPReservation;

/**
 * 
 * 
 * @author cspocc
 * 
 */
public interface DHCPServiceEndPoint extends Ordered {

	/***************************************************************************
	 * Deletes a given reservation by it's IP address
	 * 
	 * @param address
	 * @throws DHCPException
	 *             if the server cannot be contacted
	 */
	public void deleteReservation(DHCPServer server,DHCPReservation res)
			throws DHCPException;

	/**
	 * Gets the client information
	 * 
	 * @param address
	 *            the IPv4 address of the client
	 * @return a StaticDHCPReservation object describing the client or null if
	 *         the client is not found
	 * @throws DHCPException
	 *             If there is an error contacting the DHCP server
	 */
	public StaticDHCPReservation getClientInfo(DHCPServer server,
			DHCPScope scope, Inet4Address address) throws DHCPException;

	/**
	 * Returns a list of all DHCP clients in a given network scope
	 * 
	 * @param scope
	 *            The IPv4 Base address of the scope to get information for
	 * 
	 * @return a List of StaticDHCPReservation Objects for the given scope
	 * @throws DHCPException
	 *             If there is an error contacting the DHCP server
	 */
	public List<StaticDHCPReservation> getAllClientsInScope(DHCPServer server,
			DHCPScope scope) throws DHCPException;

	/**
	 * Gets all IP scopes which are managed by the DHCP server
	 * 
	 * @return an array of DHCPScope objects describing scopes on the DHCP
	 *         server
	 * @throws DHCPException
	 *             if there is an error contacting the DHCP server
	 */
	public List<DHCPScope> getAllScopes(DHCPServer server) throws DHCPException;

	/**
	 * Gets information about a specific scope on the DHCP server
	 * 
	 * @param scopeAddr
	 *            the IPv4 base address for the scope in question
	 * @return a DHCPScope object for the scope in question or null if the scope
	 *         is not found
	 * @throws DHCPException
	 *             if there is an error contacting the DHCP server
	 */
	public DHCPScope getScopeInfo(DHCPServer server, Inet4Address scopeAddr)
			throws DHCPException;

	/**
	 * Makes A DHCP Reservation for the given IP address on the DHCP server
	 * 
	 * @param address
	 *            The IP address to assign to the client
	 * @param mac
	 *            The MAC address of the client
	 * @param hostName
	 *            The Host name to give to the client (may be empty or null)
	 * @param comment
	 *            The comment to associate with the client (may be empty or
	 *            null)
	 * @throws DHCPException
	 *             if there is an error contacting the DHCP server
	 */
	public void createReservation(DHCPServer server,DHCPReservation res)
			throws DHCPException;
	
	public boolean supportsServer(DHCPServer server );
	
	
	
}
