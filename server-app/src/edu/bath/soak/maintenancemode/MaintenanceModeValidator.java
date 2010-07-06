package edu.bath.soak.maintenancemode;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import edu.bath.soak.cmd.OrderedValidator;
import edu.bath.soak.net.cmd.AlterHostCmd;

public class MaintenanceModeValidator implements OrderedValidator {

	MaintenanceModeManager maintenanceModeManager;

	public boolean supports(Class clazz) {
		// TODO Auto-generated method stub
		return true;
	}

	public void validate(Object target, Errors errors) {
		if (!maintenanceModeManager.canProceedWithEdit()) {
			throw new MaintenanceModeException("System is under maintenance");
		}
	}

	@Required
	public void setMaintenanceModeManager(
			MaintenanceModeManager maintenanceModeManager) {
		this.maintenanceModeManager = maintenanceModeManager;
	}

	public int getOrder() {
		return Integer.MAX_VALUE;
	}

}
