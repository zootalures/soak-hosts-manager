package edu.bath.soak.web.subnet;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;

import edu.bath.soak.net.model.Host;
import edu.bath.soak.net.model.NetDAO;
import edu.bath.soak.net.model.Subnet;
import edu.bath.soak.net.query.SubnetQuery;
import edu.bath.soak.query.SearchResult;

/**
 * form which handles creation/update of Subnets
 * 
 * @author cspocc
 * 
 */
public class SubnetListController extends SimpleFormController {

	NetDAO hostsDAO;

	public SubnetListController() {
		setFormView("subnet/list");
		setCommandName("s");

	}

	@Override
	protected boolean isFormSubmission(HttpServletRequest request) {
		return true;
	}

	@Override
	protected Object formBackingObject(HttpServletRequest request)
			throws Exception {

		SubnetQuery sq = new SubnetQuery();
		sq.setOrderBy("vlan.number");
		sq.setAscending(true);
		return sq;
	}

	@Override
	protected ModelAndView onSubmit(HttpServletRequest request,
			HttpServletResponse response, Object command, BindException errors)
			throws Exception {
		SubnetQuery sq = (SubnetQuery) command;
		Map<String, Object> model = new HashMap<String, Object>();
		Assert.notNull(sq);
		SearchResult<Subnet> subs = hostsDAO.searchSubnets(sq);
		Map<Subnet, Integer> usage = hostsDAO.countHostsOnSubnets(subs
				.getResults());
		Map<Subnet, Host> gateways = hostsDAO.getSubnetGateways(subs
				.getResults());
		model.put("search", subs);
		model.put("usage", usage);
		model.put("gateways", gateways);
		model.put("s", sq);

		return new ModelAndView("subnet/list", model);
	}

	@Required
	public void setHostsDAO(NetDAO hostsDao) {
		this.hostsDAO = hostsDao;
	}

}
