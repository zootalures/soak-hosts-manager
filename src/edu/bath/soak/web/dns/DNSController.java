package edu.bath.soak.web.dns;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import edu.bath.soak.dns.DNSUpdateMgr;
import edu.bath.soak.dns.model.DNSDao;
import edu.bath.soak.dns.model.DNSZone;

public class DNSController extends MultiActionController {

	DNSDao dnsDAO;
	DNSUpdateMgr dnsUpdateMgr;

	public ModelAndView list(HttpServletRequest request,
			HttpServletResponse response) {
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("fwZones", dnsDAO.getForwardZones());
		model.put("rvZones", dnsDAO.getReverseZones());

		return new ModelAndView("dns/zonelist", model);
	}

	public ModelAndView show(HttpServletRequest request,
			HttpServletResponse response) {
		long id = Long.valueOf(request.getParameter("id"));

		Map<String, Object> model = new HashMap<String, Object>();
		DNSZone zone = dnsDAO.getZone(id);
		model.put("zone", dnsDAO.getZone(id));
		model.put("records", dnsDAO.getAllRecordsForZone(zone));

		return new ModelAndView("dns/zoneshow", model);
	}

	public ModelAndView updateZones(HttpServletRequest request,
			HttpServletResponse response) {
		String incrementalP = request.getParameter("incremental");

		boolean fullUpdate = true;
		if (incrementalP != null && incrementalP.equals("true")) {
			fullUpdate = false;
		}
		Map<String, Object> model = new HashMap<String, Object>();

		List<DNSUpdateMgr.ZoneUpdateInfo> zupd = dnsUpdateMgr
				.updateAllDNSZones(fullUpdate);
		Assert.notNull(zupd);
		model.put("updates", zupd);
		return new ModelAndView("dns/zonesupdated", model);
	}

	@Required
	public void setDnsDAO(DNSDao dnsDAO) {
		this.dnsDAO = dnsDAO;
	}

	@Required
	public void setDnsUpdateMgr(DNSUpdateMgr dnsUpdateMgr) {
		this.dnsUpdateMgr = dnsUpdateMgr;
	}

}
