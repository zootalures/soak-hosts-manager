package edu.bath.soak.dhcp;

import java.util.List;

import edu.bath.soak.dhcp.model.DHCPScope;
import edu.bath.soak.dhcp.model.DHCPServer;

/*******************************************************************************
 * Interface for endpoints which support periodical synchronization of
 * information
 * 
 * @author cspocc
 * 
 */
public interface SyncedServiceEndPoint {

	public static class DHCPScopeClientsUpdateInfo {
		DHCPServer server;
		DHCPScope scope;
		int numAdded;
		int numDeleted;

		public DHCPServer getServer() {
			return server;
		}

		public void setServer(DHCPServer server) {
			this.server = server;
		}

		public DHCPScope getScope() {
			return scope;
		}

		public void setScope(DHCPScope scope) {
			this.scope = scope;
		}

		public int getNumAdded() {
			return numAdded;
		}

		public void setNumAdded(int numAdded) {
			this.numAdded = numAdded;
		}

		public int getNumDeleted() {
			return numDeleted;
		}

		public void setNumDeleted(int numDeleted) {
			this.numDeleted = numDeleted;
		}
	}

	/***************************************************************************
	 * Implementors should synchronize all client information between themselves
	 * and the target server.
	 * 
	 * @param server
	 * @return
	 */
	public List<DHCPScopeClientsUpdateInfo> syncClientsOnServer(
			DHCPServer server);

	/***************************************************************************
	 * Implementors should synchronize all scopes between themselves and the
	 * target server.
	 * 
	 * @param server
	 * @throws DHCPException
	 */
	public void syncScopesInfo(DHCPServer server) throws DHCPException;

	/**
	 * Implementors should synchronize clients in the specified scope between
	 * the remote server and themselves.
	 * 
	 * @param server
	 * @param range
	 * @return
	 */
	public DHCPScopeClientsUpdateInfo syncScopeClients(DHCPServer server,
			DHCPScope range);

}
