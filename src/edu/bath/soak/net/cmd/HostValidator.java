package edu.bath.soak.net.cmd;

import java.util.List;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import edu.bath.soak.net.model.Host;
import edu.bath.soak.net.model.HostAlias;
import edu.bath.soak.net.model.HostName;
import edu.bath.soak.net.model.NetDAO;
import edu.bath.soak.net.model.Subnet;
import edu.bath.soak.net.model.HostAlias.HostAliasType;
import edu.bath.soak.security.SecurityHelper;

/**
 * Base host validator
 * 
 * checks that host naming is consistent and unique, that IP addresses are set
 * and are unique and that MAC addresses are unique where set.
 * 
 * 
 * @author cspocc
 * 
 */
public class HostValidator implements Validator {

	SecurityHelper securityHelper;

	@Required
	public void setHostsDAO(NetDAO hostsDAO) {
		this.hostsDAO = hostsDAO;
	}

	NetDAO hostsDAO;

	public boolean supports(Class clazz) {
		// TODO Auto-generated method stub
		return Host.class.isAssignableFrom(clazz);
	}

	public void validate(Object target, Errors errors) {
		Assert.isInstanceOf(Host.class, target);
		Host host = (Host) target;
		validateIPAddressOK(host, errors);
		validateNameOK(host, errors);
		validateMacOK(host, errors);
		validateHostAliasesOK(host, errors);
		validateHostClassOK(host, errors);
		validateOrgUnitOK(host, errors);

	}

	public void validateHostAliasesOK(Host host, Errors errors) {

		int i = 0;
		if (host.getHostClass() == null)
			return;
		// all alias names must be proper host names
		boolean canHaveAliases = (host.getHostClass().getCanHaveAliases() != null && host
				.getHostClass().getCanHaveAliases() == true)
				|| securityHelper.isAdmin();

		for (HostAlias ha : host.getHostAliases()) {
			try {
				errors.pushNestedPath("hostAliases[" + i + "].alias");
				validateHostName(ha.getAlias(), errors);
			} finally {
				errors.popNestedPath();
			}
			i++;
		}

		// secondary validation
		i = 0;
		if (!errors.hasErrors()) {
			for (HostAlias ha : host.getHostAliases()) {
				String aliasPath = "hostAliases[" + i + "].alias";

				for (HostAlias haother : host.getHostAliases()) {
					if (haother != ha
							&& ha.getAlias().equals(haother.getAlias())) {
						errors.rejectValue(aliasPath, "invalid-value",
								"Aliases must have different names");
					}
				}

				if (ha.getAlias().equals(host.getHostName())) {
					errors.rejectValue(aliasPath, "invalid-value",
							"Alias must differ from host name");
				}

				if (!canHaveAliases) {
					if (ha.getId() == null) {
						errors
								.rejectValue(aliasPath, "invalid-value",
										"you cannot create aliases for this type of host");
						continue;
					} else {
						HostAlias storedAlias = hostsDAO.findAlias(ha.getId());

						if (!ha.equals(storedAlias)) {
							errors
									.rejectValue(aliasPath, "invalid-value",
											"you cannot edit aliases for this type of host");
							continue;

						}
						// we allow existing aliases to stay the same
					}
				}
				// for CNAME aliases we enforce that the name is not in use
				// elsewhere
				if (ha.getType().equals(HostAliasType.CNAME)) {
					List<Host> otherHosts = hostsDAO
							.findHostIncludingAliases(ha.getAlias());
					if (otherHosts.size() != 0
							&& !otherHosts.get(0).getId().equals(host.getId())) {
						errors.rejectValue(aliasPath, "invalid-value",
								"Name is already in use by host "
										+ otherHosts.get(0).getHostName());
					}
				} else if (ha.getType().equals(HostAliasType.AREC)) {
					// for AREC aliases we ensure that it is only used in alises
					Host oh = hostsDAO.findHost(ha.getAlias());
					if (oh != null && !oh.getId().equals(host.getId())) {
						errors.rejectValue(aliasPath, "invalid-value",
								"Name is already in use by host "
										+ oh.getHostName());
					}
					List<HostAlias> otherAliases = hostsDAO.findAliases(ha
							.getAlias());
					for (HostAlias otherAlias : otherAliases) {
						if (!otherAlias.getType().equals(HostAliasType.AREC)
								&& !otherAlias.getHost().getId().equals(
										host.getId())) {
							errors.rejectValue(aliasPath, "invalid-value",
									"Name is already in as a non-AREC alias on "
											+ otherAlias.getHost()
													.getHostName());
							break;
						}
					}

				}
				i++;
			}
		}
	}

	public void validateHostClassOK(Host host, Errors errors) {
		ValidationUtils.rejectIfEmpty(errors, "hostClass", "field-required",
				"Host class must be specified");

	}

	/**
	 * Validates a given host name
	 * 
	 * @param hostname
	 * @param errors
	 */
	public void validateHostName(HostName hostname, Errors errors) {

		ValidationUtils.rejectIfEmpty(errors, "domain", "required-field",
				"Domain is required for this host name");
		String shortName = hostname.getName();

		if (!shortName.matches("[0-9A-Za-z-]*") || shortName.startsWith("-")
				|| shortName.endsWith("-")) {
			errors
					.rejectValue(
							"name",
							"invalid-syntax",
							"Invalid host name, host names my only contain alphanumerical characters and must not start or end with a hyphen.");

		}
		if (shortName.length() > 63) {
			errors
					.rejectValue("name", "invalid-syntax",
							"Invalid host name, name must be less than 64 characters long.");
		}

	}

	public void validateIPAddressOK(Host host, Errors errors) {
		// ensure host is on a subnet
		if (null == host.getIpAddress()) {
			errors.rejectValue("ipAddress", "field-required",
					"IP Address is required");
			return;
		}

		Subnet s = hostsDAO.findSubnetContainingIP(host.getIpAddress());
		if (null == s) {
			errors.rejectValue("ipAddress", "invalid-value",
					"No subnets were found which matched this IP");
		}
		Host otherh = hostsDAO.findHost(host.getIpAddress());
		if (null != otherh && !otherh.getId().equals(host.getId())) {
			errors.rejectValue("ipAddress", "field-in-use",
					"This IP is already associated with host "
							+ otherh.getHostName());
		}

	}

	public void validateMacOK(Host host, Errors errors) {
		// MAC address must be unique
		if (null != host.getMacAddress()) {
			Host otherh = hostsDAO.findHost(host.getMacAddress());
			if (null != otherh && !otherh.getId().equals(host.getId())) {
				errors.rejectValue("macAddress", "field-in-use",
						"MAC is already associated with host "
								+ otherh.getHostName());
			}
		}
	}

	public void validateNameOK(Host host, Errors errors) {

		Assert.notNull(host);
		ValidationUtils.rejectIfEmpty(errors, "hostName", "field-required",
				"Host name must be specified");
		
		if (host.getHostName() != null) {
			ValidationUtils.rejectIfEmpty(errors, "hostName.name",
					"field-required", "Host name must be specified");
			ValidationUtils.rejectIfEmpty(errors, "hostName.domain",
					"field-required", "Host domain must be specified");
			if (errors.hasErrors())
				return;

			try {
				errors.pushNestedPath("hostName");
				validateHostName(host.getHostName(), errors);
			} finally {
				errors.popNestedPath();
			}
			if (errors.hasErrors())
				return;

			List<Host> otherHosts = hostsDAO.findHostIncludingAliases(host
					.getHostName());
			for (Host otherh : otherHosts) {
				if (null != otherh && !otherh.getId().equals(host.getId())) {
					errors.rejectValue("hostName", "field-in-use",
							"Hostname is already associated with host with IP "
									+ otherh.getIpAddress().getHostAddress());
				}
			}
		}
	}

	public void validateOrgUnitOK(Host host, Errors errors) {
		ValidationUtils.rejectIfEmpty(errors, "ownership.orgUnit",
				"field-required", "Organisational unit must be specified");
	}

	@Required
	public void setSecurityHelper(SecurityHelper securityHelper) {
		this.securityHelper = securityHelper;
	}

}
