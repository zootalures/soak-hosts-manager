package testsuites;

import junit.framework.Test;
import junit.framework.TestSuite;
import test.hosts.HostSearchTests;
import test.hosts.NetDAOTest;
import test.hosts.XMLSerializationTests;

public class DBTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Database and DAO tests");
		//$JUnit-BEGIN$
		suite.addTestSuite(NetDAOTest.class);
		suite.addTestSuite(HostSearchTests.class);
		suite.addTestSuite(XMLSerializationTests.class);
		
		//$JUnit-END$
		return suite;
	}

}
