package edu.bath.soak.dns.cmd;

import java.util.List;

import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlTransient;

import edu.bath.soak.cmd.AbstractUICmdImpl;
import edu.bath.soak.cmd.UICommand;
import edu.bath.soak.net.model.Host;

public class CleanUpDNSRecordsForHosts extends AbstractUICmdImpl implements
		UICommand {

	List<Host> hosts;
	boolean deleteConflictingRecords;

	public List<Host> getHosts() {
		return hosts;
	}

	public void setHosts(List<Host> hosts) {
		this.hosts = hosts;
	}

	public boolean isDeleteConflictingRecords() {
		return deleteConflictingRecords;
	}

	public void setDeleteConflictingRecords(boolean deleteConflictingRecords) {
		this.deleteConflictingRecords = deleteConflictingRecords;
	}

	@Transient
	@XmlTransient
	public String getCommandDescription() {
		return "Clean up DNS records of " + hosts.size() + " hosts";
	}
}
