package edu.bath.soak.web.vlan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.mvc.SimpleFormController;

import edu.bath.soak.net.model.NetDAO;
import edu.bath.soak.net.model.Subnet;
import edu.bath.soak.net.model.Vlan;

/**
 * 
 * Handles the deletion of a vlan, ensures that any subnets are put onto an
 * existing vlan.
 * 
 * @author cspocc
 * 
 */
public class DeleteVlanFormController extends SimpleFormController {

	NetDAO hostsDAO;

	public DeleteVlanFormController() {
		setCommandName("deleteVlanCommand");
		setValidator(new DeleteVlanCommandValidator());
	}

	@Override
	protected Object formBackingObject(HttpServletRequest request)
			throws Exception {

		String id = request.getParameter("id");
		Assert.notNull(id);
		DeleteVlanCommand dvc = new DeleteVlanCommand();
		dvc.setVlan(hostsDAO.getVlan(Long.parseLong(id)));
		return dvc;
	}

	@Override
	protected Map referenceData(HttpServletRequest request, Object command,
			Errors errors) throws Exception {
		DeleteVlanCommand vc = (DeleteVlanCommand) command;
		Assert.notNull(vc.getVlan());
		Map<String, Object> data = new HashMap<String, Object>();
		List<Vlan> vlans = new ArrayList<Vlan>();
		for (Vlan v : hostsDAO.getAllVlans()) {
			if (!v.equals(vc.getVlan())) {
				vlans.add(v);
			}
		}
		data.put("vlans", vlans);
		return data;
	}

	@Override
	protected void doSubmitAction(Object command) throws Exception {
		DeleteVlanCommand vc = (DeleteVlanCommand) command;
		Assert.notNull(vc.getVlan());

		Set<Subnet> subnets = vc.getVlan().getSubnets();
	
		for (Subnet s : subnets) {
			s.setVlan(vc.getMoveToVlan());
			hostsDAO.saveSubnet(s);
		}
		hostsDAO.deleteVlan(vc.getVlan());
	}

	@Required
	public void setHostsDAO(NetDAO hostsDAO) {
		this.hostsDAO = hostsDAO;
	}

}
