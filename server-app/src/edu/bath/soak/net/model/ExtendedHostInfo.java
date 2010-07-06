package edu.bath.soak.net.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlSeeAlso;

import edu.bath.soak.dns.model.DNSHostSettings;

/**
 * Abstract base class for config settings objects which are local to a given
 * host.
 * 
 * extended host info objects are keyed with a (presumed unique) string.
 * 
 * @author cspocc
 * 
 */
@Entity
// @Table(uniqueConstraints = @UniqueConstraint(columnNames = { "host_id",
// "settingsKey" }))
@XmlSeeAlso( { DNSHostSettings.class })
public class ExtendedHostInfo implements Serializable {

	Long id;

	// String key;

	public static boolean anyNonNull(Object... arguments) {
		for (Object o : arguments)
			if (o != null)
				return true;
		return false;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * the host related to this information object
	 * 
	 * @return
	 */
	// @ManyToOne()
	// public Host getHost() {
	// return host;
	// }
	//
	// public void setHost(Host host) {
	// this.host = host;
	// }
	/**
	 * Determine if this object should be saved or not.
	 * 
	 * @return
	 */
	@Transient
	public boolean isSet() {
		return false;
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
		final ExtendedHostInfo other = (ExtendedHostInfo) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	// @Column(name = "settingsKey")
	// public String getKey() {
	// return key;
	// }
	//
	// public void setKey(String key) {
	// this.key = key;
	// }
}
