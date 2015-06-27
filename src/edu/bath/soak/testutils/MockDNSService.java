package edu.bath.soak.testutils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xbill.DNS.Resolver;
import org.xbill.DNS.Update;

import edu.bath.soak.dns.DNSService;
import edu.bath.soak.dns.model.DNSZone;

public class MockDNSService implements DNSService {

	Map<DNSZone, List<Update>> updates = new HashMap<DNSZone, List<Update>>();

	public Map<DNSZone, List<Update>> getUpdates() {
		return updates;
	}

	public Resolver getResolverForZone(DNSZone zone) {
		throw new RuntimeException("operation not implemented");
	}

	public boolean isFresh(DNSZone zone) throws DNSServiceException {
		return false;
	}

	public void sendUpdate(DNSZone zone, Update update) throws DNSServiceException {
		List<Update> ulist = updates.get(zone);
		if(ulist == null){
			ulist = new ArrayList<Update> ();
			updates.put(zone,ulist);
		}
		ulist.add(update);
		

	}

}
