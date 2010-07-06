/**
 * 
 */
package edu.bath.soak.net.model;

import java.io.Serializable;
import java.util.regex.Pattern;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlID;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.sun.xml.txw2.annotation.XmlElement;

import edu.bath.soak.model.OrgUnitAcl;
import edu.bath.soak.model.OrgUnitAclEntity;
import edu.bath.soak.web.BeanView;

@Entity()
//@Proxy(lazy = false)
@XmlElement
@Cache(usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@BeanView("beanview/host/HostClass")
public class HostClass implements Serializable, Comparable<HostClass>,
		OrgUnitAclEntity {
	private String id;
	private String name;
	private String description;
	private DHCP_STATUS dhcpStatus = DHCP_STATUS.IF_POSSIBLE;
	private String allowedNamePatterns = "^.*$";
	private Pattern[] allowedNamePatternsCompiled;
	private String exampleName;
	private OrgUnitAcl orgUnitAcl = new OrgUnitAcl();
	private Long dnsTTL;
	private Boolean canHaveAliases;

	public enum DHCP_STATUS {
		REQUIRED, IF_POSSIBLE, NONE
	};

	public String getDescription() {

		return description;

	}

	public void setDescription(String description) {

		this.description = description;

	}

	@Id
	@XmlID
	public String getId() {

		return id;

	}

	public String toString() {

		return id;

	}

	public void setId(String id) {

		this.id = id;

	}

	public String getName() {

		return name;

	}

	public void setName(String name) {

		this.name = name;

	}

	@Column(nullable = false)
	public DHCP_STATUS getDHCPStatus() {

		return dhcpStatus;

	}

	public void setDHCPStatus(DHCP_STATUS dhcpStatus) {

		this.dhcpStatus = dhcpStatus;

	}

	@Override
	public int hashCode() {

		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((description == null) ? 0 : description.hashCode());
		result = prime * result
				+ ((dhcpStatus == null) ? 0 : dhcpStatus.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());

		return result;

	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final HostClass other = (HostClass) obj;
		if (description == null) {
			if (other.description != null) {

				return false;
			}
		} else if (!description.equals(other.description))
			return false;
		if (dhcpStatus == null) {
			if (other.dhcpStatus != null) {

				return false;
			}
		} else if (!dhcpStatus.equals(other.dhcpStatus))
			return false;
		if (id == null) {
			if (other.id != null) {

				return false;
			}
		} else if (!id.equals(other.id))
			return false;
		if (name == null) {
			if (other.name != null) {

				return false;
			}
		} else if (!name.equals(other.name))
			return false;

		return true;

	}

	public String getAllowedNamePatterns() {

		return allowedNamePatterns;

	}

	public void setAllowedNamePatterns(String allowedNamePatterns) {

		this.allowedNamePatterns = allowedNamePatterns;

	}

	@Transient
	public java.util.regex.Pattern[] getAllowedNamePatternsCompiled() {

		String ignoreRes;
		Pattern[] compiledREs;
		if (allowedNamePatternsCompiled != null) {

			return allowedNamePatternsCompiled;
		}
		if ((ignoreRes = allowedNamePatterns) != null) {
			String[] stringres = ignoreRes.split("\n");
			compiledREs = new Pattern[stringres.length];
			int i = 0;
			for (String re : stringres) {
				re = re.trim();
				if (!re.startsWith("^")) {
					re = "^.* " + re;
				}
				if (!re.endsWith("$")) {
					re = re + ".*$";
				}
				compiledREs[i++] = java.util.regex.Pattern.compile(re.trim());
			}

		} else {
			compiledREs = new Pattern[0];

		}

		return allowedNamePatternsCompiled = compiledREs;

	}

	/**
	 * returns true if the given host name (excluding domain name) matches one
	 * of the regular expressions which are allwoedd for this host class ;
	 * 
	 * @param name
	 * @return
	 */
	public boolean hostNameMatchesAllowed(String name) {

		for (Pattern pattern : getAllowedNamePatternsCompiled()) {
			if (pattern.matcher(name).matches()) {
				return true;
			}
		}

		return false;

	}

	public String getExampleName() {

		return exampleName;

	}

	public void setExampleName(String exampleName) {

		this.exampleName = exampleName;

	}

	public int compareTo(HostClass hc) {

		if (hc == null) {

			return -1;
		}

		return id.compareTo(hc.getId());

	}

	@Embedded
	public OrgUnitAcl getOrgUnitAcl() {

		return orgUnitAcl;

	}

	public void setOrgUnitAcl(OrgUnitAcl orgUnitAcl) {

		this.orgUnitAcl = orgUnitAcl;

	}

	public OrgUnitAclEntity aclParent() {

		return null;

	}

	public Long getDnsTTL() {

		return dnsTTL;

	}

	public void setDnsTTL(Long dnsTTL) {

		this.dnsTTL = dnsTTL;

	}

	public Boolean getCanHaveAliases() {

		return canHaveAliases;

	}

	public void setCanHaveAliases(Boolean canHaveAliases) {

		this.canHaveAliases = canHaveAliases;

	}
}