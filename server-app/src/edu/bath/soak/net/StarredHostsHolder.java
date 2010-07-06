package edu.bath.soak.net;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Session-injection oriented holder for a set of hostId.
 * 
 * @author cspocc
 * 
 */
public class StarredHostsHolder implements Serializable{
	Set<Long> starredHostIds = new HashSet<Long>();

	public Set<Long> getStarredHostIds() {
		return starredHostIds;
	}

	public void setStarredHostIds(Set<Long> starredHostIds) {
		this.starredHostIds = starredHostIds;
	}

	public boolean add(Long o) {
		return starredHostIds.add(o);
	}

	public boolean addAll(Collection<? extends Long> c) {
		return starredHostIds.addAll(c);
	}

	public void clear() {
		starredHostIds.clear();
	}

	public boolean contains(Object o) {
		return starredHostIds.contains(o);
	}

	public boolean containsAll(Collection<?> c) {
		return starredHostIds.containsAll(c);
	}

	public int size() {
		return starredHostIds.size();
	}

	public boolean remove(Object o) {
		return starredHostIds.remove(o);
	}

	public boolean removeAll(Collection<?> c) {
		return starredHostIds.removeAll(c);
	}

}
