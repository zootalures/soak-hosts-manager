package edu.bath.soak.dhcp.model;

import java.io.Serializable;
import java.net.Inet4Address;

import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.xml.bind.annotation.XmlRootElement;

import org.hibernate.annotations.Type;

import edu.bath.soak.util.TypeUtils;

/**
 * Entity representing a specific DHCP reservation in a given scope.
 * 
 * 
 * @author cspocc
 * 
 */
@Entity
@Embeddable
@Table(uniqueConstraints = @UniqueConstraint(columnNames = { "macAddress",
		"ipAddress", "scope_id" }))
@XmlRootElement
public class StaticDHCPReservation extends DHCPReservation implements
		Serializable, Comparable<DHCPReservation> {

	public void setIpAddress(Inet4Address ipAddress) {
		this.ipAddress = ipAddress;
	}

	Inet4Address ipAddress;

	@Type(type = "inet4type")
	public Inet4Address getIpAddress() {
		return ipAddress;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((ipAddress == null) ? 0 : ipAddress.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		final StaticDHCPReservation other = (StaticDHCPReservation) obj;
		if (ipAddress == null) {
			if (other.ipAddress != null)
				return false;
		} else if (!ipAddress.equals(other.ipAddress))
			return false;
		return true;
	}

	public String toString() {
		return "Reservation:" + macAddress + ":" + ipAddress + ":" + hostName;
	}

	@Override
	public int compareTo(DHCPReservation o) {
		if (o instanceof StaticDHCPReservation) {
			int rv;
			if (0 != (rv = TypeUtils.ipCmp(getIpAddress(),
					((StaticDHCPReservation) o).getIpAddress())))
				return rv;

		}
		return super.compareTo(o);
	}
}
