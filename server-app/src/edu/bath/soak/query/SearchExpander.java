package edu.bath.soak.query;

import java.io.Serializable;

import org.hibernate.Criteria;

public interface SearchExpander<ObjectType extends Serializable, Type extends SearchQuery> {

	/**
	 * Called before the search is run
	 * 
	 * @param search
	 */
	public void prepareSearchObject(Type search);

	/**
	 * Expand a specific search term.
	 * 
	 * Plugins should only return a value if they have matched the term,
	 * otherwise they should return null
	 * 
	 * @param c
	 *            The criteria to expand
	 * @param searchTerm
	 *            The search term to elaborate on
	 * @return a criteria matching the specified search timer iff the matcher
	 *         wishes to match the term, otherwise null
	 */
	public Criteria expandSearchTerm(Criteria c, String searchTerm);

	/**
	 * Expand the actual search criteria
	 * 
	 * @param search
	 * @param crit
	 * @return
	 */
	public Criteria expandSearchQuery(Type search, Criteria crit);

	/**
	 * Perform optional post-processing on the search results
	 * 
	 * @param result
	 */
	public void postProcessResults(SearchResult<ObjectType> result);
}
