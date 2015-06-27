package edu.bath.soak.web.admin.ou;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.StringUtils;
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

public class EditOrgUnitAclsFormController extends SimpleFormController {
	NetDAO hostsDAO;

	public static class EditOrgUnitAclsCommand {
		String id;
		OrgUnit orgUnit;
		Map<Subnet, Permission> subnetAcls = new HashMap<Subnet, Permission>();
		Map<NetworkClass, Permission> networkClassAcls = new HashMap<NetworkClass, Permission>();
		Map<NameDomain, Permission> nameDomainAcls = new HashMap<NameDomain, Permission>();
		Map<HostClass, Permission> hostClassAcls = new HashMap<HostClass, Permission>();
		String returnURL;

		public String getReturnURL() {
			return returnURL;
		}

		public void setReturnURL(String returnUrl) {
			this.returnURL = returnUrl;
		}

		public OrgUnit getOrgUnit() {
			return orgUnit;
		}

		public void setOrgUnit(OrgUnit orgUnit) {
			this.orgUnit = orgUnit;
		}

		public Map<Subnet, Permission> getSubnetAcls() {
			return subnetAcls;
		}

		public void setSubnetAcls(Map<Subnet, Permission> subnetAcls) {
			this.subnetAcls = subnetAcls;
		}

		public Map<NetworkClass, Permission> getNetworkClassAcls() {
			return networkClassAcls;
		}

		public void setNetworkClassAcls(
				Map<NetworkClass, Permission> networkClassAcls) {
			this.networkClassAcls = networkClassAcls;
		}

		public Map<NameDomain, Permission> getNameDomainAcls() {
			return nameDomainAcls;
		}

		public void setNameDomainAcls(Map<NameDomain, Permission> nameDomainAcls) {
			this.nameDomainAcls = nameDomainAcls;
		}

		public Map<HostClass, Permission> getHostClassAcls() {
			return hostClassAcls;
		}

		public void setHostClassAcls(Map<HostClass, Permission> hostClassAcls) {
			this.hostClassAcls = hostClassAcls;
		}

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

	}

	public EditOrgUnitAclsFormController() {
		setCommandName("editOrgUnitAclsCommand");
	}

	@Override
	protected Map referenceData(HttpServletRequest request) throws Exception {
		HashMap<String, Object> model = new HashMap<String, Object>();
		model.put("subnets", hostsDAO.getSubnets());
		model.put("nameDomains", hostsDAO.getNameDomains());
		model.put("networkClasses", hostsDAO.getNetworkClasses());
		model.put("hostClasses", hostsDAO.getHostClasses());
		return model;
	}

	/**
	 * Copies all of the permissions with respect to this org unit into the
	 * specified hash
	 * 
	 * @param orgUnit
	 * @param objects
	 * @param perms
	 */
	protected <T extends OrgUnitAclEntity> Map<T, Permission> buildAcls(
			OrgUnit orgUnit, Collection<T> objects) {
		Map<T, Permission> perms = new HashMap<T, Permission>();
		for (T object : objects) {
			Permission thePerm = object.getOrgUnitAcl().getAclEntries().get(
					orgUnit);

			if (null != thePerm) {
				perms.put(object, thePerm);
			}
		}
		return perms;
	}

	/**
	 * Calculates the list of orgUnitAclEntires which have altered permissions
	 * for this entitiy with respect to the given hash
	 * 
	 * @param orgUnit
	 * @param perms
	 * @param entries
	 * @return a list of entities which must be saved (with their respective
	 *         ACLs updated)
	 */
	protected <T extends OrgUnitAclEntity> List<T> applyOrgUnitAclChanges(
			OrgUnit orgUnit, Map<T, Permission> perms, List<T> entriesIn) {
		List<T> entriesOut = new ArrayList<T>();

		for (T entryIn : entriesIn) {
			Permission existingPermission = entryIn.getOrgUnitAcl()
					.getAclEntries().get(orgUnit);
			Permission setPermission = perms.get(entryIn);
			if (existingPermission != setPermission) {
				if (setPermission == null) {
					entryIn.getOrgUnitAcl().getAclEntries().remove(orgUnit);
				} else {
					entryIn.getOrgUnitAcl().getAclEntries().put(orgUnit,
							setPermission);
				}
				entriesOut.add(entryIn);
			}
		}
		return entriesOut;
	}

	@Override
	protected Object formBackingObject(HttpServletRequest request)
			throws Exception {
		String id = request.getParameter("id");
		OrgUnit orgUnit = hostsDAO.getOrgUnitById(id);
		if (null == orgUnit)
			throw new IllegalArgumentException(
					"Unable to find org unit with id " + id);
		EditOrgUnitAclsCommand cmd = new EditOrgUnitAclsCommand();
		cmd.setOrgUnit(orgUnit);
		cmd.setHostClassAcls(buildAcls(orgUnit, hostsDAO.getHostClasses()));
		cmd.setSubnetAcls(buildAcls(orgUnit, hostsDAO.getSubnets()));
		cmd.setNameDomainAcls(buildAcls(orgUnit, hostsDAO.getNameDomains()));
		cmd
				.setNetworkClassAcls(buildAcls(orgUnit, hostsDAO
						.getNetworkClasses()));
		cmd.setId(id);
		cmd.setOrgUnit(orgUnit);
		cmd.setReturnURL(request.getParameter("returnURL"));
		return cmd;
	}

	@Override
	protected ModelAndView onSubmit(Object command) throws Exception {
		EditOrgUnitAclsCommand cmd = (EditOrgUnitAclsCommand) command;

		for (Subnet s : applyOrgUnitAclChanges(cmd.getOrgUnit(), cmd
				.getSubnetAcls(), hostsDAO.getSubnets())) {
			hostsDAO.saveSubnet(s);
		}

		for (NameDomain nd : applyOrgUnitAclChanges(cmd.getOrgUnit(), cmd
				.getNameDomainAcls(), hostsDAO.getNameDomains())) {
			hostsDAO.saveNameDomain(nd);

		}
		for (NetworkClass nc : applyOrgUnitAclChanges(cmd.getOrgUnit(), cmd
				.getNetworkClassAcls(), hostsDAO.getNetworkClasses())) {
			hostsDAO.saveNetworkClass(nc);

		}
		for (HostClass hc : applyOrgUnitAclChanges(cmd.getOrgUnit(), cmd
				.getHostClassAcls(), hostsDAO.getHostClasses())) {
			hostsDAO.saveHostClass(hc);

		}

		if (StringUtils.hasText(cmd.getReturnURL())) {
			return new ModelAndView("redirect:" + cmd.getReturnURL());
		} else {
			return new ModelAndView(getSuccessView());
		}
	}

	@Required
	public void setHostsDAO(NetDAO hostsDAO) {
		this.hostsDAO = hostsDAO;
	}
}
