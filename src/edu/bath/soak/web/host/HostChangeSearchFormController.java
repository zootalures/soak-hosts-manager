package edu.bath.soak.web.host;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;

import edu.bath.soak.net.model.Host;
import edu.bath.soak.net.model.HostChange;
import edu.bath.soak.net.model.NetDAO;
import edu.bath.soak.net.query.HostChangeQuery;
import edu.bath.soak.query.SearchResult;
import edu.bath.soak.security.SecurityHelper;

/**
 * 
 * Web controller for searching changes to hosts.
 * 
 * @see NetDAO#searchHostChanges
 * @author cspocc
 * 
 */
public class HostChangeSearchFormController extends SimpleFormController {

	SecurityHelper securityHelper;

	NetDAO hostsDAO;

	public HostChangeSearchFormController() {
		setValidator(new Validator() {
			public boolean supports(Class clazz) {
				return HostChangeQuery.class.isAssignableFrom(clazz);
			}

			public void validate(Object target, Errors errors) {
				Assert.isInstanceOf(HostChangeQuery.class, target);

				HostChangeQuery hsq = (HostChangeQuery) target;
				if (hsq.getFromDate() != null && hsq.getToDate() != null) {
					if ((!hsq.getFromDate().equals(hsq.getToDate()))
							&& !hsq.getFromDate().before(hsq.getToDate())) {
						errors.rejectValue("toDate", "invalid-input",
								"Date must be after the from date");
					}
				}

			}
		});
		setFormView("changes/search");
		setCommandName("s");
	}

	@Override
	protected Object formBackingObject(HttpServletRequest request)
			throws Exception {
		HostChangeQuery hsq = new HostChangeQuery();
		hsq.setMaxResults(20);
		hsq.setOrderBy("changeDate");
		hsq.setAscending(false);

		return hsq;
	}

	@Override
	protected Map referenceData(HttpServletRequest request) throws Exception {
		HashMap<Object, Object> model = new HashMap<Object, Object>();
		model.put("orgUnits", securityHelper.isAdmin() ? hostsDAO.getOrgUnits()
				: securityHelper.getAllowedOrgUnitsForCurrentUser());
		return model;
	}

	@Override
	protected ModelAndView onSubmit(HttpServletRequest request,
			HttpServletResponse response, Object command, BindException errors)
			throws Exception {
		Assert.notNull(command);
		Assert.isInstanceOf(HostChangeQuery.class, command);

		HostChangeQuery query = (HostChangeQuery) command;

		Map<String, Object> model = new HashMap<String, Object>();
		model.put(getCommandName(), command);
		model.putAll(referenceData(request));
		SearchResult<HostChange> result = hostsDAO.searchHostChanges(query,true);
		model.put("results", result);

		Map<HostChange, Host> versionBefore = new HashMap<HostChange, Host>();

		
		for (HostChange hc : result.getResults()) {
			versionBefore.put(hc, hc.getHost());

		}
		model.put("hostBefore", versionBefore);
		return new ModelAndView(getFormView(), model);

	}

	@Override
	protected boolean isFormSubmission(HttpServletRequest request) {
		// TODO Auto-generated method stub
		return true;
	}

	@Required
	public void setSecurityHelper(SecurityHelper securityHelper) {
		this.securityHelper = securityHelper;
	}

	@Required
	public void setHostsDAO(NetDAO hostsDAO) {
		this.hostsDAO = hostsDAO;
	}
}
