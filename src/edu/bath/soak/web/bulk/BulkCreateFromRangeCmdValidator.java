package edu.bath.soak.web.bulk;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import edu.bath.soak.propertyeditors.HostNameEditor;
import edu.bath.soak.util.TypeUtils;

public class BulkCreateFromRangeCmdValidator implements Validator {
	HostNameEditor hostNameEditor;
	int maxHosts = 1000;

	public boolean supports(Class clazz) {
		return clazz.isAssignableFrom(BulkCreateFromRangeCmd.class);
	}

	public void validate(Object target, final Errors errors) {
		BulkCreateFromRangeCmd command = (BulkCreateFromRangeCmd) target;
		if (null == command.getRange1().getMax()
				|| null == command.getRange1().getMin()) {
			errors.rejectValue("range1", "invalid-value",
					"Both a minimum and a maximum must be specified");
			return;
		}
		if (command.getRange1().getMax() < command.getRange1().getMin()) {
			errors.rejectValue("range1", "invalid-value", "Invalid range");
		}
		if ((command.getRange2().getMax() == null && command.getRange2()
				.getMin() != null)
				|| (command.getRange2().getMax() != null && command.getRange2()
						.getMin() == null)) {
			errors.rejectValue("range2", "invalid-value", "Invalid Range");

		}
		if (!StringUtils.hasText(command.getHostnameTemplate())) {
			errors.rejectValue("hostnameTemplate", "required",
					"You must specify a template");
			return;
		}

		if (!command.getHostnameTemplate().contains(
				BulkCreateFromRangeCmd.range1PlaceHolder)) {
			errors.rejectValue("hostnameTemplate", "invalid-value",
					" The template must contain a place holder for the first range ("
							+ BulkCreateFromRangeCmd.range1PlaceHolder + ")");
		}

		if (StringUtils.hasText(command.getIpAddressTemplate())
				&& !command.getIpAddressTemplate().contains(
						BulkCreateFromRangeCmd.range1PlaceHolder)) {
			errors.rejectValue("ipAddressTemplate", "invalid-value",
					" The template must contain a place holder for the first range ("
							+ BulkCreateFromRangeCmd.range1PlaceHolder + ")");
		}

		if (command.getRange2().isSet()
				&& !command.getHostnameTemplate().contains(
						BulkCreateFromRangeCmd.range2PlaceHolder)) {
			errors.rejectValue("hostnameTemplate", "invalid-value",
					" The template must contain a place holder for the second range ("
							+ BulkCreateFromRangeCmd.range2PlaceHolder + ")");
		}

		if (command.getRange2().isSet()
				&& StringUtils.hasText(command.getIpAddressTemplate())
				&& !command.getIpAddressTemplate().contains(
						BulkCreateFromRangeCmd.range2PlaceHolder)) {
			errors.rejectValue("ipAddressTemplate", "invalid-value",
					" The template must contain a place holder for the second range ("
							+ BulkCreateFromRangeCmd.range2PlaceHolder + ")");

		}

		if (StringUtils.hasText(command.getHostnameTemplate())) {
			try {

				String hostname = command.formatHostName(1, 1);
				hostNameEditor.setAsText(hostname);
			} catch (Exception e) {
				errors.rejectValue("hostnameTemplate", "invalid-syntax",
						"Invalid host name template syntax");
			}
		}

		if (StringUtils.hasText(command.getIpAddressTemplate())) {
			try {
				String ipVal = command.formatIp(1, 1);
				TypeUtils.txtToIP(ipVal);
			} catch (Exception e) {
				errors.rejectValue("ipAddressTemplate", "invalid-syntax",
						"Invalid ip addreess template syntax");
			}
			if ((command.getRange1().getMin() != null && (command.getRange1()
					.getMin() < 0 || command.getRange1().getMin() > 255))
					&& (command.getRange1().getMax() != null && (command
							.getRange1().getMax() < 0 || command.getRange1()
							.getMax() > 255))) {
				errors
						.rejectValue("range1", "invalid-value",
								"range contains values which are incompatible with IP address template");

			}
			if ((command.getRange2().getMin() != null && (command.getRange2()
					.getMin() < 0 || command.getRange2().getMax() > 255))
					&& (command.getRange2().getMax() != null && (command
							.getRange2().getMax() < 0 || command.getRange2()
							.getMax() > 255))) {
				errors
						.rejectValue("range2", "invalid-value",
								"range contains values which are incompatible with IP address template");

			}

		}

		if (maxHosts > 0 && command.numHostsSpecified() > maxHosts) {
			errors.rejectValue("range1", "invalid-value",
					"You can only generate " + maxHosts
							+ " host records with this command");
		}
	}

	/**
	 * The maximum number of hosts which can be expanded
	 * 
	 * @param maxHosts
	 */
	public void setMaxHosts(int maxHosts) {
		this.maxHosts = maxHosts;
	}

	@Required
	public void setHostNameEditor(HostNameEditor hostNameEditor) {
		this.hostNameEditor = hostNameEditor;
	}
}
