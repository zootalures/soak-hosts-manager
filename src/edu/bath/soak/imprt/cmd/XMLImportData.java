package edu.bath.soak.imprt.cmd;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import edu.bath.soak.model.OrgUnit;
import edu.bath.soak.net.model.Host;
import edu.bath.soak.net.model.HostClass;
import edu.bath.soak.net.model.NameDomain;
import edu.bath.soak.net.model.NetworkClass;
import edu.bath.soak.net.model.Subnet;
import edu.bath.soak.net.model.Vlan;
import edu.bath.soak.web.BeanView;

@XmlRootElement(name = "SoakData")
@XmlType()
@XmlAccessorType(value = XmlAccessType.PROPERTY)
@BeanView("beanview/host/XMLImportData")
public class XMLImportData {

	List<OrgUnit> orgUnits = new ArrayList<OrgUnit>();
	List<HostClass> hostClasses = new ArrayList<HostClass>();
	List<Host> hosts = new ArrayList<Host>();
	List<NameDomain> nameDomains = new ArrayList<NameDomain>();
	List<NetworkClass> networkClasses = new ArrayList<NetworkClass>();
	List<Subnet> subnets = new ArrayList<Subnet>();
	List<Vlan> vlans = new ArrayList<Vlan>();

	public List<HostClass> getHostClasses() {
		return hostClasses;
	}

	public List<Vlan> getVlans() {
		return vlans;
	}

	public List<NameDomain> getNameDomains() {
		return nameDomains;
	}

	public List<NetworkClass> getNetworkClasses() {
		return networkClasses;
	}

	public List<Subnet> getSubnets() {
		return subnets;
	}

	public List<Host> getHosts() {
		return hosts;
	}

	public void setHostClasses(List<HostClass> hostClasses) {
		this.hostClasses = hostClasses;
	}

	public void setHosts(List<Host> hosts) {
		this.hosts = hosts;
	}

	public void setNameDomains(List<NameDomain> nameDomains) {
		this.nameDomains = nameDomains;
	}

	public void setNetworkClasses(List<NetworkClass> networkClasses) {
		this.networkClasses = networkClasses;
	}

	public void setSubnets(List<Subnet> subnets) {
		this.subnets = subnets;
	}

	public void setVlans(List<Vlan> vlans) {
		this.vlans = vlans;
	}

	public void addAll(XMLImportData data) {
		hosts.addAll(data.hosts);
		vlans.addAll(data.vlans);
		subnets.addAll(data.subnets);
		nameDomains.addAll(data.nameDomains);
		networkClasses.addAll(data.networkClasses);
		hostClasses.addAll(data.hostClasses);
		orgUnits.addAll(data.orgUnits);
	}

	public List<OrgUnit> getOrganisationalUnits() {
		return orgUnits;
	}

	public void setOrganisationalUnits(List<OrgUnit> orgUnits) {
		this.orgUnits = orgUnits;
	}
}
