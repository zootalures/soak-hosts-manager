package edu.bath.soak.net.model;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.Inet4Address;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.ObjectNotFoundException;
import org.hibernate.Session;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.xml.sax.SAXException;

import com.sun.xml.bind.IDResolver;

import edu.bath.soak.cmd.BaseCompositeCommand;
import edu.bath.soak.model.OrgUnit;
import edu.bath.soak.model.OrgUnitPrincipalMapping;
import edu.bath.soak.model.OrgUnitPrincipalMapping.PrincipalType;
import edu.bath.soak.net.model.HostChange.ChangeType;
import edu.bath.soak.net.query.HostChangeQuery;
import edu.bath.soak.net.query.HostSearchQuery;
import edu.bath.soak.net.query.SubnetQuery;
import edu.bath.soak.query.ExtensibleSearchTarget;
import edu.bath.soak.query.SearchExpander;
import edu.bath.soak.query.SearchQuery;
import edu.bath.soak.query.SearchResult;
import edu.bath.soak.security.SecurityHelper;
import edu.bath.soak.undo.cmd.SearchStoredCommandsCmd;
import edu.bath.soak.util.MacAddress;
import edu.bath.soak.util.Tuple;
import edu.bath.soak.util.TypeUtils;
import edu.bath.soak.xml.MultipleEntityIDResolver;
import edu.bath.soak.xml.SoakXMLIDResolver;
import edu.bath.soak.xml.SoakXMLManager;

/**
 * DAO for most host/networky things.
 * 
 * Most methods are self-explanatory Search methods a query object,
 * 
 * General conventions are for findXXX methods to return null if no object was
 * found and getXXX methods to throw an exception .
 * 
 * @see
 * @author cspocc
 * 
 */
@Transactional(propagation = Propagation.REQUIRED)
public class NetDAO extends HibernateDaoSupport implements
		ExtensibleSearchTarget {
	public static class NotFoundException extends RuntimeException {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public NotFoundException(String message) {
			super(message);
		}
	}

	List<SearchExpander<Host, HostSearchQuery>> searchExpanders = new ArrayList<SearchExpander<Host, HostSearchQuery>>();
	SecurityHelper securityHelper;
	List<SoakXMLIDResolver> xmlIDResolvers = new ArrayList<SoakXMLIDResolver>();
	SoakXMLManager xmlManager;

	Logger log = Logger.getLogger(NetDAO.class);

	SoakXMLIDResolver baseDBResolver = new SoakXMLIDResolver(new Class[] {
			NameDomain.class, HostClass.class, NetworkClass.class, Vlan.class,
			OrgUnit.class }) {
		@Override
		public void bind(String arg0, Object arg1) throws SAXException {
			// by default we ignore any binding in this resolver
		}

		public <T extends Object> T checkNull(Class<T> type, T var, String id) {

			if (var == null) {
				throw new UnresolvedEntityException(
						"Unable to resolve entity of type " + type
								+ " with ID " + id);
			}
			return var;
		}

		@Override
		public Callable<Object> resolve(final String id, Class type)
				throws SAXException {
			if (type.isAssignableFrom(NameDomain.class)) {
				return new Callable<Object>() {
					public Object call() throws Exception {
						return checkNull(NameDomain.class,
								getNameDomainBySuffix(id), id);
					}
				};
			} else if (type.isAssignableFrom(HostClass.class)) {
				return new Callable<Object>() {
					public Object call() throws Exception {
						return checkNull(HostClass.class, getHostClassById(id),
								id);
					}
				};

			} else if (type.isAssignableFrom(NetworkClass.class)) {
				return new Callable<Object>() {
					public Object call() throws Exception {
						return checkNull(NetworkClass.class,
								getNetworkClassById(id), id);
					}
				};
			} else if (type.isAssignableFrom(Vlan.class)) {
				return new Callable<Object>() {
					public Object call() throws Exception {

						return checkNull(Vlan.class, getVlanByNumber(Integer
								.parseInt(id)), id);
					}
				};
			} else if (type.isAssignableFrom(OrgUnit.class)) {
				return new Callable<Object>() {
					public Object call() throws Exception {
						return checkNull(OrgUnit.class, getOrgUnitById(id), id);
					}
				};
			} else {
				throw new SAXException("No ID resolver for type " + type);
			}
		}

	};

	public NetDAO() {

	}

	/***************************************************************************
	 * Counts the number of host on a single subnet
	 * 
	 * @see #countHostsOnSubnets(Collection)
	 * @param rs
	 * @return
	 */
	public int countHostsOnSubnet(Subnet rs) {
		HostSearchQuery hs = new HostSearchQuery();
		hs.setSubnet(rs);
		return countResultsForHostSearchQuery(hs, null);
	}

	/**
	 * Counts the number of hosts on a set of subnets which are owned by a given
	 * OU
	 * 
	 * @param subnets
	 *            the set of subnets to search
	 * @param ou
	 *            the OrgUnit to search
	 * @param includeEmpties
	 *            should subnet with no entries be returned in the results
	 * @return a map of subnet to Long for subnet host usage
	 */
	public Map<Subnet, Integer> countHostsOnSubnetsForOU(
			Collection<Subnet> subnets, OrgUnit ou, boolean includeEmpties) {
		Assert.notNull(subnets);
		Assert.notNull(ou);
		Assert.notNull(ou.getId());

		String having = null;
		String where = " h.orgUnit_id  = '" + ou.getId() + "' ";
		if (!includeEmpties) {
			having = " count(h.id) > 0";
		}
		return countHostsOnSubnetsWhere(subnets, where, having, includeEmpties);
	}

	Map<Subnet, Integer> countHostsOnSubnetsWhere(Collection<Subnet> subnets,
			String where, String having, boolean includeEmpties) {
		Assert.notNull(subnets);
		Session s = getSession();
		HashMap<Subnet, Integer> outputs = new HashMap<Subnet, Integer>();
		if (subnets.size() == 0)
			return outputs;
		List<Long> subnetIds = new LinkedList<Long>();
		for (Subnet subnet : subnets) {
			Assert.notNull(subnet.getId());
			subnetIds.add(subnet.getId());
		}

		if (null == where) {
			where = "TRUE";
		}
		String sql = "SELECT s.id ,count(h.id) as hostCount from Host h , Subnet s "
				+ "WHERE s.minIP <= h.ipAddress AND "
				+ "s.id in (:subnets)  AND "
				+ "s.maxIP >= h.ipAddress AND "
				+ where + " GROUP BY  s.id ";

		if (null != having) {
			sql += " HAVING " + having;
		}
		List<Object[]> values = s.createSQLQuery(sql).setParameterList(
				"subnets", subnetIds).list();

		for (Object[] value : values) {
			Subnet sub = null;
			Number subnetId = (Number) value[0];
			Number count = (Number) value[1];
			for (Subnet subl : subnets) {
				if (subl.getId().equals(subnetId.longValue())) {
					sub = subl;
					break;
				}
			}
			Assert.notNull(sub);
			outputs.put(sub, count.intValue());
		}
		if (includeEmpties)
			for (Subnet sg : subnets) {
				if (outputs.get(sg) == null)
					outputs.put(sg, 0);
			}
		return outputs;

	}

	/**
	 * returns a count of the number of hosts on a set of subnets
	 * 
	 * 
	 * @param subnets
	 *            a collection of subnets to get counts of hosts for
	 * @return A map from {@link Subnet} in subnets to the number of hosts on
	 *         that subnet
	 */
	public Map<Subnet, Integer> countHostsOnSubnets(Collection<Subnet> subnets) {
		return countHostsOnSubnetsWhere(subnets, null, null, true);

	}

	/**
	 * Counts the total number of results which the given host search will
	 * return
	 * 
	 * @param query
	 *            the Query to search on
	 * @param hostIds
	 *            - restrict to given hostIds (null OK indicates don't restrict)
	 * @return the number of results that the uninhibited query would return
	 */
	@Transactional(readOnly = true)
	public int countResultsForHostSearchQuery(HostSearchQuery query,
			Set<Long> hostIds) {
		// calculate the total number of results
		Session s = getSession();
		Criteria c = s.createCriteria(Host.class, "host");

		if (hostIds != null) {
			if (hostIds.isEmpty()) {
				return 0;
			} else {
				c = c.add(Restrictions.in("id", hostIds));
			}
		}
		c = expandHostSearchCriteria(query, c).setResultTransformer(
				CriteriaSpecification.DISTINCT_ROOT_ENTITY);

		return ((Integer) (c.setProjection(Projections.countDistinct("id"))
				.list().get(0))).intValue();
	}

	/**
	 * Given a host query, this counts the number of host which would be
	 * returned by that query grouped by host class
	 * 
	 * @param query
	 *            a host query
	 * @return a map of HostClass to integer indicating the count of hosts for
	 *         each host class
	 */
	public Map<HostClass, Integer> countHostsByHostClassForHostSearchQuery(
			HostSearchQuery query) {
		// calculate the total number of results
		Session s = getSession();
		Criteria c = s.createCriteria(Host.class, "host");

		c = expandHostSearchCriteria(query, c).setResultTransformer(
				CriteriaSpecification.DISTINCT_ROOT_ENTITY);

		c.setProjection(Projections.projectionList().add(
				Projections.groupProperty("hostClass")).add(
				Projections.countDistinct("id")));
		List<Object[]> listResults = c.list();

		HashMap<HostClass, Integer> resultMap = new HashMap<HostClass, Integer>();
		for (Object[] res : listResults) {
			resultMap.put((HostClass) res[0], (Integer) res[1]);
		}
		return resultMap;
	}

	public void deleteHost(Long hostId, String commandId) {
		deleteHost(hostId, commandId, null);
	}

	/**
	 * Deletes a host records the change with comment
	 * 
	 * @param h
	 * @param comment
	 */
	public void deleteHost(Long hostId, String commandId, String comment) {
		Host deleteHost = loadHost(hostId);

		recordCurrentHostVersion(deleteHost, commandId, ChangeType.DELETE,
				"Host deleted", comment);
		for (HostAlias ha : deleteHost.getHostAliases()) {
			getSession().delete(ha);
		}
		for (ExtendedHostInfo hc : deleteHost.getConfigSettings().values()) {
			getSession().delete(hc);
		}
		getSession().delete(deleteHost);

	}

	/**
	 * Deletes a host class, also removes any references to the host class from
	 * any network classes, name domains or subnets
	 * 
	 * @param hc
	 */
	public void deleteHostClass(HostClass hc) {

		for (NameDomain nd : getNameDomains()) {
			nd.getAllowedClasses().remove(hc);
			saveNameDomain(nd);
		}
		for (NetworkClass nc : getNetworkClasses()) {
			nc.getAllowedHostClasses().remove(hc);

			saveNetworkClass(nc);
		}
		for (Subnet s : getSubnets()) {
			s.getSubnetAllowedHostClasses().remove(hc);
			s.getSubnetDeniedHostClasses().remove(hc);
			saveSubnet(s);
		}

		getSession().delete(hc);
		getSession().flush();

	}

	/**
	 * Deletes a Name domain ,
	 * 
	 * @param nc
	 */
	public void deleteNameDomain(NameDomain nd) {

		getSession().delete(nd);
		getSession().flush();

	}

	/**
	 * Deletes a network class,
	 * 
	 * @param nc
	 */
	public void deleteNetworkClass(NetworkClass nc) {

		getSession().delete(nc);
		getSession().flush();

	}

	/**
	 * Deletes an Org unit from the database, removes the org unit from any ACLs
	 * 
	 * @param toDelete
	 */
	public void deleteOrgUnit(OrgUnit toDelete) {
		for (Subnet s : getSubnets()) {
			s.getOrgUnitAcl().getAclEntries().remove(toDelete);
			saveSubnet(s);
		}
		for (NameDomain nd : getNameDomains()) {
			nd.getOrgUnitAcl().getAclEntries().remove(toDelete);
			saveNameDomain(nd);
		}
		for (HostClass hc : getHostClasses()) {
			hc.getOrgUnitAcl().getAclEntries().remove(toDelete);
			saveHostClass(hc);
		}
		for (NetworkClass nc : getNetworkClasses()) {
			nc.getOrgUnitAcl().getAclEntries().remove(toDelete);
			saveNetworkClass(nc);
		}
		for (OrgUnitPrincipalMapping oup : getOrgUnitPrincipalMappingsForOU(toDelete)) {
			deleteOrgUnitPrincipalMapping(oup);
		}
		getSession().delete(toDelete);
		getSession().flush();
	}

	/**
	 * Deletes an org unit principal mapping from the database
	 * 
	 * @param ogr
	 */
	public void deleteOrgUnitPrincipalMapping(OrgUnitPrincipalMapping ogr) {
		getSession().delete(ogr);
		getSession().flush();
	}

	/**
	 * Deletes a Vlan Users must ensure that any subnets which reference this
	 * Vlan have been changed before deletion
	 * 
	 * @param v
	 */
	public void deleteVlan(Vlan v) {
		getSession().delete(v);
		getSession().flush();
	}

	public void deleteSubnet(Subnet s) {
		// s.getSubnetAllowedHostClasses().clear();
		// s.getSubnetDeniedHostClasses().clear();
		// getSession().save(s);
		// getSession().flush();
		getSession().delete(s);
		getSession().flush();
	}

	/**
	 * Builds the criteria for the text-based component of a query
	 * 
	 * 
	 * Tokenizes the input and then applys the following: - if t looks like an
	 * IP address - constrain on IP - If T looks like a MAC - constrain on MAC
	 * prefix
	 * 
	 * 
	 * 
	 * @param search
	 *            the host query to use
	 * @param c
	 *            the criteria to append to (will be modified in place)
	 * @return a new criteria
	 */
	Criteria expandHostSearchCriteria(HostSearchQuery search, Criteria c) {
		boolean needsAliasQuery = false;

		// .createAlias("lastUsageInfo",
		// "liu", CriteriaSpecification.LEFT_JOIN)
		if (StringUtils.hasText(search.getSearchTerm())) {

			String searchTerm = search.getSearchTerm();
			searchTerm = StringUtils.trimWhitespace(searchTerm);

			String[] searchTerms = searchTerm.split("\\s+");
			for (String st : searchTerms) {
				for (SearchExpander expander : searchExpanders) {
					Criteria matchedC = expander
							.expandSearchTerm(c, searchTerm);
					if (matchedC != null) {
						c = matchedC;
						continue;
					}
				}
				try {
					Inet4Address ip = TypeUtils.txtToIP(st);
					if (ip != null) {
						log.trace("Search term " + st
								+ "matches as IP address " + ip);
						c = c.add(Restrictions.eq("ipAddress", ip));
						continue;
					}
				} catch (Exception e) {
				}

				try {
					// is the term a mac-like thing (i.e with colons
					if (MacAddress.isPartialMac(searchTerm)) {
						log.trace("Search term " + st
								+ "matches as MAC address");

						c.add(Restrictions.like("macAddress", "%" + searchTerm
								+ "%"));

						continue;
					}

				} catch (Exception e) {
				}

				try {

					IPRange range = TypeUtils.txtToCIDRRange(st);
					log.trace("Search term " + st + "matches as CIDR  Range "
							+ range);

					c = c
							.add(Restrictions.ge("ipAddress", range.getMinIP()))
							.add(Restrictions.le("ipAddress", range.getMaxIP()));

					continue;
				} catch (Exception e) {

				}
				try {

					if (st
							.matches("\\d+\\.\\d+\\.\\d+\\.\\d+\\-\\d+\\.\\d+\\.\\d+\\.\\d+")) {
						String[] parts = st.split("\\-");

						IPRange range = new IPRange(
								TypeUtils.txtToIP(parts[0]), TypeUtils
										.txtToIP(parts[1]));
						log.trace("Search term " + st
								+ "matches as an IP rangeRange " + range);

						c = c.add(
								Restrictions.ge("ipAddress", range.getMinIP()))
								.add(
										Restrictions.le("ipAddress", range
												.getMaxIP()));

						continue;

					}
				} catch (Exception e) {

				}
				Criterion ipTerm = Restrictions.ilike("ipAddressTxt", "%" + st
						+ "%");
				Criterion descterm = Restrictions.ilike("description", "%" + st
						+ "%");

				Criterion locterm = Restrictions.ilike("location.fullLocation",
						"%" + st + "%");
				log.trace("Search term " + st + " matching as name");

				Criterion nameterm = Restrictions.ilike("hostName.FQDN", "%"
						+ st + "%");
				Criterion aliasterm = Restrictions.ilike("alias.alias.FQDN",
						"%" + st + "%");
				c = c.add(Restrictions.disjunction().add(ipTerm).add(descterm)
						.add(locterm).add(nameterm).add(aliasterm));
				needsAliasQuery = true;
			}
		}

		if (null != search.getHostClass()) {
			log.trace("constraining to host class "
					+ search.getHostClass().getId());
			c = c.add(Restrictions.eq("hostClass", search.getHostClass()));
		}

		if (null != search.getSubnet()) {
			// log.trace("constraining to subnet " +
			// search.getSubnet().getName());

			Subnet s = search.getSubnet();
			c = c.add(Restrictions.ge("ipAddress", s.getMinIP())).add(
					Restrictions.le("ipAddress", s.getMaxIP()));

		}

		if (null != search.getNameDomain()) {
			log.trace("constraining to name domain "
					+ search.getNameDomain().getSuffix());

			c = c.add(Restrictions
					.eq("hostName.domain", search.getNameDomain()));
		}

		if (null != search.getOrgUnit()) {
			log.trace("constraining search to org unit "
					+ search.getOrgUnit().getId());
			c = c
					.add(Restrictions.eq("ownership.orgUnit", search
							.getOrgUnit()));
		}

		if (null != search.getOnlyIncludeMyHosts()
				&& search.getOnlyIncludeMyHosts()) {

			log.trace("constraining search to users' hosts");
			Collection<OrgUnit> allowedOUs = securityHelper
					.getAllowedOrgUnitsForCurrentUser();
			if (!allowedOUs.isEmpty()) {
				c = c.add(Restrictions.in("ownership.orgUnit", allowedOUs));
			}

		}

		for (SearchExpander expander : searchExpanders) {
			Criteria matchedc = expander.expandSearchQuery(search, c);
			if (matchedc != null)
				c = matchedc;
		}
		if (needsAliasQuery) {
			c = c.createAlias("hostAliases", "alias",
					CriteriaSpecification.LEFT_JOIN);
		}
		return c;

	}

	/**
	 * Expands a {@link Criteria} for a {@link HostChangeQuery} search
	 * 
	 * @param query
	 *            the query to expand
	 * @param crit
	 *            the criteria to add to
	 * @return an udpated criteria
	 */
	Criteria expandSearchHostChangesCriteria(HostChangeQuery query,
			Criteria crit) {
		if (StringUtils.hasText(query.getSearchTerm())) {
			String searchTerm = query.getSearchTerm();
			searchTerm = StringUtils.trimWhitespace(searchTerm);

			String[] searchTerms = searchTerm.split("\\s+");
			for (String st : searchTerms) {
				st = st.trim();
				if (st.matches("id:\\d+")) {
					try {
						String idpart = st.substring(3);
						long id = Long.parseLong(idpart);
						crit = crit.add(Restrictions.eq("hostId", id));
						continue;
					} catch (Exception e) {

					}

				}

				if (st.matches("user:.*")) {
					try {
						String userpart = st.substring(5);
						crit = crit.add(Restrictions.eq("userId", userpart));
						continue;
					} catch (Exception e) {

					}

				}
				if (st.matches("cmdId:.*")) {
					try {
						String idpart = st.substring(6);
						crit = crit.add(Restrictions.eq("commandId", idpart));
						continue;
					} catch (Exception e) {

					}

				}
				try {
					Inet4Address ip = TypeUtils.txtToIP(st);
					if (ip != null) {
						log.trace("Search term " + st
								+ "matches as IP address " + ip);
						crit = crit.add(Restrictions.eq("ipAddress", ip));
						continue;
					}
				} catch (Exception e) {
				}

				try {

					IPRange range = TypeUtils.txtToCIDRRange(st);
					log.trace("Search term " + st + "matches as CIDR  Range "
							+ range);

					crit = crit
							.add(Restrictions.ge("ipAddress", range.getMinIP()))
							.add(Restrictions.le("ipAddress", range.getMaxIP()));

					continue;
				} catch (Exception e) {

				}
				log.trace("Search term " + st + " matching as name");

				Criterion nameterm = Restrictions.ilike("hostName.FQDN", "%"
						+ st + "%");
				Criterion comment = Restrictions.ilike("changeComments", "%"
						+ st + "%");
				Criterion username = Restrictions.ilike("userId", "%" + st
						+ "%");

				crit = crit.add(Restrictions.disjunction().add(nameterm).add(
						comment).add(username));

			}
		}

		if (!securityHelper.isAdmin() || query.isShowMine()) {
			if (securityHelper.getAllowedOrgUnitsForCurrentUser().isEmpty()) {

				return crit.add(Restrictions.eq("id", -1L));
			}

			crit = crit.add(Restrictions.in("orgUnit", securityHelper
					.getAllowedOrgUnitsForCurrentUser()));
		}
		if (query.getOrgUnit() != null) {
			if (!securityHelper.isAdmin()
					&& !securityHelper.getAllowedOrgUnitsForCurrentUser()
							.contains(query.getOrgUnit())) {
				throw new IllegalArgumentException(
						"You don't have permission to search for changes in this OU");
			}
			crit = crit.add(Restrictions.eq("orgUnit", query.getOrgUnit()));
		}

		if (query.getFromDate() != null) {
			crit = crit.add(Restrictions.ge("changeDate", query.getFromDate()));
		}
		if (query.getToDate() != null) {

			Date endDate = query.getToDate();
			endDate.setHours(23);
			endDate.setMinutes(59);
			endDate.setSeconds(59);
			crit = crit.add(Restrictions.le("changeDate", endDate));
		}
		return crit;
	}

	/**
	 * Fills a host change with data from the XML contained within it
	 * 
	 * @param hc
	 * @return
	 */
	@Transactional(readOnly = true)
	public HostChange fillHostChange(HostChange hc) {
		if (null == hc.getHostXml())
			return hc;
		hc.setHost(xmlManager.unmarshall(Host.class, getXMLIDresolver(),
				new ByteArrayInputStream(hc.getHostXml().getBytes())));
		return hc;
	}

	@Transactional(readOnly = true)
	public void fillHostChanges(List<HostChange> changes) {
		List<InputStream> ises = new ArrayList<InputStream>();

		List<HostChange> changesToFill = new ArrayList<HostChange>();
		for (HostChange theChange : changes) {
			if (theChange.getHostXml() != null) {
				ises.add(new ByteArrayInputStream(theChange.getHostXml()
						.getBytes()));
				changesToFill.add(theChange);
			}
		}
		if (changesToFill.size() > 0) {
			List<Host> hosts = xmlManager.unmarshallAll(Host.class,
					getXMLIDresolver(), ises);
			Assert.isTrue(hosts.size() == changesToFill.size());
			for (int i = 0; i < changesToFill.size(); i++) {
				HostChange hc = changesToFill.get(i);
				Host h = hosts.get(i);
				Assert.isTrue(h.getId().equals(hc.getHostId()));
				hc.setHost(h);
			}
		}
	}

	public HostAlias findAlias(Long aliasId) {
		return (HostAlias) getSession().get(HostAlias.class, aliasId);
	}

	/**
	 * Gets host aliases for a specific name
	 * 
	 * @param name
	 * @return
	 */
	@Transactional(readOnly = true)
	public List<HostAlias> findAliases(HostName name) {
		return (List<HostAlias>) getSession().createCriteria(HostAlias.class)
				.add(Restrictions.eq("alias", name)).list();

	}

	/**
	 * Finds hosts matching a given name
	 * 
	 * by default the search includes aliases
	 * 
	 * @param name
	 *            the name or alias to search for
	 * @return
	 */
	@Transactional(readOnly = true)
	public Host findHost(HostName name) {
		return (Host) getSession().createCriteria(Host.class).add(
				Restrictions.eq("hostName", name)).uniqueResult();
	}

	/**
	 * 
	 * Returns a host based on its IP address.
	 * 
	 * IPs->host mappings are presumed to be unique .
	 * 
	 * @param ip
	 * @return the host associated with ip or null
	 */
	@Transactional(readOnly = true)
	public Host findHost(Inet4Address ip) {
		Session s = getSession();
		return (Host) s.createCriteria(Host.class).add(
				Restrictions.eq("ipAddress", ip)).uniqueResult();
	}

	/**
	 * Loads a {@link Host} by it's id
	 * 
	 * @param id
	 * @return the host
	 * @throws ObjectNotFoundException
	 *             if the host is not found
	 */
	@Transactional(readOnly = true)
	public Host loadHost(long id) {
		Session s = getSession();
		Host h = (Host) s.load(Host.class, Long.valueOf(id));

		return h;
	}

	/**
	 * Gets a {@link Host} by it's id
	 * 
	 * @param id
	 * @return the host or null if the host was not found
	 */
	@Transactional(readOnly = true)
	public Host getHost(long id) {
		Session s = getSession();
		Host h = (Host) s.get(Host.class, Long.valueOf(id));

		return h;
	}

	/**
	 * Finds a host by its MAC address
	 * 
	 * @param mac
	 * @return
	 */
	@Transactional(readOnly = true)
	public Host findHost(MacAddress mac) {
		Session s = getSession();
		return (Host) s.createQuery("from Host where macAddress=? ")
				.setParameter(0, mac).uniqueResult();

	}

	/**
	 * Finds a host by its <em>Primary</em> name (does not include aliases)
	 * 
	 * @param fqdn
	 *            the fqdn to search for
	 * @return a host or null
	 */
	@Transactional(readOnly = true)
	public Host findHost(String fqdn) {
		log.trace("running findhost (String) : " + fqdn);
		HostName name;
		try {
			name = getHostNameFromFQDN(fqdn);
			log.trace("looking for host with name " + name.toString());
			return findHost(name);
		} catch (Exception e) {
			return null;
		}

	}

	/**
	 * Finds hosts matching a given name including aliases
	 * 
	 * @param name
	 *            the name to search for
	 * 
	 * @return a list of hosts which match the given host name
	 */
	@Transactional(readOnly = true)
	public List<Host> findHostIncludingAliases(HostName name) {
		// log.trace("running findhost (HostName) : " + name.toString());

		Session s = getSession();

		return getHostsByIds(findHostIdsIncludingAliases(name), "ipAddress",
				true);

	}

	public List<Long> findHostIdsIncludingAliases(HostName name) {
		// log.trace("running findhost (HostName) : " + name.toString());

		Session s = getSession();
		List<Long> hostIds = s.createCriteria(Host.class).createAlias(
				"hostAliases", "alias", CriteriaSpecification.LEFT_JOIN).add(
				Restrictions.or(Restrictions.eq("hostName", name), Restrictions
						.eq("alias.alias", name))).setProjection(
				Projections.groupProperty("id")).setCacheable(true).list();
		return hostIds;

	}

	@Transactional(readOnly = true)
	public List<Host> findHostIncludingAliases(String fqdn) {
		// log.trace("running findhost (String) : " + fqdn);
		HostName name;
		try {
			name = getHostNameFromFQDN(fqdn);
			log.trace("looking for host with name " + name.toString());
			return findHostIncludingAliases(name);
		} catch (Exception e) {
			return null;
		}

	}

	/**
	 * Returns a list of hosts which match a given list of IDS ,
	 * 
	 * if a hostid does not exists it is silently ignored and not returned in
	 * the list
	 * 
	 * @param hostIds
	 * @return
	 */
	@Transactional(readOnly = true)
	public List<Host> findHostsByIdList(Collection<Long> hostIds) {
		Session s = getSession();
		return s.createCriteria(Host.class).add(Restrictions.in("id", hostIds))
				.setResultTransformer(
						CriteriaSpecification.DISTINCT_ROOT_ENTITY)
				.setCacheable(true).list();
	}

	/*
	 * Returns a subnet based on it's base (minIP) address
	 */
	@Transactional(readOnly = true)
	public Subnet findSubnetByBaseIP(Inet4Address ip) {
		Session s = getSession();
		Subnet sub = (Subnet) s.createQuery("from Subnet where minIP=? ")
				.setLong(0, TypeUtils.ipToInt(ip)).uniqueResult();
		if (sub != null) {
			return sub;
		} else {
			throw new NotFoundException("Subnet for ip address"
					+ ip.getHostAddress() + " was not found");
		}
	}

	/**
	 * Returns a list of subnets given a VLAN
	 * 
	 * @param vlan
	 * @return
	 */
	@Transactional(readOnly = true)
	public List<Subnet> findSubnetByVlan(Vlan vlan) {
		Session s = getSession();
		return s.createQuery("from Subnet where vlan=:vlan").setEntity("vlan",
				vlan).list();

	}

	/**
	 * Returns the subnet which contains the given IP.
	 * 
	 * @param ip
	 * @return
	 */
	@Transactional(readOnly = true)
	public Subnet findSubnetContainingIP(Inet4Address ip) {
		Session s = getSession();

		Subnet sub = (Subnet) s.createCriteria(Subnet.class).add(
				Restrictions.ge("maxIP", ip)).add(Restrictions.le("minIP", ip))
				.uniqueResult();

		return sub;
	}

	/**
	 * Returns subnets which touch a given rainge (i.e. they are either included
	 * in, or partly included in the given range.
	 * 
	 * @param minaddr
	 *            the minimum address to search
	 * @param maxaddress
	 *            the maximum address to search
	 * @return
	 */
	@Transactional(readOnly = true)
	public List<Subnet> findSubnetsTouchingRange(Inet4Address minaddr,
			Inet4Address maxaddr) {
		Session s = getSession();
		return s.createQuery(
				"FROM Subnet where (minIP  >= :minaddr  AND minIP <= :maxaddr) "
						+ " OR ( maxIP >= :minaddr AND maxIP <= :maxaddr)")
				.setParameter("minaddr", minaddr).setParameter("maxaddr",
						maxaddr).list();
	}

	/**
	 * Returns subnets which touch a given rainge (i.e. they are either included
	 * in, or partly included in the given range.
	 * 
	 * @param r
	 *            the range to search
	 * @return
	 */
	@Transactional(readOnly = true)
	public List<Subnet> findSubnetsTouchingRange(IPRange r) {
		return findSubnetsTouchingRange(r.getMinIP(), r.getMaxIP());
	}

	/**
	 * Loads a specific VLAN
	 * 
	 * @param id
	 * @return
	 */

	@Transactional(readOnly = true)
	public Vlan findVlan(Long id) {
		Session s = getSession();

		Vlan v = (Vlan) s.get(Vlan.class, Long.valueOf(id));
		if (v == null)
			throw new NotFoundException("Vlan with ID " + id + " was not found");

		return v;
	}

	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public Set<HostFlag> getAllHostFlags() {
		Session s = getSession();
		List l = s.createQuery("from HostFlag").list();
		Set<HostFlag> opset = new HashSet<HostFlag>();
		opset.addAll(l);
		return opset;
	}

	/**
	 * Returns all hosts contained in a given subnet (range)
	 * 
	 * @param rs
	 * @return
	 */
	@Transactional(readOnly = true)
	public List<Host> getAllHostsInRange(IPRange rs) {
		Session s = getSession();

		List<Host> l = s.createCriteria(Host.class).add(
				Restrictions.ge("ipAddress", rs.getMinIP())).add(
				Restrictions.le("ipAddress", rs.getMaxIP())).addOrder(
				Order.asc("ipAddress")).setResultTransformer(
				CriteriaSpecification.DISTINCT_ROOT_ENTITY).list();

		return l;

	}

	/**
	 * Returns all hosts contained in a given subnet (range)
	 * 
	 * @param rs
	 * @return
	 */
	@Transactional(readOnly = true)
	public List<Inet4Address> getUsedIPsInRange(IPRange rs) {
		Session s = getSession();

		List<Inet4Address> l = s.createCriteria(Host.class).add(
				Restrictions.ge("ipAddress", rs.getMinIP())).add(
				Restrictions.le("ipAddress", rs.getMaxIP())).addOrder(
				Order.asc("ipAddress")).setProjection(
				Projections.property("ipAddress")).setResultTransformer(
				CriteriaSpecification.DISTINCT_ROOT_ENTITY).list();

		return l;

	}

	/**
	 * Returns all vlans
	 * 
	 * @return
	 */
	@Transactional(readOnly = true)
	public List<Vlan> getAllVlans() {
		Session s = getSession();
		return s.createCriteria(Vlan.class).addOrder(Order.asc("number"))
				.setResultTransformer(
						CriteriaSpecification.DISTINCT_ROOT_ENTITY).list();
	}

	@Transactional(readOnly = true)
	public BaseCompositeCommand getBaseCommandForStoredCommand(
			StoredCommand command) throws UnresolvedEntityException {
		Assert.notNull(command.getCommandXML());
		try {
			JAXBContext ctx = JAXBContext.newInstance("edu.bath.soak");

			Unmarshaller um = ctx.createUnmarshaller();
			um.setProperty(IDResolver.class.getName(), getXMLIDresolver());
			BaseCompositeCommand bcc = (BaseCompositeCommand) um
					.unmarshal(new ByteArrayInputStream(command.getCommandXML()
							.getBytes()));

			return bcc;
		} catch (JAXBException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Retrives version X of host Y, If the version is not current, it fetches
	 * the version from the change history, returns null if it isn't found.
	 * 
	 * @param hostId
	 *            the host ID to search for
	 * @param version
	 *            the version of the host to retrieve
	 * @return
	 */
	public Host getHostAtVersion(Long hostId, long version) {

		Host h = null;
		h = getHost(hostId);

		if (h != null && h.getVersion() == version) {
			return h;
		}

		HostChange hc = getHostChangeAtVersion(hostId, version);
		if (hc != null) {
			return hc.getHost();
		}
		return null;
	}

	/**
	 * Stream optimized version of
	 * 
	 * @param hostIdAndVersions
	 *            a collection of hostId -> version mappings
	 * @return
	 */
	public Collection<HostChange> getHostChangesAtVersions(
			Collection<Tuple<Long, Long>> hostIdAndVersions) {
		Session s = getSession();
		Criteria c = s.createCriteria(HostChange.class);

		Disjunction or = Restrictions.disjunction();
		for (Tuple<Long, Long> hostIdVersionPair : hostIdAndVersions) {
			or.add(Restrictions.and(Restrictions.eq("hostId", hostIdVersionPair
					.getFrom()), Restrictions.eq("version", hostIdVersionPair
					.getTo())));
		}
		c.add(or);

		List<HostChange> changes = c.list();
		fillHostChanges(changes);
		return changes;

	}

	@Transactional(readOnly = true)
	public HostChange getHostChange(long changeId) {
		Session s = getSession();
		HostChange hc = (HostChange) s.load(HostChange.class, changeId);
		if (hc != null) {
			return fillHostChange(hc);
		}

		return null;

	}

	@Transactional(readOnly = true)
	public HostChange getHostChangeAtVersion(long hostId, long version) {
		Session s = getSession();
		HostChange hc = (HostChange) s.createCriteria(HostChange.class).add(
				Restrictions.eq("hostId", hostId)).add(
				Restrictions.eq("version", version)).uniqueResult();

		if (hc != null) {
			return fillHostChange(hc);
		}
		return null;

	}

	@Transactional(readOnly = true)
	public List<HostChange> getHostChanges(Long hostId) {
		Session s = getSession();
		List<HostChange> changes = s.createCriteria(HostChange.class).add(
				Restrictions.eq("hostId", hostId)).addOrder(
				Order.asc("changeDate")).setResultTransformer(
				CriteriaSpecification.DISTINCT_ROOT_ENTITY).list();
		for (HostChange change : changes) {
			fillHostChange(change);
		}
		return changes;
	}

	/**
	 * Gets a specific host class by name
	 * 
	 * @param name
	 *            the name of the host class to get.
	 * @return
	 */
	@Transactional(readOnly = true)
	public HostClass getHostClassById(String id) {
		Session s = getSession();
		HostClass nc = (HostClass) s.get(HostClass.class, id);
		return nc;
	}

	/**
	 * Gets all available Host classes
	 * 
	 * @return
	 */
	@Transactional(readOnly = true)
	public List<HostClass> getHostClasses() {
		Session s = getSession();
		List<HostClass> hcs = s.createCriteria(HostClass.class).addOrder(
				Order.asc("name")).setResultTransformer(
				CriteriaSpecification.DISTINCT_ROOT_ENTITY).list();
		return hcs;
	}

	/**
	 * Returns a specific host flag by name
	 * 
	 * @param name
	 * @return
	 */
	@Transactional(readOnly = true)
	public HostFlag getHostFlagByName(String name) {
		Session s = getSession();
		List l = s.createQuery("from HostFlag where flag=?").setString(0, name)
				.list();
		if (l.size() > 0) {
			return (HostFlag) l.get(0);
		} else {
			return null;
		}
	}

	public Session getTheSession() {
		return super.getSession();
	}

	/**
	 * Returns a host object which is suitable for editing
	 * 
	 * Evicts the host and it's dependent properties from the session.
	 * 
	 * @param id
	 * @return
	 */
	@Transactional(readOnly = true)
	public Host getHostForEditing(long id) {
		Host h = loadHost(id);
		Session s = getSession();

		for (HostAlias a : h.getHostAliases())
			s.evict(a);
		for (ExtendedHostInfo a : h.getConfigSettings().values())
			s.evict(a);
		s.evict(h);
		return h;
	}

	/**
	 * Builds a HostName object from a fully qualified domain name
	 * 
	 * @param hostname
	 *            the host name to parse, this must be absolute, the host part
	 *            of the host name will be extracted and the NameDomain
	 *            corresponding the remainder of the string will be looked up
	 * @return a HostName object corresponding to the FQDN
	 * @throws IllegalArgumentException
	 *             if the name is not absolute, or the domain part does not
	 *             correspond to an exitant name domain
	 */
	@Transactional(readOnly = true)
	public HostName getHostNameFromFQDN(String hostname) {
		HostName hn = new HostName();
		Assert.isTrue(hostname.endsWith("."), "Host name is not absolute");
		int dotidx = hostname.indexOf(".");
		String hostpart = hostname.substring(0, dotidx);
		String domainpart = hostname.substring(dotidx);
		NameDomain d = getNameDomainBySuffix(domainpart);
		if (d == null) {
			throw new IllegalArgumentException(domainpart
					+ " is not a registered name domain");
		}
		hn.setDomain(d);
		hn.setName(hostpart);
		if (d != null && StringUtils.hasText(hostpart))
			return hn;
		else
			throw new IllegalArgumentException("Could not get name from string");
	}

	public List<Host> getHostsByIds(Collection<Long> hostIds, String orderBy,
			boolean asc) {
		Session s = getSession();
		Criteria c = s.createCriteria(Host.class);

		if (StringUtils.hasText(orderBy) && orderBy.contains("liu")) {
			c = c.createAlias("lastUsageInfo", "liu",
					CriteriaSpecification.LEFT_JOIN);

		}
		if (hostIds == null || hostIds.isEmpty()) {
			return new LinkedList<Host>();
		}
		if (StringUtils.hasText(orderBy)) {
			if (asc) {
				c = c.addOrder(Order.asc(orderBy));
			} else {
				c = c.addOrder(Order.desc(orderBy));
			}
		} else {
			c = c.addOrder(Order.asc("ipAddress"));
		}
		c.add(Restrictions.in("id", hostIds));
		return (List<Host>) c.setResultTransformer(
				CriteriaSpecification.DISTINCT_ROOT_ENTITY).list();
	}

	HostChange getLastHostChangeForHost(Long hostId) {
		Session s = getSession();

		List<HostChange> hcs = s.createCriteria(HostChange.class).add(
				Restrictions.eq("hostId", hostId)).addOrder(
				Order.desc("version")).setMaxResults(1).list();
		if (hcs.size() == 0)
			return null;
		else
			return hcs.get(0);
	}

	/***************************************************************************
	 * Look up a name domain given a suffix. The suffix should start and end
	 * with a "."
	 * 
	 * @param suffix
	 *            t
	 * @return a name domain object specifiedy by the suffix
	 */
	@Transactional(readOnly = true)
	public NameDomain getNameDomainBySuffix(String suffix) {
		Session s = getSession();
		return (NameDomain) s.get(NameDomain.class, suffix);
	}

	/**
	 * 
	 * @return a list of stored named domains
	 */
	@Transactional(readOnly = true)
	public List<NameDomain> getNameDomains() {
		Session s = getSession();
		return s.createCriteria(NameDomain.class).addOrder(Order.asc("suffix"))
				.setResultTransformer(
						CriteriaSpecification.DISTINCT_ROOT_ENTITY)
				.setCacheable(true).list();
	}

	/**
	 * Gets a specific network class by name
	 * 
	 * @param name
	 *            the name of the network class to get.
	 * @return
	 */
	@Transactional(readOnly = true)
	public NetworkClass getNetworkClassById(String id) {
		Session s = getSession();
		NetworkClass nc = (NetworkClass) s.get(NetworkClass.class, id);
		return nc;
	}

	// Session getSession() {
	// return sessionFactory.getCurrentSession();
	// }
	//
	// public SessionFactory getSessionFactory() {
	// return
	// sessionFactory;
	// }

	/**
	 * Gets all available network classes
	 * 
	 * @return a list of network class descriptions
	 */
	@Transactional(readOnly = true)
	public List<NetworkClass> getNetworkClasses() {
		Session s = getSession();
		List<NetworkClass> ncs = s.createCriteria(NetworkClass.class).addOrder(
				Order.asc("name")).setResultTransformer(
				CriteriaSpecification.DISTINCT_ROOT_ENTITY).list();
		return ncs;
	}

	/**
	 * Returns the next change for the given host changed by the given change
	 * 
	 * @param change
	 * @return the next host change or null if the passed change was the last
	 *         change
	 */
	@Transactional(readOnly = true)
	public HostChange getNextHostChange(HostChange hc) {
		Session s = getSession();
		List<HostChange> hcl = s.createCriteria(HostChange.class).addOrder(
				Order.asc("id")).add(Restrictions.eq("hostId", hc.getHostId()))
				.add(Restrictions.gt("id", hc.getId())).setFetchSize(1).list();
		if (hcl.size() == 0)
			return null;
		return fillHostChange(hcl.get(0));

	}

	@Transactional(readOnly = true)
	public OrgUnit getOrgUnitById(String id) {
		return (OrgUnit) getSession().createCriteria(OrgUnit.class).add(
				Restrictions.eq("id", id)).uniqueResult();
	}

	@Transactional(readOnly = true)
	public List<OrgUnitPrincipalMapping> getOrgUnitPrincipalMappingsForGroups(
			Collection<String> groups) {
		if (groups.isEmpty()) {
			return new ArrayList<OrgUnitPrincipalMapping>();
		}

		return getSession().createCriteria(OrgUnitPrincipalMapping.class).add(
				Restrictions.in("principal", groups)).add(
				Restrictions.eq("type", PrincipalType.GROUP)).list();
	}

	@Transactional(readOnly = true)
	public List<OrgUnitPrincipalMapping> getOrgUnitPrincipalMappingsForOU(
			OrgUnit ou) {
		return getSession().createCriteria(OrgUnitPrincipalMapping.class).add(
				Restrictions.eq("orgUnit", ou)).list();
	}

	@Transactional(readOnly = true)
	public List<OrgUnitPrincipalMapping> getOrgUnitPrincipalMappingsForUser(
			String user) {
		return getSession().createCriteria(OrgUnitPrincipalMapping.class).add(
				Restrictions.eq("principal", user)).add(
				Restrictions.eq("type", PrincipalType.USER)).list();
	}

	@Transactional(readOnly = true)
	public List<OrgUnit> getOrgUnits() {
		return getSession().createCriteria(OrgUnit.class).addOrder(
				Order.asc("name")).list();
	}

	/**
	 * Returns the previous change for the given host changed by the given
	 * change
	 * 
	 * @param change
	 * @return the previous host change or null if the passed change was the
	 *         first change
	 */
	@Transactional(readOnly = true)
	public HostChange getPreviousHostChange(HostChange hc) {
		Session s = getSession();

		List<HostChange> hcl = s.createCriteria(HostChange.class).addOrder(
				Order.desc("id"))
				.add(Restrictions.eq("hostId", hc.getHostId())).add(
						Restrictions.lt("id", hc.getId())).setFetchSize(1)
				.list();
		if (hcl.size() == 0)
			return null;
		return fillHostChange(hcl.get(0));

	}

	public StoredCommand getStoredCommand(String id) {
		return (StoredCommand) getSession().load(StoredCommand.class, id);
	}

	/**
	 * Loads a subnet by ID
	 * 
	 * @param id
	 * @return
	 * 
	 */
	@Transactional(readOnly = true)
	public Subnet getSubnet(long id) {
		Session s = getSession();
		Subnet h = (Subnet) s.get(Subnet.class, Long.valueOf(id));
		if (h == null)
			throw new NotFoundException("Subnet with ID " + id
					+ " was not found");
		return h;
	}

	/**
	 * gets all gateway hosts for a collection of subnets.
	 * 
	 * @param rs
	 *            collection of subnets to get gateways for , must not be null
	 * @return a map from subnet to Host where host is the gateway host for the
	 *         subnet (where present)
	 */
	public Map<Subnet, Host> getSubnetGateways(Collection<Subnet> rs) {
		Assert.notNull(rs);
		HashMap<Inet4Address, Subnet> revmap = new HashMap<Inet4Address, Subnet>();
		LinkedList<Inet4Address> gwIps = new LinkedList<Inet4Address>();
		HashMap<Subnet, Host> result = new HashMap<Subnet, Host>();

		if (rs.size() == 0)
			return result;
		for (Subnet sub : rs) {
			if (null != sub.getGateway()) {
				revmap.put(sub.getGateway(), sub);
				gwIps.add(sub.getGateway());
			}
		}
		List<Host> hosts = getSession().createCriteria(Host.class).add(
				Restrictions.in("ipAddress", gwIps)).setResultTransformer(
				CriteriaSpecification.DISTINCT_ROOT_ENTITY).list();

		for (Host h : hosts) {
			result.put(revmap.get(h.getIpAddress()), h);
		}

		return result;
	}

	/**
	 * Gets all subnets, By default this is ordered by the IP subnet in
	 * numerical order
	 * 
	 * @return
	 */
	@Transactional(readOnly = true)
	public List<Subnet> getSubnets() {
		Session s = getSession();
		List<Subnet> subnets = s.createCriteria(Subnet.class).addOrder(
				Order.asc("minIP")).setResultTransformer(
				CriteriaSpecification.DISTINCT_ROOT_ENTITY).setCacheable(true)
				.list();
		return subnets;
	}

	public Vlan getVlan(long id) {
		Session s = getSession();

		return (Vlan) s.load(Vlan.class, (Long) id);
	}

	public Vlan getVlanByNumber(int id) {
		Session s = getSession();

		return (Vlan) s.createCriteria(Vlan.class).add(
				Restrictions.eq("number", id)).uniqueResult();
	}

	public IDResolver getXMLIDresolver() {

		List<SoakXMLIDResolver> resolverList = new ArrayList<SoakXMLIDResolver>(
				xmlIDResolvers);
		resolverList.add(baseDBResolver);
		MultipleEntityIDResolver mir = new MultipleEntityIDResolver(
				resolverList);
		// / fix this
		return mir;
	}

	public List<SoakXMLIDResolver> getXmlIDResolvers() {
		return xmlIDResolvers;
	}

	public void marshallHostToXML(Host h, OutputStream bos) {
		try {
			JAXBContext ctx = JAXBContext.newInstance("edu.bath.soak");
			Marshaller m = ctx.createMarshaller();
			m.marshal(h, bos);
		} catch (JAXBException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Snapshots the current database state of a host in the database.
	 * 
	 * 
	 * This can be recovered at a later date.
	 * 
	 * @param hostId
	 *            the Host ID to snapshot
	 * @param changeType
	 * @param commandDescription
	 * @param comment
	 * @param userId
	 */

	HostChange recordCurrentHostVersion(Host newVersion, String commandId,
			HostChange.ChangeType changeType, String commandDescription,
			String comment) {
		Assert.notNull(newVersion);
		Assert.notNull(newVersion.getId());
		Host host = loadHost(newVersion.getId());
		Assert.notNull(host);

		HostChange change = new HostChange();
		change.setCommandId(commandId);
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		marshallHostToXML(host, bos);
		change.setCommandDescription(commandDescription);
		change.setHostXml(new String(bos.toByteArray()));
		change.setChangeType(changeType);
		change.setUserId(securityHelper.getCurrentUserId());
		change.setVersion(host.getVersion());
		change.setChangeComments(comment);

		change.setHostId(host.getId());
		change.setHostName(newVersion.getHostName());
		change.setOrgUnit(newVersion.getOwnership().getOrgUnit());
		change.setIpAddress(newVersion.getIpAddress());
		change.setChangeDate(new Date());
		Session s = getSession();
		s.save(change);
		return change;

	}

	/**
	 * notemises the creation of a host in the log, does not record any state
	 * about the host itself (this is recorded in later edits)
	 * 
	 * @param hostId
	 *            the ID of the host to save
	 * @param commandId
	 *            The command ID associated with this edit
	 * @param comment
	 *            achange
	 * @param userId
	 */
	public void recordHostCreation(Long hostId, String commandId,
			String commandDescription, String comment) {
		Assert.notNull(hostId);

		HostChange hostChange = new HostChange();
		Host h = loadHost(hostId);

		Assert.notNull(h);

		hostChange.setCommandDescription(commandDescription);
		hostChange.setCommandId(commandId);
		hostChange.setHostId(hostId);
		hostChange.setOrgUnit(h.getOwnership().getOrgUnit());
		hostChange.setUserId(securityHelper.getCurrentUserId());
		hostChange.setHostName(h.getHostName());
		hostChange.setIpAddress(h.getIpAddress());
		hostChange.setChangeComments(comment);
		hostChange.setChangeDate(new Date());
		hostChange.setChangeType(ChangeType.ADD);
		hostChange.setVersion(-1);
		getSession().save(hostChange);

	}

	public void registerXmlIDResolver(SoakXMLIDResolver resolver) {
		xmlIDResolvers.add(resolver);
	}

	/***************************************************************************
	 * Saves a host (with an empty change comment and an empty command comment
	 * 
	 * @see NetDAO#saveHost(Host, String, String)
	 * 
	 * @param host
	 *            the host to save
	 * @param commandId
	 *            command identifier (may be null)
	 * 
	 */
	public void saveHost(Host host, String commandId) {
		saveHost(host, commandId, null);
	}

	/***************************************************************************
	 * Saves a host (with an empty change comment and an empty command comment
	 * 
	 * @see NetDAO#saveHost(Host, String, String)
	 * 
	 * @param host
	 *            the host to save
	 * @param commandId
	 *            command identifier (may be null)
	 * @param changeComment
	 *            comment associated with this change
	 * 
	 */
	public void saveHost(Host host, String commandId, String changeComment) {
		saveHost(host, commandId, changeComment, null);
	}

	/**
	 * Single point for saving host changes;
	 * 
	 * every time a host is changed, the previous state is remembered in the
	 * HostChanges table with the appropriate version
	 * 
	 * @param host
	 *            the host to save
	 * @param changeComment
	 *            a comment from the user describing the action of this change
	 * @param commandDescription
	 *            a description of the command to save
	 */
	public void saveHost(Host host, String commandId, String changeComment,
			String commandDescription) {
		// log.trace("Saving host with ID " + h.getId());
		Assert.isTrue((null == host.getId()) == (null == host.getVersion()),
				"inconsistency between host version and host id");
		Session s = getSession();
		Assert.isTrue(!s.contains(host),
				"You should only be editing detached hosts");

		boolean hostExists = false;

		long oldVersion = -1L;
		/**
		 * We assume that the last committer wins, in order to ensure
		 * consistency we need to ensure that the saved host version is an
		 * increment of the latest copy of the host
		 * 
		 * in the case of an undo it may be the case that the host is a previous
		 * version
		 */
		if (host.getId() != null) {// try and find the appropriate version for
			// this
			try {

				Host existing = loadHost(host.getId());
				Assert.notNull(existing.getVersion());
				oldVersion = existing.getVersion();
				log.debug("Saving host with existing version " + oldVersion
						+ " - " + host.getVersion());

				hostExists = true;
			} catch (ObjectNotFoundException e) {

				HostChange oldHc = getLastHostChangeForHost(host.getId());
				Assert.notNull(oldHc);
				oldVersion = oldHc.getVersion();
				hostExists = false;
			}
		} else {
			hostExists = false;
			oldVersion = -1L;
		}
		if (hostExists) {

			recordCurrentHostVersion(host, commandId, ChangeType.CHANGE,
					commandDescription, changeComment);

		}

		host.setVersion(oldVersion + 1);
		int i = 0;
		// for(HostAlias ha: host.getHostAliases()){
		// ha.setIdx(i++);
		// }
		if (hostExists) {
			log.trace("existing host exists, running a merge");
			Host existing = loadHost(host.getId());
			if (!existing.getHostAliases().equals(host.getHostAliases())) {
				for (HostAlias existingAlias : existing.getHostAliases()) {
					s.delete(existingAlias);
				}
				s.refresh(existing);
				int idx = 0;
				for (HostAlias newAliases : host.getHostAliases()) {
					newAliases.setId(null);
					newAliases.setIdx(idx++);
				}
			}
			s.merge(host);
		} else {
			s.save(host);
			recordHostCreation(host.getId(), commandId, commandDescription,
					changeComment);

		}
		s.flush();
	}

	public void saveHostClass(HostClass hc) {
		getSession().saveOrUpdate(hc);
		getSession().flush();
	}

	public void saveHostFlag(HostFlag hf) {
		getSession().save(hf);
		getSession().flush();
	}

	// public void setSessionFactory(SessionFactory sessionFactory) {
	// this.sessionFactory = sessionFactory;
	// }

	/**
	 * Saves a set of hosts
	 * 
	 * @param hosts
	 *            a collection of hosts to save, these may exist already or may
	 *            be new hosts
	 * @param commandId
	 *            the command ID associated with this command
	 * @param commandDescription
	 *            a description of the command being executed
	 * @param comment
	 *            a comment
	 */
	public void saveHosts(Collection<Host> hosts, String commandId,
			String commandDescription, String comment) {
		// log.trace("Saving host with ID " + h.getId());
		Session s = getSession();
		for (Host h : hosts) {
			int idx = 0;
			for (HostAlias newAliases : h.getHostAliases()) {
				newAliases.setId(null);
				newAliases.setIdx(idx++);
			}
			if (h.getId() != null) {

				long oldVersion = -1L;
				try {
					Host existing = loadHost(h.getId());
					oldVersion = existing.getVersion();

					if (!existing.getHostAliases().equals(h.getHostAliases())) {
						for (HostAlias existingAlias : existing
								.getHostAliases()) {
							s.delete(existingAlias);
						}
					}
					s.refresh(existing);
				} catch (ObjectNotFoundException e) {
					HostChange oldHc = getLastHostChangeForHost(h.getId());
					if (oldHc != null) {
						oldVersion = oldHc.getVersion();
					}
				}

				h.setVersion(oldVersion + 1);
				recordCurrentHostVersion(h, null, ChangeType.CHANGE,
						"Bulk save", comment);

				log.trace("existing host exists, running a merge");
				// h.setVersion(findHost(h.getId()).getVersion() + 1);
				s.merge(h);
			} else {
				h.setVersion(0L);
				s.save(h);
				recordHostCreation(h.getId(), commandId, commandDescription,
						comment);

			}

		}
		s.flush();
	}

	public void saveNameDomain(NameDomain nameDomain) {
		getSession().saveOrUpdate(nameDomain);
		getSession().flush();
	}

	public void saveNetworkClass(NetworkClass nc) {
		getSession().save(nc);
		getSession().flush();

	}

	public void saveOrganisationalUnitPrincipalMapping(
			OrgUnitPrincipalMapping ogr) {
		getSession().save(ogr);
		getSession().flush();

	}

	public void saveOrgUnit(OrgUnit ou) {
		getSession().save(ou);
		getSession().flush();
	}

	/**
	 * Save a stored command
	 * 
	 * @param sc
	 */
	public void saveStoredCommand(StoredCommand sc) {
		getSession().save(sc);
		getSession().flush();
	}

	public void saveSubnet(Subnet subnet) {
		Session s = getSession();
		s.save(subnet);
		getSession().flush();

	}

	public void saveVlan(Vlan v) {
		getSession().save(v);
		getSession().flush();
	}

	public SearchResult<HostChange> searchHostChanges(HostChangeQuery query) {
		return searchHostChanges(query, false);
	}

	/***************************************************************************
	 * Searches change results,
	 * 
	 * @param query
	 * @param fillHosts
	 *            should host changes be filled from data
	 * @return
	 */
	public SearchResult<HostChange> searchHostChanges(HostChangeQuery query,
			boolean fillHosts) {
		Session s = getSession();
		Criteria crit = s.createCriteria(HostChange.class);
		crit = expandSearchHostChangesCriteria(query, crit);
		int numResults = ((Integer) (crit.setProjection(
				Projections.countDistinct("id")).list().get(0))).intValue();

		crit = s.createCriteria(HostChange.class);
		crit = expandSearchHostChangesCriteria(query, crit);
		crit = setupSearchQuery(query, crit);

		crit.setProjection(Projections.distinct(Projections.property("id")));
		List<Long> ids = crit.list();

		SearchResult<HostChange> result = new SearchResult<HostChange>(query);
		result.setFirstResultOffset(query.getFirstResult());
		result.setTotalResults(numResults);
		if (ids.size() > 0) {
			Criteria fetchChanges = s.createCriteria(HostChange.class);
			fetchChanges = setupSearchOrder(query, fetchChanges);

			fetchChanges.add(Restrictions.in("id", ids));
			fetchChanges
					.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);

			result.setResults(fetchChanges.list());

			if (fillHosts) {
				fillHostChanges(result.getResults());
			}
		} else {
			result.setResults(new LinkedList<HostChange>());
		}
		return result;
	}

	/**
	 * Executes a host search query returning a result of host Ids rather than
	 * filled hosts
	 * 
	 * @param query
	 * @param hostIds
	 * @return
	 */
	public SearchResult<Long> searchHostsToIds(HostSearchQuery query,
			Set<Long> hostIds) {
		log.trace("searching " + query);
		Session s = getSession();
		SearchResult<Long> res = new SearchResult<Long>(query);

		res.setTotalResults(countResultsForHostSearchQuery(query, hostIds));
		Criteria c;

		// recompute the search
		c = s.createCriteria(Host.class);

		ProjectionList pl = Projections.projectionList();
		pl.add(Projections.distinct(Projections.property("id")));
		// pl.add(Projections.property("id"), "host_id");

		c = expandHostSearchCriteria(query, c);

		String[] orderByFields;

		// apply the ordering
		if (StringUtils.hasText(query.getOrderBy())
				&& query.getOrderBy().equals("hostName")) {
			orderByFields = new String[] { "hostName.domain.suffix",
					"hostName.name" };
		} else if (StringUtils.hasText(query.getOrderBy())
				&& query.getOrderBy().equals("location")) {
			orderByFields = new String[] { "location.building", "location.room" };
		} else {
			if (StringUtils.hasText(query.getOrderBy()))
				orderByFields = new String[] { query.getOrderBy() };
			else
				orderByFields = new String[] { "ipAddress" };

		}
		for (String orderBy : orderByFields) {
			pl.add((Projections.property(orderBy)));
			if (query.isAscending()) {
				c = c.addOrder(Order.asc(orderBy));
			} else {
				c = c.addOrder(Order.desc(orderBy));
			}
		}
		c = c.setProjection(pl);
		if (hostIds != null) {
			if (hostIds.isEmpty()) {
				// special case as hibernate doesn't like "IN" with empty sets,
				// we're getting no results anyway so...
				return res;
			} else {
				c = c.add(Restrictions.in("id", hostIds));
			}
		}
		if (-1 != query.getFirstResult())
			c.setFirstResult(query.getFirstResult());

		if (-1 != query.getMaxResults()) {
			c.setMaxResults(query.getMaxResults());
		}
		List<Object[]> hostResults = c.list();
		List<Long> gotHostIds = new ArrayList<Long>();

		for (Object[] resobject : hostResults) {
			gotHostIds.add((Long) resobject[0]);
		}
		res.setResults(gotHostIds);
		res.setFirstResultOffset(query.getFirstResult());
		return res;
	}

	public SearchResult<Host> searchHosts(HostSearchQuery query) {
		return searchHostsInIdSet(query, null);
	}

	/**
	 * Executes a given host search
	 * 
	 * @param query
	 * @return
	 */
	public SearchResult<Host> searchHostsInIdSet(HostSearchQuery query,
			Set<Long> hostIds) {
		SearchResult<Host> res = new SearchResult<Host>(query);
		SearchResult<Long> resIds = searchHostsToIds(query, hostIds);
		res.setResults(getHostsByIds(resIds.getResults(), query.getOrderBy(),
				query.isAscending()));
		res.setTotalResults(resIds.getTotalResults());
		res.setFirstResultOffset(query.getFirstResult());
		for (SearchExpander expander : searchExpanders) {
			expander.postProcessResults(res);
		}
		return res;
	}

	public void prepareSearchObject(HostSearchQuery query) {
		for (SearchExpander expander : searchExpanders) {
			expander.prepareSearchObject(query);
		}
	}

	/**
	 * Search stored commands
	 * 
	 * @param search
	 * @return
	 */
	@Transactional(readOnly = true)
	public SearchResult<StoredCommand> searchStoredCommands(
			SearchStoredCommandsCmd search) {
		SearchResult<StoredCommand> result = new SearchResult<StoredCommand>(
				search);
		Session s = getSession();
		log.debug("Searching for stored commands belonging to "
				+ search.getUserName());
		if (!StringUtils.hasText(search.getOrderBy())) {
			search.setOrderBy("changeTime");
			search.setAscending(true);
		}

		Criteria crit = s.createCriteria(StoredCommand.class);
		if (search.isAscending()) {
			crit = crit.addOrder(Order.asc(search.getOrderBy()));
		} else {
			crit = crit.addOrder(Order.desc(search.getOrderBy()));
		}

		if (search.getUserName() != null) {
			crit.add(Restrictions.eq("user", search.getUserName()));
		}
		result.setTotalResults(((Integer) (crit.setProjection(
				Projections.countDistinct("id")).list().get(0))).intValue());

		crit = s.createCriteria(StoredCommand.class);
		if (search.isAscending()) {
			crit = crit.addOrder(Order.asc(search.getOrderBy()));
		} else {
			crit = crit.addOrder(Order.desc(search.getOrderBy()));
		}

		if (search.getUserName() != null) {
			crit.add(Restrictions.eq("user", search.getUserName()));
		}

		crit = setupSearchQuery(search, crit);

		crit.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
		result.setResults(crit.list());
		result.setFirstResultOffset(search.getFirstResult());

		return result;
	}

	/**
	 * Searches for subnets matching the given query
	 * 
	 * @param query
	 * @return
	 */
	public SearchResult<Subnet> searchSubnets(SubnetQuery query) {

		log.trace("searching " + query);
		Session s = getSession();
		Criteria c = s.createCriteria(Subnet.class).setResultTransformer(
				CriteriaSpecification.DISTINCT_ROOT_ENTITY).createAlias("vlan",
				"vlan", CriteriaSpecification.LEFT_JOIN).createAlias(
				"networkClass", "networkClass");
		c = setupSearchQuery(query, c);

		if (StringUtils.hasText(StringUtils.trimWhitespace(query
				.getSearchTerm()))) {
			String searchTerm = query.getSearchTerm();
			searchTerm = StringUtils.trimWhitespace(searchTerm);

			String[] searchTerms = searchTerm.split("\\s+");
			for (String st : searchTerms) {
				try {
					Inet4Address ip = TypeUtils.txtToIP(st);
					if (ip != null) {
						log.trace("Search term " + st
								+ "matches as IP address " + ip);
						c = c.add(Restrictions.ge("minIP", ip)).add(
								Restrictions.le("maxIP", ip));
						continue;
					}
				} catch (Exception e) {
				}
				try {
					if (st.matches("nc:.*")) {
						String ncId = st.substring(3);
						NetworkClass nc = getNetworkClassById(ncId);
						if (nc != null) {
							c.add(Restrictions.eq("networkClass", nc));
							continue;
						}
					}
				} catch (Exception e) {

				}
				c = c.add(Restrictions.disjunction().add(
						Restrictions.ilike("name", "%" + st + "%")).add(
						Restrictions.ilike("description", "%" + st + "%"))
						.add(
								Restrictions.ilike("networkClass.name", "%"
										+ st + "%")).add(
								Restrictions.ilike("minIPTxt", st + "%")));

			}

		}
		SearchResult<Subnet> sr = new SearchResult<Subnet>(query);
		sr.setFirstResultOffset(0);
		sr.setResults(c.list());
		return sr;

	}

	@Required
	public void setSecurityHelper(SecurityHelper securityHelper) {
		this.securityHelper = securityHelper;
	}

	public static Criteria setupSearchOrder(SearchQuery sq, Criteria crit) {
		String orderField = sq.getOrderBy();
		if (StringUtils.hasText(orderField)) {
			if (sq.isAscending()) {
				crit = crit.addOrder(Order.asc(orderField));
			} else {
				crit = crit.addOrder(Order.desc(orderField));
			}
		}
		return crit;
	}

	public static Criteria setupSearchQuery(SearchQuery hq, Criteria crit) {
		if (hq.isGetAll()) {
			crit = crit.setFirstResult(0);
		} else {
			crit = crit.setFirstResult(hq.getFirstResult());
			crit = crit.setMaxResults(hq.getMaxResults());
		}

		crit = setupSearchOrder(hq, crit);

		return crit;
	}

	public void registerSearchExpander(SearchExpander expander) {
		searchExpanders.add(expander);
	}

	public void setXmlIDResolvers(List<SoakXMLIDResolver> xmlIDResolvers) {
		this.xmlIDResolvers = xmlIDResolvers;
	}

	@Required
	public void setXmlManager(SoakXMLManager xmlManager) {
		this.xmlManager = xmlManager;
	}

}
