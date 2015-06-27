package edu.bath.soak.security;

import java.util.ArrayList;
import java.util.List;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import org.springframework.security.GrantedAuthority;
import org.springframework.security.ldap.InitialDirContextFactory;
import org.springframework.security.userdetails.UserDetails;
import org.springframework.security.userdetails.UserDetailsService;
import org.springframework.security.userdetails.UsernameNotFoundException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.dao.DataAccessException;

import edu.bath.soak.net.model.NetDAO;

/**
 * Custom LDAP-oriented user details service which loads appropriate user
 * information into a standard UserDetails wrapped object
 * 
 * @author cspocc TODO: this is only a skeleton at the moment .
 */
public class LDAPUserDetailsService implements UserDetailsService {

	InitialDirContextFactory initialDirContextFactory;
	NetDAO hostsDAO;
	String userSearchBase;
	String userSearchFilter = "(&(objectclass=inetOrgPerson)(uid={0}))";

	String mailAttribute = "mail";
	String cnAttribute = "cn";
	// String ldapBindDN;
	// String ldapBindPasswd;
	// String ldapURL;

	List<AuthorityGranter> authorityGranters = new ArrayList<AuthorityGranter>();
	Logger log = Logger.getLogger(getClass());

	DirContext getContext() {
		// Hashtable<String, String> env = new Hashtable();
		// env.put(Context.INITIAL_CONTEXT_FACTORY,
		// "com.sun.jndi.ldap.LdapCtxFactory");
		// env.put(Context.PROVIDER_URL, ldapURL);
		// if (null != ldapBindDN) {
		// // Authenticate as S. User and password "mysecret"
		// env.put(Context.SECURITY_AUTHENTICATION, "simple");
		// env.put(Context.SECURITY_PRINCIPAL, ldapBindDN);
		// env.put(Context.SECURITY_CREDENTIALS, ldapBindPasswd);
		// }
		// DirContext ctx = new InitialDirContext(env);
		//
		// return ctx;
		return initialDirContextFactory.newInitialDirContext();
	}

	/***************************************************************************
	 * Loads a given user from LDAP, propagates their authorities through the
	 * specified {@link AuthorityGranter} list set in
	 * {@link #setAuthorityGranters(List)}
	 * 
	 * @param username
	 *            the UID (log in id) of the user to load
	 * @return A new {@link UserDetails} object for this user
	 * @throws UsernameNotFoundException
	 *             if the user was not found
	 * @throws DataAccessException
	 *             if there was a error accessing LDAP
	 * 
	 * 
	 */
	public UserDetails loadUserByUsername(String username)
			throws UsernameNotFoundException, DataAccessException {

		try {
			DirContext ctx = getContext();
			SearchControls ctrls = new SearchControls();
			ctrls.setReturningAttributes(new String[] { "dn", mailAttribute,
					cnAttribute });
			NamingEnumeration<SearchResult> res = ctx.search(userSearchBase,
					userSearchFilter, new Object[] { username }, null);

			if (!res.hasMore()) {
				throw new UsernameNotFoundException(
						"Unable to find LDAP details for   " + username);
			}
			log.info("Binding LDAP user details for incoming user " + username);
			SearchResult firstResult = res.next();

			SoakUserDetails lud = new SoakUserDetails();
			lud.setUsername(username);
			lud.setFriendlyName(firstResult.getAttributes().get(cnAttribute)
					.get().toString());
			lud.setEmail(firstResult.getAttributes().get(mailAttribute).get()
					.toString());
			lud.setDn(firstResult.getNameInNamespace());

			List<GrantedAuthority> granted = new ArrayList<GrantedAuthority>();
			for (AuthorityGranter ag : authorityGranters) {
				granted.addAll(ag.grantAuthorities(lud));
			}
			lud.setAuthorities(granted.toArray(new GrantedAuthority[] {}));
			return lud;
		} catch (NamingException e) {
			throw new RuntimeException("Unable to load user details for "
					+ username, e);
		}
	}

	public NetDAO getHostsDAO() {
		return hostsDAO;
	}

	public void setHostsDAO(NetDAO hostsDAO) {
		this.hostsDAO = hostsDAO;
	}

	public void setUserSearchFilter(String userSearchFilter) {
		this.userSearchFilter = userSearchFilter;
	}

	public void setAuthorityGranters(List<AuthorityGranter> authorityGranters) {
		this.authorityGranters = authorityGranters;
	}

	// @Required
	// public void setLdapURL(String ldapURL) {
	// this.ldapURL = ldapURL;
	// }

	public String getUserSearchFilter() {
		return userSearchFilter;
	}

	@Required
	public void setUserSearchBase(String userSearchBase) {
		this.userSearchBase = userSearchBase;
	}

	@Required
	public void setInitialDirContextFactory(
			InitialDirContextFactory initialDirContextFactory) {
		this.initialDirContextFactory = initialDirContextFactory;
	}

	// public void setLdapBindDN(String ldapBindDN) {
	// this.ldapBindDN = ldapBindDN;
	// }
	//
	// public void setLdapBindPasswd(String ldapPasswd) {
	// this.ldapBindPasswd = ldapPasswd;
	// }
}
