package edu.bath.soak.web.subnet;

import java.net.Inet4Address;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import edu.bath.soak.net.model.NetDAO;
import edu.bath.soak.net.model.Subnet;
import edu.bath.soak.util.TypeUtils;

public class EditSubnetCommandValidator implements Validator {

	NetDAO hostsDAO;
	Logger log = Logger.getLogger(this.getClass());

	public boolean supports(Class clazz) {
		return EditSubnetCommand.class.isAssignableFrom(clazz);
	}

	public void validate(Object obj, Errors errors) {
		EditSubnetCommand s = (EditSubnetCommand) obj;
		log.trace("validating subnet command" + s);
		if (errors.hasErrors()) {
			log.trace("bailing out with binding errors");
			return;
		}
		ValidationUtils.rejectIfEmpty(errors, "baseAddress", "required_field",
				"Base address must be specified");
		ValidationUtils.rejectIfEmpty(errors, "numBits", "required_field",
				"Network bits must be specified");
		ValidationUtils.rejectIfEmpty(errors, "name", "required_field",
				"Name must be specified");
		ValidationUtils.rejectIfEmpty(errors, "networkClass", "required_field",
				"the network class must be specified");

		if (errors.hasErrors()) {
			return;
		}

		int numbits = s.getNumBits();
		if (numbits < 0 || numbits > 32) {
			errors.rejectValue("numBits", "malformed_field",
					"Invalid number of network bits");
		}

		log.trace("checking CIDR");
		// Valid base address now check it is a CIDR
		if (!TypeUtils.checkAddressIsCIDR(s.getBaseAddress(), numbits)) {
			errors.rejectValue("baseAddress", "malformed_field",
					"Not a valid address/mask (CIDR) combination");

		}
		if (errors.hasErrors()) {
			return;
		}

		// We should go on to validate the Subnet now...
		Inet4Address maxIP = TypeUtils.getCIDRMaxAddress(s.getBaseAddress(),
				numbits);

		log.trace("Looking for subnets in scope "
				+ s.getBaseAddress().getHostAddress() + " - "
				+ maxIP.getHostAddress());
		List<Subnet> list = hostsDAO.findSubnetsTouchingRange(s.getBaseAddress(),
				maxIP);

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
			errors.rejectValue("baseAddress", "malformed_field",
					"Subnet clashes with another one,  first clash is with subnet \""
							+ clash.getName() + "\"");
		}

		if (null != s.getGateway()
				&& !TypeUtils.ipInRange(s.getGateway(), TypeUtils.ipMath(s
						.getBaseAddress(), 1), TypeUtils.ipMath(maxIP, -1))) {
			errors
					.rejectValue("gateway", "invalid",
							"Gateway address must be within useable address scope of subnet");
		}
		if (errors.hasErrors()) {
			return;
		}
		log.trace("Validation successfull");

	}

	@Required
	public void setHostsDAO(NetDAO hostsDAO) {
		this.hostsDAO = hostsDAO;
	}

}
