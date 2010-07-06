package test.dns;

import junit.framework.TestCase;

import org.xbill.DNS.Record;

import edu.bath.soak.dns.model.DNSRecord;
import edu.bath.soak.dns.model.DNSZone;
import edu.bath.soak.dns.model.ForwardZone;

public class DNSRecordTests extends TestCase {
	
	DNSZone testZone ;
	
	@Override
	protected void setUp() throws Exception {
	
		testZone = new ForwardZone();
		testZone.setDomain("testdomain.");
		testZone.setDefaultTTL(3600L);
		testZone.setDescription("A test zone");
		testZone.setId(1L);
		super.setUp();
	}
	public void testFromDNSRecord(){
		DNSRecord r = new DNSRecord();
		r.setZone(testZone);
		r.setHostName("testhost.testdomain.");
		r.setTarget("127.0.0.1");
		r.setTtl(3600L);
		r.setType("A");
		
		Record dnsrec = r.getRecord();
		DNSRecord newr = DNSRecord.fromDNSRecord(testZone, dnsrec);
		assertEquals(r,newr);
	}

}
