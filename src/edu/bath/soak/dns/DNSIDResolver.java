package edu.bath.soak.dns;

import java.util.concurrent.Callable;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;
import org.xml.sax.SAXException;

import edu.bath.soak.dns.model.DNSDao;
import edu.bath.soak.dns.model.DNSZone;
import edu.bath.soak.xml.SoakXMLIDResolver;

public class DNSIDResolver extends SoakXMLIDResolver {
	DNSDao dnsDAO;
 
	public DNSIDResolver() {
		super(new Class[] { DNSZone.class });
	}

	@Override
	public void bind(String arg0, Object arg1) throws SAXException {
		// TODO Auto-generated method stub

	}

	@Override
	public Callable<?> resolve(final String id, Class clazz)
			throws SAXException {
		Assert.isTrue(supportsResolving(clazz));
		Assert.notNull(id);

		return new Callable<DNSZone>() {
			public DNSZone call() throws Exception {
				return dnsDAO.getZone(Long.parseLong(id));
			}
		};

	}

	@Required
	public void setDnsDAO(DNSDao dnsDAO) {
		this.dnsDAO = dnsDAO;
	}

}
