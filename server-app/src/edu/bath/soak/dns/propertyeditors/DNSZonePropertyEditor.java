package edu.bath.soak.dns.propertyeditors;

import java.beans.PropertyEditorSupport;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import edu.bath.soak.dns.model.DNSDao;
import edu.bath.soak.dns.model.DNSZone;

public class DNSZonePropertyEditor extends PropertyEditorSupport {
	DNSDao dnsDAO;

	public String getAsText() {
		Object value = getValue();
		return (value == null ? null : ((DNSZone) value).getId().toString());
	}

	public void setAsText(String text) throws IllegalArgumentException {
		if (StringUtils.hasText(text)) {
			try {
				Long id = Long.parseLong(text);
				Assert.notNull(id);
				DNSZone zone = dnsDAO.getZone(id);
				setValue(zone);
			} catch (Exception e) {
				throw new IllegalArgumentException(e);
			}
		} else {
			setValue(null);
		}
	}

	@Required
	public void setDnsDAO(DNSDao dnsDAO) {
		this.dnsDAO = dnsDAO;
	}

}
