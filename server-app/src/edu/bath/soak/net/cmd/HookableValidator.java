package edu.bath.soak.net.cmd;

import java.util.Comparator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.springframework.util.Assert;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import edu.bath.soak.cmd.OrderedValidator;

/*******************************************************************************
 * Hookable validator class allowing validators to push validation to other
 * plugins.
 * 
 * It is the responsibility of the superclass to ensure that super.validate is
 * called at the appropriate point in the validation chain
 * 
 * @author cspocc
 * 
 */
public abstract class HookableValidator implements Validator {
	protected SortedSet<OrderedValidator> subValidators = new TreeSet<OrderedValidator>(
			new Comparator<OrderedValidator>() {
				public int compare(OrderedValidator o1, OrderedValidator o2) {
					return ((Integer) o1.getOrder()).compareTo(o2.getOrder());
				}

			});

	public void validate(Object target, Errors errors) {
		for (Validator subval : subValidators) {
			Assert.isTrue(subval.supports(target.getClass()),"sub validator " + subval + " does not support commands of class "+ target.getClass());
			ValidationUtils.invokeValidator(subval, target, errors);
		}
	}

	public void registerSubValidator(OrderedValidator v) {
		subValidators.add(v);
	}

	/**
	 * A list of host validation components, these are invoked after semantic
	 * validation iff no errors are present.
	 * 
	 * @return
	 */
	public SortedSet<OrderedValidator> getSubValidators() {
		return subValidators;
	}

	public void setSubValidators(List<OrderedValidator> subValidators) {
		this.subValidators.clear();
		this.subValidators.addAll(subValidators);
	}
}
