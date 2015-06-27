package testsuites;

import junit.framework.Test;
import junit.framework.TestSuite;
import test.hosts.AddressSpaceManagerImplTests;
import test.hosts.AlterHostCmdValidatorPermissionTests;
import test.hosts.AlterHostCmdValidatorTests;
import test.hosts.BulkCreateHostManagerTests;
import test.hosts.BulkDeleteHostManagerTests;
import test.hosts.BulkEditHostManagerTests;
import test.hosts.BulkMoveHostsTests;
import test.hosts.HostCSVParserTests;
import test.hosts.HostsManagerImplTest;
import test.security.AcegiSecurityTests;
import test.undo.TestUndoAlterHosts;

public class MgrTests {
	public static Test suite() {
		TestSuite suite = new TestSuite("Middle tier unit tests");
		suite.addTestSuite(HostsManagerImplTest.class);
		suite.addTestSuite(AlterHostCmdValidatorTests.class);
		suite.addTestSuite(AlterHostCmdValidatorPermissionTests.class);
		suite.addTestSuite(AddressSpaceManagerImplTests.class);
		suite.addTestSuite(BulkDeleteHostManagerTests.class);
		suite.addTestSuite(BulkEditHostManagerTests.class);
		suite.addTestSuite(BulkMoveHostsTests.class);
		suite.addTestSuite(BulkCreateHostManagerTests.class);
		suite.addTestSuite(TestUndoAlterHosts.class);
		suite.addTestSuite(HostCSVParserTests.class);
		suite.addTestSuite(AcegiSecurityTests.class);
		// suite.addTestSuite(TestBulkSystemImport.class);
		return suite;
	}
}
