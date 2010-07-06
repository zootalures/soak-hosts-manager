package edu.bath.soak.web.host;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.bath.soak.mgr.StarredHostsManager;
import edu.bath.soak.net.model.Host;
import edu.bath.soak.net.model.Subnet;
import edu.bath.soak.query.SearchResult;

/**
 * Helper class for host commands 
 * @author cspocc
 *
 */
public class HostsHelper {


	/**
	 * Finds the a subnet which matches the given host 
	 * @param h host to match 
	 * @param subnets a list of subnets to compare against. 
	 * @return a subnet if found, otherwise null 
	 */
	public static  Subnet getSubnet(Host h,List<Subnet> subnets){
		for(Subnet s:subnets){
			if(s.containsIp(h.getIpAddress())){
				return s;
			}
		}
		return null;
	}
	
	/***
	 * Returns a map of host-- subnets 
	 * 
	 * @param hosts
	 * @param subnets
	 * @return
	 */
	public static Map<Host,Subnet> getSubnetMap(List<Host> hosts,List<Subnet> subnets){
		Map<Host,Subnet> map = new HashMap<Host,Subnet>();
		
		for(Host h: hosts){
			map.put(h,getSubnet(h,subnets));
		}
		return map;
	}

	/**
	 * Returns a set of all hosts which are starred out of the given query 
	 * 
	 * @param hosts
	 * @return
	 */
	static Set<Host> getStarredHostsFromSearchResults(StarredHostsManager starredHostsManager ,SearchResult<Host>hosts){
		Set<Host> starred = new HashSet<Host>();
		for(Host h: hosts.getResults()){
			if(starredHostsManager.isStarred(h)){
				starred.add(h);
			}
		}
		return starred;
	}
}
