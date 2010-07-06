package test.hosts;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.GrantedAuthority;
import org.springframework.security.GrantedAuthorityImpl;
import org.springframework.security.context.SecurityContextHolder;
import org.springframework.security.providers.TestingAuthenticationToken;

import edu.bath.soak.model.OrgUnit;
import edu.bath.soak.security.OuAdminAuthority;
import edu.bath.soak.security.SecurityHelper;
import edu.bath.soak.security.SoakUserDetails;

public class SpringSetup {

	public static String[] BASIC_TEST_LOCS = new String[] {
			"classpath:edu/bath/soak/applicationContext.xml",
			"classpath:edu/bath/soak/net/applicationContext.xml",
			"classpath:edu/bath/soak/applicationContext-data.xml",
			"classpath:applicationContext-test.xml" };

	public static String[] BASIC_LIVETEST_LOCS = new String[] {
			"classpath:applicationContext-livetestdata.xml",
			"classpath:edu/bath/soak/applicationContext-data.xml",
			"classpath:edu/bath/soak/applicationContext.xml",
			"classpath:edu/bath/soak/net/applicationContext.xml",
			"classpath:edu/bath/soak/dns/applicationContext.xml",
			"classpath:edu/bath/soak/dhcp/applicationContext.xml",
			"classpath:edu/bath/soak/hostactivity/applicationContext.xml"

	};

	public static String[] DHCP_TEST_LOCS = new String[] {
			"classpath:edu/bath/soak/applicationContext.xml",
			"classpath:edu/bath/soak/net/applicationContext.xml",
			"classpath:edu/bath/soak/dhcp/applicationContext.xml",
			"classpath:edu/bath/soak/applicationContext-data.xml",
			"classpath:applicationContext-test.xml" };

	public static String[] DNS_TEST_LOCS = new String[] {
			"classpath:edu/bath/soak/applicationContext.xml",
			"classpath:edu/bath/soak/net/applicationContext.xml",
			"classpath:edu/bath/soak/dns/applicationContext.xml",
			"classpath:edu/bath/soak/applicationContext-data.xml",
			"classpath:applicationContext-test.xml" };

	public static String[] ALL_TEST_LOCS = new String[] {
			"classpath:edu/bath/soak/applicationContext.xml",
			"classpath:edu/bath/soak/net/applicationContext.xml",
			"classpath:edu/bath/soak/dns/applicationContext.xml",
			"classpath:edu/bath/soak/dhcp/applicationContext.xml",
			"classpath:edu/bath/soak/applicationContext-data.xml",
			"classpath:applicationContext-test.xml" };

	public static String[] WEB_TEST_LOCS = new String[] {
			"classpath:edu/bath/soak/applicationContext.xml",
			"classpath:edu/bath/soak/net/applicationContext.xml",
			"classpath:edu/bath/soak/dns/applicationContext.xml",
			"classpath:edu/bath/soak/dhcp/applicationContext.xml",
			"classpath:edu/bath/soak/servletContext.xml",
			"classpath:edu/bath/soak/dns/servletContext.xml",
			"classpath:edu/bath/soak/dhcp/servletContext.xml",
			"classpath:edu/bath/soak/applicationContext-data.xml",
			"classpath:applicationContext-test.xml" };

	public static void setUpBasicAcegiAuthentication(boolean admin,
			OrgUnit... orgUnits) {

		List<GrantedAuthority> auths = new ArrayList<GrantedAuthority>();
		for (OrgUnit ou : orgUnits) {
			auths.add(new OuAdminAuthority(ou));
		}
		if (admin) {
			auths.add(new GrantedAuthorityImpl(SecurityHelper.ADMIN_ROLE));
		}

		SoakUserDetails details = new SoakUserDetails();
		details.setAuthorities(auths.toArray(new GrantedAuthority[] {}));
		details.setUsername("testuser");
		details.setFriendlyName("A. Friendly User");
		details.setEmail("nobody@nowhere.foo");
		TestingAuthenticationToken token = new TestingAuthenticationToken(
				details, null, details.getAuthorities());
		SecurityContextHolder.getContext().setAuthentication(token);
	}
}
