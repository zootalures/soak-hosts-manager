package edu.bath.soak.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import com.sun.xml.txw2.annotation.XmlElement;

@Entity
@XmlElement
public class OrgUnitPrincipalMapping implements Serializable {
	Long id;
	public static enum PrincipalType {GROUP,USER};
	OrgUnit orgUnit;
	String principal;
	PrincipalType type = PrincipalType.USER;
	
	@ManyToOne
	public OrgUnit getOrgUnit() {
		return orgUnit;
	}

	public void setOrgUnit(OrgUnit ou) {
		this.orgUnit = ou;
	}

	public String getPrincipal() {
		return principal;
	}

	public void setPrincipal(String group) {
		this.principal = group;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public PrincipalType getType() {
		return type;
	}

	public void setType(PrincipalType type) {
		this.type = type;
	}

}
