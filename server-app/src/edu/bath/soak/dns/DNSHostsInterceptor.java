package edu.bath.soak.dns;

import java.net.Inet4Address;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.xbill.DNS.ReverseMap;

import edu.bath.soak.cmd.BaseCompositeCommand;
import edu.bath.soak.cmd.CmdException;
import edu.bath.soak.cmd.CommandExpander;
import edu.bath.soak.cmd.OrderedValidator;
import edu.bath.soak.cmd.UICommand;
import edu.bath.soak.dns.cmd.DNSCmd;
import edu.bath.soak.dns.cmd.DNSHostCommandFlags;
import edu.bath.soak.dns.cmd.DNSHostCommandFlags.DNSUpdateMode;
import edu.bath.soak.dns.model.DNSDao;
import edu.bath.soak.dns.model.DNSHostSettings;
import edu.bath.soak.dns.model.DNSRecord;
import edu.bath.soak.dns.model.DNSZone;
import edu.bath.soak.dns.model.ForwardZone;
import edu.bath.soak.dns.model.ReverseZone;
import edu.bath.soak.net.cmd.AlterHostCmd;
import edu.bath.soak.net.cmd.BulkCreateEditHostsCmd;
import edu.bath.soak.net.cmd.DeleteHostUICmd;
import edu.bath.soak.net.model.Host;
import edu.bath.soak.net.model.HostAlias;
import edu.bath.soak.net.model.NetDAO;
import edu.bath.soak.net.model.HostAlias.HostAliasType;
import edu.bath.soak.security.SecurityHelper;
import edu.bath.soak.util.TypeUtils;

/**
 * Command expander which elaborates host commands with the relevant DNS updates
 * 
 * This expander relies on the fact that the Host record in the command is
 * complete (i.e. that an IP address has already been appended)
 * 
 * @author cspocc
 * 
 */
public class DNSHostsInterceptor implements CommandExpander<UICommand>,
		OrderedValidator {

	Logger log = Logger.getLogger(DNSHostsInterceptor.class);
	DNSMgr dnsMgr;
	DNSDao dnsDAO;
	NetDAO hostsDAO;
	SecurityHelper securityHelper;

	/** Flag for extra DNS command flags appended to incoming commands */
	public static final String DNS_FLAGS_KEY = "dns.advancedFlags";

	/**
	 * Computes the forward records which should be required for this host.
	 * 
	 * @param host
	 * @param zone
	 * @return a list of DNSRecords which should be added for this host
	 */
	List<DNSRecord> forwardPrimaryRecord(Host host, ForwardZone zone) {
		Assert.notNull(host);
		Assert.notNull(zone);

		ArrayList<DNSRecord> recs = new ArrayList<DNSRecord>();
		if (zone.targetIsAllowed(host.getIpAddress().getHostAddress())) {
			// Add primary "A" Record;
			DNSRecord arec = new DNSRecord();
			arec.setType("A");
			arec.setHostName(host.getHostName().toString());
			arec.setTtl(getDefaultRecordTTL(host, zone));
			arec.setTarget(host.getIpAddress().getHostAddress());
			arec.setZone(zone);

			recs.add(arec);
		}

		return recs;
	}

	public boolean isHostRelatedRecord(DNSRecord rec) {
		return rec.getType().equals("A") || rec.getType().equals("PTR")
				|| rec.getType().equals("CNAME");
	}

	public List<DNSRecord> calculateUnusedDNSRecordsForZones(
			Collection<DNSZone> zones) {

		List<DNSRecord> unusedRecords = new ArrayList<DNSRecord>();

		for (DNSZone zone : zones) {
			for (DNSRecord rec : dnsDAO.getAllRecordsForZone(zone)) {
				if (zone instanceof ForwardZone) {

				}

				try {
					log.trace("Examining record: " + rec);
					if (isHostRelatedRecord(rec)
							&& StringUtils.hasText(rec.getHostName())
							&& !hostExistsForRecord(rec)) {
						log.trace("record appears to be unused: " + rec);

						unusedRecords.add(rec);
					}
				} catch (IllegalArgumentException e) {
					log.trace("skiped unparseable record" + rec);

				}
			}

		}

		return unusedRecords;
	}

	/**
	 * returns true if a given host exists for a given record.
	 * 
	 * 
	 * @param rec
	 * @return
	 */
	public boolean hostExistsForRecord(DNSRecord rec) {
		if (rec.getType().equals("PTR")) {
			Inet4Address ip = TypeUtils.ptrNameToIP(rec.getHostName());
			Host h = hostsDAO.findHost(ip);
			if (h != null && h.getHostName().toString().equals(rec.getTarget())) {
				return true;
			}
		} else if (rec.getType().equals("CNAME")) {
			List<HostAlias> has = hostsDAO.findAliases(hostsDAO
					.getHostNameFromFQDN(rec.getHostName()));
			for (HostAlias alias : has) {
				if (alias.getHost().getHostName().toString().equals(
						rec.getTarget())) {
					return true;
				}
			}
		} else if (rec.getType().equals("A")) {
			Host h = hostsDAO.findHost(rec.getHostName());
			if (h != null) {
				if (h.getIpAddress().getHostAddress().equals(rec.getTarget())) {
					return true;
				}
			}

			List<HostAlias> has = hostsDAO.findAliases(hostsDAO
					.getHostNameFromFQDN(rec.getHostName()));
			for (HostAlias alias : has) {
				if (alias.getHost().getIpAddress().getHostAddress().equals(
						rec.getTarget())) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * returns the default TTL for a given host (with settings applied) in a
	 * given zone
	 * 
	 * @param h
	 * @param zone
	 * @return
	 */
	public Long getDefaultRecordTTL(Host h, DNSZone zone) {
		DNSHostSettings settings = (DNSHostSettings) h
				.getConfigSetting(DNS_FLAGS_KEY);
		if (settings != null && null != settings.getHostTTL()) {
			return settings.getHostTTL();
		}
		if (h.getHostClass().getDnsTTL() != null) {
			return h.getHostClass().getDnsTTL();
		}
		return dnsMgr.getDefaultTTL(zone);
	}

	/**
	 * Computes alias record(s) for a host with a given alias on a given zone
	 * 
	 * @param host
	 * @param alias
	 * @param zone
	 * @return
	 */

	List<DNSRecord> forwardAliasRecord(Host host, HostAlias alias,
			ForwardZone zone) {
		Assert.notNull(alias);
		Assert.notNull(zone);
		Assert.notNull(host);
		ArrayList<DNSRecord> recs = new ArrayList<DNSRecord>();

		// Add primary "A" Record;
		DNSRecord a = new DNSRecord();
		a.setHostName(alias.getAlias().toString());
		a.setTtl(getDefaultRecordTTL(host, zone));

		if (alias.getType().equals(HostAliasType.AREC)) {
			a.setType("A");
			a.setTarget(host.getIpAddress().getHostAddress());
		} else {
			a.setType("CNAME");
			a.setTarget(host.getHostName().toString());
		}

		a.setZone(zone);

		recs.add(a);

		return recs;
	}

	/**
	 * Returns all records which exist, which are "associated" with this host,
	 * this includes all "endorsed" records (see
	 * {@link #filterRecordsToExisting(Host)}) and records which cannot
	 * legitimately relate to any other host but are associated with this host
	 * 
	 * Specifically this includes Duplicate A records which do not point to the
	 * same IP as the primary name of this host Duplicate PTR records which do
	 * not point to the same host name as this host
	 * 
	 * Note that this does not match clashes for host aliases
	 * 
	 * @param host
	 * @return a list of records.
	 */

	public Set<DNSRecord> getExtraExistingAssociatedRecords(Host host) {
		Assert.notNull(host);
		Assert.notNull(host.getIpAddress());
		Set<DNSRecord> associated = new HashSet<DNSRecord>();

		associated.addAll(dnsDAO.findRecordsInZones(dnsDAO.getForwardZones(),
				host.getHostName().getFQDN(), "A"));

		associated.addAll(dnsDAO.findRecordsInZones(dnsDAO.getReverseZones(),
				ReverseMap.fromAddress(host.getIpAddress()).toString(), "PTR"));

		return associated;
	}

	/**
	 * 
	 * Filters a set of records to only those which actually exist
	 * 
	 * @param inputRecords
	 *            the set of records to filter
	 * @return a list of records.
	 */

	public Set<DNSRecord> filterRecordsToExisting(
			Collection<DNSRecord> inputRecords) {
		// Set<DNSRecord> required = getRequiredRecordsForHost(host);
		Set<DNSRecord> endorsed = new HashSet<DNSRecord>();

		endorsed.addAll(dnsDAO.findMatchingRecords(inputRecords));
		return endorsed;
	}

	/**
	 * Returns all records which exist, which represent semantic clashes with
	 * this given record.
	 * 
	 * This includes:
	 * 
	 * A record with both an A name and a CNAME
	 * 
	 * A PTR record with multiple values.
	 * 
	 * @param rec
	 *            the record to search for clashes for
	 * @return a set of records.
	 */
	public Set<DNSRecord> getClashingRecord(DNSRecord rec) {
		Assert.notNull(rec);
		Set<DNSRecord> recs = new HashSet<DNSRecord>();
		if (rec.getType().equals("A")) {
			recs.addAll(dnsDAO.findRecords(rec.getZone(), rec.getHostName(),
					"CNAME"));

		}
		if (rec.getType().equals("CNAME")) {
			recs.addAll(dnsDAO.findRecords(rec.getZone(), rec.getHostName(),
					"A"));

		}

		if (rec.getType().equals("PTR")) {
			for (ReverseZone z : dnsDAO.getReverseZones()) {
				recs.addAll(dnsDAO.findRecords(z, rec.getHostName(), "PTR"));
			}
		}
		return recs;
	}

	/**
	 * Computes the reverse record for this host, based on the given reverse
	 * zone
	 * 
	 * @param host
	 * @param reverse
	 * @return
	 */
	DNSRecord reverseRecord(Host host, ReverseZone reverse) {
		Assert.notNull(host);
		Assert.notNull(host.getIpAddress());
		Assert.notNull(host.getHostName());
		Assert.notNull(reverse);
		DNSRecord rec = new DNSRecord();
		rec.setType("PTR");
		rec.setHostName(ReverseMap.fromAddress(host.getIpAddress()).toString());
		rec.setTarget(host.getHostName().toString());
		rec.setTtl(getDefaultRecordTTL(host, reverse));
		rec.setZone(reverse);
		return rec;
	}

	/**
	 * Returns all of the records which a host record should have based on
	 * current policy and the host information
	 * 
	 * @param host
	 * @return
	 */
	public Set<DNSRecord> getRequiredRecordsForHost(Host host) {
		Assert.notNull(host);
		Assert.notNull(host.getIpAddress());
		Assert.notNull(host.getHostName());
		log.trace("Getting required records for " + host);
		Set<DNSRecord> requiredRecords = new HashSet<DNSRecord>();
		for (ForwardZone zone : dnsDAO.getForwardZones()) {
			log.debug("examining forward zone " + zone);
			if (zone.forwardMatches(host.getHostName().toString())) {
				log.debug(host + " matched zone " + zone + " for primary");
				requiredRecords.addAll(forwardPrimaryRecord(host, zone));
			}
			for (HostAlias alias : host.getHostAliases()) {
				if (zone.forwardMatches(alias.getAlias().toString())) {
					log.debug(host + " matched zone " + zone + " for alias");
					requiredRecords
							.addAll(forwardAliasRecord(host, alias, zone));
				}
			}
		}

		for (ReverseZone zone : dnsDAO.getReverseZones()) {

			if (zone.reverseMatches(host.getIpAddress())) {
				requiredRecords.add(reverseRecord(host, zone));
			}
		}
		return requiredRecords;
	}

	/**
	 * Returns a DNS Host Command flags object associated with this command
	 * object
	 * 
	 * If no command flags are set then returns a default (immutable) set of
	 * flags
	 * 
	 * @param chc
	 * @return a {@link DNSHostCommandFlags} object or null
	 */
	DNSHostCommandFlags getActiveCommandFlags(AlterHostCmd chc) {
		DNSHostCommandFlags flags = (DNSHostCommandFlags) chc.getOptionData()
				.get(DNS_FLAGS_KEY);

		if (null == flags) {
			return new DNSHostCommandFlags();
		} else {
			return flags;
		}

	}

	/**
	 * Predicate which determines when DNS needs to be edited
	 * 
	 * We only edit DNS if A) the hosts IP, name or aliases have changed B) if
	 * the user has requested a full DNS refresh C) if the user has explicitly
	 * changed the default TTL for this host
	 * 
	 * @param existing
	 * @param flags
	 * @param cmd
	 * @return
	 */
	public boolean doEditDNS(Host existing, DNSHostCommandFlags flags,
			AlterHostCmd cmd) {
		Host host = cmd.getNewHost();
		return !TypeUtils.nullSafeCompare(existing.getHostClass(), host
				.getHostClass())
				|| !TypeUtils.nullSafeCompare(existing.getHostName(), host
						.getHostName())
				|| !TypeUtils.nullSafeCompare(existing.getIpAddress(), host
						.getIpAddress())
				|| !existing.getHostAliases().equals(host.getHostAliases())
				|| flags.getUpdateMode().equals(
						DNSUpdateMode.DNS_REFRESH_ALL_DATA)
				|| flags.getHostTTL() != null
				&& (host.getConfigSetting(DNS_FLAGS_KEY) == null || !flags
						.getHostTTL().equals(
								((DNSHostSettings) host
										.getConfigSetting(DNS_FLAGS_KEY))
										.getHostTTL()));
	}

	DNSCmd getBaseDNSChangeForAlterHostCmd(AlterHostCmd chc) {
		Set<DNSRecord> requiredRecords;
		boolean refreshRecords = getActiveCommandFlags(chc).getUpdateMode()
				.equals(DNSUpdateMode.DNS_REFRESH_ALL_DATA);
		requiredRecords = getRequiredRecordsForHost(chc.getNewHost());
		log.trace(chc.getNewHost() + " needs " + requiredRecords.size()
				+ " DNS records ");

		DNSCmd dnscmd = new DNSCmd();
		Set<DNSRecord> existingEndorsedRecords = null;

		if (!chc.isCreation()) {
			Host existingHost = hostsDAO.loadHost(chc.getNewHost().getId());
			// accumulate all records which exist from the previous edit and all
			// records which
			// exist for the new edit
			existingEndorsedRecords = filterRecordsToExisting(getRequiredRecordsForHost(existingHost));
			existingEndorsedRecords
					.addAll(filterRecordsToExisting(requiredRecords));
		} else {
			existingEndorsedRecords = filterRecordsToExisting(requiredRecords);
		}

		log.trace(chc.getNewHost() + "  has " + existingEndorsedRecords.size()
				+ " DNS records ");

		if (refreshRecords) {
			for (DNSRecord r : requiredRecords) {
				dnscmd.insertAdd(r);
			}

			// we delete everything including associated referenced records
			existingEndorsedRecords
					.addAll(getExtraExistingAssociatedRecords(chc.getNewHost()));

			for (DNSRecord r : existingEndorsedRecords) {
				dnscmd.insertDelete(r);
			}
		} else {
			// add all new records

			for (DNSRecord r : requiredRecords) {
				if (!existingEndorsedRecords.contains(r)) {
					dnscmd.insertAdd(r);
				}
			}
			for (DNSRecord r : existingEndorsedRecords) {
				if (!requiredRecords.contains(r)) {
					dnscmd.insertDelete(r);
				}
			}
		}

		return dnscmd;
	}

	public static class DNSClashException extends CmdException {
		AlterHostCmd hostCommand;
		Set<DNSRecord> records;

		public DNSClashException(AlterHostCmd cmd, Set<DNSRecord> records) {
			this.hostCommand = cmd;
			this.records = records;
		}

		public AlterHostCmd getHostCommand() {
			return hostCommand;
		}

		public void setHostCommand(AlterHostCmd hostCommand) {
			this.hostCommand = hostCommand;
		}

		public Set<DNSRecord> getRecords() {
			return records;
		}

		public void setRecords(Set<DNSRecord> records) {
			this.records = records;
		}

	}

	/**
	 * Adds a DNS command which inserts the required records for the given host.
	 * 
	 * This will delete any previous records which belong to this host, and add
	 * any new ones.
	 * 
	 * If any new records clash with existing records which don't "belong" to
	 * this host then an error is raised, this can be overridden with an
	 * advanced flag in which case these records will be deleted.
	 * 
	 * @param alterHost
	 * @param result
	 */
	void expandAlterHostCommand(AlterHostCmd alterHost,
			BaseCompositeCommand result) {
		// log.trace("expanding command " + alterHost);
		DNSHostCommandFlags flags = getActiveCommandFlags(alterHost);
		Assert.notNull(flags);

		DNSHostSettings dnsSettings = (DNSHostSettings) alterHost.getNewHost()
				.getConfigSetting(DNS_FLAGS_KEY);

		if (null == dnsSettings) {
			dnsSettings = new DNSHostSettings();
		}

		if (flags.getUpdateMode() == DNSUpdateMode.NEVER_DNS_EDITS) {
			dnsSettings.setNeverUpdateDNS(true);
		} else {
			dnsSettings.setNeverUpdateDNS(false);
		}
		dnsSettings.setHostTTL(flags.getHostTTL());
		alterHost.getNewHost().setConfigSetting(DNS_FLAGS_KEY, dnsSettings);
		if (flags.getUpdateMode() == DNSUpdateMode.NEVER_DNS_EDITS
				|| flags.getUpdateMode() == DNSUpdateMode.NO_DNS_EDITS) {
			log.trace("user over-rode DNS update settings");
			return;
		}

		if (!alterHost.isCreation()) {
			Host existingHost = hostsDAO.loadHost(alterHost.getNewHost()
					.getId());
			if (!doEditDNS(existingHost, flags, alterHost)) {
				log.trace("No DNS changes needed");
				return;
			}

		}

		DNSCmd dnscmd = getBaseDNSChangeForAlterHostCmd(alterHost);

		Set<DNSRecord> clashes = new HashSet<DNSRecord>();
		for (DNSRecord addRec : getRequiredRecordsForHost(alterHost
				.getNewHost())) {
			clashes.addAll(getClashingRecord(addRec));
		}
		List<DNSRecord> deletions = dnscmd.getDeletions();
		// we ignore any clashing records which we are already deleting
		clashes.removeAll(deletions);
		clashes.removeAll(getRequiredRecordsForHost(alterHost.getNewHost()));

		if (!clashes.isEmpty()) {
			if (flags.isForceDNSUpdates()) {
				for (DNSRecord clash : clashes)
					dnscmd.insertDelete(clash);
			} else {
				throw new DNSClashException(alterHost, clashes);
			}
		}

		if (dnscmd.getChanges().size() > 0)
			result.getCommands().add(dnscmd);
	}

	/**
	 * Expands changes for a host deletion
	 * 
	 * Simply gets all existing records for a host, and adds deletion changes
	 * for them
	 * 
	 * @param dhc
	 * @param result
	 */
	void expandDeleteHostCommand(DeleteHostUICmd dhc,
			BaseCompositeCommand result) {
		Assert.notNull(dhc);
		Assert.notNull(result);
		Set<DNSRecord> existingRecords = filterRecordsToExisting(getRequiredRecordsForHost(dhc
				.getHost()));
		DNSCmd dnsCmd = new DNSCmd();

		for (DNSRecord rec : existingRecords) {
			dnsCmd.insertDelete(rec);
		}
		if (dnsCmd.getChanges().size() > 0) {
			result.getCommands().add(dnsCmd);
		}
	}

	/**
	 * Expands a host command by adding {@link DNSCmd} objects to the result
	 * command
	 * 
	 * For {@link AlterHostCmd} objects it adds or updates DNS records where
	 * appropriate for {@link DeleteHostUICmd} objects it deletes any existing
	 * DNS records
	 * 
	 * @param cmd
	 *            the command to expand,
	 * @param result
	 *            the resuling composite command into which DNS updates will be
	 *            placed
	 */
	public void expandCmd(UICommand cmd, BaseCompositeCommand result) {
		log.trace("expanding command " + cmd);
		if (cmd instanceof DeleteHostUICmd) {
			expandDeleteHostCommand((DeleteHostUICmd) cmd, result);
		} else if (cmd instanceof AlterHostCmd) {
			expandAlterHostCommand((AlterHostCmd) cmd, result);
		} else {
			// unless we handle this type of change we do nothing
			return;
		}

	}

	/***************************************************************************
	 * adds {@link DNSHostCommandFlags} flags to alter host commands and Bulk
	 * create/edit host commands
	 * 
	 * @param cmd
	 *            the command to set up
	 */

	public void setupCommand(UICommand cmd) {
		DNSHostCommandFlags flags = new DNSHostCommandFlags();
		if (cmd instanceof AlterHostCmd && securityHelper.isAdmin()) {
			AlterHostCmd ahcmd = (AlterHostCmd) cmd;
			DNSHostSettings settings = (DNSHostSettings) ahcmd.getNewHost()
					.getConfigSetting(DNS_FLAGS_KEY);
			if (settings != null) {
				if (null != settings.getNeverUpdateDNS()
						&& settings.getNeverUpdateDNS()) {
					flags.setUpdateMode(DNSUpdateMode.NEVER_DNS_EDITS);
				}
				if (null != settings.getHostTTL()) {
					flags.setHostTTL(settings.getHostTTL());
				}
			}
			ahcmd.getOptionData().put(DNS_FLAGS_KEY, flags);
		} else if (cmd instanceof BulkCreateEditHostsCmd
				&& securityHelper.isAdmin()) {
			cmd.getOptionData().put(DNS_FLAGS_KEY, flags);
		}

	}

	@Required
	public void setDnsMgr(DNSMgr dnsMgr) {
		this.dnsMgr = dnsMgr;
	}

	@Required
	public void setDnsDAO(DNSDao dnsDAO) {
		this.dnsDAO = dnsDAO;
	}

	@Required
	public void setHostsDAO(NetDAO hostsDAO) {
		this.hostsDAO = hostsDAO;
	}

	public boolean supports(Class clazz) {
		return canExpand(clazz);
	}

	public boolean canExpand(Class clazz) {
		return AlterHostCmd.class.isAssignableFrom(clazz)
				|| DeleteHostUICmd.class.isAssignableFrom(clazz)
				|| BulkCreateEditHostsCmd.class.isAssignableFrom(clazz);

	}

	void validateAlterHostCmd(AlterHostCmd cmd, Errors errors) {
		try {
			expandAlterHostCommand(cmd, new BaseCompositeCommand(cmd));
		} catch (DNSClashException clashE) {
			for (DNSRecord r : clashE.getRecords()) {

				errors.reject("dns-clash", "DNS reservation for <b>"
						+ r.getHostName()
						+ "</b> clashes with  (possibly unmanaged) "
						+ "record " + r + " in the zone: <b>"
						+ r.getZone().getDisplayName() + "</b>");

			}
		}
	}

	public void validate(Object arg, Errors errors) {
		UICommand cmd = (UICommand) arg;
		if (cmd instanceof AlterHostCmd) {
			validateAlterHostCmd((AlterHostCmd) cmd, errors);
		}

	}

	public int getOrder() {
		return 0;
	}

	@Required
	public void setSecurityHelper(SecurityHelper securityHelper) {
		this.securityHelper = securityHelper;
	}
}
