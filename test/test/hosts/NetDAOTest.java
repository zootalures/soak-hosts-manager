package test.hosts;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.springframework.test.AbstractTransactionalSpringContextTests;

import edu.bath.soak.net.model.Host;
import edu.bath.soak.net.model.HostAlias;
import edu.bath.soak.net.model.HostChange;
import edu.bath.soak.net.model.HostClass;
import edu.bath.soak.net.model.HostName;
import edu.bath.soak.net.model.Location;
import edu.bath.soak.net.model.NameDomain;
import edu.bath.soak.net.model.NetDAO;
import edu.bath.soak.net.model.NetworkClass;
import edu.bath.soak.net.model.Subnet;
import edu.bath.soak.net.query.HostSearchQuery;
import edu.bath.soak.testutils.TestData;
import edu.bath.soak.util.MacAddress;
import edu.bath.soak.util.TypeUtils;

public class NetDAOTest extends AbstractTransactionalSpringContextTests {
	Logger log = Logger.getLogger(NetDAOTest.class);

	public NetDAOTest() {
		setAutowireMode(AUTOWIRE_BY_NAME);
	}

	NetDAO hostsDAO;
	TestData td;

	@Override
	protected void onSetUpInTransaction() throws Exception {
		super.onSetUpInTransaction();
		td = new TestData(hostsDAO);
	}

	@Override
	protected String[] getConfigLocations() {
		return SpringSetup.BASIC_TEST_LOCS;
	}

	public void testFindSubnetContainingIP() throws Throwable {
		Subnet rs = hostsDAO.findSubnetByBaseIP(td.getTestSubnet().getMinIP());
		assertNotNull(rs);
		assertEquals(td.getTestSubnet(), rs);
	}

	public void testFindSubnetByBaseIP() throws Throwable {
		Subnet rs = hostsDAO.findSubnetByBaseIP(td.getTestSubnet().getMinIP());
		assertNotNull(rs);
		assertEquals(td.getTestSubnet(), rs);
	}

	public void testgetAllSubnets() {
		List<Subnet> subnets = hostsDAO.getSubnets();
		assertNotNull(subnets);
		for (Subnet lrs : subnets) {
			log.trace(">> " + lrs);
		}
		assertTrue(subnets.contains(td.getTestSubnet()));
	}

	public void testfindHostFQDN() throws Exception {
		Host th = hostsDAO.findHost(td.getTestHost().getHostName().toString());
		assertNotNull(th);
		assertEquals(td.getTestHost(), th);
	}

	public void testfindHostHostName() throws Exception {
		Host th = hostsDAO.findHost(td.getTestHost().getHostName());
		assertNotNull(th);
		assertEquals(td.getTestHost(), th);
	}

	public void testfindHostHostNameAlias() throws Exception {
		Host h = td.dummyHost(td.getTestSubnet(), "testinghost-new", 2);
		HostAlias ha = new HostAlias();
		HostName alias = new HostName();
		alias.setDomain(td.getTestNameDomain());
		alias.setName("testing-alias1");
		ha.setAlias(alias);
		ha.setHost(h);
		h.getHostAliases().add(ha);
		HostAlias ha2 = new HostAlias();
		HostName alias2 = new HostName();
		alias2.setDomain(td.getTestNameDomain());
		alias2.setName("testing-alias2");
		ha2.setAlias(alias2);
		ha2.setHost(h);
		h.getHostAliases().add(ha2);
		hostsDAO.saveHost(h, "testcmd");

		List<Host> thl = hostsDAO.findHostIncludingAliases(h.getHostName());
		assertEquals(1, thl.size());
		Host th = thl.get(0);
		assertEquals(th, hostsDAO.findHostIncludingAliases(alias).get(0));

		assertEquals(th, hostsDAO.findHostIncludingAliases(alias2).get(0));
	}

	public void testHostClassMapping() throws Exception {
		Host th = hostsDAO.loadHost(td.getTestHost().getId());
		assertNotNull(th);
		assertEquals(td.getTestHostClass(), th.getHostClass());
	}

	public void testHostMapping() throws Exception {

		Host h = new Host();
		Location l = new Location();
		l.setBuilding("2S");
		l.setRoom("0.31");
		h.setLocation(l);
		NameDomain dn = new NameDomain();
		dn.setSuffix(".testdomain.");

		HostName hn = new HostName();
		hn.setName("testinghost");
		hn.setDomain(td.getTestNameDomain());
		h.setHostName(hn);
		h.setHostClass(hostsDAO.getHostClassById("PC"));
		h.setDescription("A test host created by me");
		h.setIpAddress(TypeUtils.txtToIP("10.0.0.34"));
		h.setMacAddress(MacAddress.fromText("c0:FF:ee:c0:ff:ee"));
		h.addFlag(td.getTestFlag());

		hostsDAO.saveHost(h, "testcmd");

		log.trace("Created host with ID:  " + h.getId());
		long id = h.getId();
		Host nh = hostsDAO.findHost(TypeUtils.txtToIP("10.0.0.34"));
		assertEquals(h, nh);

		nh = hostsDAO.loadHost(id);
		assertEquals(h, nh);

		nh = hostsDAO.findHost(h.getHostName().toString());
		assertNotNull(nh);
		assertEquals(h, nh);

		nh = hostsDAO.findHost(hostsDAO
				.getHostNameFromFQDN("testinghost.testdomain."));
		assertEquals(h, nh);

		nh = hostsDAO.findHost(MacAddress.fromText("c0:FF:ee:c0:ff:ee"));
		assertEquals(h, nh);

		assertTrue(nh.hasFlag(td.getTestFlag()));

		nh = hostsDAO.getHostForEditing(nh.getId());

		MacAddress newmac = MacAddress.fromText("aa:bb:cc:dd:ee:ff");
		System.err.println("Changing Host MAC Adddress  ");
		nh.setMacAddress(newmac);

		hostsDAO.saveHost(nh, "testcmd");
		nh = hostsDAO.loadHost(id);

		Assert.assertEquals(nh.getMacAddress().toString(), newmac.toString());

	}

	public void testGetHostNameFromFqdn() {
		HostName hn = hostsDAO.getHostNameFromFQDN(td.getTestHost()
				.getHostName().toString());
		assertNotNull(hn);
		assertEquals(td.getTestHost().getHostName(), hn);
	}

	public void testGetNetworkClasses() {
		List<NetworkClass> ncs = hostsDAO.getNetworkClasses();
		assertFalse(ncs.size() == 0);

	}

	public void testGetNetworkClassByName() {
		List<NetworkClass> ncs = hostsDAO.getNetworkClasses();
		assertFalse(ncs.size() == 0);

		for (NetworkClass nc : ncs) {
			assertNotNull(hostsDAO.getNetworkClassById(nc.getId()));
		}
		assertTrue(ncs.contains(td.getTestNetClass()));
	}

	public void testSaveHostFirstVersioning() {
		Host testhost = td.dummyHost(td.getTestSubnet(), "testversionhost", 13);
		hostsDAO.saveHost(testhost, "testcmd");

		Host gotHost = hostsDAO.loadHost(testhost.getId());
		assertEquals(testhost, gotHost);
		assertEquals((Long) 0L, gotHost.getVersion());
		HostChange hc = hostsDAO.getHostChangeAtVersion(gotHost.getId(), 0L);
		assertNull(hc);

	}

	public void testEditHostAliases() {
		Host testhost = td.dummyHost(td.getTestSubnet(), "testversionhost", 13);
		HostAlias ha = new HostAlias();
		ha.setHost(testhost);
		HostName name = new HostName();
		name.setName("testalias");
		name.setDomain(testhost.getHostName().getDomain());
		ha.setAlias(name);
		testhost.getHostAliases().add(ha);

		HostAlias ha2 = new HostAlias();
		ha2.setHost(testhost);
		HostName name2 = new HostName();
		name2.setName("testalias2");
		name2.setDomain(testhost.getHostName().getDomain());
		ha2.setAlias(name2);
		testhost.getHostAliases().add(ha);

		hostsDAO.saveHost(testhost, "testcmd");

		Host h = hostsDAO.getHostForEditing(testhost.getId());
		assertEquals(testhost, h);
		h.getHostAliases().remove(0);
		h.getHostAliases().get(0).setIdx(0);
		hostsDAO.saveHost(h, "testcmd");
	}

	public void testSaveHostSecondVersioning() {
		Host testhost = td.dummyHost(td.getTestSubnet(), "testversionhost", 13);
		testhost.setDescription(null);
		hostsDAO.saveHost(testhost, "testcmd");

		Host gotHost = hostsDAO.getHostForEditing(testhost.getId());
		assertEquals(testhost, gotHost);
		assertEquals((Long) 0L, gotHost.getVersion());
		gotHost.setDescription("foo");
		hostsDAO.saveHost(gotHost, "testcmd");
		assertEquals((Long) 1L, gotHost.getVersion());

		HostChange hc = hostsDAO.getHostChangeAtVersion(gotHost.getId(), 0L);
		assertNotNull(hc);
		assertNull(hc.getHost().getDescription());
		assertEquals((Long) 0L, hc.getHost().getVersion());
		assertEquals(0L, hc.getVersion());
	}

	public void testDeleteHostVersioningAfterSave() {
		Host testhost = td.dummyHost(td.getTestSubnet(), "testversionhost", 13);
		testhost.setDescription(null);
		hostsDAO.saveHost(testhost, "testcmd");

		hostsDAO.deleteHost(testhost.getId(), "testcmd");
		HostChange hc = hostsDAO.getHostChangeAtVersion(testhost.getId(), 0L);
		assertNotNull(hc);
		assertNull(hc.getHost().getDescription());
		assertEquals((Long) 0L, hc.getHost().getVersion());
		assertEquals(0L, hc.getVersion());
		assertEquals(testhost, hc.getHost());

	}

	public void testCountByGroupedProperty() {
		HostClass hc1 = new HostClass();
		hc1.setId("test-1");
		hc1.setName("Test 1");
		hostsDAO.saveHostClass(hc1);
		for (int i = 0; i < 5; i++) {
			Host h = td.dummyHost(td.getTestSubnet(), "testgph-1-hosts-",
					i + 10);
			h.setHostClass(hc1);
			hostsDAO.saveHost(h, "test");
		}

		HostClass hc2 = new HostClass();
		hc2.setId("test-2");
		hc2.setName("Test 2");
		hostsDAO.saveHostClass(hc2);
		for (int i = 0; i < 3; i++) {
			Host h = td.dummyHost(td.getTestSubnet(), "testgph-2-hosts-",
					i + 20);
			h.setHostClass(hc2);
			hostsDAO.saveHost(h, "test");
		}

		HostClass hc3 = new HostClass();
		hc3.setId("test-3");
		hc3.setName("Test 3");
		hostsDAO.saveHostClass(hc3);

		for (int i = 0; i < 6; i++) {
			Host h = td.dummyHost(td.getTestSubnet(), "testgph-3-hosts-",
					i + 30);
			h.setHostClass(hc3);
			hostsDAO.saveHost(h, "test");
		}

		HostSearchQuery hsq = new HostSearchQuery();
		hsq.setSearchTerm("testgph");
		Map<HostClass, Integer> results = hostsDAO
				.countHostsByHostClassForHostSearchQuery(hsq);
		assertEquals((Integer) 5, results.get(hc1));
		assertEquals((Integer) 3, results.get(hc2));
		assertEquals((Integer) 6, results.get(hc3));
	}

	public void testCountHostsOnSubnetByGroupedProperty() {

		Subnet s1 = td.dummySubnet(20);
		hostsDAO.saveSubnet(s1);
		for (int i = 0; i < 5; i++) {
			Host h = td.dummyHost(s1, "testgph-1-hosts-", i + 10);
			hostsDAO.saveHost(h, "test");
		}
		Subnet s2 = td.dummySubnet(21);
		hostsDAO.saveSubnet(s2);
		for (int i = 0; i < 3; i++) {
			Host h = td.dummyHost(s2, "testgph-1-hosts-", i + 20);
			hostsDAO.saveHost(h, "test");
		}
		Subnet s3 = td.dummySubnet(22);
		hostsDAO.saveSubnet(s3);
		for (int i = 0; i < 6; i++) {
			Host h = td.dummyHost(s3, "testgph-1-hosts-", i + 30);
			hostsDAO.saveHost(h, "test");
		}

		List<Subnet> subs = new ArrayList<Subnet>();
		subs.add(s1);
		subs.add(s2);
		subs.add(s3);

		Map<Subnet, Integer> results = hostsDAO.countHostsOnSubnetsForOU(subs,
				td.getTestOrgUnit(),false);
		assertEquals((Integer) 5, results.get(s1));
		assertEquals((Integer) 3, results.get(s2));
		assertEquals((Integer) 6, results.get(s3));
	}

	public void setHostsDAO(NetDAO hostsDao) {
		this.hostsDAO = hostsDao;
	}

}
