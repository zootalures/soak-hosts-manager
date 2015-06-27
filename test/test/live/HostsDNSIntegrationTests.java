package test.live;

import test.hosts.HostsManagerImplTest;

public class HostsDNSIntegrationTests extends HostsManagerImplTest {

	protected void onSetUpInTransaction() throws Exception {
		super.onSetUpInTransaction();
		
 
		
	}
	public void testCreateHostCreateDNS() throws Exception {
		testCreateHostChooseIPSuccess();

		
	}
}
