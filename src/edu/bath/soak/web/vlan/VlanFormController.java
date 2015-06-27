package edu.bath.soak.web.vlan;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.servlet.mvc.SimpleFormController;

import edu.bath.soak.net.model.NetDAO;
import edu.bath.soak.net.model.Vlan;

/**
 * form which handles creation/update of Subnets
 * 
 * @author cspocc
 * 
 */
public class VlanFormController extends SimpleFormController {

	NetDAO hostsDAO;

	public VlanFormController() {
		setCommandName("vlan");
		setBindOnNewForm(true);
	}

	@Override
	protected Object formBackingObject(HttpServletRequest request)
			throws Exception {

		String id = request.getParameter("id");
		if (id != null) {
			return hostsDAO.getVlan(Long.parseLong(id));
		} else {
			return new Vlan();

		}
	} 

	@Override
	protected void doSubmitAction(Object command) throws Exception {
		hostsDAO.saveVlan((Vlan) command);
	}

	@Required
	public void setHostsDAO(NetDAO hostsDAO) {
		this.hostsDAO = hostsDAO;
	}

}
