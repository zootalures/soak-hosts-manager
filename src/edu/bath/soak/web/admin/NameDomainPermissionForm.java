package edu.bath.soak.web.admin;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.servlet.mvc.SimpleFormController;

import edu.bath.soak.net.model.HostClass;
import edu.bath.soak.net.model.NameDomain;
import edu.bath.soak.net.model.NetDAO;

public class NameDomainPermissionForm extends SimpleFormController {
	NetDAO hostsDAO;
	Logger log = Logger.getLogger(this.getClass());

	public static class NDFormData {
		Map<String, Map<String, Boolean>> permissions = new HashMap<String, Map<String, Boolean>>();

		public Map<String, Map<String, Boolean>> getPermissions() {
			return permissions;
		}

		public void setPermissions(Map<String, Map<String, Boolean>> permissions) {
			this.permissions = permissions;
		}
	}

	@Override
	protected Object formBackingObject(HttpServletRequest request)
			throws Exception {

		NDFormData formData = new NDFormData();
		List<NameDomain> nameDomains = hostsDAO.getNameDomains();
		List<HostClass> hostClasses = hostsDAO.getHostClasses();
		for (NameDomain nd : nameDomains) {
			Map<String, Boolean> perms = new HashMap<String, Boolean>();
			for (HostClass hc : hostClasses) {
				perms.put(hc.getId(), nd.getAllowedClasses().contains(hc));
			}
			formData.permissions.put(nd.getSuffix(), perms);
		}
		return formData;
	}

	@Override
	protected Map referenceData(HttpServletRequest request) throws Exception {
		HashMap<String, Object> model = new HashMap<String, Object>();
		model.put("nameDomains", hostsDAO.getNameDomains());
		model.put("hostClasses", hostsDAO.getHostClasses());
		return model;

	}
	
	

	@Override
	protected void doSubmitAction(Object command) throws Exception {
		NDFormData data = (NDFormData) command;
		List<NameDomain> nds = hostsDAO.getNameDomains();
		List<HostClass> hcs = hostsDAO.getHostClasses();
		for (NameDomain nd : nds) {
			Set<HostClass> ndhc = new HashSet<HostClass>();
			for (HostClass hc : hcs) {
				Boolean val = data.permissions.get(nd.getSuffix()).get(hc.getId());
				if (null != val && val) {
					log.trace("Setting permission on " + nd.getSuffix() + ":" + hc.getId());
					ndhc.add(hc);
				}
			}
			nd.setAllowedClasses(ndhc);
			hostsDAO.saveNameDomain(nd);
		}

	}

	@Required
	public void setHostsDAO(NetDAO hostsDAO) {
		this.hostsDAO = hostsDAO;
	}
}
