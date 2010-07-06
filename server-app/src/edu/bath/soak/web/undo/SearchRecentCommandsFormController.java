package edu.bath.soak.web.undo;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;

import edu.bath.soak.net.model.NetDAO;
import edu.bath.soak.net.model.StoredCommand;
import edu.bath.soak.query.SearchResult;
import edu.bath.soak.security.SecurityHelper;
import edu.bath.soak.undo.cmd.SearchStoredCommandsCmd;

public class SearchRecentCommandsFormController extends SimpleFormController {

	NetDAO hostsDAO;
	SecurityHelper securityHelper;

	public SearchRecentCommandsFormController() {
		setCommandName("command");
	}

	@Override
	protected Object formBackingObject(HttpServletRequest request)
			throws Exception {
		SearchStoredCommandsCmd cmd = new SearchStoredCommandsCmd();
		cmd.setOrderBy("changeTime");
		cmd.setAscending(false);
		return cmd;

	}

	@Override
	protected boolean isFormSubmission(HttpServletRequest request) {
		return true;
	}

	protected ModelAndView onSubmit(HttpServletRequest request,
			HttpServletResponse response, Object command, BindException errors)
			throws Exception {
		SearchStoredCommandsCmd cmd = (SearchStoredCommandsCmd) command;
		cmd.setUserName(securityHelper.getCurrentUser().getUsername());
		cmd.setMaxResults(50);
		SearchResult<StoredCommand> result = hostsDAO.searchStoredCommands(cmd);

		Map<String, Object> model = new HashMap<String, Object>();
		model.put("results", result);
		return new ModelAndView("undo/recentCommands", model);
	}

	@Required
	public void setHostsDAO(NetDAO hostsDAO) {
		this.hostsDAO = hostsDAO;
	}

	@Required
	public void setSecurityHelper(SecurityHelper securutyHelper) {
		this.securityHelper = securutyHelper;
	}
}
