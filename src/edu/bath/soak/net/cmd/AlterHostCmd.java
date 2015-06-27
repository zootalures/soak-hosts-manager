package edu.bath.soak.net.cmd;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import edu.bath.soak.cmd.AbstractUICmdImpl;
import edu.bath.soak.cmd.UICommand;
import edu.bath.soak.net.model.Host;
import edu.bath.soak.net.model.Subnet;
import edu.bath.soak.web.BeanView;

@BeanView(value = "beanview/host/AlterHostCmd")
@XmlRootElement
public class AlterHostCmd extends AbstractUICmdImpl implements UICommand,
		Serializable {
	Host newHost = new Host();
	boolean specifyIp;
	Subnet subnet;

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((newHost == null) ? 0 : newHost.hashCode());
		result = prime * result + (specifyIp ? 1231 : 1237);
		result = prime * result + ((subnet == null) ? 0 : subnet.hashCode());
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
		final AlterHostCmd other = (AlterHostCmd) obj;
		if (newHost == null) {
			if (other.newHost != null)
				return false;
		} else if (!newHost.equals(other.newHost))
			return false;
		if (specifyIp != other.specifyIp)
			return false;
		if (subnet == null) {
			if (other.subnet != null)
				return false;
		} else if (!subnet.equals(other.subnet))
			return false;
		return true;
	}

	public Host getNewHost() {
		return newHost;
	}

	@XmlTransient
	public Subnet getSubnet() {
		return subnet;
	}

	public boolean isSpecifyIp() {
		return specifyIp;
	}

	public void setNewHost(Host newHost) {
		this.newHost = newHost;
	}

	public void setSpecifyIp(boolean specifyIp) {
		this.specifyIp = specifyIp;
	}

	public void setSubnet(Subnet subnet) {
		this.subnet = subnet;
	}

	public boolean isCreation() {
		return newHost.getId() == null;
	}

	@Transient
	@XmlTransient
	public String getCommandDescription() {
		return "Create/edit host " + newHost.getHostName().toString();
	}
}
