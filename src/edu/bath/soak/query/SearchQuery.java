package edu.bath.soak.query;

import java.util.Map;
import java.util.HashMap;
import java.util.Map.Entry;

public class SearchQuery {
	int numResults = -1;
	int firstResult = 0;
	String orderBy;
	boolean ascending = true;
	Map<String, SearchFlag> additionalSearchContstraints = new HashMap<String, SearchFlag>();

	public SearchQuery() {

	}

	public SearchQuery(int numResults, int skipResults) {
		this.numResults = numResults;
		this.firstResult = skipResults;
	}

	public boolean isAscending() {
		return ascending;
	}

	public void setAscending(boolean ascending) {
		this.ascending = ascending;
	}

	public boolean isGetAll() {
		return numResults == -1;
	}

	public int getMaxResults() {
		return numResults;
	}

	public void setMaxResults(int numResults) {
		this.numResults = numResults;
	}

	public String getOrderBy() {
		return orderBy;
	}

	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}

	public boolean isOptionsSet() {
		for(Entry<String, SearchFlag> flag:additionalSearchContstraints.entrySet()){
			if(flag.getValue().isFlagSet()){
				return true;
			}
		}
		return false;
	}

	public int getFirstResult() {
		return firstResult;
	}

	public void setFirstResult(int firstResult) {
		this.firstResult = firstResult;
	}

	public Map<String, SearchFlag> getAdditionalSearchConstraints() {
		return additionalSearchContstraints;
	}

	public void setAdditionalSearchConstraints(
			Map<String, SearchFlag> additionalSearchContstraints) {
		this.additionalSearchContstraints = additionalSearchContstraints;
	}
}
