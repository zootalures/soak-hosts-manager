package edu.bath.soak.dns;

import java.io.IOException;
import java.net.UnknownHostException;

import org.apache.log4j.Logger;
import org.xbill.DNS.Message;
import org.xbill.DNS.Rcode;
import org.xbill.DNS.Resolver;
import org.xbill.DNS.SimpleResolver;
import org.xbill.DNS.TSIG;
import org.xbill.DNS.Update;

import edu.bath.soak.dns.model.DNSZone;

/**
 * Standard DNS Service for DDNS-like udpates
 * 
 * @author cspocc
 * 
 */
public class DDNSServiceImpl implements DNSService {

	Logger log = Logger.getLogger(this.getClass());

	/**
	 * Checks that the zone's SOA record serial matches the one stored in the
	 * zone object.
	 * 
	 * @param zone
	 *            the zone to termine the freshness of
	 * @return true if the zone is fresh, false otherwise
	 * @throws DNSServiceException
	 *             if the server could not be contacted
	 */
	public boolean isFresh(DNSZone zone) throws DNSServiceException {
		return true;
	}

	/**
	 * Returns a configured resolver for a given zone
	 * 
	 * @param zone
	 * 
	 * @return The appropriate resolver for this zone configured with the
	 *         appropriate IPs and keys
	 */
	public Resolver getResolverForZone(DNSZone zone) {

		SimpleResolver sr;

		try {
			sr = new SimpleResolver(zone.getServerIP().getHostAddress());
		} catch (UnknownHostException e) {
			throw new RuntimeException(e);
		}
		if (null != zone.getSigKey()) {
			sr.setTSIGKey(TSIG.fromString(zone.getSigKey()));
		}

		sr.setTCP(zone.isUseTCP());
		sr.setPort(zone.getServerPort());
		return sr;
	}

	/**
	 * Sends an update to the DNS server
	 * 
	 * @param zone
	 *            the zone to update
	 * @param update
	 *            an XBILL dns update object containing adds or deletes
	 * @throws DNSServiceException
	 *             if an error occured while trying to contact the server
	 */
	public void sendUpdate(DNSZone zone, Update update)
			throws DNSServiceException {
		try {
			log.debug("sending update for zone " + zone + " :  " + update
					+ " adds:" + update.getSectionArray(2).length + " deletes:"
					+ update.getSectionArray(3).length);

			Resolver resolver = getResolverForZone(zone);
			Message response = resolver.send(update);
			//System.out.println(response.toString());

			log.debug("server responded with code "
					+ response.getHeader().getRcode());
			int rcode = response.getHeader().getRcode();
			if (rcode != 0) {
				throw new DNSMgrUpdateException("Update on zone \""
						+ zone.getDomain() + "\" failed with code " + rcode
						+ " ( " + Rcode.string(rcode) + " ) ");
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw new DNSMgrUpdateException(e.getMessage());
		}
	}
}
