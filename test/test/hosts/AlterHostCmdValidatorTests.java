package test.hosts;

import java.net.Inet4Address;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.test.AbstractTransactionalSpringContextTests;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;
import org.springframework.validation.ValidationUtils;

import edu.bath.soak.mgr.AddressManagerAdvisor;
import edu.bath.soak.net.AdviceBasedAddressSpaceManager;
import edu.bath.soak.net.AllocatedAddressPool;
import edu.bath.soak.net.cmd.AlterHostCmd;
import edu.bath.soak.net.cmd.HookableValidator;
import edu.bath.soak.net.model.Host;
import edu.bath.soak.net.model.HostAlias;
import edu.bath.soak.net.model.HostName;
import edu.bath.soak.net.model.NameDomain;
import edu.bath.soak.net.model.NetDAO;
import edu.bath.soak.net.model.Subnet;
import edu.bath.soak.net.model.HostAlias.HostAliasType;
import edu.bath.soak.testutils.TestData;
import edu.bath.soak.util.MacAddress;
import edu.bath.soak.util.TypeUtils;

/**
 * Validator unit tests
 * 
 * @author cspocc
 * 
 */
public class AlterHostCmdValidatorTests extends
		AbstractTransactionalSpringContextTests {

	Logger log = Logger.getLogger(this.getClass());
	HookableValidator alterHostCmdValidator;
	NetDAO hostsDAO;
	AdviceBasedAddressSpaceManager addressSpaceManager;

	@Override
	protected String[] getConfigLocations() {
		return SpringSetup.BASIC_TEST_LOCS;
	}

	public AlterHostCmdValidatorTests() {
		setAutowireMode(AUTOWIRE_BY_NAME);
	}

	TestData td;

	@Override
	protected void onSetUpInTransaction() throws Exception {
		super.onSetUpInTransaction();
		td = new TestData(hostsDAO);
		addressSpaceManager.setAllocatedAddressPool(new AllocatedAddressPool());
		addressSpaceManager.setAdvisors(new ArrayList<AddressManagerAdvisor>());
		SpringSetup.setUpBasicAcegiAuthentication(false, td.getTestOrgUnit());
	}

	Host goodBaseHost(TestData td) {
		Host h = new Host();
		Inet4Address ip = TypeUtils.ipMath(td.getTestSubnet().getMinIP(), 20);
		h.setIpAddress(ip);
		h.setHostClass(td.getTestHostClass());
		h.getHostName().setName("testinghost");
		h.getHostName().setDomain(td.getTestNameDomain());
		h.setMacAddress(MacAddress.fromText("00:11:22:33:44:55"));
		h.getOwnership().setOrgUnit(td.getTestOrgUnit());
		return h;
	}

	/**
	 * Validator should succeed with a good host (specifying an IP)
	 * 
	 * @throws Exception
	 */

	public void testValidateGoodHostSpecifyIp() {

		AlterHostCmd cmd = new AlterHostCmd();

		cmd.setNewHost(goodBaseHost(td));
		cmd.setSpecifyIp(true);
		cmd.setSubnet(null);

		Errors e = new BeanPropertyBindingResult(cmd, "editHostCmd");
		ValidationUtils.invokeValidator(alterHostCmdValidator, cmd, e);
		if (e.hasErrors()) {
			for (ObjectError oe : (List<ObjectError>) e.getAllErrors()) {
				log.trace("Got error" + oe.toString());
			}
		}
		assertFalse(e.hasErrors());
	}

	/**
	 * Validator should fail when an alias is already use
	 */
	public void testValidateHostAliasSameAsHostname() {
		AlterHostCmd cmd = new AlterHostCmd();
		Host h = goodBaseHost(td);
		HostAlias ha = new HostAlias();
		ha.setAlias(h.getHostName());
		ha.setHost(h);
		h.getHostAliases().add(ha);

		cmd.setNewHost(h);
		cmd.setSpecifyIp(true);
		cmd.setSubnet(null);

		Errors e = new BeanPropertyBindingResult(cmd, "editHostCmd");
		ValidationUtils.invokeValidator(alterHostCmdValidator, cmd, e);
		assertTrue(e.hasErrors());
		assertNotNull(e.getFieldErrors("newHost.hostAliases[0].*"));

	}

	/**
	 * Validator should fail when an alias is already in use as a hostname
	 */
	public void testValidateHostAliasSameAsOtherHostname() {
		AlterHostCmd cmd = new AlterHostCmd();
		Host h1 = td.dummyHost(td.getTestSubnet(), "aliastestinghost", 0);

		hostsDAO.saveHost(h1,"testcmd");

		Host h = goodBaseHost(td);
		HostAlias ha = new HostAlias();
		ha.setAlias(h1.getHostName());
		ha.setHost(h);
		h.getHostAliases().add(ha);

		cmd.setNewHost(h);
		cmd.setSpecifyIp(true);
		cmd.setSubnet(null);

		Errors e = new BeanPropertyBindingResult(cmd, "editHostCmd");
		ValidationUtils.invokeValidator(alterHostCmdValidator, cmd, e);
		assertTrue(e.hasErrors());
		assertNotNull(e.getFieldErrors("newHost.hostAliases[0].*"));

	}

	/**
	 * Validator should fail when two aliases have the same name
	 */
	public void testValidateHostDuplicateAlias() {
		AlterHostCmd cmd = new AlterHostCmd();

		HostName alias = new HostName();
		alias.setName("testalias");
		alias.setDomain(td.getTestNameDomain());

		Host h = goodBaseHost(td);
		List<HostAlias> aliases = new ArrayList<HostAlias>();

		HostAlias ha = new HostAlias();

		ha.setAlias(alias);
		ha.setType(HostAliasType.AREC);
		ha.setHost(h);
		aliases.add(ha);

		HostAlias ha2 = new HostAlias();
		ha2.setAlias(alias);
		ha2.setType(HostAliasType.CNAME);
		ha2.setHost(h);
		aliases.add(ha2);
		h.setHostAliases(aliases);

		cmd.setNewHost(h);
		cmd.setSpecifyIp(true);
		cmd.setSubnet(null);

		Errors e = new BeanPropertyBindingResult(cmd, "editHostCmd");
		ValidationUtils.invokeValidator(alterHostCmdValidator, cmd, e);
		assertTrue(e.hasErrors());
		assertNotNull(e.getFieldErrors("newHost.hostAliases[0].*"));

	}

	/**
	 * Validator should fail when adding an alias which is already an alias to
	 * another host
	 */
	public void testValidateHostCNAMEAliasAlreadyPresentAsCNAMEAlias() {
		AlterHostCmd cmd = new AlterHostCmd();
		Host h1 = td.dummyHost(td.getTestSubnet(), "aliastestinghost", 0);
		HostAlias ha1 = new HostAlias();

		HostName alias = new HostName();
		alias.setDomain(td.getTestNameDomain());
		alias.setName("testhost-alias");
		ha1.setAlias(alias);
		ha1.setHost(h1);
		ha1.setType(HostAliasType.CNAME);

		h1.getHostAliases().add(ha1);

		hostsDAO.saveHost(h1,"testcmd");

		Host h = goodBaseHost(td);
		HostAlias ha2 = new HostAlias();
		ha2.setAlias(alias);
		ha2.setHost(h);
		ha2.setType(HostAliasType.CNAME);
		h.getHostAliases().add(ha2);

		cmd.setNewHost(h);
		cmd.setSpecifyIp(true);
		cmd.setSubnet(null);

		Errors e = new BeanPropertyBindingResult(cmd, "editHostCmd");
		ValidationUtils.invokeValidator(alterHostCmdValidator, cmd, e);
		assertTrue(e.hasErrors());
		assertNotNull(e.getFieldErrors("newHost.hostAliases[0].*"));
	}

	public void testValidateHostARECAliasAlreadyPresentAsCNAMEAlias() {
		AlterHostCmd cmd = new AlterHostCmd();
		Host h1 = td.dummyHost(td.getTestSubnet(), "aliastestinghost", 0);
		HostAlias ha1 = new HostAlias();

		HostName alias = new HostName();
		alias.setDomain(td.getTestNameDomain());
		alias.setName("testhost-alias");
		ha1.setAlias(alias);
		ha1.setHost(h1);
		ha1.setType(HostAliasType.CNAME);

		h1.getHostAliases().add(ha1);

		hostsDAO.saveHost(h1,"testcmd");

		Host h = goodBaseHost(td);
		HostAlias ha2 = new HostAlias();
		ha2.setAlias(alias);
		ha2.setHost(h);
		ha2.setType(HostAliasType.AREC);
		h.getHostAliases().add(ha2);

		cmd.setNewHost(h);
		cmd.setSpecifyIp(true);
		cmd.setSubnet(null);

		Errors e = new BeanPropertyBindingResult(cmd, "editHostCmd");
		ValidationUtils.invokeValidator(alterHostCmdValidator, cmd, e);
		assertTrue(e.hasErrors());
		assertNotNull(e.getFieldErrors("newHost.hostAliases[0].*"));
	}

	public void testValidateHostCNAMEAliasAlreadyPresentAsARECAlias() {
		AlterHostCmd cmd = new AlterHostCmd();
		Host h1 = td.dummyHost(td.getTestSubnet(), "aliastestinghost", 0);
		HostAlias ha1 = new HostAlias();

		HostName alias = new HostName();
		alias.setDomain(td.getTestNameDomain());
		alias.setName("testhost-alias");
		ha1.setAlias(alias);
		ha1.setHost(h1);
		ha1.setType(HostAliasType.AREC);

		h1.getHostAliases().add(ha1);

		hostsDAO.saveHost(h1,"testcmd");

		Host h = goodBaseHost(td);
		HostAlias ha2 = new HostAlias();
		ha2.setAlias(alias);
		ha2.setHost(h);
		ha2.setType(HostAliasType.CNAME);
		h.getHostAliases().add(ha2);

		cmd.setNewHost(h);
		cmd.setSpecifyIp(true);
		cmd.setSubnet(null);

		Errors e = new BeanPropertyBindingResult(cmd, "editHostCmd");
		ValidationUtils.invokeValidator(alterHostCmdValidator, cmd, e);
		assertTrue(e.hasErrors());
		assertNotNull(e.getFieldErrors("newHost.hostAliases[0].*"));
	}

	/**
	 * Multiple AREC aliases are permitted.
	 */
	public void testValidateHostARECAliasAlreadyPresentAsARECAlias() {
		AlterHostCmd cmd = new AlterHostCmd();
		Host h1 = td.dummyHost(td.getTestSubnet(), "aliastestinghost", 0);
		HostAlias ha1 = new HostAlias();

		HostName alias = new HostName();
		alias.setDomain(td.getTestNameDomain());
		alias.setName("testhost-alias");
		ha1.setAlias(alias);
		ha1.setHost(h1);
		ha1.setType(HostAliasType.AREC);

		h1.getHostAliases().add(ha1);

		hostsDAO.saveHost(h1,"testcmd");

		Host h = goodBaseHost(td);
		HostAlias ha2 = new HostAlias();
		ha2.setAlias(alias);
		ha2.setHost(h);
		ha2.setType(HostAliasType.AREC);
		h.getHostAliases().add(ha2);

		cmd.setNewHost(h);
		cmd.setSpecifyIp(true);
		cmd.setSubnet(null);

		Errors e = new BeanPropertyBindingResult(cmd, "editHostCmd");
		ValidationUtils.invokeValidator(alterHostCmdValidator, cmd, e);
		assertFalse(e.hasErrors());
	}

	public void testValidateHostAliasAlreadyPresentAsAlias() {
		AlterHostCmd cmd = new AlterHostCmd();
		Host h1 = td.dummyHost(td.getTestSubnet(), "aliastestinghost", 0);
		HostAlias ha1 = new HostAlias();

		HostName alias = new HostName();
		alias.setDomain(td.getTestNameDomain());
		alias.setName("testhost-alias");
		ha1.setAlias(alias);
		ha1.setHost(h1);
		h1.getHostAliases().add(ha1);

		hostsDAO.saveHost(h1,"testcmd");

		Host h = goodBaseHost(td);
		HostAlias ha = new HostAlias();
		ha.setAlias(alias);
		ha.setHost(h);
		h.getHostAliases().add(ha);

		cmd.setNewHost(h);
		cmd.setSpecifyIp(true);
		cmd.setSubnet(null);

		Errors e = new BeanPropertyBindingResult(cmd, "editHostCmd");
		ValidationUtils.invokeValidator(alterHostCmdValidator, cmd, e);
		assertTrue(e.hasErrors());
		assertNotNull(e.getFieldErrors("newHost.hostAliases[0].*"));
	}

	/**
	 * Validator should fail for a host which is already an alias
	 */
	public void testValidateHostAlreadyPresentAsHostAlias() {
		AlterHostCmd cmd = new AlterHostCmd();
		Host h1 = td.dummyHost(td.getTestSubnet(), "aliastestinghost", 0);
		HostAlias ha1 = new HostAlias();

		HostName alias = new HostName();
		alias.setDomain(td.getTestNameDomain());
		alias.setName("testhost-alias");
		ha1.setAlias(alias);
		ha1.setHost(h1);
		h1.getHostAliases().add(ha1);

		hostsDAO.saveHost(h1,"testcmd");

		Host h = goodBaseHost(td);
		h.setHostName(alias);

		cmd.setNewHost(h);
		cmd.setSpecifyIp(true);
		cmd.setSubnet(null);

		Errors e = new BeanPropertyBindingResult(cmd, "editHostCmd");
		ValidationUtils.invokeValidator(alterHostCmdValidator, cmd, e);
		assertTrue(e.hasErrors());
		assertNotNull(e.getFieldError("newHost.hostName"));
	}

	/**
	 * Validator should succeed with a good host (choosing an IP)
	 * 
	 * @throws Exception
	 */
	public void testValidateGoodHostSpecifySubnet() throws Exception {

		AlterHostCmd cmd = new AlterHostCmd();
		Host h = goodBaseHost(td);
		h.setIpAddress(null);
		cmd.setNewHost(h);
		cmd.setSpecifyIp(false);
		cmd.setSubnet(td.getTestSubnet());

		Errors e = new BeanPropertyBindingResult(cmd, "editHostCmd");
		ValidationUtils.invokeValidator(alterHostCmdValidator, cmd, e);
		if (e.hasErrors()) {
			for (ObjectError oe : (List<ObjectError>) e.getAllErrors()) {
				log.trace("Got error" + oe.toString());
			}
		}
		assertFalse(e.hasErrors());
	}

	/**
	 * Validator should fail when a host with the same IP is present
	 * 
	 * @throws Exception
	 */
	public void testValidateDetailsIPAlreadyPresent() throws Exception {

		Host h = goodBaseHost(td);
		h.setIpAddress(td.getTestHost().getIpAddress());
		AlterHostCmd cmd = new AlterHostCmd();
		cmd.setNewHost(h);
		cmd.setSpecifyIp(true);

		Errors e = new BeanPropertyBindingResult(cmd, "editHostCmd");
		ValidationUtils.invokeValidator(alterHostCmdValidator, cmd, e);
		assertTrue(e.hasErrors());
		assertEquals(1, e.getFieldErrors("newHost.ipAddress").size());
	}

	/**
	 * Validator should fail when a host with the same MAC is present
	 * 
	 * @throws Exception
	 */

	public void testValidateDetailsMACAlreadyPresent() throws Exception {
		Host h = goodBaseHost(td);
		h.setMacAddress(td.getTestHost().getMacAddress());
		AlterHostCmd cmd = new AlterHostCmd();
		cmd.setNewHost(h);
		cmd.setSpecifyIp(true);

		Errors e = new BeanPropertyBindingResult(cmd, "editHostCmd");
		ValidationUtils.invokeValidator(alterHostCmdValidator, cmd, e);
		assertTrue(e.hasErrors());
		assertEquals(1, e.getFieldErrors("newHost.macAddress").size());
	}

	/**
	 * Validator should fail when a host with the same name is present
	 * 
	 * @throws Exception
	 */
	public void testValidateDetailsHostNameAlreadyPresent() throws Exception {
		Host h = goodBaseHost(td);
		h.setHostName(td.getTestHost().getHostName());
		AlterHostCmd cmd = new AlterHostCmd();
		cmd.setNewHost(h);
		cmd.setSpecifyIp(true);

		Errors e = new BeanPropertyBindingResult(cmd, "editHostCmd");
		ValidationUtils.invokeValidator(alterHostCmdValidator, cmd, e);
		assertTrue(e.hasErrors());
		assertEquals(1, e.getFieldErrors("newHost.hostName").size());
	}

	/**
	 * Validator should fail when a host with the same name is present
	 * 
	 * @throws Exception
	 */
	public void testValidateDetailsHostNameInvalidND() throws Exception {
		Host h = goodBaseHost(td);
		NameDomain testNd = new NameDomain();
		testNd.setSuffix(".foobarnd.foo.com.");
		hostsDAO.saveNameDomain(testNd);

		h.getHostName().setDomain(testNd);
		AlterHostCmd cmd = new AlterHostCmd();
		cmd.setNewHost(h);
		cmd.setSpecifyIp(true);

		Errors e = new BeanPropertyBindingResult(cmd, "editHostCmd");
		ValidationUtils.invokeValidator(alterHostCmdValidator, cmd, e);
		assertTrue(e.hasErrors());
		assertEquals(1, e.getFieldErrors("newHost.hostName").size());
	}

	public void testValidateDetailsHostNameInvalidButNotChanged()
			throws Exception {
		Host h = goodBaseHost(td);
		NameDomain testNd = new NameDomain();
		testNd.setSuffix(".foobarnd.foo.com.");
		hostsDAO.saveNameDomain(testNd);

		h.getHostName().setDomain(testNd);
		hostsDAO.saveHost(h,"testcmd");
		AlterHostCmd cmd = new AlterHostCmd();
		cmd.setNewHost(h);
		cmd.setSpecifyIp(true);

		Errors e = new BeanPropertyBindingResult(cmd, "editHostCmd");
		ValidationUtils.invokeValidator(alterHostCmdValidator, cmd, e);
		assertTrue(!e.hasErrors());
	}

	/**
	 * Validator should fail when trying to add a host to a subnet which is full
	 * 
	 * @throws Exception
	 */
	public void testValidateDetailsSubnetFull() throws Exception {

		Subnet newsubnet = td.dummySubnet(39);
		hostsDAO.saveSubnet(newsubnet);
		log.trace("Created dummy subet with range: "
				+ newsubnet.getMinIP().getHostAddress() + " - "
				+ newsubnet.getMaxIP().getHostAddress());
		for (int i = 0; i < newsubnet.getNumUseableAddresses(); i++) {
			Host h = td.dummyHost(newsubnet, "fullsubnethost", i);
			log.trace("Adding host  " + h);
			hostsDAO.saveHost(h,"testcmd");
		}

		Host h = goodBaseHost(td);
		AlterHostCmd cmd = new AlterHostCmd();
		cmd.setNewHost(h);
		cmd.setSpecifyIp(false);
		cmd.setSubnet(newsubnet);

		Errors e = new BeanPropertyBindingResult(cmd, "editHostCmd");

		ValidationUtils.invokeValidator(alterHostCmdValidator, cmd, e);
		if (e.hasErrors()) {
			for (ObjectError oe : (List<ObjectError>) e.getAllErrors()) {
				log.trace("Got error" + oe.toString());
			}
		}
		assertTrue(e.hasErrors());
		log.error("got errors " + e);
		assertEquals(1, e.getFieldErrors("subnet").size());
	}

	@Required
	public void setAlterHostCmdValidator(HookableValidator alterHostCmdValidator) {
		this.alterHostCmdValidator = alterHostCmdValidator;
	}

	@Required
	public void setHostsDAO(NetDAO hostsDAO) {
		this.hostsDAO = hostsDAO;
	}

	public void setAddressSpaceManager(
			AdviceBasedAddressSpaceManager addressSpaceManager) {
		this.addressSpaceManager = addressSpaceManager;
	}

	@Override
	protected void onSetUp() throws Exception {

		super.onSetUp();
	}

}
