package edu.bath.soak.web.vlan;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import edu.bath.soak.imprt.cmd.XMLImportData;
import edu.bath.soak.net.model.NetDAO;
import edu.bath.soak.net.model.Vlan;

public class VlanController extends MultiActionController {
	NetDAO hostsDAO;

	/**
	 * Displays a list of all subnets
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView list(HttpServletRequest request,
			HttpServletResponse response) {
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("vlans", hostsDAO.getAllVlans());
		return new ModelAndView("vlan/list", model);
	}
	public ModelAndView xml(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		JAXBContext ctx = JAXBContext.newInstance("edu.bath.soak");
		XMLImportData data = new XMLImportData();
		response.setContentType("text/xml");
		//Don't bother with much buffering, just write sraight to client 
		response.setBufferSize(300);
		data.setVlans(hostsDAO.getAllVlans());
		Marshaller m = ctx.createMarshaller();
		m.marshal(data, response.getOutputStream());
		return null;
	}
	/**
	 * Views a specific vlan
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView show(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		String sid = request.getParameter("id");
		long id = Long.parseLong(sid);

		Vlan s = hostsDAO.findVlan(id);
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("vlan", s);
		return new ModelAndView("vlan/show", model);
	}

	@Required
	public void setHostsDAO(NetDAO hostsDAO) {
		this.hostsDAO = hostsDAO;
	}
}
