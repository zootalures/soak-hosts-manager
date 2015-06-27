package testsuites;

import junit.framework.Test;
import junit.framework.TestSuite;
import test.dhcp.DHCPCmdValidatorTests;
import test.dhcp.DHCPHostsInterceptorTests;

public class DHCPTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("DHCP tests");
		suite.addTestSuite(DHCPCmdValidatorTests.class);
		suite.addTestSuite(DHCPHostsInterceptorTests.class);
		return suite;
	}
}
