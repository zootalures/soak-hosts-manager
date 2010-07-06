package edu.bath.soak.net.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.Proxy;

/**
 * A Host alias, notionally we associate each alias with a specific host
 * 
 */

@Entity()
//@Proxy(lazy=false)
@XmlRootElement
@org.hibernate.annotations.Table(appliesTo = "HostAlias", indexes = {
		@Index(name = "hostalias_alias_idx", columnNames = { "name",
				"domain_suffix" }),
		@Index(name = "hostalias_hostId_idx", columnNames = { "host_id" }) })
public class HostAlias implements Comparable<HostAlias>, java.io.Serializable {
	@XmlType
	public enum HostAliasType {
		CNAME, AREC
	}

	int idx;
	Long id;
	Host host;
	HostName alias = new HostName();
	HostAliasType type = HostAliasType.CNAME;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@XmlTransient
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * The host associated with this alias
	 * 
	 * @return
	 */
	@ManyToOne(fetch=FetchType.LAZY)
	@XmlTransient
	@JoinColumn(name = "host_id", nullable = false)
	public Host getHost() {
		return host;
	}

	public void setHost(Host host) {
		this.host = host;
	}

	@Column(nullable = false)
	public HostName getAlias() {
		return alias;
	}

	public void setAlias(HostName alias) {
		this.alias = alias;
	}

	@Column(nullable = false)
	public HostAliasType getType() {
		return type;
	}

	public void setType(HostAliasType type) {
		this.type = type;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((alias == null) ? 0 : alias.hashCode());
		result = prime
				* result
				+ ((host == null) ? 0 : (host.getId() == null ? 0 : host
						.getId().hashCode()));
		result = prime * result + ((type == null) ? 0 : type.hashCode());
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
		final HostAlias other = (HostAlias) obj;
		if (alias == null) {
			if (other.alias != null)
				return false;
		} else if (!alias.equals(other.alias))
			return false;

		if (host == null) {
			if (other.host != null)
				return false;
		} else if (host.getId() == null) {
			if (other.host.getId() != null)
				return false;
		} else if (!host.getId().equals(other.host.getId())) {
			return false;
		}

		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}

	public int compareTo(HostAlias h) {
		if (alias.toString().equals(h.getAlias().toString())) {
			return type.compareTo(h.getType());
		} else {
			return alias.toString().compareTo(h.getAlias().toString());
		}
	}

	/**
	 * Somewhat annoying JAXB hack to enforce referential integrity for
	 * recovering aliases
	 * 
	 * @param u
	 * @param parent
	 */
	public void afterUnmarshal(Unmarshaller u, Object parent) {
		this.host = (Host) parent;
	}

	public int getIdx() {
		return idx;
	}

	public void setIdx(int idx) {
		this.idx = idx;
	}

}
