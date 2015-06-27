package edu.bath.soak.maintenancemode;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.webflow.core.FlowException;

/**
 * This is an cross-cutting aspect which screws around with the otherwise obtuse
 * FlowExecutionListener framework
 * 
 * This does nothing when the system is not in maintenance mode, otherwise it
 * intercepts attempts to start flows, and throws MaintenanceModeExceptions, we
 * also have to trap the exception handle and re-throw these so that the
 * conventional exception view resolver can deal with them rather than the flow
 * executor (which we can't plug into).
 * 
 * @author cspocc
 * 
 */
@Aspect
public class MaintenanceModeAspect {

	MaintenanceModeManager maintenanceModeManager;

	@Before("execution(void edu.bath.soak.web.SoakFlowExecutionListener.stateEntered(..))")
	public void onStateEntered() throws Throwable {
		if (!maintenanceModeManager.canProceedWithEdit()) {
			throw new MaintenanceModeException(
					"Editing disabled, system is under maintenance");
		}
		// pjp.proceed();
	}

	@Around("execution(void edu.bath.soak.web.SoakFlowExecutionListener.exceptionThrown(..) )")
	public void onExceptionThrown(ProceedingJoinPoint pjp) throws Throwable {
		FlowException exception = (FlowException) pjp.getArgs()[1];
		if (exception.getCause() instanceof MaintenanceModeException)
			throw exception.getCause();
		pjp.proceed();
	}

	@Required
	public void setMaintenanceModeManager(
			MaintenanceModeManager maintenanceModeManager) {
		this.maintenanceModeManager = maintenanceModeManager;
	}
}
