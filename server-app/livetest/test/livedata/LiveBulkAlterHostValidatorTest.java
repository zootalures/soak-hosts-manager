package test.livedata;

import java.net.Inet4Address;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.test.AbstractTransactionalSpringContextTests;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

import test.hosts.SpringSetup;
import edu.bath.soak.cmd.CommandDispatcherRegistryImpl;
import edu.bath.soak.dhcp.cmd.DHCPHostCommandFlags;
import edu.bath.soak.dns.DNSHostsInterceptor;
import edu.bath.soak.dns.cmd.DNSHostCommandFlags;
import edu.bath.soak.dns.cmd.DNSHostCommandFlags.DNSUpdateMode;
import edu.bath.soak.mgr.HostsManager;
import edu.bath.soak.model.OrgUnit;
import edu.bath.soak.model.OrgUnitAcl.Permission;
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
import edu.bath.soak.net.model.HostClass.DHCP_STATUS;
import edu.bath.soak.net.query.HostSearchQuery;
import edu.bath.soak.undo.UndoManagerImpl;
import edu.bath.soak.util.TypeUtils;

/**
 * Middle tier integration tests for host operations
 * 
 * @author cspocc
 * 
 */
public class LiveBulkAlterHostValidatorTest extends
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

	public LiveBulkAlterHostValidatorTest() {
		setAutowireMode(AUTOWIRE_BY_NAME);
	}

	@Override
	protected String[] getConfigLocations() {
		return SpringSetup.BASIC_LIVETEST_LOCS;
	}

	List<Host> testHosts;
	long startTime;

	@Override
	protected void onSetUpInTransaction() throws Exception {
		super.onSetUpInTransaction();

		addressSpaceManager.setAllocatedAddressPool(new AllocatedAddressPool());
		HostSearchQuery hsq = new HostSearchQuery();
		hsq.setMaxResults(150);
		hsq.setFirstResult(0);
		hsq.setSearchTerm("");
		testHosts = hostsDAO.searchHosts(hsq).getResults();
		SpringSetup.setUpBasicAcegiAuthentication(true);
		startTime = System.currentTimeMillis();
	}

	@Override
	protected void onTearDownInTransaction() throws Exception {
		long duration = System.currentTimeMillis() - startTime;
		log.info("Test ran in " + (double) duration / 1000.00 + " seconds");
	}

	/**
	 * Validates a null-edit on the selected hosts
	 */
	public void testValidateNullAlterHosts() {
		log.debug("Validating a null edit of  some hosts");
		BulkCreateEditHostsCmd bce = new BulkCreateEditHostsCmd();
		bce.setCreation(false);
		bce.setHosts(testHosts);
		Errors objectErrors = new BeanPropertyBindingResult(bce, "cmd");
		ValidationUtils.invokeValidator(bulkCreateEditHostsManager, bce,
				objectErrors);
		if(objectErrors.hasErrors()){
			System.err.println(objectErrors);
		}
		assertFalse(objectErrors.hasErrors());
	}
	public void testValidateChangeTypeOUAlterHosts() {
		log.debug("Validating a OU change  edit of  some hosts");
		OrgUnit newOu = new OrgUnit();
		newOu.setId("newou");
		newOu.setName("test OU");
		hostsDAO.saveOrgUnit(newOu);
		HostClass newHc= hostsDAO.getHostClassById("TESTINGHC");
		assertNotNull(newHc);
		

		BulkCreateEditHostsCmd bce = new BulkCreateEditHostsCmd();
		bce.setCreation(false);
		for(Host h: testHosts){
			h.getOwnership().setOrgUnit(newOu);
			h.setHostClass(newHc);
			
		}
		bce.setHosts(testHosts);
		Errors objectErrors = new BeanPropertyBindingResult(bce, "cmd");
		ValidationUtils.invokeValidator(bulkCreateEditHostsManager, bce,
				objectErrors);
		if(objectErrors.hasErrors()){
			System.err.println(objectErrors);
		}
		assertFalse(objectErrors.hasErrors());
	}
	
	public void testValidateChangeOUAlterHosts() {
		log.debug("Validating a OU change  edit of  some hosts");
		OrgUnit newOu = new OrgUnit();
		newOu.setId("newou");
		newOu.setName("test OU");
		hostsDAO.saveOrgUnit(newOu);
		
		BulkCreateEditHostsCmd bce = new BulkCreateEditHostsCmd();
		bce.setCreation(false);
		for(Host h: testHosts){
			h.getOwnership().setOrgUnit(newOu);
		}
		bce.setHosts(testHosts);
		Errors objectErrors = new BeanPropertyBindingResult(bce, "cmd");
		ValidationUtils.invokeValidator(bulkCreateEditHostsManager, bce,
				objectErrors);
		if(objectErrors.hasErrors()){
			System.err.println(objectErrors);
		}
		assertFalse(objectErrors.hasErrors());
	}

	public void testExpandRefreshDNSAlterHosts() {
		log.debug("Validating a DNS refresh edit of some hosts");
		BulkCreateEditHostsCmd bce = new BulkCreateEditHostsCmd();
		commandDispatcherRegistry.setUpCommandDefaults(bce);
		DNSHostCommandFlags dnsflags = (DNSHostCommandFlags) bce
				.getOptionData().get(DNSHostsInterceptor.DNS_FLAGS_KEY);
		assertNotNull(dnsflags);
		dnsflags.setUpdateMode(DNSUpdateMode.DNS_REFRESH_ALL_DATA);
		dnsflags.setUpdateMode(DNSUpdateMode.DNS_REFRESH_ALL_DATA);
		dnsflags.setForceDNSUpdates(true);
		DHCPHostCommandFlags dhcpflags = (DHCPHostCommandFlags) bce
				.getOptionData().get(DHCPHostCommandFlags.DHCP_FLAGS_KEY);

		assertNotNull(dhcpflags);
		dhcpflags.setRefreshDHCP(true);

		bce.setCreation(false);
		bce.setHosts(testHosts);
		Errors objectErrors = new BeanPropertyBindingResult(bce, "cmd");
		ValidationUtils.invokeValidator(bulkCreateEditHostsManager, bce,
				objectErrors);
		commandDispatcherRegistry.expandCommand(bce);
		assertFalse(objectErrors.hasErrors());
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
