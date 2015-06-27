package edu.bath.soak.dns.model;

import java.util.Date;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class DNSRecordChange {
	Long id;
	Date changeDate;
	String commandId;

	public static enum DNSChangeType {
		ADD, DEL
	};

	DNSChangeType changeType;

	@Embedded
	@AttributeOverrides( { @AttributeOverride(name = "id", column = @Column(name = "record_id")) })
	DNSRecord record;

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

	public DNSChangeType getChangeType() {
		return changeType;
	}

	public void setChangeType(DNSChangeType changeType) {
		this.changeType = changeType;
	}

	public DNSRecord getRecord() {
		return record;
	}

	public void setRecord(DNSRecord record) {
		this.record = record;
	}

	public String getCommandId() {
		return commandId;
	}

	public void setCommandId(String commandId) {
		this.commandId = commandId;
	}

}
