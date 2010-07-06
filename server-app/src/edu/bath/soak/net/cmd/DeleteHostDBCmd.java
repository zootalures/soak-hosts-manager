package edu.bath.soak.net.cmd;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

import edu.bath.soak.net.model.Host;
import edu.bath.soak.web.BeanView;

/**
 * Command to indicate the deletion of a specific host
 * 
 * @author cspocc
 * 
 */
@XmlRootElement
@BeanView("beanview/host/DeleteHostDBCmd")
public class DeleteHostDBCmd extends HostsDBCommand implements Serializable {
	Host host;
	Long versionBeforeChange;
	String changeComments;

	public Host getHost() {
		return host;
	}

	public void setHost(Host host) {
		this.host = host;
	}

	public Long getVersionBeforeChange() {
		return versionBeforeChange;
	}

	public void setVersionBeforeChange(Long versionBeforeChange) {
		this.versionBeforeChange = versionBeforeChange;
	}

	public String getChangeComments() {
		return changeComments;
	}

	public void setChangeComments(String changeComments) {
		this.changeComments = changeComments;
	}
}
