package edu.bath.soak.dns;

import org.xbill.DNS.Resolver;
import org.xbill.DNS.Update;

import edu.bath.soak.dns.model.DNSZone;

/**
 * Back-end DNS service, respsonsible for dispatching and execuing DNS updates.
 * 
 * @author cspocc
 * 
 */
public interface DNSService {
	public static class DNSServiceException extends Exception {
		private static final long serialVersionUID = 1L;

		public DNSServiceException(String s) {
			super(s);
		}

		public DNSServiceException(String s, Exception t) {
			super(s, t);
		}
	}

	public static class DNSMgrUpdateException extends DNSServiceException {
		private static final long serialVersionUID = 1L;

		public DNSMgrUpdateException(String s) {
			super(s);
		}

		public DNSMgrUpdateException(String s, Exception t) {
			super(s, t);
		}
	}

	public static class DNSMgrHostExistsException extends DNSMgrUpdateException {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public DNSMgrHostExistsException(String s) {
			super(s);
		}
	}

	/**
	 * Returns an appropriate Resolver object for a given zone
	 * 
	 * The resolver should be configured to send updates to this zone using the
	 * appropriate keys etc.
	 * 
	 * @param zone
	 * @return
	 */
	public Resolver getResolverForZone(DNSZone zone);

	/**
	 * Transmits a given DNS update for a given zone to the server
	 * 
	 * @param zone
	 * @param updates
	 * @throws DNSServiceException
	 *             if the operation failed (this should be atomic)
	 */
	public void sendUpdate(DNSZone zone, Update updates)
			throws DNSServiceException;

	/**
	 * Indicates whether or not the given DNSZone object is fresh
	 * 
	 * @param zone
	 * @return
	 * @throws DNSServiceException
	 */
	public boolean isFresh(DNSZone zone) throws DNSServiceException;

}
