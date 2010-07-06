package edu.bath.soak.web.host;

import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;

import au.com.bytecode.opencsv.CSVWriter;

import edu.bath.soak.imprt.cmd.XMLImportData;
import edu.bath.soak.mgr.StarredHostsManager;
import edu.bath.soak.net.model.Host;
import edu.bath.soak.net.model.NetDAO;
import edu.bath.soak.net.query.HostSearchQuery;
import edu.bath.soak.query.SearchResult;

public class HostSearchController extends SimpleFormController {

	Logger log = Logger.getLogger(HostSearchController.class);
	StarredHostsManager starredHostsManager;
	NetDAO hostsDAO;

	@Required
	public void setHostsDAO(NetDAO hostDAO) {
		this.hostsDAO = hostDAO;
	}

	@Override
	protected Object formBackingObject(HttpServletRequest request)
			throws Exception {
		HostSearchQuery cmd = new HostSearchQuery();
		cmd.setMaxResults(50);
		hostsDAO.prepareSearchObject(cmd);
		return cmd;
	}

	@Override
	protected ModelAndView onSubmit(HttpServletRequest request,
			HttpServletResponse response, Object command, BindException errors)
			throws Exception {

		Assert.isInstanceOf(HostSearchQuery.class, command);
		HostSearchQuery cmd = (HostSearchQuery) command;
		String displayType = request.getParameter("display");
		if (null != displayType && displayType.equals("xml")) {
			JAXBContext ctx = JAXBContext.newInstance("edu.bath.soak");
			XMLImportData data = new XMLImportData();
			cmd.setFirstResult(0);
			cmd.setMaxResults(-1);
			SearchResult<Host> result = hostsDAO.searchHosts(cmd);
			response.setContentType("text/xml");
			// Don't bother with much buffering, just write sraight to client
			response.setBufferSize(300);
			data.setHosts(result.getResults());
			Marshaller m = ctx.createMarshaller();
			m.marshal(data, response.getOutputStream());
			return null;
		} else if (null != displayType && displayType.equals("csv")) {
			cmd.setFirstResult(0);
			cmd.setMaxResults(400);
			response.setContentType("text/csv");
			response.setHeader("Content-Disposition",
					"attachment; filename=searchresults.csv");
			CSVWriter csvw = new CSVWriter(new OutputStreamWriter(response
					.getOutputStream()), ',', '"', "\r\n");

			response.setBufferSize(300);

			csvw.writeNext(new String[] { "Hostname", "MAC", "IP", "Type",
					"OU", "Building","Room", "Description","LIU" });

			while (true) {
				SearchResult<Host> result = hostsDAO.searchHosts(cmd);
				for (Host h : result.getResults()) {
					csvw
							.writeNext(new String[] {
									h.getHostName().getFQDN(),
									h.getMacAddress() != null ? h
											.getMacAddress().toString() : "",
									h.getIpAddress().getHostAddress(),
									h.getHostClass().getId(),
									h.getOwnership().getOrgUnit().getId(),
									h.getLocation().getBuilding()!=null?h.getLocation().getBuilding():"",
									h.getLocation().getRoom()!=null?h.getLocation().getRoom():"",
									h.getDescription(),
									h.getLastUsageInfo() != null ? new SimpleDateFormat(
									"yyyy-MM-dd HH:mm:ss").format(h
									.getLastUsageInfo().getChangedAt())
									: ""});
				}
				csvw.flush();
				
				if(result.getLastResultOffset() == result.getTotalResults()){
					break;
				}
				cmd.setFirstResult(cmd.getFirstResult()+400);
			}
			return null;
		} else {

			Map model = errors.getModel();
			model.putAll(referenceData(request));
			SearchResult<Host> result = hostsDAO.searchHosts(cmd);
			model.put("results", result);
			model.put("hostSubnets", HostsHelper.getSubnetMap(result
					.getResults(), hostsDAO.getSubnets()));
			model.put("starred", HostsHelper.getStarredHostsFromSearchResults(
					starredHostsManager, result));
			return new ModelAndView(getSuccessView(), model);
		}
	}

	@Override
	protected boolean isFormSubmission(HttpServletRequest request) {

		if (request.getParameterMap().size() > (request.getParameter("flash") != null ? 1
				: 0)) {
			return true;
		} else
			return false;
	}

	@Override
	protected Map referenceData(HttpServletRequest request) throws Exception {

		HashMap<String, Object> model = new HashMap<String, Object>();
		model.put("subnets", hostsDAO.getSubnets());
		model.put("hostClasses", hostsDAO.getHostClasses());
		model.put("nameDomains", hostsDAO.getNameDomains());

		model.put("orgUnits", hostsDAO.getOrgUnits());

		return model;

	}

	@Required
	public void setStarredHostsManager(StarredHostsManager starredHostsManager) {
		this.starredHostsManager = starredHostsManager;
	}

	/**
	 * Externally accessible method which allows other controllers which accept
	 * search-like arguments to bind using this controllers binder
	 * 
	 * @param request
	 * @param command
	 * @throws Exception
	 */
	public void bindSearch(HttpServletRequest request, HostSearchQuery command)
			throws Exception {
		bindAndValidate(request, command);
	}
}
