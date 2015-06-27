package edu.bath.soak.dnseditor.cmd;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import edu.bath.soak.cmd.AbstractUICmdImpl;

public class ManualDNSCommand extends AbstractUICmdImpl implements Serializable {

	List<UIDNSChange> edits = new ArrayList<UIDNSChange>();

	public String getCommandDescription() {
		return "Manual edit of DNS records";
	}

	public List<UIDNSChange> getEdits() {
		return edits;
	}

	public void setEdits(List<UIDNSChange> edits) {
		this.edits = edits;
	}

	public void addEdit(UIDNSChange edit) {
		edits.add(edit);
	}
}
