package edu.bath.soak.maintenancemode.web;

import edu.bath.soak.maintenancemode.MaintenanceModeManager.MaintenanceMode;
import edu.bath.soak.web.BeanView;
import edu.bath.soak.web.BeanViews;

@BeanViews( {
		@BeanView(view = "title", value = "beanview/maintenanceMode/MaintenanceMode-title"),
		@BeanView(view = "startPage", value = "beanview/maintenanceMode/MaintenanceMode-startPage") })
public class MaintenanceModeViewState {
	MaintenanceMode maintenanceMode;

	public MaintenanceMode getMaintenanceMode() {
		return maintenanceMode;
	}

	public void setMaintenanceMode(MaintenanceMode maintenanceMode) {
		this.maintenanceMode = maintenanceMode;
	}
}
