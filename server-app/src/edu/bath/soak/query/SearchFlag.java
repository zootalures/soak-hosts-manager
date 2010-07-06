package edu.bath.soak.query;

import edu.bath.soak.net.query.HostSearchQuery;

/**
 * Interfaces for search extensions,
 * 
 * Objects extending this class may be appended to a {@link HostSearchQuery#addHostSearchFlag(Object,SearchFlag)} through the 
 * @author cspocc
 * 
 */
public interface SearchFlag {
	public String flagKey();
	public boolean isFlagSet();
}
