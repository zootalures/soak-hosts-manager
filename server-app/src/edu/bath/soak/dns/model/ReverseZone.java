package edu.bath.soak.dns.model;

import java.io.Serializable;
import java.net.Inet4Address;

import javax.persistence.Entity;
import javax.xml.bind.annotation.XmlRootElement;

import org.xbill.DNS.Name;
import org.xbill.DNS.ReverseMap;
import org.xbill.DNS.TextParseException;

/**
 * A reverse DNS Zone.
 * 
 * @author cspocc
 * 
 */
@Entity
@XmlRootElement()
public class ReverseZone extends DNSZone implements Serializable {
	/**
	 * Checks if the given address matches the domain of this zone Only applies
	 * if the zone is reverse Zone
	 * 
	 * @param address
	 * @return
	 */
	public boolean reverseMatches(Inet4Address address) {
		try {
			Name addressName = ReverseMap.fromAddress(address);
			if (!addressName.subdomain(Name.fromString(domain)))
				return false;
			return hostNameIsAllowed(addressName.toString());
		} catch (TextParseException e) {
			throw new RuntimeException("Name was not parseable", e);
		}
	}

}
