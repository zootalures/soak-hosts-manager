package edu.bath.soak.net.model;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.ManyToOne;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.hibernate.annotations.Formula;

/**
 * 
 * @author cspocc
 * 
 */
@Embeddable
@XmlType
public class HostName implements Serializable, Comparable<HostName> {
	NameDomain domain;
	String name;

	// implicit field used mainly for hibernate

	public String toString() {
		return (name == null ? "" : name)
				+ (domain == null ? "." : domain.getSuffix());
	}

	@Column(nullable = false)
	public String getName() {
		return name;
	}

	@ManyToOne(targetEntity = NameDomain.class, optional = false, cascade = CascadeType.MERGE)
	@XmlIDREF
	public NameDomain getDomain() {
		return domain;
	}

	public void setDomain(NameDomain domain) {
		this.domain = domain;
	}

	public void setName(String name) {
		if (name != null)
			name = name.toLowerCase().trim();

		this.name = name;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((domain == null) ? 0 : domain.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		final HostName other = (HostName) obj;
		if (domain == null) {
			if (other.domain != null)
				return false;
		} else if (!domain.equals(other.domain))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	public int compareTo(HostName o) {
		return toString().compareTo(o.toString());
	}

	@XmlTransient
	@Formula(value = " CONCAT(name, domain_suffix)")
	public String getFQDN() {

		return (name == null ? "" : name)
				+ (domain != null ? domain.getSuffix() != null ? domain
						.getSuffix() : "" : "");
	}

	public void setFQDN(String val) {

	}
}
