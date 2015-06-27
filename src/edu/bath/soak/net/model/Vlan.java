package edu.bath.soak.net.model;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlTransient;

import edu.bath.soak.web.BeanView;

/**
 * Object describing a L2 802.1q Vlan Simple data descriptor, Id must be unique
 * and corresponds to the vlan identifier Name is relevant and should also
 * correspond to the short name in the vlan database (Cisco)
 * 
 * @author cspocc
 * 
 */
@Entity()
@BeanView("beanview/host/Vlan")
public class Vlan implements Serializable {
	private String name;
	private String description;
	private Long id;
	private Integer number;
	private Set<Subnet> subnets;;

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Transient
	@XmlTransient
	public String getStringRep() {
		return number + " : " + name;
	}

	public String toString() {
		return number + " (" + name + ")  ";
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Vlan) {
			return ((Vlan) obj).getId().equals(getId());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return getId().hashCode();
	}

	@XmlID
	@Transient
	@XmlElement(name = "number")
	public String getXmlID() {
		return number.toString();
	}

	public void setXmlID(String number) {
		if (number != null)
			this.number = Integer.parseInt(number);
		else
			this.number = null;
	}

	@XmlTransient
	@Column(nullable = false, name = "vlanNumber")
	public Integer getNumber() {
		return number;
	}

	public void setNumber(Integer number) {
		this.number = number;
	}

	@XmlTransient
	@OneToMany(targetEntity = Subnet.class, mappedBy = "vlan")
	public Set<Subnet> getSubnets() {
		return subnets;
	}

	public void setSubnets(Set<Subnet> subnets) {
		this.subnets = subnets;
	}
}
