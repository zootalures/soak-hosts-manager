package test.hosts;

import junit.framework.TestCase;
import edu.bath.soak.net.model.Subnet;
import edu.bath.soak.util.TypeUtils;

public class SubnetTest extends TestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testGetNetworkBits() {

		Subnet s = new Subnet();
		s.setMinIP(TypeUtils.txtToIP("138.38.32.0"));
		s.setMaxIP(TypeUtils.txtToIP("138.38.33.255"));
		assertEquals(23, s.getMaskBits());
		s.setMinIP(TypeUtils.txtToIP("138.38.32.0"));
		s.setMaxIP(TypeUtils.txtToIP("138.38.32.255"));
		assertEquals(24, s.getMaskBits());
		s.setMinIP(TypeUtils.txtToIP("0.0.0.0"));
		s.setMaxIP(TypeUtils.txtToIP("255.255.255.255"));
		assertEquals(0, s.getMaskBits());
		s.setMinIP(TypeUtils.txtToIP("138.38.32.0"));
		s.setMaxIP(TypeUtils.txtToIP("138.38.32.1"));
		assertEquals(31, s.getMaskBits());
		s.setMinIP(TypeUtils.txtToIP("138.38.32.0"));
		s.setMaxIP(TypeUtils.txtToIP("138.38.32.3"));
		assertEquals(30, s.getMaskBits());

	}

	public void testGetMaskBits() {
		Subnet s = new Subnet();
		s.setMinIP(TypeUtils.txtToIP("138.38.32.0"));
		s.setMaxIP(TypeUtils.txtToIP("138.38.33.255"));
		assertEquals(9, s.getNetworkBits());
		s.setMinIP(TypeUtils.txtToIP("138.38.32.0"));
		s.setMaxIP(TypeUtils.txtToIP("138.38.32.255"));
		assertEquals(8, s.getNetworkBits());
		s.setMinIP(TypeUtils.txtToIP("0.0.0.0"));
		s.setMaxIP(TypeUtils.txtToIP("255.255.255.255"));
		assertEquals(32, s.getNetworkBits());
		s.setMinIP(TypeUtils.txtToIP("138.38.32.0"));
		s.setMaxIP(TypeUtils.txtToIP("138.38.32.1"));
		assertEquals(1, s.getNetworkBits());
		s.setMinIP(TypeUtils.txtToIP("138.38.32.0"));
		s.setMaxIP(TypeUtils.txtToIP("138.38.32.3"));
		assertEquals(2, s.getNetworkBits());
	}

	public void testGetMinUsableAddress() {
		Subnet s = new Subnet();
		s.setMinIP(TypeUtils.txtToIP("138.38.32.0"));
		s.setMaxIP(TypeUtils.txtToIP("138.38.33.255"));
		assertEquals(TypeUtils.txtToIP("138.38.32.1"), s.getMinUsableAddress());

	}

	public void testGetMaxUsableAddress() {
		Subnet s = new Subnet();
		s.setMinIP(TypeUtils.txtToIP("138.38.32.0"));
		s.setMaxIP(TypeUtils.txtToIP("138.38.33.255"));
		assertEquals(TypeUtils.txtToIP("138.38.33.254"), s
				.getMaxUsableAddress());

	}

	public void testGetSubnetMask() {
		Subnet s = new Subnet();
		s.setMinIP(TypeUtils.txtToIP("138.38.32.0"));
		s.setMaxIP(TypeUtils.txtToIP("138.38.33.255"));
		assertEquals(TypeUtils.txtToIP("255.255.254.0"), s.getSubnetMask());
	}

	public void testGetNumUseableAddresses() {
		Subnet s = new Subnet();
		s.setMinIP(TypeUtils.txtToIP("138.38.32.0"));
		s.setMaxIP(TypeUtils.txtToIP("138.38.33.255"));
		assertEquals(510, s.getNumUseableAddresses());
	}

}
