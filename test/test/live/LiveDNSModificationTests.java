package test.live;

import java.net.Inet4Address;
import java.util.Random;

import junit.framework.TestCase;

import org.xbill.DNS.ARecord;
import org.xbill.DNS.DClass;
import org.xbill.DNS.Lookup;
import org.xbill.DNS.Name;
import org.xbill.DNS.Record;
import org.xbill.DNS.Resolver;
import org.xbill.DNS.Update;

import edu.bath.soak.dns.DDNSServiceImpl;
import edu.bath.soak.dns.model.DNSZone;
import edu.bath.soak.dns.model.ForwardZone;
import edu.bath.soak.testutils.BindWrapper;
import edu.bath.soak.util.TypeUtils;

public class LiveDNSModificationTests extends TestCase {

	DDNSServiceImpl dnsServiceImpl;

	String testName = "unittesthosts.testdomain.";
	ARecord testARecord;
	DNSZone testZone;
	BindWrapper bind;

	@Override
	protected void setUp() throws Exception {
		dnsServiceImpl = new DDNSServiceImpl();
		testZone = new ForwardZone();
		testZone.setDomain("testdomain.");
		testZone.setDefaultTTL(3600L);
		testZone.setServerIP(TypeUtils.txtToIP("127.0.0.1"));
		testZone.setServerPort(9053);
		String testkey = "soak-key";
		String testkeydata = "tbd_soak_key";

		testZone.setSigKey(testkey + ":" + testkeydata);
		bind = new BindWrapper();
		bind.addZone("testdomain.");
		bind.addKey(testkey, testkeydata);
		bind.setPort(9053);
		bind.startBind();

	}

	protected void tearDown() throws Exception {
		bind.cleanUp();

	}

	public static void assertRecordExists(Resolver resolver, Record rec) {
		Lookup l = new Lookup(rec.getName(), rec.getType());
		l.setResolver(resolver);
		l.run();
		Lookup.getDefaultCache(DClass.IN).clearCache();
		Record[] answers = l.getAnswers();
		assertNotNull(answers);

		for (Record gotAnswer : answers) {
			if (gotAnswer.equals(rec)) {
				return;
			}
		}
		fail("Record " + rec + " not present");
	}

	public static void assertRecordDoesNotExist(Resolver resolver, Record rec) {
		Lookup l = new Lookup(rec.getName(), rec.getType());
		Lookup.getDefaultCache(DClass.IN).clearCache();
		l.setResolver(resolver);
		l.run();
		Record[] answers = l.getAnswers();
		if(answers==null){
			assertTrue(l.getResult()==Lookup.HOST_NOT_FOUND);
			return;
		}

		for (Record gotAnswer : answers) {
			if (gotAnswer.equals(rec)) {
				fail("record " + rec + "exists in domain");
			}
		}
	}
	
	

	/* Tests the creation of a host record */
	public void testCreateRecord() throws Exception {
		Update u = new Update(Name.fromString(testZone.getDomain()));

		Random rand = new Random(System.currentTimeMillis());
		Inet4Address randip = TypeUtils.ipMath(TypeUtils.txtToIP("127.0.0.1"),
				rand.nextInt(128));

		testARecord= new ARecord(Name.fromString(testName), DClass.IN, 3600,
				randip);

		u = new Update(Name.fromString(testZone.getDomain()));
		u.add(testARecord);
		dnsServiceImpl.sendUpdate(testZone, u);
		Resolver resolver = dnsServiceImpl.getResolverForZone(testZone);
		assertRecordExists(resolver, testARecord);
		

	}

	public void testDeleteRecord() throws Exception {
		testCreateRecord();
		Resolver resolver = dnsServiceImpl.getResolverForZone(testZone);
		assertRecordExists(resolver, testARecord);
		Update u = new Update(Name.fromString(testZone.getDomain()));
		u.present(testARecord.getName(),testARecord.getType(),testARecord.rdataToString());
		u.delete(testARecord);
		dnsServiceImpl.sendUpdate(testZone, u);
		assertRecordDoesNotExist(resolver, testARecord);
	}
}
