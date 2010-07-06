package edu.bath.soak.net.cmd;

import java.io.Serializable;

import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import edu.bath.soak.cmd.AbstractUICmdImpl;
import edu.bath.soak.cmd.UICommand;
import edu.bath.soak.net.model.Host;
import edu.bath.soak.web.BeanView;

/**
 * Command to indicate the deletion of a specific host
 * 
 * @author cspocc
 * 
 */
@XmlRootElement
@BeanView("beanview/host/DeleteHostUICmd")
public class DeleteHostUICmd extends AbstractUICmdImpl implements UICommand,
		Serializable {
	Host host;

	public Host getHost() {
		return host;
	}

	public void setHost(Host host) {
		this.host = host;
	}

	@Transient
	@XmlTransient
	public String getCommandDescription() {
		return "Delete " + host.getHostName().toString();
	}
}
