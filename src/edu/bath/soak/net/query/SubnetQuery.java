package edu.bath.soak.net.query;

import edu.bath.soak.query.SearchQuery;

public class SubnetQuery extends SearchQuery{
	
	String searchTerm; 
	
	public String toString(){
		return "SubnetQuery:" + searchTerm;
	}

	public String getSearchTerm() {
		return searchTerm;
	}

	public void setSearchTerm(String searchTerm) {
		this.searchTerm = searchTerm;
	}
}
