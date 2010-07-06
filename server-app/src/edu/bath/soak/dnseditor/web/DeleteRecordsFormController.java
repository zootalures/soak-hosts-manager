package edu.bath.soak.dnseditor.web;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.webflow.action.FormAction;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;

import edu.bath.soak.dnseditor.cmd.DeleteRecords;
import edu.bath.soak.dnseditor.cmd.ManualDNSCommand;
import edu.bath.soak.security.SecurityHelper;

public class DeleteRecordsFormController extends FormAction implements
		Validator {
	DNSEditorFlowController dnsEditorFlowController;
	SecurityHelper securityHelper;

	@Override
	protected Object createFormObject(RequestContext context) throws Exception {
		// TODO Auto-generated method stub
		return new DeleteRecords();
	}

	@Override
	public Validator getValidator() {
		return this;
	}

	public boolean supports(Class clazz) {
		// TODO Auto-generated method stub
		return clazz.isAssignableFrom(DeleteRecords.class);
	}

	public void validate(Object target, Errors errors) {
		DeleteRecords r = (DeleteRecords) target;
		if (r.getRecords().isEmpty()) {
			errors.rejectValue("", "invalid-value",
					"You must specify at least one record to delete");
		}

	}

	public Event doDeleteRecords(RequestContext context) throws Exception {

		DeleteRecords r = (DeleteRecords) getFormObject(context);
		Assert.notNull(r);
		Assert.notEmpty(r.getRecords());

		ManualDNSCommand command = dnsEditorFlowController
				.getCurrentObject(context);
		Assert.notNull(command);
		command.addEdit(r);

		return success();
	}

	@Required
	public void setDnsEditorFlowController(
			DNSEditorFlowController dnsFlowController) {
		this.dnsEditorFlowController = dnsFlowController;
	}

	@Required
	public void setSecurityHelper(SecurityHelper securityHelper) {
		this.securityHelper = securityHelper;
	}
}
