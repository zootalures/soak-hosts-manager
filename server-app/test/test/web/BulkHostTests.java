package test.web;

import org.springframework.webflow.config.FlowDefinitionResource;
import org.springframework.webflow.config.FlowDefinitionResourceFactory;

import edu.bath.soak.testutils.TestData;

public class BulkHostTests extends BaseHostFlowTest {

	TestData td;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	public void testBulkActionOK() {
		
	}

	
	@Override
	protected FlowDefinitionResource getResource(
			FlowDefinitionResourceFactory factory) {
		return factory.createFileResource("web/WEB-INF/flows/starred-actions.flow.xml");
	
	}
}
