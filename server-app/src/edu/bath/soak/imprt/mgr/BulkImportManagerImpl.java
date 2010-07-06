package edu.bath.soak.imprt.mgr;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

import edu.bath.soak.cmd.BaseCompositeCommand;
import edu.bath.soak.cmd.CmdException;
import edu.bath.soak.cmd.CmdValidationException;
import edu.bath.soak.cmd.CommandProcessor;
import edu.bath.soak.imprt.cmd.BulkCommand;
import edu.bath.soak.imprt.cmd.BulkHostsImportCmd;
import edu.bath.soak.imprt.cmd.BulkSystemImportCmd;
import edu.bath.soak.imprt.cmd.XMLImportData;
import edu.bath.soak.mgr.HostsManager;
import edu.bath.soak.model.OrgUnit;
import edu.bath.soak.net.cmd.HostValidator;
import edu.bath.soak.net.cmd.SubnetValidator;
import edu.bath.soak.net.model.Host;
import edu.bath.soak.net.model.HostClass;
import edu.bath.soak.net.model.NameDomain;
import edu.bath.soak.net.model.NetDAO;
import edu.bath.soak.net.model.NetworkClass;
import edu.bath.soak.net.model.Subnet;
import edu.bath.soak.net.model.Vlan;
import edu.bath.soak.undo.UndoNotSupportedException;

/*******************************************************************************
 * 
 * Implements system imports, takes a {@link BulkCommand} containing one or more
 * hosts,subnets, name domains, etc and saves them to the database
 * 
 * For hosts, it invokes the {@link HostValidator} to check basic semantic
 * integrity.
 * 
 * 
 * @author cspocc
 * 
 ******************************************************************************/
public class BulkImportManagerImpl implements CommandProcessor<BulkCommand> {

	HostsManager hostsManager;
	Logger log = Logger.getLogger(BulkImportManagerImpl.class);
	NetDAO hostsDAO;
	HostValidator hostValidator;
	SubnetValidator subnetValidator;

	public void expandCmd(BulkCommand cmd, BaseCompositeCommand result) {

	}

	public void expandUndo(BulkCommand cmd, BaseCompositeCommand result)
			throws CmdException {
		throw new UndoNotSupportedException(cmd);
	}

	@Transactional
	public void implementBulkSystemImportCmd(String commandId,
			BulkSystemImportCmd cmd) throws CmdException {

		XMLImportData change = cmd.getData();
		// Pre-validate hosts
		for (OrgUnit ou : change.getOrganisationalUnits()) {
			log.debug("saving orgUnit " + ou);
			hostsDAO.saveOrgUnit(ou);

		}
		for (HostClass hc : change.getHostClasses()) {
			log.debug("saving host class " + hc);
			hostsDAO.saveHostClass(hc);
		}
		for (NameDomain nd : change.getNameDomains()) {
			log.debug("saving nameDomain " + nd);
			hostsDAO.saveNameDomain(nd);
		}
		for (NetworkClass nc : change.getNetworkClasses()) {
			log.debug("saving netclass " + nc);
			hostsDAO.saveNetworkClass(nc);
		}
		for (Vlan vlan : change.getVlans()) {
			log.debug("saving vlan " + vlan);
			hostsDAO.saveVlan(vlan);
		}

		for (Subnet subnet : change.getSubnets()) {
			log.debug("saving subnet " + subnet);
			Errors objectErrors = new BeanPropertyBindingResult(subnet,
					"subnet");
			ValidationUtils.invokeValidator(subnetValidator, subnet,
					objectErrors);
			if (objectErrors.hasErrors()) {
				log.error("rejecting subnet:" + subnet + " with errors "
						+ objectErrors);
				throw new CmdValidationException(objectErrors);
			}
			hostsDAO.saveSubnet(subnet);
		}
		// log.debug("saving host" + host);
		try {
			log.debug("validating hosts");
			for (Host host : change.getHosts()) {
				Errors objectErrors = new BeanPropertyBindingResult(host,
						"host");
				ValidationUtils.invokeValidator(hostValidator, host,
						objectErrors);
				log.trace("validating " + host);
				if (objectErrors.hasErrors()) {
					log.error("bulk import command failed  host " + host
							+ " did not validate" + objectErrors);
					throw new CmdValidationException(objectErrors);
				}
			}

			log.debug("saving " + change.getHosts().size() + " hosts");
			hostsDAO.saveHosts(change.getHosts(), commandId, "Bulk import", "");
		} catch (Exception e) {
			throw new CmdException(e);
		}

	}

	public void implementCmd(BaseCompositeCommand baseCommand,
			BulkCommand change) throws CmdException {
		if (change instanceof BulkSystemImportCmd) {
			implementBulkSystemImportCmd(baseCommand.getCommandId(),
					(BulkSystemImportCmd) change);
		}
		throw new RuntimeException(
				"Commands of type "
						+ change.getClass()
						+ " are not supported by this may indicate a bug in Hibernate, but is more likely due to unsafe use of the controller");
	}

	public void rollBackChange(BulkCommand change) throws CmdException {

	}

	public boolean supportsCmdType(Class type) {
		return BulkHostsImportCmd.class.isAssignableFrom(type);
	}

	@Required
	@Resource(name = "hostsMgr")
	public void setHostsManager(HostsManager hostsManager) {
		this.hostsManager = hostsManager;
	}

	public void setUpCommandDefaults(BulkCommand cmd) {

	}

	public int getOrder() {
		return 0;
	}

	@Required
	public void setHostsDAO(NetDAO hostsDAO) {
		this.hostsDAO = hostsDAO;
	}

	@Required
	public void setHostValidator(HostValidator hostValidator) {
		this.hostValidator = hostValidator;
	}

	@Required
	public void setSubnetValidator(SubnetValidator subnetValidator) {
		this.subnetValidator = subnetValidator;
	}
}
