package edu.bath.soak.dns;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.xbill.DNS.Name;
import org.xbill.DNS.Record;
import org.xbill.DNS.SOARecord;
import org.xbill.DNS.TSIG;
import org.xbill.DNS.TextParseException;
import org.xbill.DNS.Type;
import org.xbill.DNS.Update;
import org.xbill.DNS.ZoneTransferIn;

import edu.bath.soak.EventLog;
import edu.bath.soak.cmd.BaseCompositeCommand;
import edu.bath.soak.cmd.CmdException;
import edu.bath.soak.cmd.CmdValidationException;
import edu.bath.soak.dns.cmd.DNSChange;
import edu.bath.soak.dns.cmd.DNSCmd;
import edu.bath.soak.dns.cmd.DNSCmdValidator;
import edu.bath.soak.dns.model.DNSDao;
import edu.bath.soak.dns.model.DNSRecord;
import edu.bath.soak.dns.model.DNSZone;
import edu.bath.soak.undo.UndoNotSupportedException;

/**
 * DNS Manager implementation
 * 
 * Implements DNS Commands via a {@link DNSService } object
 * 
 * Also implemnets periodic DNS updates on given zones.
 * 
 * 
 * @author cspocc
 * 
 */
public class DNSMgrImpl implements DNSMgr, DNSUpdateMgr,
		ApplicationContextAware {

	long defaultTTL = 3600;
	DNSDao dnsDAO;
	Logger log = Logger.getLogger(DNSMgrImpl.class);
	DNSService dnsService;
	DNSCmdValidator dnsCmdValidator;
	ApplicationContext applicationContext;

	/**
	 * Implements a given {@link DNSCmd}, groups updates by zone, and then
	 * transmits a DDNS udpate for each zone which is changed (via the
	 * {@link DNSService} object linked to this manager).
	 * 
	 * Following successful updates, updates the appropriate records in the DNS
	 * cache
	 * 
	 * @param baseCommand
	 *            the parent comand of the command to expand
	 * @param cmd
	 *            the actual DNS command to implement
	 * @throws CmdException
	 */
	public void implementCmd(BaseCompositeCommand baseCommand, DNSCmd cmd)
			throws CmdException {
		Assert.notNull(cmd);

		Errors objectErrors = new BeanPropertyBindingResult(cmd, "cmd");
		ValidationUtils.invokeValidator(dnsCmdValidator, cmd, objectErrors);
		if (objectErrors.hasErrors())
			throw new CmdValidationException(objectErrors);

		log.trace("Implementing DNS command " + cmd);
		// Gather updates by zone
		try {
			// First dispatch zone deletions and execute them
			// deletions must be implemented first because of possible
			// constraint violations. Also if bind has an add and a delete in
			// the same update for the same record, the delete takes precedence
			Map<DNSZone, Update> deleteMap = new HashMap<DNSZone, Update>();
			List<DNSRecord> todelete = new ArrayList<DNSRecord>();

			for (DNSRecord change : cmd.getDeletions()) {
				DNSZone zone = change.getZone();
				Assert.notNull(zone);
				Assert.notNull(zone.getDomain());
				Update zchanges = null;
				if (null == (zchanges = deleteMap.get(zone)))
					deleteMap.put(zone, zchanges = new Update(Name
							.fromString(zone.getDomain())));

				Record chrec = change.getRecord();
				zchanges.delete(chrec);
				todelete.add(change);
			}

			for (Entry<DNSZone, Update> updateEntry : deleteMap.entrySet()) {
				try {

					dnsService.sendUpdate(updateEntry.getKey(), updateEntry
							.getValue());
				} catch (DNSService.DNSServiceException e) {
					throw new CmdException("DNS update failed on zone "
							+ updateEntry.getKey().getDomain(), e);
				}
			}
			for (DNSRecord del : todelete) {
				dnsDAO.deleteRecord(del.getId());
			}

			// now dispatch additions
			List<DNSRecord> toadd = new ArrayList<DNSRecord>();
			Map<DNSZone, Update> addMap = new HashMap<DNSZone, Update>();

			for (DNSRecord change : cmd.getAdditions()) {
				DNSZone zone = change.getZone();
				Assert.notNull(zone);
				Assert.notNull(zone.getDomain());
				Update zchanges = null;
				if (null == (zchanges = addMap.get(zone)))
					addMap.put(zone, zchanges = new Update(Name.fromString(zone
							.getDomain())));

				Record chrec = change.getRecord();
				zchanges.add(chrec);
				toadd.add(change);
			}

			for (Entry<DNSZone, Update> updateEntry : addMap.entrySet()) {
				try {
					dnsService.sendUpdate(updateEntry.getKey(), updateEntry
							.getValue());
				} catch (DNSService.DNSServiceException e) {
					throw new CmdException("DNS update failed on zone "
							+ updateEntry.getKey().getDomain(), e);
				}
			}
			for (DNSRecord add : toadd) {
				dnsDAO.addRecord(add);
			}
		} catch (TextParseException e) {
			throw new RuntimeException(e);
		}

		return;
	}

	public void expandUndo(DNSCmd cmd, BaseCompositeCommand result)
			throws CmdException {
		DNSCmd newDNSCmd = new DNSCmd();
		for (DNSChange change : cmd.getChanges()) {
			DNSRecord rec = change.getRecord();

			if (change.isAddition()) {
				DNSRecord toDelete = dnsDAO.getRecord(rec.getId());
				if (toDelete == null)
					throw new UndoNotSupportedException(cmd,
							"Can't delete record " + rec
									+ "  as it no longer exists");
				newDNSCmd.insertDelete(toDelete);
			} else {
				rec.setId(null);
				newDNSCmd.insertAdd(rec);
			}
		}
		Errors objectErrors = new BeanPropertyBindingResult(newDNSCmd, "cmd");
		ValidationUtils.invokeValidator(dnsCmdValidator, newDNSCmd,
				objectErrors);
		if (objectErrors.hasErrors())
			throw new UndoNotSupportedException(cmd, "Can't undo DNS command");
		result.appendCommand(newDNSCmd);
	}

	/**
	 * Implements an updated for an AXFR response on a zone.
	 * 
	 * The serial and all records for the zone will be updated.
	 * 
	 * @param zone
	 *            the zone to update
	 * @param records
	 *            the set of records to merge with the zone
	 */
	ZoneUpdateInfo performAXFRUpdate(DNSZone zone, List<Record> records) {
		Assert.notNull(zone);
		Assert.notNull(records);
		ZoneUpdateInfo upd = new ZoneUpdateInfo();
		log.debug("starting AXFR-type updated on zone " + zone.getDisplayName()
				+ " with " + records.size() + " records");
		long oldSerial = -1;
		if (zone.getSerial() != null) {
			oldSerial = zone.getSerial();
		}
		upd.setZone(zone);
		upd.setOldSerial(oldSerial);

		Set<DNSRecord> oldrecs = new HashSet<DNSRecord>(dnsDAO
				.getAllRecordsForZone(zone));
		Set<DNSRecord> newrecs = new HashSet<DNSRecord>();
		Set<DNSRecord> seenrecs = new HashSet<DNSRecord>();
		long newSerial = 0;
		nextrec: for (Record drec : (List<Record>) records) {
			log.trace("Got record " + drec.toString());
			if (drec instanceof SOARecord) {
				SOARecord soa = (SOARecord) drec;
				newSerial = soa.getSerial();
			}
			DNSRecord dbrec = DNSRecord.fromDNSRecord(zone, drec);

			if (!seenrecs.contains(dbrec)) {
				if (!oldrecs.contains(dbrec)) {
					if (newrecs.contains(dbrec)) {
						log.trace("found duplicate record " + dbrec);
						continue nextrec;
					}

					newrecs.add(dbrec);

				} else {
					oldrecs.remove(dbrec);
				}
			}

			seenrecs.add(dbrec);
		}
		// the new SOA must be set.
		Assert.isTrue(newSerial != 0);
		zone.setSerial(newSerial);
		upd.setNewSerial(newSerial);
		upd.setNumAdded(newrecs.size());
		upd.setNumDeleted(oldrecs.size());
		upd.setIncremental(false);
		// Old recs should now contain deletable records, new recs should
		// contain addable records.
		for (DNSRecord r : oldrecs) {
			log.debug("Deleting " + r.toString());

			dnsDAO.deleteRecord(r.getId());
		}
		for (DNSRecord r : newrecs) {
			log.debug("Inserting " + r.toString());

			r.setLastUpdateSerial(newSerial);
			dnsDAO.saveRecord(r);
		}
		zone.setLastUpdate(new Date());
		dnsDAO.saveZone(zone);
		log.debug("AXFR-type updated on zone " + zone.getDisplayName()
				+ " Completed  with to serial " + zone.getSerial() + " with "
				+ upd.getNumAdded() + " added " + upd.getNumDeleted()
				+ " deleted ");
		return upd;
	}

	/**
	 * Implements an IXFR (dynamic) update for a zone, the zone serial and all
	 * records will be updated and saved
	 * 
	 * @param zone
	 *            the zone to update
	 * @param deltas
	 */
	ZoneUpdateInfo performIXFRUpdate(DNSZone zone,
			List<ZoneTransferIn.Delta> deltas) {
		Assert.notNull(zone);
		Assert.notNull(deltas);
		log.debug("starting IXFR-type updated on zone " + zone.getDisplayName()
				+ " with " + deltas.size() + " deltas");
		ZoneUpdateInfo upd = new ZoneUpdateInfo();
		long oldSerial = -1;
		if (zone.getSerial() != null) {
			oldSerial = zone.getSerial();
		}
		upd.setZone(zone);
		upd.setOldSerial(oldSerial);

		long newserial = 0;
		int numadditions = 0;
		int numdeletions = 0;
		int numupdates = 0;

		log.debug("Performing IXFR update on zone " + zone + " with "
				+ deltas.size() + " deltas");
		for (ZoneTransferIn.Delta delta : deltas) {
			long endserial = delta.end;
			List<Record> delrecs = delta.deletes;
			for (Record dr : delrecs) {
				DNSRecord drec = dnsDAO.findRecord(zone, dr);
				if (drec != null) {
					dnsDAO.deleteRecord(drec.getId());
					numdeletions++;
				}
			}
			List<Record> addrecs = delta.adds;

			for (Record ar : addrecs) {
				DNSRecord newrec = dnsDAO.findRecord(zone, ar);
				numadditions++;
				if (null != newrec) {
					newrec.setLastUpdateSerial(endserial);
					dnsDAO.saveRecord(newrec);
					numupdates++;
				} else {
					newrec = new DNSRecord();
					newrec.setHostName(ar.getName().toString());
					newrec.setLastUpdateSerial(endserial);
					newrec.setTarget(ar.rdataToString());
					newrec.setTtl(ar.getTTL());
					newrec.setType(Type.string(ar.getType()));
					newrec.setZone(zone);
					dnsDAO.addRecord(newrec);
				}

			}
			if (newserial < endserial) {
				newserial = endserial;
			}

		}
		Assert.isTrue(newserial != 0);
		zone.setSerial(newserial);
		upd.setNewSerial(newserial);
		upd.setNumAdded(numadditions);
		upd.setNumDeleted(numdeletions);
		upd.setIncremental(true);
		zone.setLastUpdate(new Date());
		log.debug("IXFR-type updated on zone " + zone.getDisplayName()
				+ " Completed   to serial " + zone.getSerial() + " with "
				+ upd.getNumAdded() + " added " + upd.getNumDeleted()
				+ " deleted ");
		dnsDAO.saveZone(zone);
		return upd;
	}

	/**
	 * Pulls an update for a zone and updates the zone cache
	 * 
	 * writes updated zone information back to the DAO
	 * 
	 * @param zone
	 *            the zone to update
	 * @param fullRefresh
	 *            should we force a full update (AXFR) on this zone
	 */

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public synchronized ZoneUpdateInfo updateZoneData(DNSZone zone,
			boolean fullRefresh) {
		Assert.notNull(zone);
		log.debug("Updating DNS Zone " + zone);
		long serial;
		TSIG key = null;
		if (zone.getSerial() == null) {
			serial = 0;
		} else {
			serial = zone.getSerial();
		}
		if (zone.getSigKey() != null) {
			key = TSIG.fromString(zone.getSigKey());
		}
		ZoneUpdateInfo upd;
		ZoneTransferIn zti;
		try {
			log.debug("starting " + (fullRefresh ? "full" : "partial")
					+ " zone transfer of " + zone.getDomain() + " to server "
					+ zone.getServerIP() + ":" + zone.getServerPort());

			if (fullRefresh) {
				zti = ZoneTransferIn.newAXFR(Name.fromString(zone.getDomain()),
						zone.getServerIP().getHostAddress(), zone
								.getServerPort(), key);
			} else {
				zti = ZoneTransferIn.newIXFR(Name.fromString(zone.getDomain()),
						serial, true, zone.getServerIP().getHostAddress(), zone
								.getServerPort(), key);
			}
			zti.run();

			if (zti.isIXFR()) {
				log.debug("Sucessfully completed IXFR on zone " + zone);
				List<ZoneTransferIn.Delta> deltas = zti.getIXFR();
				upd = performIXFRUpdate(zone, deltas);
			} else if (zti.isAXFR()) {
				log.debug("Completed AXFR on zone " + zone);
				List<Record> records = zti.getAXFR();
				upd = performAXFRUpdate(zone, records);

			} else if (zti.isCurrent()) {
				log.debug("Zone is current, no update required :" + zone);
				upd = new ZoneUpdateInfo();
				upd.setZone(zone);

			} else {
				throw new RuntimeException(
						"I'm not sure if this is supposed to hapen");
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return upd;
	}

	/**
	 * Update all managed zones
	 * 
	 * @param fullRefresh
	 *            should we do a full update (true) or attempt incremental
	 *            updates
	 * 
	 * @return a list of {@link ZoneUpdateInfo} objects containing counts of the
	 *         number of records updated
	 */
	public List<ZoneUpdateInfo> updateAllDNSZones(boolean fullRefresh) {
		log.info("Updating all DNS Zones using a "
				+ (fullRefresh ? "full" : "partial") + " update");
		List<ZoneUpdateInfo> updates = new ArrayList<ZoneUpdateInfo>();
		int totalAdd = 0;
		int totalDel = 0;
		for (DNSZone z : dnsDAO.getAllManagedZones()) {
			try {
				ZoneUpdateInfo upd = ((DNSMgrImpl) applicationContext
						.getBean("dnsMgr")).updateZoneData(z, fullRefresh);

				totalAdd += upd.numAdded;
				totalDel += upd.numDeleted;
				updates.add(upd);
			} catch (Exception e) {
				EventLog.log().error("DNS plugin: error updating zone " + z, e);
			}
		}

		EventLog.log().info(
				"DNS plugin: updated " + updates.size() + " zones , added "
						+ totalAdd + " deleted " + totalDel + " records");

		return updates;
	}

	public long getDefaultTTL(DNSZone zone) {
		if (zone.getDefaultTTL() != null)
			return zone.getDefaultTTL();
		else
			return defaultTTL;
	}

	@Required
	public void setDnsDAO(DNSDao dnsDAO) {
		this.dnsDAO = dnsDAO;
	}

	public boolean supportsCmdType(Class type) {
		return DNSCmd.class.isAssignableFrom(type);

	}

	@Required
	public void setDnsService(DNSService dnsService) {
		this.dnsService = dnsService;
	}

	public int getOrder() {
		return 100;
	}

	@Required
	public void setDnsCmdValidator(DNSCmdValidator dnsCmdValidator) {
		this.dnsCmdValidator = dnsCmdValidator;
	}

	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}
}
