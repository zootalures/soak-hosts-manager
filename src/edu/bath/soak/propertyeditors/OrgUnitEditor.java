package edu.bath.soak.propertyeditors;

import java.beans.PropertyEditorSupport;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.StringUtils;

import edu.bath.soak.model.OrgUnit;
import edu.bath.soak.net.model.NetDAO;

public class OrgUnitEditor extends PropertyEditorSupport {

	NetDAO hostsDAO;

	@Required
	public void setHostsDAO(NetDAO hostsDAO) {
		this.hostsDAO = hostsDAO;
	}

	public String getAsText() {
		Object value = getValue();
		return (value == null ? null : ((OrgUnit)value).getId().toString());
	}

	public void setAsText(String text) throws IllegalArgumentException {
		if (StringUtils.hasText(text)) {
			try{
				OrgUnit nc = hostsDAO.getOrgUnitById(text);
				setValue(nc);
			}catch(Exception e){
				throw new IllegalArgumentException(e);
			}
		}else{
			setValue(null);
		}
	}
}
