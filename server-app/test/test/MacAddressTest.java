package test;

import junit.framework.TestCase;
import edu.bath.soak.util.MacAddress;

public class MacAddressTest extends TestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testIsPartialMac() {
		assertTrue(MacAddress.isPartialMac("0f:1a:22"));
		assertTrue(MacAddress.isPartialMac("0f:c1:22:"));
		assertTrue(MacAddress.isPartialMac("0f:"));
		assertTrue(MacAddress.isPartialMac("0f:a1:dd:ee:ff:11"));
		assertFalse(MacAddress.isPartialMac("0f:a1:dd:ee:ff:s1"));
		assertFalse(MacAddress.isPartialMac("0f:a1:dd:ee:ff:11:2"));
		assertFalse(MacAddress.isPartialMac("0f:a1:dd:ee:ff:112"));
		assertFalse(MacAddress.isPartialMac("0f:a1dd:ee:ff:s1"));

		assertFalse(MacAddress.isPartialMac(""));

	}

	public void testFromText() {
		String[] goodMacs = { "00:11:22:33:44:55", "ab:cd:ef:01:23:45",
				"00:00:00:00:00:00", "000000000000", "01234567890a" };
		String[] badMacs = { "00:11:22:33:44:5", "ab:cd:ef:01:23:4g",
				"00:00:00:00:00:001", "000000000000a", "01234567890af", "" };

		for (String macStr : goodMacs) {
			MacAddress mac = MacAddress.fromText(macStr);
			assertNotNull(mac);
		}
		for (String macStr : badMacs) {
			try {
				 MacAddress.fromText(macStr);
				fail("mac parsing of " + macStr
						+ " succeded when should have failed");
			} catch (Exception e) {

			}
		}
	}

	public void testFromBytes() {
		MacAddress mac = MacAddress.fromBytes(new byte[]{0x12,0x34,0x45,0x67,0x0e,0x0f});
		assertNotNull(mac);
		
	}


}
