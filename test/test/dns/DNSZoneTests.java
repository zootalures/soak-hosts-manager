package test.dns;

import java.util.Date;

import junit.framework.TestCase;
import edu.bath.soak.dns.model.ForwardZone;
import edu.bath.soak.dns.model.ReverseZone;
import edu.bath.soak.util.TypeUtils;

public class DNSZoneTests extends TestCase {

	ForwardZone goodFwdZone;
	ReverseZone goodRevZone;

	public void setUp() {

		goodFwdZone = new ForwardZone();
		goodFwdZone.setDefaultTTL(3600L);
		goodFwdZone.setDomain("bath.ac.uk.");
		goodFwdZone.setDescription("Bath internal forward zone");
		goodFwdZone.setId(1L);
		goodFwdZone.setExpire(24L * 3600L);
		goodFwdZone.setRefresh(24L * 3600L);
		goodFwdZone.setRetry(24L * 3600L);
		goodFwdZone.setMinTTL(3600L);
		goodFwdZone.setSerial(2006032301L);
		goodFwdZone.setServerIP(TypeUtils.txtToIP("138.38.32.3"));
		goodFwdZone.setLastUpdate(new Date());

		goodRevZone = new ReverseZone();
		goodRevZone.setDefaultTTL(3600L);
		goodRevZone.setDomain("38.138.in-addr.arpa.");
		goodRevZone.setDescription("Bath internal reverse zone");
		goodRevZone.setId(1L);
		goodRevZone.setExpire(24L * 3600L);
		goodRevZone.setRefresh(24L * 3600L);
		goodRevZone.setRetry(24L * 3600L);
		goodRevZone.setMinTTL(3600L);
		goodRevZone.setSerial(2006032301L);
		goodRevZone.setServerIP(TypeUtils.txtToIP("138.38.32.3"));
		goodRevZone.setLastUpdate(new Date());

	}

	public void tearDown() throws Exception {
		super.tearDown();
	}

	public void testForwardMatches() {
		goodFwdZone.setIgnoreHostRegexps(".*\\.campus\\.bath\\.ac\\.uk\\.\n"
				+ ".*\\.cs\\.bath\\.ac\\.uk\\.");
		assertTrue(goodFwdZone.forwardMatches("ccpc-occ1.bath.ac.uk."));
		assertTrue(goodFwdZone.forwardMatches("_test.bath.ac.uk."));
		assertTrue(goodFwdZone.forwardMatches("foo-bar.maths.bath.ac.uk."));
		assertFalse(goodFwdZone.forwardMatches("air.cs.bath.ac.uk."));
		assertFalse(goodFwdZone.forwardMatches("fish.campus.bath.ac.uk."));
		assertFalse(goodFwdZone.forwardMatches("www.google.com."));
		// Should fail for non-absolute fqdns
		try {
			goodFwdZone.forwardMatches("www.google.com");
			fail();
		} catch (Throwable t) {

		}
		// Should fail for non-absolute fqdns
		try {
			goodFwdZone.forwardMatches("test");
			fail();
		} catch (Throwable t) {

		}

		// and for reverse zones
		try {
			goodFwdZone.forwardMatches("19.52.38.138.in-addr.arpa.");
			fail();
		} catch (Throwable t) {

		}

	}

	public void testReverseMatches() {

		assertTrue(goodRevZone.reverseMatches(TypeUtils.txtToIP("138.38.32.4")));
		assertTrue(goodRevZone.reverseMatches(TypeUtils.txtToIP("138.38.53.4")));
		assertTrue(goodRevZone.reverseMatches(TypeUtils.txtToIP("138.38.0.0")));
		assertTrue(goodRevZone.reverseMatches(TypeUtils
				.txtToIP("138.38.255.255")));
		assertFalse(goodRevZone.reverseMatches(TypeUtils
				.txtToIP("138.39.255.255")));
		assertFalse(goodRevZone.reverseMatches(TypeUtils
				.txtToIP("255.255.255.255")));
		assertFalse(goodRevZone.reverseMatches(TypeUtils.txtToIP("1.2.3.4")));
		assertTrue(goodRevZone.reverseMatches(TypeUtils
				.txtToIP("138.38.108.255")));

		goodRevZone
				.setIgnoreHostRegexps("^.*\\.108\\.38\\.138\\.in-addr\\.arpa\\.$");
		assertFalse(goodRevZone.reverseMatches(TypeUtils
				.txtToIP("138.38.108.255")));

	}

}
