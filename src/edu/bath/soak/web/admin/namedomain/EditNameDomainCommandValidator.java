package edu.bath.soak.web.admin.namedomain;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import edu.bath.soak.net.model.NetDAO;

public class EditNameDomainCommandValidator implements Validator {

	NetDAO hostsDAO;

	public boolean supports(Class clazz) {
		return EditNameDomainCommand.class.isAssignableFrom(clazz);
	}

	public void validate(Object target, Errors errors) {
		Assert.isInstanceOf(EditNameDomainCommand.class, target);
		ValidationUtils.rejectIfEmpty(errors, "nameDomain.suffix",
				"required-field", "Field required");
		EditNameDomainCommand cmd = (EditNameDomainCommand) target;

		try {
			if (cmd.isCreation()
					&& StringUtils.hasText(cmd.getNameDomain().getSuffix())
					&& null != hostsDAO.getNameDomainBySuffix(cmd
							.getNameDomain().getSuffix())) {
				errors.rejectValue("nameDomain.suffix", "object-exists",
						"a name domain with this ID already exists");
			}

			String suffix = cmd.getNameDomain().getSuffix();
			if (!suffix.startsWith(".") || !suffix.endsWith(".")) {
				errors.rejectValue("nameDomain.suffix", "semantic-error",
						"Name domain suffix must start and end with a dot.");
			}

		} catch (Exception e) {
		}
	}

	@Required
	public void setHostsDAO(NetDAO hostsDAO) {
		this.hostsDAO = hostsDAO;
	}
}
