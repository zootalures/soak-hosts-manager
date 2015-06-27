package test.livedata;

import java.net.Inet4Address;
import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.test.AbstractTransactionalSpringContextTests;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

import test.hosts.SpringSetup;
import edu.bath.soak.cmd.CommandDispatcherRegistryImpl;
import edu.bath.soak.mgr.HostsManager;
import edu.bath.soak.model.OrgUnit;
import edu.bath.soak.net.AdviceBasedAddressSpaceManager;
import edu.bath.soak.net.AllocatedAddressPool;
import edu.bath.soak.net.HostsManagerImpl;
import edu.bath.soak.net.bulk.BulkCreateEditHostsManagerImpl;
import edu.bath.soak.net.cmd.AlterHostCmdValidator;
import edu.bath.soak.net.cmd.BulkCreateEditHostsCmd;
import edu.bath.soak.net.cmd.DeleteHostUICmdValidator;
import edu.bath.soak.net.model.Host;
import edu.bath.soak.net.model.HostClass;
import edu.bath.soak.net.model.NameDomain;
import edu.bath.soak.net.model.NetDAO;
import edu.bath.soak.net.model.Subnet;
import edu.bath.soak.undo.UndoManagerImpl;
import edu.bath.soak.util.TypeUtils;

/**
 * Middle tier integration tests for host operations
 * 
 * @author cspocc
 * 
 */
public class LiveBulkCreateHostValidatorTest extends
		AbstractTransactionalSpringContextTests {

	Logger log = Logger.getLogger(this.getClass());

	NetDAO hostsDAO;
	HostsManager hostsManager;
	BulkCreateEditHostsManagerImpl bulkCreateEditHostsManager;

	AlterHostCmdValidator alterHostCmdValidator;
	DeleteHostUICmdValidator deleteHostUICmdValidator;
	CommandDispatcherRegistryImpl commandDispatcherRegistry;
	AdviceBasedAddressSpaceManager addressSpaceManager;
	UndoManagerImpl undoManager;
	String lastCommandId;

	public LiveBulkCreateHostValidatorTest() {
		setAutowireMode(AUTOWIRE_BY_NAME);
	}

	@Override
	protected String[] getConfigLocations() {
		return SpringSetup.BASIC_LIVETEST_LOCS;
	}

	long startTime;

	@Override
	protected void onSetUpInTransaction() throws Exception {
		super.onSetUpInTransaction();

		addressSpaceManager.setAllocatedAddressPool(new AllocatedAddressPool());
		SpringSetup.setUpBasicAcegiAuthentication(false, hostsDAO
				.getOrgUnitById("AD"));
		startTime = System.currentTimeMillis();
	}

	@Override
	protected void onTearDownInTransaction() throws Exception {
		log.info("QCHC:"
				+ hostsDAO.getTheSession().getSessionFactory().getStatistics()
						.getQueryCacheHitCount());
		long duration = System.currentTimeMillis() - startTime;
		log.info("Test ran in " + (double) duration / 1000.00 + " seconds");
	}

	public void testCreateHostRange() throws Exception {
		BulkCreateEditHostsCmd bce = new BulkCreateEditHostsCmd();
		commandDispatcherRegistry.setUpCommandDefaults(bce);
		ArrayList<Host> createHosts = new ArrayList<Host>();
		HostClass hc = hostsDAO.getHostClassById("PC");
		OrgUnit ou = hostsDAO.getOrgUnitById("AD");
		NameDomain nd = hostsDAO.getNameDomainBySuffix(".bath.ac.uk.");
		assertNotNull(nd);
		Inet4Address initIP = TypeUtils.txtToIP("10.0.34.1");

		for (int i = 0; i < 200; i++) {
			Host h = new Host();
			h.getHostName().setDomain(nd);
			h.getHostName().setName("ccpc-testhost-" + i);
			h.setHostClass(hc);
			h.setIpAddress(TypeUtils.ipMath(initIP, i));
			h.setDescription("test host " + i);
			h.getOwnership().setOrgUnit(ou);
			createHosts.add(h);
		}

		bce.setCreation(true);
		bce.setHosts(createHosts);

		Errors objectErrors = new BeanPropertyBindingResult(bce, "cmd");
		ValidationUtils.invokeValidator(bulkCreateEditHostsManager, bce,
				objectErrors);
		assertFalse(objectErrors.hasErrors());

		System.err.println("Done");
		// commandDispatcherRegistry.expandCommand(bce);

	}
	public void testAllocateHostAddressesInBulk() throws Exception {
		BulkCreateEditHostsCmd bce = new BulkCreateEditHostsCmd();
		commandDispatcherRegistry.setUpCommandDefaults(bce);
		ArrayList<Host> createHosts = new ArrayList<Host>();
		HostClass hc = hostsDAO.getHostClassById("PC");
		OrgUnit ou = hostsDAO.getOrgUnitById("AD");
		NameDomain nd = hostsDAO.getNameDomainBySuffix(".bath.ac.uk.");
		assertNotNull(nd);
	//	Inet4Address initIP = TypeUtils.txtToIP("10.0.34.1");

		for (int i = 0; i < 200; i++) {
			Host h = new Host();
			h.getHostName().setDomain(nd);
			h.getHostName().setName("ccpc-testhost-" + i);
			h.setHostClass(hc);
			//h.setIpAddress(TypeUtils.ipMath(initIP, i));
			h.setDescription("test host " + i);
			h.getOwnership().setOrgUnit(ou);
			createHosts.add(h);
		}

		bce.setCreation(true);
		bce.setHosts(createHosts);

		Subnet s = hostsDAO.findSubnetByBaseIP(TypeUtils.txtToIP("138.38.116.0"));
		for(Host thehost: createHosts){
			addressSpaceManager.allocateIPAddress(thehost, s);
		}

		System.err.println("Done");
		// commandDispatcherRegistry.expandCommand(bce);

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

	public void setAddressSpaceManager(
			AdviceBasedAddressSpaceManager addressSpaceManager) {
		this.addressSpaceManager = addressSpaceManager;
	}

	public void setCommandDispatcherRegistry(
			CommandDispatcherRegistryImpl commandDispatcherRegistry) {
		this.commandDispatcherRegistry = commandDispatcherRegistry;
	}

}
