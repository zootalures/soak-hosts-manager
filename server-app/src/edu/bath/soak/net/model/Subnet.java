package edu.bath.soak.net.model;

import java.io.Serializable;
import java.net.Inet4Address;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.Proxy;
import org.hibernate.annotations.Table;
import org.hibernate.annotations.Type;

import com.sun.xml.txw2.annotation.XmlElement;

import edu.bath.soak.model.OrgUnitAcl;
import edu.bath.soak.model.OrgUnitAclEntity;
import edu.bath.soak.util.TypeUtils;
import edu.bath.soak.web.BeanView;
import edu.bath.soak.web.BeanViews;

/**
 * Routable subnet entity
 * 
 * Each routable subnet describes an IP range which
 */
@Entity()
@XmlRootElement
@XmlElement
@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
@Table(appliesTo = "Subnet", indexes = { @Index(name = "ipAddr_idx", columnNames = {
		"minIP", "maxIP" }) })
@BeanViews( { @BeanView("beanview/host/Subnet"),
		@BeanView(value = "beanview/host/Subnet", view = "short") })
public class Subnet extends IPRange implements Serializable, OrgUnitAclEntity {
	private static final long serialVersionUID = 1L;

	private String name;
	private OrgUnitAcl orgUnitAcl = new OrgUnitAcl();

	public static enum HostClassState {
		ALLOWED, DENIED, DEFAULT
	};

	// Set<OrgUnit> allowedOrgUnits = new HashSet<OrgUnit>();
	private String description;

	private String comments;

	private Inet4Address gateway;

	private Vlan vlan;

	private NetworkClass networkClass;

	private boolean noScan;

	Set<HostClass> subnetAllowedHostClasses = new HashSet<HostClass>();
	Set<HostClass> subnetDeniedHostClasses = new HashSet<HostClass>();

	/**
	 * The description of this subnet
	 * 
	 * @return
	 */
	@Column(name = "description")
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * The IP gateway which should normally be used by hosts on this network
	 * 
	 * @return
	 */
	@Type(type = "inet4type")
	public Inet4Address getGateway() {
		return gateway;
	}

	public void setGateway(Inet4Address gateway) {
		this.gateway = gateway;
	}

	/**
	 * A classification of what this network is to be used for
	 * 
	 * @return
	 */
	@ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	@JoinColumn(name = "networkClass", nullable = false)
	@XmlIDREF
	public NetworkClass getNetworkClass() {
		return networkClass;
	}

	public void setNetworkClass(NetworkClass localClass) {
		this.networkClass = localClass;
	}

	/**
	 * The short name of this subnet
	 * 
	 * @return
	 */
	@Column(nullable = false, name = "name", length = 255)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * The L2 802.1X VLAN upon which this subnet is transported may be NULL
	 * 
	 * @return
	 */
	@ManyToOne(optional = true)
	@XmlIDREF
	public Vlan getVlan() {
		return vlan;
	}

	public void setVlan(Vlan vlan) {
		this.vlan = vlan;
	}

	/**
	 * Should this network be excluded from network scans
	 * 
	 * @return
	 */
	@Column(name = "noScan", nullable = false)
	public boolean isNoScan() {
		return noScan;
	}

	public void setNoScan(boolean noScan) {
		this.noScan = noScan;
	}

	public String toString() {
		return "" + id + ":" + getNetworkClass() + ":"
				+ getMinIP().getHostAddress() + "-"
				+ getMaxIP().getHostAddress() + ":" + getSubnetMask() + ":"
				+ getDescription() + ":" + getName() + ":" + getIPNetwork()
				+ ":" + (isNoScan() ? "Not" : "") + "Scanned";

	}

	@Transient
	public Inet4Address getIPNetwork() {
		return getMinIP();
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	@Transient
	public int getNetworkBits() {
		long ipnum = TypeUtils.ipToInt(maxIP) - TypeUtils.ipToInt(minIP);
		return 64 - Long.numberOfLeadingZeros(ipnum);

	}

	@Transient
	public int getMaskBits() {
		return 32 - getNetworkBits();

	}

	@Transient
	public Inet4Address getMinUsableAddress() {
		return TypeUtils.ipMath(getMinIP(), 1);
	}

	@Transient
	public Inet4Address getMaxUsableAddress() {
		return TypeUtils.ipMath(getMaxIP(), -1);
	}

	@Transient
	public Inet4Address getSubnetMask() {
		return TypeUtils.intToIP(0xffffffffL << (getNetworkBits()));
	}

	@Transient
	public long getNumUseableAddresses() {
		long max = TypeUtils.ipToInt(getMaxUsableAddress());
		long min = TypeUtils.ipToInt(getMinUsableAddress());
		long numAddrs = (max - min)+1;
		return numAddrs;
	}

	@Transient
	public String getDisplayString() {
		return getMinIP().getHostAddress() + " : " + getName();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		final Subnet other = (Subnet) obj;
		if (comments == null) {
			if (other.comments != null)
				return false;
		} else if (!comments.equals(other.comments))
			return false;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (gateway == null) {
			if (other.gateway != null)
				return false;
		} else if (!gateway.equals(other.gateway))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (networkClass == null) {
			if (other.networkClass != null)
				return false;
		} else if (!networkClass.equals(other.networkClass))
			return false;
		if (noScan != other.noScan)
			return false;
		if (vlan == null) {
			if (other.vlan != null)
				return false;
		} else if (!vlan.equals(other.vlan))
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((comments == null) ? 0 : comments.hashCode());
		result = prime * result
				+ ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((gateway == null) ? 0 : gateway.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result
				+ ((networkClass == null) ? 0 : networkClass.hashCode());
		result = prime * result + (noScan ? 1231 : 1237);
		result = prime * result + ((vlan == null) ? 0 : vlan.hashCode());
		return result;
	}

	Long id;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Transient
	@XmlTransient
	public Set<HostClass> getAllowedHostClasses() {
		HashSet<HostClass> allHcs = new HashSet<HostClass>(getNetworkClass()
				.getAllowedHostClasses());
		allHcs.addAll(getSubnetAllowedHostClasses());
		allHcs.removeAll(getSubnetDeniedHostClasses());

		return allHcs;
	}

	public boolean canAddHostClass(HostClass hc) {
		return (networkClass.getAllowedHostClasses().contains(hc) || subnetAllowedHostClasses
				.contains(hc))
				&& !subnetDeniedHostClasses.contains(hc);
	}

	@ManyToMany(targetEntity = HostClass.class, cascade = {
			CascadeType.PERSIST, CascadeType.MERGE }, fetch = FetchType.EAGER)
	@JoinTable(name = "Subnet_HostClass_allowed")
	@XmlIDREF
	public Set<HostClass> getSubnetAllowedHostClasses() {
		return subnetAllowedHostClasses;
	}

	public void setSubnetAllowedHostClasses(Set<HostClass> subnetAllowedClasses) {
		this.subnetAllowedHostClasses = subnetAllowedClasses;
	}

	@ManyToMany(targetEntity = HostClass.class, cascade = {
			CascadeType.PERSIST, CascadeType.MERGE }, fetch = FetchType.EAGER)
	@JoinTable(name = "Subnet_HostClass_denied")
	@XmlIDREF
	public Set<HostClass> getSubnetDeniedHostClasses() {
		return subnetDeniedHostClasses;
	}

	public void setSubnetDeniedHostClasses(
			Set<HostClass> subnetDeniedHostClasses) {
		this.subnetDeniedHostClasses = subnetDeniedHostClasses;
	}

	// @ManyToMany(targetEntity = OrgUnit.class, cascade = { CascadeType.PERSIST
	// })
	// @XmlIDREF
	// public Set<OrgUnit> getAllowedOrgUnits() {
	// return allowedOrgUnits;
	// }
	//
	// public void setAllowedOrgUnits(Set<OrgUnit> allowedOrgUnits) {
	// this.allowedOrgUnits = allowedOrgUnits;
	// }
	@Embedded
	public OrgUnitAcl getOrgUnitAcl() {
		return orgUnitAcl;
	}

	public void setOrgUnitAcl(OrgUnitAcl orgUnitAcl) {
		this.orgUnitAcl = orgUnitAcl;
	}

	@Transient
	public OrgUnitAclEntity aclParent() {
		return networkClass;
	}
}
