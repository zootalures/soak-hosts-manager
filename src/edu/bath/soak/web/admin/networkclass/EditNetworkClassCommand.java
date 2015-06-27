package edu.bath.soak.web.admin.networkclass;

import java.util.HashMap;
import java.util.Map;

import edu.bath.soak.net.model.NetworkClass;

public class EditNetworkClassCommand {
	NetworkClass networkClass;
	Map<String, Boolean> hostClassPermissions = new HashMap<String, Boolean>();
	boolean isCreation = false;

	public NetworkClass getNetworkClass() {
		return networkClass;
	}

	public void setNetworkClass(NetworkClass networkClass) {
		this.networkClass = networkClass;
	}

	public boolean isCreation() {
		return isCreation;
	}

	public void setCreation(boolean isCreation) {
		this.isCreation = isCreation;
	}

	public Map<String, Boolean> getHostClassPermissions() {
		return hostClassPermissions;
	}

	public void setHostClassPermissions(
			Map<String, Boolean> hostClassPermissions) {
		this.hostClassPermissions = hostClassPermissions;
	};

}
