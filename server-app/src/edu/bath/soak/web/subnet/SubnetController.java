package edu.bath.soak.web.subnet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import edu.bath.soak.imprt.cmd.XMLImportData;
import edu.bath.soak.net.model.Host;
import edu.bath.soak.net.model.HostClass;
import edu.bath.soak.net.model.NetDAO;
import edu.bath.soak.net.model.Subnet;
import edu.bath.soak.net.query.HostSearchQuery;

public class SubnetController extends MultiActionController {
	NetDAO hostsDAO;

	public ModelAndView xml(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		JAXBContext ctx = JAXBContext.newInstance("edu.bath.soak");
		XMLImportData data = new XMLImportData();
		response.setContentType("text/xml");
		// Don't bother with much buffering, just write sraight to client
		response.setBufferSize(300);
		data.setSubnets(hostsDAO.getSubnets());
		Marshaller m = ctx.createMarshaller();
		m.marshal(data, response.getOutputStream());
		return null;
	}

	/**
	 * Views a specific subnet
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView view(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		String sid = request.getParameter("id");
		long id = Long.parseLong(sid);

		Subnet s = hostsDAO.getSubnet(id);
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("subnet", s);
		model.put("orgUnits", hostsDAO.getOrgUnits());

		HostSearchQuery subnetQ = new HostSearchQuery();
		subnetQ.setSubnet(s);

		final Map<HostClass, Integer> hostClassUsage = hostsDAO
				.countHostsByHostClassForHostSearchQuery(subnetQ);

		model.put("numHosts", hostsDAO.countResultsForHostSearchQuery(subnetQ,
				null));
		model.put("hostClassUsage", hostClassUsage);
		List<HostClass> hostClasses = new ArrayList<HostClass>();
		hostClasses.addAll(hostClassUsage.keySet());
		Collections.sort(hostClasses, new Comparator<HostClass>() {
			public int compare(HostClass o1, HostClass o2) {
				return hostClassUsage.get(o2).compareTo(hostClassUsage.get(o1));
			}
		});
		model.put("usedHostClasses", hostClasses);
		return new ModelAndView("subnet/view", model);
	}

	/**
	 * Views a specific subnet
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView delete(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		String sid = request.getParameter("id");
		long id = Long.parseLong(sid);
		Subnet s = hostsDAO.getSubnet(id);
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("subnet", s);
		if (hostsDAO.countHostsOnSubnet(s) > 0) {
			return new ModelAndView("subnet/cantDelete", model);
		}
		if (null != request.getParameter("noReally")) {
			hostsDAO.deleteSubnet(s);
			return new ModelAndView(
					"redirect:/subnet/list.do?flash=subnet-deleted");

		} else {

			return new ModelAndView("subnet/delete", model);
		}
	}

	@Required
	public void setHostsDAO(NetDAO hostsDAO) {
		this.hostsDAO = hostsDAO;
	}
}
