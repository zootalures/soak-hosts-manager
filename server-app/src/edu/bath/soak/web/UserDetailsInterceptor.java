package edu.bath.soak.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.context.SecurityContext;
import org.springframework.security.context.SecurityContextHolder;
import org.springframework.web.servlet.ModelAndView;

import edu.bath.soak.security.SoakUserDetails;

public class UserDetailsInterceptor implements OrderedHandlerInterceptor {

	public int getOrder() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void afterCompletion(HttpServletRequest request,
			HttpServletResponse response, Object handler, Exception ex)
			throws Exception {

	}

	public void postHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {

	}

	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler) throws Exception {

		SecurityContext ctx = SecurityContextHolder.getContext();
		if (null != ctx) {

			if (ctx.getAuthentication() != null) {
				Object userPrincipal = ctx.getAuthentication().getPrincipal();
				if (userPrincipal instanceof SoakUserDetails)
					request.setAttribute("userDetails", userPrincipal);
			}
		}
		return true;
	}

}
