package edu.bath.soak.dns;

import java.util.List;

import edu.bath.soak.dns.model.DNSZone;

/**
 * Interface for object which perform bulk-updates on DNS zones
 * 
 * @author cspocc
 * 
 */
public interface DNSUpdateMgr {

	/**  
	 * Entity for describing the results of a Zone update
	 * @author cspocc
	 *
	 */
	public static class ZoneUpdateInfo {
		DNSZone zone;
		boolean incremental;
		int numAdded;
		int numDeleted;
		long newSerial;
		long oldSerial;

		public String toString() {
			return "Update of zone " + zone + " ("
					+ (incremental ? "incremental" : "full") + ") " + oldSerial
					+ "-" + newSerial + "added:" + numAdded + " deleted:"
					+ numDeleted;
		}

		public DNSZone getZone() {
			return zone;
		}

		public void setZone(DNSZone zone) {
			this.zone = zone;
		}

		public boolean isIncremental() {
			return incremental;
		}

		public void setIncremental(boolean incremental) {
			this.incremental = incremental;
		}

		public int getNumAdded() {
			return numAdded;
		}

		public void setNumAdded(int numAdded) {
			this.numAdded = numAdded;
		}

		public int getNumDeleted() {
			return numDeleted;
		}

		public void setNumDeleted(int numDeleted) {
			this.numDeleted = numDeleted;
		}

		public long getNewSerial() {
			return newSerial;
		}

		public void setNewSerial(long newSerial) {
			this.newSerial = newSerial;
		}

		public long getOldSerial() {
			return oldSerial;
		}

		public void setOldSerial(long oldSerial) {
			this.oldSerial = oldSerial;
		}
	}

	/**
	 * Perform a DNS update on all available zones 
	 * @param fullRefresh Should the zones be refeshed incrementally or fully
	 * @return
	 */
	public List<ZoneUpdateInfo> updateAllDNSZones(boolean fullRefresh);

	/**
	 * Perform a DNS update on a specific zone 
	 * @param zone The zone to update
	 * @param fullRefresh Should the zone be refeshed incrementally or fully
	 * @return
	 */
	public ZoneUpdateInfo updateZoneData(DNSZone zone, boolean fullRefresh);
}
