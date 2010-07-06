package edu.bath.soak;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Required;

import edu.bath.soak.web.AdminConsoleInfoProvider;
import edu.bath.soak.web.DelegatingHookableViewInterceptor;
import edu.bath.soak.web.OrderedHandlerInterceptor;
import edu.bath.soak.web.host.HostInfoController;
import edu.bath.soak.web.host.ShowHostInfoInterceptor;

/**
 * Exposes web-based contact points for plugins
 * 
 * @author cspocc
 * 
 */
public class SoakWebPlugin extends SoakPlugin implements InitializingBean {
	HostInfoController hostInfoController;
	Map<OrderedHandlerInterceptor, DelegatingHookableViewInterceptor> viewInterceptors = new HashMap<OrderedHandlerInterceptor, DelegatingHookableViewInterceptor>();
	List<ShowHostInfoInterceptor> showHostInterceptors = new ArrayList<ShowHostInfoInterceptor>();

	List<AdminConsoleInfoProvider> consoleInfoInfoProviders = new ArrayList<AdminConsoleInfoProvider>();

	public void afterPropertiesSet() throws Exception {
		log.info("Rigging up web plugin " + pluginName + " : " + pluginVersion);
		for (ShowHostInfoInterceptor shii : showHostInterceptors) {
			hostInfoController.registerShowHostInterceptor(shii);
		}

		for (Entry<OrderedHandlerInterceptor, DelegatingHookableViewInterceptor> e : viewInterceptors
				.entrySet()) {
			e.getValue().registerHandlerInterceptor(e.getKey());

		}
		log.debug("Web plugin " + pluginName + " : " + pluginVersion
				+ " configured successfully");

	}

	/**
	 * The host info controller for hooking up view interceptors
	 * 
	 * @param hostInfoController
	 */
	@Required
	public void setHostInfoController(HostInfoController hostInfoController) {
		this.hostInfoController = hostInfoController;
	}

	/**
	 * Interceptors to add when a host is shown
	 * 
	 * Each of these will be called when a host is shown in the normal interface
	 * 
	 * @param showHostInterceptors
	 *            a list of interceptors
	 */
	public void setShowHostInterceptors(
			List<ShowHostInfoInterceptor> showHostInterceptors) {
		this.showHostInterceptors = showHostInterceptors;
	}

	public Map<OrderedHandlerInterceptor, DelegatingHookableViewInterceptor> getViewInterceptors() {
		return viewInterceptors;
	}

	/**
	 * Global view interceptors which will be hooked into particular handler
	 * maps
	 * 
	 * Well known view interceptors include "soakCoreViewInterceptor"
	 * 
	 * @param viewInterceptors
	 *            a map of an interceptor to the hookableViewInterceptor into
	 *            which that interceptor will be injected
	 */
	public void setViewInterceptors(
			Map<OrderedHandlerInterceptor, DelegatingHookableViewInterceptor> viewInterceptors) {
		this.viewInterceptors = viewInterceptors;
	}

	public List<AdminConsoleInfoProvider> getConsoleInfoInfoProviders() {
		return consoleInfoInfoProviders;
	}

	/***************************************************************************
	 * Sets admin console providers which will be displayed in the admin console
	 * for this plugin
	 * 
	 * @param consoleInfoInfoProviders
	 *            a list of admin console providers
	 */
	public void setConsoleInfoInfoProviders(
			List<AdminConsoleInfoProvider> consoleInfoInfoProviders) {
		this.consoleInfoInfoProviders = consoleInfoInfoProviders;
	}
}
