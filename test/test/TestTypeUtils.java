package test;

import java.net.Inet4Address;
import java.net.InetAddress;

import org.springframework.test.AssertThrows;

import junit.framework.TestCase;
import edu.bath.soak.net.model.IPRange;
import edu.bath.soak.util.MacAddress;
import edu.bath.soak.util.TypeUtils;

public class TestTypeUtils extends TestCase {

	public void testIPConv() throws Exception {
		String myIP = "138.38.52.250";
		long shouldip = 2317759738L;
		Inet4Address ipo = (Inet4Address) InetAddress.getByName(myIP);
		long intip = TypeUtils.ipToInt(ipo);
		// System.err.println("Got IP" + intip);

		if (intip != shouldip)
			fail("Ip addresses don't match : got " + intip + " expecting "
					+ shouldip);
		Inet4Address ipout = TypeUtils.intToIP(shouldip);

		if (!ipout.getHostAddress().equals(myIP))
			fail(" intIPToIPAddr failed got \"" + ipout.toString() + "\"");

		try {
			TypeUtils.txtToIP("localhost");
			fail("host matched as IP");
		} catch (Exception e) {

		}
		Inet4Address addr = TypeUtils.txtToIP(myIP);
		assertTrue(addr.getHostAddress().equals(myIP));

		assertEquals(0L, TypeUtils.ipToInt(TypeUtils.txtToIP("0.0.0.0")));
		assertEquals(1L, TypeUtils.ipToInt(TypeUtils.txtToIP("0.0.0.1")));

		assertEquals(TypeUtils.intToIP(1L), TypeUtils.txtToIP("0.0.0.1"));
		assertEquals(TypeUtils.intToIP(0L), TypeUtils.txtToIP("0.0.0.0"));
	}

	public void testMacType() {
		String mymac = "ab:cd:ef:01:23:45";
		MacAddress mac = MacAddress.fromText(mymac);
		if (!mymac.toUpperCase().equals(mac.toString())) {
			System.err.println("Got mac " + mac.toString() + " expecting "
					+ mymac);
			fail();
		}

	}

	public void testPtrToIp() {
		assertEquals(TypeUtils.txtToIP("127.0.0.1"), TypeUtils
				.ptrNameToIP("1.0.0.127.in-addr.arpa."));
		assertEquals(TypeUtils.txtToIP("255.255.255.255"), TypeUtils
				.ptrNameToIP("255.255.255.255.in-addr.arpa."));
		new AssertThrows(IllegalArgumentException.class){
			@Override
			public void test() throws Exception {
				TypeUtils
						.ptrNameToIP("255.255.255.255.255.in-addr.arpa.");
					
			}
		}.runTest();
			
		new AssertThrows(IllegalArgumentException.class){
			@Override
			public void test() throws Exception {
				TypeUtils
						.ptrNameToIP("2255.255.255.255.in-addr.arpa.");
					
			}
		}.runTest();

		new AssertThrows(IllegalArgumentException.class){
			@Override
			public void test() throws Exception {
				TypeUtils
						.ptrNameToIP("2255.255.255.a.in-addr.arpa.");
					
			}
		}.runTest();

	}

	public void testCheckAddressIsCIDR() {
		assertTrue(TypeUtils.checkAddressIsCIDR(TypeUtils
				.txtToIP("138.38.108.0"), 24));
		assertTrue(TypeUtils.checkAddressIsCIDR(
				TypeUtils.txtToIP("138.38.0.0"), 16));
		assertTrue(TypeUtils.checkAddressIsCIDR(TypeUtils.txtToIP("138.0.0.0"),
				8));
		assertTrue(TypeUtils.checkAddressIsCIDR(TypeUtils.txtToIP("0.0.0.0"),
				32));

		assertFalse(TypeUtils.checkAddressIsCIDR(TypeUtils
				.txtToIP("138.38.108.1"), 31));
		assertFalse(TypeUtils.checkAddressIsCIDR(TypeUtils
				.txtToIP("138.38.12.15"), 16));

	}

	public void testipMath() {
		assertEquals(TypeUtils.txtToIP("138.38.32.1"), TypeUtils.ipMath(
				TypeUtils.txtToIP("138.38.32.0"), 1));
		assertEquals(TypeUtils.txtToIP("138.38.32.255"), TypeUtils.ipMath(
				TypeUtils.txtToIP("138.38.32.0"), 255));
		assertEquals(TypeUtils.txtToIP("138.38.33.0"), TypeUtils.ipMath(
				TypeUtils.txtToIP("138.38.32.0"), 256));

	}

	public void testgetCIDRMaxAddress() {
		assertEquals(TypeUtils.txtToIP("138.38.255.255"), TypeUtils
				.getCIDRMaxAddress(TypeUtils.txtToIP("138.38.0.0"), 16));

		assertEquals(TypeUtils.txtToIP("138.38.0.63"), TypeUtils
				.getCIDRMaxAddress(TypeUtils.txtToIP("138.38.0.48"), 28));
	}

	public void testIPComparison() {
		Inet4Address ips[] = new Inet4Address[] { TypeUtils.txtToIP("0.0.0.0"),
				TypeUtils.txtToIP("0.0.0.1"), TypeUtils.txtToIP("1.0.0.0"),
				TypeUtils.txtToIP("1.0.0.1"), TypeUtils.txtToIP("127.0.0.1"),
				TypeUtils.txtToIP("127.0.0.2"),
				TypeUtils.txtToIP("138.38.0.0"),
				TypeUtils.txtToIP("138.38.52.19"),
				TypeUtils.txtToIP("192.168.1.1"),
				TypeUtils.txtToIP("255.255.255.255") };

		for (int i = 0; i < ips.length; i++) {
			for (int j = 0; j < ips.length; j++) {
				int val = TypeUtils.ipCompare(ips[i], ips[j]);
				// System.out.println("Comparing: " + ips[i] + " with " + ips[j]
				// + " gives " + val);
				if (i < j) {
					assertTrue(val < 0);
				} else if (i == j) {
					assertTrue(val == 0);
				} else {
					assertTrue(val > 0);
				}
			}
		}
	}

	public void testIpRangeComparison() {
		IPRange r1 = new IPRange(TypeUtils.txtToIP("138.38.52.0"), TypeUtils
				.txtToIP("138.38.55.255"));
		IPRange r2 = new IPRange(TypeUtils.txtToIP("138.38.32.0"), TypeUtils
				.txtToIP("138.38.33.255"));
		IPRange r3 = new IPRange(TypeUtils.txtToIP("0.0.0.0"), TypeUtils
				.txtToIP("255.255.255.255"));
		IPRange r4 = new IPRange(TypeUtils.txtToIP("138.38.0.0"), TypeUtils
				.txtToIP("138.38.255.255"));

		assertFalse(r1.clashesWith(r2));
		assertTrue(r1.clashesWith(r3));
		assertTrue(r3.clashesWith(r1));
		assertTrue(r4.clashesWith(r1));
		assertTrue(r1.clashesWith(r4));
	}

	public void testIpArithmetic() {
		Inet4Address ip1 = TypeUtils.txtToIP("138.38.54.19");
		Inet4Address ip2 = TypeUtils.txtToIP("0.0.0.0");
		Inet4Address ip3 = TypeUtils.txtToIP("0.0.0.1");
		Inet4Address ip4 = TypeUtils.txtToIP("255.255.255.255");
		Inet4Address ip5 = TypeUtils.txtToIP("138.38.255.255");
		Inet4Address ip6 = TypeUtils.txtToIP("138.39.0.0");
		Inet4Address ip7 = TypeUtils.txtToIP("138.38.54.20");

		assertEquals(TypeUtils.ipIncrement(ip1), ip7);
		assertEquals(TypeUtils.ipIncrement(ip2), ip3);
		assertEquals(TypeUtils.ipIncrement(ip5), ip6);
		assertEquals(TypeUtils.ipIncrement(ip4), ip2);
	}

	public void testIpCmp() {
		assertTrue(0 == TypeUtils.ipCmp(TypeUtils.txtToIP("138.38.32.0"),
				TypeUtils.txtToIP("138.38.32.0")));
		assertTrue(TypeUtils.ipCmp(TypeUtils.txtToIP("138.38.32.0"), TypeUtils
				.txtToIP("138.38.32.1")) < 0);
		assertTrue(TypeUtils.ipCmp(TypeUtils.txtToIP("138.38.32.1"), TypeUtils
				.txtToIP("138.38.32.0")) > 0);
		assertTrue(TypeUtils.ipCmp(TypeUtils.txtToIP("0.0.0.0"), TypeUtils
				.txtToIP("255.255.255.255")) < 0);
	}

	public void testIpInRange() {
		assertTrue(TypeUtils.ipInRange(TypeUtils.txtToIP("138.38.56.19"),
				TypeUtils.txtToIP("138.38.56.19"), TypeUtils
						.txtToIP("138.38.56.19")));
		assertTrue(TypeUtils.ipInRange(TypeUtils.txtToIP("138.38.56.19"),
				TypeUtils.txtToIP("138.38.55.0"), TypeUtils
						.txtToIP("138.38.56.255")));
		assertFalse(TypeUtils.ipInRange(TypeUtils.txtToIP("138.38.32.1"),
				TypeUtils.txtToIP("138.38.55.0"), TypeUtils
						.txtToIP("138.38.56.255")));
		assertFalse(TypeUtils.ipInRange(TypeUtils.txtToIP("0.0.0.0"), TypeUtils
				.txtToIP("138.38.55.0"), TypeUtils.txtToIP("138.38.56.255")));
		assertFalse(TypeUtils.ipInRange(TypeUtils.txtToIP("255.255.255.255"),
				TypeUtils.txtToIP("138.38.55.0"), TypeUtils
						.txtToIP("138.38.56.255")));
		assertTrue(TypeUtils.ipInRange(TypeUtils.txtToIP("138.38.55.0"),
				TypeUtils.txtToIP("0.0.0.0"), TypeUtils
						.txtToIP("255.255.255.255")));

		try {
			TypeUtils.ipInRange(TypeUtils.txtToIP("0.0.0.0"), TypeUtils
					.txtToIP("255.255.255.255"), TypeUtils
					.txtToIP("138.38.55.0"));
			fail("should have bailed out");
		} catch (Exception e) {
		}

	}

	public void testNetmaskBits() {
		assertEquals(32, TypeUtils.numNetmaskBits(TypeUtils
				.txtToIP("255.255.255.255")));
		assertEquals(24, TypeUtils.numNetmaskBits(TypeUtils
				.txtToIP("255.255.255.0")));
		assertEquals(23, TypeUtils.numNetmaskBits(TypeUtils
				.txtToIP("255.255.254.0")));
		assertEquals(16, TypeUtils.numNetmaskBits(TypeUtils
				.txtToIP("255.255.0.0")));
		assertEquals(8, TypeUtils
				.numNetmaskBits(TypeUtils.txtToIP("255.0.0.0")));
		assertEquals(0, TypeUtils.numNetmaskBits(TypeUtils.txtToIP("0.0.0.0")));
	}

	public void testTxtToCIDRRange() throws Exception {
		IPRange r = TypeUtils.txtToCIDRRange("138.38.0.0/16");
		assertEquals(TypeUtils.txtToIP("138.38.0.0"), r.getMinIP());
		assertEquals(TypeUtils.txtToIP("138.38.255.255"), r.getMaxIP());

		r = TypeUtils.txtToCIDRRange("0.0.0.0/0");
		assertEquals(TypeUtils.txtToIP("0.0.0.0"), r.getMinIP());
		assertEquals(TypeUtils.txtToIP("255.255.255.255"), r.getMaxIP());

		r = TypeUtils.txtToCIDRRange("192.168.1.1/32");
		assertEquals(TypeUtils.txtToIP("192.168.1.1"), r.getMinIP());
		assertEquals(TypeUtils.txtToIP("192.168.1.1"), r.getMaxIP());

		try {
			r = TypeUtils.txtToCIDRRange("");
			fail();
		} catch (Exception e) {

		}
		try {
			r = TypeUtils.txtToCIDRRange(null);
			fail();
		} catch (Exception e) {

		}

		try {
			r = TypeUtils.txtToCIDRRange("Foo/bar");
			fail();
		} catch (Exception e) {

		}
	}
}
