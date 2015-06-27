/**
 * 
 */
package edu.bath.soak.web.admin.namedomain;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import edu.bath.soak.net.model.NetDAO;
import edu.bath.soak.net.query.HostSearchQuery;

public class DeleteNameDomainCommandValidator implements Validator {
	public boolean supports(Class clazz) {
		return DeleteNameDomainCommand.class.isAssignableFrom(clazz);
	}

	NetDAO hostsDAO;

	public void validate(Object target, Errors errors) {
		ValidationUtils.rejectIfEmpty(errors, "toDelete", "requiredField",
				"field is required");
		DeleteNameDomainCommand ndc = (DeleteNameDomainCommand) target;
		if (null != ndc) {
			HostSearchQuery hsc = new HostSearchQuery();
			hsc.setNameDomain(ndc.getToDelete());
			if (0 != hostsDAO.countResultsForHostSearchQuery(hsc, null)) {
				errors
						.rejectValue(
								"toDelete",
								"semantic-error",
								"You cannot delete this name domain because it still has host records. "
										+ "	You must first either rename or delete all hosts in this domain");
			}
		}
	}

	public void setHostsDAO(NetDAO hostsDAO) {
		this.hostsDAO = hostsDAO;
	}
}