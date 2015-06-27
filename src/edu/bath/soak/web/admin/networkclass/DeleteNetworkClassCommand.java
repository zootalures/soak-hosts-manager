/**
 * 
 */
package edu.bath.soak.web.admin.networkclass;

import edu.bath.soak.net.model.NetworkClass;

public class DeleteNetworkClassCommand {
	NetworkClass toDelete;
	NetworkClass replaceWith;

	public NetworkClass getReplaceWith() {
		return replaceWith;
	}

	public void setReplaceWith(NetworkClass replaceWith) {
		this.replaceWith = replaceWith;
	}

	public NetworkClass getToDelete() {
		return toDelete;
	}

	public void setToDelete(NetworkClass toDelete) {
		this.toDelete = toDelete;
	}
}