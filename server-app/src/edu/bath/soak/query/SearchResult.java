package edu.bath.soak.query;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * Abstract class for search results.
 * 
 * @author cspocc
 * 
 * @param <T>
 *            the type of the search results.
 */
public class SearchResult<T extends Serializable> implements java.io.Serializable{

	public static class SearchPage {
		int pageNumber;
		int startOffset;

		public int getPageNumber() {
			return pageNumber;
		}

		public void setPageNumber(int pageNumber) {
			this.pageNumber = pageNumber;
		}

		public int getStartOffset() {
			return startOffset;
		}

		public void setStartOffset(int startOffset) {
			this.startOffset = startOffset;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + pageNumber;
			result = prime * result + startOffset;
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			final SearchPage other = (SearchPage) obj;
			if (pageNumber != other.pageNumber)
				return false;
			if (startOffset != other.startOffset)
				return false;
			return true;
		}
	}

	List<T> results;
	int totalResults;
	SearchQuery query;
	int firstResultOffset;

	public int getCurrentPageNumber() {
		int numResultsPerPage = query.getMaxResults();
		if (numResultsPerPage > 0) {
			return firstResultOffset / numResultsPerPage;
		} else {
			return 0;
		}
	}

	public SearchPage getCurrentPage() {
		SearchPage sp = new SearchPage();
		int currentPageNo = getCurrentPageNumber();
		sp.setPageNumber(currentPageNo);
		if (query.getMaxResults() > 0) {
			sp.setStartOffset(currentPageNo * query.getMaxResults());
		} else {
			sp.setStartOffset(0);
		}
		return sp;
	}

	public List<SearchPage> getSearchPages() {
		ArrayList<SearchPage> pages = new ArrayList<SearchPage>();
		if (query.getMaxResults() > 0) {
			for (int i = 0; i < totalResults; i += query.getMaxResults()) {
				SearchPage sp = new SearchPage();
				sp.setStartOffset(i);
				sp.setPageNumber(i / query.getMaxResults());
				pages.add(sp);

			}
		} else {
			pages.add(getCurrentPage());
		}
		return pages;

	}

	public int getNumPages(int resultsPerPage) {
		if (query.getMaxResults() > 0) {
			return totalResults / query.getMaxResults();

		}
		return 1;
	}

	public SearchResult(SearchQuery q) {
		this.query = q;
	}

	public int getFirstResultOffset() {
		return firstResultOffset;
	}

	public void setFirstResultOffset(int firstResultOffset) {
		this.firstResultOffset = firstResultOffset;
	}

	public List<T> getResults() {
		return results;
	}

	public void setResults(List<T> results) {
		this.results = results;
	}

	public int getLastResultOffset() {
		return getFirstResultOffset() + results.size();
	}

	public int getNumResultsReturned() {
		return results.size();
	}

	public int getTotalResults() {
		return totalResults;
	}

	public void setTotalResults(int totalResults) {
		this.totalResults = totalResults;
	}

	public SearchQuery getQuery() {
		return query;
	}

	public void setQuery(SearchQuery query) {
		this.query = query;
	}

}
