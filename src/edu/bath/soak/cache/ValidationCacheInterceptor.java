package edu.bath.soak.cache;

import org.apache.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Aspect
public class ValidationCacheInterceptor {
	Logger log = Logger.getLogger(ValidationCacheInterceptor.class);
	ValidationCache scopedValidationCache;

	@Around("execution(void org.springframework.validation.Validator.validate(java.lang.Object,org.springframework.validation.Errors))  && @annotation(CacheableValidatorMethod)")
	public void validatorCalled(ProceedingJoinPoint pjp) throws Throwable {
		log.info("cachable validator called on " + pjp.getTarget());
		Validator validator = (Validator) pjp.getTarget();
		Object target = pjp.getArgs()[0];
		Errors errors = (Errors) pjp.getArgs()[1];

		Errors cachedResult = scopedValidationCache.cacheFetch(validator, target);
		if (null != cachedResult) {
			log.debug("Returning cached validation result for " + validator
					+ " - " + target);
			errors.addAllErrors(cachedResult);
			return;
		} else {
			log.debug("Running validation result for " + validator + " - "
					+ target);
			pjp.proceed();
			scopedValidationCache.cachePut(validator, target, errors);

		}
	}

	@Required
	public void setValidationCache(ValidationCache scopedValidationCache) {
		this.scopedValidationCache = scopedValidationCache;
	}

}
