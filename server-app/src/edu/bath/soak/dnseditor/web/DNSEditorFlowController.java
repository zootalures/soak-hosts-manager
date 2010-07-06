package edu.bath.soak.dnseditor.web;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.webflow.action.FormAction;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;

import edu.bath.soak.cmd.CommandDispatcherRegistry;
import edu.bath.soak.dns.model.DNSDao;
import edu.bath.soak.dnseditor.cmd.ManualDNSCommand;

public class DNSEditorFlowController extends FormAction {
	public static final String COMMAND_ATTR = "command";
	DNSDao dnsDAO;
	CommandDispatcherRegistry commandDispatcherRegistry;

	public Event formBackingData(RequestContext context) throws Exception {

		context.getRequestScope().put("dnsZones", dnsDAO.getAllManagedZones());
		return success();
	}

	@Override
	protected Object createFormObject(RequestContext context) throws Exception {
		return new ManualDNSCommand();
	}

	public DNSDao getDnsDAO() {
		return dnsDAO;
	}

	@Required
	public void setDnsDAO(DNSDao dnsDao) {
		this.dnsDAO = dnsDao;
	}

	@Required
	public void setCommandDispatcherRegistry(
			CommandDispatcherRegistry commandDispatcherRegistry) {
		this.commandDispatcherRegistry = commandDispatcherRegistry;
	}

	public ManualDNSCommand getCurrentObject(RequestContext requestContext)
			throws Exception {
		return (ManualDNSCommand) getFormObject(requestContext);
	}
}
