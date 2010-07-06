package edu.bath.soak.web.admin.ou;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.beans.propertyeditors.StringArrayPropertyEditor;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.mvc.SimpleFormController;

import edu.bath.soak.model.OrgUnit;
import edu.bath.soak.model.OrgUnitPrincipalMapping;
import edu.bath.soak.model.OrgUnitPrincipalMapping.PrincipalType;
import edu.bath.soak.net.model.NetDAO;
/**
 * Simple form controller for editing org units and group/user bindings 
 * @author cspocc
 *
 */
public class EditOrgUnitFormController extends SimpleFormController {

	public EditOrgUnitFormController() {
		setValidator(new EditOrgUnitCommandValidator());
	}

	public static class EditOrganisationalUnitCommand {
		OrgUnit orgUnit;
		String[] groups = new String[] {};
		String[] users = new String[] {};

		boolean creation;

		public OrgUnit getOrgUnit() {
			return orgUnit;
		}

		public void setOrgUnit(OrgUnit orgUnit) {
			this.orgUnit = orgUnit;
		}

		public String[] getGroups() {
			return groups;
		}

		public void setGroups(String[] groups) {
			this.groups = groups;
		}

		public boolean isCreation() {
			return creation;
		}

		public void setCreation(boolean creation) {
			this.creation = creation;
		}

		public String[] getUsers() {
			return users;
		}

		public void setUsers(String[] users) {
			this.users = users;
		}

	}

	public class EditOrgUnitCommandValidator implements Validator {

		public boolean supports(Class clazz) {
			return EditOrganisationalUnitCommand.class.isAssignableFrom(clazz);
		}

		public void validate(Object target, Errors errors) {
			Assert.isInstanceOf(EditOrganisationalUnitCommand.class, target);
			EditOrganisationalUnitCommand cmd = (EditOrganisationalUnitCommand) target;
			ValidationUtils.rejectIfEmpty(errors, "orgUnit.id",
					"required-field");
			ValidationUtils.rejectIfEmpty(errors, "orgUnit.name",
					"required-field");
			if (cmd.isCreation()) {
				if (null != hostsDAO.getOrgUnitById(cmd.getOrgUnit().getId())) {
					errors.rejectValue("orgUnit.id", "invalid-value",
							"Another OU with this ID already exists");
				}
			}
		}

	}

	NetDAO hostsDAO;

	@Override
	protected void doSubmitAction(Object command) throws Exception {

		EditOrganisationalUnitCommand cmd = (EditOrganisationalUnitCommand) command;
		hostsDAO.saveOrgUnit(cmd.getOrgUnit());

		List<OrgUnitPrincipalMapping> ogrs = hostsDAO
				.getOrgUnitPrincipalMappingsForOU(cmd.getOrgUnit());

		for (String group : cmd.getGroups()) {
			if (!StringUtils.hasText(group))
				continue;
			group = group.trim();
			OrgUnitPrincipalMapping found = null;
			for (OrgUnitPrincipalMapping ogr : ogrs) {
				if (ogr.getPrincipal().equals(group)
						&& ogr.getType().equals(PrincipalType.GROUP)) {
					found = ogr;
					break;
				}
			}
			if (null == found) {
				OrgUnitPrincipalMapping ogr = new OrgUnitPrincipalMapping();
				ogr.setOrgUnit(cmd.getOrgUnit());
				ogr.setType(PrincipalType.GROUP);
				ogr.setPrincipal(group);
				hostsDAO.saveOrganisationalUnitPrincipalMapping(ogr);
			} else {
				ogrs.remove(found);
			}
		}

		for (String user : cmd.getUsers()) {
			if (!StringUtils.hasText(user))
				continue;

			user = user.trim();
			OrgUnitPrincipalMapping found = null;
			for (OrgUnitPrincipalMapping ogr : ogrs) {
				if (ogr.getPrincipal().equals(user)
						&& ogr.getType().equals(PrincipalType.USER)) {
					found = ogr;
					break;
				}
			}
			if (null == found) {
				OrgUnitPrincipalMapping ogr = new OrgUnitPrincipalMapping();
				ogr.setType(PrincipalType.USER);
				ogr.setOrgUnit(cmd.getOrgUnit());
				ogr.setPrincipal(user);
				hostsDAO.saveOrganisationalUnitPrincipalMapping(ogr);
			} else {
				ogrs.remove(found);
			}
		}

		for (OrgUnitPrincipalMapping ogr : ogrs) {
			hostsDAO.deleteOrgUnitPrincipalMapping(ogr);
		}
	}

	@Override
	protected void initBinder(HttpServletRequest request,
			ServletRequestDataBinder binder) throws Exception {
		super.initBinder(request, binder);

		StringArrayPropertyEditor spe = new StringArrayPropertyEditor("\n",
				false);

		binder.registerCustomEditor(String[].class, spe);
	}

	@Override
	protected Object formBackingObject(HttpServletRequest request)
			throws Exception {
		String id = request.getParameter("id");
		EditOrganisationalUnitCommand cmd = new EditOrganisationalUnitCommand();

		if (StringUtils.hasText(id)) {
			cmd.setOrgUnit(hostsDAO.getOrgUnitById(id));
			cmd.setCreation(false);
			ArrayList<String> groups = new ArrayList<String>();
			ArrayList<String> users = new ArrayList<String>();

			for (OrgUnitPrincipalMapping ogr : hostsDAO
					.getOrgUnitPrincipalMappingsForOU(cmd.getOrgUnit())) {
				if (ogr.getType().equals(PrincipalType.GROUP))
					groups.add(ogr.getPrincipal());
				else if (ogr.getType().equals(PrincipalType.USER))
					users.add(ogr.getPrincipal());
			}
			
			cmd.setGroups(groups.toArray(new String[] {}));
			cmd.setUsers(users.toArray(new String[] {}));
		} else {
			cmd.setCreation(true);
			cmd.setOrgUnit(new OrgUnit());
		}
		return cmd;
	}

	@Required
	public void setHostsDAO(NetDAO hostsDao) {
		this.hostsDAO = hostsDao;
	}
}
