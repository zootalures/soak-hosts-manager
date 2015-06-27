package edu.bath.soak.web.dns;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.servlet.mvc.SimpleFormController;

import edu.bath.soak.dns.model.DNSDao;
import edu.bath.soak.dns.model.DNSZone;

/**
 * form which handles creation/update of Subnets
 * 
 * @author cspocc
 * 
 */
public class DeleteDNSZoneFormController extends SimpleFormController {

	DNSDao dnsDao;

	@Override
	protected void doSubmitAction(Object command) throws Exception {
		dnsDao.deleteZone((DNSZone) command);
	}

	@Override
	protected Object formBackingObject(HttpServletRequest request)
			throws Exception {
		String id = request.getParameter("id");
		DNSZone z = dnsDao.getZone(Long.parseLong(id));
		if(z==null){
			throw new IllegalArgumentException("Zone " + id + " not found");
		}
		return z;
	}

	@Required
	public void setDnsDao(DNSDao dnsDao) {
		this.dnsDao = dnsDao;
	}

}
