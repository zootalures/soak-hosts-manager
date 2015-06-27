package edu.bath.soak.dhcp;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.validation.Errors;

import edu.bath.soak.cmd.BaseCompositeCommand;
import edu.bath.soak.cmd.CommandExpander;
import edu.bath.soak.cmd.OrderedValidator;
import edu.bath.soak.cmd.UICommand;
import edu.bath.soak.dhcp.cmd.DHCPCmd;
import edu.bath.soak.dhcp.cmd.DHCPHostCommandFlags;
import edu.bath.soak.dhcp.model.DHCPDao;
import edu.bath.soak.dhcp.model.DHCPReservation;
import edu.bath.soak.dhcp.model.DHCPScope;
import edu.bath.soak.dhcp.model.DHCPServer;
import edu.bath.soak.dhcp.model.StaticDHCPReservation;
import edu.bath.soak.net.cmd.AlterHostCmd;
import edu.bath.soak.net.cmd.BulkCreateEditHostsCmd;
import edu.bath.soak.net.cmd.DeleteHostUICmd;
import edu.bath.soak.net.model.Host;
import edu.bath.soak.net.model.NetDAO;
import edu.bath.soak.net.model.Subnet;
import edu.bath.soak.net.model.HostClass.DHCP_STATUS;
import edu.bath.soak.security.SecurityHelper;
import edu.bath.soak.util.TypeUtils;

/**
 * DHCP Host command interceptor and validator,
 * 
 * Handles alter and delete Host commands and appends necessary DHCP Updates.
 * 
 * 
 * @author cspocc
 * 
 */
public class HostsDHCPInterceptor implements CommandExpander<UICommand>,
		OrderedValidator {

	DHCPDao dhcpDAO;
	NetDAO hostsDAO;
	SecurityHelper securityHelper;

	/**
	 * Returns a list of "endorsed" DHCP reservations for a given host,
	 * 
	 * These are the registrations which would be deleted if the host were
	 * deleted.
	 * 
	 * In particular it returns any reservations for the hosts' IP or MAC
	 * address
	 * 
	 * @param h
	 * @return
	 */
	public List<StaticDHCPReservation> getExistingReservations(Host h) {
		List<StaticDHCPReservation> ress = new ArrayList<StaticDHCPReservation>();
		if (null != h.getMacAddress())
			ress.addAll(dhcpDAO.getAllReservationsForMAC(h.getMacAddress()));
		for (StaticDHCPReservation res : dhcpDAO.getAllReservationsForIP(h
				.getIpAddress())) {
			if (!ress.contains(res))
				ress.add(res);
		}

		return ress;
	}

	/**
	 * Returns a list of "required" dhcp reservations for a given host These are
	 * the registrations which would be create if the host were new
	 * 
	 * @param h
	 * @return
	 */
	public List<StaticDHCPReservation> getRequiredReservations(Host h) {

		List<StaticDHCPReservation> ress = new ArrayList<StaticDHCPReservation>();
		if (h.getMacAddress() == null
				|| h.getHostClass().getDHCPStatus().equals(DHCP_STATUS.NONE)) {
			return ress;
		}

		for (DHCPServer server : dhcpDAO.getDHCPServers()) {
			for (DHCPScope scope : dhcpDAO.getDHCPScopes(server)) {
				if (scope.containsIp(h.getIpAddress())) {
					StaticDHCPReservation res = new StaticDHCPReservation();
					res.setHostName(h.getHostName().getName());
					res.setIpAddress(h.getIpAddress());
					res.setScope(scope);
					res.setMacAddress(h.getMacAddress());
					res.setComment("HM:" + new Date().toString());
					ress.add(res);
				}
			}
		}
		return ress;
	}

	/**
	 * Expands a host update or creation command
	 * 
	 * Appends a DHCP update command to the context if any DHCP changes are
	 * required
	 * 
	 * @param cmd
	 * @param context
	 */
	public void expandAlterHostCmd(AlterHostCmd cmd,
			BaseCompositeCommand context) {
		Host oldh = null;

		Set<DHCPReservation> current_res = new HashSet<DHCPReservation>();
		current_res.addAll(getExistingReservations(cmd.getNewHost()));

		if (!cmd.isCreation()) {
			oldh = hostsDAO.loadHost(cmd.getNewHost().getId());
			current_res.addAll(getExistingReservations(oldh));
		}

		if (oldh != null && !doEditDHCP(oldh, cmd))
			return;

		Set<DHCPReservation> required_res = new HashSet<DHCPReservation>();
		required_res.addAll(getRequiredReservations(cmd.getNewHost()));

		DHCPCmd dhcpCmd = new DHCPCmd();
		boolean forceUpdate = getActiveFlags(cmd).isRefreshDHCP();
		for (DHCPReservation r : required_res) {
			if (forceUpdate || !current_res.contains(r)) {
				dhcpCmd.insertAdd(r);
			}
		}
		for (DHCPReservation r : current_res) {
			if (forceUpdate || !required_res.contains(r)) {
				dhcpCmd.insertDel(r);
			}
		}

		if (dhcpCmd.getChanges().size() != 0) {
			context.getCommands().add(dhcpCmd);
		}

	}

	/**
	 * Expands a host deletion command, removes any DHCP reservations for this
	 * host if any are there.
	 * 
	 * @param cmd
	 * @param context
	 */
	public void expandDeleteHostCmd(DeleteHostUICmd cmd,
			BaseCompositeCommand context) {
		Set<DHCPReservation> current_res = new HashSet<DHCPReservation>();
		current_res.addAll(getExistingReservations(cmd.getHost()));
		DHCPCmd dhcpCmd = new DHCPCmd();
		for (DHCPReservation r : current_res) {
			dhcpCmd.insertDel(r);
		}
		if (dhcpCmd.getChanges().size() != 0) {
			context.getCommands().add(dhcpCmd);
		}
	}

	/**
	 * Expands a host modification command with DHCP changes
	 * 
	 * @param cmd
	 *            the UICommand (either an {@link AlterHostCmd} or
	 *            {@link DeleteHostUICmd}
	 * @param result
	 *            the composite command to fill
	 */
	public void expandCmd(UICommand cmd, BaseCompositeCommand result) {
		if (cmd instanceof AlterHostCmd)
			expandAlterHostCmd((AlterHostCmd) cmd, result);
		else if (cmd instanceof DeleteHostUICmd)
			expandDeleteHostCmd((DeleteHostUICmd) cmd, result);
	}

	/***************************************************************************
	 * adds {@link DHCPHostCommandFlags} to alter host commands and Bulk
	 * create/edit host commands
	 * 
	 * @param cmd
	 *            the command to set up
	 */
	public void setupCommand(UICommand cmd) {
		DHCPHostCommandFlags flags = new DHCPHostCommandFlags();
		if ((cmd instanceof AlterHostCmd || cmd instanceof BulkCreateEditHostsCmd)
				&& securityHelper.isAdmin()) {

			cmd.getOptionData().put(DHCPHostCommandFlags.DHCP_FLAGS_KEY, flags);
		}

	}

	public boolean supports(Class clazz) {
		return canExpand(clazz);
	}

	public boolean canExpand(Class clazz) {
		return AlterHostCmd.class.isAssignableFrom(clazz)
				|| DeleteHostUICmd.class.isAssignableFrom(clazz)
				|| BulkCreateEditHostsCmd.class.isAssignableFrom(clazz);
	}

	public DHCPHostCommandFlags getActiveFlags(AlterHostCmd cmd) {
		DHCPHostCommandFlags flags = (DHCPHostCommandFlags) cmd.getOptionData()
				.get(DHCPHostCommandFlags.DHCP_FLAGS_KEY);

		if (flags == null) {
			flags = new DHCPHostCommandFlags();
		}
		return flags;
	}

	/**
	 * Should we proceed with DHCP modifications
	 * 
	 * @param existing
	 *            host (if it exists)
	 * @param cmd
	 *            the command to check
	 * @return true if DHCP should continue for this host
	 */
	public boolean doEditDHCP(Host existing, AlterHostCmd cmd) {
		Host host = cmd.getNewHost();
		return getActiveFlags(cmd).isRefreshDHCP()
				|| !TypeUtils.nullSafeCompare(existing.getHostClass(), host
						.getHostClass())
				|| !TypeUtils.nullSafeCompare(existing.getHostName(), host
						.getHostName())
				|| !TypeUtils.nullSafeCompare(existing.getIpAddress(), host
						.getIpAddress())
				|| !TypeUtils.nullSafeCompare(existing.getMacAddress(), host
						.getMacAddress());
	}

	/**
	 * Performs Validation for a host command, ensures that a MAC address is
	 * specified if the host class requires DHCP, and also ensures that a DHCP
	 * scope exists on the subnet of the reservations
	 * 
	 * @param cmd
	 * @param errors
	 */
	public void validateAlterHostCmd(AlterHostCmd cmd, Errors errors) {
		Host existing = null;
		if (!cmd.isCreation()) {
			existing = hostsDAO.loadHost(cmd.getNewHost().getId());
		}

		if ((existing == null || doEditDHCP(existing, cmd))
				&& cmd.getNewHost().getHostClass().getDHCPStatus().equals(
						DHCP_STATUS.REQUIRED)) {
			if (!cmd.isSpecifyIp()) {
				Subnet s = cmd.getSubnet();
				List<DHCPScope> scopes = dhcpDAO.getScopesMatchingRange(s);
				if (scopes.size() == 0) {
					errors
							.rejectValue("subnet", "no-scope-with-dhcp-found",
									"Host class required DHCP  but no scopes are configured on this subnet");
				}
			}

		}
	}

	public void validate(Object target, Errors errors) {
		if (target instanceof AlterHostCmd)
			validateAlterHostCmd((AlterHostCmd) target, errors);
	}

	@Required
	public void setDhcpDAO(DHCPDao dhcpDAO) {
		this.dhcpDAO = dhcpDAO;
	}

	@Required
	public void setHostsDAO(NetDAO hostsDAO) {
		this.hostsDAO = hostsDAO;
	}

	public int getOrder() {
		return 100;
	}

	@Required
	public void setSecurityHelper(SecurityHelper securityHelper) {
		this.securityHelper = securityHelper;
	}
}
