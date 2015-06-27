package test.hosts;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.test.AbstractTransactionalSpringContextTests;
import org.springframework.test.AssertThrows;

import edu.bath.soak.net.CSVParser;
import edu.bath.soak.net.CSVParser.CSVParseException;
import edu.bath.soak.net.model.Host;
import edu.bath.soak.net.model.NetDAO;
import edu.bath.soak.testutils.TestData;
import edu.bath.soak.web.DefaultHostData;

/**
 * Middle tier integration tests for host operations
 * 
 * @author cspocc
 * 
 */
public class HostCSVParserTests extends AbstractTransactionalSpringContextTests {

	Logger log = Logger.getLogger(this.getClass());

	NetDAO hostsDAO;
	CSVParser cSVParser;

	public HostCSVParserTests() {
		setAutowireMode(AUTOWIRE_BY_NAME);
	}

	@Override
	protected String[] getConfigLocations() {
		return SpringSetup.BASIC_TEST_LOCS;
	}

	TestData td;
	List<Host> testHosts;

	@Override
	protected void onSetUpInTransaction() throws Exception {
		super.onSetUpInTransaction();
		td = new TestData(hostsDAO);
		testHosts = new ArrayList<Host>();
		for (int i = 0; i < 11; i++) {
			Host dummy = td.dummyHost(td.getTestSubnet(), "bulktestHost",
					i + 80);
			log.trace("creating host " + dummy);
			dummy.getLocation().setRoom("test room");
			dummy.getLocation().setBuilding("test bldg");
			hostsDAO.saveHost(dummy, "testcmd");
			testHosts.add(dummy);
		}
		SpringSetup.setUpBasicAcegiAuthentication(false, td.getTestOrgUnit());

	}

	public void testFewerEntriesOK() throws Exception {
		String csvData = "";
		for (Host h : testHosts) {
			csvData += h.getHostName().toString() + "\n";
			;
		}
		DefaultHostData dhd = new DefaultHostData();
		List<Host> parsedHosts = cSVParser.extractBeanData(Host.class,
				new ByteArrayInputStream(csvData.getBytes()), dhd);
		for (int i = 0; i < testHosts.size(); i++) {
			assertEquals(testHosts.get(i).getHostName(), parsedHosts.get(i)
					.getHostName());
		}

	}

	public void testFewerEntriesBAD() throws Exception {
		String csvData = "";
		for (Host h : testHosts) {
			csvData += "\n";
			;
		}
		final String csvText = csvData;
		new AssertThrows(CSVParseException.class) {
			@Override
			public void test() throws Exception {
				DefaultHostData dhd = new DefaultHostData();

				// TODO Auto-generated method stub
				cSVParser.extractBeanData(Host.class, new ByteArrayInputStream(
						csvText.getBytes()), dhd);

			}
		};

	}

	public void testParseHostsOK() throws Exception {
		String csvData = "";
		for (Host h : testHosts) {
			csvData += h.getHostName().toString() + ","
					+ h.getMacAddress().toString() + ","

					+ h.getIpAddress().getHostAddress() + ","
					+ h.getHostClass().getId() + ","
					+ h.getOwnership().getOrgUnit().getId() + ","
					+ h.getLocation().getBuilding() + ","
					+ h.getLocation().getRoom() + "," + h.getDescription()
					+ "\n";
			;
		}

		log.debug("About to parse" + csvData);
		DefaultHostData dhd = new DefaultHostData();
		List<Host> parsedHosts = cSVParser.extractBeanData(Host.class,
				new ByteArrayInputStream(csvData.getBytes()), dhd);
		assertEquals(testHosts, parsedHosts);
	}

	public void testParseHostsDefaultsOK() throws Exception {
		String csvData = "";

		for (Host h : testHosts) {
			csvData += h.getHostName().toString() + ","
				+ h.getMacAddress().toString() + "," 
					+ h.getIpAddress().getHostAddress() + ","
					+ "," + "," + ","
					+ "," + h.getDescription() + "\n";
			;
		}

		log.debug("About to parse" + csvData);
		Host defaultHost = testHosts.get(0);
		DefaultHostData dhd = new DefaultHostData();
		dhd.getOwnership().setOrgUnit(defaultHost.getOwnership().getOrgUnit());
		dhd.getLocation().setBuilding(defaultHost.getLocation().getBuilding());
		dhd.getLocation().setRoom(defaultHost.getLocation().getRoom());
		dhd.setHostClass(defaultHost.getHostClass());

		List<Host> parsedHosts = cSVParser.extractBeanData(Host.class,
				new ByteArrayInputStream(csvData.getBytes()), dhd);
		assertEquals(testHosts, parsedHosts);
	}

	@Required
	public void setHostsDAO(NetDAO hostsDAO) {
		this.hostsDAO = hostsDAO;
	}

	public void setHostsCSVParser(CSVParser cSVParser) {
		this.cSVParser = cSVParser;
	}

}
