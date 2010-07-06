package test.livedata;

import java.io.PrintWriter;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.test.AbstractTransactionalSpringContextTests;

import test.hosts.SpringSetup;
import edu.bath.soak.net.HostsFileGenerator;
import edu.bath.soak.net.model.Host;
import edu.bath.soak.net.model.NetDAO;
import edu.bath.soak.net.query.HostSearchQuery;
import edu.bath.soak.query.SearchResult;
import edu.bath.soak.util.TypeUtils;

public class HostsFileTest extends AbstractTransactionalSpringContextTests {
	Logger log = Logger.getLogger(HostsFileTest.class);
	NetDAO hostsDAO;
	HostsFileGenerator hostsFileGenerator;
	
	public HostsFileTest() {
		setAutowireMode(AUTOWIRE_BY_NAME);
	}

	@Override
	protected String[] getConfigLocations() {
		return SpringSetup.BASIC_LIVETEST_LOCS;
	}

	public void setHostsDAO(NetDAO hostsDao) {
		this.hostsDAO = hostsDao;
	}
	public void setHostsFileGenerator(HostsFileGenerator hostsFileGenerator){
		this.hostsFileGenerator = hostsFileGenerator;
	}

	public void testGenerateHostsFile() {
		

	}

}
