package edu.bath.soak.web.bulk;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

import edu.bath.soak.net.cmd.HookableValidator;
import edu.bath.soak.security.SecurityHelper;

/**
 * Web-tier command validator for {@link BulkSetIPsCmd} commands
 * 
 * @author cspocc
 * 
 */
public class BulkSetIPsCmdValidator extends HookableValidator {

	SecurityHelper securityHelper;

	@Required
	public void setSecurityHelper(SecurityHelper securityHelper) {
		this.securityHelper = securityHelper;
	}

	public boolean supports(Class clazz) {
		return BulkSetIPsCmd.class.isAssignableFrom(clazz);
	}

	/**
	 */
	public void validate(Object target, Errors errors) {
		BulkSetIPsCmd command = (BulkSetIPsCmd) target;
		ValidationUtils.rejectIfEmpty(errors, "newSubnet", "field-required",
				"You must specify a subnet to pick IPs from");
		if (null != command.getNewSubnet()) {
			if (!securityHelper.canUseOrgUnitAclEntity(command.getNewSubnet())) {
				ValidationUtils
						.rejectIfEmpty(errors, "newSubnet", "no-permission",
								"You don't have permission to create hosts on this subnet");

			}
		}
		super.validate(target, errors);
	}

}
