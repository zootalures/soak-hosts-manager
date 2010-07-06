package edu.bath.soak.net.cmd;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import edu.bath.soak.net.model.Host;

/**
 * Base host command validator, ensures that hosts have a host class, IP address
 * name and org unit.
 * 
 * @author cspocc
 * 
 */
public class SaveHostCmdValidator implements Validator {
	HostValidator hostValidator;

	public boolean supports(Class clazz) {
		return SaveHostCmd.class.isAssignableFrom(clazz);
	}

	public void validate(Object target, Errors errors) {
		SaveHostCmd cmd = (SaveHostCmd) target;
		Host host = cmd.getHost();
		errors.pushNestedPath("host");
		try {
			hostValidator.validate(host, errors);
		} finally {
			errors.popNestedPath();
		}
	}

	@Required
	public void setHostValidator(HostValidator hostValidator) {
		this.hostValidator = hostValidator;
	}

}
