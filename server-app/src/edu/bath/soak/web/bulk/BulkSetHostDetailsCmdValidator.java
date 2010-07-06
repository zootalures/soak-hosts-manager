package edu.bath.soak.web.bulk;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.validation.Errors;

import edu.bath.soak.net.cmd.HookableValidator;
import edu.bath.soak.security.SecurityHelper;

public class BulkSetHostDetailsCmdValidator extends HookableValidator {

	SecurityHelper securityHelper;

	@Required
	public void setSecurityHelper(SecurityHelper securityHelper) {
		this.securityHelper = securityHelper;
	}

	public boolean supports(Class clazz) {
		return BulkSetHostDetailsCmd.class.isAssignableFrom(clazz);
	}

	/**
	 * Validates a given command by expanding into a series of alterhost details
	 * commands, performing validation on them and then translating all errors
	 * for any given host into a errros in the relevant host field of the
	 * command
	 */
	public void validate(Object target, Errors errors) {
		BulkSetHostDetailsCmd command = (BulkSetHostDetailsCmd) target;
		validateBulkAlterHostDetailsCmdSemantics(command, errors);
		if (errors.hasErrors()) {// we fail early if there is something
			// directly wrong with the command
			return;
		}
		super.validate(target, errors);
	}

	/***************************************************************************
	 * Validates the bulk command semantics for GUI or before execution
	 * 
	 * @param cmd
	 * @param errors
	 */
	public void validateBulkAlterHostDetailsCmdSemantics(
			BulkSetHostDetailsCmd cmd, Errors errors) {

		if (!(cmd.isDoChangeBuilding() || cmd.isDoChangeRoom()
				|| null != cmd.getNewHostClass()
				|| null != cmd.getNewNameDomain() || null != cmd
				.getNewOrgUnit())) {
			errors.reject("invalid-selection",
					"You must select at least one property to change");
		}
		if (cmd.getNewHostClass() != null
				&& !securityHelper
						.canUseOrgUnitAclEntity(cmd.getNewHostClass())) {
			errors
					.rejectValue("newHostClass", "no-permission",
							"You do not have permission to create this type of host class.");
		}
		if (cmd.getNewNameDomain() != null) {
			if (!securityHelper.canUseOrgUnitAclEntity(cmd.getNewNameDomain())) {
				errors
						.rejectValue("newNameDomain", "no-permission",
								"You do not have permission to create hosts on this name domain.");
			}
			if (cmd.getNewHostClass() != null
					&& !cmd.getNewNameDomain().getAllowedClasses().contains(
							cmd.getNewHostClass())) {
				errors.rejectValue("newNameDomain", "semantic-error",
						"Hosts of type " + cmd.getNewHostClass().getName()
								+ " are not allowed in the domain "
								+ cmd.getNewNameDomain().getSuffix());

			}
		}
		if (cmd.getNewOrgUnit() != null
				&& !securityHelper.isAdmin()
				&& !securityHelper.getAllowedOrgUnitsForCurrentUser().contains(
						cmd.getNewOrgUnit())) {
			errors
					.rejectValue(
							"newOrgUnit",
							"no-permission",
							"You do not have permission to create hosts belonging to this organsational unit.");
		}

	}

}
