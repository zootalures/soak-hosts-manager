package edu.bath.soak.net.bulk;

import java.net.Inet4Address;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import edu.bath.soak.cmd.BaseCompositeCommand;
import edu.bath.soak.cmd.CmdException;
import edu.bath.soak.cmd.CmdValidationException;
import edu.bath.soak.cmd.CommandDispatcherRegistry;
import edu.bath.soak.cmd.CommandExpander;
import edu.bath.soak.mgr.AddressSpaceManager;
import edu.bath.soak.net.cmd.AlterHostCmd;
import edu.bath.soak.net.cmd.BulkMoveHostsCmd;
import edu.bath.soak.net.model.Host;
import edu.bath.soak.net.model.NetDAO;
import edu.bath.soak.security.SecurityHelper;

public class BulkMoveHostsManagerImpl extends
		BulkAlterHostCmdValidator<BulkMoveHostsCmd> implements
		CommandExpander<BulkMoveHostsCmd>, Validator {
	NetDAO hostsDAO;
	SecurityHelper securityHelper;
	Logger log = Logger.getLogger(BulkMoveHostsManagerImpl.class);
	CommandDispatcherRegistry commandDispatcherRegistry;
	AddressSpaceManager addressSpaceManager;

	public boolean canExpand(Class clazz) {
		return BulkMoveHostsCmd.class.isAssignableFrom(clazz);
	}

	public void expandCmd(BulkMoveHostsCmd cmd, BaseCompositeCommand result)
			throws CmdException {
		Errors objectErrors = new BeanPropertyBindingResult(cmd, "cmd");
		ValidationUtils.invokeValidator(this, cmd, objectErrors);
		if (objectErrors.hasErrors())
			throw new CmdValidationException(objectErrors);
		for (Host h : cmd.getHosts()) {
			commandDispatcherRegistry.expandSubCommand(expandIntoAlterHostCmd(
					cmd, h), result);
		}

	}

	public int getOrder() {
		return 0;
	}

	public void setupCommand(BulkMoveHostsCmd cmd) {
	}

	public boolean supports(Class clazz) {
		return BulkMoveHostsCmd.class.isAssignableFrom(clazz);
	}

	/***************************************************************************
	 * expands a bulk move for a given host
	 * 
	 * Requires that the IP address for this host has already been chosen
	 * 
	 * @param command
	 * @param host
	 * @return
	 **************************************************************************/
	public AlterHostCmd expandIntoAlterHostCmd(BulkMoveHostsCmd command,
			Host host) {
		AlterHostCmd alterHostCmd = new AlterHostCmd();
		alterHostCmd.setNewHost(hostsDAO.getHostForEditing(host.getId()));
		Inet4Address address = command.getHostAddresses().get(host.getId());
		Assert.notNull(address);
		alterHostCmd.setSpecifyIp(true);
		alterHostCmd.getNewHost().setIpAddress(address);
		alterHostCmd.setChangeComments("Bulk move: "
				+ command.getChangeComments());
		return alterHostCmd;
	}

	public void assignAddresses(BulkMoveHostsCmd cmd) {
		for (Host h : cmd.getHosts()) {
			cmd.getHostAddresses().put(
					h.getId(),
					addressSpaceManager
							.allocateIPAddress(h, cmd.getNewSubnet()));
		}
	}

	/***************************************************************************
	 * Validates that the chose subnet is OK
	 * 
	 * @param target
	 *            a {@link BulkMoveHostsCmd} command to validate
	 * 
	 * @param errors
	 **************************************************************************/
	public void validateSubnet(Object target, Errors errors) {
		BulkMoveHostsCmd cmd = (BulkMoveHostsCmd) target;
		ValidationUtils.rejectIfEmpty(errors, "newSubnet", "field-required",
				"You must specify a subnet ");
		if (null != cmd.getNewSubnet()
				&& !securityHelper.canUseOrgUnitAclEntity(cmd.getNewSubnet())) {
			errors.rejectValue("newSubnet", "no-permissions",
					"You do not have permissions to add hosts to this subnet");
		}
	}

	/***************************************************************************
	 * Validates a bulk move command checks that each host has an allocated
	 * address and that selected addresses do not clash
	 * 
	 * @param target
	 *            a {@link BulkMoveHostsCmd} command to validate
	 * 
	 **************************************************************************/
	public void validate(Object target, Errors errors) {
		// BulkMoveHostsCmd command = (BulkMoveHostsCmd) target;
		validateSubnet(target, errors);
		if (errors.hasErrors())
			return;

		super.validate(target, errors);

	}

	@Required
	public void setHostsDAO(NetDAO hostsDAO) {
		this.hostsDAO = hostsDAO;
	}

	@Required
	public void setSecurityHelper(SecurityHelper securityHelper) {
		this.securityHelper = securityHelper;
	}

	@Required
	public void setCommandDispatcherRegistry(
			CommandDispatcherRegistry commandDispatcherRegistry) {
		this.commandDispatcherRegistry = commandDispatcherRegistry;
	}

	@Required
	public void setAddressSpaceManager(AddressSpaceManager addressSpaceManager) {
		this.addressSpaceManager = addressSpaceManager;
	}
}
