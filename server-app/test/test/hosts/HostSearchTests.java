package test.hosts;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.test.AbstractTransactionalSpringContextTests;

import edu.bath.soak.net.model.Host;
import edu.bath.soak.net.model.HostAlias;
import edu.bath.soak.net.model.HostName;
import edu.bath.soak.net.model.NetDAO;
import edu.bath.soak.net.model.Subnet;
import edu.bath.soak.net.query.HostSearchQuery;
import edu.bath.soak.query.SearchResult;
import edu.bath.soak.testutils.TestData;

public class HostSearchTests extends AbstractTransactionalSpringContextTests {
	Logger log = Logger.getLogger(NetDAOTest.class);
	NetDAO hostsDAO;

	public HostSearchTests() {
		setAutowireMode(AUTOWIRE_BY_NAME);
	}

	@Override
	protected String[] getConfigLocations() {
		return SpringSetup.BASIC_TEST_LOCS;
	}

	public void setHostsDAO(NetDAO hostsDao) {
		this.hostsDAO = hostsDao;
	}

	public void testSearchBlankName() {
		TestData td = new TestData(hostsDAO);

		HostSearchQuery search = new HostSearchQuery();

		SearchResult<Host> res = hostsDAO.searchHosts(search);

		assertNotNull(res);
		assertTrue(res.getResults().contains(td.getTestHost()));
	}

	public void testSearchHostByFQDN() {
		TestData td = new TestData(hostsDAO);

		HostSearchQuery search = new HostSearchQuery();

		Host searchhost = td.getTestHost();
		// search for single host
		search.setSearchTerm(searchhost.getHostName().toString());
		SearchResult<Host> res = hostsDAO.searchHosts(search);
		assertNotNull(res);
		assertEquals(searchhost, res.getResults().get(0));
	}

	public void testSearchHostByShortName() {
		TestData td = new TestData(hostsDAO);

		HostSearchQuery search = new HostSearchQuery();

		Host searchhost = td.getTestHost();
		// search for single host
		search.setSearchTerm(searchhost.getHostName().getName());
		SearchResult<Host> res = hostsDAO.searchHosts(search);
		assertNotNull(res);
		assertEquals(searchhost, res.getResults().get(0));
	}

	public void testSearchHostByIPAddress() {
		TestData td = new TestData(hostsDAO);

		HostSearchQuery search = new HostSearchQuery();

		Host searchhost = td.getTestHost();
		// search for single host
		search.setSearchTerm(searchhost.getIpAddress().getHostAddress());
		SearchResult<Host> res = hostsDAO.searchHosts(search);
		assertNotNull(res);
		assertEquals(1, res.getTotalResults());
		assertEquals(searchhost, res.getResults().get(0));
	}

	public void testSearchHostByMACAddress() {
		TestData td = new TestData(hostsDAO);

		HostSearchQuery search = new HostSearchQuery();

		Host searchhost = td.getTestHost();
		// search for single host
		search.setSearchTerm(searchhost.getMacAddress().toString());
		SearchResult<Host> res = hostsDAO.searchHosts(search);
		assertNotNull(res);
		assertEquals(1, res.getTotalResults());
		assertEquals(searchhost, res.getResults().get(0));
	}

	public void testSearchHostBySubnet() {
		TestData td = new TestData(hostsDAO);

		HostSearchQuery search = new HostSearchQuery();

		search.setSubnet(td.getTestSubnet());
		SearchResult<Host> res = hostsDAO.searchHosts(search);

		assertNotNull(res);
		assertTrue(res.getResults().contains(td.getTestHost()));
	}

	public void testSearchHostByNameDomain() {
		TestData td = new TestData(hostsDAO);

		HostSearchQuery search = new HostSearchQuery();

		SearchResult<Host> res = hostsDAO.searchHosts(search);

		assertNotNull(res);
		assertTrue(res.getResults().contains(td.getTestHost()));
	}

	public void testSearchHostWithAliasesPartial() {
		TestData td = new TestData(hostsDAO);

		Host editHost = hostsDAO.getHostForEditing(td.getTestHost().getId());
		HostAlias ha = new HostAlias();
		HostName hn = new HostName();
		hn.setDomain(td.getTestNameDomain());
		hn.setName("aliastesthost");
		ha.setAlias(hn);
		ha.setHost(editHost);

		editHost.getHostAliases().add(ha);
		hostsDAO.saveHost(editHost, "testcmd");

		HostSearchQuery search = new HostSearchQuery();
		search.setSearchTerm(hn.getName()
				+ td.getTestNameDomain().getSuffix().substring(0, 3));
		SearchResult<Host> res = hostsDAO.searchHosts(search);

		assertNotNull(res);
		assertEquals(1, res.getResults().size());
		Host gotHost = res.getResults().get(0);
		assertEquals(editHost, gotHost);

		search = new HostSearchQuery();
		search.setSearchTerm(editHost.getHostName().getName());
		res = hostsDAO.searchHosts(search);

		assertNotNull(res);
		assertEquals(1, res.getResults().size());
		assertEquals(editHost, res.getResults().get(0));
	}

	public void testSearchHostWithAliasesPart() {
		TestData td = new TestData(hostsDAO);

		Host editHost = hostsDAO.getHostForEditing(td.getTestHost().getId());
		HostAlias ha = new HostAlias();
		HostName hn = new HostName();
		hn.setDomain(td.getTestNameDomain());
		hn.setName("aliastesthost");
		ha.setAlias(hn);
		ha.setHost(editHost);

		editHost.getHostAliases().add(ha);
		hostsDAO.saveHost(editHost, "testcmd");

		HostSearchQuery search = new HostSearchQuery();
		search.setSearchTerm(hn.getName());
		SearchResult<Host> res = hostsDAO.searchHosts(search);

		assertNotNull(res);
		assertEquals(1, res.getResults().size());
		assertEquals(editHost, res.getResults().get(0));

		search = new HostSearchQuery();
		search.setSearchTerm(editHost.getHostName().getName());
		res = hostsDAO.searchHosts(search);

		assertNotNull(res);
		assertEquals(1, res.getResults().size());
		assertEquals(editHost, res.getResults().get(0));
	}

	public void testSearchHostWithAliasesFQDN() {
		TestData td = new TestData(hostsDAO);

		Host editHost = hostsDAO.getHostForEditing(td.getTestHost().getId());
		HostAlias ha = new HostAlias();
		HostName hn = new HostName();
		hn.setDomain(td.getTestNameDomain());
		hn.setName("aliastesthost");
		ha.setAlias(hn);
		ha.setHost(editHost);

		editHost.getHostAliases().add(ha);
		hostsDAO.saveHost(editHost, "testcmd");
		HostSearchQuery search = new HostSearchQuery();
		search.setSearchTerm(hn.toString());
		SearchResult<Host> res = hostsDAO.searchHosts(search);
		assertNotNull(res);
		assertEquals(1, res.getResults().size());
		Host reshost = res.getResults().get(0);
	    editHost = hostsDAO.getHostForEditing(td.getTestHost().getId());
		assertEquals(editHost, reshost);

		search = new HostSearchQuery();
		search.setSearchTerm(editHost.getHostName().toString());
		res = hostsDAO.searchHosts(search);

		assertNotNull(res);
		assertEquals(1, res.getResults().size());
		assertEquals(editHost, res.getResults().get(0));
	}

	public void testSearchPaging() {
		TestData td = new TestData(hostsDAO);
		HostSearchQuery search = new HostSearchQuery();
		Subnet ns = td.dummySubnet(20);

		hostsDAO.saveSubnet(ns);
		log.trace("adding 100 hosts");

		for (int i = 0; i < 100; i++) {
			Host h = td.dummyHost(ns, "testingmachineforsearch", i);
			log.trace("added host " + h.getHostName());
			hostsDAO.saveHost(h, "testcmd");
		}

		search.setSubnet(ns);
		search.setOrderBy("hostName");
		search.setAscending(true);
		SearchResult<Host> res_all = hostsDAO.searchHosts(search);
		assertNotNull(res_all);
		assertEquals(100, res_all.getResults().size());

		// test paging
		// Search for all hosts
		search = new HostSearchQuery();
		search.setSubnet(ns);
		search.setMaxResults(10);
		search.setFirstResult(10);
		search.setOrderBy("hostName");
		search.setAscending(true);
		SearchResult<Host> res_constrained = hostsDAO.searchHosts(search);
		assertNotNull(res_constrained);
		assertEquals(10, res_constrained.getResults().size());
		assertEquals(res_all.getResults().get(10), res_constrained.getResults()
				.get(0));
		assertEquals(res_all.getResults().get(19), res_constrained.getResults()
				.get(9));

	}

	public void testSearchInIDSet() {
		TestData td = new TestData(hostsDAO);
		List<Host> testhosts = new LinkedList<Host>();
		Subnet ns = td.dummySubnet(20);
		hostsDAO.saveSubnet(ns);
		for (int i = 0; i < 100; i++) {
			Host h = td.dummyHost(ns, "testingmachineforsearch", i);
			log.trace("added host " + h.getHostName());
			hostsDAO.saveHost(h, "testcmd");
			testhosts.add(h);
		}

		Set<Long> hostSet = new HashSet<Long>();

		for (int i = 0; i < 10; i++) {
			hostSet.add(testhosts.get(i).getId());
		}

		HostSearchQuery hsc = new HostSearchQuery();
		hsc.setOrderBy("ipAddress");
		hsc.setSubnet(ns);
		SearchResult<Host> res = hostsDAO.searchHostsInIdSet(hsc, hostSet);
		List<Host> sublist = testhosts.subList(0, 10);
		assertEquals(sublist, res.getResults());
	}
}
