package edu.bath.soak.testutils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.springframework.util.StringUtils;

/**
 * Simple harness for buliding and running bind
 * 
 * @author cspocc
 * 
 */
public class BindWrapper {
	Logger log = Logger.getLogger(getClass());
	String bindLocation = "/usr/sbin/named";
	int port = 9053;
	boolean deleteOnCleanup = true;
	Random r = new Random(System.currentTimeMillis());
	String baseDir = "/tmp/dns" + r.nextInt(1000);
	Map<String, String> keys = new HashMap<String, String>();
	Process bindProc;
	List<String> zones = new ArrayList<String>();
	boolean dumpLogOnCleanup = true;

	public void setBaseDir(String baseDir) {
		this.baseDir = baseDir;
	}

	public String getLogFile() {
		return baseDir + "/bind.log";
	}

	public String getBaseDir() {
		return baseDir;
	}

	public String getCacheDir() {
		return getBaseDir() + "/cache";
	}

	public String getZoneDir() {
		return getBaseDir() + "/zones";
	}

	String zoneFile(String zone) {
		return getZoneDir() + "/" + zone + ".db";
	}

	public String getConfig() {
		String config = "";

		config += "logging {\n" + "category \"default\" { \"debug\"; };\n"
				+ "category \"general\" { \"debug\"; };\n"
				+ "category \"database\" { \"debug\"; };\n"
				+ "category \"security\" { \"debug\"; };\n"
				+ "category \"config\" { \"debug\"; };\n"
				+ "category \"resolver\" { \"debug\"; };\n"
				+ "category \"xfer-in\" { \"debug\"; };\n"
				+ "category \"xfer-out\" { \"debug\"; };\n"
				+ "category \"notify\" { \"debug\"; };\n"
				+ "category \"client\" { \"debug\"; };\n"
				+ "category \"unmatched\" { \"debug\"; };\n"
				+ "category \"network\" { \"debug\"; };\n"
				+ "category \"update\" { \"debug\"; };\n"
				+ "category \"queries\" { \"debug\"; };\n"
				+ "category \"dispatch\" { \"debug\"; };\n"
				+ "category \"dnssec\" { \"debug\"; };\n"
				+ "category \"lame-servers\" { \"debug\"; };\n"
				+ "channel \"debug\" { " + "   file \"" + getLogFile()
				+ "\" versions 2 size 50m; " + "   print-time yes; "
				+ "   print-category yes; " + " };\n" + "};\n";

		config += "options { \n" + "\t directory \"" + getCacheDir() + "\"; \n"
				+ "\t auth-nxdomain no;   \n" + "\tpid-file \"" + getBaseDir()
				+ "/bind.pid\";\n" + " };\n";

		for (Entry<String, String> e : keys.entrySet()) {
			config += "\t key \"" + e.getKey() + "\" {\n"
					+ "\t\t algorithm HMAC-MD5;\n" + "\t secret \""
					+ e.getValue() + "\";\n" + "\t};\n";

		}

		for (String z : zones) {
			config += "zone \"" + z + "\" {\n" + "   type master;\n"
					+ "   file \"" + zoneFile(z) + "\";\n";

			if (keys.size() > 0) {
				config += "\tallow-update { \n ";
				for (Entry<String, String> e : keys.entrySet()) {
					config += "\t\tkey \"" + e.getKey() + "\";\n";
				}
				config += "\t};\n";

				config += "\tallow-transfer { \n ";
				for (Entry<String, String> e : keys.entrySet()) {
					config += "\t\tkey \"" + e.getKey() + "\";\n";
				}
				config += "\t};\n";

			}
			config += "};\n";

		}

		return config;
	}

	public List<String> getCommandLine() {

		return Arrays.asList(new String[] { getBindLocation(), "-f", "-p",
				"" + port, "-c", configFile() });
	}

	void mkdir_p(String dir) throws IOException {
		File f = new File(dir);
		f.mkdirs();
	}

	String configFile() {
		return getBaseDir() + "/named.conf";
	}

	void mkZoneFile(String zone, String zoneFile) throws IOException {

		PrintWriter br = new PrintWriter(new FileWriter(zoneFile));
		br.println("$ORIGIN .");
		br.println("$TTL 3600       ; 1 hour");
		br.println(zone + "             IN SOA  localhost. root.localhost. (");
		br.println("1         ; serial");
		br.println("604800     ; refresh (1 week)");
		br.println("86400      ; retry (1 day)");
		br.println("2419200    ; expire (4 weeks)");
		br.println("604800     ; minimum (1 week)");
		br.println(")");

		br.println(" NS      localhost.");
		br.close();
	}

	public void startBind() throws IOException {
		mkdir_p(getBaseDir());
		mkdir_p(getZoneDir());
		mkdir_p(getCacheDir());
		for (String zone : zones) {
			log.debug("creating zone file for zone " + zone + " in "
					+ zoneFile(zone));
			mkZoneFile(zone, zoneFile(zone));
		}
		String config = getConfig();
		//log.debug("intialising config \n" + config);

		FileWriter w = new FileWriter(configFile());
		w.write(config);
		w.close();
		List<String> cmdLine = getCommandLine();
		log.debug("Starting bind with command line "
				+ StringUtils.collectionToDelimitedString(cmdLine, " "));
		ProcessBuilder pb = new ProcessBuilder(getCommandLine());
		bindProc = pb.start();
		try {
			// give bind a bit of a chance to start (this is a hack)
			Thread.sleep(2000);
		} catch (InterruptedException e) {

		}

	}

	static public boolean deleteDirectory(File path) {
		if (path.exists()) {
			File[] files = path.listFiles();
			for (int i = 0; i < files.length; i++) {
				if (files[i].isDirectory()) {
					deleteDirectory(files[i]);
				} else {
					files[i].delete();
				}
			}
		}
		return (path.delete());
	}

	public void setDeleteOnCleanup(boolean val) {
		this.deleteOnCleanup = val;
	}

	public void cleanUp() throws IOException {
		if (null != bindProc) {
			log.debug("killing bind");
			bindProc.destroy();
			bindProc = null;
		}

		if (dumpLogOnCleanup) {
			BufferedReader in = new BufferedReader(new FileReader(getLogFile()));
			String line;
			while (null != (line = in.readLine())) {
				log.debug("DNS:" + line);
			}
		}

		if (deleteOnCleanup) {
			File basedir = new File(getBaseDir());
			log.debug("deleting working directory");
			deleteDirectory(basedir);
		}
	}

	public Map<String, String> getKeys() {
		return keys;
	}

	public void setKeys(Map<String, String> keys) {
		this.keys = keys;
	}

	public List<String> getZones() {
		return zones;
	}

	public void setZones(List<String> zones) {
		this.zones = zones;
	}

	public String getBindLocation() {
		return bindLocation;
	}

	public void setBindLocation(String bindLocation) {
		this.bindLocation = bindLocation;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public void addZone(String zone) {
		zones.add(zone);
	}

	public void addKey(String keyname, String keydata) {
		keys.put(keyname, keydata);
	}

	public boolean isDumpLogOnCleanup() {
		return dumpLogOnCleanup;
	}

	public void setDumpLogOnCleanup(boolean dumpLogOnCleanup) {
		this.dumpLogOnCleanup = dumpLogOnCleanup;
	}

	public boolean isDeleteOnCleanup() {
		return deleteOnCleanup;
	}
	@Override
	protected void finalize() throws Throwable {
		cleanUp();
	}
}
