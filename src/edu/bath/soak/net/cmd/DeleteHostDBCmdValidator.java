package edu.bath.soak.net.cmd;

import org.hibernate.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;
import org.springframework.validation.Errors;

import edu.bath.soak.net.model.NetDAO;

/**
 * Command validator for delete host commands
 * 
 * @author cspocc
 * 
 */
public class DeleteHostDBCmdValidator extends HookableValidator {

	NetDAO hostsDAO;

	public boolean supports(Class clazz) {
		return clazz.isAssignableFrom(DeleteHostDBCmd.class);
	}

	@Override
	public void validate(Object target, Errors errors) {
		Assert.notNull(target);
		Assert.isInstanceOf(DeleteHostDBCmd.class, target);

		DeleteHostDBCmd dc = (DeleteHostDBCmd) target;
		try {
			hostsDAO.loadHost(dc.getHost().getId());
		} catch (ObjectNotFoundException e) {
			errors.rejectValue("", "invalid-semantics", "Can't delete host "
					+ dc.getHost().getHostName() + " it does not exist");
		}
	}

	@Required
	public void setHostsDAO(NetDAO hostsDAO) {
		this.hostsDAO = hostsDAO;
	}

}
