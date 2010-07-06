package edu.bath.soak.web.admin.namedomain;

import edu.bath.soak.net.model.NameDomain;

public class EditNameDomainCommand {
	NameDomain nameDomain;
	boolean isCreation = false;

	public NameDomain getNameDomain() {
		return nameDomain;
	}

	public void setNameDomain(NameDomain networkClass) {
		this.nameDomain = networkClass;
	}

	public boolean isCreation() {
		return isCreation;
	}

	public void setCreation(boolean isCreation) {
		this.isCreation = isCreation;
	};

}
