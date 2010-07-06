package edu.bath.soak.dns.query;

import java.io.Serializable;

import edu.bath.soak.dns.model.DNSZone;
import edu.bath.soak.query.SearchQuery;

public class DNSSearchQuery extends SearchQuery implements Serializable {

	String searchTerm;
	String recordType;
	

	public String getSearchTerm() {
		return searchTerm;
	}

	public void setSearchTerm(String searchTerm) {
		this.searchTerm = searchTerm;
	}

	DNSZone dnsZone;

	public DNSZone getDnsZone() {
		return dnsZone;
	}

	public void setDnsZone(DNSZone dnsZone) {
		this.dnsZone = dnsZone;
	}

	public String getRecordType() {
		return recordType;
	}

	public void setRecordType(String recordType) {
		this.recordType = recordType;
	}
}
