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
import org.springframework.util.Assert;
import org.springframework.validation.Errors;

import edu.bath.soak.cmd.BaseCompositeCommand;
import edu.bath.soak.cmd.CommandDispatcherRegistryImpl;
import edu.bath.soak.cmd.ExecutableCommand;
import edu.bath.soak.cmd.OrderedValidator;
import edu.bath.soak.mgr.HostsManager;
import edu.bath.soak.net.HostsManagerImpl;
import edu.bath.soak.net.bulk.BulkDeleteHostManagerImpl;
import edu.bath.soak.net.cmd.AlterHostCmd;
import edu.bath.soak.net.cmd.BulkDeleteHostCmd;
import edu.bath.soak.net.cmd.DeleteHostDBCmd;
import edu.bath.soak.net.cmd.DeleteHostUICmd;
import edu.bath.soak.net.cmd.DeleteHostUICmdValidator;
import edu.bath.soak.net.model.Host;
import edu.bath.soak.net.model.NetDAO;
import edu.bath.soak.net.model.StoredCommand;
import edu.bath.soak.testutils.TestData;
import edu.bath.soak.undo.UndoManagerImpl;
import edu.bath.soak.undo.UndoNotSupportedException;
import edu.bath.soak.undo.cmd.UndoCmd;

/**
 * Middle tier integration tests for host operations
 * 
 * @author cspocc
 * 
 */
public class BulkDeleteHostManagerTests extends
		AbstractTransactionalSpringContextTests {

	Logger log = Logger.getLogger(this.getClass());

	NetDAO hostsDAO;
	HostsManager hostsManager;
	BulkDeleteHostManagerImpl bulkDeleteHostsManager;
	CommandDispatcherRegistryImpl commandDispatcherRegistry;
	DeleteHostUICmdValidator deleteHostUICmdValidator;
	String lastCommandId;
	UndoManagerImpl undoManager;

	public BulkDeleteHostManagerTests() {
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
		commandDispatcherRegistry.registerExpander(bulkDeleteHostsManager);
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
	 * delete multiple IPs
	 * 
	 * @throws Exception
	 */
	public void testDeleteBulkHostsOK() throws Exception {
		BulkDeleteHostCmd cmd = new BulkDeleteHostCmd();
		cmd.setHosts(testHosts);
		BaseCompositeCommand result = commandDispatcherRegistry
				.expandAndImplementCommand(cmd);
		assertNotNull(result);
		assertEquals(testHosts.size(), result.getCommands().size());
		for (int i = 0; i < testHosts.size(); i++) {
			ExecutableCommand exec = result.getCommands().get(i);
			assertTrue(exec instanceof DeleteHostDBCmd);
			assertEquals(testHosts.get(i), ((DeleteHostDBCmd) exec).getHost());
			final Host testHost = testHosts.get(i);
			new AssertThrows(ObjectNotFoundException.class) {
				@Override
				public void test() throws Exception {
					Host h = hostsDAO.loadHost(testHost.getId());
					log.error("loaded host " + h + " when it should be deleted");
				}
			}.runTest();
		}

		lastCommandId = result.getCommandId();
		assertNotNull(lastCommandId);
	}

	public void testDeleteBulkHostsEmpty() throws Exception {
		BulkDeleteHostCmd cmd = new BulkDeleteHostCmd();
		BaseCompositeCommand result = commandDispatcherRegistry
				.expandAndImplementCommand(cmd);
		assertNotNull(result);
		assertEquals(0, result.getCommands().size());
	}

	// public void testDeleteHostsNoPermission() throws Exception {
	// TODO: implement test
	// }
	//
	public void testDeleteHostsNotSaved() throws Exception {
		final BulkDeleteHostCmd cmd = new BulkDeleteHostCmd();
		cmd.getHosts().add(td.dummyHost(td.getTestSubnet(), "testyhost", 10));
		new AssertThrows(IllegalArgumentException.class) {
			@Override
			public void test() throws Exception {
				commandDispatcherRegistry.expandAndImplementCommand(cmd);

			}
		};
	}

	public void testEnsureDeleteHostValidatorIsCalled() throws Exception {
		final Set<Host> calledHosts = new HashSet<Host>();
		try {
			OrderedValidator v = new OrderedValidator() {
				public void validate(Object target, Errors errors) {
					calledHosts.add(((DeleteHostUICmd) target).getHost());
				}

				public boolean supports(Class clazz) {
					return DeleteHostUICmd.class.isAssignableFrom(clazz);
				}

				public int getOrder() {
					return 0;
				}
			};

			deleteHostUICmdValidator.registerSubValidator(v);

			BulkDeleteHostCmd cmd = new BulkDeleteHostCmd();
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

	public void testUndoBulkDeleteHostsOK() throws Exception {
		testDeleteBulkHostsOK();
		assertNotNull(lastCommandId);
		StoredCommand sc = hostsDAO.getStoredCommand(lastCommandId);
		assertNotNull(sc);
		UndoCmd undoCmd = new UndoCmd();
		undoCmd.setStoredCommand(sc);
		BaseCompositeCommand bcc = commandDispatcherRegistry
				.expandCommand(undoCmd);
		assertEquals(testHosts.size(), bcc.getCommands().size());
		log.debug("expanded Undo with " + bcc.getCommands().size()
				+ " sub commands");
		commandDispatcherRegistry.implementBaseCommand(bcc);
		for (Host testHost : testHosts) {
			Host foundHost = hostsDAO.findHost(testHost.getIpAddress());
			assertNotNull(foundHost);
			assertEquals(testHost, foundHost);
		}
	}

	public void testUndoBulkDeleteHostsOneEdited() throws Exception {
		testDeleteBulkHostsOK();

		AlterHostCmd hostCmd = new AlterHostCmd();
		final Host editedHost = td.dummyHost(td.getTestSubnet(), "edtedHost",
				82);
		editedHost.setIpAddress(testHosts.get(0).getIpAddress());
		hostCmd.setNewHost(editedHost);
		hostCmd.setSpecifyIp(true);
		hostCmd.getNewHost().getHostName().setName("editedhost");
		commandDispatcherRegistry.expandAndImplementCommand(hostCmd);
		assertNotNull(lastCommandId);

		StoredCommand sc = hostsDAO.getStoredCommand(lastCommandId);
		assertNotNull(sc);
		final UndoCmd undoCmd = new UndoCmd();
		undoCmd.setStoredCommand(sc);

		new AssertThrows(UndoNotSupportedException.class) {
			@Override
			public void test() throws Exception {
				commandDispatcherRegistry.expandCommand(undoCmd);
			}

			@Override
			protected void checkExceptionExpectations(Exception arg0) {
				Assert.isInstanceOf(UndoNotSupportedException.class, arg0);
				UndoNotSupportedException ex = (UndoNotSupportedException) arg0;
				ExecutableCommand badCmd = ex.getCommand();
				assertTrue(badCmd instanceof DeleteHostDBCmd);
				DeleteHostDBCmd delCmd = (DeleteHostDBCmd) badCmd;
				assertTrue(delCmd.getHost().getIpAddress().equals(
						editedHost.getIpAddress()));
			}
		}.runTest();

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
	public void setBulkDeleteHostsManager(
			BulkDeleteHostManagerImpl bulkDeleteHostsManager) {
		this.bulkDeleteHostsManager = bulkDeleteHostsManager;
	}

	@Required
	public void setDeleteHostUICmdValidator(
			DeleteHostUICmdValidator deleteHostUICmdValidator) {
		this.deleteHostUICmdValidator = deleteHostUICmdValidator;
	}

	public void setUndoManager(UndoManagerImpl undoManager) {
		this.undoManager = undoManager;
	}

}
