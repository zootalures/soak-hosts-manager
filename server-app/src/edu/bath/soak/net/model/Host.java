package edu.bath.soak.net.model;

import java.io.Serializable;
import java.net.Inet4Address;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Formula;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.Proxy;
import org.hibernate.annotations.Table;
import org.hibernate.annotations.Type;

import edu.bath.soak.model.ChangeInfo;
import edu.bath.soak.model.OwnedEntity;
import edu.bath.soak.model.OwnershipInfo;
import edu.bath.soak.model.URIObject;
import edu.bath.soak.util.MacAddress;
import edu.bath.soak.util.TypeUtils;
import edu.bath.soak.web.BeanView;
import edu.bath.soak.web.BeanViews;

/**
 * Base host entity describes basic information about a host
 */

@Entity()
@org.hibernate.annotations.Entity(selectBeforeUpdate = true)
@Proxy(lazy = false)
@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
@XmlAccessorType(value = XmlAccessType.PROPERTY)
@Table(appliesTo = "Host", indexes = {
		@Index(name = "hostname_idx", columnNames = { "name", "domain_suffix" }),
		@Index(name = "ipAddr_idx", columnNames = { "ipAddress" }),
		@Index(name = "macAddr_idx", columnNames = { "macAddress" }) })
@XmlType()
@XmlRootElement(name = "Host")
@BeanViews( { @BeanView("beanview/host/Host"),
		@BeanView(value = "beanview/host/Host", view = "short") })
public class Host implements URIObject, Serializable, Cloneable, OwnedEntity {
	private Long id;
	private String description;
	private Long version;

	private HostName hostName = new HostName();

	private MacAddress macAddress;

	private Inet4Address ipAddress;

	private Location location = new Location();

	private ChangeInfo changeInfo = new ChangeInfo();
	private OwnershipInfo ownership = new OwnershipInfo();

	private HostClass hostClass;

	private SortedSet<HostFlag> hostFlags = new TreeSet<HostFlag>();
	private List<HostAlias> hostAliases = new ArrayList<HostAlias>();
	private Map<String, ExtendedHostInfo> configSettings = new HashMap<String, ExtendedHostInfo>();

	private LastUsageInfo lastUsageInfo;

	/**
	 * serial UID
	 */
	private static final long serialVersionUID = 1L;

	public Host() {

	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE }, fetch = FetchType.LAZY)
	@XmlIDREF
	public HostClass getHostClass() {
		return hostClass;
	}

	public void setHostClass(HostClass hostClass) {
		this.hostClass = hostClass;
	}

	@Column(unique = true)
	public HostName getHostName() {
		return hostName;
	}

	public void setHostName(HostName hostName) {
		this.hostName = hostName;
	}

	@Column(unique = true)
	@Type(type = "inet4type")
	public Inet4Address getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(Inet4Address ipAddress) {
		this.ipAddress = ipAddress;
	}

	@XmlTransient
	@Formula(value = "INET_NTOA(ipAddress)")
	public String getIpAddressTxt() {
		return (ipAddress == null ? null : ipAddress.getHostAddress());
	}

	public void setIpAddressTxt(String val) {
		if (val != null) {
			ipAddress = TypeUtils.txtToIP(val);
		} else {
			ipAddress = null;
		}
	}

	public void setMacAddress(MacAddress mac) {
		this.macAddress = mac;
	}

	@Column(unique = true)
	@Type(type = "mactype")
	public MacAddress getMacAddress() {
		return macAddress;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		if (location == null)
			location = new Location();
		this.location = location;
	}

	public ChangeInfo getChangeInfo() {
		return changeInfo;
	}

	public void setChangeInfo(ChangeInfo changeInfo) {
		if (changeInfo == null)
			changeInfo = new ChangeInfo();
		this.changeInfo = changeInfo;
	}

	public String toString() {
		return hostName + ":" + ipAddress + ":" + description + ":"
				+ macAddress;

	}

	@Transient
	public boolean hasFlag(HostFlag flag) {
		return hostFlags.contains(flag);
	}

	public void addFlag(HostFlag flag) {
		hostFlags.add(flag);
	}

	public void deleteFlag(HostFlag flag) {
		hostFlags.remove(flag);
	}

	@Transient
	public Host getCopy() {
		try {
			return (Host) clone();
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
	}

	public OwnershipInfo getOwnership() {
		return ownership;
	}

	public void setOwnership(OwnershipInfo ownership) {
		if (null != ownership)
			this.ownership = ownership;
		else {
			this.ownership = new OwnershipInfo();
		}
	}

	@Transient
	public URI getURI() {
		if (getId() == null)
			throw new UnsupportedOperationException();
		try {
			return new URI("urn:soak:host", getId().toString(), null);
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}

	@Transient
	public Collection<URI> getAllPossibleURIs() {
		ArrayList<URI> al = new ArrayList<URI>();
		try {
			al.add(getURI());
			al.add(new URI("urn:soak:fqdn", getHostName().toString(), null));
			al.add(new URI("urn:soak:ipaddress", getIpAddress()
					.getHostAddress(), null));
			al.add(new URI("urn:soak:macaddress", getMacAddress().toString(),
					null));
			return al;
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((configSettings == null) ? 0 : configSettings.hashCode());
		result = prime * result
				+ ((description == null) ? 0 : description.hashCode());
		result = prime * result
				+ ((hostAliases == null) ? 0 : hostAliases.hashCode());
		result = prime * result
				+ ((hostClass == null) ? 0 : hostClass.hashCode());
		result = prime * result
				+ ((hostFlags == null) ? 0 : hostFlags.hashCode());
		result = prime * result
				+ ((hostName == null) ? 0 : hostName.hashCode());
		result = prime * result
				+ ((ipAddress == null) ? 0 : ipAddress.hashCode());
		result = prime * result
				+ ((location == null) ? 0 : location.hashCode());
		result = prime * result
				+ ((macAddress == null) ? 0 : macAddress.hashCode());
		result = prime * result
				+ ((ownership == null) ? 0 : ownership.hashCode());
		result = prime * result + ((version == null) ? 0 : version.hashCode());
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
		final Host other = (Host) obj;
		if (configSettings == null) {
			if (other.configSettings != null)
				return false;
		} else if (!configSettings.equals(other.configSettings))
			return false;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (hostAliases == null) {
			if (other.hostAliases != null)
				return false;
		} else if (!getHostAliases().equals(other.getHostAliases()))
			return false;
		if (hostClass == null) {
			if (other.hostClass != null)
				return false;
		} else if (!hostClass.equals(other.hostClass))
			return false;
		if (hostFlags == null) {
			if (other.hostFlags != null)
				return false;
		} else if (!hostFlags.equals(other.hostFlags))
			return false;
		if (hostName == null) {
			if (other.hostName != null)
				return false;
		} else if (!hostName.equals(other.hostName))
			return false;
		if (ipAddress == null) {
			if (other.ipAddress != null)
				return false;
		} else if (!ipAddress.equals(other.ipAddress))
			return false;
		if (location == null) {
			if (other.location != null)
				return false;
		} else if (!location.equals(other.location))
			return false;
		if (macAddress == null) {
			if (other.macAddress != null)
				return false;
		} else if (!macAddress.equals(other.macAddress))
			return false;
		if (ownership == null) {
			if (other.ownership != null)
				return false;
		} else if (!ownership.equals(other.ownership))
			return false;

		// we ignore version equality
		// if (version == null) {
		// if (other.version != null)
		// return false;
		// } else if (!version.equals(other.version))
		// return false;
		return true;
	}

	@OneToMany(mappedBy = "host", cascade = { CascadeType.ALL,
			CascadeType.REMOVE, CascadeType.MERGE, CascadeType.REFRESH }, fetch = FetchType.LAZY
		)
//	@org.hibernate.annotations.Cascade( {
//			org.hibernate.annotations.CascadeType.ALL,
//			org.hibernate.annotations.CascadeType.DELETE,
//			org.hibernate.annotations.CascadeType.MERGE,
//			org.hibernate.annotations.CascadeType.DELETE_ORPHAN })
	@org.hibernate.annotations.IndexColumn(name = "idx")
	public List<HostAlias> getHostAliases() {

		return hostAliases;
	}

	public void setHostAliases(List<HostAlias> hostAliases) {

		this.hostAliases = hostAliases;

	}

	@OneToMany(fetch = FetchType.LAZY, cascade = { CascadeType.ALL,CascadeType.REMOVE, CascadeType.MERGE, CascadeType.REFRESH  })
	@Cascade(value = org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	@JoinColumn(name = "host_id", nullable = false)
	@org.hibernate.annotations.MapKey(columns = @Column(name = "settingsKey"))
	public Map<String, ExtendedHostInfo> getConfigSettings() {
		return configSettings;
	}

	public void setConfigSettings(Map<String, ExtendedHostInfo> configSettings) {
		this.configSettings = configSettings;
	}

	@Transient
	public ExtendedHostInfo getConfigSetting(String key) {
		return configSettings.get(key);

	}

	@Transient
	public void setConfigSetting(String key, ExtendedHostInfo value) {
		if (null != value && value.isSet()) {
			// value.setHost(this);
			configSettings.put(key, value);
		} else {
			configSettings.remove(key);
		}
	}

	public Long getVersion() {
		return version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}

	/**
	 * The LRU object for this host, This should not be here, but hibernate
	 * query
	 * 
	 * @return
	 */
	@XmlTransient
	@ManyToOne(targetEntity = LastUsageInfo.class, optional = true)
	@JoinColumn(columnDefinition = "", name = "ipAddress", referencedColumnName = "ipAddress", insertable = false, updatable = false, nullable = true)
	@ForeignKey(name = "none")
	@NotFound(action = NotFoundAction.IGNORE)
	public LastUsageInfo getLastUsageInfo() {
		return lastUsageInfo;
	}

	public void setLastUsageInfo(LastUsageInfo lastUsageInfo) {
		this.lastUsageInfo = lastUsageInfo;
	}

}
