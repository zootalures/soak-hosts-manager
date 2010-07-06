package edu.bath.soak.net.model;

import java.io.Serializable;
import java.net.Inet4Address;
import java.util.Iterator;

import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlTransient;

import org.hibernate.annotations.Formula;
import org.hibernate.annotations.Type;

import edu.bath.soak.dhcp.model.DHCPScope;
import edu.bath.soak.model.ChangeInfo;
import edu.bath.soak.util.TypeUtils;

/**
 * Class which describes a range of IP addresses
 * 
 * @see Subnet
 * @see DHCPScope
 * @author cspocc
 * 
 */
@MappedSuperclass
public class IPRange implements Serializable {
	Inet4Address minIP;
	Inet4Address maxIP;
	ChangeInfo changeInfo;

	public IPRange() {

	}

	public IPRange(Inet4Address minIP, Inet4Address maxIP) {
		setMinIP(minIP);
		setMaxIP(maxIP);
	}

	@XmlTransient
	@Formula(value = " INET_NTOA(minIP)")
	public String getMinIPTxt() {
		return minIP.getHostAddress();
	}

	public void setMinIPTxt(String val) {

	}

	@XmlTransient
	@Formula(value = " INET_NTOA(maxIP)")
	public String getMaxIPTxt() {
		return maxIP.getHostAddress();
	}

	public void setMaxIPTxt(String val) {

	}

	@Type(type = "inet4type")
	public Inet4Address getMaxIP() {
		return maxIP;
	}

	public void setMaxIP(Inet4Address maxIP) {
		this.maxIP = maxIP;
	}

	@Type(type = "inet4type")
	public Inet4Address getMinIP() {
		return minIP;
	}

	public void setMinIP(Inet4Address minIP) {
		this.minIP = minIP;
	}

	public String toString() {
		return minIP.getHostAddress() + "-" + maxIP.getHostAddress();
	}

	@Transient
	@XmlTransient
	public long getNumAddresses() {
		return TypeUtils.ipToInt(getMaxIP()) - TypeUtils.ipToInt(getMinIP());
	}

	public Iterator<Inet4Address> allAddresses() {
		return new Iterator<Inet4Address>() {

			Inet4Address current = getMinIP();
			Inet4Address max = getMaxIP();

			public boolean hasNext() {
				return TypeUtils.ipCompare(current, max) <= 0;
			}

			public Inet4Address next() {
				Inet4Address next = current;
				current = TypeUtils.ipIncrement(current);
				return next;

			}

			public void remove() {
				throw new UnsupportedOperationException();

			}

		};
	}

	/**
	 * Checks if this range overlaps with another range
	 * 
	 * @param other
	 *            the range to compare with
	 * @return true if the IP addresses are conflicting
	 */
	public boolean clashesWith(IPRange other) {
		return !(TypeUtils.ipCompare(getMaxIP(), other.getMinIP()) < 0 || TypeUtils
				.ipCompare(getMinIP(), other.getMaxIP()) > 0);
	}

	/**
	 * determines if this range contains a given IP
	 * 
	 * @param ip
	 * @return true if it does, false if it doesnt
	 */
	public boolean containsIp(Inet4Address ip) {
		return !(TypeUtils.ipCompare(getMaxIP(), ip) < 0 || TypeUtils
				.ipCompare(getMinIP(), ip) > 0);
	}

	/**
	 * Returns true if the give address is in this range.
	 * 
	 * @param addr
	 * @return true if minIP <= addr <= maxIP
	 */
	public boolean inRange(Inet4Address addr) {
		return TypeUtils.ipInRange(addr, getMinIP(), getMaxIP());
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((maxIP == null) ? 0 : maxIP.hashCode());
		result = prime * result + ((minIP == null) ? 0 : minIP.hashCode());
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
		final IPRange other = (IPRange) obj;
		if (maxIP == null) {
			if (other.maxIP != null)
				return false;
		} else if (!maxIP.equals(other.maxIP))
			return false;
		if (minIP == null) {
			if (other.minIP != null)
				return false;
		} else if (!minIP.equals(other.minIP))
			return false;
		return true;
	}

}
