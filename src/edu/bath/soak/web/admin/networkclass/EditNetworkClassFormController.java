package edu.bath.soak.web.admin.networkclass;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.mvc.SimpleFormController;

import edu.bath.soak.net.model.HostClass;
import edu.bath.soak.net.model.NetDAO;
import edu.bath.soak.net.model.NetworkClass;

/**
 * form which handles creation/update of Host Class details
 * 
 * @author cspocc
 * 
 */
public class EditNetworkClassFormController extends SimpleFormController {

	NetDAO hostsDao;

	@Override
	protected void doSubmitAction(Object command) throws Exception {
		EditNetworkClassCommand cmd = (EditNetworkClassCommand) command;

		for (HostClass hc : hostsDao.getHostClasses()) {
			if (cmd.getHostClassPermissions().get(hc.getId())) {
				cmd.getNetworkClass().getAllowedHostClasses().add(hc);
			} else {
				cmd.getNetworkClass().getAllowedHostClasses().remove(hc);
			}
		}

		hostsDao.saveNetworkClass(((EditNetworkClassCommand) command)
				.getNetworkClass());
	}

	@Override
	protected Object formBackingObject(HttpServletRequest request)
			throws Exception {
		String id = request.getParameter("id");
		EditNetworkClassCommand cmd = new EditNetworkClassCommand();

		if (StringUtils.hasText(id)) {

			cmd.setNetworkClass(hostsDao.getNetworkClassById(id));
			cmd.setCreation(false);
		} else {
			cmd.setCreation(true);
			cmd.setNetworkClass(new NetworkClass());
		}

		for (HostClass hc : hostsDao.getHostClasses()) {
			cmd.getHostClassPermissions().put(hc.getId(),
					cmd.getNetworkClass().getAllowedHostClasses().contains(hc));
		}
		return cmd;
	}

	@Override
	protected Map referenceData(HttpServletRequest request) throws Exception {
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("hostClasses", hostsDao.getHostClasses());
		return data;
	}

	@Required
	public void setHostsDAO(NetDAO hostsDao) {
		this.hostsDao = hostsDao;
	}

}
