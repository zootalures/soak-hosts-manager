package edu.bath.soak.dhcp.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;

import org.hibernate.annotations.Type;

import edu.bath.soak.util.MacAddress;
import edu.bath.soak.util.TypeUtils;

/**
 * Abstract base class for DHCP Reservations, 
 * @see StaticDHCPReservation
 * @author cspocc
 *
 */
@XmlRootElement
@MappedSuperclass
@XmlSeeAlso(StaticDHCPReservation.class)
public abstract class DHCPReservation implements Comparable<DHCPReservation>,
		Serializable {

	String comment;
	String hostName;
	Long id;
	MacAddress macAddress;
	DHCPScope scope;
	Date updated;

	public String getComment() {
		return comment;
	}

	public String getHostName() {
		return hostName;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long getId() {
		return id;
	}

	@Column(nullable = false)
	@Type(type = "mactype")
	public MacAddress getMacAddress() {
		return macAddress;
	}

	@XmlIDREF
	@ManyToOne(fetch = FetchType.EAGER, targetEntity = DHCPScope.class, optional = false)
	public DHCPScope getScope() {
		return scope;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setScope(DHCPScope range) {
		this.scope = range;
	}

	public void setMacAddress(MacAddress macAddress) {
		this.macAddress = macAddress;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((macAddress == null) ? 0 : macAddress.hashCode());
		result = prime * result + ((scope == null) ? 0 : scope.hashCode());
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
		final DHCPReservation other = (DHCPReservation) obj;
		if (macAddress == null) {
			if (other.macAddress != null)
				return false;
		} else if (!macAddress.equals(other.macAddress))
			return false;
		if (scope == null) {
			if (other.scope != null)
				return false;
		} else if (!scope.equals(other.scope))
			return false;
		return true;
	}

	public Date getUpdated() {
		return updated;
	}

	public void setUpdated(Date updated) {
		this.updated = updated;
	}

	public int compareTo(DHCPReservation o) {
		int rv = 0;

		if (0 != (rv = TypeUtils.ipCmp(getScope().getMinIP(), o.getScope()
				.getMinIP()))) {
			return rv;
		}
		if (0 != (getMacAddress().compareTo(o.getMacAddress()))) {
			return rv;
		}
		return 0;
	}
}
