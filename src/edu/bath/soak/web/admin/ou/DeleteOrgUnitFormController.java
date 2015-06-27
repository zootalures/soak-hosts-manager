package edu.bath.soak.web.admin.ou;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import org.springframework.web.servlet.mvc.SimpleFormController;

import edu.bath.soak.model.OrgUnit;
import edu.bath.soak.net.model.Host;
import edu.bath.soak.net.model.NetDAO;
import edu.bath.soak.net.query.HostSearchQuery;
import edu.bath.soak.query.SearchResult;

/**
 * 
 * Simple form for handling the deletion of a host class and replacing all hosts
 * of that class with another class This is not currently implement as an
 * undoable command
 * 
 * @author cspocc
 * 
 */
public class DeleteOrgUnitFormController extends SimpleFormController {

	NetDAO hostsDao;

	public static class DeleteOrgUnitCommand {
		OrgUnit toDelete;
		OrgUnit replaceWith;

		public OrgUnit getToDelete() {
			return toDelete;
		}

		public void setToDelete(OrgUnit toDelete) {
			this.toDelete = toDelete;
		}

		public OrgUnit getReplaceWith() {
			return replaceWith;
		}

		public void setReplaceWith(OrgUnit replaceWith) {
			this.replaceWith = replaceWith;
		}

	}

	public class DeleteOrganisationalUnitCommandValidator implements Validator {
		public boolean supports(Class clazz) {
			return DeleteOrgUnitCommand.class.isAssignableFrom(clazz);
		}

		public void validate(Object target, Errors errors) {
			ValidationUtils.rejectIfEmpty(errors, "replaceWith",
					"requiredField", "field is required");
			ValidationUtils.rejectIfEmpty(errors, "toDelete", "requiredField",
					"field is required");
			DeleteOrgUnitCommand dc = (DeleteOrgUnitCommand) target;
			if (dc.toDelete.equals(dc.replaceWith)) {
				errors.rejectValue("replaceWith", "invalid-input",
						"replacement host class must differ from existing one");
			}
		}
	}

	@Override
	protected void doSubmitAction(Object command) throws Exception {
		DeleteOrgUnitCommand dc = (DeleteOrgUnitCommand) command;

		HostSearchQuery cmd = new HostSearchQuery();
		cmd.setOrgUnit(dc.getToDelete());
		SearchResult<Host> results = hostsDao.searchHosts(cmd);

		for (Host h : results.getResults()) {
			Host editable = hostsDao.getHostForEditing(h.getId());
			editable.getOwnership().setOrgUnit(dc.getReplaceWith());
			hostsDao.saveHost(editable, "deleted organisational unit "
					+ dc.getToDelete().getId());

		}
		
		

		hostsDao.deleteOrgUnit(dc.getToDelete());

	}

	public DeleteOrgUnitFormController() {
		setValidator(new DeleteOrganisationalUnitCommandValidator());

	}

	@Override
	protected Map referenceData(HttpServletRequest request, Object command,
			Errors errors) throws Exception {
		DeleteOrgUnitCommand dc = (DeleteOrgUnitCommand) command;

		HashMap<String, Object> data = new HashMap<String, Object>();
		ArrayList<OrgUnit> filtered = new ArrayList<OrgUnit>();
		for (OrgUnit hc : hostsDao.getOrgUnits()) {
			if (!hc.equals(dc.getToDelete())) {
				filtered.add(hc);
			}
		}
		data.put("orgUnits", filtered);
		return data;

	}

	@Override
	protected Object formBackingObject(HttpServletRequest request)
			throws Exception {
		String id = request.getParameter("id");
		DeleteOrgUnitCommand dc = new DeleteOrgUnitCommand();
		OrgUnit hc = hostsDao.getOrgUnitById(id);
		Assert.notNull(hc);
		dc.setToDelete(hc);
		return dc;
	}

	@Required
	public void setHostsDAO(NetDAO hostsDao) {
		this.hostsDao = hostsDao;
	}

}