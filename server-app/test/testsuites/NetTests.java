package testsuites;

import junit.framework.Test;
import junit.framework.TestSuite;
import test.dhcp.DHCPMgrTest;
import test.dns.DNSDAOTests;

public class NetTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Network dependent tests");
		//$JUnit-BEGIN$
		suite.addTestSuite(DHCPMgrTest.class);
		suite.addTestSuite(DNSDAOTests.class);
		//$JUnit-END$
		return suite;
	}

}
