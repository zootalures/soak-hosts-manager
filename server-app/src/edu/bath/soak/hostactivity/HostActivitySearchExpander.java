package edu.bath.soak.hostactivity;

import java.util.Date;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import edu.bath.soak.net.model.Host;
import edu.bath.soak.net.query.HostSearchQuery;
import edu.bath.soak.query.SearchExpander;
import edu.bath.soak.query.SearchResult;
import org.hibernate.criterion.CriteriaSpecification;

public class HostActivitySearchExpander implements
		SearchExpander<Host, HostSearchQuery> {

	public Criteria expandSearchTerm(Criteria c, String searchTerm) {
		// TODO Auto-generated method stub
		return null;
	}

	public Criteria expandSearchQuery(HostSearchQuery search, Criteria crit) {
		HostActivitySearchFlags flags = (HostActivitySearchFlags) search
				.getAdditionalSearchConstraints().get(
						HostActivitySearchFlags.FLAG_KEY);
		boolean addLIU = false;
		if ((flags != null && flags.isFlagSet()) || search.getOrderBy() != null
				&& search.getOrderBy().contains("liu")) {
			crit.createAlias("lastUsageInfo", "liu",
					CriteriaSpecification.LEFT_JOIN);
		}
		if (flags != null) {
			Date fromDate = flags.getFromDate();
			Date toDate = flags.getToDate();
			if (null != fromDate) {
				addLIU = true;
				crit = crit.add(Restrictions.ge("liu.changedAt", fromDate));
			}
			if (null != toDate) {
				addLIU = true;
				crit = crit.add(Restrictions.le("liu.changedAt", toDate));
			}
		}

		return crit;
	}

	public void prepareSearchObject(HostSearchQuery search) {
		if (!search.getAdditionalSearchConstraints().containsKey(
				HostActivitySearchFlags.FLAG_KEY)) {
			search.getAdditionalSearchConstraints().put(
					HostActivitySearchFlags.FLAG_KEY,
					new HostActivitySearchFlags());
		}
	}

	public void postProcessResults(SearchResult<Host> result) {

	}

}
