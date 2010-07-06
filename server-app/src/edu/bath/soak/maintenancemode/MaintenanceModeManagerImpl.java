package edu.bath.soak.maintenancemode;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import edu.bath.soak.EventLog;
import edu.bath.soak.security.SecurityHelper;

/**
 * Manages the maintenance mode and access decisions for users
 * 
 * @author cspocc
 * 
 */
public class MaintenanceModeManagerImpl implements MaintenanceModeManager {

	Logger log = Logger.getLogger(MaintenanceModeManagerImpl.class);
	MaintenanceMode maintenanceMode = MaintenanceMode.NORMAL;
	SecurityHelper securityHelper;

	public MaintenanceMode getMaintenanceMode() {
		return maintenanceMode;
	}

	public void setMaintenanceMode(MaintenanceMode mm) {
		if (!maintenanceMode.equals(mm)) {
			maintenanceMode = mm;
			EventLog.log().info("entering runtime mode: " + mm);
		}
	}

	public boolean canProceedWithEdit() {
		if (maintenanceMode.equals(MaintenanceMode.FULL_MAINTENANCE)
				|| (maintenanceMode.equals(MaintenanceMode.ADMIN_MAINTENANCE) && !securityHelper
						.isAdmin())) {
			return false;
		}
		return true;
	}

	@Required
	public void setSecurityHelper(SecurityHelper securityHelper) {
		this.securityHelper = securityHelper;
	}
}
