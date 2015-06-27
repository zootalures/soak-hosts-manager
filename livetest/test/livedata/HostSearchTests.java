package test.livedata;

import java.net.Inet4Address;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.test.AbstractTransactionalSpringContextTests;

import test.hosts.SpringSetup;
import edu.bath.soak.net.model.Host;
import edu.bath.soak.net.model.NetDAO;
import edu.bath.soak.net.model.Subnet;
import edu.bath.soak.net.query.HostSearchQuery;
import edu.bath.soak.query.SearchResult;
import edu.bath.soak.util.TypeUtils;

public class HostSearchTests extends AbstractTransactionalSpringContextTests {
	Logger log = Logger.getLogger(HostSearchTests.class);
	NetDAO hostsDAO;

	public HostSearchTests() {
		setAutowireMode(AUTOWIRE_BY_NAME);
	}

	@Override
	protected String[] getConfigLocations() {
		return SpringSetup.BASIC_LIVETEST_LOCS;
	}

	public void setHostsDAO(NetDAO hostsDao) {
		this.hostsDAO = hostsDao;
	}

	public void testSearchReturnsCorrectResults() {
		HostSearchQuery hsq = new HostSearchQuery();
		hsq.setMaxResults(-1);
		hsq.setSubnet(hostsDAO.findSubnetByBaseIP(TypeUtils
				.txtToIP("138.38.32.0")));
		hsq.setOrderBy("ipAddress");
		hsq.setAscending(true);
		SearchResult<Host> res = hostsDAO.searchHosts(hsq);
		assertTrue(res.getResults().size() != 0);
		assertEquals(res.getTotalResults(), res.getResults().size());

	}

	
	public void testSearchIpsInIDSet(){
		Subnet s = hostsDAO.findSubnetByBaseIP(TypeUtils.txtToIP("138.38.116.0"));
		long starttime = System.currentTimeMillis();
		
		List<Inet4Address> lists1 = hostsDAO.getUsedIPsInRange(s);
		long t1time = System.currentTimeMillis();
		List<Host> lists2 = hostsDAO.getAllHostsInRange(s);
		long t2time = System.currentTimeMillis();
		assertEquals(lists1.size(), lists2.size());
		
		for(int i =0; i< lists1.size();i++)
			assertEquals(lists1.get(i),lists2.get(i).getIpAddress());
		
		log.info(" got " + lists1.size() + " t1: " + (t1time -starttime) + "  t2: " + (t2time - starttime));
		
		
	}
}
