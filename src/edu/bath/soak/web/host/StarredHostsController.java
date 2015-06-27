package edu.bath.soak.web.host;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import edu.bath.soak.mgr.StarredHostsManager;
import edu.bath.soak.net.model.Host;
import edu.bath.soak.net.model.NetDAO;
import edu.bath.soak.net.query.HostSearchQuery;
import edu.bath.soak.query.SearchResult;

public class StarredHostsController extends MultiActionController {
	StarredHostsManager starredHostsManager;
	NetDAO hostsDAO;
	Logger log = Logger.getLogger(this.getClass());
	HostSearchController hostSearchController;

	/**
	 * Defaulte starred hosts view, operates like a search
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView showStarred(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		HostSearchQuery search = new HostSearchQuery();
		search.setAscending(true);
		search.setOrderBy(request.getParameter("orderBy"));

		// binds the incoming search parameters
		search.setMaxResults(50);
		search.setFirstResult(0);
		hostSearchController.bindSearch(request, search);

		Map<String, Object> model = hostSearchController.referenceData(request);
		model.put("results", starredHostsManager.searchHosts(search));
		model.put("s", search);

		return new ModelAndView("host/starred", model);

	}

	public ModelAndView starredHostsFragment(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		response.setHeader("Pragma", "No-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setDateHeader("Expires", 1);
		return new ModelAndView("host/starredHostsFragment");

	}

	public ModelAndView starSearch(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		HostSearchQuery search = new HostSearchQuery();
		hostsDAO.prepareSearchObject(search);
		search.setAscending(true);
		Map requestParams = request.getParameterMap();
		// binds the incoming search parameters
		hostSearchController.bindSearch(request, search);
		search.setFirstResult(0);
		search.setMaxResults(-1);
		String value = request.getParameter("value");
		SearchResult<Long> hostResult = hostsDAO.searchHostsToIds(search, null);

		boolean starValue = Boolean.parseBoolean(value);
		starredHostsManager.setStarredById(hostResult.getResults(), starValue);
		String url = request.getHeader("referer");
		response.sendRedirect(url);
		return null;
	}

	public ModelAndView clearAllStarredAjax(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		starredHostsManager.clearStarredHosts();
		response.setHeader("Pragma", "No-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setDateHeader("Expires", 1);
		response.getOutputStream().println("OK");
		return null;
	}

	public ModelAndView setStarredAjax(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		log.debug("Set starred status of some hosts :");
		String value = request.getParameter("value");
		String[] hostIds = request.getParameterValues("hostId");
		if (null != hostIds && hostIds.length > 0) {
			boolean theValue = Boolean.parseBoolean(value);
			List<Long> hostIdsLong = new ArrayList<Long>();
			for (String hostId : hostIds) {
				hostIdsLong.add(Long.parseLong(hostId));
			}
			// List<Host> hosts = hostsDAO.findHostsByIdList(hostIdsLong);
			starredHostsManager.setStarredById(hostIdsLong, theValue);
			log.debug("Set starred status of " + hostIdsLong.size()
					+ " hosts to " + theValue);

		}
		response.setHeader("Pragma", "No-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setDateHeader("Expires", 1);
		response.getOutputStream().println("OK");
		return null;
	}

	@Required
	public void setStarredHostsManager(StarredHostsManager starredHostsManger) {
		this.starredHostsManager = starredHostsManger;
	}

	@Required
	public void setHostsDAO(NetDAO hostsDAO) {
		this.hostsDAO = hostsDAO;
	}

	@Required
	public void setHostSearchController(
			HostSearchController hostSearchController) {
		this.hostSearchController = hostSearchController;
	}
}
