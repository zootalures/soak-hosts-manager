package testsuites;

import junit.framework.Test;
import junit.framework.TestSuite;

public class ReleaseFunctionality {

	public static Test suite() {
		TestSuite suite = new TestSuite("Release Functionality");
		// $JUnit-BEGIN$
		suite.addTest(WebTests.suite());
		suite.addTest(DBTests.suite());
		suite.addTest(MgrTests.suite());
		suite.addTest(ComponentTests.suite());
		suite.addTest(DHCPTests.suite());
		suite.addTest(DNSTests.suite());
		// $JUnit-END$
		return suite;
	}

}
