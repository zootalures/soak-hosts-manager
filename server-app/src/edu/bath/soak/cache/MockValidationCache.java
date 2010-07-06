package edu.bath.soak.cache;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class MockValidationCache implements ValidationCache {
	public Errors cacheFetch(Validator validator, Object target) {

		return null;

	}

	public void cachePut(Validator validator, Object target, Errors errors) {
	}
}
