package edu.bath.soak.dhcp;

import edu.bath.soak.query.SearchQuery;

public class DHCPReservationChangeQuery extends SearchQuery{
		String searchTerm;

		public String getSearchTerm() {
			return searchTerm;
		}

		public void setSearchTerm(String searchTerm) {
			this.searchTerm = searchTerm;
		}

}
