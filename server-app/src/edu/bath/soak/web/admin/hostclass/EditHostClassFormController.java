package edu.bath.soak.web.admin.hostclass;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.mvc.SimpleFormController;

import edu.bath.soak.net.model.HostClass;
import edu.bath.soak.net.model.NetDAO;

/**
 * form which handles creation/update of Host Class details
 * 
 * @author cspocc
 * 
 */
public class EditHostClassFormController extends SimpleFormController {

	NetDAO hostsDao;

	@Override
	protected void doSubmitAction(Object command) throws Exception {

		hostsDao.saveHostClass(((EditHostClassCommand) command).getHostClass());
	}

	@Override
	protected Map referenceData(HttpServletRequest request, Object command,
			Errors errors) throws Exception {
		HashMap<String,Object> referenceData = new HashMap<String, Object>();
		referenceData.put("dhcpStatuses",HostClass.DHCP_STATUS.values());
		return referenceData;
	}
	@Override
	protected Object formBackingObject(HttpServletRequest request)
			throws Exception {
		String id = request.getParameter("id");
		EditHostClassCommand cmd = new EditHostClassCommand();

		if (StringUtils.hasText(id)) {

			cmd.setHostClass(hostsDao.getHostClassById(id));
			cmd.setCreation(false);
		} else {
			cmd.setCreation(true);
			cmd.setHostClass(new HostClass());
		}
		return cmd;
	}

	@Required
	public void setHostsDAO(NetDAO hostsDao) {
		this.hostsDao = hostsDao;
	}

}
