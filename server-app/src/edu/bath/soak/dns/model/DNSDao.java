package edu.bath.soak.dns.model;

import java.net.Inet4Address;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Conjunction;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.xbill.DNS.Name;
import org.xbill.DNS.Record;
import org.xbill.DNS.ReverseMap;
import org.xbill.DNS.Type;

import edu.bath.soak.cmd.CommandContextHolder;
import edu.bath.soak.dns.DNSService.DNSServiceException;
import edu.bath.soak.dns.model.DNSRecordChange.DNSChangeType;
import edu.bath.soak.dns.query.DNSSearchQuery;
import edu.bath.soak.net.model.Host;
import edu.bath.soak.query.SearchExpander;
import edu.bath.soak.query.SearchResult;
import edu.bath.soak.util.TypeUtils;

/**
 * Manages DNS zone and record in the database,
 * 
 * 
 * @author cspocc
 * 
 */
@Transactional
public class DNSDao {

	SessionFactory sessionFactory;

	/**
	 * 
	 * returns all existing records which exist in the database and match the
	 * specied records
	 * 
	 * @param recs
	 * @return
	 */
	public List<DNSRecord> findMatchingRecords(Collection<DNSRecord> recs) {
		Criteria c = sessionFactory.getCurrentSession().createCriteria(
				DNSRecord.class);
		Disjunction disj = Restrictions.disjunction();
		for (DNSRecord r : recs) {
			Conjunction cons = Restrictions.conjunction();
			cons.add(Restrictions.eq("zone", r.getZone())).add(
					Restrictions.eq("hostName", r.getHostName())).add(
					Restrictions.eq("type", r.getType())).add(
					Restrictions.eq("target", r.getTarget()));
			disj.add(cons);
		}
		c.add(disj);
		return c.setCacheable(true).list();
	}

	public DNSRecord findRecord(DNSZone zone, Record r) {
		return findRecord(zone, r.getName().toString(), Type
				.string(r.getType()), r.rdataToString());
	}

	public List<DNSRecord> findRecords(DNSZone zone, String hostName,
			String type) {

		Session s = sessionFactory.getCurrentSession();

		return (List<DNSRecord>) s.createCriteria(DNSRecord.class).add(
				Restrictions.eq("zone", zone)).add(
				Restrictions.eq("hostName", hostName)).add(
				Restrictions.eq("type", type)).setCacheable(true).list();

	}

	public List<DNSRecord> findRecordsInZones(
			Collection<? extends DNSZone> zones, String hostName, String type) {

		Session s = sessionFactory.getCurrentSession();

		return (List<DNSRecord>) s.createCriteria(DNSRecord.class).add(
				Restrictions.in("zone", zones)).add(
				Restrictions.eq("hostName", hostName)).add(
				Restrictions.eq("type", type)).list();

	}

	public DNSRecord findRecord(DNSZone zone, String hostName, String type,
			String rdata) {

		Session s = sessionFactory.getCurrentSession();

		return (DNSRecord) s.createCriteria(DNSRecord.class).add(
				Restrictions.eq("zone", zone)).add(
				Restrictions.eq("hostName", hostName)).add(
				Restrictions.eq("type", type)).add(
				Restrictions.eq("target", rdata)).setCacheable(true).uniqueResult();

	}

	public DNSRecord getRecord(Long id) {
		return (DNSRecord) sessionFactory.getCurrentSession().get(
				DNSRecord.class, id);
	}

	/**
	 * Finds all records which reference a particular DNS Name
	 * 
	 * 
	 * Note that for normal (A) records this will return records which point to
	 * this address. For PTR records this will return records that this address
	 * points to.
	 * 
	 * FIXME: These two calls aren't really consistent
	 * 
	 * @param zone
	 * @param name
	 * @param type
	 * @return
	 * @throws DNSServiceException
	 */
	@SuppressWarnings("unchecked")
	public List<DNSRecord> findRecords(DNSZone zone, Name name, int type) {

		Session s = sessionFactory.getCurrentSession();
		Query q;

		if (type == Type.NULL || type == Type.ANY) {
			q = s.createQuery("from DNSRecord  where hostName=? and zone=?")
					.setString(0, name.toString()).setParameter(1, zone);
		} else {
			q = s.createQuery(
					"from DNSRecord where hostName=? and zone=? and type=?")
					.setString(0, name.toString()).setParameter(1, zone)
					.setString(2, Type.string(type));
		}
		return q.list();
	}

	public void saveZone(DNSZone zone) {
		if (zone.getId() != null)
			sessionFactory.getCurrentSession().merge(zone);
		else {
			sessionFactory.getCurrentSession().save(zone);

		}
	}

	/***************************************************************************
	 * Saves a record to the database,
	 * 
	 * @param rec
	 */
	public void saveRecord(DNSRecord rec) {
		Session s = sessionFactory.getCurrentSession();

		if (rec.getId() != null && s.get(DNSRecord.class, rec.getId()) != null) {
			sessionFactory.getCurrentSession().update(rec);
		} else {
			sessionFactory.getCurrentSession().save(rec);

			if (!rec.getType().equals("SOA")) {
				DNSRecordChange change = new DNSRecordChange();
				change.setRecord(rec);
				change.setCommandId(CommandContextHolder.currentCommandId());
				change.setChangeDate(new Date());
				change.setChangeType(DNSChangeType.ADD);
				sessionFactory.getCurrentSession().save(change);
			}

		}

	}

	public List<DNSZone> getAllManagedZones() {
		Session s = sessionFactory.getCurrentSession();
		return s.createQuery("From DNSZone order by id").setCacheable(true).list();
	}

	public List<ForwardZone> getForwardZones() {
		Session s = sessionFactory.getCurrentSession();
		return s.createQuery("From ForwardZone order by id").setCacheable(true).list();
	}

	public List<ReverseZone> getReverseZones() {
		Session s = sessionFactory.getCurrentSession();
		return s.createQuery("From ReverseZone order by id").setCacheable(true).list();
	}

	public List<DNSRecord> getAllRecordsForZone(DNSZone zone) {
		Assert.notNull(zone);
		Session s = sessionFactory.getCurrentSession();

		return s.createQuery(
				"From DNSRecord where zone=:zone order by hostName,type")
				.setParameter("zone", zone).list();
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public void addRecord(DNSRecord r) {
		saveRecord(r);
	}

	public void deleteRecord(Long id) {
		DNSRecord r = getRecord(id);
		if (!r.getType().equals("SOA")) {
			DNSRecordChange change = new DNSRecordChange();
			change.setRecord(r);
			change.setChangeDate(new Date());
			change.setChangeType(DNSChangeType.DEL);
			change.setCommandId(CommandContextHolder.currentCommandId());
			sessionFactory.getCurrentSession().save(change);

		}
		sessionFactory.getCurrentSession().delete(r);
		sessionFactory.getCurrentSession().flush();
	}

	public void deleteZone(DNSZone zone) {
		Session s = sessionFactory.getCurrentSession();
		for (DNSRecord rec : getAllRecordsForZone(zone)) {
			deleteRecord(rec.getId());
		}
		s.delete(zone);

	}

	public DNSZone getZone(long id) {
		return (DNSZone) sessionFactory.getCurrentSession().load(DNSZone.class,
				id);
	}

	public Criteria expandSearchCrteria(Criteria c, DNSSearchQuery search) {
		if (search.getDnsZone() != null) {
			c.add(Restrictions.eq("zone", search.getDnsZone()));
		}
		if (StringUtils.hasText(search.getRecordType())) {
			c.add(Restrictions.eq("type", search.getRecordType()));

		}
		if (StringUtils.hasText(search.getSearchTerm())) {

			String searchTerm = search.getSearchTerm();
			searchTerm = StringUtils.trimWhitespace(searchTerm);

			String[] searchTerms = searchTerm.split("\\s+");
			Conjunction conj = Restrictions.conjunction();
			for (String st : searchTerms) {
				Disjunction disj = Restrictions.disjunction();
				try {
					Inet4Address ip = TypeUtils.txtToIP(st);
					if (ip != null) {
						disj.add(Restrictions.eq("hostName", ReverseMap
								.fromAddress(ip).toString()));
					}

				} catch (Exception e) {
				}
				disj.add(Restrictions.ilike("hostName", "%" + st + "%"));
				disj.add(Restrictions.ilike("target", "%" + st + "%"));
				disj.add(Restrictions.ilike("type", "%" + st + "%"));
				conj.add(disj);

			}
			c.add(conj);
		}
		return c;
	}

	public int countResultsForSearch(DNSSearchQuery query) {
		Session s = sessionFactory.getCurrentSession();
		Criteria c = s.createCriteria(DNSRecord.class);

		return ((Integer) (c.setProjection(Projections.countDistinct("id"))
				.list().get(0))).intValue();

	}

	public SearchResult<DNSRecord> searchRecords(DNSSearchQuery query) {
		Session s = sessionFactory.getCurrentSession();
		SearchResult<DNSRecord> result = new SearchResult<DNSRecord>(query);
		result.setTotalResults(countResultsForSearch(query));
		Criteria c = s.createCriteria(DNSRecord.class);

		c = expandSearchCrteria(c, query);
		if (null != query.getOrderBy()) {
			if (query.isAscending()) {
				c.addOrder(Order.asc(query.getOrderBy()));
			} else {

				c.addOrder(Order.desc(query.getOrderBy()));
			}
		}
		if (query.getMaxResults() > 0)
			c.setMaxResults(query.getMaxResults());
		if (query.getFirstResult() > 0)
			c.setFirstResult(query.getFirstResult());

		result.setFirstResultOffset(query.getFirstResult());

		result.setResults(c.setCacheable(true).list());

		return result;
	}
}
