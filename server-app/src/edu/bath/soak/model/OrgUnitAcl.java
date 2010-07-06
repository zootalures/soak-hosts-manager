package edu.bath.soak.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.xml.bind.annotation.XmlTransient;

import org.hibernate.annotations.CollectionOfElements;

import edu.bath.soak.web.BeanView;

@Embeddable
@BeanView("beanview/core/OrgUnitAcl")
public class OrgUnitAcl implements Serializable {

	Map<OrgUnit, Permission> aclEntries = new HashMap<OrgUnit, Permission>();

	public static enum Permission {
		ALLOWED, DENIED;
	}

	@CollectionOfElements()
	@XmlTransient
	public Map<OrgUnit, Permission> getAclEntries() {
		return aclEntries;
	}

	public void setAclEntries(Map<OrgUnit, Permission> aclEntries) {
		this.aclEntries = aclEntries;
	}
}
