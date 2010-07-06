package edu.bath.soak.dns.cmd;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

import edu.bath.soak.cmd.RenderableCommandOption;
import edu.bath.soak.web.BeanView;
import edu.bath.soak.web.BeanViews;

/**
 * Command Flag object which is used to augment settings for host commands
 * 
 * @author cspocc
 * 
 */
@BeanViews( {
		@BeanView(value = "beanview/dns/DNSHostCommandFlags"),
		@BeanView(value = "beanview/dns/DNSHostCommandFlags-form", view = "form") })
@XmlRootElement
public class DNSHostCommandFlags implements Serializable,
		RenderableCommandOption {

	Long hostTTL;
	boolean forceDNSUpdates = false;

	public enum DNSUpdateMode {
		DNS_DEFAULT, NO_DNS_EDITS, NEVER_DNS_EDITS, DNS_REFRESH_ALL_DATA
	};

	DNSUpdateMode updateMode = DNSUpdateMode.DNS_DEFAULT;

	public int getOrder() {
		return 10;
	}
	public DNSUpdateMode getUpdateMode() {
		return updateMode;
	}

	public void setUpdateMode(DNSUpdateMode updateMode) {
		this.updateMode = updateMode;
	}

	public boolean isHasOptionsSet() {
		return updateMode != DNSUpdateMode.DNS_DEFAULT || hostTTL != null
				|| forceDNSUpdates;
	}

	public Long getHostTTL() {
		return hostTTL;
	}

	public void setHostTTL(Long hostTTL) {
		this.hostTTL = hostTTL;
	}

	public boolean isForceDNSUpdates() {
		return forceDNSUpdates;
	}

	public void setForceDNSUpdates(boolean forceDNSUpdates) {
		this.forceDNSUpdates = forceDNSUpdates;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (forceDNSUpdates ? 1231 : 1237);
		result = prime * result + ((hostTTL == null) ? 0 : hostTTL.hashCode());
		result = prime * result
				+ ((updateMode == null) ? 0 : updateMode.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final DNSHostCommandFlags other = (DNSHostCommandFlags) obj;
		if (forceDNSUpdates != other.forceDNSUpdates)
			return false;
		if (hostTTL == null) {
			if (other.hostTTL != null)
				return false;
		} else if (!hostTTL.equals(other.hostTTL))
			return false;
		if (updateMode == null) {
			if (other.updateMode != null)
				return false;
		} else if (!updateMode.equals(other.updateMode))
			return false;
		return true;
	}

}
