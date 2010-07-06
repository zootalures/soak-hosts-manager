package edu.bath.soak.hostactivity.model;

import java.net.Inet4Address;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import edu.bath.soak.net.model.LastUsageInfo;
import edu.bath.soak.util.MacAddress;

public class HostActivityDAO extends HibernateDaoSupport {

	public LastUsageInfo getMacIpInfoForIP(Inet4Address address) {
		Session s = getSession();
		return (LastUsageInfo) s.createCriteria(LastUsageInfo.class).add(
				Restrictions.eq("ipAddress", address)).uniqueResult();
	}

	public List<LastUsageInfo> getMacIpInfoForMac(MacAddress address) {
		Session s = getSession();
		return (List<LastUsageInfo>) s.createCriteria(LastUsageInfo.class).add(
				Restrictions.eq("macAddress", address)).addOrder(
				Order.desc("changedAt")).list();
	}

	public List<MacHistory> getMacHistoryForIp(Inet4Address address, int limit) {
		Session s = getSession();

		Criteria c = s.createCriteria(MacHistory.class).add(
				Restrictions.eq("ipAddress", address)).addOrder(
				Order.desc("changedAt"));

		if (limit > 0) {
			c.setMaxResults(limit);
		}
		List<MacHistory> history = c.list();
		return history;
	}

	public List<MacHistory> getMacHistoryForMac(MacAddress address, int limit) {
		Session s = getSession();

		Criteria c = s.createCriteria(MacHistory.class).add(
				Restrictions.disjunction().add(
						Restrictions.eq("toMac", address)).add(
						Restrictions.eq("fromMac", address))).addOrder(
				Order.desc("changedAt"));

		if (limit > 0) {
			c.setMaxResults(limit);
		}
		List<MacHistory> history = c.list();
		return history;
	}
}
