package edu.bath.soak.dns.cmd;

import java.io.Serializable;

import edu.bath.soak.dns.model.DNSRecord;

/**
 * Represents a single DNS change which may be part of a larger set
 * 
 * The changes may be additions or deletions (not modifications)
 * 
 * @author cspocc
 */
public class DNSChange implements Serializable {
	DNSRecord record;
	boolean addition = true;

	/**
	 * Returns the org.xbill.Record which is associated with this change
	 * 
	 * @return
	 */
	public DNSRecord getRecord() {
		return record;
	}

	/**
	 * Sets the record which is associated with this change
	 * 
	 * @param record
	 */
	public void setRecord(DNSRecord record) {
		this.record = record;
	}

	/**
	 * Returns the change type for this record
	 * 
	 * @return
	 */

	public String toString() {
		String verb;
		if (addition) {
			verb = "add";
		} else {
			verb = "delete";
		}
		return verb + " " + getRecord();
	}

	/**
	 * Is this change and addition or deletion
	 * 
	 * @return
	 */
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
		result = prime * result + ((record == null) ? 0 : record.hashCode());
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
		final DNSChange other = (DNSChange) obj;
		if (addition != other.addition)
			return false;
		if (record == null) {
			if (other.record != null)
				return false;
		} else if (!record.equals(other.record))
			return false;
		return true;
	}

}
