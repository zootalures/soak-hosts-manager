package edu.bath.soak.dns.cmd;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import edu.bath.soak.cmd.ExecutableCommand;
import edu.bath.soak.dns.model.DNSRecord;
import edu.bath.soak.web.BeanView;

/**
 * DNS command, encapsulates a series of DNS add/delete commands collected as
 * {@link DNSRecordChange} objects.
 * 
 * 
 * @author cspocc
 * 
 */
@XmlRootElement()
@BeanView(value = "beanview/dns/DNSCmd")
public class DNSCmd extends ExecutableCommand implements Serializable {

	public DNSCmd() {

	}

	public String getSubSystem() {
		return "DNS";
	}

	List<DNSChange> changes = new ArrayList<DNSChange>();
	public static final String SKIP_DNS_CHANGES = "SKIP_DNS_CHANGES";

	/**
	 * The changes which will be applied by this command
	 * 
	 * @return
	 */
	public List<DNSChange> getChanges() {
		return changes;
	}

	public void setChanges(List<DNSChange> changes) {
		this.changes = changes;
	}

	public String toString() {
		String s = "";
		for (DNSChange dc : getChanges()) {
			s += dc + ". ";
		}
		return s;
	}

	public String getTitle() {
		return "DNS Change";
	}

	public String getCategory() {
		return "DNS Server";
	}

	/**
	 * Returns all records which will be added by this command
	 * 
	 * @return
	 */

	public List<DNSRecord> getAdditions() {
		ArrayList<DNSRecord> adds = new ArrayList<DNSRecord>();
		for (DNSChange change : getChanges()) {
			if (change.isAddition())
				adds.add(change.getRecord());
		}
		return adds;

	}

	public void insertAdd(DNSRecord dr) {
		DNSChange change = new DNSChange();
		change.setRecord(dr);
		change.setAddition(true);
		changes.add(change);
	}

	public void insertDelete(DNSRecord dr) {
		DNSChange change = new DNSChange();
		change.setRecord(dr);
		change.setAddition(false);
		changes.add(change);
	}

	/**
	 * Returns all records which will be deleted by this command
	 * 
	 * @return
	 */

	public List<DNSRecord> getDeletions() {
		ArrayList<DNSRecord> dels = new ArrayList<DNSRecord>();
		for (DNSChange change : getChanges()) {
			if (!change.isAddition())
				dels.add(change.getRecord());
		}
		return dels;

	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((changes == null) ? 0 : changes.hashCode());
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
		final DNSCmd other = (DNSCmd) obj;
		if (changes == null) {
			if (other.changes != null)
				return false;
		} else if (!changes.equals(other.changes))
			return false;
		return true;
	}
}
