package edu.bath.soak.dhcp;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import edu.bath.soak.cmd.CommandProcessor;
import edu.bath.soak.dhcp.cmd.DHCPCmd;
import edu.bath.soak.dhcp.model.DHCPServer;

/**
 * Entry point for DHCP backends
 * 
 * @author cspocc
 * 
 */
public interface DHCPManager extends CommandProcessor<DHCPCmd> {

	public DHCPServiceEndPoint getEndpointForServer(DHCPServer s);

	public void registerEndPoint(DHCPServiceEndPoint endPoint);

	/***************************************************************************
	 * Updates all scopes on all udaptable DHCP servers which have not been
	 * updated since now - updateIntevalMs
	 * 
	 * @param updateIntervalMs
	 */
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void updateAllDhcpServers(long updateIntervalMs);
}
