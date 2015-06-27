package testsuites;

import junit.framework.Test;
import junit.framework.TestSuite;
import test.live.LiveDNSModificationTests;
import test.live.LiveDNSUpdateTests;

public class LiveTests {
	public static Test suite() {
		TestSuite suite = new TestSuite("Live tests (not for CI)");
		suite.addTestSuite(LiveDNSModificationTests.class);
		suite.addTestSuite(LiveDNSUpdateTests.class);
		return suite;
	}
}
