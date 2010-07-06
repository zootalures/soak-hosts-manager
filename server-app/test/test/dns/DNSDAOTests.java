package test.dns;

import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.test.AbstractTransactionalSpringContextTests;
import org.xbill.DNS.Name;
import org.xbill.DNS.Record;
import org.xbill.DNS.Type;

import test.hosts.SpringSetup;
import edu.bath.soak.dns.model.DNSDao;
import edu.bath.soak.dns.model.DNSRecord;
import edu.bath.soak.dns.model.DNSZone;

public class DNSDAOTests extends AbstractTransactionalSpringContextTests {
	Logger log = Logger.getLogger(DNSDAOTests.class);

	public DNSDAOTests() {
		setAutowireMode(AUTOWIRE_BY_NAME);
	}

	DNSDao dnsDAO;

	@Override
	protected String[] getConfigLocations() {
		return SpringSetup.DNS_TEST_LOCS;
	}

	/**
	 * Tests the record caching
	 * 
	 * @throws Exception
	 */
	public void testRcache() throws Exception {

		System.out.println("Getting zones");
		List<DNSZone> zones = dnsDAO.getAllManagedZones();

		System.out.println("Got " + zones.size() + " zones");

		DNSZone testZone = zones.get(0);

		Set<DNSRecord> recs = testZone.getZoneRecords();

		System.out.println("Got " + recs.size() + " records for zone "
				+ testZone);
		for (DNSRecord r : recs) {
			System.out.println("Got record " + r + " from zone " + r.getZone());
			Record dr = r.getRecord();
			assertNotNull(dr);
			System.out.println("Native record:" + dr);
		}

		List<DNSRecord> frecs = dnsDAO.findRecords(testZone, Name
				.fromString("newhost.campus.test."), Type.A);
		for (DNSRecord r : frecs) {
			System.out.println("Got record " + r + " from zone " + r.getZone());
		}

		frecs = dnsDAO.findRecords(testZone, Name
				.fromString("newhost.campus.test."), Type.ANY);
		for (DNSRecord r : frecs) {
			System.out.println("Got record " + r + " from zone " + r.getZone());
		}

	}

	@Required
	public void setDnsDAO(DNSDao dnsDao) {
		this.dnsDAO = dnsDao;
	}
}
