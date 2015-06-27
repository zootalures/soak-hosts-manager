package edu.bath.soak.net.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlIDREF;

import com.sun.xml.txw2.annotation.XmlElement;

import edu.bath.soak.model.OrgUnitAcl;
import edu.bath.soak.model.OrgUnitAclEntity;
import edu.bath.soak.web.BeanView;

@Entity()
@XmlElement
@BeanView("beanview/host/NetworkClass")

public class NetworkClass implements Serializable, Cloneable, OrgUnitAclEntity {
	private static final long serialVersionUID = 1L;
	private String name;
	private String description;
	private String id;
	Set<HostClass> allowedHostClasses = new HashSet<HostClass>();
	Set<Subnet> subnets = new HashSet<Subnet>();
	private OrgUnitAcl orgUnitAcl = new OrgUnitAcl();

	public NetworkClass() {
	}

	@Column(name = "description")
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Column(name = "name")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String toString() {
		return id + ":" + name + ":" + description;
	}

	@Id
	@XmlID
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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
		final NetworkClass other = (NetworkClass) obj;

		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@ManyToMany(targetEntity = HostClass.class, cascade = { CascadeType.PERSIST }, fetch = FetchType.EAGER)
	@XmlIDREF
	public Set<HostClass> getAllowedHostClasses() {
		return allowedHostClasses;
	}

	public void setAllowedHostClasses(Set<HostClass> allowedClasses) {
		this.allowedHostClasses = allowedClasses;
	}

	@OneToMany(mappedBy = "networkClass")
	public Set<Subnet> getSubnets() {
		return subnets;
	}

	public void setSubnets(Set<Subnet> subnets) {
		this.subnets = subnets;
	}

	@Embedded
	public OrgUnitAcl getOrgUnitAcl() {
		return orgUnitAcl;
	}

	public void setOrgUnitAcl(OrgUnitAcl orgUnitAcl) {
		if (orgUnitAcl == null)
			this.orgUnitAcl = new OrgUnitAcl();
		else
			this.orgUnitAcl = orgUnitAcl;
	}

	@Transient
	public OrgUnitAclEntity aclParent() {
		return null;
	}

}
