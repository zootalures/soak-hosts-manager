package edu.bath.soak.model;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.xml.bind.annotation.XmlIDREF;

import com.sun.xml.txw2.annotation.XmlElement;

@Entity
@Embeddable
@XmlElement
public class OwnershipInfo implements Serializable{
	private OrgUnit orgUnit;

	@XmlIDREF
	@ManyToOne(targetEntity = OrgUnit.class, optional = true)
	public OrgUnit getOrgUnit() {
		return orgUnit;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((orgUnit == null) ? 0 : orgUnit.hashCode());
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
		final OwnershipInfo other = (OwnershipInfo) obj;
		if (orgUnit == null) {
			if (other.orgUnit != null)
				return false;
		} else if (!orgUnit.equals(other.orgUnit))
			return false;
		return true;
	}

	public void setOrgUnit(OrgUnit orgUnit) {
		this.orgUnit = orgUnit;
	}

}
