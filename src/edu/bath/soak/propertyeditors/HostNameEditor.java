package edu.bath.soak.propertyeditors;

import java.beans.PropertyEditorSupport;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import edu.bath.soak.net.model.HostName;
import edu.bath.soak.net.model.NetDAO;

public class HostNameEditor extends PropertyEditorSupport {

	NetDAO hostsDAO;
	String defaultSuffix;

	@Required
	public void setHostsDAO(NetDAO hostsDAO) {
		this.hostsDAO = hostsDAO;
	}

	public String getAsText() {
		Object value = getValue();
		return (value == null ? null : ((HostName) value).toString());
	}

	public void setAsText(String text) throws IllegalArgumentException {
		if (StringUtils.hasText(text)) {
			try {
				if (!text.endsWith(".")) {
					if (defaultSuffix != null) {
						text = text + defaultSuffix;
					} else {
						throw new IllegalArgumentException(
								"Host name is not absolute and no default suffix was specified");
					}
				}
				HostName hn = hostsDAO.getHostNameFromFQDN(text);
				setValue(hn);
			} catch (Exception e) {
				throw new IllegalArgumentException(e);
			}
		} else {
			setValue(null);
		}
	}

	public void setDefaultSuffix(String defaultSuffix) {
		this.defaultSuffix = defaultSuffix;
		Assert.isTrue(defaultSuffix.endsWith("."),
				"suffix does not end with a dot");
	}
}
