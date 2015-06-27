package edu.bath.soak.testutils;

import java.net.Inet4Address;
import java.util.HashSet;
import java.util.Set;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import edu.bath.soak.model.OrgUnit;
import edu.bath.soak.model.OrgUnitAcl.Permission;
import edu.bath.soak.net.model.Host;
import edu.bath.soak.net.model.HostClass;
import edu.bath.soak.net.model.HostFlag;
import edu.bath.soak.net.model.HostName;
import edu.bath.soak.net.model.Location;
import edu.bath.soak.net.model.NameDomain;
import edu.bath.soak.net.model.NetDAO;
import edu.bath.soak.net.model.NetworkClass;
import edu.bath.soak.net.model.Subnet;
import edu.bath.soak.net.model.Vlan;
import edu.bath.soak.net.model.HostClass.DHCP_STATUS;
import edu.bath.soak.util.MacAddress;
import edu.bath.soak.util.TypeUtils;

@Transactional
public class TestData {

	OrgUnit testOrgUnit;
	Vlan testVlan;
	Subnet testSubnet;
	Host testHost;
	HostClass testHostClass;
	HostFlag testFlag;
	NetworkClass testNetClass;
	NameDomain testNameDomain;
	NetDAO dao;

	public NetworkClass dummyNetClass() {
		NetworkClass dummyClass = new NetworkClass();
		dummyClass.setId("TESTNC");
		dummyClass.setName("Test network class name ");
		dummyClass.setDescription("Testing network class");
		dummyClass.getOrgUnitAcl().getAclEntries().put(testOrgUnit,
				Permission.ALLOWED);

		return dummyClass;

	}

	public Subnet dummySubnet(int idx) {
		Subnet dummy = new Subnet();
		dummy.setComments("no comment");
		dummy.setDescription("A description of test subnet " + idx);
		dummy.setName("Test Subnet " + idx);
		dummy.setMinIP(TypeUtils.txtToIP("10.0." + idx + ".0"));
		dummy.setMaxIP(TypeUtils.txtToIP("10.0." + idx + ".255"));
		dummy.setNetworkClass(testNetClass);
		dummy.setGateway(TypeUtils.txtToIP("10.0.0.254"));
		dummy.setVlan(testVlan);
		dummy.setNoScan(false);
		dummy.getOrgUnitAcl().getAclEntries().put(testOrgUnit,
				Permission.ALLOWED);

		return dummy;
	}

	public Host dummyHost(Subnet s, String n, int i) {
		return dummyHost(s, testHostClass, testNameDomain, n, i);
	}

	public Host dummyHost(Subnet s, HostClass hc, NameDomain d, String name,
			int idx) {

		Assert.notNull(s);
		Assert.isTrue(idx >= 0 && idx < s.getNumUseableAddresses());

		Host h = new Host();
		h.setDescription("Owen's test host " + idx);
		h.setHostClass(testHostClass);
		Inet4Address hostip = TypeUtils.ipMath(s.getMinUsableAddress(), idx);
		h.setIpAddress(hostip);

		byte[] ipbytes = hostip.getAddress();
		byte[] macbytes = new byte[6];
		macbytes[0] = 0x11;
		macbytes[2] = 0x12;
		for (int j = 0; j < 4; j++)
			macbytes[j + 2] = ipbytes[j];
		MacAddress mac = MacAddress.fromBytes(macbytes);
		h.setMacAddress(mac);

		HostName hn = new HostName();
		hn.setDomain(testNameDomain);
		hn.setName(name + idx);

		h.setHostName(hn);
		Location l = new Location();
		l.setBuilding("Owens house");
		l.setRoom("Owen's room");
		h.setHostClass(testHostClass);
		h.setLocation(l);

		h.getOwnership().setOrgUnit(testOrgUnit);
		return h;
	}

	public TestData(NetDAO dao) {
		this.dao = dao;
		testVlan = new Vlan();
		testVlan.setNumber(101);
		testVlan.setName("test_vlan");
		testVlan.setDescription("Vlan for testing things");
		dao.saveVlan(testVlan);

		testOrgUnit = new OrgUnit();
		testOrgUnit.setId("TEST");
		testOrgUnit.setName("Test Org Unit");
		dao.saveOrgUnit(testOrgUnit);

		testHostClass = new HostClass();
		testHostClass.setId("PC");
		testHostClass.setName("Personal Computer");
		testHostClass
				.setDescription("Personal Computer not on Active Directory");
		testHostClass.setDHCPStatus(DHCP_STATUS.IF_POSSIBLE);
		testHostClass.getOrgUnitAcl().getAclEntries().put(testOrgUnit,
				Permission.ALLOWED);
		testHostClass.setCanHaveAliases(true);
		dao.saveHostClass(testHostClass);

		testNetClass = dummyNetClass();
		testNetClass.getAllowedHostClasses().add(testHostClass);
		testNetClass.getOrgUnitAcl().getAclEntries().put(testOrgUnit,
				Permission.ALLOWED);
		dao.saveNetworkClass(testNetClass);

		testSubnet = dummySubnet(10);
		dao.saveSubnet(testSubnet);

		testNameDomain = new NameDomain();
		testNameDomain.setSuffix(".testdomain.");
		testNameDomain.getOrgUnitAcl().getAclEntries().put(testOrgUnit,
				Permission.ALLOWED);

		dao.saveNameDomain(testNameDomain);
		Set<HostClass> hcs = new HashSet<HostClass>();
		hcs.add(testHostClass);
		testNameDomain.setAllowedClasses(hcs);
		testFlag = new HostFlag();
		testFlag.setFlag("TESTFLAG");
		testFlag.setDescription("Testing Flag");
		dao.saveHostFlag(testFlag);

		testHost = dummyHost(testSubnet, "testhost", 1);
		dao.saveHost(testHost,"testcmd");

	}

	public Vlan getTestVlan() {
		return testVlan;
	}

	public Subnet getTestSubnet() {
		return testSubnet;
	}

	public Host getTestHost() {
		return testHost;
	}

	public HostClass getTestHostClass() {
		return testHostClass;
	}

	public HostFlag getTestFlag() {
		return testFlag;
	}

	public NetworkClass getTestNetClass() {
		return testNetClass;
	}

	public NameDomain getTestNameDomain() {
		return testNameDomain;
	}

	public OrgUnit getTestOrgUnit() {
		return testOrgUnit;
	}
}
