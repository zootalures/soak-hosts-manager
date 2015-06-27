package edu.bath.soak.net.cmd;

import java.io.Serializable;

import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import edu.bath.soak.cmd.UICommand;
import edu.bath.soak.net.model.Host;
import edu.bath.soak.web.BeanView;

@XmlRootElement
@BeanView("beanview/host/BulkCreateEditHostsCmd")
public class BulkCreateEditHostsCmd extends BulkAlterHostCmd implements
		UICommand, Serializable {
	boolean creation;

	public BulkCreateEditHostsCmd(boolean isCreation) {
		this.creation = isCreation;
	}

	public BulkCreateEditHostsCmd() {

	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((hosts == null) ? 0 : hosts.hashCode());
		return result;
	}

	public boolean anyNeedIps() {
		for (Host h : hosts) {
			if (h.getIpAddress() == null)
				return true;
		}
		return false;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		final BulkCreateEditHostsCmd other = (BulkCreateEditHostsCmd) obj;

		if (hosts == null) {
			if (other.hosts != null)
				return false;
		} else if (!hosts.equals(other.hosts))
			return false;

		return true;
	}

	@Transient
	@XmlTransient
	public String getCommandDescription() {
		return "Bulk edit/creation of " + hosts.size() + " hosts ";
	}

	public boolean isCreation() {
		return creation;
	}

	public void setCreation(boolean creation) {
		this.creation = creation;
	}
}
