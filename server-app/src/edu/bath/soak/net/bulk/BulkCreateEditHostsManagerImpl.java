package edu.bath.soak.net.bulk;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import edu.bath.soak.cmd.BaseCompositeCommand;
import edu.bath.soak.cmd.CommandDispatcherRegistry;
import edu.bath.soak.cmd.CommandExpander;
import edu.bath.soak.mgr.AddressSpaceManager;
import edu.bath.soak.mgr.AddressSpaceManager.AddressInUseException;
import edu.bath.soak.net.cmd.AlterHostCmd;
import edu.bath.soak.net.cmd.BulkCreateEditHostsCmd;
import edu.bath.soak.net.model.Host;
import edu.bath.soak.net.model.NetDAO;
import edu.bath.soak.net.model.Subnet;
import edu.bath.soak.security.SecurityHelper;
import edu.bath.soak.web.BeanView;
import edu.bath.soak.web.bulk.BulkSetHostDetailsCmd;

/**
 * Manager/controller class for implementing bulk alteration of basic host
 * details of hosts.
 * 
 * 
 * @author cspocc
 * 
 */
public class BulkCreateEditHostsManagerImpl extends
		BulkAlterHostCmdValidator<BulkCreateEditHostsCmd> implements
		CommandExpander<BulkCreateEditHostsCmd>, Validator {
	NetDAO hostsDAO;
	SecurityHelper securityHelper;
	Logger log = Logger.getLogger(BulkCreateEditHostsManagerImpl.class);
	AddressSpaceManager addressSpaceManager;
	CommandDispatcherRegistry commandDispatcherRegistry;

	/**
	 * Expands the host alteration command
	 * 
	 * @param cmd
	 *            the command to expand
	 * @param result
	 *            the resultant base command to execute
	 */
	public void expandCmd(BulkCreateEditHostsCmd cmd,
			BaseCompositeCommand result) {
		log.trace("Expanding bulk host creation command" + cmd);
		for (Host h : cmd.getHosts()) {
			commandDispatcherRegistry.expandSubCommand(expandIntoAlterHostCmd(
					cmd, h), result);
		}
	}

	/**
	 * Expands a {@link BulkCreateEditHostsCmd} into an individual
	 * {@link AlterHostCmd}
	 * 
	 * copies host data into the new command and copies any host flags into this
	 * command
	 * 
	 * @param command
	 *            the command to expand
	 * @param host
	 *            the host to apply this command to
	 * @return a new {@link AlterHostCmd} which performs the bulk edits for the
	 *         specified host
	 */
	public AlterHostCmd expandIntoAlterHostCmd(BulkCreateEditHostsCmd command,
			Host host) {

		AlterHostCmd cmd = new AlterHostCmd();
		cmd.setNewHost(host);
		cmd.setSpecifyIp(true);
		cmd.getOptionData().putAll(command.getOptionData());
		return cmd;
	}

	/***************************************************************************
	 * Fills host details with adddresses assigned from the pool
	 * 
	 * N.b. this will not fail if an
	 * 
	 * @param command
	 * 
	 * @param clearIP
	 *            should existing IP addresses be removed, if set to false then
	 *            only hosts with null addresses are allocated.
	 * @param s
	 */
	public void assignAddresses(BulkCreateEditHostsCmd command, Subnet s,
			boolean clearIP) {
		Assert.notNull(s);
		for (Host h : command.getHosts()) {
			if (h.getIpAddress() != null) {
				try {
					addressSpaceManager.preAllocateIPAddress(h, h
							.getIpAddress());
				} catch (AddressInUseException e) {

				}
			}
		}
		for (Host h : command.getHosts()) {
			if (h.getIpAddress() == null || clearIP) {
				h.setIpAddress(addressSpaceManager.allocateIPAddress(h, s));
			}
		}

	}

	@Override
	public void validate(Object target, Errors errors) {

		super.validate(target, errors);
	}

	public boolean canExpand(Class clazz) {
		return BulkCreateEditHostsCmd.class.isAssignableFrom(clazz);
	}

	public void setupCommand(BulkCreateEditHostsCmd cmd) {

	}

	public boolean supports(Class clazz) {
		return BulkCreateEditHostsCmd.class.isAssignableFrom(clazz);
	}

	public boolean supportsCmdType(Class type) {
		return BulkSetHostDetailsCmd.class.isAssignableFrom(type);
	}

	public int getOrder() {
		return 0;
	}

	@Required
	public void setCommandDispatcherRegistry(
			CommandDispatcherRegistry commandDispatcherRegistry) {
		this.commandDispatcherRegistry = commandDispatcherRegistry;
	}

	@Required
	public void setSecurityHelper(SecurityHelper securityHelper) {
		this.securityHelper = securityHelper;
	}

	@Required
	public void setHostsDAO(NetDAO hostsDAO) {
		this.hostsDAO = hostsDAO;
	}

	@Required
	public void setAddressSpaceManager(AddressSpaceManager addressSpaceManger) {
		this.addressSpaceManager = addressSpaceManger;
	}

}
