package edu.bath.soak.testutils;

import java.io.IOException;
import java.net.Inet4Address;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;

import edu.bath.soak.dhcp.model.DBBackedDHCPServer;
import edu.bath.soak.dhcp.model.DHCPDao;
import edu.bath.soak.dhcp.model.DHCPScope;
import edu.bath.soak.dns.DNSMgrImpl;
import edu.bath.soak.dns.model.DNSDao;
import edu.bath.soak.dns.model.ForwardZone;
import edu.bath.soak.dns.model.ReverseZone;
import edu.bath.soak.net.model.HostClass;
import edu.bath.soak.net.model.NameDomain;
import edu.bath.soak.net.model.NetDAO;
import edu.bath.soak.net.model.NetworkClass;
import edu.bath.soak.net.model.Subnet;
import edu.bath.soak.net.model.Vlan;
import edu.bath.soak.net.model.HostClass.DHCP_STATUS;
import edu.bath.soak.util.TypeUtils;

public class DevTestData implements ApplicationListener {
	DNSDao dnsDAO;
	NetDAO netDAO;
	DHCPDao dhcpDAO;
	DNSMgrImpl dnsMgrImpl;
	BindWrapper bindWrapper;
	NameDomain standardND;
	ForwardZone standardNDFwZone;

	NameDomain adND;
	ForwardZone adNDFwZone;

	Vlan testVlan1;
	Vlan testVlan2;

	HostClass adPCHC;
	HostClass printerHC;
	HostClass gwHC;

	NetworkClass testNetworkClass1;
	NetworkClass testNetworkClass2;

	Subnet testSubnet1;
	Subnet testSubnet2;
	boolean setUPcomplete;

	public void setUp() throws IOException {
		if (bindWrapper != null) {
			bindWrapper.cleanUp();
		}

		int testDNSServerPort = 9094;
		Inet4Address testDNSServerIP = TypeUtils.txtToIP("127.0.0.1");
		String keyname = "soak-testkey";
		String keydata = "tbd_dns_test_key";

		testVlan1 = new Vlan();
		testVlan1.setNumber(109);
		testVlan1.setName("Test Vlan 1");
		netDAO.saveVlan(testVlan1);

		testVlan2 = new Vlan();
		testVlan2.setNumber(110);
		testVlan2.setName("Test Vlan 2");
		netDAO.saveVlan(testVlan2);

		testNetworkClass1 = new NetworkClass();
		testNetworkClass1.setName("Campus");
		testNetworkClass1.setId("CAMPUS");
		testNetworkClass1.setDescription("CAMPUS network");
		
		netDAO.saveNetworkClass(testNetworkClass1);

		Subnet sn = testSubnet1 = new Subnet();
		sn.setName("test subnet 1");
		sn.setNetworkClass(testNetworkClass1);
		sn.setMinIP(TypeUtils.txtToIP("10.0.0.0"));
		sn.setMaxIP(TypeUtils.txtToIP("10.0.0.255"));
		sn.setGateway(TypeUtils.txtToIP("10.0.0.254"));
		sn.setDescription("Testing subnet 1");
		sn.setVlan(testVlan1);
		netDAO.saveSubnet(testSubnet1);

		sn = testSubnet2 = new Subnet();
		sn.setName("test subnet 2");
		sn.setNetworkClass(testNetworkClass1);
		sn.setMinIP(TypeUtils.txtToIP("10.1.0.0"));
		sn.setMaxIP(TypeUtils.txtToIP("10.1.255.255"));
		sn.setGateway(TypeUtils.txtToIP("10.1.255.254"));
		sn.setDescription("Testing subnet 2");
		sn.setVlan(testVlan2);
		netDAO.saveSubnet(testSubnet2);

		HostClass hc = adPCHC = new HostClass();
		hc.setDescription("Active directory PC");
		hc.setId("ADPC");
		hc.setName("Active directory PC");
		hc.setDHCPStatus(DHCP_STATUS.REQUIRED);
		netDAO.saveHostClass(adPCHC);
		
		hc = printerHC = new HostClass();
		hc.setDescription("Printer");
		hc.setId("PRINTER");
		hc.setName("Printer");
		hc.setDHCPStatus(DHCP_STATUS.IF_POSSIBLE);
		netDAO.saveHostClass(printerHC);

		hc = gwHC = new HostClass();
		hc.setDescription("Gateway");
		hc.setId("GW");
		hc.setName("IP Gateway");
		hc.setDHCPStatus(DHCP_STATUS.NONE);
		netDAO.saveHostClass(gwHC);

		NameDomain nd = standardND = new NameDomain();
		nd.setSuffix(".testdomain.");
		Set<HostClass> hcs = new HashSet<HostClass>();
		hcs.add(gwHC);
		hcs.add(printerHC);
		nd.setAllowedClasses(hcs);
		netDAO.saveNameDomain(nd);

		nd = adND = new NameDomain();
		nd.setSuffix(".campus.testdomain.");
		hcs = new HashSet<HostClass>();
		hcs.add(printerHC);
		hcs.add(adPCHC);
		nd.setAllowedClasses(hcs);
		netDAO.saveNameDomain(nd);

		List<String> zones = new ArrayList<String>();

		ForwardZone fz = standardNDFwZone = new ForwardZone();
		fz.setDefaultTTL(3600L);
		fz.setDescription("Test Zone");
		fz.setDisplayName("Standard internal test zone");
		fz.setDomain("testdomain.");
		fz.setSigKey(keyname + ":" + keydata);
		fz.setServerIP(testDNSServerIP);
		fz.setServerPort(testDNSServerPort);
		fz.setIgnoreHostRegexps("^.*\\.campus.testdomain\\.$");
		dnsDAO.saveZone(fz);
		zones.add(fz.getDomain());

		fz = standardNDFwZone = new ForwardZone();
		fz.setDefaultTTL(3600L);
		fz.setDescription("Test Zone");
		fz.setDisplayName("Standard internal test zone");
		fz.setDomain("campus.testdomain.");
		fz.setSigKey(keyname + ":" + keydata);
		fz.setServerIP(testDNSServerIP);
		fz.setServerPort(testDNSServerPort);
		dnsDAO.saveZone(fz);
		zones.add(fz.getDomain());

		ReverseZone rz = new ReverseZone();
		rz.setDefaultTTL(3600L);
		rz.setDisplayName("Reverse zone ");
		rz.setDescription("Test reverse zone");
		rz.setDomain("10.in-addr.arpa.");
		rz.setSigKey(keyname + ":" + keydata);
		rz.setServerIP(testDNSServerIP);
		rz.setServerPort(testDNSServerPort);
		dnsDAO.saveZone(rz);
		zones.add(rz.getDomain());

		
		DBBackedDHCPServer dhcpserver = new DBBackedDHCPServer();
		dhcpserver.setDisplayName("Test DHCP Server");
		dhcpDAO.saveDHCPServer(dhcpserver);
		
		DHCPScope scope = new DHCPScope();
		scope.setMinIP(testSubnet2.getMinIP());
		scope.setMaxIP(testSubnet2.getMaxIP());
		scope.setServer(dhcpserver);
		dhcpDAO.saveScope(scope);
		
		Map<String, String> keys = new HashMap<String, String>();

		keys.put(keyname, keydata);

		bindWrapper = new BindWrapper();
		bindWrapper.setDeleteOnCleanup(true);
		bindWrapper.setPort(testDNSServerPort);
		bindWrapper.setZones(zones);
		bindWrapper.setKeys(keys);
		bindWrapper.startBind();

	}

	public void onApplicationEvent(ApplicationEvent event) {
		try {
			if (event instanceof ContextRefreshedEvent && !setUPcomplete) {
				setUp();
				setUPcomplete = true;
			} else if (event instanceof ContextClosedEvent) {
				if (bindWrapper != null) {
					bindWrapper.cleanUp();
				}

			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	@Required
	public void setDnsDAO(DNSDao dnsDAO) {
		this.dnsDAO = dnsDAO;
	}

	@Required
	public void setNetDAO(NetDAO netDAO) {
		this.netDAO = netDAO;
	}

	@Required
	public void setDnsMgrImpl(DNSMgrImpl dnsMgrImpl) {
		this.dnsMgrImpl = dnsMgrImpl;
	}

	@Required
	public void setDhcpDAO(DHCPDao dhcpDAO) {
		this.dhcpDAO = dhcpDAO;
	}

}
