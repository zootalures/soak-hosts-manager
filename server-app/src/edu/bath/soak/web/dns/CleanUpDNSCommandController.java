package edu.bath.soak.web.dns;

import org.springframework.webflow.action.AbstractAction;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;

public class CleanUpDNSCommandController extends AbstractAction{

	@Override
	protected Event doExecute(RequestContext context) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	
	public Event expandImportCritera(RequestContext context) throws Exception {

		
		return success();
	}
}
