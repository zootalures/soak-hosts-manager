package edu.bath.soak.web.admin.networkclass;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import edu.bath.soak.net.model.NetDAO;

public class EditNetworkClassCommandValidator implements Validator {

	NetDAO hostsDAO;

	public boolean supports(Class clazz) {
		return EditNetworkClassCommand.class.isAssignableFrom(clazz);
	}

	public void validate(Object target, Errors errors) {
		Assert.isInstanceOf(EditNetworkClassCommand.class, target);
		ValidationUtils.rejectIfEmpty(errors, "networkClass.id",
				"required-field", "Field required");
		ValidationUtils.rejectIfEmpty(errors, "networkClass.name",
				"required-field", "Field required");
		EditNetworkClassCommand cmd = (EditNetworkClassCommand) target;

		try {
			if (cmd.isCreation()
					&& StringUtils.hasText(cmd.getNetworkClass().getId())
					&& null != hostsDAO.getNetworkClassById(cmd.getNetworkClass()
							.getId())) {
				errors.rejectValue("networkClass.id", "object-exists",
						"a network class with this ID already exists");
			}
		} catch (Exception e) {
		}
	}

	@Required
	public void setHostsDAO(NetDAO hostsDAO) {
		this.hostsDAO = hostsDAO;
	}
}
