package edu.bath.soak.maintenancemode;

import edu.bath.soak.web.BeanView;
import edu.bath.soak.web.admin.AdminConsoleObject;

public interface MaintenanceModeManager {
	@BeanView(view = "adminConsole", value = "beanview/maintenanceMode/MaintenanceMode-adminConsole")
	public enum MaintenanceMode implements AdminConsoleObject {
		NORMAL, ADMIN_MAINTENANCE, FULL_MAINTENANCE

		;

		public int getOrder() {
			return 0;
		}
	};

	public MaintenanceMode getMaintenanceMode();

	public void setMaintenanceMode(MaintenanceMode mm);

	public boolean canProceedWithEdit();
}
