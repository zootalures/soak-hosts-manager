package edu.bath.soak.dns;

import java.net.Inet4Address;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.xbill.DNS.ReverseMap;

import edu.bath.soak.dns.model.DNSDao;
import edu.bath.soak.dns.model.DNSRecord;
import edu.bath.soak.dns.model.DNSZone;
import edu.bath.soak.dns.model.ReverseZone;
import edu.bath.soak.mgr.AddressManagerAdvisor;
import edu.bath.soak.net.model.Host;
import edu.bath.soak.net.model.Subnet;

/**
 * Advisor for address allocator which queries DNS records for likely conflicts
 * with existing (possibly stray) PTR records.
 * 
 * @author cspocc
 * 
 */
public class DNSAddressManagerAdvisor implements AddressManagerAdvisor {
	DNSDao dnsDAO;
	Logger log = Logger.getLogger(this.getClass());

	/**
	 * Advises the allocator to prefer IP addresses which don't have existing
	 * PTR records before trying to those which do have PTR records.
	 * 
	 * @param h
	 *            the host to get advice for
	 * @param s
	 *            the subnet to give advice on
	 * @param addr
	 *            the address to advise on
	 * @return advice
	 */
	public AddressManagerAdvice getAdviceForAllocation(Host h, Subnet s,
			Inet4Address addr) {
		log.trace("looking for clashes on host:" + h + " in subnet " + s
				+ " for IP:" + addr);
		List<ReverseZone> revzones = dnsDAO.getReverseZones();

		String revname = ReverseMap.fromAddress(addr).toString();
		List<DNSRecord> records = dnsDAO.findRecordsInZones(revzones,  revname, "PTR");
		if (records.size() != 0) {
			log.trace("Advising against " + addr);

			return AddressManagerAdvice.PREFER_NOT;
		}

		return AddressManagerAdvice.OK;
	}

	@Required
	public void setDnsDAO(DNSDao dnsDAO) {
		this.dnsDAO = dnsDAO;
	}

	public int getOrder() {
		return 0;
	}
}
