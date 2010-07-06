package test.hosts;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.hibernate.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.test.AbstractTransactionalSpringContextTests;
import org.springframework.test.AssertThrows;
import org.springframework.validation.Errors;

import edu.bath.soak.cmd.BaseCompositeCommand;
import edu.bath.soak.cmd.CmdValidationException;
import edu.bath.soak.cmd.CommandDispatcherRegistryImpl;
import edu.bath.soak.cmd.OrderedValidator;
import edu.bath.soak.mgr.HostsManager;
import edu.bath.soak.model.OrgUnitAcl.Permission;
import edu.bath.soak.net.HostsManagerImpl;
import edu.bath.soak.net.bulk.BulkCreateEditHostsManagerImpl;
import edu.bath.soak.net.cmd.AlterHostCmd;
import edu.bath.soak.net.cmd.AlterHostCmdValidator;
import edu.bath.soak.net.cmd.BulkCreateEditHostsCmd;
import edu.bath.soak.net.model.Host;
import edu.bath.soak.net.model.NetDAO;
import edu.bath.soak.net.model.StoredCommand;
import edu.bath.soak.net.model.Subnet;
import edu.bath.soak.security.SecurityHelper;
import edu.bath.soak.testutils.TestData;
import edu.bath.soak.undo.UndoManagerImpl;
import edu.bath.soak.undo.cmd.UndoCmd;
import edu.bath.soak.util.TypeUtils;

/**
 * Middle tier integration tests for host operations
 * 
 * @author cspocc
 * 
 */
public class BulkCreateHostManagerTests extends
		AbstractTransactionalSpringContextTests {

	Logger log = Logger.getLogger(this.getClass());

	NetDAO hostsDAO;
	HostsManager hostsManager;
	BulkCreateEditHostsManagerImpl bulkCreateEditHostsManager;
	AlterHostCmdValidator alterHostCmdValidator;
	CommandDispatcherRegistryImpl commandDispatcherRegistry;
	UndoManagerImpl undoManager;
	SecurityHelper securityHelper;
	public void setSecurityHelper(SecurityHelper securityHelper) {
		this.securityHelper = securityHelper;
	}

	String lastCommandId;

	public BulkCreateHostManagerTests() {
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
	public void testCreateBulkHostsOK() throws Exception {
		BulkCreateEditHostsCmd command = new BulkCreateEditHostsCmd();
		command.setHosts(testHosts);
		BaseCompositeCommand bcc = commandDispatcherRegistry
				.expandAndImplementCommand(command);
		lastCommandId = bcc.getCommandId();
		for (Host h : testHosts) {
			Host gotHost = hostsDAO.loadHost(h.getId());
			assertEquals(h, gotHost);
		}
	}

	public void testEditBulkHostSubnetNoPermission() throws Exception {

		final BulkCreateEditHostsCmd command = new BulkCreateEditHostsCmd();
		command.setHosts(testHosts);
		Subnet ts2 = new Subnet();
		ts2.setNetworkClass(td.getTestNetClass());
		ts2.setMinIP(TypeUtils.txtToIP("127.0.0.0"));
		ts2.setMaxIP(TypeUtils.txtToIP("127.0.0.255"));
		ts2.setName("test subnet2");
		ts2.setDescription("blah blah blah");
		ts2.getOrgUnitAcl().getAclEntries().put(td.getTestOrgUnit(),
				Permission.DENIED);
		ts2.getAllowedHostClasses().add(td.getTestHostClass());
		hostsDAO.saveSubnet(ts2);
		hostsDAO.getTheSession().evict(ts2);
		
		testHosts.get(0).setIpAddress(TypeUtils.txtToIP("127.0.0.1"));
		assertFalse(securityHelper.canUse(ts2, testHosts.get(0).getOwnership().getOrgUnit()));
		command.setHosts(testHosts);
		new AssertThrows(CmdValidationException.class) {
			@Override
			public void test() throws Exception {
				commandDispatcherRegistry.expandAndImplementCommand(command);

			}

			@Override
			protected void checkExceptionExpectations(Exception arg0) {
				CmdValidationException exception = (CmdValidationException) arg0;
				Errors errors = exception.getErrors();
				assertEquals(1, errors.getAllErrors().size());
			}
		}.runTest();

	}

	public void testUndoBulkCreateHostsOK() throws Exception {
		testCreateBulkHostsOK();
		assertNotNull(lastCommandId);

		StoredCommand sc = hostsDAO.getStoredCommand(lastCommandId);
		UndoCmd undoCmd = new UndoCmd();
		undoCmd.setStoredCommand(sc);
		BaseCompositeCommand bcc = commandDispatcherRegistry
				.expandAndImplementCommand(undoCmd);
		assertEquals(testHosts.size(), bcc.getCommands().size());

		for (final Host th : testHosts) {
			new AssertThrows(ObjectNotFoundException.class) {
				@Override
				public void test() throws Exception {
					hostsDAO.loadHost(th.getId());
				}

			}.runTest();
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

	public void setUndoManager(UndoManagerImpl undoManager) {
		this.undoManager = undoManager;
	}

	public void setBulkCreateEditHostsManager(
			BulkCreateEditHostsManagerImpl bulkCreateEditHostsManager) {
		this.bulkCreateEditHostsManager = bulkCreateEditHostsManager;
	}

}
