package edu.bath.soak.web.admin.ou;

import java.security.Permissions;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;

import edu.bath.soak.model.OrgUnit;
import edu.bath.soak.model.OrgUnitAclEntity;
import edu.bath.soak.model.OrgUnitAcl.Permission;
import edu.bath.soak.net.model.HostClass;
import edu.bath.soak.net.model.NameDomain;
import edu.bath.soak.net.model.NetDAO;
import edu.bath.soak.net.model.NetworkClass;
import edu.bath.soak.net.model.Subnet;
import edu.bath.soak.security.SecurityHelper;

public class EditOrgUnitAclEntityController extends SimpleFormController {
	NetDAO hostsDAO;
	SecurityHelper securityHelper;

	public static class EditOrgUnitAclEntityCommand {
		String type;
		String id;
		OrgUnitAclEntity entity;
		Map<String, Permission> acl = new HashMap<String, Permission>();
		String returnURL;

		NetDAO hostsDAO;

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public OrgUnitAclEntity getEntity() {
			return entity;
		}

		public void setEntity(OrgUnitAclEntity entity) {
			this.entity = entity;
		}

		public Map<String, Permission> getAcl() {
			return acl;
		}

		public void setAcl(Map<String, Permission> acl) {
			this.acl = acl;
		}

		public String getReturnURL() {
			return returnURL;
		}

		public void setReturnURL(String returnUrl) {
			this.returnURL = returnUrl;
		}

	}

	public EditOrgUnitAclEntityController() {
		setCommandName("editOrgUnitAclEntityCommand");
	}

	public OrgUnitAclEntity loadEntity(String type, String id) {
		OrgUnitAclEntity obj = null;
		if (type.equals("subnet")) {
			obj = hostsDAO.getSubnet(Long.parseLong(id));
		} else if (type.equals("nameDomain")) {
			obj = hostsDAO.getNameDomainBySuffix(id);
		} else if (type.equals("hostClass")) {
			obj = hostsDAO.getHostClassById(id);

		} else if (type.equals("networkClass")) {
			obj = hostsDAO.getNetworkClassById(id);
		}
		if (obj == null)
			throw new IllegalArgumentException("unknown  or unfound entity : "
					+ type + " : " + id);
		return obj;
	}

	public void saveEntity(OrgUnitAclEntity entity) {
		if (entity instanceof Subnet) {
			hostsDAO.saveSubnet((Subnet) entity);
		} else if (entity instanceof NetworkClass) {
			hostsDAO.saveNetworkClass((NetworkClass) entity);
		} else if (entity instanceof NameDomain) {
			hostsDAO.saveNameDomain((NameDomain) entity);
		} else if (entity instanceof HostClass) {
			hostsDAO.saveHostClass((HostClass) entity);
		}
	}

	@Override
	protected Map referenceData(HttpServletRequest request, Object command,
			Errors errors) throws Exception {
		// TODO Auto-generated method stub
		HashMap<String, Object> model = new HashMap<String, Object>();
		List<OrgUnit> orgUnits = hostsDAO.getOrgUnits();
		model.put("orgUnits", orgUnits);
		Map<OrgUnit, Permission> defaultPerms = new HashMap<OrgUnit, Permission>();
		EditOrgUnitAclEntityCommand cmd = (EditOrgUnitAclEntityCommand) command;
		for (OrgUnit ou : orgUnits) {
			OrgUnitAclEntity parent = cmd.getEntity().aclParent();
			if (parent != null && securityHelper.canUse(parent, ou)) {
				defaultPerms.put(ou, Permission.ALLOWED);
			} else {
				defaultPerms.put(ou, Permission.DENIED);
			}
		}
		model.put("defaultPerms", defaultPerms);
		return model;
	}

	@Override
	protected Object formBackingObject(HttpServletRequest request)
			throws Exception {
		String type = request.getParameter("type");
		String id = request.getParameter("id");
		EditOrgUnitAclEntityCommand cmd = new EditOrgUnitAclEntityCommand();
		cmd.setEntity(loadEntity(type, id));
		Map<String, Permission> acl = new HashMap<String, Permission>();
		for (Entry<OrgUnit, Permission> entry : cmd.getEntity().getOrgUnitAcl()
				.getAclEntries().entrySet()) {
			acl.put(entry.getKey().getId(), entry.getValue());
		}
		cmd.setAcl(acl);
		cmd.setReturnURL(request.getParameter("returnURL"));
		cmd.setType(type);
		cmd.setId(id);
		return cmd;
	}

	@Override
	protected ModelAndView onSubmit(Object command) throws Exception {
		EditOrgUnitAclEntityCommand cmd = (EditOrgUnitAclEntityCommand) command;
		Map<OrgUnit, Permission> acls = new HashMap<OrgUnit, Permission>();
		List<OrgUnit> orgUnits = hostsDAO.getOrgUnits();

		for (OrgUnit orgUnit : orgUnits) {
			Permission perm = cmd.getAcl().get(orgUnit.getId());
			if (null != perm) {
				acls.put(orgUnit, perm);
			}
		}
		cmd.entity.getOrgUnitAcl().setAclEntries(acls);

		saveEntity(cmd.entity);

		if (null != cmd.getReturnURL()) {
			return new ModelAndView("redirect:" + cmd.getReturnURL());
		} else {
			return new ModelAndView(getSuccessView());
		}
	}

	@Required
	public void setHostsDAO(NetDAO hostsDAO) {
		this.hostsDAO = hostsDAO;
	}

	public SecurityHelper getSecurityHelper() {
		return securityHelper;
	}

	@Required
	public void setSecurityHelper(SecurityHelper securityHelper) {
		this.securityHelper = securityHelper;
	}
}
