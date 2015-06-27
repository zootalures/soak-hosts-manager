package edu.bath.soak.web.undo;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;
import org.springframework.webflow.action.MultiAction;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;

import edu.bath.soak.cmd.BaseCompositeCommand;
import edu.bath.soak.net.model.NetDAO;
import edu.bath.soak.net.model.StoredCommand;
import edu.bath.soak.undo.cmd.UndoCmd;
import edu.bath.soak.web.bulk.BulkHostsFlowController;

public class UndoCommandController extends MultiAction {
	NetDAO hostsDAO;
	BulkHostsFlowController bulkHostsFlowController;

	public Event setupUndoCommand(RequestContext context) {
		UndoCmd command = new UndoCmd();
		String commandId = context.getFlowScope().getString("commandId");
		Assert.notNull(commandId,
				"Command ID must be specified for this command");
		StoredCommand sc = hostsDAO.getStoredCommand(commandId);
		BaseCompositeCommand bcc = hostsDAO.getBaseCommandForStoredCommand(sc);

		context.getFlowScope().put("baseCommand", bcc);

		command.setStoredCommand(sc);
		context.getFlowScope().put(
				bulkHostsFlowController.getCommandAttribute(), command);
		return success();
	}

	@Required
	public void setHostsDAO(NetDAO hostsDAO) {
		this.hostsDAO = hostsDAO;
	}

	@Required
	public void setBulkHostsFlowController(
			BulkHostsFlowController bulkHostsFlowController) {
		this.bulkHostsFlowController = bulkHostsFlowController;
	}
}
