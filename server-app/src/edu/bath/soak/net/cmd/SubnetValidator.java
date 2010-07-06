package edu.bath.soak.net.cmd;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import edu.bath.soak.net.model.NetDAO;
import edu.bath.soak.net.model.Subnet;
import edu.bath.soak.util.TypeUtils;

public class SubnetValidator implements Validator {
	NetDAO hostsDAO;
	Logger log = Logger.getLogger(SubnetValidator.class);

	public boolean supports(Class clazz) {
		return Subnet.class.isAssignableFrom(clazz);
	}

	public void validate(Object target, Errors errors) {
		Subnet s = (Subnet) target;
		log.trace("validating subnet " + s);

		ValidationUtils.rejectIfEmpty(errors, "minIP", "required_field",
				"Min address must be specified");
		ValidationUtils.rejectIfEmpty(errors, "maxIP", "required_field",
				"Max address must be specified");
		ValidationUtils.rejectIfEmpty(errors, "name", "required_field",
				"Name must be specified");
		ValidationUtils.rejectIfEmpty(errors, "networkClass", "required_field",
				"the network class must be specified");

		if (errors.hasErrors()) {
			return;
		}

		log.trace("checking CIDR");

		List<Subnet> list = hostsDAO.findSubnetsTouchingRange(s.getMinIP(), s
				.getMaxIP());

		Subnet clash = null;
		for (Subnet csn : list) {
			log.trace("got possible clash subnet :" + s);
			if (!csn.getId().equals(s.getId())) {
				clash = csn;
				break;
			} else {
				log.trace("not a clash after all");
			}
		}

		if (null != clash) {
			errors.rejectValue("", "malformed_field",
					"Subnet clashes with another one,  first clash is with subnet \""
							+ clash.getName() + "\"");
		}

		if (null != s.getGateway()
				&& !TypeUtils.ipInRange(s.getGateway(), s.getMinIP(), s
						.getMaxIP())) {
			errors
					.rejectValue("gateway", "invalid",
							"Gateway address must be within useable address scope of subnet");
		}
	}

	public NetDAO getHostsDAO() {
		return hostsDAO;
	}

	@Required
	public void setHostsDAO(NetDAO hostsDAO) {
		this.hostsDAO = hostsDAO;
	}

}
