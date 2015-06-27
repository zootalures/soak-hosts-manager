package edu.bath.soak.net;

import org.hibernate.Criteria;

import edu.bath.soak.net.query.HostSearchQuery;

public interface HostSearchInterceptor {
	public void prepareNewSearch(HostSearchQuery cmd);
	public void extendSearchCriteria(Criteria hostSearchCriteria);
	
}
