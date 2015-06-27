package edu.bath.soak.mgr;

import java.util.Collection;
import java.util.Set;

import edu.bath.soak.net.model.Host;
import edu.bath.soak.net.query.HostSearchQuery;
import edu.bath.soak.query.SearchResult;

/**
 * "Starred" hosts manager
 * 
 * manages the users selection hosts hosts
 * 
 * @author cspocc
 * 
 */
public interface StarredHostsManager {

	/**
	 * Sets the given host as starred
	 * 
	 * The host must exist (i.e. have an ID)
	 * 
	 * @param host
	 * @param value
	 */
	public void setStarred(Host host, boolean value);

	/**
	 * Sets all hosts in the collection to be starred
	 * 
	 * All hosts must exist in the database
	 * 
	 * @param hosts
	 * @param value
	 */
	public void setStarred(Collection<Host> hosts, boolean value);
	/**
	 * Sets all hosts in the collection to be starred
	 * 
	 * All hosts must exist in the database
	 * 
	 * @param hosts
	 * @param value
	 */
	public void setStarredById(Collection<Long> hosts, boolean value);

	/**
	 * Determines if the given host is starred
	 * 
	 * @param h
	 * @return
	 */
	public boolean isStarred(Host h);

	/**
	 * Returns a set of all starred hosts
	 * 
	 * All hosts return must be guaranteed to exist at the point of calling
	 * 
	 * @return
	 */
	public Set<Host> getStarredHosts();

	/**
	 * Returns a set of hostIds which correspond to currently starred hosts,
	 * Note that this method does not have to guarantee that these hosts still
	 * exists
	 * 
	 * @return
	 */
	public Set<Long> getStarredHostIds();

	/**
	 * Performs a host search only on the starred hosts
	 * 
	 * @param query
	 * @return
	 */
	public SearchResult<Host> searchHosts(HostSearchQuery query);

	/**
	 * Clears all starred hosts
	 */
	public void clearStarredHosts();
}
