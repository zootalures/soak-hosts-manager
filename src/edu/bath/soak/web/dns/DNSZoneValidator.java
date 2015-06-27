package edu.bath.soak.web.dns;

import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import edu.bath.soak.dns.model.DNSZone;
import edu.bath.soak.dns.model.ReverseZone;

public class DNSZoneValidator implements Validator {

	public boolean supports(Class clazz) {
		return DNSZone.class.isAssignableFrom(clazz);
	}

	public void validate(Object target, Errors errors) {
		Assert.isInstanceOf(DNSZone.class, target);
		DNSZone zone = (DNSZone) target;
		ValidationUtils.rejectIfEmpty(errors, "domain", "invalid-value",
				"Domain must be specified");

		if (StringUtils.hasText(zone.getDomain())) {
			if (zone instanceof ReverseZone) {
				if (!zone.getDomain().endsWith(".in-addr.arpa.")) {
					errors.rejectValue("domain", "invalid-value",
							"Invalid reverse domain-name");
				}
			}

			if (!zone.getDomain().matches("[0-9A-Za-z-_\\.]*")) {
				errors.rejectValue("domain", "invalid-value",
						"Domain name contains invalid characters");
			}
		}
		ValidationUtils.rejectIfEmpty(errors, "serverIP", "invalid-value",
				"Server IP must be specified");
		ValidationUtils.rejectIfEmpty(errors, "displayName", "invalid-value",
				"Domain must be specified");
		ValidationUtils.rejectIfEmpty(errors, "sigKey", "invalid-value",
				"TSIG Key must be specified");
		if (StringUtils.hasText(zone.getIgnoreHostRegexps())) {
			try {
				zone.getCompiledIgnoreHostRegexps();
			} catch (Exception e) {
				errors.rejectValue("ignoreHostRegexps", "invalid-value",
						"One or more expressions did not compile");
			}
		}
	}
}
