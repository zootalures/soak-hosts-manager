package edu.bath.soak;

import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.bind.annotation.XmlSeeAlso;

import edu.bath.soak.cmd.BaseCompositeCommand;
import edu.bath.soak.imprt.cmd.XMLImportData;
import edu.bath.soak.net.cmd.AlterHostCmd;
import edu.bath.soak.net.cmd.BulkCreateEditHostsCmd;
import edu.bath.soak.net.cmd.BulkDeleteHostCmd;
import edu.bath.soak.net.cmd.BulkMoveHostsCmd;
import edu.bath.soak.net.cmd.DeleteHostDBCmd;
import edu.bath.soak.net.cmd.DeleteHostUICmd;
import edu.bath.soak.net.cmd.SaveHostCmd;
import edu.bath.soak.net.model.Host;
import edu.bath.soak.net.model.HostAlias;
import edu.bath.soak.undo.cmd.UndoCmd;
import edu.bath.soak.web.bulk.BulkSetHostDetailsCmd;

@XmlRegistry
@XmlSeeAlso(value = { edu.bath.soak.dhcp.ObjectFactory.class,
		edu.bath.soak.dns.ObjectFactory.class })
public class ObjectFactory {
	public ObjectFactory() {
	}

	public Host createHost() {
		return new Host();

	}

	public HostAlias createHostAlias() {
		return new HostAlias();
	}

	public XMLImportData createXMLImportData() {
		return new XMLImportData();
	}

	public BaseCompositeCommand createBaseCompositeCommand() {
		return new BaseCompositeCommand();
	}

	public AlterHostCmd createAlterHostCmd() {
		return new AlterHostCmd();
	}

	public BulkSetHostDetailsCmd createBulkAlterHostDetailsCmd() {
		return new BulkSetHostDetailsCmd();
	}

	public BulkMoveHostsCmd createBulkMoveHostsCmd() {
		return new BulkMoveHostsCmd();
	}

	public BulkDeleteHostCmd createBulkDeleteHostCmd() {
		return new BulkDeleteHostCmd();
	}

	public BulkCreateEditHostsCmd createBulkCreateHostCmd() {
		return new BulkCreateEditHostsCmd();
	}

	public SaveHostCmd createSaveHostCmd() {
		return new SaveHostCmd();
	}

	public DeleteHostDBCmd createDeleteHostCmd() {
		return new DeleteHostDBCmd();
	}

	public DeleteHostUICmd createDeleteHostUICmd() {
		return new DeleteHostUICmd();
	}

	public UndoCmd createUndoCmd() {
		return new UndoCmd();
	}

}
