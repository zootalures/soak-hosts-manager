package edu.bath.soak.dhcp.model;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlID;

import edu.bath.soak.net.model.IPRange;

/**
 * Entity bean which handles
 * 
 * @author cspocc
 * 
 */
@Entity
public class DHCPScope extends IPRange implements Serializable {

	String name;
	String comment;
	Date fetchedOn;
	DHCPServer server;
	Long id;
	Set<DHCPReservation> reservations;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long getId() {
		return id;
	}

	@XmlID
	@Transient
	public String getXMLId() {
		return id.toString();
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public Date getFetchedOn() {
		return fetchedOn;
	}

	public void setFetchedOn(Date fetchedOn) {
		this.fetchedOn = fetchedOn;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean equals(Object o) {
		if (!(o instanceof DHCPScope))
			return false;
		DHCPScope r = (DHCPScope) o;
		return r.getServer().equals(getServer())
				&& r.getMinIP().equals(getMinIP())
				&& r.getMaxIP().equals(getMaxIP());

	}

	public int hashCode() {
		return server.hashCode() + getMinIP().hashCode()
				+ getMaxIP().hashCode();
	}

	@ManyToOne(optional = false)
	public DHCPServer getServer() {
		return server;
	}

	public void setServer(DHCPServer server) {
		this.server = server;
	}

	public String toString() {
		return "Range " + getMinIP().getHostAddress() + " - "
				+ getMaxIP().getHostAddress();
	}

	@OneToMany(targetEntity = StaticDHCPReservation.class, mappedBy = "scope")
	public Set<DHCPReservation> getReservations() {
		return reservations;
	}

	public void setReservations(Set<DHCPReservation> reservations) {
		this.reservations = reservations;
	}
}