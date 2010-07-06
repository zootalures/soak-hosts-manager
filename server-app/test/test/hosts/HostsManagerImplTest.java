package test.hosts;

import java.net.Inet4Address;
import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.test.AbstractTransactionalSpringContextTests;

import edu.bath.soak.cmd.BaseCompositeCommand;
import edu.bath.soak.cmd.CommandDispatcherRegistry;
import edu.bath.soak.cmd.CommandDispatcherRegistryImpl;
import edu.bath.soak.mgr.HostsManager;
import edu.bath.soak.net.AdviceBasedAddressSpaceManager;
import edu.bath.soak.net.AllocatedAddressPool;
import edu.bath.soak.net.HostsManagerImpl;
import edu.bath.soak.net.cmd.AlterHostCmd;
import edu.bath.soak.net.model.Host;
import edu.bath.soak.net.model.HostAlias;
import edu.bath.soak.net.model.HostName;
import edu.bath.soak.net.model.NetDAO;
import edu.bath.soak.net.model.HostAlias.HostAliasType;
import edu.bath.soak.testutils.TestData;
import edu.bath.soak.util.MacAddress;
import edu.bath.soak.util.TypeUtils;

/**
 * Middle tier integration tests for host operations
 * 
 * @author cspocc
 * 
 */
public class HostsManagerImplTest extends
		AbstractTransactionalSpringContextTests {

	Logger log = Logger.getLogger(this.getClass());

	NetDAO hostsDAO;
	HostsManager hostsManager;
	AdviceBasedAddressSpaceManager addressSpaceManager;
	CommandDispatcherRegistry commandDispatcherRegistry;
	Host testHost = null;
	String createHostDomainName = "testdomain.";
	String createHostName = "createdhost." + createHostDomainName;

	public HostsManagerImplTest() {
		setAutowireMode(AUTOWIRE_BY_NAME);
	}

	@Override
	protected String[] getConfigLocations() {
		return SpringSetup.BASIC_TEST_LOCS;
	}

	TestData td;

	@Override
	protected void onSetUpInTransaction() throws Exception {

		super.onSetUpInTransaction();
		commandDispatcherRegistry = new CommandDispatcherRegistryImpl();
		commandDispatcherRegistry.registerExpander(hostsManager);
		commandDispatcherRegistry.registerDispatcher(hostsManager);

		addressSpaceManager.setAllocatedAddressPool(new AllocatedAddressPool());
		addressSpaceManager.setAdvisors(new ArrayList());
		td = new TestData(hostsDAO);
		testHost = null;
		SpringSetup.setUpBasicAcegiAuthentication(false, td.getTestOrgUnit());

	}

	/**
	 * Simple host creation with no IP set.
	 * 
	 * @throws Exception
	 */
	public void testCreateHostChooseIPSuccess() throws Exception {
		testHost = td.dummyHost(td.getTestSubnet(), "testinghost", 10);
		AlterHostCmd cmd = new AlterHostCmd();
		Host h = cmd.getNewHost();
		h.setDescription("host description");
		h.setHostName(testHost.getHostName());
		h.setHostClass(testHost.getHostClass());
		h.setMacAddress(testHost.getMacAddress());
		h.getOwnership().setOrgUnit(testHost.getOwnership().getOrgUnit());
		cmd.setSpecifyIp(false);
		cmd.setSubnet(td.getTestSubnet());

		BaseCompositeCommand result = commandDispatcherRegistry
				.expandAndImplementCommand(cmd);
		assertNotNull(result);

		Host nh = hostsDAO.findHost(testHost.getHostName().toString());
		assertNotNull(nh);
		assertEquals(nh.getDescription(), h.getDescription());
		testHost = nh;

	}

	/**
	 * Simple host creation with specific IP
	 * 
	 * @throws Exception
	 */
	public void testCreateHostUseSpecificIPSuccess() throws Exception {
		testHost = td.dummyHost(td.getTestSubnet(), "testinghost", 10);
		AlterHostCmd cmd = new AlterHostCmd();
		Host h = cmd.getNewHost();
		h.setDescription("host description");
		h.setHostName(testHost.getHostName());
		h.setHostClass(testHost.getHostClass());
		h.setMacAddress(testHost.getMacAddress());
		h.setIpAddress(testHost.getIpAddress());
		h.getOwnership().setOrgUnit(testHost.getOwnership().getOrgUnit());

		cmd.setSpecifyIp(true);

		BaseCompositeCommand result = commandDispatcherRegistry
				.expandAndImplementCommand(cmd);
		assertNotNull(result);

		Host nh = hostsDAO.findHost(testHost.getHostName());
		assertNotNull(nh);
		assertEquals(h.getDescription(), nh.getDescription());
		assertEquals(testHost.getIpAddress(), h.getIpAddress());
		testHost = nh;

	}

	/**
	 * Modification of host
	 * 
	 * @throws Exception
	 */
	public void testUpdateHostChangeIPSuccess() throws Exception {
		testCreateHostChooseIPSuccess();

		Inet4Address oldIP = testHost.getIpAddress();
		testHost = hostsDAO.getHostForEditing(testHost.getId());

		AlterHostCmd edit = new AlterHostCmd();
		edit.setNewHost(testHost);
		Inet4Address newIP = TypeUtils.ipMath(oldIP, 10);
		log.trace("Change host IP from " + oldIP.getHostAddress() + " to "
				+ newIP.getHostAddress());

		testHost.setIpAddress(newIP);
		edit.setSpecifyIp(true);
		BaseCompositeCommand result = commandDispatcherRegistry
				.expandAndImplementCommand(edit);
		assertNotNull(result);
		assertNull(hostsDAO.findHost(oldIP));
		Host nh = hostsDAO.findHost(newIP);
		assertNotNull(nh);
		testHost = nh;

	}

	/**
	 * Modification host host MAC
	 * 
	 * @throws Exception
	 */
	public void testUpdateHostChangeMACSuccess() throws Exception {
		testCreateHostChooseIPSuccess();
		assertNotNull(testHost);
		MacAddress oldMac = testHost.getMacAddress();
		testHost = hostsDAO.getHostForEditing(testHost.getId());
		assertNotNull(testHost);
		MacAddress newMac = MacAddress.fromText("c0:ff:ee:c0:ff:ee");
		AlterHostCmd edit = new AlterHostCmd();
		edit.setNewHost(testHost);
		testHost.setMacAddress(newMac);
		edit.setSpecifyIp(true);
		BaseCompositeCommand result = commandDispatcherRegistry
				.expandAndImplementCommand(edit);
		assertNotNull(result);
		assertNull(hostsDAO.findHost(oldMac));
		Host nh = hostsDAO.findHost(newMac);
		assertNotNull(nh);
		testHost = nh;

	}

	/**
	 * Modification of host name
	 * 
	 * @throws Exception
	 */
	public void testUpdateHostChangeHostNameSuccess() throws Exception {
		testCreateHostChooseIPSuccess();
		assertNotNull(testHost);
		HostName oldName = testHost.getHostName();

		testHost = hostsDAO.getHostForEditing(testHost.getId());

		assertNotNull(testHost);

		HostName newName = new HostName();
		newName.setDomain(oldName.getDomain());
		newName.setName("testing-host2");
		AlterHostCmd edit = new AlterHostCmd();
		edit.setNewHost(testHost);
		testHost.setHostName(newName);
		edit.setSpecifyIp(true);
		commandDispatcherRegistry.expandAndImplementCommand(edit);

		assertNull(hostsDAO.findHost(oldName));
		Host nh = hostsDAO.findHost(newName);
		assertNotNull(nh);
		testHost = nh;

	}

	/**
	 * creation of host with alias
	 * 
	 * @throws Exception
	 */
	public void testCreateHostWithAliases() throws Exception {
		testHost = td.dummyHost(td.getTestSubnet(), "testinghost", 10);

		AlterHostCmd cmd = new AlterHostCmd();
		Host h = cmd.getNewHost();
		h.setDescription("host description");
		h.setHostName(testHost.getHostName());
		h.setHostClass(testHost.getHostClass());
		h.setMacAddress(testHost.getMacAddress());
		h.getOwnership().setOrgUnit(testHost.getOwnership().getOrgUnit());
		HostAlias ha1 = new HostAlias();
		HostName alias1 = new HostName();
		alias1.setName("testinhost-alias");
		alias1.setDomain(td.getTestNameDomain());
		ha1.setAlias(alias1);
		ha1.setType(HostAliasType.CNAME);
		ha1.setHost(h);
		h.getHostAliases().add(ha1);

		cmd.setSpecifyIp(false);
		cmd.setSubnet(td.getTestSubnet());

		BaseCompositeCommand result = commandDispatcherRegistry
				.expandAndImplementCommand(cmd);

		assertNotNull(result);

		Host nh = hostsDAO.findHost(testHost.getHostName().toString());
		assertNotNull(nh);
		assertEquals(nh.getDescription(), h.getDescription());
		assertEquals(nh.getHostAliases().toArray(new HostAlias[] {})[0], ha1);
		testHost = nh;

	}

	/**
	 * Modification of host with aliases
	 * 
	 * @throws Exception
	 */
	public void testUpdateHostSetAliases() throws Exception {
		testCreateHostChooseIPSuccess();
		Host h = hostsDAO.getHostForEditing(testHost.getId());
		assertNotNull(h);

		HostAlias ha1 = new HostAlias();
		HostName alias1 = new HostName();
		alias1.setName("testinhost-alias");
		alias1.setDomain(td.getTestNameDomain());
		ha1.setAlias(alias1);
		ha1.setType(HostAliasType.CNAME);
		ha1.setHost(h);
		h.getHostAliases().add(ha1);

		AlterHostCmd cmd = new AlterHostCmd();
		cmd.setNewHost(h);
		cmd.setSpecifyIp(true);

		BaseCompositeCommand result = commandDispatcherRegistry
				.expandAndImplementCommand(cmd);

		assertNotNull(result);

		Host nh = hostsDAO.findHost(testHost.getHostName().toString());
		assertNotNull(nh);
		assertEquals(1, nh.getHostAliases().size());
		assertEquals(nh.getHostAliases().toArray(new HostAlias[] {})[0], ha1);
		testHost = nh;

	}

	public void testUpdateHostChangeAliases() throws Exception {
		testCreateHostWithAliases();
		Host h = hostsDAO.getHostForEditing(testHost.getId());
		assertNotNull(h);

		HostAlias ha1 = h.getHostAliases().get(0);
		HostName alias1 = new HostName();
		alias1.setName("testinhost-alias1");
		alias1.setDomain(td.getTestNameDomain());
		ha1.setAlias(alias1);

		HostAlias ha2 = new HostAlias();
		HostName alias2 = new HostName();
		alias2.setName("testinhost-alias2");
		alias2.setDomain(td.getTestNameDomain());
		ha2.setAlias(alias2);
		ha2.setType(HostAliasType.AREC);
		ha2.setHost(h);
		h.getHostAliases().add(ha2);

		AlterHostCmd cmd = new AlterHostCmd();
		cmd.setNewHost(h);
		cmd.setSpecifyIp(true);

		BaseCompositeCommand result = commandDispatcherRegistry
				.expandAndImplementCommand(cmd);
		assertNotNull(result);

		Host nh = hostsDAO.findHost(testHost.getHostName().toString());
		assertNotNull(nh);
		assertEquals(2, nh.getHostAliases().size());
		assertEquals(nh.getHostAliases().toArray(new HostAlias[] {})[0], ha1);
		assertEquals(nh.getHostAliases().toArray(new HostAlias[] {})[1], ha2);
		testHost = nh;

	}

	public void testUpdateHostDeleteAlias() throws Exception {
		testUpdateHostChangeAliases();
		Host h = hostsDAO.getHostForEditing(testHost.getId());
		assertNotNull(h);

		h.getHostAliases().remove(h.getHostAliases().get(0));

		AlterHostCmd cmd = new AlterHostCmd();
		cmd.setNewHost(h);
		cmd.setSpecifyIp(true);

		BaseCompositeCommand result = commandDispatcherRegistry
				.expandAndImplementCommand(cmd);
		assertNotNull(result);

		Host nh = hostsDAO.findHost(testHost.getHostName().toString());
		assertNotNull(nh);
		assertEquals(1, nh.getHostAliases().size());
		testHost = nh;

	}

	public void testUpdateHostDeleteAllAliases() throws Exception {
		testUpdateHostChangeAliases();
		Host h = hostsDAO.getHostForEditing(testHost.getId());
		assertNotNull(h);

		h.setHostAliases(new ArrayList<HostAlias>());
		AlterHostCmd cmd = new AlterHostCmd();
		cmd.setNewHost(h);
		cmd.setSpecifyIp(true);

		BaseCompositeCommand result = commandDispatcherRegistry
				.expandAndImplementCommand(cmd);
		assertNotNull(result);

		Host nh = hostsDAO.findHost(testHost.getHostName().toString());
		assertNotNull(nh);
		assertEquals(0, nh.getHostAliases().size());
		testHost = nh;

	}

	@Required
	public void setHostsDAO(NetDAO hostsDAO) {
		this.hostsDAO = hostsDAO;
	}

	@Required
	public void setHostsManager(HostsManagerImpl hostsManager) {
		this.hostsManager = hostsManager;
	}

	@Required
	public void setAddressSpaceManager(
			AdviceBasedAddressSpaceManager addressSpaceManager) {
		this.addressSpaceManager = addressSpaceManager;
	}

}
