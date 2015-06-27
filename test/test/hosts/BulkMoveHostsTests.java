package test.hosts;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.test.AbstractTransactionalSpringContextTests;
import org.springframework.validation.Errors;

import edu.bath.soak.cmd.BaseCompositeCommand;
import edu.bath.soak.cmd.CommandDispatcherRegistryImpl;
import edu.bath.soak.cmd.OrderedValidator;
import edu.bath.soak.mgr.HostsManager;
import edu.bath.soak.net.AdviceBasedAddressSpaceManager;
import edu.bath.soak.net.AllocatedAddressPool;
import edu.bath.soak.net.HostsManagerImpl;
import edu.bath.soak.net.bulk.BulkMoveHostsManagerImpl;
import edu.bath.soak.net.cmd.AlterHostCmd;
import edu.bath.soak.net.cmd.AlterHostCmdValidator;
import edu.bath.soak.net.cmd.BulkMoveHostsCmd;
import edu.bath.soak.net.cmd.DeleteHostUICmdValidator;
import edu.bath.soak.net.model.Host;
import edu.bath.soak.net.model.NetDAO;
import edu.bath.soak.net.model.StoredCommand;
import edu.bath.soak.net.model.Subnet;
import edu.bath.soak.testutils.TestData;
import edu.bath.soak.undo.UndoManagerImpl;
import edu.bath.soak.undo.cmd.UndoCmd;

/**
 * Middle tier integration tests for host operations
 * 
 * @author cspocc
 * 
 */
public class BulkMoveHostsTests extends AbstractTransactionalSpringContextTests {

	Logger log = Logger.getLogger(this.getClass());

	NetDAO hostsDAO;
	HostsManager hostsManager;
	BulkMoveHostsManagerImpl bulkMoveHostsManager;

	AlterHostCmdValidator alterHostCmdValidator;
	DeleteHostUICmdValidator deleteHostUICmdValidator;
	CommandDispatcherRegistryImpl commandDispatcherRegistry;
	String lastCommandId;

	public BulkMoveHostsTests() {
		setAutowireMode(AUTOWIRE_BY_NAME);
	}

	@Override
	protected String[] getConfigLocations() {
		return SpringSetup.BASIC_TEST_LOCS;
	}

	TestData td;
	List<Host> testHosts;

	UndoManagerImpl undoManager;

	AdviceBasedAddressSpaceManager addressSpaceManager;

	@Override
	protected void onSetUpInTransaction() throws Exception {
		super.onSetUpInTransaction();
		addressSpaceManager.setAllocatedAddressPool(new AllocatedAddressPool());
		commandDispatcherRegistry = new CommandDispatcherRegistryImpl();
		commandDispatcherRegistry.registerExpander(hostsManager);
		commandDispatcherRegistry.registerDispatcher(hostsManager);
		commandDispatcherRegistry.registerExpander(bulkMoveHostsManager);
		commandDispatcherRegistry.setUndoManager(undoManager);
		commandDispatcherRegistry.registerExpander(undoManager);
		bulkMoveHostsManager.setAlterHostCmdValidator(alterHostCmdValidator);
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

			testBulkMoveHostsOK();
			for (Host h : testHosts) {
				if (!calledHosts.contains(h)) {
					fail("Host " + h + "was not validated");
				}
			}
		} finally {
			setDirty();
		}
	}

	public void testBulkMoveHostsOK() throws Exception {
		BulkMoveHostsCmd command = new BulkMoveHostsCmd();
		Subnet newSubnet = td.dummySubnet(101);
		hostsDAO.saveSubnet(newSubnet);

		command.setHosts(testHosts);
		command.setNewSubnet(newSubnet);
		bulkMoveHostsManager.assignAddresses(command);
		BaseCompositeCommand result = commandDispatcherRegistry
				.expandAndImplementCommand(command);

		for (Host h : testHosts) {
			Host gotHost = hostsDAO.loadHost(h.getId());
			assertEquals(command.getHostAddresses().get(h.getId()), gotHost
					.getIpAddress());
			assertTrue(newSubnet.containsIp(gotHost.getIpAddress()));
		}
		lastCommandId = result.getCommandId();

	}

	public void testUndoBulkMoveCmdOK() throws Exception {
		testBulkMoveHostsOK();
		assertNotNull(lastCommandId);

		StoredCommand sc = hostsDAO.getStoredCommand(lastCommandId);
		UndoCmd undoCmd = new UndoCmd();
		undoCmd.setStoredCommand(sc);
		BaseCompositeCommand bcc = commandDispatcherRegistry
				.expandAndImplementCommand(undoCmd);
		assertEquals(testHosts.size(), bcc.getCommands().size());

		for (Host th : testHosts) {
			Host gotHost = hostsDAO.loadHost(th.getId());
			assertTrue(td.getTestSubnet().containsIp(gotHost.getIpAddress()));
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
	public void setBulkMoveHostsManager(
			BulkMoveHostsManagerImpl bulkMoveHostsManager) {
		this.bulkMoveHostsManager = bulkMoveHostsManager;
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

	public void setAddressSpaceManager(
			AdviceBasedAddressSpaceManager addressSpaceManager) {
		this.addressSpaceManager = addressSpaceManager;
	}

}
