package edu.bath.soak.cmd;

import org.springframework.core.Ordered;
import org.springframework.validation.Validator;

import edu.bath.soak.net.cmd.HookableValidator;

/**
 * Convenience interface which includes both {@link Validator} and
 * {@link Ordered}. Used primarily for registering sub-validators with
 * {@link HookableValidator} objects.
 * 
 * @author cspocc
 * 
 */
public interface OrderedValidator extends Validator, Ordered {

}
