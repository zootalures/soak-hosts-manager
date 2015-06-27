package edu.bath.soak.net.cmd;

import javax.xml.bind.annotation.XmlRootElement;

import edu.bath.soak.net.model.Host;

@XmlRootElement
public abstract class BaseHostsDBCommand extends HostsDBCommand {
	Host host;
	String changeComments;

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((changeComments == null) ? 0 : changeComments.hashCode());
		result = prime * result + ((host == null) ? 0 : host.hashCode());
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
		final BaseHostsDBCommand other = (BaseHostsDBCommand) obj;
		if (changeComments == null) {
			if (other.changeComments != null)
				return false;
		} else if (!changeComments.equals(other.changeComments))
			return false;
		if (host == null) {
			if (other.host != null)
				return false;
		} else if (!host.equals(other.host))
			return false;
		return true;
	}

	public Host getHost() {
		return host;
	}

	public void setHost(Host host) {
		this.host = host;
	}

	public String getChangeComments() {
		return changeComments;
	}

	public void setChangeComments(String commandComments) {
		this.changeComments = commandComments;
	}
}
