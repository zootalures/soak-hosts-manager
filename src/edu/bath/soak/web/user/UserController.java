package edu.bath.soak.web.user;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.GrantedAuthority;
import org.springframework.security.userdetails.UserDetails;
import org.springframework.security.userdetails.UserDetailsService;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import edu.bath.soak.model.OrgUnit;
import edu.bath.soak.net.model.NetDAO;
import edu.bath.soak.security.LDAPUserDetailsService;
import edu.bath.soak.security.OuAdminAuthority;
import edu.bath.soak.security.SecurityHelper;

/**
 * 
 * Web tier controller for viewing user details and groups
 * 
 * @author cspocc
 * 
 */
public class UserController extends MultiActionController {
	SecurityHelper securityHelper;
	UserDetailsService userDetailsService;

	NetDAO hostsDAO;

	public ModelAndView details(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		String userId = null;
		if (securityHelper.isAdmin() && null != request.getParameter("user")) {
			userId = request.getParameter("user");
		} else {
			userId = securityHelper.getCurrentUserId();
		}

		Map<String, Object> model = new HashMap<String, Object>();

		
		UserDetails ud  = null;
		
		try{
			ud = userDetailsService.loadUserByUsername(userId);
		}catch(Exception e){
			model.put("userName",userId);
			return new ModelAndView("user/userNotFound");
		}
		model.put("showUserDetails", ud);
		List<OrgUnit> ouAuths = new LinkedList<OrgUnit>();
		boolean isAdmin = false;
		boolean isUser = false;
		for (GrantedAuthority ga : ud.getAuthorities()) {
			if (ga instanceof OuAdminAuthority) {
				ouAuths.add(((OuAdminAuthority) ga).getOu());
			} else if (ga.getAuthority().equals(SecurityHelper.USER_ROLE)) {
				isUser = true;
			} else if (ga.getAuthority().equals(SecurityHelper.ADMIN_ROLE)) {
				isAdmin = true;
			}
		}
		model.put("ouAdmins", ouAuths);
		model.put("isAdmin", isAdmin);
		model.put("isUser", isUser);

		return new ModelAndView("user/details", model);
	}

	@Required
	public void setSecurityHelper(SecurityHelper securityHelper) {
		this.securityHelper = securityHelper;
	}

	@Required
	public void setHostsDAO(NetDAO hostsDAO) {
		this.hostsDAO = hostsDAO;
	}

	@Required
	public void setUserDetailsService(UserDetailsService userDetailsService) {
		this.userDetailsService = userDetailsService;
	}
}
