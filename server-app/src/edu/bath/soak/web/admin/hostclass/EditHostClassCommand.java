package edu.bath.soak.web.admin.hostclass;

import edu.bath.soak.net.model.HostClass;

public class EditHostClassCommand {
	HostClass hostClass;
	
	boolean isCreation = false;
	public HostClass getHostClass() {
		return hostClass;
	}
	public void setHostClass(HostClass hostClass) {
		this.hostClass = hostClass;
	}
	public boolean isCreation() {
		return isCreation;
	}
	public void setCreation(boolean isCreation) {
		this.isCreation = isCreation;
	};
	
}
