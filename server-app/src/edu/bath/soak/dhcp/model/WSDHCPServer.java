package edu.bath.soak.dhcp.model;

import java.io.Serializable;
import java.net.Inet4Address;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Transient;

import edu.bath.soak.util.TypeUtils;
import edu.bath.soak.web.BeanView;
import edu.bath.soak.web.BeanViews;

@BeanViews( {
		@BeanView(value = "beanview/dhcp/WSDHCPServer"),
		@BeanView(value = "beanview/dhcp/WSDHCPServer-form", view = "form") })
@Entity
public class WSDHCPServer extends DHCPServer implements Serializable,
		UpdateableDhcpServer {

	Inet4Address serverIP;
	String agentUrl;
	String userName;
	String password;
	Date lastSubnetsFetched;

	@Transient
	public Date getLastFetched() {
		return getLastSubnetsFetched();
	}

	public Long getServerIntIP() {
		if (null == serverIP)
			return null;
		return TypeUtils.ipToInt(serverIP);
	}

	public void setServerIntIP(Long ip) {
		if (null == ip) {
			serverIP = null;
		} else {
			serverIP = TypeUtils.intToIP(ip);
		}
	}

	@Transient
	public Inet4Address getServerIP() {
		return serverIP;
	}

	public void setServerIP(Inet4Address serverIP) {
		this.serverIP = serverIP;
	}

	public String getAgentUrl() {
		return agentUrl;
	}

	public void setAgentUrl(String agentUrl) {
		this.agentUrl = agentUrl;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Date getLastSubnetsFetched() {
		return lastSubnetsFetched;
	}

	public void setLastSubnetsFetched(Date lastSubnetsFetched) {
		this.lastSubnetsFetched = lastSubnetsFetched;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((agentUrl == null) ? 0 : agentUrl.hashCode());
		result = prime * result
				+ ((password == null) ? 0 : password.hashCode());
		result = prime * result
				+ ((serverIP == null) ? 0 : serverIP.hashCode());
		result = prime * result
				+ ((userName == null) ? 0 : userName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		final WSDHCPServer other = (WSDHCPServer) obj;
		if (agentUrl == null) {
			if (other.agentUrl != null)
				return false;
		} else if (!agentUrl.equals(other.agentUrl))
			return false;
		if (password == null) {
			if (other.password != null)
				return false;
		} else if (!password.equals(other.password))
			return false;
		if (serverIP == null) {
			if (other.serverIP != null)
				return false;
		} else if (!serverIP.equals(other.serverIP))
			return false;
		if (userName == null) {
			if (other.userName != null)
				return false;
		} else if (!userName.equals(other.userName))
			return false;
		return true;
	}

}
