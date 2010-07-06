	package edu.bath.soak.net.bulk;

import java.net.Inet4Address;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;
import org.springframework.validation.ValidationUtils;

import edu.bath.soak.net.cmd.AlterHostCmd;
import edu.bath.soak.net.cmd.AlterHostCmdValidator;
import edu.bath.soak.net.cmd.BulkAlterHostCmd;
import edu.bath.soak.net.cmd.BulkCreateEditHostsCmd;
import edu.bath.soak.net.cmd.HookableValidator;
import edu.bath.soak.net.model.Host;
import edu.bath.soak.net.model.NetDAO;

/**
 * Command validator for create/edit host commands
 * 
 * @author cspocc
 * 
 */
public abstract class BulkAlterHostCmdValidator<CmdType extends BulkAlterHostCmd>
		extends HookableValidator {
	NetDAO hostsDAO;
	AlterHostCmdValidator alterHostCmdValidator;
	Logger log = Logger.getLogger(BulkAlterHostCmdValidator.class);

	@Required
	public void setHostsDAO(NetDAO dao) {
		this.hostsDAO = dao;
	}

	public boolean supports(Class clazz) {
		return clazz.isAssignableFrom(BulkCreateEditHostsCmd.class);
	}

	public abstract AlterHostCmd expandIntoAlterHostCmd(CmdType command,
			Host host);

	@Override
	public void validate(Object target, Errors errors) {
		Assert.notNull(target);
		Assert.isInstanceOf(BulkAlterHostCmd.class, target);
		CmdType command = (CmdType) target;
		for (int i = 0; i < command.getHosts().size(); i++) {

			try {
				errors.pushNestedPath("hosts[" + i + "]");
				Host host = command.getHosts().get(i);

				AlterHostCmd hostCommand = expandIntoAlterHostCmd(command, host);
				Errors objectErrors = new BeanPropertyBindingResult(
						hostCommand, "cmd");
				ValidationUtils.invokeValidator(alterHostCmdValidator,
						hostCommand, objectErrors);

				for (ObjectError objError : (List<ObjectError>) objectErrors
						.getAllErrors()) {

					errors.rejectValue("", objError.getCode(), host.getHostName().getName() + " : " + objError
							.getDefaultMessage());

				}
				// Check that we've correctly transferred errors to this host
				Assert.isTrue(!objectErrors.hasErrors() || errors.hasErrors());
			} finally {
				errors.popNestedPath();
			}

		}

		for (int i = 0; i < command.getHosts().size(); i++) {
			Host h1 = command.getHosts().get(i);
			Inet4Address hostIp = command.selectedAddressForHost(h1);
//			if (hostIp == null) {
//				errors.rejectValue("hosts[" + i + "].ipAddress",
//						"field-required",
//						"You must specify an IP for this host");
//			}

			for (Host h2 : command.getHosts()) {
				if (null != h1.getIpAddress() && null != h2.getIpAddress()
						&& h1 != h2) {
					try {
						errors.pushNestedPath("hosts[" + i + "]");

						if (command.selectedAddressForHost(h2).equals(hostIp)) {
							errors.rejectValue("", "invalid-valiue",
									 h1.getHostName().getName() + " : " +"Selected IP address clashes with host "
											+ h2.getHostName().toString());

						}
						if (h1.getHostName().equals(h2.getHostName())) {
							errors.rejectValue("", "invalid-value",
									 h1.getHostName().getName() + " : " +"Host name clashes with host with IP "
											+ h2.getIpAddress()
													.getHostAddress());
						}
						if (null != h1.getMacAddress()
								&& null != h2.getMacAddress()
								&& h1.getMacAddress()
										.equals(h2.getMacAddress())) {
							errors.rejectValue("", "invalid-value",
									 h1.getHostName().getName() + " : " +"MAC address clashes with host "
											+ h2.getHostName().toString());

						}
					} finally {
						errors.popNestedPath();
					}

				}
			}
		}

		if (errors.hasErrors()) {
			log
					.debug("Validation of bulk alter host command failed with errors"
							+ errors);
		}
		super.validate(target, errors);
	}

	public void setAlterHostCmdValidator(
			AlterHostCmdValidator alterHostCmdValidator) {
		this.alterHostCmdValidator = alterHostCmdValidator;
	}

}
