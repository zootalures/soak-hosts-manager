package test.hosts;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.test.AbstractTransactionalSpringContextTests;
import org.springframework.test.AssertThrows;

import edu.bath.soak.net.CSVParser;
import edu.bath.soak.net.HostsFileGenerator;
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
public class HostsFileGenerationTests extends AbstractTransactionalSpringContextTests {

	Logger log = Logger.getLogger(this.getClass());

	NetDAO hostsDAO;
	HostsFileGenerator hostsFileGenerator;
	
	public HostsFileGenerationTests() {
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
		
		SpringSetup.setUpBasicAcegiAuthentication(false, td.getTestOrgUnit());

	}


	@Required
	public void setHostsDAO(NetDAO hostsDAO) {
		this.hostsDAO = hostsDAO;
	}


}
