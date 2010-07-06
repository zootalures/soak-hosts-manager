package edu.bath.soak.dhcp.cmd;

import java.util.Collection;
import java.util.HashSet;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.validation.Errors;

import edu.bath.soak.dhcp.model.DHCPDao;
import edu.bath.soak.dhcp.model.DHCPReservation;
import edu.bath.soak.dhcp.model.StaticDHCPReservation;
import edu.bath.soak.net.cmd.HookableValidator;

/**
 * Base DHCP Command Validator, ensures semantic integrity of DHCP
 * 
 * prevents two reservations on the same Scope with the same MAC
 * 
 * 
 * For creations it checks that a reservation does not exist exists or exists
 * but is deleted in this command
 * 
 * For deletions it checks that the specified record exists.
 * 
 * @author cspocc
 * 
 */
public class DHCPCmdValidator extends HookableValidator {

	DHCPDao dhcpDAO;

	public boolean supports(Class clazz) {
		return DHCPCmd.class.isAssignableFrom(clazz);
	}

	public void validate(Object target, Errors errors) {
		DHCPCmd cmd = (DHCPCmd) target;
		Collection<DHCPReservation> deletions = cmd.getDeletions();

		for (int i = 0; i < cmd.getChanges().size(); i++) {
			DHCPChange change = (DHCPChange) cmd.getChanges().get(i);
			if (change.isAddition()) {
				DHCPReservation reservation = change.getReservation();
				HashSet<StaticDHCPReservation> clashingReservations = new HashSet<StaticDHCPReservation>();

				clashingReservations.addAll(dhcpDAO
						.getAllReservationsForMAC(change.getReservation()
								.getMacAddress()));

				if (reservation instanceof StaticDHCPReservation) {

					if (reservation.getScope() == null) {
						errors.reject("invalid-addition",
								"cannot create reservation " + reservation
										+ "  no scope specified");
					} else if (!reservation.getScope().containsIp(
							((StaticDHCPReservation) reservation)
									.getIpAddress())) {

						errors
								.reject(
										"invalid-addition",
										"Cannot create reservation "
												+ reservation
												+ " the IP address is not inside the specified scope");
					}

					clashingReservations
							.addAll(dhcpDAO
									.getAllReservationsForIP(((StaticDHCPReservation) reservation)
											.getIpAddress()));
				}

				for (StaticDHCPReservation clRes : clashingReservations) {
					if (clRes.getScope().equals(reservation.getScope())
							&& !deletions.contains(clRes)) {
						errors
								.reject(
										"invalid-addition",
										"The reservation "
												+ reservation
												+ " cannot be added because it clashes with an existing reservation: "
												+ clRes);
					}

				}

			} else {
				DHCPReservation res = dhcpDAO.getAllReservationForMAC(change
						.getReservation().getScope(), change.getReservation()
						.getMacAddress());
				if (res == null)
					errors.rejectValue("", "DHCP change tries to delete "
							+ change.getReservation()
							+ " which doesn't seem to exist");
			}
		}
		super.validate(target, errors);
	}

	@Required
	public void setDhcpDAO(DHCPDao dhcpDAO) {
		this.dhcpDAO = dhcpDAO;
	}
}
