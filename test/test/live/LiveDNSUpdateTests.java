package test.live;

import static test.live.LiveDNSModificationTests.assertRecordDoesNotExist;
import static test.live.LiveDNSModificationTests.assertRecordExists;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.test.AbstractTransactionalSpringContextTests;
import org.xbill.DNS.ARecord;
import org.xbill.DNS.CNAMERecord;
import org.xbill.DNS.DClass;
import org.xbill.DNS.Message;
import org.xbill.DNS.Name;
import org.xbill.DNS.Record;
import org.xbill.DNS.Resolver;
import org.xbill.DNS.SOARecord;
import org.xbill.DNS.TXTRecord;
import org.xbill.DNS.Type;
import org.xbill.DNS.Update;

import test.hosts.SpringSetup;
import edu.bath.soak.dns.DDNSServiceImpl;
import edu.bath.soak.dns.DNSUpdateMgr;
import edu.bath.soak.dns.model.DNSDao;
import edu.bath.soak.dns.model.DNSRecord;
import edu.bath.soak.dns.model.DNSZone;
import edu.bath.soak.dns.model.ForwardZone;
import edu.bath.soak.testutils.BindWrapper;
import edu.bath.soak.util.TypeUtils;

public class LiveDNSUpdateTests extends AbstractTransactionalSpringContextTests {

	DDNSServiceImpl dnsServiceImpl;
	DNSUpdateMgr dnsUpdateMgrImpl;
	DNSDao dnsDAO;

	DNSZone testZone;
	String testName = "unittesthosts.testdomain.";
	ARecord testARecord;
	BindWrapper bindWrapper;

	public LiveDNSUpdateTests() {
		setAutowireMode(AUTOWIRE_BY_NAME);
	}

	@Override
	protected String[] getConfigLocations() {
		return SpringSetup.BASIC_TEST_LOCS;
	}

	Logger log = Logger.getLogger(getClass());

	@Override
	protected void onSetUp() throws Exception {

		super.onSetUp();
		bindWrapper = new BindWrapper();
		String keyName = "soak-key";
		String keyData = "nNNm4hGjvsqaXKYlfQjWYdDCljBF6AtmtxNMqkabAllKPJcsubvmP3sbn3M79AQlm9jeyV4O7eFCtg6eIelF5g==";
		String testDN = "testdomain.";
		int port = 9053;
		testZone = new ForwardZone();
		testZone.setDomain("testdomain.");
		testZone.setDefaultTTL(3600L);
		testZone.setServerIP(TypeUtils.txtToIP("127.0.0.1"));
		testZone.setServerPort(port);

		testZone.setSigKey(keyName + ":" + keyData);
		dnsDAO.saveZone(testZone);

		bindWrapper.addKey(keyName, keyData);
		bindWrapper.addZone(testDN);
		bindWrapper.setPort(port);
		// bindWrapper.setDeleteOnCleanup(false);
		bindWrapper.startBind();
	}

	@Override
	protected void onTearDown() throws Exception {
		bindWrapper.cleanUp();
		super.onTearDown();

	}

	/**
	 * Tests a full zone update
	 * 
	 * @throws Exception
	 */
	public void testUpdateZoneFull() throws Exception {
		dnsUpdateMgrImpl.updateZoneData(testZone, false);
		List<DNSRecord> recs = dnsDAO.getAllRecordsForZone(testZone);
		SOARecord soarec = null;
		for (DNSRecord rec : recs) {
			log.trace("Got record" + rec);
			if (rec.getType() == "SOA") {
				// there must be only one SOA
				assertNull(soarec);
				soarec = (SOARecord) rec.getRecord();
			}
		}
		// every zone must have an SOA
		assertNotNull(soarec);
		// should just have the SOA and the NS
		assertEquals(2, recs.size());
		// The Serial in the zone must match the SOA serial
		assertEquals((long) soarec.getSerial(), (long) testZone.getSerial());
	}

	/**
	 * Tests an incremental update
	 * 
	 * @throws Exception
	 */
	public void testUpdateZoneIncrementalAfterAdd() throws Exception {
		dnsUpdateMgrImpl.updateZoneData(testZone, false);
		Update u = new Update(Name.fromString(testZone.getDomain()));
		testARecord = new ARecord(Name.fromString(testName), DClass.IN, 3600,
				TypeUtils.txtToIP("138.38.0.2"));

		u.add(testARecord);

		Resolver r = dnsServiceImpl.getResolverForZone(testZone);
		Message m = r.send(u);
		assertEquals(0,m.getRcode());
		Thread.sleep(2000);

		assertRecordExists(r, testARecord);
		dnsUpdateMgrImpl.updateZoneData(testZone, false);
		DNSRecord ar = DNSRecord.fromDNSRecord(testZone, testARecord);
		List<DNSRecord> recs = dnsDAO.getAllRecordsForZone(testZone);
		assertTrue(recs.contains(ar));
		for (DNSRecord rec : recs) {
			log.trace("Got record" + rec);
		}
		// should just have the SOA and the NS
		assertEquals(3, recs.size());

	}

	public void testUpdateZoneAfterDelete() throws Exception {
		testUpdateZoneIncrementalAfterAdd();
		assertNotNull(testARecord);

		// check that the record exists in both the server and the cache
		List<DNSRecord> zonerecs = dnsDAO.getAllRecordsForZone(testZone);
		assertTrue(zonerecs.contains(DNSRecord.fromDNSRecord(testZone,
				testARecord)));

		Resolver resolver = dnsServiceImpl.getResolverForZone(testZone);
		assertRecordExists(resolver, testARecord);

		// Delete the record
		Update u = new Update(Name.fromString(testZone.getDomain()));
		u.delete(testARecord.getName(), testARecord.getType());
		resolver.send(u);
		Thread.sleep(2000);

		// check that the record doesn't exist in both the server and the cache
		LiveDNSModificationTests
				.assertRecordDoesNotExist(resolver, testARecord);
		dnsUpdateMgrImpl.updateZoneData(testZone, false);
		zonerecs = dnsDAO.getAllRecordsForZone(testZone);
		assertFalse(zonerecs.contains(testARecord));

	}

	public void testUpdateZoneMultiple() throws Exception {
		dnsUpdateMgrImpl.updateZoneData(testZone, false);

		List<Record> newrecs = new ArrayList<Record>();
		newrecs.add(new ARecord(Name
				.fromString("test1." + testZone.getDomain()), DClass.IN, 3600L,
				TypeUtils.txtToIP("10.0.0.2")));

		newrecs.add(new ARecord(Name
				.fromString("test2." + testZone.getDomain()), DClass.IN, 3600L,
				TypeUtils.txtToIP("10.0.0.3")));
		newrecs.add(new ARecord(Name
				.fromString("test3." + testZone.getDomain()), DClass.IN, 3600L,
				TypeUtils.txtToIP("10.0.0.4")));
		newrecs.add(new ARecord(Name
				.fromString("test4." + testZone.getDomain()), DClass.IN, 3600L,
				TypeUtils.txtToIP("10.0.0.5")));
		newrecs.add(new ARecord(Name
				.fromString("test5." + testZone.getDomain()), DClass.IN, 3600L,
				TypeUtils.txtToIP("10.0.0.6")));
		newrecs.add(new CNAMERecord(Name.fromString("test6."
				+ testZone.getDomain()), DClass.IN, 3600L, Name
				.fromString("test1." + testZone.getDomain())));
		newrecs.add(new TXTRecord(Name.fromString("test7."
				+ testZone.getDomain()), DClass.IN, 3600L, "name=foo"));
		Resolver resolver = dnsServiceImpl.getResolverForZone(testZone);
		Message rc;
		log.info("ADDDING some records");
		{
			// Add some records to the zone and check that they show up
			Update update = new Update(Name.fromString(testZone.getDomain()));
			for (Record rec : newrecs) {
				update.absent(rec.getName(), rec.getType());
				update.add(rec);
			}

			rc = resolver.send(update);
			assertTrue(0 == rc.getRcode());

			dnsUpdateMgrImpl.updateZoneData(testZone, false);
			List<DNSRecord> allrecs1 = dnsDAO.getAllRecordsForZone(testZone);
			for (Record rec : newrecs) {
				assertTrue(allrecs1.contains(DNSRecord.fromDNSRecord(testZone,
						rec)));
				assertRecordExists(resolver, rec);
			}
		}
		// Now delete some of those and check that they have gone
		log.info("DELETING some of those records");

		List<Record> delrecs = new ArrayList<Record>();
		delrecs.add(newrecs.get(0));
		delrecs.add(newrecs.get(2));
		delrecs.add(newrecs.get(4));

		{
			Update delupdate = new Update(Name.fromString(testZone.getDomain()));
			for (Record rec : delrecs) {
				delupdate.delete(rec.getName(), rec.getType());
			}

			rc = resolver.send(delupdate);
			assertTrue(0 == rc.getRcode());

		}

		dnsUpdateMgrImpl.updateZoneData(testZone, false);
		List<DNSRecord> allrecs2 = dnsDAO.getAllRecordsForZone(testZone);

		for (Record rec : delrecs) {
			assertFalse(allrecs2.contains(DNSRecord
					.fromDNSRecord(testZone, rec)));
			assertRecordDoesNotExist(resolver, rec);
		}

		List<Record> newrecs2 = new ArrayList<Record>();
		newrecs2.add(new ARecord(Name.fromString("test10."
				+ testZone.getDomain()), DClass.IN, 3600L, TypeUtils
				.txtToIP("10.0.0.10")));
		newrecs2.add(new ARecord(Name.fromString("test11."
				+ testZone.getDomain()), DClass.IN, 3600L, TypeUtils
				.txtToIP("10.0.0.11")));

		newrecs2.add(new ARecord(Name.fromString("test12."
				+ testZone.getDomain()), DClass.IN, 3600L, TypeUtils
				.txtToIP("10.0.0.12")));
		{
			Update alldelupdate = new Update(Name.fromString(testZone
					.getDomain()));
			log.info("DELETING all old records and ADDING some more ");
			// Now delete all records from the zone and add some more
			for (Record rec : newrecs) {
				alldelupdate.delete(rec);
			}

			for (Record rec : newrecs2) {
				alldelupdate.add(rec);
			}
			rc = resolver.send(alldelupdate);
			assertTrue(0 == rc.getRcode());
		}

		List<Record> delrecs2 = new ArrayList<Record>();
		delrecs2.add(newrecs2.get(0));
		{
			// actually let's delete a couple of those and update again

			Update deleteagainupdate = new Update(Name.fromString(testZone
					.getDomain()));

			for (Record rec : delrecs2) {
				deleteagainupdate.delete(rec);
			}
			log.info("DELETING one of those");
			rc = resolver.send(deleteagainupdate);
			assertTrue(0 == rc.getRcode());
		}
		// finally let's update and check that everything is as it should be.
		log.info("doing final update ");

		dnsUpdateMgrImpl.updateZoneData(testZone, false);
		List<DNSRecord> finalgotrecs = dnsDAO.getAllRecordsForZone(testZone);
		for (DNSRecord gotrec : finalgotrecs) {
			Record dnsr = gotrec.getRecord();
			assertFalse(newrecs.contains(dnsr));
			if (dnsr.getType() != Type.NS && dnsr.getType() != Type.SOA)
				assertTrue(newrecs2.contains(dnsr));
		}
		for (Record dnsr : newrecs2) {
			DNSRecord dbrec = DNSRecord.fromDNSRecord(testZone, dnsr);
			assertTrue(delrecs2.contains(dnsr) || finalgotrecs.contains(dbrec));
		}

		// //now delete the remaining records and make sure that the DNS is back
		// to scratch
		Update cleanup = new Update(Name.fromString(testZone.getDomain()));
		for (Record dnsr : newrecs2) {
			cleanup.delete(dnsr);
		}
		rc = resolver.send(cleanup);
		assertEquals(0, rc.getRcode());
		dnsUpdateMgrImpl.updateZoneData(testZone, false);
		List<DNSRecord> cleanedrecs = dnsDAO.getAllRecordsForZone(testZone);
		// should just be the NS and the SOA
		assertEquals(2, cleanedrecs.size());

	}

	public void testRunMultipleLotsOfTimes() throws Exception {
		for (int i = 0; i < 100; i++) {
			testUpdateZoneMultiple();
		}
		dnsUpdateMgrImpl.updateZoneData(testZone, false);
		List<DNSRecord> cleanedrecs = dnsDAO.getAllRecordsForZone(testZone);
		// should just be the NS and the SOA
		assertEquals(2, cleanedrecs.size());
	}

	public void setDnsDAO(DNSDao dnsDAO) {
		this.dnsDAO = dnsDAO;
	}

	public void setDnsService(DDNSServiceImpl dnsServiceImpl) {
		this.dnsServiceImpl = dnsServiceImpl;
	}

	public void setDnsUpdateMgr(DNSUpdateMgr dnsUpdateMgrImpl) {
		this.dnsUpdateMgrImpl = dnsUpdateMgrImpl;
	}

}
