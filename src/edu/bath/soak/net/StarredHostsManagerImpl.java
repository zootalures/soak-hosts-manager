package edu.bath.soak.net;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;

import edu.bath.soak.mgr.StarredHostsManager;
import edu.bath.soak.net.model.Host;
import edu.bath.soak.net.model.NetDAO;
import edu.bath.soak.net.query.HostSearchQuery;
import edu.bath.soak.query.SearchResult;

/**
 * Session scoped manager which stores a list of "starred" (i.e. selected) hosts
 * 
 * 
 * This bean only stores the Ids of starred hosts and not the host objects
 * themselves,
 * 
 * Hosts are checked for existence in the database when they are added and in
 * the case of {@link #getStarredHosts()} when they are retrieved
 * 
 * @author cspocc
 * 
 */
public class StarredHostsManagerImpl implements StarredHostsManager,
		Serializable {
	NetDAO hostsDAO;
	StarredHostsHolder holder;

	/**
	 * Returns the current set of hosts which is selected . Note that only those
	 * hosts which exist and are marked as starred will be returned
	 * 
	 * We oportunistically update the list of Starred host ids here to reflect
	 * what is actually there
	 */
	public Set<Host> getStarredHosts() {
		Set<Long> newStarredHostIds = new HashSet<Long>();

		HashSet<Host> hosts = new HashSet<Host>();
		if (holder.size() > 0) {
			hosts
					.addAll(hostsDAO.findHostsByIdList(holder
							.getStarredHostIds()));
			for (Host h : hosts)
				newStarredHostIds.add(h.getId());
		}
		holder.setStarredHostIds(newStarredHostIds);

		return hosts;
	}

	/**
	 * determines if a given host is selected (this is a fast operation)
	 */
	public boolean isStarred(Host h) {
		Assert.notNull(h.getId());
		return holder.contains(h.getId());
	}

	/**
	 * Sets a given host as being starred or un-starred based on the value of
	 * "value"
	 * 
	 * 
	 */
	public void setStarred(Host host, boolean value) {
		Assert.notNull(host.getId());
		if (value) {
			holder.getStarredHostIds().add(host.getId());

		} else {
			holder.getStarredHostIds().remove(host.getId());
		}
	}

	/***************************************************************************
	 * 
	 * Searches the starred hosts using the standard search API.
	 * 
	 * @param query
	 *            a {@link HostSearchQuery} object to query on,
	 */
	public SearchResult<Host> searchHosts(HostSearchQuery query) {
		return hostsDAO.searchHostsInIdSet(query, getStarredHostIds());

	}

	public Set<Long> getStarredHostIds() {
		return holder.getStarredHostIds();
	}

	/**
	 * Sets all hosts in the collection to be starred
	 * 
	 * All hosts must exist in the database
	 * 
	 * @param hosts
	 * @param value
	 */
	public void setStarredById(Collection<Long> hosts, boolean value) {
		if (value) {
			holder.addAll(hosts);
		} else {
			holder.removeAll(hosts);
		}
	}

	public void setStarred(Collection<Host> hosts, boolean starred) {
		Assert.notNull(hosts, "hosts collection must not be null");
		for (Host h : hosts) {
			Assert
					.notNull(h.getId(),
							"Host must have an ID when being starred");
			if (starred) {
				holder.add(h.getId());
			} else {
				holder.remove(h.getId());
			}
		}

	}

	public void clearStarredHosts() {
		holder.clear();
	}

	@Required
	public void setHostsDAO(NetDAO hostsDAO) {
		this.hostsDAO = hostsDAO;
	}

	@Required
	public void setHolder(StarredHostsHolder holder) {
		this.holder = holder;
	}

}
