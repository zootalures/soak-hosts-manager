package edu.bath.soak.dnseditor.web;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;
import org.springframework.webflow.action.FormAction;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;

import edu.bath.soak.dns.DNSHostsInterceptor;
import edu.bath.soak.dns.model.DNSDao;
import edu.bath.soak.dns.model.DNSRecord;
import edu.bath.soak.dns.query.DNSSearchQuery;
import edu.bath.soak.query.SearchResult;

public class SearchDNSFormController extends FormAction {
	DNSDao dnsDAO;
	DNSHostsInterceptor dnsHostsInterceptor;

	@Override
	protected Object createFormObject(RequestContext context) throws Exception {
		// TODO Auto-generated method stub

		DNSSearchQuery query = new DNSSearchQuery();
		query.setMaxResults(50);
		query.setOrderBy("hostName");
		query.setAscending(true);
		return query;
	}

	public Event doSearch(RequestContext context) throws Exception {
		DNSSearchQuery command = (DNSSearchQuery) getFormObject(context);
		Assert.notNull(command);

		SearchResult<DNSRecord> result = dnsDAO.searchRecords(command);
		HashMap<DNSRecord, Boolean> hasHost = new HashMap<DNSRecord, Boolean>();
		for (DNSRecord rec : result.getResults()) {
			try {
				hasHost.put(rec, dnsHostsInterceptor.hostExistsForRecord(rec));
			} catch (Exception e) {

			}
		}
		context.getFlashScope().put("results", result);
		context.getFlashScope().put("hasHost", hasHost);
		return success();
	}

	@Required
	public void setDnsDAO(DNSDao dnsDAO) {
		this.dnsDAO = dnsDAO;
	}

	@Required
	public void setDnsHostsInterceptor(DNSHostsInterceptor dnsHostsInterceptor) {
		this.dnsHostsInterceptor = dnsHostsInterceptor;
	}
}
