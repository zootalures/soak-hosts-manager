package edu.bath.soak.net.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;

/**
 * Holder for parsable stored commands
 * 
 * @author cspocc
 * 
 */
@Entity
public class StoredCommand implements Serializable {
	String id;
	String commandXML;
	String commandDescription;
	String changeComments;
	String user;
	Date changeTime;

	@Id
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Lob()
	@Column(length = 2147483647)
	public String getCommandXML() {
		return commandXML;
	}

	public void setCommandXML(String commandXML) {
		this.commandXML = commandXML;
	}

	public String getChangeComments() {
		return changeComments;
	}

	public void setChangeComments(String changeComments) {
		this.changeComments = changeComments;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public Date getChangeTime() {
		return changeTime;
	}

	public void setChangeTime(Date changeTime) {
		this.changeTime = changeTime;
	}

	public String getCommandDescription() {
		return commandDescription;
	}

	public void setCommandDescription(String commandDescription) {
		this.commandDescription = commandDescription;
	}

}
