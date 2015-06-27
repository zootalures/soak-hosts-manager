package test.live;

import java.net.Inet4Address;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.test.AbstractTransactionalSpringContextTests;

import test.hosts.SpringSetup;
import edu.bath.soak.dhcp.WSStaticDHCPServiceEndpoint;
import edu.bath.soak.dhcp.SyncedServiceEndPoint.DHCPScopeClientsUpdateInfo;
import edu.bath.soak.dhcp.model.DHCPDao;
import edu.bath.soak.dhcp.model.DHCPScope;
import edu.bath.soak.dhcp.model.StaticDHCPReservation;
import edu.bath.soak.dhcp.model.WSDHCPServer;
import edu.bath.soak.util.MacAddress;
import edu.bath.soak.util.TypeUtils;
import edu.bath.soak.ws.dhcp.DHCPWSClientInfo;
import edu.bath.soak.ws.dhcp.DHCPWSSubnetInfo;

/**
 * Simple live tests to validate the web service endpoint and connector for the
 * DHCP web serice,
 * 
 * @author cspocc
 * 
 */
public class LiveDHCPTests extends AbstractTransactionalSpringContextTests {

	WSDHCPServer server;
	DHCPDao dhcpDao;
	WSStaticDHCPServiceEndpoint wsDhcpEndpoint;
	Logger log = Logger.getLogger(this.getClass());

	public Inet4Address nonEmptyScopeIp;
	public Inet4Address testScopeIp;
	DHCPScope testScope;
	StaticDHCPReservation testReservation;

	public LiveDHCPTests() {
		setAutowireMode(AUTOWIRE_BY_NAME);
	}

	@Override
	protected String[] getConfigLocations() {
		return SpringSetup.DHCP_TEST_LOCS;
	}

	protected void onSetUpInTransaction() throws Exception {
		super.onSetUpInTransaction();
		server = new WSDHCPServer();
		server.setDisplayName("Test DHCP Server");
		server
				.setAgentUrl("tbd_dhcp_wsdl");
		server.setUserName("tbd_dhcp_username");
		server.setPassword("tbd_dhcp_passwd");
		server.setServerIP(TypeUtils.txtToIP("tbd_dhcp_server_ip"));
		dhcpDao.saveDHCPServer(server);

		nonEmptyScopeIp = TypeUtils.txtToIP("138.38.56.0");
		testScopeIp = nonEmptyScopeIp;

		testReservation = new StaticDHCPReservation();
		testReservation.setHostName("ccpc-soaktest");
		testReservation.setMacAddress(MacAddress.fromText("c0:ff:ee:c0:ff:ee"));
		testReservation.setIpAddress(TypeUtils.txtToIP("138.38.42.230"));
		ensureTestIsDeleted();

	}

	@Override
	protected void onTearDownInTransaction() throws Exception {
		super.onTearDownInTransaction();
		ensureTestIsDeleted();
	}

	public void testUpdateAllScopes() throws Exception {
		wsDhcpEndpoint.syncScopesInfo(server);
	}

	public void ensureTestIsDeleted() {
		try {
			DHCPWSClientInfo wsClientInfo = wsDhcpEndpoint.getRemoteClientInfo(
					server, testReservation.getIpAddress());
			if (wsClientInfo != null) {
				wsDhcpEndpoint.getDHCPWSvcSoap(server).deleteReservation(
						server.getServerIP().getHostAddress(),
						testReservation.getScope().getMinIP().getHostAddress(),
						testReservation.getIpAddress().getHostAddress(),
						testReservation.getMacAddress().toString());
				assertNull(wsDhcpEndpoint.getRemoteClientInfo(server,
						testReservation.getIpAddress()));
			}
		} catch (Exception e) {

		}
	}

	public void testCreateReservation() throws Exception {
		testUpdateAllScopes();

		DHCPScope testScope = wsDhcpEndpoint.getScopeInfo(server,
				this.testScopeIp);
		assertNotNull(testScope);
		assertTrue(testScope.containsIp(testReservation.getIpAddress()));
		testReservation.setScope(testScope);
		wsDhcpEndpoint.createReservation(server, testReservation);
		DHCPWSClientInfo clinfo = wsDhcpEndpoint.getRemoteClientInfo(server,
				testReservation.getIpAddress());
		assertEquals(TypeUtils.txtToIP(clinfo.getIPAddress()), testReservation
				.getIpAddress());
		assertEquals(MacAddress.fromText(clinfo.getMacAddress()),
				testReservation.getMacAddress());

	}

	/**
	 * Synchronizes all client infos, checks that re-syncing leads to the same;
	 * 
	 * @throws Exception
	 */
	public void testUpdateClientInfos() throws Exception {
		testUpdateAllScopes();
		for (DHCPScope scope : wsDhcpEndpoint.getAllScopes(server)) {
			DHCPScopeClientsUpdateInfo upd = wsDhcpEndpoint.syncScopeClients(
					server, scope);
			log.trace("Updated scope " + scope + ": added " + upd.getNumAdded()
					+ ", Deleted " + upd.getNumDeleted());

			if (scope.getMinIP().equals(nonEmptyScopeIp)) {
				assertTrue(upd.getNumAdded() > 0);
			}

		}
		DHCPScope testscope = dhcpDao.getScopeContainingIp(server,
				nonEmptyScopeIp);
		assertNotNull(testscope);
		List<StaticDHCPReservation> res = dhcpDao
				.getReservationsInScope(testscope);
		assertTrue(res.size() != 0);

		for (DHCPScope scope : wsDhcpEndpoint.getAllScopes(server)) {
			DHCPScopeClientsUpdateInfo upd = wsDhcpEndpoint.syncScopeClients(
					server, scope);
			assertEquals(upd.getNumAdded(), 0);
			assertEquals(upd.getNumDeleted(), 0);

		}

	}

	public void testGetSubnetInfos() {
		List<DHCPWSSubnetInfo> scopes = wsDhcpEndpoint.getRemoteScopes(server);
		log.trace("Got scopes");
		for (DHCPWSSubnetInfo scope : scopes) {
			log.trace("Got Scope "
					+ WSStaticDHCPServiceEndpoint.DHCPWSSubnetInfo2DHCPRange(
							server, scope, null));
		}
	}

	public void testGetClientInfo() {

	}

	public void testGetClientInfosInfos() throws Exception {
		log.trace("Starting test");
		List<DHCPWSSubnetInfo> scopes = wsDhcpEndpoint.getRemoteScopes(server);
		log.trace("Got scopes");
		for (DHCPWSSubnetInfo scope : scopes) {
			log.trace("Got Scope "
					+ WSStaticDHCPServiceEndpoint.DHCPWSSubnetInfo2DHCPRange(
							server, scope, null));
			DHCPScope dscope = WSStaticDHCPServiceEndpoint
					.DHCPWSSubnetInfo2DHCPRange(server, scope, null);
			List<StaticDHCPReservation> clients = wsDhcpEndpoint
					.getRemoteClientsInScope(server, dscope);
			for (StaticDHCPReservation client : clients) {
				log.trace("Client " + client);
			}
		}
	}

	public void setDhcpDAO(DHCPDao dhcpDao) {
		this.dhcpDao = dhcpDao;
	}

	public void setWsDhcpEndpoint(WSStaticDHCPServiceEndpoint wsDhcpEndpoint) {
		this.wsDhcpEndpoint = wsDhcpEndpoint;
	}

}
