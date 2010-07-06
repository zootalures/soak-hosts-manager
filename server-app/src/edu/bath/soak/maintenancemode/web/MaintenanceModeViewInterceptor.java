package edu.bath.soak.maintenancemode.web;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.servlet.ModelAndView;

import edu.bath.soak.maintenancemode.MaintenanceModeManager;
import edu.bath.soak.maintenancemode.MaintenanceModeManager.MaintenanceMode;
import edu.bath.soak.web.OrderedHandlerInterceptor;

/**
 * # Injects view extension beans when the system is in maintenance mode
 * 
 * @author cspocc
 * 
 */
public class MaintenanceModeViewInterceptor implements
		OrderedHandlerInterceptor {

	MaintenanceModeManager maintenanceModeManager;

	public int getOrder() {
		return 100;
	}

	public void afterCompletion(HttpServletRequest request,
			HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		// TODO Auto-generated method stub

	}

	public void postHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {

	}

	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler) throws Exception {
		MaintenanceMode currentMode = maintenanceModeManager
				.getMaintenanceMode();

		if (currentMode.equals(MaintenanceMode.NORMAL)
				|| request.getAttribute("mm_data_set") != null) {
			return true;
		}

		request.setAttribute("mm_data_set", true);

		MaintenanceModeViewState maintenanceModeViewState = new MaintenanceModeViewState();
		maintenanceModeViewState.setMaintenanceMode(currentMode);
		List<Object> titleBeans = (List<Object>) request.getAttribute("titleBeans");
		if (null == titleBeans) {
			titleBeans = new ArrayList<Object>();
		}

		titleBeans.add(maintenanceModeViewState);
		request.setAttribute("titleBeans", titleBeans);

		List<Object> startPageBeans = (List<Object>) request.getAttribute("startPageBeans");

		if (null == startPageBeans) {
			startPageBeans = new ArrayList<Object>();
		}

		startPageBeans.add(maintenanceModeViewState);

		request.setAttribute("startPageBeans", startPageBeans);
		return true;
	}

	@Required
	public void setMaintenanceModeManager(
			MaintenanceModeManager maintenanceModeManager) {
		this.maintenanceModeManager = maintenanceModeManager;
	}

}
