package test.web;

import java.util.Collection;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.util.Assert;
import org.springframework.webflow.definition.registry.FlowDefinitionRegistry;
import org.springframework.webflow.definition.registry.FlowDefinitionRegistryImpl;
import org.springframework.webflow.test.MockParameterMap;
import org.springframework.webflow.test.execution.AbstractXmlFlowExecutionTests;

import test.hosts.SpringSetup;
import edu.bath.soak.model.OrgUnit;
import edu.bath.soak.net.model.NetDAO;

public abstract class BaseHostFlowTest extends AbstractXmlFlowExecutionTests {

	ApplicationContext context;
	NetDAO hostsDAO;;

	static MockParameterMap buildParams(String... parms) {
		MockParameterMap parmMap = new MockParameterMap();
		Assert.isTrue(parms.length % 2 == 0);
		for (int i = 0; i < parms.length; i += 2) {
			parmMap.put(parms[i], parms[i + 1]);
		}
		return parmMap;

	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		context = new FileSystemXmlApplicationContext(getContextLocations());
		hostsDAO = (NetDAO) context.getBean("hostsDAO");
	}

	public String[] getContextLocations() {
		return SpringSetup.WEB_TEST_LOCS;
	}

	public BaseHostFlowTest() {
		super();

	}

	void grantOuAdminPrivs(Collection<OrgUnit> orgUnits) {

	}



	public ApplicationContext getContext() {
		return context;
	}

	public void setContext(ApplicationContext context) {
		this.context = context;
	}
}
