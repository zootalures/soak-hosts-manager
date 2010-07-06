package edu.bath.soak.web.host;

import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import edu.bath.soak.mgr.HostsManager;
import edu.bath.soak.net.model.Host;
import edu.bath.soak.net.model.HostChange;
import edu.bath.soak.net.model.NetDAO;
import edu.bath.soak.net.model.Subnet;
import edu.bath.soak.net.query.HostChangeQuery;
import edu.bath.soak.query.SearchResult;
import edu.bath.soak.security.SecurityHelper;
import edu.bath.soak.util.MacAddress;
import edu.bath.soak.util.OrderedComparator;
import edu.bath.soak.util.TypeUtils;

public class HostInfoController extends MultiActionController {
	NetDAO hostsDAO;
	HostsManager hostsManager;
	SortedSet<ShowHostInfoInterceptor> showHostInterceptors = new TreeSet<ShowHostInfoInterceptor>(
			new OrderedComparator());
	SecurityHelper securityHelper;

	/**
	 * Displays a specific host change
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView showChange(HttpServletRequest request,
			HttpServletResponse response) {

		Map<String, Object> model = new HashMap<String, Object>();
		long changeId = Long.parseLong(request.getParameter("id"));
		HostChange hc = hostsDAO.getHostChange(changeId);
		model.put("change", hc);
		HostChange previous = hostsDAO.getPreviousHostChange(hc);
		model.put("previous", previous);
		model.put("previousHost", hc.getHost());
		model.put("changeHost", hostsDAO.getHostAtVersion(hc.getHostId(), hc
				.getVersion() + 1));

		model.put("next", hostsDAO.getNextHostChange(hc));
		model.put("nextHost", hostsDAO.getHostAtVersion(hc.getHostId(), hc
				.getVersion() + 2));

		return new ModelAndView("host/showChange", model);
	}

	HostHistoryView getHostHistory(Host h) {
		HostChangeQuery hsq = new HostChangeQuery();
		hsq.setSearchTerm("id:" + h.getId());
		hsq.setOrderBy("changeDate");
		hsq.setAscending(false);
		hsq.setMaxResults(10);
		SearchResult<HostChange> result = hostsDAO.searchHostChanges(hsq);
		HostHistoryView hhv = new HostHistoryView(h, result);
		Map<HostChange, Host> versionBefore = new HashMap<HostChange, Host>();
		for (HostChange hc : result.getResults()) {
			versionBefore.put(hc, hostsDAO.getHostAtVersion(h.getId(), hc
					.getVersion()));
		}
		hhv.setHostBefore(versionBefore);
		return hhv;
	}

	/**
	 * Displays information about a given host
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView show(HttpServletRequest request,
			HttpServletResponse response) {

		Map<String, Object> model = new HashMap<String, Object>();
		Host h = null;
		if (null != request.getParameter("id")) {
			h = hostsDAO.loadHost(Long.parseLong(request.getParameter("id")));
		} else if (null != request.getParameter("name")) {
			h = hostsDAO.findHost(request.getParameter("name"));
		} else if (null != request.getParameter("ip")) {
			h = hostsDAO
					.findHost(TypeUtils.txtToIP(request.getParameter("ip")));
		} else if (null != request.getParameter("mac")) {
			h = hostsDAO.findHost(MacAddress.fromText(request
					.getParameter("mac")));
		}
		if (null == h) {
			throw new RuntimeException("No matching host found");
		}

		HostView hv = new HostView(h);
		// Show the basic host info
		hv.getTabByName(HostView.BASIC_INFO).addViewComponent(h);

		HostHistoryView hhv = getHostHistory(h);
		if (securityHelper.canEdit(h.getOwnership())) {
			hv.getTabByName(HostView.HOST_HISTORY).addViewComponent(hhv);

		}

		Subnet s = hostsDAO.findSubnetContainingIP(h.getIpAddress());
		if (null != s) {
			hv.getTabByName(HostView.NETWORK_INFO).addViewComponent(
					new HostSubnetView(h, s));
		}
		for (ShowHostInfoInterceptor interceptor : showHostInterceptors) {
			interceptor.elaborateView(hv, request);
		}
		model.put("hostView", hv);
		return new ModelAndView("host/show", model);
	}

	/**
	 * Registers a new host info interceptor, this will be invoked whenever a
	 * host is shown in its appropriate order
	 * 
	 * @param interceptor
	 */
	public void registerShowHostInterceptor(ShowHostInfoInterceptor interceptor) {
		showHostInterceptors.add(interceptor);
	}

	@Required
	public void setHostsDAO(NetDAO hostsDAO) {
		this.hostsDAO = hostsDAO;
	}

	@Required
	public void setHostsManager(HostsManager hostsManager) {
		this.hostsManager = hostsManager;
	}

	@Required
	public void setSecurityHelper(SecurityHelper securityHelper) {
		this.securityHelper = securityHelper;
	}

}
