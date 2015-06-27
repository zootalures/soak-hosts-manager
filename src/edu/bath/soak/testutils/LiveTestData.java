package edu.bath.soak.testutils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.springframework.security.GrantedAuthority;
import org.springframework.security.GrantedAuthorityImpl;
import org.springframework.security.context.SecurityContextHolder;
import org.springframework.security.providers.TestingAuthenticationToken;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.transaction.annotation.Transactional;

import com.sun.xml.bind.IDResolver;

import edu.bath.soak.dhcp.model.DBBackedDHCPServer;
import edu.bath.soak.dhcp.model.DHCPDao;
import edu.bath.soak.dhcp.model.DHCPScope;
import edu.bath.soak.dhcp.model.StaticDHCPReservation;
import edu.bath.soak.dns.DNSMgrImpl;
import edu.bath.soak.dns.model.DNSDao;
import edu.bath.soak.dns.model.DNSZone;
import edu.bath.soak.imprt.cmd.BulkSystemImportCmd;
import edu.bath.soak.imprt.cmd.XMLImportData;
import edu.bath.soak.imprt.mgr.BulkImportManagerImpl;
import edu.bath.soak.net.model.NetDAO;
import edu.bath.soak.security.SecurityHelper;
import edu.bath.soak.security.SoakUserDetails;
import edu.bath.soak.util.MacAddress;
import edu.bath.soak.util.TypeUtils;
import edu.bath.soak.xml.DelegatingIDresolver;

public class LiveTestData implements ApplicationListener {
	DNSDao dnsDAO;
	NetDAO netDAO;
	DHCPDao dhcpDAO;
	DNSMgrImpl dnsMgrImpl;
	BindWrapper bindWrapper;
	BulkImportManagerImpl bulkImportManagerImpl;
	List<String> importFiles = new ArrayList<String>();
	boolean setUPcomplete = false;
	String dhcpImportFile = null;
	Logger log = Logger.getLogger(getClass());
	List<DNSZone> dnsZones = new ArrayList<DNSZone>();
	String importDirectory;

	public void setupDHCP(String file) throws Exception {
		DBBackedDHCPServer testDHCPServer = new DBBackedDHCPServer();
		testDHCPServer.setDisplayName("Test DB-Backed server");
		dhcpDAO.saveDHCPServer(testDHCPServer);
		DHCPScope allAddresses = new DHCPScope();
		allAddresses.setMinIP(TypeUtils.txtToIP("0.0.0.0"));
		allAddresses.setMaxIP(TypeUtils.txtToIP("255.255.255.255"));
		allAddresses.setServer(testDHCPServer);
		dhcpDAO.saveScope(allAddresses);
		BufferedReader fileInput = new BufferedReader(new FileReader(file));
		String line;
		while (null != (line = fileInput.readLine())) {
			String[] parts = line.trim().split("\\s+");
			if (parts.length == 2) {
				StaticDHCPReservation sres = new StaticDHCPReservation();
				sres.setScope(allAddresses);
				sres.setIpAddress(TypeUtils.txtToIP(parts[0]));
				sres.setMacAddress(MacAddress.fromText(parts[1]));
				dhcpDAO.saveReservation(sres);
			}
		}
	}

	public List<String> getAllImportFiles() {
		ArrayList<String> files = new ArrayList<String>();

		if (null != importDirectory) {
			if (!importDirectory.endsWith(File.separator)) {
				importDirectory = importDirectory + File.separator;

			}
			log.info("Importing data from " + importDirectory);
			File importDir = new File(importDirectory);
			if (importDir.exists() && importDir.isDirectory()) {
				String[] importFiles = importDir.list(new FilenameFilter() {
					public boolean accept(File dir, String name) {
						return name.endsWith(".xml");

					};
				});

				Arrays.sort(importFiles);

				for (String file : importFiles) {
					file = importDirectory + file;
					File okfile = new File(file + ".OK");
					if ((!okfile.exists()) && okfile.getParentFile().canWrite()) {
						log.info("queueing file " + file + " for import");

						files.add(file);
					} else {
						log.error("skipping " + file
								+ "  already imported or cannot write to "
								+ okfile);
					}
				}
			}
		} else {
			files.addAll(importFiles);
		}
		return files;
	}

	@Transactional
	public void onApplicationEvent(ApplicationEvent event) {
		try {
			List<GrantedAuthority> auths = new ArrayList<GrantedAuthority>();
			auths.add(new GrantedAuthorityImpl(SecurityHelper.ADMIN_ROLE));

			SoakUserDetails details = new SoakUserDetails();
			details.setAuthorities(auths.toArray(new GrantedAuthority[] {}));
			details.setUsername("system-import");
			details.setFriendlyName("A. Friendly User");
			details.setEmail("nobody@nowhere.foo");
			TestingAuthenticationToken token = new TestingAuthenticationToken(
					details, null, details.getAuthorities());
			SecurityContextHolder.getContext().setAuthentication(token);

			if (event instanceof ContextRefreshedEvent && !setUPcomplete) {
				// bindWrapper = new BindWrapper();
				// bindWrapper.startBind();

				JAXBContext ctx = JAXBContext.newInstance("edu.bath.soak");
				Unmarshaller unmarshaller = ctx.createUnmarshaller();
				unmarshaller.setProperty(IDResolver.class.getName(),
						new DelegatingIDresolver(netDAO.getXMLIDresolver()));

				XMLImportData data = new XMLImportData();
				for (String file : getAllImportFiles()) {
					log.info("Importing data from " + file);
					file = file.trim();
					data.addAll((XMLImportData) unmarshaller
							.unmarshal(new FileInputStream(file)));

				}
				BulkSystemImportCmd bulkcmd = new BulkSystemImportCmd();
				bulkcmd.setData(data);

				bulkImportManagerImpl.implementBulkSystemImportCmd(UUID
						.randomUUID().toString(), bulkcmd);

				for (DNSZone dz : dnsZones) {
					dnsDAO.saveZone(dz);
				}
				if (null != dhcpImportFile)
					setupDHCP(dhcpImportFile);
				setUPcomplete = true;
				for (String file : getAllImportFiles()) {
					String okFileName = file + ".OK";
					File okFile = new File(okFileName);
					try {
						okFile.createNewFile();
					} catch (IOException e) {

					}
				}
			} else if (event instanceof ContextClosedEvent) {

			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {

			SecurityContextHolder.clearContext();
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

	public java.util.List<String> getImportFiles() {
		return importFiles;
	}

	public void setImportFiles(java.util.List<String> importFiles) {
		this.importFiles = importFiles;
	}

	@Required
	public void setBulkManager(BulkImportManagerImpl bulkImportManagerImpl) {
		this.bulkImportManagerImpl = bulkImportManagerImpl;
	}

	public String getDhcpImportFile() {
		return dhcpImportFile;
	}

	public void setDhcpImportFile(String dhcpImportFile) {
		this.dhcpImportFile = dhcpImportFile;
	}

	public List<DNSZone> getDnsZones() {
		return dnsZones;
	}

	public void setDnsZones(List<DNSZone> dnsZones) {
		this.dnsZones = dnsZones;
	}

	public void setImportDirectory(String importDirectory) {
		this.importDirectory = importDirectory;
	}

}
