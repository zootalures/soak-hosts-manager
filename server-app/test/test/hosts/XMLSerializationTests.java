package test.hosts;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.test.AbstractTransactionalSpringContextTests;

import edu.bath.soak.imprt.cmd.XMLImportData;
import edu.bath.soak.net.model.Host;
import edu.bath.soak.net.model.HostAlias;
import edu.bath.soak.net.model.HostName;
import edu.bath.soak.net.model.NetDAO;
import edu.bath.soak.net.query.HostSearchQuery;
import edu.bath.soak.query.SearchResult;
import edu.bath.soak.testutils.TestData;
import edu.bath.soak.xml.SoakXMLManager;

public class XMLSerializationTests extends
		AbstractTransactionalSpringContextTests {

	NetDAO hostsDAO;
	TestData td;
	Logger log = Logger.getLogger(this.getClass());
	SoakXMLManager xmlManger;

	@Override
	protected void onSetUpInTransaction() throws Exception {
		super.onSetUpInTransaction();
		td = new TestData(hostsDAO);
	}

	@Override
	protected String[] getConfigLocations() {
		return SpringSetup.BASIC_TEST_LOCS;
	}

	public void testSerializeHostAsXML() throws Exception {
		Host testHost = hostsDAO.getHostForEditing(td.getTestHost().getId());
		HostAlias a = new HostAlias();
		a.setHost(testHost);
		HostName alias = new HostName();
		alias.setName("testalias");
		alias.setDomain(td.getTestNameDomain());
		a.setAlias(alias);
		testHost.getHostAliases().add(a);
		HostAlias b = new HostAlias();
		b.setHost(testHost);
		HostName blias = new HostName();
		blias.setName("testalias2");
		blias.setDomain(td.getTestNameDomain());
		b.setAlias(blias);
		testHost.getHostAliases().add(b);

		hostsDAO.saveHost(testHost,"testcmd");

		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		xmlManger.marshall(testHost, bos);

		Host newH = xmlManger.unmarshall(Host.class, hostsDAO
				.getXMLIDresolver(),
				new ByteArrayInputStream(bos.toByteArray()));

		assertEquals(testHost, newH);
		assertEquals(testHost.getHostName(), newH.getHostName());
		assertEquals(testHost.getIpAddress(), newH.getIpAddress());
		assertEquals(testHost.getMacAddress(), newH.getMacAddress());
		assertEquals(testHost.getHostClass(), newH.getHostClass());
		assertEquals(testHost.getHostAliases(), newH.getHostAliases());

	}

	public void testSystemExport() throws Exception {
		XMLImportData command = new XMLImportData();
		HostSearchQuery hq = new HostSearchQuery();
		hq.setOrderBy("ipAddress");
		hq.setAscending(true);
		hq.setMaxResults(-1);
		hq.setFirstResult(0);
		SearchResult<Host> res = hostsDAO.searchHosts(hq);
		command.setHosts(res.getResults());
		command.setVlans(hostsDAO.getAllVlans());
		command.setNameDomains(hostsDAO.getNameDomains());
		command.setSubnets(hostsDAO.getSubnets());
		command.setHostClasses(hostsDAO.getHostClasses());
		command.setNetworkClasses(hostsDAO.getNetworkClasses());

		JAXBContext ctx = JAXBContext.newInstance("edu.bath.soak");
		Marshaller marshaller = ctx.createMarshaller();
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		marshaller.marshal(command, bos);

	}

	public void testImportBathData() throws Exception {

	}

	public void setHostsDAO(NetDAO hostsDAO) {
		this.hostsDAO = hostsDAO;
	}

	@Required
	public void setXmlManger(SoakXMLManager xmlManger) {
		this.xmlManger = xmlManger;
	}

}
