package test.dns;

import java.net.Inet4Address;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.test.AbstractTransactionalSpringContextTests;
import org.xbill.DNS.ReverseMap;

import test.hosts.SpringSetup;
import edu.bath.soak.dns.DNSAddressManagerAdvisor;
import edu.bath.soak.dns.model.DNSDao;
import edu.bath.soak.dns.model.DNSRecord;
import edu.bath.soak.dns.model.ReverseZone;
import edu.bath.soak.mgr.AddressManagerAdvisor.AddressManagerAdvice;
import edu.bath.soak.net.model.Host;
import edu.bath.soak.net.model.NetDAO;
import edu.bath.soak.net.model.Subnet;
import edu.bath.soak.testutils.TestData;
import edu.bath.soak.util.TypeUtils;

public class DNSAddressAdvisorTests extends
		AbstractTransactionalSpringContextTests {
	Logger log = Logger.getLogger(this.getClass());
	DNSAddressManagerAdvisor dnsAddressManagerAdvisor;
	DNSDao dnsDAO;
	NetDAO hostsDAO;
	TestData td;
	private ReverseZone reverseZone;

	@Override
	protected void onSetUpInTransaction() throws Exception {
		td = new TestData(hostsDAO);
		super.onSetUpInTransaction();
		
		reverseZone = new ReverseZone();
		reverseZone.setDescription("test reverse");
		reverseZone.setDisplayName("test zone");
		reverseZone.setDomain("10.in-addr.arpa.");
		reverseZone.setServerIP(TypeUtils.txtToIP("127.0.0.1"));
		dnsDAO.saveZone(reverseZone);

	}

	/**
	 * Checks that all allocations are ok if no records exists for a subnet
	 * 
	 * @throws Exception
	 */
	public void testAllocateAddressSuccess() throws Exception {
		Subnet newSubnet = td.dummySubnet(42);

		hostsDAO.saveSubnet(newSubnet);
		Host dummyHost = td.dummyHost(td.getTestSubnet(), "qwasdasd", 12);
		dummyHost.setIpAddress(null);
		for (Inet4Address addr = newSubnet.getMinIP(); !addr.equals(newSubnet
				.getMaxIP()); addr = TypeUtils.ipIncrement(addr)) {
			assertEquals(AddressManagerAdvice.OK, dnsAddressManagerAdvisor
					.getAdviceForAllocation(dummyHost, newSubnet, addr));
		}
	}

	/**
	 * Checks that all allocations are ok if no records exists for a subnet
	 * 
	 * @throws Exception
	 */
	public void testAllocateAddressWithReservationSuccess() throws Exception {
		Subnet newSubnet = td.dummySubnet(42);
		hostsDAO.saveSubnet(newSubnet);
		Host dummyHost = td.dummyHost(td.getTestSubnet(), "qwasdasd", 12);
	

		DNSRecord clashRec = new DNSRecord();
		clashRec.setHostName(ReverseMap.fromAddress(dummyHost.getIpAddress())
				.toString());
		clashRec.setType("PTR");
		clashRec.setTarget("testhost.bath.ac.uk.");
		clashRec.setZone(reverseZone);
		clashRec.setTtl(3600L);
		dnsDAO.saveRecord(clashRec);

		dummyHost.setIpAddress(null);
		for (Inet4Address addr = newSubnet.getMinIP(); !addr.equals(newSubnet
				.getMaxIP()); addr = TypeUtils.ipIncrement(addr)) {
			if (addr.equals(dummyHost.getIpAddress())) {
				assertEquals(AddressManagerAdvice.PREFER_NOT, dnsAddressManagerAdvisor
						.getAdviceForAllocation(dummyHost, newSubnet, addr));
			} else {
				assertEquals(AddressManagerAdvice.OK, dnsAddressManagerAdvisor
						.getAdviceForAllocation(dummyHost, newSubnet, addr));
			}
		}
	}

	@Override
	protected String[] getConfigLocations() {
		return SpringSetup.DNS_TEST_LOCS;
	}

	public DNSAddressAdvisorTests() {
		setAutowireMode(AUTOWIRE_BY_NAME);
	}

	@Required
	public void setDnsDAO(DNSDao dao) {
		this.dnsDAO = dao;
	}

	public void setDnsAddressManagerAdvisor(
			DNSAddressManagerAdvisor dnsAddressManagerAdvisor) {
		this.dnsAddressManagerAdvisor = dnsAddressManagerAdvisor;
	}

	public void setHostsDAO(NetDAO hostsDAO) {
		this.hostsDAO = hostsDAO;
	}

}
