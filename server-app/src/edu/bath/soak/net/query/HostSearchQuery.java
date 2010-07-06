package edu.bath.soak.net.query;

import java.util.HashMap;
import java.util.Map;

import edu.bath.soak.model.OrgUnit;
import edu.bath.soak.net.model.HostClass;
import edu.bath.soak.net.model.NameDomain;
import edu.bath.soak.net.model.Subnet;
import edu.bath.soak.query.SearchFlag;
import edu.bath.soak.web.BeanView;
import edu.bath.soak.web.BeanViews;

/**
 * A host search command
 * 
 * @author cspocc
 * 
 */
@BeanView("beanview/host/HostSearchQuery")
public class HostSearchQuery extends HostQuery {
	String searchTerm;

	HostClass hostClass;
	Subnet subnet;
	NameDomain nameDomain;
	OrgUnit orgUnit;
	Map<Object, SearchFlag> searchFlags = new HashMap<Object, SearchFlag>();
	Boolean onlyIncludeMyHosts;

	Integer page;

	/**
	 * The textual search term(s) to apply to the search
	 * 
	 * terms are seperated and
	 * 
	 * @return
	 */
	public String getSearchTerm() {
		return searchTerm;
	}

	public void setSearchTerm(String searchTerm) {
		this.searchTerm = searchTerm;
	}

	public HostClass getHostClass() {
		return hostClass;
	}

	public void setHostClass(HostClass hostClass) {
		this.hostClass = hostClass;
	}

	public Subnet getSubnet() {
		return subnet;
	}

	public void setSubnet(Subnet subnet) {
		this.subnet = subnet;
	}

	public NameDomain getNameDomain() {
		return nameDomain;
	}

	public void setNameDomain(NameDomain nameDomain) {
		this.nameDomain = nameDomain;
	}

	public Integer getPage() {
		return page;
	}

	public void setPage(Integer page) {
		this.page = page;
	}

	public Map<Object, SearchFlag> getSearchFlags() {
		return searchFlags;
	}

	public void setSearchFlags(Map<Object, SearchFlag> searchFlags) {
		this.searchFlags = searchFlags;
	}

	public OrgUnit getOrgUnit() {
		return orgUnit;
	}

	public void setOrgUnit(OrgUnit orgUnit) {
		this.orgUnit = orgUnit;
	}

	public boolean isOptionsSet() {
		return orgUnit != null || nameDomain != null || subnet != null
				|| hostClass != null || anySearchFlagsSet()
				|| super.isOptionsSet();
	}

	boolean anySearchFlagsSet() {
		for (SearchFlag flag : searchFlags.values()) {
			if (flag.isFlagSet())
				return true;
		}
		return false;
	}

	/**
	 * Add a search flag to this search
	 * 
	 * @param key
	 * @param flag
	 */
	public void addHostSearchFlag(Object key, SearchFlag flag) {
		searchFlags.put(key, flag);
	}

	public Boolean getOnlyIncludeMyHosts() {
		return onlyIncludeMyHosts;
	}

	public void setOnlyIncludeMyHosts(Boolean onlyIncludeMyHosts) {
		this.onlyIncludeMyHosts = onlyIncludeMyHosts;
	}
}
