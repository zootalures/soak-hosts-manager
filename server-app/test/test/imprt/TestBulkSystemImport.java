package test.imprt;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.springframework.test.AbstractTransactionalSpringContextTests;

import test.hosts.SpringSetup;

import com.sun.xml.bind.IDResolver;

import edu.bath.soak.imprt.cmd.BulkSystemImportCmd;
import edu.bath.soak.imprt.cmd.XMLImportData;
import edu.bath.soak.imprt.mgr.BulkImportManagerImpl;
import edu.bath.soak.net.model.NetDAO;
import edu.bath.soak.xml.DelegatingIDresolver;

public class TestBulkSystemImport extends
		AbstractTransactionalSpringContextTests {

	BulkImportManagerImpl bulkImportManagerImpl;
	NetDAO hostsDAO;
	

	public TestBulkSystemImport() {
		setAutowireMode(AUTOWIRE_BY_NAME);
	}

	@Override
	protected String[] getConfigLocations() {
		return SpringSetup.BASIC_TEST_LOCS;
	}

	public void testBulkImportBathData()throws Exception{
		XMLImportData data = new XMLImportData();
		
		JAXBContext ctx = JAXBContext.newInstance("edu.bath.soak");
		Unmarshaller unmarshaller= ctx.createUnmarshaller();
		unmarshaller.setProperty(IDResolver.class.getName(), new DelegatingIDresolver(hostsDAO.getXMLIDresolver()));
	
		data.addAll((XMLImportData)unmarshaller.unmarshal(new File("bathdata/vlans.xml")));
		data.addAll((XMLImportData)unmarshaller.unmarshal(new File("bathdata/hostClasses.xml")));
		data.addAll((XMLImportData)unmarshaller.unmarshal(new File("bathdata/networkClasses.xml")));
		data.addAll((XMLImportData)unmarshaller.unmarshal(new File("bathdata/nameDomains.xml")));
		data.addAll((XMLImportData)unmarshaller.unmarshal(new File("bathdata/subnets.xml")));
		data.addAll((XMLImportData)unmarshaller.unmarshal(new File("bathdata/hosts.xml")));
		
		BulkSystemImportCmd cmd = new BulkSystemImportCmd();
		cmd.setData(data);
		bulkImportManagerImpl.implementCmd(null, cmd);
	};
	
	public void setBulkManager(BulkImportManagerImpl bulkImportManagerImpl) {
		this.bulkImportManagerImpl = bulkImportManagerImpl;
	}

	public void setHostsDAO(NetDAO hostsDAO) {
		this.hostsDAO = hostsDAO;
	}
	
}
