package edu.bath.soak.net.bulk;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;

import edu.bath.soak.cmd.BaseCompositeCommand;
import edu.bath.soak.cmd.CommandDispatcherRegistry;
import edu.bath.soak.cmd.CommandExpander;
import edu.bath.soak.net.cmd.BulkDeleteHostCmd;
import edu.bath.soak.net.cmd.DeleteHostUICmd;
import edu.bath.soak.net.model.Host;

/**
 * Manager/controller class for implementing bulk deletetion of hosts
 * 
 * @author cspocc
 * 
 */
public class BulkDeleteHostManagerImpl implements
		CommandExpander<BulkDeleteHostCmd> {

	CommandDispatcherRegistry commandDispatcherRegistry;

	public boolean supportsCmdType(Class type) {
		return BulkDeleteHostCmd.class.isAssignableFrom(type);
	}

	public int getOrder() {
		return 0;
	}

	public void expandCmd(BulkDeleteHostCmd cmd, BaseCompositeCommand result) {

		for (Host h : cmd.getHosts()) {
			Assert.notNull(h.getId());
			DeleteHostUICmd deleteHostCmd = new DeleteHostUICmd();
			deleteHostCmd.setHost(h);
			deleteHostCmd.setChangeComments(cmd.getChangeComments());

			// We expect the host command handler to insert the database
			// operations and DNS and DHCP command handlers to do the same.
			commandDispatcherRegistry.expandSubCommand(deleteHostCmd, result);
		}
	}

	public boolean canExpand(Class clazz) {
		return BulkDeleteHostCmd.class.isAssignableFrom(clazz);
	}

	public void setupCommand(BulkDeleteHostCmd cmd) {

	}

	@Required
	public void setCommandDispatcherRegistry(
			CommandDispatcherRegistry commandDispatcherRegistry) {
		this.commandDispatcherRegistry = commandDispatcherRegistry;
	}

}
