package edu.bath.soak.hostactivity.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Transient;

import org.hibernate.annotations.Type;

import edu.bath.soak.util.MacAddress;

//switch varchar (255) NOT NULL, 
//iid    int(11) UNSIGNED NOT NULL,
//mac    varchar(20) NOT NULL,
//changed_at DATETIME,
//created_at DATETIME,
//PRIMARY KEY (switch,iid,mac),
//INDEX switchmac_mac_idx  (mac)
@Entity
public class SwitchMac {

	@Embeddable
	public static class SwitchMacKey implements Serializable {
		String switchName;
		Integer iid;
		MacAddress macAddress;

		@Column(name = "switch")
		public String getSwitchName() {
			return switchName;
		}

		public void setSwitchName(String switchName) {
			this.switchName = switchName;
		}

		public Integer getIid() {
			return iid;
		}

		public void setIid(Integer iid) {
			this.iid = iid;
		}

		@Column(name = "mac")
		@Type(type = "mactype")
		public MacAddress getMacAddress() {
			return macAddress;
		}

		public void setMacAddress(MacAddress macAddress) {
			this.macAddress = macAddress;
		}

	}

	Date createdAt;
	Date changedAt;
	SwitchMacKey key = new SwitchMacKey();

	@Transient
	public String getSwitchName() {
		return key.switchName;
	}

	public void setSwitchName(String switchName) {
		this.key.switchName = switchName;
	}

	@Transient
	public Integer getIid() {
		return key.iid;
	}

	public void setIid(Integer iid) {
		this.key.iid = iid;
	}

	@Transient
	public MacAddress getMacAddress() {
		return key.macAddress;
	}

	public void setMacAddress(MacAddress macAddress) {
		this.key.macAddress = macAddress;
	}

	@Column(name = "created_at")
	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	@Column(name = "changed_at")
	public Date getChangedAt() {
		return changedAt;
	}

	public void setChangedAt(Date changedAt) {
		this.changedAt = changedAt;
	}

	@EmbeddedId
	public SwitchMacKey getKey() {
		return key;
	}

	public void setKey(SwitchMacKey key) {
		this.key = key;
	}

}
