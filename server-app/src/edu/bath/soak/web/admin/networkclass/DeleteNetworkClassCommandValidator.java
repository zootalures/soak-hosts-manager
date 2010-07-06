/**
 * 
 */
package edu.bath.soak.web.admin.networkclass;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

public class DeleteNetworkClassCommandValidator implements Validator {
	public boolean supports(Class clazz) {
		return DeleteNetworkClassCommand.class.isAssignableFrom(clazz);
	}

	public void validate(Object target, Errors errors) {
		ValidationUtils.rejectIfEmpty(errors, "replaceWith",
				"requiredField", "field is required");
		ValidationUtils.rejectIfEmpty(errors, "toDelete", "requiredField",
				"field is required");
		DeleteNetworkClassCommand dc = (DeleteNetworkClassCommand) target;
		if (dc.toDelete.equals(dc.replaceWith)) {
			errors.rejectValue("replaceWith", "invalid-input",
					"replacement host class must differ from existing one");
		}
	}
}