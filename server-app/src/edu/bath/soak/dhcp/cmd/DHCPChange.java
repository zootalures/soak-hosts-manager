package edu.bath.soak.dhcp.cmd;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;

import edu.bath.soak.dhcp.model.DHCPReservation;

@XmlRootElement
public class DHCPChange implements Serializable {
	DHCPReservation reservation;
	boolean addition;

	@XmlElementRef
	public DHCPReservation getReservation() {
		return reservation;
	}

	public void setReservation(DHCPReservation reservation) {
		this.reservation = reservation;
	}

	public boolean isAddition() {
		return addition;
	}

	public void setAddition(boolean addition) {
		this.addition = addition;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (addition ? 1231 : 1237);
		result = prime * result
				+ ((reservation == null) ? 0 : reservation.hashCode());
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
		final DHCPChange other = (DHCPChange) obj;
		if (addition != other.addition)
			return false;
		if (reservation == null) {
			if (other.reservation != null)
				return false;
		} else if (!reservation.equals(other.reservation))
			return false;
		return true;
	}
}
