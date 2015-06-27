package edu.bath.soak.cache;

import java.util.HashMap;

import org.apache.log4j.Logger;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class ScopedValidationCache implements ValidationCache {
	HashMap<CachedValidationEntry, Errors> cachedObjects = new HashMap<CachedValidationEntry, Errors>();

	static class CachedValidationEntry {
		Object target;
		Validator validator;

		public CachedValidationEntry(Object target, Validator validator) {
			this.target = target;
			this.validator = validator;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result
					+ ((target == null) ? 0 : target.hashCode());
			result = prime * result
					+ ((validator == null) ? 0 : validator.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			final CachedValidationEntry other = (CachedValidationEntry) obj;
			if (target == null) {
				if (other.target != null)
					return false;
			} else if (!target.equals(other.target))
				return false;
			if (validator == null) {
				if (other.validator != null)
					return false;
			} else if (!validator.equals(other.validator))
				return false;
			return true;
		}

		public Object getTarget() {
			return target;
		}

		public void setTarget(Object target) {
			this.target = target;
		}

		public Validator getValidator() {
			return validator;
		}

		public void setValidator(Validator validator) {
			this.validator = validator;
		}

	}

	Logger log = Logger.getLogger(ScopedValidationCache.class);

	/* (non-Javadoc)
	 * @see edu.bath.soak.cache.ValidationCache#cacheFetch(org.springframework.validation.Validator, java.lang.Object)
	 */
	public Errors cacheFetch(Validator validator, Object target) {
		log.debug("trying cache fetch for " + target.hashCode()
				+ " cache contains " + cachedObjects.size());

		return cachedObjects.get(new CachedValidationEntry(target, validator));

	}

	/* (non-Javadoc)
	 * @see edu.bath.soak.cache.ValidationCache#cachePut(org.springframework.validation.Validator, java.lang.Object, org.springframework.validation.Errors)
	 */
	public void cachePut(Validator validator, Object target, Errors errors) {
		cachedObjects.put(new CachedValidationEntry(target, validator), errors);
	}
}
