package test.hosts;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.test.AbstractTransactionalSpringContextTests;
import org.springframework.test.AssertThrows;
import org.springframework.util.Assert;
import org.springframework.validation.Errors;

import edu.bath.soak.cmd.BaseCompositeCommand;
import edu.bath.soak.cmd.CmdValidationException;
import edu.bath.soak.cmd.CommandDispatcherRegistryImpl;
import edu.bath.soak.cmd.ExecutableCommand;
import edu.bath.soak.cmd.OrderedValidator;
import edu.bath.soak.mgr.HostsManager;
import edu.bath.soak.model.OrgUnitAcl.Permission;
import edu.bath.soak.net.HostsManagerImpl;
import edu.bath.soak.net.bulk.BulkCreateEditHostsManagerImpl;
import edu.bath.soak.net.cmd.AlterHostCmd;
import edu.bath.soak.net.cmd.AlterHostCmdValidator;
import edu.bath.soak.net.cmd.BulkCreateEditHostsCmd;
import edu.bath.soak.net.cmd.DeleteHostUICmdValidator;
import edu.bath.soak.net.cmd.SaveHostCmd;
import edu.bath.soak.net.model.Host;
import edu.bath.soak.net.model.HostChange;
import edu.bath.soak.net.model.HostClass;
import edu.bath.soak.net.model.NameDomain;
import edu.bath.soak.net.model.NetDAO;
import edu.bath.soak.net.model.StoredCommand;
import edu.bath.soak.net.model.HostClass.DHCP_STATUS;
import edu.bath.soak.testutils.TestData;
import edu.bath.soak.undo.UndoManagerImpl;
import edu.bath.soak.undo.cmd.UndoCmd;

/**
 * Middle tier integration tests for host operations
 * 
 * @author cspocc
 * 
 */
public class BulkEditHostManagerTests extends
		AbstractTransactionalSpringContextTests {

	Logger log = Logger.getLogger(this.getClass());

	NetDAO hostsDAO;
	HostsManager hostsManager;
	BulkCreateEditHostsManagerImpl bulkCreateEditHostsManager;
	AlterHostCmdValidator alterHostCmdValidator;
	DeleteHostUICmdValidator deleteHostUICmdValidator;
	CommandDispatcherRegistryImpl commandDispatcherRegistry;
	UndoManagerImpl undoManager;

	String lastCommandId;

	public BulkEditHostManagerTests() {
		setAutowireMode(AUTOWIRE_BY_NAME);
	}

	@Override
	protected String[] getConfigLocations() {
		return SpringSetup.BASIC_TEST_LOCS;
	}

	TestData td;
	List<Host> testHosts;

	@Override
	protected void onSetUpInTransaction() throws Exception {
		super.onSetUpInTransaction();
		commandDispatcherRegistry = new CommandDispatcherRegistryImpl();
		commandDispatcherRegistry.registerExpander(hostsManager);
		commandDispatcherRegistry.registerDispatcher(hostsManager);
		commandDispatcherRegistry.registerExpander(bulkCreateEditHostsManager);
		commandDispatcherRegistry.setUndoManager(undoManager);
		commandDispatcherRegistry.registerExpander(undoManager);
		undoManager.setCommandDispatcherRegistry(commandDispatcherRegistry);

		td = new TestData(hostsDAO);
		testHosts = new ArrayList<Host>();
		for (int i = 0; i < 11; i++) {
			Host dummy = td.dummyHost(td.getTestSubnet(), "bulktestHost",
					i + 80);
			log.trace("creating host " + dummy);
			hostsDAO.saveHost(dummy,"testcmd");
			testHosts.add(dummy);
		}
		SpringSetup.setUpBasicAcegiAuthentication(false, td.getTestOrgUnit());
	}

	/**
	 * We test that the given validator is called via the subvalidators of the
	 * alter host command validator for each host
	 * 
	 * @throws Exception
	 */
	public void testEnsureAlterHostValidatorIsCalled() throws Exception {
		final Set<Host> calledHosts = new HashSet<Host>();
		try {
			OrderedValidator v = new OrderedValidator() {
				public void validate(Object target, Errors errors) {
					calledHosts.add(((AlterHostCmd) target).getNewHost());
				}

				public boolean supports(Class clazz) {
					return AlterHostCmd.class.isAssignableFrom(clazz);
				}

				public int getOrder() {
					return 0;
				}
			};

			alterHostCmdValidator.registerSubValidator(v);

			BulkCreateEditHostsCmd cmd = new BulkCreateEditHostsCmd();
			cmd.setHosts(testHosts);
			for (Host h : cmd.getHosts()) {
				h.getLocation().setBuilding("NEWBUILD");
			}

			commandDispatcherRegistry.expandCommand(cmd);
			for (Host h : testHosts) {
				if (!calledHosts.contains(h)) {
					fail("Host " + h + "was not validated");
				}
			}
		} finally {
			setDirty();
		}
	}

	/**
	 * Change host details name domain
	 * 
	 * @throws Exception
	 */
	public void testEditBulkHostNameDomainOK() throws Exception {
		BulkCreateEditHostsCmd command = new BulkCreateEditHostsCmd();

		NameDomain newNameDomain = new NameDomain();
		newNameDomain.setSuffix(".newtestdomain.com.");
		HashSet<HostClass> hcSet = new HashSet<HostClass>();
		hcSet.add(td.getTestHostClass());
		newNameDomain.setAllowedClasses(hcSet);
		newNameDomain.getOrgUnitAcl().getAclEntries().put(td.getTestOrgUnit(),
				Permission.ALLOWED);

		hostsDAO.saveNameDomain(newNameDomain);

		for (Host h : testHosts) {
			Host newh = hostsDAO.getHostForEditing(h.getId());
			command.getHosts().add(newh);

			newh.getHostName().setDomain(newNameDomain);
		}
		commandDispatcherRegistry.expandAndImplementCommand(command);
		for (Host h : testHosts) {
			Host gotHost = hostsDAO.loadHost(h.getId());
			assertEquals(newNameDomain, gotHost.getHostName().getDomain());
		}
	}

	public void testEditBulkHostNameDomainNoPermission() throws Exception {
		final BulkCreateEditHostsCmd command = new BulkCreateEditHostsCmd();
		NameDomain newNameDomain = new NameDomain();
		newNameDomain.setSuffix(".newtestdomain.com.");
		HashSet<HostClass> hcSet = new HashSet<HostClass>();
		hcSet.add(td.getTestHostClass());
		newNameDomain.setAllowedClasses(hcSet);
		hostsDAO.saveNameDomain(newNameDomain);

		command.setHosts(testHosts);
		for (Host h : command.getHosts()) {
			h.getHostName().setDomain(newNameDomain);
		}
		new AssertThrows(CmdValidationException.class) {
			@Override
			public void test() throws Exception {
				commandDispatcherRegistry.expandAndImplementCommand(command);

			}

		};

	}

	public void testEditBulkHostClassOK() throws Exception {
		BulkCreateEditHostsCmd command = new BulkCreateEditHostsCmd();

		HostClass newHostClass = new HostClass();
		newHostClass.setId("NEWHOSTCLASS");
		newHostClass.setName("new host class");
		newHostClass.setDHCPStatus(DHCP_STATUS.IF_POSSIBLE);
		newHostClass.getOrgUnitAcl().getAclEntries().put(td.getTestOrgUnit(),
				Permission.ALLOWED);
		hostsDAO.saveHostClass(newHostClass);

		td.getTestNameDomain().getAllowedClasses().add(newHostClass);
		hostsDAO.saveNameDomain(td.getTestNameDomain());

		td.getTestNetClass().getAllowedHostClasses().add(newHostClass);
		hostsDAO.saveNetworkClass(td.getTestNetClass());

		for (Host h : testHosts) {
			Host newh = hostsDAO.getHostForEditing(h.getId());
			command.getHosts().add(newh);

			newh.setHostClass(newHostClass);
		}
		BaseCompositeCommand result = commandDispatcherRegistry
				.expandAndImplementCommand(command);

		for (Host h : testHosts) {
			Host gotHost = hostsDAO.loadHost(h.getId());
			assertEquals(newHostClass, gotHost.getHostClass());
		}
		lastCommandId = result.getCommandId();

	}

	public void testEditBulkHostClassHostBadNd() throws Exception {
		final BulkCreateEditHostsCmd command = new BulkCreateEditHostsCmd();

		HostClass newHostClass = new HostClass();
		newHostClass.setId("NEWHOSTCLASS");
		newHostClass.setName("new host class");
		newHostClass.setDHCPStatus(DHCP_STATUS.IF_POSSIBLE);
		newHostClass.getOrgUnitAcl().getAclEntries().put(td.getTestOrgUnit(),
				Permission.ALLOWED);
		hostsDAO.saveHostClass(newHostClass);

		td.getTestNameDomain().getAllowedClasses().add(newHostClass);
		hostsDAO.saveNameDomain(td.getTestNameDomain());

		td.getTestNetClass().getAllowedHostClasses().add(newHostClass);
		hostsDAO.saveNetworkClass(td.getTestNetClass());

		// Note that this name domain is not permitted to take the new host
		// class
		NameDomain newNameDomain = new NameDomain();
		newNameDomain.setSuffix(".newtestdomain.com.");
		HashSet<HostClass> hcSet = new HashSet<HostClass>();
		hcSet.add(td.getTestHostClass());
		newNameDomain.setAllowedClasses(hcSet);
		newNameDomain.getOrgUnitAcl().getAclEntries().put(td.getTestOrgUnit(),
				Permission.ALLOWED);

		hostsDAO.saveNameDomain(newNameDomain);
		testHosts.get(0).getHostName().setDomain(newNameDomain);
		command.setHosts(testHosts);
		for (Host h : command.getHosts()) {
			h.setHostClass(newHostClass);
		}
		new AssertThrows(CmdValidationException.class) {
			@Override
			public void test() throws Exception {
				commandDispatcherRegistry.expandAndImplementCommand(command);
			}

			@Override
			protected void checkExceptionExpectations(Exception arg0) {

			}
		};
	}

	public void testUndoBulkEditHostStateOK() throws Exception {
		testEditBulkHostClassOK();
		assertNotNull(lastCommandId);

		StoredCommand sc = hostsDAO.getStoredCommand(lastCommandId);
		BaseCompositeCommand bcc = hostsDAO.getBaseCommandForStoredCommand(sc);

		for (Host th : testHosts) {
			HostChange hc = hostsDAO.getHostChangeAtVersion(th.getId(), th
					.getVersion() - 1);
			assertNotNull(hc);
			assertEquals(td.getTestHostClass(), hc.getHost().getHostClass());

			SaveHostCmd saveCmd = null;
			for (ExecutableCommand cmd : bcc.getCommands()) {
				Assert.isInstanceOf(SaveHostCmd.class, cmd);
				SaveHostCmd gotCmd = (SaveHostCmd) cmd;
				if (gotCmd.getHost().getId().equals(th.getId())) {
					saveCmd = gotCmd;
					break;
				}
			}
			assertNotNull(saveCmd);
			assertEquals((Long) hc.getVersion(), (Long) saveCmd
					.getVersionBeforeChange());

		}

	}

	public void testUndoBulkEditHostsOK() throws Exception {
		testEditBulkHostClassOK();
		assertNotNull(lastCommandId);

		StoredCommand sc = hostsDAO.getStoredCommand(lastCommandId);
		UndoCmd undoCmd = new UndoCmd();
		undoCmd.setStoredCommand(sc);
		BaseCompositeCommand bcc = commandDispatcherRegistry
				.expandAndImplementCommand(undoCmd);
		assertEquals(testHosts.size(), bcc.getCommands().size());

		for (Host th : testHosts) {
			Host gotHost = hostsDAO.loadHost(th.getId());
			assertEquals(td.getTestHostClass(), gotHost.getHostClass());
		}
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
	public void setAlterHostCmdValidator(
			AlterHostCmdValidator alterHostCmdValidator) {
		this.alterHostCmdValidator = alterHostCmdValidator;
	}

	@Required
	public void setDeleteHostUICmdValidator(
			DeleteHostUICmdValidator deleteHostUICmdValidator) {
		this.deleteHostUICmdValidator = deleteHostUICmdValidator;
	}

	public void setUndoManager(UndoManagerImpl undoManager) {
		this.undoManager = undoManager;
	}

	@Required
	public void setBulkCreateEditHostsManager(
			BulkCreateEditHostsManagerImpl bulkCreateEditHostsManager) {
		this.bulkCreateEditHostsManager = bulkCreateEditHostsManager;
	}

}
