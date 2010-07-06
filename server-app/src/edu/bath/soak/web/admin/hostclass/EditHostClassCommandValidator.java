package edu.bath.soak.web.admin.hostclass;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import edu.bath.soak.net.model.NetDAO;

public class EditHostClassCommandValidator implements Validator {

	NetDAO hostsDAO;

	public boolean supports(Class clazz) {
		return EditHostClassCommand.class.isAssignableFrom(clazz);
	}

	public void validate(Object target, Errors errors) {
		Assert.isInstanceOf(EditHostClassCommand.class, target);
		ValidationUtils.rejectIfEmpty(errors, "hostClass.id", "required-field",
				"Field required");
		ValidationUtils.rejectIfEmpty(errors, "hostClass.name",
				"required-field", "Field required");
		ValidationUtils.rejectIfEmpty(errors, "hostClass.DHCPStatus",
				"required-field", "Field required");
		EditHostClassCommand cmd = (EditHostClassCommand) target;
		try {
			cmd.getHostClass().getAllowedNamePatternsCompiled();
		} catch (Exception e) {
			errors.rejectValue("networkClass.allowedNamePatterns",
					"invalid-sytnax", "Unable to parse regular expression");
		}

		try {
			if (cmd.isCreation()
					&& StringUtils.hasText(cmd.getHostClass().getId())
					&& null != hostsDAO.getHostClassById(cmd.getHostClass()
							.getId())) {
				errors.rejectValue("networkClass.id", "object-exists",
						"a host class with this ID already exists");
			}
		} catch (Exception e) {
		}
	}

	@Required
	public void setHostsDAO(NetDAO hostsDAO) {
		this.hostsDAO = hostsDAO;
	}
}
