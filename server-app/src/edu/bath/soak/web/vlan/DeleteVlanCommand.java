package edu.bath.soak.web.vlan;

import edu.bath.soak.net.model.Vlan;

public class DeleteVlanCommand {

	Vlan vlan;
	Vlan moveToVlan;

	public Vlan getVlan() {
		return vlan;
	}

	public void setVlan(Vlan vlan) {
		this.vlan = vlan;
	}

	public Vlan getMoveToVlan() {
		return moveToVlan;
	}

	public void setMoveToVlan(Vlan moveToVlan) {
		this.moveToVlan = moveToVlan;
	}
	
}
