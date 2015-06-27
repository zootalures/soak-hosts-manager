package testsuites;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {
	public static Test suite() {
		TestSuite suite = new TestSuite("All Tests");
		suite.addTest(ReleaseFunctionality.suite());
		suite.addTest(LiveTests.suite());
		// $JUnit-END$
		return suite;
	}
}
