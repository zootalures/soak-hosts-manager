package testsuites;

import junit.framework.Test;
import junit.framework.TestSuite;
import test.dns.DNSAddressAdvisorTests;
import test.dns.DNSCmdValidatorTests;
import test.dns.DNSHostsInterceptorTests;
import test.dns.DNSRecordTests;
import test.dns.DNSZoneTests;

public class DNSTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("DNS Plugin tests");
		suite.addTestSuite(DNSHostsInterceptorTests.class);
		suite.addTestSuite(DNSAddressAdvisorTests.class);
		suite.addTestSuite(DNSCmdValidatorTests.class);
		suite.addTestSuite(DNSRecordTests.class);
		suite.addTestSuite(DNSZoneTests.class);
		// suite.addTestSuite(DNSDAOTests.class);

		return suite;
	}
}
