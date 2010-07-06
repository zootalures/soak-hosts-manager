package test.web;

import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.AbstractTransactionalSpringContextTests;
import org.springframework.web.servlet.ModelAndView;

import test.hosts.SpringSetup;
import edu.bath.soak.net.model.NetDAO;
import edu.bath.soak.net.model.Subnet;
import edu.bath.soak.net.model.Vlan;
import edu.bath.soak.testutils.TestData;
import edu.bath.soak.util.TypeUtils;
import edu.bath.soak.web.subnet.SubnetController;

public class TestSubnetController extends
		AbstractTransactionalSpringContextTests {

	Logger log = Logger.getLogger(TestSubnetController.class);
	NetDAO hostDAO;
	SubnetController subnetController;
	public TestSubnetController() {
		super();
		setAutowireMode(AUTOWIRE_BY_NAME);
	}

	@Override
	protected String[] getConfigLocations() {
		return SpringSetup.WEB_TEST_LOCS;
	}

	public Subnet addDummySubnet() {

		Subnet s = new Subnet();
		Vlan v = new Vlan();
		v.setName("test");
		v.setId(Long.valueOf(1));
		hostDAO.saveVlan(v);

		s.setDescription("Test subnet");
		s.setMinIP(TypeUtils.txtToIP("138.38.32.0"));
		s.setMaxIP(TypeUtils.txtToIP("138.38.33.255"));
		s.setName("testing");
		s.setVlan(v);

		hostDAO.saveSubnet(s);

		Long id = s.getId();

		return hostDAO.getSubnet(id);
	}

	/**
	 * lists subnets
	 * 
	 * @throws Exception
	 */
//	public void testListSubnets() throws Exception {
//		TestData td = new TestData(hostDAO);
//		MockHttpServletRequest req = new MockHttpServletRequest();
//		MockHttpServletResponse resp = new MockHttpServletResponse();
//
//		log.trace("requesting subnet list from controller");
//		ModelAndView mv = subnetController.sear(req, resp);
//		log.trace("got subnet list from controller");
//		Map<String, Object> model = mv.getModel();
//		assertNotNull(model);
//		SearchResult<Subnet> sr = (SearchResult<Subnet>) model.get("search");
//		List<Subnet> subnets = sr.getResults();
//		assertNotNull(subnets);
//		assertTrue(subnets.contains(td.getTestSubnet()));
//
//		log.trace("got subnet list from controller");
//
//	}

	public void testViewSubnetSuccessful() throws Exception {
		TestData td = new TestData(hostDAO);
		MockHttpServletRequest req = new MockHttpServletRequest();
		MockHttpServletResponse resp = new MockHttpServletResponse();
		Subnet s = td.getTestSubnet();

		log.trace("requesting subnet " + s.getId() + " from controller");

		req.addParameter("id", ((Long) s.getId()).toString());

		ModelAndView mv = subnetController.view(req, resp);
		log.trace("got response from controller");

		Map<String, Object> model = mv.getModel();
		assertNotNull(model);
		Subnet gotsn = (Subnet) model.get("subnet");
		assertNotNull(gotsn);
		assertEquals(s, gotsn);
		log.trace("got subnet " + gotsn + "from controller");
	}

	public void setHostsDAO(NetDAO hostDAO) {
		this.hostDAO = hostDAO;
	}

	public void setSubnetController(SubnetController subnetController) {
		this.subnetController = subnetController;
	}

}
