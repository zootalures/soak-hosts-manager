package test.hosts;

import java.net.Inet4Address;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

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
import edu.bath.soak.net.cmd.AlterHostCmdValidator;
import edu.bath.soak.net.cmd.HookableValidator;
import edu.bath.soak.net.model.Host;
import edu.bath.soak.net.model.HostAlias;
import edu.bath.soak.net.model.HostClass;
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
public class AlterHostCmdValidatorPermissionTests extends
		AbstractTransactionalSpringContextTests {

	Logger log = Logger.getLogger(this.getClass());
	AlterHostCmdValidator alterHostCmdValidator;
	NetDAO hostsDAO;
	AdviceBasedAddressSpaceManager addressSpaceManager;

	@Override
	protected String[] getConfigLocations() {
		return SpringSetup.BASIC_TEST_LOCS;
	}

	public AlterHostCmdValidatorPermissionTests() {
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

	public void testCreateNewPCNoPermissionToNameDomain() {
		NameDomain newNd = new NameDomain();
		newNd.setAllowedClasses(Collections.singleton(td.getTestHostClass()));
		newNd.setSuffix(".testdomain.foo.");
		hostsDAO.saveNameDomain(newNd);
		Host h = td.dummyHost(td.getTestSubnet(), "sadasd", 100);
		h.getHostName().setDomain(newNd);
		AlterHostCmd cmd = new AlterHostCmd();
		cmd.setNewHost(h);
		cmd.setSpecifyIp(true);
		Errors e = new BeanPropertyBindingResult(cmd, "editHostCmd");

		alterHostCmdValidator.checkPermissions(cmd, null, e);
		assertTrue(e.hasErrors());
		assertEquals(1, e.getFieldErrors("newHost.hostName.domain").size());
	}

	public void testEditHostNoPermissionToNameDomain() {
		Host h = td.dummyHost(td.getTestSubnet(), "sadasd", 100);
		hostsDAO.saveHost(h, "test");
		Host newHost = hostsDAO.getHostForEditing(h.getId());
		h = hostsDAO.getHost(h.getId());
		NameDomain newNd = new NameDomain();
		newNd.setAllowedClasses(Collections.singleton(td.getTestHostClass()));
		newNd.setSuffix(".testdomain.foo.");
		hostsDAO.saveNameDomain(newNd);

		newHost.getHostName().setDomain(newNd);
		AlterHostCmd cmd = new AlterHostCmd();
		cmd.setNewHost(newHost);
		cmd.setSpecifyIp(true);
		Errors e = new BeanPropertyBindingResult(cmd, "editHostCmd");

		alterHostCmdValidator.checkPermissions(cmd, h, e);
		assertTrue(e.hasErrors());
		assertEquals(1, e.getFieldErrors("newHost.hostName.domain").size());
	}

	public void testCreateHostNoPermissionToHostClass() {

		Host h = td.dummyHost(td.getTestSubnet(), "sadasd", 100);

		HostClass newHc = new HostClass();
		newHc.setId("NEWHC");
		newHc.setDescription("Foo bar");
		hostsDAO.saveHostClass(newHc);

		td.getTestSubnet().getSubnetAllowedHostClasses().add(newHc);

		h.setHostClass(newHc);
		AlterHostCmd cmd = new AlterHostCmd();
		cmd.setNewHost(h);
		cmd.setSpecifyIp(true);
		Errors e = new BeanPropertyBindingResult(cmd, "editHostCmd");

		alterHostCmdValidator.checkPermissions(cmd, h, e);
		assertTrue(e.hasErrors());
		assertEquals(1, e.getFieldErrors("newHost.hostClass").size());
	}

	public void testEditHostNoPermissionToHostClass() {

		Host h = td.dummyHost(td.getTestSubnet(), "sadasd", 100);
		hostsDAO.saveHost(h, "test");

		Host newHost = hostsDAO.getHostForEditing(h.getId());
		h = hostsDAO.getHost(h.getId());
		HostClass newHc = new HostClass();
		newHc.setId("NEWHC");
		newHc.setDescription("Foo bar");
		hostsDAO.saveHostClass(newHc);

		td.getTestSubnet().getSubnetAllowedHostClasses().add(newHc);

		newHost.setHostClass(newHc);
		AlterHostCmd cmd = new AlterHostCmd();
		cmd.setNewHost(newHost);
		cmd.setSpecifyIp(true);
		Errors e = new BeanPropertyBindingResult(cmd, "editHostCmd");

		alterHostCmdValidator.checkPermissions(cmd, h, e);
		assertTrue(e.hasErrors());
		assertEquals(1, e.getFieldErrors("newHost.hostClass").size());
	}

	@Required
	public void setAlterHostCmdValidator(
			AlterHostCmdValidator alterHostCmdValidator) {
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
