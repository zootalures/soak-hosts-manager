package edu.bath.soak.net.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.hibernate.annotations.Proxy;

import edu.bath.soak.model.ChangeInfo;
import edu.bath.soak.model.OrgUnitAcl;
import edu.bath.soak.model.OrgUnitAclEntity;
import edu.bath.soak.web.BeanView;

/**
 * Name domains are a set of suffix that may be used to
 * 
 * @author cspocc
 * 
 */
@Entity()
//@Proxy(lazy = false)
@XmlType
@BeanView("beanview/host/NameDomain")

public class NameDomain implements Serializable, OrgUnitAclEntity {

	String suffix;

	private ChangeInfo changeInfo = new ChangeInfo();
	Set<HostClass> allowedClasses = new HashSet<HostClass>();
	private OrgUnitAcl orgUnitAcl = new OrgUnitAcl();

	@Id
	@XmlID
	public String getSuffix() {
		return suffix;
	}

	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}

	@XmlTransient
	public ChangeInfo getChangeInfo() {
		return changeInfo;
	}

	public void setChangeInfo(ChangeInfo changeInfo) {
		this.changeInfo = changeInfo;
	}

	@ManyToMany(targetEntity = HostClass.class, cascade = { CascadeType.PERSIST }, fetch = FetchType.EAGER)
	@XmlIDREF
	public Set<HostClass> getAllowedClasses() {
		return allowedClasses;
	}

	public void setAllowedClasses(Set<HostClass> allowedClasses) {
		this.allowedClasses = allowedClasses;

	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((suffix == null) ? 0 : suffix.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final NameDomain other = (NameDomain) obj;
		if (suffix == null) {
			if (other.suffix != null)
				return false;
		} else if (!suffix.equals(other.suffix))
			return false;
		return true;
	}

	public OrgUnitAcl getOrgUnitAcl() {
		return orgUnitAcl;
	}

	public void setOrgUnitAcl(OrgUnitAcl orgUnitAcl) {
		this.orgUnitAcl = orgUnitAcl;
	}

	public OrgUnitAclEntity aclParent() {
		return null;
	}
}
