package test.hosts;

import java.net.Inet4Address;
import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.test.AbstractTransactionalSpringContextTests;

import edu.bath.soak.mgr.AddressManagerAdvisor;
import edu.bath.soak.mgr.AvoidHighAndLowAddressesAdvisor;
import edu.bath.soak.mgr.AddressSpaceManager.AddressSpaceFullException;
import edu.bath.soak.net.AdviceBasedAddressSpaceManager;
import edu.bath.soak.net.AllocatedAddressPool;
import edu.bath.soak.net.model.Host;
import edu.bath.soak.net.model.NetDAO;
import edu.bath.soak.net.model.Subnet;
import edu.bath.soak.testutils.TestData;

public class AddressSpaceManagerImplTests extends
		AbstractTransactionalSpringContextTests {
	Logger log = Logger.getLogger(this.getClass());
	AdviceBasedAddressSpaceManager addressSpaceManager;

	NetDAO hostsDAO;
	TestData td;

	@Override
	protected void onSetUpInTransaction() throws Exception {
		super.onSetUpInTransaction();
		td = new TestData(hostsDAO);
		addressSpaceManager.setAllocatedAddressPool(new AllocatedAddressPool());
		// by default, clear the advisors
		addressSpaceManager.setAdvisors(new ArrayList<AddressManagerAdvisor>());
		
	}

	@Override
	protected String[] getConfigLocations() {
		return SpringSetup.BASIC_TEST_LOCS;
	}

	public AddressSpaceManagerImplTests() {
		setAutowireMode(AUTOWIRE_BY_NAME);
	}

	@Required
	public void setHostsDAO(NetDAO hostsDAO) {
		this.hostsDAO = hostsDAO;
	}

	/**
	 * Checks that chosing a new IP address on a subnet works, also checks that
	 * a subsequent request yeilds the same address.
	 */
	public void testChooseNewIPAddressSameSubnetSuccess() {

		Inet4Address addr = addressSpaceManager.allocateIPAddress(td
				.getTestHost(), td.getTestSubnet());
		assertNotNull(addr);

		assertSame(addr, td.getTestHost().getIpAddress());
		Inet4Address addr2 = addressSpaceManager.allocateIPAddress(td
				.getTestHost(), td.getTestSubnet());
		assertSame(addr, addr2);
	}

	public void testOnlyPoorAddressesAvailable() {
		AddressManagerAdvisor adv = new AddressManagerAdvisor() {
			public AddressManagerAdvice getAdviceForAllocation(Host h,
					Subnet s, Inet4Address addr) {
				return AddressManagerAdvice.PREFER_NOT;
			}
			public int getOrder() {
				return 0;
			}
		};
		Host testHost = td.dummyHost(td.getTestSubnet(), "asdasdas", 123);
		testHost.setIpAddress(null);
		ArrayList<AddressManagerAdvisor> advs = new ArrayList<AddressManagerAdvisor>();
		advs.add(adv);
		addressSpaceManager.setAdvisors(advs);
		Inet4Address addr = addressSpaceManager.allocateIPAddress(testHost, td.getTestSubnet());
		assertNotNull(addr);

	}

	public void testAvoidHighAndLowAddressesAdvisor() {
		AvoidHighAndLowAddressesAdvisor hla = new AvoidHighAndLowAddressesAdvisor();
		hla.setBottomRange(10);
		hla.setMinSubnetSize(100);
		hla.setTopRange(10);
		Subnet newSubnet  = td.dummySubnet(234);
		hostsDAO.saveSubnet(newSubnet);
		
		Host testHost = td.dummyHost(td.getTestSubnet(), "asdasdas", 123);
		testHost.setIpAddress(null);
		ArrayList<AddressManagerAdvisor> advs = new ArrayList<AddressManagerAdvisor>();
		advs.add(hla);
		addressSpaceManager.setAdvisors(advs);
		Inet4Address addr = addressSpaceManager.allocateIPAddress(testHost, td.getTestSubnet());
		
		assertNotNull(addr);

	}

	/**
	 * Checks that chosing a new IP address on a subnet works, also checks that
	 * a subsequent request yeilds a different address.
	 */
	public void testChooseNewIPAddressDifferentSubnetSuccess() {
		Host testHost = td.getTestHost();
		Subnet testSubnet = td.getTestSubnet();
		Subnet otherSubnet = td.dummySubnet(23);
		hostsDAO.saveSubnet(otherSubnet);

		// try allocating on new subnet
		Inet4Address addr = addressSpaceManager.allocateIPAddress(testHost,
				otherSubnet);
		assertNotNull(addr);
		assertNotSame(addr, testHost.getIpAddress());
		assertSame(addr, addressSpaceManager.allocateIPAddress(testHost,
				otherSubnet));
		assertSame(addr, addressSpaceManager.getAllocatedIP(testHost));
		assertTrue(otherSubnet.containsIp(addr));

		// try allocating on old subnet
		Inet4Address addr2 = addressSpaceManager.allocateIPAddress(testHost,
				testSubnet);
		assertNotSame(addr, addr2);
		assertSame(addr2, addressSpaceManager.allocateIPAddress(testHost,
				testSubnet));
		assertSame(addr2, addressSpaceManager.getAllocatedIP(testHost));
		assertTrue(testSubnet.containsIp(addr2));

	}

	/**
	 * ensures that you can't allocate addresses on a subnet which is full
	 */
	public void testChooseNewIPAddressFull() {
		Subnet newsubnet = td.dummySubnet(39);
		hostsDAO.saveSubnet(newsubnet);

		for (int i = 0; i < newsubnet.getNumUseableAddresses(); i++) {
			Host h = td.dummyHost(newsubnet, "fullsubnethost", i);
			log.trace("Adding host  " + h);
			hostsDAO.saveHost(h,"testcmd");
		}

		try {
			addressSpaceManager.allocateIPAddress(td.getTestHost(), newsubnet);
			fail();
		} catch (AddressSpaceFullException e) {

		} catch (Throwable e) {
			fail("expecting another exception, got " + e);
		}
		assertFalse(addressSpaceManager.hasFreeAddresses(td.getTestHostClass(),
				newsubnet));
	}

	public void testGetNumAvailableAddresses() {
		Subnet newsubnet = td.dummySubnet(40);
		hostsDAO.saveSubnet(newsubnet);

		log.trace("checking free addresses");
		assertEquals(newsubnet.getNumUseableAddresses(), addressSpaceManager
				.getNumAvailableAddresses(td.getTestHostClass(), newsubnet));
		log.trace("Allocating extra address");

		addressSpaceManager.allocateIPAddress(td.getTestHost(), newsubnet);

		log.trace("checking free addressess");

		assertEquals(newsubnet.getNumUseableAddresses() -1,
				addressSpaceManager.getNumAvailableAddresses(td
						.getTestHostClass(), newsubnet));

	}

	public void testHasFreeAddresses() {
		Subnet newsubnet = td.dummySubnet(39);
		hostsDAO.saveSubnet(newsubnet);
		assertTrue(addressSpaceManager.hasFreeAddresses(td.getTestHostClass(),
				newsubnet));

	}

	public void setAddressSpaceManager(
			AdviceBasedAddressSpaceManager addressSpaceManager) {
		this.addressSpaceManager = addressSpaceManager;
	}

	
}
