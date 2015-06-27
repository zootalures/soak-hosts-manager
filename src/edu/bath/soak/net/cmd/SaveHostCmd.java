package edu.bath.soak.net.cmd;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

import edu.bath.soak.web.BeanView;

@XmlRootElement
@BeanView(value = "beanview/host/SaveHostCmd")
public class SaveHostCmd extends BaseHostsDBCommand implements Serializable {
	boolean creation;
	Long versionBeforeChange;

	public boolean isCreation() {
		return creation;
	}

	public void setCreation(boolean creation) {
		this.creation = creation;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + (creation ? 1231 : 1237);
		result = prime
				* result
				+ ((versionBeforeChange == null) ? 0 : versionBeforeChange
						.hashCode());
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
		final SaveHostCmd other = (SaveHostCmd) obj;
		if (creation != other.creation)
			return false;
		if (versionBeforeChange == null) {
			if (other.versionBeforeChange != null)
				return false;
		} else if (!versionBeforeChange.equals(other.versionBeforeChange))
			return false;
		return true;
	}

	public Long getVersionBeforeChange() {

		return versionBeforeChange;
	}

	public void setVersionBeforeChange(Long versionBeforeChange) {
		this.versionBeforeChange = versionBeforeChange;
	}

}
