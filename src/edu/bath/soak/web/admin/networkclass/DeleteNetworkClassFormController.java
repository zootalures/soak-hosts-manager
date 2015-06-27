package edu.bath.soak.web.admin.networkclass;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.mvc.SimpleFormController;

import edu.bath.soak.net.model.NetDAO;
import edu.bath.soak.net.model.NetworkClass;
import edu.bath.soak.net.model.Subnet;

/**
 * 
 * Simple form for handling the deletion of a host class and replacing all hosts
 * of that class with another class This is not currently implement as an
 * undoable command
 * 
 * @author cspocc
 * 
 */
public class DeleteNetworkClassFormController extends SimpleFormController {

	NetDAO hostsDao;

	@Override
	protected void doSubmitAction(Object command) throws Exception {
		DeleteNetworkClassCommand dc = (DeleteNetworkClassCommand) command;

		for (Subnet s : hostsDao.getSubnets()) {
			s.setNetworkClass(dc.getReplaceWith());
			hostsDao.saveSubnet(s);
		}
		hostsDao.deleteNetworkClass(dc.getToDelete());

	}

	public DeleteNetworkClassFormController() {

	}

	@Override
	protected Map referenceData(HttpServletRequest request, Object command,
			Errors errors) throws Exception {
		DeleteNetworkClassCommand dc = (DeleteNetworkClassCommand) command;

		HashMap<String, Object> data = new HashMap<String, Object>();
		ArrayList<NetworkClass> filtered = new ArrayList<NetworkClass>();
		for (NetworkClass hc : hostsDao.getNetworkClasses()) {
			if (!hc.equals(dc.getToDelete())) {
				filtered.add(hc);
			}
		}
		data.put("networkClasses", filtered);
		return data;

	}

	@Override
	protected Object formBackingObject(HttpServletRequest request)
			throws Exception {
		String id = request.getParameter("id");
		DeleteNetworkClassCommand dc = new DeleteNetworkClassCommand();
		NetworkClass nc = hostsDao.getNetworkClassById(id);
		Assert.notNull(nc);
		dc.setToDelete(nc);
		return dc;
	}

	@Required
	public void setHostsDAO(NetDAO hostsDao) {
		this.hostsDao = hostsDao;
	}

}