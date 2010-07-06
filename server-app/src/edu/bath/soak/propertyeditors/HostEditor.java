package edu.bath.soak.propertyeditors;

import java.beans.PropertyEditorSupport;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import edu.bath.soak.net.model.Host;
import edu.bath.soak.net.model.NetDAO;

public class HostEditor extends PropertyEditorSupport {

	NetDAO hostsDAO;

	@Required
	public void setHostsDAO(NetDAO hostsDAO) {
		this.hostsDAO = hostsDAO;
	}

	public String getAsText() {
		Object value = getValue();
		return (value == null ? null
				: (((Host) value).getId() != null ? (((Host) value).getId()
						.toString()) : null));
	}

	public void setAsText(String text) throws IllegalArgumentException {
		if (StringUtils.hasText(text)) {
			try {
				Long id = Long.parseLong(text);
				Assert.notNull(id);
				Host host = hostsDAO.loadHost(id);
				setValue(host);
			} catch (Exception e) {
				throw new IllegalArgumentException(e);
			}
		} else {
			setValue(null);
		}
	}
}
