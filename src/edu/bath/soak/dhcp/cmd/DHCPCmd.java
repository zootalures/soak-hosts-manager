package edu.bath.soak.dhcp.cmd;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import edu.bath.soak.cmd.ExecutableCommand;
import edu.bath.soak.dhcp.model.DHCPReservation;
import edu.bath.soak.web.BeanView;

@XmlRootElement
@BeanView(value = "beanview/dhcp/DHCPCmd")
public class DHCPCmd extends ExecutableCommand implements Serializable {
	public DHCPCmd() {
	}

	List<DHCPChange> changes = new ArrayList<DHCPChange>();

	public String getSubSystem() {
		return "DHCP";
	}

	public List<DHCPChange> getChanges() {
		return changes;
	}

	@XmlTransient
	@Transient
	public List<DHCPReservation> getAdditions() {
		ArrayList<DHCPReservation> ress = new ArrayList<DHCPReservation>();
		for (DHCPChange change : changes) {
			if (change.isAddition())
				ress.add(change.getReservation());
		}
		return ress;
	}

	@XmlTransient
	@Transient
	public List<DHCPReservation> getDeletions() {
		ArrayList<DHCPReservation> ress = new ArrayList<DHCPReservation>();
		for (DHCPChange change : changes) {
			if (!change.isAddition())
				ress.add(change.getReservation());
		}
		return ress;
	}

	public void setChanges(List<DHCPChange> changes) {
		this.changes = changes;
	}

	public void insertAdd(DHCPReservation r) {
		DHCPChange change = new DHCPChange();
		change.setAddition(true);
		change.setReservation(r);
		changes.add(change);
	}

	public void insertDel(DHCPReservation r) {
		DHCPChange change = new DHCPChange();
		change.setAddition(false);
		change.setReservation(r);
		changes.add(change);
	}

	public String toString() {
		String change = "";
		for (DHCPChange dc : changes) {
			change += dc.toString() + ". ";

		}
		return change;
	}

	public String getCategory() {
		return "DHCP Server";
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
		final DHCPCmd other = (DHCPCmd) obj;
		if (changes == null) {
			if (other.changes != null)
				return false;
		} else if (!changes.equals(other.changes))
			return false;
		return true;
	}
}
