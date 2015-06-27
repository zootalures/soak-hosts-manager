package edu.bath.soak.web.dns;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import org.springframework.webflow.action.MultiAction;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;

import edu.bath.soak.dns.DNSHostsInterceptor;
import edu.bath.soak.dns.cmd.CleanUpUnusedDNSRecordsCmd;
import edu.bath.soak.dns.model.DNSDao;

/**
 * Web tier controller which expands clean up DNS commands into normal DNS
 * commands
 * 
 * @author cspocc
 * 
 */
public class CleanUpUnusedDNSRecordsController extends MultiAction implements
		Validator {
	DNSHostsInterceptor dnsHostsInterceptor;
	DNSDao dnsDAO;

	public Event setUpReferenceData(RequestContext context) {
		context.getFlowScope().put("dnsZones", dnsDAO.getAllManagedZones());
		return success();
	}

	public Event calculateRecordsToCleanUp(RequestContext context) {
		CleanUpUnusedDNSRecordsCmd command = (CleanUpUnusedDNSRecordsCmd) context
				.getFlowScope().get("command");
		Assert.notNull(command);
		context.getFlowScope().put(
				"unusedRecords",
				dnsHostsInterceptor.calculateUnusedDNSRecordsForZones(command
						.getZones()));

		return success();
	}

	public void validate(Object target, Errors errors) {
		// TODO Auto-generated method stub

	}

	public boolean supports(Class clazz) {
		// TODO Auto-generated method stub
		return CleanUpUnusedDNSRecordsCmd.class.isAssignableFrom(clazz);
	}

	public void validateZones(Object target, Errors errors) {
		ValidationUtils.rejectIfEmpty(errors, "zones", "invalid-value",
				"you must specify at least one zone");
	}

	public void validateRecords(Object target, Errors errors) {
		ValidationUtils.rejectIfEmpty(errors, "toDelete", "invalid-value",
				"you must specify at least one record to clean up");
	}

	@Required
	public void setDnsDAO(DNSDao dnsDAO) {
		this.dnsDAO = dnsDAO;
	}

	@Required
	public void setDnsHostsInterceptor(DNSHostsInterceptor dnsHostsInterceptor) {
		this.dnsHostsInterceptor = dnsHostsInterceptor;
	}

}
