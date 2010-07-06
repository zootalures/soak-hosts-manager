package edu.bath.soak.dhcp.model;

import java.util.Date;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(uniqueConstraints = {})
public class DHCPReservationChange implements java.io.Serializable{
	Long id;
	Date changeDate;
	String commandId;

	public static enum DHCPChangeType {
		ADD, DEL
	};

	DHCPChangeType changeType;

	StaticDHCPReservation reservation;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getChangeDate() {
		return changeDate;
	}

	public void setChangeDate(Date changeDate) {
		this.changeDate = changeDate;
	}

	public String getCommandId() {
		return commandId;
	}

	public void setCommandId(String commandId) {
		this.commandId = commandId;
	}

	public DHCPChangeType getChangeType() {
		return changeType;
	}

	public void setChangeType(DHCPChangeType changeType) {
		this.changeType = changeType;
	}

	@Embedded
	public StaticDHCPReservation getReservation() {
		return reservation;
	}

	public void setReservation(StaticDHCPReservation reservation) {
		this.reservation = reservation;
	}

}
