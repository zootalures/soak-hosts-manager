package test.web;

import org.springframework.webflow.config.FlowDefinitionResourceFactory;

import edu.bath.soak.testutils.TestData;

public class HostCrudFlowTest extends BaseHostFlowTest {

	TestData td;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		td = new TestData(hostsDAO);
	}

	public void testCreateStartHost() {
		assertFlowExecutionActive();
	}

	public void testCreateHostSelectType() {
		// testCreateStartHost();
		// HostClass hc = td.getTestHostClass();
		// ApplicationView vs = applicationView(signalEvent("submit",
		// buildParams(
		// "newHost.hostClass", hc.getId())));
		// assertViewNameEquals("host/editHostDetails", vs);
	}

	public void testCreateHostOnSpecificSubnetStart() {
		// MutableAttributeMap map = new LocalAttributeMap();
		// Subnet s = td.getTestSubnet();
		// map.put("subnetId", s.getId());
		// ApplicationView vs = applicationView(startFlow(map));
		// assertViewNameEquals("host/chooseHostType", vs);

	}

	@Override
	protected org.springframework.webflow.config.FlowDefinitionResource getResource(
			FlowDefinitionResourceFactory factory) {
		return factory.createResource("web/WEB-INF/flows/update-host.flow.xml");
	}
}
