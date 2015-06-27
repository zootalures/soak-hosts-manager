package edu.bath.soak.dns.model;

import java.io.Serializable;
import java.net.Inet4Address;
import java.util.Date;
import java.util.Set;
import java.util.regex.Pattern;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlSeeAlso;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;

import edu.bath.soak.web.BeanView;

@Entity
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@XmlSeeAlso(value = { ForwardZone.class, ReverseZone.class })
@BeanView("beanview/dns/DNSZone")
public abstract class DNSZone implements Serializable, Cloneable,
		Comparable<DNSZone> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6962316704513337837L;

	private Long id;

	String domain;
	String displayName;
	String sigKey;
	boolean useTCP = true;
	Inet4Address serverIP;

	Integer serverPort = 53;

	Long serial;

	Long refresh;

	Long retry;

	Long expire;

	Long minTTL;

	Long defaultTTL;

	Date lastUpdate;

	Set<DNSRecord> dnsRecords;

	String ignoreHostRegexps;

	String ignoreTargetRegexps;

	String description;

	Pattern[] ignoreHostRegexpsCompiled = null;
	Pattern[] ignoreTargetRegexpsCompiled = null;

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		if (null != domain) {
			if (!domain.endsWith(".")) {
				domain = domain + ".";
			}
			if (domain.length() > 1 && domain.startsWith(".")) {
				domain = domain.substring(1);
			}
		}
		this.domain = domain;
	}

	@Column(nullable = true)
	public Long getExpire() {
		return expire;
	}

	public void setExpire(Long expire) {
		this.expire = expire;
	}

	@XmlID
	@Transient
	public String getXmlID() {
		return id.toString();
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(nullable = true)
	public Date getLastUpdate() {
		return lastUpdate;
	}

	public void setLastUpdate(Date lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

	@Column(nullable = true)
	public Long getMinTTL() {
		return minTTL;
	}

	public void setMinTTL(Long minTTL) {
		this.minTTL = minTTL;
	}

	@Column(nullable = true)
	public Long getRefresh() {
		return refresh;
	}

	public void setRefresh(Long refresh) {
		this.refresh = refresh;
	}

	@Column(nullable = true)
	public Long getRetry() {
		return retry;
	}

	public void setRetry(Long retry) {
		this.retry = retry;
	}

	@Column(nullable = true)
	public Long getSerial() {
		return serial;
	}

	public void setSerial(Long serial) {
		this.serial = serial;
	}

	@Type(type = "inet4type")
	public Inet4Address getServerIP() {
		return serverIP;
	}

	public void setServerIP(Inet4Address serverIP) {
		this.serverIP = serverIP;
	}

	@Column(name = "defaultTTL", nullable = true)
	public Long getDefaultTTL() {
		return defaultTTL;
	}

	public void setDefaultTTL(Long defaultTTL) {
		this.defaultTTL = defaultTTL;
	}

	public String toString() {
		return getDomain() + ":" + getSerial() + ":"
				+ getServerIP().getHostAddress();
	}

	@OneToMany(targetEntity = DNSRecord.class, cascade = { CascadeType.REMOVE }, mappedBy = "zone",fetch=FetchType.LAZY)
	public Set<DNSRecord> getZoneRecords() {
		return dnsRecords;
	}

	public void setZoneRecords(Set<DNSRecord> recs) {
		this.dnsRecords = recs;
	}

	@Column(nullable = true)
	public String getIgnoreHostRegexps() {
		return ignoreHostRegexps;
	}

	public void setIgnoreHostRegexps(String ignoreHostRegexps) {
		this.ignoreHostRegexps = ignoreHostRegexps;
		this.ignoreHostRegexpsCompiled = null;

	}

	/**
	 * Returns an array of compiled regular expressions which will match hosts
	 * which will not be added to this domain
	 * 
	 * @return
	 */
	@Transient
	public Pattern[] getCompiledIgnoreHostRegexps() {
		if (ignoreHostRegexpsCompiled != null) {
			return ignoreHostRegexpsCompiled;
		}
		String ignoreRes;
		Pattern[] compiledREs;
		if ((ignoreRes = getIgnoreHostRegexps()) != null) {
			String[] stringres = ignoreRes.split("\n");
			compiledREs = new Pattern[stringres.length];
			int i = 0;
			for (String re : stringres) {
				compiledREs[i++] = Pattern.compile(re.trim());
			}

		} else {
			compiledREs = new Pattern[0];

		}

		return ignoreHostRegexpsCompiled = compiledREs;
	}

	/**
	 * Returns an array of compiled regular expressions which will match targets
	 * which will not be added to this domain
	 * 
	 * @return
	 */
	@Transient
	public Pattern[] getCompiledIgnoreTargetRegexps() {
		if (ignoreTargetRegexpsCompiled != null) {
			return ignoreTargetRegexpsCompiled;
		}
		String ignoreRes;
		Pattern[] compiledREs;
		if ((ignoreRes = ignoreTargetRegexps) != null) {
			String[] stringres = ignoreRes.split("\n");
			compiledREs = new Pattern[stringres.length];
			int i = 0;
			for (String re : stringres) {
				compiledREs[i++] = Pattern.compile(re.trim());
			}

		} else {
			compiledREs = new Pattern[0];

		}

		return ignoreTargetRegexpsCompiled = compiledREs;
	}

	@Column(nullable = true)
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		final DNSZone other = (DNSZone) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getSigKey() {
		return sigKey;
	}

	public void setSigKey(String sigKey) {
		this.sigKey = sigKey;
	}

	/**
	 * Should TCP be used to interact with the server for this zone.
	 * 
	 * @return
	 */

	public boolean isUseTCP() {
		return useTCP;
	}

	public void setUseTCP(boolean useTCP) {
		this.useTCP = useTCP;
	}

	/**
	 * The port on the server to connect to
	 * 
	 * @return
	 */
	public Integer getServerPort() {
		return serverPort;
	}

	public void setServerPort(Integer serverPort) {
		this.serverPort = serverPort;
	}

	public String compareString() {
		String[] parts = domain.split("\\.");
		String reverse = "";
		for (int i = parts.length - 1; i >= 0; i--) {
			reverse += "." + parts[i];

		}
		return reverse;

	}

	public int compareTo(DNSZone o) {
		return displayName.compareTo(o.getDisplayName());
		// return compareString().compareTo(o.compareString());
	}

	/**
	 * A new-line seperated set of regular expressions which, when matched
	 * should not be included in this zone.
	 * 
	 * @return
	 */
	public String getIgnoreTargetRegexps() {
		return ignoreTargetRegexps;
	}

	public void setIgnoreTargetRegexps(String ignoreTargetRegexps) {
		this.ignoreTargetRegexps = ignoreTargetRegexps;
		this.ignoreTargetRegexpsCompiled = null;
	}

	/**
	 * indicates if a given target string is permitted in this zone.
	 * 
	 * This is a bit of a hack primariliy intended to implement the exclusion of
	 * private addresses from public facing views.
	 * 
	 * @param host
	 * @return
	 */
	public boolean hostNameIsAllowed(String host) {
		for (Pattern p : getCompiledIgnoreHostRegexps()) {
			if (p.matcher(host).matches())
				return false;
		}

		return true;
	}

	/**
	 * indicates if a given target string is permitted in this zone.
	 * 
	 * This is a bit of a hack primariliy intended to implement the exclusion of
	 * private addresses from public facing views.
	 * 
	 * @param target
	 * @return
	 */
	public boolean targetIsAllowed(String target) {
		for (Pattern p : getCompiledIgnoreTargetRegexps()) {
			if (p.matcher(target).matches())
				return false;
		}

		return true;
	}
}
