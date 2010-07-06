package edu.bath.soak.net.cmd;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

import edu.bath.soak.security.SecurityHelper;

/**
 * Command validator for create/edit host commands
 * 
 * @author cspocc
 * 
 */
public class DeleteHostUICmdValidator extends HookableValidator {
	SecurityHelper securityHelper;

	public boolean supports(Class clazz) {
		return clazz.isAssignableFrom(DeleteHostUICmd.class);
	}

	@Override
	public void validate(Object target, Errors errors) {
		Assert.notNull(target);
		Assert.isInstanceOf(DeleteHostUICmd.class, target);
		DeleteHostUICmd cmd = (DeleteHostUICmd) target;
		ValidationUtils.rejectIfEmpty(errors, "host", "field-required",
				"you must specify a host for deletion");
		if (null != cmd.getHost()
				&& !securityHelper.canEdit(cmd.getHost().getOwnership())) {
			errors.rejectValue("host", "no-permission",
					"You do not have permission to delete this host");
		}
		super.validate(target, errors);
	}

	@Required
	public void setSecurityHelper(SecurityHelper securityHelper) {
		this.securityHelper = securityHelper;
	}

}
