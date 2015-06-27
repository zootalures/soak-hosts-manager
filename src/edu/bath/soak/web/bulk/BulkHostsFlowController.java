package edu.bath.soak.web.bulk;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.webflow.action.MultiAction;
import org.springframework.webflow.core.collection.MutableAttributeMap;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;

import edu.bath.soak.DataSourceRegistry;
import edu.bath.soak.cmd.BaseCompositeCommand;
import edu.bath.soak.cmd.CommandDispatcherRegistry;
import edu.bath.soak.cmd.UICommand;
import edu.bath.soak.mgr.StarredHostsManager;
import edu.bath.soak.mgr.AddressSpaceManager.AddressSpaceFullException;
import edu.bath.soak.model.HostDataSource;
import edu.bath.soak.net.CSVParser;
import edu.bath.soak.net.bulk.BulkCreateEditHostsManagerImpl;
import edu.bath.soak.net.bulk.BulkMoveHostsManagerImpl;
import edu.bath.soak.net.cmd.BulkAlterHostCmd;
import edu.bath.soak.net.cmd.BulkCreateEditHostsCmd;
import edu.bath.soak.net.cmd.BulkDeleteHostCmd;
import edu.bath.soak.net.cmd.BulkMoveHostsCmd;
import edu.bath.soak.net.model.Host;
import edu.bath.soak.net.model.HostClass;
import edu.bath.soak.net.model.NameDomain;
import edu.bath.soak.net.model.NetDAO;
import edu.bath.soak.net.model.Subnet;
import edu.bath.soak.propertyeditors.HostNameEditor;
import edu.bath.soak.security.SecurityHelper;

/*******************************************************************************
 * Controller for bulk actions on hosts
 * 
 * 
 * @author cspocc
 * 
 ******************************************************************************/
public class BulkHostsFlowController extends MultiAction {
	CommandDispatcherRegistry commandDispatcherRegistry;
	SecurityHelper securityHelper;
	NetDAO hostsDAO;
	StarredHostsManager starredHostsManager;
	String selectedHostsAttribute = "selectedHosts";
	String commandAttribute = "command";
	BulkMoveHostsManagerImpl bulkMoveHostsManger;
	BulkCreateEditHostsManagerImpl bulkCreateEditHostsManager;
	DataSourceRegistry dataSourceRegistry;
	
	public BulkHostsFlowController() {
	}

	/***************************************************************************
	 * Extracts a collection of selected hosts from the selectedHostsAttribute
	 * scope attribute
	 * 
	 * @param context
	 * @return
	 **************************************************************************/
	public Collection<Host> getSelectedHosts(RequestContext context) {
		return (Collection<Host>) context.getFlowScope().getCollection(
				selectedHostsAttribute);
	}
	
	public Event canCreate(RequestContext context) {

		Collection<HostClass> permittedHostClasses = securityHelper
				.filterAllowedEntitiesForUser(hostsDAO.getHostClasses());
		Collection<Subnet> permittedSubnets = securityHelper
				.filterAllowedEntitiesForUser(hostsDAO.getSubnets());
		Collection<NameDomain> permittedNameDomains = securityHelper
				.filterAllowedEntitiesForUser(hostsDAO.getNameDomains());
		if (permittedHostClasses.isEmpty() || permittedSubnets.isEmpty()
				|| permittedNameDomains.isEmpty()) {
			return result("permissionDenied");
		}
		return success();
	}

	public <T extends BulkAlterHostCmd> T getCommand(Class<T> type,
			RequestContext context) {
		Assert.notNull(context.getFlowScope().get(commandAttribute),
				"no command found in attribute " + commandAttribute);
		Assert.isInstanceOf(type, context.getFlowScope().get(commandAttribute),
				"command in attribute " + commandAttribute
						+ " is not a bulk create host command");
		return (T) context.getFlowScope().get(commandAttribute);
	}

	/**
	 * Extracts current starred hostts from the starred hosts controller and
	 * places them into the flow
	 * 
	 * @param context
	 * @return
	 * @throws Exception
	 */
	public Event getStarredHosts(RequestContext context) throws Exception {
		logger.trace("Getting starred hosts from starred hosts controller");
		Collection<Host> hosts = starredHostsManager.getStarredHosts();
		logger.trace("found " + hosts.size() + " hosts");

		if (hosts.size() == 0) {
			return new Event(this, "noStarredHosts");
		} else {
			context.getFlowScope().put(selectedHostsAttribute, hosts);
			return success();
		}
	}

	/***************************************************************************
	 * Sets up the bulk move command, taking hosts from the
	 * selectedHostsAttribute flow scope attribute and placing the command in
	 * the commandAttribute attribute
	 * 
	 * @param context
	 * @return success
	 * @throws Exception
	 **************************************************************************/
	public Event setUpMoveCommand(RequestContext context) throws Exception {
		Collection<Host> hosts = getSelectedHosts(context);

		Assert.notEmpty(hosts);
		BulkMoveHostsCmd cmd = new BulkMoveHostsCmd();
		commandDispatcherRegistry.setUpCommandDefaults(cmd);

		cmd.getHosts().addAll(hosts);
		context.getFlowScope().put(commandAttribute, cmd);
		logger.trace("constructed move command with " + cmd.getHosts().size()
				+ " hosts ");
		return success();
	}

	/***************************************************************************
	 * Allocates addresses for each host and copies them into the command
	 * address map, these can then be edited
	 * 
	 * @param context
	 * @return
	 * @throws Exception
	 */
	public Event allocateMoveIpAddresses(RequestContext context)
			throws Exception {
		BulkMoveHostsCmd cmd = (BulkMoveHostsCmd) context.getFlowScope().get(
				commandAttribute);
		Assert.notNull(cmd);
		Assert.notNull(cmd.getNewSubnet());
		context.getFlowScope().put("subnetFull", false);

		try {
			bulkMoveHostsManger.assignAddresses(cmd);
		} catch (AddressSpaceFullException full) {
			context.getFlowScope().put("subnetFull", true);
			return new Event(this, "full");
		}

		return success();

	}

	/***************************************************************************
	 * Sets up the bulk deletion command, taking hosts from the
	 * selectedHostsAttribute flow scope attribute and placing the command in
	 * the commandAttribute attribute
	 * 
	 * @param context
	 * @return success
	 * @throws Exception
	 **************************************************************************/
	public Event setUpDeleteCommand(RequestContext context) throws Exception {
		Collection<Host> hosts = getSelectedHosts(context);
		Assert.notEmpty(hosts);
		BulkDeleteHostCmd cmd = new BulkDeleteHostCmd();
		cmd.getHosts().addAll(hosts);
		context.getFlowScope().put(commandAttribute, cmd);
		logger.trace("constructed delete command with " + cmd.getHosts().size()
				+ " hosts ");
		return success();
	}

	/**
	 * Performs IP address asignment based on the IP address command stored in
	 * the "setIPsCmd" attribute
	 * 
	 * @param context
	 * @return
	 */
	public Event assignIPAddresses(RequestContext context) {
		BulkCreateEditHostsCmd cmd = getCommand(BulkCreateEditHostsCmd.class,
				context);
		BulkSetIPsCmd setIPsCmd = (BulkSetIPsCmd) context.getFlowScope().get(
				"setIPsCmd");
		Assert.notNull(setIPsCmd);
		Assert.notNull(setIPsCmd.getNewSubnet());

		try {
			if (cmd.anyNeedIps() || setIPsCmd.isClearIPs())
				bulkCreateEditHostsManager.assignAddresses(cmd, setIPsCmd
						.getNewSubnet(), setIPsCmd.isClearIPs());
		} catch (AddressSpaceFullException e) {
			context.getFlashScope().put("subnetFull", true);
			return result("subnetFull");
		}
		return success();
	}

	/**
	 * Applies a filter to all selected hosts using a filter specified by the
	 * "applyFilterCmd" attribute which must be a a BulkApplyFilterCmd object
	 * 
	 * @param context
	 * @return
	 */
	public Event applyFilter(RequestContext context) {
		BulkCreateEditHostsCmd cmd = getCommand(BulkCreateEditHostsCmd.class,
				context);
		BulkApplyFilterCmd applyFilterCmd = (BulkApplyFilterCmd) context
				.getFlowScope().get("applyFilterCmd");
		Assert.notNull(cmd);
		Assert.notNull(applyFilterCmd);
		Assert.notNull(applyFilterCmd.filter);

		HostDataSource filter = dataSourceRegistry.getDataSource(applyFilterCmd
				.getFilter());
		Assert.notNull(filter);
		for (Host h : cmd.getHosts()) {
			filter.fillInfoForHost(h, applyFilterCmd.isOverwrite());
		}

		return success();
	}

	/**
	 * applies the changes in a bulk-edit command to the current set of hosts.
	 * 
	 * @param context
	 * @return
	 */
	public Event applyBulkEditChanges(RequestContext context) {

		BulkSetHostDetailsCmd rootCmd = (BulkSetHostDetailsCmd) context
				.getFlowScope().get("setDetailsCmd");
		BulkCreateEditHostsCmd editCmd = (BulkCreateEditHostsCmd) context
				.getFlowScope().get(commandAttribute);
		Assert.notNull(rootCmd);
		Assert.notNull(editCmd);

		for (Host host : editCmd.getHosts()) {
			if (null != rootCmd.getNewHostClass()) {
				host.setHostClass(rootCmd.getNewHostClass());
			}

			if (null != rootCmd.getNewNameDomain()) {
				host.getHostName().setDomain(rootCmd.getNewNameDomain());
			}
			if (rootCmd.isDoChangeBuilding()) {
				host.getLocation().setBuilding(rootCmd.getNewHostBuilding());
			}
			if (rootCmd.isDoChangeRoom()) {
				host.getLocation().setRoom(rootCmd.getNewHostRoom());
			}

			if (null != rootCmd.getNewOrgUnit()) {
				host.getOwnership().setOrgUnit(rootCmd.getNewOrgUnit());
			}
		}
		return success();
	}

	/***************************************************************************
	 * Sets up a bulk edit command, taking hosts from selectedHostsAttribute
	 * flow scope attribute and placing the command in the commandAttribute
	 * attribute
	 * 
	 * @param context
	 * @return
	 * @throws Exception
	 */
	public Event setUpBulkEditCommand(RequestContext context) throws Exception {
		Collection<Host> hosts = getSelectedHosts(context);
		Assert.notEmpty(hosts);
		BulkCreateEditHostsCmd cmd = new BulkCreateEditHostsCmd();
		commandDispatcherRegistry.setUpCommandDefaults(cmd);
		if (null != context.getFlowScope().get("isCreation")) {
			cmd.setCreation(true);
			cmd.getHosts().addAll(hosts);
		} else {
			List<Host> gothosts = new ArrayList<Host>();

			for (Host h : hosts) {
				gothosts.add(hostsDAO.getHostForEditing(h.getId()));
			}
			Collections.sort(gothosts, new Comparator<Host>() {
				public int compare(Host o1, Host o2) {
					return o1.getHostName().compareTo(o2.getHostName());
				}
			});
			cmd.setHosts(gothosts);

		}
		context.getFlowScope().put("startIndex", 0);

		context.getFlowScope().put(commandAttribute, cmd);
		logger.trace("constructed edit command with " + cmd.getHosts().size()
				+ " hosts ");

		return success();
	}

	/***************************************************************************
	 * Sets up reference data for an edit form
	 * 
	 * @param context
	 * @return
	 * @throws Exception
	 **************************************************************************/
	public Event setupFormBackingDataForEdit(RequestContext context)
			throws Exception {
		MutableAttributeMap model = context.getRequestScope();
		model.put("hostClasses", securityHelper
				.filterAllowedEntitiesForUser(hostsDAO.getHostClasses()));
		model.put("nameDomains", securityHelper
				.filterAllowedEntitiesForUser(hostsDAO.getNameDomains()));

		model.put("subnets", securityHelper
				.filterAllowedEntitiesForUser(hostsDAO.getSubnets()));
		if (securityHelper.isAdmin()) {
			model.put("orgUnits", hostsDAO.getOrgUnits());
		} else {
			model.put("orgUnits", securityHelper
					.getAllowedOrgUnitsForCurrentUser());
		}
		model.put("dataSources", dataSourceRegistry.getDataSources());
		return success();
	}

	/***************************************************************************
	 * Sets up reference data for move subnet form form
	 * 
	 * @param context
	 * @return
	 * @throws Exception
	 **************************************************************************/
	public Event setupFormBackingDataForMove(RequestContext context)
			throws Exception {
		MutableAttributeMap model = context.getRequestScope();
		model.put("subnets", securityHelper
				.filterAllowedEntitiesForUser(hostsDAO.getSubnets()));
		return success();
	}

	/***************************************************************************
	 * Takes the current command object in the commandAttribute flow scope and
	 * generates a preview by expanding the command places result in the
	 * "preview" flow scope variable
	 * 
	 * @param context
	 * @return
	 * @throws Exception
	 **************************************************************************/
	public Event generatePreview(RequestContext context) throws Exception {
		UICommand cmd = (UICommand) context.getFlowScope()
				.get(commandAttribute);
		Assert.notNull(cmd, "Command object not found");
		logger.trace("About to preview command ");
		BaseCompositeCommand bcc = commandDispatcherRegistry.expandCommand(cmd);

		logger.trace("Got preview for " + bcc.getBaseChange() + " with "
				+ bcc.getAggregateChanges().size() + " consequences");
		context.getFlowScope().put("preview", bcc);
		return success();
	}

	// public Event checkPreview(RequestContext context) throws Exception {
	// BaseCompositeCommand bcc = (BaseCompositeCommand) context
	// .getFlowScope().get("preview");
	// return success();
	// }

	/***************************************************************************
	 * Takes the current command object and executes it , putting the result
	 * into the "result" flow scope variable
	 * 
	 * @param context
	 * @return
	 * @throws Exception
	 **************************************************************************/
	public Event runCommand(RequestContext context) throws Exception {
		UICommand cmd = (UICommand) context.getFlowScope()
				.get(commandAttribute);
		Assert.notNull(cmd, "Command object not found");
		logger.info("Executing bulk command " + cmd);
		BaseCompositeCommand bcc = commandDispatcherRegistry
				.expandAndImplementCommand(cmd);
		context.getFlowScope().put("result", bcc);

		return success();
	}

	/***************************************************************************
	 * Removes all hosts from the "selected" flow scope variable which the user
	 * does not have permission to edit
	 * 
	 * @param context
	 * @return
	 * @throws Exception
	 **************************************************************************/
	public Event filterHostsWithPermissions(RequestContext context)
			throws Exception {
		Collection<Host> hosts = getSelectedHosts(context);
		Assert.notEmpty(hosts, "No selected hosts specified");
		context.getFlowScope().put(selectedHostsAttribute,
				securityHelper.filterAllowedOwnedEntitiesForUser(hosts));
		return success();
	}

	/***************************************************************************
	 * Checks that he current user has write permission to all hosts stored in
	 * the selectedHostsAttribute flow scope varabble
	 * 
	 * @return "noPermission" if user has permissions to none of the hosts
	 * @return "mixedPermission" if user has permissions to some but not all
	 *         hosts
	 * @return "success" if user has permission to to all hosts
	 **************************************************************************/
	public Event checkPermissions(RequestContext context) {
		Collection<Host> hosts = (Collection<Host>) context.getFlowScope().get(
				selectedHostsAttribute);
		Assert.notEmpty(hosts, "No selected hosts specified");
		boolean gotBad = false;
		boolean gotGood = false;
		for (Host h : hosts) {
			if (securityHelper.canEdit(h.getOwnership())) {

				gotGood = true;
			} else {
				gotBad = true;
			}

		}
		if (gotBad && !gotGood)
			return new Event(this, "noPermissions");
		if (gotBad && gotGood)
			return new Event(this, "mixedPermissions");
		return new Event(this, "success");
	}

	public Event changePage(RequestContext context) {
		Integer start = context.getRequestParameters().getInteger("startIndex");
		context.getFlowScope().put("startIndex", start);
		return success();
	}

	public Event setupPaging(RequestContext context) {
		BulkAlterHostCmd cmd = (BulkAlterHostCmd) context.getFlowScope().get(
				commandAttribute);

		int nhosts = cmd.getHosts().size();
		int numperpage = 20;
		Integer start = context.getFlowScope().getInteger("startIndex");
		if (start == null)
			start = 0;

		context.getFlowScope().put("startIndex", start);
		context.getFlowScope().put("numPerPage", numperpage);

		Long pages[] = new Long[(nhosts / numperpage) + 1];

		if (nhosts == 0) {
			pages[0] = 0L;
		}
		for (int i = 0; i < (nhosts / numperpage) + 1; i++) {
			pages[i] = (long) i * numperpage;
		}
		context.getFlowScope().put("pages", pages);
		if (start > nhosts)
			start = 0;
		if (start < 0)
			start = 0;
		return success();
	}

	@Required
	public void setCommandDispatcherRegistry(
			CommandDispatcherRegistry commandDispatcherRegistry) {
		this.commandDispatcherRegistry = commandDispatcherRegistry;
	}

	@Required
	public void setStarredHostsManager(StarredHostsManager starredHostsManager) {
		this.starredHostsManager = starredHostsManager;
	}

	@Required
	public void setSecurityHelper(SecurityHelper securityHelper) {
		this.securityHelper = securityHelper;
	}

	@Required
	public void setHostsDAO(NetDAO hostsDAO) {
		this.hostsDAO = hostsDAO;
	}

	public String getCommandAttribute() {
		return commandAttribute;
	}

	public void setCommandAttribute(String commandAttribute) {
		this.commandAttribute = commandAttribute;
	}

	@Required
	public void setBulkMoveHostsManager(
			BulkMoveHostsManagerImpl bulkMoveHostsManger) {
		this.bulkMoveHostsManger = bulkMoveHostsManger;
	}

	@Required
	public void setBulkCreateEditHostsManager(
			BulkCreateEditHostsManagerImpl bulkCreateEditHostsManager) {
		this.bulkCreateEditHostsManager = bulkCreateEditHostsManager;
	}

	@Required
	public void setDataSourceRegistry(DataSourceRegistry dataSourceRegistry) {
		this.dataSourceRegistry = dataSourceRegistry;
	}

	

}
