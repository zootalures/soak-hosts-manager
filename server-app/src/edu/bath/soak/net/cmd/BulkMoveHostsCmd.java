package edu.bath.soak.net.cmd;

import java.io.Serializable;
import java.net.Inet4Address;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import edu.bath.soak.cmd.UICommand;
import edu.bath.soak.net.model.Host;
import edu.bath.soak.net.model.Subnet;
import edu.bath.soak.web.BeanView;

@XmlRootElement
@BeanView("beanview/host/BulkMoveHostsCmd")
public class BulkMoveHostsCmd extends BulkAlterHostCmd implements UICommand,
		Serializable {
	Subnet newSubnet;
	Map<Long, Inet4Address> hostAddresses = new HashMap<Long, Inet4Address>();

	public Subnet getNewSubnet() {
		return newSubnet;
	}

	@Override
	public Inet4Address selectedAddressForHost(Host h) {
		return hostAddresses.get(h.getId());
	}

	public void setNewSubnet(Subnet newSubnet) {
		this.newSubnet = newSubnet;
	}

	public List<Host> getHosts() {
		return hosts;
	}

	public void setHosts(List<Host> hosts) {
		this.hosts = hosts;
	}

	public Map<Long, Inet4Address> getHostAddresses() {
		return hostAddresses;
	}

	public void setHostAddresses(Map<Long, Inet4Address> hostAddresses) {
		this.hostAddresses = hostAddresses;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((hostAddresses == null) ? 0 : hostAddresses.hashCode());
		result = prime * result + ((hosts == null) ? 0 : hosts.hashCode());
		result = prime * result
				+ ((newSubnet == null) ? 0 : newSubnet.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		final BulkMoveHostsCmd other = (BulkMoveHostsCmd) obj;
		if (hostAddresses == null) {
			if (other.hostAddresses != null)
				return false;
		} else if (!hostAddresses.equals(other.hostAddresses))
			return false;
		if (hosts == null) {
			if (other.hosts != null)
				return false;
		} else if (!hosts.equals(other.hosts))
			return false;
		if (newSubnet == null) {
			if (other.newSubnet != null)
				return false;
		} else if (!newSubnet.equals(other.newSubnet))
			return false;
		return true;
	}

	@Transient
	@XmlTransient
	public String getCommandDescription() {
		return "Bulk move of " + hosts.size() + " hosts to subnet "
				+ newSubnet.getDisplayString();
	}
}
