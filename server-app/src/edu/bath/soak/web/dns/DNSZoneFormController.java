package edu.bath.soak.web.dns;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.mvc.SimpleFormController;

import edu.bath.soak.dns.model.DNSDao;
import edu.bath.soak.dns.model.DNSZone;
import edu.bath.soak.dns.model.ForwardZone;
import edu.bath.soak.dns.model.ReverseZone;

/**
 * form which handles creation/update of Subnets
 * 
 * @author cspocc
 * 
 */
public class DNSZoneFormController extends SimpleFormController {

	DNSDao dnsDao;

	@Override
	protected void doSubmitAction(Object command) throws Exception {
		dnsDao.saveZone((DNSZone) command);
	}

	@Override
	protected Object formBackingObject(HttpServletRequest request)
			throws Exception {
		String id = request.getParameter("id");
		if (StringUtils.hasText(id)) {
			return dnsDao.getZone(Long.parseLong(id));
		} else {
			String type = request.getParameter("type");
			Assert.hasText(type);
			DNSZone zone;
			if (type.equals("reverse")) {
				zone = new ReverseZone();
			} else {
				zone = new ForwardZone();
			}
			zone.setUseTCP(true);
			zone.setServerPort(53);
			return zone;
		}

	}

	@Required
	public void setDnsDao(DNSDao dnsDao) {
		this.dnsDao = dnsDao;
	}

}
