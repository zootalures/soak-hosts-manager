package edu.bath.soak.net.cmd;

import java.net.Inet4Address;
import java.util.ArrayList;
import java.util.List;

import edu.bath.soak.cmd.AbstractUICmdImpl;
import edu.bath.soak.net.model.Host;
import edu.bath.soak.web.bulk.BulkSetHostDetailsCmd;

/**
 * Base class for commands which alter more than one host
 * 
 * @see {@link BulkAlterHostCmd } {@link BulkCreateEditHostsCmd}
 *      {@link BulkSetHostDetailsCmd} {@link BulkMoveHostsCmd}
 * 
 * 
 * @author cspocc
 * 
 */
public abstract class BulkAlterHostCmd extends AbstractUICmdImpl {

	protected List<Host> hosts = new ArrayList<Host>();

	public Inet4Address selectedAddressForHost(Host h) {
		return h.getIpAddress();
	}

	public BulkAlterHostCmd() {
		super();
	}

	public List<Host> getHosts() {
		return hosts;
	}

	public void setHosts(List<Host> hosts) {
		this.hosts = hosts;
	}

}