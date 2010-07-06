package edu.bath.soak.dnseditor;

import org.springframework.util.Assert;

import edu.bath.soak.cmd.BaseCompositeCommand;
import edu.bath.soak.cmd.CmdException;
import edu.bath.soak.cmd.CommandExpander;
import edu.bath.soak.cmd.UICommand;
import edu.bath.soak.dns.cmd.DNSCmd;
import edu.bath.soak.dnseditor.cmd.UIDNSChange;
import edu.bath.soak.dnseditor.cmd.ManualDNSCommand;

/**
 * Manager which implements Manual DNS commands
 * 
 * @author cspocc
 * 
 */
public class DNSEditorManager implements CommandExpander {

	public boolean canExpand(Class clazz) {

		return clazz.isAssignableFrom(ManualDNSCommand.class);
	}

	/**
	 * Expands {@link ManualDNSCommand} objects in to {@link DNSCmd} objects
	 */
	public void expandCmd(UICommand cmd, BaseCompositeCommand result)
			throws CmdException {
		Assert.notNull(cmd);
		Assert.notNull(result);
		Assert.isAssignable(ManualDNSCommand.class, cmd.getClass());

		ManualDNSCommand dnscmd = (ManualDNSCommand) cmd;
		DNSCmd dnsCommand = new DNSCmd();
		for (UIDNSChange edit : dnscmd.getEdits()) {
			edit.fillCmd(dnsCommand);
		}

		result.appendCommand(dnsCommand);

	}

	public void setupCommand(UICommand cmd) {
		// TODO Auto-generated method stub

	}

	public int getOrder() {
		// TODO Auto-generated method stub
		return 0;
	}

}
