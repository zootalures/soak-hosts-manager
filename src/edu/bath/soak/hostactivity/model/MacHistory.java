package edu.bath.soak.hostactivity.model;

import java.net.Inet4Address;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import org.hibernate.annotations.Type;

import edu.bath.soak.util.MacAddress;

@Entity
public class MacHistory {
	Long id;
	MacAddress fromMac;
	MacAddress toMac;
	Inet4Address ipAddress;
	Date changedAt;

	@Id
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Type(type = "mactype")
	@Column(name = "from_mac")
	public MacAddress getFromMac() {
		return fromMac;
	}

	public void setFromMac(MacAddress fromMac) {
		this.fromMac = fromMac;
	}

	@Type(type = "mactype")
	@Column(name = "to_mac")
	public MacAddress getToMac() {
		return toMac;
	}

	public void setToMac(MacAddress toMac) {
		this.toMac = toMac;
	}

	@Column(name = "ipAddress")
	@Type(type = "inet4type")
	public Inet4Address getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(Inet4Address ipAddress) {
		this.ipAddress = ipAddress;
	}

	@Column(name = "changed_at")
	public Date getChangedAt() {
		return changedAt;
	}

	public void setChangedAt(Date changedAt) {
		this.changedAt = changedAt;
	}
}
