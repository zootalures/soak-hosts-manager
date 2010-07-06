package edu.bath.soak.security;

import java.util.ArrayList;
import java.util.List;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import org.springframework.security.GrantedAuthority;
import org.springframework.security.GrantedAuthorityImpl;
import org.springframework.security.ldap.InitialDirContextFactory;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import edu.bath.soak.model.OrgUnitPrincipalMapping;
import edu.bath.soak.net.model.NetDAO;

/**
 * Grants OU admin authorities to users based on their LDAP principal
 * memberships and the mappings stored in the database
 * 
 * @author cspocc
 * 
 */
public class LDAPGroupOUAuthorityGranter implements AuthorityGranter {
	NetDAO hostsDAO;
	Logger log = Logger.getLogger(getClass());
	InitialDirContextFactory initialDirContextFactory;
	LDAPUserDetailsService ldapUserDetailsService;
	List<String> adminGroups = new ArrayList<String>();
	List<String> adminUsers = new ArrayList<String>();
	boolean requireAccessMembershipToUse = false;
	List<String> accessGroups = new ArrayList<String>();
	List<String> accessUsers = new ArrayList<String>();

	String groupIdentifyinAttribute = "cn";
	String groupMembershipSearchFilter = "(&(objectclass=groupOfNames)(cn=*)(member={0} ))";
	String groupSearchBase;
	String groupSearchFilter = "(&(objectclass=groupOfNames)(cn={0}))";

	List<String> getGroups(SoakUserDetails lud) {

		try {
			DirContext ctx = initialDirContextFactory.newInitialDirContext();

			SearchControls groupsCtrl = new SearchControls();
			groupsCtrl
					.setReturningAttributes(new String[] { groupIdentifyinAttribute });

			NamingEnumeration<SearchResult> groupres = ctx.search(
					groupSearchBase, groupMembershipSearchFilter, new Object[] {
							lud.getDn(), lud.getUsername() }, groupsCtrl);

			ArrayList<String> groups = new ArrayList<String>();
			while (groupres.hasMoreElements()) {
				SearchResult res = groupres.nextElement();
				String groupname = res.getAttributes().get(
						groupIdentifyinAttribute).get().toString();
				log.debug("user " + lud.getUsername() + " in " + groupname);
				groups.add(groupname);
			}
			return groups;
		} catch (NamingException e) {
			throw new RuntimeException(e);
		}
	}

	public List<GrantedAuthority> grantAuthorities(SoakUserDetails ud) {
		List<GrantedAuthority> granted = new ArrayList<GrantedAuthority>();

		List<String> groups = getGroups(ud);
		boolean isAdmin = false;
		boolean isAccess = false;
		for (OrgUnitPrincipalMapping ogm : hostsDAO
				.getOrgUnitPrincipalMappingsForGroups(groups)) {
			OuAdminAuthority auth = new OuAdminAuthority(ogm.getOrgUnit());
			granted.add(auth);
			log.info("Granted " + auth + " to " + ud.getUsername());

		}

		for (String group : groups) {
			if (adminGroups.contains(group)) {
				isAdmin = true;
			}
			if (accessGroups.contains(group)) {
				log.info("user " + ud.getUsername() + "is in access group");
				isAccess = true;
			}
		}

		for (OrgUnitPrincipalMapping oum : hostsDAO
				.getOrgUnitPrincipalMappingsForUser(ud.getUsername())) {
			OuAdminAuthority auth = new OuAdminAuthority(oum.getOrgUnit());
			granted.add(auth);
			log.info("Granted " + auth);

		}

		if (adminUsers.contains(ud.getUsername())) {
			isAdmin = true;
		}

		/**
		 * We grant access if * user is admin, * Access membership is not
		 * required and user is in an OU group * user is in an access membership
		 * group
		 */
		if (isAdmin || (!requireAccessMembershipToUse && !granted.isEmpty())
				|| isAccess) {
			GrantedAuthority ga = new GrantedAuthorityImpl(
					SecurityHelper.USER_ROLE);
			granted.add(ga);
			log.info("Granted " + ga);
		}

		if (isAdmin) {
			GrantedAuthority adminAuth = new GrantedAuthorityImpl(
					SecurityHelper.ADMIN_ROLE);

			granted.add(adminAuth);
			log.info("Granted " + adminAuth);
		}
		return granted;
	}

	@Required
	public void setHostsDAO(NetDAO hostsDAO) {
		this.hostsDAO = hostsDAO;
	}

	public void setInitialDirContextFactory(
			InitialDirContextFactory initialDirContextFactory) {
		this.initialDirContextFactory = initialDirContextFactory;
	}

	public List<String> getAdminGroups() {
		return adminGroups;
	}

	public void setAdminGroups(List<String> adminGroups) {
		this.adminGroups = adminGroups;
	}

	public List<String> getAdminUsers() {
		return adminUsers;
	}

	public void setAdminUsers(List<String> adminUsers) {
		this.adminUsers = adminUsers;
	}

	public void setGroupMembershipSearchFilter(
			String groupMembershipSearchFilter) {
		this.groupMembershipSearchFilter = groupMembershipSearchFilter;
	}

	@Required
	public void setGroupSearchBase(String groupSearchBase) {
		this.groupSearchBase = groupSearchBase;
	}

	public List<String> getAccessGroups() {
		return accessGroups;
	}

	public void setAccessGroups(List<String> accessGroups) {
		this.accessGroups = accessGroups;
	}

	public List<String> getAccessUsers() {
		return accessUsers;
	}

	public void setAccessUsers(List<String> accessUsers) {
		this.accessUsers = accessUsers;
	}

	public boolean isRequireAccessMembershipToUse() {
		return requireAccessMembershipToUse;
	}

	public void setRequireAccessMembershipToUse(boolean requireAccess) {
		this.requireAccessMembershipToUse = requireAccess;
	}

}
