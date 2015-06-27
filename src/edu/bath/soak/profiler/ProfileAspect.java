package edu.bath.soak.profiler;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

@Aspect()
public class ProfileAspect {

	
	@Pointcut("* edu.bath.soak..*(..)")
	public void edgeprofiled() {
		
	}

	@Around("edgeprofiled()")
	public void doProfiled(ProceedingJoinPoint pjp) throws Throwable {
		pjp.proceed();
		
	}
}
