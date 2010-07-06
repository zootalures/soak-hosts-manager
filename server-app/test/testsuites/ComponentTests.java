package testsuites;

import junit.framework.Test;
import junit.framework.TestSuite;
import test.FunctionTests;
import test.MacAddressTest;
import test.TestTypeUtils;
import test.dns.DNSRecordTests;
import test.hosts.SubnetTest;

public class ComponentTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Component unit test Suite");
		// $JUnit-BEGIN$
		suite.addTestSuite(FunctionTests.class);

		suite.addTestSuite(TestTypeUtils.class);
		suite.addTestSuite(MacAddressTest.class);
		suite.addTestSuite(SubnetTest.class);
		suite.addTestSuite(DNSRecordTests.class);
		// $JUnit-END$
		return suite;
	}

}
