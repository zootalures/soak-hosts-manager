package edu.bath.soak.dns.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlTransient;

import org.springframework.util.Assert;
import org.xbill.DNS.DClass;
import org.xbill.DNS.Name;
import org.xbill.DNS.Record;
import org.xbill.DNS.Type;

@Entity
@Embeddable
@Table(uniqueConstraints = @UniqueConstraint(columnNames = { "hostname",
		"type", "target", "zone_id" }))
public class DNSRecord implements Comparable<DNSRecord>, Serializable {
	Long id;
	DNSZone zone;
	Long lastUpdateSerial;
	String target;
	String hostname;
	String type;

	Long ttl;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(nullable = false)
	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	@Column(nullable = false)
	public Long getTtl() {
		return ttl;
	}

	public void setTtl(Long ttl) {
		this.ttl = ttl;
	}

	@Column(nullable = false)
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@ManyToOne(fetch = FetchType.EAGER, targetEntity = DNSZone.class, optional = false)
	@XmlIDREF
	public DNSZone getZone() {
		return zone;
	}

	public void setZone(DNSZone zone) {
		this.zone = zone;
	}

	public static DNSRecord fromDNSRecord(DNSZone zone, Record record) {
		Assert.notNull(zone);
		Assert.notNull(record);

		DNSRecord r = new DNSRecord();
		r.setZone(zone);
		r.setHostName(record.getName().toString());
		r.setTarget(record.rdataToString());
		r.setTtl(record.getTTL());
		r.setType(Type.string(record.getType()));
		return r;
	}

	@Transient
	@XmlTransient
	public Record getRecord() {
		try {
			Record r = Record.fromString(Name.fromString(getHostName()), Type
					.value(getType()), DClass.IN, getTtl(), getTarget(), null);

			if (r == null) {
				throw new RuntimeException(
						"Unable to create dns record structure for this record");
			}
			return r;
		} catch (Exception e) {
			throw new RuntimeException("Unabled to parse record structure", e);
		}
	}

	@Column(nullable = false)
	public String getHostName() {
		return hostname;
	}

	public void setHostName(String hostname) {
		this.hostname = hostname;
	}

	public String toString() {
		return getHostName() + "'" + ":" + getType() + ":" + getTtl() + ":"
				+ "'" + getTarget() + "'";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((hostname == null) ? 0 : hostname.hashCode());
		result = prime * result + ((target == null) ? 0 : target.hashCode());
		// result = prime * result + ((ttl == null) ? 0 : ttl.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		result = prime * result + ((zone == null) ? 0 : zone.hashCode());
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
		final DNSRecord other = (DNSRecord) obj;
		if (hostname == null) {
			if (other.hostname != null)
				return false;
		} else if (!hostname.equals(other.hostname))
			return false;
		if (target == null) {
			if (other.target != null)
				return false;
		} else if (!target.equals(other.target))
			return false;
		// if (ttl == null) {
		// if (other.ttl != null)
		// return false;
		// } else if (!ttl.equals(other.ttl))
		// return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		if (zone == null) {
			if (other.zone != null)
				return false;
		} else if (!zone.equals(other.zone))
			return false;
		return true;
	}

	public boolean equalsIncludingTtl(DNSRecord other) {
		if (this == other)
			return true;
		if (other == null)
			return false;
		if (hostname == null) {
			if (other.hostname != null)
				return false;
		} else if (!hostname.equals(other.hostname))
			return false;
		if (target == null) {
			if (other.target != null)
				return false;
		} else if (!target.equals(other.target))
			return false;
		 if (ttl == null) {
			if (other.ttl != null)
				return false;
		} else if (!ttl.equals(other.ttl))
			return false;

		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		if (zone == null) {
			if (other.zone != null)
				return false;
		} else if (!zone.equals(other.zone))
			return false;
		return true;
	}

	public Long getLastUpdateSerial() {
		return lastUpdateSerial;
	}

	public void setLastUpdateSerial(Long lastUpdateSerial) {
		this.lastUpdateSerial = lastUpdateSerial;
	}

	public int compareTo(DNSRecord o) {
		int rv;
		if (0 != (rv = getZone().compareTo(o.getZone())))
			return rv;
		if (0 != (rv = getHostName().compareTo(o.getHostName())))
			return rv;
		if (0 != (rv = getType().compareTo(o.getType())))
			return rv;
		if (0 != (rv = getTarget().compareTo(o.getTarget())))
			return rv;
		if (getTtl() != null)
			return getTtl().compareTo(o.getTtl());
		if (o.getTtl() != null)
			return 1;
		return 0;
	}
}
