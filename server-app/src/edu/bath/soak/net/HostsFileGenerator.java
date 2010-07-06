package edu.bath.soak.net;

import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.StringUtils;

import edu.bath.soak.net.model.Host;
import edu.bath.soak.net.model.HostAlias;
import edu.bath.soak.net.model.NetDAO;
import edu.bath.soak.net.model.Subnet;
import edu.bath.soak.net.model.Vlan;
import edu.bath.soak.net.query.HostSearchQuery;
import edu.bath.soak.net.query.SubnetQuery;
import edu.bath.soak.query.SearchResult;
import edu.bath.soak.util.TypeUtils;

/*******************************************************************************
 * Outputs the contents of the hosts file to to a printwriter.
 * 
 * @author cspocc
 * 
 */
public class HostsFileGenerator {
	NetDAO hostsDAO;
	String trimHostNameSuffix = null;
	int maxComment = 0;
	String preamble = null;
	int defaultTabDist = 30;
	long liuThreshold = 3600L * 24L * 180L;

	String mapHostName(String hostName) {
		if (trimHostNameSuffix != null && hostName.endsWith(trimHostNameSuffix)) {
			hostName = hostName.substring(0, hostName.length()
					- trimHostNameSuffix.length());
		}
		return hostName;

	}

	String mapComment(String comment) {
		if (maxComment > 0 && comment.length() > maxComment) {
			comment = comment.substring(0, maxComment) + "...";
		}
		return comment.replaceAll("[\\r\\n\\t]", " ");
	}

	/**
	 * Writes a single line to the output pw
	 * 
	 * @param subnet
	 *            the current subnet
	 * @param host
	 *            the host to write
	 * @param pw
	 *            the print writer to output to
	 */
	void writeHostLineEntry(Subnet subnet, Host host, PrintWriter pw) {
		String ip = host.getIpAddress().getHostAddress();
		String hostName = mapHostName(host.getHostName().getFQDN());
		for (HostAlias ha : host.getHostAliases()) {
			hostName += " " + mapHostName(ha.getAlias().getFQDN());
		}
		int len = hostName.length();
		for (int i = 0; i < defaultTabDist - len; i++) {
			hostName += " ";
		}
		String comment = "";
		comment += host.getOwnership().getOrgUnit().getId() + " ";
		comment += host.getHostClass().getId() + " ";

		comment += host.getLocation().toString() + " ";
		if (null != host.getMacAddress()) {
			comment += host.getMacAddress().toString() + " ";
		}

		Date thresh = new Date(System.currentTimeMillis()
				- (liuThreshold * 1000L));
		if (null != host.getLastUsageInfo()
				&& host.getLastUsageInfo().getChangedAt().before(thresh)) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

			comment += "LIU:"
					+ sdf.format(host.getLastUsageInfo().getChangedAt()) + " ";
		}

		if (host.getDescription() != null) {
			comment = comment + mapComment(host.getDescription());
		}

		pw.println(ip + "\t" + hostName + " # " + comment);
	}

	/**
	 * Simple internal wordwrap function 
	 * @param val
	 * @param maxwidth
	 * @return
	 */
	List<String> wordwrap(String val, int maxwidth) {
		Pattern wrapRE = Pattern.compile("(\\S\\S{" + maxwidth + ",}|.{1,"
				+ maxwidth + "})(\\s+|$)");

		List<String> list = new LinkedList<String>();

		Matcher m = wrapRE.matcher(val);

		while (m.find())
			list.add(m.group());

		return list;
	}

	/**
	 * Writes an entry for a subnet to the output
	 * @param s the subnet to write
	 * @param pw the printwriter to output  to 
	 */
	void writeSubnetEntry(Subnet s, PrintWriter pw) {
		pw.println("#");
		pw.println("###");
		pw.println("# Subnet  : " + s.getName() + "  "
				+ s.getMinIP().getHostAddress() + "/" + s.getMaskBits());
		pw.println("# Range   : " + s.getMinIP().getHostAddress() + " - "
				+ s.getMaxIP().getHostAddress());
		pw.println("# Netmask : " + s.getSubnetMask().getHostAddress());

		if (null != s.getGateway()) {
			String gw = s.getGateway().getHostAddress();
			Host gwHost = null;
			if (null != (gwHost = hostsDAO.findHost(s.getGateway()))) {
				gw += " (" + mapHostName(gwHost.getHostName().getFQDN()) + " )";
			}
			pw.println("# Gateway : " + gw);

		}

		if (s.getVlan() != null) {
			Vlan v = s.getVlan();
			pw.println("# Vlan: " + v.getName() + " (" + v.getNumber() + ")");
		}
		if (StringUtils.hasText(s.getDescription())) {
			pw.println("# Description ");
			pw.println("# ----------- ");
			List<String> descriptionLines = wordwrap(s.getDescription(), 79);
			for (String l :descriptionLines ) {
				
				pw.println("# " + l.trim());
			}
		}
		pw.println("###");

	}

	public void generateHostsFile(PrintWriter pw) {
		SubnetQuery sq = new SubnetQuery();
		sq.setOrderBy("minIP");
		sq.setAscending(true);
		SearchResult<Subnet> result = hostsDAO.searchSubnets(sq);
		if (preamble != null) {
			pw.println(preamble);
		}
		pw.println("# Generated automatically on " + (new Date()).toString()
				+ " by the hosts manager");
		for (Subnet s : result.getResults()) {
			writeSubnetEntry(s, pw);

			HostSearchQuery hsc = new HostSearchQuery();
			hsc.setSubnet(s);
			hsc.setOrderBy("ipAddress");
			hsc.setAscending(true);
			List<Host> hresult = hostsDAO.getAllHostsInRange(s);
			Host prevHost = null;
			for (Host h : hresult) {
				if (prevHost != null
						&& !prevHost.getIpAddress().equals(
								TypeUtils.ipDecrement(h.getIpAddress()))) {
					pw.println("#");// we print a blank line if there is an IP
					// gap
				}
				writeHostLineEntry(s, h, pw);
				prevHost = h;
				pw.flush();
			}
			pw.println("#  ");
		}
	}

	@Required
	public void setHostsDAO(NetDAO hostsDAO) {
		this.hostsDAO = hostsDAO;
	}

	public void setPreamble(String preamble) {
		this.preamble = preamble;
	}

	public void setTrimHostNameSuffix(String trimHostNameSuffix) {
		this.trimHostNameSuffix = trimHostNameSuffix;
	}

	public void setMaxComment(int maxComment) {
		this.maxComment = maxComment;
	}

	/**
	 * The amount of time before a LIU Date is added to a host defaults to 90
	 * days
	 * 
	 * @param liuThreshold
	 */
	public void setLiuThreshold(int liuThreshold) {
		this.liuThreshold = liuThreshold;
	}
}
