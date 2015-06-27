package edu.bath.soak.web.admin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import edu.bath.soak.PluginManager;
import edu.bath.soak.SoakPlugin;
import edu.bath.soak.SoakWebPlugin;
import edu.bath.soak.model.OrgUnit;
import edu.bath.soak.model.OrgUnitPrincipalMapping;
import edu.bath.soak.net.model.HostClass;
import edu.bath.soak.net.model.NameDomain;
import edu.bath.soak.net.model.NetDAO;
import edu.bath.soak.net.model.Subnet;
import edu.bath.soak.net.query.HostSearchQuery;
import edu.bath.soak.security.SecurityHelper;
import edu.bath.soak.util.OrderedComparator;
import edu.bath.soak.web.AdminConsoleInfoProvider;

public class AdminController extends MultiActionController implements
		AdminConsoleInfoProvider {
	NetDAO hostsDAO;
	SecurityHelper securityHelper;
	PluginManager pluginManager;
	PluginManager webPluginManager;

	public ModelAndView index(HttpServletRequest request,
			HttpServletResponse response) {

		Map<String, Object> model = new HashMap<String, Object>();
		TreeSet<AdminConsoleObject> adminObjects = new TreeSet<AdminConsoleObject>(
				new OrderedComparator());

		for (SoakPlugin plugin : webPluginManager.getPlugins()) {
			if (plugin instanceof SoakWebPlugin) {
				for (AdminConsoleInfoProvider provider : ((SoakWebPlugin) plugin)
						.getConsoleInfoInfoProviders()) {
					adminObjects.addAll(provider.getAdminConsoleInfo());

				}
			}

		}

		model.put("adminObjects", adminObjects);
		return new ModelAndView("admin/index", model);

	}

	public List<AdminConsoleObject> getAdminConsoleInfo() {
		HostsSummary summary = new HostsSummary();
		final Map<HostClass, Integer> hostsByTypeCounts = hostsDAO
				.countHostsByHostClassForHostSearchQuery(new HostSearchQuery());
		List<HostClass> hostClassesByNum = new ArrayList<HostClass>();
		hostClassesByNum.addAll(hostsByTypeCounts.keySet());
		Collections.sort(hostClassesByNum, new Comparator<HostClass>() {
			public int compare(HostClass o1, HostClass o2) {
				return hostsByTypeCounts.get(o2).compareTo(
						hostsByTypeCounts.get(o1));
			}
		});

		summary.setHostsByType(hostsByTypeCounts);
		summary.setHostClassesByNum(hostClassesByNum);
		summary.setTotalHosts(hostsDAO.countResultsForHostSearchQuery(
				new HostSearchQuery(), null));

		ArrayList<AdminConsoleObject> objs = new ArrayList<AdminConsoleObject>();
		objs.add(summary);
		return objs;
	}

	public ModelAndView nameDomains(HttpServletRequest request,
			HttpServletResponse response) {
		HashMap<String, Object> model = new HashMap<String, Object>();
		List<NameDomain> nameDomains = hostsDAO.getNameDomains();
		model.put("nameDomains", nameDomains);
		model.put("hostClasses", hostsDAO.getHostClasses());
		Map<NameDomain, Integer> hostCounts = new HashMap<NameDomain, Integer>();
		for (NameDomain nd : nameDomains) {
			HostSearchQuery hs = new HostSearchQuery();
			hs.setNameDomain(nd);
			hostCounts.put(nd, hostsDAO
					.countResultsForHostSearchQuery(hs, null));
		}
		model.put("hostCounts", hostCounts);
		return new ModelAndView("admin/nameDomains", model);

	}

	public ModelAndView orgUnits(HttpServletRequest request,
			HttpServletResponse response) {
		HashMap<String, Object> model = new HashMap<String, Object>();
		List<OrgUnit> orgUnits = hostsDAO.getOrgUnits();
		model.put("orgUnits", orgUnits);
		Map<OrgUnit, List<OrgUnitPrincipalMapping>> orgUnitMappings = new HashMap<OrgUnit, List<OrgUnitPrincipalMapping>>();
		Map<OrgUnit, Integer> hostCounts = new HashMap<OrgUnit, Integer>();

		for (OrgUnit ou : orgUnits) {

			orgUnitMappings.put(ou, hostsDAO
					.getOrgUnitPrincipalMappingsForOU(ou));
			HostSearchQuery hsc = new HostSearchQuery();
			hsc.setOrgUnit(ou);
			hostCounts.put(ou, hostsDAO.countResultsForHostSearchQuery(hsc,
					null));
			orgUnitMappings.put(ou, hostsDAO
					.getOrgUnitPrincipalMappingsForOU(ou));
		}
		model.put("orgUnitMappings", orgUnitMappings);

		model.put("hostCounts", hostCounts);

		return new ModelAndView("admin/orgUnits", model);

	}

	public ModelAndView netClasses(HttpServletRequest request,
			HttpServletResponse response) {
		HashMap<String, Object> model = new HashMap<String, Object>();
		model.put("hostClasses", hostsDAO.getHostClasses());
		model.put("networkClasses", hostsDAO.getNetworkClasses());
		return new ModelAndView("admin/netClasses", model);

	}

	public ModelAndView hostChanges(HttpServletRequest request,
			HttpServletResponse response) {
		HashMap<String, Object> model = new HashMap<String, Object>();
		List<NameDomain> nameDomains = hostsDAO.getNameDomains();
		model.put("nameDomains", nameDomains);
		model.put("hostClasses", hostsDAO.getHostClasses());
		Map<NameDomain, Integer> hostCounts = new HashMap<NameDomain, Integer>();
		for (NameDomain nd : nameDomains) {
			HostSearchQuery hs = new HostSearchQuery();
			hs.setNameDomain(nd);
			hostCounts.put(nd, hostsDAO
					.countResultsForHostSearchQuery(hs, null));
		}
		model.put("hostCounts", hostCounts);
		return new ModelAndView("admin/nameDomains", model);

	}

	public ModelAndView hostClasses(HttpServletRequest request,
			HttpServletResponse response) {
		HashMap<String, Object> model = new HashMap<String, Object>();
		List<HostClass> hostClasses = hostsDAO.getHostClasses();
		model.put("hostClasses", hostClasses);
		Map<HostClass, Integer> hostCounts = new HashMap<HostClass, Integer>();
		for (HostClass hc : hostClasses) {
			HostSearchQuery hs = new HostSearchQuery();
			hs.setHostClass(hc);
			hostCounts.put(hc, hostsDAO
					.countResultsForHostSearchQuery(hs, null));
		}
		model.put("hostCounts", hostCounts);
		return new ModelAndView("admin/hostClasses", model);

	}

	public ModelAndView plugins(HttpServletRequest request,
			HttpServletResponse response) {
		HashMap<String, Object> model = new HashMap<String, Object>();

		model.put("plugins", pluginManager.getPlugins());
		return new ModelAndView("admin/plugins", model);

	}

	public ModelAndView showOrgUnit(HttpServletRequest request,
			HttpServletResponse response) {
		HashMap<String, Object> model = new HashMap<String, Object>();

		String id = request.getParameter("id");
		OrgUnit ou = hostsDAO.getOrgUnitById(id);
		if (null == ou) {
			throw new IllegalArgumentException("can't find OU:" + id);
		}
		model.put("orgUnit", ou);

		HostSearchQuery ouQuery = new HostSearchQuery();
		ouQuery.setOrgUnit(ou);

		model.put("hostsCount", hostsDAO.countResultsForHostSearchQuery(
				ouQuery, null));
		// Count host types owned by this OU

		final Map<HostClass, Integer> hostsByTypeCounts = hostsDAO
				.countHostsByHostClassForHostSearchQuery(ouQuery);
		List<HostClass> hostClassesByNum = new ArrayList<HostClass>();
		hostClassesByNum.addAll(hostsByTypeCounts.keySet());
		Collections.sort(hostClassesByNum, new Comparator<HostClass>() {
			public int compare(HostClass o1, HostClass o2) {
				return hostsByTypeCounts.get(o2).compareTo(
						hostsByTypeCounts.get(o1));
			}
		});
		model.put("hostsByType", hostsByTypeCounts);
		model.put("hostClassesByNum", hostClassesByNum);

		// Count subnets used by this OU
		final Map<Subnet, Integer> hostsBySubnetCounts = hostsDAO
				.countHostsOnSubnetsForOU(hostsDAO.getSubnets(), ou, false);

		List<Subnet> subnetsByNum = new ArrayList<Subnet>();
		subnetsByNum.addAll(hostsBySubnetCounts.keySet());

		Collections.sort(subnetsByNum, new Comparator<Subnet>() {
			public int compare(Subnet o1, Subnet o2) {
				return hostsBySubnetCounts.get(o2).compareTo(
						hostsBySubnetCounts.get(o1));
			}
		});
		model.put("hostsBySubnet", hostsBySubnetCounts);
		model.put("subnetsByNum", subnetsByNum);

		// Get Allowed subnets for this OU
		List<Subnet> allowedSubnets = new ArrayList<Subnet>();
		for (Subnet s : hostsDAO.getSubnets()) {
			if (securityHelper.canUse(s, ou)) {
				allowedSubnets.add(s);
			}
		}
		model.put("allowedSubnets", allowedSubnets);

		List<HostClass> allowedHostClasses = new ArrayList<HostClass>();
		for (HostClass s : hostsDAO.getHostClasses()) {
			if (securityHelper.canUse(s, ou)) {
				allowedHostClasses.add(s);
			}
		}
		model.put("allowedHostClasses", allowedHostClasses);

		List<NameDomain> allowedNameDomains = new ArrayList<NameDomain>();
		for (NameDomain s : hostsDAO.getNameDomains()) {
			if (securityHelper.canUse(s, ou)) {
				allowedNameDomains.add(s);
			}
		}
		model.put("allowedNameDomains", allowedNameDomains);
		model.put("orgUnitMappings", hostsDAO
				.getOrgUnitPrincipalMappingsForOU(ou));

		return new ModelAndView("orgunit/showOrgUnit", model);
	}

	@Required
	public void setHostsDAO(NetDAO hostsDAO) {
		this.hostsDAO = hostsDAO;
	}

	@Required
	public void setSecurityHelper(SecurityHelper securityHelper) {
		this.securityHelper = securityHelper;
	}

	@Required
	public void setPluginManager(PluginManager pluginManager) {
		this.pluginManager = pluginManager;
	}

	@Required
	public void setWebPluginManager(PluginManager webPlugimManager) {
		this.webPluginManager = webPlugimManager;
	}

}
