package test.undo;

import java.io.ByteArrayOutputStream;
import java.net.Inet4Address;

import org.hibernate.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.test.AbstractTransactionalSpringContextTests;
import org.springframework.test.AssertThrows;

import test.hosts.SpringSetup;
import edu.bath.soak.cmd.BaseCompositeCommand;
import edu.bath.soak.cmd.CommandDispatcherRegistry;
import edu.bath.soak.dhcp.model.DHCPDao;
import edu.bath.soak.dns.DNSMgrImpl;
import edu.bath.soak.dns.model.DNSDao;
import edu.bath.soak.dns.model.ForwardZone;
import edu.bath.soak.dns.model.ReverseZone;
import edu.bath.soak.net.cmd.AlterHostCmd;
import edu.bath.soak.net.cmd.DeleteHostUICmd;
import edu.bath.soak.net.model.Host;
import edu.bath.soak.net.model.NetDAO;
import edu.bath.soak.net.model.StoredCommand;
import edu.bath.soak.testutils.MockDNSService;
import edu.bath.soak.testutils.TestData;
import edu.bath.soak.undo.UndoNotSupportedException;
import edu.bath.soak.undo.cmd.UndoCmd;
import edu.bath.soak.util.TypeUtils;
import edu.bath.soak.xml.SoakXMLManager;

public class TestUndoAlterHosts extends AbstractTransactionalSpringContextTests {
	NetDAO hostsDAO;

	TestData td;
	SoakXMLManager xmlManager;
	CommandDispatcherRegistry commandDispatcherRegistry;

	public TestUndoAlterHosts() {
		setAutowireMode(AUTOWIRE_BY_NAME);

	}

	DHCPDao dhcpDAO;
	DNSDao dnsDAO;
	DNSMgrImpl dnsMgr;
	ForwardZone testFwZone;
	ReverseZone testReverseZone;

	@Override
	protected void onSetUpInTransaction() throws Exception {
		// TODO Auto-generated method stub
		super.onSetUpInTransaction();
		td = new TestData(hostsDAO);
		SpringSetup.setUpBasicAcegiAuthentication(false, td.getTestOrgUnit());

		MockDNSService mockDNSService = new MockDNSService();
		dnsMgr.setDnsService(mockDNSService);

		testFwZone = new ForwardZone();
		testFwZone.setDomain("testdomain.");
		testFwZone.setDisplayName("Test forward Zone");
		testFwZone.setDescription("test forward zone");
		testFwZone.setIgnoreHostRegexps(".*\\.campus\\.testdomain\\.");
		testFwZone.setServerIP(TypeUtils.txtToIP("127.0.0.1"));
		dnsDAO.saveZone(testFwZone);

		testReverseZone = new ReverseZone();
		testReverseZone.setDomain("10.in-addr.arpa.");
		testReverseZone.setDescription("TEst Zone");
		testReverseZone.setServerIP(TypeUtils.txtToIP("127.0.0.1"));

		dnsDAO.saveZone(testReverseZone);

	}

	@Override
	protected String[] getConfigLocations() {
		return SpringSetup.ALL_TEST_LOCS;

	}

	public void testAlterHostCmdSeralization() {

		AlterHostCmd cmd = new AlterHostCmd();
		Host testHost = td.dummyHost(td.getTestSubnet(), "testhost", 10);
		cmd.setNewHost(testHost);
		cmd.setSpecifyIp(true);
		BaseCompositeCommand result = commandDispatcherRegistry
				.expandCommand(cmd);
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		xmlManager.marshall(result, bos);

		String xml = new String(bos.toByteArray());
		assertNotNull(xml);

		logger.debug("Got XML:" + xml);
		StoredCommand sc = new StoredCommand();
		sc.setCommandXML(xml);
		BaseCompositeCommand got = hostsDAO.getBaseCommandForStoredCommand(sc);
		assertEquals(result.getBaseChange(), got.getBaseChange());
		assertEquals(result.getCommands().size(), got.getCommands().size());

		assertEquals(result, got);
	}

	BaseCompositeCommand lastCommandResult;

	public void testUndoCreateSingleHost() {
		AlterHostCmd cmd = new AlterHostCmd();
		Host testHost = td.dummyHost(td.getTestSubnet(), "testhost", 10);
		cmd.setNewHost(testHost);
		cmd.setSpecifyIp(true);
		BaseCompositeCommand result = commandDispatcherRegistry
				.expandAndImplementCommand(cmd);
		assertNotNull(result.getCommandId());

		final Host h = hostsDAO.findHost(testHost.getIpAddress());

		assertNotNull(h);
		assertEquals(testHost, h);

		StoredCommand savedCommand = hostsDAO.getStoredCommand(result
				.getCommandId());

		assertNotNull(savedCommand);
		BaseCompositeCommand savedResult = hostsDAO
				.getBaseCommandForStoredCommand(savedCommand);
		assertNotNull(savedResult);
		assertEquals(result, savedResult);
		UndoCmd undoCmd = new UndoCmd();
		undoCmd.setStoredCommand(savedCommand);
		BaseCompositeCommand undoResult = commandDispatcherRegistry
				.expandAndImplementCommand(undoCmd);

		lastCommandResult = undoResult;
		assertNotNull(undoResult);
		assertEquals(result.getAggregateChanges().size(), undoResult
				.getAggregateChanges().size());

		new AssertThrows(ObjectNotFoundException.class) {
			@Override
			public void test() throws Exception {
				hostsDAO.loadHost(h.getId());
			}
		}.runTest();
	}

	public void testUndoDeleteSingleHost() {
		AlterHostCmd cmd = new AlterHostCmd();
		final Host testHost = td.dummyHost(td.getTestSubnet(), "testhost", 10);
		cmd.setNewHost(testHost);
		cmd.setSpecifyIp(true);

		final BaseCompositeCommand result = commandDispatcherRegistry
				.expandAndImplementCommand(cmd);
		Host oldHost = hostsDAO.getHostForEditing(testHost.getId());
		assertNotNull(oldHost);
		DeleteHostUICmd deleteHostUICmd = new DeleteHostUICmd();
		deleteHostUICmd.setHost(oldHost);
		final BaseCompositeCommand delresult = commandDispatcherRegistry
				.expandAndImplementCommand(deleteHostUICmd);
		new AssertThrows(ObjectNotFoundException.class) {
			@Override
			public void test() throws Exception {
				hostsDAO.loadHost(testHost.getId());
			}
		}.runTest();

		// Check we can't undo the original command
		new AssertThrows(UndoNotSupportedException.class) {
			@Override
			public void test() throws Exception {
				UndoCmd undo = new UndoCmd();
				undo.setStoredCommand(hostsDAO.getStoredCommand(result
						.getCommandId()));
				commandDispatcherRegistry.expandAndImplementCommand(undo);
			}

		}.runTest();

		UndoCmd undoDelete = new UndoCmd();
		undoDelete.setStoredCommand(hostsDAO.getStoredCommand(delresult
				.getCommandId()));

		BaseCompositeCommand undoDeleteResult = commandDispatcherRegistry
				.expandCommand(undoDelete);

		commandDispatcherRegistry.implementBaseCommand(undoDeleteResult);
		Host restoredHost = hostsDAO.findHost(oldHost.getIpAddress());
		assertEquals(oldHost, restoredHost);
	}

	public void testUndoAlterHost() {

		AlterHostCmd cmd = new AlterHostCmd();
		Host testHost = td.dummyHost(td.getTestSubnet(), "testhost", 10);
		cmd.setNewHost(testHost);
		cmd.setSpecifyIp(true);
		BaseCompositeCommand result = commandDispatcherRegistry
				.expandAndImplementCommand(cmd);
		assertNotNull(result.getCommandId());
		Inet4Address oldAddress = testHost.getIpAddress();
		final Host h = hostsDAO.findHost(oldAddress);

		assertNotNull(h);
		assertEquals(testHost, h);

		cmd = new AlterHostCmd();
		cmd.setSpecifyIp(true);
		cmd.setNewHost(hostsDAO.getHostForEditing(testHost.getId()));
		Inet4Address newAddress = TypeUtils
				.ipIncrement(testHost.getIpAddress());
		cmd.getNewHost().setIpAddress(newAddress);
		result = commandDispatcherRegistry.expandAndImplementCommand(cmd);
		assertNotNull(result.getCommandId());

		StoredCommand savedCommand = hostsDAO.getStoredCommand(result
				.getCommandId());
		assertNotNull(savedCommand);
		BaseCompositeCommand savedResult = hostsDAO
				.getBaseCommandForStoredCommand(savedCommand);
		assertNotNull(savedResult);
		assertEquals(result, savedResult);

		UndoCmd undoCmd = new UndoCmd();
		undoCmd.setStoredCommand(savedCommand);
		BaseCompositeCommand undoResult = commandDispatcherRegistry
				.expandAndImplementCommand(undoCmd);

		Host undoneHost = hostsDAO.loadHost(testHost.getId());
		assertNotNull(undoneHost);

		assertNotNull(undoResult);

		assertEquals(oldAddress, undoneHost.getIpAddress());

	}

	@Required
	public void setHostsDAO(NetDAO hostsDAO) {
		this.hostsDAO = hostsDAO;
	}

	@Required
	public void setCommandDispatcherRegistry(
			CommandDispatcherRegistry commandDispatcherRegistry) {
		this.commandDispatcherRegistry = commandDispatcherRegistry;
	}

	@Required
	public void setXmlManager(SoakXMLManager xmlManager) {
		this.xmlManager = xmlManager;
	}

	public void setDnsMgr(DNSMgrImpl dnsManager) {
		this.dnsMgr = dnsManager;
	}

	@Required
	public void setDnsDAO(DNSDao dnsDAO) {
		this.dnsDAO = dnsDAO;
	}

	@Required
	public void setDhcpDAO(DHCPDao dhcpDAO) {
		this.dhcpDAO = dhcpDAO;
	}

}
