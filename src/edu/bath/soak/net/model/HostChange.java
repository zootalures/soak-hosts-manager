package edu.bath.soak.net.model;

import java.net.Inet4Address;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.Type;

import edu.bath.soak.model.OrgUnit;

@Entity
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = { "changeType",
		"hostId", "version" }) })
public class HostChange implements java.io.Serializable {
	Date changeDate;
	HostName name;
	Inet4Address ipAddress;
	String commandId;
	OrgUnit orgUnit;

	long version;

	String hostXml;
	Long id;
	ChangeType changeType;
	Host hostAfterChange;

	String userId;
	String changeComments;
	Long hostId;

	String commandDescription;

	public static enum ChangeType {
		ADD, DELETE, CHANGE
	};

	public Date getChangeDate() {
		return changeDate;
	}

	public void setChangeDate(Date changeDate) {
		this.changeDate = changeDate;
	}

	public ChangeType getChangeType() {
		return changeType;
	}

	public void setChangeType(ChangeType changeType) {
		this.changeType = changeType;
	}

	@Lob
	public String getHostXml() {

		return hostXml;
	}

	public void setHostXml(String hostData) {
		hostXml = hostData;

	}

	@Transient
	public Host getHost() {
		return hostAfterChange;
	}

	public void setHost(Host host) {
		this.hostAfterChange = host;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getHostId() {
		return hostId;
	}

	public void setHostId(Long hostId) {
		this.hostId = hostId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getChangeComments() {
		return changeComments;
	}

	public void setChangeComments(String changeComment) {
		this.changeComments = changeComment;
	}

	@Column(nullable = false)
	public HostName getHostName() {
		return name;
	}

	public void setHostName(HostName name) {
		this.name = name;
	}

	@Type(type = "inet4type")
	public Inet4Address getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(Inet4Address ipAddress) {
		this.ipAddress = ipAddress;
	}

	@Column(nullable = false)
	public long getVersion() {
		return version;
	}

	public void setVersion(long version) {
		this.version = version;
	}

	public String getCommandDescription() {
		return commandDescription;
	}

	public void setCommandDescription(String commandDescription) {
		this.commandDescription = commandDescription;
	}

	public String getCommandId() {
		return commandId;
	}

	public void setCommandId(String commandId) {
		this.commandId = commandId;
	}

	@ManyToOne(targetEntity = OrgUnit.class, optional = true)
	@NotFound(action = NotFoundAction.IGNORE)
	public OrgUnit getOrgUnit() {
		return orgUnit;
	}

	public void setOrgUnit(OrgUnit orgUnit) {
		this.orgUnit = orgUnit;
	}

}
