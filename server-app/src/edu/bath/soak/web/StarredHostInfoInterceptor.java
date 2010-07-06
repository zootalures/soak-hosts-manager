package edu.bath.soak.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import edu.bath.soak.mgr.StarredHostsManager;

/**
 * Display augmentor which adds starred hosts information to each outgoing
 * request.
 * 
 * @author cspocc
 * 
 */
public class StarredHostInfoInterceptor implements OrderedHandlerInterceptor {
	StarredHostsManager starredHostsManager;

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
		request.setAttribute("starredHostIds",
				starredHostsManager.getStarredHostIds());
		request.setAttribute("numStarredHosts",
				starredHostsManager.getStarredHostIds().size());
		return true;
	}

	@Required
	public void setStarredHostsManager(StarredHostsManager starredHostsManager) {
		this.starredHostsManager = starredHostsManager;
	}

}
