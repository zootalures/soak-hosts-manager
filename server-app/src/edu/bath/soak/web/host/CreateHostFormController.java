package edu.bath.soak.web.host;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import javax.persistence.PersistenceContext;

import org.apache.commons.collections.FactoryUtils;
import org.apache.commons.collections.list.LazyList;
import org.apache.log4j.Logger;
import org.springframework.beans.PropertyEditorRegistrar;
import org.springframework.beans.PropertyEditorRegistry;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.validation.DataBinder;
import org.springframework.webflow.action.FormAction;
import org.springframework.webflow.core.collection.MutableAttributeMap;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;

import edu.bath.soak.cmd.BaseCompositeCommand;
import edu.bath.soak.cmd.CommandDispatcherRegistry;
import edu.bath.soak.cmd.RenderableCommandOption;
import edu.bath.soak.mgr.HostsManager;
import edu.bath.soak.model.OrgUnit;
import edu.bath.soak.net.cmd.AlterHostCmd;
import edu.bath.soak.net.model.Host;
import edu.bath.soak.net.model.HostAlias;
import edu.bath.soak.net.model.HostChange;
import edu.bath.soak.net.model.HostClass;
import edu.bath.soak.net.model.NameDomain;
import edu.bath.soak.net.model.NetDAO;
import edu.bath.soak.net.model.Subnet;
import edu.bath.soak.security.SecurityHelper;

/**
 * Form controller for creating and editing hosts as part of a flow
 * 
 * @author cspocc
 * 
 */
public class CreateHostFormController extends FormAction {
	NetDAO hostsDAO;
	HostsManager hostsMgr;
	PropertyEditorRegistrar customEditorRegistrar;
	Logger log = Logger.getLogger(CreateHostFormController.class);
	SecurityHelper securityHelper;
	CommandDispatcherRegistry commandDispatcherRegistry;

	public CreateHostFormController() {
		setFormObjectName("createHostCmd");
	}

	/**
	 * Initialises the backing form command
	 */
	@Override
	protected Object createFormObject(RequestContext context) throws Exception {
		log.trace("createFormObject called");

		AlterHostCmd cmd = new AlterHostCmd();
		Long hostId = null;
		Long restoreID = null;
		if (null != (hostId = (Long) context.getFlowScope().get("hostId"))) {
			Host h;
			if (null != (restoreID = (Long) context.getFlowScope().get(
					"restoreId"))) {

				HostChange change = hostsDAO.getHostChange(restoreID);
				Assert.notNull(change.getHost(),
						"Host change does not have an associated state");
				Host original = hostsDAO.loadHost(hostId);
				Assert.notNull(original, "unable to find host with ID "
						+ hostId);
				Assert.notNull(change, "Unable to find host change with ID"
						+ change.getId());
				h = change.getHost();

			} else {
				log.debug("Host edit detected, editing host " + hostId);
				h = hostsDAO.getHostForEditing(hostId);
			}
			cmd.setSpecifyIp(true);
			cmd.setNewHost(h);
		} else {
			Long subnetId = null;

			if (null != (subnetId = (Long) context.getFlowScope().getLong(
					"subnetId"))) {
				Subnet s = hostsDAO.getSubnet(subnetId);
				log.debug("creating host on given subnet " + s);

				cmd.setSpecifyIp(false);
				cmd.setSubnet(s);
			}
			cmd.setNewHost(new Host());
		}

		List aliases = LazyList.decorate(cmd.getNewHost().getHostAliases(),
				FactoryUtils.instantiateFactory(HostAlias.class));
		cmd.getNewHost().setHostAliases(aliases);
		if (!cmd.isSpecifyIp()) {
			cmd.getNewHost().setIpAddress(null);
		}
		// set up the initial tab
		cmd.getOptionData().put("gui.lastTab", 0);
		commandDispatcherRegistry.setUpCommandDefaults(cmd);
		return cmd;
	}

	@Override
	protected void doBind(RequestContext context, DataBinder binder)
			throws Exception {
		AlterHostCmd cmd = (AlterHostCmd) getFormObject(context);
		super.doBind(context, binder);
		List<HostAlias> aliases = new ArrayList<HostAlias>();
		int idx = 0;
		for (HostAlias h : cmd.getNewHost().getHostAliases()) {
			if (null != h.getAlias()
					&& StringUtils.hasText(h.getAlias().getName())) {
				h.setIdx(idx++);

				h.setHost(cmd.getNewHost());
				aliases.add(h);

			}
		}
		List<HostAlias> hostaliases = LazyList.decorate(aliases, FactoryUtils
				.instantiateFactory(HostAlias.class));
		log.debug("Aliases now has size " + hostaliases.size());
		cmd.getNewHost().setHostAliases(hostaliases);
	}

	public Event checkPermissions(RequestContext context) {
		Long hostId = null;

		if (null != (hostId = (Long) context.getFlowScope().get("hostId"))) {
			Host h = hostsDAO.loadHost(hostId);
			if (null != h) {
				if (securityHelper.canEdit(h.getOwnership())) {
					return success();
				} else {
					return result("failure");
				}
			} else {
				throw new IllegalArgumentException("Host not found");

			}
		} else {
			throw new IllegalArgumentException("Host not found");
		}

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

	/**
	 * Populates request attributes with suplemental view data for form for an
	 * edit
	 * 
	 * @param context
	 * @return
	 * @throws Exception
	 */
	public Event setupEditReferenceData(RequestContext context)
			throws Exception {
		log.trace("setupReferenceData called");
		AlterHostCmd cmd = (AlterHostCmd) getFormObject(context);

		Assert.notNull(cmd);
		// start with collections of objects which user can add to
		Collection<HostClass> permittedHostClasses = securityHelper
				.filterAllowedEntitiesForUser(hostsDAO.getHostClasses());
		Collection<Subnet> permittedSubnets = securityHelper
				.filterAllowedEntitiesForUser(hostsDAO.getSubnets());
		Collection<NameDomain> permittedNameDomains = securityHelper
				.filterAllowedEntitiesForUser(hostsDAO.getNameDomains());

		List<HostClass> allowedHostClasses = new ArrayList<HostClass>();
		// Filter out any network classes which don't have any permissible
		// subnets or name domains
		for (HostClass hc : permittedHostClasses) {
			boolean hasAllowedNDs = false;
			boolean hasAllowedSNs = false;
			ndLoop: for (NameDomain nd : permittedNameDomains) {
				if (nd.getAllowedClasses().contains(hc)) {
					hasAllowedNDs = true;
					break ndLoop;
				}
			}

			for (Subnet s : permittedSubnets) {
				if (s.getAllowedHostClasses().contains(hc)) {
					hasAllowedSNs = true;
				}
			}
			if (hasAllowedSNs && hasAllowedNDs)
				allowedHostClasses.add(hc);
		}

		MutableAttributeMap requestScope = context.getRequestScope();

		List<NameDomain> allowedNameDomains = new ArrayList<NameDomain>();
		HostClass hc = cmd.getNewHost().getHostClass();
		Assert.notNull(hc);

		for (NameDomain nd : permittedNameDomains) {
			if (nd.getAllowedClasses().contains(hc))
				allowedNameDomains.add(nd);
		}
		List<Subnet> allowedSubnets = new ArrayList<Subnet>();
		for (Subnet s : permittedSubnets) {
			if (s.getAllowedHostClasses().contains(hc))
				allowedSubnets.add(s);
		}

		// Cleaning up for preserving existing types
		// We allow hosts to remain as un-creatable types
		if (!cmd.isCreation()) {
			Host existingHost = hostsDAO.loadHost(cmd.getNewHost().getId());

			if (!allowedHostClasses.contains(existingHost.getHostClass())) {
				allowedHostClasses.add(existingHost.getHostClass());
			}

			// Unless we have changed the host class, we permit an edited host
			// to retain a (possibly illegal) name domain

			if (existingHost.getHostClass().equals(
					cmd.getNewHost().getHostClass())
					&& !allowedNameDomains.contains(existingHost.getHostName()
							.getDomain())) {
				allowedNameDomains.add(existingHost.getHostName().getDomain());
			}
		}

		Collection<OrgUnit> allowedOrgUnits;
		if (securityHelper.isAdmin()) {
			ArrayList<OrgUnit> ous = new ArrayList<OrgUnit>();
			ous.addAll(securityHelper.getAllowedOrgUnitsForCurrentUser());
			Collections.sort(ous);
			for (OrgUnit ou : hostsDAO.getOrgUnits()) {
				if (!ous.contains(ou)) {
					ous.add(ou);
				}
			}
			allowedOrgUnits = ous;
		} else {
			ArrayList<OrgUnit> ous = new ArrayList<OrgUnit>();
			ous.addAll(securityHelper.getAllowedOrgUnitsForCurrentUser());
			Collections.sort(ous);

			allowedOrgUnits = ous;
		}

		requestScope.put("allowedOUs", allowedOrgUnits);
		requestScope.put("hostClasses", allowedHostClasses);

		requestScope.put("hostAliasTypes", HostAlias.HostAliasType.values());

		requestScope.put("domains", allowedNameDomains);
		requestScope.put("subnets", allowedSubnets);
		HashMap<Object, RenderableCommandOption> renderableOptions = new HashMap<Object, RenderableCommandOption>();
		for (Entry<Object, Object> option : cmd.getOptionData().entrySet()) {
			if (option.getValue() instanceof RenderableCommandOption) {
				renderableOptions.put(option.getKey(),
						(RenderableCommandOption) option.getValue());
			}
		}
		requestScope.put("renderableOptions", renderableOptions);
		boolean showAliases = securityHelper.isAdmin()
				|| cmd.getNewHost().getHostClass().getCanHaveAliases()
				|| cmd.getNewHost().getHostAliases().size() > 0;
		requestScope.put("showAliases", showAliases);

		return success();
	}

	/**
	 * Sets up request attributes assuming that the host class has already been
	 * specified
	 * 
	 * @param context
	 * @return
	 * @throws Exception
	 */
	public Event setupCreateReferenceData(RequestContext context)
			throws Exception {

		// start with collections of objects which user can add to
		Collection<HostClass> permittedHostClasses = securityHelper
				.filterAllowedEntitiesForUser(hostsDAO.getHostClasses());
		Collection<Subnet> permittedSubnets = securityHelper
				.filterAllowedEntitiesForUser(hostsDAO.getSubnets());
		Collection<NameDomain> permittedNameDomains = securityHelper
				.filterAllowedEntitiesForUser(hostsDAO.getNameDomains());

		List<HostClass> allowedHostClasses = new ArrayList<HostClass>();
		// Filter out any network classes which don't have any permissible
		// subnets or name domains
		for (HostClass hc : permittedHostClasses) {
			boolean hasAllowedNDs = false;
			boolean hasAllowedSNs = false;
			ndLoop: for (NameDomain nd : permittedNameDomains) {
				if (nd.getAllowedClasses().contains(hc)) {
					hasAllowedNDs = true;
					break ndLoop;
				}
			}

			for (Subnet s : permittedSubnets) {
				if (s.getAllowedHostClasses().contains(hc)) {
					hasAllowedSNs = true;
				}
			}
			if (hasAllowedSNs && hasAllowedNDs)
				allowedHostClasses.add(hc);
		}

		MutableAttributeMap requestScope = context.getRequestScope();

		requestScope.put("hostClasses", allowedHostClasses);
		return success();
	}

	@Override
	protected void registerPropertyEditors(PropertyEditorRegistry registry) {
		customEditorRegistrar.registerCustomEditors(registry);
	}

	/**
	 * Calculates the change set for this action and fills the changes into the
	 * command object
	 * 
	 * @param context
	 * @return
	 * @throws Exception
	 */
	public Event calculateChanges(RequestContext context) throws Exception {
		AlterHostCmd cmd = (AlterHostCmd) getFormObject(context);
		log.trace("computing changes for host");

		BaseCompositeCommand result = commandDispatcherRegistry
				.expandCommand(cmd);
		MutableAttributeMap requestScope = context.getRequestScope();
		requestScope.put("changeResult", result);
		log.trace("got changes" + result);
		return success();
	}

	/**
	 * Re-populates the changes and applies them
	 * 
	 * @param context
	 * @return
	 * @throws Exception
	 */
	public Event applyChanges(RequestContext context) throws Exception {
		AlterHostCmd cmd = (AlterHostCmd) getFormObject(context);
		log.trace("applying changes for host " + cmd.getNewHost() + " with ID "
				+ cmd.getNewHost().getId());
		BaseCompositeCommand result = commandDispatcherRegistry
				.expandAndImplementCommand(cmd);
		MutableAttributeMap requestScope = context.getRequestScope();
		requestScope.put("changeResult", result);
		return success();
	}

	@Required
	public void setHostsDAO(NetDAO hostsDao) {
		this.hostsDAO = hostsDao;
	}

	@Required
	public void setCustomEditorRegistrar(
			PropertyEditorRegistrar customEditorRegistrar) {
		this.customEditorRegistrar = customEditorRegistrar;
	}

	@Required
	public void setHostsMgr(HostsManager hostsMgr) {
		this.hostsMgr = hostsMgr;
	}

	@Required
	public void setSecurityHelper(SecurityHelper securityHelper) {
		this.securityHelper = securityHelper;
	}

	@Required
	public void setCommandDispatcherRegistry(
			CommandDispatcherRegistry commandDispatcherRegistry) {
		this.commandDispatcherRegistry = commandDispatcherRegistry;
	}
}
