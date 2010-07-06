package edu.bath.soak.web.undo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import edu.bath.soak.net.model.HostChange;
import edu.bath.soak.net.model.NetDAO;
import edu.bath.soak.net.model.StoredCommand;
import edu.bath.soak.net.query.HostChangeQuery;
import edu.bath.soak.security.SecurityHelper;

/**
 * 
 * Web tier controller for viewing user details and groups
 * 
 * @author cspocc
 * 
 */
public class StoredCommandController extends MultiActionController {
	SecurityHelper securityHelper;
	NetDAO hostsDAO;

	/**
	 * Shows a particular stored command
	 * 
	 * Throws a {@link SecurityException} if the currently active use does not
	 * have permission to view the given command
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView showCommand(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		String commandID = request.getParameter("id");

		Map<String, Object> model = new HashMap<String, Object>();
		StoredCommand cmd = hostsDAO.getStoredCommand(commandID);
//		if (!cmd.getUser()
//				.equals(securityHelper.getCurrentUser().getUsername())
//				&& !securityHelper.isAdmin())
//			throw new SecurityException(
//					" you do not have permission to view this command");

		model.put("command", cmd);
		model.put("baseCommand", hostsDAO.getBaseCommandForStoredCommand(cmd));
		HostChangeQuery hsq = new HostChangeQuery();
		hsq.setAscending(true);
		hsq.setOrderBy("hostName");
		hsq.setSearchTerm("cmdId:" + cmd.getId());
		List<HostChange> hostChanges = hostsDAO.searchHostChanges(hsq).getResults();
		
		for(HostChange hostChange:hostChanges){
			hostsDAO.fillHostChange(hostChange);
		}
		model.put("hostChanges",hostChanges);
		return new ModelAndView("undo/showCommand", model);
	}

	@Required
	public void setSecurityHelper(SecurityHelper securityHelper) {
		this.securityHelper = securityHelper;
	}

	@Required
	public void setHostsDAO(NetDAO hostsDAO) {
		this.hostsDAO = hostsDAO;
	}
}
