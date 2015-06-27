package edu.bath.soak.dhcp;

import java.util.Comparator;
import java.util.Date;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

import edu.bath.soak.EventLog;
import edu.bath.soak.cmd.BaseCompositeCommand;
import edu.bath.soak.cmd.CmdException;
import edu.bath.soak.cmd.CmdValidationException;
import edu.bath.soak.dhcp.SyncedServiceEndPoint.DHCPScopeClientsUpdateInfo;
import edu.bath.soak.dhcp.cmd.DHCPChange;
import edu.bath.soak.dhcp.cmd.DHCPCmd;
import edu.bath.soak.dhcp.cmd.DHCPCmdValidator;
import edu.bath.soak.dhcp.model.DHCPDao;
import edu.bath.soak.dhcp.model.DHCPReservation;
import edu.bath.soak.dhcp.model.DHCPScope;
import edu.bath.soak.dhcp.model.DHCPServer;
import edu.bath.soak.dhcp.model.UpdateableDhcpServer;
import edu.bath.soak.undo.UndoNotSupportedException;

/**
 * Implements the {@link DHCPManager} interface for implementing {@link DHCPCmd}
 * commands.
 * 
 * 
 * @author cspocc
 * 
 */
public class DHCPManagerImpl implements DHCPManager {

	SortedSet<DHCPServiceEndPoint> endPoints = new TreeSet<DHCPServiceEndPoint>(
			new Comparator<DHCPServiceEndPoint>() {
				public int compare(DHCPServiceEndPoint arg0,
						DHCPServiceEndPoint arg1) {
					return ((Integer) arg0.getOrder()).compareTo(arg1
							.getOrder());
				}
			});
	DHCPDao dhcpDao;
	DHCPCmdValidator dhcpCmdValidator;
	Logger log = Logger.getLogger(DHCPManagerImpl.class);

	/***************************************************************************
	 * Inverts a DHCP command, replaces adds with deletes
	 * 
	 * @param cmd
	 *            the command to undo
	 * @param result
	 * @throws CmdException
	 */
	public void expandUndo(DHCPCmd cmd, BaseCompositeCommand result)
			throws CmdException {
		DHCPCmd undoCommand = new DHCPCmd();
		for (DHCPChange change : cmd.getChanges()) {
			DHCPReservation res = change.getReservation();
			if (null == dhcpDao.getScope(res.getScope().getId())) {
				throw new UndoNotSupportedException(cmd, "Scope "
						+ res.getScope().getName() + " no longer exists");
			}
			if (change.isAddition()) {
				undoCommand.insertDel(change.getReservation());
			} else {
				res.setId(null);
				undoCommand.insertAdd(res);
			}
		}

		if (undoCommand.getChanges().size() > 0) {
			Errors objectErrors = new BeanPropertyBindingResult(cmd, "cmd");
			ValidationUtils.invokeValidator(dhcpCmdValidator, undoCommand,
					objectErrors);

			if (objectErrors.hasErrors())
				throw new CmdValidationException(objectErrors);
			result.appendCommand(undoCommand);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void implementCmd(BaseCompositeCommand baseCommand, DHCPCmd cmd)
			throws CmdException {
		Errors objectErrors = new BeanPropertyBindingResult(cmd, "cmd");
		ValidationUtils.invokeValidator(dhcpCmdValidator, cmd, objectErrors);

		if (objectErrors.hasErrors())
			throw new CmdValidationException(objectErrors);

		// we do deletions first, then insertions for consistancy
		for (DHCPReservation dhcpchange : cmd.getDeletions()) {
			DHCPServer server = dhcpchange.getScope().getServer();

			DHCPServiceEndPoint ep = getEndpointForServer(server);
			Assert.notNull(ep);
			log.info("Deleting DHCP Reservation " + dhcpchange + " on server "
					+ server);

			ep.deleteReservation(server, dhcpchange);

		}

		for (DHCPReservation dhcpchange : cmd.getAdditions()) {
			DHCPServer server = dhcpchange.getScope().getServer();

			DHCPServiceEndPoint ep = getEndpointForServer(server);
			Assert.notNull(ep);
			log.info("Creating DHCP Reservation " + dhcpchange + " on server "
					+ server);

			ep.createReservation(server, dhcpchange);

		}

	}

	/**
	 * returns the endpoint which handles requests for a given server.
	 */
	public DHCPServiceEndPoint getEndpointForServer(DHCPServer server) {
		for (DHCPServiceEndPoint ep : endPoints) {
			if (ep.supportsServer(server)) {
				return ep;
			}
		}
		return null;
	}

	public boolean supportsCmdType(Class type) {
		return DHCPCmd.class.isAssignableFrom(type);
	}

	public void setEndPoints(Set<DHCPServiceEndPoint> endPoints) {
		this.endPoints.clear();
		this.endPoints.addAll(endPoints);
	}

	/***************************************************************************
	 * Updates all scopes on all udaptable DHCP servers which have not been
	 * updated since now - updateIntevalMs
	 * 
	 * @param updateIntervalMs
	 */
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void updateAllDhcpServers(long updateIntervalMs) {
		Date updateSince = new Date(System.currentTimeMillis()
				- updateIntervalMs);
		log.debug("DHCP Incremental update starting");
		int numServersUpdated = 0;
		int numScopesUpdated = 0;
		int numRecordsAdded = 0;
		int numRecordsDeleted = 0;

		for (DHCPServer server : dhcpDao.getDHCPServers()) {
			try {
				DHCPServiceEndPoint endPoint = getEndpointForServer(server);
				if (endPoint instanceof SyncedServiceEndPoint) {

					if (server instanceof UpdateableDhcpServer) {
						Date lastFetched = ((UpdateableDhcpServer) server)
								.getLastFetched();

						if (null == lastFetched
								|| new Date(lastFetched.getTime())
										.compareTo(updateSince) < 0) {
							log.info("Synchronizing scopes on server"
									+ server.getDisplayName());

							((SyncedServiceEndPoint) endPoint)
									.syncScopesInfo(server);
							numServersUpdated++;

						}
					}

					for (DHCPScope scope : dhcpDao.getDHCPScopes(server)) {
						Date fetchDate = scope.getFetchedOn();

						if (null == fetchDate
								|| new Date(fetchDate.getTime())
										.compareTo(updateSince) < 0) {
							numScopesUpdated++;

							log.info("Syncing scope " + scope + " on server "
									+ server.getDisplayName());
							DHCPScopeClientsUpdateInfo info = ((SyncedServiceEndPoint) endPoint)
									.syncScopeClients(server, scope);
							numRecordsAdded += info.getNumAdded();
							numRecordsDeleted += info.getNumDeleted();
						}
					}
				}
			} catch (Exception e) {
				EventLog.log().error("Error updating DHCP server " + server, e);
			}
		}
		EventLog
				.log()
				.info(
						String
								.format(
										"DHCP Plugin: Completed update of all DHCP servers, updated %d servers, %d scopes, added %d records, deleted %d records ",
										numServersUpdated, numScopesUpdated,
										numRecordsAdded, numRecordsDeleted));
	}

	/**
	 * Registers a new endpoint with the DHCP manager
	 */
	public void registerEndPoint(DHCPServiceEndPoint endPoint) {
		endPoints.add(endPoint);
	}

	@Required
	public void setDhcpDao(DHCPDao dhcpDao) {
		this.dhcpDao = dhcpDao;
	}

	public int getOrder() {
		return 0;
	}

	@Required
	public void setDhcpCmdValidator(DHCPCmdValidator dhcpCmdValidator) {
		this.dhcpCmdValidator = dhcpCmdValidator;
	}
}
