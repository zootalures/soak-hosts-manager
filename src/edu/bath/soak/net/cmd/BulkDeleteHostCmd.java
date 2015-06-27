package edu.bath.soak.net.cmd;

import java.io.Serializable;

import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import edu.bath.soak.cmd.UICommand;
import edu.bath.soak.web.BeanView;

@XmlRootElement
@BeanView("beanview/host/BulkDeleteHostCmd")
public class BulkDeleteHostCmd extends BulkAlterHostCmd implements UICommand,
		Serializable {

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((hosts == null) ? 0 : hosts.hashCode());
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
		final BulkDeleteHostCmd other = (BulkDeleteHostCmd) obj;
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
		return "Bulk deletion of " + hosts.size() + " hosts";
	}
}
