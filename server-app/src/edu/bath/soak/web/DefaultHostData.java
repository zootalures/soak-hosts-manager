package edu.bath.soak.web;

import edu.bath.soak.model.OwnershipInfo;
import edu.bath.soak.net.model.Host;
import edu.bath.soak.net.model.HostClass;
import edu.bath.soak.net.model.Location;

public class DefaultHostData implements java.io.Serializable {
	HostClass hostClass;
	Location location = new Location();
	OwnershipInfo ownership = new OwnershipInfo();

	public HostClass getHostClass() {
		return hostClass;
	}

	public void setHostClass(HostClass hostClass) {
		this.hostClass = hostClass;
	}

	public Location getLocation() { 
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public OwnershipInfo getOwnership() {
		return ownership;
	}

	public void setOwnership(OwnershipInfo ownership) {
		this.ownership = ownership;
	}

	public void applyDefaults(Host h) {
		if (null == h.getHostClass()) {
			h.setHostClass(hostClass);
		}

		if (null == h.getLocation().getBuilding()) {
			h.getLocation().setBuilding(location.getBuilding());
		}
		if (null == h.getLocation().getRoom()) {
			h.getLocation().setRoom(location.getRoom());
		}
		if (null == h.getOwnership().getOrgUnit()) {
			h.getOwnership().setOrgUnit(ownership.getOrgUnit());
		}
	}
}
