package test.dns;

import java.net.Inet4Address;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.test.AbstractTransactionalSpringContextTests;
import org.xbill.DNS.ReverseMap;

import test.hosts.SpringSetup;
import edu.bath.soak.cmd.BaseCompositeCommand;
import edu.bath.soak.cmd.CmdException;
import edu.bath.soak.cmd.CommandDispatcherRegistry;
import edu.bath.soak.cmd.CommandDispatcherRegistryImpl;
import edu.bath.soak.cmd.ExecutableCommand;
import edu.bath.soak.dns.DNSHostsInterceptor;
import edu.bath.soak.dns.DNSMgrImpl;
import edu.bath.soak.dns.DNSHostsInterceptor.DNSClashException;
import edu.bath.soak.dns.cmd.DNSCmd;
import edu.bath.soak.dns.cmd.DNSHostCommandFlags;
import edu.bath.soak.dns.model.DNSDao;
import edu.bath.soak.dns.model.DNSHostSettings;
import edu.bath.soak.dns.model.DNSRecord;
import edu.bath.soak.dns.model.ForwardZone;
import edu.bath.soak.dns.model.ReverseZone;
import edu.bath.soak.mgr.HostsManager;
import edu.bath.soak.net.AdviceBasedAddressSpaceManager;
import edu.bath.soak.net.AllocatedAddressPool;
import edu.bath.soak.net.cmd.AlterHostCmd;
import edu.bath.soak.net.model.Host;
import edu.bath.soak.net.model.HostName;
import edu.bath.soak.net.model.NetDAO;
import edu.bath.soak.testutils.MockDNSService;
import edu.bath.soak.testutils.TestData;
import edu.bath.soak.util.MacAddress;
import edu.bath.soak.util.TypeUtils;

public class DNSHostsInterceptorTests extends
		AbstractTransactionalSpringContextTests {

	Logger log = Logger.getLogger(this.getClass());

	NetDAO hostsDAO;
	DNSDao dnsDAO;
	DNSHostsInterceptor dnsHostsInterceptor;
	CommandDispatcherRegistry commandDispatcherRegistry;
	AdviceBasedAddressSpaceManager addressSpaceManager;
	HostsManager hostsManager;
	DNSMgrImpl dnsMgr;
	Host testHost = null;

	public DNSHostsInterceptorTests() {
		setAutowireMode(AUTOWIRE_BY_NAME);
	}

	@Override
	protected String[] getConfigLocations() {
		return SpringSetup.DNS_TEST_LOCS;
	}

	TestData td;
	ForwardZone testFwZone;
	ForwardZone testFwZone2;
	ReverseZone testReverseZone;
	MockDNSService mockDNSService;

	private DNSRecord badCNAMERecord;

	// private DNSRecord badForwardRecord;

	private DNSRecord badReverseRecord;

	@Override
	protected void onSetUpInTransaction() throws Exception {
		td = new TestData(hostsDAO);
		SpringSetup.setUpBasicAcegiAuthentication(true, td.getTestOrgUnit());

		mockDNSService = new MockDNSService();
		dnsMgr.setDnsService(mockDNSService);

		testFwZone = new ForwardZone();
		testFwZone.setDomain("testdomain.");
		testFwZone.setDisplayName("Test forward Zone");
		testFwZone.setDescription("test forward zone");
		testFwZone.setIgnoreHostRegexps(".*\\.campus\\.testdomain\\.");
		testFwZone.setServerIP(TypeUtils.txtToIP("127.0.0.1"));
		dnsDAO.saveZone(testFwZone);

		testFwZone2 = new ForwardZone();
		testFwZone2.setDomain("campus.testdomain.");
		testFwZone2.setDisplayName("Test forward Zone 2");
		testFwZone2.setDescription("test forward zone 2");
		testFwZone2.setServerIP(TypeUtils.txtToIP("127.0.0.1"));

		dnsDAO.saveZone(testFwZone2);

		testReverseZone = new ReverseZone();
		testReverseZone.setDomain("10.in-addr.arpa.");
		testReverseZone.setDescription("TEst Zone");
		testReverseZone.setServerIP(TypeUtils.txtToIP("127.0.0.1"));

		dnsDAO.saveZone(testReverseZone);
		testHost = null;

		addressSpaceManager.setAllocatedAddressPool(new AllocatedAddressPool());
		commandDispatcherRegistry = new CommandDispatcherRegistryImpl();
		commandDispatcherRegistry.registerExpander(dnsHostsInterceptor);
		commandDispatcherRegistry.registerExpander(hostsManager);
		commandDispatcherRegistry.registerDispatcher(dnsMgr);
		commandDispatcherRegistry.registerDispatcher(hostsManager);

	}

	public void testCreateNewHostCheckRecords() throws Exception {
		testHost = new Host();

		testHost.setHostName(hostsDAO
				.getHostNameFromFQDN("testdnshost.testdomain."));
		testHost.setIpAddress(TypeUtils.ipIncrement(td.getTestHost()
				.getIpAddress()));
		testHost.setHostClass(td.getTestHostClass());
		testHost.setMacAddress(MacAddress.fromText("00:11:22:33:44:55"));
		testHost.getOwnership().setOrgUnit(td.getTestOrgUnit());
		AlterHostCmd cmd = new AlterHostCmd();
		cmd.setSpecifyIp(true);
		cmd.setNewHost(testHost);

		BaseCompositeCommand res = commandDispatcherRegistry
				.expandAndImplementCommand(cmd);

		DNSCmd dnsCmd = null;
		for (ExecutableCommand change : res.getAggregateChanges()) {
			if (change instanceof DNSCmd) {
				assertNull(dnsCmd);
				dnsCmd = (DNSCmd) change;
			}
		}

		assertNotNull(dnsCmd);
		assertEquals(2, dnsCmd.getChanges().size());

		assertTrue(dnsCmd.getDeletions().size() == 0);

		DNSRecord fwdRec = new DNSRecord();
		fwdRec.setZone(testFwZone);
		fwdRec.setHostName(testHost.getHostName().toString());
		fwdRec.setTarget(testHost.getIpAddress().getHostAddress());
		fwdRec.setType("A");
		fwdRec.setTtl(3600L);
		assertTrue(dnsCmd.getAdditions().contains(fwdRec));

		DNSRecord revRec = new DNSRecord();
		revRec.setZone(testReverseZone);
		revRec.setHostName(ReverseMap.fromAddress(testHost.getIpAddress())
				.toString());
		revRec.setType("PTR");
		revRec.setTarget(testHost.getHostName().toString());
		revRec.setTtl(3600L);

		assertTrue(dnsCmd.getAdditions().contains(revRec));
		testHost = hostsDAO.findHost(testHost.getHostName());
	}

	public void testChangeNoChange() throws Exception {
		testCreateNewHostCheckRecords();
		AlterHostCmd cmd = new AlterHostCmd();

		cmd.setNewHost(hostsDAO.getHostForEditing(testHost.getId()));
		cmd.setSpecifyIp(true);

		BaseCompositeCommand res = commandDispatcherRegistry
				.expandAndImplementCommand(cmd);
		//
		DNSCmd dnsCmd = null;
		for (ExecutableCommand change : res.getAggregateChanges()) {
			if (change instanceof DNSCmd) {
				assertNull(dnsCmd);
				dnsCmd = (DNSCmd) change;
			}
		}
		assertNull(dnsCmd);
	}

	public void testHostCmdValidatorCreateSucess() throws Exception {
		AlterHostCmd cmd = new AlterHostCmd();
		cmd.setSpecifyIp(true);
		cmd.setNewHost(td.dummyHost(td.getTestSubnet(), "createHost", 66));

	}

	/**
	 * Inserts records which will clash with host H
	 * 
	 * @param host
	 */
	void addBadRecords(Host host) {
		//
		// badForwardRecord = new DNSRecord();
		// badForwardRecord.setZone(testFwZone);
		// badForwardRecord.setHostName(host.getHostName().toString());
		// badForwardRecord.setType("A");
		// badForwardRecord.setTtl(3600L);
		// badForwardRecord.setTarget("10.0.0.41");
		// dnsDAO.saveRecord(badForwardRecord);

		badCNAMERecord = new DNSRecord();
		badCNAMERecord.setZone(testFwZone);
		badCNAMERecord.setHostName(host.getHostName().toString());
		badCNAMERecord.setType("CNAME");
		badCNAMERecord.setTtl(3600L);
		badCNAMERecord.setTarget("badfoohost.boo.testdomain.");
		dnsDAO.saveRecord(badCNAMERecord);

		badReverseRecord = new DNSRecord();
		badReverseRecord.setZone(testReverseZone);
		badReverseRecord.setHostName(ReverseMap
				.fromAddress(host.getIpAddress()).toString());
		badReverseRecord.setType("PTR");
		badReverseRecord.setTtl(3600L);
		badReverseRecord.setTarget("foobarhost.testdomain.");
		dnsDAO.saveRecord(badReverseRecord);
	}

	/**
	 * tests that a spurrious record is deleted when a spurrious records exists
	 * 
	 * @throws Exception
	 */
	public void testChangeIPExistingBadRecord() throws Exception {

		testHost = new Host();
		testHost.setHostName(hostsDAO
				.getHostNameFromFQDN("testdnshost.testdomain."));
		testHost.setIpAddress(TypeUtils.ipIncrement(td.getTestHost()
				.getIpAddress()));
		testHost.setHostClass(td.getTestHostClass());
		testHost.setMacAddress(MacAddress.fromText("00:11:22:33:44:55"));
		testHost.getOwnership().setOrgUnit(td.getTestOrgUnit());
		addBadRecords(testHost);
		AlterHostCmd cmd = new AlterHostCmd();

		cmd.setNewHost(testHost);
		cmd.setSpecifyIp(true);

		commandDispatcherRegistry.setUpCommandDefaults(cmd);
		try {
			commandDispatcherRegistry.expandAndImplementCommand(cmd);
			fail();
		} catch (CmdException ex) {

		}

		DNSHostCommandFlags flags = (DNSHostCommandFlags) cmd.getOptionData()
				.get(DNSHostsInterceptor.DNS_FLAGS_KEY);
		assertNotNull(flags);
		flags.setForceDNSUpdates(true);
		commandDispatcherRegistry.expandAndImplementCommand(cmd);

	}

	public void testValidateUpdateHostBadRecords() throws Exception {
		testCreateNewHostCheckRecords();
		testHost = hostsDAO.getHostForEditing(testHost.getId());
		testHost.setHostName(hostsDAO
				.getHostNameFromFQDN("testdnshost2a.testdomain."));
		testHost.setIpAddress(TypeUtils.ipIncrement(td.getTestHost()
				.getIpAddress()));
		testHost.setHostClass(td.getTestHostClass());
		testHost.setMacAddress(MacAddress.fromText("00:11:22:33:44:55"));
		addBadRecords(testHost);
		AlterHostCmd cmd = new AlterHostCmd();

		cmd.setNewHost(testHost);
		assertTrue(!cmd.isCreation());
		cmd.setSpecifyIp(true);

		try {
			dnsHostsInterceptor.expandCmd(cmd, new BaseCompositeCommand(cmd));
			fail();
		} catch (DNSClashException clash) {
			Set<DNSRecord> clashes = clash.getRecords();
			assertEquals(2, clashes.size());
			assertTrue(clashes.contains(badCNAMERecord));
			// assertTrue(clashes.contains(badForwardRecord));
			assertTrue(clashes.contains(badReverseRecord));

		}

	}

	public void testValidateCreateHostCmdBadRecords() throws Exception {

		testHost = new Host();
		testHost.setHostName(hostsDAO
				.getHostNameFromFQDN("testdnshost.testdomain."));
		testHost.setIpAddress(TypeUtils.ipIncrement(td.getTestHost()
				.getIpAddress()));
		testHost.setHostClass(td.getTestHostClass());
		testHost.setMacAddress(MacAddress.fromText("00:11:22:33:44:55"));
		addBadRecords(testHost);
		AlterHostCmd cmd = new AlterHostCmd();

		cmd.setNewHost(testHost);
		cmd.setSpecifyIp(true);

		try {
			dnsHostsInterceptor.expandCmd(cmd, new BaseCompositeCommand(cmd));
			fail();
		} catch (DNSClashException clash) {
			Set<DNSRecord> clashes = clash.getRecords();
			assertEquals(2, clashes.size());
			assertTrue(clashes.contains(badCNAMERecord));
			// assertTrue(clashes.contains(badForwardRecord));
			assertTrue(clashes.contains(badReverseRecord));

		}

	}

	public void testChangeHostName() throws Exception {
		testCreateNewHostCheckRecords();
		AlterHostCmd cmd = new AlterHostCmd();

		testHost = hostsDAO.getHostForEditing(testHost.getId());
		HostName oldHostName = testHost.getHostName();
		HostName newHostName = hostsDAO.getHostNameFromFQDN("newdnshostname"
				+ oldHostName.getDomain().getSuffix());
		testHost.setHostName(newHostName);

		cmd.setNewHost(testHost);
		cmd.setSpecifyIp(true);

		try {
			dnsHostsInterceptor.expandCmd(cmd, new BaseCompositeCommand(cmd));
		} catch (DNSClashException e) {
			log.trace("Got clash with records " + e.getRecords());
			throw e;
		}

		BaseCompositeCommand res = commandDispatcherRegistry
				.expandAndImplementCommand(cmd);

		DNSCmd dnsCmd = null;
		for (ExecutableCommand change : res.getCommands()) {
			if (change instanceof DNSCmd) {
				assertNull(dnsCmd);
				dnsCmd = (DNSCmd) change;
			}
		}
		assertNotNull(dnsCmd);
		assertEquals(2, dnsCmd.getAdditions().size());
		assertEquals(2, dnsCmd.getDeletions().size());
		DNSRecord oldFwdRec = new DNSRecord();
		oldFwdRec.setZone(testFwZone);
		oldFwdRec.setHostName(oldHostName.toString());
		oldFwdRec.setTarget(testHost.getIpAddress().getHostAddress());
		oldFwdRec.setType("A");
		oldFwdRec.setTtl(3600L);
		assertTrue(dnsCmd.getDeletions().contains(oldFwdRec));

		DNSRecord oldRevRec = new DNSRecord();
		oldRevRec.setZone(testReverseZone);
		oldRevRec.setHostName(ReverseMap.fromAddress(testHost.getIpAddress())
				.toString());
		oldRevRec.setType("PTR");
		oldRevRec.setTarget(oldHostName.toString());
		oldRevRec.setTtl(3600L);

		assertTrue(dnsCmd.getDeletions().contains(oldRevRec));

		DNSRecord fwdRec = new DNSRecord();
		fwdRec.setZone(testFwZone);
		fwdRec.setHostName(testHost.getHostName().toString());
		fwdRec.setTarget(testHost.getIpAddress().getHostAddress());
		fwdRec.setType("A");
		fwdRec.setTtl(3600L);
		assertTrue(dnsCmd.getAdditions().contains(fwdRec));

		DNSRecord revRec = new DNSRecord();
		revRec.setZone(testReverseZone);
		revRec.setHostName(ReverseMap.fromAddress(testHost.getIpAddress())
				.toString());
		revRec.setType("PTR");
		revRec.setTarget(testHost.getHostName().toString());
		revRec.setTtl(3600L);

		assertTrue(dnsCmd.getAdditions().contains(revRec));

	}

	public void testChangeHostIP() throws Exception {
		testCreateNewHostCheckRecords();
		AlterHostCmd cmd = new AlterHostCmd();
		testHost = hostsDAO.getHostForEditing(testHost.getId());
		cmd.setNewHost(testHost);
		cmd.setSpecifyIp(true);
		Inet4Address oldIP = testHost.getIpAddress();
		Inet4Address newIP = TypeUtils.ipIncrement(testHost.getIpAddress());
		testHost.setIpAddress(newIP);
		BaseCompositeCommand res = commandDispatcherRegistry
				.expandAndImplementCommand(cmd);

		DNSCmd dnsCmd = null;
		for (ExecutableCommand change : res.getCommands()) {
			if (change instanceof DNSCmd) {
				assertNull(dnsCmd);
				dnsCmd = (DNSCmd) change;
			}
		}

		assertNotNull(dnsCmd);
		assertEquals(2, dnsCmd.getAdditions().size());
		assertEquals(2, dnsCmd.getDeletions().size());
		DNSRecord oldFwdRec = new DNSRecord();
		oldFwdRec.setZone(testFwZone);
		oldFwdRec.setHostName(testHost.getHostName().toString());
		oldFwdRec.setTarget(oldIP.getHostAddress());
		oldFwdRec.setType("A");
		oldFwdRec.setTtl(3600L);
		assertTrue(dnsCmd.getDeletions().contains(oldFwdRec));

		DNSRecord oldRevRec = new DNSRecord();
		oldRevRec.setZone(testReverseZone);
		oldRevRec.setHostName(ReverseMap.fromAddress(oldIP).toString());
		oldRevRec.setType("PTR");
		oldRevRec.setTarget(testHost.getHostName().toString());
		oldRevRec.setTtl(3600L);

		assertTrue(dnsCmd.getDeletions().contains(oldRevRec));

		DNSRecord fwdRec = new DNSRecord();
		fwdRec.setZone(testFwZone);
		fwdRec.setHostName(testHost.getHostName().toString());
		fwdRec.setTarget(testHost.getIpAddress().getHostAddress());
		fwdRec.setType("A");
		fwdRec.setTtl(3600L);
		assertTrue(dnsCmd.getAdditions().contains(fwdRec));

		DNSRecord revRec = new DNSRecord();
		revRec.setZone(testReverseZone);
		revRec.setHostName(ReverseMap.fromAddress(testHost.getIpAddress())
				.toString());
		revRec.setType("PTR");
		revRec.setTarget(testHost.getHostName().toString());
		revRec.setTtl(3600L);

		assertTrue(dnsCmd.getAdditions().contains(revRec));
	}

	public void testDNSSettingsChangedChangeTTL() throws Exception {
		testCreateNewHostCheckRecords();
		AlterHostCmd cmd = new AlterHostCmd();
		cmd.setNewHost(hostsDAO.getHostForEditing(testHost.getId()));
		cmd.setSpecifyIp(true);
		commandDispatcherRegistry.setUpCommandDefaults(cmd);
		DNSHostCommandFlags flags = (DNSHostCommandFlags) cmd.getOptionData()
				.get(DNSHostsInterceptor.DNS_FLAGS_KEY);
		assertNotNull(flags);

		flags.setHostTTL(1000L);
		commandDispatcherRegistry.expandAndImplementCommand(cmd);

	}

	public void testDNSHostInfoIsSaved() {

		Host th = td.dummyHost(td.getTestSubnet(), "testhost", 24);
		DNSHostSettings settings = new DNSHostSettings();
		settings.setHostTTL(100L);
		th.setConfigSetting(DNSHostsInterceptor.DNS_FLAGS_KEY, settings);
		assertNotNull(th.getConfigSetting(DNSHostsInterceptor.DNS_FLAGS_KEY));
		hostsDAO.saveHost(th,"testcmd");
		Host gh = hostsDAO.getHostForEditing(th.getId());
		assertNotNull(gh.getConfigSetting(DNSHostsInterceptor.DNS_FLAGS_KEY));
	}

	public void setHostsDAO(NetDAO hostsDAO) {
		this.hostsDAO = hostsDAO;
	}

	public void setHostsManager(HostsManager hostsManager) {
		this.hostsManager = hostsManager;
	}

	public void setDnsMgr(DNSMgrImpl dnsMgr) {
		this.dnsMgr = dnsMgr;
	}

	public void setDnsDAO(DNSDao dnsDAO) {
		this.dnsDAO = dnsDAO;
	}

	public void setDnsHostsInterceptor(DNSHostsInterceptor dnsHostsInterceptor) {
		this.dnsHostsInterceptor = dnsHostsInterceptor;
	}

	public void setAddressSpaceManager(
			AdviceBasedAddressSpaceManager addressSpaceManager) {
		this.addressSpaceManager = addressSpaceManager;
	}

}
