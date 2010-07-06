/**
 * 
 */
package edu.bath.soak.web.admin.namedomain;

import edu.bath.soak.net.model.NameDomain;

public class DeleteNameDomainCommand {
	NameDomain toDelete;
	NameDomain replaceWith;

	public NameDomain getReplaceWith() {
		return replaceWith;
	}

	public void setReplaceWith(NameDomain replaceWith) {
		this.replaceWith = replaceWith;
	}

	public NameDomain getToDelete() {
		return toDelete;
	}

	public void setToDelete(NameDomain toDelete) {
		this.toDelete = toDelete;
	}
}