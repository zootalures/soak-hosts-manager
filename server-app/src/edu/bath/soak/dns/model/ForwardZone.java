package edu.bath.soak.dns.model;

import java.io.Serializable;
import java.util.regex.Pattern;

import javax.persistence.Entity;
import javax.xml.bind.annotation.XmlRootElement;

import org.springframework.util.Assert;
import org.xbill.DNS.Name;
import org.xbill.DNS.TextParseException;

import edu.bath.soak.net.model.NameDomain;

/**
 * A forward DNS zone.
 * 
 * @author cspocc
 * 
 */
@Entity
@XmlRootElement()
public class ForwardZone extends DNSZone implements Serializable {
	/**
	 * 
	 * Determines if a fully qualified domain name "matches" this zone (i.e.
	 * should this name appear in this zone)
	 * 
	 * Checks that the domain is a strict subdomain of this domain, and that the
	 * host name is not ignroed by a specified regular expression.
	 * 
	 * @param hostname
	 * @return
	 */
	public boolean forwardMatches(String hostname) {
		Assert.notNull(hostname);
		try {
			if (!Name.fromString(hostname).subdomain(Name.fromString(domain))) {
				return false;
			}

			return hostNameIsAllowed(hostname);

		} catch (TextParseException e) {
			throw new RuntimeException("domain did not parse", e);
		}
	}

	/**
	 * indicates if the given name domain matches the domain managed by this
	 * zone.
	 * 
	 * Matches if the name domain is a suffix of this zone and it does not match
	 * any of the excluded name domains specified in this zone.
	 * 
	 * @param nd
	 * @return
	 */
	public boolean matchesNameDomain(NameDomain nd) {
		Assert.notNull(nd);
		if (!nd.getSuffix().endsWith(domain)) {
			return false;
		}
		return targetIsAllowed(nd.getSuffix());
	}
}
