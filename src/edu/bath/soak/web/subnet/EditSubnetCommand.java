package edu.bath.soak.web.subnet;

import java.net.Inet4Address;
import java.util.HashMap;
import java.util.Map;

import edu.bath.soak.net.model.NetworkClass;
import edu.bath.soak.net.model.Subnet;
import edu.bath.soak.net.model.Vlan;
import edu.bath.soak.net.model.Subnet.HostClassState;

public class EditSubnetCommand {

	Inet4Address baseAddress;
	Integer numBits = 24;
	String description;
	String name;
	Inet4Address gateway;
	Boolean noScan;
	String comments;
	Vlan vlan;
	Long id;
	NetworkClass networkClass;
	Map<String, Subnet.HostClassState> hostClassPermissions = new HashMap<String, Subnet.HostClassState>();

	public Inet4Address getBaseAddress() {
		return baseAddress;
	}

	public void setBaseAddress(Inet4Address baseAddress) {
		this.baseAddress = baseAddress;
	}

	public Integer getNumBits() {
		return numBits;
	}

	public void setNumBits(Integer numBits) {
		this.numBits = numBits;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Inet4Address getGateway() {
		return gateway;
	}

	public void setGateway(Inet4Address gateway) {
		this.gateway = gateway;
	}

	public Boolean getNoScan() {
		return noScan;
	}

	public void setNoScan(Boolean noScan) {
		this.noScan = noScan;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public Vlan getVlan() {
		return vlan;
	}

	public void setVlan(Vlan vlan) {
		this.vlan = vlan;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public NetworkClass getNetworkClass() {
		return networkClass;
	}

	public void setNetworkClass(NetworkClass networkClass) {
		this.networkClass = networkClass;
	}

	public Map<String, HostClassState> getHostClassPermissions() {
		return hostClassPermissions;
	}

	public void setHostClassPermissions(
			Map<String, HostClassState> hostClassPermissions) {
		this.hostClassPermissions = hostClassPermissions;
	}

}
