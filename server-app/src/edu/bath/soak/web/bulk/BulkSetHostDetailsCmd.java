package edu.bath.soak.web.bulk;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlIDREF;

import edu.bath.soak.model.OrgUnit;
import edu.bath.soak.net.model.HostClass;
import edu.bath.soak.net.model.NameDomain;

/**
 * Command object representing a bulk alteration of one or more hosts
 * 
 * @author cspocc
 * 
 */

public class BulkSetHostDetailsCmd implements Serializable {
	HostClass newHostClass;
	NameDomain newNameDomain;
	OrgUnit newOrgUnit;
	boolean doChangeBuilding = false;
	String newHostBuilding;
	boolean doChangeRoom = false;
	String newHostRoom;

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + (doChangeBuilding ? 1231 : 1237);
		result = prime * result + (doChangeRoom ? 1231 : 1237);
		result = prime * result
				+ ((newHostBuilding == null) ? 0 : newHostBuilding.hashCode());
		result = prime * result
				+ ((newHostClass == null) ? 0 : newHostClass.hashCode());
		result = prime * result
				+ ((newHostRoom == null) ? 0 : newHostRoom.hashCode());
		result = prime * result
				+ ((newNameDomain == null) ? 0 : newNameDomain.hashCode());
		result = prime * result
				+ ((newOrgUnit == null) ? 0 : newOrgUnit.hashCode());
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
		final BulkSetHostDetailsCmd other = (BulkSetHostDetailsCmd) obj;
		if (doChangeBuilding != other.doChangeBuilding)
			return false;
		if (doChangeRoom != other.doChangeRoom)
			return false;

		if (newHostBuilding == null) {
			if (other.newHostBuilding != null)
				return false;
		} else if (!newHostBuilding.equals(other.newHostBuilding))
			return false;
		if (newHostClass == null) {
			if (other.newHostClass != null)
				return false;
		} else if (!newHostClass.equals(other.newHostClass))
			return false;
		if (newHostRoom == null) {
			if (other.newHostRoom != null)
				return false;
		} else if (!newHostRoom.equals(other.newHostRoom))
			return false;
		if (newNameDomain == null) {
			if (other.newNameDomain != null)
				return false;
		} else if (!newNameDomain.equals(other.newNameDomain))
			return false;
		if (newOrgUnit == null) {
			if (other.newOrgUnit != null)
				return false;
		} else if (!newOrgUnit.equals(other.newOrgUnit))
			return false;
		return true;
	}

	@XmlIDREF
	public HostClass getNewHostClass() {
		return newHostClass;
	}

	public void setNewHostClass(HostClass newHostClass) {
		this.newHostClass = newHostClass;
	}

	public String getNewHostBuilding() {
		return newHostBuilding;
	}

	public void setNewHostBuilding(String newHostBuilding) {
		this.newHostBuilding = newHostBuilding;
	}

	public String getNewHostRoom() {
		return newHostRoom;
	}

	public void setNewHostRoom(String newHostRoom) {
		this.newHostRoom = newHostRoom;
	}

	public OrgUnit getNewOrgUnit() {
		return newOrgUnit;
	}

	public void setNewOrgUnit(OrgUnit newOrgUnit) {
		this.newOrgUnit = newOrgUnit;
	}

	public NameDomain getNewNameDomain() {
		return newNameDomain;
	}

	public void setNewNameDomain(NameDomain newNameDomain) {
		this.newNameDomain = newNameDomain;
	}

	public boolean isDoChangeRoom() {
		return doChangeRoom;
	}

	public void setDoChangeRoom(boolean doChangeRoom) {
		this.doChangeRoom = doChangeRoom;
	}

	public boolean isDoChangeBuilding() {
		return doChangeBuilding;
	}

	public void setDoChangeBuilding(boolean doChangeBuilding) {
		this.doChangeBuilding = doChangeBuilding;
	}

}
