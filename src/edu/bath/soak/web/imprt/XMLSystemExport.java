package edu.bath.soak.web.imprt;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import edu.bath.soak.imprt.cmd.XMLImportData;
import edu.bath.soak.net.model.Host;
import edu.bath.soak.net.model.NetDAO;
import edu.bath.soak.net.query.HostSearchQuery;
import edu.bath.soak.query.SearchResult;

public class XMLSystemExport extends MultiActionController {
	NetDAO hostsDAO;
	
		
	public ModelAndView exportXML(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		XMLImportData command = new XMLImportData();
		HostSearchQuery hq = new HostSearchQuery();
		hq.setOrderBy("ipAddress");
		hq.setAscending(true);
		hq.setMaxResults(-1);
		hq.setFirstResult(0);
		SearchResult<Host>res = hostsDAO.searchHosts(hq);
		command.setHosts(res.getResults());
		command.setVlans(hostsDAO.getAllVlans());
		command.setNameDomains(hostsDAO.getNameDomains());
		command.setSubnets(hostsDAO.getSubnets());
		command.setHostClasses(hostsDAO.getHostClasses());
		command.setNetworkClasses(hostsDAO.getNetworkClasses());
		
		JAXBContext ctx = JAXBContext.newInstance("edu.bath.soak");
		Marshaller marshaller= ctx.createMarshaller();
		response.setContentType("text/xml");
		
		marshaller.marshal(command, response.getOutputStream());

		return null;
	}

	

	@Required
	public void setHostsDAO(NetDAO hostsDAO) {
		this.hostsDAO = hostsDAO;
	}

}
