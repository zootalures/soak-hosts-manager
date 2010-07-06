package testsuites;

import junit.framework.Test;
import junit.framework.TestSuite;
import test.web.TestSubnetController;

public class WebTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Web integration tests" );
		//$JUnit-BEGIN$
		suite.addTestSuite(TestSubnetController.class);
		//$JUnit-END$
		return suite;
	}

}
