package edu.bath.soak.web.dhcp;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import edu.bath.soak.dhcp.model.StaticDHCPReservation;
import edu.bath.soak.web.BeanView;
import edu.bath.soak.web.host.HostView;

@BeanView("beanview/dhcp/DHCPHostReservationsView")
public class DHCPHostReservationsView {
	static enum DHCP_VIEW_STATUS {
		MISSING, OK, SPURIOUS
	}

	List<StaticDHCPReservation> reservations = new LinkedList<StaticDHCPReservation>();
	Map<StaticDHCPReservation, DHCP_VIEW_STATUS> reservationState = new HashMap<StaticDHCPReservation, DHCP_VIEW_STATUS>();

	HostView parent;

	public boolean isHasErrors() {
		for (DHCP_VIEW_STATUS stat : reservationState.values()) {
			if (!stat.equals(DHCP_VIEW_STATUS.OK)) {
				return true;
			}
		}
		return false;

	}

	public DHCPHostReservationsView(HostView parent) {
		this.parent = parent;
	}

	public HostView getParent() {
		return parent;
	}

	public void setParent(HostView parent) {
		this.parent = parent;
	}

	public List<StaticDHCPReservation> getReservations() {
		return reservations;
	}

	public void setReservations(List<StaticDHCPReservation> allReservations) {
		this.reservations = allReservations;
	}

	public Map<StaticDHCPReservation, DHCP_VIEW_STATUS> getReservationState() {
		return reservationState;
	}

	public void setReservationState(
			Map<StaticDHCPReservation, DHCP_VIEW_STATUS> reservationStatuses) {
		this.reservationState = reservationStatuses;
	}

}
