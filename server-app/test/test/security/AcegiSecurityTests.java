package test.security;

import java.util.Collection;

import org.apache.log4j.Logger;
import org.springframework.test.AbstractTransactionalSpringContextTests;

import test.hosts.SpringSetup;
import edu.bath.soak.model.OrgUnit;
import edu.bath.soak.model.OrgUnitAcl.Permission;
import edu.bath.soak.net.model.NetDAO;
import edu.bath.soak.net.model.Subnet;
import edu.bath.soak.security.AcegiSecurityHelperImpl;
import edu.bath.soak.testutils.TestData;

public class AcegiSecurityTests extends AbstractTransactionalSpringContextTests {
	Logger log = Logger.getLogger(AcegiSecurityTests.class);

	public AcegiSecurityTests() {
		setAutowireMode(AUTOWIRE_BY_NAME);
	}

	AcegiSecurityHelperImpl securityHelper;
	NetDAO hostsDAO;
	TestData td;

	@Override
	protected void onSetUpInTransaction() throws Exception {
		super.onSetUpInTransaction();
		td = new TestData(hostsDAO);
		securityHelper = new AcegiSecurityHelperImpl();

	}

	public void testCanUseUnion() {
		Subnet s1 = td.dummySubnet(10);
		Subnet s2 = td.dummySubnet(11);

		OrgUnit o1 = new OrgUnit();
		o1.setId("TESTOU1");
		o1.setName("Test ou 1");
		hostsDAO.saveOrgUnit(o1);

		OrgUnit o2 = new OrgUnit();
		o2.setId("TESTOU2");
		o2.setName("Test ou 2");
		hostsDAO.saveOrgUnit(o2);

		s1.getOrgUnitAcl().getAclEntries().put(o1, Permission.ALLOWED);

		s2.getOrgUnitAcl().getAclEntries().put(o2, Permission.ALLOWED);

		hostsDAO.saveSubnet(s1);
		hostsDAO.saveSubnet(s2);
		SpringSetup.setUpBasicAcegiAuthentication(false, o1, o2);
		assertTrue(securityHelper.getAllowedOrgUnitsForCurrentUser().contains(
				o1));
		assertTrue(securityHelper.getAllowedOrgUnitsForCurrentUser().contains(
				o2));

		Collection<Subnet> accessSubs = securityHelper
				.filterAllowedEntitiesForUser(hostsDAO.getSubnets());
		assertTrue(accessSubs.contains(s1));
		assertTrue(accessSubs.contains(s2));
		assertTrue(securityHelper.canUseOrgUnitAclEntity(s1));
		assertTrue(securityHelper.canUseOrgUnitAclEntity(s2));

	}

	@Override
	protected String[] getConfigLocations() {
		return SpringSetup.BASIC_TEST_LOCS;
	}

	public void setHostsDAO(NetDAO hostsDAO) {
		this.hostsDAO = hostsDAO;
	}

}
