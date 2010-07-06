package edu.bath.soak.imprt.cmd;

import java.util.List;

import edu.bath.soak.net.model.Host;

public class BulkHostsImportCmd extends BulkCommand {

	List<Host> newHosts;

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((newHosts == null) ? 0 : newHosts.hashCode());
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
		final BulkHostsImportCmd other = (BulkHostsImportCmd) obj;
		if (newHosts == null) {
			if (other.newHosts != null)
				return false;
		} else if (!newHosts.equals(other.newHosts))
			return false;
		return true;
	}

	public List<Host> getNewHosts() {
		return newHosts;
	}

	public void setNewHosts(List<Host> newHosts) {
		this.newHosts = newHosts;
	}

}