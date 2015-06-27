package edu.bath.soak.dhcp;

import javax.xml.bind.annotation.XmlRegistry;

import edu.bath.soak.dhcp.cmd.DHCPChange;
import edu.bath.soak.dhcp.cmd.DHCPCmd;
import edu.bath.soak.dhcp.cmd.DHCPHostCommandFlags;
import edu.bath.soak.dhcp.model.StaticDHCPReservation;

@XmlRegistry
public class ObjectFactory {

	public DHCPCmd createDHCPCmd() {
		return new DHCPCmd();
	}

	public DHCPChange createDHCPChange() {
		return new DHCPChange();
	}

	public StaticDHCPReservation createStaticDHCPReservation() {
		return new StaticDHCPReservation();
	}

	public DHCPHostCommandFlags createDHCPHostCommandFlags() {
		return new DHCPHostCommandFlags();
	}

}
