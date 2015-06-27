package edu.bath.soak.dns.cmd;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import edu.bath.soak.cmd.AbstractUICmdImpl;
import edu.bath.soak.cmd.UICommand;
import edu.bath.soak.dns.model.DNSRecord;

@XmlRootElement
public class BulkDeleteDNSRecordsCmd extends AbstractUICmdImpl implements
		UICommand, Serializable {

	List<DNSRecord> toDelete = new ArrayList<DNSRecord>();

	@Transient
	@XmlTransient
	public String getCommandDescription() {
		return "Remove unused DNS Records";
	}

	@XmlTransient
	public List<DNSRecord> getToDelete() {
		return toDelete;
	}

	public void setToDelete(List<DNSRecord> toDelete) {
		this.toDelete = toDelete;
	}
}
