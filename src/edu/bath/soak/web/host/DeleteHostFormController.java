package edu.bath.soak.web.host;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.webflow.action.FormAction;
import org.springframework.webflow.core.collection.MutableAttributeMap;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;

import edu.bath.soak.cmd.BaseCompositeCommand;
import edu.bath.soak.cmd.CommandDispatcherRegistry;
import edu.bath.soak.net.cmd.DeleteHostUICmd;
import edu.bath.soak.net.model.Host;
import edu.bath.soak.net.model.NetDAO;
import edu.bath.soak.security.SecurityHelper;

/**
 * Form controller for creating and editing hosts as part of a flow
 * 
 * @author cspocc
 * 
 */
public class DeleteHostFormController extends FormAction {
	NetDAO hostsDAO;
	Logger log = Logger.getLogger(DeleteHostFormController.class);
	SecurityHelper securityHelper;
	CommandDispatcherRegistry commandDispatcherRegistry;

	public DeleteHostFormController() {
		setFormObjectName("deleteHostCmd");
	}

	/**
	 * Initialises the backing form command
	 */
	@Override
	protected Object createFormObject(RequestContext context) throws Exception {
		log.trace("createFormObject called");

		DeleteHostUICmd cmd = new DeleteHostUICmd();

		Long hostId = null;
		if (null != (hostId = (Long) context.getFlowScope().get("hostId"))) {
			log.debug("Host edit detected, editing host " + hostId);
			Host h = hostsDAO.getHostForEditing(hostId);
			cmd.setHost(h);
		} else {
			throw new IllegalArgumentException(
					"Unable to find host with commandId " + hostId);
		}
		return cmd;
	}

	public Event checkPermissions(RequestContext context) throws Exception {
		Long hostId = null;
		if (null != (hostId = (Long) context.getFlowScope().get("hostId"))) {
			Host h = hostsDAO.loadHost(hostId);
			if (securityHelper.canEdit(h.getOwnership())) {
				return success();
			} else {
				return result("failure");
			}
		} else {
			throw new IllegalArgumentException("Host not found");
		}
	}

	/**
	 * Calculates the change set for this action and fills the changes into the
	 * command object
	 * 
	 * @param context
	 * @return
	 * @throws Exception
	 */
	public Event calculateChanges(RequestContext context) throws Exception {
		DeleteHostUICmd cmd = (DeleteHostUICmd) getFormObject(context);
		log.trace("computing changes for host");
		BaseCompositeCommand result = commandDispatcherRegistry
				.expandCommand(cmd);
		MutableAttributeMap requestScope = context.getRequestScope();
		requestScope.put("changeResult", result);
		log.trace("got changes" + result);
		return success();
	}

	/**
	 * Re-populates the changes and applies them
	 * 
	 * @param context
	 * @return
	 * @throws Exception
	 */
	public Event applyChanges(RequestContext context) throws Exception {
		DeleteHostUICmd cmd = (DeleteHostUICmd) getFormObject(context);
		log.trace("deleting host  " + cmd.getHost() + " with ID "
				+ cmd.getHost().getId());

		BaseCompositeCommand result = commandDispatcherRegistry
				.expandAndImplementCommand(cmd);
		MutableAttributeMap requestScope = context.getRequestScope();
		requestScope.put("changeResult", result);
		return success();
	}

	@Required
	public void setHostsDAO(NetDAO hostsDao) {
		this.hostsDAO = hostsDao;
	}

	public void setSecurityHelper(SecurityHelper securityHelper) {
		this.securityHelper = securityHelper;
	}

	@Required
	public void setCommandDispatcherRegistry(
			CommandDispatcherRegistry commandDispatcherRegistry) {
		this.commandDispatcherRegistry = commandDispatcherRegistry;
	}
}
