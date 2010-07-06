package edu.bath.soak.maintenancemode.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;

import edu.bath.soak.maintenancemode.MaintenanceModeManager;
import edu.bath.soak.maintenancemode.MaintenanceModeManager.MaintenanceMode;
import edu.bath.soak.security.SecurityHelper;
import edu.bath.soak.web.AdminConsoleInfoProvider;
import edu.bath.soak.web.admin.AdminConsoleObject;

/**
 * sets the current maintenance mode
 * 
 * @author cspocc
 * 
 */
public class MaintenanceModeController extends SimpleFormController implements
		AdminConsoleInfoProvider {

	SecurityHelper securityHelper;
	MaintenanceModeManager maintenanceModeManager;

	public static class SetMaintenanceMode {
		MaintenanceMode newMode;

		public MaintenanceMode getNewMode() {
			return newMode;
		}

		public void setNewMode(MaintenanceMode newMode) {
			this.newMode = newMode;
		}
	}

	@Override
	protected Object formBackingObject(HttpServletRequest request)
			throws Exception {
		SetMaintenanceMode cmd = new SetMaintenanceMode();
		cmd.setNewMode(maintenanceModeManager.getMaintenanceMode());
		return cmd;
	}

	public List<AdminConsoleObject> getAdminConsoleInfo() {
		ArrayList<AdminConsoleObject> objs = new ArrayList<AdminConsoleObject>();
		objs.add(maintenanceModeManager.getMaintenanceMode());
		return objs;
	}

	@Override
	protected Map referenceData(HttpServletRequest request, Object command,
			Errors errors) throws Exception {

		Map<String, Object> model = new HashMap<String, Object>();
		model.put("maintenanceMode", maintenanceModeManager
				.getMaintenanceMode());
		return model;
	}

	@Override
	protected ModelAndView onSubmit(Object command) throws Exception {
		Assert.isTrue(securityHelper.isAdmin());
		Assert.isInstanceOf(SetMaintenanceMode.class, command);
		SetMaintenanceMode cmd = (SetMaintenanceMode) command;
		Assert.notNull(cmd.getNewMode());
		maintenanceModeManager.setMaintenanceMode(cmd.getNewMode());

		Map<String, Object> model = new HashMap<String, Object>();
		model.put("maintenanceMode", maintenanceModeManager
				.getMaintenanceMode());
		return new ModelAndView(getSuccessView(), model);
	}

	@Required
	public void setSecurityHelper(SecurityHelper securityHelper) {
		this.securityHelper = securityHelper;
	}

	@Required
	public void setMaintenanceModeManager(
			MaintenanceModeManager maintenanceModeManager) {
		this.maintenanceModeManager = maintenanceModeManager;
	}

}
