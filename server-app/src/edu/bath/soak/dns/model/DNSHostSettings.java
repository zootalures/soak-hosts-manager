package edu.bath.soak.dns.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import edu.bath.soak.net.model.ExtendedHostInfo;

@Entity
@XmlRootElement
public class DNSHostSettings extends ExtendedHostInfo implements Serializable {
	Boolean neverUpdateDNS;
	Long hostTTL;

	public DNSHostSettings() {
		// setKey(DNSHostsInterceptor.DNS_FLAGS_KEY);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((hostTTL == null) ? 0 : hostTTL.hashCode());
		result = prime * result
				+ ((neverUpdateDNS == null) ? 0 : neverUpdateDNS.hashCode());
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
		final DNSHostSettings other = (DNSHostSettings) obj;
		if (hostTTL == null) {
			if (other.hostTTL != null)
				return false;
		} else if (!hostTTL.equals(other.hostTTL))
			return false;
		if (neverUpdateDNS == null) {
			if (other.neverUpdateDNS != null)
				return false;
		} else if (!neverUpdateDNS.equals(other.neverUpdateDNS))
			return false;
		return true;
	}

	@Override
	@Transient
	@XmlTransient
	public boolean isSet() {
		return (neverUpdateDNS != null && neverUpdateDNS) || null != hostTTL;
	}

	public void setNeverUpdateDNS(Boolean neverUpdateDNS) {
		this.neverUpdateDNS = neverUpdateDNS;
	}

	public Long getHostTTL() {
		return hostTTL;
	}

	public void setHostTTL(Long hostTTL) {
		this.hostTTL = hostTTL;
	}

	public Boolean getNeverUpdateDNS() {
		return neverUpdateDNS;
	}
}
