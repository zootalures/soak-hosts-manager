package edu.bath.soak.dns.cmd;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import edu.bath.soak.cmd.UICommand;
import edu.bath.soak.dns.model.DNSZone;
import edu.bath.soak.web.BeanView;

@XmlRootElement
@BeanView("beanview/dns/CleanUpUnusedDNSRecordsCmd")
public class CleanUpUnusedDNSRecordsCmd extends BulkDeleteDNSRecordsCmd
		implements UICommand, Serializable {

	List<DNSZone> zones = new ArrayList<DNSZone>();

	@Transient
	@XmlTransient
	public String getCommandDescription() {
		return "Remove unused DNS Records";
	}

	@XmlIDREF
	@XmlElements( { @XmlElement(name = "zone", type = DNSZone.class) })
	public List<DNSZone> getZones() {
		return zones;
	}

	public void setZones(List<DNSZone> zones) {
		this.zones = zones;
	}

}
