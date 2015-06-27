package edu.bath.soak.hostactivity;

import java.net.Inet4Address;

import org.springframework.beans.factory.annotation.Required;

import edu.bath.soak.hostactivity.model.HostActivityDAO;
import edu.bath.soak.mgr.AddressManagerAdvisor;
import edu.bath.soak.net.AdviceBasedAddressSpaceManager;
import edu.bath.soak.net.model.Host;
import edu.bath.soak.net.model.Subnet;
import edu.bath.soak.util.MacAddress;

/*******************************************************************************
 * {@link AddressManagerAdvisor} plugin to
 * {@link AdviceBasedAddressSpaceManager} which avoids hosts which have been
 * active (on a different MAC address than the host specified) in a given
 * period;
 * 
 * This only advises against, and does not block IPs
 * 
 * @author cspocc
 * 
 */
public class AvoidRecentlyActiveIps implements AddressManagerAdvisor {
	HostActivityDAO hostActivityDAO;
	int lastInUseSeconds = 3600 * 6;

	public AddressManagerAdvice getAdviceForAllocation(Host h, Subnet s,
			Inet4Address addr) {
		MacAddress mac = h.getMacAddress();
		
		return null;
	}

	public int getOrder() {
		return 100;
	}

	@Required
	public void setHostActivityDAO(HostActivityDAO hostActivityDAO) {
		this.hostActivityDAO = hostActivityDAO;
	}

	public int getLastInUseSeconds() {
		return lastInUseSeconds;
	}

	/**
	 * Sets the number of seconds which an IP must have been active in before
	 * the host will be marked as OK
	 * 
	 * @param lastInUseSeconds
	 */
	public void setLastInUseSeconds(int lastInUseSeconds) {
		this.lastInUseSeconds = lastInUseSeconds;
	}

}
