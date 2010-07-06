package test.web;

import org.apache.log4j.Logger;
import org.springframework.test.AbstractTransactionalSpringContextTests;

import test.hosts.SpringSetup;
import edu.bath.soak.net.model.NetDAO;
import edu.bath.soak.net.model.Subnet;
import edu.bath.soak.net.model.Vlan;
import edu.bath.soak.testutils.TestData;
import edu.bath.soak.web.subnet.SubnetController;

public class TestSubnetCRUDController extends
		AbstractTransactionalSpringContextTests {
	Logger log = Logger.getLogger(TestSubnetCRUDController.class);
	NetDAO hostsDAO;
	SubnetController subnetCRUDController;

	public TestSubnetCRUDController() {
		super();
		setAutowireMode(AUTOWIRE_BY_NAME);

	}

	public Vlan dummyVlan() {
		Vlan v = new Vlan();
		v.setId(Long.valueOf(100));
		v.setName("Test VLAN");
		v.setDescription("Testing vlan");
		return v;
	}

	public void testGetFormNew() {

	}

	public void testCreateSubnetOK() {
	

	}

	public void testEditSubnetOK() {
		TestData td = new TestData(hostsDAO);
		
		Subnet new_s = td.dummySubnet(5);
		hostsDAO.saveSubnet(new_s);

	}

	public void setHostsDAO(NetDAO hostDAO) {
		this.hostsDAO = hostDAO;
	}

	@Override
	protected String[] getConfigLocations() {
		return SpringSetup.WEB_TEST_LOCS;
	}

	public void setSubnetCRUDController(SubnetController subnetCRUDController) {
		this.subnetCRUDController = subnetCRUDController;
	}

}
