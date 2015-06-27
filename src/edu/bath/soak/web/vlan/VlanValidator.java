package edu.bath.soak.web.vlan;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import edu.bath.soak.net.model.NetDAO;
import edu.bath.soak.net.model.Vlan;

public class VlanValidator implements Validator {
	NetDAO hostsDAO;

	public boolean supports(Class arg0) {
		return Vlan.class.isAssignableFrom(arg0);
	}

	public void validate(Object arg, Errors errors) {
		Assert.isInstanceOf(Vlan.class, arg);
		Vlan v = (Vlan) arg;
		ValidationUtils.rejectIfEmpty(errors, "number", "invalid-value",
				"Vlan number required");
		ValidationUtils.rejectIfEmpty(errors, "name", "invalid-value",
				"Vlan name required");

		Vlan other;
		if (null != (other = hostsDAO.getVlanByNumber(v.getNumber()))&& !other.getId().equals(v.getId())) {
			errors.rejectValue("number", "invalid-valie",
					"This vlan number is already assigned to vlan: "
							+ other.getName());
		}
	}

	@Required
	public void setHostsDAO(NetDAO hostsDAO) {
		this.hostsDAO = hostsDAO;
	}

}
