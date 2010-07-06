package edu.bath.soak.maintenancemode;

import org.apache.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Required;

import edu.bath.soak.security.SecurityHelper;

@Aspect
public class SystemMaintenanceModeAspect {
	Logger log = Logger.getLogger(SystemMaintenanceModeAspect.class);
	MaintenanceModeManager maintenanceModeManager;
	SecurityHelper securityHelper;

	@Around("execution(* edu.bath.soak.net.mode.NetDAO.save..*(..))")
	public Object aroundSave(ProceedingJoinPoint pjp) throws Throwable {
		log.debug("Maintenance mode interceptor invoked");
		return pjp.proceed();
	}

	@Required
	public void setMaintenanceModeManager(
			MaintenanceModeManager maintenanceModeManager) {
		this.maintenanceModeManager = maintenanceModeManager;
	}

	public void setSecurityHelper(SecurityHelper securityHelper) {
		this.securityHelper = securityHelper;
	}
}
