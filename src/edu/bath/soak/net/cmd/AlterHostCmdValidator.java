package edu.bath.soak.net.cmd;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

import edu.bath.soak.cache.CacheableValidatorMethod;
import edu.bath.soak.mgr.AddressSpaceManager;
import edu.bath.soak.mgr.AddressSpaceManager.AddressSpaceFullException;
import edu.bath.soak.net.model.Host;
import edu.bath.soak.net.model.HostClass;
import edu.bath.soak.net.model.NetDAO;
import edu.bath.soak.net.model.Subnet;
import edu.bath.soak.net.model.HostClass.DHCP_STATUS;
import edu.bath.soak.security.SecurityHelper;
import edu.bath.soak.util.TypeUtils;

/**
 * Command validator for create/edit host commands
 * 
 * @author cspocc
 * 
 */
public class AlterHostCmdValidator extends HookableValidator {
	NetDAO hostsDAO;
	SecurityHelper securityHelper;
	HostValidator hostValidator;
	AddressSpaceManager addressSpaceManager;

	@Required
	public void setHostsDAO(NetDAO dao) {
		this.hostsDAO = dao;
	}

	public boolean supports(Class clazz) {
		return clazz.isAssignableFrom(AlterHostCmd.class);
	}

	@Override
	@CacheableValidatorMethod
	public void validate(Object target, Errors errors) {
		Assert.notNull(target);
		Assert.isInstanceOf(AlterHostCmd.class, target);
		AlterHostCmd cmd = (AlterHostCmd) target;
		validateHostDetails(target, errors);
		if (errors.hasErrors())
			return;
		validateHostSemantics(target, errors);
		if (errors.hasErrors())
			return;
		if (!cmd.isSpecifyIp()) {
			// pre allocate IP address
			addressSpaceManager.allocateIPAddress(cmd.getNewHost(), cmd
					.getSubnet());
		}

		super.validate(target, errors);
	}

	/**
	 * Ensures that the host class is specified
	 * 
	 * @param target
	 * @param errors
	 */
	public void validateHostClass(Object target, Errors errors) {
		ValidationUtils.rejectIfEmpty(errors, "newHost.hostClass",
				"field-required", "Host class must be specified");
	}

	public void validateHostAliases(Object target, Errors errors) {
		AlterHostCmd cmd = (AlterHostCmd) target;
		try {

			errors.pushNestedPath("newHost");
			hostValidator.validateHostAliasesOK(cmd.getNewHost(), errors);

		} finally {
			errors.popNestedPath();
		}
	}

	/**
	 * Choke point for permissions, gets slotted into semantic checking.
	 * 
	 * @param target
	 * @param existingHost
	 * @param errors
	 */
	public void checkPermissions(AlterHostCmd target, Host existingHost,
			Errors errors) {
		AlterHostCmd cmd = (AlterHostCmd) target;
		Assert.isTrue(cmd.isCreation() || existingHost != null);
		if (!securityHelper.canEdit(cmd.getNewHost().getOwnership())) {
			if (!cmd.isCreation()) {
				errors.reject("permission denied",
						"You do not have permission to edit hosts in this OU");

			} else {
				errors
						.reject("permission denied",
								"You do not have permission to create hosts in this OU");
			}
		}

		if (cmd.isCreation()
				|| !existingHost.getIpAddress().equals(
						cmd.getNewHost().getIpAddress())) {
			Subnet s = hostsDAO.findSubnetContainingIP(cmd.getNewHost()
					.getIpAddress());
			if (!securityHelper.canUseOrgUnitAclEntity(s)) {
				errors
						.rejectValue("newHost.ipAddress", "no-permissions",
								"You do not have permission to add hosts to this subnet");
			} else if (!securityHelper.isAdmin()
					&& !securityHelper.canUse(s, cmd.getNewHost()
							.getOwnership().getOrgUnit())) {

				errors
						.rejectValue("newHost.ipAddress", "no-permissions",
								"The owner of this host does not have permission to add hosts to this subnet.");
			}
		}

		/**
		 * Check that any host class change is permitted
		 */
		if (cmd.isCreation()
				|| !existingHost.getHostClass().equals(
						cmd.getNewHost().getHostClass())) {
			if (!securityHelper.canUseOrgUnitAclEntity(cmd.getNewHost()
					.getHostClass())) {
				errors
						.rejectValue("newHost.hostClass", "no-permissions",
								"You do not have permission to create hosts of this type");
			} else if (!securityHelper.isAdmin()
					&& !securityHelper.canUse(cmd.getNewHost().getHostClass(),
							cmd.getNewHost().getOwnership().getOrgUnit())) {
				errors
						.rejectValue("newHost.hostClass", "no-permissions",
								"The owner of this host does not have permissions to create hosts of this type");

			}
		}

		/**
		 * Check that any name domain change is permitted
		 */
		if (cmd.isCreation()
				|| !existingHost.getHostName().getDomain().equals(
						cmd.getNewHost().getHostName().getDomain())) {
			if (!securityHelper.canUseOrgUnitAclEntity(cmd.getNewHost()
					.getHostName().getDomain())) {
				errors
						.rejectValue("newHost.hostName.domain",
								"no-permissions",
								"You do not have permission to create hosts in this domain");

			} else if (!securityHelper.isAdmin()
					&& !securityHelper.canUse(cmd.getNewHost().getHostName()
							.getDomain(), cmd.getNewHost().getOwnership()
							.getOrgUnit())) {
				errors
						.rejectValue(
								"newHost.hostName.domain",
								"no-permissions",
								"The owner of this host does not have permissions to create hosts in this domain");

			}

		}

	}

	public void validateHostClassNaming(AlterHostCmd cmd, Host existingHost,
			Errors errors) {

		if (cmd.isCreation()
				|| (existingHost != null && !existingHost.getHostName().equals(
						cmd.getNewHost().getHostName()))) {
			HostClass hc = cmd.getNewHost().getHostClass();
			if (!hc.hostNameMatchesAllowed(cmd.getNewHost().getHostName()
					.getName())) {
				errors.rejectValue("newHost.hostName.name", "invalid-name",
						"This name is not valid for this type of host names must  follow the pattern:"
								+ hc.getExampleName());
			}

		}

		ValidationUtils.rejectIfEmpty(errors, "newHost.hostClass",
				"field-required", "Host class must be specified");
	}

	public void validateHostDetails(Object target, Errors errors) {

		AlterHostCmd cmd = (AlterHostCmd) target;
		try {
			errors.pushNestedPath("newHost");
			hostValidator.validateNameOK(cmd.getNewHost(), errors);
			hostValidator.validateHostClassOK(cmd.getNewHost(), errors);
			if (cmd.isSpecifyIp())
				hostValidator.validateIPAddressOK(cmd.getNewHost(), errors);

			hostValidator.validateHostAliasesOK(cmd.getNewHost(), errors);
			hostValidator.validateMacOK(cmd.getNewHost(), errors);
			hostValidator.validateOrgUnitOK(cmd.getNewHost(), errors);

		} finally {
			errors.popNestedPath();
		}

	}

	public boolean needsDHCP(Host h) {
		DHCP_STATUS status = h.getHostClass().getDHCPStatus();
		return (status == DHCP_STATUS.REQUIRED);
	}

	/**
	 * Validates the base name and address semantics for this host
	 * 
	 * @param target
	 * @param errors
	 */
	public void validateHostSemantics(Object target, Errors errors) {
		AlterHostCmd cmd = (AlterHostCmd) target;

		Host h = cmd.getNewHost();
		Host existingHost = null;
		if (cmd.getNewHost().getId() != null) {
			existingHost = hostsDAO.loadHost(cmd.getNewHost().getId());
		}

		validateHostClassNaming(cmd, existingHost, errors);

		if ((cmd.isCreation() || !TypeUtils.nullSafeCompare(existingHost
				.getMacAddress(), cmd.getNewHost().getMacAddress()))
				&& needsDHCP(cmd.getNewHost())
				&& null == cmd.getNewHost().getMacAddress()) {
			errors.rejectValue("newHost.macAddress", "field-required",
					"Mac address must be specified for this type of host.");
		}

		if ((null == existingHost
				|| (!existingHost.getHostName().getDomain().equals(
						cmd.getNewHost().getHostName().getDomain())) || !(existingHost
				.getHostClass().equals(cmd.getNewHost().getHostClass())))
				&& !cmd.getNewHost().getHostName().getDomain()
						.getAllowedClasses().contains(h.getHostClass())) {
			errors.rejectValue("newHost.hostName", "invalid-domain",
					"Hosts of type " + h.getHostClass().getName()
							+ " may not be added to this domain");
		}
		if (!cmd.isSpecifyIp()) {
			// ensure that an IP address is allocated to host before we proceed
			// FIXME: This code is really in the wrong place, it should be
			// refactored to somewhere more obvious
			cmd.getNewHost().setIpAddress(null);
			try {
				cmd.getNewHost().setIpAddress(
						addressSpaceManager.allocateIPAddress(cmd.getNewHost(),
								cmd.getSubnet()));
			} catch (AddressSpaceFullException full) {
				errors.rejectValue("subnet", "semantic-error",
						"No free usable addresses were found on this subnet");
				return;
			}

		}
		Subnet subnet = hostsDAO.findSubnetContainingIP(h.getIpAddress());
		if (subnet == null
				&& (cmd.isCreation() || (null != existingHost && (!existingHost
						.getIpAddress().equals(cmd.getNewHost().getIpAddress()))))) {
			errors.rejectValue("subnet", "semantic-error",
					"Subnet does not exist");
			return;
		}
		if ((null == existingHost || !existingHost.getHostClass().equals(
				cmd.getNewHost().getHostClass()))
				&& !subnet.canAddHostClass(cmd.getNewHost().getHostClass())) {
			errors.rejectValue("newHost.hostClass", "invalid-subnet",
					"Host of this type may not be placed on this subnet");

		}

		checkPermissions(cmd, existingHost, errors);

	}

	@Required
	public void setAddressSpaceManager(AddressSpaceManager addressSpaceManager) {
		this.addressSpaceManager = addressSpaceManager;
	}

	@Required
	public void setSecurityHelper(SecurityHelper securityHelper) {
		this.securityHelper = securityHelper;
	}

	@Required
	public void setHostValidator(HostValidator hostValidator) {
		this.hostValidator = hostValidator;
	}

}
