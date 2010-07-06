package edu.bath.soak.net.model;

import java.io.Serializable;
import java.net.Inet4Address;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

import edu.bath.soak.util.MacAddress;

/**
 * Host-seperated entity which is used primarly by the hostactivity plugin
 * rather than the core;
 * 
 * 
 * This records when an IP address was last listed in use, and which MAC it was
 * using.
 * 
 * Nothing in the core relies on this, however hibernate forces us to make it
 * part of the core package
 * 
 * @author cspocc
 * 
 */
@Entity
@Table(name = "MacIp")
public class LastUsageInfo implements Serializable {
	MacAddress macAddress;

	Inet4Address ipAddress;
	Date changedAt;
	Date createdAt;

	@Type(type = "mactype")
	@Column(name = "mac")
	public MacAddress getMacAddress() {
		return macAddress;
	}

	public void setMacAddress(MacAddress macAddress) {
		this.macAddress = macAddress;
	}

	@Id
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

	@Column(name = "created_at")
	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

}
