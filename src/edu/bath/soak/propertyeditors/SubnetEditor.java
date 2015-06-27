package edu.bath.soak.propertyeditors;

import java.beans.PropertyEditorSupport;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.StringUtils;

import edu.bath.soak.net.model.NetDAO;
import edu.bath.soak.net.model.Subnet;

public class SubnetEditor extends PropertyEditorSupport {

	NetDAO hostsDAO;

	@Required
	public void setHostsDAO(NetDAO hostsDAO) {
		this.hostsDAO = hostsDAO;
	}

	public String getAsText() {
		Object value = getValue();
		if (value == null) {
			return "";
		} else {
			Subnet s = (Subnet) value;
			return s.getId().toString();
		}
	}

	public void setAsText(String text) throws IllegalArgumentException {
		if (StringUtils.hasText(text)) {
			try {
				Long id = Long.parseLong(text);
				Subnet v = hostsDAO.getSubnet(id);

				setValue(v);
			} catch (Exception e) {
				throw new IllegalArgumentException(e);
			}
		} else {
			setValue(null);
		}

	}

}
