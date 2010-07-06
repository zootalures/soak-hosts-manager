package edu.bath.soak.web.vlan;

import org.springframework.util.Assert;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

public class DeleteVlanCommandValidator implements Validator {

	public boolean supports(Class arg0) {
		return DeleteVlanCommand.class.isAssignableFrom(arg0);
	}

	public void validate(Object arg, Errors errors) {
		Assert.isInstanceOf(DeleteVlanCommand.class, arg);
		DeleteVlanCommand vc = (DeleteVlanCommand) arg;
		ValidationUtils.rejectIfEmpty(errors, "vlan", "invalid-value",
				"Vlan required");
		if (vc.getVlan().getSubnets().size() > 0) {
			if (vc.getMoveToVlan() != null
					&& vc.getVlan().equals(vc.getMoveToVlan()))
				errors.rejectValue("moveToVlan", "invalid-value",
						"New vlan must be different from deleted vlan ");

		}
	}

}
