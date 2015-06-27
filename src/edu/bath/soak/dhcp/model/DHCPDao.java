package edu.bath.soak.dhcp.model;

import java.net.Inet4Address;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import edu.bath.soak.cmd.CommandContextHolder;
import edu.bath.soak.dhcp.DHCPReservationChangeQuery;
import edu.bath.soak.net.model.IPRange;
import edu.bath.soak.net.model.NetDAO;
import edu.bath.soak.query.SearchResult;
import edu.bath.soak.util.MacAddress;
import edu.bath.soak.util.TypeUtils;

/**
 * DAO Object for
 * 
 * @author cspocc
 * 
 */
@Transactional
public class DHCPDao extends HibernateDaoSupport {
	Logger log = Logger.getLogger(DHCPDao.class);

	List<StaticDHCPReservation> getAllReservationsInRange(DHCPScope range) {
		Session s = getSession();
		return s.createCriteria(StaticDHCPReservation.class).add(
				Restrictions.eq("scope", range)).list();
	}

	public List<StaticDHCPReservation> getAllReservationsForMAC(MacAddress mac) {
		Session s = getSession();
		return s.createCriteria(StaticDHCPReservation.class).add(
				Restrictions.eq("macAddress", mac)).setCacheable(true).list();
	}

	public StaticDHCPReservation getAllReservationForMAC(DHCPScope range,
			MacAddress mac) {
		Session s = getSession();
		return (StaticDHCPReservation) s.createCriteria(
				StaticDHCPReservation.class).add(
				Restrictions.eq("macAddress", mac)).add(
				Restrictions.eq("scope", range)).setCacheable(true).uniqueResult();

	}

	public StaticDHCPReservation getReservationForIP(DHCPScope range,
			Inet4Address addr) {
		Session s = getSession();
		return (StaticDHCPReservation) s.createCriteria(
				StaticDHCPReservation.class).add(
				Restrictions.eq("ipAddress", addr)).add(
				Restrictions.eq("scope", range)).setCacheable(true).uniqueResult();

	}

	public StaticDHCPReservation getReservationForMAC(DHCPScope range,
			MacAddress addr) {
		Session s = getSession();
		return (StaticDHCPReservation) s.createCriteria(
				StaticDHCPReservation.class).add(
				Restrictions.eq("macAddress", addr)).add(
				Restrictions.eq("scope", range)).setCacheable(true).uniqueResult();

	}

	public void saveDHCPServer(DHCPServer info) {
		Session s = getSession();
		s.saveOrUpdate(info);
	}

	public void saveScope(DHCPScope info) {
		Session s = getSession();
		s.saveOrUpdate(info);
		s.flush();
	}

	public List<DHCPServer> getDHCPServers() {
		Session s = getSession();
		return s.createCriteria(DHCPServer.class).list();
	}

	public DHCPServer getDHCPServer(long id) {
		Session s = getSession();
		return (DHCPServer) s.get(DHCPServer.class, id);
	}

	public List<DHCPScope> getDHCPScopes(DHCPServer server) {
		Session s = getSession();
		return s.createCriteria(DHCPScope.class).add(
				Restrictions.eq("server", server)).setCacheable(true).list();
	}

	public List<StaticDHCPReservation> getReservationsInScope(DHCPScope scope) {
		Session s = getSession();
		return s.createCriteria(StaticDHCPReservation.class).add(
				Restrictions.eq("scope", scope)).list();
	}

	public DHCPScope getScopeContainingIp(DHCPServer server,
			Inet4Address address) {
		Session s = getSession();
		List<DHCPScope> scopes = getDHCPScopes(server);
		for(DHCPScope scope: scopes){
			if(scope.containsIp(address))
				return scope;
		}
		return null;
	}

	public List<DHCPScope> getScopesMatchingRange(IPRange range) {
		Session s = getSession();
		return s.createQuery(
				"FROM DHCPScope  where (minIP <= :minip  AND maxIP >= :minip) "
						+ "OR (minIP <=:maxip AND maxIP >= :maxip) "
						+ "OR (:minip <= minIP AND :maxip >= minIP) "
						+ "OR (:maxip <=minIP AND :maxip >=maxIP)")
				.setParameter("minip", range.getMinIP()).setParameter("maxip",
						range.getMaxIP()).list();
	}

	public void saveReservation(StaticDHCPReservation res) {
		Session s = getSession();
		s.save(res);

		if (res instanceof StaticDHCPReservation) {
			DHCPReservationChange change = new DHCPReservationChange();
			change.setReservation(res);
			change.setChangeDate(new Date());
			change.setChangeType(DHCPReservationChange.DHCPChangeType.ADD);
			change.setCommandId(CommandContextHolder.currentCommandId());
			s.save(change);
		}
	}

	public SearchResult<DHCPReservationChange> searchDHCPReservationChanges(
			DHCPReservationChangeQuery query) {
		Session s = getSession();

		Criteria crit = s.createCriteria(DHCPReservationChange.class);
		crit = expandSearchDHCPReservationChangesCriteria(query, crit);
		int numResults = ((Integer) (crit
				.setProjection(Projections.count("id")).list().get(0)))
				.intValue();

		crit = s.createCriteria(DHCPReservationChange.class);
		crit = expandSearchDHCPReservationChangesCriteria(query, crit);
		crit = NetDAO.setupSearchQuery(query, crit);

		crit.setProjection(Projections.distinct(Projections.property("id")));
		List<Long> ids = crit.list();

		SearchResult<DHCPReservationChange> result = new SearchResult<DHCPReservationChange>(
				query);
		result.setFirstResultOffset(query.getFirstResult());
		result.setTotalResults(numResults);
		if (ids.size() > 0) {
			Criteria fetchChanges = s
					.createCriteria(DHCPReservationChange.class);
			fetchChanges = NetDAO.setupSearchOrder(query, fetchChanges);

			fetchChanges.add(Restrictions.in("id", ids));
			fetchChanges
					.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);

			result.setResults(fetchChanges.list());
		} else {
			result.setResults(new LinkedList<DHCPReservationChange>());
		}
		return result;

	}

	private Criteria expandSearchDHCPReservationChangesCriteria(
			DHCPReservationChangeQuery query, Criteria crit) {
		if (StringUtils.hasText(query.getSearchTerm())) {
			String searchTerm = query.getSearchTerm();
			searchTerm = StringUtils.trimWhitespace(searchTerm);

			String[] searchTerms = searchTerm.split("\\s+");
			for (String st : searchTerms) {
				st = st.trim();
				if (st.matches("cmdId:.*")) {
					try {
						String idpart = st.substring(6);
						crit = crit.add(Restrictions.eq("commandId", idpart));
						continue;
					} catch (Exception e) {

					}

				}
				try {
					// is the term a mac-like thing (i.e with colons
					if (MacAddress.isPartialMac(searchTerm)) {
						log.trace("Search term " + st
								+ "matches as MAC address");

						crit.add(Restrictions.like("reservation.macAddress",
								"%" + searchTerm + "%"));

						continue;
					}

				} catch (Exception e) {
				}

				try {
					Inet4Address ip = TypeUtils.txtToIP(st);
					if (ip != null) {
						log.trace("Search term " + st
								+ "matches as IP address " + ip);
						crit = crit.add(Restrictions.eq(
								"reservation.ipAddress", ip));
						continue;
					}
				} catch (Exception e) {
				}

				try {

					IPRange range = TypeUtils.txtToCIDRRange(st);
					log.trace("Search term " + st + "matches as CIDR  Range "
							+ range);

					crit = crit.add(
							Restrictions.ge("reservation.ipAddress", range
									.getMinIP())).add(
							Restrictions.le("reservation.ipAddress", range
									.getMaxIP()));

					continue;
				} catch (Exception e) {

				}
				log.trace("Search term " + st + " matching as name");

				Criterion nameterm = Restrictions.ilike(
						"reservation.hostName.FQDN", "%" + st + "%");

				crit = crit.add(nameterm);

			}
		}
		return crit;
	}

	public void deleteReservation(Long id) {

		Session s = getSession();
		StaticDHCPReservation res = (StaticDHCPReservation) s.load(
				StaticDHCPReservation.class, id);
		s.delete(res);

		if (res instanceof StaticDHCPReservation) {
			DHCPReservationChange change = new DHCPReservationChange();

			change.setReservation((StaticDHCPReservation) res);
			change.setChangeDate(new Date());
			change.setChangeType(DHCPReservationChange.DHCPChangeType.DEL);
			change.setCommandId(CommandContextHolder.currentCommandId());
			s.save(change);
		}
		s.flush();

	}

	public List<StaticDHCPReservation> getAllReservationsForIP(
			Inet4Address ipAddress) {
		Session s = getSession();
		return s.createCriteria(StaticDHCPReservation.class).add(
				Restrictions.eq("ipAddress", ipAddress)).setCacheable(true).list();
	}

	public DHCPScope getScope(long id) {
		Session s = getSession();
		return (DHCPScope) s.load(DHCPScope.class, id);
	}
}
