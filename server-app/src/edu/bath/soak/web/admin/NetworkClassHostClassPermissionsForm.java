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
import edu.bath.soak.net.model.NetDAO;
import edu.bath.soak.net.model.NetworkClass;

/**
 * Form controller for editin network class /host class permissions as a matrix 
 * @author cspocc
 *
 */
public class NetworkClassHostClassPermissionsForm extends SimpleFormController {
	NetDAO hostsDAO;
	Logger log = Logger.getLogger(this.getClass());

	public static class NCFormData {
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

		NCFormData formData = new NCFormData();
		List<NetworkClass> networkClasses = hostsDAO.getNetworkClasses();
		List<HostClass> hostClasses = hostsDAO.getHostClasses();
		for (NetworkClass nc : networkClasses) {
			Map<String, Boolean> perms = new HashMap<String, Boolean>();
			for (HostClass hc : hostClasses) {
				perms.put(hc.getId(), nc.getAllowedHostClasses().contains(hc));
			}
			formData.permissions.put(nc.getId(), perms);
		}
		return formData;
	}

	@Override
	protected Map referenceData(HttpServletRequest request) throws Exception {
		HashMap<String, Object> model = new HashMap<String, Object>();
		model.put("networkClasses", hostsDAO.getNetworkClasses());
		model.put("hostClasses", hostsDAO.getHostClasses());
		return model;

	}
	
	

	@Override
	protected void doSubmitAction(Object command) throws Exception {
		NCFormData data = (NCFormData) command;
		List<NetworkClass> ncs = hostsDAO.getNetworkClasses();
		List<HostClass> hcs = hostsDAO.getHostClasses();
		for (NetworkClass nc : ncs) {
			Set<HostClass> nchc = new HashSet<HostClass>();
			for (HostClass hc : hcs) {
				Boolean val = data.permissions.get(nc.getId()).get(hc.getId());
				if (null != val && val) {
					log.trace("Setting permission on " + nc.getId() + ":" + hc.getId());
					nchc.add(hc);
				}
			}
			nc.setAllowedHostClasses(nchc);
			hostsDAO.saveNetworkClass(nc);
		}

	}

	@Required
	public void setHostsDAO(NetDAO hostsDAO) {
		this.hostsDAO = hostsDAO;
	}
}
