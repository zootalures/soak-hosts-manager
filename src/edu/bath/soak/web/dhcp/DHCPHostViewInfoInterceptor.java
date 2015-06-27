package edu.bath.soak.web.dhcp;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;

import edu.bath.soak.dhcp.HostsDHCPInterceptor;
import edu.bath.soak.dhcp.model.StaticDHCPReservation;
import edu.bath.soak.net.model.HostClass.DHCP_STATUS;
import edu.bath.soak.web.dhcp.DHCPHostReservationsView.DHCP_VIEW_STATUS;
import edu.bath.soak.web.host.HostView;
import edu.bath.soak.web.host.HostViewTab;
import edu.bath.soak.web.host.ShowHostInfoInterceptor;

/*******************************************************************************
 * View interceptor which adds host DHCP Reservation information to view in the
 * RELATED_INFO tab
 * 
 * @author cspocc
 * 
 */
public class DHCPHostViewInfoInterceptor implements ShowHostInfoInterceptor {

	HostsDHCPInterceptor hostsDHCPInterceptor;

	public void elaborateView(HostView view, HttpServletRequest request) {
		HostViewTab tab = view.getTabByName(HostView.RELATED_INFO);
		Assert.notNull(tab);
		DHCPHostReservationsView dv = new DHCPHostReservationsView(view);
		List<StaticDHCPReservation> existing = hostsDHCPInterceptor
				.getExistingReservations(view.getHost());

		List<StaticDHCPReservation> required = hostsDHCPInterceptor
				.getRequiredReservations(view.getHost());

		List<StaticDHCPReservation> allReservations = new LinkedList<StaticDHCPReservation>();
		Map<StaticDHCPReservation, DHCP_VIEW_STATUS> status = new HashMap<StaticDHCPReservation, DHCP_VIEW_STATUS>();

		for (StaticDHCPReservation res : existing) {
			allReservations.add(res);
			if (!required.contains(res)) {
				status.put(res, DHCP_VIEW_STATUS.SPURIOUS);
			} else {
				status.put(res, DHCP_VIEW_STATUS.OK);
			}
		}
		for (StaticDHCPReservation res : required) {
			if (!existing.contains(res)) {
				allReservations.add(res);
				status.put(res, DHCP_VIEW_STATUS.MISSING);
			}
		}
		dv.setReservations(allReservations);
		dv.setReservationState(status);
		if (dv.getReservations().size() > 0
				|| view.getHost().getHostClass().getDHCPStatus() != DHCP_STATUS.NONE)

			tab.addViewComponent(dv);
	}

	public int getOrder() {
		return (Integer.MAX_VALUE / 2) + 1;
	}

	@Required
	public void setHostsDHCPInterceptor(
			HostsDHCPInterceptor hostsDHCPInterceptor) {
		this.hostsDHCPInterceptor = hostsDHCPInterceptor;
	}

}
