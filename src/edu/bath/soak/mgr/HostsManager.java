package edu.bath.soak.mgr;

import edu.bath.soak.cmd.CommandExpander;
import edu.bath.soak.cmd.CommandProcessor;
import edu.bath.soak.cmd.UICommand;
import edu.bath.soak.net.cmd.HostsDBCommand;

public interface HostsManager extends
		CommandExpander<UICommand>,
		CommandProcessor<HostsDBCommand> {

}
