package test.dhcp;

import java.net.Inet4Address;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.test.AbstractTransactionalSpringContextTests;
import org.springframework.util.Assert;
import org.springframework.validation.Errors;

import test.hosts.SpringSetup;
import edu.bath.soak.cmd.BaseCompositeCommand;
import edu.bath.soak.cmd.CommandDispatcherRegistry;
import edu.bath.soak.cmd.ExecutableCommand;
import edu.bath.soak.cmd.OrderedValidator;
import edu.bath.soak.dhcp.DHCPReservationChangeQuery;
import edu.bath.soak.dhcp.cmd.DHCPCmd;
import edu.bath.soak.dhcp.cmd.DHCPCmdValidator;
import edu.bath.soak.dhcp.model.DBBackedDHCPServer;
import edu.bath.soak.dhcp.model.DHCPDao;
import edu.bath.soak.dhcp.model.DHCPReservationChange;
import edu.bath.soak.dhcp.model.DHCPScope;
import edu.bath.soak.dhcp.model.DHCPServer;
import edu.bath.soak.dhcp.model.StaticDHCPReservation;
import edu.bath.soak.net.cmd.AlterHostCmd;
import edu.bath.soak.net.cmd.DeleteHostUICmd;
import edu.bath.soak.net.model.Host;
import edu.bath.soak.net.model.NetDAO;
import edu.bath.soak.net.model.StoredCommand;
import edu.bath.soak.query.SearchResult;
import edu.bath.soak.testutils.TestData;
import edu.bath.soak.undo.cmd.UndoCmd;
import edu.bath.soak.util.MacAddress;
import edu.bath.soak.util.TypeUtils;

/*******************************************************************************
 * Integration tests for DHCP backend,
 * 
 * runs host creation/deletion/alteration on hosts and checks that relevant DHCP
 * reservations are created
 * 
 * @author cspocc
 * 
 */
public class DHCPHostsInterceptorTests extends
		AbstractTransactionalSpringContextTests {

	Logger log = Logger.getLogger(this.getClass());
	CommandDispatcherRegistry commandDispatcherRegistry;
	DHCPServer testServer;
	NetDAO hostsDAO;
	DHCPDao dhcpDAO;
	DHCPScope testScope;
	TestData td;
	DHCPCmdValidator dhcpCmdValidator;
	Set<DHCPCmd> validatorCalledOn;

	public DHCPHostsInterceptorTests() {
		setAutowireMode(AUTOWIRE_BY_NAME);
	}

	@Override
	protected String[] getConfigLocations() {
		return SpringSetup.DHCP_TEST_LOCS;

	}

	@Override
	protected void onSetUpInTransaction() throws Exception {
		super.onSetUpInTransaction();
		td = new TestData(hostsDAO);
		DBBackedDHCPServer testDHCPServer = new DBBackedDHCPServer();
		testDHCPServer.setDisplayName("Test DB-Backed server");
		dhcpDAO.saveDHCPServer(testDHCPServer);
		testScope = new DHCPScope();
		testScope.setMinIP(TypeUtils.txtToIP("0.0.0.0"));
		testScope.setMaxIP(TypeUtils.txtToIP("255.255.255.255"));
		testScope.setServer(testDHCPServer);
		dhcpDAO.saveScope(testScope);

		OrderedValidator testValidator = new OrderedValidator() {
			public boolean supports(Class clazz) {
				return DHCPCmd.class.isAssignableFrom(clazz);
			}

			public void validate(Object target, Errors errors) {
				Assert.isInstanceOf(DHCPCmd.class, target);
				validatorCalledOn.add((DHCPCmd) target);
			}

			public int getOrder() {
				return 0;
			}
		};

		dhcpCmdValidator.setSubValidators(Collections
				.singletonList(testValidator));
		validatorCalledOn = new HashSet<DHCPCmd>();
		SpringSetup.setUpBasicAcegiAuthentication(false, td.getTestOrgUnit());
	}

	Host testHost;

	/**
	 * tests simple host creation
	 */
	public void testCreateHost() {
		testHost = td.dummyHost(td.getTestSubnet(), "testdhcpHost", 13);
		AlterHostCmd createHost = new AlterHostCmd();
		createHost.setNewHost(testHost);
		createHost.setSpecifyIp(true);

		commandDispatcherRegistry.expandAndImplementCommand(createHost);
		List<StaticDHCPReservation> dhcpReses = dhcpDAO
				.getAllReservationsForMAC(testHost.getMacAddress());
		assertEquals(1, dhcpReses.size());
		StaticDHCPReservation res = dhcpReses.get(0);
		assertEquals(testHost.getMacAddress(), res.getMacAddress());
		assertEquals(testHost.getIpAddress(), res.getIpAddress());
		assertEquals(1, dhcpDAO.getReservationsInScope(testScope).size());

		assertEquals(1, validatorCalledOn.size());
	}

	public void testDeleteHost() {
		testCreateHost();
		validatorCalledOn.clear();

		assertNotNull(testHost);
		DeleteHostUICmd delCmd = new DeleteHostUICmd();
		delCmd.setHost(testHost);
		BaseCompositeCommand result = commandDispatcherRegistry
				.expandAndImplementCommand(delCmd);
		DHCPCmd gotCmd = null;
		for (ExecutableCommand cmd : result.getAggregateChanges()) {
			if (cmd instanceof DHCPCmd) {
				assertNull(gotCmd);
				gotCmd = (DHCPCmd) cmd;
			}
		}
		assertNotNull(gotCmd);
		assertTrue(validatorCalledOn.contains(gotCmd));
		assertEquals(0, dhcpDAO.getReservationsInScope(testScope).size());

	}

	public void testMoveHostChangeIp() {
		testCreateHost();
		validatorCalledOn.clear();
		assertNotNull(testHost);
		AlterHostCmd cmd = new AlterHostCmd();
		testHost = hostsDAO.getHostForEditing(testHost.getId());
		Inet4Address oldIP = testHost.getIpAddress();
		Inet4Address newIP = TypeUtils.ipIncrement(oldIP);

		testHost.setIpAddress(newIP);
		cmd.setSpecifyIp(true);
		cmd.setNewHost(testHost);
		commandDispatcherRegistry.expandAndImplementCommand(cmd);
		assertEquals(0, dhcpDAO.getAllReservationsForIP(oldIP).size());
		List<StaticDHCPReservation> dhcpReses = dhcpDAO
				.getAllReservationsForIP(newIP);
		assertEquals(1, dhcpReses.size());
		StaticDHCPReservation res = dhcpReses.get(0);
		assertEquals(testHost.getMacAddress(), res.getMacAddress());
		assertEquals(1, dhcpDAO.getReservationsInScope(testScope).size());
		assertEquals(1, validatorCalledOn.size());

	}

	public void testAlterHostChangeMAC() {
		testCreateHost();
		validatorCalledOn.clear();
		assertNotNull(testHost);

		AlterHostCmd cmd = new AlterHostCmd();
		testHost = hostsDAO.getHostForEditing(testHost.getId());
		MacAddress oldMac = testHost.getMacAddress();
		MacAddress newMac = MacAddress.fromText("00:55:22:33:11:44");

		testHost.setMacAddress(newMac);
		cmd.setSpecifyIp(true);
		cmd.setNewHost(testHost);
		commandDispatcherRegistry.expandAndImplementCommand(cmd);
		assertEquals(0, dhcpDAO.getAllReservationsForMAC(oldMac).size());
		List<StaticDHCPReservation> dhcpReses = dhcpDAO
				.getAllReservationsForMAC(newMac);
		assertEquals(1, dhcpReses.size());
		StaticDHCPReservation res = dhcpReses.get(0);
		assertEquals(testHost.getIpAddress(), res.getIpAddress());
		assertEquals(1, validatorCalledOn.size());

	}

	public void testUndoStateOnCreate() {
		testHost = td.dummyHost(td.getTestSubnet(), "testdhcpHost", 13);
		AlterHostCmd createHost = new AlterHostCmd();
		createHost.setNewHost(testHost);
		createHost.setSpecifyIp(true);

		BaseCompositeCommand cmd = commandDispatcherRegistry
				.expandAndImplementCommand(createHost);
		StoredCommand stored = hostsDAO.getStoredCommand(cmd.getCommandId());
		BaseCompositeCommand storedCmd = hostsDAO
				.getBaseCommandForStoredCommand(stored);
		assertEquals(cmd, storedCmd);

		UndoCmd undoCmd = new UndoCmd();
		undoCmd.setStoredCommand(stored);

		commandDispatcherRegistry.expandAndImplementCommand(undoCmd);
		assertEquals(0, dhcpDAO.getReservationsInScope(testScope).size());
	}

	public void testChangeStateStoredOnCreate() {
		testHost = td.dummyHost(td.getTestSubnet(), "testdhcpHost", 13);
		AlterHostCmd createHost = new AlterHostCmd();
		createHost.setNewHost(testHost);
		createHost.setSpecifyIp(true);

		BaseCompositeCommand cmd = commandDispatcherRegistry
				.expandAndImplementCommand(createHost);

		assertNotNull(cmd.getCommandId());
		DHCPReservationChangeQuery drcq = new DHCPReservationChangeQuery();
		drcq.setSearchTerm("cmdId:" + cmd.getCommandId());
		SearchResult<DHCPReservationChange> results = dhcpDAO
				.searchDHCPReservationChanges(drcq);
		assertNotNull(results);
		assertTrue(results.getResults().size() > 0);
		StaticDHCPReservation res = results.getResults().get(0)
				.getReservation();
		assertNotNull(res);
		assertEquals(testHost.getMacAddress(), res.getMacAddress());
		assertEquals(testHost.getIpAddress(), res.getIpAddress());

	}

	public void setHostsDAO(NetDAO hostsDAO) {
		this.hostsDAO = hostsDAO;
	}

	public void setDhcpDAO(DHCPDao dhcpDAO) {
		this.dhcpDAO = dhcpDAO;
	}

	public void setCommandDispatcherRegistry(
			CommandDispatcherRegistry commandDispatcherRegistry) {
		this.commandDispatcherRegistry = commandDispatcherRegistry;
	}

	public void setDhcpCmdValidator(DHCPCmdValidator dhcpCmdValidator) {
		this.dhcpCmdValidator = dhcpCmdValidator;
	}

}
