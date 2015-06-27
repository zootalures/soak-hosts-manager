package edu.bath.soak.cache;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public interface ValidationCache {

	public abstract Errors cacheFetch(Validator validator, Object target);

	public abstract void cachePut(Validator validator, Object target,
			Errors errors);

}