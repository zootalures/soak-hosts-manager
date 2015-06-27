package edu.bath.soak.web.admin.hostclass;

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

import edu.bath.soak.net.model.Host;
import edu.bath.soak.net.model.HostClass;
import edu.bath.soak.net.model.NetDAO;
import edu.bath.soak.net.query.HostSearchQuery;
import edu.bath.soak.query.SearchResult;

/**
 * 
 * Simple form for handling the deletion of a host class and replacing all hosts
 * of that class with another class This is not currently implement  as an undoable command
 * 
 * @author cspocc
 * 
 */
public class DeleteHostClassFormController extends SimpleFormController {

	NetDAO hostsDao;

	public static class DeleteHostClassCommand {
		HostClass toDelete;
		HostClass replaceWith;
		
		public HostClass getReplaceWith() {
			return replaceWith;
		}

		public void setReplaceWith(HostClass replaceWith) {
			this.replaceWith = replaceWith;
		}

		public HostClass getToDelete() {
			return toDelete;
		}

		public void setToDelete(HostClass toDelete) {
			this.toDelete = toDelete;
		}
	}

	public class DeleteHostClassCommandValidator implements Validator {
		public boolean supports(Class clazz) {
			return DeleteHostClassCommand.class.isAssignableFrom(clazz);
		}

		public void validate(Object target, Errors errors) {
			ValidationUtils.rejectIfEmpty(errors, "replaceWith",
					"requiredField", "field is required");
			ValidationUtils.rejectIfEmpty(errors, "toDelete", "requiredField",
					"field is required");
			DeleteHostClassCommand dc = (DeleteHostClassCommand) target;
			if (dc.toDelete.equals(dc.replaceWith)) {
				errors.rejectValue("replaceWith", "invalid-input",
						"replacement host class must differ from existing one");
			}
		}
	}

	@Override
	protected void doSubmitAction(Object command) throws Exception {
		DeleteHostClassCommand dc = (DeleteHostClassCommand) command;

		HostSearchQuery cmd = new HostSearchQuery();
		cmd.setHostClass(dc.getToDelete());
		SearchResult<Host > results = hostsDao.searchHosts(cmd);
		
		
		for(Host h: results.getResults()){
			Host editable = hostsDao.getHostForEditing(h.getId());
			editable.setHostClass(dc.getReplaceWith());
			hostsDao.saveHost(editable,"deleted host class" + dc.getToDelete().getId());
		}
		hostsDao.deleteHostClass(dc.getToDelete());
		
		
	}

	public DeleteHostClassFormController() {
		setValidator(new DeleteHostClassCommandValidator());

	}

	@Override
	protected Map referenceData(HttpServletRequest request, Object command,
			Errors errors) throws Exception {
		DeleteHostClassCommand dc = (DeleteHostClassCommand) command;

		HashMap<String, Object> data = new HashMap<String,Object>();
		ArrayList<HostClass> filtered = new ArrayList<HostClass>();
		for(HostClass hc: hostsDao.getHostClasses()){
			if(!hc.equals(dc.getToDelete())){
				filtered.add(hc);
			}
		}
		data.put("hostClasses", filtered);
		return data;

	}

	@Override
	protected Object formBackingObject(HttpServletRequest request)
			throws Exception {
		String id = request.getParameter("id");
		DeleteHostClassCommand dc = new DeleteHostClassCommand();
		HostClass hc = hostsDao.getHostClassById(id);
		Assert.notNull(hc);
		dc.setToDelete(hc);
		return dc;
	}

	@Required
	public void setHostsDAO(NetDAO hostsDao) {
		this.hostsDao = hostsDao;
	}

}